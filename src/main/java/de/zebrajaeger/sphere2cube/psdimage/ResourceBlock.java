package de.zebrajaeger.sphere2cube.psdimage;

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

import de.zebrajaeger.sphere2cube.psdimage.autopano.GPanoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * the resource block with panoramix data from autopano
 * <p>
 * @author Lars Brandt on 08.05.2016.
 */
public class ResourceBlock {
    private static Logger LOG = LoggerFactory.getLogger(ResourceBlock.class);

    private transient byte[] signature = {'8', 'B', 'I', 'M'};
    private int uid;
    private PascalString name;
    private long size;
    private transient byte[] data;
    private Object decodedData = null;

    public long read(ImageInputStream is) throws IOException {
        long res = 0;

        this.signature = new byte[4];
        is.read(this.signature);
        res += 4;

        this.uid = is.readShort();
        res += 2;

        name = new PascalString();
        res += name.read(is);

        size = is.readInt();
        res += 4;

        data = new byte[(int) size];
        is.read(data);
        res += data.length;

        decodeData();
        return res;
    }

    protected void decodeData() {
        if (uid == 1058) {
            // EXIF
            // decodedData = new String(data, StandardCharsets.US_ASCII);
        } else if (uid == 1060) {
            // XMP see http://www.w3.org/RDF/Validator/
            decodedData = new String(data, StandardCharsets.US_ASCII);
            try {
                decodedData = GPanoData.Builder.buildFrombytes(data);
            } catch (final SAXException | IOException | ParserConfigurationException e) {
                LOG.error("could not parse XMP-FileUtils Data", e);
            }
        }
    }

    public Object getDecodedData() {
        return decodedData;
    }
}
