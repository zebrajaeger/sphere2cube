package de.zebrajaeger.sphere2cube.result;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class View {
    private double hlookat = 0d;
    private double vlookat = 0d;
    private double maxpixelzoom = 1d;
    private double fovmax = 150d;
    private Limitview limitview = Limitview.AUTO;

    public static View of() {
        return new View();
    }

    public View hlookat(double hlookat) {
        this.hlookat = hlookat;
        return this;
    }

    public View vlookat(double vlookat) {
        this.vlookat = hlookat;
        return this;
    }

    public View maxpixelzoom(double maxpixelzoom) {
        this.maxpixelzoom = maxpixelzoom;
        return this;
    }

    public View fovmax(double fovmax) {
        this.fovmax = fovmax;
        return this;
    }

    public View limitview(Limitview limitview) {
        this.limitview = limitview;
        return this;
    }

    private View() {
    }

    public double getHlookat() {
        return hlookat;
    }

    public double getVlookat() {
        return vlookat;
    }

    public double getMaxpixelzoom() {
        return maxpixelzoom;
    }

    public double getFovmax() {
        return fovmax;
    }

    public Limitview getLimitview() {
        return limitview;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
