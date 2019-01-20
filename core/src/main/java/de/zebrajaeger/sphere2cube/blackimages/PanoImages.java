package de.zebrajaeger.sphere2cube.blackimages;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.sphere2cube.converter.Face;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class PanoImages {
    private List<PanoImage> panoImages = new LinkedList<>();

    private AtomicLong count = new AtomicLong(0);

    public synchronized void add(PanoImage panoImage) {
        count.incrementAndGet();
        panoImages.add(panoImage);
    }

    public List<PanoImage> getPanoImages() {
        return panoImages;
    }

    public void setPanoImages(List<PanoImage> panoImages) {
        this.panoImages = panoImages;
    }

    @Deprecated
    public void save(File file) throws IOException {
        new ObjectMapper().writeValue(file, this);
    }

    public void raster(Consumer<LayerFaceRaster> consumer) {
        Map<Integer, Map<Face, List<PanoImage>>> map = getCategorizedPanoImages();
        for (Integer layer : map.keySet()) {
            Map<Face, List<PanoImage>> faceListMap = map.get(layer);
            for (Face face : faceListMap.keySet()) {
                Raster raster = new Raster(faceListMap.get(face));
                consumer.accept(new LayerFaceRaster(layer, face, raster));
            }
        }
    }

    public Map<Integer, Map<Face, Raster>> toRaster() {
        Map<Integer, Map<Face, Raster>> result = new HashMap<>();
        raster(lfr -> {
            Map<Face, Raster> faces = result.get(lfr.layer);
            if (faces == null) {
                faces = new HashMap<>();
                result.put(lfr.layer, faces);
            }

            faces.put(lfr.face, lfr.raster);
        });
        return result;
    }

    private Map<Integer, Map<Face, List<PanoImage>>> getCategorizedPanoImages() {
        Map<Integer, Map<Face, List<PanoImage>>> map = new HashMap<>();
        panoImages.forEach(panoImage -> {
            Integer l = panoImage.getLayer();
            Map<Face, List<PanoImage>> faces = map.get(l);
            if (faces == null) {
                faces = new HashMap<>();
                map.put(l, faces);
            }

            List<PanoImage> panoImages = faces.get(panoImage.getFace());
            if (panoImages == null) {
                panoImages = new LinkedList<>();
                faces.put(panoImage.getFace(), panoImages);
            }

            panoImages.add(panoImage);
        });
        return map;
    }

    public List<PanoImage> getPanoImagesWithType(PanoImage.Type type) {
        return panoImages
                .stream()
                .filter(panoImage -> type.equals(panoImage.getType()))
                .collect(Collectors.toList());
    }

    public List<String> getPanoImageNamesWithType(PanoImage.Type type) {
        return panoImages
                .stream()
                .filter(panoImage -> type.equals(panoImage.getType()))
                .map(PanoImage::getPath)
                .collect(Collectors.toList());
    }

    public static class LayerFaceRaster {
        private int layer;
        private Face face;
        private Raster raster;

        public LayerFaceRaster(int layer, Face face, Raster raster) {
            this.layer = layer;
            this.face = face;
            this.raster = raster;
        }

        public int getLayer() {
            return layer;
        }

        public Face getFace() {
            return face;
        }

        public Raster getRaster() {
            return raster;
        }
    }
}
