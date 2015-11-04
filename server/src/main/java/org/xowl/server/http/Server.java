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

package org.xowl.server.http;

import com.sun.net.httpserver.HttpServer;
import org.xowl.server.ServerConfiguration;
import org.xowl.server.db.Controller;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

/**
 * Implementation of the HTTP server for xOWL
 *
 * @author Laurent Wouters
 */
public class Server implements Closeable, Executor {
    /**
     * The bound of the executor queue
     */
    private static final int EXECUTOR_QUEUE_BOUND = 128;
    /**
     * The executor marker for stopping
     */
    private static final Runnable EXECUTOR_STOP_MARKER = new Runnable() {
        @Override
        public void run() {
            // do nothing
        }
    };

    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The backing HTTP server
     */
    private final HttpServer server;
    /**
     * The daemon thread executing the tasks for the server
     */
    private final Thread executorThread;
    /**
     * The queue for the executor
     */
    private final ArrayBlockingQueue<Runnable> executorQueue;

    /**
     * Initializes this server
     *
     * @param configuration The current configuration
     * @param controller    The current controller
     */
    public Server(ServerConfiguration configuration, Controller controller) {
        this.configuration = configuration;
        InetSocketAddress address = new InetSocketAddress(
                configuration.getHttpAddress(),
                configuration.getHttpPort());
        HttpServer temp = null;
        try {
            temp = HttpServer.create(address, configuration.getHttpBacklog());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        server = temp;
        if (server != null) {
            server.createContext("/", new FullHandler(controller));
            server.setExecutor(this);
        }
        executorQueue = new ArrayBlockingQueue<>(EXECUTOR_QUEUE_BOUND);
        executorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                execute();
            }
        }, Server.class.getCanonicalName());
    }

    /**
     * Starts this server
     */
    public void start() {
        if (server != null) {
            server.start();
            executorThread.start();
        }
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.stop(configuration.getHttpStopTimeout());
        }
        if (executorThread.isAlive()) {
            executorQueue.clear();
            executorQueue.offer(EXECUTOR_STOP_MARKER);
            try {
                executorThread.join(configuration.getHttpStopTimeout());
            } catch (InterruptedException exception) {
                // do nothing
            }
            if (executorThread.isAlive()) {
                executorThread.interrupt();
            }
        }
    }

    @Override
    public void execute(Runnable runnable) {
        executorQueue.offer(runnable);
    }

    /**
     * The main function for the executor
     */
    private void execute() {
        while (true) {
            try {
                Runnable task = executorQueue.take();
                if (task == EXECUTOR_STOP_MARKER)
                    break;
                task.run();
            } catch (InterruptedException exception) {
                break;
            }
        }
    }
}
