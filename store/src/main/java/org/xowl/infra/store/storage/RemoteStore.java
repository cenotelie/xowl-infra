/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.store.storage;

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.owl.AnonymousNode;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.store.storage.remote.Connection;
import org.xowl.infra.store.storage.remote.RemoteDataset;

import java.util.Collection;
import java.util.Iterator;

/**
 * Concrete implementation of a data store
 *
 * @author Laurent Wouters
 */
class RemoteStore extends BaseStore {
    /**
     * The nodes to use
     */
    private final CachedNodes nodes;
    /**
     * The remote dataset
     */
    private final RemoteDataset dataset;
    /**
     * Whether this store is read-only
     */
    private final boolean isReadonly;

    /**
     * Initializes this store
     *
     * @param connection The connection to the remote host
     * @param isReadonly Whether this store is read-only
     */
    public RemoteStore(Connection connection, boolean isReadonly) {
        this.nodes = new CachedNodes();
        this.dataset = new RemoteDataset(nodes, connection);
        this.isReadonly = isReadonly;
    }

    @Override
    public void close() throws Exception {
        dataset.close();
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
        if (isReadonly)
            throw new IllegalStateException("Read-only");
        dataset.insert(changeset);
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        if (isReadonly)
            throw new IllegalStateException("Read-only");
        dataset.add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (isReadonly)
            throw new IllegalStateException("Read-only");
        dataset.add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        if (isReadonly)
            throw new IllegalStateException("Read-only");
        dataset.remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        if (isReadonly)
            throw new IllegalStateException("Read-only");
        dataset.remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        if (isReadonly)
            throw new IllegalStateException("Read-only");
        dataset.clear();
    }

    @Override
    public void clear(GraphNode graph) {
        if (isReadonly)
            throw new IllegalStateException("Read-only");
        dataset.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        if (isReadonly)
            throw new IllegalStateException("Read-only");
        dataset.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        if (isReadonly)
            throw new IllegalStateException("Read-only");
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
}
