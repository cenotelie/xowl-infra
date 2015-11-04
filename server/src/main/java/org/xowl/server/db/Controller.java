/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors:
 * Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.server.db;

import org.mindrot.jbcrypt.BCrypt;
import org.xowl.server.ServerConfiguration;
import org.xowl.store.ProxyObject;
import org.xowl.utils.ConsoleLogger;
import org.xowl.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The main controller for the hosted databases
 *
 * @author Laurent Wouters
 */
public class Controller {
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
     * The User concept in the administration database
     */
    public static final String SCHEMA_ADMIN_USERS = "http://xowl.org/server/users#";

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
        init();
    }

    /**
     * Initializes this controller
     */
    private void init() {
        ProxyObject classDB = adminDB.getRepository().resolveProxy(SCHEMA_ADMIN_DATABASE);
        for (ProxyObject poDB : classDB.getInstances()) {
            String name = (String) poDB.getDataValue(SCHEMA_ADMIN_NAME);
            String location = (String) poDB.getDataValue(SCHEMA_ADMIN_LOCATION);
            try {
                Database db = new Database(configuration, new File(configuration.getRoot(), location));
                databases.put(name, db);
            } catch (IOException exception) {
                // do nothing, this exception is reported by the db logger
            }
        }
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
        Database result = databases.get(name);
        if (result != null)
            return result;
        File folder = new File(configuration.getRoot(), name);
        try {
            result = new Database(configuration, folder);
        } catch (IOException exception) {
            // do nothing, this exception is reported by the db logger
        }
        if (result != null)
            databases.put(name, result);
        return result;
    }

    /**
     * Login a user
     *
     * @param login    The user to log in
     * @param password The user password
     * @return The user if the operation is successful, null otherwise
     */
    public User login(String login, String password) {
        String userIRI = SCHEMA_ADMIN_USERS + login;
        ProxyObject proxy = adminDB.getRepository().getProxy(userIRI);
        if (proxy == null)
            return null;
        String hash = (String) proxy.getDataValue(SCHEMA_ADMIN_PASSWORD);
        return BCrypt.checkpw(password, hash) ? new User(proxy) : null;
    }

    /**
     * Gets a user
     *
     * @param login The user's login
     * @return The user
     */
    public User getUser(String login) {
        String userIRI = SCHEMA_ADMIN_USERS + login;
        ProxyObject proxy = adminDB.getRepository().getProxy(userIRI);
        return proxy != null ? new User(proxy) : null;
    }

    /**
     * Creates a new user for this server
     *
     * @param login    The login
     * @param password The password
     * @return The new user
     */
    public User newUser(String login, String password) {
        if (password.length() < configuration.getSecurityMinPasswordLength()) {
            logger.error("Failed to create user: password does not meet expectations");
            return null;
        }
        String userIRI = SCHEMA_ADMIN_USERS + login;
        ProxyObject proxy = adminDB.getRepository().getProxy(userIRI);
        if (proxy != null) {
            // user already exist
            logger.error("Failed to create user: user already exist");
            return null;
        }
        proxy = adminDB.getRepository().resolveProxy(userIRI);
        proxy.setValue(SCHEMA_ADMIN_NAME, login);
        proxy.setValue(SCHEMA_ADMIN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt(configuration.getSecurityBCryptCycleCount())));
        return new User(proxy);
    }
}
