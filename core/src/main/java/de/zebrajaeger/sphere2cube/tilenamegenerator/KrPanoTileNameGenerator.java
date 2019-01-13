package de.zebrajaeger.sphere2cube.tilenamegenerator;

/**
 * https://krpano.com/docu/xml/#image.url.placeholders
 *
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class KrPanoTileNameGenerator extends GenericTileNameGenerator {

    public static KrPanoTileNameGenerator of() {
        return new KrPanoTileNameGenerator();
    }

    public KrPanoTileNameGenerator() {
        super("%s/l%l/%000y_%000x.png");
    }
}
