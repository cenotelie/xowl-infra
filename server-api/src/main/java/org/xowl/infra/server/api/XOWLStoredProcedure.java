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

package org.xowl.infra.server.api;

import org.xowl.infra.utils.Serializable;

import java.util.Collection;

/**
 * Represents a stored procedure in a database
 * A stored procedure is a set of parametric SPARQL commands
 *
 * @author Laurent Wouters
 */
public interface XOWLStoredProcedure extends Serializable {
    /**
     * Gets the name (IRI) of this procedure
     *
     * @return The name of this procedure
     */
    String getName();

    /**
     * Gets the definition of this procedure
     *
     * @return The definition of this procedure
     */
    String getDefinition();

    /**
     * Gets the parameters for the execution
     *
     * @return The parameters for the execution
     */
    Collection<String> getParameters();
}
