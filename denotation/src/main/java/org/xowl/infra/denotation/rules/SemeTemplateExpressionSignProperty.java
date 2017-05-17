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

import org.xowl.infra.denotation.phrases.SignProperty;
import org.xowl.infra.store.rdf.*;

/**
 * An expression for a seme when the value is the value of a sign's property
 *
 * @author Laurent Wouters
 */
public class SemeTemplateExpressionSignProperty implements SemeTemplateExpression {
    /**
     * The referenced sign antecedent
     */
    private final SignAntecedent reference;
    /**
     * The sign property
     */
    private final SignProperty property;

    /**
     * Initializes this property
     *
     * @param reference The referenced sign antecedent
     * @param property  The sign property
     */
    public SemeTemplateExpressionSignProperty(SignAntecedent reference, SignProperty property) {
        this.reference = reference;
        this.property = property;
    }

    /**
     * Gets the referenced sign antecedent
     *
     * @return The referenced sign antecedent
     */
    public SignAntecedent getReference() {
        return reference;
    }

    /**
     * Gets the sign property
     *
     * @return The sign property
     */
    public SignProperty getProperty() {
        return property;
    }

    @Override
    public Node getRdfNode(DenotationRuleContext context) {
        SubjectNode nodeSign = reference.getSubject(context);
        for (Quad quad : context.getRdfRule().getPatterns().get(0).getPositives()) {
            if (quad.getSubject() == nodeSign
                    && quad.getProperty().getNodeType() == Node.TYPE_IRI
                    && ((IRINode) quad.getProperty()).getIRIValue().equals(property.getIdentifier())) {
                return quad.getObject();
            }
        }

        VariableNode variableValue = context.getVariable();
        context.getRdfRule().addAntecedentPositive(new Quad(context.getGraphSigns(),
                nodeSign,
                context.getNodes().getIRINode(property.getIdentifier()),
                variableValue
        ));
        return variableValue;
    }
}
