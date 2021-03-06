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

package org.xowl.infra.server;

import fr.cenotelie.commons.utils.IOUtils;
import fr.cenotelie.commons.utils.ini.IniDocument;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.server.standalone.Program;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents the configuration of the server
 *
 * @author Laurent Wouters
 */
public class ServerConfiguration {
    /**
     * Location in the resources of the default configuration file
     */
    private static final String FILE_DEFAULT = "/org/xowl/infra/server/config/default.ini";
    /**
     * Name of the configuration file in a startup folder
     */
    private static final String FILE_NAME = "xowl-server.ini";

    /**
     * The default configuration
     */
    private final IniDocument confDefault;
    /**
     * The current configuration file
     */
    private final IniDocument confFile;
    /**
     * The directory from which the server has been started
     */
    private final File startupLocation;
    /**
     * The root folder for this server's databases
     */
    private final File root;

    /**
     * Initializes this configuration
     *
     * @param startupDirectory The directory from which the server is starting up, or null for the current directory
     */
    public ServerConfiguration(String startupDirectory) {
        confDefault = new IniDocument();
        confFile = new IniDocument();
        InputStream stream = Program.class.getResourceAsStream(FILE_DEFAULT);
        try {
            confDefault.load(stream, IOUtils.CHARSET);
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
        startupLocation = startupDirectory != null ? new File(startupDirectory) : new File(System.getProperty("user.dir"));
        File file = new File(startupLocation, FILE_NAME);
        try {
            if (file.exists()) {
                confFile.load(file);
            }
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
        root = new File(startupLocation, getValue(null, "repository"));
        if (!root.exists()) {
            if (!root.mkdirs()) {
                Logging.get().error("Failed to create the repository folder for the databases");
            }
        }
    }

    /**
     * Gets the value for a property
     *
     * @param section  A configuration section
     * @param property A configuration property
     * @return The associated value
     */
    private String getValue(String section, String property) {
        String result = confFile.get(section, property);
        if (result != null)
            return result;
        return confDefault.get(section, property);
    }

    /**
     * Gets the folder from which this server was started
     *
     * @return The folder from which this server was started
     */
    public File getStartupFolder() {
        return startupLocation;
    }

    /**
     * Gets the root folder for this server's databases
     *
     * @return The root folder for the databases
     */
    public File getDatabasesFolder() {
        return root;
    }

    /**
     * Gets the default maximum of concurrent threads for a database
     *
     * @return The default maximum of concurrent threads for a database
     */
    public int getDefaultMaxThreads() {
        return Integer.parseInt(getValue(null, "defaultMaxThreads"));
    }

    /**
     * Gets the name of the administration database
     *
     * @return The name of the administration database
     */
    public String getAdminDBName() {
        return getValue("admin", "dbName");
    }

    /**
     * Gets the name of the default administrator account
     *
     * @return The name of the default administrator account
     */
    public String getAdminDefaultUser() {
        return getValue("admin", "defaultUser");
    }

    /**
     * Gets the password of the default administrator account
     *
     * @return The password of the default administrator account
     */
    public String getAdminDefaultPassword() {
        return getValue("admin", "defaultPassword");
    }

    /**
     * Gets the minimum length of a user password
     *
     * @return The minimum length of a user password
     */
    public int getSecurityMinPasswordLength() {
        return Integer.parseInt(getValue("security", "minPasswordLength"));
    }

    /**
     * Gets the number of cycles for the bcrpyt hash function
     *
     * @return The number of cycles
     */
    public int getSecurityBCryptCycleCount() {
        return Integer.parseInt(getValue("security", "bcryptCycle"));
    }

    /**
     * Gets the maximal number of login attempt before banning a client
     *
     * @return The maximal number of login attempt
     */
    public int getSecurityMaxLoginAttempt() {
        return Integer.parseInt(getValue("security", "maxLoginAttempt"));
    }

    /**
     * Gets the length of client ban in seconds
     *
     * @return The length of client ban in seconds
     */
    public int getSecurityBanLength() {
        return Integer.parseInt(getValue("security", "banLength"));
    }

    /**
     * Gets the time to live in seconds of an authentication token
     *
     * @return The time to live in seconds of an authentication token
     */
    public int getSecurityTokenTTL() {
        return Integer.parseInt(getValue("security", "tokenTTL"));
    }

    /**
     * Gets the security realm for this server
     *
     * @return The security realm for this server
     */
    public String getSecurityRealm() {
        return getValue("security", "realm");
    }

    /**
     * Gets the path to the key store for SSL certificates
     *
     * @return The path to the key store
     */
    public String getSecurityKeyStore() {
        return getValue("security", "keyStore");
    }

    /**
     * Gets the password for the key store
     *
     * @return The password for the key store
     */
    public String getSecurityKeyStorePassword() {
        return getValue("security", "keyStorePassword");
    }

    /**
     * Gets whether to use the HTTPS protocol
     *
     * @return Whether to use the HTTPS protocol
     */
    public boolean getHttpSecure() {
        return "true".equalsIgnoreCase(getValue("http", "address"));
    }

    /**
     * Gets the address to bind for the HTTP server
     *
     * @return The address to bind
     */
    public String getHttpAddress() {
        return getValue("http", "address");
    }

    /**
     * Gets the port to bind for the HTTP server
     *
     * @return The port to bind
     */
    public int getHttpPort() {
        return Integer.parseInt(getValue("http", "port"));
    }

    /**
     * Gets the maximum backlog for the HTTP server
     * This is the maximum number of queued incoming connections to allow on the listening socket.
     * 0 or less indicates a system-specific value.
     *
     * @return The maximum backlog for the HTTP server
     */
    public int getHttpBacklog() {
        return Integer.parseInt(getValue("http", "backlog"));
    }

    /**
     * Gets the timeout when stopping the HTTP server
     *
     * @return The timeout when stopping the HTTP server
     */
    public int getHttpStopTimeout() {
        return Integer.parseInt(getValue("http", "stopTimeout"));
    }

    /**
     * Gets the name of the public database that contains linked data
     *
     * @return The name of the public database that contains linked data
     */
    public String getLinkedDataPublicDb() {
        return getValue("linkedData", "publicDb");
    }

    /**
     * Gets the name of the user to use for anonymous connection to the linked data
     *
     * @return The name of the user to use for anonymous connection to the linked data
     */
    public String getLinkedDataPublicUser() {
        return getValue("linkedData", "publicUser");
    }

    /**
     * Gets the URI prefix to be used for linked data resources
     *
     * @return The URI prefix to be used for linked data resources
     */
    public String getLinkedDataPrefix() {
        return getValue("linkedData", "prefix");
    }

    /**
     * Registers in the configuration the location and password for the key store
     *
     * @param location The location to the key store
     * @param password the password to the key store
     */
    public void setupKeyStore(String location, String password) {
        confFile.add("security", "keyStore", location);
        confFile.add("security", "keyStorePassword", password);
        try {
            confFile.save(new File(startupLocation, FILE_NAME));
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
    }
}
