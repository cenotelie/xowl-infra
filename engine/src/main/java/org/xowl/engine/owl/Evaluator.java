/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.engine.owl;

import org.xowl.lang.actions.QueryVariable;
import org.xowl.lang.owl2.*;
import org.xowl.lang.runtime.Class;
import org.xowl.lang.runtime.*;
import org.xowl.lang.runtime.Literal;

/**
 * Represents an evaluator of OWL dynamix expressions
 *
 * @author Laurent Wouters
 */
public interface Evaluator {
    /**
     * Determines whether the specified expression can be evaluated by this evaluator
     *
     * @param expression An expression
     * @return <code>true</code> if the specified expression can be evaluated
     */
    boolean can(Expression expression);

    /**
     * Determines whether the specified query variable can be evaluated by this evaluator
     *
     * @param variable A query variable
     * @return <code>true</code> if the specified query variable can be evaluated
     */
    boolean can(QueryVariable variable);

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
    Datatype evalDataProperty(Datarange expression);

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
     * @param expression An literal expression
     * @return The evaluated runtime literal
     */
    Literal evalLiteral(LiteralExpression expression);
}
