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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The main controller for the hosted databases
 *
 * @author Laurent Wouters
 */
public abstract class Controller implements Closeable {
    /**
     * The User concept in the administration database
     */
    public static final String SCHEMA_ADMIN_USER = "http://xowl.org/server/admin#User";
    /**
     * The Database concept in the administration database
     */
    public static final String SCHEMA_ADMIN_DATABASE = "http://xowl.org/server/admin#Database";
    /**
     * The Name concept in the administration database
     */
    public static final String SCHEMA_ADMIN_NAME = "http://xowl.org/server/admin#name";
    /**
     * The Location concept in the administration database
     */
    public static final String SCHEMA_ADMIN_LOCATION = "http://xowl.org/server/admin#location";
    /**
     * The Password concept in the administration database
     */
    public static final String SCHEMA_ADMIN_PASSWORD = "http://xowl.org/server/admin#password";
    /**
     * The AdminOf concept in the administration database
     */
    public static final String SCHEMA_ADMIN_ADMINOF = "http://xowl.org/server/admin#adminOf";
    /**
     * The CanRead concept in the administration database
     */
    public static final String SCHEMA_ADMIN_CANREAD = "http://xowl.org/server/admin#canRead";
    /**
     * The CanWrite concept in the administration database
     */
    public static final String SCHEMA_ADMIN_CANWRITE = "http://xowl.org/server/admin#canWrite";
    /**
     * The User graph in the administration database
     */
    public static final String SCHEMA_ADMIN_USERS = "http://xowl.org/server/users#";
    /**
     * The Database graph in the administration database
     */
    public static final String SCHEMA_ADMIN_DBS = "http://xowl.org/server/db#";

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
        if (isEmpty) {
            adminDB.proxy.setValue(Vocabulary.rdfType, adminDB.getRepository().resolveProxy(SCHEMA_ADMIN_DATABASE));
            adminDB.proxy.setValue(SCHEMA_ADMIN_NAME, configuration.getAdminDBName());
            adminDB.proxy.setValue(SCHEMA_ADMIN_LOCATION, ".");
            newUser(configuration.getAdminDefaultUser(), configuration.getAdminDefaultPassword());
            User admin = getUser(configuration.getAdminDefaultUser());
            admin.proxy.setValue(SCHEMA_ADMIN_ADMINOF, adminDB.proxy);
            admin.proxy.setValue(SCHEMA_ADMIN_CANREAD, adminDB.proxy);
            admin.proxy.setValue(SCHEMA_ADMIN_CANWRITE, adminDB.proxy);
            adminDB.getRepository().getStore().commit();
        } else {
            ProxyObject classDB = adminDB.getRepository().resolveProxy(SCHEMA_ADMIN_DATABASE);
            for (ProxyObject poDB : classDB.getInstances()) {
                if (poDB == adminDB.proxy)
                    continue;
                logger.info("Found database " + poDB.getIRIString());
                String name = (String) poDB.getDataValue(SCHEMA_ADMIN_NAME);
                String location = (String) poDB.getDataValue(SCHEMA_ADMIN_LOCATION);
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
     * Gets the database for the specified name
     *
     * @param name The name of a database
     * @return The database for the specified name
     */
    public Database getDatabase(String name) {
        return databases.get(name);
    }

    /**
     * Creates a new database
     *
     * @param name The name of the database
     * @return The created database
     */
    public synchronized Database newDatabase(String name) {
        logger.info("Creating new database \"" + name + "\" ...");
        if (!name.matches("[_a-zA-Z0-9]+")) {
            logger.error("Failed to create database \"" + name + "\": name does not meet expectations");
            return null;
        }
        Database result = databases.get(name);
        if (result != null) {
            logger.warning("Database already exist: \"" + name + "\"");
            return result;
        }
        File folder = new File(configuration.getRoot(), name);
        try {
            ProxyObject proxy = adminDB.getRepository().resolveProxy(SCHEMA_ADMIN_DBS + name);
            proxy.setValue(Vocabulary.rdfType, adminDB.getRepository().resolveProxy(SCHEMA_ADMIN_DATABASE));
            proxy.setValue(SCHEMA_ADMIN_NAME, name);
            proxy.setValue(SCHEMA_ADMIN_LOCATION, folder.getAbsolutePath());
            result = new Database(folder, proxy);
            adminDB.getRepository().getStore().commit();
        } catch (IOException exception) {
            // do nothing, this exception is reported by the db logger
            logger.error("Failed to create database \"" + name + "\"");
            return null;
        }
        databases.put(name, result);
        logger.info("Created new database \"" + name + "\"");
        return result;
    }

    /**
     * Gets whether a client is banned
     *
     * @param client A client
     * @return Wether the client is banned
     */
    public boolean isBanned(InetAddress client) {
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

    /**
     * Handles a login failure from a client
     *
     * @param client The client trying to login
     */
    private void onLoginFailure(InetAddress client) {
        ClientLogin cl = clients.get(client);
        if (cl == null) {
            cl = new ClientLogin();
            clients.put(client, cl);
        }
        cl.failedAttempt++;
        if (cl.failedAttempt >= configuration.getSecurityMaxLoginAttempt()) {
            // too much failure, ban this client for a while
            cl.banTimeStamp = Calendar.getInstance().getTime().getTime();
        }
    }

    /**
     * Login a user
     *
     * @param client   The client trying to login
     * @param login    The user to log in
     * @param password The user password
     * @return Whether the login/password is acceptable
     */
    public boolean login(InetAddress client, String login, String password) {
        if (isBanned(client))
            return false;
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            onLoginFailure(client);
            logger.info("Login failure for " + login + " from " + client.toString());
            return false;
        }
        String userIRI = SCHEMA_ADMIN_USERS + login;
        ProxyObject proxy = adminDB.getRepository().getProxy(userIRI);
        if (proxy == null) {
            onLoginFailure(client);
            logger.info("Login failure for " + login);
            return false;
        }
        String hash = (String) proxy.getDataValue(SCHEMA_ADMIN_PASSWORD);
        if (!BCrypt.checkpw(password, hash)) {
            onLoginFailure(client);
            logger.info("Login failure for " + login);
            return false;
        } else {
            clients.remove(client);
            logger.info("Login success for " + login);
            return true;
        }
    }

    /**
     * Gets the user for the specified login
     *
     * @param login The user's login
     * @return The user
     */
    public User getUser(String login) {
        String userIRI = SCHEMA_ADMIN_USERS + login;
        ProxyObject proxy = adminDB.getRepository().getProxy(userIRI);
        return proxy == null ? null : new User(proxy);
    }

    /**
     * Creates a new user for this server
     *
     * @param login    The login
     * @param password The password
     * @return Whether the operation succeeded
     */
    public synchronized boolean newUser(String login, String password) {
        logger.info("Creating new user \"" + login + "\" ...");
        if (!login.matches("[_a-zA-Z0-9]+")) {
            logger.error("Failed to create user \"" + login + "\": login does not meet expectations");
            return false;
        }
        if (password.length() < configuration.getSecurityMinPasswordLength()) {
            logger.error("Failed to create user \"" + login + "\": password does not meet expectations");
            return false;
        }
        String userIRI = SCHEMA_ADMIN_USERS + login;
        ProxyObject proxy = adminDB.getRepository().getProxy(userIRI);
        if (proxy != null) {
            // user already exist
            logger.error("Failed to create user \"" + login + "\": user already exist");
            return false;
        }
        proxy = adminDB.getRepository().resolveProxy(userIRI);
        proxy.setValue(Vocabulary.rdfType, adminDB.getRepository().resolveProxy(SCHEMA_ADMIN_USER));
        proxy.setValue(SCHEMA_ADMIN_NAME, login);
        proxy.setValue(SCHEMA_ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
        adminDB.getRepository().getStore().commit();
        logger.info("Created new user \"" + login + "\"");
        return true;
    }

    /**
     * Makes a user an administrator of a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean makeUserAdmin(User user, Database database) {
        return changeUserPriviliedge(user.proxy, database.proxy, SCHEMA_ADMIN_ADMINOF, true);
    }

    /**
     * Revokes a user as an administrator of a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean revokeUserAdmin(User user, Database database) {
        return changeUserPriviliedge(user.proxy, database.proxy, SCHEMA_ADMIN_ADMINOF, false);
    }

    /**
     * Makes a user able to read a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean addUserCanRead(User user, Database database) {
        return changeUserPriviliedge(user.proxy, database.proxy, SCHEMA_ADMIN_CANREAD, true);
    }

    /**
     * Revokes a user the ability to read from a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean revokeUserCanRead(User user, Database database) {
        return changeUserPriviliedge(user.proxy, database.proxy, SCHEMA_ADMIN_CANREAD, false);
    }

    /**
     * Makes a user able to write a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean addUserCanWrite(User user, Database database) {
        return changeUserPriviliedge(user.proxy, database.proxy, SCHEMA_ADMIN_CANWRITE, true);
    }

    /**
     * Revokes a user the ability to write to a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the operation succeeded
     */
    public boolean revokeUserCanWrite(User user, Database database) {
        return changeUserPriviliedge(user.proxy, database.proxy, SCHEMA_ADMIN_CANWRITE, false);
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
    private synchronized boolean changeUserPriviliedge(ProxyObject user, ProxyObject database, String privilege, boolean positive) {
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
        return true;
    }

    /**
     * Gets whether the user is a server administrator
     *
     * @param user The user
     * @return Whether the user is a server administrator
     */
    public boolean isServerAdmin(User user) {
        return isAllowed(user.proxy, adminDB.proxy, SCHEMA_ADMIN_ADMINOF);
    }

    /**
     * Gets whether the user is the administrator of a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the user is an administrator
     */
    public boolean isAdminOf(User user, Database database) {
        return isAllowed(user.proxy, database.proxy, SCHEMA_ADMIN_ADMINOF);
    }

    /**
     * Gets whether the user can read a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the user can read a database
     */
    public boolean canRead(User user, Database database) {
        return isAllowed(user.proxy, database.proxy, SCHEMA_ADMIN_CANREAD);
    }

    /**
     * Gets whether the user can write to a database
     *
     * @param user     The user
     * @param database The database
     * @return Whether the user can write to a database
     */
    public boolean canWrite(User user, Database database) {
        return isAllowed(user.proxy, database.proxy, SCHEMA_ADMIN_CANWRITE);
    }

    /**
     * Gets whether a user is granted a privilege on a database
     *
     * @param user      The user
     * @param database  The database
     * @param privilege The privilege
     * @return Whether the user is allowed
     */
    private synchronized boolean isAllowed(ProxyObject user, ProxyObject database, String privilege) {
        Collection<ProxyObject> dbs = user.getObjectValues(privilege);
        return dbs.contains(database);
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
