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

package org.xowl.store.rdf;

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.lang.owl2.Expression;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Literal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a node associated to a dynamic xOWL expression in a RDF graph
 *
 * @author Laurent Wouters
 */
public class XOWLDynamicNode implements RDFSubjectNode, RDFProperty {
    /**
     * The associated dynamic expression
     */
    private Expression dynExpression;
    /**
     * The possible types for the expression
     */
    private List<Class> types;

    /**
     * Initializes this node
     *
     * @param exp The dynamic expression represented by this node
     */
    XOWLDynamicNode(Expression exp) {
        dynExpression = exp;
        types = new ArrayList<>();
    }

    /**
     * Gets the dynamic expression associated to this node
     *
     * @return The dynamic expression associated to this node
     */
    public Expression getDynamicExpression() {
        return dynExpression;
    }

    /**
     * Gets the possible types for the expression represented by this node
     *
     * @return The possible types for the expression represented by this node
     */
    public Collection<Class> getTypes() {
        return types;
    }

    /**
     * Adds a possible type for the dynamic expression represented by this node
     *
     * @param type A new possible type for the expression
     */
    public void addType(Class type) {
        if (!types.contains(type))
            types.add(type);
    }

    @Override
    public RDFNodeType getNodeType() {
        return RDFNodeType.DYNAMIC;
    }

    @Override
    public IRI getIRI() {
        return null;
    }

    @Override
    public Literal getLiteralValue() {
        return null;
    }

    @Override
    public int getBlankID() {
        return 0;
    }

    @Override
    public AnonymousIndividual getAnonymous() {
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.dynExpression != null ? this.dynExpression.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XOWLDynamicNode))
            return false;
        XOWLDynamicNode o = (XOWLDynamicNode) obj;
        return (o.dynExpression == this.dynExpression);
    }

    @Override
    public String toString() {
        return "<dyn>";
    }
}
