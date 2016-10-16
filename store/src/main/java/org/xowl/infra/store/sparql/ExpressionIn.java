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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A SPARQL expression that checks whether a value is within a set of specified ones
 *
 * @author Laurent Wouters
 */
public class ExpressionIn implements Expression {
    /**
     * The expression that check
     */
    private final Expression primary;
    /**
     * The set of expressions to check against
     */
    private final List<Expression> range;

    /**
     * Initializes this expression
     *
     * @param primary The expression that check
     * @param range   The set of expressions to check against
     */
    public ExpressionIn(Expression primary, List<Expression> range) {
        this.primary = primary;
        this.range = new ArrayList<>(range);
    }

    @Override
    public Object eval(EvalContext context, RDFPatternSolution bindings) throws EvalException {
        Object value = primary.eval(context, bindings);
        for (Expression test : range) {
            Object testValue = test.eval(context, bindings);
            if (ExpressionOperator.equals(value, testValue))
                return true;
        }
        return false;
    }

    @Override
    public Object eval(EvalContext context, Solutions solutions) throws EvalException {
        List<Object> result = new ArrayList<>(solutions.size());
        for (RDFPatternSolution solution : solutions)
            result.add(eval(context, solution));
        return result;
    }

    @Override
    public boolean containsAggregate() {
        return primary.containsAggregate();
    }

    @Override
    public Expression clone(Map<String, Node> parameters) {
        List<Expression> range = new ArrayList<>(this.range.size());
        for (Expression argument : this.range) {
            range.add(argument.clone(parameters));
        }
        return new ExpressionIn(primary.clone(parameters), range);
    }
}
