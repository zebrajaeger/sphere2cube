package de.zebrajaeger.sphere2cube.plugin;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

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

public abstract class BaseMojo extends AbstractMojo {
    @Parameter(property = "sourceDirectory", defaultValue = "${project.basedir}/src")
    protected File sourceDirectory;
    @Parameter(property = "sourceGlob", defaultValue = "*.{psd,psb}")
    protected String sourceGlob;

    protected abstract void handleSourceImage(File sourceImage) throws IOException, ImageProcessingException;

    private File convertAndCreateDirectories(String templateString, String imageName, boolean isFile) {
        JtwigTemplate template = JtwigTemplate.inlineTemplate(templateString);
        JtwigModel model = JtwigModel.newModel().with("imageName", imageName);
        File result = new File(template.render(model));
        if (!result.exists()) {
            if (isFile) {
                result.getParentFile().mkdirs();
            } else {
                result.mkdirs();
            }
        }
        return result;
    }

    public void execute() throws MojoExecutionException {

        //targetFolder.mkdirs();

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

    protected ViewCalculator.PanoView findView(File sourceFile) throws IOException, ImageProcessingException {
        return ViewCalculator
                .of(sourceFile)
                .createPanoView()
                .orElseThrow(() -> new IllegalArgumentException("Can not extract viewdata from input image: '" + sourceFile.getAbsolutePath() + "'"));
    }
}
