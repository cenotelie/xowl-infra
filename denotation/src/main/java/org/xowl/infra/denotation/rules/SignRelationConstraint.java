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
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.VariableNode;

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
     * The related sign antecedent
     */
    private final SignAntecedent relatedSign;

    /**
     * Initializes this constraint
     *
     * @param relation    The subject sign relation
     * @param relatedSign The related sign antecedent
     */
    public SignRelationConstraint(SignRelation relation, SignAntecedent relatedSign) {
        this.relation = relation;
        this.relatedSign = relatedSign;
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
     * Gets the related sign antecedent
     *
     * @return The related sign antecedent
     */
    public SignAntecedent getRelatedSign() {
        return relatedSign;
    }

    /**
     * Builds the RDF rule with this antecedent
     *
     * @param parent  The variable for the parent pattern
     * @param context The current context
     */
    public void buildRdf(VariableNode parent, DenotationRuleContext context) {
        context.getRdfRule().addAntecedentPositive(new Quad(context.getGraphSigns(),
                parent,
                context.getNodes().getIRINode(relation.getIdentifier()),
                relatedSign.getSubject(context)
        ));
    }
}
