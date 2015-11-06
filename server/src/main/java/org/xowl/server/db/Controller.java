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
import org.xowl.server.ServerConfiguration;
import org.xowl.store.ProxyObject;
import org.xowl.store.Vocabulary;
import org.xowl.utils.ConsoleLogger;
import org.xowl.utils.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
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
        if (configuration.getRoot().exists()) {
            String[] children = configuration.getRoot().list();
            isEmpty = children == null || children.length == 0;
        }
        logger.info("Initializing the controller");
        adminDB = new Database(configuration, configuration.getRoot());
        databases.put(configuration.getAdminDBName(), adminDB);
        clients = new HashMap<>();
        users = new HashMap<>();
        if (isEmpty) {
            adminDB.proxy.setValue(Vocabulary.rdfType, adminDB.repository.resolveProxy(Schema.ADMIN_DATABASE));
            adminDB.proxy.setValue(Schema.ADMIN_NAME, configuration.getAdminDBName());
            adminDB.proxy.setValue(Schema.ADMIN_LOCATION, ".");
            User admin = doCreateUser(configuration.getAdminDefaultUser(), configuration.getAdminDefaultPassword());
            users.put(admin.getName(), admin);
            admin.proxy.setValue(Schema.ADMIN_ADMINOF, adminDB.proxy);
            admin.proxy.setValue(Schema.ADMIN_CANREAD, adminDB.proxy);
            admin.proxy.setValue(Schema.ADMIN_CANWRITE, adminDB.proxy);
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
                    Database db = new Database(new File(configuration.getRoot(), location), poDB);
                    databases.put(name, db);
                    logger.error("Loaded database " + poDB.getIRIString() + " as " + name);
                } catch (IOException exception) {
                    // do nothing, this exception is reported by the db logger
                    logger.error("Failed to load database " + poDB.getIRIString() + " as " + name);
                }
            }
        }
        logger.info("Controller is ready");
    }

    /**
     * Gets whether a client is banned
     *
     * @param client A client
     * @return Wether the client is banned
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
     * @return The user of the login is successful, null otherwise
     */
    public User login(InetAddress client, String login, String password) {
        if (isBanned(client))
            return null;
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            onLoginFailure(client);
            logger.info("Login failure for " + login + " from " + client.toString());
            return null;
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
            onLoginFailure(client);
            logger.info("Login failure for " + login);
            return null;
        }
        if (!BCrypt.checkpw(password, hash)) {
            onLoginFailure(client);
            logger.info("Login failure for " + login);
            return null;
        } else {
            synchronized (clients) {
                clients.remove(client);
            }
            logger.info("Login success for " + login);
            return null;
        }
    }

    /**
     * Handles a login failure from a client
     *
     * @param client The client trying to login
     */
    private void onLoginFailure(InetAddress client) {
        synchronized (clients) {
            ClientLogin cl = clients.get(client);
            if (cl == null) {
                cl = new ClientLogin();
                clients.put(client, cl);
            }
            cl.failedAttempt++;
            if (cl.failedAttempt >= configuration.getSecurityMaxLoginAttempt()) {
                // too much failure, ban this client for a while
                logger.info("Banned client " + client.toString() + " for " + configuration.getSecurityBanLength() + " seconds");
                cl.banTimeStamp = Calendar.getInstance().getTime().getTime();
            }
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
     * Gets the user for the specified login
     *
     * @param client The client requesting the information
     * @param login  The user's login
     * @return The user, or null if the information is not available
     */
    public User getUser(User client, String login) {
        if (!checkIsServerAdmin(client))
            return null;
        String userIRI = Schema.ADMIN_GRAPH_USERS + login;
        ProxyObject proxy;
        String name = null;
        synchronized (adminDB) {
            proxy = adminDB.repository.getProxy(userIRI);
            if (proxy != null)
                name = (String) proxy.getDataValue(Schema.ADMIN_NAME);
        }
        if (proxy == null)
            return null;
        User result = users.get(name);
        if (result == null) {
            result = new User(proxy);
            users.put(name, result);
        }
        return result;
    }

    /**
     * Gets the users on this server
     *
     * @param client The client requesting the information
     * @return The users
     */
    public List<User> getUsers(User client) {
        List<User> result = new ArrayList<>();
        if (!checkIsServerAdmin(client))
            return result;
        synchronized (adminDB) {
            ProxyObject classUser = adminDB.repository.resolveProxy(Schema.ADMIN_USER);
            for (ProxyObject poUser : classUser.getInstances()) {
                String name = (String) poUser.getDataValue(Schema.ADMIN_NAME);
                User user = users.get(name);
                if (user == null) {
                    user = new User(poUser);
                    users.put(name, user);
                }
                result.add(user);
            }
        }
        return result;
    }

    /**
     * Creates a new user for this server
     *
     * @param client   The requesting client
     * @param login    The login
     * @param password The password
     * @return Whether the operation succeeded
     */
    public User createUser(User client, String login, String password) {
        if (!checkIsServerAdmin(client))
            return null;
        if (!login.matches("[_a-zA-Z0-9]+"))
            return null;
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return null;
        return doCreateUser(login, password);
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
            if (proxy == null)
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
     * @return Whether the operation succeeded
     */
    public boolean deleteUser(User client, User toDelete) {
        if (!checkIsServerAdmin(client))
            return false;
        synchronized (users) {
            users.remove(toDelete.getName());
        }
        synchronized (adminDB) {
            toDelete.proxy.delete();
            adminDB.repository.getStore().commit();
        }
        return true;
    }

    /**
     * Changes the password of the requesting user
     *
     * @param user     The user
     * @param password The new password
     * @return Whether the operation succeeded
     */
    public boolean changePassword(User user, String password) {
        if (password.length() < configuration.getSecurityMinPasswordLength())
            return false;
        synchronized (adminDB) {
            user.proxy.unset(Schema.ADMIN_PASSWORD);
            user.proxy.setValue(Schema.ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
            adminDB.repository.getStore().commit();
        }
        return true;
    }

    /**
     * Resets the password of a target user
     *
     * @param client   The requesting client
     * @param target   The user for which the password should be changed
     * @param password The new password
     * @return Whether the operation succeeded
     */
    public boolean resetPassword(User client, User target, String password) {
        return checkIsServerAdmin(client) && changePassword(target, password);
    }

    /**
     * Grants server administrative privilege to a target user
     *
     * @param client The requesting client
     * @param target The target user
     * @return Whether the operation succeeded
     */
    public boolean grantServerAdmin(User client, User target) {
        return checkIsServerAdmin(client) && changeUserPrivilege(target.proxy, adminDB.proxy, Schema.ADMIN_ADMINOF, true);
    }

    /**
     * Revokes server administrative privilege to a target user
     *
     * @param client The requesting client
     * @param target The target user
     * @return Whether the operation succeeded
     */
    public boolean revokeServerAdmin(User client, User target) {
        return checkIsServerAdmin(client) && changeUserPrivilege(target.proxy, adminDB.proxy, Schema.ADMIN_ADMINOF, false);
    }

    /**
     * Grants database administrative privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean grantDBAdmin(User client, User user, Database database) {
        return (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) && changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_ADMINOF, true);
    }

    /**
     * Revokes database administrative privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean revokeDBAdmin(User client, User user, Database database) {
        return (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) && changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_ADMINOF, false);
    }

    /**
     * Grants database reading privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean grantDBRead(User client, User user, Database database) {
        return (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) && changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_CANREAD, true);
    }

    /**
     * Revokes database reading privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean revokeDBRead(User client, User user, Database database) {
        return (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) && changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_CANREAD, false);
    }


    /**
     * Grants database writing privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean grantDBWrite(User client, User user, Database database) {
        return (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) && changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_CANWRITE, true);
    }

    /**
     * Revokes database writing privilege to a target user
     *
     * @param client   The requesting client
     * @param user     The target user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean revokeDBWrite(User client, User user, Database database) {
        return (checkIsServerAdmin(client) || checkIsDBAdmin(client, database)) && changeUserPrivilege(user.proxy, database.proxy, Schema.ADMIN_CANWRITE, false);
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
                user.setValue(privilege, database);
            } else {
                if (!dbs.contains(database))
                    return false;
                user.unset(privilege, database);
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
     * @return The database for the specified name
     */
    public Database getDatabase(User client, String name) {
        Database database;
        synchronized (databases) {
            database = databases.get(name);
        }
        if (database == null)
            return null;
        if (checkIsServerAdmin(client)
                || checkIsDBAdmin(client, database)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)
                || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE))
            return database;
        return null;
    }

    /**
     * Gets the databases on this server
     *
     * @param client The requesting client
     * @return The databases
     */
    public List<Database> getDatabases(User client) {
        List<Database> result = new ArrayList<>();
        if (checkIsServerAdmin(client)) {
            result.addAll(databases.values());
            return result;
        }
        synchronized (databases) {
            for (Database database : databases.values()) {
                if (checkIsServerAdmin(client)
                        || checkIsDBAdmin(client, database)
                        || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANREAD)
                        || checkIsAllowed(client.proxy, database.proxy, Schema.ADMIN_CANWRITE))
                    result.add(database);
            }
        }
        return result;
    }

    /**
     * Creates a new database
     *
     * @param client The requesting client
     * @param name   The name of the database
     * @return The created database
     */
    public Database createDatabase(User client, String name) {
        if (!checkIsServerAdmin(client))
            return null;
        if (!name.matches("[_a-zA-Z0-9]+"))
            return null;

        synchronized (databases) {
            Database result = databases.get(name);
            if (result != null)
                return null;
            File folder = new File(configuration.getRoot(), name);
            try {
                ProxyObject proxy = adminDB.repository.resolveProxy(Schema.ADMIN_GRAPH_DBS + name);
                proxy.setValue(Vocabulary.rdfType, adminDB.repository.resolveProxy(Schema.ADMIN_DATABASE));
                proxy.setValue(Schema.ADMIN_NAME, name);
                proxy.setValue(Schema.ADMIN_LOCATION, folder.getAbsolutePath());
                result = new Database(folder, proxy);
                adminDB.repository.getStore().commit();
                databases.put(name, result);
                return result;
            } catch (IOException exception) {
                return null;
            }
        }
    }

    /**
     * Drops a new database
     *
     * @param client   The requesting client
     * @param database The database to drop
     * @return Whether the operation succeeded
     */
    public boolean dropDatabase(User client, Database database) {
        if (database == adminDB)
            return false;
        if (!checkIsServerAdmin(client) || checkIsDBAdmin(client, database))
            return false;
        synchronized (databases) {
            databases.remove(database.getName());
            File folder = new File((String) database.proxy.getDataValue(Schema.ADMIN_LOCATION));
            try {
                Files.delete(folder.toPath());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            database.proxy.delete();
            adminDB.repository.getStore().commit();
        }
        return true;
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
    public abstract void requestShutdown();

    /**
     * Request the server to restart
     */
    public abstract void requestRestart();
}
