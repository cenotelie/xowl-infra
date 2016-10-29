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

import com.sun.net.httpserver.*;
import org.xowl.infra.server.impl.ServerConfiguration;
import org.xowl.infra.server.impl.ServerController;
import org.xowl.infra.utils.SSLGenerator;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.logging.Logging;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the HTTP server for xOWL
 * Schema for the served URIs:
 * /api         Access point where to post core administrative commands
 * /api/db/xxx  Access point where to post commands specific to the database "xxx"
 * /web/        The web application front page
 *
 * @author Laurent Wouters
 */
public class HTTPServer implements Closeable {
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
     * The alias for generated certificates
     */
    public static final String GENERATED_ALIAS = "server.xowl.org";
    /**
     * The file name for the key store
     */
    private static final String KEY_STORE_FILE = "keystore.jks";

    /**
     * Gets the key store
     *
     * @param configuration The current configuration
     * @return The key store
     */
    private static Couple<KeyStore, String> getKeyStore(ServerConfiguration configuration) {
        String location = configuration.getSecurityKeyStore();
        String password = configuration.getSecurityKeyStorePassword();
        if (location == null) {
            File target = new File(configuration.getStartupFolder(), KEY_STORE_FILE);
            password = SSLGenerator.generateKeyStore(target, GENERATED_ALIAS);
            if (password == null)
                return null;
            location = KEY_STORE_FILE;
            configuration.setupKeyStore(location, password);
        }
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream stream = new FileInputStream(new File(configuration.getStartupFolder(), location))) {
                keyStore.load(stream, password.toCharArray());
            }
            return new Couple<>(keyStore, password);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException exception) {
            Logging.getDefault().error(exception);
            return null;
        }
    }

    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The backing HTTP server
     */
    private final HttpsServer server;
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
    public HTTPServer(ServerConfiguration configuration, final ServerController controller) {
        Logging.getDefault().info("Initializing the HTTPS server ...");
        this.configuration = configuration;
        SSLContext sslContext = null;
        Couple<KeyStore, String> ssl = getKeyStore(configuration);
        if (ssl != null) {
            try {
                Logging.getDefault().info("Setting up SSL");
                KeyManagerFactory keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManager.init(ssl.x, ssl.y.toCharArray());
                TrustManagerFactory trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManager.init(ssl.x);
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException exception) {
                Logging.getDefault().error(exception);
            }
        }
        InetSocketAddress address = new InetSocketAddress(
                configuration.getHttpAddress(),
                configuration.getHttpPort());
        HttpsServer temp = null;
        try {
            Logging.getDefault().info("Creating the HTTPS server");
            temp = HttpsServer.create(address, configuration.getHttpBacklog());
        } catch (IOException exception) {
            Logging.getDefault().error(exception);
        }
        if (temp != null && sslContext != null) {
            org.xowl.infra.server.standalone.Authenticator authenticator = new org.xowl.infra.server.standalone.Authenticator(controller, configuration.getSecurityRealm());
            server = temp;
            server.createContext("/api", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    try {
                        ((new HTTPAPIConnection(controller, httpExchange))).run();
                    } catch (Exception exception) {
                        Logging.getDefault().error(exception);
                    }
                }
            }).setAuthenticator(authenticator);
            server.createContext("/web/", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    ((new HTTPWebConnection(httpExchange))).run();
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
                        Logging.getDefault().error(exception);
                    }
                }
            });
            Logging.getDefault().info("HTTPS server is ready");
        } else {
            server = null;
        }
        if (server != null) {
            ArrayBlockingQueue<Runnable> executorQueue = new ArrayBlockingQueue<>(EXECUTOR_QUEUE_BOUND);
            executorPool = new ThreadPoolExecutor(EXECUTOR_POOL_MIN, EXECUTOR_POOL_MAX, 0, TimeUnit.SECONDS, executorQueue);
            server.setExecutor(executorPool);
        } else {
            executorPool = null;
        }
    }

    /**
     * Starts this server
     */
    public void start() {
        if (server != null) {
            server.start();
        }
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.stop(configuration.getHttpStopTimeout());
            executorPool.shutdown();
            try {
                executorPool.awaitTermination(configuration.getHttpStopTimeout(), TimeUnit.SECONDS);
            } catch (InterruptedException exception) {
                // do nothing
            }
        }
    }
}
