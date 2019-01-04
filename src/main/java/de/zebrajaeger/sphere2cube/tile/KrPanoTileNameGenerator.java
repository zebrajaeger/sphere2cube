package de.zebrajaeger.sphere2cube.tile;

import de.zebrajaeger.sphere2cube.Face;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://krpano.com/docu/xml/#image.url.placeholders
 *
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
@SuppressWarnings("Duplicates")
public class KrPanoTileNameGenerator implements TileNameGenerator {

    private static final String CUBE_FACE = "(%s)";
    private static final String CUBE_LAYER = "(%l)";
    private static final String HORIZONTAL_TILE_INDEX = "(%(0*)h|%(0*)x|%(0*)u|%(0*)c)";
    private static final String VERTICAL_TILE_INDEX = "(%(0*)v|%(0*)y|%(0*)r)";

    private String pattern;

    public static KrPanoTileNameGenerator of(String pattern) {
        return new KrPanoTileNameGenerator(pattern);
    }

    private KrPanoTileNameGenerator(String pattern) {
        this.pattern = pattern;
    }

    public String generateName(Face face, int layer, int x, int y) {
        return generateName(pattern, face, layer, x, y);
    }

    public String generateName(String pattern, Face face, int layer, int x, int y) {
        String result = replaceFace(pattern, face);
        result = replaceLayer(result, layer);
        result = replaceHorizontalIndex(result, x);
        result = replaceVerticalIndex(result, y);
        return result;
    }

    private String replaceFace(String pattern, Face face) {
        String replacement = faceToPrefix(face);
        Matcher m = Pattern.compile("(.*)" + CUBE_FACE + "(.*)").matcher(pattern);
        while (m.matches()) {
            pattern = m.group(1) + replacement + m.group(3);
            m = Pattern.compile("(.*)" + CUBE_FACE + "(.*)").matcher(pattern);
        }

        return pattern;
    }

    private String replaceLayer(String pattern, int layer) {
        Matcher m = Pattern.compile("(.*)" + CUBE_LAYER + "(.*)").matcher(pattern);
        while (m.matches()) {
            pattern = m.group(1) + layer + m.group(3);
            m = Pattern.compile("(.*)" + CUBE_LAYER + "(.*)").matcher(pattern);
        }

        return pattern;
    }

    private String replaceVerticalIndex(String pattern, int index) {
        Matcher m = Pattern.compile("^(.*)" + VERTICAL_TILE_INDEX + "(.*)?").matcher(pattern);
        while (m.matches()) {

            String zeros = null;
            if (m.group(3) != null) {
                zeros = m.group(3);
            }
            if (m.group(4) != null) {
                zeros = m.group(4);
            }
            if (m.group(5) != null) {
                zeros = m.group(5);
            }

            if (zeros == null || zeros.length() == 0) {
                pattern = m.group(1) + index + m.group(6);
            } else {
                pattern = m.group(1) + String.format("%0" + zeros.length() + "d", index) + m.group(6);
            }

            m = Pattern.compile("^(.*)" + VERTICAL_TILE_INDEX + "(.*)?").matcher(pattern);
        }
        return pattern;
    }

    private String replaceHorizontalIndex(String pattern, int index) {
        Matcher m = Pattern.compile("^(.*)" + HORIZONTAL_TILE_INDEX + "(.*)?").matcher(pattern);
        while (m.matches()) {

            String zeros = null;
            if (m.group(3) != null) {
                zeros = m.group(3);
            }
            if (m.group(4) != null) {
                zeros = m.group(4);
            }
            if (m.group(5) != null) {
                zeros = m.group(5);
            }
            if (m.group(6) != null) {
                zeros = m.group(6);
            }

            if (zeros == null || zeros.length() == 0) {
                pattern = m.group(1) + index + m.group(7);
            } else {
                pattern = m.group(1) + String.format("%0" + zeros.length() + "d", index) + m.group(7);
            }

            m = Pattern.compile("(.*)" + HORIZONTAL_TILE_INDEX + "(.*)").matcher(pattern);
        }
        return pattern;
    }

    /**
     * https://krpano.com/docu/xml/#image.cubelabels
     */
    private String faceToPrefix(Face face) {
        switch (face) {
            case BACK:
                return "b";
            case LEFT:
                return "l";
            case FRONT:
                return "f";
            case RIGHT:
                return "r";
            case TOP:
                return "u";
            case BOTTOM:
                return "d";
            default:
                throw new RuntimeException("Unknown face: '" + face + "'");
        }
    }
}
