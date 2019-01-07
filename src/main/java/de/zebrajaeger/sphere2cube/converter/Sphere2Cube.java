package de.zebrajaeger.sphere2cube.converter;

import de.zebrajaeger.sphere2cube.img.ISourceImage;
import de.zebrajaeger.sphere2cube.result.Level;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public Sphere2Cube debug(boolean tileDebug, boolean tileDebugOverwriteContent) {
        this.tileDebug = tileDebug;
        this.tileDebugOverwriteContent = tileDebugOverwriteContent;
        return this;
    }

    public RenderedPano renderPano(ISourceImage source, int tileEdge, int minTargetEdge) {

        LOG.info("Pano {} x {} -> {} x {}", source.getOriginalW(), source.getOriginalH(), source.getW(), source.getH());

        // thread pool
        int cpus = Runtime.getRuntime().availableProcessors();
        LOG.info("Using {} CPUs", cpus);
        ExecutorService executor = Executors.newFixedThreadPool(
                cpus,
                new BasicThreadFactory.Builder().priority(Thread.MIN_PRIORITY).build());

        // tiles
        Map<Face, List<Level>> faceListMap = renderFaces(executor, source, source.getW() / 4, minTargetEdge, tileEdge);
        RenderedPano renderedPano = new RenderedPano(RenderedPano.Type.CUBIC, tileEdge, faceListMap.get(Face.FRONT));

        // wait for all jobs finished
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // nothing to do
        }

        return renderedPano;
    }

    private Map<Face, List<Level>> renderFaces(ExecutorService executor, ISourceImage source, int srcEdge, int minTargetEdge, int tileEdge) {
        Map<Face, List<Level>> result = new HashMap<>();
        for (Face face : Face.values()) {
            result.put(face, renderFace(executor, source, face, srcEdge, minTargetEdge, tileEdge));
        }
        return result;
    }

    private List<Level> renderFace(ExecutorService executor, ISourceImage source, Face face, int sourceEdge, int minTargetEdge, int tileEdge) {
        List<Level> result = new LinkedList<>();

        LOG.info("Render Face: " + face);

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
        targetEdge = maxTargetEdge;
        for (int level = maxLevel; level > 0; --level) {
            result.add(renderLayer(executor, source, face, level, sourceEdge, targetEdge, tileEdge));
            targetEdge /= 2;
        }

        return result;
    }

    private Level renderLayer(ExecutorService executor, ISourceImage source, Face face, int layer, int sourceEdge, int targetEdge, int tileEdge) {
        LOG.info("    Render Layer: " + layer);

        // precheck only if a tile is bigger than the shortest edge of the (scaled to layer requirements) original image
        double r = (double) targetEdge / (double) sourceEdge; // scale of target
        double oX = (double) source.getOriginalH() * r;
        double oY = (double) source.getOriginalW() * r;
        double oL = Math.min(oX, oY);
        boolean precheck = (oL > tileEdge);

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
                        .preCheck(precheck)
                        .tilePosition(layer, face, x, y)
                        .tileSection(x1, x2, y1, y2)
                        .mirror(false, face == Face.TOP)
                        .tilesInFace(count, count)
                        .edgeSizes(sourceEdge, targetEdge, tileEdge, tileEdge);

                // put to render-queue
                executor.submit(TileRenderJob
                        .of(trf, source)
                        .renderConsumer(renderConsumer)
                        .noRenderConsumer(noRenderConsumer)
                        .debug(tileDebug, tileDebugOverwriteContent));
                ++y;
            }
            ++x;
        }

        return new Level(layer, targetEdge, targetEdge, targetEdge, x, y);
    }
}
