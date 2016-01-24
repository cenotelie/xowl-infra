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

import org.xowl.infra.lang.actions.QueryVariable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of bindings (variables bound to values)
 *
 * @author Laurent Wouters
 */
public class Bindings {
    /**
     * The content of the bindings
     */
    private final Map<QueryVariable, Object> content;

    /**
     * Initializes the bindings
     */
    Bindings() {
        this.content = new HashMap<>();
    }

    /**
     * Gets the number of bindings
     *
     * @return The number of bindings
     */
    public int size() {
        return content.size();
    }

    /**
     * Gets the list of bound variables
     *
     * @return The list of bound variables
     */
    public Collection<QueryVariable> getVariables() {
        return content.keySet();
    }

    /**
     * Retrieves the value associated to the specified variable
     *
     * @param variable A variable
     * @return The value associated to the specified variable
     */
    public Object get(QueryVariable variable) {
        return content.get(variable);
    }

    /**
     * Retrieves the value associated to the specified variable
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

    /**
     * Binds a variable
     *
     * @param variable The variable to bind
     * @param value    The associated value
     */
    void bind(QueryVariable variable, Object value) {
        content.put(variable, value);
    }
}
