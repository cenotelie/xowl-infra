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

import org.xowl.infra.lang.actions.DynamicExpression;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.utils.collections.Couple;

import java.util.*;

/**
 * Utilities for the evaluation of SPARQL queries
 *
 * @author Laurent Wouters
 */
class Utils {
    /**
     * Clones an RDF node with a possible replacement
     *
     * @param original   The original RDF node
     * @param parameters The parameters for replacement
     * @return The clone
     */
    public static Node clone(Node original, Map<String, Node> parameters) {
        if (original.getNodeType() == Node.TYPE_VARIABLE && parameters != null) {
            Node result = parameters.get(((VariableNode) original).getName());
            if (result != null)
                return result;
        }
        return original;
    }

    /**
     * Clones an RDF quad with possible replacements
     *
     * @param original   The original RDF quad
     * @param parameters The parameters for replacement
     * @return The clone
     */
    public static Quad clone(Quad original, Map<String, Node> parameters) {
        return new Quad(
                (GraphNode) clone(original.getGraph(), parameters),
                (SubjectNode) clone(original.getSubject(), parameters),
                (Property) clone(original.getProperty(), parameters),
                clone(original.getObject(), parameters)
        );
    }

    /**
     * Clones an RDF pattern with possible replacements
     *
     * @param original   The original RDF pattern
     * @param parameters The parameters for replacement
     * @return The clone
     */
    public static RDFPattern clone(RDFPattern original, Map<String, Node> parameters) {
        RDFPattern result = new RDFPattern();
        for (Quad positive : original.getPositives())
            result.getPositives().add(clone(positive, parameters));
        for (Collection<Quad> set : original.getNegatives()) {
            Collection<Quad> set2 = new ArrayList<>(set.size());
            for (Quad quad : set)
                set2.add(clone(quad, parameters));
            result.getNegatives().add(set2);
        }
        return result;
    }

    /**
     * Evaluates a dynamic expression to a native value
     *
     * @param context    The evaluation context
     * @param solution   The current bindings
     * @param expression The expression to evaluate
     * @return The evaluated native value
     */
    public static Object evaluateNative(EvalContext context, RDFPatternSolution solution, DynamicExpression expression) {
        Map<String, Object> bindings = new HashMap<>();
        for (Couple<VariableNode, Node> binding : solution)
            bindings.put(binding.x.getName(), RDFUtils.getNative(binding.y));
        return context.getEvaluator().eval(bindings, expression);
    }

    /**
     * Instantiates a RDF node given a solution, i.e. a set of bindings for the variable nodes and the current mapping of blank nodes.
     * A variable node is replaced by its value in the solution.
     * A blank node is replaced by its instance-specific value in the blanks mapping.
     * If this is the first time this blank node has been encountered in the template, the new instance-specific blank node is created and associated.
     *
     * @param context  The evaluation context
     * @param solution The current bindings
     * @param blanks   The current mapping of blank nodes to their instance
     * @param node     The node to instantiate
     * @return The instantiated node, or null if it cannot be instantiated
     */
    private static Node instantiate(EvalContext context, RDFPatternSolution solution, Map<Node, Node> blanks, Node node) {
        if (node == null)
            return null;
        Node result = node;
        if (result.getNodeType() == Node.TYPE_VARIABLE) {
            Node value = solution.get((VariableNode) result);
            if (value == null)
                return null;
            result = value;
        } else if (result.getNodeType() == Node.TYPE_BLANK) {
            Node value = blanks.get(node);
            if (value == null) {
                value = context.getNodes().getBlankNode();
                blanks.put(node, value);
            }
            return value;
        }
        if (result.getNodeType() == Node.TYPE_DYNAMIC && context.getEvaluator() != null) {
            Object value = evaluateNative(context, solution, ((DynamicNode) result).getDynamicExpression());
            return RDFUtils.getRDF(context.getNodes(), value);
        }
        return result;
    }

    /**
     * Instantiate of template of quads given a solution, i.e. a set of bindings for the variable nodes.
     * This replaces the variable nodes in the template by their value.
     * The blank nodes in the template are also instantiated into new instance-specific blank nodes.
     *
     * @param context  The evaluation context
     * @param template The template
     * @param solution The query solution mapping the variables to their value
     * @param buffer   The buffer for the realized quads
     */
    public static void instantiate(EvalContext context, RDFPatternSolution solution, Collection<Quad> template, Collection<Quad> buffer) throws EvalException {
        Map<Node, Node> blanks = new HashMap<>();
        for (Quad quad : template) {
            GraphNode graph = (GraphNode) instantiate(context, solution, blanks, quad.getGraph());
            if (graph == null)
                continue;
            SubjectNode subject = (SubjectNode) instantiate(context, solution, blanks, quad.getSubject());
            if (subject == null)
                continue;
            Property property = (Property) instantiate(context, solution, blanks, quad.getProperty());
            if (property == null)
                continue;
            Node object = instantiate(context, solution, blanks, quad.getObject());
            if (object == null)
                continue;
            buffer.add(new Quad(graph, subject, property, object));
        }
    }

    /**
     * Filters a set of solutions based on the value of an expression
     *
     * @param solutions  The solutions to filter
     * @param expression The expression used to discriminate
     * @param context    The evaluation context
     * @return The filtered solutions
     */
    public static Solutions filter(Solutions solutions, Expression expression, EvalContext context) {
        SolutionsMultiset result = new SolutionsMultiset(solutions.size());
        for (RDFPatternSolution solution : solutions) {
            try {
                if (ExpressionOperator.bool(ExpressionOperator.primitive(expression.eval(context, solution)))) {
                    result.add(solution);
                }
            } catch (EvalException exception) {
                // do nothing
            }
        }
        return result;
    }

    /**
     * Performs the join of two solution sets.
     * A solution is in the joined set is the merged solution of a solution on the left and another on the right.
     * The join is the set of all the merged solutions between compatible solutions on the left and right.
     *
     * @param left  A set of solutions
     * @param right Another set of solutions
     * @return The join set
     */
    public static Solutions join(Solutions left, Solutions right) {
        SolutionsMultiset result = new SolutionsMultiset((left.size() == 0 ? 1 : left.size()) * (right.size() == 0 ? 1 : right.size()));
        for (RDFPatternSolution l : left) {
            for (RDFPatternSolution r : right) {
                RDFPatternSolution j = merge(l, r);
                if (j != null)
                    result.add(j);
            }
        }
        return result;
    }

    /**
     * Performs the left join of two solution sets.
     * A solution is in the joined set is the merged solution of a solution on the left and another on the right.
     * The join is the set of all the merged solutions between compatible solutions on the left and right.
     *
     * @param left       A set of solutions
     * @param right      Another set of solutions
     * @param expression The expression for the value
     * @param context    The evaluation context
     * @return The join set
     */
    public static Solutions leftJoin(Solutions left, Solutions right, Expression expression, EvalContext context) {
        SolutionsMultiset result = new SolutionsMultiset((left.size() == 0 ? 1 : left.size()) * (right.size() == 0 ? 1 : right.size()));
        for (RDFPatternSolution l : left) {
            if (right.size() == 0) {
                result.add(l);
            } else {
                RDFPatternSolution match = null;
                for (RDFPatternSolution r : right) {
                    if (compatible(l, r)) {
                        boolean value = false;
                        RDFPatternSolution merge = merge(l, r);
                        try {
                            value = ExpressionOperator.bool(ExpressionOperator.primitive(expression.eval(context, merge)));
                        } catch (EvalException exception) {
                            // do nothing
                        }
                        match = value ? merge : l;
                        break;
                    }
                }
                result.add(match != null ? match : l);
            }
        }
        return result;
    }

    /**
     * Performs the union of two solution sets.
     *
     * @param left  A set of solutions
     * @param right Another set of solutions
     * @return The union set
     */
    public static Solutions union(Solutions left, Solutions right) {
        SolutionsMultiset result = new SolutionsMultiset(left.size() + right.size());
        for (RDFPatternSolution l : left)
            result.add(l);
        for (RDFPatternSolution r : right)
            result.add(r);
        return result;
    }

    /**
     * Performs the set difference between a left set of solution and a right set of solutions.
     * The result is the subset of solutions on the left that are not compatible with a solution on the right.
     *
     * @param left  A set of solutions
     * @param right Another set of solutions
     * @return The difference
     */
    public static Solutions minus(Solutions left, Solutions right) {
        SolutionsMultiset result = new SolutionsMultiset(left.size());
        for (RDFPatternSolution l : left) {
            boolean match = false;
            for (RDFPatternSolution r : right) {
                if (compatible(l, r)) {
                    match = true;
                    break;
                }
            }
            if (!match)
                result.add(l);
        }
        return result;
    }

    /**
     * Extends a solution with a new binding
     *
     * @param solution   The original solution
     * @param variable   The variable to bind
     * @param expression The expression for the value
     * @param context    The evaluation context
     * @return The new query solution
     */
    public static RDFPatternSolution extend(RDFPatternSolution solution, VariableNode variable, Expression expression, EvalContext context) {
        Object value = null;
        try {
            value = expression.eval(context, solution);
        } catch (EvalException exception) {
            // do nothing
        }
        if (value == null)
            return solution;
        Node valueNode = ExpressionOperator.rdf(value, context);
        if (valueNode == null)
            return solution;
        return new RDFPatternSolution(solution, variable, valueNode);
    }

    /**
     * Extends a set of solutions with a new binding
     *
     * @param solutions  The original solutions
     * @param variable   The variable to bind
     * @param expression The expression for the value
     * @param context    The evaluation context
     * @return The new query solutions
     */
    public static Solutions extend(Solutions solutions, VariableNode variable, Expression expression, EvalContext context) {
        SolutionsMultiset result = new SolutionsMultiset(solutions.size());
        for (RDFPatternSolution solution : solutions)
            result.add(extend(solution, variable, expression, context));
        return result;
    }

    /**
     * Orders a set of solutions
     *
     * @param solutions  The original solutions
     * @param conditions The ordering conditions
     * @param context    The evaluation context
     * @return The ordered solutions
     */
    public static Solutions orderBy(Solutions solutions, List<Couple<Expression, Boolean>> conditions, EvalContext context) {
        if (conditions.isEmpty())
            return solutions;
        Couple<RDFPatternSolution, Double>[] buffer = new Couple[solutions.size()];
        int index = 0;
        for (RDFPatternSolution solution : solutions)
            buffer[index++] = new Couple<>(solution, 0d);
        orderByComputeKey(buffer, 0, buffer.length, conditions.get(0).x, context);
        orderBy(buffer, 0, buffer.length, conditions, 0, context);
        SolutionsMultiset result = new SolutionsMultiset(solutions.size());
        for (int i = 0; i != buffer.length; i++)
            result.add(buffer[i].x);
        return result;
    }

    /**
     * Orders solutions in a buffer according to the specified conditions
     *
     * @param buffer     The buffer of solutions
     * @param first      The index of the first solution
     * @param last       The index of the last solution, excluded
     * @param conditions The conditions
     * @param ci         The index of the condition to use
     * @param context    The evaluation context
     */
    private static void orderBy(Couple<RDFPatternSolution, Double>[] buffer, int first, int last, List<Couple<Expression, Boolean>> conditions, int ci, EvalContext context) {
        final boolean isDescending = conditions.get(ci).y;
        Arrays.sort(buffer, first, last, new Comparator<Couple<RDFPatternSolution, Double>>() {
            @Override
            public int compare(Couple<RDFPatternSolution, Double> item1, Couple<RDFPatternSolution, Double> item2) {
                if (item1.y == null) {
                    if (item2 != null)
                        return isDescending ? 1 : -1;
                    return 0;
                } else if (item2.y == null) {
                    return isDescending ? -1 : 1;
                } else {
                    return isDescending ? item2.y.compareTo(item1.y) : item1.y.compareTo(item2.y);
                }
            }
        });
        if (ci + 1 == conditions.size())
            return;
        final Expression expression = conditions.get(ci + 1).x;
        Double current = buffer[first].y;
        int indexCurrent = first;
        for (int i = first; i != last; i++) {
            if (!Objects.equals(buffer[i].y, current)) {
                // difference with the previous value
                orderByComputeKey(buffer, indexCurrent, i, expression, context);
                orderBy(buffer, indexCurrent, i, conditions, ci + 1, context);
                current = buffer[i].y;
                indexCurrent = i;
            }
        }
        if (indexCurrent < last - 1) {
            orderBy(buffer, indexCurrent, last, conditions, ci + 1, context);
        }
    }

    /**
     * Computes the sorting key for the buffer slice
     *
     * @param buffer     The buffer
     * @param first      The index of the first solution
     * @param last       The index of the last solution, excluded
     * @param expression The expression to use for the computation of the key
     * @param context    The evaluation context
     */
    private static void orderByComputeKey(Couple<RDFPatternSolution, Double>[] buffer, int first, int last, Expression expression, EvalContext context) {
        for (int i = first; i != last; i++) {
            try {
                Double key;
                Object value = ExpressionOperator.primitive(expression.eval(context, buffer[i].x));
                if (ExpressionOperator.isNumInteger(value)) {
                    key = (double) ExpressionOperator.integer(value);
                } else if (ExpressionOperator.isNumDecimal(value)) {
                    key = ExpressionOperator.decimal(value);
                } else {
                    key = null;
                }
                buffer[i].y = key;
            } catch (EvalException exception) {
                buffer[i].y = null;
            }
        }
    }

    /**
     * Projects a set of solutions onto new bindings
     *
     * @param solutions  the original solutions
     * @param projection The projection variables
     * @param context    The evaluation context
     * @return The projected solutions
     * @throws EvalException When an error occurs during the evaluation
     */
    public static Solutions project(Solutions solutions, List<Couple<VariableNode, Expression>> projection, EvalContext context) throws EvalException {
        boolean aggregates = false;
        for (Couple<VariableNode, Expression> projector : projection) {
            if (projector.y != null && projector.y.containsAggregate()) {
                aggregates = true;
                break;
            }
        }
        return aggregates ? projectAggregates(solutions, projection, context) : projectSimple(solutions, projection, context);
    }

    /**
     * Projects a set of solutions onto new bindings, in the case of aggregates
     *
     * @param solutions  the original solutions
     * @param projection The projection variables
     * @param context    The evaluation context
     * @return The projected solutions
     * @throws EvalException When an error occurs during the evaluation
     */
    private static Solutions projectAggregates(Solutions solutions, List<Couple<VariableNode, Expression>> projection, EvalContext context) throws EvalException {
        List<List> projected = new ArrayList<>();
        for (Couple<VariableNode, Expression> projector : projection) {
            Object evaluated = projector.y.eval(context, solutions);
            if (evaluated instanceof List)
                projected.add((List) evaluated);
            else
                projected.add(Collections.singletonList(evaluated));
        }

        // cross product
        SolutionsMultiset result = new SolutionsMultiset();
        List<Couple<VariableNode, Node>> bindings = new ArrayList<>(projected.size());
        for (int i = 0; i != projection.size(); i++) {
            VariableNode variable = projection.get(i).x;
            List values = projected.get(i);
            Node value = values.isEmpty() ? null : ExpressionOperator.rdf(values.get(0), context);
            bindings.add(new Couple<>(variable, value));
        }
        result.add(new RDFPatternSolution(bindings));


        List<RDFPatternSolution> buffer = new ArrayList<>();
        for (int i = 0; i != projection.size(); i++) {
            List values = projected.get(i);
            if (values.size() <= 1)
                continue;
            VariableNode variable = projection.get(i).x;
            for (Object value : values) {
                Node valueNode = values.isEmpty() ? null : ExpressionOperator.rdf(value, context);
                for (RDFPatternSolution solution : result)
                    buffer.add(new RDFPatternSolution(solution, variable, valueNode));
                for (RDFPatternSolution solution : buffer)
                    result.add(solution);
                buffer.clear();
            }
        }
        return result;
    }

    /**
     * Projects a set of solutions onto new bindings, in the simple case without aggregates
     *
     * @param solutions  the original solutions
     * @param projection The projection variables
     * @param context    The evaluation context
     * @return The projected solutions
     * @throws EvalException When an error occurs during the evaluation
     */
    private static Solutions projectSimple(Solutions solutions, List<Couple<VariableNode, Expression>> projection, EvalContext context) throws EvalException {
        SolutionsMultiset result = new SolutionsMultiset(solutions.size());
        for (RDFPatternSolution solution : solutions) {
            List<Couple<VariableNode, Node>> bindings = new ArrayList<>();
            for (Couple<VariableNode, Expression> projector : projection) {
                if (projector.y != null) {
                    Object value = null;
                    try {
                        value = projector.y.eval(context, solution);
                    } catch (EvalException exception) {
                        // do nothing
                    }
                    Node valueNode = ExpressionOperator.rdf(value, context);
                    bindings.add(new Couple<>(projector.x, valueNode));
                } else {
                    bindings.add(new Couple<>(projector.x, solution.get(projector.x)));
                }
            }
            result.add(new RDFPatternSolution(bindings));
        }
        return result;
    }

    /**
     * Gets the distinct solutions from the specified original set
     *
     * @param solutions A set of original solutions
     * @return The set of distinct solutions
     */
    public static Solutions distinct(Solutions solutions) {
        return new SolutionsMultiset(solutions, true);
    }

    /**
     * Gets the reduced solutions from the specified original set
     *
     * @param solutions A set of original solutions
     * @return The set of distinct solutions
     */
    public static Solutions reduced(Solutions solutions) {
        return new SolutionsMultiset(solutions, true);
    }

    /**
     * Slices the specified sequence of solutions
     *
     * @param solutions The original solutions
     * @param start     The index of the first solution to include in the slice
     * @param length    The length of the slice
     * @return The resulting solutions
     */
    public static Solutions slice(Solutions solutions, int start, int length) {
        SolutionsMultiset result = new SolutionsMultiset(length);
        if (start >= solutions.size())
            return result;
        int index = 0;
        int remaining = length;
        for (RDFPatternSolution solution : solutions) {
            if (index >= start) {
                result.add(solution);
                remaining--;
            }
            index++;
            if (remaining == 0)
                break;
        }
        return result;
    }

    /**
     * Determines whether two solutions are compatible.
     * Two solutions are compatible iff for all variables bound by both solution, their value is the same.
     * The variables that are bound in a solution but not the other are not considered.
     *
     * @param left  A solution
     * @param right Another solution
     * @return Whether the two solutions are compatible
     */
    public static boolean compatible(RDFPatternSolution left, RDFPatternSolution right) {
        for (Couple<VariableNode, Node> binding : left) {
            Node value = right.get(binding.x);
            if (value != null && !RDFUtils.same(binding.y, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Builds the merge of two solutions.
     * If the two solutions are not compatible, null is returned.
     * If the solutions are compatible, the merged solution contains all the bindings from both solutions.
     *
     * @param left  A solution
     * @param right Another solution
     * @return The merged solution, or null if the two were not compatible
     */
    public static RDFPatternSolution merge(RDFPatternSolution left, RDFPatternSolution right) {
        Collection<Couple<VariableNode, Node>> bindings = new ArrayList<>();
        for (Couple<VariableNode, Node> binding : left) {
            Node value = right.get(binding.x);
            if (value != null) {
                if (!RDFUtils.same(binding.y, value))
                    return null;
                bindings.add(binding);
            } else {
                bindings.add(binding);
            }
        }
        for (Couple<VariableNode, Node> binding : right) {
            boolean found = false;
            for (Couple<VariableNode, Node> present : bindings) {
                if (RDFUtils.same(binding.x, present.x)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                bindings.add(binding);
            }
        }
        return new RDFPatternSolution(bindings);
    }

    /**
     * Groups solutions according to a sequence of expressions
     *
     * @param solutions   The original solutions
     * @param expressions The expressions for the grouping keys
     * @param context     The evaluation context
     * @return The grouped solutions
     */
    public static Solutions group(Solutions solutions, List<Couple<VariableNode, Expression>> expressions, EvalContext context) {
        SolutionsGroup result = new SolutionsGroup();
        for (RDFPatternSolution solution : solutions) {
            RDFPatternSolution targetSolution = solution;
            List<Object> keys = new ArrayList<>(expressions.size());
            for (Couple<VariableNode, Expression> expression : expressions) {
                Object key = null;
                try {
                    key = expression.y.eval(context, solution);
                } catch (EvalException exception) {
                    // do nothing
                }
                keys.add(key);
                if (expression.x != null)
                    targetSolution = new RDFPatternSolution(targetSolution, expression.x, ExpressionOperator.rdf(key, context));
            }
            result.add(keys, targetSolution);
        }
        return result;
    }
}
