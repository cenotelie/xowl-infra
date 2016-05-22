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

import org.xowl.infra.store.rdf.RDFPatternSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SPARQL function
 *
 * @author Laurent Wouters
 */
public abstract class ExpressionFunction {
    /**
     * The function name (or IRI)
     */
    private final String name;
    /**
     * The minimum number of arguments
     */
    private final int argsCountMin;
    /**
     * The maximum number of arguments
     */
    private final int argsCountMax;

    /**
     * Gets the function name (or IRI)
     *
     * @return The function name (or IRI)
     */
    public String getName() {
        return name;
    }

    /**
     * Initializes this function
     *
     * @param name         The function name (or IRI)
     * @param argsCountMin The minimum number of arguments
     * @param argsCountMax The maximum number of arguments
     */
    public ExpressionFunction(String name, int argsCountMin, int argsCountMax) {
        this.name = name;
        this.argsCountMin = argsCountMin;
        this.argsCountMax = argsCountMax;
    }

    /**
     * Checks the number of arguments
     *
     * @param arguments The passed arguments
     * @throws EvalException When an error occurs during the evaluation
     */
    private void checkArgsCount(List<Expression> arguments) throws EvalException {
        if (arguments.size() < argsCountMin || arguments.size() > argsCountMax)
            throw new EvalException("Function " + name + " requires [" + argsCountMin + "-" + argsCountMax + "] argument(s)");
    }

    /**
     * Evaluates this function on a single solution
     *
     * @param context   The evaluation context
     * @param bindings  The current bindings
     * @param arguments The passed arguments
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public Object eval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvalException {
        checkArgsCount(arguments);
        return doEval(context, bindings, arguments);
    }

    /**
     * Evaluates this function on a multiple solutions
     *
     * @param context   The evaluation context
     * @param solutions The current set of solutions
     * @param arguments The passed arguments
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    public Object eval(EvalContext context, Solutions solutions, List<Expression> arguments) throws EvalException {
        checkArgsCount(arguments);
        List<Object> results = new ArrayList<>(solutions.size());
        for (RDFPatternSolution solution : solutions)
            results.add(doEval(context, solution, arguments));
        return results;
    }

    /**
     * Evaluates this function on a single solution
     *
     * @param context   The evaluation context
     * @param bindings  The current bindings
     * @param arguments The passed arguments
     * @return The result
     * @throws EvalException When an error occurs during the evaluation
     */
    protected abstract Object doEval(EvalContext context, RDFPatternSolution bindings, List<Expression> arguments) throws EvalException;
}
