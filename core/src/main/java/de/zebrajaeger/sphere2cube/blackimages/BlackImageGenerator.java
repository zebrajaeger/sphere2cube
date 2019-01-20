package de.zebrajaeger.sphere2cube.blackimages;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BlackImageGenerator {

    public static final ImageInitializer BLACK = img -> {
        int w = img.getWidth();
        int h = img.getHeight();
        WritableRaster r = img.getRaster();
        int[] pixel = new int[3];
        Arrays.fill(pixel, 0);
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                r.setPixel(i, j, pixel);
            }
        }
    };

    private Map<Long, Entry> cache = new HashMap<>();
    private ImageInitializer imageInitializer;

    public static BlackImageGenerator of() {
        return of(BLACK);
    }

    public static BlackImageGenerator of(ImageInitializer imageInitializator) {
        return new BlackImageGenerator(imageInitializator);
    }

    private BlackImageGenerator(ImageInitializer imageInitializator) {
        this.imageInitializer = imageInitializator;
    }

    private long makeKey(int w, int h) {
        return (long) (w) << 16 + h;
    }

    private Entry create(int w, int h) {
        // create image
        BufferedImage img = new BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);

        // initialize image
        if(imageInitializer!=null){
            imageInitializer.accept(img);
        }

        // compress to bytearray
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            throw new RuntimeException("Error creating black image", e);
        }
        return new Entry(os.toByteArray(),w,h);
    }

    public byte[] generate(int w, int h) {
        long key = makeKey(w, h);
        Entry img = cache.get(key);
        if (img == null) {
            img = create(w, h);
            cache.put(key, img);
        }
        img.access();
        return img.data;
    }

    public void writeToFile(int w, int h, File target) throws IOException {
        byte[] img = generate(w, h);
        FileUtils.writeByteArrayToFile(target, img);
    }

    @Override
    public String toString() {
        return cache.values().stream().map( e -> e.toString() ).collect(Collectors.joining(", \n"));
    }

    public interface ImageInitializer extends Consumer<BufferedImage> {
    }

    private static class Entry{
        private byte[] data;
        private int w;
        private int h;
        private long accessed = 0;

        public Entry(byte[] data, int w, int h) {
            this.data = data;
            this.w = w;
            this.h = h;
        }

        public synchronized void access(){
            accessed++;
        }

        public byte[] getData() {
            return data;
        }

        public int getW() {
            return w;
        }

        public int getH() {
            return h;
        }

        public long getAccessed() {
            return accessed;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "w=" + w +
                    ", h=" + h +
                    ", size=" + data.length +
                    ", accessed=" + accessed +
                    '}';
        }
    }
}
