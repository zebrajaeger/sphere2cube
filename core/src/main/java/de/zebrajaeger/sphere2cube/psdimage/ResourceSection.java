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

import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import de.zebrajaeger.sphere2cube.psdimage.autopano.GPanoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;

/**
 * The pds resource section, divided in resource blocks
 *
 * @author Lars Brandt on 08.05.2016.
 */
public class ResourceSection {
    private final Logger LOG = LoggerFactory.getLogger(ResourceSection.class);

    private final LinkedList<ResourceBlock> blocks = new LinkedList<>();

    public ResourceSection(byte[] data) {
        ByteArrayImageInputStream is = new ByteArrayImageInputStream(data);

        try {
            // minimum header size at least 10 bytes
            while (is.length() > (is.getStreamPosition()+10)) {
                LOG.debug("Read resourceblock. Available: " + (is.length() > is.getStreamPosition()));
                final ResourceBlock irb = new ResourceBlock();
                irb.read(is);
                blocks.add(irb);
            }
        } catch (IOException e) {
            LOG.error("Could not read ResourceBlock", e);
        }
    }

    public GPanoData getGPanoData() {
        for (final ResourceBlock b : blocks) {
            if (b.getDecodedData() instanceof GPanoData) {
                return (GPanoData) b.getDecodedData();
            }
        }
        return null;
    }
}
