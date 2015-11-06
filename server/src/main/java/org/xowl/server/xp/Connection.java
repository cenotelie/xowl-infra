/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Contributors:
 * Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.server.xp;

import org.xowl.server.ServerConfiguration;
import org.xowl.server.db.Controller;
import org.xowl.server.db.Database;
import org.xowl.server.db.User;

import java.io.*;
import java.net.Socket;

/**
 * Represents an active connection to the xOWL protocol server
 * This class basically implements the xOWL protocol from the server's standpoint
 * xOWL protocol is a text-based protocol, text is encoded in UTF-8. Server >> Client
 * === state 0 - welcome
 * if remote IP is banned, close this connection and exit
 * S >> "XOWL SERVER servername"
 * goto state 1
 * === state 1 - not authenticated
 * S << "AUTH LOGIN login"
 * S << "AUTH PASSWORD password"
 * S >> "AUTH OK", goto state 2
 * or
 * S >> "AUTH FAILED"
 * increment login attempt count
 * if attempt count is more than threshold, ban IP and close connection
 * === state 2 - authenticated
 * accept commands
 *
 * @author Laurent Wouters
 */
class Connection implements Runnable {
    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The current controller
     */
    private final Controller controller;
    /**
     * The socket for the this connection
     */
    private final Socket socket;
    /**
     * The stream used for writing to the socket
     */
    private final BufferedWriter socketOutput;
    /**
     * The stream used for reading to the socket
     */
    private final BufferedReader socketInput;
    /**
     * The authenticated user on this connection
     */
    private User user;

    /**
     * Initializes this connection
     *
     * @param controller The current controller
     * @param socket     The socket for the this connection
     */
    public Connection(ServerConfiguration configuration, Controller controller, Socket socket) {
        this.configuration = configuration;
        this.controller = controller;
        this.socket = socket;
        BufferedWriter output = null;
        BufferedReader input = null;
        try {
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        }
        socketOutput = output;
        socketInput = input;
    }

    @Override
    public void run() {
        try {
            if (controller.isBanned(socket.getInetAddress()))
                return;
            doRun();
        } catch (IOException exception) {
            controller.getLogger().error(exception);
        } finally {
            try {
                socket.close();
            } catch (IOException exception) {
                controller.getLogger().error(exception);
            }
        }
    }

    /**
     * Runs the xOWL protocol
     *
     * @throws IOException When an IO error occurs
     */
    private void doRun() throws IOException {
        // state 0
        send("XOWL SERVER " + configuration.getServerName());
        socketOutput.newLine();
        // state 1
        while (true) {
            String line = socketInput.readLine();
            if (!line.startsWith("AUTH LOGIN "))
                continue;
            String login = line.substring("AUTH LOGIN ".length());
            line = socketInput.readLine();
            if (!line.startsWith("AUTH PASSWORD "))
                continue;
            String password = line.substring("AUTH PASSWORD ".length());
            boolean success = controller.login(socket.getInetAddress(), login, password);
            if (success) {
                user = controller.getUser(login);
                send("AUTH OK");
                break;
            }
            if (controller.isBanned(socket.getInetAddress())) {
                return;
            }
            send("AUTH FAILED");
        }
        // state 2
        runAuthenticated();
    }

    /**
     * Runs this connection with an authenticated user
     *
     * @throws IOException When an IO error occurs
     */
    private void runAuthenticated() throws IOException {
        while (true) {
            String line = socketInput.readLine();
            if (line == null || line.equals("LOGOUT")) {
                send("BYE");
                return;
            }
            if (line.startsWith("ADMIN ")) {
                if (!runAdmin(line)) {
                    send("BYE");
                    return;
                }
                continue;
            }
            if (line.startsWith("DATABASE ")) {
                if (!runDatabase(line)) {
                    send("BYE");
                    return;
                }
                continue;
            }
            send("PROTOCOL ERROR");
        }
    }

    /**
     * Runs this connection on an administrative command
     *
     * @param line The opening command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdmin(String line) throws IOException {
        if (line.equals("ADMIN SHUTDOWN")) {
            send("==> shutting down");
            controller.requestShutdown();
            return false;
        }
        if (line.equals("ADMIN RESTART")) {
            send("==> restarting");
            controller.requestRestart();
            return false;
        }

        if (line.equals("ADMIN CREATE USER")) {
            if (!controller.isServerAdmin(user)) {
                send("UNAUTHORIZED");
                return true;
            }
            send("login?");
            String login = socketInput.readLine();
            send("password?");
            String password = socketInput.readLine();
            send("same password?");
            String password2 = socketInput.readLine();
            if (!password.equals(password2)) {
                send("passwords do not match");
                return true;
            }
            boolean success = controller.newUser(login, password);
            send(success ? "OK" : "FAILED");
            return true;
        }
        if (line.equals("ADMIN DELETE USER")) {
            if (!controller.isServerAdmin(user)) {
                send("UNAUTHORIZED");
                return true;
            }
            String login = socketInput.readLine();
            // TODO: implement this
            send("FAILED");
            return true;
        }
        if (line.equals("ADMIN CHANGE PASSWORD")) {
            // TODO: implement this
            send("FAILED");
            return true;
        }
        if (line.equals("ADMIN RESET PASSWORD")) {
            if (!controller.isServerAdmin(user)) {
                send("UNAUTHORIZED");
                return true;
            }
            String login = socketInput.readLine();
            String password = socketInput.readLine();
            // TODO: implement this
            send("FAILED");
            return true;
        }

        if (line.equals("ADMIN CREATE DATABASE")) {
            if (!controller.isServerAdmin(user)) {
                send("UNAUTHORIZED");
                return true;
            }
            String name = socketInput.readLine();
            Database db = controller.newDatabase(name);
            send(db != null ? "OK" : "FAILED");
            return true;
        }
        if (line.equals("ADMIN DROP DATABASE")) {
            if (!controller.isServerAdmin(user)) {
                send("UNAUTHORIZED");
                return true;
            }
            String name = socketInput.readLine();
            // TODO: implement this
            send("FAILED");
            return true;
        }
        if (line.equals("ADMIN GRANT ADMIN")) {
            if (!controller.isServerAdmin(user)) {
                send("UNAUTHORIZED");
                return true;
            }
            String dbName = socketInput.readLine();
            String userName = socketInput.readLine();
            // TODO: implement this
            send("FAILED");
            return true;
        }
        if (line.equals("ADMIN GRANT READ")) {
            String dbName = socketInput.readLine();
            String userName = socketInput.readLine();
            // TODO: implement this
            send("FAILED");
            return true;
        }
        if (line.equals("ADMIN GRANT WRITE")) {
            String dbName = socketInput.readLine();
            String userName = socketInput.readLine();
            // TODO: implement this
            send("FAILED");
            return true;
        }
        send("PROTOCOL ERROR");
        return true;
    }

    /**
     * Runs this connection on a database-specific command
     *
     * @param line The opening command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runDatabase(String line) throws IOException {


        send("PROTOCOL ERROR");
        return true;
    }

    /**
     * Sends data over the socket
     *
     * @param message The message to send
     */
    private void send(String message) throws IOException {
        socketOutput.write(message);
        socketOutput.newLine();
        socketOutput.flush();
    }
}
