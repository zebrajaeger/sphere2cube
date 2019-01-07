package de.zebrajaeger.sphere2cube.tilenamegenerator;

import de.zebrajaeger.sphere2cube.converter.Face;

/**
 * https://krpano.com/docu/xml/#image.url.placeholders
 *
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class PannellumTileNameGenerator extends GenericTileNameGenerator {

    public static PannellumTileNameGenerator of() {
        return new PannellumTileNameGenerator();
    }

    public PannellumTileNameGenerator() {
        super("%l/%s/%y_%x.png");
    }

    public String generateName(String pattern, Face face, int layer, int countX, int x, int countY, int y) {
        return super.generateName(pattern, face, layer, countX, x - 1, countY, y - 1);
    }
}
