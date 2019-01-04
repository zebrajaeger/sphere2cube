package de.zebrajaeger.sphere2cube.img;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class SourceImage implements ISourceImage {
    private File file;
    private BufferedImage image;
    private WritableRaster raster;
    private int w;
    private int h;

    private double fovX = 360d;
    private double offX = 0d;
    private double fovY = 180d;
    private double offY = 0d;

    private int minX;
    private int maxX;
    private int minY;
    private int maxY;

    private double[] outBoundColor = new double[]{0f, 0f, 0f, 0f};

    public static SourceImage of(File file) throws IOException {
        return new SourceImage().read(file);
    }

    protected SourceImage() {
    }

    public SourceImage read(File file) throws IOException {
        this.file = file;
        image = ImageIO.read(file);
        raster = image.getRaster();
        w = image.getWidth();
        h = image.getHeight();
        minX = 0;
        maxX = image.getWidth();
        minY = 0;
        maxY = image.getHeight();
        return this;
    }

    public SourceImage fov(Double fovX, Double offX, Double fovY, Double offY) {
        if (fovX == null && fovY == null) {
            throw new RuntimeException("at least fovX or fovY must be set");
        }

        double iW = image.getWidth();
        double iH = image.getHeight();

        if (fovY != null) {
            this.fovY = fovY;
            this.h = (int) (iH * 180d / fovY);
            this.w = this.h * 2;
            this.fovX = 360d * iW / (double) this.w;
        }

        if (offY != null) {
            this.offY = offY;
        } else {
            this.offY = 0d;
        }

        // x leads
        if (fovX != null) {
            this.fovX = fovX;
            this.w = (int) (iW * 360d / fovX);
            this.h = this.w / 2;
            this.fovY = 180d * iH / (double) this.h;
        }
        if (offX != null) {
            this.offX = offX;
        } else {
            this.offX = 0d;
        }

        int oX = (int) (iW * this.offX / 360d);
        this.minX = ((this.w - image.getWidth()) / 2) + oX;
        this.maxX = minX + image.getWidth();

        int oY = (int) (iH * this.offY / 180d);
        this.minY = ((this.h - image.getHeight()) / 2) + oY;
        this.maxY = minY + image.getHeight();

        return this;
    }

    public void readPixel(int x, int y, double[] result) {
        if (x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY) {
            raster.getPixel(x - minX, y - minY, result);
        } else {
            System.arraycopy(outBoundColor, 0, result, 0, result.length);
        }
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
