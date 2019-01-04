package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.img.ISourceImage;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.img.TargetImage;

import java.io.File;
import java.io.IOException;

/**
 * Single equirectangular spherical Image to multible cube images
 */
@SuppressWarnings("Duplicates")
public class Sphere2Cube3 {

    private int inW;
    private int inH;

    // helpers
    private static final double PI = Math.PI;

    // buffer stuff
    private double[] p1 = new double[3];
    private double[] p2 = new double[3];
    private double[] p3 = new double[3];
    private double[] p4 = new double[3];
    private double[] pt = new double[3];
    private ISourceImage source;
    private ITargetImage target;

    public static void main(String[] args) throws IOException {
        //new Sphere2Cube3().process(new File("samples/buckingham(1024 x512).jpg"));
        //new Sphere2Cube3().process(new File("samples/pano2(10000x5000).jpg"));
        new Sphere2Cube3().process(new File("samples/pano2(10000x5000).jpg"));
    }

    private void outImgToXYZ(Xyz xyz, int i, int j, int face, double edge) {
        double a = 2d * (double) i / edge;
        double b = 2d * (double) j / edge;

        switch (face) {
            case 0:
                xyz.set(-1d, 1d - a, 1d - b); // back
                break;
            case 1:
                xyz.set(a - 1d, -1d, 1d - b);// left
                break;
            case 2:
                xyz.set(1d, a - 1d, 1d - b);// front
                break;
            case 3:
                xyz.set(1d - a, 1d, 1d - b);// right
                break;
            case 4:
                xyz.set(1d - b, a - 1d, 1d);// top
                break;
            case 5:
                xyz.set(1d - b, a - 1d, -1d); // bottom
                break;
            default:
                throw new RuntimeException("WTF!!!!???");
        }
    }

    public void process(File sourceFile) throws IOException {
        source = SourceImage.of(sourceFile);

        inW = source.getW();
        inH = source.getH();
        double srcEdge = inW / 4;
        double targetEdge = 1000d;
        int targetEdgeI = (int) targetEdge;

        Xyz xyz = new Xyz();

        // 0 - back, 1 - left 2 - front, 3 - right, 4 - top, 5 - bottom
        for (int face = 0; face < 6; ++face) {
            target = TargetImage.of(targetEdgeI, targetEdgeI);

            for (int i = 0; i < targetEdgeI; ++i) {
                for (int j = 0; j < targetEdgeI; ++j) {
                    copyPixel(xyz, i, j, face, srcEdge, targetEdge);
                }
            }

            target.save(new File("target/out_" + face + ".png"));
        }
    }

    private int clip(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private void readPixel(int x, int y, double[] result) {
        source.readPixel(x % inW, clip(y, 0, inH - 1), result);
    }

    private void writePixel(int x, int y, double[] value) {
        target.writePixel(x, y, value);
    }

    private void copyPixel(Xyz xyz, int i, int j, int face, double srcEdge, double targetEdge) {
        outImgToXYZ(xyz, i, j, face, targetEdge);
        double theta = Math.atan2(xyz.y, xyz.x);
        double r = Math.hypot(xyz.x, xyz.y);
        double phi = Math.atan2(xyz.z, r);

        // source img coords
        double uf = (2d * srcEdge * (theta + PI) / PI);
        double vf = (2D * srcEdge * (PI / 2d - phi) / PI);

        // Use bilinear interpolation between the four surrounding pixels
        int ui = (int) Math.floor(uf);  // coord of pixel to bottom left
        int vi = (int) Math.floor(vf);
        int u2 = ui + 1;       // coords of pixel to top right
        int v2 = vi + 1;
        double mu = uf - (double) ui;      // fraction of way across pixel
        double nu = vf - (double) vi;

        // Pixel values of four corners
        readPixel(ui, vi, p1);
        readPixel(u2, vi, p2);
        readPixel(ui, v2, p3);
        readPixel(u2, v2, p4);

        // interpolate
        pt[0] = p1[0] * (1d - mu) * (1d - nu)
                + p2[0] * (mu) * (1d - nu)
                + p3[0] * (1d - mu) * nu
                + p4[0] * mu * nu;
        pt[1] = p1[1] * (1d - mu) * (1d - nu)
                + p2[1] * (mu) * (1d - nu)
                + p3[1] * (1d - mu) * nu
                + p4[1] * mu * nu;
        pt[2] = p1[2] * (1d - mu) * (1d - nu)
                + p2[2] * (mu) * (1d - nu)
                + p3[2] * (1d - mu) * nu
                + p4[2] * mu * nu;

        writePixel(i, j, pt);
    }

    private class Xyz {
        double x;
        double y;
        double z;

        public void set(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
