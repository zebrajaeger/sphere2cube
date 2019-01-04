package de.zebrajaeger.sphere2cube.indexhtml;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class IndexHtml {
    private String title;
    private String panoJs = "pano.js";
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
