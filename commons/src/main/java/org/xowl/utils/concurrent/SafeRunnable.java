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

package org.xowl.utils.concurrent;

import org.xowl.utils.logging.ConsoleLogger;
import org.xowl.utils.logging.Logger;

/**
 * Implements a runnable that ensure that all its locks are released before finishing
 *
 * @author Laurent Wouters
 */
public abstract class SafeRunnable implements Runnable {
    /**
     * The logger for this runnable
     */
    protected final Logger logger;

    /**
     * Initializes this runnable
     *
     * @param logger The logger to use
     */
    public SafeRunnable(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            ConsoleLogger.INSTANCE.error(exception);
        } finally {
            LockManager.cleanup();
        }
    }

    /**
     * Effectively run
     */
    public abstract void doRun();
}
