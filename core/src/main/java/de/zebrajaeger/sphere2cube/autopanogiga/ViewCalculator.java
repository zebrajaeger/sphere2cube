package de.zebrajaeger.sphere2cube.autopanogiga;

import com.drew.imaging.ImageProcessingException;
import de.zebrajaeger.sphere2cube.psdimage.ReadablePsdImage;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public class ViewCalculator {

    private int sourceWidth;
    private int sourceHeight;

    private Long targetWidth;
    private Long targetHeight;

    private Long borderLeft;
    private Long borderRight;
    private Long borderTop;
    private Long borderBottom;

    private Double fovX;
    private Double fovX1;
    private Double fovX2;
    private Double fovXOffset;
    private Double fovY;
    private Double fovY1;
    private Double fovY2;
    private Double fovYOffset;

    private String projection;

    private ViewCalculator(int sourceWidth, int sourceHeight) {
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    public static ViewCalculator of(File sourceImageFile) throws IOException, ImageProcessingException {

        ReadablePsdImage sourceImage = ReadablePsdImage.headerOnly(sourceImageFile);
        ViewCalculator result = ViewCalculator.of(sourceImage);

        // read data from gpanodata first (most precise)
        sourceImage.getGPanoData().ifPresent(panoData -> {
            result.targetWidth = panoData.getFullPanoWidthPixels();
            result.targetHeight = panoData.getFullPanoHeightPixels();
            result.borderLeft = panoData.getCroppedAreaLeftPixels();
            result.borderTop = panoData.getCroppedAreaTopPixels();
            result.projection = panoData.getProjectionType();
        });

        // fill missing data from exif values (if present).(Less precise cause of floating point values with tow decimal places)
        if (result.targetWidth == null || result.targetHeight == null || result.borderLeft == null || result.borderTop == null) {
            KolorExifData.of(sourceImageFile).ifPresent(exifData -> {
                if (result.projection == null) {
                    result.projection = exifData.getProjection();
                }

                // target width from source width and fov
                if (result.targetWidth == null) {
                    Double fovX = exifData.getFovX();
                    if (fovX != null) {
                        result.targetWidth = (long) ((result.sourceWidth * 360d) / fovX);
                    }
                }

                // target height from source height and fov
                if (result.targetHeight == null) {
                    Double fovY = exifData.getFovY();
                    if (fovY != null) {
                        result.targetHeight = (long) ((result.sourceHeight * 180d) / fovY);
                    }
                }

                // now, (targetWidth/2) == targetHeight ! isn't it? -> change height and hopefully that's right...
                if (result.targetWidth != null && result.targetHeight != null) {
                    if (result.targetWidth / 2 != result.targetHeight) {
                        result.targetHeight = result.targetWidth / 2;
                    }
                }

                // if no X-Border is present, we center the source image horizontal.
                if (result.borderLeft == null && result.targetWidth != null) {
                    result.borderLeft = (result.targetWidth - result.sourceWidth) / 2;

                    // TODO There seems to be no exif data for X-Offset!?
                }

                // if no Y-Border is present, we center the source image vertical.
                if (result.borderTop == null && result.targetHeight != null) {
                    result.borderTop = (result.targetHeight - result.sourceHeight) / 2;

                    // if we can find offset data, we add it to the border (and shift the source image position)
                    Double offsetDeg = exifData.getFovYOffset();
                    if (offsetDeg != null) {
                        long offsetPx = (long) ((double) result.targetWidth * (offsetDeg / 180d));

                        // notice: minus because... whatever. Maybe Kolor uses another Y-axis direction!?
                        result.borderTop -= offsetPx;
                    }
                }
            });
        }

        if (result.targetWidth != null) {
            // compute fov X
            result.fovX = (double) (result.sourceWidth * 360) / (double) result.targetWidth;

            // calculate right border
            if (result.borderLeft != null) {
                result.borderRight = result.targetWidth - result.sourceWidth - result.borderLeft;
                result.fovXOffset = (double) ((result.borderLeft - result.borderRight) / 2) * 360d / (double) result.targetWidth;
            }
        }

        if (result.targetHeight != null) {
            // compute fov Y

            result.fovY = (double) (result.sourceHeight * 180) / (double) result.targetHeight;
            if (result.borderTop != null) {
                result.borderBottom = result.targetHeight - result.sourceHeight - result.borderTop;
                result.fovYOffset = (double) ((result.borderTop - result.borderBottom) / 2) * 180d / (double) result.targetHeight;
            }
        }

        if (result.fovX != null) {
            if (result.fovXOffset != null) {
                result.fovX1 = 180d - (result.fovX / 2d) + result.fovXOffset;
            } else {
                result.fovX1 = 180d - (result.fovX / 2d);
            }
            result.fovX2 = result.fovX1 + result.fovX;
        }

        if (result.fovY != null) {
            if (result.fovYOffset != null) {
                result.fovY1 = 90d - (result.fovY / 2d) + result.fovYOffset;
            } else {
                result.fovY1 = 90d - (result.fovY / 2d);
            }
            result.fovY2 = result.fovY1 + result.fovY;
        }

        return result;
    }

    public static ViewCalculator of(ReadablePsdImage image) {
        return new ViewCalculator(image.getWidth(), image.getHeight());
    }

    public Optional<PanoView> createPanoView() {
        PanoView result = null;
        if (fovX1 != null && fovX2 != null && fovY1 != null && fovY2 != null) {
            double lookAtX = (fovX1 + fovX2) / 2;
            double lookAtY = (fovY1 + fovY2) / 2;
            double yOff = this.fovYOffset != null ? this.fovYOffset : 0d;
            result = new PanoView(
                    fovX1 - 180d, fovX2 - 180d, fovY1 - 90d, fovY2 - 90d,
                    fovY1 - 90d - yOff - yOff, fovY2 - 90d - yOff - yOff,
                    lookAtX - 180d, lookAtY - 90d,
                    lookAtY - 90d // TODO not correct
            );

            if (fovXOffset != null) {
                result.fovXOffset = fovXOffset;
                result.fovX1 += fovXOffset;
                result.fovX2 += fovXOffset;
                result.lookAtX += fovXOffset;
            }
            result.fovYOffset = yOff;
            result.fovY1 += yOff;
            result.fovY2 += yOff;
            result.lookAtY += yOff;
        }
        return Optional.ofNullable(result);
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public Long getTargetWidth() {
        return targetWidth;
    }

    public Integer getTargetWidthAsInteger() {
        return asInteger(targetWidth);
    }

    public Long getTargetHeight() {
        return targetHeight;
    }

    public Integer getTargetHeightAsInteger() {
        return asInteger(targetHeight);
    }

    public Long getBorderLeft() {
        return borderLeft;
    }

    public Integer getBorderLeftAsInteger() {
        return asInteger(borderLeft);
    }

    public Long getBorderRight() {
        return borderRight;
    }

    public Integer getBorderRightAsInteger() {
        return asInteger(borderRight);
    }

    public Long getBorderTop() {
        return borderTop;
    }

    public Integer getBorderTopAsInteger() {
        return asInteger(borderTop);
    }

    public Long getBorderBottom() {
        return borderBottom;
    }

    public Integer getBorderBottomAsInteger() {
        return asInteger(borderBottom);
    }

    public Double getFovX() {
        return fovX;
    }

    public Double getFovXOffset() {
        return fovXOffset;
    }

    public Double getFovY() {
        return fovY;
    }

    public Double getFovYOffset() {
        return fovYOffset;
    }

    public Long getFovYOffsetPx() {
        if (targetHeight == null || fovYOffset == null) {
            return null;
        }
        return (long) ((double) targetHeight * fovYOffset / 180d);
    }

    public void getFovYOffsetPx(Consumer<Long> consumer) {
        if (targetHeight != null && fovYOffset != null) {
            consumer.accept((long) ((double) targetHeight * fovYOffset / 180d));
        }
    }

    public Double getFovX1() {
        return fovX1;
    }

    public Double getFovX2() {
        return fovX2;
    }

    public Double getFovY1() {
        return fovY1;
    }

    public Double getFovY2() {
        return fovY2;
    }

    private Integer asInteger(Long value) {
        return (value == null) ? null : (int) value.longValue();
    }

    public String getProjection() {
        return projection;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public static class PanoView {
        private double fovX;
        private double fovXOffset = 0;
        private double fovX1;
        private double fovX2;
        private double fovY;
        private double fovYOffset = 0;
        private double fovY1;
        private double fovY2;
        private double fovY1Inv;
        private double fovY2Inv;
        private double lookAtX;
        private double lookAtY;
        private double lookAtYInv;

        public PanoView(
                double fovX1, double fovX2, double fovY1, double fovY2,
                double fovY1Inv, double fovY2Inv,
                double lookAtX, double lookAtY,
                double lookAtYInv) {
            this.fovX1 = fovX1;
            this.fovX2 = fovX2;
            this.fovX = fovX2 - fovX1;
            this.fovY1 = fovY1;
            this.fovY2 = fovY2;
            this.fovY = fovY2 - fovY1;

            this.fovY2Inv = fovY2Inv;
            this.fovY1Inv = fovY1Inv;

            this.lookAtX = lookAtX;
            this.lookAtY = lookAtY;
        }

        public double getFovX1() {
            return fovX1;
        }

        public double getFovX2() {
            return fovX2;
        }

        public double getFovY1() {
            return fovY1;
        }

        public double getFovY2() {
            return fovY2;
        }

        public double getLookAtX() {
            return lookAtX;
        }

        public double getLookAtY() {
            return lookAtY;
        }

        public double getFovX() {
            return fovX;
        }

        public double getFovY() {
            return fovY;
        }

        public double getFovXOffset() {
            return fovXOffset;
        }

        public double getFovYOffset() {
            return fovYOffset;
        }

        public double getFovY1Inv() {
            return fovY1Inv;
        }

        public double getFovY2Inv() {
            return fovY2Inv;
        }

        public double getLookAtYInv() {
            return lookAtYInv;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
