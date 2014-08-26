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

import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.Triple;

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents an ontology filtering node in a RETE network
 *
 * @author Laurent Wouters
 */
public class FilterNode implements FactActivable {
    /**
     * The alpha memory storing the filter's results
     */
    private AlphaMemory child;
    /**
     * The ontology to look for
     */
    private Ontology ontology;

    /**
     * Initializes this filtering node
     *
     * @param parent   The parent fact holder
     * @param ontology The ontology to look for
     */
    public FilterNode(FactHolder parent, Ontology ontology) {
        this.child = new AlphaMemory();
        this.ontology = ontology;
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
    public void activateFact(Triple fact) {
        if (ontology == null || ontology == fact.getOntology())
            child.activateFact(fact);
    }

    @Override
    public void deactivateFact(Triple fact) {
        if (ontology == null || ontology == fact.getOntology())
            child.deactivateFact(fact);
    }

    @Override
    public void activateFacts(Collection<Triple> facts) {
        applyFilter(facts);
        if (!facts.isEmpty())
            child.activateFacts(facts);
    }

    @Override
    public void deactivateFacts(Collection<Triple> facts) {
        applyFilter(facts);
        if (!facts.isEmpty())
            child.deactivateFacts(facts);
    }

    /**
     * Applies the filter on the specified set of facts
     *
     * @param facts The facts to filter
     */
    private void applyFilter(Collection<Triple> facts) {
        if (ontology == null)
            return;
        Iterator<Triple> iterator = facts.iterator();
        while (iterator.hasNext()) {
            Triple current = iterator.next();
            if (ontology != current.getOntology())
                iterator.remove();
        }
    }
}
