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

/**
 * Represents an iterator that adapts the elements of another iterator
 *
 * @param <T> The type of elements to iterator over
 * @param <X> The type of input elements
 * @author Laurent Wouters
 */
public class AdaptingIterator<T, X> implements Iterator<T> {
    /**
     * The innner iterator
     */
    protected final Iterator<X> content;
    /**
     * The adapter for translating the element
     */
    protected final Adapter<T> adapter;

    /**
     * Initializes this iterator
     *
     * @param content The inner iterator
     * @param adapter The adapter to use
     */
    public AdaptingIterator(Iterator<X> content, Adapter<T> adapter) {
        this.content = content;
        this.adapter = adapter;
    }

    @Override
    public boolean hasNext() {
        return content.hasNext();
    }

    @Override
    public T next() {
        return adapter.adapt(content.next());
    }

    @Override
    public void remove() {
        content.remove();
    }
}
