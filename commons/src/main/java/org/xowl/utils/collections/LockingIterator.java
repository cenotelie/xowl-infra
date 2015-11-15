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

package org.xowl.utils.collections;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * Implements a proxy iterator that maintains a lock until the proxied iterator has finished.
 * This iterator does not try to take the lock, it is assumed to be already taken during the construction.
 *
 * @param <T> The type of the data to iterate over
 * @author Laurent Wouters
 */
public class LockingIterator<T> implements Iterator<T>, AutoCloseable {
    /**
     * The inner iterator
     */
    private final Iterator<T> inner;
    /**
     * The maintained lock
     */
    private final Lock lock;
    /**
     * Whether the lock has been released
     */
    private boolean hasReleased;

    /**
     * Initializes this iterator
     *
     * @param inner The iterator to proxy
     * @param lock  The lock to maintain
     */
    public LockingIterator(Iterator<T> inner, Lock lock) {
        this.inner = inner;
        this.lock = lock;
    }

    /**
     * Releases the lock held by this iterator
     */
    private void release() {
        if (hasReleased)
            return;
        lock.unlock();
        hasReleased = true;
    }

    @Override
    public boolean hasNext() {
        try {
            boolean result = inner.hasNext();
            if (!result) {
                release();
            }
            return result;
        } catch (Exception exception) {
            release();
            throw exception;
        }
    }

    @Override
    public T next() {
        try {
            T result = inner.next();
            hasNext();
            return result;
        } catch (Exception exception) {
            release();
            throw exception;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws Exception {
        release();
    }
}
