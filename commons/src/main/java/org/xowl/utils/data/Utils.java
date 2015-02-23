/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
package org.xowl.utils.data;

import java.io.DataInput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Utility class for IO
 *
 * @author Laurent Wouters
 */
class Utils {
    /**
     * The encoding to use for binary serialization
     */
    private static final String STRING_ENCODING = "UTF8";

    /**
     * Reads the specified number of bytes
     *
     * @param input  The input
     * @param length The number of bytes to read
     * @return The read bytes
     * @throws IOException on reading
     */
    public static byte[] readBytes(DataInput input, int length) throws IOException {
        byte[] buffer = new byte[length];
        for (int i = 0; i != length; i++)
            buffer[i] = input.readByte();
        return buffer;
    }

    /**
     * Reads a string from the input
     *
     * @param input The input
     * @return the read string
     * @throws IOException on reading
     */
    public static String readString(DataInput input) throws IOException {
        int length = input.readInt();
        byte[] bytes = readBytes(input, length);
        return new String(bytes, STRING_ENCODING);
    }

    /**
     * Gets the serialization of the specified string
     *
     * @param value The string to serialize
     * @return The serialization
     */
    public static byte[] getStringBytes(String value) {
        try {
            return value.getBytes(STRING_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
