package de.zebrajaeger.sphere2cube.indexhtml;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class IndexHtmGeneratorKrPano extends IndexHtmlGenerator {

    public static final String TEMPLATE_NAME = "krpano.index.html.twig";

    public static IndexHtmGeneratorKrPano of() {
        return new IndexHtmGeneratorKrPano();
    }

    private IndexHtmGeneratorKrPano() {
    }

    public String generate(IndexHtml indexHtml) throws IOException {
        return generate(TEMPLATE_NAME, indexHtml);
    }

    /**
     * @author Lars Brandt, Silpion IT Solutions GmbH
     */
    public static class IndexHtml {
        private String title;
        private String panoJs = "krpano.js";
        private String panoXml = "pano.xml";

        public IndexHtml(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String getPanoJs() {
            return panoJs;
        }

        public String getPanoXml() {
            return panoXml;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
