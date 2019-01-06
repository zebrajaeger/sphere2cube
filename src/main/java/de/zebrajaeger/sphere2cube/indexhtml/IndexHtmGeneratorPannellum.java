package de.zebrajaeger.sphere2cube.indexhtml;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class IndexHtmGeneratorPannellum extends IndexHtmlGenerator {

    public static IndexHtmGeneratorPannellum of() {
        return new IndexHtmGeneratorPannellum();
    }

    private IndexHtmGeneratorPannellum() {
    }

    public String generate(IndexHtml indexHtml) throws IOException {
        return generate("pannellum.indexhtml.twig", indexHtml);
    }

    /**
     * @author Lars Brandt, Silpion IT Solutions GmbH
     */
    public static class IndexHtml {
        private String title;
        private String basePath;
        private String path;
        private String extension;
        private long tileResolution;
        private long maxLevel;
        private long cubeResolution;

        public IndexHtml(String title, String basePath, String path, String extension, long tileResolution, long maxLevel, long cubeResolution) {
            this.title = title;
            this.basePath = basePath;
            this.path = path;
            this.extension = extension;
            this.tileResolution = tileResolution;
            this.maxLevel = maxLevel;
            this.cubeResolution = cubeResolution;
        }

        public String getTitle() {
            return title;
        }

        public String getBasePath() {
            return basePath;
        }

        public String getPath() {
            return path;
        }

        public String getExtension() {
            return extension;
        }

        public long getTileResolution() {
            return tileResolution;
        }

        public long getMaxLevel() {
            return maxLevel;
        }

        public long getCubeResolution() {
            return cubeResolution;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
