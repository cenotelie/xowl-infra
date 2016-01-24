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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.Repository;

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
    Result execute(Repository repository);
}
