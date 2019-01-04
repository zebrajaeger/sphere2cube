package de.zebrajaeger.sphere2cube.tile;

import de.zebrajaeger.sphere2cube.Face;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public interface TileNameGenerator {
    String generateName(String pattern, Face face, int layer, int x, int y);
    String generateName(Face face, int layer, int x, int y);
}
