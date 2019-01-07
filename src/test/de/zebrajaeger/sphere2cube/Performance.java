package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.utils.Stopwatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
@Disabled
public class Performance {

    private static final Logger LOG = LoggerFactory.getLogger(Performance.class);

    @Test
    public void test1() {
        testX(1000, 500, 100);
    }
    @Test
    public void test2() {
        testX(2000, 1000, 100);
    }
    @Test
    public void test3() {
        testX(5000, 2500, 10);
    }
    @Test
    public void test4() {
        testX(10000, 5000, 2);
    }
    @Test
    public void test5() {
        testX(20000, 10000, 2);
    }
    @Test
    public void test6() {
        testX(200000, 100000, 1);
    }

    private void testX(int w, int h, int count) {
        LOG.info("##################### WARMUP #####################");
        getAllPixelsRGBInt(w, h, count);
        getAllPixelsRGBFloat(w, h, count);
        getAllPixelsRGBDouble(w, h, count);

        LOG.info("----- Test1 -----");
        getAllPixelsRGBInt(w, h, count);
        getAllPixelsRGBFloat(w, h, count);
        getAllPixelsRGBDouble(w, h, count);
        LOG.info("----- Test2 -----");
        getAllPixelsRGBInt(w, h, count);
        getAllPixelsRGBFloat(w, h, count);
        getAllPixelsRGBDouble(w, h, count);
        LOG.info("----- Test3 -----");
        getAllPixelsRGBInt(w, h, count);
        getAllPixelsRGBFloat(w, h, count);
        getAllPixelsRGBDouble(w, h, count);
        LOG.info("----- Test4 -----");
        getAllPixelsRGBInt(w, h, count);
        getAllPixelsRGBFloat(w, h, count);
        getAllPixelsRGBDouble(w, h, count);
        LOG.info("----- Test5 -----");
        getAllPixelsRGBInt(w, h, count);
        getAllPixelsRGBFloat(w, h, count);
        getAllPixelsRGBDouble(w, h, count);
    }

    private void getAllPixelsRGBInt(int w, int h, int count) {
        LOG.info("getAllPixelsRGBInt(w:{}, h:{}, count:{})", w, h, count);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = bi.getRaster();
        Stopwatch stopwatch = Stopwatch.fromNow();

        int[] buffer = new int[3];

        for (int t = 0; t < count; ++t) {
            for (int i = 0; i < w; ++i) {
                for (int j = 0; j < h; ++j) {
                    raster.getPixel(i, j, buffer);
                }
            }
        }

        LOG.info(stopwatch.stop().toHumanReadable());
    }

    private void getAllPixelsRGBFloat(int w, int h, int count) {
        LOG.info("getAllPixelsRGBFloat(w:{}, h:{}, count:{})", w, h, count);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = bi.getRaster();
        Stopwatch stopwatch = Stopwatch.fromNow();

        float[] buffer = new float[3];

        for (int t = 0; t < count; ++t) {
            for (int i = 0; i < w; ++i) {
                for (int j = 0; j < h; ++j) {
                    raster.getPixel(i, j, buffer);
                }
            }
        }

        LOG.info(stopwatch.stop().toHumanReadable());
    }
    private void getAllPixelsRGBDouble(int w, int h, int count) {
        LOG.info("getAllPixelsRGBDouble(w:{}, h:{}, count:{})", w, h, count);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = bi.getRaster();
        Stopwatch stopwatch = Stopwatch.fromNow();

        double[] buffer = new double[3];

        for (int t = 0; t < count; ++t) {
            for (int i = 0; i < w; ++i) {
                for (int j = 0; j < h; ++j) {
                    raster.getPixel(i, j, buffer);
                }
            }
        }

        LOG.info(stopwatch.stop().toHumanReadable());
    }
}
