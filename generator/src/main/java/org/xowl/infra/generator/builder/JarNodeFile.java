/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.xowl.infra.generator.builder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * Represents a jar node for a file
 *
 * @author Laurent Wouters
 */
class JarNodeFile implements JarNode {
    /**
     * the size of the writing buffer
     */
    private static final int WRITE_BUFFER_SIZE = 1024;

    /**
     * The full path for this entry
     */
    private String path;
    /**
     * The file at this entry
     */
    private File file;

    /**
     * Initializes this entry
     *
     * @param path The full path
     * @param file The file for the entry
     */
    public JarNodeFile(String path, File file) {
        this.path = path;
        this.file = file;
    }

    @Override
    public void add(String path, String[] parts, int index, File file) {
    }

    @Override
    public void createEntry(JarOutputStream stream) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        JarEntry fileEntry = new JarEntry(path);
        stream.putNextEntry(fileEntry);
        byte[] data = new byte[WRITE_BUFFER_SIZE];
        int byteCount;
        while ((byteCount = bufferedInputStream.read(data, 0, WRITE_BUFFER_SIZE)) > -1) {
            stream.write(data, 0, byteCount);
        }
        bufferedInputStream.close();
        fileInputStream.close();
    }
}
