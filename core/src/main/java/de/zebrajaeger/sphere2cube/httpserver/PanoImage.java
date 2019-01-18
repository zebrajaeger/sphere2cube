package de.zebrajaeger.sphere2cube.httpserver;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class PanoImage {
    private String path;
    private boolean isBlack;
    private int width;
    private int height;

    public PanoImage(String path, boolean isBlack, int width, int height) {
        this.path = path;
        this.isBlack = isBlack;
        this.width = width;
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public boolean isBlack() {
        return isBlack;
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
}
