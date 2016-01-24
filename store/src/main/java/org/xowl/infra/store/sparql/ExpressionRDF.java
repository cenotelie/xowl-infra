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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.Repository;
import org.xowl.infra.store.owl.DynamicNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.QuerySolution;
import org.xowl.infra.store.rdf.VariableNode;

import java.util.ArrayList;
import java.util.List;

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
     * Initializes this expression
     *
     * @param node The RDF node to represent
     */
    public ExpressionRDF(Node node) {
        this.node = node;
    }

    @Override
    public Object eval(Repository repository, QuerySolution bindings) throws EvalException {
        if (node == null)
            return null;
        Node result = node;
        if (result.getNodeType() == Node.TYPE_VARIABLE) {
            Node value = bindings.get((VariableNode) result);
            if (value == null)
                return new ExpressionErrorValue("Unbound variable " + ((VariableNode) result).getName());
            result = value;
        }
        if (result.getNodeType() == Node.TYPE_DYNAMIC && repository.getEvaluator() != null) {
            return Utils.evaluateNative(repository, bindings, ((DynamicNode) result).getDynamicExpression());
        }
        return result;
    }

    @Override
    public Object eval(Repository repository, Solutions solutions) throws EvalException {
        List<Object> result = new ArrayList<>(solutions.size());
        for (QuerySolution solution : solutions)
            result.add(eval(repository, solution));
        return result;
    }
}
