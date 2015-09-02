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

package org.xowl.store.rete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a fast buffer of T elements with a fixed size
 *
 * @author Laurent Wouters
 */
class FastBuffer<T> implements Collection<T> {
    /**
     * The internal representation
     */
    private T[] inner;
    /**
     * The number of elements in this buffer
     */
    private int size;

    /**
     * Initializes this buffer with the specified elements
     *
     * @param init The original elements
     */
    public FastBuffer(Collection<T> init) {
        if (init instanceof FastBuffer)
            copy((FastBuffer<T>) init);
        else {
            this.inner = (T[]) (new Object[init.size()]);
            this.size = 0;
            int i = 0;
            for (T elem : init) {
                if (elem == null)
                    continue;
                this.inner[i] = elem;
                i++;
                size++;
            }
        }
    }

    /**
     * Copies the specified buffer into this one
     *
     * @param original The buffer to copy
     */
    private void copy(FastBuffer<T> original) {
        this.inner = Arrays.copyOf(original.inner, original.inner.length);
        this.size = original.size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;
            private int last = 0;

            {
                lookupNext();
            }

            @Override
            public boolean hasNext() {
                return (index != inner.length);
            }

            @Override
            public T next() {
                T current = inner[index];
                last = index;
                index++;
                lookupNext();
                return current;
            }

            @Override
            public void remove() {
                inner[last] = null;
                size--;
            }

            private void lookupNext() {
                while (index != inner.length) {
                    if (inner[index] != null)
                        return;
                    index++;
                }
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i != inner.length; i++)
            if (inner[i] == o)
                return true;
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> clctn) {
        return false;
    }

    @Override
    public boolean add(T e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> clctn) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public Object[] toArray() {
        Object[] temp = new Object[size];
        int index = 0;
        for (int i = 0; i != inner.length; i++) {
            if (inner[i] != null) {
                temp[index] = inner[i];
                index++;
            }
        }
        return temp;
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return null;
    }
}
