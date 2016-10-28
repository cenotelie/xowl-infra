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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.rdf.Node;

import java.util.Map;

/**
 * Represents SPARQL command
 *
 * @author Laurent Wouters
 */
public interface Command {
    /**
     * The MIME content-type for a SPARQL query
     */
    String MIME_SPARQL_QUERY = "application/sparql-query";
    /**
     * The MIME content type for a SPARQL update
     */
    String MIME_SPARQL_UPDATE = "application/sparql-update";

    /**
     * Executes this command on the specified repository
     *
     * @param repository The repository on which to execute the command
     * @return The command's result
     */
    Result execute(RepositoryRDF repository);

    /**
     * Gets a copy of this command
     *
     * @param parameters The parameters to be replaced during the clone
     * @return A copy of this command
     */
    Command clone(Map<String, Node> parameters);
}
