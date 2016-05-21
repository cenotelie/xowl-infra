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

package org.xowl.infra.store.rdf;

/**
 * Represents a match of a RDF pattern
 *
 * @author Laurent Wouters
 */
public interface RDFPatternMatch {
    /**
     * Gets whether this is equivalent to another match
     *
     * @param match Another match
     * @return Whether the match is equivalent
     */
    boolean sameAs(RDFPatternMatch match);

    /**
     * Gets the value bound to the specified variable in this token
     *
     * @param variable A variable
     * @return The value bound to the variable, or null if none is
     */
    Node getBinding(VariableNode variable);

    /**
     * Gets the associated solution
     *
     * @return The solution for the match
     */
    RDFPatternSolution getSolution();
}
