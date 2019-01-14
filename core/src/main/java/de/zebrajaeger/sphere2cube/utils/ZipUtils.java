package de.zebrajaeger.sphere2cube.utils;

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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Lars Brandt on 22.05.2016.
 */
public class ZipUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ZipUtils.class);

    public static void compressDirectory(File pSource, File zipFile, String zipBaseDir) throws IOException {
        try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            compressDirectory(pSource, zipBaseDir, zipStream);
        }
    }

    public static void compressDirectory(File pSource, File zipFile) throws IOException {
        try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            compressDirectory(pSource, "", zipStream);
        }
    }

    private static void compressDirectory(File pSource, String zipDir, ZipOutputStream zipStream) throws IOException {
        for (File f : pSource.listFiles()) {
            String zipName = (zipDir.isEmpty()) ? f.getName() : zipDir + File.separator + f.getName();
            if (f.isFile()) {
                LOG.debug("Add File: '{}' as '{}'", f.getAbsolutePath(), zipName);
                ZipEntry entry = new ZipEntry(zipName);
                zipStream.putNextEntry(entry);

                try (FileInputStream in = new FileInputStream(f)) {
                    IOUtils.copy(in, zipStream);
                }
            } else if (f.isDirectory()) {
                LOG.debug("Add Directory: '{}'", f.getAbsolutePath(), zipName);
                compressDirectory(f, zipName, zipStream);
            }
        }
    }
}
