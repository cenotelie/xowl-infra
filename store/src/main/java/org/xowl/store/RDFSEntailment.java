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

/**
 * Represents RDFS entailment rules
 *
 * @author Laurent Wouters
 */
class RDFSEntailment implements ChangeListener {
    /**
     * The graph for the entailed quads
     */
    private final IRINode graphTarget;
    /**
     * The rdf:type IRI node
     */
    private final IRINode rdfType;
    /**
     * The rdfs:Resource IRI node
     */
    private final IRINode rdfsResource;
    /**
     * The target dataset for the entailed quads
     */
    private final Dataset target;

    /**
     * Initializes this entailment
     *
     * @param nodes  The node manager to use to resolve required IRI nodes
     * @param target The target dataset for the entailed quads
     */
    public RDFSEntailment(NodeManager nodes, Dataset target) {
        this.target = target;
        this.graphTarget = nodes.getIRINode(IRIs.GRAPH_INFERENCE);
        this.rdfType = nodes.getIRINode(Vocabulary.rdfType);
        this.rdfsResource = nodes.getIRINode(Vocabulary.rdfsResource);
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
        if (quad.getSubject().getNodeType() == Node.TYPE_IRI) {
            try {
                target.add(graphTarget, quad.getSubject(), rdfType, rdfsResource);
            } catch (UnsupportedNodeType exception) {
                //  cannot happen
            }
        }
        if (quad.getObject().getNodeType() == Node.TYPE_IRI) {
            try {
                target.add(graphTarget, (IRINode) quad.getObject(), rdfType, rdfsResource);
            } catch (UnsupportedNodeType exception) {
                //  cannot happen
            }
        }
    }

    @Override
    public void onRemoved(Quad quad) {
        if (quad.getSubject().getNodeType() == Node.TYPE_IRI) {
            try {
                target.remove(graphTarget, quad.getSubject(), rdfType, rdfsResource);
            } catch (UnsupportedNodeType exception) {
                //  cannot happen
            }
        }
        if (quad.getObject().getNodeType() == Node.TYPE_IRI) {
            try {
                target.remove(graphTarget, (IRINode) quad.getObject(), rdfType, rdfsResource);
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
