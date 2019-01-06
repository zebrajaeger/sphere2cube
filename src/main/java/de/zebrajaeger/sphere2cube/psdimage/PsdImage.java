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

import java.util.Optional;

/**
 * The basic psd data structure
 * Technical description:  https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/
 *
 * @author Lars Brandt on 07.05.2016.
 */
public class PsdImage {

    private String id = "8BPS";
    private int version = 2;
    private int channels = 4;
    private long height;
    private long width;
    private int depth = 8;
    private int colorMode = 3;
    private long colorDataSize = 0;
    private long layerMaskSize = 0;
    private int compression = 0;
    private ResourceSection resources = null;

    public PsdImage() {
    }

    public void readValuesFrom(PsdImage o) {
        id = o.id;
        version = o.version;
        channels = o.channels;
        height = o.height;
        width = o.width;
        depth = o.depth;
        colorMode = o.colorMode;
        colorDataSize = o.colorDataSize;
        layerMaskSize = o.layerMaskSize;
        compression = o.compression;
        resources = null;
    }

    public int getWidth() {
        return (int) width;
    }

    public int getHeight() {
        return (int) height;
    }

    public boolean isPSB() {
        return version == 2;
    }

    public Optional<GPanoData> getGPanoData() {
        return Optional.ofNullable(resources.getGPanoData());
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public int getChannels() {
        return channels;
    }

    public int getDepth() {
        return depth;
    }

    public int getColorMode() {
        return colorMode;
    }

    public long getColorDataSize() {
        return colorDataSize;
    }

    public long getLayerMaskSize() {
        return layerMaskSize;
    }

    public int getCompression() {
        return compression;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }

    public void setColorDataSize(long colorDataSize) {
        this.colorDataSize = colorDataSize;
    }

    public void setLayerMaskSize(long layerMaskSize) {
        this.layerMaskSize = layerMaskSize;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public ResourceSection getResources() {
        return resources;
    }

    public void setResources(ResourceSection resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "PsdImage{" +
                "id='" + id + '\'' +
                ", version=" + version +
                ", channels=" + channels +
                ", height=" + height +
                ", width=" + width +
                ", depth=" + depth +
                ", colorMode=" + colorMode +
                ", colorDataSize=" + colorDataSize +
                ", layerMaskSize=" + layerMaskSize +
                ", compression=" + compression +
                '}';
    }
}
