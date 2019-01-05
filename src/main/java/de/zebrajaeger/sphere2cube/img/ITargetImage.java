package de.zebrajaeger.sphere2cube.img;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public interface ITargetImage {
    void writePixel(int x, int y, double[] value);

    Graphics2D getGraphics();

    int getW();

    int getH();

    void save(File file) throws IOException;

    void save(String filePath) throws IOException;
}
