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

import de.zebrajaeger.sphere2cube.psdimage.linereader.LineReader;
import de.zebrajaeger.sphere2cube.psdimage.linereader.RLELineReader;
import de.zebrajaeger.sphere2cube.psdimage.linereader.RawLineReader;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;

/**
 * A psd image that can be ridden from a file
 * <p>
 *
 * @author Lars Brandt on 13.05.2016.
 */
public class ReadablePsdImage extends PsdImage {
    private File file;
    private FileImageInputStream inputStream = null;
    private LineReader lineReader;
    private long[] compressionLineSizes;

    public static ReadablePsdImage headerOnly(File file) throws IOException {
        ReadablePsdImage sourceImage = new ReadablePsdImage(file);
        sourceImage.open();
        sourceImage.readHeader();
        sourceImage.close();
        return sourceImage;
    }

    public static ReadablePsdImage of(File file) throws IOException {
        ReadablePsdImage sourceImage = new ReadablePsdImage(file);
        sourceImage.open();
        sourceImage.readHeader();
        return sourceImage;
    }

    private ReadablePsdImage(File file) {
        this.file = file;
    }

    public void open() throws IOException {
        if (inputStream == null) {
            inputStream = new FileImageInputStream(file);
        } else {
            throw new IllegalStateException("already opened");
        }
    }

    public void close() throws IOException {
        if (inputStream == null) {
            throw new IllegalStateException("already closed");
        } else {
            try {
                inputStream.close();
            } finally {
                inputStream = null;
            }
        }
    }

    public long[] getCompressionLineSizes() {
        return compressionLineSizes;
    }

    public void readHeader() throws IOException {
        FileImageInputStream is = getInputStream();

        // header
        setId(PsdUtils.readCString(is, 4));
        setVersion(is.readShort());
        is.skipBytes(6);
        setChannels(is.readShort());
        setHeight(is.readInt());
        setWidth(is.readInt());
        setDepth(is.readShort());
        setColorMode(is.readShort());

        // color data
        setColorDataSize(is.readInt());
        is.skipBytes(getColorDataSize());

        // resource section(s)
        int size = is.readInt();
        byte[] resourceSection = new byte[size];
        is.read(resourceSection);
        setResources(new ResourceSection(resourceSection));

        //setResources(new ResourceSection());
        //getResources().read(is);

        // layer mask data
        if (isPSB()) {
            setLayerMaskSize(is.readLong());
        } else {
            setLayerMaskSize(is.readInt());
        }
        is.skipBytes(getLayerMaskSize());

        // imageDataStart
        setCompression(is.readShort());

        if (getCompression() == 0) {
            lineReader = new RawLineReader(getInputStream(), getWidth());
        } else if (getCompression() == 1) {
            compressionLineSizes = PsdUtils.readInts(is, (getChannels() * getHeight()));
            lineReader = new RLELineReader(getInputStream(), getWidth());
        } else {
            lineReader = null;
        }
    }

    public File getFile() {
        return file;
    }

    public FileImageInputStream getInputStream() {
        return inputStream;
    }

    public LineReader getLineReader() {
        return lineReader;
    }
}
