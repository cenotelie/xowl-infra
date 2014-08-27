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

package org.xowl.store.owl;

import org.xowl.lang.actions.QueryVariable;
import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.lang.owl2.Expression;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;
import org.xowl.utils.collections.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a store of RDF graph with xOWL extensions
 *
 * @author Laurent Wouters
 */
public class XOWLGraph extends RDFGraph {
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
     * Initializes this graph
     *
     * @throws java.io.IOException when the store cannot allocate a temporary file
     */
    public XOWLGraph() throws IOException {
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
        VariableNode node = new VariableNode(variable);
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
     * Adds the specified triple to this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    @Override
    public void add(Ontology ontology, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case AnonymousNode.TYPE:
                addEdgeFromAnonymous(ontology, (AnonymousNode) subject, property, value);
                break;
            case IRINode.TYPE:
                addEdgeFromIRI(ontology, (IRINode) subject, property, value);
                break;
            case BlankNode.TYPE:
                addEdgeFromBlank(ontology, (BlankNode) subject, property, value);
                break;
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Adds the specified triple to this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    protected void addEdgeFromAnonymous(Ontology ontology, AnonymousNode subject, Property property, Node value) {
        AnonymousIndividual key = subject.getAnonymous();
        EdgeBucket bucket = edgesAnon.get(key);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesAnon.put(key, bucket);
        }
        bucket.add(ontology, property, value);
    }

    /**
     * Removes the specified triple from this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    @Override
    public void remove(Ontology ontology, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case AnonymousNode.TYPE:
                removeEdgeFromAnon(ontology, (AnonymousNode) subject, property, value);
                break;
            case IRINode.TYPE:
                removeEdgeFromIRI(ontology, (IRINode) subject, property, value);
                break;
            case BlankNode.TYPE:
                removeEdgeFromBlank(ontology, (BlankNode) subject, property, value);
                break;
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Removes the specified triple from this graph
     *
     * @param ontology The ontology containing the triple
     * @param subject  The triple subject node
     * @param property The triple property
     * @param value    The triple value
     */
    protected void removeEdgeFromAnon(Ontology ontology, AnonymousNode subject, Property property, Node value) {
        AnonymousIndividual key = subject.getAnonymous();
        EdgeBucket bucket = edgesAnon.get(key);
        if (bucket == null)
            return;
        bucket.remove(ontology, property, value);
        if (bucket.getSize() == 0)
            edgesAnon.remove(key);
    }

    /**
     * Gets an iterator over all the subjects starting edges in the graph
     *
     * @return An iterator over all the subjects starting edges in the graph
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
