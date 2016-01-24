/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.infra.engine;

import clojure.lang.Compiler;
import clojure.lang.*;
import org.xowl.infra.lang.actions.DynamicExpression;
import org.xowl.infra.lang.actions.FunctionExpression;
import org.xowl.infra.lang.actions.OpaqueExpression;
import org.xowl.infra.lang.owl2.*;
import org.xowl.infra.lang.runtime.*;
import org.xowl.infra.lang.runtime.Literal;
import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.ProxyObject;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.utils.Files;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * Represents the Clojure evaluator for xOWL ontologies
 *
 * @author Laurent Wouters
 */
public class ClojureEvaluator implements Evaluator {
    /**
     * The root namespace for the Clojure symbols
     */
    private static final String CLJ_NAMESPACE_ROOT_NAME = "org.xowl.engine.clojure";
    /**
     * The root namespace for the Clojure symbols
     */
    private static final Namespace CLJ_NAMESPACE_ROOT = Namespace.findOrCreate(Symbol.intern(CLJ_NAMESPACE_ROOT_NAME));
    /**
     * The Clojure function definitions that are not yet compiled
     */
    private static final List<ClojureFunction> OUTSTANDING_DEFINITIONS = new ArrayList<>();

    /**
     * Registers a function definition
     *
     * @param name       The function's name
     * @param definition The function's content definition as a string
     * @return The managing object for the function
     */
    protected static ClojureFunction register(String name, String definition) {
        ClojureFunction result = new ClojureFunction(name, definition);
        OUTSTANDING_DEFINITIONS.add(result);
        return result;
    }

    /**
     * Compiles the outstanding function definitions
     */
    protected static void compileOutstandings() {
        if (OUTSTANDING_DEFINITIONS.isEmpty())
            return;
        //Var ns = RT.CURRENT_NS; // forces the initialization of the runtime before any call to the compiler
        StringBuilder builder = new StringBuilder();
        builder.append("(ns ");
        builder.append(CLJ_NAMESPACE_ROOT);
        builder.append(")");
        builder.append(Files.LINE_SEPARATOR);
        builder.append("(declare & ");
        for (ClojureFunction function : OUTSTANDING_DEFINITIONS) {
            builder.append(function.getName());
            builder.append(" ");
        }
        builder.append(")");
        builder.append(Files.LINE_SEPARATOR);
        builder.append("[ ");
        for (ClojureFunction function : OUTSTANDING_DEFINITIONS) {
            builder.append(function.getContent());
            builder.append(Files.LINE_SEPARATOR);
        }
        builder.append(" ]");
        Reader reader = new StringReader(builder.toString());
        Iterator iterator = ((Iterable) Compiler.load(reader)).iterator();
        try {
            reader.close();
        } catch (IOException ex) {
            // do nothing
        }
        for (ClojureFunction function : OUTSTANDING_DEFINITIONS) {
            IFn definition = (IFn) iterator.next();
            Var.intern(CLJ_NAMESPACE_ROOT, Symbol.intern(function.getName()), definition);
            function.setClojure(definition);
        }
        OUTSTANDING_DEFINITIONS.clear();
    }

    /**
     * The stack of lexical bindings
     */
    private final Stack<Map<String, Object>> contexts;

    /**
     * Initializes this evaluator
     */
    public ClojureEvaluator() {
        this.contexts = new Stack<>();
    }

    /**
     * Executes a function
     *
     * @param function   The function
     * @param parameters The parameters
     * @return The returned value
     */
    public Object execute(ProxyObject function, Object... parameters) {
        compileOutstandings();
        // retrieve the Clojure expression
        OpaqueExpression expression = (OpaqueExpression) function.getDataValue(Vocabulary.xowlDefinedAs);
        ClojureFunction inner = (ClojureFunction) expression.getValue();
        if (parameters == null || parameters.length == 0)
            return inner.getClojure().invoke();
        switch (parameters.length) {
            case 1:
                return inner.getClojure().invoke(parameters[0]);
            case 2:
                return inner.getClojure().invoke(parameters[0], parameters[1]);
            case 3:
                return inner.getClojure().invoke(parameters[0], parameters[1], parameters[2]);
            case 4:
                return inner.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3]);
            case 5:
                return inner.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4]);
            case 6:
                return inner.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5]);
            case 7:
                return inner.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6]);
            case 8:
                return inner.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7]);
            case 9:
                return inner.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[9]);
            case 10:
                return inner.getClojure().invoke(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[9], parameters[10]);
        }
        return inner.getClojure().invoke(parameters);
    }

    @Override
    public void push(Map<String, Object> context) {
        contexts.push(context);
    }

    @Override
    public void pop() {
        contexts.pop();
    }

    @Override
    public Object eval(DynamicExpression expression) {
        compileOutstandings();
        Namespace old = (Namespace) RT.CURRENT_NS.deref();
        RT.CURRENT_NS.bindRoot(CLJ_NAMESPACE_ROOT);

        List content = new ArrayList();
        HashSet<String> names = new HashSet<>();
        for (int i = 0; i != contexts.size(); i++) {
            for (Map.Entry<String, Object> binding : contexts.get(i).entrySet()) {
                if (!names.contains(binding.getKey()) && !(binding.getValue() instanceof IRI)) {
                    content.add(Symbol.create(binding.getKey()));
                    content.add(binding.getValue());
                    names.add(binding.getKey());
                }
            }
        }
        PersistentVector content1 = PersistentVector.create(content.toArray());
        Object cljExp = ((OpaqueExpression) expression).getValue();
        IPersistentList top = PersistentList.create(Arrays.asList(Symbol.create("clojure.core", "let"), content1, cljExp));
        Object result = Compiler.eval(top);

        RT.CURRENT_NS.bindRoot(old);
        return result;
    }

    @Override
    public org.xowl.infra.lang.runtime.Class evalClass(ClassExpression expression) {
        return null;
    }

    @Override
    public ObjectProperty evalObjectProperty(ObjectPropertyExpression expression) {
        return null;
    }

    @Override
    public DataProperty evalDataProperty(DataPropertyExpression expression) {
        return null;
    }

    @Override
    public Datatype evalDatatype(Datarange expression) {
        return null;
    }

    @Override
    public Individual evalIndividual(IndividualExpression expression) {
        return null;
    }

    @Override
    public Literal evalLiteral(LiteralExpression expression) {
        return null;
    }

    @Override
    public Function evalFunction(FunctionExpression expression) {
        return null;
    }
}
