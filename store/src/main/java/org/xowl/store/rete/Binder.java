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

package org.xowl.store.rete;

import org.xowl.store.rdf.RDFTripleField;
import org.xowl.store.rdf.XOWLTriple;
import org.xowl.store.rdf.XOWLVariableNode;

/**
 * Represents a binding operation that binds a variable to the field of a triple
 *
 * @author Laurent Wouters
 */
public class Binder {
    /**
     * The variable to bind to
     */
    private XOWLVariableNode variable;
    /**
     * The field in a triple that shall be bound
     */
    private RDFTripleField field;

    /**
     * Initializes this element
     *
     * @param variable The variable to bind to
     * @param field    The field in a triple that shall be bound
     */
    public Binder(XOWLVariableNode variable, RDFTripleField field) {
        this.variable = variable;
        this.field = field;
    }

    /**
     * Executes the binding operation
     *
     * @param token A token which will contain the binding
     * @param fact  A fact
     */
    public void execute(Token token, XOWLTriple fact) {
        token.bind(variable, fact.getField(field));
    }
}
