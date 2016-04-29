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
import org.xowl.infra.lang.actions.FunctionExpression;
import org.xowl.infra.lang.owl2.*;
import org.xowl.infra.lang.runtime.Class;
import org.xowl.infra.lang.runtime.*;
import org.xowl.infra.lang.runtime.Literal;

import java.util.Map;

/**
 * Represents an evaluator of OWL dynamix expressions
 *
 * @author Laurent Wouters
 */
public interface Evaluator {
    /**
     * Pushes a new context onto the stack of this evaluator
     *
     * @param context The context to push
     */
    void push(Map<String, Object> context);

    /**
     * Pops the head context from the stack of this evaluator
     */
    void pop();

    /**
     * Evaluates the specified expression
     *
     * @param expression An expression
     * @return The evaluated value
     */
    Object eval(DynamicExpression expression);

    /**
     * Evaluates the specified class expression
     *
     * @param expression A class expression
     * @return The evaluated runtime class
     */
    Class evalClass(ClassExpression expression);

    /**
     * Evaluates the specified object property expression
     *
     * @param expression A object property expression
     * @return The evaluated runtime object property
     */
    ObjectProperty evalObjectProperty(ObjectPropertyExpression expression);

    /**
     * Evaluates the specified data property expression
     *
     * @param expression A data property expression
     * @return The evaluated runtime data property
     */
    DataProperty evalDataProperty(DataPropertyExpression expression);

    /**
     * Evaluates the specified datarange
     *
     * @param expression A datarange
     * @return The evaluated runtime datatype
     */
    Datatype evalDatatype(Datarange expression);

    /**
     * Evaluates the specified individual expression
     *
     * @param expression An individual expression
     * @return The evaluated runtime individual
     */
    Individual evalIndividual(IndividualExpression expression);

    /**
     * Evaluates the specified literal expression
     *
     * @param expression A literal expression
     * @return The evaluated runtime literal
     */
    Literal evalLiteral(LiteralExpression expression);

    /**
     * Evaluates the specified function expression
     *
     * @param expression A function expression
     * @return The evaluated runtime function
     */
    Function evalFunction(FunctionExpression expression);
}
