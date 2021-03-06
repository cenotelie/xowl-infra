/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.store.execution;

import java.util.Map;

/**
 * Represents an evaluator of xOWL dynamic expressions
 *
 * @author Laurent Wouters
 */
public interface Evaluator {
    /**
     * Evaluates the specified expression
     *
     * @param bindings   The new contextual bindings
     * @param expression An evaluable expression
     * @return The evaluated value
     */
    Object eval(Map<String, Object> bindings, EvaluableExpression expression);

    /**
     * Tries to retrieve a xOWL function
     *
     * @param functionIRI The identifier (IRI) of a function
     * @return The function's definition, if it is defined, false otherwise
     */
    ExecutableFunction getFunction(String functionIRI);

    /**
     * Executes a xOWL function
     *
     * @param functionIRI The identifier (IRI) of a function
     * @param parameters  The parameters
     * @return The function's result
     */
    Object execute(String functionIRI, Object... parameters);
}
