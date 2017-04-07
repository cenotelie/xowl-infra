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

package org.xowl.infra.engine;

import org.xowl.infra.store.execution.ExecutionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents the thread-specific context of an evaluator
 *
 * @author Laurent Wouters
 */
class ClojureExecutionContext {
    /**
     * The thread-local context
     */
    private static final ThreadLocal<ClojureExecutionContext> THREAD_CONTEXT = new ThreadLocal<>();

    /**
     * Gets the evaluator context for the current thread
     *
     * @param executionManager The current execution manager
     * @return The evaluator context for the current thread
     */
    public static ClojureExecutionContext get(ExecutionManager executionManager) {
        ClojureExecutionContext context = THREAD_CONTEXT.get();
        if (context == null) {
            if (executionManager == null)
                // cannot resolve the context
                return null;
            context = new ClojureExecutionContext(executionManager);
            THREAD_CONTEXT.set(context);
            return context;
        }
        if (executionManager == null)
            return context;
        if (context.executionManager != executionManager) {
            context = new ClojureExecutionContext(executionManager);
            THREAD_CONTEXT.set(context);
        }
        return context;
    }

    /**
     * The associated execution manager
     */
    private final ExecutionManager executionManager;
    /**
     * The stack of lexical bindings
     */
    private final Stack<Map<String, Object>> contexts;

    /**
     * Initializes this context
     *
     * @param executionManager The associated execution manager
     */
    public ClojureExecutionContext(ExecutionManager executionManager) {
        this.executionManager = executionManager;
        this.contexts = new Stack<>();
    }

    /**
     * Gets the associated execution manager
     *
     * @return The associated execution manager
     */
    public ExecutionManager getExecutionManager() {
        return executionManager;
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
    public Map<String, Object> getAllBindings() {
        Map<String, Object> result = new HashMap<>();
        for (Map<String, Object> bindings : contexts) {
            result.putAll(bindings);
        }
        return result;
    }
}
