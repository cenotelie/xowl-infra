/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 ******************************************************************************/

package org.xowl.store.rdf;

import org.xowl.store.IRIs;
import org.xowl.store.RDFUtils;
import org.xowl.store.rete.*;
import org.xowl.store.storage.BaseStore;
import org.xowl.store.storage.Dataset;
import org.xowl.store.storage.UnsupportedNodeType;
import org.xowl.utils.collections.Couple;

import java.util.*;

/**
 * Represents a rule engine operating over a RDF dataset
 *
 * @author Laurent Wouters
 */
public class RuleEngine implements ChangeListener {
    /**
     * Represents the data of a rule fired by this engine
     */
    private static class ExecutedRule {
        /**
         * The fired rule
         */
        public final Rule rule;
        /**
         * The token that triggered the rule
         */
        public final Token token;
        /**
         * The mapping of special nodes in the consequents
         */
        public final Map<Node, Node> specials;

        /**
         * Initializes this data
         *
         * @param rule     The fired rule
         * @param token    The token that triggered the rule
         * @param specials The mapping of special nodes in the consequents
         */
        public ExecutedRule(Rule rule, Token token, Map<Node, Node> specials) {
            this.rule = rule;
            this.token = token;
            this.specials = specials;
        }
    }

    /**
     * The RDF dataset for the input
     */
    private final Dataset inputStore;
    /**
     * The RDF store for the output
     */
    private final BaseStore outputStore;
    /**
     * The corpus of active rules
     */
    private final Map<Rule, RETERule> rules;
    /**
     * A RETE network for the pattern matching of queries
     */
    private final RETENetwork rete;
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
     * Flag whether outstanding changes are currently being applied
     */
    private boolean isFlushing;
    /**
     * Buffer of positive quads
     */
    private final Collection<Quad> bufferPositives;
    /**
     * Buffer of negative quads
     */
    private final Collection<Quad> bufferNegatives;
    /**
     * The current requests to fire a rule
     */
    private final Map<Token, Rule> requestsToFire;
    /**
     * The current requests to unfire a rule
     */
    private final List<Token> requestsToUnfire;
    /**
     * The currently produced data
     */
    private final Map<Token, ExecutedRule> executed;

    /**
     * Initializes this engine
     *
     * @param inputStore  The RDF store serving as input
     * @param outputStore The RDF store for the output
     */
    public RuleEngine(Dataset inputStore, BaseStore outputStore) {
        this.inputStore = inputStore;
        this.outputStore = outputStore;
        this.rules = new HashMap<>();
        this.rete = new RETENetwork(inputStore);
        this.newAdded = new ArrayList<>();
        this.newRemoved = new ArrayList<>();
        this.newChangesets = new ArrayList<>();
        this.bufferPositives = new ArrayList<>();
        this.bufferNegatives = new ArrayList<>();
        this.requestsToFire = new HashMap<>();
        this.requestsToUnfire = new ArrayList<>();
        this.executed = new HashMap<>();
        inputStore.addListener(this);
    }

    /**
     * Gets the active rules
     *
     * @return The active rules
     */
    public Collection<Rule> getRules() {
        return rules.keySet();
    }

    /**
     * Adds the specified rule
     *
     * @param rule The rule to add
     */
    public void add(final Rule rule) {
        RETERule reteRule = new RETERule(new TokenActivable() {
            @Override
            public void activateToken(Token token) {
                requestsToFire.put(token, rule);
            }

            @Override
            public void deactivateToken(Token token) {
                if (requestsToFire.containsKey(token))
                    // still not fire, cancel
                    requestsToFire.remove(token);
                else
                    // request to unfire
                    requestsToUnfire.add(token);
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
        });
        reteRule.getPositives().addAll(rule.getAntecedentSourcePositives());
        reteRule.getPositives().addAll(rule.getAntecedentMetaPositives());
        reteRule.getNegatives().addAll(rule.getAntecedentSourceNegatives());
        reteRule.getNegatives().addAll(rule.getAntecedentMetaNegatives());
        rules.put(rule, reteRule);
        rete.addRule(reteRule);
    }

    /**
     * Removes the specified rule
     *
     * @param rule The rule to remove
     */
    public void remove(Rule rule) {
        rete.removeRule(rules.get(rule));
        rules.remove(rule);
    }

    /**
     * Removes the specified rule
     *
     * @param iri The IRI of the rule to remove
     */
    public void remove(String iri) {
        for (Rule rule : rules.keySet()) {
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
        newAdded.remove(quad);
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
                    // TODO: report this
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
        List<Token> requests = new ArrayList<>(requestsToUnfire);
        requestsToUnfire.clear();
        for (Token token : requests) {
            ExecutedRule data = executed.remove(token);
            if (data != null) {
                // recreate the changeset
                Changeset changeset = process(data.rule, data.token, data.specials);
                if (changeset == null) {
                    // TODO: report this
                    continue;
                }
                bufferPositives.addAll(changeset.getRemoved());
                bufferNegatives.addAll(changeset.getAdded());
            }
        }
    }

    /**
     * Performs the outstanding firing requests
     */
    private void performFire() {
        Map<Token, Rule> requests = new HashMap<>(requestsToFire);
        requestsToFire.clear();
        for (Map.Entry<Token, Rule> entry : requests.entrySet()) {
            if (entry.getValue().isDistinct()) {
                // check this is the first solution set for this rule
                boolean found = false;
                for (Map.Entry<Token, ExecutedRule> execution : executed.entrySet()) {
                    if (execution.getValue().rule == entry.getValue() && execution.getKey().sameAs(entry.getKey())) {
                        found = true;
                        break;
                    }
                }
                if (found)
                    // a matching token has already triggered the rule
                    continue;
            }
            Map<Node, Node> specials = new HashMap<>();
            Changeset changeset = process(entry.getValue(), entry.getKey(), specials);
            if (changeset == null) {
                // TODO: report this
                continue;
            }
            ExecutedRule data = new ExecutedRule(entry.getValue(), entry.getKey(), specials.isEmpty() ? null : specials);
            executed.put(data.token, data);
            bufferPositives.addAll(changeset.getAdded());
            bufferNegatives.addAll(changeset.getRemoved());
        }
    }

    /**
     * Processes the specified rule with the specified token fot the generation of the changeset
     *
     * @param rule     The rule to generate a changeset from
     * @param token    The token providing the bindings
     * @param specials The mapping of special nodes in the consequents
     * @return The corresponding production
     */
    private Changeset process(Rule rule, Token token, Map<Node, Node> specials) {
        List<Quad> positives = new ArrayList<>();
        List<Quad> negatives = new ArrayList<>();
        for (Quad quad : rule.getConsequentTargetPositives()) {
            Quad result = process(rule, quad, token, specials);
            if (result == null)
                return null;
            positives.add(result);
        }
        for (Quad quad : rule.getConsequentMetaPositives()) {
            Quad result = process(rule, quad, token, specials);
            if (result == null)
                return null;
            positives.add(result);
        }
        for (Quad quad : rule.getConsequentTargetNegatives()) {
            Quad result = process(rule, quad, token, specials);
            if (result == null)
                return null;
            negatives.add(result);
        }
        for (Quad quad : rule.getConsequentMetaNegatives()) {
            Quad result = process(rule, quad, token, specials);
            if (result == null)
                return null;
            negatives.add(result);
        }
        return Changeset.fromAddedRemoved(positives, negatives);
    }

    /**
     * Processes the specified quad
     *
     * @param rule     The rule to generate a changeset from
     * @param quad     The quad to process
     * @param token    The token providing the bindings
     * @param specials The mapping of special nodes in the consequents
     * @return The processed node
     */
    private Quad process(Rule rule, Quad quad, Token token, Map<Node, Node> specials) {
        Node nodeGraph = process(rule, quad.getGraph(), token, specials, true);
        Node nodeSubject = process(rule, quad.getSubject(), token, specials, false);
        Node nodeProperty = process(rule, quad.getProperty(), token, specials, false);
        Node nodeObject = process(rule, quad.getObject(), token, specials, false);
        if ((!(nodeGraph instanceof GraphNode)) || (!(nodeSubject instanceof SubjectNode)) || (!(nodeProperty instanceof Property)))
            return null;
        return new Quad((GraphNode) nodeGraph, (SubjectNode) nodeSubject, (Property) nodeProperty, nodeObject);
    }

    /**
     * Processes the specified node
     *
     * @param rule      The rule to generate a changeset from
     * @param node      The node to process
     * @param token     The token providing the bindings
     * @param specials  The mapping of special nodes in the consequents
     * @param createIRI Whether to create an IRI node or a blank node in the case of an unbound variable
     * @return The processed node
     */
    private Node process(Rule rule, Node node, Token token, Map<Node, Node> specials, boolean createIRI) {
        if (node == null || node.getNodeType() == Node.TYPE_VARIABLE) {
            return processResolve((VariableNode) node, token, specials, createIRI);
        } else if (node.getNodeType() == Node.TYPE_IRI) {
            return node;
        } else if (node.getNodeType() == Node.TYPE_BLANK) {
            return node;
        } else if (node.getNodeType() == Node.TYPE_LITERAL) {
            return node;
        } else {
            return processOtherNode(rule, node, token, specials);
        }
    }

    /**
     * Resolves the specified variable node
     *
     * @param variable  A variable node
     * @param token     The token providing the bindings
     * @param specials  The mapping of special nodes in the consequents
     * @param createIRI Whether to create an IRI node or a blank node in the case of an unbound variable
     * @return The variable value
     */
    private Node processResolve(VariableNode variable, Token token, Map<Node, Node> specials, boolean createIRI) {
        Node result = token.getBinding(variable);
        if (result != null)
            return result;
        result = specials.get(variable);
        if (result != null)
            return result;
        if (createIRI)
            result = outputStore.getIRINode((GraphNode) null);
        else
            result = outputStore.getBlankNode();
        specials.put(variable, result);
        return result;
    }

    /**
     * Processes the specified node that is not supported by this engine
     *
     * @param rule     The rule to generate a changeset from
     * @param node     The node to process
     * @param token    The token providing the bindings
     * @param specials The mapping of special nodes in the consequents
     * @return The processed node
     */
    protected Node processOtherNode(Rule rule, Node node, Token token, Map<Node, Node> specials) {
        return node;
    }

    /**
     * Gets the matching status of the specified rule
     *
     * @param rule A rule's IRI
     * @return The matching status
     */
    public MatchStatus getMatchStatus(String rule) {
        for (Map.Entry<Rule, RETERule> entry : rules.entrySet()) {
            if (entry.getKey().getIRI().equals(rule)) {
                return rete.getStatus(entry.getValue());
            }
        }
        // not a rule in this engine
        return null;
    }

    /**
     * Gets the matching status of the specified rule
     *
     * @param rule A rule
     * @return The matching status
     */
    public MatchStatus getMatchStatus(Rule rule) {
        RETERule reteRule = rules.get(rule);
        if (reteRule == null)
            // not a rule in this engine
            return null;
        return rete.getStatus(reteRule);
    }

    /**
     * Explains how the specified quad has been produced
     *
     * @param quad The quad to explain
     * @return The explanation
     */
    public RuleExplanation explain(Quad quad) {
        List<RuleExplanation.ENode> nodes = new ArrayList<>();
        RuleExplanation result = new RuleExplanation(quad);
        nodes.add(result.getRoot());
        // close the list
        for (int i = 0; i != nodes.size(); i++) {
            RuleExplanation.ENode current = nodes.get(i);
            GraphNode graph = current.quad.getGraph();
            if (graph.getNodeType() != Node.TYPE_IRI)
                continue;
            String iri = ((IRINode) graph).getIRIValue();
            if (!IRIs.GRAPH_INFERENCE.equals(iri))
                continue;
            // this is an inferred quad
            Collection<ExecutedRule> executions = getExecutedRulesFor(current.quad);
            for (ExecutedRule execution : executions) {
                List<RuleExplanation.ENode> targets = new ArrayList<>(1);
                for (Quad pattern : execution.rule.getAntecedentSourcePositives()) {
                    Node nodeSubject = process(execution.rule, pattern.getSubject(), execution.token, null, false);
                    Node nodeProperty = process(execution.rule, pattern.getProperty(), execution.token, null, false);
                    Node nodeObject = process(execution.rule, pattern.getObject(), execution.token, null, false);
                    Iterator<Quad> iterator = inputStore.getAll((SubjectNode) nodeSubject, (Property) nodeProperty, nodeObject);
                    while (iterator.hasNext()) {
                        Quad antecedent = iterator.next();
                        RuleExplanation.ENode target = result.resolve(antecedent);
                        if (!nodes.contains(target))
                            nodes.add(target);
                        targets.add(target);
                    }
                }
                if (!targets.isEmpty())
                    current.antecedents.add(new Couple<>(execution.rule.getIRI(), targets));
            }
        }
        return result;
    }

    /**
     * Gets the rule executions that produced the specified quad
     *
     * @param quad A quad
     * @return The rule executions that produced the quads
     */
    private Collection<ExecutedRule> getExecutedRulesFor(Quad quad) {
        List<ExecutedRule> result = new ArrayList<>(1);
        for (ExecutedRule execution : executed.values()) {
            for (Quad pattern : execution.rule.getConsequentTargetPositives()) {
                if (pattern.getSubject().getNodeType() != Node.TYPE_VARIABLE && !RDFUtils.same(pattern.getSubject(), quad.getSubject()))
                    continue;
                if (pattern.getProperty().getNodeType() != Node.TYPE_VARIABLE && !RDFUtils.same(pattern.getProperty(), quad.getProperty()))
                    continue;
                if (pattern.getObject().getNodeType() != Node.TYPE_VARIABLE && !RDFUtils.same(pattern.getObject(), quad.getObject()))
                    continue;

                // here the pattern could have produced the quad given the right bindings
                Node node = process(execution.rule, pattern.getSubject(), execution.token, execution.specials, false);
                if (!RDFUtils.same(quad.getSubject(), node))
                    continue;
                node = process(execution.rule, pattern.getProperty(), execution.token, execution.specials, false);
                if (!RDFUtils.same(quad.getProperty(), node))
                    continue;
                node = process(execution.rule, pattern.getObject(), execution.token, execution.specials, false);
                if (!RDFUtils.same(quad.getObject(), node))
                    continue;

                // here the pattern produced the quad for this execution
                result.add(execution);
                // stop here assuming that the quad is only produced once by this execution
                break;
            }
        }
        return result;
    }
}
