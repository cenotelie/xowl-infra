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
    private final RDFPattern antecedents;
    /**
     * The pattern for the rule consequents
     */
    private final RDFPattern consequents;

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
        this.antecedents = new RDFPattern();
        this.consequents = new RDFPattern();
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
    public List<RDFPattern> getPatterns() {
        return Collections.singletonList(antecedents);
    }

    @Override
    public Collection<VariableNode> getAntecedentVariables() {
        Collection<VariableNode> result = new ArrayList<>();
        findVariables(result, antecedents);
        return result;
    }

    @Override
    public void onPatternMatched(RDFRuleEngine.ProductionHandler handler, RDFPattern pattern, RDFPatternMatch match) {
        if (distinct) {
            for (RDFRuleExecution execution : handler.getExecutions()) {
                if (((RDFRuleExecutionSimple) execution).getMatch().sameAs(match))
                    return;
            }
        }
        handler.onTrigger(new RDFRuleExecutionSimple(this, match));
    }

    @Override
    public void onPatternDematched(RDFRuleEngine.ProductionHandler handler, RDFPattern pattern, RDFPatternMatch match) {
        for (RDFRuleExecution execution : handler.getExecutions()) {
            if (((RDFRuleExecutionSimple) execution).getMatch() == match) {
                handler.onInvalidate(execution);
                return;
            }
        }
    }

    @Override
    public Changeset produce(RDFRuleExecution execution, NodeManager nodes, Evaluator evaluator) {
        return produceQuads(execution, nodes, evaluator, consequents);
    }
}
