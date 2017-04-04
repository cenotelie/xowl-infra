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

import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.RDFPatternSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the call to an external function in an expression
 *
 * @author Laurent Wouters
 */
public class ExpressionFunctionCall implements Expression {
    /**
     * The function's IRI
     */
    private final String iri;
    /**
     * The arguments to use
     */
    private final List<Expression> arguments;
    /**
     * Whether the DISTINCT keyword is applied
     */
    private final boolean isDistinct;
    /**
     * The separator marker
     */
    private final String separator;

    /**
     * Initializes this expression
     *
     * @param iri        The function's IRI
     * @param arguments  The arguments to use
     * @param isDistinct Whether the DISTINCT keyword is applied
     * @param separator  The separator marker
     */
    public ExpressionFunctionCall(String iri, List<Expression> arguments, boolean isDistinct, String separator) {
        this.iri = iri;
        this.arguments = new ArrayList<>(arguments);
        this.isDistinct = isDistinct;
        this.separator = separator;
    }

    @Override
    public Object eval(EvalContext context, RDFPatternSolution bindings) throws EvalException {
        ExpressionFunction function = ExpressionFunctions.get(iri);
        if (function != null) {
            // this is a builtin function
            return function.eval(context, bindings, arguments);
        }
        Evaluator evaluator = context.getEvaluator();
        if (evaluator == null)
            throw new EvalException("Unknown function " + iri);
        if (!evaluator.isDefined(iri))
            throw new EvalException("Unknown function " + iri);
        Object[] parameters = new Object[arguments.size()];
        for (int i = 0; i != arguments.size(); i++)
            parameters[i] = arguments.get(i).eval(context, bindings);
        return RDFUtils.getRDF(context.getNodes(), evaluator.execute(iri, parameters));
    }

    @Override
    public Object eval(EvalContext context, Solutions solutions) throws EvalException {
        if (iri.equalsIgnoreCase("COUNT"))
            return evalAggregateCount(context, solutions);
        if (iri.equalsIgnoreCase("SUM"))
            return evalAggregateSum(context, solutions);
        if (iri.equalsIgnoreCase("MIN"))
            return evalAggregateMin(context, solutions);
        if (iri.equalsIgnoreCase("MAX"))
            return evalAggregateMax(context, solutions);
        if (iri.equalsIgnoreCase("AVG"))
            return evalAggregateAverage(context, solutions);
        if (iri.equalsIgnoreCase("SAMPLE"))
            return evalAggregateSample(context, solutions);
        if (iri.equalsIgnoreCase("GROUP_CONCAT"))
            return evalAggregateGroupConcat(context, solutions);
        ExpressionFunction function = ExpressionFunctions.get(iri);
        if (function == null)
            throw new EvalException("Unknown function " + iri);
        return function.eval(context, solutions, arguments);
    }

    @Override
    public boolean containsAggregate() {
        if (iri.equalsIgnoreCase("COUNT")
                || iri.equalsIgnoreCase("SUM")
                || iri.equalsIgnoreCase("MIN")
                || iri.equalsIgnoreCase("MAX")
                || iri.equalsIgnoreCase("AVG")
                || iri.equalsIgnoreCase("SAMPLE")
                || iri.equalsIgnoreCase("GROUP_CONCAT"))
            return true;
        for (Expression arg : arguments) {
            if (arg.containsAggregate())
                return true;
        }
        return false;
    }

    @Override
    public Expression clone(Map<String, Node> parameters) {
        List<Expression> arguments = new ArrayList<>(this.arguments.size());
        for (Expression argument : this.arguments) {
            arguments.add(argument.clone(parameters));
        }
        return new ExpressionFunctionCall(iri, arguments, isDistinct, separator);
    }

    /**
     * Evaluates the COUNT aggregate
     *
     * @param context   The evaluation context
     * @param solutions The current solutions
     * @return The evaluated value
     * @throws EvalException When an error occurs during the evaluation
     */
    private Object evalAggregateCount(EvalContext context, Solutions solutions) throws EvalException {
        if (arguments.size() > 1)
            throw new EvalException("COUNT requires 1 argument");
        if (isDistinct) {
            if (arguments.isEmpty())
                return getDistincts(solutions).size();
            Object evaluated = arguments.get(0).eval(context, solutions);
            if (evaluated instanceof List)
                return getDistincts((List) evaluated).size();
            return (evaluated == null ? 0 : 1);
        } else {
            if (arguments.isEmpty())
                return solutions.size();
            Object evaluated = arguments.get(0).eval(context, solutions);
            if (evaluated instanceof List)
                return ((List) evaluated).size();
            return (evaluated == null ? 0 : 1);
        }
    }

    /**
     * Evaluates the SUM aggregate
     *
     * @param context   The evaluation context
     * @param solutions The current solutions
     * @return The evaluated value
     * @throws EvalException When an error occurs during the evaluation
     */
    private Object evalAggregateSum(EvalContext context, Solutions solutions) throws EvalException {
        throw new EvalException("Unsupported aggregate");
    }

    /**
     * Evaluates the MIN aggregate
     *
     * @param context   The evaluation context
     * @param solutions The current solutions
     * @return The evaluated value
     * @throws EvalException When an error occurs during the evaluation
     */
    private Object evalAggregateMin(EvalContext context, Solutions solutions) throws EvalException {
        throw new EvalException("Unsupported aggregate");
    }

    /**
     * Evaluates the MAX aggregate
     *
     * @param context   The evaluation context
     * @param solutions The current solutions
     * @return The evaluated value
     * @throws EvalException When an error occurs during the evaluation
     */
    private Object evalAggregateMax(EvalContext context, Solutions solutions) throws EvalException {
        throw new EvalException("Unsupported aggregate");
    }

    /**
     * Evaluates the AVG aggregate
     *
     * @param context   The evaluation context
     * @param solutions The current solutions
     * @return The evaluated value
     * @throws EvalException When an error occurs during the evaluation
     */
    private Object evalAggregateAverage(EvalContext context, Solutions solutions) throws EvalException {
        throw new EvalException("Unsupported aggregate");
    }

    /**
     * Evaluates the SAMPLE aggregate
     *
     * @param context   The evaluation context
     * @param solutions The current solutions
     * @return The evaluated value
     * @throws EvalException When an error occurs during the evaluation
     */
    private Object evalAggregateSample(EvalContext context, Solutions solutions) throws EvalException {
        throw new EvalException("Unsupported aggregate");
    }

    /**
     * Evaluates the GROUP_CONCAT aggregate
     *
     * @param context   The evaluation context
     * @param solutions The current solutions
     * @return The evaluated value
     * @throws EvalException When an error occurs during the evaluation
     */
    private Object evalAggregateGroupConcat(EvalContext context, Solutions solutions) throws EvalException {
        throw new EvalException("Unsupported aggregate");
    }

    /**
     * Gets the distinct values
     *
     * @param originals The original values
     * @return The distinct values
     */
    private List<Object> getDistincts(List<Object> originals) {
        List<Object> result = new ArrayList<>();
        for (Object original : originals)
            if (original != null && !result.contains(original))
                result.add(original);
        return result;
    }

    /**
     * Gets the distinct solutions
     *
     * @param solutions The original solutions
     * @return The distinct ones
     */
    private Solutions getDistincts(Solutions solutions) {
        SolutionsMultiset result = new SolutionsMultiset();
        for (RDFPatternSolution solution : solutions)
            result.add(solution);
        return result;
    }
}
