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

import org.xowl.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements APIs for the safe management of locks
 *
 * @author Laurent Wouters
 */
public class LockManager {
    /**
     * Whether to debug lock leaks
     */
    public static final boolean DEBUG_LEAKS = true;

    /**
     * The currently tracked locks
     */
    private static final ConcurrentHashMap<Thread, List<TrackedLock>> LOCKS = new ConcurrentHashMap<>();

    /**
     * Register a lock
     *
     * @param lock The lock to register
     */
    public static void register(TrackedLock lock) {
        List<TrackedLock> locks = LOCKS.get(Thread.currentThread());
        if (locks == null) {
            locks = new ArrayList<>();
            LOCKS.put(Thread.currentThread(), locks);
        }
        locks.add(lock);
    }

    /**
     * Un-registers a lock
     *
     * @param lock The lock to un-register
     */
    public static void unregister(TrackedLock lock) {
        List<TrackedLock> locks = LOCKS.get(Thread.currentThread());
        locks.remove(lock);
        if (locks.isEmpty())
            LOCKS.remove(Thread.currentThread());
    }

    /**
     * Cleans up the remaining non-released locks for the current thread
     */
    public static void cleanup() {
        List<TrackedLock> locks = LOCKS.get(Thread.currentThread());
        if (locks == null)
            return;
        for (TrackedLock lock : locks) {
            if (DEBUG_LEAKS) {
                Logger.DEFAULT.warning(new LeakingLock(lock.trace()));
            }
            lock.simpleRelease();
        }
        LOCKS.remove(Thread.currentThread());
    }
}
