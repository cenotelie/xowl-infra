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

import clojure.java.api.Clojure;
import clojure.lang.Compiler;
import clojure.lang.*;
import org.xowl.hime.redist.ASTNode;
import org.xowl.infra.store.loaders.XOWLLexer;
import org.xowl.infra.store.loaders.XOWLParser;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.Logging;

import java.io.*;
import java.util.*;

/**
 * The global manager of Clojure code
 *
 * @author Laurent Wouters
 */
public class ClojureManager {
    /**
     * The root namespace for the Clojure symbols
     */
    private static final String NAMESPACE_ROOT_NAME = ClojureManager.class.getPackage().getName();
    /**
     * The root namespace for the Clojure symbols
     */
    public static final Namespace NAMESPACE_ROOT = Namespace.findOrCreate(Symbol.intern(NAMESPACE_ROOT_NAME));
    /**
     * The resource for the Clojure bindings
     */
    private static final String BINDINGS_RESOURCE = "/org/xowl/infra/engine/bindings.clj";
    /**
     * Whether the bindings are loaded
     */
    private static boolean BINDINGS_LOADED = false;
    /**
     * The map of the known Clojure functions
     */
    private static final Map<String, ClojureFunction> FUNCTIONS = new HashMap<>();
    /**
     * The Clojure function definitions that are not yet compiled
     */
    private static final List<ClojureFunction> FUNCTIONS_TO_COMPILE = new ArrayList<>();
    /**
     * The counter for anonymous functions
     */
    private static int COUNTER = 0;

    /**
     * Generates a new Clojure name for an anonymous function
     *
     * @return A new Clojure name
     */
    private static String newCljName() {
        String name = "http://xowl.org/infra/engine/clojure#Function" + COUNTER;
        COUNTER++;
        return name;
    }

    /**
     * Gets the Clojure object for the specified parsed expression
     *
     * @param definition The definition of the Clojure expression
     * @return The Clojure object representing the expression
     */
    public static Object loadExpression(ASTNode definition) {
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
    public static ClojureFunction loadFunction(String iri, ASTNode definition) {
        if (iri == null)
            iri = newCljName();
        String[] parts = iri.split("#");
        String name = parts[parts.length - 1];
        StringBuilder builder = new StringBuilder();
        serializeClojure(builder, definition);
        ClojureFunction function = new ClojureFunction(iri, name, builder.toString());
        synchronized (FUNCTIONS_TO_COMPILE) {
            FUNCTIONS.put(iri, function);
            FUNCTIONS_TO_COMPILE.add(function);
        }
        return function;
    }

    /**
     * Retrieves the Clojure function for the specified IRI
     *
     * @param iri The IRI of a function
     * @return The function, or null it is not defined
     */
    public static ClojureFunction getFunction(String iri) {
        return FUNCTIONS.get(iri);
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

    /**
     * Compiles the outstanding function definitions
     */
    public static void compileOutstandings() {
        synchronized (FUNCTIONS_TO_COMPILE) {
            if (!BINDINGS_LOADED)
                loadBindings();
            if (FUNCTIONS_TO_COMPILE.isEmpty())
                return;
            //Var ns = RT.CURRENT_NS; // forces the initialization of the runtime before any call to the compiler
            StringBuilder builder = new StringBuilder();
            builder.append("(ns ");
            builder.append(NAMESPACE_ROOT);
            builder.append(")");
            builder.append(IOUtils.LINE_SEPARATOR);
            builder.append("(declare & ");
            for (ClojureFunction function : FUNCTIONS_TO_COMPILE) {
                builder.append(function.getName());
                builder.append(" ");
            }
            builder.append(")");
            builder.append(IOUtils.LINE_SEPARATOR);
            builder.append("[ ");
            for (ClojureFunction function : FUNCTIONS_TO_COMPILE) {
                builder.append(function.getContent());
                builder.append(IOUtils.LINE_SEPARATOR);
            }
            builder.append(" ]");
            try (Reader reader = new StringReader(builder.toString())) {
                Iterator iterator = ((Iterable) Compiler.load(reader)).iterator();
                for (ClojureFunction function : FUNCTIONS_TO_COMPILE) {
                    IFn definition = (IFn) iterator.next();
                    Var.intern(NAMESPACE_ROOT, Symbol.intern(function.getName()), definition);
                    function.setClojure(definition);
                }
            } catch (IOException ex) {
                // do nothing
            }
            FUNCTIONS_TO_COMPILE.clear();
        }
    }

    /**
     * Loads the Clojure bindings
     */
    private static void loadBindings() {
        try (InputStream stream = ClojureManager.class.getResourceAsStream(BINDINGS_RESOURCE)) {
            loadClojure(stream);
        } catch (IOException exception) {
            Logging.get().error(exception);
        }
    }

    /**
     * Loads Clojure code from the specified stream
     *
     * @param stream The input stream to load from
     * @throws IOException When the input cannot be read
     */
    static void loadClojure(InputStream stream) throws IOException {
        Compiler.load(new InputStreamReader(stream, IOUtils.CHARSET));
        BINDINGS_LOADED = true;
    }
}
