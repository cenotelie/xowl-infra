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

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implements a re-entrant lock that is tracked by the lock manager
 *
 * @author Laurent Wouters
 */
public class TrackedReentrantLock extends ReentrantLock implements TrackedLock {
    /**
     * The stack trace of the last locking
     */
    private StackTraceElement[] trace;

    /**
     * Initializes this lock
     */
    public TrackedReentrantLock() {
        super();
    }

    /**
     * Initializes this lock
     *
     * @param fair Whether to use fair locking (default false)
     */
    public TrackedReentrantLock(boolean fair) {
        super(fair);
    }

    @Override
    public void lock() {
        super.lock();
        if (getHoldCount() == 1) {
            if (LockManager.DEBUG_LEAKS) {
                trace = Thread.currentThread().getStackTrace();
                trace = Arrays.copyOfRange(trace, 1, trace.length);
            }
            LockManager.register(this);
        }
    }

    @Override
    public void unlock() {
        super.unlock();
        if (!isHeldByCurrentThread()) {
            LockManager.unregister(this);
        }
    }

    @Override
    public StackTraceElement[] trace() {
        return trace;
    }

    @Override
    public void simpleRelease() {
        while (isHeldByCurrentThread()) {
            super.unlock();
        }
    }
}
