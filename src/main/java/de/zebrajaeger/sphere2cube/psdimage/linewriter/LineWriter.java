package de.zebrajaeger.sphere2cube.psdimage.linewriter;

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
import java.nio.ByteBuffer;

/**
 * @author Lars Brandt on 07.05.2016.
 */
public abstract class LineWriter {
    private OutputStream outputStream;
    private int lineSize;

    public LineWriter(OutputStream outputStream, int lineSize) {
        this.outputStream = outputStream;
        this.lineSize = lineSize;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public int getLineSize() {
        return lineSize;
    }

    public abstract void writeLine(ByteBuffer buffer) throws IOException;
}
