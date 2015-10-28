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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a single store backed by on-disk files with a caching layer
 *
 * @author Laurent Wouters
 */
public class SingleStore implements BaseStore {
    /**
     * The backing on-disk store
     */
    private final OnDiskStore backend;
    /**
     * The cache
     */
    private final InMemoryStore cache;
    
    /**
     * Initializes this store
     * 
     * @param directory The parent directory containing the backing files
     */
    public SingleStore(File directory) {
        backend = new OnDiskStore(directory);
        cache = new InMemoryStore();
    }

    @Override
    public void addListener(ChangeListener listener) {
        backend.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        backend.removeListener(listener);
    }

    @Override
    public Iterator<Quad> getAll() {
        return backend.getAll();
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph) {
        return backend.getAll(graph);
    }

    @Override
    public Iterator<Quad> getAll(SubjectNode subject, Property property, Node object) {
        return backend.getAll(subject, property, object);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return backend.getAll(graph, subject, property, object);
    }

    @Override
    public Collection<GraphNode> getGraphs() {
        return backend.getGraphs();
    }

    @Override
    public long count() {
        return backend.count();
    }

    @Override
    public long count(GraphNode graph) {
        return backend.count(graph);
    }

    @Override
    public long count(SubjectNode subject, Property property, Node object) {
        return backend.count(subject, property, object);
    }

    @Override
    public long count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return backend.count(graph, subject, property, object);
    }


    @Override
    public void insert(Changeset changeset) throws UnsupportedNodeType {
        backend.insert(changeset);
    }

    @Override
    public void add(Quad quad) throws UnsupportedNodeType {
        backend.add(quad);
    }

    @Override
    public void add(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        backend.add(graph, subject, property, value);
    }

    @Override
    public void remove(Quad quad) throws UnsupportedNodeType {
        backend.remove(quad);
    }

    @Override
    public void remove(GraphNode graph, SubjectNode subject, Property property, Node value) throws UnsupportedNodeType {
        backend.remove(graph, subject, property, value);
    }

    @Override
    public void clear() {
        backend.clear();
    }

    @Override
    public void clear(GraphNode graph) {
        backend.clear(graph);
    }

    @Override
    public void copy(GraphNode origin, GraphNode target, boolean overwrite) {
        backend.copy(origin, target, overwrite);
    }

    @Override
    public void move(GraphNode origin, GraphNode target) {
        backend.move(origin, target);
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        return backend.getIRINode(graph);
    }

    @Override
    public IRINode getIRINode(String iri) {
        return backend.getIRINode(iri);
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        return backend.getExistingIRINode(iri);
    }

    @Override
    public BlankNode getBlankNode() {
        return backend.getBlankNode();
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return backend.getLiteralNode(lex, datatype, lang);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return backend.getAnonNode(individual);
    }

    @Override
    public void close() throws Exception {
        backend.close();
        cache.close();
    }
}
