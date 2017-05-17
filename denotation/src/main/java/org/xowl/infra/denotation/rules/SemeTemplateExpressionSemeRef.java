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

import org.xowl.infra.store.rdf.Node;

/**
 * Represents a reference to a seme as an expression
 *
 * @author Laurent Wouters
 */
public class SemeTemplateExpressionSemeRef implements SemeTemplateExpression {
    /**
     * The referenced seme consequent
     */
    private final SemeConsequent reference;

    /**
     * Initializes this property
     *
     * @param reference The referenced seme consequent
     */
    public SemeTemplateExpressionSemeRef(SemeConsequent reference) {
        this.reference = reference;
    }

    /**
     * Gets the referenced seme consequent
     *
     * @return The referenced seme consequent
     */
    public SemeConsequent getReference() {
        return reference;
    }

    @Override
    public Node getRdfNode(DenotationRuleContext context) {
        return reference.getSubject(context);
    }
}
