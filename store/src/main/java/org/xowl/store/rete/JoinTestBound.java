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

import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.QuadField;
import org.xowl.store.rdf.VariableNode;

/**
 * Represents a test on a bound variable for the joining operations in the beta graph of a RETE network
 *
 * @author Laurent Wouters
 */
class JoinTestBound extends JoinTest {
    /**
     * The variable to test
     */
    private VariableNode variable;
    /**
     * The field of a triple to test
     */
    private QuadField field;

    /**
     * Initializes this test
     *
     * @param variable The tested variable
     * @param field    The tested field
     */
    public JoinTestBound(VariableNode variable, QuadField field) {
        this.variable = variable;
        this.field = field;
    }

    @Override
    public boolean useInIndex() {
        return true;
    }

    @Override
    public boolean check(Token token, Quad fact) {
        Node arg1 = token.getBinding(variable);
        Node arg2 = fact.getField(field);
        return (arg1.getNodeType() == arg2.getNodeType() && arg1.equals(arg2));
    }

    @Override
    public Node getIndex(Token token) {
        return token.getBinding(variable);
    }

    @Override
    public Node getIndex(Quad fact) {
        return fact.getField(field);
    }
}
