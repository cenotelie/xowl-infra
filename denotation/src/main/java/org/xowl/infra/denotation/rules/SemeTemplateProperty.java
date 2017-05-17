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

import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.SubjectNode;

/**
 * Represents a template for a seme's property
 *
 * @author Laurent Wouters
 */
public class SemeTemplateProperty {
    /**
     * The property's IRI
     */
    private final String propertyIri;
    /**
     * The expression for the value
     */
    private final SemeTemplateExpression expression;

    /**
     * Initializes this property
     *
     * @param propertyIri The property's IRI
     * @param expression  The expression for the value
     */
    public SemeTemplateProperty(String propertyIri, SemeTemplateExpression expression) {
        this.propertyIri = propertyIri;
        this.expression = expression;
    }

    /**
     * Gets the property's IRI
     *
     * @return The property's IRI
     */
    public String getPropertyIri() {
        return propertyIri;
    }

    /**
     * Builds the RDF rule for this property
     *
     * @param parent  The node representing this consequent
     * @param context The current context
     */
    public void buildRdfProperty(SubjectNode parent, DenotationRuleContext context) {
        context.getRdfRule().addConsequentPositive(new Quad(context.getGraphSemes(),
                parent,
                context.getNodes().getIRINode(propertyIri),
                expression.getRdfNode(context)
        ));
    }
}
