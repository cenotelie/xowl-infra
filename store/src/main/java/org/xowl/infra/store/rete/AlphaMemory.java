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

package org.xowl.infra.store.rete;

import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.Dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents an alpha memory in a RETE graph
 *
 * @author Laurent Wouters
 */
class AlphaMemory implements FactActivable, FactHolder, AlphaMemoryBucketElement {
    /**
     * The parent RDF store
     */
    private Dataset store;
    /**
     * The pattern matched by this memory
     */
    private Quad pattern;
    /**
     * The cache of facts
     */
    private Collection<Quad> cache;
    /**
     * List of the children of this node
     */
    private final List<FactActivable> children;

    /**
     * Initializes this memory
     */
    public AlphaMemory() {
        children = new ArrayList<>();
    }

    @Override
    public Collection<Quad> getFacts() {
        if (cache == null) {
            cache = new FactCollection(store, pattern);
        }
        return cache;
    }

    @Override
    public void addChild(FactActivable node) {
        children.add(node);
    }

    @Override
    public void removeChild(FactActivable node) {
        children.remove(node);
    }

    @Override
    public void activateFact(Quad fact) {
        cache = null;
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).activateFact(fact);
    }

    @Override
    public void deactivateFact(Quad fact) {
        cache = null;
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).deactivateFact(fact);
    }

    @Override
    public void activateFacts(Collection<Quad> facts) {
        cache = null;
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).activateFacts(new FastBuffer<>(facts));
    }

    @Override
    public void deactivateFacts(Collection<Quad> facts) {
        cache = null;
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).deactivateFacts(new FastBuffer<>(facts));
    }

    @Override
    public void matchMemories(AlphaMemoryBuffer buffer, Quad quad) {
        buffer.add(this);
    }

    @Override
    public AlphaMemory resolveMemory(Quad pattern, Dataset store) {
        if (this.store == null) {
            this.store = store;
            this.pattern = pattern;
            this.cache = null;
        }
        return this;
    }
}
