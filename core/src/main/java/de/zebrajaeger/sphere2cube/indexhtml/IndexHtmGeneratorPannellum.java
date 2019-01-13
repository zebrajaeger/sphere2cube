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
        private String pageTitle;
        private String panoTitle;
        private String panoAuthor;
        private String basePath;
        private String path;
        private String extension;
        private long tileResolution;
        private long maxLevel;
        private long cubeResolution;

        private double minYaw = -180d;
        private double maxYaw = 180d;
        private double minPitch = -90;
        private double maxPitch = -90;

        private boolean autoLoad = false;
        private double autoRotate = 0d;

        public static IndexHtml of() {
            return new IndexHtml();
        }

        private IndexHtml() {
        }

        public IndexHtml auto(boolean autoLoad, double autoRotate) {
            this.autoLoad = autoLoad;
            this.autoRotate = autoRotate;

            return this;
        }

        public IndexHtml resolution(long cubeResolution, long tileResolution, long maxLevel) {
            this.cubeResolution = cubeResolution;
            this.tileResolution = tileResolution;
            this.maxLevel = maxLevel;

            return this;
        }

        public IndexHtml path(String basePath, String path, String extension) {
            this.basePath = basePath;
            this.path = path;
            this.extension = extension;
            return this;
        }

        public IndexHtml meta(String pageTitle, String panoTitle, String panoAuthor) {
            this.pageTitle = pageTitle;
            this.panoTitle = panoTitle;
            this.panoAuthor = panoAuthor;
            return this;
        }

        public IndexHtml fov(double minYaw, double maxYaw, double minPitch, double maxPitch) {
            this.minYaw = minYaw;
            this.maxYaw = maxYaw;
            this.minPitch = minPitch;
            this.maxPitch = maxPitch;
            return this;
        }

        public String getPageTitle() {
            return pageTitle;
        }

        public String getPanoTitle() {
            return panoTitle;
        }

        public String getPanoAuthor() {
            return panoAuthor;
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

        public double getMinYaw() {
            return minYaw;
        }

        public double getMaxYaw() {
            return maxYaw;
        }

        public double getMinPitch() {
            return minPitch;
        }

        public double getMaxPitch() {
            return maxPitch;
        }

        public boolean isAutoLoad() {
            return autoLoad;
        }

        public double getAutoRotate() {
            return autoRotate;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
