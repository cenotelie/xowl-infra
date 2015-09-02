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
import org.xowl.store.storage.cache.CachedDataset;
import org.xowl.store.storage.cache.CachedNodes;

import java.util.Iterator;

/**
 * Concrete implementation of an in-memory data store
 * This implementation delegates all its behavior to caching stores
 *
 * @author Laurent Wouters
 */
public class InMemoryStore implements BaseStore {
    /**
     * The store for the nodes
     */
    private final CachedNodes nodes;
    /**
     * The store for the dataset
     */
    private final CachedDataset dataset;

    /**
     * Initializes this store
     */
    public InMemoryStore() {
        nodes = new CachedNodes();
        dataset = new CachedDataset();
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
    public void insert(Change change) throws UnsupportedNodeType {
        dataset.insert(change);
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
