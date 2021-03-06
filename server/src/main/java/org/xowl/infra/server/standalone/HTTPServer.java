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
import fr.cenotelie.commons.utils.SSLGenerator;
import fr.cenotelie.commons.utils.collections.Couple;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.server.ServerConfiguration;
import org.xowl.infra.server.api.ApiV1;
import org.xowl.infra.server.impl.ControllerServer;

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
     * The URI prefix for served web resources
     */
    private static final String URI_PREFIX_WEB = "/web/";

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
            Logging.get().error(exception);
            return null;
        }
    }

    /**
     * Creates a new HTTP server
     *
     * @param configuration The current configuration
     * @return The created server
     */
    private static HttpServer initNewHttpServer(ServerConfiguration configuration) {
        InetSocketAddress address = new InetSocketAddress(
                configuration.getHttpAddress(),
                configuration.getHttpPort());
        Logging.get().info("Creating the HTTP server");
        HttpServer server;
        try {

            server = HttpServer.create(address, configuration.getHttpBacklog());
        } catch (IOException exception) {
            Logging.get().error(exception);
            return null;
        }
        return server;
    }

    /**
     * Creates a new HTTPS server
     *
     * @param configuration The current configuration
     * @return The created server
     */
    private static HttpServer initNewHttpsServer(ServerConfiguration configuration) {
        SSLContext sslContext;
        Couple<KeyStore, String> ssl = getKeyStore(configuration);
        if (ssl == null)
            return null;
        try {
            Logging.get().info("Setting up SSL");
            KeyManagerFactory keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManager.init(ssl.x, ssl.y.toCharArray());
            TrustManagerFactory trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManager.init(ssl.x);
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException exception) {
            Logging.get().error(exception);
            return null;
        }
        InetSocketAddress address = new InetSocketAddress(
                configuration.getHttpAddress(),
                configuration.getHttpPort());
        Logging.get().info("Creating the HTTPS server");
        HttpsServer server;
        try {

            server = HttpsServer.create(address, configuration.getHttpBacklog());
        } catch (IOException exception) {
            Logging.get().error(exception);
            return null;
        }
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
                    Logging.get().error(exception);
                }
            }
        });
        return server;
    }

    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The backing HTTP server
     */
    private final HttpServer server;
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
    public HTTPServer(ServerConfiguration configuration, final ControllerServer controller) {
        Logging.get().info("Initializing the HTTP server ...");
        this.configuration = configuration;
        server = configuration.getHttpSecure() ? initNewHttpsServer(configuration) : initNewHttpServer(configuration);
        if (server != null) {
            server.createContext(ApiV1.URI_PREFIX, new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    try {
                        ((new HTTPConnectionApiV1(controller, httpExchange))).run();
                    } catch (Exception exception) {
                        Logging.get().error(exception);
                    }
                }
            });
            server.createContext(URI_PREFIX_WEB, new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    ((new HTTPConnectionWeb(httpExchange))).run();
                }
            });
            server.createContext("/" + configuration.getLinkedDataPrefix() + "/", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    try {
                        ((new HTTPConnectionLD(HTTPServer.this.configuration, controller, httpExchange))).run();
                    } catch (Exception exception) {
                        Logging.get().error(exception);
                    }
                }
            });
            Logging.get().info("HTTP server is ready");
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
