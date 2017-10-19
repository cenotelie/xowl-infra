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

package org.xowl.infra.engine;

import clojure.lang.IFn;
import fr.cenotelie.commons.utils.TextUtils;
import org.xowl.infra.store.execution.ExecutableFunction;

/**
 * Represents the definition of a Clojure function
 *
 * @author Laurent Wouters
 */
class ClojureFunction implements ExecutableFunction {
    /**
     * The function's global IRI
     */
    private final String iri;
    /**
     * The function's short name
     */
    private final String name;
    /**
     * The function's content definition as a string
     */
    private final String source;
    /**
     * The function's compiled definition
     */
    private IFn clojure;

    @Override
    public String getIdentifier() {
        return iri;
    }

    /**
     * Gets the function's short name
     *
     * @return The function's short name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the function's source definition as a string
     *
     * @return The function's source definition as a string
     */
    public String getSource() {
        return source;
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
    }

    /**
     * Initializes this function
     *
     * @param iri    The function's global IRI
     * @param name   The function's short name
     * @param source The function's content definition as a string
     */
    public ClojureFunction(String iri, String name, String source) {
        this.iri = iri;
        this.name = name;
        this.source = source;
    }

    @Override
    public String serializedString() {
        return source;
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(ExecutableFunction.class.getCanonicalName()) +
                "\", \"identifier\": \"" +
                TextUtils.escapeStringJSON(iri) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(name) +
                "\", \"source\": \"" +
                TextUtils.escapeStringJSON(source) +
                "\"}";
    }
}
