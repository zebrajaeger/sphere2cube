package de.zebrajaeger.sphere2cube.sandbox;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtmGeneratorPannellum;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.tilenamegenerator.PannellumTileNameGenerator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("Duplicates")
public class AppPannellum extends App {
    private static final Logger LOG = LoggerFactory.getLogger(AppPannellum.class);

    public static void main(String[] args) throws IOException, ImageProcessingException {
        new AppPannellum().process(args);
    }

    protected void process(String[] args) throws IOException, ImageProcessingException {

        boolean clean = true;
        boolean tiles = true;
        boolean preview = false;
        boolean server = true;
        boolean tileDebug = false;

        //File sourceFile = new File("samples/test.psb");
        File sourceFile = new File("samples/sylvester[S][35.60x4.95(-14.99)].psb");
        File root = new File("target/pano");
        File tileRoot = new File(root, "tiles");

        if (clean) {
            clean(root);
        }

        // read image
        // TODO check that all needed values are available (at last fovX and fovY)
        // TODO check projection == equirectangular
        startTask("Load source image");
        ViewCalculator.PanoView panoView = findView(sourceFile);
        SourceImage source = SourceImage.of(sourceFile).fov(panoView);
        LOG.info(panoView.toString());
        stopTask();

        // preview
        if (preview) {
            preview(source, new File(root, "preview.jpg"));
        }

        // render tiles
        startTask("Render tile");
        RenderedPano renderedPano = renderTiles(
                tileRoot,
                source,
                PannellumTileNameGenerator.of(),
                !tiles,
                tileDebug,
                false);
        stopTask();

        // index.html
        File indexHtmlFile = new File(root, "index.html");
        startTask("Create index.html");
        String indexHtml = IndexHtmGeneratorPannellum
                .of()
                .generate(
                        IndexHtmGeneratorPannellum.IndexHtml
                                .of()
                                .path("tiles", "/%l/%s/%y_%x", "png")
                                .resolution(renderedPano.getMaxLevel().getTargetEdge(), 512, renderedPano.getMaxLevel().getIndex())
                                .fov(panoView.getFovX1(), panoView.getFovX2(), panoView.getFovY1Inv(), panoView.getFovY2Inv())
                                .meta("test", "test", "Lars")
                                .auto(true, 1)
                );
        FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);
        stopTask();

        finish();

        // start http server
//        File assetsRoot = new File("assets");
//        server(root, new File(assetsRoot, "dark.png"));
        if (server) {
            server(root);
        }
    }
}
