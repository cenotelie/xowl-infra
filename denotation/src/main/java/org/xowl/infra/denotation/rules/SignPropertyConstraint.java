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
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.store.storage.NodeManager;

import java.util.Collections;

/**
 * Represents a constraint on a sign property
 *
 * @author Laurent Wouters
 */
public class SignPropertyConstraint {
    /**
     * The subject sign property
     */
    private final SignProperty property;
    /**
     * The value to compare to
     */
    private final Object value;
    /**
     * Whether the constraint is positive
     */
    private final boolean isPositive;

    /**
     * Initializes this constraint
     *
     * @param property   The subject sign property
     * @param value      The value to compare to
     * @param isPositive Whether the constraint is positive
     */
    public SignPropertyConstraint(SignProperty property, Object value, boolean isPositive) {
        this.property = property;
        this.value = value;
        this.isPositive = isPositive;
    }

    /**
     * Gets the subject sign property
     *
     * @return The subject sign property
     */
    public SignProperty getProperty() {
        return property;
    }

    /**
     * Gets the value to compare to
     *
     * @return The value to compare to
     */
    public Object getValue() {
        return value;
    }

    /**
     * Gets whether the constraint is positive
     *
     * @return Whether the constraint is positive
     */
    public boolean isPositive() {
        return isPositive;
    }

    /**
     * Builds the RDF rule with this antecedent
     *
     * @param graphSigns The graph for the signs
     * @param graphSemes The graph for the semes
     * @param graphMeta  The graph for the metadata
     * @param nodes      The node manager to use
     * @param parent     The variable for the parent pattern
     * @param context    The current context
     */
    public void buildRdf(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, VariableNode parent, DenotationRuleContext context) {
        if (!property.isRdfSerialized())
            return;
        if (isPositive) {
            context.getRdfRule().addAntecedentPositive(new Quad(graphSigns,
                    parent,
                    nodes.getIRINode(property.getIdentifier()),
                    property.serializeValueRdf(nodes, value)
            ));
        } else {
            context.getRdfRule().addAntecedentNegatives(Collections.singletonList(new Quad(graphSigns,
                    parent,
                    nodes.getIRINode(property.getIdentifier()),
                    property.serializeValueRdf(nodes, value)
            )));
        }
    }
}
