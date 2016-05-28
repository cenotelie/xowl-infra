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

package org.xowl.infra.utils.logging;

/**
 * Logging management
 *
 * @author Laurent Wouters
 */
public class Logging {
    /**
     * The default logger
     */
    private static Logger DEFAULT = new ConsoleLogger();

    /**
     * Gets the default logger
     *
     * @return The default logger
     */
    public static Logger getDefault() {
        return DEFAULT;
    }

    /**
     * Sets the default logger
     *
     * @param logger The default logger
     */
    public static void setDefault(Logger logger) {
        DEFAULT = logger;
    }
}
