/**********************************************************************
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
 **********************************************************************/
package org.xowl.utils.collections;

import java.util.Iterator;

/**
 * Represents an iterator that adapts another while skipping over null values
 *
 * @param <T> The type of the data to iterate over
 * @author Laurent Wouters
 */
public class SkippableIterator<T> implements Iterator<T> {
    /**
     * The original iterator
     */
    private Iterator<T> original;
    /**
     * The next item
     */
    private T nextItem;

    /**
     * Initializes this iterator
     *
     * @param original The original iterator
     */
    public SkippableIterator(Iterator<T> original) {
        this.original = original;
        while (original.hasNext() && nextItem == null) {
            nextItem = original.next();
        }
    }

    @Override
    public boolean hasNext() {
        return (nextItem != null);
    }

    @Override
    public T next() {
        T result = nextItem;
        nextItem = null;
        while (original.hasNext() && nextItem == null) {
            nextItem = original.next();
        }
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
