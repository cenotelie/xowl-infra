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
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;

import java.util.Map;

/**
 * A constant expression in SPARQL, usually a native value (Integer, Long, Boolean, etc.)
 *
 * @author Laurent Wouters
 */
public class ExpressionConstant implements Expression {
    /**
     * The constant value
     */
    private final Object value;

    /**
     * Initializes this expression
     *
     * @param value The constant value
     */
    public ExpressionConstant(Object value) {
        this.value = value;
    }

    @Override
    public Object eval(EvalContext context, RDFPatternSolution bindings) {
        return value;
    }

    @Override
    public Object eval(EvalContext context, Solutions solutions) {
        return value;
    }

    @Override
    public boolean containsAggregate() {
        return false;
    }

    @Override
    public Expression clone(Map<String, Node> parameters) {
        return new ExpressionConstant(value);
    }
}
