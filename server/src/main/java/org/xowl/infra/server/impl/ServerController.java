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

package org.xowl.infra.server.impl;

import org.mindrot.jbcrypt.BCrypt;
import org.xowl.infra.server.api.XOWLDatabase;
import org.xowl.infra.server.api.XOWLPrivilege;
import org.xowl.infra.server.base.BaseDatabasePrivileges;
import org.xowl.infra.server.base.BaseUserPrivileges;
import org.xowl.infra.server.standalone.Program;
import org.xowl.infra.server.xsp.*;
import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.utils.logging.Logging;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

/**
 * Implements the server API for this server
 *
 * @author Laurent Wouters
 */
public class ServerController implements Closeable {
    /**
     * The data about a client
     */
    private static class ClientLogin {
        /**
         * The number of failed attempt
         */
        public int failedAttempt = 0;
        /**
         * The timestamp of the ban
         */
        public long banTimeStamp = -1;
    }

    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The currently hosted repositories
     */
    private final Map<String, ServerDatabase> databases;
    /**
     * The administration database
     */
    private final ServerDatabase adminDB;
    /**
     * The map of clients with failed login attempts
     */
    private final Map<InetAddress, ClientLogin> clients;
    /**
     * The map of current users on this server
     */
    private final Map<String, ServerUser> users;

    /**
     * Initializes this controller
     *
     * @param configuration The current configuration
     * @throws IOException When the location cannot be accessed
     */
    public ServerController(ServerConfiguration configuration) throws IOException {
        this.configuration = configuration;
        this.databases = new HashMap<>();
        boolean isEmpty = true;
        if (configuration.getDatabasesFolder().exists()) {
            String[] children = configuration.getDatabasesFolder().list();
            isEmpty = children == null || children.length == 0;
        }
        Logging.getDefault().info("Initializing the controller");
        adminDB = new ServerDatabase(configuration, new File(configuration.getDatabasesFolder(), configuration.getAdminDBName()));
        databases.put(configuration.getAdminDBName(), adminDB);
        clients = new HashMap<>();
        users = new HashMap<>();
        if (isEmpty) {
            adminDB.getProxy().setValue(Vocabulary.rdfType, adminDB.getRepository().resolveProxy(Schema.ADMIN_DATABASE));
            adminDB.getProxy().setValue(Schema.ADMIN_NAME, configuration.getAdminDBName());
            adminDB.getProxy().setValue(Schema.ADMIN_LOCATION, ".");
            ServerUser admin = doCreateUser(configuration.getAdminDefaultUser(), configuration.getAdminDefaultPassword());
            admin.getProxy().addValue(Schema.ADMIN_ADMINOF, adminDB.getProxy());
            adminDB.getRepository().getStore().commit();
        } else {
            ProxyObject classDB = adminDB.getRepository().resolveProxy(Schema.ADMIN_DATABASE);
            for (ProxyObject poDB : classDB.getInstances()) {
                if (poDB == adminDB.getProxy())
                    continue;
                Logging.getDefault().info("Found database " + poDB.getIRIString());
                String name = (String) poDB.getDataValue(Schema.ADMIN_NAME);
                String location = (String) poDB.getDataValue(Schema.ADMIN_LOCATION);
                try {
                    ServerDatabase db = new ServerDatabase(configuration, new File(configuration.getDatabasesFolder(), location), poDB);
                    databases.put(name, db);
                    Logging.getDefault().info("Loaded database " + poDB.getIRIString() + " as " + name);
                } catch (IOException exception) {
                    // do nothing, this exception is reported by the db Logging.getDefault()
                    Logging.getDefault().error("Failed to load database " + poDB.getIRIString() + " as " + name);
                }
            }
        }
        Logging.getDefault().info("Controller is ready");
    }

    @Override
    public void close() throws IOException {
        Logging.getDefault().info("Closing all databases ...");
        for (Map.Entry<String, ServerDatabase> entry : databases.entrySet()) {
            Logging.getDefault().info("Closing database " + entry.getKey());
            try {
                entry.getValue().close();
            } catch (IOException exception) {
                Logging.getDefault().error(exception);
            }
            Logging.getDefault().info("Closed database " + entry.getKey());
        }
        databases.clear();
        Logging.getDefault().info("All databases closed");
    }

    /**
     * Request the server to shutdown
     */
    protected void onRequestShutdown() {
    }

    /**
     * Request the server to restart
     */
    protected void onRequestRestart() {
    }

    /**
     * Gets the requesting client for the specified login
     *
     * @param login The login of a user
     * @return The client
     */
    public ServerUser getPrincipal(String login) {
        return doGetUser(login);
    }

    /**
     * Login a user
     *
     * @param client   The client trying to login
     * @param login    The user to log in
     * @param password The user password
     * @return The protocol reply, or null if the client is banned
     */
    public XSPReply login(InetAddress client, String login, String password) {
        if (isBanned(client))
            return null;
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            boolean banned = onLoginFailure(client);
            Logging.getDefault().info("Login failure for " + login + " from " + client.toString());
            return banned ? null : XSPReplyFailure.instance();
        }
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        String hash = null;
        synchronized (adminDB) {
            proxy = adminDB.getRepository().getProxy(userIRI);
            if (proxy != null)
                hash = (String) proxy.getDataValue(Schema.ADMIN_PASSWORD);
        }
        if (proxy == null) {
            boolean banned = onLoginFailure(client);
            Logging.getDefault().info("Login failure for " + login + " from " + client.toString());
            return banned ? null : XSPReplyFailure.instance();
        }
        if (!BCrypt.checkpw(password, hash)) {
            boolean banned = onLoginFailure(client);
            Logging.getDefault().info("Login failure for " + login + " from " + client.toString());
            return banned ? null : XSPReplyFailure.instance();
        } else {
            synchronized (clients) {
                clients.remove(client);
            }
            ServerUser user;
            synchronized (users) {
                user = users.get(login);
                if (user == null) {
                    user = new ServerUser(proxy);
                    users.put(login, user);
                }
            }
            return new XSPReplyResult<>(user);
        }
    }

    /**
     * Request the server shutdown
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public XSPReply serverShutdown(ServerUser client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        onRequestShutdown();
        return XSPReplySuccess.instance();
    }

    /**
     * Request the server restart
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public XSPReply serverRestart(ServerUser client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        onRequestRestart();
        return XSPReplySuccess.instance();
    }

    /**
     * Request the info about a user
     *
     * @param client The requesting client
     * @param login  The requested user name
     * @return The protocol reply
     */
    public XSPReply getUser(ServerUser client, String login) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (client.getName().equals(login))
            return new XSPReplyResult<>(client);
        ServerUser user = doGetUser(login);
        if (user == null)
            return XSPReplyNotFound.instance();
        return new XSPReplyResult<>(user);
    }

    /**
     * Requests the list of users on this server
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public XSPReply getUsers(ServerUser client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        Collection<ServerUser> result = new ArrayList<>();
        synchronized (adminDB) {
            ProxyObject classUser = adminDB.getRepository().resolveProxy(Schema.ADMIN_USER);
            for (ProxyObject poUser : classUser.getInstances()) {
                String name = (String) poUser.getDataValue(Schema.ADMIN_NAME);
                synchronized (users) {
                    ServerUser user = users.get(name);
                    if (user == null) {
                        user = new ServerUser(poUser);
                        users.put(name, user);
                    }
                    result.add(user);
                }
            }
        }
        return new XSPReplyResultCollection<>(result);
    }

    /**
     * Request the creation of a user
     *
     * @param client   The requesting client
     * @param login    The login for the new user
     * @param password The password for the new user
     * @return The protocol reply
     */
    public XSPReply createUser(ServerUser client, String login, String password) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        if (!login.matches("[_a-zA-Z0-9]+"))
            return new XSPReplyFailure("Login does not meet requirements ([_a-zA-Z0-9]+)");
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return new XSPReplyFailure("Password does not meet requirements (min length " + configuration.getSecurityMinPasswordLength() + ")");
        ServerUser user = doCreateUser(login, password);
        if (user == null)
            return new XSPReplyFailure("User already exists");
        return new XSPReplyResult<>(user);
    }

    /**
     * Deletes a user
     *
     * @param client   The requesting client
     * @param toDelete The user to delete
     * @return The protocol reply
     */
    public XSPReply deleteUser(ServerUser client, String toDelete) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        ServerUser user = doGetUser(toDelete);
        if (user == null)
            return XSPReplyNotFound.instance();
        synchronized (users) {
            user = users.remove(toDelete);
        }
        if (user == null)
            return XSPReplyNotFound.instance();
        synchronized (adminDB) {
            user.getProxy().delete();
            adminDB.getRepository().getStore().commit();
        }
        return XSPReplySuccess.instance();
    }

    /**
     * Changes the password of the requesting client
     *
     * @param client   The requesting client
     * @param password The new password
     * @return The protocol reply
     */
    public XSPReply changePassword(ServerUser client, String password) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        return doResetPassword(client.getName(), password);
    }

    /**
     * Resets the password for another user
     *
     * @param client   The requesting client
     * @param target   The target user
     * @param password The new password
     * @return The protocol reply
     */
    public XSPReply resetPassword(ServerUser client, String target, String password) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (client.getName().equals(target) || checkIsServerAdmin(client))
            return doResetPassword(client.getName(), password);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the privileges assigned to a user
     *
     * @param client The requesting client
     * @param name   The target user
     * @return The protocol reply
     */
    public XSPReply getPrivilegesUser(ServerUser client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (client.getName().equals(name) || checkIsServerAdmin(client)) {
            ServerUser user = doGetUser(name);
            if (user == null)
                return XSPReplyNotFound.instance();
            BaseUserPrivileges result = new BaseUserPrivileges(checkIsServerAdmin(user));
            for (ProxyObject value : user.getProxy().getObjectValues(Schema.ADMIN_ADMINOF)) {
                ServerDatabase database = doGetDatabase(value);
                result.add(database, XOWLPrivilege.ADMIN);
            }
            for (ProxyObject value : user.getProxy().getObjectValues(Schema.ADMIN_CANWRITE)) {
                ServerDatabase database = doGetDatabase(value);
                result.add(database, XOWLPrivilege.WRITE);
            }
            for (ProxyObject value : user.getProxy().getObjectValues(Schema.ADMIN_CANREAD)) {
                ServerDatabase database = doGetDatabase(value);
                result.add(database, XOWLPrivilege.READ);
            }
            return new XSPReplyResult<>(result);
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Grants server administrator privilege
     *
     * @param client The requesting client
     * @param name   The target user
     * @return The protocol reply
     */
    public XSPReply grantServerAdmin(ServerUser client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        ServerUser target = doGetUser(name);
        if (target == null)
            return XSPReplyNotFound.instance();
        boolean success = doChangeUserPrivilege(target.getProxy(), adminDB.getProxy(), Schema.ADMIN_ADMINOF, true);
        return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
    }

    /**
     * Revokes server administrator privilege
     *
     * @param client The requesting client
     * @param name   The target user
     * @return The protocol reply
     */
    public XSPReply revokeServerAdmin(ServerUser client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        ServerUser target = doGetUser(name);
        if (target == null)
            return XSPReplyNotFound.instance();
        boolean success = doChangeUserPrivilege(target.getProxy(), adminDB.getProxy(), Schema.ADMIN_ADMINOF, false);
        return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
    }

    /**
     * Grants a privilege on a database
     *
     * @param client    The requesting client
     * @param user      The target user
     * @param database  The target database
     * @param privilege The privilege
     * @return The protocol reply
     */
    public XSPReply grantDB(ServerUser client, String user, String database, int privilege) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            ServerUser target = doGetUser(user);
            if (target == null)
                return XSPReplyNotFound.instance();
            String priv = (privilege == XOWLPrivilege.ADMIN ? Schema.ADMIN_ADMINOF : (privilege == XOWLPrivilege.WRITE ? Schema.ADMIN_CANWRITE : (privilege == XOWLPrivilege.READ ? Schema.ADMIN_CANREAD : null)));
            if (priv == null)
                return XSPReplyNotFound.instance();
            boolean success = doChangeUserPrivilege(target.getProxy(), db.getProxy(), priv, true);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Revokes a privilege on a database
     *
     * @param client    The requesting client
     * @param user      The target user
     * @param database  The target database
     * @param privilege The privilege
     * @return The protocol reply
     */
    public XSPReply revokeDB(ServerUser client, String user, String database, int privilege) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            ServerUser target = doGetUser(user);
            if (target == null)
                return XSPReplyNotFound.instance();
            String priv = (privilege == XOWLPrivilege.ADMIN ? Schema.ADMIN_ADMINOF : (privilege == XOWLPrivilege.WRITE ? Schema.ADMIN_CANWRITE : (privilege == XOWLPrivilege.READ ? Schema.ADMIN_CANREAD : null)));
            if (priv == null)
                return XSPReplyNotFound.instance();
            boolean success = doChangeUserPrivilege(target.getProxy(), db.getProxy(), priv, false);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the info about a database
     *
     * @param client The requesting client
     * @param name   The target database
     * @return The protocol reply
     */
    public XSPReply getDatabase(ServerUser client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(name);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD)) {
            return new XSPReplyResult<>(db);
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the databases on this server
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public XSPReply getDatabases(ServerUser client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        Collection<XOWLDatabase> result = new ArrayList<>();
        synchronized (databases) {
            if (checkIsServerAdmin(client)) {
                result.addAll(databases.values());
                return new XSPReplyResultCollection<>(result);
            }
            for (ServerDatabase database : databases.values()) {
                if (checkIsServerAdmin(client)
                        || checkIsDBAdmin(client, database)
                        || checkIsAllowed(client.getProxy(), database.getProxy(), Schema.ADMIN_CANREAD)
                        || checkIsAllowed(client.getProxy(), database.getProxy(), Schema.ADMIN_CANWRITE))
                    result.add(database);
            }
        }
        return new XSPReplyResultCollection<>(result);
    }

    /**
     * Creates a new database
     *
     * @param client The requesting client
     * @param name   The name of the new database
     * @return The protocol reply
     */
    public XSPReply createDatabase(ServerUser client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        if (!name.matches("[_a-zA-Z0-9]+"))
            return new XSPReplyFailure("Database name does not match requirements ([_a-zA-Z0-9]+)");

        synchronized (databases) {
            ServerDatabase result = databases.get(name);
            if (result != null)
                return new XSPReplyFailure("The database already exists");
            File folder = new File(configuration.getDatabasesFolder(), name);
            try {
                ProxyObject proxy = adminDB.getRepository().resolveProxy(Schema.ADMIN_GRAPH_DBS + name);
                proxy.setValue(Vocabulary.rdfType, adminDB.getRepository().resolveProxy(Schema.ADMIN_DATABASE));
                proxy.setValue(Schema.ADMIN_NAME, name);
                proxy.setValue(Schema.ADMIN_LOCATION, name);
                result = new ServerDatabase(configuration, folder, proxy);
                adminDB.getRepository().getStore().commit();
                result.getRepository().getStore().commit();
                databases.put(name, result);
                return new XSPReplyResult<>(result);
            } catch (IOException exception) {
                return XSPReplyFailure.instance();
            }
        }
    }

    /**
     * Drops a database
     *
     * @param client The requesting client
     * @param name   The target database
     * @return The protocol reply
     */
    public XSPReply dropDatabase(ServerUser client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        synchronized (databases) {
            ServerDatabase database = databases.remove(name);
            if (database == null)
                return XSPReplyNotFound.instance();
            File folder = new File(configuration.getDatabasesFolder(), (String) database.getProxy().getDataValue(Schema.ADMIN_LOCATION));
            try {
                database.close();
            } catch (IOException exception) {
                Logging.getDefault().error(exception);
            }
            if (!Program.delete(folder)) {
                Logging.getDefault().error("Failed to delete " + folder.getAbsolutePath());
            }
            database.getProxy().delete();
            adminDB.getRepository().getStore().commit();
        }
        return XSPReplySuccess.instance();
    }

    /**
     * Gets the privileges on a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply getPrivilegesDB(ServerUser client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            BaseDatabasePrivileges result = new BaseDatabasePrivileges();
            for (ProxyObject value : db.getProxy().getObjectsFrom(Schema.ADMIN_ADMINOF)) {
                ServerUser user = doGetUser(value);
                result.add(user, XOWLPrivilege.ADMIN);
            }
            for (ProxyObject value : db.getProxy().getObjectsFrom(Schema.ADMIN_CANWRITE)) {
                ServerUser user = doGetUser(value);
                result.add(user, XOWLPrivilege.WRITE);
            }
            for (ProxyObject value : db.getProxy().getObjectsFrom(Schema.ADMIN_CANREAD)) {
                ServerUser user = doGetUser(value);
                result.add(user, XOWLPrivilege.READ);
            }
            return new XSPReplyResult<>(result);
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Executes SPARQL commands
     *
     * @param client      The requesting client
     * @param database    The target database
     * @param sparql      The SPARQL command(s)
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     * @return The protocol reply
     */
    public XSPReply sparql(ServerUser client, String database, String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD)) {
            return db.sparql(sparql, defaultIRIs, namedIRIs);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Gets the entailment regime
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply getEntailmentRegime(ServerUser client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD))
            return db.getEntailmentRegime();
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Sets the entailment regime
     *
     * @param client   The requesting client
     * @param database The target database
     * @param regime   The entailment regime
     * @return The protocol reply
     */
    public XSPReply setEntailmentRegime(ServerUser client, String database, String regime) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.setEntailmentRegime(regime);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the rule for the specified name
     *
     * @param client   The requesting client
     * @param database The target database
     * @param name     The name (IRI) of a rule
     * @return The protocol reply
     */
    public XSPReply getRule(ServerUser client, String database, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD))
            return db.getRule(name);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the rules in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply getRules(ServerUser client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD))
            return db.getRules();
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Adds a new rule to this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param content  The rule's content
     * @param activate Whether to readily activate the rule
     * @return The protocol reply
     */
    public XSPReply addRule(ServerUser client, String database, String content, boolean activate) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.addRule(content, activate);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Removes a rule from this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule to remove
     * @return The protocol reply
     */
    public XSPReply removeRule(ServerUser client, String database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.removeRule(rule);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Activates an existing rule in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule to activate
     * @return The protocol reply
     */
    public XSPReply activateRule(ServerUser client, String database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.activateRule(rule);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Deactivates an existing rule in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule to deactivate
     * @return The protocol reply
     */
    public XSPReply deactivateRule(ServerUser client, String database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.deactivateRule(rule);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the matching status of a rule in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule to inquire
     * @return The protocol reply
     */
    public XSPReply getRuleStatus(ServerUser client, String database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD))
            return db.getRuleStatus(rule);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the stored procedure for the specified name
     *
     * @param client   The requesting client
     * @param database The target database
     * @param iri      The name (IRI) of a procedure
     * @return The protocol reply
     */
    public XSPReply getStoreProcedure(ServerUser client, String database, String iri) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD))
            return db.getStoreProcedure(iri);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the stored procedures in a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply getStoredProcedures(ServerUser client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD))
            return db.getStoredProcedures();
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Adds a stored procedure in a database
     *
     * @param client     The requesting client
     * @param database   The database that would store the procedure
     * @param iri        The name (IRI) of the procedure
     * @param sparql     The SPARQL definition of the procedure
     * @param parameters The parameters for this procedure
     * @return The protocol reply
     */
    public XSPReply addStoredProcedure(ServerUser client, String database, String iri, String sparql, Collection<String> parameters) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.addStoredProcedure(iri, sparql, parameters);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Removes a stored procedure from a database
     *
     * @param client    The requesting client
     * @param database  The target database
     * @param procedure The procedure to remove
     * @return The protocol reply
     */
    public XSPReply removeStoredProcedure(ServerUser client, String database, String procedure) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.removeStoredProcedure(procedure);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Executes a stored procedure
     *
     * @param client            The requesting client
     * @param database          The target database
     * @param procedure         The procedure to execute
     * @param contextDefinition The context for the execution
     * @return The protocol reply
     */
    public XSPReply executeStoredProcedure(ServerUser client, String database, String procedure, String contextDefinition) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.getProxy(), db.getProxy(), Schema.ADMIN_CANREAD))
            return db.executeStoredProcedure(procedure, contextDefinition);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Uploads some content to this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param syntax   The content's syntax
     * @param content  The content
     * @return The protocol reply
     */
    public XSPReply upload(ServerUser client, String database, String syntax, String content) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.upload(syntax, content);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the statistics for a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply getStatistics(ServerUser client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        ServerDatabase db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db))
            return db.getStatistics();
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets whether a client is banned
     *
     * @param client A client
     * @return Whether the client is banned
     */
    private boolean isBanned(InetAddress client) {
        synchronized (clients) {
            ClientLogin cl = clients.get(client);
            if (cl == null)
                return false;
            if (cl.banTimeStamp == -1)
                return false;
            long now = Calendar.getInstance().getTime().getTime();
            long diff = now - cl.banTimeStamp;
            if (diff < 1000 * configuration.getSecurityBanLength()) {
                // still banned
                return true;
            } else {
                // not banned anymore
                clients.remove(client);
                return false;
            }
        }
    }

    /**
     * Handles a login failure from a client
     *
     * @param client The client trying to login
     * @return Whether the failure resulted in the client being banned
     */
    private boolean onLoginFailure(InetAddress client) {
        synchronized (clients) {
            ClientLogin cl = clients.get(client);
            if (cl == null) {
                cl = new ClientLogin();
                clients.put(client, cl);
            }
            cl.failedAttempt++;
            if (client.equals(InetAddress.getLoopbackAddress())) {
                // the loopback client cannot be banned
                return false;
            }
            if (cl.failedAttempt >= configuration.getSecurityMaxLoginAttempt()) {
                // too much failure, ban this client for a while
                Logging.getDefault().info("Banned client " + client.toString() + " for " + configuration.getSecurityBanLength() + " seconds");
                cl.banTimeStamp = Calendar.getInstance().getTime().getTime();
                return true;
            }
            return false;
        }
    }

    /**
     * Realizes the creation of a user
     *
     * @param login    The login for the new user
     * @param password The password for the new user
     * @return The created user
     */
    private ServerUser doCreateUser(String login, String password) {
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        synchronized (adminDB) {
            proxy = adminDB.getRepository().getProxy(userIRI);
            if (proxy != null)
                return null;
            proxy = adminDB.getRepository().resolveProxy(userIRI);
            proxy.setValue(Vocabulary.rdfType, adminDB.getRepository().resolveProxy(Schema.ADMIN_USER));
            proxy.setValue(Schema.ADMIN_NAME, login);
            proxy.setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.getRepository().getStore().commit();
        }
        ServerUser result = new ServerUser(proxy);
        synchronized (users) {
            users.put(login, result);
        }
        return result;
    }

    /**
     * Gets the user for a login
     *
     * @param login A login
     * @return The user, or null if it is not found
     */
    private ServerUser doGetUser(String login) {
        synchronized (users) {
            ServerUser result = users.get(login);
            if (result != null)
                return result;
        }
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        String name = null;
        synchronized (adminDB) {
            proxy = adminDB.getRepository().getProxy(userIRI);
            if (proxy != null)
                name = (String) proxy.getDataValue(Schema.ADMIN_NAME);
        }
        if (proxy == null)
            return null;
        synchronized (users) {
            ServerUser result = users.get(name);
            if (result == null) {
                result = new ServerUser(proxy);
                users.put(name, result);
            }
            return result;
        }
    }

    /**
     * Resolves the user for the specified proxy object
     *
     * @param proxy A proxy
     * @return The user
     */
    private ServerUser doGetUser(ProxyObject proxy) {
        synchronized (users) {
            for (ServerUser user : users.values()) {
                if (user.getProxy() == proxy)
                    return user;
            }
            ServerUser user = new ServerUser(proxy);
            users.put(user.getName(), user);
            return user;
        }
    }

    /**
     * Resolves the database for the specified name
     *
     * @param name The name of a database
     * @return The database, or null if it is unknown
     */
    private ServerDatabase doGetDatabase(String name) {
        synchronized (databases) {
            return databases.get(name);
        }
    }

    /**
     * Resolves the database for the specified proxy object
     *
     * @param proxy A proxy
     * @return The database, or null if it is unknown
     */
    private ServerDatabase doGetDatabase(ProxyObject proxy) {
        synchronized (databases) {
            for (ServerDatabase db : databases.values()) {
                if (db.getProxy() == proxy)
                    return db;
            }
        }
        return null;
    }

    /**
     * Gets whether a user is granted a privilege on a database
     *
     * @param user      The user
     * @param database  The database
     * @param privilege The privilege
     * @return Whether the user is allowed
     */
    private boolean checkIsAllowed(ProxyObject user, ProxyObject database, String privilege) {
        Collection<ProxyObject> dbs;
        synchronized (adminDB) {
            dbs = user.getObjectValues(privilege);
        }
        return dbs.contains(database);
    }

    /**
     * Gets whether a user is an administrator of a database
     *
     * @param user     A user
     * @param database A database
     * @return Whether the user is an administrator of the database
     */
    private boolean checkIsDBAdmin(ServerUser user, ServerDatabase database) {
        return checkIsAllowed(user.getProxy(), database.getProxy(), Schema.ADMIN_ADMINOF);
    }

    /**
     * Gets whether a user is a server administrator
     *
     * @param user A user
     * @return Whether the user is a server administrator
     */
    private boolean checkIsServerAdmin(ServerUser user) {
        return checkIsAllowed(user.getProxy(), adminDB.getProxy(), Schema.ADMIN_ADMINOF);
    }

    /**
     * Resets the password of a user
     *
     * @param name     The user's name
     * @param password The new password
     * @return The protocol reply
     */
    private XSPReply doResetPassword(String name, String password) {
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return new XSPReplyFailure("Password does not meet requirements");
        ServerUser user = doGetUser(name);
        if (user == null)
            return XSPReplyNotFound.instance();
        synchronized (adminDB) {
            user.getProxy().removeAllValues(Schema.ADMIN_PASSWORD);
            user.getProxy().setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.getRepository().getStore().commit();
        }
        return XSPReplySuccess.instance();
    }

    /**
     * Change a user privilege on a database
     *
     * @param user      The user
     * @param database  The database
     * @param privilege The privilege
     * @param positive  Whether to add or remove the privilege
     * @return Whether the operation succeeded
     */
    private boolean doChangeUserPrivilege(ProxyObject user, ProxyObject database, String privilege, boolean positive) {
        synchronized (adminDB) {
            Collection<ProxyObject> dbs = user.getObjectValues(privilege);
            if (positive) {
                if (dbs.contains(database))
                    return false;
                user.addValue(privilege, database);
            } else {
                if (!dbs.contains(database))
                    return false;
                user.removeValue(privilege, database);
            }
            adminDB.getRepository().getStore().commit();
        }
        return true;
    }
}
