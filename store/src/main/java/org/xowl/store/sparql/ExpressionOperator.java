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

package org.xowl.store.sparql;

import org.xowl.store.Repository;
import org.xowl.store.rdf.QuerySolution;

/**
 * Represents an operator in an expression
 *
 * @author Laurent Wouters
 */
public class ExpressionOperator implements Expression {
    public enum Op {
        BoolAnd, BoolOr, BoolNot,
        Equal, NotEqual, Less, LessOrEqual, Greater, GreaterOrEqual,
        Plus, Minus, Multiply, Divide,
        UnaryPlus, UnaryMinus
    }

    /**
     * The operator
     */
    private final Op operator;
    /**
     * The first operand
     */
    private final Expression operand1;
    /**
     * The second operand
     */
    private final Expression operand2;

    /**
     * Initializes an unary operation
     *
     * @param operator The operator
     * @param operand  The operand
     */
    public ExpressionOperator(Op operator, Expression operand) {
        this.operator = operator;
        this.operand1 = operand;
        this.operand2 = null;
    }

    /**
     * Initializes a binary operation
     *
     * @param operator The operator
     * @param operand1 The first operand
     * @param operand2 The second operand
     */
    public ExpressionOperator(Op operator, Expression operand1, Expression operand2) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public Object eval(Repository repository, QuerySolution bindings) throws EvalException {
        throw new EvalException("Not yet implemented");
    }


    /**
     * Determines whether the two operands are equals modulo the SPARQL semantics
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     */
    public static boolean op_equals(Object left, Object right) {
        return left != null && right != null && left.equals(right);
    }
}
