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

import fr.cenotelie.commons.utils.collections.FastBuffer;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.store.execution.Evaluator;
import org.xowl.infra.store.rete.RETENetwork;
import org.xowl.infra.store.rete.RETERule;
import org.xowl.infra.store.rete.Token;
import org.xowl.infra.store.rete.TokenActivable;
import org.xowl.infra.store.storage.Store;
import org.xowl.infra.store.storage.UnsupportedNodeType;

import java.util.*;

/**
 * Represents a rule engine operating over a RDF dataset
 * This structure is thread-safe.
 *
 * @author Laurent Wouters
 */
public class RDFRuleEngine implements ChangeListener {
    /**
     * The RDF store for the output
     */
    private final Store outputStore;
    /**
     * A RETE network for the pattern matching of queries
     */
    private final RETENetwork rete;
    /**
     * The evaluator for this engine
     */
    private final Evaluator evaluator;
    /**
     * The corpus of active rules
     */
    private final Map<RDFRule, RuleData> rulesData;

    /*
    Engine input, output and backend RETE network
     */
    /**
     * The thread-specific engine inputs and outputs
     */
    private final ThreadLocal<EngineIO> threadIO;
    /**
     * Initializes this engine
     *
     * @param inputStore  The RDF store serving as input
     * @param outputStore The RDF store for the output
     * @param evaluator   The evaluator for this engine
     */
    public RDFRuleEngine(Store inputStore, Store outputStore, Evaluator evaluator) {
        this.outputStore = outputStore;
        this.rete = new RETENetwork(inputStore);
        this.evaluator = evaluator;
        this.rulesData = new HashMap<>();
        this.threadIO = new ThreadLocal<>();
        inputStore.addListener(this);
    }

    /**
     * Gets the thread-specific engine inputs and outputs
     *
     * @return The engine inputs and outputs for this thread
     */
    private EngineIO getIO() {
        EngineIO result = threadIO.get();
        if (result == null) {
            result = new EngineIO();
            threadIO.set(result);
        }
        return result;
    }

    /**
     * Gets the active rules
     *
     * @return The active rules
     */
    public Collection<RDFRule> getRules() {
        synchronized (rulesData) {
            return new ArrayList<>(rulesData.keySet());
        }
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
        synchronized (rulesData) {
            rulesData.put(rule, data);
        }
        for (i = 0; i != data.matchers.length; i++)
            rete.addRule(data.matchers[i]);
    }

    /**
     * Removes the specified rule
     *
     * @param rule The rule to remove
     */
    public void remove(RDFRule rule) {
        RuleData data;
        synchronized (rulesData) {
            data = rulesData.remove(rule);
        }
        if (data == null)
            return;
        for (RETERule matcher : data.matchers)
            rete.removeRule(matcher);
        Iterator<RDFRuleExecution> executions = data.getExecutions();
        while (executions.hasNext()) {
            RDFRuleExecution execution = executions.next();
            if (execution == null)
                continue;
            // invalidate the consequents of all executions of the rule
            Changeset changeset = data.original.produce(execution, outputStore.getTransaction().getDataset(), evaluator);
            if (changeset == null) {
                Logging.get().warning("Failed to process the changeset for rule " + data.original.getIRI());
            } else {
                try {
                    outputStore.getTransaction().getDataset().insert(Changeset.reverse(changeset));
                } catch (UnsupportedNodeType ex) {
                    Logging.get().error(ex);
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
        RDFRule rule = null;
        synchronized (rulesData) {
            for (RDFRule r : rulesData.keySet()) {
                if (r.getIRI().equals(iri)) {
                    rule = r;
                    break;
                }
            }
        }
        if (rule != null)
            remove(rule);
    }

    @Override
    public void onIncremented(Quad quad) {
        // do nothing
    }

    @Override
    public void onDecremented(Quad quad) {
        // re-inject decremented quads as negative
        EngineIO io = getIO();
        io.addRemovedQuad(quad);
        flush(io);
    }

    @Override
    public void onAdded(Quad quad) {
        EngineIO io = getIO();
        io.addAddedQuad(quad);
        flush(io);
    }

    @Override
    public void onRemoved(Quad quad) {
        EngineIO io = getIO();
        io.addRemovedQuad(quad);
        flush(io);
    }

    @Override
    public void onChange(Changeset changeset) {
        EngineIO io = getIO();
        io.addChangeset(changeset);
        flush(io);
    }

    /**
     * Flushes any outstanding changes in the input or the output
     */
    public void flush() {
        flush(getIO());
    }

    /**
     * Flushes any outstanding changes in the input or the output
     *
     * @param io The thread-specific inputs and outputs
     */
    private void flush(EngineIO io) {
        if (io.isFlushing)
            return;
        io.isFlushing = true;
        while (io.hasOutstandingChanges()) {
            // inject in the RETE network
            rete.injectPositives(io.checkoutPositivesQuads());
            rete.injectNegatives(io.checkoutNegativeQuads());

            // un-fire rules
            Collection<RDFRuleExecution> requests = io.checkoutRequestsToUnfire();
            for (RDFRuleExecution execution : requests) {
                Changeset changeset = execution.getRule().produce(execution, outputStore.getTransaction().getDataset(), evaluator);
                if (changeset == null) {
                    Logging.get().warning("Failed to process the changeset for rule " + execution.getRule().getIRI());
                    continue;
                }
                io.addChangesetNegative(changeset);
            }

            // fire rules
            requests = io.checkoutRequestsToFire();
            for (RDFRuleExecution execution : requests) {
                Changeset changeset = execution.getRule().produce(execution, outputStore.getTransaction().getDataset(), evaluator);
                if (changeset == null) {
                    Logging.get().warning("Failed to process the changeset for rule " + execution.getRule().getIRI());
                    continue;
                }
                io.addChangeset(changeset);
            }

            // inject the changes if necessary
            if (io.hasOutstandingChanges()) {
                try {
                    outputStore.getTransaction().getDataset().insert(Changeset.fromAddedRemoved(io.checkoutPositivesQuads(), io.checkoutNegativeQuads()));
                } catch (UnsupportedNodeType ex) {
                    Logging.get().error(ex);
                }
            }
        }
        io.isFlushing = false;
    }

    /**
     * Gets the status of a rule
     *
     * @param rule A rule's IRI
     * @return The status of the rule, or null if it is not in this engine
     */
    public RDFRuleStatus getMatchStatus(String rule) {
        RuleData data = null;
        synchronized (rulesData) {
            for (Map.Entry<RDFRule, RuleData> entry : rulesData.entrySet()) {
                if (entry.getKey().getIRI().equals(rule)) {
                    data = entry.getValue();
                    break;
                }
            }
        }
        return data == null ? null : new RDFRuleStatus(data.getExecutions());
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
        return new RDFRuleStatus(data.getExecutions());
    }

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
        Iterator<RDFRuleExecution> getExecutions();

        /**
         * Gets the node manager
         *
         * @return The node manager
         */
        DatasetNodes getNodes();

        /**
         * Gets the current evaluator
         *
         * @return The current evaluator
         */
        Evaluator getEvaluator();
    }

    /**
     * Represents the thread-specific inputs and outputs of the engine
     */
    private static class EngineIO {
        /**
         * Flag whether outstanding changes are currently being applied
         */
        public boolean isFlushing;
        /**
         * Buffer of positive quads yet to be flushed
         */
        private Collection<Quad> bufferPositives;
        /**
         * Buffer of negative quads yet ro be flushed
         */
        private Collection<Quad> bufferNegatives;
        /**
         * The current requests to fire a rule
         */
        private Collection<RDFRuleExecution> requestsToFire;
        /**
         * The current requests to unfire a rule
         */
        private Collection<RDFRuleExecution> requestsToUnfire;

        /**
         * Gets whether there are outstanding changes
         *
         * @return Whether there are outstanding changes
         */
        public boolean hasOutstandingChanges() {
            return (bufferPositives != null && !bufferPositives.isEmpty())
                    || (bufferNegatives != null && !bufferNegatives.isEmpty())
                    || (requestsToFire != null && !requestsToFire.isEmpty())
                    || (requestsToUnfire != null && !requestsToUnfire.isEmpty());
        }

        /**
         * Gets the positive quads to be injected (and resets the buffer)
         *
         * @return The positive quads to be injected
         */
        public Collection<Quad> checkoutPositivesQuads() {
            Collection<Quad> result = bufferPositives == null ? Collections.emptyList() : bufferPositives;
            bufferPositives = null;
            return result;
        }

        /**
         * Gets the negative quads to be injected (and resets the buffer)
         *
         * @return The negative quads to be injected
         */
        public Collection<Quad> checkoutNegativeQuads() {
            Collection<Quad> result = bufferNegatives == null ? Collections.emptyList() : bufferNegatives;
            bufferNegatives = null;
            return result;
        }

        /**
         * Gets the requests to fire (and resets the buffer)
         *
         * @return The requests to fire
         */
        public Collection<RDFRuleExecution> checkoutRequestsToFire() {
            Collection<RDFRuleExecution> result = requestsToFire == null ? Collections.emptyList() : requestsToFire;
            requestsToFire = null;
            return result;
        }

        /**
         * Gets the requests to un-fire (and resets the buffer)
         *
         * @return The requests to un-fire
         */
        public Collection<RDFRuleExecution> checkoutRequestsToUnfire() {
            Collection<RDFRuleExecution> result = requestsToUnfire == null ? Collections.emptyList() : requestsToUnfire;
            requestsToUnfire = null;
            return result;
        }

        /**
         * Adds a request to fire a rule
         *
         * @param execution The requested rule execution
         */
        public void addRequestToFire(RDFRuleExecution execution) {
            if (requestsToFire == null)
                requestsToFire = new ArrayList<>();
            requestsToFire.add(execution);
        }

        /**
         * Adds a request to un-fire a rule
         *
         * @param execution The rule execution to retract
         */
        public void addRequestToUnfire(RDFRuleExecution execution) {
            if (requestsToFire != null) {
                if (requestsToFire.remove(execution))
                    return;
            }
            if (requestsToUnfire == null)
                requestsToUnfire = new ArrayList<>();
            requestsToUnfire.add(execution);
        }

        /**
         * Adds a quad that is being added
         *
         * @param quad The added quad
         */
        public void addAddedQuad(Quad quad) {
            if (bufferPositives == null)
                bufferPositives = new ArrayList<>();
            bufferPositives.add(quad);
        }

        /**
         * Adds a quad that is being removed
         *
         * @param quad The removed quad
         */
        public void addRemovedQuad(Quad quad) {
            if (bufferNegatives == null)
                bufferNegatives = new ArrayList<>();
            bufferNegatives.add(quad);
        }

        /**
         * Adds a changeset being injected
         *
         * @param changeset The injected changeset
         */
        public void addChangeset(Changeset changeset) {
            if (!changeset.getAdded().isEmpty()) {
                if (bufferPositives == null)
                    bufferPositives = new ArrayList<>();
                bufferPositives.addAll(changeset.getAdded());
            }
            if (!changeset.getDecremented().isEmpty()) {
                if (bufferNegatives == null)
                    bufferNegatives = new ArrayList<>();
                bufferNegatives.addAll(changeset.getDecremented());
            }
            if (!changeset.getRemoved().isEmpty()) {
                if (bufferNegatives == null)
                    bufferNegatives = new ArrayList<>();
                bufferNegatives.addAll(changeset.getRemoved());
            }
        }

        /**
         * Adds a negative changeset being injected
         *
         * @param changeset The injected negative changeset
         */
        public void addChangesetNegative(Changeset changeset) {
            if (!changeset.getAdded().isEmpty()) {
                if (bufferNegatives == null)
                    bufferNegatives = new ArrayList<>();
                bufferNegatives.addAll(changeset.getAdded());
            }
            if (!changeset.getRemoved().isEmpty()) {
                if (bufferPositives == null)
                    bufferPositives = new ArrayList<>();
                bufferPositives.addAll(changeset.getRemoved());
            }
        }
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
        public final FastBuffer<RDFRuleExecution> executions;

        /**
         * Initializes this data
         *
         * @param original The original rule
         */
        public RuleData(RDFRule original) {
            this.original = original;
            this.matchers = new RETERule[original.getPatterns().size()];
            this.executions = new FastBuffer<>(8);
        }

        @Override
        public void onTrigger(RDFRuleExecution execution) {
            boolean fire;
            synchronized (executions) {
                fire = executions.add(execution);
            }
            if (fire)
                getIO().addRequestToFire(execution);
        }

        @Override
        public void onInvalidate(RDFRuleExecution execution) {
            boolean unfire;
            synchronized (executions) {
                unfire = executions.remove(execution);
            }
            if (unfire)
                getIO().addRequestToUnfire(execution);
        }

        @Override
        public Iterator<RDFRuleExecution> getExecutions() {
            return executions.iterator();
        }

        @Override
        public DatasetNodes getNodes() {
            return outputStore.getTransaction().getDataset();
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
}
