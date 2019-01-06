package de.zebrajaeger.sphere2cube.converter;

import de.zebrajaeger.sphere2cube.img.ITargetImage;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TileInfo {
    private int level;
    private Face face;
    private int indexX;
    private int indexY;
    private int edge;
    private ITargetImage target;

    public TileInfo(int level, Face face, int indexX, int indexY, int edge, ITargetImage target) {
        this.level = level;
        this.face = face;
        this.indexX = indexX;
        this.indexY = indexY;
        this.edge = edge;
        this.target = target;
    }

    public int getLevel() {
        return level;
    }

    public Face getFace() {
        return face;
    }

    public int getIndexX() {
        return indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public int getEdge() {
        return edge;
    }

    public ITargetImage getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
