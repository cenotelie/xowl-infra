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

package org.xowl.store.rete;

import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.RDFStore;
import org.xowl.store.rdf.UnsupportedNodeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an alpha memory in a RETE graph
 *
 * @author Laurent Wouters
 */
class AlphaMemory implements FactActivable, FactHolder, AlphaMemoryBucketElement {
    /**
     * The facts in this memory
     */
    private Collection<Quad> facts;
    /**
     * List of the children of this node
     */
    private List<FactActivable> children;

    /**
     * Initializes this memory
     */
    public AlphaMemory() {
        children = new ArrayList<>();
    }

    @Override
    public Collection<Quad> getFacts() {
        return facts;
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
        facts.add(fact);
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).activateFact(fact);
    }

    @Override
    public void deactivateFact(Quad fact) {
        facts.remove(fact);
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).deactivateFact(fact);
    }

    @Override
    public void activateFacts(Collection<Quad> facts) {
        this.facts.addAll(facts);
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).activateFacts(new FastBuffer<>(facts));
    }

    @Override
    public void deactivateFacts(Collection<Quad> facts) {
        this.facts.removeAll(facts);
        for (int i = children.size() - 1; i != -1; i--)
            children.get(i).deactivateFacts(new FastBuffer<>(facts));
    }

    @Override
    public void matchMemories(AlphaMemoryBuffer buffer, Quad quad) {
        buffer.add(this);
    }

    @Override
    public AlphaMemory resolveMemory(Quad pattern, RDFStore store) {
        if (facts == null) {
            // this memory has just been created
            facts = new ArrayList<>();
            if (store != null) {
                // if we have a store attached, populate the this memory with the matching triples
                try {
                    Iterator<Quad> data = store.getAll(pattern.getGraph(), pattern.getSubject(), pattern.getProperty(), pattern.getObject());
                    while (data.hasNext()) {
                        facts.add(data.next());
                    }
                } catch (UnsupportedNodeType ex) {
                    // do nothing
                }
            }
        }
        return this;
    }
}
