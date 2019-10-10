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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.execution.EvaluationException;
import org.xowl.infra.store.rdf.DynamicNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;
import org.xowl.infra.store.rdf.VariableNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the use of an RDF value in an expression
 *
 * @author Laurent Wouters
 */
public class ExpressionRDF implements Expression {
    /**
     * The RDF node
     */
    private final Node node;

    /**
     * Gets the represented node
     *
     * @return The represented node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Initializes this expression
     *
     * @param node The RDF node to represent
     */
    public ExpressionRDF(Node node) {
        this.node = node;
    }

    @Override
    public Object eval(EvalContext context, RDFPatternSolution bindings) {
        Node result = node;
        if (result != null && result.getNodeType() == Node.TYPE_VARIABLE) {
            result = bindings.get((VariableNode) result);
        }
        if (result != null && result.getNodeType() == Node.TYPE_DYNAMIC && context.getEvaluator() != null) {
            return Utils.evaluateNative(context, bindings, ((DynamicNode) result).getEvaluable());
        }
        return result;
    }

    @Override
    public Object eval(EvalContext context, Solutions solutions) throws EvaluationException {
        if (node == null)
            return null;
        if (node.getNodeType() == Node.TYPE_VARIABLE || node.getNodeType() == Node.TYPE_DYNAMIC) {
            List<Object> result = new ArrayList<>(solutions.size());
            for (RDFPatternSolution solution : solutions)
                result.add(eval(context, solution));
            return result;
        }
        return node;
    }

    @Override
    public boolean containsAggregate() {
        return false;
    }

    @Override
    public Expression clone(Map<String, Node> parameters) {
        return new ExpressionRDF(Utils.clone(node, parameters));
    }
}
