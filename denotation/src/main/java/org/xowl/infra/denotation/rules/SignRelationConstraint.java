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

import org.xowl.infra.denotation.phrases.SignRelation;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.store.storage.NodeManager;

/**
 * Represents a constraint on a sign relation
 *
 * @author Laurent Wouters
 */
public class SignRelationConstraint {
    /**
     * The subject sign relation
     */
    private final SignRelation relation;
    /**
     * The related sign pattern
     */
    private final SignPattern signPattern;

    /**
     * Initializes this constraint
     *
     * @param relation    The subject sign relation
     * @param signPattern The related sign pattern
     */
    public SignRelationConstraint(SignRelation relation, SignPattern signPattern) {
        this.relation = relation;
        this.signPattern = signPattern;
    }

    /**
     * Gets the subject sign relation
     *
     * @return The subject sign relation
     */
    public SignRelation getRelation() {
        return relation;
    }

    /**
     * Gets the related sign pattern
     *
     * @return The related sign pattern
     */
    public SignPattern getSignPattern() {
        return signPattern;
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
        context.getRdfRule().addAntecedentPositive(new Quad(graphSigns,
                parent,
                nodes.getIRINode(relation.getIdentifier()),
                context.getVariable(signPattern.getIdentifier())
        ));
    }
}
