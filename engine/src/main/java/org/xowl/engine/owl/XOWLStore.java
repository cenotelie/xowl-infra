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

package org.xowl.engine.owl;

import org.xowl.lang.actions.QueryVariable;
import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.lang.owl2.Expression;
import org.xowl.store.rdf.*;
import org.xowl.utils.collections.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a store of RDF store with xOWL extensions
 *
 * @author Laurent Wouters
 */
public class XOWLStore extends RDFStore {
    /**
     * The store of existing anonymous nodes
     */
    protected Map<AnonymousIndividual, AnonymousNode> mapNodeAnons;
    /**
     * The store of existing variable nodes
     */
    protected Map<QueryVariable, VariableNode> mapNodeVariables;
    /**
     * The store of existing dynamic nodes
     */
    protected Map<Expression, DynamicNode> mapNodeDynamics;

    /**
     * The store for edges starting from anonymous nodes
     */
    protected Map<AnonymousIndividual, EdgeBucket> edgesAnon;

    /**
     * Initializes this store
     *
     * @throws java.io.IOException when the store cannot allocate a temporary file
     */
    public XOWLStore() throws IOException {
        super();
        mapNodeAnons = new HashMap<>();
        mapNodeVariables = new HashMap<>();
        mapNodeDynamics = new HashMap<>();
        edgesAnon = new HashMap<>();
    }

    /**
     * Gets the RDF node for the given anonymous individual
     *
     * @param anon An anonymous individual
     * @return The associated RDF node
     */
    public AnonymousNode getAnonymousNode(AnonymousIndividual anon) {
        AnonymousNode node = mapNodeAnons.get(anon);
        if (node != null)
            return node;
        node = new AnonymousNode(anon);
        mapNodeAnons.put(anon, node);
        return node;
    }

    /**
     * Gets the RDF node for the specified variable
     *
     * @param variable A variable
     * @return The associated RDF node
     */
    public VariableNode getVariableNode(QueryVariable variable) {
        if (mapNodeVariables.containsKey(variable))
            return mapNodeVariables.get(variable);
        VariableNode node = new VariableNode(variable.getName());
        mapNodeVariables.put(variable, node);
        return node;
    }

    /**
     * Gets the RDF node for the specified expression
     *
     * @param expression An expression
     * @return The associated RDF node
     */
    public DynamicNode getDynamicNode(Expression expression) {
        if (mapNodeDynamics.containsKey(expression))
            return mapNodeDynamics.get(expression);
        DynamicNode node = new DynamicNode(expression);
        mapNodeDynamics.put(expression, node);
        return node;
    }

    /**
     * Adds the specified quad to this store
     *
     * @param graph    The store containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case AnonymousNode.TYPE:
                addEdgeFromAnonymous(graph, (AnonymousNode) subject, property, value);
                break;
            case IRINode.TYPE:
                addEdgeFromIRI(graph, (IRINode) subject, property, value);
                break;
            case BlankNode.TYPE:
                addEdgeFromBlank(graph, (BlankNode) subject, property, value);
                break;
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Adds the specified triple to this store
     *
     * @param graph    The store containing the quad
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    protected void addEdgeFromAnonymous(GraphNode graph, AnonymousNode subject, Property property, Node value) {
        AnonymousIndividual key = subject.getAnonymous();
        EdgeBucket bucket = edgesAnon.get(key);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesAnon.put(key, bucket);
        }
        bucket.add(graph, property, value);
    }

    /**
     * Removes the specified quad from this store
     *
     * @param graph    The store containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case AnonymousNode.TYPE:
                removeEdgeFromAnon(graph, (AnonymousNode) subject, property, value);
                break;
            case IRINode.TYPE:
                removeEdgeFromIRI(graph, (IRINode) subject, property, value);
                break;
            case BlankNode.TYPE:
                removeEdgeFromBlank(graph, (BlankNode) subject, property, value);
                break;
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Removes the specified quad from this store
     *
     * @param graph    The store containing the quad
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    protected void removeEdgeFromAnon(GraphNode graph, AnonymousNode subject, Property property, Node value) {
        AnonymousIndividual key = subject.getAnonymous();
        EdgeBucket bucket = edgesAnon.get(key);
        if (bucket == null)
            return;
        bucket.remove(graph, property, value);
        if (bucket.getSize() == 0)
            edgesAnon.remove(key);
    }

    /**
     * Gets an iterator over all the subjects starting edges in the store
     *
     * @return An iterator over all the subjects starting edges in the store
     */
    @Override
    protected Iterator<Couple<SubjectNode, EdgeBucket>> getAllSubjects() {
        return new ConcatenatedIterator<Couple<SubjectNode, EdgeBucket>>(new Iterator[]{
                new AdaptingIterator<>(edgesIRI.keySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                        Couple<SubjectNode, EdgeBucket> result = new Couple<>();
                        result.x = mapNodeIRIs.get(element);
                        result.y = edgesIRI.get(element);
                        return result;
                    }
                }),
                new AdaptingIterator<>(edgesAnon.keySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                        Couple<SubjectNode, EdgeBucket> result = new Couple<>();
                        result.x = mapNodeAnons.get(element);
                        result.y = edgesAnon.get(element);
                        return result;
                    }
                }),
                new AdaptingIterator<>(new IndexIterator<>(edgesBlank), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                        Couple<SubjectNode, EdgeBucket> result = new Couple<>();
                        Integer index = (Integer) element;
                        result.x = new BlankNode(index);
                        result.y = edgesBlank[index];
                        return result;
                    }
                })
        });
    }

    /**
     * Gets the edge bucket for the specified node
     *
     * @param node A node
     * @return The associated edge bucket, or null if there is none
     */
    @Override
    protected EdgeBucket getBucketFor(Node node) {
        if (node != null && node.getNodeType() == AnonymousNode.TYPE)
            return edgesAnon.get(((AnonymousNode) node).getAnonymous());
        return super.getBucketFor(node);
    }
}
