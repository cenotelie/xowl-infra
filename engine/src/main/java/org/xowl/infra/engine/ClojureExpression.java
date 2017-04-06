/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.engine;

/**
 * Represents a Clojure expression to be evaluated
 *
 * @author Laurent Wouters
 */
public class ClojureExpression {
    /**
     * The expression's compiled definition
     */
    private Object definition;

    /**
     * Gets the expression's compiled definition
     *
     * @return The expression's compiled definition
     */
    public Object getDefinition() {
        return definition;
    }

    /**
     * Initializes this function
     *
     * @param definition The expression's compiled definition
     */
    public ClojureExpression(Object definition) {
        this.definition = definition;
    }
}
