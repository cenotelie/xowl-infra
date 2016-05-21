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
import org.xowl.infra.store.sparql.CommandSelect;
import org.xowl.infra.store.sparql.GraphPattern;
import org.xowl.infra.store.storage.NodeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a rule that uses a SPARQL SELECT query as an antecedent
 *
 * @author Laurent Wouters
 */
public class RDFRuleSelect extends RDFRule {
    /**
     * The antecedent graph pattern representing the SPARQL SELECT
     */
    private final GraphPattern antecedent;
    /**
     * The pattern for the rule consequents
     */
    private final RDFPattern consequents;

    /**
     * Initializes this rule
     *
     * @param iri The rule's identifying iri
     * @param antecedent The antecedent graph pattern representing the SPARQL SELECT
     */
    public RDFRuleSelect(String iri, GraphPattern antecedent) {
        super(iri);
        this.antecedent = antecedent;
        this.consequents = new RDFPattern();
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
    public Collection<RDFPattern> getPatterns() {
        return null;
    }

    @Override
    public Collection<RDFRuleExecution> onPatternMatched(Collection<RDFRuleExecution> executions, RDFPattern pattern, RDFPatternMatch match) {
        return null;
    }

    @Override
    public Collection<RDFRuleExecution> onPatternDematched(Collection<RDFRuleExecution> executions, RDFPattern pattern, RDFPatternMatch match) {
        return null;
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
