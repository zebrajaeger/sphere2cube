package de.zebrajaeger.sphere2cube.jocl;

import de.zebrajaeger.sphere2cube.converter.Face;

import java.util.function.Consumer;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public abstract class JoclCalculationJob {
    private int tileSize;

    private Face face;
    private double sourceEdge;
    private double targetEdge;

    private int x1;
    private int x2;
    private int y1;
    private int y2;

    private boolean invertX;
    private boolean invertY;

    private double[] uf;
    private double[] vf;
    private final int w;
    private final int h;

    public JoclCalculationJob(int tileSize, Face face, double sourceEdge, double targetEdge, int x1, int x2, int y1, int y2, boolean invertX, boolean invertY) {
        this.tileSize = tileSize;
        this.face = face;
        this.sourceEdge = sourceEdge;
        this.targetEdge = targetEdge;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.invertX = invertX;
        this.invertY = invertY;
        w = x2 - x1;
        h = y2 - y1;
    }

    protected void setToSource(int i, int x, int y, double[] srcArrayA, double[] srcArrayB) {
        double a = 2d * (double) x / getTargetEdge();
        if (isInvertX()) {
            a = 2d - a;
        }
        double b = 2d * (double) y / getTargetEdge();
        if (isInvertY()) {
            b = 2d - b;
        }
        srcArrayA[i] = a;
        srcArrayB[i] = b;
    }

    public abstract int getGlobalWorkUnit();

    public abstract void fillSource(double[] srcArrayA, double[] srcArrayB);

    public abstract void pixels(Consumer<Result> result);

    public Face getFace() {
        return face;
    }

    public void setResult(double[] uf, double[] vf) {
        this.uf = uf;
        this.vf = vf;
    }

    public int getTileSize() {
        return tileSize;
    }

    public double getSourceEdge() {
        return sourceEdge;
    }

    public double getTargetEdge() {
        return targetEdge;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public boolean isInvertX() {
        return invertX;
    }

    public boolean isInvertY() {
        return invertY;
    }

    public double[] getUf() {
        return uf;
    }

    public double[] getVf() {
        return vf;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public class Result {
        private int x;
        private int y;
        private int xIndex;
        private int yIndex;
        private double uV;
        private double fV;

        public Result set(int x, int y, int xIndex, int yIndex, double uV, double fV) {
            this.x = x;
            this.y = y;
            this.xIndex = xIndex;
            this.yIndex = yIndex;
            this.uV = uV;
            this.fV = fV;
            return this;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public double getuV() {
            return uV;
        }

        public double getfV() {
            return fV;
        }

        public int getxIndex() {
            return xIndex;
        }

        public int getyIndex() {
            return yIndex;
        }
    }
}
