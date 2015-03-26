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
 * Represents an iterator that concatenates other iterators
 *
 * @param <T> The type of elements to iterator over
 * @author Laurent Wouters
 */
public class ConcatenatedIterator<T> implements Iterator<T> {
    /**
     * The innner iterators
     */
    private final Iterator<T>[] content;
    /**
     * The index of the current iterator
     */
    private int index;

    /**
     * Initializes this iterator
     *
     * @param content The inner iterators
     */
    public ConcatenatedIterator(Iterator<T>[] content) {
        this.content = content;
        this.index = 0;
        while (index != content.length && !content[index].hasNext())
            index++;
    }

    @Override
    public boolean hasNext() {
        return (index != content.length && content[index].hasNext());
    }

    @Override
    public T next() {
        T result = content[index].next();
        while (index != content.length && !content[index].hasNext())
            index++;
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
