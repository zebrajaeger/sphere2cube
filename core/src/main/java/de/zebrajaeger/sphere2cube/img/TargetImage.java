package de.zebrajaeger.sphere2cube.img;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
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

    public void saveAsJPG(File file, float quality) throws IOException {
        try (FileOutputStream os = new FileOutputStream(file)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam writerParams = writer.getDefaultWriteParam();
            writerParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writerParams.setCompressionQuality(quality / 100f);
            writer.setOutput(ImageIO.createImageOutputStream(os));
            writer.write(null, new IIOImage(image, null, null), writerParams);
        }
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
