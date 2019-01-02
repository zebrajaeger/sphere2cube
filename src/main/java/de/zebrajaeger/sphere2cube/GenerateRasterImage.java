package de.zebrajaeger.sphere2cube;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateRasterImage {
    public static void main(String[] args) throws IOException {
        File targetFile = new File("samples/raster.png");

        BufferedImage target = new BufferedImage(1000, 500, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) target.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 1000, 500);

        for (int i = 0; i < 37; ++i) {
            double v = (double)i/36d;
            Color color = new Color((int) (255 * v), (int) (255 - (255 * v)), 0);
            g.setColor(color);

            int x = (int) (v*1000d);
            g.drawLine(x, 0, x, 500);
            g.drawLine(x-1, 0, x-1, 500);
            g.drawLine(x+1, 0, x+1, 500);
        }

        for (int i = 0; i < 19; ++i) {
            double v = (double)i/18d;
            Color color = new Color(0, (int) (255 * v),(int) (255 - (255 * v)));
            g.setColor(color);

            int y = (int) (v*500d);
            g.drawLine(0, y, 1000, y);
            g.drawLine(0, y-1, 1000, y-1);
            g.drawLine(0, y+1, 1000, y+1);
        }
        ImageIO.write(target, "png", targetFile);
    }
}
