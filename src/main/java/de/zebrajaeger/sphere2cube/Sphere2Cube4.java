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
public class Sphere2Cube4 {

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
    private Xyz xyz = new Xyz();
    private ISourceImage source;
    private ITargetImage target;

    public static void main(String[] args) throws IOException {
        new Sphere2Cube4().process(new File("samples/buckingham(1024 x512).jpg"));
        //new Sphere2Cube4().process(new File("samples/pano2(10000x5000).jpg"));
        //new Sphere2Cube4().process(new File("samples/pano2(10000x5000).jpg"));
    }

    public void process(File sourceFile) throws IOException {
        source = SourceImage.of(sourceFile);

        inW = source.getW();
        inH = source.getH();

        renderFaces(inW, 1024, 1024);
    }

    void renderFaces(int srcEdge, int minTargetEdge, int tileEdge) throws IOException {
        for (Face face : Face.values()) {
            renderFace(face, srcEdge, minTargetEdge, tileEdge);
        }
    }

    private void renderFace(Face face, int srcEdge, int minTargetEdge, int tileEdge) throws IOException {
        int targetEdge = srcEdge; // TODO ok??
        do {
            renderLayer(face, srcEdge, targetEdge, tileEdge);
            targetEdge /= 2;
        } while (targetEdge > minTargetEdge);
    }

    private void renderLayer(Face face, int srcEdge, int targetEdge, int tileEdge) throws IOException {
        double srcEdgeD = srcEdge;
        double targetEdgeD = targetEdge;

        File parent = new File(String.format("target/tiles/"));
        if (!parent.exists()) {
            parent.mkdirs();
        }

        int x = 0;
        for (int x1 = 0; x1 < targetEdge; x1 += tileEdge) {
            int x2 = Math.min(x1 + tileEdge, targetEdge);

            int y = 0;
            for (int y1 = 0; y1 < targetEdge; y1 += tileEdge) {
                int y2 = Math.min(y1 + tileEdge, targetEdge);
                renderTile(face,
                        srcEdgeD,
                        targetEdgeD,
                        x1, x2,
                        y1, y2,
                        new File(parent, String.format("%s_%04dx%04d.png",face.getFilePrefix(), x, y)));
                ++y;
            }
            ++x;
        }
    }

    private void renderTile(Face face, double srcEdge, double targetEdge, int x1, int x2, int y1, int y2, File targetFile) throws IOException {
        target = TargetImage.of(x2 - x1, y2 - y1);
        for (int x = x1, xt = 0; x < x2; ++x, ++xt) {
            for (int y = y1, yt = 0; y < y2; ++y, ++yt) {
                double[] value = copyPixel(x, y, face, srcEdge, targetEdge);
                writePixel(xt, yt, value);
            }
        }
        target.save(targetFile);
    }

    private int clip(int value, int max) {
        if (value < 0) {
            return 0;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private void readPixel(int x, int y, double[] result) {
        source.readPixel(x % inW, clip(y, inH - 1), result);
    }

    private void writePixel(int x, int y, double[] value) {
        target.writePixel(x, y, value);
    }

    private double[] copyPixel(int i, int j, Face face, double srcEdge, double targetEdge) {
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

        return pt;
    }

    private void outImgToXYZ(Xyz xyz, int i, int j, Face face, double edge) {
        double a = 2d * (double) i / edge;
        double b = 2d * (double) j / edge;

        switch (face) {
            case BACK:
                xyz.set(-1d, 1d - a, 1d - b);
                break;
            case LEFT:
                xyz.set(a - 1d, -1d, 1d - b);
                break;
            case FRONT:
                xyz.set(1d, a - 1d, 1d - b);
                break;
            case RIGHT:
                xyz.set(1d - a, 1d, 1d - b);
                break;
            case TOP:
                xyz.set(1d - b, a - 1d, 1d);
                break;
            case BOTTOM:
                xyz.set(1d - b, a - 1d, -1d);
                break;
        }
    }
    private enum Face {
        BACK("b"), LEFT("l"), FRONT("f"), RIGHT("r"), TOP("u"), BOTTOM("d");

        private String filePrefix;

        Face(String filePrefix) {
            this.filePrefix = filePrefix;
        }

        public String getFilePrefix() {
            return filePrefix;
        }
    }

    private class Xyz {
        double x;
        double y;
        double z;

        void set(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
