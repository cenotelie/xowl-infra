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

import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.Evaluator;
import org.xowl.infra.store.storage.NodeManager;

import java.util.*;

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
     * @param guard    The rule's guard, if any
     */
    public RDFRuleSimple(String iri, boolean distinct, EvaluableExpression guard) {
        this(iri, distinct, guard, null);
    }

    /**
     * Initializes this rule
     *
     * @param iri      The rule's identifying iri
     * @param distinct Whether to trigger this rule only on distinct solutions
     * @param guard    The rule's guard, if any
     * @param source   The source for this rule
     */
    public RDFRuleSimple(String iri, boolean distinct, EvaluableExpression guard, String source) {
        super(iri, guard, source);
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
            negatives.add(new ArrayList<>());
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
            Iterator<RDFRuleExecution> executions = handler.getExecutions();
            while (executions.hasNext()) {
                RDFRuleExecution execution = executions.next();
                if (execution != null && ((RDFRuleExecutionSimple) execution).getMatch().sameAs(match))
                    return;
            }
        }
        RDFRuleExecutionSimple execution = new RDFRuleExecutionSimple(this, match);
        if (!canFire(execution, handler.getEvaluator()))
            return;
        handler.onTrigger(execution);
    }

    @Override
    public void onPatternDematched(RDFRuleEngine.ProductionHandler handler, RDFPattern pattern, RDFPatternMatch match) {
        Iterator<RDFRuleExecution> executions = handler.getExecutions();
        while (executions.hasNext()) {
            RDFRuleExecution execution = executions.next();
            if (execution != null && ((RDFRuleExecutionSimple) execution).getMatch() == match) {
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
