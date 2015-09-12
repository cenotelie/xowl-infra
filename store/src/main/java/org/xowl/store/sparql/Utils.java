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

package org.xowl.store.sparql;

import org.xowl.lang.actions.DynamicExpression;
import org.xowl.lang.owl2.IRI;
import org.xowl.store.Datatypes;
import org.xowl.store.Repository;
import org.xowl.store.owl.DynamicNode;
import org.xowl.store.rdf.*;
import org.xowl.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for the evaluation of SPARQL queries
 *
 * @author Laurent Wouters
 */
class Utils {
    /**
     * Evaluates a dynamic expression to a native value
     *
     * @param repository The current repository
     * @param solution   The current bindings
     * @param expression The expression to evaluate
     * @return The evaluated native value
     */
    public static Object evaluateNative(Repository repository, QuerySolution solution, DynamicExpression expression) {
        Map<String, Object> bindings = new HashMap<>();
        for (Couple<VariableNode, Node> binding : solution)
            bindings.put(binding.x.getName(), org.xowl.store.rdf.Utils.getNative(binding.y));
        repository.getEvaluator().push(bindings);
        Object result = repository.getEvaluator().eval(expression);
        repository.getEvaluator().pop();
        return result;
    }

    /**
     * Evaluates a SPARQL expression as a boolean native value
     *
     * @param repository The current repository
     * @param solution   The current bindings
     * @param expression The expression to evaluate
     * @return The evaluated node
     */
    public static boolean evaluateNativeBoolean(Repository repository, QuerySolution solution, Expression expression) throws EvalException {
        Object value = expression.eval(repository, solution);
        if (value instanceof DynamicNode)
            value = evaluateNative(repository, solution, ((DynamicNode) value).getDynamicExpression());
        if (value instanceof LiteralNode)
            value = Datatypes.toNative((LiteralNode) value);
        return (value instanceof Boolean && (Boolean) value);
    }

    /**
     * Evaluates a SPARQL expression as a RDF node
     *
     * @param repository The current repository
     * @param solution   The current bindings
     * @param expression The expression to evaluate
     * @return The evaluated node
     */
    public static Node evaluateRDF(Repository repository, QuerySolution solution, Expression expression) throws EvalException {
        Object value = expression.eval(repository, solution);
        if (value instanceof DynamicNode)
            value = evaluateNative(repository, solution, ((DynamicNode) value).getDynamicExpression());
        if (value instanceof Node) {
            return (Node) value;
        } else if (value instanceof IRI) {
            return repository.getStore().getIRINode(((IRI) value).getHasValue());
        } else {
            Couple<String, String> literal = Datatypes.toLiteral(value);
            return repository.getStore().getLiteralNode(literal.x, literal.y, null);
        }
    }

    /**
     * Instantiates a RDF node given a solution, i.e. a set of bindings for the variable nodes and the current mapping of blank nodes.
     * A variable node is replaced by its value in the solution.
     * A blank node is replaced by its instance-specific value in the blanks mapping.
     * If this is the first time this blank node has been encountered in the template, the new instance-specific blank node is created and associated.
     *
     * @param repository The current repository
     * @param solution   The current bindings
     * @param blanks     The current mapping of blank nodes to their instance
     * @param node       The node to instantiate
     * @return The instantiated node
     */
    private static Node instantiate(Repository repository, QuerySolution solution, Map<Node, Node> blanks, Node node) throws EvalException {
        if (node == null)
            throw new EvalException("The node cannot be null");
        Node result = node;
        if (result.getNodeType() == Node.TYPE_VARIABLE) {
            Node value = solution.get((VariableNode) result);
            if (value == null)
                throw new EvalException("Unbound variable " + ((VariableNode) result).getName() + " in the template");
            result = value;
        } else if (result.getNodeType() == Node.TYPE_BLANK) {
            Node value = blanks.get(node);
            if (value == null) {
                value = repository.getStore().getBlankNode();
                blanks.put(node, value);
            }
            return value;
        }
        if (result.getNodeType() == Node.TYPE_DYNAMIC && repository.getEvaluator() != null) {
            Object value = evaluateNative(repository, solution, ((DynamicNode) result).getDynamicExpression());
            if (value instanceof Node) {
                result = (Node) value;
            } else if (value instanceof IRI) {
                result = repository.getStore().getIRINode(((IRI) value).getHasValue());
            } else {
                Couple<String, String> literal = Datatypes.toLiteral(value);
                result = repository.getStore().getLiteralNode(literal.x, literal.y, null);
            }
        }
        return result;
    }

    /**
     * Instantiate of template of quads given a solution, i.e. a set of bindings for the variable nodes.
     * This replaces the variable nodes in the template by their value.
     * The blank nodes in the template are also instantiated into new instance-specific blank nodes.
     *
     * @param repository The current repository
     * @param template   The template
     * @param solution   The query solution mapping the variables to their value
     * @param buffer     The buffer for the realized quads
     */
    public static void instantiate(Repository repository, QuerySolution solution, Collection<Quad> template, Collection<Quad> buffer) throws EvalException {
        Map<Node, Node> blanks = new HashMap<>();
        for (Quad quad : template) {
            GraphNode graph = (GraphNode) instantiate(repository, solution, blanks, quad.getGraph());
            SubjectNode subject = (SubjectNode) instantiate(repository, solution, blanks, quad.getSubject());
            Property property = (Property) instantiate(repository, solution, blanks, quad.getProperty());
            Node object = instantiate(repository, solution, blanks, quad.getObject());
            buffer.add(new Quad(graph, subject, property, object));
        }
    }

    /**
     * Performs the join of two solution sets.
     * A solution is in the joined set is the merged solution of a solution on the left and another on the right.
     * The join is the set of all the merged solutions between compatible solutions on the left and right.
     *
     * @param left  A set of solutions
     * @param right Another set of solutions
     * @return The join set
     */
    public static Collection<QuerySolution> join(Collection<QuerySolution> left, Collection<QuerySolution> right) {
        Collection<QuerySolution> result = new ArrayList<>();
        for (QuerySolution l : left) {
            for (QuerySolution r : right) {
                QuerySolution j = merge(l, r);
                if (j != null)
                    result.add(j);
            }
        }
        return result;
    }

    /**
     * Performs the set difference between a left set of solution and a right set of solutions.
     * The result is the subset of solutions on the left that are not compatible with a solution on the right.
     *
     * @param left  A set of solutions
     * @param right Another set of solutions
     * @return The difference
     */
    public static Collection<QuerySolution> minus(Collection<QuerySolution> left, Collection<QuerySolution> right) {
        Collection<QuerySolution> result = new ArrayList<>();
        for (QuerySolution l : left) {
            boolean match = false;
            for (QuerySolution r : right) {
                if (compatible(l, r)) {
                    match = true;
                    break;
                }
            }
            if (!match)
                result.add(l);
        }
        return result;
    }

    /**
     * Determines whether two solutions are compatible.
     * Two solutions are compatible iff for all variables bound by both solution, their value is the same.
     * The variables that are bound in a solution but not the other are not considered.
     *
     * @param left  A solution
     * @param right Another solution
     * @return Whether the two solutions are compatible
     */
    public static boolean compatible(QuerySolution left, QuerySolution right) {
        for (Couple<VariableNode, Node> binding : left) {
            Node value = right.get(binding.x);
            if (value != null && !org.xowl.store.rdf.Utils.same(binding.y, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Builds the merge of two solutions.
     * If the two solutions are not compatible, null is returned.
     * If the solutions are compatible, the merged solution contains all the bindings from both solutions.
     *
     * @param left  A solution
     * @param right Another solution
     * @return The merged solution, or null if the two were not compatible
     */
    public static QuerySolution merge(QuerySolution left, QuerySolution right) {
        Collection<Couple<VariableNode, Node>> bindings = new ArrayList<>();
        for (Couple<VariableNode, Node> binding : left) {
            Node value = right.get(binding.x);
            if (value != null) {
                if (!org.xowl.store.rdf.Utils.same(binding.y, value))
                    return null;
                bindings.add(binding);
            } else {
                bindings.add(binding);
            }
        }
        for (Couple<VariableNode, Node> binding : right) {
            boolean found = false;
            for (Couple<VariableNode, Node> present : bindings) {
                if (org.xowl.store.rdf.Utils.same(binding.x, present.x)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                bindings.add(binding);
            }
        }
        return new QuerySolution(bindings);
    }

    /**
     * Quotes the string with the CSV requirements
     * (See <a href="http://www.ietf.org/rfc/rfc4180.txt">CSV</a>)
     *
     * @param value The value to quote
     * @return The safe quoted value
     */
    public static String quoteCSV(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"')
                builder.append('"');
            builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Quotes the string with the TSV requirements
     *
     * @param value The value to quote
     * @return The safe quoted value
     */
    public static String quoteTSV(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"')
                builder.append("\\\"");
            else if (c == '\t')
                builder.append("\\t");
            else if (c == '\r')
                builder.append("\\r");
            else if (c == '\n')
                builder.append("\\n");
            else
                builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Quotes the string with the JSON requirements
     *
     * @param value The value to quote
     * @return The safe quoted value
     */
    public static String quoteJSON(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"')
                builder.append("\\\"");
            else if (c == '\t')
                builder.append("\\t");
            else if (c == '\r')
                builder.append("\\r");
            else if (c == '\n')
                builder.append("\\n");
            else
                builder.append(c);
        }
        return builder.toString();
    }
}
