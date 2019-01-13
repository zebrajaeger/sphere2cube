package de.zebrajaeger.sphere2cube.jocl;

import de.zebrajaeger.sphere2cube.converter.Face;

import java.util.function.Consumer;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class JoclEdgeCalculationJob extends JoclCalculationJob {

    public JoclEdgeCalculationJob(int tileSize, Face face, double sourceEdge, double targetEdge, int x1, int x2, int y1, int y2, boolean invertX, boolean invertY) {
        super(tileSize, face, sourceEdge, targetEdge, x1, x2, y1, y2, invertX, invertY);
    }

    @Override
    public int getGlobalWorkUnit() {
        return 4;
    }

    public void fillSource(double[] srcArrayA, double[] srcArrayB) {
        setToSource(0, getX1(), getY1(), srcArrayA, srcArrayB);
        setToSource(1, getX2() - 1, getY1(), srcArrayA, srcArrayB);
        setToSource(2, getX1(), getY2() - 1, srcArrayA, srcArrayB);
        setToSource(3, getX2() - 1, getY2() - 1, srcArrayA, srcArrayB);
    }

    public void pixels(Consumer<Result> result) {
        Result r = new Result();
        double[] uv = getUf();
        double[] fv = getVf();
        result.accept(r.set(getX1(), getY1(), 0, 0, uv[0], fv[0]));
        result.accept(r.set(getX2() - 1, getY1(), getW() - 1, 0, uv[1], fv[1]));
        result.accept(r.set(getX1(), getY2() - 1, 0, getH() - 1, uv[2], fv[2]));
        result.accept(r.set(getX2() - 1, getY2() - 1, getW() - 1, getH() - 1, uv[3], fv[3]));
    }
}
