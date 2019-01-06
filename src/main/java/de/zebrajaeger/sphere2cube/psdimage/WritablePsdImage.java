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

import de.zebrajaeger.sphere2cube.psdimage.linewriter.LineWriter;
import de.zebrajaeger.sphere2cube.psdimage.linewriter.RAWLineWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * a psd image that can be stored in a file
 * <p>
 * @author Lars Brandt on 13.05.2016.
 */
public class WritablePsdImage extends PsdImage {
    private File file;
    private DecoratedOutputStream outputStream;

    public WritablePsdImage(File file) {
        this.file = file;
    }

    public void open() throws FileNotFoundException {
        if (outputStream == null) {
            outputStream = new DecoratedOutputStream(new BufferedOutputStream(new FileOutputStream(file), 1024 * 1024));
        } else {
            throw new IllegalStateException("already opened");
        }
    }

    public void close() throws IOException {
        if (outputStream != null) {
            outputStream.getOutputStream().flush();
            outputStream.getOutputStream().close();
        } else {
            throw new IllegalStateException("already closed");
        }
    }

    public void writeHeader() throws IOException {
        outputStream.writeString(getId());
        outputStream.writeUnsignedShort(getVersion());
        outputStream.writeBytes(new byte[6]);
        outputStream.writeUnsignedShort(getChannels());
        outputStream.writeUnsignedInt(getHeight());
        outputStream.writeUnsignedInt(getWidth());
        outputStream.writeUnsignedShort(getDepth());
        outputStream.writeUnsignedShort(getColorMode());
        outputStream.writeUnsignedInt(0); // color data size = 0
        outputStream.writeUnsignedInt(0); // resourcesection

        if (isPSB()) {
            outputStream.writeLong(getLayerMaskSize());
        } else {
            outputStream.writeUnsignedInt(getLayerMaskSize());
        }

        outputStream.writeUnsignedShort(getCompression());
        if (getCompression() == 0) {
            lineWriter = new RAWLineWriter(outputStream.getOutputStream(), getWidth());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private LineWriter lineWriter;

    public LineWriter getLineWriter() {
        return lineWriter;
    }
}
