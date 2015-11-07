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

package org.xowl.server;

import org.xowl.utils.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Represents the configuration of the server
 *
 * @author Laurent Wouters
 */
public class ServerConfiguration {
    /**
     * Location in the resources of the default configuration file
     */
    private static final String FILE_DEFAULT = "/org/xowl/server/config/default.ini";
    /**
     * Name of the configuration file in a root folder
     */
    private static final String FILE_NAME = "config.ini";

    /**
     * The default configuration
     */
    private final Configuration confDefault;
    /**
     * The current configuration file
     */
    private final Configuration confFile;
    /**
     * The root folder for this server
     */
    private final File root;

    /**
     * Initializes this configuration
     *
     * @param args The startup arguments
     */
    public ServerConfiguration(String[] args) {
        confDefault = new Configuration();
        confFile = new Configuration();
        InputStream stream = Program.class.getResourceAsStream(FILE_DEFAULT);
        try {
            confDefault.load(stream, Charset.forName("UTF-8"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        root = (args.length > 0) ? new File(args[0]) : new File(System.getProperty("user.dir"));
        File file = new File(root, FILE_NAME);
        try {
            if (file.exists()) {
                confFile.load(file.getAbsolutePath(), Charset.forName("UTF-8"));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
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
     * Gets the root folder for this server
     *
     * @return The root folder
     */
    public File getRoot() {
        return root;
    }

    /**
     * Gets the name of this server
     *
     * @return The name of this server
     */
    public String getServerName() {
        return getValue(null, "serverName");
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
     * Gets the login for the guest account
     *
     * @return The login for the guest account
     */
    public String getHttpGuestLogin() {
        return getValue("http", "guestLogin");
    }

    /**
     * Gets the password for the guest account
     *
     * @return The password for the guest account
     */
    public String getHttpGuestPassword() {
        return getValue("http", "guestPassword");
    }

    /**
     * Gets the address to bind for the XSP server
     *
     * @return The address to bind
     */
    public String getXSPAddress() {
        return getValue("xsp", "address");
    }

    /**
     * Gets the port to bind for the XSP server
     *
     * @return The port to bind
     */
    public int getXSPPort() {
        return Integer.parseInt(getValue("xsp", "port"));
    }

    /**
     * Gets the maximum backlog for the XSP server
     * This is the maximum number of queued incoming connections to allow on the listening socket.
     * 0 or less indicates a system-specific value.
     *
     * @return The maximum backlog for the xOWL protocol server
     */
    public int getXSPBacklog() {
        return Integer.parseInt(getValue("xsp", "backlog"));
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
            confFile.save(new File(root, FILE_NAME).getAbsolutePath(), Charset.forName("UTF-8"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
