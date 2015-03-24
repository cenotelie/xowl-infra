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
package org.xowl.store.rete;

import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;
import org.xowl.utils.collections.CombiningIterator;
import org.xowl.utils.collections.Couple;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a collection of tokens in a Beta Memory
 *
 * @author Laurent Wouters
 */
class TokenCollection implements Collection<Token> {
    /**
     * The token store
     */
    private Map<Token, BetaMemory.TChildren> store;
    /**
     * The size of this collection
     */
    private int size;

    /**
     * Initializes this collection
     *
     * @param store The parent store
     */
    public TokenCollection(Map<Token, BetaMemory.TChildren> store) {
        this.store = store;
        this.size = -1;
    }

    @Override
    public int size() {
        if (size > -1)
            return size;
        size = 0;
        for (BetaMemory.TChildren children : store.values())
            size += children.count;
        return size;
    }

    @Override
    public boolean isEmpty() {
        if (size == -1)
            size();
        return (size == 0);
    }

    @Override
    public Iterator<Token> iterator() {
        CombiningIterator<Map.Entry<Token, BetaMemory.TChildren>, Token> coupleIterator = new CombiningIterator<>(store.entrySet().iterator(), new Adapter<Iterator<Token>>() {
            @Override
            public <X> Iterator<Token> adapt(X element) {
                return ((Map.Entry<Token, BetaMemory.TChildren>) element).getValue().iterator();
            }
        });
        return new AdaptingIterator<>(coupleIterator, new Adapter<Token>() {
            @Override
            public <X> Token adapt(X element) {
                return ((Couple<Map.Entry<Token, BetaMemory.TChildren>, Token>) element).y;
            }
        });
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Token))
            return false;
        Token token = (Token) o;
        for (BetaMemory.TChildren children : store.values()) {
            if (children.contains(token))
                return true;
        }
        return false;
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
