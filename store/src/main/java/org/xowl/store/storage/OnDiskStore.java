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

package org.xowl.store.storage;

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.persistent.PersistedDataset;
import org.xowl.store.storage.persistent.PersistedNodes;
import org.xowl.store.storage.persistent.StorageException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Concrete implementation of a persisted data store.
 * This implementation delegates all its behavior to persisted stores.
 * This class is NOT thread safe.
 *
 * @author Laurent Wouters
 */
class OnDiskStore implements BaseStore {
    /**
     * The store for the nodes
     */
    private final PersistedNodes nodes;
    /**
     * The store for the dataset
     */
    private final PersistedDataset dataset;

    /**
     * Initializes this store
     *
     * @param directory  The parent directory containing the backing files
     * @param isReadonly Whether this store is in readonly mode
     * @throws IOException      When the backing files cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public OnDiskStore(File directory, boolean isReadonly) throws IOException, StorageException {
        nodes = new PersistedNodes(directory, isReadonly);
        dataset = new PersistedDataset(nodes, directory, isReadonly);
    }

    @Override
    public boolean commit() {
        boolean success = nodes.commit();
        success &= dataset.commit();
        return success;
    }

    @Override
    public boolean rollback() {
        boolean success = nodes.rollback();
        success &= dataset.rollback();
        return success;
    }

    @Override
    public void addListener(ChangeListener listener) {
        dataset.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        dataset.removeListener(listener);
    }

    @Override
    public long getMultiplicity(Quad quad) {
        return dataset.getMultiplicity(quad);
    }

    @Override
    public long getMultiplicity(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return dataset.getMultiplicity(graph, subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll() {
        return dataset.getAll();
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        return dataset.getAll(graph);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return dataset.getAll(subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return dataset.getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return dataset.getGraphs();
    }

    @Override
    public long count() {
        return dataset.count();
    }

    @Override
    public long count(GraphNode graph) {
        return dataset.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return dataset.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return dataset.count(graph, subject, property, object);
    }


    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        dataset.insert(changeset);
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        dataset.add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        dataset.add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        dataset.remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        dataset.remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        dataset.clear();
    }

    @Override
    public void clear(GraphNode graph) {
        dataset.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        dataset.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        dataset.move(origin, target);
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        return nodes.getIRINode(graph);
    }

    @Override
    public IRINode getIRINode(String iri) {
        return nodes.getIRINode(iri);
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        return nodes.getExistingIRINode(iri);
    }

    @Override
    public BlankNode getBlankNode() {
        return nodes.getBlankNode();
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return nodes.getLiteralNode(lex, datatype, lang);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return nodes.getAnonNode(individual);
    }

    @Override
    public void close() throws Exception {
        Exception ex = null;
        try {
            nodes.close();
        } catch (Exception exception) {
            ex = exception;
        }
        try {
            dataset.close();
        } catch (Exception exception) {
            // TODO: clean this, the previous ex could be swallowed
            ex = exception;
        }
        throw ex;
    }
}
