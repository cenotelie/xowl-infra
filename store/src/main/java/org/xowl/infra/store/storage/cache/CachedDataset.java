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

package org.xowl.infra.store.storage.cache;

import org.xowl.infra.store.owl.AnonymousNode;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.store.storage.impl.DatasetImpl;
import org.xowl.infra.store.storage.impl.MQuad;
import org.xowl.infra.utils.collections.*;

import java.util.*;

/**
 * Represents a cached RDF dataset
 *
 * @author Laurent Wouters
 */
public class CachedDataset extends DatasetImpl {
    /**
     * The store for edges starting from IRI nodes
     */
    private final Map<IRINode, EdgeBucket> edgesIRI;
    /**
     * The store for edges starting from blank nodes
     */
    private final Map<BlankNode, EdgeBucket> edgesBlank;
    /**
     * The store for edges starting from anonymous nodes
     */
    private final Map<AnonymousNode, EdgeBucket> edgesAnon;

    /**
     * Initializes this dataset
     */
    public CachedDataset() {
        edgesIRI = new HashMap<>();
        edgesBlank = new HashMap<>();
        edgesAnon = new HashMap<>();
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        EdgeBucket bucket = getBucketFor(subject);
        if (bucket == null)
            return 0;
        return bucket.getMultiplicity(graph, property, object);
    }

    @Override
    public Iterator<Quad> getAll(final GraphNode graph, final SubjectNode subject, final Property property, final Node object) {
        if (subject == null || subject.getNodeType() == Node.TYPE_VARIABLE) {
            AdaptingIterator<MQuad, Couple<Couple<SubjectNode, EdgeBucket>, MQuad>> result = new AdaptingIterator<>(new CombiningIterator<Couple<SubjectNode, EdgeBucket>, MQuad>(getAllSubjects(), new Adapter<Iterator<MQuad>>() {
                @Override
                public <X> Iterator<MQuad> adapt(X element) {
                    Couple<SubjectNode, EdgeBucket> subject = (Couple<SubjectNode, EdgeBucket>) element;
                    return subject.y.getAll(graph, property, object);
                }
            }) {
                @Override
                public void remove() {
                    lastRightIterator.remove();
                    if (current.x.y.getSize() == 0)
                        leftIterator.remove();
                }
            }, new Adapter<MQuad>() {
                @Override
                public <X> MQuad adapt(X element) {
                    Couple<Couple<SubjectNode, EdgeBucket>, MQuad> result = (Couple<Couple<SubjectNode, EdgeBucket>, MQuad>) element;
                    result.y.setSubject(result.x.x);
                    return result.y;
                }
            });
            return (Iterator) result;
        } else {
            final EdgeBucket bucket = getBucketFor(subject);
            if (bucket == null)
                return new SingleIterator<>(null);
            return new AdaptingIterator<Quad, MQuad>(bucket.getAll(graph, property, object), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    MQuad quad = (MQuad) element;
                    quad.setSubject(subject);
                    return quad;
                }
            }) {
                @Override
                public void remove() {
                    content.remove();
                    if (bucket.getSize() == 0)
                        removeBucketFor(subject);
                }
            };
        }
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        Collection<GraphNode> result = new ArrayList<>();
        for (EdgeBucket bucket : edgesIRI.values())
            bucket.getGraphs(result);
        for (EdgeBucket bucket : edgesBlank.values())
            bucket.getGraphs(result);
        for (EdgeBucket bucket : edgesAnon.values())
            bucket.getGraphs(result);
        return result;
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        if (subject == null || subject.getNodeType() == Node.TYPE_VARIABLE) {
            int count = 0;
            Iterator<Couple<SubjectNode, EdgeBucket>> iterator = getAllSubjects();
            while (iterator.hasNext())
                count += iterator.next().y.count(graph, property, object);
            return count;
        } else {
            EdgeBucket bucket = getBucketFor(subject);
            if (bucket == null)
                return 0;
            return bucket.count(graph, property, object);
        }
    }

    @Override
    public int doAddQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case Node.TYPE_IRI:
                return doAddEdgeFromIRI(graph, (IRINode) subject, property, value);
            case Node.TYPE_BLANK:
                return doAddEdgeFromBlank(graph, (BlankNode) subject, property, value);
            case Node.TYPE_ANONYMOUS:
                return doAddEdgeFromAnonymous(graph, (AnonymousNode) subject, property, value);
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Executes the insertion of a single instance of a quad with an IRI subject into this store.
     * If the quad is already in the store, its multiplicity is increased.
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    private int doAddEdgeFromIRI(GraphNode graph, IRINode subject, Property property, Node value) {
        EdgeBucket bucket = edgesIRI.get(subject);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesIRI.put(subject, bucket);
        }
        return bucket.add(graph, property, value);
    }

    /**
     * Executes the insertion of a single instance of a quad with an blank node subject into this store.
     * If the quad is already in the store, its multiplicity is increased.
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    private int doAddEdgeFromBlank(GraphNode graph, BlankNode subject, Property property, Node value) {
        EdgeBucket bucket = edgesBlank.get(subject);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesBlank.put(subject, bucket);
        }
        return bucket.add(graph, property, value);
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
    private int doAddEdgeFromAnonymous(GraphNode graph, AnonymousNode subject, Property property, Node value) {
        EdgeBucket bucket = edgesAnon.get(subject);
        if (bucket == null) {
            bucket = new EdgeBucket();
            edgesAnon.put(subject, bucket);
        }
        return bucket.add(graph, property, value);
    }

    @Override
    public int doRemoveQuad(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case Node.TYPE_IRI:
                return doRemoveEdgeFromIRI(graph, (IRINode) subject, property, value);
            case Node.TYPE_BLANK:
                return doRemoveEdgeFromBlank(graph, (BlankNode) subject, property, value);
            case Node.TYPE_ANONYMOUS:
                return doRemoveEdgeFromAnon(graph, (AnonymousNode) subject, property, value);
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    @Override
    public void doRemoveQuads(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {
        if (subject == null)
            doRemoveEdgesFromAll(graph, property, value, bufferDecremented, bufferRemoved);
        else
            doRemoveEdges(graph, subject, property, value, bufferDecremented, bufferRemoved);
    }

    /**
     * Executes the removal operation of a single instance of quads from this store
     *
     * @param graph             The graph to match, or null
     * @param subject           The quad subject node to match, or null
     * @param property          The quad property to match, or null
     * @param value             The quad value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdges(GraphNode graph, SubjectNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case Node.TYPE_IRI:
                return doRemoveEdgesFromIRI(graph, (IRINode) subject, property, value, bufferDecremented, bufferRemoved);
            case Node.TYPE_BLANK:
                return doRemoveEdgesFromBlank(graph, (BlankNode) subject, property, value, bufferDecremented, bufferRemoved);
            case Node.TYPE_ANONYMOUS:
                return doRemoveEdgesFromAnon(graph, (AnonymousNode) subject, property, value, bufferDecremented, bufferRemoved);
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Executes the removal operation of a single instance of quads from this store
     *
     * @param graph             The graph to match, or null
     * @param property          The quad property to match, or null
     * @param value             The quad value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromAll(GraphNode graph, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        doRemoveEdgesFromIRIs(graph, property, value, bufferDecremented, bufferRemoved);
        doRemoveEdgesFromBlanks(graph, property, value, bufferDecremented, bufferRemoved);
        doRemoveEdgesFromAnons(graph, property, value, bufferDecremented, bufferRemoved);
        return DatasetImpl.REMOVE_RESULT_REMOVED;
    }

    /**
     * Executes the removal operation of a single instance of a quad with an IRI subject from this store
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    private int doRemoveEdgeFromIRI(GraphNode graph, IRINode subject, Property property, Node value) {
        EdgeBucket bucket = edgesIRI.get(subject);
        if (bucket == null)
            return DatasetImpl.REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == DatasetImpl.REMOVE_RESULT_EMPTIED) {
            edgesIRI.remove(subject);
            return DatasetImpl.REMOVE_RESULT_REMOVED;
        }
        return result;
    }

    /**
     * Executes the removal operation of a single instance of a quad with an IRI subject from this store
     *
     * @param graph             The graph to match, or null
     * @param subject           The quad subject node to match, or null
     * @param property          The quad property to match, or null
     * @param value             The quad value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromIRI(GraphNode graph, IRINode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        EdgeBucket bucket = edgesIRI.get(subject);
        if (bucket == null)
            return DatasetImpl.REMOVE_RESULT_NOT_FOUND;
        int originalSizeDec = bufferDecremented.size();
        int originalSizeRem = bufferRemoved.size();
        int result = bucket.removeAll(graph, property, value, bufferDecremented, bufferRemoved);
        if (result == DatasetImpl.REMOVE_RESULT_EMPTIED)
            edgesIRI.remove(subject);
        for (int j = originalSizeDec; j != bufferDecremented.size(); j++)
            bufferDecremented.get(j).setSubject(subject);
        for (int j = originalSizeRem; j != bufferRemoved.size(); j++)
            bufferRemoved.get(j).setSubject(subject);
        return result;
    }

    /**
     * Executes the removal operation of a single instance of matching quads with an IRI subject from this store
     *
     * @param graph             The graph to match, or null
     * @param property          The quad property to match, or null
     * @param value             The quad value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromIRIs(GraphNode graph, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        Iterator<Map.Entry<IRINode, EdgeBucket>> iterator = edgesIRI.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<IRINode, EdgeBucket> entry = iterator.next();
            int originalSizeDec = bufferDecremented.size();
            int originalSizeRem = bufferRemoved.size();
            int result = entry.getValue().removeAll(graph, property, value, bufferDecremented, bufferRemoved);
            if (result == DatasetImpl.REMOVE_RESULT_EMPTIED)
                iterator.remove();
            for (int j = originalSizeDec; j != bufferDecremented.size(); j++)
                bufferDecremented.get(j).setSubject(entry.getKey());
            for (int j = originalSizeRem; j != bufferRemoved.size(); j++)
                bufferRemoved.get(j).setSubject(entry.getKey());
        }
        return DatasetImpl.REMOVE_RESULT_REMOVED;
    }

    /**
     * Executes the removal operation of a single instance of a quad with a blank node subject from this store
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     */
    private int doRemoveEdgeFromBlank(GraphNode graph, BlankNode subject, Property property, Node value) {
        EdgeBucket bucket = edgesBlank.get(subject);
        if (bucket == null)
            return DatasetImpl.REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == DatasetImpl.REMOVE_RESULT_EMPTIED) {
            edgesBlank.remove(subject);
            return DatasetImpl.REMOVE_RESULT_REMOVED;
        }
        return result;
    }

    /**
     * Executes the removal operation of a single instance of matching quads with a blank node subject from this store
     *
     * @param graph             The graph to match, or null
     * @param subject           The quad subject node to match, or null
     * @param property          The quad property to match, or null
     * @param value             The quad value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromBlank(GraphNode graph, BlankNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        EdgeBucket bucket = edgesBlank.get(subject);
        if (bucket == null)
            return DatasetImpl.REMOVE_RESULT_NOT_FOUND;
        int originalSizeDec = bufferDecremented.size();
        int originalSizeRem = bufferRemoved.size();
        int result = bucket.removeAll(graph, property, value, bufferDecremented, bufferRemoved);
        if (result == DatasetImpl.REMOVE_RESULT_EMPTIED)
            edgesBlank.remove(subject);
        for (int j = originalSizeDec; j != bufferDecremented.size(); j++)
            bufferDecremented.get(j).setSubject(subject);
        for (int j = originalSizeRem; j != bufferRemoved.size(); j++)
            bufferRemoved.get(j).setSubject(subject);
        return result;
    }

    /**
     * Executes the removal operation of a single instance of matching quads with a blank node subject from this store
     *
     * @param graph             The graph to match, or null
     * @param property          The quad property to match, or null
     * @param value             The quad value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromBlanks(GraphNode graph, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        Iterator<Map.Entry<BlankNode, EdgeBucket>> iterator = edgesBlank.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlankNode, EdgeBucket> entry = iterator.next();
            int originalSizeDec = bufferDecremented.size();
            int originalSizeRem = bufferRemoved.size();
            int result = entry.getValue().removeAll(graph, property, value, bufferDecremented, bufferRemoved);
            if (result == DatasetImpl.REMOVE_RESULT_EMPTIED)
                iterator.remove();
            for (int j = originalSizeDec; j != bufferDecremented.size(); j++)
                bufferDecremented.get(j).setSubject(entry.getKey());
            for (int j = originalSizeRem; j != bufferRemoved.size(); j++)
                bufferRemoved.get(j).setSubject(entry.getKey());
        }
        return DatasetImpl.REMOVE_RESULT_REMOVED;
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
    private int doRemoveEdgeFromAnon(GraphNode graph, AnonymousNode subject, Property property, Node value) {
        EdgeBucket bucket = edgesAnon.get(subject);
        if (bucket == null)
            return DatasetImpl.REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == DatasetImpl.REMOVE_RESULT_EMPTIED) {
            edgesAnon.remove(subject);
            return DatasetImpl.REMOVE_RESULT_REMOVED;
        }
        return result;
    }

    /**
     * Removes all the matching quads for an anonymous subject
     *
     * @param graph             The graph to match, or null
     * @param subject           The quad subject node to match, or null
     * @param property          The quad property to match, or null
     * @param value             The quad value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromAnon(GraphNode graph, AnonymousNode subject, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        EdgeBucket bucket = edgesAnon.get(subject);
        if (bucket == null)
            return DatasetImpl.REMOVE_RESULT_NOT_FOUND;
        int originalSizeDec = bufferDecremented.size();
        int originalSizeRem = bufferRemoved.size();
        int result = bucket.removeAll(graph, property, value, bufferDecremented, bufferRemoved);
        if (result == DatasetImpl.REMOVE_RESULT_EMPTIED)
            edgesAnon.remove(subject);
        for (int j = originalSizeDec; j != bufferDecremented.size(); j++)
            bufferDecremented.get(j).setSubject(subject);
        for (int j = originalSizeRem; j != bufferRemoved.size(); j++)
            bufferRemoved.get(j).setSubject(subject);
        return result;
    }

    /**
     * Removes all the matching quads for anonymous subjects
     *
     * @param graph             The graph to match, or null
     * @param property          The quad property to match, or null
     * @param value             The quad value to match, or null
     * @param bufferDecremented The buffer for the decremented quads
     * @param bufferRemoved     The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromAnons(GraphNode graph, Property property, Node value, List<MQuad> bufferDecremented, List<MQuad> bufferRemoved) {
        Iterator<Map.Entry<AnonymousNode, EdgeBucket>> iterator = edgesAnon.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<AnonymousNode, EdgeBucket> entry = iterator.next();
            int originalSizeDec = bufferDecremented.size();
            int originalSizeRem = bufferRemoved.size();
            int result = entry.getValue().removeAll(graph, property, value, bufferDecremented, bufferRemoved);
            if (result == DatasetImpl.REMOVE_RESULT_EMPTIED)
                iterator.remove();
            for (int j = originalSizeDec; j != bufferDecremented.size(); j++)
                bufferDecremented.get(j).setSubject(entry.getKey());
            for (int j = originalSizeRem; j != bufferRemoved.size(); j++)
                bufferRemoved.get(j).setSubject(entry.getKey());
        }
        return DatasetImpl.REMOVE_RESULT_REMOVED;
    }

    @Override
    public void doClear(List<MQuad> buffer) {
        doClearFromIRIs(buffer);
        doClearFromBlanks(buffer);
        doClearFromAnons(buffer);
    }

    @Override
    public void doClear(GraphNode graph, List<MQuad> buffer) {
        doClearFromIRIs(graph, buffer);
        doClearFromBlanks(graph, buffer);
        doClearFromAnons(graph, buffer);
    }

    /**
     * Executes the clear operation removing all quads with IRI nodes as subject
     *
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromIRIs(List<MQuad> buffer) {
        for (Map.Entry<IRINode, EdgeBucket> entry : edgesIRI.entrySet()) {
            int originalSize = buffer.size();
            entry.getValue().clear(buffer);
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
        }
        edgesIRI.clear();
    }

    /**
     * Executes the clear operation removing all quads with IRI nodes as subject for the specified graph
     *
     * @param graph  The graph to clear
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromIRIs(GraphNode graph, List<MQuad> buffer) {
        List<IRINode> toDelete = new ArrayList<>();
        for (Map.Entry<IRINode, EdgeBucket> entry : edgesIRI.entrySet()) {
            int originalSize = buffer.size();
            boolean empty = entry.getValue().clear(graph, buffer);
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (IRINode key : toDelete)
            edgesIRI.remove(key);
    }

    /**
     * Executes the clear operation removing all quads with blank nodes as subject
     *
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromBlanks(List<MQuad> buffer) {
        for (Map.Entry<BlankNode, EdgeBucket> entry : edgesBlank.entrySet()) {
            int originalSize = buffer.size();
            entry.getValue().clear(buffer);
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
        }
        edgesBlank.clear();
    }

    /**
     * Executes the clear operation removing all quads with blank nodes as subject for the specified graph
     *
     * @param graph  The graph to clear
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromBlanks(GraphNode graph, List<MQuad> buffer) {
        List<BlankNode> toDelete = new ArrayList<>();
        for (Map.Entry<BlankNode, EdgeBucket> entry : edgesBlank.entrySet()) {
            int originalSize = buffer.size();
            boolean empty = entry.getValue().clear(graph, buffer);
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (BlankNode key : toDelete)
            edgesBlank.remove(key);
    }

    /**
     * Executes the clear operation removing all quads with anonymous nodes as subject
     *
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromAnons(List<MQuad> buffer) {
        for (Map.Entry<AnonymousNode, EdgeBucket> entry : edgesAnon.entrySet()) {
            int originalSize = buffer.size();
            entry.getValue().clear(buffer);
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
        }
        edgesAnon.clear();
    }

    /**
     * Executes the clear operation removing all quads with anonymous nodes as subject for the specified graph
     *
     * @param graph  The graph to clear
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromAnons(GraphNode graph, List<MQuad> buffer) {
        List<AnonymousNode> toDelete = new ArrayList<>();
        for (Map.Entry<AnonymousNode, EdgeBucket> entry : edgesAnon.entrySet()) {
            int originalSize = buffer.size();
            boolean empty = entry.getValue().clear(graph, buffer);
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (AnonymousNode key : toDelete)
            edgesAnon.remove(key);
    }

    @Override
    public void doCopy(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        doCopyFromIRIs(origin, target, bufferOld, bufferNew, overwrite);
        doCopyFromBlanks(origin, target, bufferOld, bufferNew, overwrite);
        doCopyFromAnons(origin, target, bufferOld, bufferNew, overwrite);
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed if the overwrite flag is used.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is not affected.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     */
    private void doCopyFromIRIs(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        List<IRINode> toDelete = new ArrayList<>();
        for (Map.Entry<IRINode, EdgeBucket> entry : edgesIRI.entrySet()) {
            int originalSizeOld = bufferOld.size();
            int originalSizeNew = bufferNew.size();
            boolean empty = entry.getValue().copy(origin, target, bufferOld, bufferNew, overwrite);
            for (int j = originalSizeOld; j != bufferOld.size(); j++)
                bufferOld.get(j).setSubject(entry.getKey());
            for (int j = originalSizeNew; j != bufferNew.size(); j++)
                bufferNew.get(j).setSubject(entry.getKey());
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (IRINode key : toDelete)
            edgesIRI.remove(key);
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed if the overwrite flag is used.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is not affected.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     */
    private void doCopyFromBlanks(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        List<BlankNode> toDelete = new ArrayList<>();
        for (Map.Entry<BlankNode, EdgeBucket> entry : edgesBlank.entrySet()) {
            int originalSizeOld = bufferOld.size();
            int originalSizeNew = bufferNew.size();
            boolean empty = entry.getValue().copy(origin, target, bufferOld, bufferNew, overwrite);
            for (int j = originalSizeOld; j != bufferOld.size(); j++)
                bufferOld.get(j).setSubject(entry.getKey());
            for (int j = originalSizeNew; j != bufferNew.size(); j++)
                bufferNew.get(j).setSubject(entry.getKey());
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (BlankNode key : toDelete)
            edgesBlank.remove(key);
    }

    /**
     * Copies all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed if the overwrite flag is used.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is not affected.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     * @param overwrite Whether to overwrite quads from the target graph
     */
    private void doCopyFromAnons(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew, boolean overwrite) {
        List<AnonymousNode> toDelete = new ArrayList<>();
        for (Map.Entry<AnonymousNode, EdgeBucket> entry : edgesAnon.entrySet()) {
            int originalSizeOld = bufferOld.size();
            int originalSizeNew = bufferNew.size();
            boolean empty = entry.getValue().copy(origin, target, bufferOld, bufferNew, overwrite);
            for (int j = originalSizeOld; j != bufferOld.size(); j++)
                bufferOld.get(j).setSubject(entry.getKey());
            for (int j = originalSizeNew; j != bufferNew.size(); j++)
                bufferNew.get(j).setSubject(entry.getKey());
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (AnonymousNode key : toDelete)
            edgesAnon.remove(key);
    }

    @Override
    public void doMove(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        doMoveFromIRIs(origin, target, bufferOld, bufferNew);
        doMoveFromBlanks(origin, target, bufferOld, bufferNew);
        doMoveFromAnons(origin, target, bufferOld, bufferNew);
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is always removed.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     */
    private void doMoveFromIRIs(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        List<IRINode> toDelete = new ArrayList<>();
        for (Map.Entry<IRINode, EdgeBucket> entry : edgesIRI.entrySet()) {
            int originalSizeOld = bufferOld.size();
            int originalSizeNew = bufferNew.size();
            boolean empty = entry.getValue().move(origin, target, bufferOld, bufferNew);
            for (int j = originalSizeOld; j != bufferOld.size(); j++)
                bufferOld.get(j).setSubject(entry.getKey());
            for (int j = originalSizeNew; j != bufferNew.size(); j++)
                bufferNew.get(j).setSubject(entry.getKey());
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (IRINode key : toDelete)
            edgesIRI.remove(key);
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is always removed.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     */
    private void doMoveFromBlanks(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        List<BlankNode> toDelete = new ArrayList<>();
        for (Map.Entry<BlankNode, EdgeBucket> entry : edgesBlank.entrySet()) {
            int originalSizeOld = bufferOld.size();
            int originalSizeNew = bufferNew.size();
            boolean empty = entry.getValue().move(origin, target, bufferOld, bufferNew);
            for (int j = originalSizeOld; j != bufferOld.size(); j++)
                bufferOld.get(j).setSubject(entry.getKey());
            for (int j = originalSizeNew; j != bufferNew.size(); j++)
                bufferNew.get(j).setSubject(entry.getKey());
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (BlankNode key : toDelete)
            edgesBlank.remove(key);
    }

    /**
     * Moves all the quads with the specified origin graph, to quads with the target graph.
     * Pre-existing quads from the target graph that do not correspond to an equivalent in the origin graph are removed.
     * If a target quad already exists, its multiplicity is incremented, otherwise it is created.
     * The quad in the origin graph is always removed.
     *
     * @param origin    The origin graph
     * @param target    The target graph
     * @param bufferOld The buffer of the removed quads
     * @param bufferNew The buffer of the new quads
     */
    private void doMoveFromAnons(GraphNode origin, GraphNode target, List<MQuad> bufferOld, List<MQuad> bufferNew) {
        List<AnonymousNode> toDelete = new ArrayList<>();
        for (Map.Entry<AnonymousNode, EdgeBucket> entry : edgesAnon.entrySet()) {
            int originalSizeOld = bufferOld.size();
            int originalSizeNew = bufferNew.size();
            boolean empty = entry.getValue().move(origin, target, bufferOld, bufferNew);
            for (int j = originalSizeOld; j != bufferOld.size(); j++)
                bufferOld.get(j).setSubject(entry.getKey());
            for (int j = originalSizeNew; j != bufferNew.size(); j++)
                bufferNew.get(j).setSubject(entry.getKey());
            if (empty)
                toDelete.add(entry.getKey());
        }
        for (AnonymousNode key : toDelete)
            edgesAnon.remove(key);
    }

    /**
     * Gets an iterator over all the subjects starting edges in the graph
     *
     * @return An iterator over all the subjects starting edges in the graph
     */
    private Iterator<Couple<SubjectNode, EdgeBucket>> getAllSubjects() {
        AdaptingIterator<Couple<SubjectNode, EdgeBucket>, Map.Entry<IRINode, EdgeBucket>> iterator1 = new AdaptingIterator<>(edgesIRI.entrySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
            @Override
            public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                Map.Entry<IRINode, EdgeBucket> entry = (Map.Entry) element;
                return new Couple<SubjectNode, EdgeBucket>(entry.getKey(), entry.getValue());
            }
        });
        AdaptingIterator<Couple<SubjectNode, EdgeBucket>, Map.Entry<BlankNode, EdgeBucket>> iterator2 = new AdaptingIterator<>(edgesBlank.entrySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
            @Override
            public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                Map.Entry<BlankNode, EdgeBucket> entry = (Map.Entry) element;
                return new Couple<SubjectNode, EdgeBucket>(entry.getKey(), entry.getValue());
            }
        });
        AdaptingIterator<Couple<SubjectNode, EdgeBucket>, Map.Entry<AnonymousNode, EdgeBucket>> iterator3 = new AdaptingIterator<>(edgesAnon.entrySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
            @Override
            public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                Map.Entry<AnonymousNode, EdgeBucket> entry = (Map.Entry) element;
                return new Couple<SubjectNode, EdgeBucket>(entry.getKey(), entry.getValue());
            }
        });
        return new ConcatenatedIterator<>(new Iterator[]{
                iterator1,
                iterator2,
                iterator3
        });
    }

    /**
     * Gets the edge bucket for the specified node
     *
     * @param node A node
     * @return The associated edge bucket, or null if there is none
     */
    private EdgeBucket getBucketFor(Node node) {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                return edgesIRI.get(node);
            case Node.TYPE_BLANK:
                return edgesBlank.get(node);
            case Node.TYPE_ANONYMOUS:
                return edgesAnon.get(node);

            default:
                return null;
        }
    }

    /**
     * Removes the edge bucket for the specified node
     *
     * @param node A node
     */
    private void removeBucketFor(Node node) {
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                edgesIRI.remove(node);
                break;
            case Node.TYPE_BLANK:
                edgesBlank.remove(node);
                break;
            case Node.TYPE_ANONYMOUS:
                edgesAnon.remove(node);
                break;
        }
    }
}
