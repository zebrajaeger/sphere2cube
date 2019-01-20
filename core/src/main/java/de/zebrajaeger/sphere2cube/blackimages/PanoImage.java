package de.zebrajaeger.sphere2cube.blackimages;

import de.zebrajaeger.sphere2cube.converter.Face;
import de.zebrajaeger.sphere2cube.converter.TileRenderResult;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class PanoImage {
    private int layer;
    private Face face;
    private int indexX;
    private int indexY;
    private String path;
    private Type type;
    private int width;
    private int height;

    public PanoImage(TileRenderResult trr, String path, Type type) {
        this.layer = trr.getTileRenderInfo().getLevel();
        this.face = trr.getTileRenderInfo().getFace();
        this.indexX = trr.getTileRenderInfo().getTileIndexX();
        this.indexY = trr.getTileRenderInfo().getTileIndexY();
        this.path = path;
        this.type = type;
        this.width = trr.getTileRenderInfo().getTileEdgeX();
        this.height = trr.getTileRenderInfo().getTileEdgeY();
    }

    public int getLayer() {
        return layer;
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

    public String getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public enum Type {
        BLACK,BORDER,IMAGE
    }
}
