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

package org.xowl.infra.server.standalone;

import org.xowl.infra.server.impl.ServerConfiguration;
import org.xowl.infra.server.impl.ServerController;
import org.xowl.infra.utils.logging.ConsoleLogger;
import org.xowl.infra.utils.logging.DispatchLogger;
import org.xowl.infra.utils.logging.FileLogger;
import org.xowl.infra.utils.logging.Logging;

import java.io.File;
import java.io.IOException;
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
        Logging.setDefault(new DispatchLogger(new FileLogger(new File(configuration.getStartupFolder(), "server.log")), new ConsoleLogger()));
        try {
            controller = new ServerController(Logging.getDefault(), configuration) {

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
        } catch (Exception exception) {
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
        Logging.getDefault().info("Shutting down this server ...");
        try {
            httpServer.close();
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
        }
        try {
            controller.close();
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
        }
        httpServer = null;
        controller = null;
    }

}
