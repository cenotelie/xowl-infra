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

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import org.xowl.server.ServerConfiguration;
import org.xowl.server.db.Controller;
import org.xowl.server.ssl.SSLManager;
import org.xowl.utils.collections.Couple;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
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
    private final HttpsServer server;
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
    public Server(ServerConfiguration configuration, final Controller controller) {
        controller.getLogger().info("Initializing the HTTPS server ...");
        this.configuration = configuration;
        SSLContext sslContext = null;
        Couple<KeyStore, String> ssl = SSLManager.getKeyStore(configuration);
        if (ssl != null) {
            try {
                controller.getLogger().info("Setting up SSL");
                KeyManagerFactory keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManager.init(ssl.x, ssl.y.toCharArray());
                TrustManagerFactory trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManager.init(ssl.x);
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException exception) {
                exception.printStackTrace();
            }
        }
        InetSocketAddress address = new InetSocketAddress(
                configuration.getHttpAddress(),
                configuration.getHttpPort());
        HttpsServer temp = null;
        try {
            controller.getLogger().info("Creating the HTTPS server");
            temp = HttpsServer.create(address, configuration.getHttpBacklog());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (temp != null && sslContext != null) {
            server = temp;
            server.createContext("/", new TopHandler(controller)).setAuthenticator(new BasicAuthenticator(configuration.getSecurityRealm()) {
                @Override
                public boolean checkCredentials(String login, String password) {
                    return controller.login(login, password);
                }
            });
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                @Override
                public void configure(HttpsParameters params) {
                    try {
                        SSLContext defaultSSLContext = SSLContext.getDefault();
                        SSLEngine engine = defaultSSLContext.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());
                        params.setSSLParameters(defaultSSLContext.getDefaultSSLParameters());
                    } catch (NoSuchAlgorithmException exception) {
                        exception.printStackTrace();
                    }
                }
            });
            server.setExecutor(this);
            controller.getLogger().info("HTTPS server is ready");
        } else {
            server = null;
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
