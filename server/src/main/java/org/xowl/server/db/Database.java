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

import org.xowl.server.ServerConfiguration;
import org.xowl.store.ProxyObject;
import org.xowl.store.Repository;
import org.xowl.store.storage.BaseStore;
import org.xowl.store.storage.StoreFactory;
import org.xowl.utils.ConsoleLogger;
import org.xowl.utils.Logger;
import org.xowl.utils.config.Configuration;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Represents a database hosted on this server
 *
 * @author Laurent Wouters
 */
public class Database implements Closeable {
    /**
     * The configuration file for a repository
     */
    private static final String REPO_CONF_NAME = "config.ini";

    /**
     * The logger
     */
    private final Logger logger;
    /**
     * The repository
     */
    private final Repository repository;
    /**
     * The current configuration for this database
     */
    private final Configuration configuration;
    /**
     * The proxy object representing this database
     */
    final ProxyObject proxy;

    /**
     * Initializes this database (as the admin database)
     *
     * @param confServer The server configuration
     * @param location   The database's location
     * @throws IOException When the location cannot be accessed
     */
    public Database(ServerConfiguration confServer, File location) throws IOException {
        this.logger = new ConsoleLogger();
        this.configuration = new Configuration();
        if (!location.exists()) {
            if (!location.mkdirs()) {
                throw error(logger, "Failed to create the directory for repository at " + location.getPath());
            }
        }
        File configFile = new File(location, REPO_CONF_NAME);
        if (configFile.exists()) {
            configuration.load(configFile.getAbsolutePath(), Charset.forName("UTF-8"));
        }
        String cBackend = configuration.getValue("storage");
        BaseStore store = Objects.equals(cBackend, "memory") ? StoreFactory.newInMemoryStore() : StoreFactory.newFileStore(location);
        this.repository = new Repository(store);
        this.proxy = repository.resolveProxy(Controller.SCHEMA_ADMIN_DBS + confServer.getAdminDBName());
    }

    /**
     * Initializes this database
     *
     * @param location The database's location
     * @param proxy    The proxy object representing this database
     * @throws IOException When the location cannot be accessed
     */
    public Database(File location, ProxyObject proxy) throws IOException {
        this.logger = new ConsoleLogger();
        this.configuration = new Configuration();
        if (!location.exists()) {
            if (!location.mkdirs()) {
                throw error(logger, "Failed to create the directory for repository at " + location.getPath());
            }
        }
        File configFile = new File(location, REPO_CONF_NAME);
        if (configFile.exists()) {
            configuration.load(configFile.getAbsolutePath(), Charset.forName("UTF-8"));
        }
        String cBackend = configuration.getValue("storage");
        BaseStore store = Objects.equals(cBackend, "memory") ? StoreFactory.newInMemoryStore() : StoreFactory.newFileStore(location);
        this.repository = new Repository(store);
        this.proxy = proxy;
    }

    /**
     * Prepares a new exception to be thrown
     *
     * @param logger  The current logger
     * @param message The message for the exception
     * @return The exception
     */
    private static IOException error(Logger logger, String message) {
        IOException result = new IOException(message);
        logger.error(result);
        return result;
    }

    /**
     * Gets the repository
     *
     * @return The repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Gets the logger for this database
     *
     * @return The logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets the name of this user
     *
     * @return The name of this user
     */
    public String getName() {
        return (String) proxy.getDataValue(Controller.SCHEMA_ADMIN_NAME);
    }

    @Override
    public void close() throws IOException {
        try {
            repository.getStore().close();
        } catch (Exception exception) {
            logger.error(exception);
        }
    }
}
