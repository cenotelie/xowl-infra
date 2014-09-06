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

import org.xowl.store.rdf.GraphNode;
import org.xowl.store.rdf.Quad;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents an graph filtering node in a RETE network
 *
 * @author Laurent Wouters
 */
public class FilterNode implements FactActivable {
    /**
     * The alpha memory storing the filter's results
     */
    private AlphaMemory child;
    /**
     * The graph to look for
     */
    private GraphNode graph;

    /**
     * Initializes this filtering node
     *
     * @param parent   The parent fact holder
     * @param graph The graph to look for
     */
    public FilterNode(FactHolder parent, GraphNode graph) {
        this.child = new AlphaMemory();
        this.graph = graph;
        parent.addChild(this);
        int size = parent.getFacts().size();
        if (size != 0) {
            if (size == 1)
                activateFact(parent.getFacts().iterator().next());
            else
                activateFacts(new FastBuffer<>(parent.getFacts()));
        }
    }

    /**
     * Gets the child of this node
     *
     * @return The child of this node
     */
    public AlphaMemory getChild() {
        return child;
    }

    @Override
    public void activateFact(Quad fact) {
        if (graph == null || graph.equals(fact.getGraph()))
            child.activateFact(fact);
    }

    @Override
    public void deactivateFact(Quad fact) {
        if (graph == null || graph.equals(fact.getGraph()))
            child.deactivateFact(fact);
    }

    @Override
    public void activateFacts(Collection<Quad> facts) {
        applyFilter(facts);
        if (!facts.isEmpty())
            child.activateFacts(facts);
    }

    @Override
    public void deactivateFacts(Collection<Quad> facts) {
        applyFilter(facts);
        if (!facts.isEmpty())
            child.deactivateFacts(facts);
    }

    /**
     * Applies the filter on the specified set of facts
     *
     * @param facts The facts to filter
     */
    private void applyFilter(Collection<Quad> facts) {
        if (graph == null)
            return;
        Iterator<Quad> iterator = facts.iterator();
        while (iterator.hasNext()) {
            Quad current = iterator.next();
            if (!graph.equals(current.getGraph()))
                iterator.remove();
        }
    }
}
