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

package org.xowl.store.owl;

import org.xowl.lang.actions.QueryVariable;
import org.xowl.store.rdf.VariableNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the context of a translation
 *
 * @author Laurent Wouters
 */
public class TranslationContext {
    /**
     * Map associating input query variables to RDF variable nodes
     */
    private final Map<QueryVariable, VariableNode> mapVariables;
    /**
     * The inverse map
     */
    private final Map<VariableNode, QueryVariable> mapInverse;

    /**
     * Initializes this context
     */
    public TranslationContext() {
        this.mapVariables = new HashMap<>();
        this.mapInverse = new HashMap<>();
    }

    /**
     * Resolves the RDF variable node associated to the specified query variable with the specified type
     *
     * @param variable A query variable
     * @param type     The expected type of the variable
     * @return The associated RDF variable node
     */
    public VariableNode resolve(QueryVariable variable, Class type) {
        VariableNode node = mapVariables.get(variable);
        if (node == null) {
            node = new VariableNode(variable.getName());
            mapVariables.put(variable, node);
            mapInverse.put(node, variable);
        }
        return node;
    }

    /**
     * Gets the query variable associated to the specified variable node
     *
     * @param node A variable node
     * @return The associated query variable
     */
    public QueryVariable get(VariableNode node) {
        return mapInverse.get(node);
    }
}
