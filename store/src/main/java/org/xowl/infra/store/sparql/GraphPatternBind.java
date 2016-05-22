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

import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.utils.collections.Couple;

import java.util.ArrayList;

/**
 * A graph pattern represented by the explicit binding of a variable
 *
 * @author Laurent Wouters
 */
public class GraphPatternBind implements GraphPattern {
    /**
     * The inner pattern
     */
    private final GraphPattern origin;
    /**
     * The variable to bind
     */
    private final VariableNode variable;
    /**
     * The expression for the value to bind to
     */
    private final Expression expression;

    /**
     * Initializes this graph pattern
     *
     * @param origin     The inner pattern
     * @param variable   The variable to bind
     * @param expression The expression for the value to bind to
     */
    public GraphPatternBind(GraphPattern origin, VariableNode variable, Expression expression) {
        this.origin = origin;
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public Solutions eval(EvalContext context) throws EvalException {
        if (origin != null) {
            Solutions originalSolutions = origin.eval(context);
            return Utils.extend(originalSolutions, variable, expression, context);
        } else {
            Node value = ExpressionOperator.rdf(expression.eval(context, (RDFPatternSolution) null), context);
            ArrayList<Couple<VariableNode, Node>> bindings = new ArrayList<>();
            bindings.add(new Couple<>(variable, value));
            RDFPatternSolution solution = new RDFPatternSolution(bindings);
            SolutionsMultiset result = new SolutionsMultiset(1);
            result.add(solution);
            return result;
        }
    }

    @Override
    public void inspect(Inspector inspector) {
        inspector.onGraphPattern(this);
        if (origin != null)
            origin.inspect(inspector);
    }
}
