package de.zebrajaeger.sphere2cube.converter;

import de.zebrajaeger.sphere2cube.Utils;
import de.zebrajaeger.sphere2cube.img.ISourceImage;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtmGenerator;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtml;
import de.zebrajaeger.sphere2cube.panoxml.PanoXmlGenerator;
import de.zebrajaeger.sphere2cube.result.Level;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.result.View;
import de.zebrajaeger.sphere2cube.tilenamegenerator.KrPanoTileNameGenerator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Single equirectangular spherical Image to multible cube images
 */
@SuppressWarnings("Duplicates")
public class Sphere2Cube {

    private static final Logger LOG = LoggerFactory.getLogger(Sphere2Cube.class);

    private final KrPanoTileNameGenerator tileNameGenerator;

    public Sphere2Cube(String tilePattern) {
        tileNameGenerator = KrPanoTileNameGenerator.of(tilePattern);
    }

    public void renderPano(ISourceImage source, File panoXmlFile, File indexHtmlFile, int tileEdge) throws IOException {

        LOG.info("Pano {} x {} -> {} x {}", source.getOriginalW(), source.getoriginalH(), source.getW(), source.getH());
        long startTime = System.currentTimeMillis();

        // thread pool
        int cpus = Runtime.getRuntime().availableProcessors();
        LOG.info("Using {} CPUs", cpus);
        ExecutorService executor = Executors.newFixedThreadPool(cpus);

        // tiles
        Map<Face, List<Level>> faceListMap = renderFaces(executor, source, source.getW() / 4, 1024, tileEdge);
        RenderedPano renderedPano = new RenderedPano(RenderedPano.Type.CUBIC, tileEdge, View.of().maxpixelzoom(10d), faceListMap.get(Face.FRONT));

        // wait for all jobs finished
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // nothing to do
        }

        // pano.xml
        String panoXml = PanoXmlGenerator.of().generate(renderedPano);
        FileUtils.write(panoXmlFile, panoXml, StandardCharsets.UTF_8);

        // index.html
        String indexHtml = IndexHtmGenerator.of().generate(new IndexHtml("TestPano"));
        FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);

        LOG.info("finished in {}", Utils.durationToString(System.currentTimeMillis() - startTime));
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
        int targetEdge = 4 * sourceEdge / 3;
        int layer = 1;
        do {
            result.add(renderLayer(executor, source, face, layer, sourceEdge, targetEdge, tileEdge));
            targetEdge /= 2;
            ++layer;
        } while (targetEdge > minTargetEdge);

        return result;
    }

    private Level renderLayer(ExecutorService executor, ISourceImage source, Face face, int layer, int sourceEdge, int targetEdge, int tileEdge) {
        LOG.info("    Render Layer: " + layer);

        int x = 0;
        int y = 0;
        int count = (targetEdge / tileEdge) + ((targetEdge % tileEdge != 0) ? 1 : 0);

        for (int x1 = 0; x1 < targetEdge; x1 += tileEdge) {
            int x2 = Math.min(x1 + tileEdge, targetEdge);
            y = 0;
            for (int y1 = 0; y1 < targetEdge; y1 += tileEdge) {
                int y2 = Math.min(y1 + tileEdge, targetEdge);

                TileRenderInfo trf = TileRenderInfo.of()
                        .tilePosition(face, x, y)
                        .tileSection(x1, x2, y1, y2)
                        .mirror(false, face == Face.TOP)
                        .tilesInFace(count, count)
                        .edgeSizes(sourceEdge, targetEdge, tileEdge, tileEdge)
                        .targetFile(new File(tileNameGenerator.generateName(face, layer, count, x + 1, count, y + 1)));

                executor.submit(TileRenderJob.of(trf, source).tileDebug(true));

                ++y;
            }
            ++x;
        }

        return new Level(layer, targetEdge, targetEdge, x, y);
    }
}
