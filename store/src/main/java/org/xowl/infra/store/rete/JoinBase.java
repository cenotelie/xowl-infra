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

/**
 * Represents the basic infrastructure for the realisation of join tests
 *
 * @author Laurent Wouters
 */
class JoinBase {
    /**
     * The first test
     */
    protected final JoinTest test1;
    /**
     * The second test
     */
    protected final JoinTest test2;
    /**
     * The third test
     */
    protected final JoinTest test3;
    /**
     * The fourth test
     */
    protected final JoinTest test4;

    /**
     * Initializes this element
     *
     * @param tests The tests to use (array of size 4)
     */
    public JoinBase(JoinTest... tests) {
        this.test1 = tests[0];
        this.test2 = tests[1];
        this.test3 = tests[2];
        this.test4 = tests[3];
    }

    /**
     * Determines whether the specified couple passes the tests
     *
     * @param token A token
     * @param fact  A fact
     * @return true if the couple passes the tests
     */
    protected boolean passTests(Token token, Quad fact) {
        return ((test1 == null || test1.check(token, fact))
                && (test2 == null || test2.check(token, fact))
                && (test3 == null || test3.check(token, fact))
                && (test4 == null || test4.check(token, fact)));
    }
}
