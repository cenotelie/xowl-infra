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

package org.xowl.infra.store.rete;

import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.Dataset;

import java.util.*;

/**
 * Represents a RETE network
 *
 * @author Laurent Wouters
 */
public class RETENetwork {
    /**
     * Cache of inputs for this network
     */
    private final Dataset input;
    /**
     * The alpha graph, i.e. the input layer of the network
     */
    private final AlphaGraph alpha;
    /**
     * The implementation data of the RETE rules
     */
    private final Map<RETERule, RuleData> rules;

    /**
     * Initializes this network
     *
     * @param input The RDF store to use as input
     */
    public RETENetwork(Dataset input) {
        this.input = input;
        this.alpha = new AlphaGraph();
        this.rules = new HashMap<>();
    }

    /**
     * Injects a collection of changes in this network
     *
     * @param changeset A changeset
     */
    public void inject(Changeset changeset) {
        injectPositives(changeset.getAdded());
        injectNegatives(changeset.getRemoved());
    }

    /**
     * Injects a collection of changes in this network
     *
     * @param changeset A changeset
     */
    public void injectPositives(Collection<Quad> changeset) {
        if (!changeset.isEmpty())
            alpha.fire(changeset);
    }

    /**
     * Injects a collection of changes in this network
     *
     * @param changeset A changeset
     */
    public void injectNegatives(Collection<Quad> changeset) {
        if (!changeset.isEmpty())
            alpha.unfire(changeset);
    }

    /**
     * Adds a rule to this network
     *
     * @param rule The rule to addMemoryFor
     */
    public void addRule(RETERule rule) {
        RuleData ruleData = new RuleData();
        List<JoinData> joinData = getJoinData(rule);

        // Build RETE for positives
        Iterator<JoinData> iterator = joinData.iterator();
        BetaMemory beta = BetaMemory.getDummy();
        for (Quad pattern : rule.getPositives()) {
            JoinData data = iterator.next();
            ruleData.positives.add(data);
            FactHolder alpha = this.alpha.resolveMemory(pattern, input);
            data.nodeJoin = new BetaJoinNode(alpha, beta, data.tests, data.binders);
            beta = data.nodeJoin.getChild();
        }

        // Append negative conditions
        TokenHolder last = beta;
        for (Collection<Quad> conjunction : rule.getNegatives()) {
            if (conjunction.size() == 1) {
                JoinData data = iterator.next();
                Quad pattern = conjunction.iterator().next();
                FactHolder alpha = this.alpha.resolveMemory(pattern, input);
                last = new BetaNegativeJoinNode(alpha, last, data.tests);
                ruleData.negatives.add(last);
            } else {
                BetaNCCEntryNode entry = new BetaNCCEntryNode(last, conjunction.size());
                last = entry;
                for (Quad pattern : conjunction) {
                    JoinData data = iterator.next();
                    FactHolder alpha = this.alpha.resolveMemory(pattern, input);
                    BetaJoinNode join = new BetaJoinNode(alpha, last, data.tests, data.binders);
                    last = join.getChild();
                }
                last.addChild(entry.getExitNode());
                last = entry.getExitNode();
                ruleData.negatives.add(entry);
                ruleData.negatives.add(last);
            }
        }

        // Append output node
        last.addChild(rule.getOutput());
        synchronized (rules) {
            rules.put(rule, ruleData);
        }

        // push the dummy token into this network to trigger the beta network
        if (!ruleData.positives.isEmpty()) {
            // we have a positive network to begin with
            ruleData.positives.get(0).nodeJoin.activateTokens(BetaMemory.getDummy().getTokens());
        } else if (!ruleData.negatives.isEmpty()) {
            // no positive network, but a negative one
            TokenHolder first = ruleData.negatives.get(0);
            if (first instanceof BetaNegativeJoinNode)
                ((BetaNegativeJoinNode) first).activateTokens(BetaMemory.getDummy().getTokens());
            else if (first instanceof BetaNCCEntryNode)
                ((BetaNCCEntryNode) first).activateTokens(BetaMemory.getDummy().getTokens());
        }
    }

    /**
     * Removes a rule from this network
     *
     * @param rule The rule to removeMemoryFor
     */
    public void removeRule(RETERule rule) {
        RuleData data;
        synchronized (rules) {
            data = rules.get(rule);
        }
        if (data == null)
            // the rule is not in this network ...
            return;
        // remove the rule's output
        // if there is a negative join network, this is not necessary because we will also remove it
        if (data.negatives.isEmpty()) {
            // no negative, remove from the positive network
            data.positives.get(data.positives.size() - 1).nodeJoin.getChild().removeChild(rule.getOutput());
        } else {
            // remove the negative network
            int index = data.negatives.size() - 1;
            while (index != -1) {
                TokenHolder node = data.negatives.get(index);
                node.onDestroy();
                index--;
            }
        }
        // remove the positive network
        {
            for (int i = data.positives.size() - 1; i != -1; i--) {
                JoinData join = data.positives.get(i);
                join.nodeJoin.getChild().onDestroy();
                join.nodeJoin.onDestroy();
            }
        }
    }

    /**
     * Removes a set of rules from this network
     *
     * @param rules The rules to removeMemory
     */
    public void removeRules(Collection<RETERule> rules) {
        for (RETERule rule : rules)
            removeRule(rule);
    }

    /**
     * Removes all the rules from this network
     */
    public void removeAllRules() {
        synchronized (rules) {
            rules.clear();
        }
        alpha.clear();
        BetaMemory.getDummy().removeAllChildren();
    }

    /**
     * Gets the status of the specified rule in this network
     *
     * @param rule A rule in this network
     * @return The rule's status
     */
    public MatchStatus getStatus(RETERule rule) {
        RuleData data;
        synchronized (rules) {
            data = rules.get(rule);
        }
        MatchStatus result = new MatchStatus();
        if (data == null)
            // the rule is not in this network ...
            return result;
        int index = 0;
        for (Quad positivePattern : rule.getPositives()) {
            JoinData joinData = data.positives.get(index);
            index++;
            MatchStatusStep step = new MatchStatusStep(positivePattern);
            for (Token token : joinData.nodeJoin.getChild().getTokens())
                step.addBindings(token);
            result.addStep(step);
        }
        return result;
    }

    /**
     * Gets the join data for the specified RETE rule
     *
     * @param rule A RETE rule
     * @return The corresponding join data
     */
    private List<JoinData> getJoinData(RETERule rule) {
        List<JoinData> tests = new ArrayList<>();
        List<VariableNode> boundVariables = new ArrayList<>();
        for (Quad pattern : rule.getPositives())
            tests.add(getJoinData(pattern, boundVariables));
        for (Collection<Quad> conjunction : rule.getNegatives()) {
            // prevent registering negative variables as bound
            List<VariableNode> negativeVariables = new ArrayList<>(boundVariables);
            for (Quad pattern : conjunction)
                tests.add(getJoinData(pattern, negativeVariables));
        }
        return tests;
    }

    /**
     * Gets the join data for the specified items
     *
     * @param pattern        The pattern at this level
     * @param boundVariables The bound variables
     * @return The join data
     */
    private JoinData getJoinData(Quad pattern, Collection<VariableNode> boundVariables) {
        JoinData currentData = new JoinData();
        Map<VariableNode, QuadField> unboundVariables = new HashMap<>();
        buildJoinData(currentData, pattern, QuadField.SUBJECT, boundVariables, unboundVariables);
        buildJoinData(currentData, pattern, QuadField.PROPERTY, boundVariables, unboundVariables);
        buildJoinData(currentData, pattern, QuadField.VALUE, boundVariables, unboundVariables);
        buildJoinData(currentData, pattern, QuadField.GRAPH, boundVariables, unboundVariables);
        boundVariables.addAll(unboundVariables.keySet());
        return currentData;
    }

    /**
     * Builds the join data
     *
     * @param data             The join data to build
     * @param pattern          The pattern quad to build from
     * @param field            The quad field to inspect
     * @param boundVariables   The bound variables
     * @param unboundVariables The unbound variables in this quad pattern
     */
    private void buildJoinData(JoinData data, Quad pattern, QuadField field, Collection<VariableNode> boundVariables, Map<VariableNode, QuadField> unboundVariables) {
        Node node = pattern.getField(field);
        if (node != null && node.getNodeType() == Node.TYPE_VARIABLE) {
            VariableNode variable = (VariableNode) node;
            if (boundVariables.contains(variable)) {
                // the variable is bound, test the value of the fact against the bound variable's value
                data.tests.add(new JoinTestBound(variable, field));
            } else if (unboundVariables.containsKey(variable)) {
                // the value is not bound but it has already been found in another field
                // test that the two fields are the same
                data.tests.add(new JoinTestUnbound(unboundVariables.get(variable), field));
            } else {
                // this variable is not known
                // create the binder and register it as an unbound variable at this point
                data.binders.add(new Binder(variable, field));
                unboundVariables.put(variable, field);
            }
        }
    }

    /**
     * Represents the data of a compiled RETE rule
     */
    private static class RuleData {
        /**
         * The data for the positive joins
         */
        public List<JoinData> positives;
        /**
         * The nodes for the negative joins
         */
        public final List<TokenHolder> negatives;

        /**
         * Initializes this data
         */
        public RuleData() {
            this.positives = new ArrayList<>();
            this.negatives = new ArrayList<>();
        }
    }

    /**
     * Represents the data of a join node
     */
    private static class JoinData {
        /**
         * The tests for the join
         */
        public final List<JoinTest> tests;
        /**
         * The binding operations
         */
        public final List<Binder> binders;
        /**
         * The corresponding join node
         */
        public BetaJoinNode nodeJoin;

        /**
         * Initializes the data
         */
        public JoinData() {
            this.tests = new ArrayList<>(4);
            this.binders = new ArrayList<>();
        }
    }
}
