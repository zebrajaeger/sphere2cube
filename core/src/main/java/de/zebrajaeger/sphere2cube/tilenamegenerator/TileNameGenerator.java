package de.zebrajaeger.sphere2cube.tilenamegenerator;

import de.zebrajaeger.sphere2cube.converter.Face;
import de.zebrajaeger.sphere2cube.converter.TileRenderInfo;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public interface TileNameGenerator {
    String generateName(TileRenderInfo trf);

    String generateName(String pattern, Face face, int layer, int countX, int x, int countY, int y);

    String generateName(Face face, int layer, int countX, int x, int countY, int y);
}
