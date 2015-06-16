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

package org.xowl.store.rdf;

import org.xowl.utils.collections.*;

import java.util.*;

/**
 * Represents a store that is the projection of another according to a query
 *
 * @author Laurent Wouters
 */
public class ProjectedStore extends AbstractStore implements QueryObserver {
    /**
     * The base query
     */
    private Query query;
    /**
     * The current solutions
     */
    private Collection<QuerySolution> solutions;

    /**
     * Gets the base query represented by this store
     * @return The base query
     */
    public Query getQuery() {
        return query;
    }

    /**
     * Initializes this store
     * @param query The base query
     */
    public ProjectedStore(Query query) {
        this.query = query;
        this.solutions = new ArrayList<>();
    }

    @Override
    public void onNewSolution(QuerySolution solution) {
        solutions.add(solution);
    }

    @Override
    public void onSolutionRevoked(QuerySolution solution) {
        solutions.remove(solution);
    }

    @Override
    public Iterator<Quad> getAll(GraphNode graph, SubjectNode subject, Property property, Node object) {
        // the pattern quads that can produce quads of the expected form
        List<Quad> quads = getMatching(graph, subject, property, object);
        if (quads.isEmpty()) {
            return new Iterator<Quad>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Quad next() {
                    return null;
                }
            };
        }
        final Map<Node, Object> known = new HashMap<>();
        return new SkippableIterator<>(new AdaptingIterator<>(new CombiningIterator<>(quads.iterator(), new Adapter<Iterator<QuerySolution>>() {
            @Override
            public <X> Iterator<QuerySolution> adapt(X element) {
                known.clear();
                return solutions.iterator();
            }
        }), new Adapter<Quad>() {
            @Override
            public <X> Quad adapt(X element) {
                Couple<Quad, QuerySolution> couple = (Couple<Quad, QuerySolution>) element;
                Map<Node, Object> map = known;
                map = check(couple.x.getGraph(), couple.y, map);
                map = check(couple.x.getSubject(), couple.y, map);
                map = check(couple.x.getProperty(), couple.y, map);
                map = check(couple.x.getObject(), couple.y, map);
                if (map.get(null) != couple.y)
                    // this matches another solution, do not duplicate
                    return null;
                return new Quad(
                        couple.x.getGraph().getNodeType() == VariableNode.TYPE ? (GraphNode) couple.y.get((VariableNode) couple.x.getGraph()) : couple.x.getGraph(),
                        couple.x.getSubject().getNodeType() == VariableNode.TYPE ? (SubjectNode) couple.y.get((VariableNode) couple.x.getSubject()) : couple.x.getSubject(),
                        couple.x.getProperty().getNodeType() == VariableNode.TYPE ? (Property) couple.y.get((VariableNode) couple.x.getProperty()) : couple.x.getProperty(),
                        couple.x.getObject().getNodeType() == VariableNode.TYPE ? couple.y.get((VariableNode) couple.x.getObject()) : couple.x.getObject()
                );
            }
        }));
    }

    @Override
    public int count(GraphNode graph, SubjectNode subject, Property property, Node object) {
        return 0;
    }

    /**
     * Checks the specified pattern node and query solution against the map of known equivalents
     * @param patternNode A node in a quad pattern
     * @param solution A query solution
     * @param map The
     * @return
     */
    private Map<Node, Object> check(Node patternNode, QuerySolution solution, Map<Node, Object> map) {
        if (patternNode.getNodeType() != VariableNode.TYPE) {
            return map;
        }
        Node value = solution.get((VariableNode) patternNode);
        Map<Node, Object> next = (Map<Node, Object>) map.get(value);
        if (next == null) {
            next = new HashMap<>();
            next.put(null, solution);
            map.put(value, next);
        }
        return next;
    }

    /**
     * Gets the positive quad pattern in the base query that can produce quads of the specified form
     * @param graph    A containing graph to match, or null
     * @param subject  A subject node to match, or null
     * @param property A property to match, or null
     * @param object   An object node to match, or null
     * @return The matching quad patterns
     */
    private List<Quad> getMatching(GraphNode graph, SubjectNode subject, Property property, Node object) {
        List<Quad> quads = new ArrayList<>();
        Map<VariableNode, Node> mapping = new HashMap<>();
        for (Quad quad : query.getPositives()) {
            mapping.clear();
            if (match(quad.getGraph(), graph, mapping)
                    && match(quad.getSubject(), subject, mapping)
                    && match(quad.getProperty(), property, mapping)
                    && match(quad.getObject(), object, mapping)) {
                quads.add(quad);
            }
        }
        return  quads;
    }

    /**
     * Gets whether the two nodes match
     * @param node1 The first node
     * @param node2 The second node
     * @param mapping The current variable mappings
     * @return true if the nodes match
     */
    private boolean match(Node node1, Node node2, Map<VariableNode, Node> mapping) {
        if (node1 == null || node2 == null) {
            // this is an arbitrary node => match anything
            return true;
        } else if (node1.getNodeType() == VariableNode.TYPE) {
            // this is a variable
            Node value = mapping.get(node1);
            if (value != null) {
                // the variable is bound
                return value.equals(node2);
            } else {
                // not yet bound
                mapping.put((VariableNode) node1, node2);
                if (node2.getNodeType() == VariableNode.TYPE)
                    mapping.put((VariableNode) node2, node1);
                return true;
            }
        } else if (node2.getNodeType() == VariableNode.TYPE) {
            // this is a variable
            Node value = mapping.get(node2);
            if (value != null) {
                // the variable is bound
                return value.equals(node1);
            } else {
                // not yet bound
                mapping.put((VariableNode) node2, node1);
                return true;
            }
        } else {
            return node1.equals(node2);
        }
    }
}
