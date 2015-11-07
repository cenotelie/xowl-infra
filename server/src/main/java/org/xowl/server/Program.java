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
import org.xowl.utils.BufferedLogger;

import java.io.IOException;

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
        this.configuration = new ServerConfiguration(args);
        this.shouldStop = false;
    }

    /**
     * Runs this program
     */
    public void run() {
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
            exception.printStackTrace();
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
            exception.printStackTrace();
        }
        try {
            httpServer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            controller.close();
        } catch (IOException exception) {
            exception.printStackTrace();
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
}
