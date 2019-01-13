package de.zebrajaeger.sphere2cube.plugin;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.BlackImageGenerator;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.converter.Sphere2Cube;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.ImgScaler;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtmGeneratorPannellum;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.tilenamegenerator.PannellumTileNameGenerator;
import de.zebrajaeger.sphere2cube.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
@Mojo(name = "pano", requiresProject = false, defaultPhase = LifecyclePhase.COMPILE)
public class PanoMojo extends AbstractMojo {

    @Component
    private MojoExecution execution;

    @Parameter(property = "sourceDirectory", defaultValue = "${project.basedir}/src")
    private File sourceDirectory;
    @Parameter(property = "sourceGlob", defaultValue = "*.{psd,psb}")
    private String sourceGlob;

    @Parameter(property = "generatePreview", defaultValue = "true")
    private boolean generatePreview;
//    @Parameter(property = "overwritePreviewIfExists", defaultValue = "false")
//    private boolean overwritePreviewIfExists;
    @Parameter(property = "previewFile", defaultValue = "${project.build.directory}/preview.jpg")
    private File previewFile;
    @Parameter(property = "previewQuality", defaultValue = "80")
    private int previewQuality;

    @Parameter(property = "generatePannellum", defaultValue = "true")
    private boolean generatePannellum;
//    @Parameter(property = "overwritePannellumIfExists", defaultValue = "false")
//    private boolean overwritePannellumIfExists;
    @Parameter(property = "pannellumTileSize", defaultValue = "512")
    private int pannellumTileSize;
    @Parameter(property = "pannellumTargetFolder", defaultValue = "${project.build.directory}")
    private File pannellumTargetFolder;
    @Parameter(property = "pannellumPageTitle", defaultValue = "Page Title")
    private String pannellumPageTitle;
    @Parameter(property = "pannellumPanoTitle")
    private String pannellumPanoTitle;
    @Parameter(property = "pannellumPanoAuthor")
    private String pannellumPanoAuthor;

//    @Parameter(property = "generatePano", defaultValue = "true")
//    private boolean generatePano;
//    @Parameter(property = "overwritePanoIfExists", defaultValue = "false")
//    private boolean overwritePanoIfExists;
//    @Parameter(property = "panoTargetFolder", defaultValue = "${project.build.directory}")
//    private File panoTargetFolder;
//    @Parameter(property = "krPanoExe", defaultValue = "${krpano.exe}")
//    private File krPanoExe;
//    @Parameter(property = "krPanoConfig", defaultValue = "${krpano.config}")
//    private File krPanoConfig;
//    @Parameter(property = "krPanoRenderTimeout", defaultValue = "7200")
//    private long krPanoRenderTimeout;

    @Parameter(property = "generateZip", defaultValue = "true")
    private boolean generateZip;
    @Parameter(property = "overwriteZipIfExists", defaultValue = "false")
    private boolean overwriteZipIfExists;
    @Parameter(property = "zipTargetFolder", defaultValue = "${project.build.directory}")
    private File zipTargetFolder;

//    @Parameter(property = "modifyPanoConfig", defaultValue = "true")
//    private boolean modifyPanoConfig;

    public void execute() throws MojoExecutionException {

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

    ViewCalculator.PanoView findView(File sourceFile) throws IOException, ImageProcessingException {
        return ViewCalculator
                .of(sourceFile)
                .createPanoView()
                .orElseThrow(() -> new IllegalArgumentException("Can not extract viewdata from input image: '" + sourceFile.getAbsolutePath() + "'"));
    }

    private void handleSourceImage(File sourceImage) throws MojoExecutionException, IOException, ImageProcessingException {
        ViewCalculator.PanoView panoView = findView(sourceImage);
        SourceImage source = SourceImage.of(sourceImage).fov(panoView);

        if (generatePreview) {
            ITargetImage.Format imageFormat = Utils
                    .findImageFormat(previewFile.getName())
                    .orElseThrow(() -> new UnsupportedOperationException("No Writer for image-format available"));

            ITargetImage targetImage = ImgScaler
                    .of(source)
                    .scaleTo(1024, false);
            if (ITargetImage.Format.JPG.equals(imageFormat)) {
                targetImage.saveAsJPG(previewFile, previewQuality);
            } else {
                targetImage.save(previewFile, imageFormat);
            }
        }

        if (generatePannellum) {
            PannellumTileNameGenerator tileNameGenerator = PannellumTileNameGenerator.of();
            BlackImageGenerator blackImageGenerator = BlackImageGenerator.of()
            RenderedPano renderedPano = Sphere2Cube
                    .of()
                    .debug(false, false)
                    .forceTileRenderingUpToLevel(2)
                    .renderConsumer(trf -> {
                        File target = new File(pannellumTargetFolder, tileNameGenerator.generateName(trf.getTileRenderInfo()));
                        try {
                            trf.getTargetImage().save(target);
                        } catch (IOException e) {
                            getLog().error(String.format("Could not save tile: '%s'", target.getAbsolutePath()), e);
                        }
                    })
                    .noRenderConsumer(trf -> {
                        File target = new File(pannellumTargetFolder, tileNameGenerator.generateName(trf.getTileRenderInfo()));
                        try {
                            blackImageGenerator.writeToFile(
                                    trf.getTileRenderInfo().getTileEdgeX(),
                                    trf.getTileRenderInfo().getTileEdgeY(),
                                    target);
                        } catch (IOException e) {
                            getLog().error(String.format("Could not save tile: '{}'", target.getAbsolutePath()), e);
                        }
                    })
                    .renderPano(source, pannellumTileSize);

            File indexHtmlFile = new File(pannellumTargetFolder, "index.html");
            String indexHtml = IndexHtmGeneratorPannellum
                    .of()
                    .generate(
                            IndexHtmGeneratorPannellum.IndexHtml
                                    .of()
                                    .path("tiles", "/%l/%s/%y_%x", "png")
                                    .resolution(renderedPano.getMaxLevel().getTargetEdge(), 512, renderedPano.getMaxLevel().getIndex())
                                    .fov(panoView.getFovX1(), panoView.getFovX2(), panoView.getFovY1Inv(), panoView.getFovY2Inv())
                                    .meta(pannellumPageTitle, pannellumPanoTitle, pannellumPanoAuthor)
                                    .auto(true, 1)
                    );
            FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);
        }


    }
}