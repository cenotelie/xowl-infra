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

import clojure.lang.Compiler;
import clojure.lang.*;
import fr.cenotelie.commons.utils.IOUtils;
import fr.cenotelie.commons.utils.TextUtils;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.EvaluationUtils;
import org.xowl.infra.store.execution.ExecutableFunction;
import org.xowl.infra.store.execution.ExecutionManager;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * Manages the execution of xOWL ontologies expressed using Clojure
 *
 * @author Laurent Wouters
 */
public class ClojureExecutionManager implements ExecutionManager {
    /**
     * The number of repositories
     */
    private static int REPOSITORY_COUNT = 0;

    /**
     * Gets the next Clojure namespace
     *
     * @return The next Clojure namespace
     */
    private synchronized static String getNextNamespace() {
        String result = ClojureExecutionManager.class.getPackage().getName() + ".clojure.Repository" + Integer.toHexString(REPOSITORY_COUNT);
        REPOSITORY_COUNT++;
        return result;
    }

    /**
     * The parent repository
     */
    private final Repository repository;
    /**
     * The root namespace for the Clojure symbols
     */
    private final String cljNamespace;
    /**
     * The root namespace for the Clojure symbols
     */
    public final Namespace cljNamespaceRoot;
    /**
     * The map of the known Clojure functions
     */
    private final Map<String, ClojureFunction> cljFunctions;
    /**
     * The Clojure function definitions that are not yet compiled
     */
    private final List<ClojureFunction> cljToBuild;
    /**
     * The counter for anonymous functions
     */
    private int counter;

    /**
     * Initializes this manager
     *
     * @param repository The parent repository
     */
    public ClojureExecutionManager(Repository repository) {
        this.repository = repository;
        this.cljNamespace = getNextNamespace();
        this.cljNamespaceRoot = Namespace.findOrCreate(Symbol.intern(cljNamespace));
        this.cljFunctions = new HashMap<>();
        this.cljToBuild = new ArrayList<>();
        this.counter = 0;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public Object eval(Map<String, Object> bindings, EvaluableExpression expression) {
        if (expression instanceof ClojureExpression)
            return eval(bindings, (ClojureExpression) expression);
        return null;
    }

    /**
     * Evaluates a Clojure expression
     *
     * @param bindings   The new contextual bindings
     * @param expression The expression to evaluate
     * @return The evaluated value
     */
    private Object eval(Map<String, Object> bindings, ClojureExpression expression) {
        compile();
        ClojureExecutionContext context = ClojureExecutionContext.get(this);
        if (context == null)
            return null;
        try {
            context.push(bindings);
            StringBuilder builder = new StringBuilder();
            builder.append("(ns ");
            builder.append(cljNamespace);
            builder.append(" (:require [org.xowl.infra.engine.ClojureBindings :as xowl]))");
            builder.append(IOUtils.LINE_SEPARATOR);
            builder.append("(let [");
            for (Map.Entry<String, Object> binding : context.getAllBindings().entrySet()) {
                builder.append(binding.getKey());
                builder.append(" ");
                Object value = binding.getValue();
                if (EvaluationUtils.isNumDecimal(value) || EvaluationUtils.isNumInteger(value) || value instanceof Boolean)
                    builder.append(value.toString());
                else if (value instanceof IRI) {
                    builder.append("\"");
                    builder.append(TextUtils.escapeStringBaseDoubleQuote(((IRI) value).getHasValue()));
                    builder.append("\"");
                } else {
                    builder.append("\"");
                    builder.append(TextUtils.escapeStringBaseDoubleQuote(value.toString()));
                    builder.append("\"");
                }
                builder.append(" ");
            }
            builder.append("] ");
            builder.append(expression.getSource());
            builder.append(")");
            try {
                Var.pushThreadBindings(RT.map(Compiler.LOADER, ClojureInit.getClassLoader()));
                return Compiler.load(new StringReader(builder.toString()));
            } finally {
                Var.popThreadBindings();
            }
        } finally {
            context.pop();
        }
    }

    @Override
    public ExecutableFunction getFunction(String functionIRI) {
        return cljFunctions.get(functionIRI);
    }

    @Override
    public Object execute(String functionIRI, Object... parameters) {
        ClojureFunction cljFunction = cljFunctions.get(functionIRI);
        if (cljFunction == null)
            return null;
        return execute(cljFunction, parameters);
    }

    /**
     * Executes a function
     *
     * @param function   The function
     * @param parameters The parameters
     * @return The returned value
     */
    private Object execute(ClojureFunction function, Object... parameters) {
        ClojureExecutionContext.get(this);
        compile();
        try {
            Var.pushThreadBindings(RT.map(Compiler.LOADER, ClojureInit.getClassLoader()));
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
        } finally {
            Var.popThreadBindings();
        }
    }

    @Override
    public EvaluableExpression loadExpression(String source) {
        return new ClojureExpression(source);
    }

    @Override
    public void registerFunction(String functionIRI, EvaluableExpression definition) {
        if (functionIRI == null)
            functionIRI = newCljName();
        String[] parts = functionIRI.split("#");
        String name = parts[parts.length - 1];
        ClojureFunction function = new ClojureFunction(functionIRI, name, definition.getSource());
        synchronized (cljToBuild) {
            cljFunctions.put(functionIRI, function);
            cljToBuild.add(function);
        }
    }

    @Override
    public void unregisterFunction(String functionIRI) {
        synchronized (cljToBuild) {
            cljFunctions.remove(functionIRI);
            for (ClojureFunction function : cljToBuild) {
                if (Objects.equals(functionIRI, function.getIdentifier())) {
                    cljToBuild.remove(function);
                    return;
                }
            }
        }
    }

    /**
     * Generates a new Clojure name for an anonymous function
     *
     * @return A new Clojure name
     */
    private synchronized String newCljName() {
        String name = "http://xowl.org/infra/engine/clojure#Function" + counter;
        counter++;
        return name;
    }

    /**
     * Compiles the outstanding function definitions
     */
    private void compile() {
        synchronized (cljToBuild) {
            if (cljToBuild.isEmpty())
                return;
            StringBuilder builder = new StringBuilder();
            builder.append("(ns ");
            builder.append(cljNamespace);
            builder.append(" (:require [org.xowl.infra.engine.ClojureBindings :as xowl]))");
            builder.append(IOUtils.LINE_SEPARATOR);
            builder.append("(declare & ");
            for (ClojureFunction function : cljToBuild) {
                builder.append(function.getName());
                builder.append(" ");
            }
            builder.append(")");
            builder.append(IOUtils.LINE_SEPARATOR);
            builder.append("[ ");
            for (ClojureFunction function : cljToBuild) {
                builder.append(function.getSource());
                builder.append(IOUtils.LINE_SEPARATOR);
            }
            builder.append(" ]");
            try (Reader reader = new StringReader(builder.toString())) {
                try {
                    Var.pushThreadBindings(RT.map(Compiler.LOADER, ClojureInit.getClassLoader()));
                    Iterator iterator = ((Iterable) Compiler.load(reader)).iterator();
                    for (ClojureFunction function : cljToBuild) {
                        IFn definition = (IFn) iterator.next();
                        Var.intern(cljNamespaceRoot, Symbol.intern(function.getName()), definition);
                        function.setClojure(definition);
                    }
                } finally {
                    Var.popThreadBindings();
                }
            } catch (IOException exception) {
                // do nothing
            }
            cljToBuild.clear();
        }
    }
}
