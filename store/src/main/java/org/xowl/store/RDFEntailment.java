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

package org.xowl.store;

import org.xowl.store.rdf.*;
import org.xowl.store.storage.Dataset;
import org.xowl.store.storage.NodeManager;
import org.xowl.store.storage.UnsupportedNodeType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents RDF entailment rules
 *
 * @author Laurent Wouters
 */
class RDFEntailment implements ChangeListener {
    /**
     * Encapsulates a counter
     */
    private static class Counter {
        /**
         * The value
         */
        private long value;

        /**
         * Increments the counter
         */
        public void inc() {
            value++;
        }

        /**
         * Decrements the counter
         * @return true if the counter is 0 or less
         */
        public boolean dec() {
            return (--value <= 0);
        }
    }

    /**
     * The target node manager for new blank nodes
     */
    private final NodeManager targetNodes;
    /**
     * The target dataset for the entailed quads
     */
    private final Dataset targetDataset;
    /**
     * The counters for the properties
     */
    private final Map<Property, Counter> counters;
    /**
     * The graph for the entailed quads
     */
    private final IRINode graphTarget;
    /**
     * The rdf:type IRI node
     */
    private final IRINode rdfType;
    /**
     * The rdf:Property IRI node
     */
    private final IRINode rdfProperty;

    /**
     * Initializes this entailment
     *
     * @param targetNodes  The target node manager for new blank nodes
     * @param targetDataset The target dataset for the entailed quads
     */
    public RDFEntailment(NodeManager targetNodes, Dataset targetDataset) {
        this.targetNodes = targetNodes;
        this.targetDataset = targetDataset;
        this.counters = new HashMap<>();
        this.graphTarget = targetNodes.getIRINode(IRIs.GRAPH_INFERENCE);
        this.rdfType = targetNodes.getIRINode(Vocabulary.rdfType);
        this.rdfProperty = targetNodes.getIRINode(Vocabulary.rdfProperty);
    }

    @Override
    public void onIncremented(Quad quad) {
        // do nothing
    }

    @Override
    public void onDecremented(Quad quad) {
        // do nothing
    }

    @Override
    public void onAdded(Quad quad) {
        // rule rdfD2, ?x ?p ?y => ?p rdf:type rdf:Property
        Counter counter = counters.get(quad.getProperty());
        if (counter == null) {
            counter = new Counter();
            counter.inc();
            try {
                targetDataset.add(graphTarget, (SubjectNode) quad.getProperty(), rdfType, rdfProperty);
            } catch (UnsupportedNodeType exception) {
                //  cannot happen
            }
        }

        if (quad.getObject().getNodeType() == Node.TYPE_LITERAL) {
            try {
                targetDataset.add(graphTarget, (SubjectNode) quad.getProperty(), rdfType, rdfProperty);

                LiteralNode literal = (LiteralNode) quad.getObject();


                targetDataset.add(graphTarget, (IRINode) quad.getObject(), rdfType, rdfsResource);
            } catch (UnsupportedNodeType exception) {
                //  cannot happen
            }
        }
    }

    @Override
    public void onRemoved(Quad quad) {
        if (quad.getSubject().getNodeType() == Node.TYPE_IRI) {
            try {
                targetDataset.remove(graphTarget, quad.getSubject(), rdfType, rdfsResource);
            } catch (UnsupportedNodeType exception) {
                //  cannot happen
            }
        }
        if (quad.getObject().getNodeType() == Node.TYPE_IRI) {
            try {
                targetDataset.remove(graphTarget, (IRINode) quad.getObject(), rdfType, rdfsResource);
            } catch (UnsupportedNodeType exception) {
                //  cannot happen
            }
        }
    }

    @Override
    public void onChange(Changeset changeset) {
        for (Quad quad : changeset.getAdded())
            onAdded(quad);
        for (Quad quad : changeset.getRemoved())
            onRemoved(quad);
    }
}
