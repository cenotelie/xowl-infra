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

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.xowl.lang.actions.OpaqueExpression;
import org.xowl.store.ProxyObject;
import org.xowl.store.Repository;
import org.xowl.store.Vocabulary;
import org.xowl.utils.Logger;

/**
 * Represents an execution engine for xOWL ontologies
 *
 * @author Laurent Wouters
 */
public class Engine {
    /**
     * The engine's logger
     */
    private final Logger logger;
    /**
     * The backend repository
     */
    private final Repository repository;

    /**
     * Gets the backend repository
     *
     * @return The backend repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Initializes this engine
     *
     * @param logger     The engine's logger
     * @param repository The backend repository
     */
    public Engine(Logger logger, Repository repository) {
        this.logger = logger;
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
        // retrieve the Clojure expression
        OpaqueExpression expression = (OpaqueExpression) function.getDataValue(Vocabulary.xowlDefinedAs);
        Object clj = expression.getValue();
        if (!(clj instanceof IFn)) {
            // first evaluate the expression
            IFn eval = Clojure.var("clojure.core", "eval");
            clj = eval.invoke(clj);
        }
        if (clj instanceof IFn) {
            // this is a function, call it
            if (parameters == null || parameters.length == 0)
                return ((IFn) clj).invoke();
            return ((IFn) clj).invoke(parameters);
        } else {
            // still not a function, log this
            logger.error("Opaque definition of function " + function.getIRIString() + " cannot be executed");
            return null;
        }
    }
}
