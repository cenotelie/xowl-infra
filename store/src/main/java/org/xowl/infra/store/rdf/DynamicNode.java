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

package org.xowl.infra.store.rdf;

import org.xowl.infra.store.execution.EvaluableExpression;

import java.util.Objects;

/**
 * Represents a node associated to a dynamic xOWL expression in a RDF store
 *
 * @author Laurent Wouters
 */
public abstract class DynamicNode implements SubjectNode, Property, GraphNode {
    @Override
    public int getNodeType() {
        return TYPE_DYNAMIC;
    }

    /**
     * Gets the dynamic expression associated to this node
     *
     * @return The dynamic expression associated to this node
     */
    public abstract EvaluableExpression getEvaluable();

    @Override
    public int hashCode() {
        return getEvaluable().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DynamicNode && Objects.equals(getEvaluable(), ((DynamicNode) obj).getEvaluable());
    }

    @Override
    public String toString() {
        return "<dyn>";
    }
}
