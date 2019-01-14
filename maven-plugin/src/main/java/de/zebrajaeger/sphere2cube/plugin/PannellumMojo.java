package de.zebrajaeger.sphere2cube.plugin;

import de.zebrajaeger.sphere2cube.BlackImageGenerator;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
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

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
@SuppressWarnings("unused")
@Mojo(name = "pannellum", requiresProject = false, defaultPhase = LifecyclePhase.COMPILE)
public class PannellumMojo extends PanoMojo {

    @SuppressWarnings("Unused")
    @Parameter(property = "pannellumPanoTitle")
    private String pannellumPanoTitle;
    @SuppressWarnings("Unused")
    @Parameter(property = "pannellumPanoAuthor")
    private String pannellumPanoAuthor;

    @Override
    protected void createPano(SourceImage sourceImage, ViewCalculator.PanoView panoView) throws IOException {
        BlackImageGenerator blackImageGenerator = BlackImageGenerator.of();
        TileNameGenerator finalTileNameGenerator = PannellumTileNameGenerator.of();
        File finalPanoTargetFolder = targetFolder;
        RenderedPano renderedPano = Sphere2Cube
                .of()
                .debug(false, false)
                .forceTileRenderingUpToLevel(2)
                .renderConsumer(trf -> {
                    File target = new File(finalPanoTargetFolder, finalTileNameGenerator.generateName(trf.getTileRenderInfo()));
                    try {
                        trf.getTargetImage().save(target);
                    } catch (IOException e) {
                        getLog().error(String.format("Could not save tile: '%s'", target.getAbsolutePath()), e);
                    }
                })
                .noRenderConsumer(trf -> {
                    File target = new File(finalPanoTargetFolder, finalTileNameGenerator.generateName(trf.getTileRenderInfo()));
                    try {
                        blackImageGenerator.writeToFile(
                                trf.getTileRenderInfo().getTileEdgeX(),
                                trf.getTileRenderInfo().getTileEdgeY(),
                                target);
                    } catch (IOException e) {
                        getLog().error(String.format("Could not save tile: '%s'", target.getAbsolutePath()), e);
                    }
                })
                .renderPano(sourceImage, tileSize);

        File indexHtmlFile = new File(targetFolder, "index.html");
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
        FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);
    }
}