package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.converter.Sphere2Cube;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args) throws IOException, URISyntaxException {
        if (true) {
            // clean
            FileUtils.deleteDirectory(new File("target/pano"));

            // copy assets
            FileUtils.copyDirectory(new File("samples/skin"), new File("target/pano/skin"));
            FileUtils.copyFile(new File("samples/krpano.js"), new File("target/pano/krpano.js"));
            FileUtils.copyFile(new File("samples/tour_testingserver.exe"), new File("target/pano/tour_testingserver.exe"));

            // render pano
            Sphere2Cube s2c = new Sphere2Cube("target/pano/tiles/%s/l%l/%000y_%000x.png");
            //s2c.renderPano(new File("samples/buckingham.jpg"), 512);
            s2c.renderPano(
                    new File("samples/raster(5000x2500).png"),
                    new File("target/pano/pano.xml"),
                    new File("target/pano/index.html"),
                    512);

        }

        // start http server
        StaticWebServer.of(new File("target/pano"))
                .start()
                .openBrowser();
    }
}
