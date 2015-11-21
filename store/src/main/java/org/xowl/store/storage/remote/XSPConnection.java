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
import org.xowl.utils.Files;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

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
    private static final int TIMEOUT_RETRY_ATTEMPTS = 3;
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
     * The name of the server
     */
    private String serverName;

    /**
     * Gets the status of the last connection attempt
     *
     * @return The last status
     */
    public int getLastStatus() {
        return lastStatus;
    }

    /**
     * Gets the name of the server
     *
     * @return The name of the server
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Initializes this connection
     *
     * @param host     The XSP host
     * @param port     The XSP port
     * @param database The target database, if any
     * @param login    Login for the endpoint
     * @param password Password for the endpoint
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
     * Request the xOWL server shutdown
     *
     * @return The protocol reply
     */
    public XSPReply serverShutdown() {
        String response = request("ADMIN SHUTDOWN");
        if (response == null)
            return new XSPReplyNetworkError(getReplyForError());
        if (response.startsWith("KO"))
            return getReplyForFailure(response.substring(2));
        return XSPReplySuccess.instance();
    }

    /**
     * Request the xOWL server restart
     *
     * @return The protocol reply
     */
    public XSPReply serverRestart() {
        String response = request("ADMIN RESTART");
        if (response == null)
            return new XSPReplyNetworkError(getReplyForError());
        if (response.startsWith("KO"))
            return getReplyForFailure(response.substring(2));
        return XSPReplySuccess.instance();
    }

    /**
     * Requests the list of the users on the xOWL server
     *
     * @return The protocol reply
     */
    public XSPReply getUsers() {
        String response = request("ADMIN LIST USERS");
        if (response == null)
            return new XSPReplyNetworkError(getReplyForError());
        if (response.startsWith("KO"))
            return getReplyForFailure(response.substring(2));
        response = response.substring(2);
        String[] lines = response.split(Files.LINE_SEPARATOR);
        List<String> users = new ArrayList<>();
        for (int i = 0; i != lines.length; i++) {
            String line = lines[i].trim();
            if (!lines[i].isEmpty())
                users.add(line);
        }
        return new XSPReplyResult<>(users);
    }

    /**
     * Request the creation of a new user
     *
     * @param login    The login for the new user
     * @param password The password for the new user
     * @return The protocol reply
     */
    public XSPReply createUser(String login, String password) {
        String response = request("ADMIN CREATE USER " + login + " " + password);
        if (response == null)
            return new XSPReplyNetworkError(getReplyForError());
        if (response.startsWith("KO"))
            return getReplyForFailure(response.substring(2));
        return XSPReplySuccess.instance();
    }

    /**
     * Request the deletion of a user
     *
     * @param login The login of the user to delete
     * @return The protocol reply
     */
    public XSPReply deleteUser(String login, String password) {
        String response = request("ADMIN DELETE USER " + login);
        if (response == null)
            return new XSPReplyNetworkError(getReplyForError());
        if (response.startsWith("KO"))
            return getReplyForFailure(response.substring(2));
        return XSPReplySuccess.instance();
    }

    /**
     * Request the change of the password for the current user
     *
     * @param password The new password for the user
     * @return The protocol reply
     */
    public XSPReply changePassword(String password) {
        String response = request("ADMIN CHANGE PASSWORD " + password);
        if (response == null)
            return new XSPReplyNetworkError(getReplyForError());
        if (response.startsWith("KO"))
            return getReplyForFailure(response.substring(2));
        return XSPReplySuccess.instance();
    }

    /**
     * Request the reset of the password of another user
     *
     * @param login    The login of the user to reset the password for
     * @param password The new password for the user
     * @return The protocol reply
     */
    public XSPReply resetPassword(String login, String password) {
        String response = request("ADMIN RESET PASSWORD " + login + " " + password);
        if (response == null)
            return new XSPReplyNetworkError(getReplyForError());
        if (response.startsWith("KO"))
            return getReplyForFailure(response.substring(2));
        return XSPReplySuccess.instance();
    }

    /**
     * Gets the XSP reply message for a network error
     *
     * @return The XSP reply message
     */
    protected String getReplyForError() {
        switch (lastStatus) {
            case CONNECTION_OK:
                return null;
            case CONNECTION_TIMEOUT:
                return "Timeout";
            case CONNECTION_CLOSED_BY_HOST:
                return "Connection closed by host";
            case CONNECTION_UNEXECTED_HOST:
                return "Host handshake failed";
            case CONNECTION_AUTHENTICATION_FAILED:
                return "Authentication failed";
            case CONNECTION_SOCKET_CREATION_FAILED:
                return "Socket creation failed";
            case CONNECTION_RESOLUTION_FAILED:
                return "Host resolution failed";
            case CONNECTION_SOCKET_CONF_FAILED:
                return "Socket configuration failed";
            case CONNECTION_IO_FAILED:
                return "Reading/Writing failed";
        }
        return "Unknown error";
    }

    /**
     * Gets the XSP reply for a failing server response
     *
     * @param response The server response
     * @return The XSP reply
     */
    private XSPReply getReplyForFailure(String response) {
        if ("UNAUTHENTICATED".equals(response))
            return XSPReplyUnauthenticated.instance();
        if ("UNAUTHORIZED".equals(response))
            return XSPReplyUnauthorized.instance();
        return new XSPReplyFailure(response);
    }

    /**
     * Sends an XSP message over this connection
     *
     * @param message The message
     * @return The response, or null of the connection failed
     */
    protected synchronized String request(String message) {
        boolean success = doSend(message);
        if (!success)
            return null;

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
     * Sends the specified message
     * This method makes
     *
     * @param message The message to send
     * @return Whether the operation succeeded
     */
    private boolean doSend(String message) {
        // first attempt to send the message
        int status = doTrySend(message);
        if (status < 0) {
            // failed to connect at all
            return false;
        }
        if (status > 0) {
            // succeeded
            return true;
        }

        // here the first attempt failed, but we were connected once
        // try to restore the connection
        return (doTrySend(message) > 0);
    }

    /**
     * Attempts to send a message over the socket
     *
     * @param message The message to send
     * @return Whether the operation succeeded
     */
    private int doTrySend(String message) {
        if (!doGetConnected())
            // cannot get connected at all
            return -1;
        // here we are supposed to be connected and authenticated
        try {
            SocketHelper.write(socket, message);
            // we sent the message, stop here
            return 1;
        } catch (IOException exception) {
            // this is a broken pipe, the server must have close the connection
        }
        // cleanup the old socket
        try {
            socket.close();
        } catch (IOException e) {
            // do nothing
        }
        socket = null;
        return 0;
    }

    /**
     * Ensures that the socket is connected
     *
     * @return Whether the operation succeeded
     */
    private boolean doGetConnected() {
        if (socket == null || !socket.isConnected()) {
            if (lastStatus > CONNECTION_TIMEOUT) {
                // fatal error other than timeout occurred before
                return false;
            }
            lastStatus = doTryConnect();
            int retries = 0;
            while (lastStatus == CONNECTION_TIMEOUT && retries < TIMEOUT_RETRY_ATTEMPTS) {
                // sleep for a while and retry
                try {
                    Thread.sleep(TIMEOUT_RETRY_INTERVAL);
                } catch (InterruptedException exception) {
                    // WTF
                    return false;
                }
                retries++;
                lastStatus = doTryConnect();
            }
            return (lastStatus == CONNECTION_OK);
        }
        return true;
    }

    /**
     * Tries to connect to the remote host
     *
     * @return The result of the connection attempt
     */
    private int doTryConnect() {
        int status = connectionSetupSocket();
        if (status != CONNECTION_OK)
            return status;
        status = connectionGreetings();
        if (status != CONNECTION_OK)
            return status;
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
        if (!greeting.startsWith("XOWL SERVER ")) {
            // the socket closed
            try {
                socket.close();
            } catch (IOException e) {
                // do nothing
            }
            socket = null;
            return CONNECTION_UNEXECTED_HOST;
        }
        serverName = greeting.substring("XOWL SERVER ".length());
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
        if (!response.startsWith("OK")) {
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
