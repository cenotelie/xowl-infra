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

package org.xowl.infra.store.execution;

import org.xowl.infra.store.Repository;
import org.xowl.infra.store.loaders.XOWLDeserializer;

/**
 * Manages the execution of xOWL ontologies for a repository
 * Provides the implementation for execution extensions
 *
 * @author Laurent Wouters
 */
public interface ExecutionManager extends Evaluator {
    /**
     * Gets the parent repository
     *
     * @return The parent repository
     */
    Repository getRepository();

    /**
     * Gets the deserializer of xOWL ontologies
     *
     * @return The deserializer of xOWL ontologies
     */
    XOWLDeserializer getDeserializer();

    /**
     * Loads an evaluable expression from the specified source
     *
     * @param source The source of an evaluable expression
     * @return The expression
     */
    EvaluableExpression loadExpression(String source);

    /**
     * Registers a xOWL function
     *
     * @param functionIRI The IRI of the function
     * @param definition  The function's definition
     */
    void registerFunction(String functionIRI, EvaluableExpression definition);

    /**
     * Unregisters a xOWL function
     *
     * @param functionIRI The IRI of the function
     */
    void unregisterFunction(String functionIRI);
}
