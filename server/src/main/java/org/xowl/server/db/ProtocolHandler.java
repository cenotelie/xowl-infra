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

package org.xowl.server.db;

import java.net.InetAddress;
import java.util.Collections;

/**
 * Implements the xOWL Server Protocol
 *
 * @author Laurent Wouters
 */
public abstract class ProtocolHandler {
    /**
     * The backend server controller
     */
    protected final Controller controller;
    /**
     * The currently authenticated user, if any
     */
    protected User user;

    /**
     * Initializes this instance
     *
     * @param controller The backend server controller
     */
    public ProtocolHandler(Controller controller) {
        this.controller = controller;
    }

    /**
     * Executes a command
     *
     * @param command The command to execute
     * @return The protocol reply, or null if the client got banned
     */
    public ProtocolReply execute(String command) {
        if (command == null)
            return ProtocolReplyFailure.instance();
        if (command.startsWith("AUTH ")) {
            ProtocolReply reply = runAuth(command);
            if (reply == null)
                // client got banned
                onExit();
            return reply;
        }
        if (command.equals("LOGOUT")) {
            if (user == null)
                return ProtocolReplyUnauthenticated.instance();
            onExit();
            user = null;
            return new ProtocolReplySuccess("BYE");
        }
        if (command.equals("EXIT") || command.equals("BYE")) {
            onExit();
            user = null;
            return new ProtocolReplySuccess("BYE");
        }
        if (command.equals("WHOAMI")) {
            if (user == null)
                return ProtocolReplyUnauthenticated.instance();
            return new ProtocolReplySuccess(user.getName());
        }
        if (command.startsWith("ADMIN "))
            return runAdmin(command);
        if (command.startsWith("SPARQL "))
            return runSPARQL(command);
        if (command.startsWith("DATABASE "))
            return runDB(command);
        return new ProtocolReplyFailure("UNRECOGNIZED COMMAND");
    }

    /**
     * Gets the client remote address
     *
     * @return The client for this handler
     */
    protected abstract InetAddress getClient();

    /**
     * When the user requested to exit
     */
    protected abstract void onExit();

    /**
     * Runs the authentication command
     * Expected command line: AUTH login password
     *
     * @param line the authentication command
     * @return The protocol reply
     */
    private ProtocolReply runAuth(String line) {
        line = line.substring("AUTH ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String password = line.substring(index + 1);
        ProtocolReply result = controller.login(getClient(), login, password);
        if (result == null) {
            // client got banned
            return null;
        }
        if (result.isSuccess()) {
            user = ((ProtocolReplyResult<User>) result).getData();
            return ProtocolReplySuccess.instance();
        }
        return result;
    }

    /**
     * Runs a SPARQL command
     * Expected command line: SPARQL database command
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runSPARQL(String line) {
        line = line.substring("SPARQL ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String dbName = line.substring(0, index);
        String sparql = line.substring(index + 1);
        ProtocolReply database = controller.getDatabase(user, dbName);
        if (!database.isSuccess())
            return database;
        return controller.sparql(user, ((ProtocolReplyResult<Database>) database).getData(), sparql, Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    /**
     * Runs a DATABASE command
     * Expected command line: DATABASE database command
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runDB(String line) {
        line = line.substring("DATABASE ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String dbName = line.substring(0, index);
        line = line.substring(index + 1);
        ProtocolReply dbResult = controller.getDatabase(user, dbName);
        if (!dbResult.isSuccess())
            return dbResult;
        Database database = ((ProtocolReplyResult<Database>) dbResult).getData();
        if (line.equals("ENTAILMENT"))
            return runDBGetEntailment(database);
        if (line.startsWith("ENTAILMENT"))
            return runDBSetEntailment(database, line);
        if (line.equals("LIST RULES"))
            return runDBListRules(database);
        if (line.equals("LIST ACTIVE RULES"))
            return runDBListActiveRules(database);
        if (line.startsWith("ADD RULE "))
            return runDBAddRule(database, line);
        if (line.startsWith("REMOVE RULE "))
            return runDBRemoveRule(database, line);
        if (line.startsWith("ACTIVATE "))
            return runDBActivateRule(database, line);
        if (line.startsWith("DEACTIVATE "))
            return runDBDeactivateRule(database, line);
        if (line.startsWith("IS ACTIVE "))
            return runDBIsActiveRule(database, line);
        if (line.startsWith("STATUS "))
            return runDBGetRuleStatus(database, line);
        if (line.startsWith("EXPLAIN "))
            return runDBGetExplanation(database, line);
        return new ProtocolReplyFailure("UNRECOGNIZED COMMAND");
    }

    /**
     * Request the retrieval of the entailment regime
     * Expected command line: DATABASE db ENTAILMENT
     *
     * @param database The active database
     * @return The protocol reply
     */
    private ProtocolReply runDBGetEntailment(Database database) {
        return controller.dbGetEntailmentRegime(user, database);
    }

    /**
     * Request the setting of the entailment regime
     * Expected command line: DATABASE db ENTAILMENT regime
     *
     * @param database The active database
     * @return The protocol reply
     */
    private ProtocolReply runDBSetEntailment(Database database, String line) {
        String regime = line.substring("ENTAILMENT ".length());
        return controller.dbSetEntailmentRegime(user, database, regime);
    }

    /**
     * Request the listing of the all the rules rules
     * Expected command line: DATABASE db LIST RULES
     *
     * @param database The active database
     * @return The protocol reply
     */
    private ProtocolReply runDBListRules(Database database) {
        return controller.dbListAllRules(user, database);
    }

    /**
     * Request the listing of the active rules
     * Expected command line: DATABASE db LIST ACTIVE RULES
     *
     * @param database The active database
     * @return The protocol reply
     */
    private ProtocolReply runDBListActiveRules(Database database) {
        return controller.dbListActiveRules(user, database);
    }

    /**
     * Request the insertion of a rule
     * Expected command line: DATABASE db ADD RULE content
     *
     * @param database The active database
     * @param line     The command line
     * @return The protocol reply
     */
    private ProtocolReply runDBAddRule(Database database, String line) {
        String content = line.substring("ADD RULE ".length());
        return controller.dbAddRule(user, database, content, false);
    }

    /**
     * Request the removal of a rule
     * Expected command line: DATABASE db REMOVE RULE rule
     *
     * @param database The active database
     * @param line     The command line
     * @return The protocol reply
     */
    private ProtocolReply runDBRemoveRule(Database database, String line) {
        String rule = line.substring("REMOVE RULE ".length());
        return controller.dbRemoveRule(user, database, rule);
    }

    /**
     * Request the activation of a rule
     * Expected command line: DATABASE db ACTIVATE rule
     *
     * @param database The active database
     * @param line     The command line
     * @return The protocol reply
     */
    private ProtocolReply runDBActivateRule(Database database, String line) {
        String rule = line.substring("ACTIVATE ".length());
        return controller.dbActivateRule(user, database, rule);
    }

    /**
     * Request the deactivation of a rule
     * Expected command line: DATABASE db DEACTIVATE rule
     *
     * @param database The active database
     * @param line     The command line
     * @return The protocol reply
     */
    private ProtocolReply runDBDeactivateRule(Database database, String line) {
        String rule = line.substring("DEACTIVATE ".length());
        return controller.dbDeactivateRule(user, database, rule);
    }

    /**
     * Request whether a rule is active
     * Expected command line: DATABASE db IS ACTIVE rule
     *
     * @param database The active database
     * @param line     The command line
     * @return The protocol reply
     */
    private ProtocolReply runDBIsActiveRule(Database database, String line) {
        String rule = line.substring("IS ACTIVE ".length());
        return controller.dbIsRuleActive(user, database, rule);
    }

    /**
     * Request the matching status of a rule
     * Expected command line: DATABASE db STATUS rule
     *
     * @param database The active database
     * @param line     The command line
     * @return The protocol reply
     */
    private ProtocolReply runDBGetRuleStatus(Database database, String line) {
        String rule = line.substring("STATUS ".length());
        return controller.dbGetRuleStatus(user, database, rule);
    }

    /**
     * Request the explanation of a quad
     * Expected command line: DATABASE db EXPLAIN quad
     *
     * @param database The active database
     * @param line     The command line
     * @return The protocol reply
     */
    private ProtocolReply runDBGetExplanation(Database database, String line) {
        String quad = line.substring("EXPLAIN ".length());
        return controller.dbGetQuadExplanation(user, database, quad);
    }

    /**
     * Runs a administrative command
     *
     * @param command The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdmin(String command) {
        if (command.equals("ADMIN SHUTDOWN")) {
            onExit();
            return controller.serverShutdown(user);
        }
        if (command.equals("ADMIN RESTART")) {
            onExit();
            return controller.serverRestart(user);
        }

        if (command.equals("ADMIN LIST USERS"))
            return runAdminListUsers();
        if (command.startsWith("ADMIN CREATE USER "))
            return runAdminCreateUser(command);
        if (command.startsWith("ADMIN DELETE USER "))
            return runAdminDeleteUser(command);
        if (command.startsWith("ADMIN CHANGE PASSWORD "))
            return runAdminChangePassword(command);
        if (command.startsWith("ADMIN RESET PASSWORD "))
            return runAdminResetPassword(command);
        if (command.startsWith("ADMIN GRANT SERVER ADMIN "))
            return runAdminGrantServerAdmin(command);
        if (command.startsWith("ADMIN REVOKE SERVER ADMIN "))
            return runAdminRevokeServerAdmin(command);
        if (command.startsWith("ADMIN GRANT ADMIN "))
            return runAdminGrantDBAdmin(command);
        if (command.startsWith("ADMIN REVOKE ADMIN "))
            return runAdminRevokeDBAdmin(command);
        if (command.startsWith("ADMIN GRANT READ "))
            return runAdminGrantDBRead(command);
        if (command.startsWith("ADMIN REVOKE READ "))
            return runAdminRevokeDBRead(command);
        if (command.startsWith("ADMIN GRANT READ "))
            return runAdminGrantDBWrite(command);
        if (command.startsWith("ADMIN REVOKE READ "))
            return runAdminRevokeDBWrite(command);

        if (command.equals("ADMIN LIST DATABASES"))
            return runAdminListDatabases();
        if (command.startsWith("ADMIN CREATE DATABASE "))
            return runAdminCreateDatabase(command);
        if (command.startsWith("ADMIN DROP DATABASE "))
            return runAdminDropDatabase(command);

        return new ProtocolReplyFailure("UNRECOGNIZED COMMAND");
    }

    /**
     * Request the listing of the users on this server
     * Expected command line: ADMIN LIST USERS
     *
     * @return The protocol reply
     */
    private ProtocolReply runAdminListUsers() {
        return controller.getUsers(user);
    }

    /**
     * Request the creation of a new user.
     * Expected command line: ADMIN CREATE USER login password
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminCreateUser(String line) {
        line = line.substring("ADMIN CREATE USER ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String password = line.substring(index + 1);
        return controller.createUser(user, login, password);
    }

    /**
     * Request the deletion of a user.
     * Expected command line: ADMIN DELETE USER login
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminDeleteUser(String line) {
        String login = line.substring("ADMIN DELETE USER ".length());
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        return controller.deleteUser(user, ((ProtocolReplyResult<User>) target).getData());
    }

    /**
     * Request the change of the current user password
     * Expected command line: ADMIN CHANGE PASSWORD password
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminChangePassword(String line) {
        String password = line.substring("ADMIN CHANGE PASSWORD ".length());
        return controller.changePassword(user, password);
    }

    /**
     * Request the reset of the password of another user
     * Expected command line: ADMIN RESET PASSWORD login password
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminResetPassword(String line) {
        line = line.substring("ADMIN RESET PASSWORD ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String password = line.substring(index + 1);
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        return controller.resetPassword(user, ((ProtocolReplyResult<User>) target).getData(), password);
    }

    /**
     * Request the grant of administrator privilege to this server
     * Expected command line: ADMIN GRANT SERVER ADMIN login
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminGrantServerAdmin(String line) {
        String login = line.substring("ADMIN GRANT SERVER ADMIN ".length());
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        return controller.grantServerAdmin(user, ((ProtocolReplyResult<User>) target).getData());
    }

    /**
     * Request the revocation of administrator privilege to this server
     * Expected command line: ADMIN GRANT SERVER ADMIN login
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminRevokeServerAdmin(String line) {
        String login = line.substring("ADMIN REVOKE SERVER ADMIN ".length());
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        return controller.revokeServerAdmin(user, ((ProtocolReplyResult<User>) target).getData());
    }

    /**
     * Request the grant of administrator privilege on a database
     * Expected command line: ADMIN GRANT ADMIN login database
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminGrantDBAdmin(String line) {
        line = line.substring("ADMIN GRANT ADMIN ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        ProtocolReply database = controller.getDatabase(user, dbName);
        if (!database.isSuccess())
            return database;
        return controller.grantDBAdmin(user, ((ProtocolReplyResult<User>) target).getData(), ((ProtocolReplyResult<Database>) database).getData());
    }

    /**
     * Request the revocation of administrator privilege on a database
     * Expected command line: ADMIN REVOKE ADMIN login database
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminRevokeDBAdmin(String line) {
        line = line.substring("ADMIN REVOKE ADMIN ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        ProtocolReply database = controller.getDatabase(user, dbName);
        if (!database.isSuccess())
            return database;
        return controller.revokeDBAdmin(user, ((ProtocolReplyResult<User>) target).getData(), ((ProtocolReplyResult<Database>) database).getData());
    }

    /**
     * Request the grant of reading privilege on a database
     * Expected command line: ADMIN GRANT READ login database
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminGrantDBRead(String line) {
        line = line.substring("ADMIN GRANT READ ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        ProtocolReply database = controller.getDatabase(user, dbName);
        if (!database.isSuccess())
            return database;
        return controller.grantDBRead(user, ((ProtocolReplyResult<User>) target).getData(), ((ProtocolReplyResult<Database>) database).getData());
    }

    /**
     * Request the revocation of reading privilege on a database
     * Expected command line: ADMIN REVOKE READ login database
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminRevokeDBRead(String line) {
        line = line.substring("ADMIN REVOKE READ ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        ProtocolReply database = controller.getDatabase(user, dbName);
        if (!database.isSuccess())
            return database;
        return controller.revokeDBRead(user, ((ProtocolReplyResult<User>) target).getData(), ((ProtocolReplyResult<Database>) database).getData());
    }

    /**
     * Request the grant of writing privilege on a database
     * Expected command line: ADMIN GRANT WRITE login database
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminGrantDBWrite(String line) {
        line = line.substring("ADMIN GRANT WRITE ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        ProtocolReply database = controller.getDatabase(user, dbName);
        if (!database.isSuccess())
            return database;
        return controller.grantDBWrite(user, ((ProtocolReplyResult<User>) target).getData(), ((ProtocolReplyResult<Database>) database).getData());
    }

    /**
     * Request the revocation of writing privilege on a database
     * Expected command line: ADMIN REVOKE WRITE login database
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminRevokeDBWrite(String line) {
        line = line.substring("ADMIN REVOKE WRITE ".length());
        int index = line.indexOf(' ');
        if (index == -1)
            return new ProtocolReplyFailure("INVALID COMMAND");
        String login = line.substring(0, index);
        String dbName = line.substring(index + 1);
        ProtocolReply target = controller.getUser(user, login);
        if (!target.isSuccess())
            return target;
        ProtocolReply database = controller.getDatabase(user, dbName);
        if (!database.isSuccess())
            return database;
        return controller.revokeDBWrite(user, ((ProtocolReplyResult<User>) target).getData(), ((ProtocolReplyResult<Database>) database).getData());
    }

    /**
     * Request the listing of the databases on this server
     * Expected command line: ADMIN LIST DATABASES
     *
     * @return The protocol reply
     */
    private ProtocolReply runAdminListDatabases() {
        return controller.getDatabases(user);
    }

    /**
     * Request the creation of a new database
     * Expected command line: ADMIN CREATE DATABASE name
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminCreateDatabase(String line) {
        String name = line.substring("ADMIN CREATE DATABASE ".length());
        return controller.createDatabase(user, name);
    }

    /**
     * Request the deletion of a database
     * Expected command line: ADMIN DROP DATABASE name
     *
     * @param line The command line
     * @return The protocol reply
     */
    private ProtocolReply runAdminDropDatabase(String line) {
        String name = line.substring("ADMIN DROP DATABASE ".length());
        ProtocolReply target = controller.getDatabase(user, name);
        if (!target.isSuccess() || !(target instanceof ProtocolReplyResult))
            return target;
        return controller.dropDatabase(user, ((ProtocolReplyResult<Database>) target).getData());
    }
}
