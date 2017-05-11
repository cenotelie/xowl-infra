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

import org.xowl.infra.denotation.Denotation;
import org.xowl.infra.denotation.phrases.Sign;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.SubjectNode;
import org.xowl.infra.store.rdf.VariableNode;
import org.xowl.infra.store.storage.NodeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a pattern of a sign in a phrase as an antecedent to a denotation rule
 *
 * @author Laurent Wouters
 */
public class SignPattern implements SignAntecedent {
    /**
     * The identifier for this pattern
     */
    private final String identifier;
    /**
     * The constraint on the sign's properties
     */
    private List<SignPropertyConstraint> properties;
    /**
     * The constraint on the sign's relations
     */
    private List<SignRelationConstraint> relations;
    /**
     * The bounded seme required to match this pattern
     */
    private SemeConsequent seme;

    /**
     * Initializes this pattern
     *
     * @param identifier The identifier for this pattern
     */
    public SignPattern(String identifier) {
        this.identifier = identifier;
        this.properties = null;
        this.relations = null;
        this.seme = null;
    }

    /**
     * Gets the identifier for this pattern
     *
     * @return The identifier for this pattern
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the constraints on the sign's properties
     *
     * @return The constraints on the sign's properties
     */
    public List<SignPropertyConstraint> getPropertiesConstraints() {
        if (properties == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(properties);
    }

    /**
     * Adds a property constraint
     *
     * @param constraint The constraint to add
     */
    public void addPropertiesConstraint(SignPropertyConstraint constraint) {
        if (properties == null)
            properties = new ArrayList<>();
        properties.add(constraint);
    }

    /**
     * Removes a property constraint
     *
     * @param constraint The constraint to remove
     */
    public void removePropertiesConstraint(SignPropertyConstraint constraint) {
        if (properties == null)
            return;
        properties.remove(constraint);
    }

    /**
     * Gets the constraints on the sign's relations
     *
     * @return The constraints on the sign's relations
     */
    public List<SignRelationConstraint> getRelationsConstraints() {
        if (relations == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(relations);
    }

    /**
     * Adds a relation constraint
     *
     * @param constraint The constraint to add
     */
    public void addRelationConstraint(SignRelationConstraint constraint) {
        if (relations == null)
            relations = new ArrayList<>();
        relations.add(constraint);
    }

    /**
     * Removes a relation constraint
     *
     * @param constraint The constraint to remove
     */
    public void removeRelationConstraint(SignRelationConstraint constraint) {
        if (relations == null)
            return;
        relations.remove(constraint);
    }

    /**
     * Gets the bounded seme required to match this pattern, if any
     *
     * @return The bounded seme required to match this pattern, if any
     */
    public SemeConsequent getBoundSeme() {
        return seme;
    }

    /**
     * Sets the bounded seme required to match this pattern
     *
     * @param seme The bounded seme required to match this pattern
     */
    public void setBoundSeme(SemeConsequent seme) {
        this.seme = seme;
    }

    @Override
    public SubjectNode getSubject(NodeManager nodes, DenotationRuleContext context) {
        return context.getVariable(identifier);
    }

    @Override
    public void buildRdf(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, DenotationRuleContext context) {
        VariableNode variable = context.getVariable(identifier);
        // basic type matching
        context.getRdfRule().addAntecedentPositive(new Quad(graphSigns,
                variable,
                nodes.getIRINode(Vocabulary.rdfType),
                nodes.getIRINode(Sign.TYPE_SIGN)
        ));

        // properties
        if (properties != null) {
            for (SignPropertyConstraint constraint : properties)
                constraint.buildRdf(graphSigns, graphSemes, graphMeta, nodes, variable, context);
        }

        // relations
        if (relations != null) {
            for (SignRelationConstraint constraint : relations)
                constraint.buildRdf(graphSigns, graphSemes, graphMeta, nodes, variable, context);
        }

        // binding
        if (seme != null) {
            context.getRdfRule().addAntecedentPositive(new Quad(graphMeta,
                    variable,
                    nodes.getIRINode(Denotation.META_TRACE),
                    seme.getSubject(nodes, context)
            ));
        }

        // matched by
        context.getRdfRule().addConsequentPositive(new Quad(graphMeta,
                nodes.getIRINode(variable),
                nodes.getIRINode(Denotation.META_MATCHED_BY),
                nodes.getIRINode(context.getRdfRule().getIRI())
        ));
    }
}
