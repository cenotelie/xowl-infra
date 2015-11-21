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
import org.xowl.store.sparql.ResultFailure;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Manages a connection to a xOWL server using the xOWL Server Protocol
 *
 * @author Laurent Wouters
 */
public class XSPConnection extends Connection {
    /**
     * Timeout for reading from the socket
     */
    private static final int READING_TIMEOUT = 2000;
    /**
     * The number of attempts for trying to reconnect on timeout
     */
    private static final int TIMEOUT_RETRY_ATTEMPS = 3;
    /**
     * The interval between retries on timeout
     */
    private static final int TIMEOUT_RETRY_INTERVAL = 1500;

    /**
     * The connection attempt succeeded, the connection is now open
     */
    public static final int CONNECTION_OK = 0;
    /**
     * The connection timed-out while reading
     */
    public static final int CONNECTION_TIMEOUT = 1;
    /**
     * The connection attempt failed.
     * The connection was closed by the remote host.
     * This could be caused by the client being banned by the server
     */
    public static final int CONNECTION_CLOSED_BY_HOST = 2;
    /**
     * The connection went through but the server's greetings were unexpected
     */
    public static final int CONNECTION_UNEXECTED_HOST = 3;
    /**
     * The connection attempt failed.
     * The authentication process failed, the login/password was not accepted.
     */
    public static final int CONNECTION_AUTHENTICATION_FAILED = 4;
    /**
     * The connection attempt failed due to an error while creating the socket
     */
    public static final int CONNECTION_SOCKET_CREATION_FAILED = 5;
    /**
     * The connection attempt failed due to the inability to resolve the required remote host
     */
    public static final int CONNECTION_RESOLUTION_FAILED = 6;
    /**
     * The connection attempt failed due to an error while configuring the socket
     */
    public static final int CONNECTION_SOCKET_CONF_FAILED = 7;
    /**
     * The connection failed to an IO error (could be anything)
     */
    public static final int CONNECTION_IO_FAILED = 8;


    /**
     * The remote host to connect to
     */
    private final String host;
    /**
     * The remote port to connect to
     */
    private final int port;
    /**
     * The target database
     */
    private final String database;
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
     * The status of the last connection attempt
     */
    private int lastStatus;

    /**
     * Gets whether this connection is open
     *
     * @return Whether the connection is open
     */
    public boolean isOpen() {
        return socket != null && socket.isConnected();
    }

    /**
     * Gets the status of the last connection attempt
     *
     * @return The last status
     */
    public int getLastStatus() {
        return lastStatus;
    }

    /**
     * Initializes this connection
     *
     * @param host     The XSP host
     * @param port     The XSP port
     * @param database The target database
     * @param login    Login for the endpoint, if any, used for an HTTP Basic authentication
     * @param password Password for the endpoint, if any, used for an HTTP Basic authentication
     */
    public XSPConnection(String host, int port, String database, String login, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.login = login;
        this.password = password;
        this.lastStatus = CONNECTION_OK;
    }

    @Override
    public void close() throws IOException {
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        } finally {
            socket = null;
        }
    }

    @Override
    public Result sparqlQuery(String command) {
        String response = request("SPARQL " + database + " " + command);
        if (response == null)
            return new ResultFailure("connection failed");
        if (response.startsWith("KO"))
            return new ResultFailure(response.substring(2));
        response = response.substring(2);
        if (response.startsWith("{")) {
            // solution set
            return parseResponseSolutions(response);
        } else {
            return parseResponseQuads(response);
        }
    }

    @Override
    public Result sparqlUpdate(String command) {
        String response = request("SPARQL " + database + " " + command);
        if (response == null)
            return new ResultFailure("connection failed");
        if (response.startsWith("KO"))
            return new ResultFailure(response.substring(2));
        return parseResponseUpdate(response.substring(2));
    }

    /**
     * Sends a message over this connection
     *
     * @param message The message
     * @return The response, or null of the connection failed
     */
    protected synchronized String request(String message) {
        if (!isOpen()) {
            if (lastStatus > CONNECTION_TIMEOUT) {
                // fatal error other than timeout occurred before
                return null;
            }
            lastStatus = connect();
            int retries = 0;
            while (lastStatus == CONNECTION_TIMEOUT && retries < TIMEOUT_RETRY_ATTEMPS) {
                // sleep for a while and retry
                try {
                    Thread.sleep(TIMEOUT_RETRY_INTERVAL);
                } catch (InterruptedException exception) {
                    // WTF
                    return null;
                }
                retries++;
                lastStatus = connect();
            }
            if (lastStatus != CONNECTION_OK)
                return null;
        }

        try {
            SocketHelper.write(socket, message);
        } catch (IOException exception) {
            // IO failed
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return null;
        }
        try {
            String response = SocketHelper.read(socket);
            if (response == null) {
                // connection closed
                socket.close();
                socket = null;
                return null;
            }
            return response;
        } catch (SocketTimeoutException exception) {
            // timeout while reading
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return null;
        } catch (IOException exception) {
            // IO failed
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return null;
        }
    }

    /**
     * Connects to the remote host
     *
     * @return The result of the connection attempt
     */
    private int connect() {
        int result = connectionSetupSocket();
        if (result != CONNECTION_OK)
            return result;
        result = connectionGreetings();
        if (result != CONNECTION_OK)
            return result;
        return connectionAuthentication();
    }

    /**
     * Initial setup of the socket for the connection
     *
     * @return The connection's status
     */
    private int connectionSetupSocket() {
        try {
            socket = (SSLSocket) sslContext.getSocketFactory().createSocket();
        } catch (IOException exception) {
            // failed to create the socket
            return CONNECTION_SOCKET_CREATION_FAILED;
        }
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException exception) {
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_RESOLUTION_FAILED;
        }
        try {
            socket.setSoTimeout(READING_TIMEOUT);
        } catch (SocketException exception) {
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_SOCKET_CONF_FAILED;
        }
        return CONNECTION_OK;
    }

    /**
     * Performs the greeting phase of the initial connection setup
     *
     * @return The connection's status
     */
    private int connectionGreetings() {
        String greeting;
        try {
            greeting = SocketHelper.read(socket);
        } catch (SocketTimeoutException exception) {
            // server failed to send greetings
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_TIMEOUT;
        } catch (IOException exception) {
            // socket reading failed
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_IO_FAILED;
        }
        if (greeting == null) {
            // the socket closed
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_CLOSED_BY_HOST;
        }
        if (!greeting.startsWith("XOWL SERVER")) {
            // the socket closed
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_UNEXECTED_HOST;
        }
        return CONNECTION_OK;
    }

    /**
     * Performs the authentication part of the initial connection setup
     *
     * @return The connection's status
     */
    private int connectionAuthentication() {
        try {
            SocketHelper.write(socket, "AUTH " + login + " " + password);
        } catch (IOException exception) {
            // socket writing failed
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_IO_FAILED;
        }
        String response;
        try {
            response = SocketHelper.read(socket);
        } catch (SocketTimeoutException exception) {
            // server failed to respond
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_TIMEOUT;
        } catch (IOException exception) {
            // socket reading failed
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_IO_FAILED;
        }
        if (response == null) {
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_CLOSED_BY_HOST;
        }
        if (!response.equals("OK")) {
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_AUTHENTICATION_FAILED;
        }
        return CONNECTION_OK;
    }
}
