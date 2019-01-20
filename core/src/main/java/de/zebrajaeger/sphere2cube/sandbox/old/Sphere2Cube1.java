package de.zebrajaeger.sphere2cube.sandbox.old;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Single equirectangular spherical Image to single cube image
 */
@SuppressWarnings("Duplicates")
public class Sphere2Cube1 {

    private WritableRaster sourceRaster;
    private BufferedImage source;
    private BufferedImage out;
    private WritableRaster targetRaster;
    private int inW;
    private int inH;
    private int edge;

    public static void main(String[] args) throws IOException {
        new Sphere2Cube1().process();
    }

    private void outImgToXYZ(Xyz xyz, int i, int j, int face, int edge) {
        double edgeF = edge;

        double a = 2d * (double) i / edgeF;
        double b = 2d * (double) j / edgeF;

        switch (face) {
            case 0:
                // back
                xyz.x = -1d;
                xyz.y = 1d - a;
                xyz.z = 3d - b;
                break;
            case 1:
                // left
                xyz.x = a - 3d;
                xyz.y = -1d;
                xyz.z = 3d - b;
                break;
            case 2:
                // front
                xyz.x = 1d;
                xyz.y = a - 5d;
                xyz.z = 3d - b;
                break;
            case 3:
                // right
                xyz.x = 7d - a;
                xyz.y = 1d;
                xyz.z = 3d - b;
                break;
            case 4:
                // top
                xyz.x = b - 1d;
                xyz.y = a - 5d;
                xyz.z = 1d;
                break;
            case 5:
                // bottom
                xyz.x = 5d - b;
                xyz.y = a - 5d;
                xyz.z = -1d;
                break;
            default:
                throw new RuntimeException("WTF!!!!???");
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
        sourceRaster.getPixel(x % inW, clip(y, 0, inH - 1), result);
    }

    private void writePixel(int x, int y, double[] value) {
        targetRaster.setPixel(x, y, value);
    }

    public void process() throws IOException {
        File sourceFile = new File("samples/buckingham.jpg");
        File targetFile = new File("out.jpg");
        source = ImageIO.read(sourceFile);
        sourceRaster = source.getRaster();

        inW = source.getWidth();
        inH = source.getHeight();
        edge = inW / 4;
        out = new BufferedImage(inW, inW * 3 / 4, BufferedImage.TYPE_INT_RGB);
        targetRaster = out.getRaster();

        Xyz xyz = new Xyz();
        double[] p1 = new double[3];
        double[] p2 = new double[3];
        double[] p3 = new double[3];
        double[] p4 = new double[3];
        double[] pt = new double[3];

        final double pi = Math.PI;
        for (int i = 0; i < inW; ++i) {

            // 0 - back, 1 - left 2 - front, 3 - right, 4 - top, 5 - bottom
            int face = i / edge;
            int jFrom = edge;
            int jTo = edge * 2;
            if (face == 2) {
                jFrom = 0;
                jTo = edge * 3;
            }

            for (int j = jFrom; j < jTo; ++j) {
                int face2;
                if (j < edge) {
                    face2 = 4;
                } else if (j >= 2 * edge) {
                    face2 = 5;
                } else {
                    face2 = face;
                }

                outImgToXYZ(xyz, i, j, face2, edge);
                double theta = Math.atan2(xyz.y, xyz.x);
                double r = Math.hypot(xyz.x, xyz.y);
                double phi = Math.atan2(xyz.z, r);

                // source img coords
                double uf = (2d * edge * (theta + pi) / pi);
                double vf = (2D * edge * (pi / 2d - phi) / pi);

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
        }

        ImageIO.write(out, "jpg", targetFile);
    }

    private class Xyz {
        double x;
        double y;
        double z;
    }
}
