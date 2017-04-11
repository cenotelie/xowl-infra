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
import org.xowl.infra.store.execution.EvaluationUtils;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public Object eval(EvalContext context, RDFPatternSolution bindings) throws EvaluationException {
        Object v1 = operand1.eval(context, bindings);
        Object v2 = operand2 == null ? null : operand2.eval(context, bindings);
        return apply(v1, v2);
    }

    @Override
    public Object eval(EvalContext context, Solutions solutions) throws EvaluationException {
        List<Object> result = new ArrayList<>(solutions.size());
        for (RDFPatternSolution solution : solutions)
            result.add(eval(context, solution));
        return result;
    }

    @Override
    public boolean containsAggregate() {
        return ((operand1 != null && operand1.containsAggregate()) || (operand2 != null && operand2.containsAggregate()));
    }

    @Override
    public Expression clone(Map<String, Node> parameters) {
        Expression left = operand1 == null ? null : operand1.clone(parameters);
        Expression right = operand2 == null ? null : operand2.clone(parameters);
        return new ExpressionOperator(operator, left, right);
    }

    /**
     * Applies the operator represented by this expression onto the specified values
     *
     * @param v1 The value of the first operand
     * @param v2 The value of the second operand
     * @return The result
     */
    private Object apply(Object v1, Object v2) throws EvaluationException {
        switch (operator) {
            case BoolAnd:
                return EvaluationUtils.boolean_and(v1, v2);
            case BoolOr:
                return EvaluationUtils.boolean_or(v1, v2);
            case BoolNot:
                return EvaluationUtils.boolean_not(v1);
            case Equal:
                return EvaluationUtils.equals(v1, v2);
            case NotEqual:
                return EvaluationUtils.different(v1, v2);
            case Less:
                return EvaluationUtils.lesser_than(v1, v2);
            case LessOrEqual:
                return EvaluationUtils.lesser_or_equal(v1, v2);
            case Greater:
                return EvaluationUtils.greater_than(v1, v2);
            case GreaterOrEqual:
                return EvaluationUtils.greater_or_equal(v1, v2);
            case Plus:
                return EvaluationUtils.plus(v1, v2);
            case Minus:
                return EvaluationUtils.minus(v1, v2);
            case Multiply:
                return EvaluationUtils.multiply(v1, v2);
            case Divide:
                return EvaluationUtils.divide(v1, v2);
            case UnaryPlus:
                return EvaluationUtils.plus(v1);
            case UnaryMinus:
                return EvaluationUtils.minus(v1);
        }
        throw new EvaluationException("Unrecognized operator");
    }

}
