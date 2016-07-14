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

import org.xowl.infra.utils.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * A logger that is backed by a file
 *
 * @author Laurent Wouters
 */
public class FileLogger implements Logger {
    /**
     * The file to write to
     */
    private final File logFile;

    /**
     * Initializes this logger
     *
     * @param file The file to write to
     */
    public FileLogger(File file) {
        this.logFile = file;
    }

    @Override
    public void debug(Object message) {
        log(message, LEVEL_DEBUG);
    }

    @Override
    public void info(Object message) {
        log(message, LEVEL_INFO);
    }

    @Override
    public void warning(Object message) {
        log(message, LEVEL_WARNING);
    }

    @Override
    public void error(Object message) {
        log(message, LEVEL_ERROR);
    }

    /**
     * Logs a message
     *
     * @param message The message
     * @param level   The level to log at
     */
    private synchronized void log(Object message, String level) {
        String date = DateFormat.getDateTimeInstance().format(new Date());
        try (FileOutputStream stream = new FileOutputStream(logFile, true)) {
            OutputStreamWriter writer = new OutputStreamWriter(stream, Files.CHARSET);
            if (message instanceof Exception) {
                Exception ex = (Exception) message;
                writer.write(date + " [" + level + "] " + ex.getClass().getName() + (ex.getMessage() != null ? " " + ex.getMessage() : ""));
                writer.write(Files.LINE_SEPARATOR);
                for (StackTraceElement element : ex.getStackTrace()) {
                    writer.write(date + " [" + level + "] \t" + element.toString());
                    writer.write(Files.LINE_SEPARATOR);
                }
            } else {
                writer.write(date + " [" + level + "] " + message.toString());
                writer.write(Files.LINE_SEPARATOR);
            }
            writer.flush();
        } catch (IOException exception) {
            // do nothing
        }
    }
}
