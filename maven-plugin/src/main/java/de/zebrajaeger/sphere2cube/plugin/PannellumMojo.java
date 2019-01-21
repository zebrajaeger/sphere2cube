package de.zebrajaeger.sphere2cube.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.blackimages.BlackImageGenerator;
import de.zebrajaeger.sphere2cube.blackimages.ImagesAndSizes;
import de.zebrajaeger.sphere2cube.blackimages.PanoImage;
import de.zebrajaeger.sphere2cube.blackimages.PanoImages;
import de.zebrajaeger.sphere2cube.converter.Sphere2Cube;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtmGeneratorPannellum;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.tilenamegenerator.PannellumTileNameGenerator;
import de.zebrajaeger.sphere2cube.tilenamegenerator.TileNameGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
@SuppressWarnings("unused")
@Mojo(name = "pannellum", requiresProject = false, defaultPhase = LifecyclePhase.COMPILE)
public class PannellumMojo extends PanoMojo {

    @SuppressWarnings("Unused")
    @Parameter(property = "pannellumSaveImages", defaultValue = "true") // TODO change me to true
    private boolean pannellumSaveImages;
    @SuppressWarnings("Unused")
    @Parameter(property = "pannellumSaveBlackImages", defaultValue = "false") // TODO change me to true
    private boolean pannellumSaveBlackImages;
    @SuppressWarnings("Unused")
    @Parameter(property = "pannellumDownscaleOriginal", defaultValue = "false") // TODO change me to true
    private boolean pannellumDownscaleOriginal;
    @SuppressWarnings("Unused")
    @Parameter(property = "pannellumPanoTitle")
    private String pannellumPanoTitle;
    @SuppressWarnings("Unused")
    @Parameter(property = "pannellumPanoAuthor")
    private String pannellumPanoAuthor;

    @Parameter(property = "krPanoViewerScript")
    private File krPanoViewerScript;
    @Parameter(property = "krPanoSkinFolder")
    private File krPanoSkinFolder;

    @Parameter(property = "generateImageInfoFile", defaultValue = "true")
    protected boolean generateImageInfoFile;
    @Parameter(property = "imageInfoFile", defaultValue = "${project.build.directory}/{{imageName}}/images.json")
    protected String imageInfoFile;
    @Parameter(property = "blackImagesInfoFile", defaultValue = "${project.build.directory}/{{imageName}}/images.black.json")
    protected String blackImagesInfoFile;
    @Parameter(property = "contentImagesInfoFile", defaultValue = "${project.build.directory}/{{imageName}}/images.content.json")
    protected String contentImagesInfoFile;

    @Parameter(property = "generateBlackImageReferences", defaultValue = "true")
    protected boolean generateBlackImageReferences;
    @Parameter(property = "blackImagesFilesRoot", defaultValue = "${project.build.directory}/{{imageName}}")
    protected String blackImagesFilesRoot;
    @Parameter(property = "blackImagesFileName", defaultValue = "tiles/black/{{w}}x{{h}}.png")
    protected String blackImagesFileName;

    @Override
    protected void createPano(String imageName, SourceImage sourceImage, ViewCalculator.PanoView panoView) throws IOException {
        TemplateEngine te = TemplateEngine.of().with("imageName", imageName);

        if (generateTiles || generatePage) {
            // Tiles
            PanoImages panoImages = new PanoImages();

            BlackImageGenerator finalBlackImageGenerator = BlackImageGenerator.of();
            TileNameGenerator finalTileNameGenerator = PannellumTileNameGenerator.of();
            File finalPanoTargetFolder = te.convertToFileAndCreateDirectories(tilesFolder, false);
            AtomicLong count = new AtomicLong(0);

            RenderedPano renderedPano = Sphere2Cube
                    .of()
                    .debug(false, false)
                    .renderTiles(generateTiles)
                    .forceTileRenderingUpToLevel(2)
                    .downscale(pannellumDownscaleOriginal)
                    .renderConsumer(trf -> {
                        String name = finalTileNameGenerator.generateName(trf.getTileRenderInfo());
                        count.incrementAndGet();
                        if (generateImageInfoFile) {
                            panoImages.add(new PanoImage(trf, name, PanoImage.Type.IMAGE));
                        }
                        if (pannellumSaveImages) {
                            File target = new File(finalPanoTargetFolder, name);
                            try {
                                trf.getTargetImage().save(target);
                            } catch (IOException e) {
                                getLog().error(String.format("Could not save tile: '%s'", target.getAbsolutePath()), e);
                            }
                        }
                    })
                    .noRenderConsumer(trf -> {
                        String name = finalTileNameGenerator.generateName(trf.getTileRenderInfo());
                        count.incrementAndGet();
                        if (generateImageInfoFile) {
                            panoImages.add(new PanoImage(trf, name, PanoImage.Type.BLACK));
                        }
                        if (pannellumSaveBlackImages) {
                            File target = new File(finalPanoTargetFolder, name);
                            try {
                                finalBlackImageGenerator.writeToFile(
                                        trf.getTileRenderInfo().getTileEdgeX(),
                                        trf.getTileRenderInfo().getTileEdgeY(),
                                        target);
                            } catch (IOException e) {
                                getLog().error(String.format("Could not save tile: '%s'", target.getAbsolutePath()), e);
                            }
                        }
                    })
                    .renderPano(sourceImage, tileSize);

            getLog().info("Images created: " + count);

            // imageinfo
            panoImages.raster(layerFaceRaster -> {
                getLog().info(String.format("---------- %s / %s ----------", layerFaceRaster.getLayer(), layerFaceRaster.getFace()));
                getLog().info(layerFaceRaster.getRaster().toString());
            });

            ObjectMapper mapper = new ObjectMapper();

            ImagesAndSizes blackImagesAndSizes =
                    ImagesAndSizes.of(pi -> te.with("w", pi.getWidth()).with("h", pi.getHeight()).convert(blackImagesFileName))
                            .panoImages(panoImages.getPanoImagesWithType(PanoImage.Type.BLACK));
            if (generateImageInfoFile) {
                // all images
                mapper.writeValue(te.convertToFileAndCreateDirectories(imageInfoFile, true), panoImages);

                // black image list
                mapper.writeValue(te.convertToFileAndCreateDirectories(blackImagesInfoFile, true), blackImagesAndSizes);

                // content image list
                List<String> contentImageNamesList =
                        panoImages.getPanoImagesWithType(PanoImage.Type.IMAGE)
                        .stream()
                        .map(panoImage -> panoImage.getPath())
                        .collect(Collectors.toList());
                mapper.writeValue(te.convertToFileAndCreateDirectories(contentImagesInfoFile, true), contentImageNamesList);
            }

            if (generateBlackImageReferences) {
                // write black reference images
                blackImagesAndSizes.getReferences().values().forEach(reference -> {
                    File target = new File(te.convertToFile(blackImagesFilesRoot), reference.getPath());
                    target.getParentFile();
                    try {
                        getLog().info("Create black reference file: '" + target.getAbsolutePath() + "'");
                        finalBlackImageGenerator.writeToFile(
                                reference.getW(),
                                reference.getH(),
                                target);
                    } catch (IOException e) {
                        getLog().error("Could not create black image: '" + target.getAbsolutePath() + "'", e);
                    }
                });
            }

            // page - pannellum
            if (generatePage) {
                File convertedPageFile = te.convertToFileAndCreateDirectories(pageFile, true);
                String indexHtml = IndexHtmGeneratorPannellum
                        .of()
                        .generate(
                                IndexHtmGeneratorPannellum.IndexHtml
                                        .of()
                                        .path("tiles", "/%l/%s/%y_%x", "png")
                                        .resolution(renderedPano.getMaxLevel().getTargetEdge(), 512, renderedPano.getMaxLevel().getIndex())
                                        .fov(panoView.getFovX1(), panoView.getFovX2(), panoView.getFovY1Inv(), panoView.getFovY2Inv())
                                        .meta(pageTitle, pannellumPanoTitle, pannellumPanoAuthor)
                                        .auto(true, 1)
                        );
                FileUtils.write(convertedPageFile, indexHtml, StandardCharsets.UTF_8);
            }

            // page - krpano
            // does not work with pannellum tiles. Pannellum uses index counters (0..n) whil krpano uses a count counter (1..n) and there is no way to configure this
//            if (generatePage) {
//                File panoXmlFile = new File(targetFolder, "pano.xml");
//                String panoXml = PanoXmlGenerator
//                        .of()
//                        .variable("view", View.of().maxpixelzoom(10d))
//                        .generate(renderedPano);
//                FileUtils.write(panoXmlFile, panoXml, StandardCharsets.UTF_8);
//
//                // index.html
//                File indexHtmlFile = new File(targetFolder, "krpano.index.html");
//                String indexHtml = IndexHtmGeneratorKrPano.of().generate(new IndexHtmGeneratorKrPano.IndexHtml(pageTitle));
//                FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);
//
//                if (krPanoViewerScript != null && krPanoViewerScript.exists()) {
//                    FileUtils.copyFile(krPanoViewerScript, new File(targetFolder, "krpano.js"));
//                }
//                if (krPanoSkinFolder != null && krPanoSkinFolder.exists()) {
//                    FileUtils.copyDirectory(krPanoSkinFolder, new File(targetFolder, "skin"));
//                }
//            }
        }
    }
}