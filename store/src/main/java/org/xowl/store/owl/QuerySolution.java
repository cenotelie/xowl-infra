/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.store.owl;

import org.xowl.lang.actions.QueryVariable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a solution to an OWL query
 *
 * @author Laurent Wouters
 */
public class QuerySolution {
    /**
     * The content of this solution
     */
    private final Map<QueryVariable, Object> content;

    /**
     * Initializes this solution
     *
     * @param bindings The bindings represented by this solution
     */
    QuerySolution(Map<QueryVariable, Object> bindings) {
        this.content = new HashMap<>(bindings);
    }

    /**
     * Gets the size of this solution, i.e. the number of bindings it represents
     *
     * @return The size of this solution
     */
    public int size() {
        return content.size();
    }

    /**
     * Gets the list of the matched variables in this solution
     *
     * @return The list if the matched variables in this solution
     */
    public Collection<QueryVariable> getVariables() {
        return content.keySet();
    }

    /**
     * Retrieves the value associated to the specified variable in this solution
     *
     * @param variable A variable
     * @return The value associated to the specified variable
     */
    public Object get(QueryVariable variable) {
        return content.get(variable);
    }

    /**
     * Retrieves the value associated to the specified variable in this solution
     *
     * @param variable A variable
     * @return The value associated to the specified variable
     */
    public Object get(String variable) {
        for (QueryVariable var : content.keySet()) {
            if (var.getName().equals(variable))
                return content.get(var);
        }
        return null;
    }
}
