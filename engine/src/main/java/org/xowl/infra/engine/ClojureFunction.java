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

package org.xowl.infra.engine;

import clojure.lang.IFn;

/**
 * Represents the definition of a Clojure function
 *
 * @author Laurent Wouters
 */
class ClojureFunction {
    /**
     * The function's name
     */
    private final String name;
    /**
     * The function's content definition as a string
     */
    private String content;
    /**
     * The function's compiled definition
     */
    private IFn clojure;

    /**
     * Gets the function's name
     *
     * @return The function's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the function's content definition as a string
     *
     * @return The function's content definition as a string
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the function's compiled definition
     *
     * @return The function's compiled definition
     */
    public IFn getClojure() {
        return clojure;
    }

    /**
     * Sets the function's compiled definition
     *
     * @param clojure The function's compiled definition
     */
    public void setClojure(IFn clojure) {
        this.clojure = clojure;
        this.content = null;
    }

    /**
     * Initializes this function
     *
     * @param name    The function's name
     * @param content The function's content definition as a string
     */
    public ClojureFunction(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
