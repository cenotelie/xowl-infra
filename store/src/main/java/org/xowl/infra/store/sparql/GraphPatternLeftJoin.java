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

import java.util.Map;

/**
 * A graph pattern as the left join of two other patterns
 *
 * @author Laurent Wouters
 */
public class GraphPatternLeftJoin implements GraphPattern {
    /**
     * The pattern on the left
     */
    private final GraphPattern left;
    /**
     * The pattern on the right
     */
    private final GraphPattern right;
    /**
     * The expression for this join
     */
    private final Expression expression;

    /**
     * Initializes this graph pattern
     *
     * @param left       The pattern on the left
     * @param right      The pattern on the right
     * @param expression The expression for this join
     */
    public GraphPatternLeftJoin(GraphPattern left, GraphPattern right, Expression expression) {
        this.left = left;
        this.right = right;
        this.expression = expression;
    }

    @Override
    public Solutions eval(EvalContext context) throws EvalException {
        Solutions leftSolutions = left.eval(context);
        Solutions rightSolutions = right.eval(context);
        return Utils.leftJoin(leftSolutions, rightSolutions, expression, context);
    }

    @Override
    public void inspect(Inspector inspector) {
        inspector.onGraphPattern(this);
        left.inspect(inspector);
        right.inspect(inspector);
    }

    @Override
    public GraphPattern clone(Map<String, Node> parameters) {
        return new GraphPatternLeftJoin(left.clone(parameters), right.clone(parameters), expression.clone(parameters));
    }
}
