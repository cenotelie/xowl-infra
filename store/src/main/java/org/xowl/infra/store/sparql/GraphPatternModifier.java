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

import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.utils.collections.Couple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The modifiers of a graph pattern
 *
 * @author Laurent Wouters
 */
public class GraphPatternModifier {
    /**
     * The grouping clauses
     */
    private final List<Couple<VariableNode, Expression>> groups;
    /**
     * The HAVING constraints
     */
    private final List<Expression> having;
    /**
     * The ordering clauses
     */
    private final List<Couple<Expression, Boolean>> order;
    /**
     * The offset into the results
     */
    private int offset;
    /**
     * The limit to the results
     */
    private int limit;

    /**
     * Initializes an empty modifier
     */
    public GraphPatternModifier() {
        this.groups = new ArrayList<>();
        this.having = new ArrayList<>();
        this.order = new ArrayList<>();
        this.offset = 0;
        this.limit = Integer.MAX_VALUE;
    }

    /**
     * Adds a grouping clause based on an expression
     *
     * @param expression The expression used to derive the grouping key
     */
    public void addGroup(Expression expression) {
        groups.add(new Couple<VariableNode, Expression>(null, expression));
    }

    /**
     * Adds a group clause based on an expression
     *
     * @param expression The expression used to derive the grouping key
     * @param variable   The new derived variable bound to the grouping key
     */
    public void addGroup(Expression expression, VariableNode variable) {
        groups.add(new Couple<>(variable, expression));
    }

    /**
     * Adds a HAVING constraint represented by an expression
     *
     * @param expression The HAVING constraint
     */
    public void addConstraint(Expression expression) {
        having.add(expression);
    }

    /**
     * Adds an ordering constraint represented by an expression
     *
     * @param expression The expression used to derive the ordering key
     */
    public void addOrdering(Expression expression) {
        order.add(new Couple<>(expression, false));
    }

    /**
     * Adds an ordering constraint represented by an expression
     *
     * @param expression   The expression used to derive the ordering key
     * @param isDescending Whether the order is descending
     */
    public void addOrdering(Expression expression, boolean isDescending) {
        order.add(new Couple<>(expression, isDescending));
    }

    /**
     * Sets the OFFSET constraint
     *
     * @param offset The offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Sets the LIMIT constraint
     *
     * @param limit The limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Applies this modifier to the specified solution set
     *
     * @param solutions The solution set
     * @param context   The evaluation context
     * @return The transformed solution set
     * @throws EvalException When an error occurs during the evaluation
     */
    public Solutions apply(Solutions solutions, EvalContext context) throws EvalException {
        Solutions result = solutions;
        if (!having.isEmpty()) {
            Expression exp = having.get(0);
            for (int i = 1; i != having.size(); i++)
                exp = new ExpressionOperator(ExpressionOperator.Op.BoolAnd, exp, having.get(i));
            result = Utils.filter(result, exp, context);
        }
        if (!order.isEmpty())
            result = Utils.orderBy(result, order, context);
        if (offset != 0 || limit != Integer.MAX_VALUE)
            result = Utils.slice(result, offset, limit);
        if (!groups.isEmpty())
            result = Utils.group(result, groups, context);
        return result;
    }

    /**
     * Clones this modifier
     *
     * @param parameters The parameter for replacements
     * @return The clone
     */
    public GraphPatternModifier clone(Map<String, Node> parameters) {
        GraphPatternModifier result = new GraphPatternModifier();
        for (Couple<VariableNode, Expression> group : this.groups) {
            result.groups.add(new Couple<>(
                    (VariableNode) Utils.clone(group.x, parameters),
                    group.y.clone(parameters)
            ));
        }
        for (Expression expression : this.having) {
            result.having.add(expression.clone(parameters));
        }
        for (Couple<Expression, Boolean> couple : this.order) {
            result.order.add(new Couple<>(
                    couple.x.clone(parameters),
                    couple.y
            ));
        }
        result.offset = this.offset;
        result.limit = this.limit;
        return result;
    }
}
