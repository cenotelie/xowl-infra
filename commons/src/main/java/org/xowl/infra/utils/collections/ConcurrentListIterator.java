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

package org.xowl.infra.utils.collections;

import java.util.Iterator;
import java.util.List;

/**
 * An iterator over a list that is susceptible to change over the course of the iteration
 *
 * @author Laurent Wouters
 */
public class ConcurrentListIterator<T> implements Iterator<T> {
    /**
     * The original list
     */
    protected final List<T> content;
    /**
     * The next item
     */
    protected int nextIndex;

    /**
     * Initializes this iterator
     *
     * @param content The original list
     */
    public ConcurrentListIterator(List<T> content) {
        this.content = content;
        this.nextIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return nextIndex < content.size();
    }

    @Override
    public T next() {
        if (nextIndex >= content.size())
            return null;
        T result;
        try {
            result = content.get(nextIndex);
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
        nextIndex++;
        return result;
    }

    @Override
    public void remove() {
        int toRemove = nextIndex - 1;
        if (toRemove >= 0 && toRemove < content.size()) {
            try {
                content.remove(toRemove);
            } catch (IndexOutOfBoundsException exception) {
                // ignore
            }
        }
    }
}
