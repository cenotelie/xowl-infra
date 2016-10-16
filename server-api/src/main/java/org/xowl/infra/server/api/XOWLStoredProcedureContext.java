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

import org.xowl.infra.store.Serializable;
import org.xowl.infra.store.rdf.Node;

import java.util.List;
import java.util.Map;

/**
 * Represents the execution context of a stored procedure
 *
 * @author Laurent Wouters
 */
public interface XOWLStoredProcedureContext extends Serializable {
    /**
     * Gets the context's default IRIs
     *
     * @return The context's default IRIs
     */
    List<String> getDefaultIRIs();

    /**
     * Gets the context's named IRIs
     *
     * @return The context's named IRIs
     */
    List<String> getNamedIRIs();

    /**
     * Gets the parameters for the execution
     *
     * @return The parameters for the execution
     */
    Map<String, Node> getParameters();
}
