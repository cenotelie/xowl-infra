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

import org.xowl.infra.store.EvaluatorContext;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.loaders.SPARQLLoader;
import org.xowl.infra.store.sparql.Command;
import org.xowl.infra.store.sparql.Result;
import org.xowl.infra.store.sparql.ResultFailure;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.logging.BufferedLogger;

import java.io.StringReader;

/**
 * Provides a convenient API for accessing data from Clojure
 *
 * @author Laurent Wouters
 */
class ClojureAPI {
    /**
     * Executes a SPARQL query against the current repository
     *
     * @param query The SPARQL query to execute
     * @return The result
     */
    public static Result sparql(String query) {
        EvaluatorContext context = EvaluatorContext.get(null);
        if (context == null)
            return new ResultFailure("No current evaluation context");
        if (!(context.getRepository() instanceof RepositoryRDF))
            return new ResultFailure("The current repository is not a RDF repository");

        NodeManager nodeManager = ((RepositoryRDF) context.getRepository()).getStore();
        BufferedLogger logger = new BufferedLogger();
        SPARQLLoader loader = new SPARQLLoader(nodeManager);
        Command command = loader.load(logger, new StringReader(query));
        if (command == null)
            return new ResultFailure(logger.getErrorsAsString());
        if (!logger.getErrorMessages().isEmpty())
            return new ResultFailure(logger.getErrorsAsString());
        return command.execute(((RepositoryRDF) context.getRepository()));
    }
}
