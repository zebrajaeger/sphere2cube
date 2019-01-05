package de.zebrajaeger.sphere2cube.img;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
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
    private Format format;

    public static TargetImage of(Format format, int w, int h) {
        return new TargetImage(format, w, h);
    }

    public static TargetImage of(int w, int h) {
        return new TargetImage(Format.PNG, w, h);
    }

    protected TargetImage(Format format, int w, int h) {
        this.format = format;
        this.w = w;
        this.h = h;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        raster = image.getRaster();
    }

    public void save(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        ImageIO.write(image, format.getFormatName(), file);
    }

    public void save(String filePath) throws IOException {
        save(new File(filePath));
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

    public enum Format {
        JPG("jpg"),
        PNG("png"),
        ;

        private String formatName;

        Format(String formatName) {
            this.formatName = formatName;
        }

        public String getFormatName() {
            return formatName;
        }
    }

}
