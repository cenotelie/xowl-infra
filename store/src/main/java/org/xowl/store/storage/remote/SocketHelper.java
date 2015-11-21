/*******************************************************************************
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
 ******************************************************************************/

package org.xowl.store.storage.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Utility APIs for reading and writing messages over a network socket
 *
 * @author Laurent Wouters
 */
public class SocketHelper {
    /**
     * The charset to use for strings
     */
    public static final String CHARSET = "UTF-8";

    /**
     * Writes the specified message over the socket
     *
     * @param socket  The socket to write to
     * @param message The message to send
     * @throws IOException When an IO error occurs
     */
    public static void write(Socket socket, String message) throws IOException {
        byte[] content = message.getBytes(Charset.forName(CHARSET));
        byte byte0 = (byte) (0x000000FF & content.length);
        byte byte1 = (byte) ((0x0000FF00 & content.length) >>> 8);
        byte byte2 = (byte) ((0x00FF0000 & content.length) >>> 16);
        byte byte3 = (byte) ((0xFF000000 & content.length) >>> 24);
        OutputStream stream = socket.getOutputStream();
        stream.write(byte0);
        stream.write(byte1);
        stream.write(byte2);
        stream.write(byte3);
        stream.write(content);
        stream.flush();
    }

    /**
     * Reads a message from the socket
     *
     * @param socket The socket to read from
     * @return The message, or null of the socket was closed
     * @throws IOException When an IO error occurs
     */
    public static String read(Socket socket) throws IOException {
        try {
            InputStream stream = socket.getInputStream();
            int i0 = stream.read();
            if (i0 < 0)
                return null;
            int i1 = stream.read();
            if (i1 < 0)
                return null;
            int i2 = stream.read();
            if (i2 < 0)
                return null;
            int i3 = stream.read();
            if (i3 < 0)
                return null;
            int length = (((((byte) i3) & 0xff) << 24) |
                    ((((byte) i2) & 0xff) << 16) |
                    ((((byte) i1) & 0xff) << 8) |
                    ((((byte) i0) & 0xff)));
            byte[] content = new byte[length];
            int index = 0;
            while (index < length) {
                int read = stream.read(content, index, length - index);
                if (read == 0)
                    return null;
                index += read;
            }
            return new String(content, Charset.forName(CHARSET));
        } catch (SocketException exception) {
            return null;
        }
    }
}
