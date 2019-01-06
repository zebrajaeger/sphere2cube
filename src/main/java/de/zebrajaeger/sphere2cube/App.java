package de.zebrajaeger.sphere2cube;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.converter.Sphere2Cube;
import de.zebrajaeger.sphere2cube.httpserver.StaticWebServer;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.ImgScaler;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtmGenerator;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtml;
import de.zebrajaeger.sphere2cube.panoxml.PanoXmlGenerator;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.result.View;
import de.zebrajaeger.sphere2cube.tilenamegenerator.KrPanoTileNameGenerator;
import de.zebrajaeger.sphere2cube.utils.Stopwatch;
import de.zebrajaeger.sphere2cube.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, ImageProcessingException {
        File root = new File("target/pano");
        Stopwatch stopwatch;

        // clean
        stopwatch = Stopwatch.fromNow();
        LOG.info("Clean");
        FileUtils.deleteDirectory(root);
        root.mkdirs();
        LOG.info("finished in {}", stopwatch.stop().toHumanReadable());

        // copy assets
        stopwatch = Stopwatch.fromNow();
        LOG.info("Copy assets");
        FileUtils.copyDirectory(new File("assets/skin"), new File("target/pano/skin"));
        FileUtils.copyFile(new File("assets/krpano.js"), new File("target/pano/krpano.js"));
        LOG.info("finished in {}", stopwatch.stop().toHumanReadable());

        // render pano
        stopwatch = Stopwatch.fromNow();
        LOG.info("Render tile");
        File sourceFile = new File("samples/sylvester[S][35.60x4.95(-14.99)].psb");
        ViewCalculator.PanoView panoView = ViewCalculator
                .of(sourceFile)
                .createPanoView()
                .orElseThrow(() -> new IllegalArgumentException("Can not extract viewdata from input image: '" + sourceFile.getAbsolutePath() + "'"));

        // TODO check that all needed values are available (at last fovX and fovY)
        // TODO check projection == equirectangular
        Sphere2Cube s2c = Sphere2Cube.of(KrPanoTileNameGenerator.of("target/pano/tiles/%s/l%l/%000y_%000x.png"));
        SourceImage source = SourceImage.of(sourceFile).fov(panoView);
        RenderedPano renderedPano = s2c.renderPano(source, 512, 1024, View.of().maxpixelzoom(10d));
        LOG.info("finished in {}", stopwatch.stop().toHumanReadable());

        // preview
        stopwatch = Stopwatch.fromNow();
        File previewFile = new File("target/pano/preview.jpg");
        LOG.info("Create preview: '{}'", previewFile.getAbsolutePath());
        ITargetImage.Format imageFormat = Utils
                .findImageFormat(previewFile.getName())
                .orElseThrow(() -> new UnsupportedOperationException("No Writer for image-format available"));
        ImgScaler
                .of(source)
                .scaleTo(1024, false)
                .save(previewFile, imageFormat);
        LOG.info("finished in {}", stopwatch.stop().toHumanReadable());

        // pano.xml
        stopwatch = Stopwatch.fromNow();
        File panoXmlFile = new File("target/pano/pano.xml");
        LOG.info("Create krpano config: '{}'", panoXmlFile.getAbsolutePath());
        String panoXml = PanoXmlGenerator.of().generate(renderedPano);
        FileUtils.write(panoXmlFile, panoXml, StandardCharsets.UTF_8);
        LOG.info("finished in {}", stopwatch.stop().toHumanReadable());

        // index.html
        stopwatch = Stopwatch.fromNow();
        File indexHtmlFile = new File("target/pano/index.html");
        LOG.info("Create index.html: '{}'", indexHtmlFile.getAbsolutePath());
        String indexHtml = IndexHtmGenerator.of().generate(new IndexHtml("TestPano"));
        FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);
        LOG.info("finished in {}", stopwatch.stop().toHumanReadable());

        // start http server
        LOG.info("Start http server with docroot: '{}'", root.getAbsolutePath());
        StaticWebServer.of(root)
                .darkImage("assets/dark.png")
                .start()
                .openBrowser();
    }
}
