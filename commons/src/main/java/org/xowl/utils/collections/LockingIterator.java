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
    private static class Item {
        public LockingIterator iterator;
        public StackTraceElement[] stackTrace;

        public Item(LockingIterator iterator) {
            this.iterator = iterator;
            this.stackTrace = Thread.currentThread().getStackTrace();
        }
    }

    /**
     * The map of currently open iterators, i.e. iterators that hold a lock
     */
    private static final Map<Thread, List<Item>> ITERATORS = new HashMap<>();

    /**
     * Cleans up the remaining iterators for the current thread
     */
    public static void cleanup() {
        List<Item> values = ITERATORS.get(Thread.currentThread());
        if (values == null)
            return;
        for (Item item : values) {
            System.err.println("Leaking locking iterator by:");
            for (int i = 1; i != item.stackTrace.length; i++) {
                System.err.println(item.stackTrace[i].toString());
            }
            item.iterator.lock.unlock();
            item.iterator.hasReleased = true;
        }
        ITERATORS.remove(Thread.currentThread());
    }

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
        List<Item> values = ITERATORS.get(Thread.currentThread());
        if (values == null) {
            values = new ArrayList<>();
            ITERATORS.put(Thread.currentThread(), values);
        }
        values.add(new Item(this));
    }

    /**
     * Releases the lock held by this iterator
     */
    private void release() {
        if (hasReleased)
            return;
        List<Item> values = ITERATORS.get(Thread.currentThread());
        for (int i = 0; i != values.size(); i++) {
            if (values.get(i).iterator == this) {
                values.remove(i);
                break;
            }
        }
        if (values.isEmpty())
            ITERATORS.remove(Thread.currentThread());
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
