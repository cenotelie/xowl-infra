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

import org.xowl.store.sparql.Result;
import org.xowl.utils.logging.Logger;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.InetSocketAddress;

/**
 * @author Laurent Wouters
 */
public class XSPConnection extends Connection {
    /**
     * The remote host to connect to
     */
    protected final String host;
    /**
     * The remote port to connect to
     */
    protected final int port;
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
    protected SSLSocket socket;
    /**
     * The stream used for writing to the socket
     */
    protected BufferedWriter socketOutput;
    /**
     * The stream used for reading to the socket
     */
    protected BufferedReader socketInput;

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
     * @param host     The XSP host
     * @param port     The XSP port
     * @param login    Login for the endpoint, if any, used for an HTTP Basic authentication
     * @param password Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    public XSPConnection(String host, int port, String login, String password) {
        this.host = host;
        this.port = port;
        this.login = login;
        this.password = password;
        try {
            connect();
        } catch (IOException exception) {
            Logger.DEFAULT.error(exception);
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null && socket.isConnected()) {
            try {
                socketInput.close();
                socketOutput.close();
                socket.close();
            } finally {
                socket = null;
                socketInput = null;
                socketOutput = null;
            }
        } else {
            socket = null;
            socketInput = null;
            socketOutput = null;
        }
    }

    @Override
    public Result sparqlQuery(String command) {
        return null;
    }

    @Override
    public Result sparqlUpdate(String command) {
        return null;
    }

    /**
     * Connects to the remote host
     *
     * @throws IOException When an IO exception occurs
     */
    protected void connect() throws IOException {
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
    protected String read() throws IOException {
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
    protected void send(String message) throws IOException {
        if (socketOutput == null)
            return;
        socketOutput.write(message);
        socketOutput.newLine();
        socketOutput.flush();
    }
}
