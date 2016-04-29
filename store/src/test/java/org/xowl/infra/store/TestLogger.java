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

package org.xowl.infra.store;

import org.xowl.infra.utils.logging.Logger;

/**
 * Error logger for the tests
 */
public class TestLogger implements Logger {

    /**
     * The status of this error
     */
    private boolean onError;

    /**
     * Determines whether this logger is on error
     *
     * @return Whether this logger is on error
     */
    public boolean isOnError() {
        return onError;
    }

    /**
     * Resets the error trigger
     */
    public void reset() {
        onError = false;
    }

    @Override
    public void debug(Object message) {

    }

    @Override
    public void info(Object message) {
    }

    @Override
    public void warning(Object message) {
    }

    @Override
    public void error(Object message) {
        onError = true;
    }
}
