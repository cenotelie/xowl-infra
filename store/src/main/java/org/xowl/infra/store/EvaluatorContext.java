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

package org.xowl.infra.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents the thread-specific context of an evaluator
 *
 * @author Laurent Wouters
 */
public class EvaluatorContext {
    /**
     * The thread-local context
     */
    private static final ThreadLocal<EvaluatorContext> THREAD_CONTEXT = new ThreadLocal<>();

    /**
     * Gets the evaluator context for the current thread
     *
     * @param evaluator The current evaluator
     * @return The evaluator context for the current thread
     */
    public static EvaluatorContext get(Evaluator evaluator) {
        EvaluatorContext context = THREAD_CONTEXT.get();
        if (context == null) {
            if (evaluator == null)
                // cannot resolve the context
                return null;
            context = new EvaluatorContext(evaluator);
            THREAD_CONTEXT.set(context);
        }
        if (evaluator == null)
            return context;
        if (context.getEvaluator() != evaluator) {
            context = new EvaluatorContext(evaluator);
            THREAD_CONTEXT.set(context);
        }
        return context;
    }

    /**
     * The associated evaluator
     */
    private final Evaluator evaluator;
    /**
     * The stack of lexical bindings
     */
    private final Stack<Map<String, Object>> contexts;

    /**
     * Initializes this context
     */
    public EvaluatorContext(Evaluator evaluator) {
        this.evaluator = evaluator;
        this.contexts = new Stack<>();
    }

    /**
     * Gets the evaluator associated to this context
     *
     * @return The evaluator associated to this context
     */
    public Evaluator getEvaluator() {
        return evaluator;
    }

    /**
     * Gets the repository associated to this context
     *
     * @return The repository associated to this context
     */
    public Repository getRepository() {
        return evaluator.getRepository();
    }

    /**
     * Pushes a new set of bindings for a lexical context
     *
     * @param bindings The lexical bindings to push
     */
    public void push(Map<String, Object> bindings) {
        contexts.push(bindings);
    }

    /**
     * Pops the head lexical bindings
     */
    public void pop() {
        contexts.pop();
    }

    /**
     * Gets the binding for the specified name
     *
     * @param name The name
     * @return The associated value, or null if it does not exist
     */
    public Object getBinding(String name) {
        for (int i = contexts.size() - 1; i != -1; i--) {
            Object result = contexts.get(i).get(name);
            if (result != null)
                return result;
        }
        return null;
    }

    /**
     * Gets all the bindings in this context
     *
     * @return The bindings in this context
     */
    public Map<String, Object> getBindings() {
        Map<String, Object> result = new HashMap<>();
        for (Map<String, Object> bindings : contexts) {
            result.putAll(bindings);
        }
        return result;
    }
}
