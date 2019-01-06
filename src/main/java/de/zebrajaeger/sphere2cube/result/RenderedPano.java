package de.zebrajaeger.sphere2cube.result;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Comparator;
import java.util.List;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class RenderedPano {
    private Type type = Type.CUBIC;
    private int tileSize = 512;
    private List<Level> levels;
    private boolean showerrors = true;

    public RenderedPano(Type type, int tileSize,  List<Level> levels) {
        this.type = type;
        this.tileSize = tileSize;
        this.levels = levels;
    }

    public Level getMaxLevel(){
        return levels
                .stream()
                .max(Comparator.comparingInt(Level::getIndex))
                .orElseThrow(() -> new IllegalStateException("No level found"));
    }

    public boolean isMultires() {
        return levels.size() > 1;
    }

    public Type getType() {
        return type;
    }

    public int getTileSize() {
        return tileSize;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public boolean isShowerrors() {
        return showerrors;
    }

    public enum Type {
        CUBIC("CUBE")
        ;

        private String label;

        Type(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
