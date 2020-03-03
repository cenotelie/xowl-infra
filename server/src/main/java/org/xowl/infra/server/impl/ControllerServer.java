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

import fr.cenotelie.commons.utils.Base64;
import fr.cenotelie.commons.utils.IOUtils;
import fr.cenotelie.commons.utils.api.*;
import fr.cenotelie.commons.utils.logging.BufferedLogger;
import fr.cenotelie.commons.utils.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.xowl.infra.server.ServerConfiguration;
import org.xowl.infra.server.api.*;
import org.xowl.infra.server.base.BaseDatabasePrivileges;
import org.xowl.infra.server.base.BaseUserPrivileges;
import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.RDFRuleStatus;
import org.xowl.infra.store.sparql.Result;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.*;

/**
 * A server controller is an entity that serves and controls a set of persisted databases.
 * The controller is responsible for enforcing the access policy.
 *
 * @author Laurent Wouters
 */
public class ControllerServer implements Closeable {
    /**
     * The logger for this controller
     */
    protected final Logger logger;
    /**
     * The current configuration
     */
    private final ServerConfiguration configuration;
    /**
     * The Message Authentication Code algorithm to use for securing user tokens
     */
    private final Mac securityMAC;
    /**
     * The private security key for the Message Authentication Code
     */
    private final Key securityKey;
    /**
     * The time to live in seconds of an authentication token
     */
    private final long securityTokenTTL;
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
    public ControllerServer(Logger logger, ServerConfiguration configuration) throws Exception {
        this.logger = logger;
        this.configuration = configuration;
        this.databases = new HashMap<>();
        boolean isEmpty = true;
        if (configuration.getDatabasesFolder().exists()) {
            String[] children = configuration.getDatabasesFolder().list();
            isEmpty = children == null || children.length == 0;
        }
        logger.info("Initializing the controller");
        this.securityMAC = Mac.getInstance("HmacSHA256");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        keyGenerator.init(256);
        this.securityKey = keyGenerator.generateKey();
        this.securityTokenTTL = configuration.getSecurityTokenTTL();
        this.adminDB = newDB(new ControllerDatabase(
                new File(configuration.getDatabasesFolder(), configuration.getAdminDBName()),
                configuration.getDefaultMaxThreads(),
                configuration.getAdminDBName()));
        this.databases.put(adminDB.getIdentifier(), adminDB);
        this.clients = new HashMap<>();
        this.users = new HashMap<>();
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
        adminDB.dbController.proxy.setValue(Vocabulary.rdfType, adminDB.dbController.getRepository().resolveProxy(Schema.ADMIN_DATABASE));
        adminDB.dbController.proxy.setValue(Schema.ADMIN_NAME, adminDB.getIdentifier());
        adminDB.dbController.proxy.setValue(Schema.ADMIN_LOCATION, ".");
        UserImpl admin = doCreateUser(configuration.getAdminDefaultUser(), configuration.getAdminDefaultPassword());
        admin.userController.proxy.addValue(Schema.ADMIN_ADMINOF, adminDB.dbController.proxy);
        adminDB.dbController.getRepository().getStore().commit();
    }

    /**
     * Initializes the databases managed by this controller
     */
    private void initializeDatabases() {
        ProxyObject classDB = adminDB.dbController.getRepository().resolveProxy(Schema.ADMIN_DATABASE);
        for (ProxyObject poDB : classDB.getInstances()) {
            if (poDB == adminDB.dbController.proxy)
                continue;
            logger.info("Found database " + poDB.getIRIString());
            String name = (String) poDB.getDataValue(Schema.ADMIN_NAME);
            String location = (String) poDB.getDataValue(Schema.ADMIN_LOCATION);
            try {
                DatabaseImpl db = newDB(new ControllerDatabase(
                        new File(configuration.getDatabasesFolder(), location),
                        configuration.getDefaultMaxThreads(),
                        poDB,
                        name,
                        null));
                databases.put(db.getIdentifier(), db);
                logger.info("Loaded database " + poDB.getIRIString() + " as " + db.getIdentifier());
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
     * @return The database object
     */
    protected DatabaseImpl newDB(ControllerDatabase controller) {
        return new DatabaseImpl(this, controller);
    }

    /**
     * Creates a new user
     *
     * @param controller The associated user controller
     * @return The user object
     */
    protected UserImpl newUser(ControllerUser controller) {
        return new UserImpl(this, controller);
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
                entry.getValue().dbController.close();
            } catch (IOException exception) {
                logger.error(exception);
            }
            logger.info("Closed database " + entry.getKey());
        }
        databases.clear();
        logger.info("All databases closed");
    }

    /**
     * Gets the time to live in seconds of an authentication token
     *
     * @return The time to live in seconds of an authentication token
     */
    public long getSecurityTokenTTL() {
        return securityTokenTTL;
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
     * Performs the login of a user
     *
     * @param client   The client trying to login
     * @param login    The user to log in
     * @param password The user password
     * @return The protocol reply, or null if the client is banned
     */
    public Reply login(InetAddress client, String login, String password) {
        if (isBanned(client))
            return ReplyUnauthenticated.instance();
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            onLoginFailure(client);
            logger.info("Authentication failure from " + client.toString() + " on initial login with " + login);
            return ReplyUnauthenticated.instance();
        }
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        String hash = null;
        synchronized (adminDB) {
            proxy = adminDB.dbController.getRepository().getProxy(userIRI);
            if (proxy != null)
                hash = (String) proxy.getDataValue(Schema.ADMIN_PASSWORD);
        }
        if (proxy == null) {
            onLoginFailure(client);
            logger.info("Authentication failure from " + client.toString() + " on initial login with " + login);
            return ReplyUnauthenticated.instance();
        }
        if (!BCrypt.checkpw(password, hash)) {
            onLoginFailure(client);
            logger.info("Authentication failure from " + client.toString() + " on initial login with " + login);
            return ReplyUnauthenticated.instance();
        } else {
            synchronized (clients) {
                clients.remove(client);
            }
            logger.info("Authentication success from " + client.toString() + " on initial login with " + login);
            return new ReplyResult<>(buildTokenFor(login));
        }
    }

    /**
     * Performs the logout of a user
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public Reply logout(UserImpl client) {
        return ReplySuccess.instance();
    }

    /**
     * Performs the authentication of a user using a token
     *
     * @param client The requesting client
     * @param token  The provided token
     * @return The protocol reply, or null if the client is banned
     */
    public Reply authenticate(InetAddress client, String token) {
        if (isBanned(client))
            return ReplyUnauthenticated.instance();
        Reply reply = checkToken(token);
        if (reply == ReplyUnauthenticated.instance()) {
            // the token is invalid
            onLoginFailure(client);
            logger.info("Authentication failure from " + client.toString() + " with invalid token");
            return reply;
        }
        if (!reply.isSuccess()) {
            logger.info("Authentication failure from " + client.toString() + " with invalid token");
            return reply;
        }
        UserImpl user = getPrincipal(((ReplyResult<String>) reply).getData());
        return new ReplyResult<>(user);
    }

    /**
     * Performs the authentication of a user using a login and a password
     *
     * @param client   The requesting client
     * @param login    The login
     * @param password The password
     * @return The protocol reply, or null if the client is banned
     */
    public Reply authenticate(InetAddress client, String login, String password) {
        Reply reply = login(client, login, password);
        if (!reply.isSuccess()) {
            return reply;
        }
        UserImpl user = getPrincipal(login);
        return new ReplyResult<>(user);
    }

    /**
     * Request the server shutdown
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public Reply serverShutdown(UserImpl client) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return ReplyUnauthorized.instance();
        onRequestShutdown();
        return ReplySuccess.instance();
    }

    /**
     * Request the server restart
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public Reply serverRestart(UserImpl client) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return ReplyUnauthorized.instance();
        onRequestRestart();
        return ReplySuccess.instance();
    }

    /**
     * Grants server administrator privilege
     *
     * @param client The requesting client
     * @param name   The target user
     * @return The protocol reply
     */
    public Reply serverGrantAdmin(UserImpl client, String name) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return ReplyUnauthorized.instance();
        UserImpl target = doGetUser(name);
        if (target == null)
            return ReplyNotFound.instance();
        return doChangeUserPrivilege(target, adminDB, Schema.ADMIN_ADMINOF, true);
    }

    /**
     * Revokes server administrator privilege
     *
     * @param client The requesting client
     * @param name   The target user
     * @return The protocol reply
     */
    public Reply serverRevokeAdmin(UserImpl client, String name) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return ReplyUnauthorized.instance();
        UserImpl target = doGetUser(name);
        if (target == null)
            return ReplyNotFound.instance();
        return doChangeUserPrivilege(target, adminDB, Schema.ADMIN_ADMINOF, false);
    }

    /**
     * Gets the databases on this server
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public Reply getDatabases(UserImpl client) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        Collection<XOWLDatabase> result = new ArrayList<>();
        synchronized (databases) {
            if (checkIsServerAdmin(client)) {
                for (DatabaseImpl db : databases.values())
                    result.add(db);
                return new ReplyResultCollection<>(result);
            }
            for (DatabaseImpl database : databases.values()) {
                if (checkCanRead(client, database))
                    result.add(database);
            }
        }
        return new ReplyResultCollection<>(result);
    }

    /**
     * Gets the info about a database
     *
     * @param client The requesting client
     * @param name   The target database
     * @return The protocol reply
     */
    public Reply getDatabase(UserImpl client, String name) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(name);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanRead(client, db)) {
            return new ReplyResult<>(db);
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Creates a new database
     *
     * @param client   The requesting client
     * @param name     The name of the new database
     * @param dbConfig The initial configuration
     * @return The protocol reply
     */
    public Reply createDatabase(UserImpl client, String name, XOWLDatabaseConfiguration dbConfig) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return ReplyUnauthorized.instance();
        if (!name.matches("[_a-zA-Z0-9]+"))
            return new ReplyApiError(ApiV1.ERROR_DB_NAME_REQUIREMENT_FAILED);

        synchronized (databases) {
            DatabaseImpl result = databases.get(name);
            if (result != null)
                return new ReplyApiError(ApiV1.ERROR_DB_ALREADY_EXIST);
            File folder = new File(configuration.getDatabasesFolder(), name);
            try {
                ProxyObject proxy = adminDB.dbController.getRepository().resolveProxy(Schema.ADMIN_GRAPH_DBS + name);
                proxy.setValue(Vocabulary.rdfType, adminDB.dbController.getRepository().resolveProxy(Schema.ADMIN_DATABASE));
                proxy.setValue(Schema.ADMIN_NAME, name);
                proxy.setValue(Schema.ADMIN_LOCATION, name);
                DatabaseImpl db = newDB(new ControllerDatabase(
                        folder,
                        configuration.getDefaultMaxThreads(),
                        proxy,
                        name,
                        dbConfig));
                adminDB.dbController.getRepository().getStore().commit();
                db.dbController.getRepository().getStore().commit();
                databases.put(name, db);
                return new ReplyResult<>(db);
            } catch (Exception exception) {
                return new ReplyException(exception);
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
    public Reply dropDatabase(UserImpl client, String name) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return ReplyUnauthorized.instance();
        synchronized (databases) {
            DatabaseImpl database = databases.remove(name);
            if (database == null)
                return ReplyNotFound.instance();
            File folder = new File(configuration.getDatabasesFolder(), (String) database.dbController.proxy.getDataValue(Schema.ADMIN_LOCATION));
            try {
                database.dbController.close();
            } catch (IOException exception) {
                logger.error(exception);
            }
            if (!IOUtils.deleteFolder(folder)) {
                logger.error("Failed to delete " + folder.getAbsolutePath());
            }
            database.dbController.proxy.delete();
            adminDB.dbController.getRepository().getStore().commit();
        }
        return ReplySuccess.instance();
    }

    /**
     * Gets the metric definition for a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public Reply getDatabaseMetric(UserImpl client, String database) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            return new ReplyResult<>(db.dbController.getMetric());
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Gets a snapshot of the metrics for a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public Reply getDatabaseMetricSnapshot(UserImpl client, String database) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            return new ReplyResult<>(db.dbController.getMetricSnapshot());
        }
        return ReplyUnauthorized.instance();
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
    public Reply sparql(UserImpl client, String database, String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        boolean canWrite = checkCanWrite(client, db);
        if (canWrite || checkCanRead(client, db)) {
            Result result = db.dbController.sparql(sparql, defaultIRIs, namedIRIs, !canWrite);
            return new ReplyResult<>(result);
        } else {
            return ReplyUnauthorized.instance();
        }
    }

    /**
     * Gets the entailment regime
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public Reply getEntailmentRegime(UserImpl client, String database) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanRead(client, db))
            return new ReplyResult<>(db.dbController.getEntailmentRegime());
        return ReplyUnauthorized.instance();
    }

    /**
     * Sets the entailment regime
     *
     * @param client   The requesting client
     * @param database The target database
     * @param regime   The entailment regime
     * @return The protocol reply
     */
    public Reply setEntailmentRegime(UserImpl client, String database, String regime) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            try {
                db.dbController.setEntailmentRegime(regime);
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
            return ReplySuccess.instance();
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Gets the privileges on a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public Reply getDatabasePrivileges(UserImpl client, String database) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            BaseDatabasePrivileges result = new BaseDatabasePrivileges();
            for (ProxyObject value : db.dbController.proxy.getObjectsFrom(Schema.ADMIN_ADMINOF)) {
                UserImpl user = doGetUser(value);
                result.add(user, XOWLPrivilege.ADMIN);
            }
            for (ProxyObject value : db.dbController.proxy.getObjectsFrom(Schema.ADMIN_CANWRITE)) {
                UserImpl user = doGetUser(value);
                result.add(user, XOWLPrivilege.WRITE);
            }
            for (ProxyObject value : db.dbController.proxy.getObjectsFrom(Schema.ADMIN_CANREAD)) {
                UserImpl user = doGetUser(value);
                result.add(user, XOWLPrivilege.READ);
            }
            return new ReplyResult<>(result);
        }
        return ReplyUnauthorized.instance();
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
    public Reply grantDatabase(UserImpl client, String user, String database, int privilege) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            UserImpl target = doGetUser(user);
            if (target == null)
                return ReplyNotFound.instance();
            String priv = (privilege == XOWLPrivilege.ADMIN ? Schema.ADMIN_ADMINOF : (privilege == XOWLPrivilege.WRITE ? Schema.ADMIN_CANWRITE : (privilege == XOWLPrivilege.READ ? Schema.ADMIN_CANREAD : null)));
            if (priv == null)
                return ReplyNotFound.instance();
            return doChangeUserPrivilege(target, db, priv, true);
        }
        return ReplyUnauthorized.instance();
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
    public Reply revokeDatabase(UserImpl client, String user, String database, int privilege) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            UserImpl target = doGetUser(user);
            if (target == null)
                return ReplyNotFound.instance();
            String priv = (privilege == XOWLPrivilege.ADMIN ? Schema.ADMIN_ADMINOF : (privilege == XOWLPrivilege.WRITE ? Schema.ADMIN_CANWRITE : (privilege == XOWLPrivilege.READ ? Schema.ADMIN_CANREAD : null)));
            if (priv == null)
                return ReplyNotFound.instance();
            return doChangeUserPrivilege(target, db, priv, false);
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Gets the rules in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public Reply getRules(UserImpl client, String database) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanRead(client, db)) {
            try {
                Collection<XOWLRule> rules = db.dbController.getRules();
                return new ReplyResultCollection<>(rules);
            } catch (IOException exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Gets the rule for the specified name
     *
     * @param client   The requesting client
     * @param database The target database
     * @param name     The name (IRI) of a rule
     * @return The protocol reply
     */
    public Reply getRule(UserImpl client, String database, String name) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanRead(client, db)) {
            try {
                XOWLRule rule = db.dbController.getRule(name);
                if (rule == null)
                    return ReplyNotFound.instance();
                return new ReplyResult<>(rule);
            } catch (IOException exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
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
    public Reply addRule(UserImpl client, String database, String content, boolean activate) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            try {
                XOWLRule rule = db.dbController.addRule(content, activate);
                return new ReplyResult<>(rule);
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Removes a rule from this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule to remove
     * @return The protocol reply
     */
    public Reply removeRule(UserImpl client, String database, String rule) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            try {
                db.dbController.removeRule(rule);
                return ReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Activates an existing rule in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule to activate
     * @return The protocol reply
     */
    public Reply activateRule(UserImpl client, String database, String rule) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            try {
                db.dbController.activateRule(rule);
                return ReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Deactivates an existing rule in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule to deactivate
     * @return The protocol reply
     */
    public Reply deactivateRule(UserImpl client, String database, String rule) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            try {
                db.dbController.deactivateRule(rule);
                return ReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Gets the matching status of a rule in this database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule to inquire
     * @return The protocol reply
     */
    public Reply getRuleStatus(UserImpl client, String database, String rule) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanRead(client, db)) {
            try {
                RDFRuleStatus status = db.dbController.getRuleStatus(rule);
                if (status == null)
                    return new ReplyApiError(ApiV1.ERROR_RULE_NOT_ACTIVE);
                return new ReplyResult<>(status);
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Gets the stored procedures in a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public Reply getStoredProcedures(UserImpl client, String database) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanRead(client, db)) {
            try {
                Collection<XOWLStoredProcedure> procedures = db.dbController.getStoredProcedures();
                return new ReplyResultCollection<>(procedures);
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Gets the stored procedure for the specified name
     *
     * @param client   The requesting client
     * @param database The target database
     * @param iri      The name (IRI) of a procedure
     * @return The protocol reply
     */
    public Reply getStoreProcedure(UserImpl client, String database, String iri) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanRead(client, db)) {
            try {
                XOWLStoredProcedure procedure = db.dbController.getStoredProcedure(iri);
                if (procedure == null)
                    return ReplyNotFound.instance();
                return new ReplyResult<>(procedure);
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
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
    public Reply addStoredProcedure(UserImpl client, String database, String iri, String sparql, Collection<String> parameters) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            try {
                XOWLStoredProcedure procedure = db.dbController.addStoredProcedure(iri, sparql, parameters);
                return new ReplyResult<>(procedure);
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Removes a stored procedure from a database
     *
     * @param client    The requesting client
     * @param database  The target database
     * @param procedure The procedure to remove
     * @return The protocol reply
     */
    public Reply removeStoredProcedure(UserImpl client, String database, String procedure) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanAdmin(client, db)) {
            try {
                db.dbController.removeStoredProcedure(procedure);
                return ReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
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
    public Reply executeStoredProcedure(UserImpl client, String database, String procedure, String contextDefinition) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        boolean canWrite = checkCanWrite(client, db);
        if (canWrite || checkCanRead(client, db)) {
            try {
                Result result = db.dbController.executeStoredProcedure(procedure, contextDefinition, !canWrite);
                return new ReplyResult<>(result);
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
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
    public Reply upload(UserImpl client, String database, String syntax, String content) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        DatabaseImpl db = doGetDatabase(database);
        if (db == null)
            return ReplyNotFound.instance();
        if (checkCanWrite(client, db)) {
            try {
                BufferedLogger logger = new BufferedLogger();
                db.dbController.upload(logger, syntax, content);
                if (!logger.getErrorMessages().isEmpty())
                    return new ReplyApiError(ApiV1.ERROR_CONTENT_PARSING_FAILED, logger.getErrorsAsString());
                return ReplySuccess.instance();
            } catch (Exception exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Requests the list of users on this server
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public Reply getUsers(UserImpl client) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        Collection<UserImpl> result = new ArrayList<>();
        synchronized (adminDB) {
            ProxyObject classUser = adminDB.dbController.getRepository().resolveProxy(Schema.ADMIN_USER);
            for (ProxyObject poUser : classUser.getInstances()) {
                String name = (String) poUser.getDataValue(Schema.ADMIN_NAME);
                synchronized (users) {
                    UserImpl user = users.get(name);
                    if (user == null) {
                        user = newUser(new ControllerUser(poUser));
                        users.put(name, user);
                    }
                    result.add(user);
                }
            }
        }
        return new ReplyResultCollection<>(result);
    }

    /**
     * Request the info about a user
     *
     * @param client The requesting client
     * @param login  The requested user name
     * @return The protocol reply
     */
    public Reply getUser(UserImpl client, String login) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (client.getIdentifier().equals(login))
            return new ReplyResult<>(client);
        UserImpl user = doGetUser(login);
        if (user == null)
            return ReplyNotFound.instance();
        return new ReplyResult<>(user);
    }

    /**
     * Request the creation of a user
     *
     * @param client   The requesting client
     * @param login    The login for the new user
     * @param password The password for the new user
     * @return The protocol reply
     */
    public Reply createUser(UserImpl client, String login, String password) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return ReplyUnauthorized.instance();
        if (!login.matches("[_a-zA-Z0-9]+"))
            return new ReplyApiError(ApiV1.ERROR_USER_NAME_REQUIREMENT_FAILED);
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return new ReplyApiError(ApiV1.ERROR_USER_PASSWORD_REQUIREMENT_FAILED, "Required minimal length: " + configuration.getSecurityMinPasswordLength());
        UserImpl user = doCreateUser(login, password);
        if (user == null)
            return new ReplyApiError(ApiV1.ERROR_USER_ALREADY_EXIST);
        return new ReplyResult<>(user);
    }

    /**
     * Deletes a user
     *
     * @param client   The requesting client
     * @param toDelete The user to delete
     * @return The protocol reply
     */
    public Reply deleteUser(UserImpl client, String toDelete) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return ReplyUnauthorized.instance();
        UserImpl user = doGetUser(toDelete);
        if (user == null)
            return ReplyNotFound.instance();
        synchronized (users) {
            user = users.remove(toDelete);
        }
        if (user == null)
            return ReplyNotFound.instance();
        synchronized (adminDB) {
            user.userController.proxy.delete();
            adminDB.dbController.getRepository().getStore().commit();
        }
        return ReplySuccess.instance();
    }

    /**
     * Resets the password for another user
     *
     * @param client   The requesting client
     * @param target   The target user
     * @param password The new password
     * @return The protocol reply
     */
    public Reply updatePassword(UserImpl client, String target, String password) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (client.getIdentifier().equals(target) || checkIsServerAdmin(client))
            return doResetPassword(target, password);
        return ReplyUnauthorized.instance();
    }

    /**
     * Resets the password of a user
     *
     * @param name     The user's name
     * @param password The new password
     * @return The protocol reply
     */
    private Reply doResetPassword(String name, String password) {
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return new ReplyApiError(ApiV1.ERROR_USER_PASSWORD_REQUIREMENT_FAILED, "Required minimal length: " + configuration.getSecurityMinPasswordLength());
        UserImpl user = doGetUser(name);
        if (user == null)
            return ReplyNotFound.instance();
        synchronized (adminDB) {
            user.userController.proxy.removeAllValues(Schema.ADMIN_PASSWORD);
            user.userController.proxy.setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.dbController.getRepository().getStore().commit();
        }
        return ReplySuccess.instance();
    }

    /**
     * Gets the privileges assigned to a user
     *
     * @param client The requesting client
     * @param name   The target user
     * @return The protocol reply
     */
    public Reply getUserPrivileges(UserImpl client, String name) {
        if (client == null)
            return ReplyUnauthenticated.instance();
        if (client.getIdentifier().equals(name) || checkIsServerAdmin(client)) {
            UserImpl user = doGetUser(name);
            if (user == null)
                return ReplyNotFound.instance();
            BaseUserPrivileges result = new BaseUserPrivileges(checkIsServerAdmin(user));
            for (ProxyObject value : user.userController.proxy.getObjectValues(Schema.ADMIN_ADMINOF)) {
                DatabaseImpl database = doGetDatabase(value);
                result.add(database, XOWLPrivilege.ADMIN);
            }
            for (ProxyObject value : user.userController.proxy.getObjectValues(Schema.ADMIN_CANWRITE)) {
                DatabaseImpl database = doGetDatabase(value);
                result.add(database, XOWLPrivilege.WRITE);
            }
            for (ProxyObject value : user.userController.proxy.getObjectValues(Schema.ADMIN_CANREAD)) {
                DatabaseImpl database = doGetDatabase(value);
                result.add(database, XOWLPrivilege.READ);
            }
            return new ReplyResult<>(result);
        }
        return ReplyUnauthorized.instance();
    }

    /**
     * Builds an authentication token for the specified login
     *
     * @param login The user login
     * @return The new authentication token
     */
    private String buildTokenFor(String login) {
        long timestamp = System.currentTimeMillis();
        long validUntil = timestamp + securityTokenTTL * 1000;
        byte[] text = login.getBytes(IOUtils.CHARSET);
        byte[] tokenData = Arrays.copyOf(text, text.length + 8);
        tokenData[text.length] = (byte) ((validUntil & 0xFF00000000000000L) >>> 56);
        tokenData[text.length + 1] = (byte) ((validUntil & 0x00FF000000000000L) >>> 48);
        tokenData[text.length + 2] = (byte) ((validUntil & 0x0000FF0000000000L) >>> 40);
        tokenData[text.length + 3] = (byte) ((validUntil & 0x000000FF00000000L) >>> 32);
        tokenData[text.length + 4] = (byte) ((validUntil & 0x00000000FF000000L) >>> 24);
        tokenData[text.length + 5] = (byte) ((validUntil & 0x0000000000FF0000L) >>> 16);
        tokenData[text.length + 6] = (byte) ((validUntil & 0x000000000000FF00L) >>> 8);
        tokenData[text.length + 7] = (byte) ((validUntil & 0x00000000000000FFL));

        synchronized (securityMAC) {
            try {
                securityMAC.init(securityKey);
                byte[] tokenHash = securityMAC.doFinal(tokenData);
                byte[] token = Arrays.copyOf(tokenData, tokenData.length + tokenHash.length);
                System.arraycopy(tokenHash, 0, token, tokenData.length, tokenHash.length);
                return Base64.encodeBase64(token);
            } catch (InvalidKeyException exception) {
                logger.error(exception);
                return null;
            }
        }
    }

    /**
     * Checks whether the token is valid
     *
     * @param token The authentication token to check
     * @return The protocol reply, or null if the token is invalid
     */
    private Reply checkToken(String token) {
        byte[] tokenBytes = Base64.decodeBase64(token);
        if (tokenBytes.length <= 32 + 8)
            return ReplyUnauthenticated.instance();
        byte[] tokenData = Arrays.copyOf(tokenBytes, tokenBytes.length - 32);
        byte[] hashProvided = new byte[32];
        System.arraycopy(tokenBytes, tokenBytes.length - 32, hashProvided, 0, 32);

        // checks the hash
        synchronized (securityMAC) {
            try {
                securityMAC.init(securityKey);
                byte[] computedHash = securityMAC.doFinal(tokenData);
                if (!Arrays.equals(hashProvided, computedHash))
                    // the token does not checks out ...
                    return ReplyUnauthenticated.instance();
            } catch (InvalidKeyException exception) {
                logger.error(exception);
                return new ReplyException(exception);
            }
        }

        byte b0 = tokenBytes[tokenBytes.length - 32 - 8];
        byte b1 = tokenBytes[tokenBytes.length - 32 - 7];
        byte b2 = tokenBytes[tokenBytes.length - 32 - 6];
        byte b3 = tokenBytes[tokenBytes.length - 32 - 5];
        byte b4 = tokenBytes[tokenBytes.length - 32 - 4];
        byte b5 = tokenBytes[tokenBytes.length - 32 - 3];
        byte b6 = tokenBytes[tokenBytes.length - 32 - 2];
        byte b7 = tokenBytes[tokenBytes.length - 32 - 1];
        long validUntil = ((long) b0 & 0xFFL) << 56
                | ((long) b1 & 0xFFL) << 48
                | ((long) b2 & 0xFFL) << 40
                | ((long) b3 & 0xFFL) << 32
                | ((long) b4 & 0xFFL) << 24
                | ((long) b5 & 0xFFL) << 16
                | ((long) b6 & 0xFFL) << 8
                | ((long) b7 & 0xFFL);
        if (System.currentTimeMillis() > validUntil)
            // the token expired
            return ReplyExpiredSession.instance();
        return new ReplyResult<>(new String(tokenBytes, 0, tokenBytes.length - 32 - 8, IOUtils.CHARSET));
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
            proxy = adminDB.dbController.getRepository().getProxy(userIRI);
            if (proxy != null)
                return null;
            proxy = adminDB.dbController.getRepository().resolveProxy(userIRI);
            proxy.setValue(Vocabulary.rdfType, adminDB.dbController.getRepository().resolveProxy(Schema.ADMIN_USER));
            proxy.setValue(Schema.ADMIN_NAME, login);
            proxy.setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.dbController.getRepository().getStore().commit();
        }
        UserImpl result = newUser(new ControllerUser(proxy));
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
            proxy = adminDB.dbController.getRepository().getProxy(userIRI);
            if (proxy != null)
                name = (String) proxy.getDataValue(Schema.ADMIN_NAME);
        }
        if (proxy == null)
            return null;
        synchronized (users) {
            UserImpl result = users.get(name);
            if (result == null) {
                result = newUser(new ControllerUser(proxy));
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
                if (user.userController.proxy == proxy)
                    return user;
            }
            UserImpl user = newUser(new ControllerUser(proxy));
            users.put(user.getIdentifier(), user);
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
                if (db.dbController.proxy == proxy)
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
     * Gets whether a user can read a database
     *
     * @param user     A user
     * @param database A database
     * @return Whether the user is an administrator of the database
     */
    private boolean checkCanRead(UserImpl user, DatabaseImpl database) {
        return checkIsServerAdmin(user)
                || checkIsAllowed(user.userController.proxy, database.dbController.proxy, Schema.ADMIN_ADMINOF)
                || checkIsAllowed(user.userController.proxy, database.dbController.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(user.userController.proxy, database.dbController.proxy, Schema.ADMIN_CANREAD);
    }

    /**
     * Gets whether a user can write to a database
     *
     * @param user     A user
     * @param database A database
     * @return Whether the user is an administrator of the database
     */
    private boolean checkCanWrite(UserImpl user, DatabaseImpl database) {
        return checkIsServerAdmin(user)
                || checkIsAllowed(user.userController.proxy, database.dbController.proxy, Schema.ADMIN_ADMINOF)
                || checkIsAllowed(user.userController.proxy, database.dbController.proxy, Schema.ADMIN_CANWRITE);
    }

    /**
     * Gets whether a user is an administrator of a database
     *
     * @param user     A user
     * @param database A database
     * @return Whether the user is an administrator of the database
     */
    private boolean checkCanAdmin(UserImpl user, DatabaseImpl database) {
        return checkIsServerAdmin(user)
                || checkIsAllowed(user.userController.proxy, database.dbController.proxy, Schema.ADMIN_ADMINOF);
    }

    /**
     * Gets whether a user is a server administrator
     *
     * @param user A user
     * @return Whether the user is a server administrator
     */
    private boolean checkIsServerAdmin(UserImpl user) {
        return checkIsAllowed(user.userController.proxy, adminDB.dbController.proxy, Schema.ADMIN_ADMINOF);
    }

    /**
     * Change a user privilege on a database
     *
     * @param user      The user
     * @param database  The database
     * @param privilege The privilege
     * @param positive  Whether to add or remove the privilege
     * @return The protocol reply
     */
    private Reply doChangeUserPrivilege(UserImpl user, DatabaseImpl database, String privilege, boolean positive) {
        synchronized (adminDB) {
            Collection<ProxyObject> dbs = user.userController.proxy.getObjectValues(privilege);
            if (positive) {
                if (dbs.contains(database.dbController.proxy))
                    return new ReplyApiError(ApiV1.ERROR_PRIVILEGE_ALREADY_GRANTED);
                user.userController.proxy.addValue(privilege, database.dbController.proxy);
            } else {
                if (!dbs.contains(database.dbController.proxy))
                    return new ReplyApiError(ApiV1.ERROR_PRIVILEGE_NOT_GRANTED);
                user.userController.proxy.removeValue(privilege, database.dbController.proxy);
            }
            adminDB.dbController.getRepository().getStore().commit();
        }
        return ReplySuccess.instance();
    }

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
}
