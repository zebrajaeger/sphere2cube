package de.zebrajaeger.sphere2cube;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtmGeneratorKrPano;
import de.zebrajaeger.sphere2cube.panoxml.PanoXmlGenerator;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.result.View;
import de.zebrajaeger.sphere2cube.tilenamegenerator.KrPanoTileNameGenerator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("Duplicates")
public class AppKrPano extends App {
    private static final Logger LOG = LoggerFactory.getLogger(AppKrPano.class);

    public static void main(String[] args) throws IOException, ImageProcessingException {
        new AppKrPano().process(args);
    }

    protected void process(String[] args) throws IOException, ImageProcessingException {
        File sourceFile = new File("samples/sylvester[S][35.60x4.95(-14.99)].psb");
        File root = new File("target/pano");
        File tileRoot = new File(root, "tiles");
        File assetsRoot = new File("assets");

        clean(root);

        // copy assets
        startTask("Copy assets");
        FileUtils.copyDirectory(new File(assetsRoot, "skin"), new File(root, "skin"));
        FileUtils.copyFile(new File(assetsRoot, "krpano.js"), new File(root, "krpano.js"));
        stopTask();

        // render pano
        // TODO check that all needed values are available (at last fovX and fovY)
        // TODO check projection == equirectangular
        startTask("Render tile");
        ViewCalculator.PanoView panoView = findView(sourceFile);
        SourceImage source = SourceImage.of(sourceFile).fov(panoView);
        RenderedPano renderedPano = renderTiles(tileRoot, source, KrPanoTileNameGenerator.of());
        stopTask();

        // preview
        preview(source, new File(root, "preview.jpg"));

        // pano.xml
        startTask("Create krpano config");
        File panoXmlFile = new File(root, "pano.xml");
        String panoXml = PanoXmlGenerator
                .of()
                .variable("view", View.of().maxpixelzoom(10d))
                .generate(renderedPano);
        FileUtils.write(panoXmlFile, panoXml, StandardCharsets.UTF_8);
        stopTask();

        // index.html
        File indexHtmlFile = new File(root, "index.html");
        startTask("Create index.html");
        String indexHtml = IndexHtmGeneratorKrPano.of().generate(new IndexHtmGeneratorKrPano.IndexHtml("TestPano"));
        FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);
        stopTask();

        // start http server
        server(root, new File(assetsRoot, "dark.png"));
    }
}
