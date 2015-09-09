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

import org.xowl.lang.owl2.IRI;
import org.xowl.store.Datatypes;
import org.xowl.store.Repository;
import org.xowl.store.owl.DynamicNode;
import org.xowl.store.rdf.*;
import org.xowl.utils.collections.Couple;

import java.util.Collection;

/**
 * Utilities for the evaluation of SPARQL queries
 *
 * @author Laurent Wouters
 */
class Utils {
    /**
     * Evaluates a SPARQL expression as a boolean native value
     *
     * @param repository The current repository
     * @param solution   The current bindings
     * @param expression The expression to evaluate
     * @return The evaluated node
     */
    public static boolean evaluateBoolean(Repository repository, QuerySolution solution, Expression expression) throws EvalException {
        Object value = expression.eval(repository, solution);
        if (value instanceof DynamicNode)
            value = repository.getEvaluator().eval(((DynamicNode) value).getDynamicExpression());
        if (value instanceof LiteralNode)
            value = Datatypes.toNative((LiteralNode) value);
        return (value instanceof Boolean && (Boolean) value);
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
            value = repository.getEvaluator().eval(((DynamicNode) value).getDynamicExpression());
        if (value instanceof LiteralNode)
            value = Datatypes.toNative((LiteralNode) value);
        return value;
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
            value = repository.getEvaluator().eval(((DynamicNode) value).getDynamicExpression());
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
     * Evaluates a RDF node
     *
     * @param repository The current repository
     * @param solution   The current bindings
     * @param node       The node to evaluate
     * @return The evaluated node
     */
    public static Node evaluate(Repository repository, QuerySolution solution, Node node) throws EvalException {
        if (node == null)
            throw new EvalException("The node cannot be null");
        Node result = node;
        if (result.getNodeType() == Node.TYPE_VARIABLE) {
            Node value = solution.get((VariableNode) result);
            if (value == null)
                throw new EvalException("Unbound variable " + ((VariableNode) result).getName() + " in the template");
            result = value;
        }
        if (result.getNodeType() == Node.TYPE_DYNAMIC && repository.getEvaluator() != null) {
            Object value = repository.getEvaluator().eval(((DynamicNode) result).getDynamicExpression());
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
     * Applies the specified mapping to a template.
     * This replaces the variable nodes in the template by their value in the mapping
     *
     * @param repository The current repository
     * @param template   The template
     * @param solution   The query solution mapping the variables to their value
     * @param buffer     The buffer for the realized quads
     */
    public static void evaluate(Repository repository, QuerySolution solution, Collection<Quad> template, Collection<Quad> buffer) throws EvalException {
        for (Quad quad : template) {
            GraphNode graph = (GraphNode) evaluate(repository, solution, quad.getGraph());
            SubjectNode subject = (SubjectNode) evaluate(repository, solution, quad.getSubject());
            Property property = (Property) evaluate(repository, solution, quad.getProperty());
            Node object = evaluate(repository, solution, quad.getObject());
            buffer.add(new Quad(graph, subject, property, object));
        }
    }
}
