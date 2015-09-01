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

package org.xowl.store.storage.cache;

import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.Dataset;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.utils.collections.*;

import java.util.*;

/**
 * Represents a cached RDF dataset
 *
 * @author Laurent Wouters
 */
class CachedDataset implements Dataset {
    /**
     * When adding a quad, something weird happened
     */
    public static final int ADD_RESULT_UNKNOWN = 0;
    /**
     * When adding a quad, it was already present and its multiplicity incremented
     */
    public static final int ADD_RESULT_INCREMENT = 1;
    /**
     * When adding a quad, it was not already present and was therefore new
     */
    public static final int ADD_RESULT_NEW = 2;
    /**
     * When removing a quad, it was not found in the store
     */
    public static final int REMOVE_RESULT_NOT_FOUND = 0;
    /**
     * When removing a quad, it was decremented and the multiplicity did not reached 0 yet
     */
    public static final int REMOVE_RESULT_DECREMENT = 1;
    /**
     * When removing a quad, its multiplicity reached 0
     */
    public static final int REMOVE_RESULT_REMOVED = 2;
    /**
     * When removing a quad, its multiplicity reached 0 and the child dataset is now empty
     */
    public static final int REMOVE_RESULT_EMPTIED = 6;

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
     * The current listeners on this store
     */
    private final Collection<ChangeListener> listeners;

    /**
     * Initializes this dataset
     */
    public CachedDataset() {
        edgesIRI = new HashMap<>();
        edgesBlank = new HashMap<>();
        edgesAnon = new HashMap<>();
        listeners = new ArrayList<>();
    }

    /**
     * Broadcasts the information that a new quad was added
     *
     * @param quad The quad
     */
    private void onQuadAdded(Quad quad) {
        Change change = new Change(quad, true);
        for (ChangeListener listener : listeners)
            listener.onChange(change);
    }

    /**
     * Broadcasts the information that a quad was removed
     *
     * @param quad The quad
     */
    private void onQuadRemoved(Quad quad) {
        Change change = new Change(quad, false);
        for (ChangeListener listener : listeners)
            listener.onChange(change);
    }

    @Override
    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void insert(Change change) throws UnsupportedNodeType {
        Quad quad = change.getValue();
        if (change.isPositive()) {
            int result = doAddEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == ADD_RESULT_NEW) {
                for (ChangeListener listener : listeners) {
                    listener.onChange(change);
                }
            }
        } else {
            int result = doRemoveEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == REMOVE_RESULT_REMOVED) {
                for (ChangeListener listener : listeners) {
                    listener.onChange(change);
                }
            }
        }
    }

    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        Collection<Quad> positives = new ArrayList<>();
        Collection<Quad> negatives = new ArrayList<>();
        for (Quad quad : changeset.getPositives()) {
            int result = doAddEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == ADD_RESULT_NEW)
                positives.add(quad);
        }
        for (Quad quad : changeset.getNegatives()) {
            int result = doRemoveEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == REMOVE_RESULT_REMOVED)
                negatives.add(quad);
        }
        if (!positives.isEmpty() || !negatives.isEmpty()) {
            // transmit the changes only if a there are some!
            Changeset newChangeset = new Changeset(positives, negatives);
            for (ChangeListener listener : listeners) {
                listener.onChange(newChangeset);
            }
        }
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        int result = doAddEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
        if (result == ADD_RESULT_NEW)
            onQuadAdded(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        int result = doAddEdge(graph, subject, property, value);
        if (result == ADD_RESULT_NEW)
            onQuadAdded(new Quad(graph, subject, property, value));
    }

    /**
     * Executes the insertion of a single instance of a quad into this store.
     * If the quad is already in the store, its multiplicity is increased.
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    private int doAddEdge(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case IRINode.TYPE:
                return doAddEdgeFromIRI(graph, (IRINode) subject, property, value);
            case BlankNode.TYPE:
                return doAddEdgeFromBlank(graph, (BlankNode) subject, property, value);
            case AnonymousNode.TYPE:
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

    /**
     * Clears this store by removing all quads
     */
    public void clear() {
        List<CachedQuad> buffer = new ArrayList<>();
        doClearFromAll(buffer);
        if (!buffer.isEmpty()) {
            Changeset changeset = new Changeset(new ArrayList<Quad>(), (Collection) buffer);
            for (ChangeListener listener : listeners) {
                listener.onChange(changeset);
            }
        }
    }

    /**
     * Executes the clear operation removing all quads from this store
     *
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromAll(List<CachedQuad> buffer) {
        doClearFromIRIs(buffer);
        doClearFromBlanks(buffer);
        doClearFromAnons(buffer);
    }

    /**
     * Executes the clear operation removing all quads with IRI nodes as subject
     *
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromIRIs(List<CachedQuad> buffer) {
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
     * Executes the clear operation removing all quads with blank nodes as subject
     *
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromBlanks(List<CachedQuad> buffer) {
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
     * Executes the clear operation removing all quads with anonymous nodes as subject
     *
     * @param buffer The buffer for the removed quads
     */
    private void doClearFromAnons(List<CachedQuad> buffer) {
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

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        if (quad.getGraph() != null && quad.getSubject() != null && quad.getProperty() != null && quad.getObject() != null) {
            // remove a single quad
            int result = doRemoveEdge(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject());
            if (result == REMOVE_RESULT_REMOVED)
                onQuadRemoved(quad);
        } else {
            List<CachedQuad> buffer = new ArrayList<>();
            if (quad.getSubject() == null)
                doRemoveEdgesFromAll(quad.getGraph(), quad.getProperty(), quad.getObject(), buffer);
            else
                doRemoveEdges(quad.getGraph(), quad.getSubject(), quad.getProperty(), quad.getObject(), buffer);
            if (!buffer.isEmpty()) {
                Changeset changeset = new Changeset(new ArrayList<Quad>(), (Collection) buffer);
                for (ChangeListener listener : listeners) {
                    listener.onChange(changeset);
                }
            }
        }
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (graph != null && subject != null && property != null && value != null) {
            // remove a single quad
            int result = doRemoveEdge(graph, subject, property, value);
            if (result == REMOVE_RESULT_REMOVED)
                onQuadRemoved(new Quad(graph, subject, property, value));
        } else {
            List<CachedQuad> buffer = new ArrayList<>();
            if (subject == null)
                doRemoveEdgesFromAll(graph, property, value, buffer);
            else
                doRemoveEdges(graph, subject, property, value, buffer);
            if (!buffer.isEmpty()) {
                Changeset changeset = new Changeset(new ArrayList<Quad>(), (Collection) buffer);
                for (ChangeListener listener : listeners) {
                    listener.onChange(changeset);
                }
            }
        }
    }

    /**
     * Executes the removal operation of a single instance of a quad from this store
     *
     * @param graph    The graph containing the quad
     * @param subject  The quad subject node
     * @param property The quad property
     * @param value    The quad value
     * @return The operation result
     * @throws UnsupportedNodeType when the subject node type is unsupported
     */
    private int doRemoveEdge(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case IRINode.TYPE:
                return doRemoveEdgeFromIRI(graph, (IRINode) subject, property, value);
            case BlankNode.TYPE:
                return doRemoveEdgeFromBlank(graph, (BlankNode) subject, property, value);
            case AnonymousNode.TYPE:
                return doRemoveEdgeFromAnon(graph, (AnonymousNode) subject, property, value);
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Executes the removal operation of a single instance of quads from this store
     *
     * @param graph    The graph to match, or null
     * @param subject  The quad subject node to match, or null
     * @param property The quad property to match, or null
     * @param value    The quad value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdges(GraphNode graph, SubjectNode subject, Property property, Node value, List<CachedQuad> buffer) throws UnsupportedNodeType {
        switch (subject.getNodeType()) {
            case IRINode.TYPE:
                return doRemoveEdgesFromIRI(graph, (IRINode) subject, property, value, buffer);
            case BlankNode.TYPE:
                return doRemoveEdgesFromBlank(graph, (BlankNode) subject, property, value, buffer);
            case AnonymousNode.TYPE:
                return doRemoveEdgesFromAnon(graph, (AnonymousNode) subject, property, value, buffer);
            default:
                throw new UnsupportedNodeType(subject, "Subject node must be IRI or BLANK");
        }
    }

    /**
     * Executes the removal operation of a single instance of quads from this store
     *
     * @param graph    The graph to match, or null
     * @param property The quad property to match, or null
     * @param value    The quad value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromAll(GraphNode graph, Property property, Node value, List<CachedQuad> buffer) {
        doRemoveEdgesFromIRIs(graph, property, value, buffer);
        doRemoveEdgesFromBlanks(graph, property, value, buffer);
        doRemoveEdgesFromAnons(graph, property, value, buffer);
        return REMOVE_RESULT_REMOVED;
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
            return REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == REMOVE_RESULT_EMPTIED) {
            edgesIRI.remove(subject);
            return REMOVE_RESULT_REMOVED;
        }
        return result;
    }

    /**
     * Executes the removal operation of a single instance of a quad with an IRI subject from this store
     *
     * @param graph    The graph to match, or null
     * @param subject  The quad subject node to match, or null
     * @param property The quad property to match, or null
     * @param value    The quad value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromIRI(GraphNode graph, IRINode subject, Property property, Node value, List<CachedQuad> buffer) {
        EdgeBucket bucket = edgesIRI.get(subject);
        if (bucket == null)
            return REMOVE_RESULT_NOT_FOUND;
        int originalSize = buffer.size();
        int result = bucket.removeAll(graph, property, value, buffer);
        if (result == REMOVE_RESULT_EMPTIED)
            edgesIRI.remove(subject);
        for (int j = originalSize; j != buffer.size(); j++)
            buffer.get(j).setSubject(subject);
        return result;
    }

    /**
     * Executes the removal operation of a single instance of matching quads with an IRI subject from this store
     *
     * @param graph    The graph to match, or null
     * @param property The quad property to match, or null
     * @param value    The quad value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromIRIs(GraphNode graph, Property property, Node value, List<CachedQuad> buffer) {
        Iterator<Map.Entry<IRINode, EdgeBucket>> iterator = edgesIRI.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<IRINode, EdgeBucket> entry = iterator.next();
            int originalSize = buffer.size();
            int result = entry.getValue().removeAll(graph, property, value, buffer);
            if (result == REMOVE_RESULT_EMPTIED)
                iterator.remove();
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
        }
        return REMOVE_RESULT_REMOVED;
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
            return REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == REMOVE_RESULT_EMPTIED) {
            edgesBlank.remove(subject);
            return REMOVE_RESULT_REMOVED;
        }
        return result;
    }

    /**
     * Executes the removal operation of a single instance of matching quads with a blank node subject from this store
     *
     * @param graph    The graph to match, or null
     * @param subject  The quad subject node to match, or null
     * @param property The quad property to match, or null
     * @param value    The quad value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromBlank(GraphNode graph, BlankNode subject, Property property, Node value, List<CachedQuad> buffer) {
        EdgeBucket bucket = edgesBlank.get(subject);
        if (bucket == null)
            return REMOVE_RESULT_NOT_FOUND;
        int originalSize = buffer.size();
        int result = bucket.removeAll(graph, property, value, buffer);
        if (result == REMOVE_RESULT_EMPTIED)
            edgesBlank.remove(subject);
        for (int j = originalSize; j != buffer.size(); j++)
            buffer.get(j).setSubject(subject);
        return result;
    }

    /**
     * Executes the removal operation of a single instance of matching quads with a blank node subject from this store
     *
     * @param graph    The graph to match, or null
     * @param property The quad property to match, or null
     * @param value    The quad value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromBlanks(GraphNode graph, Property property, Node value, List<CachedQuad> buffer) {
        Iterator<Map.Entry<BlankNode, EdgeBucket>> iterator = edgesBlank.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlankNode, EdgeBucket> entry = iterator.next();
            int originalSize = buffer.size();
            int result = entry.getValue().removeAll(graph, property, value, buffer);
            if (result == REMOVE_RESULT_EMPTIED)
                iterator.remove();
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
        }
        return REMOVE_RESULT_REMOVED;
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
            return REMOVE_RESULT_NOT_FOUND;
        int result = bucket.remove(graph, property, value);
        if (result == REMOVE_RESULT_EMPTIED) {
            edgesAnon.remove(subject);
            return REMOVE_RESULT_REMOVED;
        }
        return result;
    }

    /**
     * Removes all the matching quads for an anonymous subject
     *
     * @param graph    The graph to match, or null
     * @param subject  The quad subject node to match, or null
     * @param property The quad property to match, or null
     * @param value    The quad value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromAnon(GraphNode graph, AnonymousNode subject, Property property, Node value, List<CachedQuad> buffer) {
        EdgeBucket bucket = edgesAnon.get(subject);
        if (bucket == null)
            return REMOVE_RESULT_NOT_FOUND;
        int originalSize = buffer.size();
        int result = bucket.removeAll(graph, property, value, buffer);
        if (result == REMOVE_RESULT_EMPTIED)
            edgesAnon.remove(subject);
        for (int j = originalSize; j != buffer.size(); j++)
            buffer.get(j).setSubject(subject);
        return result;
    }

    /**
     * Removes all the matching quads for anonymous subjects
     *
     * @param graph    The graph to match, or null
     * @param property The quad property to match, or null
     * @param value    The quad value to match, or null
     * @param buffer   The buffer for the removed quads
     * @return The operation result
     */
    private int doRemoveEdgesFromAnons(GraphNode graph, Property property, Node value, List<CachedQuad> buffer) {
        Iterator<Map.Entry<AnonymousNode, EdgeBucket>> iterator = edgesAnon.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<AnonymousNode, EdgeBucket> entry = iterator.next();
            int originalSize = buffer.size();
            int result = entry.getValue().removeAll(graph, property, value, buffer);
            if (result == REMOVE_RESULT_EMPTIED)
                iterator.remove();
            if (buffer.size() > originalSize) {
                for (int j = originalSize; j != buffer.size(); j++)
                    buffer.get(j).setSubject(entry.getKey());
            }
        }
        return REMOVE_RESULT_REMOVED;
    }

    @Override
    public Iterator<Quad> getAll() {
        return getAll(null, null, null, null);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        return getAll(graph, null, null, null);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return getAll(null, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(final GraphNode graph, final SubjectNode subject, final Property property, final Node object) {
        if (subject == null || subject.getNodeType() == VariableNode.TYPE) {
            return new AdaptingIterator<>(new CombiningIterator<>(getAllSubjects(), new Adapter<Iterator<CachedQuad>>() {
                @Override
                public <X> Iterator<CachedQuad> adapt(X element) {
                    Couple<SubjectNode, EdgeBucket> subject = (Couple<SubjectNode, EdgeBucket>) element;
                    return subject.y.getAll(graph, property, object);
                }
            }), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    Couple<Couple<SubjectNode, EdgeBucket>, CachedQuad> result = (Couple<Couple<SubjectNode, EdgeBucket>, CachedQuad>) element;
                    result.y.setSubject(result.x.x);
                    return result.y;
                }
            });
        } else {
            EdgeBucket bucket = getBucketFor(subject);
            if (bucket == null)
                return new SingleIterator<>(null);
            return new AdaptingIterator<>(bucket.getAll(graph, property, object), new Adapter<Quad>() {
                @Override
                public <X> Quad adapt(X element) {
                    CachedQuad quad = (CachedQuad) element;
                    quad.setSubject(subject);
                    return quad;
                }
            });
        }
    }

    @Override
    public long count() {
        return count(null, null, null, null);
    }

    @Override
    public long count(GraphNode graph) {
        return count(graph, null, null, null);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return count(null, subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        if (subject == null || subject.getNodeType() == VariableNode.TYPE) {
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

    /**
     * Gets an iterator over all the subjects starting edges in the graph
     *
     * @return An iterator over all the subjects starting edges in the graph
     */
    private Iterator<Couple<SubjectNode, EdgeBucket>> getAllSubjects() {
        return new ConcatenatedIterator<Couple<SubjectNode, EdgeBucket>>(new Iterator[]{
                new AdaptingIterator<>(edgesIRI.entrySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                        Map.Entry<IRINode, EdgeBucket> entry = (Map.Entry) element;
                        return new Couple<SubjectNode, EdgeBucket>(entry.getKey(), entry.getValue());
                    }
                }),
                new AdaptingIterator<>(edgesBlank.entrySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                        Map.Entry<BlankNode, EdgeBucket> entry = (Map.Entry) element;
                        return new Couple<SubjectNode, EdgeBucket>(entry.getKey(), entry.getValue());
                    }
                }),
                new AdaptingIterator<>(edgesAnon.entrySet().iterator(), new Adapter<Couple<SubjectNode, EdgeBucket>>() {
                    @Override
                    public <X> Couple<SubjectNode, EdgeBucket> adapt(X element) {
                        Map.Entry<AnonymousNode, EdgeBucket> entry = (Map.Entry) element;
                        return new Couple<SubjectNode, EdgeBucket>(entry.getKey(), entry.getValue());
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
    private EdgeBucket getBucketFor(Node node) {
        switch (node.getNodeType()) {
            case IRINode.TYPE:
                return edgesIRI.get(node);
            case BlankNode.TYPE:
                return edgesBlank.get(node);
            case AnonymousNode.TYPE:
                return edgesAnon.get(node);

            default:
                return null;
        }
    }
}
