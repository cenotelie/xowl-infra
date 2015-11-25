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

package org.xowl.server;

import org.xowl.server.db.Controller;
import org.xowl.server.http.HTTPServer;
import org.xowl.server.xsp.XSPServer;
import org.xowl.utils.logging.BufferedLogger;
import org.xowl.utils.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The main program for this server
 *
 * @author Laurent Wouters
 */
public class Program {
    /**
     * The main entry point
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        Program instance = new Program(args);
        instance.run();
    }

    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * Marker whether the program should stop
     */
    private boolean shouldStop;

    /**
     * Initializes this program
     *
     * @param args The arguments
     */
    public Program(String[] args) {
        this.configuration = new ServerConfiguration(args.length >= 1 ? args[0] : null);
        this.shouldStop = !this.configuration.getRoot().exists();
    }

    /**
     * Runs this program
     */
    public void run() {
        if (shouldStop)
            return;

        // setup and start
        Controller controller;
        try {
            controller = new Controller(configuration) {

                @Override
                public void onRequestShutdown() {
                    shouldStop = true;
                }

                @Override
                public void onRequestRestart() {
                    shouldStop = true;
                }
            };
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
            return;
        }
        HTTPServer httpServer = new HTTPServer(configuration, controller);
        httpServer.start();
        XSPServer xspServer = new XSPServer(configuration, controller);

        while (!shouldStop) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException exception) {
                break;
            }
        }

        // cleanup
        controller.getLogger().info("Shutting down this server ...");
        try {
            xspServer.close();
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
        }
        try {
            httpServer.close();
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
        }
        try {
            controller.close();
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
        }
    }

    /**
     * Gets the content of the log
     *
     * @param logger The logger
     * @return The content of the log
     */
    public static String getLog(BufferedLogger logger) {
        StringBuilder builder = new StringBuilder();
        for (Object error : logger.getErrorMessages()) {
            builder.append(error.toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Hexadecimal characters
     */
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Encodes a series of bytes
     *
     * @param bytes The bytes to encode
     * @return The encoded text
     */
    public static String encode(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        int j = 0;
        for (int i = 0; i != bytes.length; i++) {
            chars[j++] = HEX[(bytes[i] & 0xF0) >>> 4];
            chars[j++] = HEX[bytes[i] & 0x0F];
        }
        return new String(chars);
    }

    /**
     * The size of buffers for loading content
     */
    private static final int BUFFER_SIZE = 1024;

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
     * Deletes a folder
     *
     * @param folder The folder to delete
     * @return true if the operation succeeded, false otherwise
     */
    public static boolean delete(File folder) {
        boolean success = false;
        File[] children = folder.listFiles();
        if (children == null)
            return false;
        for (int i = 0; i != children.length; i++) {
            if (children[i].isFile())
                success |= children[i].delete();
            else
                success |= delete(children[i]);
        }
        return success;
    }
}
