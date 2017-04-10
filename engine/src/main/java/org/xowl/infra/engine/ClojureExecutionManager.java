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
import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutableFunction;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.loaders.XOWLDeserializer;
import org.xowl.infra.store.loaders.XOWLLexer;
import org.xowl.infra.store.loaders.XOWLParser;
import org.xowl.infra.utils.IOUtils;

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
            List clojureContext = new ArrayList();
            for (Map.Entry<String, Object> binding : context.getAllBindings().entrySet()) {
                if (binding.getValue() instanceof IRI)
                    continue;
                clojureContext.add(Symbol.create(binding.getKey()));
                clojureContext.add(binding.getValue());
            }
            IPersistentList top = PersistentList.create(Arrays.asList(
                    Symbol.create("clojure.core", "let"),
                    PersistentVector.create(clojureContext.toArray()),
                    // require bindings
                    PersistentList.create(Arrays.asList(
                            Symbol.create("clojure.core", "eval"),
                            expression.getClojure()
                    ))
            ));
            return Compiler.eval(top);
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

    @Override
    public XOWLDeserializer getDeserializer() {
        return new ClojureXOWLDeserializer(this);
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
     * Gets the Clojure object for the specified parsed expression
     *
     * @param definition The definition of the Clojure expression
     * @return The Clojure object representing the expression
     */
    public ClojureExpression loadExpression(ASTNode definition) {
        return new ClojureExpression(serializeClojure(definition));
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
                Iterator iterator = ((Iterable) Compiler.load(reader)).iterator();
                for (ClojureFunction function : cljToBuild) {
                    IFn definition = (IFn) iterator.next();
                    Var.intern(cljNamespaceRoot, Symbol.intern(function.getName()), definition);
                    function.setClojure(definition);
                }
            } catch (IOException exception) {
                // do nothing
            }
            cljToBuild.clear();
        }
    }


    /**
     * Re-serializes the specified AST node into a string for the Clojure reader
     *
     * @param node An AST node
     * @return The serialized Clojure source
     */
    private static String serializeClojure(ASTNode node) {
        StringBuilder builder = new StringBuilder();
        serializeClojure(builder, node);
        return builder.toString();
    }

    /**
     * Re-serializes the specified AST node into a string for the Clojure reader
     *
     * @param builder The string builder for the result
     * @param node    An AST node
     */
    private static void serializeClojure(StringBuilder builder, ASTNode node) {
        switch (node.getSymbol().getID()) {
            case XOWLLexer.ID.CLJ_SYMBOL:
            case XOWLLexer.ID.CLJ_KEYWORD:
            case XOWLLexer.ID.LITERAL_STRING:
            case XOWLLexer.ID.LITERAL_CHAR:
            case XOWLLexer.ID.LITERAL_NIL:
            case XOWLLexer.ID.LITERAL_TRUE:
            case XOWLLexer.ID.LITERAL_FALSE:
            case XOWLLexer.ID.LITERAL_INTEGER:
            case XOWLLexer.ID.LITERAL_FLOAT:
            case XOWLLexer.ID.LITERAL_RATIO:
            case XOWLLexer.ID.LITERAL_ARGUMENT:
                builder.append(node.getValue());
                break;
            case XOWLParser.ID.list:
                builder.append("( ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append(")");
                break;
            case XOWLParser.ID.vector:
                builder.append("[ ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append("]");
                break;
            case XOWLParser.ID.map:
                builder.append("{ ");
                for (ASTNode couple : node.getChildren()) {
                    serializeClojure(builder, couple.getChildren().get(0));
                    serializeClojure(builder, couple.getChildren().get(1));
                }
                builder.append("}");
                break;
            case XOWLParser.ID.set:
                builder.append("#{ ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append("}");
                break;
            case XOWLParser.ID.constructor:
                builder.append("#");
                serializeClojure(builder, node.getChildren().get(0));
                serializeClojure(builder, node.getChildren().get(1));
                break;
            case XOWLParser.ID.quote:
                builder.append("'");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.deref:
                builder.append("@");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.metadata:
                builder.append("^");
                serializeClojure(builder, node.getChildren().get(0));
                serializeClojure(builder, node.getChildren().get(1));
                break;
            case XOWLParser.ID.regexp:
                builder.append("#");
                builder.append(node.getChildren().get(0));
                break;
            case XOWLParser.ID.var_quote:
                builder.append("#'");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.anon_function:
                builder.append("#");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.ignore:
                builder.append("#_");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.syntax_quote:
                builder.append("`");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.unquote:
                builder.append("~");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.unquote_splicing:
                builder.append("~@");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case XOWLParser.ID.conditional:
                builder.append("#?");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            default:
                throw new Error("Unsupported construct: " + node.getSymbol().getName());
        }
        builder.append(" ");
    }
}
