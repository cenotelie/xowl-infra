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

package org.xowl.infra.store.rdf;

import org.xowl.infra.store.storage.NodeManager;

/**
 * Represents an entity that can resolves a variable in a RDF rule's consequent
 *
 * @author Laurent Wouters
 */
public abstract class VariableResolver {
    /**
     * Resolves the specified variable
     *
     * @param variable  A variable
     * @param execution The current rule execution
     * @param nodes     The nodes manager to user
     * @param isGraph   Whether the variable is used as a graph   @return The resolved node
     */
    public abstract Node resolve(VariableNode variable, RDFRuleExecution execution, NodeManager nodes, boolean isGraph);
}
