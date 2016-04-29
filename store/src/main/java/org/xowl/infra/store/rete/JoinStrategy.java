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

import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a join strategy in a RETE network
 *
 * @author Laurent Wouters
 */
abstract class JoinStrategy extends JoinBase {
    /**
     * Initializes this strategy
     *
     * @param test1 The first test
     * @param test2 The second test
     * @param test3 The third test
     * @param test4 The fourth test
     */
    public JoinStrategy(JoinTest test1, JoinTest test2, JoinTest test3, JoinTest test4) {
        super(test1, test2, test3, test4);
    }

    /**
     * Joins the token and fact collections
     *
     * @param tokens A collection of tokens
     * @param facts  A collection of facts
     * @return The result
     */
    public abstract Iterator<Couple> join(Collection<Token> tokens, Collection<Quad> facts);
}
