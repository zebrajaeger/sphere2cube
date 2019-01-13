package de.zebrajaeger.sphere2cube.psdimage.definitions;

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

/**
 * @author Lars Brandt on 16.05.2016.
 */
public enum Compression {
    RAW(0), RLE(1), ZIP_WITHOUT_PREDICTION(2), ZIP_WITH_PREDICTION(3), UNKNOWN(-1);

    int id;

    Compression(int id) {
        this.id = id;
    }

    static Compression get(int id) {
        switch (id) {
            case 0:
                return RAW;
            case 1:
                return RLE;
            case 2:
                return ZIP_WITHOUT_PREDICTION;
            case 3:
                return ZIP_WITH_PREDICTION;
            default:
                return UNKNOWN;
        }
    }

    int getId() {
        return id;
    }
}
