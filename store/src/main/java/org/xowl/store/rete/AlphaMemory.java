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

import org.xowl.store.rdf.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents an alpha memory in a RETE graph
 *
 * @author Laurent Wouters
 */
public class AlphaMemory implements FactActivable, FactHolder {
    /**
     * The facts in this memory
     */
    private Collection<Triple> facts;
    /**
     * List of the children of this node
     */
    private List<FactActivable> children;

    /**
     * Initializes this memory
     */
    public AlphaMemory() {
        facts = new ArrayList<>();
        children = new ArrayList<>();
    }

    @Override
    public Collection<Triple> getFacts() {
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
    public void activateFact(Triple fact) {
        facts.add(fact);
        for (int i = children.size() - 1; i != 0; i--)
            children.get(i).activateFact(fact);
    }

    @Override
    public void deactivateFact(Triple fact) {
        facts.remove(fact);
        for (int i = children.size() - 1; i != 0; i--)
            children.get(i).deactivateFact(fact);
    }

    @Override
    public void activateFacts(Collection<Triple> facts) {
        this.facts.addAll(facts);
        for (int i = children.size() - 1; i != 0; i--)
            children.get(i).activateFacts(new FastBuffer<>(facts));
    }

    @Override
    public void deactivateFacts(Collection<Triple> facts) {
        this.facts.removeAll(facts);
        for (int i = children.size() - 1; i != 0; i--)
            children.get(i).deactivateFacts(new FastBuffer<>(facts));
    }
}
