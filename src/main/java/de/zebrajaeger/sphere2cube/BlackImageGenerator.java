package de.zebrajaeger.sphere2cube;

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

    private Map<Long, byte[]> cache = new HashMap<>();
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

    private byte[] create(int w, int h) {
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
        return os.toByteArray();
    }

    public byte[] generate(int w, int h) {
        long key = makeKey(w, h);
        byte[] img = cache.get(key);
        if (img == null) {
            img = create(w, h);
            cache.put(key, img);
        }
        return img;
    }

    public void writeToFile(int w, int h, File target) throws IOException {
        byte[] img = generate(w, h);
        FileUtils.writeByteArrayToFile(target, img);
    }

    public interface ImageInitializer extends Consumer<BufferedImage> {
    }
}
