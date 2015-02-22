/**********************************************************************
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
 **********************************************************************/
package org.xowl.utils;

import java.io.PrintStream;

/**
 * Console logger
 *
 * @author Laurent Wouters
 */
public class ConsoleLogger implements Logger {
    private static final String LEVEL_DEBUG = "DEBUG";
    private static final String LEVEL_INFO = "INFO";
    private static final String LEVEL_WARNING = "WARNING";
    private static final String LEVEL_ERROR = "ERROR";

    @Override
    public void debug(Object message) {
        log(message, LEVEL_DEBUG, false);
    }

    @Override
    public void info(Object message) {
        log(message, LEVEL_INFO, false);
    }

    @Override
    public void warning(Object message) {
        log(message, LEVEL_WARNING, false);
    }

    @Override
    public void error(Object message) {
        log(message, LEVEL_ERROR, true);
    }

    /**
     * Logs a message
     *
     * @param message   The message
     * @param level     The level to log at
     * @param useStdErr Whether to use the standard error output stream
     */
    private void log(Object message, String level, boolean useStdErr) {
        PrintStream stream = System.out;
        if (useStdErr)
            stream = System.err;
        if (message instanceof Exception) {
            Exception ex = (Exception) message;
            stream.println("[" + level + "] " + ex.getMessage());
            for (StackTraceElement element : ex.getStackTrace()) {
                stream.println("[" + level + "] " + element.toString());
            }
        } else {
            stream.println("[" + level + "] " + message.toString());
        }
    }
}
