package de.zebrajaeger.sphere2cube.psdimage.linereader;

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

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Lars Brandt on 07.05.2016.
 */
public class RawLineReader extends LineReader {

    public RawLineReader(ImageInputStream inputStream, int lineSize) {
        super(inputStream, lineSize);
    }

    @Override
    public DecodeResult readLine(ByteBuffer buffer) throws IOException {
        if (buffer.capacity() != getLineSize()) {
            throw new IllegalArgumentException("buffersize (" + buffer.capacity() + ") must match linesize (" + getLineSize() + ")");
        }

        buffer.clear();
        int total = getInputStream().read(buffer.array());
        return new DecodeResult(total, total);
    }
}
