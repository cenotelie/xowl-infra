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

package org.xowl.infra.denotation.rules;

import java.util.Collection;

/**
 * An expression for the appearance (or not) in an enumeration
 *
 * @author Laurent Wouters
 */
public class ExpressionIn implements Expression {
    /**
     * The value to look for in the enumeration
     */
    private final Expression value;
    /**
     * The possible values of the enumeration
     */
    private final Collection<Expression> enumeration;
    /**
     * Whether this expression is positive
     */
    private final boolean isNegative;

    /**
     * Initializes this expression
     *
     * @param value       The value to look for in the enumeration
     * @param enumeration The possible values of the enumeration
     * @param isNegative  Whether this expression is positive
     */
    public ExpressionIn(Expression value, Collection<Expression> enumeration, boolean isNegative) {
        this.value = value;
        this.enumeration = enumeration;
        this.isNegative = isNegative;
    }
}
