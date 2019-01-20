package de.zebrajaeger.sphere2cube.blackimages;

import java.util.List;

public class Raster {
    private PanoImage[][] raster;
    private int width = 0;
    private int height = 0;

    public Raster(List<PanoImage> panoImages) {
        // find out size
        for (PanoImage panoImage : panoImages) {
            width = Math.max(width, panoImage.getIndexX() + 1);
            height = Math.max(height, panoImage.getIndexY() + 1);
        }

        // init with arrays
        raster = new PanoImage[height][];
        for (int i = 0; i < height; ++i) {
            raster[i] = new PanoImage[width];
        }

        // put to raster
        panoImages.forEach(this::set);
    }

    public void set(PanoImage panoImage) {
        set(panoImage.getIndexX(), panoImage.getIndexY(), panoImage);
    }

    public void set(int x, int y, PanoImage panoImage) {
        raster[y][x] = panoImage;
    }

    public PanoImage get(int x, int y) {
        return raster[y][x];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < height; ++i) {
            if (i != 0) {
                sb.append('\n');
            }
            for (int j = 0; j < width; ++j) {
                PanoImage panoImage = get(j, i);
                if (panoImage == null || panoImage.getType() == null) {
                    sb.append('?');
                } else {
                    switch (panoImage.getType()) {
                        case BLACK:
                            sb.append('\u00B7');
                            break;
                        case BORDER:
                            sb.append('O');
                            break;
                        case IMAGE:
                            sb.append('#');
                            break;
                    }
                }
            }
        }

        return sb.toString();
    }
}
