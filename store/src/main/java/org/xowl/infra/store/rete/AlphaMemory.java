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

import fr.cenotelie.commons.utils.collections.FastBuffer;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.Store;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents an alpha memory in a RETE graph
 *
 * @author Laurent Wouters
 */
class AlphaMemory implements FactActivable, FactHolder, AlphaMemoryBucketElement {
    /**
     * The parent RDF store
     */
    private final Store store;
    /**
     * The pattern matched by this memory
     */
    private final Quad pattern;
    /**
     * The cache of facts
     */
    private volatile Collection<Quad> cache;
    /**
     * List of the children of this node
     */
    private final FastBuffer<FactActivable> children;

    /**
     * Initializes this memory
     *
     * @param pattern The data to match
     * @param store   The RDF data
     */
    public AlphaMemory(Quad pattern, Store store) {
        this.store = store;
        this.pattern = pattern;
        this.children = new FastBuffer<>(8);
    }

    @Override
    public Collection<Quad> getFacts() {
        Collection<Quad> result = cache;
        if (cache == null) {
            result = new FactCollection(store, pattern);
            cache = result;
        }
        return result;
    }

    @Override
    public void addChild(FactActivable node) {
        synchronized (children) {
            children.add(node);
        }
    }

    @Override
    public void removeChild(FactActivable node) {
        synchronized (children) {
            children.remove(node);
        }
    }

    @Override
    public void activateFact(Quad fact) {
        cache = null;
        Iterator<FactActivable> iterator = children.reverseIterator();
        while (iterator.hasNext()) {
            FactActivable child = iterator.next();
            if (child != null)
                child.activateFact(fact);
        }
    }

    @Override
    public void deactivateFact(Quad fact) {
        cache = null;
        Iterator<FactActivable> iterator = children.reverseIterator();
        while (iterator.hasNext()) {
            FactActivable child = iterator.next();
            if (child != null)
                child.deactivateFact(fact);
        }
    }

    @Override
    public void activateFacts(Collection<Quad> facts) {
        cache = null;
        Iterator<FactActivable> iterator = children.reverseIterator();
        while (iterator.hasNext()) {
            FactActivable child = iterator.next();
            if (child != null)
                child.activateFacts(new FastBuffer<>(facts));
        }
    }

    @Override
    public void deactivateFacts(Collection<Quad> facts) {
        cache = null;
        Iterator<FactActivable> iterator = children.reverseIterator();
        while (iterator.hasNext()) {
            FactActivable child = iterator.next();
            if (child != null)
                child.deactivateFacts(new FastBuffer<>(facts));
        }
    }

    @Override
    public void matchMemories(AlphaMemoryBuffer buffer, Quad quad) {
        buffer.add(this);
    }

    @Override
    public AlphaMemory resolveMemory(Quad pattern, Store store) {
        return this;
    }
}
