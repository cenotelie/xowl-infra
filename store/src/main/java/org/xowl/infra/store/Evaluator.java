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

package org.xowl.infra.store;

import org.xowl.infra.lang.actions.DynamicExpression;

/**
 * Represents an evaluator of xOWL dynamic expressions
 *
 * @author Laurent Wouters
 */
public interface Evaluator {
    /**
     * Gets the parent repository
     *
     * @return The parent repository
     */
    Repository getRepository();

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    Object eval(DynamicExpression expression);

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @param parameters The parameters for the evaluation
     * @return The evaluated value
     */
    Object eval(DynamicExpression expression, Object... parameters);

    /**
     * Gets whether a function identified by its IRI is defined
     *
     * @param function The IRI of a function
     * @return Whether the function is defined
     */
    boolean isDefined(String function);

    /**
     * Executes a function
     *
     * @param function   The IRI of the function to execute
     * @param parameters The parameters for the function
     * @return The value returned by the execution
     */
    Object execute(String function, Object... parameters);
}
