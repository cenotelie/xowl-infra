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
package org.xowl.infra.store.rete;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.Dataset;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.collections.CloseableIterator;
import org.xowl.infra.utils.collections.SingleIterator;
import org.xowl.infra.utils.logging.Logging;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a collection of facts that is implemented as a query on a RDF store
 *
 * @author Laurent Wouters
 */
class FactCollection implements Collection<Quad> {
    /**
     * The parent RDF store
     */
    private final Dataset store;
    /**
     * The matched pattern
     */
    private final Quad pattern;
    /**
     * The size of this collection
     */
    private int size;

    /**
     * Initializes this collection
     *
     * @param store   The parent RDF store
     * @param pattern The matched pattern
     */
    public FactCollection(Dataset store, Quad pattern) {
        this.store = store;
        this.pattern = pattern;
        this.size = -1;
    }

    /**
     * Gets a new iterator
     *
     * @return A new iterator
     */
    private Iterator<Quad> getNewIterator() {
        try {
            return store.getAll(pattern.getGraph(), pattern.getSubject(), pattern.getProperty(), pattern.getObject());
        } catch (UnsupportedNodeType exception) {
            Logging.getDefault().error(exception);
            return new SingleIterator<>(null);
        }
    }

    @Override
    public int size() {
        if (size > -1)
            return size;
        try {
            size = (int) store.count(pattern.getGraph(), pattern.getSubject(), pattern.getProperty(), pattern.getObject());
        } catch (UnsupportedNodeType exception) {
            Logging.getDefault().error(exception);
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        if (size > -1) {
            return (size == 0);
        } else {
            Iterator<Quad> iterator = getNewIterator();
            boolean empty = !iterator.hasNext();
            if (iterator instanceof CloseableIterator) {
                try {
                    ((CloseableIterator) iterator).close();
                } catch (Exception exception) {
                    Logging.getDefault().error(exception);
                }
            }
            if (empty)
                size = 0;
            return empty;
        }
    }

    @Override
    public Iterator<Quad> iterator() {
        // get a fresh iterator
        return getNewIterator();
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Quad))
            return false;
        Quad quad = (Quad) o;
        if (pattern.getGraph().getNodeType() != Node.TYPE_VARIABLE && !RDFUtils.same(pattern.getGraph(), quad.getGraph()))
            return false;
        if (pattern.getSubject().getNodeType() != Node.TYPE_VARIABLE && !RDFUtils.same(pattern.getSubject(), quad.getSubject()))
            return false;
        if (pattern.getProperty().getNodeType() != Node.TYPE_VARIABLE && !RDFUtils.same(pattern.getProperty(), quad.getProperty()))
            return false;
        if (pattern.getObject().getNodeType() != Node.TYPE_VARIABLE && !RDFUtils.same(pattern.getObject(), quad.getObject()))
            return false;
        // the quad matches the pattern
        try {
            Iterator<Quad> iterator = store.getAll(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            boolean result = iterator.hasNext();
            if (iterator instanceof CloseableIterator) {
                try {
                    ((CloseableIterator) iterator).close();
                } catch (Exception exception) {
                    Logging.getDefault().error(exception);
                }
            }
            return result;
        } catch (UnsupportedNodeType exception) {
            Logging.getDefault().error(exception);
            return false;
        }
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
    public boolean add(Quad quad) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Quad> c) {
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
