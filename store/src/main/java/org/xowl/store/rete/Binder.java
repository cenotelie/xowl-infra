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

import org.xowl.store.owl.VariableNode;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.QuadField;

/**
 * Represents a binding operation that binds a variable to the field of a triple
 *
 * @author Laurent Wouters
 */
public class Binder {
    /**
     * The variable to bind to
     */
    private VariableNode variable;
    /**
     * The field in a triple that shall be bound
     */
    private QuadField field;

    /**
     * Initializes this element
     *
     * @param variable The variable to bind to
     * @param field    The field in a triple that shall be bound
     */
    public Binder(VariableNode variable, QuadField field) {
        this.variable = variable;
        this.field = field;
    }

    /**
     * Executes the binding operation
     *
     * @param token A token which will contain the binding
     * @param fact  A fact
     */
    public void execute(Token token, Quad fact) {
        token.bind(variable, fact.getField(field));
    }
}
