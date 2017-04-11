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
 * Represents an expression in SPARQL
 *
 * @author Laurent Wouters
 */
public interface Expression {
    /**
     * Evaluates this expression
     *
     * @param context  The evaluation context
     * @param bindings The current bindings
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    Object eval(EvalContext context, RDFPatternSolution bindings) throws EvaluationException;

    /**
     * Evaluates this expression for multiple expressions
     *
     * @param context   The evaluation context
     * @param solutions The current set of solutions
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    Object eval(EvalContext context, Solutions solutions) throws EvaluationException;

    /**
     * Gets whether this expression contains an aggregate
     *
     * @return Whether this expression contains an aggregate
     */
    boolean containsAggregate();

    /**
     * Gets a copy of this expression
     *
     * @param parameters The parameters to be replaced during the clone
     * @return A copy of this expression
     */
    Expression clone(Map<String, Node> parameters);
}
