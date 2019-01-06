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
import java.util.function.Consumer;

@SuppressWarnings("Duplicates")
public class TileRenderJob implements Callable<TileRenderInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(TileRenderJob.class);

    private Consumer<TileRenderResult> renderConsumer;
    private Consumer<TileRenderResult> noRenderConsumer;

    private boolean tileDebug;
    private boolean tileDebugOverwriteContent = false;

    private TileRenderInfo trf;
    private ISourceImage source;

    private int inW;
    private int inH;
    private static final double PI = Math.PI;
    private double[] p1 = new double[3];
    private double[] p2 = new double[3];
    private double[] p3 = new double[3];
    private double[] p4 = new double[3];

    public static TileRenderJob of(boolean tileDebug, TileRenderInfo trf, ISourceImage source) {
        return new TileRenderJob(tileDebug, trf, source);
    }

    public static TileRenderJob of(TileRenderInfo trf, ISourceImage source) {
        return of(false, trf, source);
    }

    public TileRenderJob renderConsumer(Consumer<TileRenderResult> renderConsumer) {
        this.renderConsumer = renderConsumer;
        return this;
    }
    public TileRenderJob noRenderConsumer(Consumer<TileRenderResult> noRenderConsumer) {
        this.noRenderConsumer = noRenderConsumer;
        return this;
    }

    public TileRenderJob debug(boolean tileDebug, boolean tileDebugOverwriteContent) {
        this.tileDebug = tileDebug;
        this.tileDebugOverwriteContent = tileDebugOverwriteContent;
        return this;
    }

    private TileRenderJob(boolean tileDebug, TileRenderInfo trf, ISourceImage source) {
        this.tileDebug = tileDebug;
        this.trf = trf;
        this.source = source;
        inW = source.getW();
        inH = source.getH();
    }

    private boolean isTileWithinSource(boolean invertX, boolean invertY) {
        double[] result = new double[3];
        int x1 = trf.getX1();
        int y1 = trf.getY1();
        int x2 = trf.getX2() - 1;
        int y2 = trf.getY2() - 1;
        Face face = trf.getFace();
        int sourceEdge = trf.getSourceEdge();
        double targetEdge = trf.getTargetEdge();
        return copyPixel(invertX, invertY, x1, y1, face, sourceEdge, targetEdge, result)
                | copyPixel(invertX, invertY, x2, y1, face, sourceEdge, targetEdge, result)
                | copyPixel(invertX, invertY, x1, trf.getY2() - 1, face, sourceEdge, targetEdge, result)
                | copyPixel(invertX, invertY, x2, y2, face, sourceEdge, targetEdge, result);
    }

    @Override
    public TileRenderInfo call() {
        long timestamp = System.currentTimeMillis();

        boolean invertX = false;
        boolean invertY = trf.getFace() == Face.TOP;

        boolean force = trf.isForceTileRendering();
        // render if precheck allowed and precheck match or n precheck
        if (force || !trf.isPreCheck() || isTileWithinSource(invertX, invertY)) {

            Face face = trf.getFace();
            double sourceEdge = trf.getSourcEdge();
            double targetEdge = trf.getTargetEdge();

            double[] result = new double[3];
            boolean withinSource = false;
            ITargetImage target = TargetImage.of(trf.getTileEdgeX(), trf.getTileEdgeY());
            for (int x = trf.getX1(), xt = 0; x < trf.getX2(); ++x, ++xt) {
                for (int y = trf.getY1(), yt = 0; y < trf.getY2(); ++y, ++yt) {
                    withinSource |= copyPixel(invertX, invertY, x, y, face, sourceEdge, targetEdge, result);
                    target.writePixel(xt, yt, result);
                }
            }

            if (force|| trf.isRenderTileIfNotInSource() || withinSource) {
                if (tileDebug) {
                    renderDebugInfo(target, trf);
                }
                renderConsumer.accept(new TileRenderResult(trf, target));
                LOG.info("       Rendered in {}ms ", System.currentTimeMillis() - timestamp);
            } else {
                noRenderConsumer.accept(new TileRenderResult(trf, target));
            }
        } else {
            noRenderConsumer.accept(new TileRenderResult(trf, null));
        }
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

    private boolean readPixel(int x, int y, double[] result) {
        return source.readPixel(x % inW, clip(y, inH - 1), result);
    }

    private boolean copyPixel(boolean invertX, boolean invertY, int i, int j, Face face, double sourceEdge, double targetEdge, double[] result) {
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
        boolean insidePano = readPixel(ui, vi, p1);
        insidePano |= readPixel(u2, vi, p2);
        insidePano |= readPixel(ui, v2, p3);
        insidePano |= readPixel(u2, v2, p4);

        // interpolate
        result[0] = p1[0] * (1d - mu) * (1d - nu)
                + p2[0] * (mu) * (1d - nu)
                + p3[0] * (1d - mu) * nu
                + p4[0] * mu * nu;
        result[1] = p1[1] * (1d - mu) * (1d - nu)
                + p2[1] * (mu) * (1d - nu)
                + p3[1] * (1d - mu) * nu
                + p4[1] * mu * nu;
        result[2] = p1[2] * (1d - mu) * (1d - nu)
                + p2[2] * (mu) * (1d - nu)
                + p3[2] * (1d - mu) * nu
                + p4[2] * mu * nu;

        return insidePano;
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
        if (tileDebugOverwriteContent) {
            g.setColor(backgroundColor);
            g.fillRect(0, 0, trf.getTileEdgeX(), trf.getTileEdgeY());
        }

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
