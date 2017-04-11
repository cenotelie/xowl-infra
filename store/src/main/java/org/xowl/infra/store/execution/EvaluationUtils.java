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

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.Node;

import java.util.Date;
import java.util.Objects;

/**
 * Utility API for the evaluation of expressions
 *
 * @author Laurent Wouters
 */
public class EvaluationUtils {
    /**
     * Coerce an expression to a primitive value
     *
     * @param value An expression's value
     * @return The primitive equivalent
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static Object primitive(Object value) throws EvaluationException {
        if (value == null)
            return null;
        if (value instanceof Node)
            return RDFUtils.getNative((Node) value);
        return value;
    }

    /**
     * Coerce the value to a boolean
     *
     * @param value The value to coerce
     * @return The boolean
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean bool(Object value) throws EvaluationException {
        if (value == null)
            return false;
        if (value instanceof Boolean)
            return (Boolean) value;
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
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static long integer(Object value) throws EvaluationException {
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
        throw new EvaluationException("Incorrect type " + value.getClass().getName());
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
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static double decimal(Object value) throws EvaluationException {
        if (value instanceof Float) {
            return ((Float) value);
        } else if (value instanceof Double) {
            return ((Double) value);
        }
        throw new EvaluationException("Incorrect type " + value.getClass().getName());
    }

    /**
     * Boolean AND operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean boolean_and(Object left, Object right) throws EvaluationException {
        return bool(primitive(left)) && bool(primitive(right));
    }

    /**
     * Boolean OR operand
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean boolean_or(Object left, Object right) throws EvaluationException {
        return bool(primitive(left)) || bool(primitive(right));
    }

    /**
     * Boolean NOT operator
     *
     * @param operand The operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean boolean_not(Object operand) throws EvaluationException {
        return !bool(primitive(operand));
    }

    /**
     * SPARQL equality operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean equals(Object left, Object right) throws EvaluationException {
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
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean different(Object left, Object right) throws EvaluationException {
        return !equals(left, right);
    }

    /**
     * SPARQL &lt; operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean lesser_than(Object left, Object right) throws EvaluationException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof Boolean || right instanceof Boolean)
            throw new EvaluationException("Type error");

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

        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL &lt;= operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean lesser_or_equal(Object left, Object right) throws EvaluationException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof Boolean || right instanceof Boolean)
            throw new EvaluationException("Type error");

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

        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL &gt; operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean greater_than(Object left, Object right) throws EvaluationException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof Boolean || right instanceof Boolean)
            throw new EvaluationException("Type error");

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

        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL &gt;= operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static boolean greater_or_equal(Object left, Object right) throws EvaluationException {
        left = primitive(left);
        right = primitive(right);

        if (left instanceof Boolean || right instanceof Boolean)
            throw new EvaluationException("Type error");

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

        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL unary + operator
     *
     * @param value The operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static Object plus(Object value) throws EvaluationException {
        value = primitive(value);
        if (isNumInteger(value) || isNumDecimal(value))
            return value;
        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL binary + operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static Object plus(Object left, Object right) throws EvaluationException {
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
                throw new EvaluationException("Type error");
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
                throw new EvaluationException("Type error");
            }
        }

        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL unary - operator
     *
     * @param value The operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static Object minus(Object value) throws EvaluationException {
        value = primitive(value);
        if (isNumInteger(value))
            return -integer(value);
        if (isNumDecimal(value))
            return -decimal(value);
        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL binary - operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static Object minus(Object left, Object right) throws EvaluationException {
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
                throw new EvaluationException("Type error");
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
                throw new EvaluationException("Type error");
            }
        }
        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL * operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static Object multiply(Object left, Object right) throws EvaluationException {
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
                throw new EvaluationException("Type error");
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
                throw new EvaluationException("Type error");
            }
        }
        throw new EvaluationException("Type error");
    }

    /**
     * SPARQL / operator
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The result
     * @throws EvaluationException When an error occurs during the evaluation
     */
    public static Object divide(Object left, Object right) throws EvaluationException {
        left = primitive(left);
        right = primitive(right);
        if (isNumInteger(left)) {
            if (isNumInteger(right)) {
                long l1 = integer(left);
                long l2 = integer(right);
                return l2 == 0 ? new EvaluationException("Divide by 0") : l1 / l2;
            } else if (isNumDecimal(right)) {
                long l1 = integer(left);
                double l2 = decimal(right);
                return l2 == 0 ? new EvaluationException("Divide by 0") : l1 / l2;
            } else {
                throw new EvaluationException("Type error");
            }
        }
        if (isNumDecimal(left)) {
            if (isNumInteger(right)) {
                double l1 = decimal(left);
                long l2 = integer(right);
                return l2 == 0 ? new EvaluationException("Divide by 0") : l1 / l2;
            } else if (isNumDecimal(right)) {
                double l1 = decimal(left);
                double l2 = decimal(right);
                return l2 == 0 ? new EvaluationException("Divide by 0") : l1 / l2;
            } else {
                throw new EvaluationException("Type error");
            }
        }
        throw new EvaluationException("Type error");
    }
}
