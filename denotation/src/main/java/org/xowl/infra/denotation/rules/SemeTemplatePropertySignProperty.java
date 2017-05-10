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
import org.xowl.infra.store.storage.NodeManager;

/**
 * A property template for a seme when the value is the value of a sign's property
 *
 * @author Laurent Wouters
 */
public class SemeTemplatePropertySignProperty extends SemeTemplateProperty {
    /**
     * The referenced sign pattern
     */
    private final SignPattern reference;
    /**
     * The sign property
     */
    private final SignProperty property;

    /**
     * Initializes this property
     *
     * @param propertyIri The property's IRI
     * @param reference   The referenced sign pattern
     * @param property    The sign property
     */
    public SemeTemplatePropertySignProperty(String propertyIri, SignPattern reference, SignProperty property) {
        super(propertyIri);
        this.reference = reference;
        this.property = property;
    }

    /**
     * Gets the referenced sign pattern
     *
     * @return The referenced sign pattern
     */
    public SignPattern getReference() {
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
    public void buildRdfProperty(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, SubjectNode parent, DenotationRuleContext context) {
        VariableNode referencedSign = context.getVariable(reference.getIdentifier());
        Node foundObject = null;
        for (Quad quad : context.getRdfRule().getPatterns().get(0).getPositives()) {
            if (quad.getSubject() == referencedSign
                    && quad.getProperty().getNodeType() == Node.TYPE_IRI
                    && ((IRINode) quad.getProperty()).getIRIValue().equals(property.getIdentifier())) {
                foundObject = quad.getObject();
                break;
            }
        }

        if (foundObject == null) {
            // the value for the property is not in the antecedents => add it
            VariableNode variableValue = context.getVariable();
            context.getRdfRule().addAntecedentPositive(new Quad(graphSigns,
                    referencedSign,
                    nodes.getIRINode(property.getIdentifier()),
                    variableValue
            ));
            context.getRdfRule().addConsequentPositive(new Quad(graphSemes,
                    parent,
                    nodes.getIRINode(propertyIri),
                    variableValue
            ));
        } else {
            context.getRdfRule().addConsequentPositive(new Quad(graphSemes,
                    parent,
                    nodes.getIRINode(propertyIri),
                    foundObject
            ));
        }
    }
}
