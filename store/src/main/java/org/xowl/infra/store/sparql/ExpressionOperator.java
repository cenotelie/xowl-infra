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

import org.xowl.infra.store.Datatypes;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.LiteralNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;
import org.xowl.infra.utils.collections.Couple;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public Object eval(Repository repository, RDFPatternSolution bindings) throws EvalException {
        Object v1 = operand1.eval(repository, bindings);
        Object v2 = operand2 == null ? null : operand2.eval(repository, bindings);
        return apply(v1, v2);
    }

    @Override
    public Object eval(Repository repository, Solutions solutions) throws EvalException {
        List<Object> result = new ArrayList<>(solutions.size());
        for (RDFPatternSolution solution : solutions)
            result.add(eval(repository, solution));
        return result;
    }

    /**
     * Applies the operator represented by this expression onto the specified values
     *
     * @param v1 The value of the first operand
     * @param v2 The value of the second operand
     * @return The result
     */
    private Object apply(Object v1, Object v2) throws EvalException {
        switch (operator) {
            case BoolAnd:
                return boolean_and(v1, v2);
            case BoolOr:
                return boolean_or(v1, v2);
            case BoolNot:
                return boolean_not(v1);
            case Equal:
                return equals(v1, v2);
            case NotEqual:
                return different(v1, v2);
            case Less:
                return lesser_than(v1, v2);
            case LessOrEqual:
                return lesser_or_equal(v1, v2);
            case Greater:
                return greater_than(v1, v2);
            case GreaterOrEqual:
                return greater_or_equal(v1, v2);
            case Plus:
                return plus(v1, v2);
            case Minus:
                return minus(v1, v2);
            case Multiply:
                return multiply(v1, v2);
            case Divide:
                return divide(v1, v2);
            case UnaryPlus:
                return plus(v1);
            case UnaryMinus:
                return minus(v1);
        }
        throw new EvalException("Unrecognized operator");
    }

    /**
     * Coerce an expression to a primitive value
     *
     * @param value An expression's value
     * @return The primitive equivalent
     * @throws EvalException When an error occurs during the evaluation
     */
    public static Object primitive(Object value) throws EvalException {
        if (value == null)
            return null;
        if (value instanceof Node) {
            Node node = (Node) value;
            if (node.getNodeType() == Node.TYPE_LITERAL) {
                return Datatypes.toNative((LiteralNode) node);
            } else {
                return new ExpressionErrorValue("RDF node other than literals cannot be coerced");
            }
        }
        return value;
    }

    /**
     * Coerce an expression to a RDF value
     *
     * @param value      An expression's value
     * @param repository The current repository
     * @return The RDF node equivalent
     */
    public static Node rdf(Object value, Repository repository) {
        if (value == null)
            return null;
        if (value instanceof ExpressionErrorValue)
            return null;
        if (value instanceof Node)
            return ((Node) value);
        Couple<String, String> literalData = Datatypes.toLiteral(repository);
        return repository.getStore().getLiteralNode(literalData.x, literalData.y, null);
    }

    /**
     * Coerce the value to a boolean
     *
     * @param value The value to coerce
     * @return The boolean
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean bool(Object value) throws EvalException {
        if (value == null)
            return false;
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof ExpressionErrorValue)
            return false;
        if (isNumInteger(value))
            return integer(value) != 0;
        if (isNumDecimal(value))
            return decimal(value) != 0;
        return false;
    }

    /**
     * Gets whether the value of a numeric integer type
     *
     * @param value The value
     * @return Whether the value of a numeric integer type
     */
    public static boolean isNumInteger(Object value) {
        return (value instanceof Byte || value instanceof Character || value instanceof Short || value instanceof Integer || value instanceof Long);
    }

    /**
     * Coerce the value to a numeric integer
     *
     * @param value The value
     * @return The coerced value
     * @throws EvalException When an error occurs during the evaluation
     */
    public static long integer(Object value) throws EvalException {
        if (value instanceof Byte) {
            return ((Byte) value);
        } else if (value instanceof Character) {
            return ((Character) value);
        } else if (value instanceof Short) {
            return ((Short) value);
        } else if (value instanceof Integer) {
            return ((Integer) value);
        } else if (value instanceof Long) {
            return ((Long) value);
        }
        throw new EvalException("Incorrect type " + value.getClass().getName());
    }

    /**
     * Gets whether the value of a numeric decimal type
     *
     * @param value The value
     * @return Whether the value of a numeric decimal type
     */
    public static boolean isNumDecimal(Object value) {
        return (value instanceof Float || value instanceof Double);
    }

    /**
     * Coerce the value to a numeric decimal
     *
     * @param value The value
     * @return The coerced value
     * @throws EvalException When an error occurs during the evaluation
     */
    public static double decimal(Object value) throws EvalException {
        if (value instanceof Float) {
            return ((Float) value);
        } else if (value instanceof Double) {
            return ((Double) value);
        }
        throw new EvalException("Incorrect type " + value.getClass().getName());
    }

    /**
     * Boolean AND operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean boolean_and(Object left, Object right) throws EvalException {
        return bool(primitive(left)) && bool(primitive(right));
    }

    /**
     * Boolean OR operand
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean boolean_or(Object left, Object right) throws EvalException {
        return bool(primitive(left)) || bool(primitive(right));
    }

    /**
     * Boolean NOT operator
     *
     * @param operand The operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean boolean_not(Object operand) throws EvalException {
        return !bool(primitive(operand));
    }

    /**
     * SPARQL equality operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean equals(Object left, Object right) throws EvalException {
        if (left == null && right == null)
            return true;
        if (left == null || right == null)
            return false;

        if (left instanceof Node) {
            Node nodeLeft = (Node) left;
            if (right instanceof Node) {
                Node nodeRight = (Node) right;
                if (nodeLeft.getNodeType() == Node.TYPE_LITERAL) {
                    return nodeRight.getNodeType() == Node.TYPE_LITERAL && Objects.equals(primitive(nodeLeft), primitive(nodeRight));
                } else if (nodeRight.getNodeType() == Node.TYPE_LITERAL)
                    return false;
                return RDFUtils.same(nodeLeft, nodeRight);
            } else
                return nodeLeft.getNodeType() == Node.TYPE_LITERAL && Objects.equals(primitive(nodeLeft), right);
        } else if (right instanceof Node) {
            Node nodeRight = (Node) right;
            return nodeRight.getNodeType() == Node.TYPE_LITERAL && Objects.equals(left, primitive(nodeRight));
        }
        return Objects.equals(left, right);
    }

    /**
     * SPARQL difference operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean different(Object left, Object right) throws EvalException {
        return !equals(left, right);
    }

    /**
     * SPARQL &lt; operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean lesser_than(Object left, Object right) throws EvalException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof Boolean || right instanceof Boolean)
            throw new EvalException("Type error");

        if (left instanceof String) {
            return right instanceof String && left.toString().compareTo(right.toString()) < 0;
        } else if (right instanceof String) {
            return false;
        }

        if (left instanceof Date) {
            return right instanceof Date && ((Date) left).compareTo((Date) right) < 0;
        } else if (right instanceof Date) {
            return false;
        }

        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l1 < l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l1 < l2;
            } else {
                return false;
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l1 < l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l1 < l2;
            } else {
                return false;
            }
        }

        throw new EvalException("Type error");
    }

    /**
     * SPARQL &lt;= operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean lesser_or_equal(Object left, Object right) throws EvalException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof Boolean || right instanceof Boolean)
            throw new EvalException("Type error");

        if (left instanceof String) {
            return right instanceof String && left.toString().compareTo(right.toString()) <= 0;
        } else if (right instanceof String) {
            return false;
        }

        if (left instanceof Date) {
            return right instanceof Date && ((Date) left).compareTo((Date) right) <= 0;
        } else if (right instanceof Date) {
            return false;
        }

        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l1 <= l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l1 <= l2;
            } else {
                return false;
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l1 <= l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l1 <= l2;
            } else {
                return false;
            }
        }

        throw new EvalException("Type error");
    }

    /**
     * SPARQL &gt; operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean greater_than(Object left, Object right) throws EvalException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof Boolean || right instanceof Boolean)
            throw new EvalException("Type error");

        if (left instanceof String) {
            return right instanceof String && left.toString().compareTo(right.toString()) > 0;
        } else if (right instanceof String) {
            return false;
        }

        if (left instanceof Date) {
            return right instanceof Date && ((Date) left).compareTo((Date) right) > 0;
        } else if (right instanceof Date) {
            return false;
        }

        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l1 > l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l1 > l2;
            } else {
                return false;
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l1 > l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l1 > l2;
            } else {
                return false;
            }
        }

        throw new EvalException("Type error");
    }

    /**
     * SPARQL &gt;= operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static boolean greater_or_equal(Object left, Object right) throws EvalException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof Boolean || right instanceof Boolean)
            throw new EvalException("Type error");

        if (left instanceof String) {
            return right instanceof String && left.toString().compareTo(right.toString()) >= 0;
        } else if (right instanceof String) {
            return false;
        }

        if (left instanceof Date) {
            return right instanceof Date && ((Date) left).compareTo((Date) right) >= 0;
        } else if (right instanceof Date) {
            return false;
        }

        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l1 >= l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l1 >= l2;
            } else {
                return false;
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l1 >= l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l1 >= l2;
            } else {
                return false;
            }
        }

        throw new EvalException("Type error");
    }

    /**
     * SPARQL unary + operator
     *
     * @param value The operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static Object plus(Object value) throws EvalException {
        value = primitive(value);
        if (isNumInteger(value) || isNumDecimal(value))
            return value;
        throw new EvalException("Type error");
    }

    /**
     * SPARQL binary + operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static Object plus(Object left, Object right) throws EvalException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof String) {
            return left.toString() + (right == null ? "" : right.toString());
        } else if (right instanceof String) {
            return (left == null ? "" : left.toString()) + right.toString();
        }

        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l1 + l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l1 + l2;
            } else {
                throw new EvalException("Type error");
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l1 + l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l1 + l2;
            } else {
                throw new EvalException("Type error");
            }
        }

        throw new EvalException("Type error");
    }

    /**
     * SPARQL unary - operator
     *
     * @param value The operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static Object minus(Object value) throws EvalException {
        value = primitive(value);
        if (isNumInteger(value))
            return -integer(value);
        if (isNumDecimal(value))
            return -decimal(value);
        throw new EvalException("Type error");
    }

    /**
     * SPARQL binary - operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static Object minus(Object left, Object right) throws EvalException {
        left = primitive(left);
        right = primitive(right);
        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l1 - l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l1 - l2;
            } else {
                throw new EvalException("Type error");
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l1 - l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l1 - l2;
            } else {
                throw new EvalException("Type error");
            }
        }
        throw new EvalException("Type error");
    }

    /**
     * SPARQL * operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static Object multiply(Object left, Object right) throws EvalException {
        left = primitive(left);
        right = primitive(right);
        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l1 * l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l1 * l2;
            } else {
                throw new EvalException("Type error");
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l1 * l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l1 * l2;
            } else {
                throw new EvalException("Type error");
            }
        }
        throw new EvalException("Type error");
    }

    /**
     * SPARQL / operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public static Object divide(Object left, Object right) throws EvalException {
        left = primitive(left);
        right = primitive(right);
        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l2 == 0 ? new EvalException("Divide by 0") : l1 / l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l2 == 0 ? new EvalException("Divide by 0") : l1 / l2;
            } else {
                throw new EvalException("Type error");
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l2 == 0 ? new EvalException("Divide by 0") : l1 / l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l2 == 0 ? new EvalException("Divide by 0") : l1 / l2;
            } else {
                throw new EvalException("Type error");
            }
        }
        throw new EvalException("Type error");
    }
}
