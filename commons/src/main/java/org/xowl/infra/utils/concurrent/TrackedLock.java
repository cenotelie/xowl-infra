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

import java.util.concurrent.locks.Lock;

/**
 * Represents a lock that is tracked by the lock manager
 *
 * @author Laurent Wouters
 */
public interface TrackedLock extends Lock {
    /**
     * Gets the stack trace of the last locking operation
     *
     * @return The stack trace
     */
    StackTraceElement[] trace();

    /**
     * Release the lock
     */
    void simpleRelease();
}
