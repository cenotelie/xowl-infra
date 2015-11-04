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
    private static final String FILE_NAME = "conf.ini";

    /**
     * The singleton instance
     */
    private static ServerConfiguration INSTANCE;

    /**
     * Initializes the configuration
     *
     * @param args The startup arguments
     */
    public static void init(String[] args) {
        INSTANCE = new ServerConfiguration(args);
    }

    /**
     * The default configuration
     */
    private Configuration confDefault;
    /**
     * The current configuration file
     */
    private Configuration confFile;

    /**
     * Initializes this configuration
     *
     * @param args The startup arguments
     */
    private ServerConfiguration(String[] args) {
        confDefault = new Configuration();
        confFile = new Configuration();
        InputStream stream = Program.class.getResourceAsStream(FILE_DEFAULT);
        try {
            confDefault.load(stream, Charset.forName("UTF-8"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        File rootFolder = (args.length > 0) ? new File(args[0]) : new File(System.getProperty("user.dir"));
        File file = new File(rootFolder, FILE_NAME);
        try {
            confFile.load(file.getAbsolutePath(), Charset.forName("UTF-8"));
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
     * Gets the address to bind for the HTTP server
     * @return The address to bind
     */
    public static String getHttpAddress() {
        return INSTANCE.getValue("http", "address");
    }
}
