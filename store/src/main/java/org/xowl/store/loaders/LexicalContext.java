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
package org.xowl.store.loaders;

import org.xowl.lang.actions.QueryVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a lexical context for a loader
 *
 * @author Laurent Wouters
 */
public class LexicalContext {
    /**
     * The parent context
     */
    private final LexicalContext parent;
    /**
     * The query variables
     */
    private final Map<String, QueryVariable> queryVariables;

    /**
     * Initializes this context
     *
     * @param parent The parent context
     */
    public LexicalContext(LexicalContext parent) {
        this.parent = parent;
        this.queryVariables = new HashMap<>();
    }

    /**
     * Initializes this context
     */
    public LexicalContext() {
        this(null);
    }

    /**
     * Resolves a query variable with the specified name
     *
     * @param name The name of a variable
     * @return The variable
     */
    public QueryVariable resolveQVar(String name) {
        LexicalContext current = this;
        while (current != null) {
            QueryVariable var = current.queryVariables.get(name);
            if (var != null)
                return var;
            current = current.parent;
        }
        // not found in the parents
        QueryVariable variable = new QueryVariable();
        variable.setName(name);
        queryVariables.put(name, variable);
        return variable;
    }
}
