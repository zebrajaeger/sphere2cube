package de.zebrajaeger.sphere2cube.img;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public interface ISourceImage {
    void readPixel(int x, int y, double[] result);
    int getW();
    int getH();
    int getOriginalW();
    int getoriginalH();
}
