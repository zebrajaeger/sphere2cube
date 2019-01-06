package de.zebrajaeger.sphere2cube;

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

        File sourceFile = new File("samples/sylvester[S][35.60x4.95(-14.99)].psb");
        File root = new File("target/pano");
        File tileRoot = new File(root, "tiles");
        File assetsRoot = new File("assets");

        clean(root);

        // render pano
        // TODO check that all needed values are available (at last fovX and fovY)
        // TODO check projection == equirectangular
        startTask("Render tile");
        ViewCalculator.PanoView panoView = findView(sourceFile);
        SourceImage source = SourceImage.of(sourceFile).fov(panoView);
        RenderedPano renderedPano = renderTiles(tileRoot, source, PannellumTileNameGenerator.of());
        stopTask();

        // preview
        preview(source, new File(root, "preview.jpg"));

        // index.html
        File indexHtmlFile = new File(root, "index.html");
        startTask("Create index.html");
        String indexHtml = IndexHtmGeneratorPannellum.of().generate(
                new IndexHtmGeneratorPannellum.IndexHtml(
                        "TestPano",
                        "tiles",
                        "/%l/%s%y_%x",
                        "png",
                        512,
                        renderedPano.getMaxLevel().getIndex(),
                        renderedPano.getMaxLevel().getW())
        );
        FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);
        stopTask();

        // start http server
        server(root, new File(assetsRoot, "dark.png"));
//        server(root);
    }
}
