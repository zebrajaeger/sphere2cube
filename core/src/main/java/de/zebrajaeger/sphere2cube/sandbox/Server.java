package de.zebrajaeger.sphere2cube.sandbox;

import de.zebrajaeger.sphere2cube.httpserver.StaticWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        File root = new File("target/pano");
        // start http server
        LOG.info("Start http server with docroot: '{}'", root.getAbsolutePath());
        StaticWebServer.of(root)
                .darkImage("assets/dark.png")
                .start()
                .openBrowser();
    }
}
