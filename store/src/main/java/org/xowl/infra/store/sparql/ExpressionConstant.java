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
import org.xowl.infra.store.rdf.QuerySolution;

import java.util.ArrayList;
import java.util.List;

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
    public Object eval(Repository repository, QuerySolution bindings) throws EvalException {
        return value;
    }

    @Override
    public Object eval(Repository repository, Solutions solutions) throws EvalException {
        List<Object> result = new ArrayList<>(solutions.size());
        for (QuerySolution solution : solutions)
            result.add(eval(repository, solution));
        return result;
    }
}
