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
import org.xowl.infra.store.rete.RETENetwork;
import org.xowl.infra.store.rete.RETERule;
import org.xowl.infra.store.rete.Token;
import org.xowl.infra.store.rete.TokenActivable;
import org.xowl.infra.store.storage.BaseStore;
import org.xowl.infra.store.storage.Dataset;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.logging.Logger;

import java.util.*;

/**
 * Represents a rule engine operating over a RDF dataset
 *
 * @author Laurent Wouters
 */
public class RDFRuleEngine implements ChangeListener {
    /**
     * Events for rule productions
     */
    public interface ProductionHandler {
        /**
         * When a rule execution is triggered
         *
         * @param execution The rule execution
         */
        void onTrigger(RDFRuleExecution execution);

        /**
         * When a rule execution is invalidated
         *
         * @param execution The rule execution
         */
        void onInvalidate(RDFRuleExecution execution);

        /**
         * Gets the triggered execution for the associated rule
         *
         * @return The executions
         */
        Collection<RDFRuleExecution> getExecutions();

        /**
         * Gets the node manager
         *
         * @return The node manager
         */
        NodeManager getNodes();

        /**
         * Gets the current evaluator
         *
         * @return The current evaluator
         */
        Evaluator getEvaluator();
    }

    /**
     * Represents the compiled data of a rule
     */
    private class RuleData implements ProductionHandler {
        /**
         * The original RDF rule
         */
        public final RDFRule original;
        /**
         * The RETE rules for pattern matching
         */
        public final RETERule[] matchers;
        /**
         * The rule executions
         */
        public final Collection<RDFRuleExecution> executions;

        /**
         * Initializes this data
         *
         * @param original The original rule
         */
        public RuleData(RDFRule original) {
            this.original = original;
            this.matchers = new RETERule[original.getPatterns().size()];
            this.executions = new ArrayList<>();
        }

        @Override
        public void onTrigger(RDFRuleExecution execution) {
            executions.add(execution);
            requestsToFire.add(execution);
        }

        @Override
        public void onInvalidate(RDFRuleExecution execution) {
            executions.remove(execution);
            if (!requestsToFire.remove(execution))
                requestsToUnfire.add(execution);
        }

        @Override
        public Collection<RDFRuleExecution> getExecutions() {
            return executions;
        }

        @Override
        public NodeManager getNodes() {
            return outputStore;
        }

        @Override
        public Evaluator getEvaluator() {
            return evaluator;
        }
    }

    /**
     * The output of a RETE rule for this engine
     */
    private class RETEOutput implements TokenActivable {
        /**
         * The parent rule data
         */
        private final RuleData data;
        /**
         * The pattern matched at this output
         */
        private final RDFPattern pattern;

        /**
         * Initializes this output
         *
         * @param data    The parent rule data
         * @param pattern The pattern matched at this output
         */
        public RETEOutput(RuleData data, RDFPattern pattern) {
            this.data = data;
            this.pattern = pattern;
        }

        @Override
        public void activateToken(Token token) {
            data.original.onPatternMatched(data, pattern, token);
        }

        @Override
        public void deactivateToken(Token token) {
            data.original.onPatternDematched(data, pattern, token);
        }

        @Override
        public void activateTokens(Collection<Token> tokens) {
            for (Token token : tokens)
                activateToken(token);
        }

        @Override
        public void deactivateTokens(Collection<Token> tokens) {
            for (Token token : tokens)
                deactivateToken(token);
        }
    }

    /*
    Engine input, output and backend RETE network
     */
    /**
     * The RDF store for the output
     */
    private final BaseStore outputStore;
    /**
     * A RETE network for the pattern matching of queries
     */
    private final RETENetwork rete;
    /**
     * The evaluator for this engine
     */
    private final Evaluator evaluator;

    /*
    Data for handling incoming changes to the input dataset
     */
    /**
     * The new added quads since the last application
     */
    private final List<Quad> newAdded;
    /**
     * The new removed quads since the last application
     */
    private final List<Quad> newRemoved;
    /**
     * The new changesets since the last application
     */
    private final List<Changeset> newChangesets;
    /**
     * Buffer of positive quads
     */
    private final Collection<Quad> bufferPositives;
    /**
     * Buffer of negative quads
     */
    private final Collection<Quad> bufferNegatives;
    /**
     * Flag whether outstanding changes are currently being applied
     */
    private boolean isFlushing;

    /*
    Engine rule management data
     */
    /**
     * The corpus of active rules
     */
    private final Map<RDFRule, RuleData> rulesData;
    /**
     * The current requests to fire a rule
     */
    private final Collection<RDFRuleExecution> requestsToFire;
    /**
     * The current requests to unfire a rule
     */
    private final Collection<RDFRuleExecution> requestsToUnfire;

    /**
     * Initializes this engine
     *
     * @param inputStore  The RDF store serving as input
     * @param outputStore The RDF store for the output
     * @param evaluator   The evaluator for this engine
     */
    public RDFRuleEngine(Dataset inputStore, BaseStore outputStore, Evaluator evaluator) {
        this.outputStore = outputStore;
        this.rete = new RETENetwork(inputStore);
        this.evaluator = evaluator;
        this.newAdded = new ArrayList<>();
        this.newRemoved = new ArrayList<>();
        this.newChangesets = new ArrayList<>();
        this.bufferPositives = new ArrayList<>();
        this.bufferNegatives = new ArrayList<>();
        this.rulesData = new HashMap<>();
        this.requestsToFire = new ArrayList<>();
        this.requestsToUnfire = new ArrayList<>();
        inputStore.addListener(this);
    }

    /**
     * Gets the active rules
     *
     * @return The active rules
     */
    public Collection<RDFRule> getRules() {
        return rulesData.keySet();
    }

    /**
     * Adds the specified rule
     *
     * @param rule The rule to add
     */
    public void add(final RDFRule rule) {
        RuleData data = new RuleData(rule);
        int i = 0;
        for (RDFPattern part : rule.getPatterns()) {
            data.matchers[i] = new RETERule(new RETEOutput(data, part));
            data.matchers[i].getPositives().addAll(part.getPositives());
            data.matchers[i].getNegatives().addAll(part.getNegatives());
            i++;
        }
        rulesData.put(rule, data);
        for (i = 0; i != data.matchers.length; i++)
            rete.addRule(data.matchers[i]);
    }

    /**
     * Removes the specified rule
     *
     * @param rule The rule to remove
     */
    public void remove(RDFRule rule) {
        RuleData data = rulesData.remove(rule);
        if (data == null)
            return;
        for (RETERule matcher : data.matchers)
            rete.removeRule(matcher);
        for (RDFRuleExecution execution : data.executions) {
            Changeset changeset = data.original.produce(execution, outputStore, evaluator);
            if (changeset == null) {
                Logger.DEFAULT.error("Failed to process changeset for rule " + data.original.getIRI());
            } else {
                try {
                    outputStore.insert(Changeset.reverse(changeset));
                } catch (UnsupportedNodeType ex) {
                    Logger.DEFAULT.error(ex);
                }
            }
        }
    }

    /**
     * Removes the specified rule
     *
     * @param iri The IRI of the rule to remove
     */
    public void remove(String iri) {
        for (RDFRule rule : rulesData.keySet()) {
            if (rule.getIRI().equals(iri)) {
                remove(rule);
                return;
            }
        }
    }

    @Override
    public void onIncremented(Quad quad) {
        // do nothing
    }

    @Override
    public void onDecremented(Quad quad) {
        // re-inject decremented quads as negative
        newRemoved.add(quad);
        flush();
    }

    @Override
    public void onAdded(Quad quad) {
        newAdded.add(quad);
        flush();
    }

    @Override
    public void onRemoved(Quad quad) {
        newRemoved.add(quad);
        flush();
    }

    @Override
    public void onChange(Changeset changeset) {
        newChangesets.add(changeset);
        flush();
    }

    /**
     * Flushes any outstanding changes in the input or the output
     */
    public void flush() {
        if (isFlushing)
            return;
        isFlushing = true;
        while (!newAdded.isEmpty() || !newRemoved.isEmpty() || !newChangesets.isEmpty() || !requestsToFire.isEmpty() || !requestsToUnfire.isEmpty()) {
            injectChanges();
            performUnfire();
            performFire();
            // inject the changes if necessary
            if (!bufferPositives.isEmpty() || !bufferNegatives.isEmpty()) {
                try {
                    outputStore.insert(Changeset.fromAddedRemoved(bufferPositives, bufferNegatives));
                } catch (UnsupportedNodeType ex) {
                    Logger.DEFAULT.error(ex);
                }
                bufferPositives.clear();
                bufferNegatives.clear();
            }
        }
        isFlushing = false;
    }

    /**
     * Inject the outstanding changes in the RETE network
     */
    private void injectChanges() {
        // build the buffer for the injection in the RETE network
        bufferPositives.addAll(newAdded);
        bufferNegatives.addAll(newRemoved);
        newAdded.clear();
        newRemoved.clear();
        for (Changeset changeset : newChangesets) {
            // re-inject decremented quads as negative
            // do nothing with the incremented quads
            bufferPositives.addAll(changeset.getAdded());
            bufferNegatives.addAll(changeset.getRemoved());
            bufferNegatives.addAll(changeset.getDecremented());
        }
        newChangesets.clear();
        // inject in the RETE network
        rete.injectPositives(bufferPositives);
        rete.injectNegatives(bufferNegatives);
        bufferPositives.clear();
        bufferNegatives.clear();
    }

    /**
     * Performs the outstanding unfiring requests
     */
    private void performUnfire() {
        List<RDFRuleExecution> requests = new ArrayList<>(requestsToUnfire);
        requestsToUnfire.clear();
        for (RDFRuleExecution execution : requests) {
            Changeset changeset = execution.getRule().produce(execution, outputStore, evaluator);
            if (changeset == null) {
                Logger.DEFAULT.error("Failed to process the changeset for rule " + execution.getRule().getIRI());
                continue;
            }
            bufferPositives.addAll(changeset.getRemoved());
            bufferNegatives.addAll(changeset.getAdded());
        }
    }

    /**
     * Performs the outstanding firing requests
     */
    private void performFire() {
        List<RDFRuleExecution> requests = new ArrayList<>(requestsToFire);
        requestsToFire.clear();
        for (RDFRuleExecution execution : requests) {
            Changeset changeset = execution.getRule().produce(execution, outputStore, evaluator);
            if (changeset == null) {
                Logger.DEFAULT.error("Failed to process the changeset for rule " + execution.getRule().getIRI());
                continue;
            }
            bufferPositives.addAll(changeset.getAdded());
            bufferNegatives.addAll(changeset.getRemoved());
        }
    }

    /**
     * Gets the status of a rule
     *
     * @param rule A rule's IRI
     * @return The status of the rule, or null if it is not in this engine
     */
    public RDFRuleStatus getMatchStatus(String rule) {
        for (Map.Entry<RDFRule, RuleData> entry : rulesData.entrySet()) {
            if (entry.getKey().getIRI().equals(rule)) {
                return new RDFRuleStatus(new ArrayList<>(entry.getValue().executions));
            }
        }
        // not a rule in this engine
        return null;
    }

    /**
     * Gets the status of a rule
     *
     * @param rule A rule
     * @return The status of the rule, or null if it is not in this engine
     */
    public RDFRuleStatus getMatchStatus(RDFRule rule) {
        RuleData data = rulesData.get(rule);
        if (data == null)
            return null;
        return new RDFRuleStatus(new ArrayList<>(data.executions));
    }
}
