package de.zebrajaeger.sphere2cube.converter;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public enum Face {
    BACK(0), LEFT(1), FRONT(2), RIGHT(3), TOP(4), BOTTOM(5);

    private int nr;

    Face(int nr) {
        this.nr = nr;
    }

    public int getNr() {
        return nr;
    }}
