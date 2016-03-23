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

package org.xowl.infra.store.rdf;

/**
 * Represents a variable in a quad pattern
 *
 * @author Laurent Wouters
 */
public class VariableNode implements SubjectNode, Property, GraphNode {
    /**
     * The variable's name
     */
    private final String name;

    /**
     * Initializes this node
     *
     * @param name The variable's name
     */
    public VariableNode(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the variable represented by this node
     *
     * @return The variable's name
     */
    public String getName() {
        return name;
    }

    @Override
    public int getNodeType() {
        return TYPE_VARIABLE;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof VariableNode && name.equals(((VariableNode) obj).name));
    }

    @Override
    public String toString() {
        return "?" + name;
    }
}
