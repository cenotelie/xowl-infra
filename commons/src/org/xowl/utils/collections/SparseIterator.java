/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 * Represents an iterator over an array that may contain null values
 * @param <T> The type of the data to iterate over
 */
public class SparseIterator<T> implements Iterator<T> {
    /**
     * The content to iterate over
     */
    private T[] content;
    /**
     * The current index
     */
    private int index;

    /**
     * Initializes this iterator
     * @param content The content to iterate over
     */
    public SparseIterator(T[] content) {
        this.content = content;
        this.index = 0;
        while (index != content.length && content[index] == null)
            index++;
    }

    @Override
    public boolean hasNext() {
        return (index != content.length);
    }

    @Override
    public T next() {
        T result = content[index];
        index++;
        while (index != content.length && content[index] == null)
            index++;
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
