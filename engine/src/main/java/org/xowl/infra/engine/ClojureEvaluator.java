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

package org.xowl.infra.engine;

import clojure.lang.Compiler;
import clojure.lang.*;
import org.xowl.infra.lang.actions.DynamicExpression;
import org.xowl.infra.lang.actions.OpaqueExpression;
import org.xowl.infra.lang.actions.QueryVariable;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.EvaluatorContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Represents the Clojure evaluator for xOWL ontologies
 *
 * @author Laurent Wouters
 */
public class ClojureEvaluator implements Evaluator {
    /**
     * Initializes this evaluator
     */
    public ClojureEvaluator() {
    }

    @Override
    public Object eval(DynamicExpression expression) {
        if (expression instanceof QueryVariable) {
            EvaluatorContext context = EvaluatorContext.get();
            return context.getBinding(((QueryVariable) expression).getName());
        } else if (expression instanceof OpaqueExpression) {
            Object value = ((OpaqueExpression) expression).getValue();
            if (value instanceof ClojureFunction)
                return execute((ClojureFunction) value);
            return evaluateExpression(value);
        }
        return null;
    }

    @Override
    public Object eval(DynamicExpression expression, Object... parameters) {
        if (expression instanceof QueryVariable) {
            EvaluatorContext context = EvaluatorContext.get();
            return context.getBinding(((QueryVariable) expression).getName());
        } else if (expression instanceof OpaqueExpression) {
            Object value = ((OpaqueExpression) expression).getValue();
            if (value instanceof ClojureFunction)
                return execute((ClojureFunction) value, parameters);
            return evaluateExpression(value);
        }
        return null;
    }

    @Override
    public boolean isDefined(String function) {
        return ClojureManager.getFunction(function) != null;
    }

    @Override
    public Object execute(String function, Object... parameters) {
        ClojureFunction cljFunction = ClojureManager.getFunction(function);
        if (cljFunction == null)
            return null;
        return execute(cljFunction, parameters);
    }

    /**
     * Evaluates a Clojure expression
     *
     * @param cljExp The expression to evaluate
     * @return The evaluated value
     */
    private Object evaluateExpression(Object cljExp) {
        ClojureManager.compileOutstandings();
        Namespace old = (Namespace) RT.CURRENT_NS.deref();
        RT.CURRENT_NS.bindRoot(ClojureManager.NAMESPACE_ROOT);

        List content = new ArrayList();
        for (Map.Entry<String, Object> binding : EvaluatorContext.get().getBindings().entrySet()) {
            if (binding.getValue() instanceof IRI)
                continue;
            content.add(Symbol.create(binding.getKey()));
            content.add(binding.getValue());
        }
        PersistentVector content1 = PersistentVector.create(content.toArray());
        IPersistentList top = PersistentList.create(Arrays.asList(Symbol.create("clojure.core", "let"), content1, cljExp));
        Object result = Compiler.eval(top);

        RT.CURRENT_NS.bindRoot(old);
        return result;
    }

    /**
     * Executes a function
     *
     * @param function   The function
     * @param parameters The parameters
     * @return The returned value
     */
    private Object execute(ClojureFunction function, Object... parameters) {
        ClojureManager.compileOutstandings();
        if (parameters == null || parameters.length == 0)
            return function.getClojure().invoke();
        switch (parameters.length) {
            case 1:
                return function.getClojure().invoke(parameters[0]);
            case 2:
                return function.getClojure().invoke(parameters[0], parameters[1]);
            case 3:
                return function.getClojure().invoke(parameters[0], parameters[1], parameters[2]);
            case 4:
                return function.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3]);
            case 5:
                return function.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4]);
            case 6:
                return function.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5]);
            case 7:
                return function.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6]);
            case 8:
                return function.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7]);
            case 9:
                return function.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[9]);
            case 10:
                return function.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[9], parameters[10]);
        }
        return function.getClojure().invoke(parameters);
    }
}
