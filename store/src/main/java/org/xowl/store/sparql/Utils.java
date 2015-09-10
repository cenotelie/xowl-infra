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
    public static Object evluateNative(Repository repository, QuerySolution solution, DynamicExpression expression) {
        Map<String, Object> bindings = new HashMap<>();
        for (Couple<VariableNode, Node> binding : solution)
            bindings.put(binding.x.getName(), org.xowl.store.rdf.Utils.getNative(binding.y));
        repository.getEvaluator().push(bindings);
        Object result = repository.getEvaluator().eval(expression);
        repository.getEvaluator().pop();
        return result;
    }

    /**
     * Evaluates a SPARQL expression as a native value
     *
     * @param repository The current repository
     * @param solution   The current bindings
     * @param expression The expression to evaluate
     * @return The evaluated node
     */
    public static Object evaluateNative(Repository repository, QuerySolution solution, Expression expression) throws EvalException {
        Object value = expression.eval(repository, solution);
        if (value instanceof DynamicNode)
            value = evluateNative(repository, solution, ((DynamicNode) value).getDynamicExpression());
        if (value instanceof LiteralNode)
            value = Datatypes.toNative((LiteralNode) value);
        return value;
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
            value = evluateNative(repository, solution, ((DynamicNode) value).getDynamicExpression());
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
            value = evluateNative(repository, solution, ((DynamicNode) value).getDynamicExpression());
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
            Object value = evluateNative(repository, solution, ((DynamicNode) result).getDynamicExpression());
            if (value instanceof Node) {
                result = (GraphNode) value;
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
}
