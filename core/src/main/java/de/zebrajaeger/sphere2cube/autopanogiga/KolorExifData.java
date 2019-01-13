package de.zebrajaeger.sphere2cube.autopanogiga;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Examplke Data:<br>
 * Kolor stitching | 92 pictures | Size: 30918 x 15459 | Lens: Standard | RMS: 5.10 | FOV: 360.00 x 180.00 ~ 0.00 | Projection: Spherical | Color: LDR |<br>
 * Kolor stitching | 95 pictures | Size: 91193 x 9557 | Lens: Standard | RMS: 2.57 | FOV: 360.00 x 37.73 ~ -16.14 | Projection: Spherical | Color: LDR |<br>
 * Kolor stitching | 107 pictures | Size: 22768 x 30193 | Lens: Standard | RMS: 2.83 | FOV: 115.57 x 153.26 ~ -1.04 | Projection: Spherical | Color: LDR |
 */
public class KolorExifData {

    private static final Logger LOG = LoggerFactory.getLogger(KolorExifData.class);

    public static final String SPACE = "\\s*";
    public static final String LONG_GROUP = "(-?\\d+)";
    public static final String FLOAT_GROUP = "(-?\\d+(?:.\\d+)?)";
    public static final String STRING_GROUP = "([^\\s]+)";

    public static final Pattern KOLOR_PATTERN = Pattern.compile(SPACE + "Kolor stitching" + SPACE);
    public static final Pattern PICTURES_PATTERN = Pattern.compile(SPACE + LONG_GROUP + SPACE + "pictures" + SPACE);
    public static final Pattern SIZE_PATTERN = Pattern.compile(SPACE + "Size:" + SPACE + LONG_GROUP + SPACE + "x" + SPACE + LONG_GROUP + SPACE);
    public static final Pattern LENS_PATTERN = Pattern.compile(SPACE + "Lens: " + SPACE + STRING_GROUP + SPACE);
    public static final Pattern RMS_PATTERN = Pattern.compile(SPACE + "RMS: " + SPACE + FLOAT_GROUP + SPACE);
    public static final Pattern FOV_PATTERN = Pattern.compile(SPACE + "FOV:" + SPACE + FLOAT_GROUP + SPACE + "x" + SPACE + FLOAT_GROUP + SPACE + "~" + SPACE + FLOAT_GROUP + SPACE);
    public static final Pattern PROJECTION_PATTERN = Pattern.compile(SPACE + "Projection:" + SPACE + STRING_GROUP + SPACE);
    public static final Pattern COLOR_PATTERN = Pattern.compile(SPACE + "Color:" + SPACE + STRING_GROUP + SPACE);

    private Long pictures;
    private Long width;
    private Long height;
    private String lens;
    private Double rms;
    private Double fovX;
    private Double fovY;
    private Double fovYOffset;
    private String projection;
    private String color;

    private KolorExifData() {
    }

    public static Optional<KolorExifData> of(File sourceImage) throws ImageProcessingException, IOException {
        return of(ImageMetadataReader.readMetadata(sourceImage));
    }

    public static Optional<KolorExifData> of(Metadata metadata) {
        List<String> results = new LinkedList<>();
        for (Directory d : metadata.getDirectories()) {
            for (Tag t : d.getTags()) {
                //if ("Exif SubIFD".equals(d.getName()) && "User Comment".equals(t.getTagName())) {
                if ("User Comment".equals(t.getTagName())) {
                    results.add(t.getDescription());
                }
            }
        }

        for (String s : results) {
            Optional<KolorExifData> of = of(s);
            if (of.isPresent() && of.get().isPopulated()) {
                return of;
            }
        }

        return Optional.empty();
    }

    private boolean isPopulated() {
        return pictures != null
                || width != null
                || height != null
                || lens != null
                || rms != null
                || fovX != null
                || fovY != null
                || fovYOffset != null
                || projection != null
                || color != null;
    }

    public static Optional<KolorExifData> of(String exif) {
        if (exif == null) {
            return Optional.empty();
        }

        KolorExifData result = new KolorExifData();
        for (String s : exif.split("\\|")) {

            Matcher kolorMatcher = KOLOR_PATTERN.matcher(s);
            if (kolorMatcher.matches()) {
                LOG.debug("'Kolor stitching' found");
                //ignore
                continue;
            }

            Matcher picturesMatcher = PICTURES_PATTERN.matcher(s);
            if (picturesMatcher.matches()) {
                result.pictures = asLong(picturesMatcher, 1);
                LOG.debug(String.format("Pictures: '%s'", result.pictures));
                continue;
            }

            Matcher sizeMatcher = SIZE_PATTERN.matcher(s);
            if (sizeMatcher.matches()) {
                result.width = asLong(sizeMatcher, 1);
                result.height = asLong(sizeMatcher, 2);
                LOG.debug(String.format("Width, Height: '%s', '%s'", result.width, result.height));
                continue;
            }

            Matcher lensMatcher = LENS_PATTERN.matcher(s);
            if (lensMatcher.matches()) {
                result.lens = asString(lensMatcher, 1);
                LOG.debug(String.format("Lens: '%s'", result.lens));
                continue;
            }

            Matcher rmsMatcher = RMS_PATTERN.matcher(s);
            if (rmsMatcher.matches()) {
                result.rms = asDouble(rmsMatcher, 1);
                LOG.debug(String.format("RMS: '%s'", result.rms));
                continue;
            }

            Matcher fovMatcher = FOV_PATTERN.matcher(s);
            if (fovMatcher.matches()) {
                result.fovX = asDouble(fovMatcher, 1);
                result.fovY = asDouble(fovMatcher, 2);
                result.fovYOffset = asDouble(fovMatcher, 3);
                LOG.debug(String.format("FovX, FovY, FovYOffset: '%s', '%s','%s'", result.fovX, result.fovY, result.fovYOffset));
                continue;
            }

            Matcher projectionMatcher = PROJECTION_PATTERN.matcher(s);
            if (projectionMatcher.matches()) {
                result.projection = asString(projectionMatcher, 1);
                LOG.debug(String.format("Projection: '%s'", result.projection));
                continue;
            }

            Matcher colorMatcher = COLOR_PATTERN.matcher(s);
            if (colorMatcher.matches()) {
                result.color = asString(colorMatcher, 1);
                LOG.debug(String.format("Color: '%s'", result.color));
                continue;
            }

            LOG.info(String.format("Unknows Kolor Exif Data: '%s'", s));
        }

        return Optional.of(result);
    }

    private static double asDouble(Matcher matcher, int groupIndex) {
        return Double.parseDouble(matcher.group(groupIndex));
    }

    private static long asLong(Matcher matcher, int groupIndex) {
        return Long.parseLong(matcher.group(groupIndex));
    }

    private static String asString(Matcher matcher, int groupIndex) {
        return matcher.group(groupIndex);
    }

    public Long getPictures() {
        return pictures;
    }

    public Long getWidth() {
        return width;
    }

    public Long getHeight() {
        return height;
    }

    public String getLens() {
        return lens;
    }

    public Double getRms() {
        return rms;
    }

    public Double getFovX() {
        return fovX;
    }

    public Double getFovY() {
        return fovY;
    }

    public Double getFovYOffset() {
        return fovYOffset;
    }

    public String getProjection() {
        return projection;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
