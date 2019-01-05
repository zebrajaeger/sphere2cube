package de.zebrajaeger.sphere2cube.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.FileUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public class StaticWebServer {
    private File serverRoot;
    private File darkImage;

    private int port = 80;
    private HttpServer httpsServer;

    public static StaticWebServer of(File serverRoot) {
        return new StaticWebServer(serverRoot);
    }
    public static StaticWebServer of(String serverRoot) {
        return new StaticWebServer(new File(serverRoot));
    }

    private StaticWebServer(File serverRoot) {
        this.serverRoot = serverRoot;
    }

    public StaticWebServer start() throws IOException {
        httpsServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpsServer.createContext("/", new MyHandler());
        httpsServer.start();
        return this;
    }

    public StaticWebServer darkImage(File darkImage) {
        this.darkImage = darkImage;
        return this;
    }

    public StaticWebServer darkImage(String darkImage) {
        return darkImage(new File(darkImage));
    }

    public StaticWebServer stop() {
        httpsServer.stop(0);
        return this;
    }

    public StaticWebServer openBrowser() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost/"));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("ubale to open browser", e);
            }
        }
        return this;
    }

    public class MyHandler implements HttpHandler {

        private void handle404(HttpExchange httpExchange) throws IOException {
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.getResponseBody().close();
        }

        private void handleDirectory(HttpExchange httpExchange, File file) throws IOException {
            handleFile(httpExchange, new File(file, "index.html"));
        }

        private void handleFile(HttpExchange httpExchange, File file) throws IOException {
            if (!file.exists()) {
                handle404(httpExchange);
            } else {
                sendFile(httpExchange, file);
            }
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String path = httpExchange.getRequestURI().toString();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            path = path.replace("/../", "");

            File source = new File(serverRoot, path);
            if (source.exists()) {
                if (source.isDirectory()) {
                    handleDirectory(httpExchange, source);
                } else {
                    handleFile(httpExchange, source);
                }
            } else {
                if (path.endsWith(".png") && darkImage != null) {
                    sendFile(httpExchange, darkImage);
                } else {
                    handle404(httpExchange);
                }
            }
        }
    }

    private void sendFile(HttpExchange httpExchange, File file) throws IOException {
        String mime = URLConnection.guessContentTypeFromName(file.getName());
        if (mime != null) {
            httpExchange.getResponseHeaders().set("Content-Type", mime);
        }
        httpExchange.sendResponseHeaders(200, file.length());
        try (OutputStream os = httpExchange.getResponseBody()) {
            FileUtils.copyFile(file, os);
        }
    }
}
