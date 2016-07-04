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

package org.xowl.infra.utils.concurrent;

import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.Logging;

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
        } catch (Throwable error1) {
            Logging.getDefault().error(error1);
            try {
                onRunFailed(error1);
            } catch (Throwable error2) {
                // do nothing with this
            }
        }
    }

    /**
     * Effectively run
     */
    public abstract void doRun();

    /**
     * Event called when the run failed
     * This method can be used for cleanup when then run failed.
     * This method is not called when the run method terminate normally
     *
     * @param throwable The raised error
     */
    protected void onRunFailed(Throwable throwable) {
        // do nothing
    }
}
