package de.zebrajaeger.sphere2cube.jocl;

import de.zebrajaeger.sphere2cube.converter.Face;

import java.util.function.Consumer;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class JoclEdgeCalculationJob extends JoclCalculationJob {

    public JoclEdgeCalculationJob(int tileSize, Face face, double targetEdge, int x1, int x2, int y1, int y2, boolean invertX, boolean invertY) {
        super(tileSize, face, targetEdge, x1, x2, y1, y2, invertX, invertY);
    }

    @Override
    public int getGlobalWorkUnit() {
        return 4;
    }

    public void fillSource(double[] srcArrayA, double[] srcArrayB) {
        int x2 = getW() - 1;
        int y2 = getH() - 1;
        setToSource(0, 0, 0, srcArrayA, srcArrayB);
        setToSource(1, x2, 0, srcArrayA, srcArrayB);
        setToSource(2, 0, y2, srcArrayA, srcArrayB);
        setToSource(3, x2, y2, srcArrayA, srcArrayB);
    }

    public void pixels(Consumer<Result> result) {
        Result r = new Result();
        double[] uv = getUv();
        double[] fv = getFv();
        r.set(getX1(), getY1(), 0, 0, uv[0], fv[0]);
        r.set(getX2() - 1, getY1(), getW() - 1, 0, uv[1], fv[1]);
        r.set(getX1(), getY2() - 1, 0, getH() - 1, uv[2], fv[2]);
        r.set(getX2() - 1, getY2() - 1, getW() - 1, getH() - 1, uv[3], fv[3]);
    }

    //    public void fillSource(double[] srcArrayA, double[] srcArrayB) {
//        int i=0;
//        for (int y = y1; y < y2; ++y) {
//            for (int x = x1; x < x2; ++x) {
//                double a = 2d * (double) x / targetEdge;
//                if (invertX) {
//                    a = 2d - a;
//                }
//                double b = 2d * (double) y / targetEdge;
//                if (invertY) {
//                    b = 2d - b;
//                }
//                srcArrayA[i] = a;
//                srcArrayB[i] = b;
//            }
//        }
//    }
//
//    public void pixels(Consumer<Result> result) {
//        int i=0;
//        Result r = new Result();
//        for (int y = y1, yi = 0; y < y2; ++y, ++yi) {
//            for (int x = x1, xi = 0; x < x2; ++x, ++x) {
//                r.set(x, y, xi, yi, uv[i], fv[i]);
//                result.accept(r);
//            }
//        }
//    }
//
//    private void edge(Consumer<Result> consumer, int x, int y, Result r) {
//        int i = (tileSize * x) + y;
//        consumer.accept(r.set(x, y, x1 + x, y1 + y, uv[i], fv[i]));
//    }
//
//    public void edges(Consumer<Result> consumer) {
//        Result r = new Result();
//        int ex = x2 - x1-1;
//        int ey = y2 - y1-1;
//        edge(consumer, 0, 0, r);
//        edge(consumer, ex, 0, r);
//        edge(consumer, 0, ey, r);
//        edge(consumer, ex, ey, r);
//    }
//
//    public Face getFace() {
//        return face;
//    }
//
//    public void setResult(double[] uv, double[] fv) {
//        this.uv = uv;
//        this.fv = fv;
//    }
//
//    public class Result {
//        private int x;
//        private int y;
//        private int xIndex;
//        private int yIndex;
//        private double uV;
//        private double fV;
//
//        public Result set(int x, int y, int xIndex, int yIndex, double uV, double fV) {
//            this.x = x;
//            this.y = y;
//            this.xIndex = xIndex;
//            this.yIndex = yIndex;
//            this.uV = uV;
//            this.fV = fV;
//            return this;
//        }
//
//        public int getX() {
//            return x;
//        }
//
//        public int getY() {
//            return y;
//        }
//
//        public double getuV() {
//            return uV;
//        }
//
//        public double getfV() {
//            return fV;
//        }
//
//        public int getxIndex() {
//            return xIndex;
//        }
//
//        public int getyIndex() {
//            return yIndex;
//        }
//    }
}
