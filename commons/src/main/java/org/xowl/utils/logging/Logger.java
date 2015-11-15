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

package org.xowl.utils.logging;

/**
 * Represents an interface for a logging mechanism
 *
 * @author Laurent Wouters
 */
public interface Logger {
    /**
     * Logs a debug message
     *
     * @param message The message
     */
    void debug(Object message);

    /**
     * Logs an information message
     *
     * @param message The message
     */
    void info(Object message);

    /**
     * Logs a warning message
     *
     * @param message The message
     */
    void warning(Object message);

    /**
     * Logs an error message
     *
     * @param message The message
     */
    void error(Object message);
}
