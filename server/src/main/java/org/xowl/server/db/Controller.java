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
import org.xowl.utils.ConsoleLogger;
import org.xowl.utils.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * The main controller for the hosted databases
 *
 * @author Laurent Wouters
 */
public class Controller implements Closeable {
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
     * The User graph in the administration database
     */
    public static final String SCHEMA_ADMIN_USERS = "http://xowl.org/server/users#";
    /**
     * The Database graph in the administration database
     */
    public static final String SCHEMA_ADMIN_DBS = "http://xowl.org/server/db#";

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
        this.adminDB = new Database(configuration, configuration.getRoot());
        this.databases.put(configuration.getAdminDBName(), adminDB);
        init();
    }

    /**
     * Initializes this controller
     */
    private void init() {
        logger.info("Initializing the controller");
        ProxyObject classDB = adminDB.getRepository().resolveProxy(SCHEMA_ADMIN_DATABASE);
        for (ProxyObject poDB : classDB.getInstances()) {
            logger.info("Found database " + poDB.getIRIString());
            String name = (String) poDB.getDataValue(SCHEMA_ADMIN_NAME);
            String location = (String) poDB.getDataValue(SCHEMA_ADMIN_LOCATION);
            try {
                Database db = new Database(configuration, new File(configuration.getRoot(), location));
                databases.put(name, db);
                logger.error("Loaded database " + poDB.getIRIString() + " as " + name);
            } catch (IOException exception) {
                // do nothing, this exception is reported by the db logger
                logger.error("Failed to load database " + poDB.getIRIString() + " as " + name);
            }
        }
    }

    /**
     * Characters for session tokens
     */
    private static final char[] TOKEN_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Generates a new session token
     *
     * @return A new session token
     */
    private static String newToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        char[] chars = new char[bytes.length * 2];
        int j = 0;
        for (int i = 0; i != bytes.length; i++) {
            chars[j++] = TOKEN_CHARS[(bytes[i] & 0xF0) >>> 4];
            chars[j++] = TOKEN_CHARS[bytes[i] & 0x0F];
        }
        return new String(chars);
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
    public Database newDatabase(String name) {
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
            result = new Database(configuration, folder);
            ProxyObject proxy = adminDB.getRepository().resolveProxy(SCHEMA_ADMIN_DBS + name);
            proxy.setValue(SCHEMA_ADMIN_NAME, name);
            proxy.setValue(SCHEMA_ADMIN_LOCATION, folder.getAbsolutePath());
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
     * Login a user
     *
     * @param login    The user to log in
     * @param password The user password
     * @return Whether the login/password is acceptable
     */
    public boolean login(String login, String password) {
        logger.info("Login attempt for " + login);
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            logger.info("Login failure for " + login);
            return false;
        }
        String userIRI = SCHEMA_ADMIN_USERS + login;
        ProxyObject proxy = adminDB.getRepository().getProxy(userIRI);
        if (proxy == null) {
            logger.info("Login failure for " + login);
            return false;
        }
        String hash = (String) proxy.getDataValue(SCHEMA_ADMIN_PASSWORD);
        if (!BCrypt.checkpw(password, hash)) {
            logger.info("Login failure for " + login);
            return false;
        } else {
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
    public boolean newUser(String login, String password) {
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
        proxy.setValue(SCHEMA_ADMIN_NAME, login);
        proxy.setValue(SCHEMA_ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
        adminDB.getRepository().getStore().commit();
        logger.info("Created new user \"" + login + "\"");
        return true;
    }

    @Override
    public void close() throws IOException {
        logger.info("Closing all databases ...");
        adminDB.close();
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
}
