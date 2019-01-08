package de.zebrajaeger.sphere2cube.converter;

import de.zebrajaeger.sphere2cube.img.ISourceImage;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.TargetImage;
import de.zebrajaeger.sphere2cube.jocl.JoclAllCalculationJob;
import de.zebrajaeger.sphere2cube.jocl.JoclCalculator;
import de.zebrajaeger.sphere2cube.jocl.JoclCalculatorManager;
import de.zebrajaeger.sphere2cube.jocl.JoclEdgeCalculationJob;
import net.jafama.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@SuppressWarnings("Duplicates")
public class TileRenderJobJocl implements Callable<TileRenderInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(TileRenderJobJocl.class);

    private Consumer<TileRenderResult> renderConsumer;
    private Consumer<TileRenderResult> noRenderConsumer;

    private boolean tileDebug;
    private boolean tileDebugOverwriteContent = false;

    private TileRenderInfo trf;
    private ISourceImage source;

    private int inW;
    private int inH;
    private double[] p1 = new double[3];
    private double[] p2 = new double[3];
    private double[] p3 = new double[3];
    private double[] p4 = new double[3];

    public static TileRenderJobJocl of(boolean tileDebug, TileRenderInfo trf, ISourceImage source) {
        return new TileRenderJobJocl(tileDebug, trf, source);
    }

    public static TileRenderJobJocl of(TileRenderInfo trf, ISourceImage source) {
        return of(false, trf, source);
    }

    public TileRenderJobJocl renderConsumer(Consumer<TileRenderResult> renderConsumer) {
        this.renderConsumer = renderConsumer;
        return this;
    }

    public TileRenderJobJocl noRenderConsumer(Consumer<TileRenderResult> noRenderConsumer) {
        this.noRenderConsumer = noRenderConsumer;
        return this;
    }

    public TileRenderJobJocl debug(boolean tileDebug, boolean tileDebugOverwriteContent) {
        this.tileDebug = tileDebug;
        this.tileDebugOverwriteContent = tileDebugOverwriteContent;
        return this;
    }

    private TileRenderJobJocl(boolean tileDebug, TileRenderInfo trf, ISourceImage source) {
        this.tileDebug = tileDebug;
        this.trf = trf;
        this.source = source;
        inW = source.getW();
        inH = source.getH();
    }

    @Override
    public TileRenderInfo call() {
        long timestamp = System.currentTimeMillis();

        boolean invertX = false;
        boolean invertY = trf.getFace() == Face.TOP;

        ITargetImage target = TargetImage.of(trf.getTileEdgeX(), trf.getTileEdgeY());

        JoclCalculator calculator = JoclCalculatorManager.getInstance().getCalculator();

        double[] pixelValue = new double[3];

        final AtomicBoolean withinSource = new AtomicBoolean(false);
        calculator.calc(new JoclEdgeCalculationJob(
                trf.getTileEdgeX(), trf.getFace(), trf.getTargetEdge(),
                trf.getX1(), trf.getX2(), trf.getY1(), trf.getY2(),
                invertX, invertY)).pixels(
                pixel -> withinSource.set(withinSource.get() || readBilinearPixel(pixel.getuV(), pixel.getfV(), pixelValue))
        );

        withinSource.set(true);

        // render if precheck allowed and precheck match or n precheck
        if (trf.isForceTileRendering() || !trf.isPreCheck() || withinSource.get()) {
            withinSource.set(false);
            calculator.calc(new JoclAllCalculationJob(
                    trf.getTileEdgeX(), trf.getFace(), trf.getTargetEdge(),
                    trf.getX1(), trf.getX2(), trf.getY1(), trf.getY2(),
                    invertX, invertY)).pixels(pixel -> {
                withinSource.set(withinSource.get() || readBilinearPixel(pixel.getuV(), pixel.getfV(), pixelValue));
                target.writePixel(pixel.getxIndex(), pixel.getyIndex(), pixelValue);
            });

            if (tileDebug) {
                renderDebugInfo(target, trf);
            }

            if (withinSource.get()) {
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
//
//    private boolean isTileWithinSource(JoclCalculationJob job) {
//        JoclCalculator calculator = JoclCalculatorManager.getInstance().getCalculator();
//        calculator.calc(new JoclEdgeCalculationJob())
//        double[] pixelValue = new double[3];
//        final AtomicBoolean withinSource = new AtomicBoolean(false);
//        job.edges(edge -> withinSource.set(withinSource.get() || readBilinearPixel(edge.getuV(), edge.getfV(), pixelValue)));
//        return withinSource.get();
//    }

    private boolean readPixel(int x, int y, double[] result) {
        if (y < 0) {
            y = 0;
        }
        if (y > inH - 1) {
            y = inH - 1;
        }
        return source.readPixel(x % inW, y, result);
    }

    private boolean readBilinearPixel(double uf, double vf, double[] result) {

        // Use bilinear interpolation between the four surrounding pixels
        int ui = (int) FastMath.floor(uf);  // coord of pixel to bottom left
        int vi = (int) FastMath.floor(vf);
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

        // Level/Face text
        int textHeight = g.getFontMetrics().getHeight();
        String text = String.format("%02d / %s", trf.getLevel(), trf.getFace().toString());
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textCenterY = ((trf.getTileEdgeY() - textHeight) / 2);
        g.drawString(text, (trf.getTileEdgeX() - textWidth) / 2, textCenterY - (int) (1.5 * textHeight));

        // index text
        text = String.format("%04d x %04d", trf.getTileCountX(), trf.getTileCountY());
        textWidth = g.getFontMetrics().stringWidth(text);
        textCenterY = ((trf.getTileEdgeY() - textHeight) / 2);
        g.drawString(text, (trf.getTileEdgeX() - textWidth) / 2, textCenterY);

        // size text
        text = String.format("%04d x %04d", trf.getTileEdgeX(), trf.getTileEdgeY());
        textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (trf.getTileEdgeX() - textWidth) / 2, textCenterY + (int) (2.5 * textHeight));

        g.setColor(originalColor);
        g.setStroke(originalStroke);

        g.dispose();
    }
}
