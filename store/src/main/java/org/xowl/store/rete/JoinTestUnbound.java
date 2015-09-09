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
package org.xowl.store.rete;

import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.QuadField;
import org.xowl.store.rdf.Utils;

/**
 * Represents a test on an unbound variable for the joining operations in the beta graph of a RETE network
 *
 * @author Laurent Wouters
 */
class JoinTestUnbound extends JoinTest {
    /**
     * The reference field to test against
     */
    private final QuadField reference;
    /**
     * The field to be tested
     */
    private final QuadField tested;

    /**
     * Initializes this test
     *
     * @param reference The reference field to test against
     * @param tested    The field to be tested
     */
    public JoinTestUnbound(QuadField reference, QuadField tested) {
        this.reference = reference;
        this.tested = tested;
    }

    @Override
    public boolean useInIndex() {
        return false;
    }

    @Override
    public boolean check(Token token, Quad fact) {
        Node arg1 = fact.getField(reference);
        Node arg2 = fact.getField(tested);
        return Utils.same(arg1, arg2);
    }

    @Override
    public Node getIndex(Token token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getIndex(Quad fact) {
        throw new UnsupportedOperationException();
    }
}
