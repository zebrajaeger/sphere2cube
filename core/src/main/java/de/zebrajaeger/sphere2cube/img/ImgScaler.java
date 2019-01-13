package de.zebrajaeger.sphere2cube.img;

import com.twelvemonkeys.image.ResampleOp;

import java.awt.image.BufferedImage;

public class ImgScaler {
    public static final int DEFAULT_FILTER = ResampleOp.FILTER_LANCZOS; // slow but pretty
    private ISourceImage source;

    public static ImgScaler of(ISourceImage source) {
        return new ImgScaler(source);
    }

    private ImgScaler(ISourceImage source) {
        this.source = source;
    }

    public ITargetImage scaleTo(int maxEdge, boolean canScaleUp) {
        // find scale factor
        double sX = (double) source.getOriginalW() / (double) maxEdge;
        if (!canScaleUp && sX < 1d) {
            sX = 1d;
        }
        double sY = (double) source.getOriginalH() / (double) maxEdge;
        if (!canScaleUp && sY < 1d) {
            sY = 1d;
        }
        double s = Math.max(sX, sY);

        // target size
        int tX = (int) ((double) source.getOriginalW() / s);
        int tY = (int) ((double) source.getOriginalH() / s);

        if (source instanceof SourceImage) {
            // if source is a SourceImage, we can access the BufferedImage an can use twelfemonkeys for scaling...
            SourceImage si = (SourceImage) source;
            BufferedImage result = new ResampleOp(tX, tY, DEFAULT_FILTER).filter(si.getImage(), null);
            return TargetImage.of(result);

        } else {
            // ...otherwise wie take the nearest neighbour algorithm. Fast but ugly
            TargetImage target = TargetImage.of(tX, tY);

            double[] buffer = new double[3];

            double xSrc = source.getOriginalX();
            for (int x = 0; x < tX; ++x) {
                double ySrc = source.getOriginalY();
                for (int y = 0; y < tY; ++y) {
                    source.readPixel((int) xSrc, (int) ySrc, buffer);
                    target.writePixel(x, y, buffer);
                    ySrc += s;
                }
                xSrc += s;
            }

            return target;
        }
    }
}