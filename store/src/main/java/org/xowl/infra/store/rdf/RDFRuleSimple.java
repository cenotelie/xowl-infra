/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.store.rdf;

import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.rete.Token;
import org.xowl.infra.store.storage.NodeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a simple RDF rule for a rule engine
 *
 * @author Laurent Wouters
 */
public class RDFRuleSimple extends RDFRule {
    /**
     * Flags whether to trigger this rule only on distinct solutions
     */
    private final boolean distinct;
    /**
     * The pattern for the rule antecedents
     */
    private final RDFRulePatternPart antecedents;
    /**
     * The pattern for the rule consequents
     */
    private final RDFRulePatternPart consequents;

    /**
     * Gets whether to trigger this rule only on distinct solutions
     *
     * @return Whether to trigger this rule only on distinct solutions
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * Initializes this rule
     *
     * @param iri      The rule's identifying iri
     * @param distinct Whether to trigger this rule only on distinct solutions
     */
    public RDFRuleSimple(String iri, boolean distinct) {
        super(iri);
        this.distinct = distinct;
        this.antecedents = new RDFRulePatternPart();
        this.consequents = new RDFRulePatternPart();
    }

    /**
     * Adds a positive antecedent
     *
     * @param quad A positive antecedent
     */
    public void addAntecedentPositive(Quad quad) {
        antecedents.getPositives().add(quad);
    }

    /**
     * Adds a negative antecedent conjunction
     *
     * @param quads A negative antecedent conjunction
     */
    public void addAntecedentNegatives(Collection<Quad> quads) {
        antecedents.getNegatives().add(quads);
    }

    /**
     * Adds a positive consequent
     *
     * @param quad A positive consequent
     */
    public void addConsequentPositive(Quad quad) {
        consequents.getPositives().add(quad);
    }

    /**
     * Adds a negative consequent
     *
     * @param quad A negative consequent
     */
    public void addConsequentNegative(Quad quad) {
        Collection<Collection<Quad>> negatives = consequents.getNegatives();
        if (negatives.isEmpty())
            negatives.add(new ArrayList<Quad>());
        negatives.iterator().next().add(quad);
    }

    @Override
    public List<RDFRulePatternPart> getPatternParts() {
        return Collections.singletonList(antecedents);
    }

    @Override
    public RDFRuleExecution isTriggered(Collection<RDFRuleExecution> executions, Token[] tokens) {
        if (distinct) {
            for (RDFRuleExecution execution : executions) {
                if (execution.tokens[0].sameAs(tokens[0]))
                    return null;
            }
        }
        return new RDFRuleExecution(tokens);
    }

    @Override
    public Changeset produce(RDFRuleExecution execution, NodeManager nodes, Evaluator evaluator) {
        List<Quad> positives = new ArrayList<>();
        List<Quad> negatives = new ArrayList<>();
        for (Quad quad : consequents.getPositives()) {
            Quad result = produceQuad(execution, nodes, evaluator, quad);
            if (result == null)
                return null;
            positives.add(result);
        }
        for (Collection<Quad> collection : consequents.getNegatives()) {
            for (Quad quad : collection) {
                Quad result = produceQuad(execution, nodes, evaluator, quad);
                if (result == null)
                    return null;
                negatives.add(result);
            }
        }
        if (negatives.isEmpty())
            return Changeset.fromAdded(positives);
        if (positives.isEmpty())
            return Changeset.fromRemoved(negatives);
        return Changeset.fromAddedRemoved(positives, negatives);
    }
}
