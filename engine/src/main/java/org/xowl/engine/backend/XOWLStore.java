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

package org.xowl.engine.backend;

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.rdf.*;
import org.xowl.utils.collections.*;
import org.xowl.utils.data.Attribute;
import org.xowl.utils.data.Dataset;

import java.io.IOException;
import java.util.*;

/**
 * Represents a store of RDF store with xOWL extensions
 *
 * @author Laurent Wouters
 */
public class XOWLStore extends RDFStore {
    /**
     * The store of existing anonymous nodes
     */
    protected Map<String, AnonymousNode> mapNodeAnons;

    /**
     * The store for edges starting from anonymous nodes
     */
    protected Map<String, EdgeBucket> edgesAnon;

    /**
     * Initializes this store
     *
     * @throws java.io.IOException When the store cannot allocate a temporary file
     */
    public XOWLStore() throws IOException {
        super();
        mapNodeAnons = new HashMap<>();
        edgesAnon = new HashMap<>();
    }

    /**
     * Gets the RDF node for the given anonymous individual
     *
     * @param anon An anonymous individual
     * @return The associated RDF node
     */
    public AnonymousNode getAnonymousNode(AnonymousIndividual anon) {
        AnonymousNode node = mapNodeAnons.get(anon.getNodeID());
        if (node != null)
            return node;
        node = new AnonymousNode(anon);
        mapNodeAnons.put(anon.getNodeID(), node);
        return node;
    }

    /**
     * Gets the IRI nodes that are within the specified iri namespace
     *
     * @param iri An iri namespace
     * @return The corresponding IRI nodes
     */
    public Collection<IRINode> getNodesWithin(String iri) {
        List<IRINode> result = new ArrayList<>();
        for (IRINode node : mapNodeIRIs.values())
            if (node.getIRIValue().startsWith(iri))
                result.add(node);
        return result;
    }

    /**
     * Adds the specified quad to this store
     *
     * @param graph    The store containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     * @throws org.xowl.store.rdf.UnsupportedNodeType When the subject node type is unsupported
     */
    @Override
    protected int doAddEdge(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case AnonymousNode.TYPE:
                return doAddEdgeFromAnonymous(graph, (AnonymousNode) subject, property, value);
            case IRINode.TYPE:
                return doAddEdgeFromIRI(graph, (IRINode) subject, property, value);
            case BlankNode.TYPE:
                return doAddEdgeFromBlank(graph, (BlankNode) subject, property, value);
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
     * @return The operation result
     */
    protected int doAddEdgeFromAnonymous(GraphNode graph, AnonymousNode subject, Property property, Node value) {
        String key = subject.getAnonymous().getNodeID();
        EdgeBucket bucket = edgesAnon.get(key);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesAnon.put(key, bucket);
        }
        return bucket.add(graph, property, value);
    }

    /**
     * Removes the specified quad from this store
     *
     * @param graph    The store containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     * @throws org.xowl.store.rdf.UnsupportedNodeType when the subject node type is unsupported
     */
    @Override
    protected int doRemoveEdge(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case AnonymousNode.TYPE:
                return doRemoveEdgeFromAnon(graph, (AnonymousNode) subject, property, value);
            case IRINode.TYPE:
                return doRemoveEdgeFromIRI(graph, (IRINode) subject, property, value);
            case BlankNode.TYPE:
                return doRemoveEdgeFromBlank(graph, (BlankNode) subject, property, value);
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
     * @return The operation result
     */
    protected int doRemoveEdgeFromAnon(GraphNode graph, AnonymousNode subject, Property property, Node value) {
        String key = subject.getAnonymous().getNodeID();
        EdgeBucket bucket = edgesAnon.get(key);
        if (bucket == null)
            return REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == REMOVE_RESULT_EMPTIED) {
            edgesAnon.remove(key);
            return REMOVE_RESULT_REMOVED;
        }
        return result;
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
            return edgesAnon.get(((AnonymousNode) node).getAnonymous().getNodeID());
        return super.getBucketFor(node);
    }

    @Override
    protected void serializeOtherNodes(Dataset dataset, org.xowl.utils.data.Node treeNodes) {
        org.xowl.utils.data.Node collection = new org.xowl.utils.data.Node(dataset, SERIALIZATION_COLLECTION);
        Attribute attributeType = new Attribute(dataset, Node.SERIALIZATION_TYPE);
        attributeType.setValue(AnonymousNode.TYPE);
        collection.getAttributes().add(attributeType);
        for (AnonymousNode anon : mapNodeAnons.values()) {
            collection.getChildren().add(anon.serialize(dataset));
        }
        treeNodes.getChildren().add(collection);
    }

    @Override
    protected void serializeOtherEdges(Dataset dataset, org.xowl.utils.data.Node treeEdges) {
        org.xowl.utils.data.Node collection = new org.xowl.utils.data.Node(dataset, SERIALIZATION_COLLECTION);
        Attribute attributeType = new Attribute(dataset, Node.SERIALIZATION_TYPE);
        attributeType.setValue(AnonymousNode.TYPE);
        collection.getAttributes().add(attributeType);
        for (Map.Entry<String, AnonymousNode> entry : mapNodeAnons.entrySet()) {
            org.xowl.utils.data.Node nodeBucket = entry.getValue().serialize(dataset);
            Attribute attributeID = new Attribute(dataset, SERIALIZATION_ID);
            attributeID.setValue(entry.getKey());
            nodeBucket.getAttributes().add(attributeID);
            collection.getChildren().add(nodeBucket);
        }
        treeEdges.getChildren().add(collection);
    }

    @Override
    protected void deserializeOtherNodes(org.xowl.utils.data.Node collection) {
        for (org.xowl.utils.data.Node node : collection.getChildren()) {
            AnonymousNode anon = new AnonymousNode(node);
            mapNodeAnons.put(anon.getAnonymous().getNodeID(), anon);
        }
    }

    @Override
    protected void deserializeOtherEdges(org.xowl.utils.data.Node collection) {
        for (org.xowl.utils.data.Node node : collection.getChildren()) {
            String id = (String) node.attribute(SERIALIZATION_ID).getValue();
            EdgeBucket bucket = new EdgeBucket(this, node);
            edgesAnon.put(id, bucket);
        }
    }

    @Override
    protected Node getOherNodeFor(org.xowl.utils.data.Node data) {
        return mapNodeAnons.get(data.attribute(AnonymousNode.SERIALIZATION_ID).getValue());
    }
}