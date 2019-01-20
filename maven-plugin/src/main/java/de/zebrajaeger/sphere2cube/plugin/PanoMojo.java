package de.zebrajaeger.sphere2cube.plugin;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.ImgScaler;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.utils.Utils;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public abstract class PanoMojo extends BaseMojo {

    @Parameter(property = "generatePreview", defaultValue = "true")
    protected boolean generatePreview;
    //    @Parameter(property = "overwritePreviewIfExists", defaultValue = "false")
//    private boolean overwritePreviewIfExists;
    @Parameter(property = "previewFile", defaultValue = "${project.build.directory}/{{imageName}}/preview.jpg")
    protected String previewFile;
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
    @Parameter(property = "tilesFolder", defaultValue = "${project.build.directory}/{{imageName}}/tiles")
    protected String tilesFolder;
    @Parameter(property = "tileSize", defaultValue = "512")
    protected int tileSize;

    @Parameter(property = "generatePage", defaultValue = "true")
    protected boolean generatePage;
    @Parameter(property = "pageFile", defaultValue = "${project.build.directory}/{{imageName}}/index.html")
    protected String pageFile;
    @Parameter(property = "pageTitle", defaultValue = "Page Title")
    protected String pageTitle;

    protected abstract void createPano(String imageName, SourceImage sourceImage, ViewCalculator.PanoView panoView) throws IOException;

    protected void handleSourceImage(File sourceImage) throws IOException, ImageProcessingException {
        String imageName = FileUtils.removeExtension(sourceImage.getName());
        TemplateEngine te = TemplateEngine.of().with("imageName", imageName);

        getLog().info("Load image: '" + sourceImage.getAbsolutePath() + "'");

        ViewCalculator.PanoView panoView = findView(sourceImage);
        SourceImage source = SourceImage.of(sourceImage).fov(panoView);

        // PREVIEW
        if (generatePreview) {
            File convertedPreviewFile = te.convertToFileAndCreateDirectories(previewFile, true);
            getLog().info("Generate preview at: '" + convertedPreviewFile.getAbsolutePath() + "'");
            convertedPreviewFile.getParentFile().mkdirs();
            ITargetImage.Format imageFormat = Utils
                    .findImageFormat(convertedPreviewFile.getName())
                    .orElseThrow(() -> new UnsupportedOperationException("No Writer for image-format available"));

            ITargetImage targetImage = ImgScaler
                    .of(source)
                    .scaleTo(previewMaxEdgeSize, previewCanScaleUp);
            if (ITargetImage.Format.JPG.equals(imageFormat)) {
                targetImage.saveAsJPG(convertedPreviewFile, previewQuality);
            } else {
                targetImage.save(convertedPreviewFile, imageFormat);
            }
            getLog().info("review result is: '" + convertedPreviewFile.getAbsolutePath() + "");
        }

        // Pano (tiles, index.html, config etc.)
        getLog().info("Generate tiles, index.html, config etc");
        createPano(imageName, source, panoView);
    }
}