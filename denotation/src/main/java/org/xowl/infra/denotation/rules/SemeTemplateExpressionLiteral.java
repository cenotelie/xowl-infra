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

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.Node;

/**
 * Represents a literal as an expression
 *
 * @author Laurent Wouters
 */
public class SemeTemplateExpressionLiteral implements SemeTemplateExpression {
    /**
     * The value
     */
    private final Object value;

    /**
     * Initializes this property
     *
     * @param value The expression
     */
    public SemeTemplateExpressionLiteral(Object value) {
        this.value = value;
    }

    /**
     * Gets the value
     *
     * @return The value
     */
    public Object getValue() {
        return value;
    }

    @Override
    public Node getRdfNode(DenotationRuleContext context) {
        return RDFUtils.getRDF(context.getNodes(), value);
    }
}
