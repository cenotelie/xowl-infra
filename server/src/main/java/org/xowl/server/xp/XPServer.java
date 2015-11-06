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

package org.xowl.server.xp;

import org.xowl.server.ServerConfiguration;
import org.xowl.server.db.Controller;
import org.xowl.server.ssl.SSLManager;
import org.xowl.utils.collections.Couple;

import javax.net.ssl.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Server for the xOWL protocol
 *
 * @author Laurent Wouters
 */
public class XPServer implements Closeable {
    /**
     * The bound of the executor queue
     */
    private static final int EXECUTOR_QUEUE_BOUND = 128;
    /**
     * The executor core pool size
     */
    private static final int EXECUTOR_POOL_MIN = 8;
    /**
     * The executor max pool size
     */
    private static final int EXECUTOR_POOL_MAX = 16;

    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The current controller
     */
    private final Controller controller;
    /**
     * The socket
     */
    private final SSLServerSocket socket;
    /**
     * The pool of executor threads
     */
    private final ThreadPoolExecutor executorPool;

    /**
     * Initializes this server
     *
     * @param configuration The current configuration
     * @param controller    The current controller
     */
    public XPServer(ServerConfiguration configuration, Controller controller) {
        controller.getLogger().info("Initializing the xOWL protocol server ...");
        this.configuration = configuration;
        this.controller = controller;
        ArrayBlockingQueue<Runnable> executorQueue = new ArrayBlockingQueue<>(EXECUTOR_QUEUE_BOUND);
        executorPool = new ThreadPoolExecutor(EXECUTOR_POOL_MIN, EXECUTOR_POOL_MAX, 0, TimeUnit.SECONDS, executorQueue);

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

        SSLServerSocket temp = null;
        try {
            controller.getLogger().info("Creating the xOWL protocol server");
            InetAddress address = InetAddress.getByName(configuration.getXPAddress());
            temp = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(
                    configuration.getXPPort(),
                    configuration.getXPBacklog(),
                    address);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if (temp != null && sslContext != null) {
            SSLEngine sslEngine = sslContext.createSSLEngine();
            SSLParameters sslParams = sslContext.getDefaultSSLParameters();
            sslParams.setNeedClientAuth(false);
            sslParams.setCipherSuites(sslEngine.getEnabledCipherSuites());
            sslParams.setProtocols(sslEngine.getEnabledProtocols());
            socket = temp;
            socket.setSSLParameters(sslParams);
            executorPool.execute(new Runnable() {
                @Override
                public void run() {
                    listen();
                }
            });
            controller.getLogger().info("xOWL protocol server is ready");
        } else {
            socket = null;
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
            executorPool.shutdown();
        }
    }

    /**
     * The main method for the listening thread
     */
    private void listen() {
        while (true) {
            try {
                Socket socket = this.socket.accept();
                Connection connection = new Connection(configuration, controller, socket);
                executorPool.execute(connection);
            } catch (IOException exception) {
                break;
            }
        }
    }
}
