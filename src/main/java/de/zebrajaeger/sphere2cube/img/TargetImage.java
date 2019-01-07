package de.zebrajaeger.sphere2cube.img;

import javax.imageio.ImageIO;
import java.awt.*;
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

    public static TargetImage of(BufferedImage image) {
        return new TargetImage(image);
    }

    protected TargetImage(int w, int h) {
        this.w = w;
        this.h = h;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        raster = image.getRaster();
    }

    protected TargetImage(BufferedImage image) {
        this.w = image.getWidth();
        this.h = image.getHeight();
        this.image = image;
        raster = image.getRaster();
    }

    public void save(File file) throws IOException {
        save(file, Format.PNG);
    }

    @Override
    public void save(File file, Format format) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        ImageIO.write(image, format.getFormatName(), file);
    }

    public void save(String filePath) throws IOException {
        save(new File(filePath));
    }

    @Override
    public void save(String filePath, Format format) throws IOException {
        save(new File(filePath), format);
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) image.getGraphics();
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
