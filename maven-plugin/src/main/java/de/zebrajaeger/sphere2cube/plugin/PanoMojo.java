package de.zebrajaeger.sphere2cube.plugin;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.ImgScaler;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.utils.Utils;
import de.zebrajaeger.sphere2cube.utils.ZipUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class PanoMojo extends AbstractMojo {

    @Component
    protected MojoExecution execution;

    @Parameter(property = "sourceDirectory", defaultValue = "${project.basedir}/src")
    protected File sourceDirectory;
    @Parameter(property = "sourceGlob", defaultValue = "*.{psd,psb}")
    protected String sourceGlob;

    @Parameter(property = "generatePreview", defaultValue = "true")
    protected boolean generatePreview;
    //    @Parameter(property = "overwritePreviewIfExists", defaultValue = "false")
//    private boolean overwritePreviewIfExists;
    @Parameter(property = "previewFile", defaultValue = "${project.build.directory}/preview.jpg")
    protected File previewFile;
    @Parameter(property = "previewQuality", defaultValue = "80")
    protected int previewQuality;
    @Parameter(property = "previewMaxEdgeSize", defaultValue = "1024")
    protected int previewMaxEdgeSize;
    @Parameter(property = "previewCanScaleUp", defaultValue = "false")
    protected boolean previewCanScaleUp;

    //    @Parameter(property = "overwritePanoIfExists", defaultValue = "false")
//    private boolean overwritePanoIfExists;
    @Parameter(property = "generateTiles", defaultValue = "true")
    protected boolean generateTiles;
    @Parameter(property = "targetFolder", defaultValue = "${project.build.directory}")
    protected File targetFolder;
    @Parameter(property = "tileSize", defaultValue = "512")
    protected int tileSize;

    @Parameter(property = "generatePage", defaultValue = "true")
    protected boolean generatePage;
    @Parameter(property = "pageTitle", defaultValue = "Page Title")
    protected String pageTitle;

    @Parameter(property = "generateZip", defaultValue = "true")
    protected boolean generateZip;
    //    @Parameter(property = "overwriteZipIfExists", defaultValue = "false")
//    private boolean overwriteZipIfExists;
//    @Parameter(property = "zipTargetFolder", defaultValue = "${project.build.directory}")
//    protected File zipTargetFolder;

    protected abstract void createPano(SourceImage sourceImage, ViewCalculator.PanoView panoView) throws IOException;

    public void execute() throws MojoExecutionException {

        targetFolder.mkdirs();

        List<File> sourceImages;
        try {
            sourceImages = findSourceImages(sourceDirectory, sourceGlob);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to resolve source(s)", e);
        }

        for (File sourceImage : sourceImages) {
            try {
                handleSourceImage(sourceImage);
            } catch (IOException | ImageProcessingException e) {
                throw new MojoExecutionException("failed", e);
            }
        }
    }

    private List<File> findSourceImages(File sourceDir, String glob) throws IOException {
        List<File> result = new LinkedList<>();

        if (!glob.startsWith("glob:") && !glob.startsWith("regex:")) {
            glob = "glob:" + glob;
        }

        Path sourcepath = Paths.get(sourceDir.toURI());
        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
        Files.walkFileTree(Paths.get(sourceDir.toURI()), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                Path relPath = path.subpath(sourcepath.getNameCount(), path.getNameCount());
                if (pathMatcher.matches(relPath)) {
                    result.add(path.toFile());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });

        return result;
    }

    private ViewCalculator.PanoView findView(File sourceFile) throws IOException, ImageProcessingException {
        return ViewCalculator
                .of(sourceFile)
                .createPanoView()
                .orElseThrow(() -> new IllegalArgumentException("Can not extract viewdata from input image: '" + sourceFile.getAbsolutePath() + "'"));
    }

    private void handleSourceImage(File sourceImage) throws IOException, ImageProcessingException {
        getLog().info("Load image: '" + sourceImage.getAbsolutePath() + "'");

        ViewCalculator.PanoView panoView = findView(sourceImage);
        SourceImage source = SourceImage.of(sourceImage).fov(panoView);

        // PREVIEW
        if (generatePreview) {
            getLog().info("Generate preview at: '" + previewFile.getAbsolutePath() + "'");
            previewFile.getParentFile().mkdirs();
            ITargetImage.Format imageFormat = Utils
                    .findImageFormat(previewFile.getName())
                    .orElseThrow(() -> new UnsupportedOperationException("No Writer for image-format available"));

            ITargetImage targetImage = ImgScaler
                    .of(source)
                    .scaleTo(previewMaxEdgeSize, previewCanScaleUp);
            if (ITargetImage.Format.JPG.equals(imageFormat)) {
                targetImage.saveAsJPG(previewFile, previewQuality);
            } else {
                targetImage.save(previewFile, imageFormat);
            }
            getLog().info("review result is: '" + previewFile.getAbsolutePath() + "");
        }

        // Pano (tiles, index.html, config etc.)
        getLog().info("Generate tiles, index.html, config etc");
        createPano(source, panoView);

        // ZIP
        if (generateZip) {
            File zipFile = new File(targetFolder.getParent(), FilenameUtils.removeExtension(sourceImage.getName()) + ".zip");
            getLog().info("Generate zip archive at: '" + zipFile.getAbsolutePath() + "'");
            targetFolder.mkdirs();
            ZipUtils.compressDirectory(
                    targetFolder,
                    zipFile);
        }
    }
}