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

import fr.cenotelie.commons.utils.logging.ConsoleLogger;
import fr.cenotelie.commons.utils.logging.DispatchLogger;
import fr.cenotelie.commons.utils.logging.FileLogger;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.server.ServerConfiguration;
import org.xowl.infra.server.impl.ControllerServer;

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
     * Exit code on a nominal shutdown
     */
    private static final int EXIT_CODE_NORMAL = 0;
    /**
     * Exit code on a restart request
     */
    private static final int EXIT_CODE_RESTART = 5;

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
     * Whether a server restart was requested
     */
    private boolean shouldRestart;
    /**
     * The top controller for this application
     */
    private ControllerServer controller;
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
        this.shouldRestart = false;
    }

    /**
     * Runs this program
     */
    public void run() {
        Logging.setDefault(new DispatchLogger(new FileLogger(new File(configuration.getStartupFolder(), "server.log")), new ConsoleLogger()));
        if (!init())
            return;

        // register hook for shutdown events
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shouldStop = true;
            signal.countDown();
            onClose();
        }, Program.class.getCanonicalName() + ".shutdown"));

        while (!shouldStop) {
            try {
                signal.await();
            } catch (InterruptedException exception) {
                break;
            }
        }

        onClose();

        if (shouldRestart)
            System.exit(EXIT_CODE_RESTART);
        else
            System.exit(EXIT_CODE_NORMAL);
    }

    /**
     * Initializes the application
     *
     * @return Whether the operation succeeded
     */
    private boolean init() {
        Logging.get().info("Starting the server ...");
        if (!configuration.getDatabasesFolder().exists()) {
            Logging.get().error("The repository location does not exist: " + configuration.getDatabasesFolder().getAbsolutePath());
            System.exit(1);
            return false;
        }
        try {
            controller = new ControllerServer(Logging.get(), configuration) {

                @Override
                public void onRequestShutdown() {
                    shouldStop = true;
                    shouldRestart = false;
                    signal.countDown();
                }

                @Override
                public void onRequestRestart() {
                    shouldStop = true;
                    shouldRestart = true;
                    signal.countDown();
                }
            };
        } catch (Exception exception) {
            Logging.get().error(exception);
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
        Logging.get().info("Shutting down this server ...");
        try {
            httpServer.close();
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
        try {
            controller.close();
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
        httpServer = null;
        controller = null;
    }

}
