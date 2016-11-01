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
     * @return The evaluator context for the current thread
     */
    public static EvaluatorContext get() {
        EvaluatorContext context = THREAD_CONTEXT.get();
        if (context == null) {
            context = new EvaluatorContext();
            THREAD_CONTEXT.set(context);
        }
        return context;
    }

    /**
     * The stack of lexical bindings
     */
    private final Stack<Map<String, Object>> contexts;

    /**
     * Initializes this context
     */
    public EvaluatorContext() {
        this.contexts = new Stack<>();
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
     * Gets the head lexical bindings
     *
     * @return The head lexical bindings
     */
    public Map<String, Object> peek() {
        return contexts.peek();
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