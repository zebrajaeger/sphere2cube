package de.zebrajaeger.sphere2cube.result;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class Level {
    private int index;
    private int w;
    private int h;
    private int tilesX;
    private int tilesY;

    public Level(int index, int w, int h, int tilesX, int tilesY) {
        this.index = index;
        this.w = w;
        this.h = h;
        this.tilesX = tilesX;
        this.tilesY = tilesY;
    }

    public int getIndex() {
        return index;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getTilesX() {
        return tilesX;
    }

    public int getTilesY() {
        return tilesY;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
