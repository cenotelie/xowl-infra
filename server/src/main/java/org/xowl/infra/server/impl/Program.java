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

package org.xowl.infra.server.impl;

import org.xowl.infra.server.standalone.HTTPServer;
import org.xowl.infra.server.base.ServerConfiguration;
import org.xowl.infra.server.standalone.ServerController;
import org.xowl.infra.utils.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
     * The signalling object for the main thread
     */
    private final CountDownLatch signal;
    /**
     * Whether the server should stop
     */
    private boolean shouldStop;
    /**
     * The top controller for this application
     */
    private ServerController controller;
    /**
     * The HTTP server
     */
    private HTTPServer httpServer;

    /**
     * Initializes this program
     *
     * @param args The arguments
     */
    public Program(String[] args) {
        this.configuration = new ServerConfiguration(args.length >= 1 ? args[0] : null);
        this.signal = new CountDownLatch(1);
        this.shouldStop = false;
    }

    /**
     * Runs this program
     */
    public void run() {
        if (!init())
            return;

        // register hook for shutdown events
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shouldStop = true;
                signal.countDown();
                onClose();
            }
        }, Program.class.getCanonicalName() + ".shutdown"));

        while (!shouldStop) {
            try {
                signal.await();
            } catch (InterruptedException exception) {
                break;
            }
        }

        onClose();
    }

    /**
     * Initializes the application
     *
     * @return Whether the operation succeeded
     */
    private boolean init() {
        if (!configuration.getDatabasesFolder().exists()) {
            System.err.println("The repository location does not exist: " + configuration.getDatabasesFolder().getAbsolutePath());
            System.exit(1);
            return false;
        }
        try {
            controller = new ServerController(configuration) {

                @Override
                public void onRequestShutdown() {
                    shouldStop = true;
                    signal.countDown();
                }

                @Override
                public void onRequestRestart() {
                    shouldStop = true;
                    signal.countDown();
                }
            };
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
            return false;
        }
        httpServer = new HTTPServer(configuration, controller);
        httpServer.start();
        return true;
    }

    /**
     * When this application is closing
     */
    private synchronized void onClose() {
        if (controller == null)
            return;
        controller.getLogger().info("Shutting down this server ...");
        try {
            httpServer.close();
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
        try {
            controller.close();
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
        httpServer = null;
        controller = null;
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
