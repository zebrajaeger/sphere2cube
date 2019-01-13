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

import java.io.IOException;
import java.io.OutputStream;

/**
 * this decorates an output stream with some helper methods to write different data types
 * <p>
 * @author Lars Brandt on 13.05.2016.
 */
public class DecoratedOutputStream {
    private OutputStream outputStream;

    public DecoratedOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeString(String value) throws IOException {
        writeBytes(value.getBytes());
    }

    public void writeByte(int value) throws IOException {
        outputStream.write(value);
    }

    public void writeUnsignedShort(int value) throws IOException {
        outputStream.write((value >> 8) & 0xff);
        outputStream.write(value & 0xff);
    }

    public void writeUnsignedInt(long value) throws IOException {
        outputStream.write((int) ((value >> 24) & 0xff));
        outputStream.write((int) ((value >> 16) & 0xff));
        outputStream.write((int) ((value >> 8) & 0xff));
        outputStream.write((int) (value & 0xff));
    }

    public void writeLong(long value) throws IOException {
        outputStream.write((int) ((value >> 56) & 0xff));
        outputStream.write((int) ((value >> 48) & 0xff));
        outputStream.write((int) ((value >> 40) & 0xff));
        outputStream.write((int) ((value >> 32) & 0xff));
        outputStream.write((int) ((value >> 24) & 0xff));
        outputStream.write((int) ((value >> 16) & 0xff));
        outputStream.write((int) ((value >> 8) & 0xff));
        outputStream.write((int) (value & 0xff));
    }

    public void writeBytes(byte[] value) throws IOException {
        outputStream.write(value);
    }

    public void writeBytes(byte[] value, int offset, int length) throws IOException {
        outputStream.write(value, offset, length);
    }

    public void writeUnsignedShorts(int[] value) throws IOException {
        for (int i : value) {
            writeUnsignedInt(i);
        }
    }

    public void writeUnsignedInts(long[] value) throws IOException {
        for (long l : value) {
            writeUnsignedInt(l);
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
