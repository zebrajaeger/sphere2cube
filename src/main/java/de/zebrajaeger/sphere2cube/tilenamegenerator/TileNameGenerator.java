package de.zebrajaeger.sphere2cube.tilenamegenerator;

import de.zebrajaeger.sphere2cube.converter.Face;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public interface TileNameGenerator {
    String generateName(String pattern, Face face, int layer, int countX, int x, int countY, int y);

    String generateName(Face face, int layer, int countX, int x, int countY, int y);
}
