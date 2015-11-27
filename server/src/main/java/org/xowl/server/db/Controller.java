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

import org.mindrot.jbcrypt.BCrypt;
import org.xowl.server.Program;
import org.xowl.server.ServerConfiguration;
import org.xowl.store.EntailmentRegime;
import org.xowl.store.ProxyObject;
import org.xowl.store.Vocabulary;
import org.xowl.store.loaders.SPARQLLoader;
import org.xowl.store.rdf.Quad;
import org.xowl.store.sparql.Command;
import org.xowl.store.sparql.Result;
import org.xowl.store.sparql.ResultSuccess;
import org.xowl.store.storage.remote.*;
import org.xowl.utils.logging.BufferedLogger;
import org.xowl.utils.logging.ConsoleLogger;
import org.xowl.utils.logging.DispatchLogger;
import org.xowl.utils.logging.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.util.*;

/**
 * The main controller for the hosted databases
 *
 * @author Laurent Wouters
 */
public abstract class Controller implements Closeable {
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
     * The main logger
     */
    private final Logger logger;
    /**
     * The currently hosted repositories
     */
    private final Map<String, Database> databases;
    /**
     * The administration database
     */
    private final Database adminDB;
    /**
     * The map of clients with failed login attempts
     */
    private final Map<InetAddress, ClientLogin> clients;
    /**
     * The map of current users on this server
     */
    private final Map<String, User> users;

    /**
     * Gets current configuration
     *
     * @return The current configuration
     */
    public ServerConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the main logger
     *
     * @return The main logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Resolves the user for the specified login
     *
     * @param login A login
     * @return The user
     */
    public User user(String login) {
        return users.get(login);
    }

    /**
     * Resolves the user for the specified proxy object
     *
     * @param proxy A proxy
     * @return The user
     */
    public User user(ProxyObject proxy) {
        synchronized (users) {
            for (User user : users.values()) {
                if (user.proxy == proxy)
                    return user;
            }
            User user = new User(proxy);
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
    private Database database(String name) {
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
    private Database database(ProxyObject proxy) {
        synchronized (databases) {
            for (Database db : databases.values()) {
                if (db.proxy == proxy)
                    return db;
            }
        }
        return null;
    }

    /**
     * Initializes this controller
     *
     * @param configuration The current configuration
     * @throws IOException When the location cannot be accessed
     */
    public Controller(ServerConfiguration configuration) throws IOException {
        this.configuration = configuration;
        this.logger = new ConsoleLogger();
        this.databases = new HashMap<>();
        boolean isEmpty = true;
        if (configuration.getDatabasesFolder().exists()) {
            String[] children = configuration.getDatabasesFolder().list();
            isEmpty = children == null || children.length == 0;
        }
        logger.info("Initializing the controller");
        adminDB = new Database(configuration, configuration.getDatabasesFolder());
        databases.put(configuration.getAdminDBName(), adminDB);
        clients = new HashMap<>();
        users = new HashMap<>();
        if (isEmpty) {
            adminDB.proxy.setValue(Vocabulary.rdfType, adminDB.repository.resolveProxy(Schema.ADMIN_DATABASE));
            adminDB.proxy.setValue(Schema.ADMIN_NAME, configuration.getAdminDBName());
            adminDB.proxy.setValue(Schema.ADMIN_LOCATION, ".");
            User admin = doCreateUser(configuration.getAdminDefaultUser(), configuration.getAdminDefaultPassword());
            users.put(admin.getName(), admin);
            admin.proxy.addValue(Schema.ADMIN_ADMINOF, adminDB.proxy);
            adminDB.repository.getStore().commit();
        } else {
            ProxyObject classDB = adminDB.repository.resolveProxy(Schema.ADMIN_DATABASE);
            for (ProxyObject poDB : classDB.getInstances()) {
                if (poDB == adminDB.proxy)
                    continue;
                logger.info("Found database " + poDB.getIRIString());
                String name = (String) poDB.getDataValue(Schema.ADMIN_NAME);
                String location = (String) poDB.getDataValue(Schema.ADMIN_LOCATION);
                try {
                    Database db = new Database(new File(configuration.getDatabasesFolder(), location), poDB);
                    databases.put(name, db);
                    logger.info("Loaded database " + poDB.getIRIString() + " as " + name);
                } catch (IOException exception) {
                    // do nothing, this exception is reported by the db logger
                    logger.error("Failed to load database " + poDB.getIRIString() + " as " + name);
                }
            }
        }
        logger.info("Controller is ready");
    }

    /**
     * Dumps the admin database onto the console
     */
    private void debugDumpAdminDB() {
        Iterator<Quad> quads = adminDB.repository.getStore().getAll();
        System.out.println("=======");
        while (quads.hasNext()) {
            System.out.println(quads.next());
        }
    }

    /**
     * Gets whether a client is banned
     *
     * @param client A client
     * @return Whether the client is banned
     */
    public boolean isBanned(InetAddress client) {
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
            proxy = adminDB.repository.getProxy(userIRI);
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
            User user;
            synchronized (users) {
                user = users.get(login);
                if (user == null) {
                    user = new User(proxy);
                    users.put(login, user);
                }
            }
            return new XSPReplyResult<>(user);
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
    private boolean checkIsDBAdmin(User user, Database database) {
        return checkIsAllowed(user.proxy, database.proxy, Schema.ADMIN_ADMINOF);
    }

    /**
     * Gets whether a user is a server administrator
     *
     * @param user A user
     * @return Whether the user is a server administrator
     */
    private boolean checkIsServerAdmin(User user) {
        return checkIsAllowed(user.proxy, adminDB.proxy, Schema.ADMIN_ADMINOF);
    }

    /**
     * Requests the shutdown of the server
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public XSPReply serverShutdown(User client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        onRequestShutdown();
        return XSPReplySuccess.instance();
    }

    /**
     * Requests the restart of the server
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public XSPReply serverRestart(User client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        onRequestRestart();
        return XSPReplySuccess.instance();
    }

    /**
     * Gets the user for the specified login
     *
     * @param client The client requesting the information
     * @param login  The user's login
     * @return The protocol reply
     */
    public XSPReply getUser(User client, String login) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (client.getName().equals(login))
            return new XSPReplyResult<>(client);
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        String name = null;
        synchronized (adminDB) {
            proxy = adminDB.repository.getProxy(userIRI);
            if (proxy != null)
                name = (String) proxy.getDataValue(Schema.ADMIN_NAME);
        }
        if (proxy == null)
            return new XSPReplyFailure("User does not exist");
        synchronized (users) {
            User result = users.get(name);
            if (result == null) {
                result = new User(proxy);
                users.put(name, result);
            }
            return new XSPReplyResult<>(result);
        }
    }

    /**
     * Gets the users on this server
     *
     * @param client The client requesting the information
     * @return The protocol reply
     */
    public XSPReply getUsers(User client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        Collection<User> result = new ArrayList<>();
        synchronized (adminDB) {
            ProxyObject classUser = adminDB.repository.resolveProxy(Schema.ADMIN_USER);
            for (ProxyObject poUser : classUser.getInstances()) {
                String name = (String) poUser.getDataValue(Schema.ADMIN_NAME);
                synchronized (users) {
                    User user = users.get(name);
                    if (user == null) {
                        user = new User(poUser);
                        users.put(name, user);
                    }
                    result.add(user);
                }
            }
        }
        return new XSPReplyResult<>(result);
    }

    /**
     * Creates a new user for this server
     *
     * @param client   The requesting client
     * @param login    The login
     * @param password The password
     * @return The protocol reply
     */
    public XSPReply createUser(User client, String login, String password) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        if (!login.matches("[_a-zA-Z0-9]+"))
            return new XSPReplyFailure("Login does not meet requirements ([_a-zA-Z0-9]+)");
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return new XSPReplyFailure("Password does not meet requirements (min length " + configuration.getSecurityMinPasswordLength() + ")");
        User user = doCreateUser(login, password);
        if (user == null)
            return new XSPReplyFailure("User already exists");
        return new XSPReplyResult<>(user);
    }

    /**
     * Realizes the creation of a user
     *
     * @param login    The login for the new user
     * @param password The password for the new user
     * @return The created user
     */
    private User doCreateUser(String login, String password) {
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        synchronized (adminDB) {
            proxy = adminDB.repository.getProxy(userIRI);
            if (proxy != null)
                return null;
            proxy = adminDB.repository.resolveProxy(userIRI);
            proxy.setValue(Vocabulary.rdfType, adminDB.repository.resolveProxy(Schema.ADMIN_USER));
            proxy.setValue(Schema.ADMIN_NAME, login);
            proxy.setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.repository.getStore().commit();
        }
        User result = new User(proxy);
        synchronized (users) {
            users.put(login, result);
        }
        return result;
    }

    /**
     * Deletes a user from this server
     *
     * @param client   The requesting client
     * @param toDelete The user to delete
     * @return The protocol reply
     */
    public XSPReply deleteUser(User client, User toDelete) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        synchronized (users) {
            users.remove(toDelete.getName());
        }
        synchronized (adminDB) {
            toDelete.proxy.delete();
            adminDB.repository.getStore().commit();
        }
        return XSPReplySuccess.instance();
    }

    /**
     * Changes the password of the requesting user
     *
     * @param user     The user
     * @param password The new password
     * @return The protocol reply
     */
    public XSPReply changePassword(User user, String password) {
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return new XSPReplyFailure("Password does not meet requirements");
        synchronized (adminDB) {
            user.proxy.removeAllValues(Schema.ADMIN_PASSWORD);
            user.proxy.setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.repository.getStore().commit();
        }
        return XSPReplySuccess.instance();
    }

    /**
     * Resets the password of a target user
     *
     * @param client   The requesting client
     * @param target   The user for which the password should be changed
     * @param password The new password
     * @return The protocol reply
     */
    public XSPReply resetPassword(User client, User target, String password) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        return changePassword(target, password);
    }

    /**
     * Gets the privileges assigned to a target user
     *
     * @param client The requesting client
     * @param target The target user
     * @return The protocol reply
     */
    public XSPReply getUserPrivileges(User client, User target) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (client == target || checkIsServerAdmin(client)) {
            UserPrivileges result = new UserPrivileges();
            for (ProxyObject value : target.proxy.getObjectValues(Schema.ADMIN_ADMINOF)) {
                Database database = database(value);
                result.add(database, Schema.PRIVILEGE_ADMIN);
            }
            for (ProxyObject value : target.proxy.getObjectValues(Schema.ADMIN_CANWRITE)) {
                Database database = database(value);
                result.add(database, Schema.PRIVILEGE_WRITE);
            }
            for (ProxyObject value : target.proxy.getObjectValues(Schema.ADMIN_CANREAD)) {
                Database database = database(value);
                result.add(database, Schema.PRIVILEGE_READ);
            }
            return new XSPReplyResult<>(result);
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Grants server administrative privilege to a target user
     *
     * @param client The requesting client
     * @param target The target user
     * @return The protocol reply
     */
    public XSPReply grantServerAdmin(User client, User target) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)) {
            boolean success = changeUserPrivilege(target.proxy, adminDB.proxy, Schema.ADMIN_ADMINOF, true);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Revokes server administrative privilege to a target user
     *
     * @param client The requesting client
     * @param target The target user
     * @return The protocol reply
     */
    public XSPReply revokeServerAdmin(User client, User target) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)) {
            boolean success = changeUserPrivilege(target.proxy, adminDB.proxy, Schema.ADMIN_ADMINOF, false);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Grants database administrative privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return The protocol reply
     */
    public XSPReply grantDBAdmin(User client, User user, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            boolean success = changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_ADMINOF, true);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Revokes database administrative privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return The protocol reply
     */
    public XSPReply revokeDBAdmin(User client, User user, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            boolean success = changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_ADMINOF, false);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Grants database reading privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return The protocol reply
     */
    public XSPReply grantDBRead(User client, User user, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            boolean success = changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_CANREAD, true);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Revokes database reading privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return The protocol reply
     */
    public XSPReply revokeDBRead(User client, User user, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            boolean success = changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_CANREAD, false);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }


    /**
     * Grants database writing privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return The protocol reply
     */
    public XSPReply grantDBWrite(User client, User user, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            boolean success = changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_CANWRITE, true);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Revokes database writing privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return The protocol reply
     */
    public XSPReply revokeDBWrite(User client, User user, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            boolean success = changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_CANWRITE, false);
            return success ? XSPReplySuccess.instance() : XSPReplyFailure.instance();
        }
        return XSPReplyUnauthorized.instance();
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
    private boolean changeUserPrivilege(ProxyObject user, ProxyObject database, String privilege, boolean positive) {
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
            adminDB.repository.getStore().commit();
        }
        return true;
    }

    /**
     * Gets the database for the specified name
     *
     * @param client The requesting client
     * @param name   The name of a database
     * @return The protocol reply
     */
    public XSPReply getDatabase(User client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        Database database = database(name);
        if (database == null)
            return XSPReplyFailure.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE))
            return new XSPReplyResult<>(database);
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Gets the databases on this server
     *
     * @param client The requesting client
     * @return The protocol reply
     */
    public XSPReply getDatabases(User client) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        Collection<Database> result = new ArrayList<>();
        synchronized (databases) {
            if (checkIsServerAdmin(client)) {
                result.addAll(databases.values());
                return new XSPReplyResult<>(result);
            }
            for (Database database : databases.values()) {
                if (checkIsServerAdmin(client)
                        || checkIsDBAdmin(client, database)
                        || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)
                        || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE))
                    result.add(database);
            }
        }
        return new XSPReplyResult<>(result);
    }

    /**
     * Creates a new database
     *
     * @param client The requesting client
     * @param name   The name of the database
     * @return The protocol reply
     */
    public XSPReply createDatabase(User client, String name) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (!checkIsServerAdmin(client))
            return XSPReplyUnauthorized.instance();
        if (!name.matches("[_a-zA-Z0-9]+"))
            return new XSPReplyFailure("Database name does not match requirements ([_a-zA-Z0-9]+)");

        synchronized (databases) {
            Database result = databases.get(name);
            if (result != null)
                return new XSPReplyFailure("The database already exists");
            File folder = new File(configuration.getDatabasesFolder(), name);
            try {
                ProxyObject proxy = adminDB.repository.resolveProxy(Schema.ADMIN_GRAPH_DBS + name);
                proxy.setValue(Vocabulary.rdfType, adminDB.repository.resolveProxy(Schema.ADMIN_DATABASE));
                proxy.setValue(Schema.ADMIN_NAME, name);
                proxy.setValue(Schema.ADMIN_LOCATION, name);
                result = new Database(folder, proxy);
                adminDB.repository.getStore().commit();
                result.repository.getStore().commit();
                databases.put(name, result);
                return XSPReplySuccess.instance();
            } catch (IOException exception) {
                return XSPReplyFailure.instance();
            }
        }
    }

    /**
     * Drops a new database
     *
     * @param client   The requesting client
     * @param database The database to drop
     * @return The protocol reply
     */
    public XSPReply dropDatabase(User client, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (database == adminDB)
            return XSPReplyFailure.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            synchronized (databases) {
                databases.remove(database.getName());
                File folder = new File(configuration.getDatabasesFolder(), (String) database.proxy.getDataValue(Schema.ADMIN_LOCATION));
                try {
                    database.close();
                } catch (IOException exception) {
                    Logger.DEFAULT.error(exception);
                }
                if (!Program.delete(folder)) {
                    logger.error("Failed to delete " + folder.getAbsolutePath());
                }
                database.proxy.delete();
                adminDB.repository.getStore().commit();
            }
            return XSPReplySuccess.instance();
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Gets the privileges assigned to a users on a database
     *
     * @param client The requesting client
     * @param target The target database
     * @return The protocol reply
     */
    public XSPReply getDatabasePrivileges(User client, Database target) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, target)) {
            DatabasePrivileges result = new DatabasePrivileges();
            for (ProxyObject value : target.proxy.getObjectsFrom(Schema.ADMIN_ADMINOF)) {
                User user = user(value);
                result.add(user, Schema.PRIVILEGE_ADMIN);
            }
            for (ProxyObject value : target.proxy.getObjectsFrom(Schema.ADMIN_CANWRITE)) {
                User user = user(value);
                result.add(user, Schema.PRIVILEGE_WRITE);
            }
            for (ProxyObject value : target.proxy.getObjectsFrom(Schema.ADMIN_CANREAD)) {
                User user = user(value);
                result.add(user, Schema.PRIVILEGE_READ);
            }
            return new XSPReplyResult<>(result);
        }
        return XSPReplyUnauthorized.instance();
    }

    /**
     * Executes a SPARQL command
     *
     * @param client      The requesting client
     * @param database    The target database
     * @param sparql      The SPARQL command
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     * @return The protocol reply
     */
    public XSPReply sparql(User client, Database database, String sparql, List<String> defaultIRIs, List<String> namedIRIs) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)) {
            BufferedLogger bufferedLogger = new BufferedLogger();
            DispatchLogger dispatchLogger = new DispatchLogger(database.logger, bufferedLogger);
            if (defaultIRIs == null)
                defaultIRIs = Collections.emptyList();
            if (namedIRIs == null)
                namedIRIs = Collections.emptyList();
            SPARQLLoader loader = new SPARQLLoader(database.repository.getStore(), defaultIRIs, namedIRIs);
            List<Command> commands = loader.load(dispatchLogger, new StringReader(sparql));
            if (commands == null) {
                // ill-formed request
                dispatchLogger.error("Failed to parse and load the request");
                return new XSPReplyFailure(Program.getLog(bufferedLogger));
            }
            Result result = ResultSuccess.INSTANCE;
            for (Command command : commands) {
                result = command.execute(database.repository);
                if (result.isFailure()) {
                    break;
                }
            }
            return new XSPReplyResult<>(result);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Gets the entailment regime for a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply dbGetEntailmentRegime(User client, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)) {
            return new XSPReplyResult<>(database.getEntailmentRegime());
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Sets the entailment regime for a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param regime   The entailment regime
     * @return The protocol reply
     */
    public XSPReply dbSetEntailmentRegime(User client, Database database, String regime) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            database.setEntailmentRegime(EntailmentRegime.valueOf(regime));
            return XSPReplySuccess.instance();
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Lists all the rules on a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply dbListAllRules(User client, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)) {
            return database.getAllRules();
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Lists the currently active rules on a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @return The protocol reply
     */
    public XSPReply dbListActiveRules(User client, Database database) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)) {
            return database.getActiveRules();
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Adds a new rule to a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param content  The rule's content
     * @param activate Whether to readily activate the rule
     * @return The protocol reply
     */
    public XSPReply dbAddRule(User client, Database database, String content, boolean activate) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            return database.addRule(content, activate);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Removes a rule from a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule's IRI
     * @return The protocol reply
     */
    public XSPReply dbRemoveRule(User client, Database database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            return database.removeRule(rule);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Gets the definition of a rule
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule's IRI
     * @return The protocol reply
     */
    public XSPReply dbGetRuleDefinition(User client, Database database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            return database.getRuleDefinition(rule);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Activates an existing rule in a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule's IRI
     * @return The protocol reply
     */
    public XSPReply dbActivateRule(User client, Database database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            return database.activateRule(rule);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Deactivates an existing rule in a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule's IRI
     * @return The protocol reply
     */
    public XSPReply dbDeactivateRule(User client, Database database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) {
            return database.deactivateRule(rule);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Gets whether a rule in a database is currently active
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule's IRI
     * @return The protocol reply
     */
    public XSPReply dbIsRuleActive(User client, Database database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)) {
            return database.isRuleActive(rule);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Gets the matching status of a rule in a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param rule     The rule's IRI
     * @return The protocol reply
     */
    public XSPReply dbGetRuleStatus(User client, Database database, String rule) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)) {
            return database.getRuleStatus(rule);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    /**
     * Gets the explanation for a quad in a database
     *
     * @param client   The requesting client
     * @param database The target database
     * @param quad     The quad serialization
     * @return The protocol reply
     */
    public XSPReply dbGetQuadExplanation(User client, Database database, String quad) {
        if (client == null)
            return XSPReplyUnauthenticated.instance();
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)) {
            return database.getExplanation(quad);
        } else {
            return XSPReplyUnauthorized.instance();
        }
    }

    @Override
    public void close() throws IOException {
        logger.info("Closing all databases ...");
        for (Map.Entry<String, Database> entry : databases.entrySet()) {
            logger.info("Closing database " + entry.getKey());
            try {
                entry.getValue().close();
            } catch (IOException exception) {
                logger.error(exception);
            }
            logger.info("Closed database " + entry.getKey());
        }
        databases.clear();
        logger.info("All databases closed");
    }

    /**
     * Request the server to shutdown
     */
    protected abstract void onRequestShutdown();

    /**
     * Request the server to restart
     */
    protected abstract void onRequestRestart();
}
