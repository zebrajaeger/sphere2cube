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
public enum ColorModel {
    BITMAP_MONOCHROME(0),
    GRAY_SCALE(1),
    INDEXED_COLOR(2),
    RGB_COLOR(3),
    CYMK_COLOR(4),
    MULTICHANNEL_COLOR(5),
    DUOTONE(8),
    LAB_COLOR(9),
    UNKNOWN(-1);

    int id;

    ColorModel(int id) {
        this.id = id;
    }

    static ColorModel get(int id) {
        switch (id) {
            case 0:
                return BITMAP_MONOCHROME;
            case 1:
                return GRAY_SCALE;
            case 2:
                return INDEXED_COLOR;
            case 3:
                return RGB_COLOR;
            case 4:
                return CYMK_COLOR;
            case 5:
                return MULTICHANNEL_COLOR;
            case 6:
                return DUOTONE;
            case 7:
                return LAB_COLOR;
            default:
                return UNKNOWN;
        }
    }

    int getId() {
        return id;
    }

}
