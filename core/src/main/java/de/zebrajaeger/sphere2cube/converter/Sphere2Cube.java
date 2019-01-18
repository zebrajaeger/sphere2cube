package de.zebrajaeger.sphere2cube.converter;

import de.zebrajaeger.sphere2cube.img.ISourceImage;
import de.zebrajaeger.sphere2cube.result.Level;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.utils.Stopwatch;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Single equirectangular spherical Image to multible cube images
 */
@SuppressWarnings("Duplicates")
public class Sphere2Cube {

    private static final Logger LOG = LoggerFactory.getLogger(Sphere2Cube.class);

    private Consumer<TileRenderResult> renderConsumer;
    private Consumer<TileRenderResult> noRenderConsumer;
    private boolean renderTiles = true;
    private boolean tileDebug;
    private boolean tileDebugOverwriteContent;
    private int forceTileRenderingUpToLevel = 0;

    public static Sphere2Cube of() {
        return new Sphere2Cube();
    }

    private Sphere2Cube() {
    }

    public Sphere2Cube renderConsumer(Consumer<TileRenderResult> renderConsumer) {
        this.renderConsumer = renderConsumer;
        return this;
    }

    public Sphere2Cube noRenderConsumer(Consumer<TileRenderResult> noRenderConsumer) {
        this.noRenderConsumer = noRenderConsumer;
        return this;
    }

    public Sphere2Cube forceTileRenderingUpToLevel(int forceTileRenderingUpToLevel) {
        this.forceTileRenderingUpToLevel = forceTileRenderingUpToLevel;
        return this;
    }

    public Sphere2Cube renderTiles(boolean renderTiles) {
        this.renderTiles = renderTiles;
        return this;
    }

    public Sphere2Cube debug(boolean tileDebug, boolean tileDebugOverwriteContent) {
        this.tileDebug = tileDebug;
        this.tileDebugOverwriteContent = tileDebugOverwriteContent;
        return this;
    }

    public RenderedPano renderPano(ISourceImage source, int tileEdge) {

        LOG.info("Pano {} x {} -> {} x {}", source.getOriginalW(), source.getOriginalH(), source.getW(), source.getH());

        // thread pool
        int cpus = Runtime.getRuntime().availableProcessors();
        LOG.info("Using {} CPUs", cpus);
        ExecutorService executor = Executors.newFixedThreadPool(
                cpus,
                new BasicThreadFactory.Builder().priority(Thread.MIN_PRIORITY).build());

        // tiles
        List<Level> levels = renderFaces(executor, source, tileEdge);
        RenderedPano renderedPano = new RenderedPano(RenderedPano.Type.CUBIC, tileEdge, levels);

        // wait for all jobs finished
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            LOG.warn("INTERRUPTED", e);
            Thread.currentThread().interrupt();
        }

        LOG.warn(executor.toString());

        return renderedPano;
    }

    private List<Level> renderFaces(ExecutorService executor, ISourceImage source, int tileEdge) {
        List<Level> result = new LinkedList<>();

        int sourceEdge = source.getW() / 4;
        int maxTargetEdge = 4 * sourceEdge / 3;

        // TODO move up
        // level with best resolution hat highest layer number. So we need the number and count down to 1
        int targetEdge = maxTargetEdge;
        int maxLevel = 1;
        while (targetEdge > tileEdge) {
            targetEdge /= 2;
            ++maxLevel;
        }

        // render
        ISourceImage currentImage = source;
        targetEdge = maxTargetEdge;
        for (int level = maxLevel; level > 0; --level) {
            Level l = null;
            for (Face face : Face.values()) {
                LOG.info("Render Level: '{}' Face: '{}')", level, face);
                l = renderLayer(executor, source, face, level, sourceEdge, targetEdge, tileEdge);
                LOG.info("    -> '{}' image(s)", l.getImageCount());
            }
            result.add(l);
            targetEdge /= 2;
            Stopwatch stopwatch = Stopwatch.fromNow();
            if (renderTiles) {
                currentImage = currentImage.createScaledInstance(currentImage.getOriginalW() / 2, currentImage.getOriginalW() / 4);
            }
            LOG.info("Downscale in '{}'", stopwatch.stop().toHumanReadable());
        }

        return result;
    }

    private Level renderLayer(ExecutorService executor, ISourceImage source, Face face, int layer, int sourceEdge, int targetEdge, int tileEdge) {
        // precheck only if a tile is bigger than the shortest edge of the (scaled to layer requirements) original image
        double r = (double) targetEdge / (double) sourceEdge; // scale of target
        double oX = (double) source.getOriginalH() * r;
        double oY = (double) source.getOriginalW() * r;
        double oL = Math.min(oX, oY);
        boolean preCheck = (oL > tileEdge);

        int x = 0;
        int y = 0;
        int count = (targetEdge / tileEdge) + ((targetEdge % tileEdge != 0) ? 1 : 0);

        for (int x1 = 0; x1 < targetEdge; x1 += tileEdge) {
            int x2 = Math.min(x1 + tileEdge, targetEdge);
            y = 0;
            for (int y1 = 0; y1 < targetEdge; y1 += tileEdge) {
                int y2 = Math.min(y1 + tileEdge, targetEdge);

                TileRenderInfo trf = TileRenderInfo
                        .of()
                        .forceTileRendering(layer <= forceTileRenderingUpToLevel)
                        .renderTileIfNotInSource(false)
                        .preCheck(preCheck)
                        .tilePosition(layer, face, x, y)
                        .tileSection(x1, x2, y1, y2)
                        .mirror(false, face == Face.TOP)
                        .tilesInFace(count, count)
                        .edgeSizes(sourceEdge, targetEdge, tileEdge, tileEdge);

                // put to render-queue
                if (renderTiles) {
                    executor.submit(TileRenderJob
                            .of(trf, source)
                            .renderConsumer(renderConsumer)
                            .noRenderConsumer(noRenderConsumer)
                            .debug(tileDebug, tileDebugOverwriteContent));
                }
                ++y;
            }
            ++x;
        }

        return new Level(layer, targetEdge, targetEdge, targetEdge, x, y, count * count);
    }
}
