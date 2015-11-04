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
import java.security.SecureRandom;

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
    private static final String FILE_NAME = "conf.ini";
    /**
     * The number of bytes the password pepper
     */
    private static final int PEPPER_LENGTH = 20;

    /**
     * The default configuration
     */
    private Configuration confDefault;
    /**
     * The current configuration file
     */
    private Configuration confFile;
    /**
     * The root folder for this server
     */
    private File root;

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
        String result = confFile.getValue(section, property);
        if (result != null)
            return result;
        return confDefault.getValue(section, property);
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
}
