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

package org.xowl.engine;

import clojure.lang.Compiler;
import clojure.lang.*;
import org.xowl.lang.actions.FunctionExpression;
import org.xowl.lang.actions.OpaqueExpression;
import org.xowl.lang.owl2.*;
import org.xowl.lang.runtime.*;
import org.xowl.lang.runtime.Literal;
import org.xowl.store.ProxyObject;
import org.xowl.store.Repository;
import org.xowl.store.Vocabulary;
import org.xowl.store.owl.Evaluator;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an execution engine for xOWL ontologies
 *
 * @author Laurent Wouters
 */
public class Engine implements Evaluator {
    /**
     * The root namespace for the Clojure functions
     */
    protected static final String CLJ_NAMESPACE_ROOT = "org.xowl.engine.clojure";

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
        StringBuilder builder = new StringBuilder();
        builder.append("(ns ");
        builder.append(CLJ_NAMESPACE_ROOT);
        builder.append(")");
        builder.append(System.lineSeparator());
        builder.append("(declare & ");
        for (ClojureFunction function : OUTSTANDING_DEFINITIONS) {
            builder.append(function.getName());
            builder.append(" ");
        }
        builder.append(")");
        builder.append(System.lineSeparator());
        builder.append("[ ");
        for (ClojureFunction function : OUTSTANDING_DEFINITIONS) {
            builder.append(function.getContent());
            builder.append(System.lineSeparator());
        }
        builder.append(" ]");
        Reader reader = new StringReader(builder.toString());
        PersistentVector vector = (PersistentVector) Compiler.load(reader);
        try {
            reader.close();
        } catch (IOException ex) {
            // do nothing
        }
        Iterator iterator = vector.iterator();
        for (ClojureFunction function : OUTSTANDING_DEFINITIONS) {
            IFn definition = (IFn) iterator.next();
            Var.intern(Namespace.findOrCreate(Symbol.intern(CLJ_NAMESPACE_ROOT)), Symbol.intern(function.getName()), definition);
            function.setClojure(definition);
        }
        OUTSTANDING_DEFINITIONS.clear();
    }

    /**
     * The backend repository
     */
    private final Repository repository;

    /**
     * Initializes this engine
     *
     * @param repository The backend repository
     */
    public Engine(Repository repository) {
        this.repository = repository;
    }

    /**
     * Executes a function
     *
     * @param functionIRI The IRI of a function
     * @param parameters  The parameters
     * @return The returned value
     */
    public Object execute(String functionIRI, Object... parameters) {
        return execute(repository.getProxy(functionIRI), parameters);
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
    public boolean can(Expression expression) {
        return false;
    }

    @Override
    public Object eval(Expression expression) {
        return null;
    }

    @Override
    public org.xowl.lang.runtime.Class evalClass(ClassExpression expression) {
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
