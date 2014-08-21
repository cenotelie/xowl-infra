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
 * Representing an iterator computing the cross-produce of two collections
 * @param <X> The first type of data to iterate over
 * @param <Y> The second type of data to iterate over
 */
public class CombiningIterator<X, Y> implements Iterator<Couple<X, Y>> {
    /**
     * The current result that has been return by the next function
     */
    private Couple<X, Y> current;
    /**
     * The next result to be returned
     */
    private Couple<X, Y> nextResult;
    /**
     * The iterator of values on the left
     */
    private Iterator<X> leftIterator;
    /**
     * The current iterator for the right elements
     */
    private Iterator<Y> rightIterator;
    /**
     * The adapter to get an iterator of Y for each X item
     */
    private Adapter<Iterator<Y>> adapter;

    /**
     * Initializes this iterator
     * @param leftIterator The iterator of values on the left
     * @param adapter The adapter to get an iterator of Y for each X item
     */
    public CombiningIterator(Iterator<X> leftIterator, Adapter<Iterator<Y>> adapter) {
        this.current = new Couple<>();
        this.nextResult = new Couple<>();
        this.leftIterator = leftIterator;
        this.adapter = adapter;
        findNext();
    }

    /**
     * Finds the next result
     */
    private void findNext() {
        if (rightIterator != null && rightIterator.hasNext()) {
            nextResult.y = rightIterator.next();
        } else {
            while (leftIterator.hasNext()) {
                nextResult.x = leftIterator.next();
                rightIterator = adapter.adapt(nextResult.x);
                if (rightIterator.hasNext()) {
                    nextResult.y = rightIterator.next();
                    return;
                }
            }
            nextResult.x = null;
            nextResult.y = null;
        }
    }

    @Override
    public boolean hasNext() {
        return (nextResult.x != null && nextResult.y != null);
    }

    @Override
    public Couple<X, Y> next() {
        current.x = nextResult.x;
        current.y = nextResult.y;
        findNext();
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
