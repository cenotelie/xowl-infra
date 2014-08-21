/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/

package org.xowl.utils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Utility methods for handling files
 *
 * @author Laurent Wouters
 */
public class Files {
    private static final int bufferSize = 1024;

    private static final int[] bomUTF8 = new int[]{0xEF, 0xBB, 0xBF};
    private static final int[] bomUTF16LE = new int[]{0xFF, 0xFE};
    private static final int[] bomUTF16BE = new int[]{0xFE, 0xFF};
    private static final int[] bomUTF32LE = new int[]{0xFF, 0xFE, 0x00, 0x00};
    private static final int[] bomUTF32BE = new int[]{0x00, 0x00, 0xFE, 0xFF};

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
        if (charset.name().equals("UTF-8")) overflow = strip(stream, bomUTF8);
        else if (charset.name().equals("UTF-16LE")) overflow = strip(stream, bomUTF16LE);
        else if (charset.name().equals("UTF-16BE")) overflow = strip(stream, bomUTF16BE);
        else if (charset.name().equals("UTF-32LE")) overflow = strip(stream, bomUTF32LE);
        else if (charset.name().equals("UTF-32BE")) overflow = strip(stream, bomUTF32BE);
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
        char[] buf = new char[bufferSize];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            fileData.append(buf, 0, numRead);
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * Detects the encoding of the specified file
     *
     * @param file A file
     * @return The file's encoding
     * @throws IOException when reading fails
     */
    public static Charset detectEncoding(String file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        try {
            return detectEncoding(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Detects the encoding of the specified stream
     *
     * @param stream A stream
     * @return The stream's encoding
     * @throws IOException when reading fails
     */
    public static Charset detectEncoding(InputStream stream) throws IOException {
        int b0 = stream.read();
        int b1 = stream.read();
        if (b0 == -1 || b1 == -1) {
            stream.close();
            return Charset.forName("UTF-8");
        }
        if (b0 == bomUTF16BE[0] && b1 == bomUTF16BE[1]) {
            stream.close();
            return Charset.forName("UTF-16BE");
        }
        int b2 = stream.read();
        if (b2 == -1) {
            stream.close();
            if (b0 == bomUTF16LE[0] && b1 == bomUTF16LE[1]) return Charset.forName("UTF-16LE");
            return Charset.forName("UTF-8");
        }
        if (b0 == bomUTF8[0] && b1 == bomUTF8[1] && b2 == bomUTF8[2]) {
            stream.close();
            return Charset.forName("UTF-8");
        }
        int b3 = stream.read();
        if (b3 == -1) {
            stream.close();
            if (b0 == bomUTF16LE[0] && b1 == bomUTF16LE[1]) return Charset.forName("UTF-16LE");
            return Charset.forName("UTF-8");
        }

        if (b0 == bomUTF32BE[0] && b1 == bomUTF32BE[1] && b2 == bomUTF32BE[2] && b3 == bomUTF32BE[3])
            return Charset.forName("UTF-32BE");
        if (b0 == bomUTF32LE[0] && b1 == bomUTF32LE[1] && b2 == bomUTF32LE[2] && b3 == bomUTF32LE[3])
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
}
