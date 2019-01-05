package de.zebrajaeger.sphere2cube.converter;

import de.zebrajaeger.sphere2cube.img.ISourceImage;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.TargetImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.concurrent.Callable;

@SuppressWarnings("Duplicates")
public class TileRenderJob implements Callable<TileRenderInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(TileRenderJob.class);

    private boolean tileDebug;
    private TileRenderInfo trf;
    private ISourceImage source;

    private int inW;
    private int inH;
    private static final double PI = Math.PI;
    private double[] p1 = new double[3];
    private double[] p2 = new double[3];
    private double[] p3 = new double[3];
    private double[] p4 = new double[3];
    private double[] pt = new double[3];

    public static TileRenderJob of(boolean tileDebug, TileRenderInfo trf, ISourceImage source) {
        return new TileRenderJob(tileDebug, trf, source);
    }

    public static TileRenderJob of(TileRenderInfo trf, ISourceImage source) {
        return of(false, trf, source);
    }

    public TileRenderJob tileDebug(boolean tileDebug) {
        this.tileDebug = tileDebug;
        return this;
    }

    private TileRenderJob(boolean tileDebug, TileRenderInfo trf, ISourceImage source) {
        this.tileDebug = tileDebug;
        this.trf = trf;
        this.source = source;
        inW = source.getW();
        inH = source.getH();
    }

    @Override
    public TileRenderInfo call() throws Exception {
        long timestamp = System.currentTimeMillis();

        Face face = trf.getFace();
        double sourceEdge = trf.getSourcEdge();
        double targetEdge = trf.getTargetEdge();
        boolean invertX = false;
        boolean invertY = trf.getFace() == Face.TOP;

        ITargetImage target = TargetImage.of(trf.getTileEdgeX(), trf.getTileEdgeY());
        for (int x = trf.getX1(), xt = 0; x < trf.getX2(); ++x, ++xt) {
            for (int y = trf.getY1(), yt = 0; y < trf.getY2(); ++y, ++yt) {
                double[] value = copyPixel(invertX, invertY, x, y, face, sourceEdge, targetEdge);
                target.writePixel(xt, yt, value);
            }
        }

        if (tileDebug) {
            renderDebugInfo(target, trf);
        }
        target.save(trf.getTargetFile());

        LOG.info("Rendered: '{}' in {}ms ", trf.getTargetFile().getAbsolutePath(), System.currentTimeMillis() -  timestamp);

        return trf;
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

    private double[] copyPixel(boolean invertX, boolean invertY, int i, int j, Face face, double sourceEdge, double targetEdge) {
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

    private void renderDebugInfo(ITargetImage target, TileRenderInfo trf) {
        Graphics2D g = target.getGraphics();

        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();

        Stroke borderStroke = new BasicStroke(20.0f);

        int lX = trf.getTileEdgeX() - 1;
        int lY = trf.getTileEdgeY() - 1;

        Color backgroundColor = Color.WHITE;
        Color borderColor = Color.WHITE;
        switch (trf.getFace()) {
            case BACK:
                backgroundColor = new Color(0, 50, 0);
                borderColor = new Color(0, 200, 0);
                break;
            case LEFT:
                backgroundColor = new Color(50, 50, 0);
                borderColor = new Color(200, 200, 0);
                break;
            case FRONT:
                backgroundColor = new Color(0, 50, 50);
                borderColor = new Color(0, 200, 200);
                break;
            case RIGHT:
                backgroundColor = new Color(50, 0, 50);
                borderColor = new Color(200, 0, 200);
                break;
            case TOP:
                backgroundColor = new Color(0, 0, 50);
                borderColor = new Color(0, 0, 200);
                break;
            case BOTTOM:
                backgroundColor = new Color(50, 50, 50);
                borderColor = new Color(200, 200, 200);
                break;
        }

        // overwrite Content
        //g.setColor(backgroundColor);
        //g.fillRect(0, 0, trf.getTileEdgeX(), trf.getTileEdgeY());

        g.setColor(borderColor);
        g.setStroke(trf.isTopTile() ? borderStroke : originalStroke);
        g.drawLine(0, 0, lX, 0); // top
        g.setStroke(trf.isBottomTile() ? borderStroke : originalStroke);
        g.drawLine(0, lY, lX, lY); // bottom
        g.setStroke(trf.isLeftTile() ? borderStroke : originalStroke);
        g.drawLine(0, 0, 0, lY); // left
        g.setStroke(trf.isRightTile() ? borderStroke : originalStroke);
        g.drawLine(lX, 0, lX, lY); // right
        g.setStroke(originalStroke);

        g.setColor(borderColor);
        g.setFont(new Font("Verdana", Font.BOLD, 30));

        // index text
        String text = String.format("%04d x %04d", trf.getTileCountX(), trf.getTileCountY());
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getHeight();
        int textCenterY = ((trf.getTileEdgeY() - textHeight) / 2);
        g.drawString(text, (trf.getTileEdgeX() - textWidth) / 2, textCenterY - textHeight);

        // size text
        text = String.format("%04d x %04d", trf.getTileEdgeX(), trf.getTileEdgeY());
        textWidth = g.getFontMetrics().stringWidth(text);
        textHeight = g.getFontMetrics().getHeight();
        g.drawString(text, (trf.getTileEdgeX() - textWidth) / 2, textCenterY + textHeight);

        g.setColor(originalColor);
        g.setStroke(originalStroke);
    }
}
