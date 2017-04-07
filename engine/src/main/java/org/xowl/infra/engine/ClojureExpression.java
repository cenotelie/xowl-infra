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

import clojure.java.api.Clojure;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.utils.TextUtils;

/**
 * Represents a Clojure expression to be evaluated
 *
 * @author Laurent Wouters
 */
class ClojureExpression implements EvaluableExpression {
    /**
     * The expression's source definition
     */
    private final String source;
    /**
     * The expression's compiled definition
     */
    private final Object clojure;

    /**
     * Gets the expression's source definition
     *
     * @return The expression's source definition
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the expression's compiled definition
     *
     * @return The expression's compiled definition
     */
    public Object getClojure() {
        return clojure;
    }

    /**
     * Initializes this expression
     *
     * @param source The expression's source definition
     */
    public ClojureExpression(String source) {
        this.source = source;
        this.clojure = Clojure.read(source);
    }

    @Override
    public String serializedString() {
        return source;
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                TextUtils.escapeStringJSON(EvaluableExpression.class.getCanonicalName()) +
                "\", \"source\": \"" +
                TextUtils.escapeStringJSON(source) +
                "\"}";
    }
}
