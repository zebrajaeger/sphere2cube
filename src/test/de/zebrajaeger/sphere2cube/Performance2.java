package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.converter.Face;
import de.zebrajaeger.sphere2cube.utils.Stopwatch;
import net.jafama.FastMath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
@SuppressWarnings("Duplicates")
@Disabled
public class Performance2 {
    private static final Logger LOG = LoggerFactory.getLogger(Performance2.class);
    private static final double PI = Math.PI;

    @Test
    public void test1() {
        double[] buffer = new double[3];

        Stopwatch stopwatch = Stopwatch.fromNow();
        // 100 Tiles á 512 x 512
        double result = 0;
        for (int count = 0; count < 100; ++count) {
            for (int i = 0; i < 262144; ++i) {
                result += x1(false, false, 0, 0, Face.BACK, 100d, 1000d, buffer);
            }
        }
        LOG.info("" + result);
        LOG.info(stopwatch.stop().toHumanReadable());
    }

    @Test
    public void test2() {
        double[] buffer = new double[3];

        Stopwatch stopwatch = Stopwatch.fromNow();
        // 100 Tiles á 512 x 512
        double result = 0;
        for (int count = 0; count < 100; ++count) {
            for (int i = 0; i < 262144; ++i) {
                result += x2(false, false, 0, 0, Face.BACK, 100d, 1000d, buffer);
            }
        }
        LOG.info("" + result);
        LOG.info(stopwatch.stop().toHumanReadable());
    }

    private double x1(boolean invertX, boolean invertY, int i, int j, Face face, double sourceEdge, double targetEdge, double[] result) {
        double a = 2d * (double) i / targetEdge;
        if (invertX) {
            a = 2d - a;
        }
        double b = 2d * (double) j / targetEdge;
        if (invertY) {
            b = 2d - b;
        }
        double x, y, z;
        switch (face) {
            case BACK:
                x = -1d;
                y = 1d - a;
                z = 1d - b;
                break;
            case LEFT:
                x = a - 1d;
                y = -1d;
                z = 1d - b;
                break;
            case FRONT:
                x = 1d;
                y = a - 1d;
                z = 1d - b;
                break;
            case RIGHT:
                x = 1d - a;
                y = 1d;
                z = 1d - b;
                break;
            case TOP:
                x = 1d - b;
                y = a - 1d;
                z = 1d;
                break;
            case BOTTOM:
                x = 1d - b;
                y = a - 1d;
                z = -1d;
                break;
            default:
                throw new RuntimeException("Unknown face:" + face);
        }

        //outImgToXYZ(xyz, i, j, face, targetEdge);
        double theta = Math.atan2(y, x);
        double r = Math.hypot(x, y);
        double phi = Math.atan2(z, r);

        // source img coords
        double uf = (2d * sourceEdge * (theta + PI) / PI);
        double vf = (2D * sourceEdge * (PI / 2d - phi) / PI);

        // Use bilinear interpolation between the four surrounding pixels
        int ui = (int) Math.floor(uf);  // coord of pixel to bottom left
        int vi = (int) Math.floor(vf);
        int u2 = ui + 1;       // coords of pixel to top right
        int v2 = vi + 1;
        double mu = uf - (double) ui;      // fraction of way across pixel
        double nu = vf - (double) vi;

        return x+y+z;
    }

    private double x2(boolean invertX, boolean invertY, int i, int j, Face face, double sourceEdge, double targetEdge, double[] result) {
        double a = 2d * (double) i / targetEdge;
        if (invertX) {
            a = 2d - a;
        }
        double b = 2d * (double) j / targetEdge;
        if (invertY) {
            b = 2d - b;
        }
        double x, y, z;
        switch (face) {
            case BACK:
                x = -1d;
                y = 1d - a;
                z = 1d - b;
                break;
            case LEFT:
                x = a - 1d;
                y = -1d;
                z = 1d - b;
                break;
            case FRONT:
                x = 1d;
                y = a - 1d;
                z = 1d - b;
                break;
            case RIGHT:
                x = 1d - a;
                y = 1d;
                z = 1d - b;
                break;
            case TOP:
                x = 1d - b;
                y = a - 1d;
                z = 1d;
                break;
            case BOTTOM:
                x = 1d - b;
                y = a - 1d;
                z = -1d;
                break;
            default:
                throw new RuntimeException("Unknown face:" + face);
        }

        //outImgToXYZ(xyz, i, j, face, targetEdge);
        double theta = FastMath.atan2(y, x);
        double r = FastMath.hypot(x, y);
        double phi = FastMath.atan2(z, r);

        // source img coords
        double uf = (2d * sourceEdge * (theta + PI) / PI);
        double vf = (2D * sourceEdge * (PI / 2d - phi) / PI);

        // Use bilinear interpolation between the four surrounding pixels
        int ui = (int) FastMath.floor(uf);  // coord of pixel to bottom left
        int vi = (int) FastMath.floor(vf);
        int u2 = ui + 1;       // coords of pixel to top right
        int v2 = vi + 1;
        double mu = uf - (double) ui;      // fraction of way across pixel
        double nu = vf - (double) vi;

        return x+y+z;
    }

}
