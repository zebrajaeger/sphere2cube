package de.zebrajaeger.sphere2cube.jocl;

import de.zebrajaeger.sphere2cube.converter.Face;

import java.util.function.Consumer;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class JoclAllCalculationJob extends JoclCalculationJob {

    public JoclAllCalculationJob(int tileSize, Face face, double targetEdge, int x1, int x2, int y1, int y2, boolean invertX, boolean invertY) {
        super(tileSize, face, targetEdge, x1, x2, y1, y2, invertX, invertY);
    }

    public int getGlobalWorkUnit() {
        return getW() * getH();
    }

    public void fillSource(double[] srcArrayA, double[] srcArrayB) {
        int i = 0;
        for (int y = getY1(); y < getY2(); ++y) {
            for (int x = getX1(); x < getY2(); ++x) {
                setToSource(i, x, y, srcArrayA, srcArrayA);
                ++i;
            }
        }
    }

    public void pixels(Consumer<Result> result) {
        Result r = new Result();
        double[] uv = getUv();
        double[] fv = getFv();

        int i = 0;
        for (int y = getY1(), yi = 0; y < getY2(); ++y, ++yi) {
            for (int x = getX1(), xi = 0; x < getX2(); ++x, ++xi) {
                r.set(x, y, xi, yi, uv[i], fv[i]);
                result.accept(r);
                ++i;
            }
        }
    }
}
