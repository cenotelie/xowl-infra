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
import org.xowl.server.xp.XPServer;

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
                public void requestShutdown() {
                    shouldStop = true;
                }

                @Override
                public void requestRestart() {
                    shouldStop = true;
                }
            };
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        HTTPServer httpServer = new HTTPServer(configuration, controller);
        httpServer.start();
        XPServer xpServer = new XPServer(configuration, controller);

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
            xpServer.close();
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
}
