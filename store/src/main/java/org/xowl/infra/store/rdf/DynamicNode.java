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

/**
 * Represents a node associated to a dynamic xOWL expression in a RDF store
 *
 * @author Laurent Wouters
 */
public class DynamicNode implements SubjectNode, Property, GraphNode {
    /**
     * The represented evaluable expression
     */
    private final EvaluableExpression evaluable;

    /**
     * Initializes this node
     *
     * @param evaluable The represented evaluable expression
     */
    public DynamicNode(EvaluableExpression evaluable) {
        this.evaluable = evaluable;
    }

    /**
     * Gets the dynamic expression associated to this node
     *
     * @return The dynamic expression associated to this node
     */
    public EvaluableExpression getEvaluable() {
        return evaluable;
    }

    @Override
    public int getNodeType() {
        return TYPE_DYNAMIC;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.evaluable != null ? this.evaluable.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DynamicNode))
            return false;
        DynamicNode o = (DynamicNode) obj;
        return (o.evaluable == this.evaluable);
    }

    @Override
    public String toString() {
        return "<" + evaluable.serializedString() + ">";
    }
}
