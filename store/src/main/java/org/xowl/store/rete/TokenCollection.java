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

import java.util.Collection;

/**
 * Represents a collection of tokens in a Beta Memory
 *
 * @author Laurent Wouters
 */
abstract class TokenCollection implements Collection<Token> {
    /**
     * The size of this collection
     */
    private int size;

    /**
     * Initializes this collection
     */
    public TokenCollection() {
        this.size = -1;
    }

    /**
     * Computes the size of this collection
     *
     * @return The size of this collection
     */
    protected abstract int getSize();

    /**
     * Gets whether the specified token is in this collection
     *
     * @param token A token
     * @return Whether the specified token is in this collection
     */
    protected abstract boolean contains(Token token);

    @Override
    public int size() {
        if (size > -1)
            return size;
        size = getSize();
        return size;
    }

    @Override
    public boolean isEmpty() {
        if (size == -1)
            size();
        return (size == 0);
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof Token && contains((Token) o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Token token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Token> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
