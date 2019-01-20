package de.zebrajaeger.sphere2cube.sandbox;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateTestImage {
    public static void main(String[] args) throws IOException {
        createImage(1000);
        createImage(5000);
        createImage(10000);
        createImage(20000);
    }

    private static void createImage(int w) throws IOException {
        System.out.println("Create Image with width '" + w + "'");
        int h = w / 2;

        BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);

        for (int i = 0; i < 37; ++i) {
            double v = (double) i / 36d;
            Color color = new Color((int) (255 * v), (int) (255 - (255 * v)), 0);
            g.setColor(color);

            int x = (int) (v * (double) w);
            g.drawLine(x, 0, x, h);
            g.drawLine(x - 1, 0, x - 1, h);
            g.drawLine(x + 1, 0, x + 1, h);
        }

        for (int i = 0; i < 19; ++i) {
            double v = (double) i / 18d;
            Color color = new Color(0, (int) (255 * v), (int) (255 - (255 * v)));
            g.setColor(color);

            int y = (int) (v * (double) h);
            g.drawLine(0, y, w, y);
            g.drawLine(0, y - 1, w, y - 1);
            g.drawLine(0, y + 1, w, y + 1);
        }

        g.dispose();

        ImageIO.write(target, "png", new File(String.format("samples/raster(%sx%s).png", w, h)));
        System.out.println("ok");
    }
}
