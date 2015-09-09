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

import org.xowl.store.Repository;
import org.xowl.store.owl.DynamicNode;
import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.QuerySolution;
import org.xowl.store.rdf.VariableNode;

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
        if (node.getNodeType() == Node.TYPE_VARIABLE)
            return bindings.get((VariableNode) node);
        if (node.getNodeType() == Node.TYPE_DYNAMIC)
            return repository.getEvaluator().eval(((DynamicNode) node).getDynamicExpression());
        return node;
    }
}
