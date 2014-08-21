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

import org.xowl.lang.actions.QueryVariable;
import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Literal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a node associated to a variable in a RDF graph
 *
 * @author Laurent Wouters
 */
public class XOWLVariableNode implements RDFSubjectNode, RDFProperty {
    /**
     * The variable associated to this node
     */
    private QueryVariable variable;
    /**
     * The possible types for the variable
     */
    private List<Class> types;

    /**
     * Initializes this node
     *
     * @param var The variable represented by this node
     */
    XOWLVariableNode(QueryVariable var) {
        variable = var;
        types = new ArrayList<>();
    }

    /**
     * Gets the variable represented by this node
     *
     * @return The variable represented by this node
     */
    public QueryVariable getVariable() {
        return variable;
    }

    /**
     * Get the possible types for the variable represented by this node
     *
     * @return The possible types for the variable
     */
    public Collection<Class> getTypes() {
        return types;
    }

    /**
     * Adds a new possible type for the variable represented by this node
     *
     * @param type A possible type for the variable
     */
    public void addType(Class type) {
        if (!types.contains(type))
            types.add(type);
    }

    @Override
    public RDFNodeType getNodeType() {
        return RDFNodeType.VARIABLE;
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
        int hash = 7;
        hash = 97 * hash + (this.variable != null ? this.variable.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XOWLVariableNode))
            return false;
        XOWLVariableNode o = (XOWLVariableNode) obj;
        return (o.variable.getName().equals(this.variable.getName()));
    }

    @Override
    public String toString() {
        return "?" + variable.getName();
    }
}
