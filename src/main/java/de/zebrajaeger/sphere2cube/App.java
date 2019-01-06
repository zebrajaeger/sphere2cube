package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.converter.Sphere2Cube;
import de.zebrajaeger.sphere2cube.httpserver.StaticWebServer;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args) throws IOException, URISyntaxException {
            File root = new File("target/pano");
            // clean
            FileUtils.deleteDirectory(root);
            root.mkdirs();

            // copy assets
            FileUtils.copyDirectory(new File("assets/skin"), new File("target/pano/skin"));
            FileUtils.copyFile(new File("assets/krpano.js"), new File("target/pano/krpano.js"));

            // render pano
            Sphere2Cube s2c = new Sphere2Cube("target/pano/tiles/%s/l%l/%000y_%000x.png");
            s2c.renderPano(
                    //SourceImage.of("samples/raster(5000x2500).png").fov(180d, 0d, 90d, 0d),
                    //SourceImage.of("samples/pano1(5376x2688).jpg").fov(180d, 0d, 90d, 0d),
                    SourceImage.of("samples/sylvester[S][35.60x4.95(-14.99)].psb").fov(35.6, 0d, 4.95d, 0d),
                    new File("target/pano/pano.xml"),
                    new File("target/pano/index.html"),
                    new File("target/pano/preview.jpg"),
                    512,
                    1024);

        // start http server
        StaticWebServer.of("target/pano")
                .darkImage("assets/dark.png")
                .start()
                .openBrowser();
    }
}
