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

import java.io.IOException;
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
    private Map<RETERule, List<JoinData>> rules;

    /**
     * Initializes this network
     *
     * @throws java.io.IOException when the store cannot allocate a temporary file
     */
    public RETENetwork() throws IOException {
        this.input = new RDFStore();
        this.alpha = new AlphaGraph();
        this.rules = new HashMap<>();
    }

    /**
     * Injects a change in this network
     *
     * @param change A change
     * @throws org.xowl.store.rdf.UnsupportedNodeType when a type of node is unsupported
     */
    public void inject(Change change) throws UnsupportedNodeType {
        if (change.isPositive()) {
            input.add(change.getValue());
            alpha.fire(change.getValue());
        } else {
            input.remove(change.getValue());
            alpha.unfire(change.getValue());
        }
    }

    /**
     * Injects a collection of changes in this network
     *
     * @param changes A collection of changes
     * @throws org.xowl.store.rdf.UnsupportedNodeType when a type of node is unsupported
     */
    public void inject(Collection<Change> changes) throws UnsupportedNodeType {
        Collection<Quad> positives = new ArrayList<>();
        Collection<Quad> negatives = new ArrayList<>();
        for (Change change : changes) {
            if (change.isPositive()) {
                input.add(change.getValue());
                positives.add(change.getValue());
            } else {
                input.remove(change.getValue());
                negatives.add(change.getValue());
            }
        }
        if (!negatives.isEmpty()) {
            alpha.unfire(negatives);
        }
        if (!positives.isEmpty())
            alpha.fire(positives);
    }

    /**
     * Adds a rule to this network
     *
     * @param rule The rule to add
     */
    public void addRule(RETERule rule) {
        List<JoinData> levels = getJoinData(rule);
        // Build RETE for positives
        Iterator<JoinData> iterData = levels.iterator();
        BetaMemory beta = BetaMemory.getDummy();
        for (Quad pattern : rule.getPositives()) {
            JoinData data = iterData.next();
            FactHolder alpha = getProvider(pattern);
            BetaJoinNode join = beta.resolveJoin(alpha, data.tests);
            beta = join.resolveMemory(data.binders);
        }
        // Append negative conditions
        TokenHolder last = beta;
        for (Collection<Quad> conjunction : rule.getNegatives()) {
            if (conjunction.size() == 1) {
                JoinData data = iterData.next();
                Quad pattern = conjunction.iterator().next();
                FactHolder alpha = getProvider(pattern);
                last = new BetaNegativeJoinNode(alpha, last, data.tests);
            } else {
                BetaNCCEntryNode entry = new BetaNCCEntryNode(last);
                last = entry;
                for (Quad pattern : conjunction) {
                    JoinData data = iterData.next();
                    FactHolder alpha = getProvider(pattern);
                    BetaJoinNode join = new BetaJoinNode(alpha, last, data.tests);
                    last = join.resolveMemory(data.binders);
                }
                last.addChild(entry.getExitNode());
                last = entry.getExitNode();
            }
        }
        // Append output node
        last.addChild(rule.getOutput());
        rules.put(rule, levels);
        beta.push();
    }

    /**
     * Removes a rule from this network
     *
     * @param rule The rule to remove
     */
    public void removeRule(RETERule rule) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes a set of rules from this network
     *
     * @param rules The rules to remove
     */
    public void removeRules(Collection<RETERule> rules) {
        throw new UnsupportedOperationException();
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
     * Resolves the provider of facts for the specified pattern
     *
     * @param pattern A pattern of triple
     * @return The corresponding fact provider
     */
    private FactHolder getProvider(Quad pattern) {
        AlphaMemory alpha = this.alpha.resolveMemory(pattern, input);
        if (pattern.getGraph() != null) {
            FilterNode fn = new FilterNode(alpha, pattern.getGraph());
            alpha = fn.getChild();
        }
        return alpha;
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
        Node node = pattern.getSubject();
        if (node.getNodeType() == VariableNode.TYPE) {
            VariableNode var = (VariableNode) node;
            if (variables.contains(var)) {
                QuadField field = QuadField.SUBJECT;
                BetaJoinNodeTest test = new BetaJoinNodeTest(var, field);
                currentData.tests.add(test);
            } else {
                currentData.binders.add(new Binder(var, QuadField.SUBJECT));
            }
            variables.add(var);
        }

        node = pattern.getProperty();
        if (node.getNodeType() == VariableNode.TYPE) {
            VariableNode var = (VariableNode) node;
            if (variables.contains(var)) {
                QuadField field = QuadField.PROPERTY;
                BetaJoinNodeTest test = new BetaJoinNodeTest(var, field);
                currentData.tests.add(test);
            } else {
                currentData.binders.add(new Binder(var, QuadField.PROPERTY));
            }
            variables.add(var);
        }

        node = pattern.getObject();
        if (node.getNodeType() == VariableNode.TYPE) {
            VariableNode var = (VariableNode) node;
            if (variables.contains(var)) {
                QuadField field = QuadField.VALUE;
                BetaJoinNodeTest test = new BetaJoinNodeTest(var, field);
                currentData.tests.add(test);
            } else {
                currentData.binders.add(new Binder(var, QuadField.VALUE));
            }
            variables.add(var);
        }
        return currentData;
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
         * Initializes the data
         */
        public JoinData() {
            this.tests = new ArrayList<>();
            this.binders = new ArrayList<>();
        }
    }
}
