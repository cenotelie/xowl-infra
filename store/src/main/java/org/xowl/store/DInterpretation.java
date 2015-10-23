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
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;
import org.xowl.utils.collections.ConcatenatedIterator;

import java.util.*;

/**
 * Implements RDF D-Interpretations
 * (see <a href="http://www.w3.org/TR/2013/WD-rdf11-mt-20130723/#D_interpretations">D-Interpretations</a>)
 * This class acts as a proxy dataset that complements an original one with datatype entailments
 *
 * @author Laurent Wouters
 */
class DInterpretation implements Dataset {

    private interface Interpreter {
        String getDatatype();

        boolean isCorrect(LiteralNode node);

        boolean entails(LiteralNode premise, LiteralNode conclusion);
    }




    private final Dataset dataset;

    private final Map<String, Interpreter> interpreters;

    private boolean isUnsat;


    /**
     * Initializes a default interpretation
     *
     * @param target The target dataset for entailments
     */
    public DInterpretation(Dataset target) {
        this(target, new String[]{Vocabulary.rdfLangString, Vocabulary.xsdString});
    }

    /**
     * Initializes this D-Interpretation
     *
     * @param target The target dataset for entailments
     * @param recognized The explicitly recognized datatypes
     */
    public DInterpretation(Dataset target, String[] recognized) {
        this.recognized = Collections.unmodifiableList(Arrays.asList(recognized));
        this.unrecognized = Collections.EMPTY_LIST;
        this.target = target;
    }

    /**
     * Initializes this D-Interpretation
     *
     * @param target The target dataset for entailments
     * @param recognized   The explicitly recognized datatypes
     * @param unrecognized The explicitly unrecognized datatypes
     */
    public DInterpretation(Dataset target, String[] recognized, String[] unrecognized) {
        this.recognized = Collections.unmodifiableList(Arrays.asList(recognized));
        this.unrecognized = Collections.unmodifiableList(Arrays.asList(unrecognized));
        this.target = target;
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
}
