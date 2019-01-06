package de.zebrajaeger.sphere2cube.psdimage.autopano;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import de.zebrajaeger.sphere2cube.psdimage.XmlUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * A wrapper for the XMP-Data that Autopano normally puts into a rendered panoramic image
 *
 * @author Lars Brandt
 */
public class GPanoData {

    private static Logger LOG = LoggerFactory.getLogger(GPanoData.class);

    private Boolean usePanoramaViewer = null;
    private String projectionType = null;
    private Long croppedAreaLeftPixels = null;
    private String stitchingSoftware = null;
    private Long croppedAreaImageWidthPixels = null;
    private Long sourcePhotosCount = null;
    private Long croppedAreaTopPixels = null;
    private Long croppedAreaImageHeightPixels = null;
    private Long fullPanoWidthPixels = null;
    private Long fullPanoHeightPixels = null;

    /**
     * Changes the bytes into ascii string and pares it as a XML-document
     *
     * @param data the data to parse
     */
    protected void parse(byte[] data) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.parse(new ByteArrayInputStream(data));

        final Node root = XmlUtils.find(document, "x:xmpmeta");
        if (root == null) {
            return;
        }

        final Node rdf = XmlUtils.find(root, "rdf:RDF");
        if (rdf == null) {
            return;
        }

        final Node desc = XmlUtils.findWithAttrubute(rdf, "rdf:Description", "xmlns:GPano");
        if (rdf == desc) {
            return;
        }

        final HashMap<String, String> a = XmlUtils.getAttributes(desc, "GPano:", true);

        usePanoramaViewer = parseBoolean(a, "UsePanoramaViewer");
        projectionType = parseString(a, "ProjectionType");
        croppedAreaLeftPixels = parseLong(a, "CroppedAreaLeftPixels");
        stitchingSoftware = parseString(a, "StitchingSoftware");
        croppedAreaImageWidthPixels = parseLong(a, "CroppedAreaImageWidthPixels");
        sourcePhotosCount = parseLong(a, "SourcePhotosCount");
        croppedAreaTopPixels = parseLong(a, "CroppedAreaTopPixels");
        croppedAreaImageHeightPixels = parseLong(a, "CroppedAreaImageHeightPixels");
        fullPanoWidthPixels = parseLong(a, "FullPanoWidthPixels");
        fullPanoHeightPixels = parseLong(a, "FullPanoHeightPixels");
    }

    protected static String parseString(HashMap<String, String> map, String name) {
        return map.get(name);
    }

    protected static Long parseLong(HashMap<String, String> map, String name) {
        final String val = map.get(name);
        if (val == null) {
            return null;
        }
        try {
            return Long.parseLong(val);
        } catch (final NumberFormatException e) {
            final String msg = String.format("can not convert parameter '%s' with value '%s' to long", name, val);
            LOG.error(msg);
        }
        return null;
    }

    protected static Boolean parseBoolean(HashMap<String, String> map, String name) {
        final String val = map.get(name);
        if (val == null) {
            return null;
        }
        try {
            return Boolean.parseBoolean(val);
        } catch (final NumberFormatException e) {
            final String msg = String.format("can not convert parameter '%s' with value '%s' to long", name, val);
            LOG.error(msg);
        }
        return null;
    }

    public Boolean getUsePanoramaViewer() {
        return this.usePanoramaViewer;
    }

    public String getProjectionType() {
        return this.projectionType;
    }

    public Long getCroppedAreaLeftPixels() {
        return this.croppedAreaLeftPixels;
    }

    public String getStitchingSoftware() {
        return this.stitchingSoftware;
    }

    public Long getCroppedAreaImageWidthPixels() {
        return this.croppedAreaImageWidthPixels;
    }

    public Long getSourcePhotosCount() {
        return this.sourcePhotosCount;
    }

    public Long getCroppedAreaTopPixels() {
        return this.croppedAreaTopPixels;
    }

    public Long getCroppedAreaImageHeightPixels() {
        return this.croppedAreaImageHeightPixels;
    }

    public Long getFullPanoWidthPixels() {
        return this.fullPanoWidthPixels;
    }

    public Long getFullPanoHeightPixels() {
        return this.fullPanoHeightPixels;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public static class Builder {

        public static GPanoData buildFrombytes(byte[] content) throws ParserConfigurationException, SAXException,
                IOException {
            final GPanoData res = new GPanoData();
            res.parse(content);
            return res;
        }
    }
}
