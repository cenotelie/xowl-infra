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

package org.xowl.infra.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for handling files
 *
 * @author Laurent Wouters
 */
public class Files {
    /**
     * The line separator for all xOWL IO operations
     */
    public static final String LINE_SEPARATOR = "\n";
    /**
     * The charset to use for xOWL
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");
    /**
     * The size of buffers for loading content
     */
    private static final int BUFFER_SIZE = 1024;
    /**
     * The byte-order mark for UTF-8
     */
    private static final int[] BOM_UTF8 = new int[]{0xEF, 0xBB, 0xBF};
    /**
     * The byte-order mark for UTF-16 little endian
     */
    private static final int[] BOM_UTF16_LE = new int[]{0xFF, 0xFE};
    /**
     * The byte-order mark for UTF-16 big endian
     */
    private static final int[] BOM_UTF16_BE = new int[]{0xFE, 0xFF};
    /**
     * The byte-order mark for UTF-32 little endian
     */
    private static final int[] BOM_UTF32_LE = new int[]{0xFF, 0xFE, 0x00, 0x00};
    /**
     * The byte-order mark for UTF-32 big endian
     */
    private static final int[] BOM_UTF32_BE = new int[]{0x00, 0x00, 0xFE, 0xFF};


    /**
     * Gets a reader for the specified file
     *
     * @param file A file
     * @return A reader to read the file with the detected encoding
     * @throws IOException when reading fails
     */
    public static Reader getReader(String file) throws IOException {
        Charset charset = detectEncoding(file);
        InputStream stream = new java.io.FileInputStream(file);
        int[] overflow = null;
        if (charset.name().equals("UTF-8")) overflow = strip(stream, BOM_UTF8);
        else if (charset.name().equals("UTF-16LE")) overflow = strip(stream, BOM_UTF16_LE);
        else if (charset.name().equals("UTF-16BE")) overflow = strip(stream, BOM_UTF16_BE);
        else if (charset.name().equals("UTF-32LE")) overflow = strip(stream, BOM_UTF32_LE);
        else if (charset.name().equals("UTF-32BE")) overflow = strip(stream, BOM_UTF32_BE);
        if (overflow != null)
            stream = new CompositeInputStream(overflow, stream);
        return new InputStreamReader(stream, charset);
    }

    /**
     * Gets a writer for the specified file
     *
     * @param file A file
     * @return A writer for the file
     * @throws IOException when writing fails
     */
    public static Writer getWriter(String file) throws IOException {
        return new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
    }

    /**
     * Reads the complete content of the specified stream with the specified charset
     *
     * @param stream  A stream
     * @param charset The charset to use
     * @return The stream's content
     * @throws IOException when reading fails
     */
    public static String read(InputStream stream, Charset charset) throws IOException {
        return read(new InputStreamReader(stream, charset));
    }

    /**
     * Reads the complete content of the specified reader
     *
     * @param reader A reader
     * @return The reader's content
     * @throws IOException when reading fails
     */
    public static String read(Reader reader) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        char[] buf = new char[BUFFER_SIZE];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            fileData.append(buf, 0, numRead);
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * Loads all the content from the specified input stream
     *
     * @param stream The stream to load from
     * @return The loaded content
     * @throws IOException When the reading the stream fails
     */
    public static byte[] load(InputStream stream) throws IOException {
        List<byte[]> content = new ArrayList<>();
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = 0;
        int read;
        int size = 0;
        while (true) {
            read = stream.read(buffer, length, BUFFER_SIZE - length);
            if (read == -1) {
                if (length != 0) {
                    content.add(buffer);
                    size += length;
                }
                break;
            }
            length += read;
            if (length == BUFFER_SIZE) {
                content.add(buffer);
                size += BUFFER_SIZE;
                buffer = new byte[BUFFER_SIZE];
                length = 0;
            }
        }

        byte[] result = new byte[size];
        int current = 0;
        for (int i = 0; i != content.size(); i++) {
            if (i == content.size() - 1) {
                // the last buffer
                System.arraycopy(content.get(i), 0, result, current, size - current);
            } else {
                System.arraycopy(content.get(i), 0, result, current, BUFFER_SIZE);
                current += BUFFER_SIZE;
            }
        }
        return result;
    }

    /**
     * Detects the encoding of the specified file
     *
     * @param file A file
     * @return The file's encoding
     * @throws IOException when reading fails
     */
    public static Charset detectEncoding(String file) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return detectEncoding(stream);
        }
    }

    /**
     * Detects the encoding of the specified stream
     *
     * @param stream A stream
     * @return The stream's encoding
     * @throws IOException when reading fails
     */
    private static Charset detectEncoding(InputStream stream) throws IOException {
        int b0 = stream.read();
        int b1 = stream.read();
        if (b0 == -1 || b1 == -1) {
            stream.close();
            return Charset.forName("UTF-8");
        }
        if (b0 == BOM_UTF16_BE[0] && b1 == BOM_UTF16_BE[1]) {
            stream.close();
            return Charset.forName("UTF-16BE");
        }
        int b2 = stream.read();
        if (b2 == -1) {
            stream.close();
            if (b0 == BOM_UTF16_LE[0] && b1 == BOM_UTF16_LE[1]) return Charset.forName("UTF-16LE");
            return Charset.forName("UTF-8");
        }
        if (b0 == BOM_UTF8[0] && b1 == BOM_UTF8[1] && b2 == BOM_UTF8[2]) {
            stream.close();
            return Charset.forName("UTF-8");
        }
        int b3 = stream.read();
        if (b3 == -1) {
            stream.close();
            if (b0 == BOM_UTF16_LE[0] && b1 == BOM_UTF16_LE[1]) return Charset.forName("UTF-16LE");
            return Charset.forName("UTF-8");
        }

        if (b0 == BOM_UTF32_BE[0] && b1 == BOM_UTF32_BE[1] && b2 == BOM_UTF32_BE[2] && b3 == BOM_UTF32_BE[3])
            return Charset.forName("UTF-32BE");
        if (b0 == BOM_UTF32_LE[0] && b1 == BOM_UTF32_LE[1] && b2 == BOM_UTF32_LE[2] && b3 == BOM_UTF32_LE[3])
            return Charset.forName("UTF-32LE");
        return Charset.forName("UTF-8");
    }

    /**
     * Strips the specified stream from the specified header
     *
     * @param stream A stream
     * @param bom    The heading BOM
     * @return The over-read bytes
     * @throws IOException when reading fails
     */
    private static int[] strip(InputStream stream, int[] bom) throws IOException {
        int[] overflow = new int[bom.length];
        for (int i = 0; i != bom.length; i++) {
            int value = stream.read();
            overflow[i] = value;
            if (value != bom[i])
                return overflow;
        }
        return null;
    }

    /**
     * Deletes a folder
     *
     * @param folder The folder to delete
     * @return true if the operation succeeded, false otherwise
     */
    public static boolean deleteFolder(File folder) {
        boolean success = false;
        File[] children = folder.listFiles();
        if (children == null)
            return false;
        for (int i = 0; i != children.length; i++) {
            if (children[i].isFile())
                success |= children[i].delete();
            else
                success |= deleteFolder(children[i]);
        }
        return success;
    }
}
