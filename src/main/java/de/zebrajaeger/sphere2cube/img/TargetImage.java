package de.zebrajaeger.sphere2cube.img;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class TargetImage implements ITargetImage {
    private BufferedImage image;
    private WritableRaster raster;
    private int w;
    private int h;

    public static TargetImage of(int w, int h) {
        return new TargetImage(w, h);
    }

    protected TargetImage(int w, int h) {
        this.w = w;
        this.h = h;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        raster = image.getRaster();
    }

    public void save(File file) throws IOException {
        ImageIO.write(image, "jpg", file);
    }

    @Override
    public void writePixel(int x, int y, double[] value) {
        raster.setPixel(x, y, value);
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public int getH() {
        return h;
    }
}
