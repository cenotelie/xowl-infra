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

package org.xowl.infra.store.rete;

import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.QuadField;
import org.xowl.infra.store.rdf.VariableNode;

/**
 * Represents a binding operation that binds a variable to the field of a triple
 *
 * @author Laurent Wouters
 */
class Binder {
    /**
     * The variable to bind to
     */
    private final VariableNode variable;
    /**
     * The field in a triple that shall be bound
     */
    private final QuadField field;

    /**
     * Gets the variable to bind
     *
     * @return The variable to bind
     */
    public VariableNode getVariable() {
        return variable;
    }

    /**
     * Gets the field to bind
     *
     * @return The field to bind
     */
    public QuadField getField() {
        return field;
    }

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
