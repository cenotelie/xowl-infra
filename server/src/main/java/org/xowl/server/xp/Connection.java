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
import org.xowl.server.db.Database;
import org.xowl.server.db.User;

import java.io.*;
import java.net.Socket;
import java.util.List;

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
        // state 1
        while (true) {
            String line = socketInput.readLine();
            if (line == null)
                return;
            if (!line.startsWith("AUTH LOGIN "))
                continue;
            String login = line.substring("AUTH LOGIN ".length()).trim();
            line = socketInput.readLine();
            if (line == null)
                return;
            if (!line.startsWith("AUTH PASSWORD "))
                continue;
            String password = line.substring("AUTH PASSWORD ".length()).trim();
            user = controller.login(socket.getInetAddress(), login, password);
            if (user != null) {
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
            if (line == null) {
                // client quit
                return;
            }
            if (line.equals("LOGOUT")) {
                send("BYE");
                return;
            }
            if (line.equals("WHOAMI")) {
                send(user.getName());
                continue;
            }
            if (line.startsWith("ADMIN ")) {
                if (!runAdmin(line)) {
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
        if (line.equals("ADMIN LIST USERS"))
            return runAdminListUsers();
        if (line.startsWith("ADMIN CREATE USER "))
            return runAdminCreateUser(line);
        if (line.startsWith("ADMIN DELETE USER "))
            return runAdminDeleteUser(line);
        if (line.startsWith("ADMIN CHANGE PASSWORD "))
            return runAdminChangePassword(line);
        if (line.startsWith("ADMIN RESET PASSWORD "))
            return runAdminResetPassword(line);
        if (line.startsWith("ADMIN GRANT SERVER ADMIN "))
            return runAdminGrantServerAdmin(line);
        if (line.startsWith("ADMIN REVOKE SERVER ADMIN "))
            return runAdminRevokeServerAdmin(line);
        if (line.startsWith("ADMIN GRANT ADMIN "))
            return runAdminGrantDBAdmin(line);
        if (line.startsWith("ADMIN REVOKE ADMIN "))
            return runAdminRevokeDBAdmin(line);
        if (line.startsWith("ADMIN GRANT READ "))
            return runAdminGrantDBRead(line);
        if (line.startsWith("ADMIN REVOKE READ "))
            return runAdminRevokeDBRead(line);
        if (line.startsWith("ADMIN GRANT READ "))
            return runAdminGrantDBWrite(line);
        if (line.startsWith("ADMIN REVOKE READ "))
            return runAdminRevokeDBWrite(line);
        if (line.equals("ADMIN LIST DATABASES"))
            return runAdminListDatabases();
        if (line.startsWith("ADMIN CREATE DATABASE "))
            return runAdminCreateDatabase(line);
        if (line.startsWith("ADMIN DROP DATABASE "))
            return runAdminDropDatabase(line);
        send("PROTOCOL ERROR");
        return true;
    }

    /**
     * Request the listing of the users on this server
     * Expected command line: ADMIN LIST USERS
     *
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminListUsers() throws IOException {
        List<User> users = controller.getUsers(user);
        for (User user : users) {
            send(user.getName());
        }
        return true;
    }

    /**
     * Request the creation of a new user.
     * Expected command line: ADMIN CREATE USER login password
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminCreateUser(String line) throws IOException {
        line = line.substring("ADMIN CREATE USER ".length());
        int index = line.indexOf(' ');
        if (index == -1) {
            send("INVALID COMMAND");
            return true;
        }
        String login = line.substring(0, index);
        String password = line.substring(index + 1);
        User created = controller.createUser(user, login, password);
        send(created != null ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the deletion of a user.
     * Expected command line: ADMIN DELETE USER login
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminDeleteUser(String line) throws IOException {
        String login = line.substring("ADMIN DELETE USER ".length());
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.deleteUser(user, target);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the change of the current user password
     * Expected command line: ADMIN CHANGE PASSWORD password
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminChangePassword(String line) throws IOException {
        String password = line.substring("ADMIN CHANGE PASSWORD ".length());
        boolean success = controller.changePassword(user, password);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the reset of the password of another user
     * Expected command line: ADMIN RESET PASSWORD login password
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminResetPassword(String line) throws IOException {
        line = line.substring("ADMIN RESET PASSWORD ".length());
        int index = line.indexOf(' ');
        if (index == -1) {
            send("INVALID COMMAND");
            return true;
        }
        String login = line.substring(0, index);
        String password = line.substring(index + 1);
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.resetPassword(user, target, password);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the grant of administrator privilege to this server
     * Expected command line: ADMIN GRANT SERVER ADMIN login
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminGrantServerAdmin(String line) throws IOException {
        String login = line.substring("ADMIN GRANT SERVER ADMIN ".length());
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.grantServerAdmin(user, target);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the revocation of administrator privilege to this server
     * Expected command line: ADMIN GRANT SERVER ADMIN login
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminRevokeServerAdmin(String line) throws IOException {
        String login = line.substring("ADMIN REVOKE SERVER ADMIN ".length());
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.revokeServerAdmin(user, target);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the grant of administrator privilege on a database
     * Expected command line: ADMIN GRANT ADMIN login database
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminGrantDBAdmin(String line) throws IOException {
        line = line.substring("ADMIN GRANT ADMIN ".length());
        int index = line.indexOf(' ');
        if (index == -1) {
            send("INVALID COMMAND");
            return true;
        }
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        Database database = controller.getDatabase(user, dbName);
        if (database == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.grantDBAdmin(user, target, database);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the revocation of administrator privilege on a database
     * Expected command line: ADMIN REVOKE ADMIN login database
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminRevokeDBAdmin(String line) throws IOException {
        line = line.substring("ADMIN REVOKE ADMIN ".length());
        int index = line.indexOf(' ');
        if (index == -1) {
            send("INVALID COMMAND");
            return true;
        }
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        Database database = controller.getDatabase(user, dbName);
        if (database == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.revokeDBAdmin(user, target, database);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the grant of reading privilege on a database
     * Expected command line: ADMIN GRANT READ login database
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminGrantDBRead(String line) throws IOException {
        line = line.substring("ADMIN GRANT READ ".length());
        int index = line.indexOf(' ');
        if (index == -1) {
            send("INVALID COMMAND");
            return true;
        }
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        Database database = controller.getDatabase(user, dbName);
        if (database == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.grantDBRead(user, target, database);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the revocation of reading privilege on a database
     * Expected command line: ADMIN REVOKE READ login database
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminRevokeDBRead(String line) throws IOException {
        line = line.substring("ADMIN REVOKE READ ".length());
        int index = line.indexOf(' ');
        if (index == -1) {
            send("INVALID COMMAND");
            return true;
        }
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        Database database = controller.getDatabase(user, dbName);
        if (database == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.revokeDBRead(user, target, database);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the grant of writing privilege on a database
     * Expected command line: ADMIN GRANT WRITE login database
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminGrantDBWrite(String line) throws IOException {
        line = line.substring("ADMIN GRANT WRITE ".length());
        int index = line.indexOf(' ');
        if (index == -1) {
            send("INVALID COMMAND");
            return true;
        }
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        Database database = controller.getDatabase(user, dbName);
        if (database == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.grantDBWrite(user, target, database);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the revocation of writing privilege on a database
     * Expected command line: ADMIN REVOKE WRITE login database
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminRevokeDBWrite(String line) throws IOException {
        line = line.substring("ADMIN REVOKE WRITE ".length());
        int index = line.indexOf(' ');
        if (index == -1) {
            send("INVALID COMMAND");
            return true;
        }
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        User target = controller.getUser(user, login);
        if (target == null) {
            send("FAILED");
            return true;
        }
        Database database = controller.getDatabase(user, dbName);
        if (database == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.revokeDBWrite(user, target, database);
        send(success ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the listing of the databases on this server
     * Expected command line: ADMIN LIST DATABASES
     *
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminListDatabases() throws IOException {
        List<Database> databases = controller.getDatabases(user);
        for (Database database : databases) {
            send(database.getName());
        }
        return true;
    }

    /**
     * Request the creation of a new database
     * Expected command line: ADMIN CREATE DATABASE name
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminCreateDatabase(String line) throws IOException {
        String name = line.substring("ADMIN CREATE DATABASE ".length());
        Database database = controller.createDatabase(user, name);
        send(database != null ? "OK" : "FAILED");
        return true;
    }

    /**
     * Request the deletion of a database
     * Expected command line: ADMIN DROP DATABASE name
     *
     * @param line The command line
     * @return Whether to continue the connection
     * @throws IOException When an IO error occurs
     */
    private boolean runAdminDropDatabase(String line) throws IOException {
        String name = line.substring("ADMIN DROP DATABASE ".length());
        Database target = controller.getDatabase(user, name);
        if (target == null) {
            send("FAILED");
            return true;
        }
        boolean success = controller.dropDatabase(user, target);
        send(success ? "OK" : "FAILED");
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
