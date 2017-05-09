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

package org.xowl.infra.denotation.rules;

/**
 * An operator expression
 *
 * @author Laurent Wouters
 */
public class ExpressionOperator implements Expression {
    /**
     * The possible operators
     */
    public enum Operator {
        Plus, Minus, Multiply, Divide,
        BooleanAnd, BooleanOr, BooleanNot,
        Equal, Different, Greater, GreaterEqual, Lesser, LesserEqual
    }

    /**
     * The operator
     */
    private final Operator operator;
    /**
     * The first operand
     */
    private final Expression operand1;
    /**
     * The second operand
     */
    private final Expression operand2;

    /**
     * Initializes this expression
     *
     * @param operator The operator
     * @param operand1 The first operand
     * @param operand2 The second operand
     */
    public ExpressionOperator(Operator operator, Expression operand1, Expression operand2) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
}
