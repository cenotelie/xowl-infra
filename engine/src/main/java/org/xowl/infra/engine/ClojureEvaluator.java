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

import clojure.java.api.Clojure;
import clojure.lang.Compiler;
import clojure.lang.*;
import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.lang.actions.DynamicExpression;
import org.xowl.infra.lang.actions.OpaqueExpression;
import org.xowl.infra.lang.actions.QueryVariable;
import org.xowl.infra.lang.owl2.IRI;
import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.EvaluatorContext;
import org.xowl.infra.store.Repository;
import org.xowl.infra.store.loaders.XOWLLexer;
import org.xowl.infra.store.loaders.XOWLParser;
import org.xowl.infra.utils.IOUtils;

import java.io.*;
import java.util.*;

/**
 * Represents the Clojure evaluator for xOWL ontologies
 *
 * @author Laurent Wouters
 */
public class ClojureEvaluator implements Evaluator {
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
        String result = ClojureEvaluator.class.getCanonicalName() + ".Repository" + Integer.toHexString(REPOSITORY_COUNT);
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
     * Initializes this evaluator
     *
     * @param repository The parent repository
     */
    public ClojureEvaluator(Repository repository) {
        this.repository = repository;
        this.cljNamespace = getNextNamespace();
        this.cljNamespaceRoot = Namespace.findOrCreate(Symbol.intern(cljNamespace));
        this.cljFunctions = new HashMap<>();
        this.cljToBuild = new ArrayList<>();
        this.counter = 0;
    }

    /**
     * Gets the Clojure object for the specified parsed expression
     *
     * @param definition The definition of the Clojure expression
     * @return The Clojure object representing the expression
     */
    public Object loadExpression(ASTNode definition) {
        StringBuilder builder = new StringBuilder();
        serializeClojure(builder, definition);
        return Clojure.read(builder.toString());
    }

    /**
     * Loads the definition of a Clojure function
     *
     * @param iri        The function's global IRI
     * @param definition The function's definition as an AST
     * @return The managing object for the function
     */
    public ClojureFunction loadFunction(String iri, ASTNode definition) {
        if (iri == null)
            iri = newCljName();
        String[] parts = iri.split("#");
        String name = parts[parts.length - 1];
        StringBuilder builder = new StringBuilder();
        serializeClojure(builder, definition);
        ClojureFunction function = new ClojureFunction(iri, name, builder.toString());
        synchronized (cljToBuild) {
            cljFunctions.put(iri, function);
            cljToBuild.add(function);
        }
        return function;
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
     * Retrieves the Clojure function for the specified IRI
     *
     * @param iri The IRI of a function
     * @return The function, or null it is not defined
     */
    public ClojureFunction getFunction(String iri) {
        return cljFunctions.get(iri);
    }

    /**
     * Compiles the outstanding function definitions
     */
    private void compileOutstandings() {
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
                builder.append(function.getContent());
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
            } catch (IOException ex) {
                // do nothing
            }
            cljToBuild.clear();
        }
    }

    /**
     * Loads Clojure code from the specified stream
     *
     * @param stream The input stream to load from
     * @throws IOException When the input cannot be read
     */
    void loadClojure(InputStream stream) throws IOException {
        Compiler.load(new InputStreamReader(stream, IOUtils.CHARSET));
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public Object eval(DynamicExpression expression) {
        if (expression instanceof QueryVariable) {
            EvaluatorContext context = EvaluatorContext.get(this);
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
            EvaluatorContext context = EvaluatorContext.get(this);
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
        return getFunction(function) != null;
    }

    @Override
    public Object execute(String function, Object... parameters) {
        ClojureFunction cljFunction = getFunction(function);
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
        compileOutstandings();
        synchronized (RT.CURRENT_NS) {
            Namespace old = (Namespace) RT.CURRENT_NS.deref();
            RT.CURRENT_NS.bindRoot(cljNamespaceRoot);
            List content = new ArrayList();
            for (Map.Entry<String, Object> binding : EvaluatorContext.get(this).getBindings().entrySet()) {
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
    }

    /**
     * Executes a function
     *
     * @param function   The function
     * @param parameters The parameters
     * @return The returned value
     */
    private Object execute(ClojureFunction function, Object... parameters) {
        EvaluatorContext.get(this);
        compileOutstandings();
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
