/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/

package org.xowl.store.rete;

import org.xowl.store.rdf.*;

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
    private RDFStore input;
    /**
     * The alpha graph, i.e. the input layer of the network
     */
    private AlphaGraph alpha;
    /**
     * The implementation data of the RETE rules
     */
    private Map<RETERule, RuleData> rules;

    /**
     * Initializes this network
     *
     * @param input The RDF store to use as input
     */
    public RETENetwork(RDFStore input) {
        this.input = input;
        this.alpha = new AlphaGraph();
        this.rules = new HashMap<>();
    }

    /**
     * Injects a change in this network
     *
     * @param change A change
     */
    public void inject(Change change) {
        if (change.isPositive()) {
            alpha.fire(change.getValue());
        } else {
            alpha.unfire(change.getValue());
        }
    }

    /**
     * Injects a collection of changes in this network
     *
     * @param changeset A changeset
     */
    public void inject(Changeset changeset) {
        injectPositives(changeset.getPositives());
        injectNegatives(changeset.getNegatives());
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
        ruleData.positives = getJoinData(rule);
        // Build RETE for positives
        Iterator<JoinData> iterData = ruleData.positives.iterator();
        BetaMemory beta = BetaMemory.getDummy();
        for (Quad pattern : rule.getPositives()) {
            JoinData data = iterData.next();
            FactHolder alpha = this.alpha.resolveMemory(pattern, input);
            BetaJoinNode join = beta.resolveJoin(alpha, data.tests);
            beta = join.resolveMemory(data.binders);
            data.nodeJoin = join;
            data.nodeMemory = beta;
        }
        // Append negative conditions
        TokenHolder last = beta;
        for (Collection<Quad> conjunction : rule.getNegatives()) {
            if (conjunction.size() == 1) {
                JoinData data = iterData.next();
                Quad pattern = conjunction.iterator().next();
                FactHolder alpha = this.alpha.resolveMemory(pattern, input);
                last = new BetaNegativeJoinNode(alpha, last, data.tests);
                ruleData.negatives.add(last);
            } else {
                BetaNCCEntryNode entry = new BetaNCCEntryNode(last);
                last = entry;
                for (Quad pattern : conjunction) {
                    JoinData data = iterData.next();
                    FactHolder alpha = this.alpha.resolveMemory(pattern, input);
                    BetaJoinNode join = new BetaJoinNode(alpha, last, data.tests);
                    last = join.resolveMemory(data.binders);
                }
                last.addChild(entry.getExitNode());
                last = entry.getExitNode();
                ruleData.negatives.add(entry);
                ruleData.negatives.add(last);
            }
        }
        // Append output node
        last.addChild(rule.getOutput());
        rules.put(rule, ruleData);
        beta.push();
    }

    /**
     * Removes a rule from this network
     *
     * @param rule The rule to removeMemoryFor
     */
    public void removeRule(RETERule rule) {
        RuleData data = rules.get(rule);
        if (data == null)
            // the rule is not in this network ...
            return;
        // remove the rule's output
        // if there is a negative join network, this is not necessary because we will also remove it
        if (data.negatives.isEmpty()) {
            // no negative, remove from the positive network
            data.positives.get(data.positives.size() - 1).nodeMemory.removeChild(rule.getOutput());
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
                join.nodeJoin.counter--;
                if (join.nodeJoin.counter == 0) {
                    // this join is no longer required, remove it from the network
                    join.nodeMemory.onDestroy();
                    join.nodeJoin.onDestroy();
                }
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
        alpha.clear();
        BetaMemory.getDummy().removeAllChildren();
        rules.clear();
    }

    /**
     * Gets the join data for the specified RETE rule
     *
     * @param rule A RETE rule
     * @return The corresponding join data
     */
    private List<JoinData> getJoinData(RETERule rule) {
        List<JoinData> tests = new ArrayList<>();
        List<VariableNode> variables = new ArrayList<>();
        for (Quad pattern : rule.getPositives())
            tests.add(getJoinData(pattern, variables));
        for (Collection<Quad> conjunction : rule.getNegatives())
            for (Quad pattern : conjunction)
                tests.add(getJoinData(pattern, variables));
        return tests;
    }

    /**
     * Gets the join data for the specified items
     *
     * @param pattern   The pattern at this level
     * @param variables The pre-existing variables
     * @return The join data
     */
    private JoinData getJoinData(Quad pattern, Collection<VariableNode> variables) {
        JoinData currentData = new JoinData();
        buildJoinData(currentData, pattern, QuadField.SUBJECT, variables);
        buildJoinData(currentData, pattern, QuadField.PROPERTY, variables);
        buildJoinData(currentData, pattern, QuadField.VALUE, variables);
        buildJoinData(currentData, pattern, QuadField.GRAPH, variables);
        return currentData;
    }

    /**
     * Builds the join data
     *
     * @param data      The join data to build
     * @param pattern   The pattern quad to build from
     * @param field     The quad field to inspect
     * @param variables The current found variables so far
     */
    private void buildJoinData(JoinData data, Quad pattern, QuadField field, Collection<VariableNode> variables) {
        Node node = pattern.getField(field);
        if (node.getNodeType() == VariableNode.TYPE) {
            VariableNode var = (VariableNode) node;
            if (variables.contains(var)) {
                data.tests.add(new BetaJoinNodeTest(var, field));
            } else {
                data.binders.add(new Binder(var, field));
                variables.add(var);
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
        public List<TokenHolder> negatives;

        /**
         * Initializes this data
         */
        public RuleData() {
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
        public List<BetaJoinNodeTest> tests;
        /**
         * The binding operations
         */
        public List<Binder> binders;
        /**
         * The corresponding join node
         */
        public BetaJoinNode nodeJoin;
        /**
         * The child memory
         */
        public BetaMemory nodeMemory;

        /**
         * Initializes the data
         */
        public JoinData() {
            this.tests = new ArrayList<>();
            this.binders = new ArrayList<>();
        }
    }
}
