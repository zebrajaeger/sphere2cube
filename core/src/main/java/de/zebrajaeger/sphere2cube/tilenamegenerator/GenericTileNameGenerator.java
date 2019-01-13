package de.zebrajaeger.sphere2cube.tilenamegenerator;

import de.zebrajaeger.sphere2cube.converter.Face;
import de.zebrajaeger.sphere2cube.converter.TileRenderInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://krpano.com/docu/xml/#image.url.placeholders
 *
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
@SuppressWarnings("Duplicates")
public class GenericTileNameGenerator implements TileNameGenerator {

    private static final String CUBE_FACE = "(%s)";
    private static final String CUBE_LAYER = "(%l)";
    private static final String HORIZONTAL_TILE_INDEX = "(%(0*)h|%(0*)x|%(0*)u|%(0*)c)";
    private static final String VERTICAL_TILE_INDEX = "(%(0*)v|%(0*)y|%(0*)r)";

    private String pattern;

    public static GenericTileNameGenerator of(String pattern) {
        return new GenericTileNameGenerator(pattern);
    }

    protected GenericTileNameGenerator(String pattern) {
        this.pattern = pattern;
    }

    public String generateName(TileRenderInfo trf) {
        return generateName(trf.getFace(), trf.getLevel(), 0, trf.getTileCountX(), 0, trf.getTileCountY());
    }

    public String generateName(Face face, int layer, int countX, int x, int countY, int y) {
        return generateName(pattern, face, layer, countX, x, countY, y);
    }

    public String generateName(String pattern, Face face, int layer, int countX, int x, int countY, int y) {
        int sizeX = findSize(countX - 1);
        int sizeY = findSize(countY - 1);

        String result = replaceFace(pattern, face);
        result = replaceLayer(result, layer);
        result = replaceHorizontalIndex(result, sizeX, x);
        result = replaceVerticalIndex(result, sizeY, y);
        return result;
    }

    private int findSize(int count) {
        if (count < 10) {
            return 1;
        } else if (count < 100) {
            return 2;
        } else if (count < 1000) {
            return 3;
        } else if (count < 10000) {
            return 4;
        } else if (count < 10000) {
            return 5;
        } else if (count < 10000) {
            return 6;
        } else if (count < 10000000) {
            return 7;
        } else {
            throw new RuntimeException("Count too big: " + count);
        }
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

    private String replaceVerticalIndex(String pattern, int sizeY, int index) {
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
                pattern = m.group(1) + String.format("%0" + sizeY + "d", index) + m.group(6);
            } else {
                pattern = m.group(1) + String.format("%0" + (zeros.length() + 1) + "d", index) + m.group(6);
            }

            m = Pattern.compile("^(.*)" + VERTICAL_TILE_INDEX + "(.*)?").matcher(pattern);
        }
        return pattern;
    }

    private String replaceHorizontalIndex(String pattern, int sizeX, int index) {
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
                pattern = m.group(1) + String.format("%0" + sizeX + "d", index) + m.group(7);
            } else {
                pattern = m.group(1) + String.format("%0" + (zeros.length() + 1) + "d", index) + m.group(7);
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
