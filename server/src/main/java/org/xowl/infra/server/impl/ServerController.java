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
import org.xowl.infra.server.api.XOWLRule;
import org.xowl.infra.server.api.XOWLStoredProcedure;
import org.xowl.infra.server.base.BaseDatabasePrivileges;
import org.xowl.infra.server.base.BaseUserPrivileges;
import org.xowl.infra.server.xsp.*;
import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.RDFRuleStatus;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.BufferedLogger;
import org.xowl.infra.utils.logging.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

/**
 * A server controller is an entity that serves and controls a set of persisted databases.
 * The controller is responsible for enforcing the access policy.
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
     * The logger for this controller
     */
    protected final Logger logger;
    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The currently hosted repositories
     */
    private final Map<String, DatabaseImpl> databases;
    /**
     * The administration database
     */
    private final DatabaseImpl adminDB;
    /**
     * The map of current users on this server
     */
    private final Map<String, UserImpl> users;
    /**
     * The map of clients with failed login attempts
     */
    private final Map<InetAddress, ClientLogin> clients;

    /**
     * Initializes this controller
     *
     * @param logger        The logger for this controller
     * @param configuration The current configuration
     * @throws Exception When the location cannot be accessed
     */
    public ServerController(Logger logger, ServerConfiguration configuration) throws Exception {
        this.logger = logger;
        this.configuration = configuration;
        this.databases = new HashMap<>();
        boolean isEmpty = true;
        if (configuration.getDatabasesFolder().exists()) {
            String[] children = configuration.getDatabasesFolder().list();
            isEmpty = children == null || children.length == 0;
        }
        logger.info("Initializing the controller");
        DatabaseController dbCtrl = new DatabaseController(new File(configuration.getDatabasesFolder(), configuration.getAdminDBName()), configuration.getDefaultMaxThreads());
        adminDB = newDB(dbCtrl,
                dbCtrl.getRepository().resolveProxy(Schema.ADMIN_GRAPH_DBS + configuration.getAdminDBName()),
                configuration.getAdminDBName());
        databases.put(adminDB.getName(), adminDB);
        clients = new HashMap<>();
        users = new HashMap<>();
        if (isEmpty)
            initializeAdminDB();
        else
            initializeDatabases();
        logger.info("Controller is ready");
    }

    /**
     * Initializes the administration database
     */
    private void initializeAdminDB() {
        adminDB.proxy.setValue(Vocabulary.rdfType, adminDB.controller.getRepository().resolveProxy(Schema.ADMIN_DATABASE));
        adminDB.proxy.setValue(Schema.ADMIN_NAME, adminDB.getName());
        adminDB.proxy.setValue(Schema.ADMIN_LOCATION, ".");
        UserImpl admin = doCreateUser(configuration.getAdminDefaultUser(), configuration.getAdminDefaultPassword());
        admin.proxy.addValue(Schema.ADMIN_ADMINOF, adminDB.proxy);
        adminDB.controller.getRepository().getStore().commit();
    }

    /**
     * Initializes the databases managed by this controller
     */
    private void initializeDatabases() {
        ProxyObject classDB = adminDB.controller.getRepository().resolveProxy(Schema.ADMIN_DATABASE);
        for (ProxyObject poDB : classDB.getInstances()) {
            if (poDB == adminDB.proxy)
                continue;
            logger.info("Found database " + poDB.getIRIString());
            String name = (String) poDB.getDataValue(Schema.ADMIN_NAME);
            String location = (String) poDB.getDataValue(Schema.ADMIN_LOCATION);
            try {
                DatabaseController controller = new DatabaseController(new File(configuration.getDatabasesFolder(), location), configuration.getDefaultMaxThreads());
                DatabaseImpl db = newDB(controller, poDB);
                databases.put(db.getName(), db);
                logger.info("Loaded database " + poDB.getIRIString() + " as " + db.getName());
            } catch (Exception exception) {
                // do nothing, this exception is reported by the db logger
                logger.error("Failed to load database " + poDB.getIRIString() + " as " + name);
            }
        }
    }

    /**
     * Creates a new database
     *
     * @param controller The associated database controller
     * @param proxy      The proxy object that represents the database in the administration database
     */
    protected DatabaseImpl newDB(DatabaseController controller, ProxyObject proxy) {
        return new DatabaseImpl(controller, proxy);
    }

    /**
     * Creates a new database
     *
     * @param controller The associated database controller
     * @param proxy      The proxy object that represents the database in the administration database
     * @param name       The name of the database
     */
    protected DatabaseImpl newDB(DatabaseController controller, ProxyObject proxy, String name) {
        return new DatabaseImpl(controller, proxy, name);
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

    @Override
    public void close() throws IOException {
        logger.info("Closing all databases ...");
        for (Map.Entry<String, DatabaseImpl> entry : databases.entrySet()) {
            logger.info("Closing database " + entry.getKey());
            try {
                entry.getValue().controller.close();
            } catch (IOException exception) {
                logger.error(exception);
            }
            logger.info("Closed database " + entry.getKey());
        }
        databases.clear();
        logger.info("All databases closed");
    }

    /**
     * Gets the user for the specified login
     *
     * @param login The login of a user
     * @return The user, or null if there is none for this login
     */
    public UserImpl getPrincipal(String login) {
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
            logger.info("Login failure for " + login + " from " + client.toString());
            return banned ? null : XSPReplyFailure.instance();
        }
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        String hash = null;
        synchronized (adminDB) {
            proxy = adminDB.controller.getRepository().getProxy(userIRI);
            if (proxy != null)
                hash = (String) proxy.getDataValue(Schema.ADMIN_PASSWORD);
        }
        if (proxy == null) {
            boolean banned = onLoginFailure(client);
            logger.info("Login failure for " + login + " from " + client.toString());
            return banned ? null : XSPReplyFailure.instance();
        }
        if (!BCrypt.checkpw(password, hash)) {
            boolean banned = onLoginFailure(client);
            logger.info("Login failure for " + login + " from " + client.toString());
            return banned ? null : XSPReplyFailure.instance();
        } else {
            synchronized (clients) {
                clients.remove(client);
            }
            UserImpl user;
            synchronized (users) {
                user = users.get(login);
                if (user == null) {
                    user = new UserImpl(proxy);
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
    public XSPReply serverShutdown(UserImpl client) {
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
    public XSPReply serverRestart(UserImpl client) {
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
    public XSPReply getUser(UserImpl client, String login) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (client.getName().equals(login))
            return new XSPReplyResult<>(client);
        UserImpl user = doGetUser(login);
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
    public XSPReply getUsers(UserImpl client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        Collection<UserImpl> result = new ArrayList<>();
        synchronized (adminDB) {
            ProxyObject classUser = adminDB.controller.getRepository().resolveProxy(Schema.ADMIN_USER);
            for (ProxyObject poUser : classUser.getInstances()) {
                String name = (String) poUser.getDataValue(Schema.ADMIN_NAME);
                synchronized (users) {
                    UserImpl user = users.get(name);
                    if (user == null) {
                        user = new UserImpl(poUser);
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
    public XSPReply createUser(UserImpl client, String login, String password) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        if (!login.matches("[_a-zA-Z0-9]+"))
            return new XSPReplyFailure("Login does not meet requirements ([_a-zA-Z0-9]+)");
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return new XSPReplyFailure("Password does not meet requirements (min length " + configuration.getSecurityMinPasswordLength() + ")");
        UserImpl user = doCreateUser(login, password);
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
    public XSPReply deleteUser(UserImpl client, String toDelete) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        UserImpl user = doGetUser(toDelete);
        if (user == null)
            return XSPReplyNotFound.instance();
        synchronized (users) {
            user = users.remove(toDelete);
        }
        if (user == null)
            return XSPReplyNotFound.instance();
        synchronized (adminDB) {
            user.proxy.delete();
            adminDB.controller.getRepository().getStore().commit();
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
    public XSPReply changePassword(UserImpl client, String password) {
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
    public XSPReply resetPassword(UserImpl client, String target, String password) {
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
    public XSPReply getPrivilegesUser(UserImpl client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (client.getName().equals(name) || checkIsServerAdmin(client)) {
            UserImpl user = doGetUser(name);
            if (user == null)
                return XSPReplyNotFound.instance();
            BaseUserPrivileges result = new BaseUserPrivileges(checkIsServerAdmin(user));
            for (ProxyObject value : user.proxy.getObjectValues(Schema.ADMIN_ADMINOF)) {
                DatabaseImpl database = doGetDatabase(value);
                result.add(database, XOWLPrivilege.ADMIN);
            }
            for (ProxyObject value : user.proxy.getObjectValues(Schema.ADMIN_CANWRITE)) {
                DatabaseImpl database = doGetDatabase(value);
                result.add(database, XOWLPrivilege.WRITE);
            }
            for (ProxyObject value : user.proxy.getObjectValues(Schema.ADMIN_CANREAD)) {
                DatabaseImpl database = doGetDatabase(value);
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
    public XSPReply grantServerAdmin(UserImpl client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        UserImpl target = doGetUser(name);
        if (target == null)
            return XSPReplyNotFound.instance();
        boolean success = doChangeUserPrivilege(target.proxy, adminDB.proxy, Schema.ADMIN_ADMINOF, true);
        return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
    }

    /**
     * Revokes server administrator privilege
     *
     * @param client The requesting client
     * @param name   The target user
     * @return The protocol reply
     */
    public XSPReply revokeServerAdmin(UserImpl client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        UserImpl target = doGetUser(name);
        if (target == null)
            return XSPReplyNotFound.instance();
        boolean success = doChangeUserPrivilege(target.proxy, adminDB.proxy, Schema.ADMIN_ADMINOF, false);
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
    public XSPReply grantDB(UserImpl client, String user, String database, int privilege) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            UserImpl target = doGetUser(user);
            if (target == null)
                return XSPReplyNotFound.instance();
            String priv = (privilege == XOWLPrivilege.ADMIN ? Schema.ADMIN_ADMINOF : (privilege == XOWLPrivilege.WRITE ? Schema.ADMIN_CANWRITE : (privilege == XOWLPrivilege.READ ? Schema.ADMIN_CANREAD : null)));
            if (priv == null)
                return XSPReplyNotFound.instance();
            boolean success = doChangeUserPrivilege(target.proxy, db.proxy, priv, true);
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
    public XSPReply revokeDB(UserImpl client, String user, String database, int privilege) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            UserImpl target = doGetUser(user);
            if (target == null)
                return XSPReplyNotFound.instance();
            String priv = (privilege == XOWLPrivilege.ADMIN ? Schema.ADMIN_ADMINOF : (privilege == XOWLPrivilege.WRITE ? Schema.ADMIN_CANWRITE : (privilege == XOWLPrivilege.READ ? Schema.ADMIN_CANREAD : null)));
            if (priv == null)
                return XSPReplyNotFound.instance();
            boolean success = doChangeUserPrivilege(target.proxy, db.proxy, priv, false);
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
    public XSPReply getDatabase(UserImpl client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(name);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD)) {
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
    public XSPReply getDatabases(UserImpl client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        Collection<XOWLDatabase> result = new ArrayList<>();
        synchronized (databases) {
            if (checkIsServerAdmin(client)) {
                result.addAll(databases.values());
                return new XSPReplyResultCollection<>(result);
            }
            for (DatabaseImpl database : databases.values()) {
                if (checkIsServerAdmin(client)
                        || checkIsDBAdmin(client, database)
                        || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)
                        || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE))
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
    public XSPReply createDatabase(UserImpl client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        if (!name.matches("[_a-zA-Z0-9]+"))
            return new XSPReplyFailure("Database name does not match requirements ([_a-zA-Z0-9]+)");

        synchronized (databases) {
            DatabaseImpl result = databases.get(name);
            if (result != null)
                return new XSPReplyFailure("The database already exists");
            File folder = new File(configuration.getDatabasesFolder(), name);
            try {
                ProxyObject proxy = adminDB.controller.getRepository().resolveProxy(Schema.ADMIN_GRAPH_DBS + name);
                proxy.setValue(Vocabulary.rdfType, adminDB.controller.getRepository().resolveProxy(Schema.ADMIN_DATABASE));
                proxy.setValue(Schema.ADMIN_NAME, name);
                proxy.setValue(Schema.ADMIN_LOCATION, name);
                DatabaseController controller = new DatabaseController(folder, configuration.getDefaultMaxThreads());
                result = newDB(controller, proxy);
                adminDB.controller.getRepository().getStore().commit();
                result.controller.getRepository().getStore().commit();
                databases.put(name, result);
                return new XSPReplyResult<>(result);
            } catch (Exception exception) {
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
    public XSPReply dropDatabase(UserImpl client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        synchronized (databases) {
            DatabaseImpl database = databases.remove(name);
            if (database == null)
                return XSPReplyNotFound.instance();
            File folder = new File(configuration.getDatabasesFolder(), (String) database.proxy.getDataValue(Schema.ADMIN_LOCATION));
            try {
                database.controller.close();
            } catch (IOException exception) {
                logger.error(exception);
            }
            if (!Files.deleteFolder(folder)) {
                logger.error("Failed to delete " + folder.getAbsolutePath());
            }
            database.proxy.delete();
            adminDB.controller.getRepository().getStore().commit();
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
    public XSPReply getPrivilegesDB(UserImpl client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            BaseDatabasePrivileges result = new BaseDatabasePrivileges();
            for (ProxyObject value : db.proxy.getObjectsFrom(Schema.ADMIN_ADMINOF)) {
                UserImpl user = doGetUser(value);
                result.add(user, XOWLPrivilege.ADMIN);
            }
            for (ProxyObject value : db.proxy.getObjectsFrom(Schema.ADMIN_CANWRITE)) {
                UserImpl user = doGetUser(value);
                result.add(user, XOWLPrivilege.WRITE);
            }
            for (ProxyObject value : db.proxy.getObjectsFrom(Schema.ADMIN_CANREAD)) {
                UserImpl user = doGetUser(value);
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
    public XSPReply sparql(UserImpl client, String database, String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        boolean canWrite = checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE);
        if (canWrite || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD)) {
            Result result = db.controller.sparql(sparql, defaultIRIs, namedIRIs, !canWrite);
            if (result.isFailure())
                return new XSPReplyFailure(((ResultFailure) result).getMessage());
            return new XSPReplyResult<>(result);
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
    public XSPReply getEntailmentRegime(UserImpl client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD))
            return new XSPReplyResult<>(db.controller.getEntailmentRegime());
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
    public XSPReply setEntailmentRegime(UserImpl client, String database, String regime) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            try {
                db.controller.setEntailmentRegime(regime);
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
            return XSPReplySuccess.instance();
        }
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
    public XSPReply getRule(UserImpl client, String database, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD)) {
            try {
                XOWLRule rule = db.controller.getRule(name);
                if (rule == null)
                    return XSPReplyNotFound.instance();
                return new XSPReplyResult<>(rule);
            } catch (IOException exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the rules in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply getRules(UserImpl client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD)) {
            try {
                Collection<XOWLRule> rules = db.controller.getRules();
                return new XSPReplyResultCollection<>(rules);
            } catch (IOException exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply addRule(UserImpl client, String database, String content, boolean activate) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            try {
                XOWLRule rule = db.controller.addRule(content, activate);
                return new XSPReplyResult<>(rule);
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply removeRule(UserImpl client, String database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            try {
                db.controller.removeRule(rule);
                return XSPReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply activateRule(UserImpl client, String database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            try {
                db.controller.activateRule(rule);
                return XSPReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply deactivateRule(UserImpl client, String database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            try {
                db.controller.deactivateRule(rule);
                return XSPReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply getRuleStatus(UserImpl client, String database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD)) {
            try {
                RDFRuleStatus status = db.controller.getRuleStatus(rule);
                if (status == null)
                    return new XSPReplyFailure("The rule is not active");
                return new XSPReplyResult<>(status);
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply getStoreProcedure(UserImpl client, String database, String iri) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD)) {
            try {
                XOWLStoredProcedure procedure = db.controller.getStoredProcedure(iri);
                if (procedure == null)
                    return XSPReplyNotFound.instance();
                return new XSPReplyResult<>(procedure);
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the stored procedures in a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply getStoredProcedures(UserImpl client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD)) {
            try {
                Collection<XOWLStoredProcedure> procedures = db.controller.getStoredProcedures();
                return new XSPReplyResultCollection<>(procedures);
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply addStoredProcedure(UserImpl client, String database, String iri, String sparql, Collection<String> parameters) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            try {
                XOWLStoredProcedure procedure = db.controller.addStoredProcedure(iri, sparql, parameters);
                return new XSPReplyResult<>(procedure);
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply removeStoredProcedure(UserImpl client, String database, String procedure) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            try {
                db.controller.removeStoredProcedure(procedure);
                return XSPReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply executeStoredProcedure(UserImpl client, String database, String procedure, String contextDefinition) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        boolean canWrite = checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE);
        if (canWrite || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANREAD)) {
            try {
                Result result = db.controller.executeStoredProcedure(procedure, contextDefinition, !canWrite);
                if (result.isFailure())
                    return new XSPReplyFailure(((ResultFailure) result).getMessage());
                return new XSPReplyResult<>(result);
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
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
    public XSPReply upload(UserImpl client, String database, String syntax, String content) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, db)
                || checkIsAllowed(client.proxy, db.proxy, Schema.ADMIN_CANWRITE)) {
            try {
                BufferedLogger logger = new BufferedLogger();
                db.controller.upload(logger, syntax, content);
                if (!logger.getErrorMessages().isEmpty())
                    return new XSPReplyFailure(logger.getErrorsAsString());
                return XSPReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new XSPReplyFailure(exception.getMessage());
            }
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the statistics for a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply getStatistics(UserImpl client, String database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return XSPReplyNotFound.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, db)) {
            return new XSPReplyResult<>(db.controller.getStatistics());
        }
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
                logger.info("Banned client " + client.toString() + " for " + configuration.getSecurityBanLength() + " seconds");
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
    private UserImpl doCreateUser(String login, String password) {
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        synchronized (adminDB) {
            proxy = adminDB.controller.getRepository().getProxy(userIRI);
            if (proxy != null)
                return null;
            proxy = adminDB.controller.getRepository().resolveProxy(userIRI);
            proxy.setValue(Vocabulary.rdfType, adminDB.controller.getRepository().resolveProxy(Schema.ADMIN_USER));
            proxy.setValue(Schema.ADMIN_NAME, login);
            proxy.setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.controller.getRepository().getStore().commit();
        }
        UserImpl result = new UserImpl(proxy);
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
    private UserImpl doGetUser(String login) {
        synchronized (users) {
            UserImpl result = users.get(login);
            if (result != null)
                return result;
        }
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        String name = null;
        synchronized (adminDB) {
            proxy = adminDB.controller.getRepository().getProxy(userIRI);
            if (proxy != null)
                name = (String) proxy.getDataValue(Schema.ADMIN_NAME);
        }
        if (proxy == null)
            return null;
        synchronized (users) {
            UserImpl result = users.get(name);
            if (result == null) {
                result = new UserImpl(proxy);
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
    private UserImpl doGetUser(ProxyObject proxy) {
        synchronized (users) {
            for (UserImpl user : users.values()) {
                if (user.proxy == proxy)
                    return user;
            }
            UserImpl user = new UserImpl(proxy);
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
    private DatabaseImpl doGetDatabase(String name) {
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
    private DatabaseImpl doGetDatabase(ProxyObject proxy) {
        synchronized (databases) {
            for (DatabaseImpl db : databases.values()) {
                if (db.proxy == proxy)
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
    private boolean checkIsDBAdmin(UserImpl user, DatabaseImpl database) {
        return checkIsAllowed(user.proxy, database.proxy, Schema.ADMIN_ADMINOF);
    }

    /**
     * Gets whether a user is a server administrator
     *
     * @param user A user
     * @return Whether the user is a server administrator
     */
    private boolean checkIsServerAdmin(UserImpl user) {
        return checkIsAllowed(user.proxy, adminDB.proxy, Schema.ADMIN_ADMINOF);
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
        UserImpl user = doGetUser(name);
        if (user == null)
            return XSPReplyNotFound.instance();
        synchronized (adminDB) {
            user.proxy.removeAllValues(Schema.ADMIN_PASSWORD);
            user.proxy.setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.controller.getRepository().getStore().commit();
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
            adminDB.controller.getRepository().getStore().commit();
        }
        return true;
    }
}
