package de.zebrajaeger.sphere2cube;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
import de.zebrajaeger.sphere2cube.converter.Sphere2Cube;
import de.zebrajaeger.sphere2cube.httpserver.StaticWebServer;
import de.zebrajaeger.sphere2cube.img.ISourceImage;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.ImgScaler;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.tilenamegenerator.TileNameGenerator;
import de.zebrajaeger.sphere2cube.utils.Stopwatch;
import de.zebrajaeger.sphere2cube.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private Stopwatch stopwatch;
    private String currentTask;
    private BlackImageGenerator blackImageGenerator = BlackImageGenerator.of();

    public void startTask(String task) {
        stopwatch = Stopwatch.fromNow();
        currentTask = task;
        LOG.info("Task '{}' started", currentTask);
    }

    public void stopTask() {
        LOG.info("Task '{}' finished in {}", currentTask, stopwatch.stop().toHumanReadable());
    }

    void clean(File root) throws IOException {
        // clean
        startTask("Clean");
        FileUtils.deleteDirectory(root);
        root.mkdirs();
        stopTask();
    }

    ViewCalculator.PanoView findView(File sourceFile) throws IOException, ImageProcessingException {
        return ViewCalculator
                .of(sourceFile)
                .createPanoView()
                .orElseThrow(() -> new IllegalArgumentException("Can not extract viewdata from input image: '" + sourceFile.getAbsolutePath() + "'"));
    }

    void preview(ISourceImage source, File previewFile) throws IOException {
        startTask("Create preview");
        ITargetImage.Format imageFormat = Utils
                .findImageFormat(previewFile.getName())
                .orElseThrow(() -> new UnsupportedOperationException("No Writer for image-format available"));
        ImgScaler
                .of(source)
                .scaleTo(1024, false)
                .save(previewFile, imageFormat);
        stopTask();
    }

    protected RenderedPano renderTiles(File tileRoot,
                                       SourceImage source,
                                       TileNameGenerator tileNameGenerator,
                                       boolean dryRun,
                                       boolean tileDebug,
                                       boolean tileDebugOverwriteContent) {
        return Sphere2Cube
                .of()
                .dryRun(dryRun)
                .debug(tileDebug, tileDebugOverwriteContent)
                .forceTileRenderingUpToLevel(2)
                .renderConsumer(trf -> {
                    File target = new File(tileRoot, tileNameGenerator.generateName(trf.getTileRenderInfo()));
                    try {
                        trf.getTargetImage().save(target);
                    } catch (IOException e) {
                        LOG.error("Could not save tile: '{}'", target.getAbsolutePath(), e);
                    }
                })
                .noRenderConsumer(trf -> {
                    System.out.print(".");
//                    File target = new File(tileRoot, tileNameGenerator.generateName(trf.getTileRenderInfo()));
//                    try {
//                        blackImageGenerator.writeToFile(
//                                trf.getTileRenderInfo().getTileEdgeX(),
//                                trf.getTileRenderInfo().getTileEdgeY(),
//                                target);
//                    } catch (IOException e) {
//                        LOG.error("Could not save tile: '{}'", target.getAbsolutePath(), e);
//                    }
                })
                .renderPano(source, 512);
    }

    void server(File root) throws IOException {
        server(root, null);
    }

    void server(File root, File darkImage) throws IOException {
        LOG.info("Start http server with docroot: '{}'", root.getAbsolutePath());
        StaticWebServer.of(root)
                .darkImage(darkImage)
                .start()
                .openBrowser();
    }

    void finish() {
        LOG.info(blackImageGenerator.toString());
    }
}
