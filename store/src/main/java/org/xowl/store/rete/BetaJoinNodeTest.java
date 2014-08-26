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
import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.Triple;
import org.xowl.store.rdf.TripleField;

/**
 * Represents a test for the joining operations in the beta graph of a RETE network
 *
 * @author Laurent Wouters
 */
public class BetaJoinNodeTest {
    /**
     * The variable to test
     */
    private VariableNode variable;
    /**
     * The field of a triple to test
     */
    private TripleField field;

    /**
     * Initializes this test
     *
     * @param var   The tested variable
     * @param field The tested field
     */
    public BetaJoinNodeTest(VariableNode var, TripleField field) {
        this.variable = var;
        this.field = field;
    }

    /**
     * Gets the tested variable
     *
     * @return The tested variable
     */
    public VariableNode getVariable() {
        return variable;
    }

    /**
     * Gets the tested field
     *
     * @return The tested field
     */
    public TripleField getField() {
        return field;
    }

    /**
     * Executes this test
     *
     * @param token The token to test
     * @param fact  The fact to test
     * @return true if the test succeeded
     */
    public boolean check(Token token, Triple fact) {
        Node arg1 = token.getBinding(variable);
        Node arg2 = fact.getField(field);
        return (arg1 == arg2);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.variable != null ? this.variable.hashCode() : 0);
        hash = 37 * hash + (this.field != null ? this.field.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BetaJoinNodeTest))
            return false;
        BetaJoinNodeTest test = (BetaJoinNodeTest) obj;
        if (this.variable != test.variable)
            return false;
        return (this.field == test.field);
    }
}
