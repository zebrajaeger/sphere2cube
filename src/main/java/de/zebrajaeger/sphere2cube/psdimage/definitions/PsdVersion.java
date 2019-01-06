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
public enum PsdVersion {
    PSD(1), PSB(2), UNKNOWN(-1);

    int id;

    PsdVersion(int id) {
        this.id = id;
    }

    static PsdVersion get(int id) {
        switch (id) {
            case 1:
                return PSD;
            case 2:
                return PSB;
            default:
                return UNKNOWN;
        }
    }

    int getId() {
        return id;
    }
}
