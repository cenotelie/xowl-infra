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

package org.xowl.store.storage.remote;

import org.xowl.utils.logging.ConsoleLogger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Represents a connection to a xOWL server using the xOWL protocol
 *
 * @author Laurent Wouters
 */
public class XOWLConnection implements Closeable {
    /**
     * The remote host to connect to
     */
    private final String host;
    /**
     * The remote port to connect to
     */
    private final int port;
    /**
     * The login
     */
    private final String login;
    /**
     * The password
     */
    private final String password;
    /**
     * The socket for the this connection
     */
    private SSLSocket socket;
    /**
     * The stream used for writing to the socket
     */
    private BufferedWriter socketOutput;
    /**
     * The stream used for reading to the socket
     */
    private BufferedReader socketInput;

    /**
     * Gets whether this connection is open
     *
     * @return Whether the connection is open
     */
    public boolean isOpen() {
        return socket != null && socket.isConnected();
    }

    /**
     * Initializes this connection
     *
     * @param host     The remote host to connect to
     * @param port     The remote port to connect to
     * @param login    The login
     * @param password The password
     */
    public XOWLConnection(String host, int port, String login, String password) {
        this.host = host;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    /**
     * Connects to the remote host
     *
     * @throws IOException When an IO exception occurs
     */
    public void connect() throws IOException {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            System.out.println();
                            // TODO: check certificate
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            System.out.println();
                            // TODO: check certificate
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException exception) {
            ConsoleLogger.INSTANCE.error(exception);
            return;
        }
        socket = (SSLSocket) sslContext.getSocketFactory().createSocket();
        socket.connect(new InetSocketAddress(host, port));
        socketOutput = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        send("AUTH " + login + " " + password);
    }

    /**
     * Reads the next available data from this connection
     *
     * @return The data
     * @throws IOException When an IO error occurs
     */
    public String read() throws IOException {
        if (socketInput == null)
            return null;
        return socketInput.readLine();
    }

    /**
     * Sends a message on this connection
     *
     * @param message The message
     * @throws IOException When an IO error occurs
     */
    public void send(String message) throws IOException {
        if (socketOutput == null)
            return;
        socketOutput.write(message);
        socketOutput.newLine();
        socketOutput.flush();
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            try {
                socketInput.close();
                socketOutput.close();
                socket.close();
            } finally {
                socket = null;
                socketInput = null;
                socketOutput = null;
            }
        }
    }
}
