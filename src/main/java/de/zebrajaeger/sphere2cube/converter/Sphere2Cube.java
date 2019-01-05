package de.zebrajaeger.sphere2cube.converter;

import de.zebrajaeger.sphere2cube.img.ISourceImage;
import de.zebrajaeger.sphere2cube.img.ITargetImage;
import de.zebrajaeger.sphere2cube.img.SourceImage;
import de.zebrajaeger.sphere2cube.img.TargetImage;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtmGenerator;
import de.zebrajaeger.sphere2cube.indexhtml.IndexHtml;
import de.zebrajaeger.sphere2cube.panoxml.PanoXmlGenerator;
import de.zebrajaeger.sphere2cube.result.Level;
import de.zebrajaeger.sphere2cube.result.RenderedPano;
import de.zebrajaeger.sphere2cube.result.View;
import de.zebrajaeger.sphere2cube.tilenamegenerator.KrPanoTileNameGenerator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Single equirectangular spherical Image to multible cube images
 */
@SuppressWarnings("Duplicates")
public class Sphere2Cube {

    private static final Logger LOG = LoggerFactory.getLogger(Sphere2Cube.class);

    private boolean tileDebug = true;

    private final KrPanoTileNameGenerator tileNameGenerator;
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

    public Sphere2Cube(String tilePattern) {
        tileNameGenerator = KrPanoTileNameGenerator.of(tilePattern);
    }

    public void renderPano(File sourceFile, File panoXmlFile, File indexHtmlFile, int tileEdge) throws IOException {
        RenderedPano renderedPano = renderPano(SourceImage.of(sourceFile).fov(180d, 0d, 90d, 0d), tileEdge);

        String panoXml = PanoXmlGenerator.of().generate(renderedPano);
        FileUtils.write(panoXmlFile, panoXml, StandardCharsets.UTF_8);

        String indexHtml = IndexHtmGenerator.of().generate(new IndexHtml("TestPano"));
        FileUtils.write(indexHtmlFile, indexHtml, StandardCharsets.UTF_8);
    }

    public RenderedPano renderPano(SourceImage sourceFile, int tileEdge) throws IOException {
        source = sourceFile;
        inW = source.getW();
        inH = source.getH();

        Map<Face, List<Level>> faceListMap = renderFaces(inW / 4, 1024, tileEdge);
        return new RenderedPano(RenderedPano.Type.CUBIC, tileEdge, View.of().maxpixelzoom(10d), faceListMap.get(Face.FRONT));
    }

    Map<Face, List<Level>> renderFaces(int srcEdge, int minTargetEdge, int tileEdge) throws IOException {
        Map<Face, List<Level>> result = new HashMap<>();
        for (Face face : Face.values()) {
            result.put(face, renderFace(face, srcEdge, minTargetEdge, tileEdge));
        }
        return result;
    }

    private List<Level> renderFace(Face face, int srcEdge, int minTargetEdge, int tileEdge) throws IOException {
        List<Level> result = new LinkedList<>();

        LOG.info("Render Face: " + face);
        int targetEdge = srcEdge;
        int layer = 1;
        do {
            result.add(renderLayer(face, layer, srcEdge, targetEdge, tileEdge));
            targetEdge /= 2;
            ++layer;
        } while (targetEdge > minTargetEdge);

        return result;
    }

    private Level renderLayer(Face face, int layer, int srcEdge, int targetEdge, int tileEdge) throws IOException {
        LOG.info("    Render Layer: " + layer);

        int x = 0;
        int y = 0;
        int count = (targetEdge / tileEdge) + ((targetEdge % tileEdge != 0) ? 1 : 0);

        for (int x1 = 0; x1 < targetEdge; x1 += tileEdge) {
            int x2 = Math.min(x1 + tileEdge, targetEdge);
            y = 0;
            for (int y1 = 0; y1 < targetEdge; y1 += tileEdge) {
                int y2 = Math.min(y1 + tileEdge, targetEdge);

                renderTile(
                        TileRenderInfo.of()
                                .tilePosition(face, x, y)
                                .tileSection(x1, x2, y1, y2)
                                .mirror(false, false)
                                .tilesInFace(count, count)
                                .edgeSizes(srcEdge, targetEdge, tileEdge, tileEdge)
                                .targetFile(new File(tileNameGenerator.generateName(face, layer, count, x + 1, count, y + 1)))
                );

                ++y;
            }
            ++x;
        }

        return new Level(layer, targetEdge, targetEdge, x, y);
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
        g.setColor(backgroundColor);
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

    private void renderTile(TileRenderInfo trf) throws IOException {
        Face face = trf.getFace();
        double sourcEdge = trf.getSourcEdge();
        double targetEdge = trf.getTargetEdge();

        ITargetImage target = TargetImage.of(trf.getTileEdgeX(), trf.getTileEdgeY());
        for (int x = trf.getX1(), xt = 0; x < trf.getX2(); ++x, ++xt) {
            for (int y = trf.getY1(), yt = 0; y < trf.getY2(); ++y, ++yt) {
                double[] value = copyPixel(x, y, face, sourcEdge, targetEdge);
                target.writePixel(xt, yt, value);
            }
        }

        if (tileDebug) {
            renderDebugInfo(target, trf);
        }
        target.save(trf.getTargetFile());
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
