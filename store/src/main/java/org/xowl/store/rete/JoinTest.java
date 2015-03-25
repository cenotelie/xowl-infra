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

/**
 * Represents a test for the joining operations in the beta graph of a RETE network
 *
 * @author Laurent Wouters
 */
abstract class JoinTest {
    /**
     * Gets whether this test can be used to build the indices
     *
     * @return Whether this test can be used to build the indices
     */
    public abstract boolean useInIndex();

    /**
     * Executes this test
     *
     * @param token The token to test
     * @param fact  The fact to test
     * @return true if the test succeeded
     */
    public abstract boolean check(Token token, Quad fact);

    /**
     * Gets index value of the specified token for this test
     *
     * @param token A token
     * @return The corresponding index value
     */
    public abstract Node getIndex(Token token);

    /**
     * Gets index value of the specified fact for this test
     *
     * @param fact A fact
     * @return The corresponding index value
     */
    public abstract Node getIndex(Quad fact);
}
