package de.zebrajaeger.sphere2cube.img;

import com.twelvemonkeys.image.ResampleOp;
import de.zebrajaeger.sphere2cube.autopanogiga.ViewCalculator;
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
    private double iW;
    private double iH;

    public static SourceImage of(String file) throws IOException {
        return new SourceImage().read(new File(file));
    }

    public static SourceImage of(File file) throws IOException {
        return new SourceImage().read(file);
    }

    protected SourceImage() {
    }

    public SourceImage read(File file) throws IOException {
        this.file = file;
         BufferedImage img = ImageIO.read(file);
        if (img == null) {
            throw new IllegalArgumentException("Could not read image: '" + file.getAbsolutePath() + "'");
        }
        image(img);
        return this;
    }

    protected SourceImage image(BufferedImage image){
        this.image = image;
        this.raster = image.getRaster();
        this.w = image.getWidth();
        this.h = image.getHeight();
        this.minX = 0;
        this.maxX = image.getWidth();
        this.minY = 0;
        this.maxY = image.getHeight();
        this.iW = image.getWidth();
        this.iH = image.getHeight();

        return this;
    }

    public SourceImage fov(ViewCalculator.PanoView panoView) {
        return fov(panoView.getFovX(),
                panoView.getFovXOffset(),
                panoView.getFovX(),
                panoView.getFovYOffset());
    }

    public ISourceImage createScaledInstance(int w, int h){
        return new SourceImage()
                .image(new ResampleOp(w, h, ResampleOp.FILTER_LANCZOS).filter(image, null))
                .fov(this);
    }

    protected SourceImage fov(SourceImage source) {
        return fov(source.fovX, source.offX, source.fovY, source.offY);
    }

    public SourceImage fov(Double fovX, Double offX, Double fovY, Double offY) {
        if (fovX == null && fovY == null) {
            throw new RuntimeException("at least fovX or fovY must be set");
        }

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

        int oX = (int) (w * this.offX / 360d);
        this.minX = ((this.w - image.getWidth()) / 2) + oX;
        this.maxX = minX + image.getWidth();

        int oY = (int) (h * this.offY / 180d);
        this.minY = ((this.h - image.getHeight()) / 2) + oY;
        this.maxY = minY + image.getHeight();

        return this;
    }

    public boolean readPixel(int x, int y, double[] result) {
        if (x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY) {
            raster.getPixel(x - minX, y - minY, result);
            return true;
        } else {
            System.arraycopy(outBoundColor, 0, result, 0, result.length);
            return false;
        }
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public int getOriginalW() {
        return (int) iW;
    }

    @Override
    public int getOriginalH() {
        return (int) iH;
    }

    @Override
    public int getOriginalX() {
        return minX;
    }

    @Override
    public int getOriginalY() {
        return minY;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
