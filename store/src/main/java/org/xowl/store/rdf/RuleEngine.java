/**********************************************************************
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
 **********************************************************************/
package org.xowl.store.rdf;

import org.xowl.store.rete.*;

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
        public Rule rule;
        /**
         * The token that triggered the rule
         */
        public Token token;
        /**
         * The produced changeset
         */
        public Changeset changeset;

        /**
         * Initializes this data
         *
         * @param rule      The fired rule
         * @param token     The token that triggered the rule
         * @param changeset The produced changeset
         */
        public ExecutedRule(Rule rule, Token token, Changeset changeset) {
            this.rule = rule;
            this.token = token;
            this.changeset = changeset;
        }
    }

    /**
     * The RDF store to operate over
     */
    private RDFStore store;
    /**
     * The corpus of active rules
     */
    private Map<Rule, RETERule> rules;
    /**
     * A RETE network for the pattern matching of queries
     */
    private RETENetwork rete;
    /**
     * The new changes since the last application
     */
    private List<Change> newChanges;
    /**
     * The new changesets since the last application
     */
    private List<Changeset> newChangesets;
    /**
     * Flag whether outstanding changes are currently being applied
     */
    private boolean isApplying;
    /**
     * Buffer of positive quads
     */
    private Collection<Quad> bufferPositives;
    /**
     * Buffer of negative quads
     */
    private Collection<Quad> bufferNegatives;
    /**
     * The current requests to fire a rule
     */
    private Map<Token, Rule> requestsToFire;
    /**
     * The current requests to unfire a rule
     */
    private List<Token> requestsToUnfire;
    /**
     * The currently produced data
     */
    private Map<Token, ExecutedRule> executed;

    /**
     * Initializes this engine
     *
     * @param store The RDF store to operate over
     */
    public RuleEngine(RDFStore store) {
        this.store = store;
        this.rules = new HashMap<>();
        this.rete = new RETENetwork(store);
        this.newChanges = new ArrayList<>();
        this.newChangesets = new ArrayList<>();
        this.bufferPositives = new ArrayList<>();
        this.bufferNegatives = new ArrayList<>();
        this.requestsToFire = new HashMap<>();
        this.requestsToUnfire = new ArrayList<>();
        this.executed = new HashMap<>();
        store.addListener(this);
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
    public void onChange(Change change) {
        newChanges.add(change);
        apply();
    }

    @Override
    public void onChange(Changeset changeset) {
        newChangesets.add(changeset);
        apply();
    }

    /**
     * Applies all outstanding changes
     */
    protected void apply() {
        if (isApplying)
            return;
        isApplying = true;
        while (newChanges.size() > 0 || newChangesets.size() > 0) {
            injectChanges();
            performUnfire();
            performFire();
            // inject the changes if necessary
            if (!bufferPositives.isEmpty() || !bufferNegatives.isEmpty()) {
                try {
                    store.insert(new Changeset(bufferPositives, bufferNegatives));
                } catch (UnsupportedNodeType ex) {
                    // TODO: report this
                }
                bufferPositives.clear();
                bufferNegatives.clear();
            }
        }
        isApplying = false;
    }

    /**
     * Inject the outstanding changes in the RETE network
     */
    private void injectChanges() {
        // build the buffer for the injection in the RETE network
        for (Change change : newChanges) {
            if (change.isPositive())
                bufferPositives.add(change.getValue());
            else
                bufferNegatives.add(change.getValue());
        }
        newChanges.clear();
        for (Changeset changeset : newChangesets) {
            bufferPositives.addAll(changeset.getPositives());
            bufferNegatives.addAll(changeset.getNegatives());
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
                bufferPositives.addAll(data.changeset.getNegatives());
                bufferNegatives.addAll(data.changeset.getPositives());
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
            Changeset changeset = process(entry.getValue(), entry.getKey());
            if (changeset == null)
                continue;
            ExecutedRule data = new ExecutedRule(entry.getValue(), entry.getKey(), changeset);
            executed.put(data.token, data);
            bufferPositives.addAll(changeset.getPositives());
            bufferNegatives.addAll(changeset.getNegatives());
        }
    }

    /**
     * Processes the specified rule with the specified token
     *
     * @param rule  A rule
     * @param token A token
     * @return The corresponding production
     */
    private Changeset process(Rule rule, Token token) {
        Map<VariableNode, Node> bindings = new HashMap<>(token.getBindings());
        List<Quad> positives = new ArrayList<>();
        List<Quad> negatives = new ArrayList<>();
        for (Quad quad : rule.getConsequentTargetPositives()) {
            Quad result = process(quad, bindings);
            if (result == null)
                return null;
            positives.add(result);
        }
        for (Quad quad : rule.getConsequentMetaPositives()) {
            Quad result = process(quad, bindings);
            if (result == null)
                return null;
            positives.add(result);
        }
        for (Quad quad : rule.getConsequentTargetNegatives()) {
            Quad result = process(quad, bindings);
            if (result == null)
                return null;
            negatives.add(result);
        }
        for (Quad quad : rule.getConsequentMetaNegatives()) {
            Quad result = process(quad, bindings);
            if (result == null)
                return null;
            negatives.add(result);
        }
        return new Changeset(positives, negatives);
    }

    /**
     * Processes the specified quad
     *
     * @param quad     The quad to process
     * @param bindings The map of bindings
     * @return The processed node
     */
    private Quad process(Quad quad, Map<VariableNode, Node> bindings) {
        Node nodeGraph = process(quad.getGraph(), bindings, true);
        Node nodeSubject = process(quad.getSubject(), bindings, false);
        Node nodeProperty = process(quad.getProperty(), bindings, false);
        Node nodeObject = process(quad.getObject(), bindings, false);
        if ((!(nodeGraph instanceof GraphNode)) || (!(nodeSubject instanceof SubjectNode)) || (!(nodeProperty instanceof Property)))
            return null;
        return new Quad((GraphNode) nodeGraph, (SubjectNode) nodeSubject, (Property) nodeProperty, nodeObject);
    }

    /**
     * Processes the specified node
     *
     * @param node      The node to process
     * @param bindings  The map of bindings
     * @param createIRI Whether to create an IRI node or a blank node in the case of an unbound variable
     * @return The processed node
     */
    private Node process(Node node, Map<VariableNode, Node> bindings, boolean createIRI) {
        if (node.getNodeType() == VariableNode.TYPE) {
            return processResolve((VariableNode) node, bindings, createIRI);
        } else if (node.getNodeType() == IRINode.TYPE) {
            return node;
        } else if (node.getNodeType() == BlankNode.TYPE) {
            return node;
        } else if (node.getNodeType() == LiteralNode.TYPE) {
            return node;
        } else {
            return processOtherNode(node);
        }
    }

    /**
     * Resolves the specified variable node
     *
     * @param variable  A variable node
     * @param bindings  The map of bindings
     * @param createIRI Whether to create an IRI node or a blank node in the case of an unbound variable
     * @return The variable value
     */
    private Node processResolve(VariableNode variable, Map<VariableNode, Node> bindings, boolean createIRI) {
        Node result = bindings.get(variable);
        if (result != null)
            return result;
        if (createIRI)
            result = store.newNodeIRI(null);
        else
            result = store.newNodeBlank();
        bindings.put(variable, result);
        return result;
    }

    /**
     * Processes the specified node that is not supported by this engine
     *
     * @param node The node to process
     * @return The processed node
     */
    protected Node processOtherNode(Node node) {
        return node;
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
     * Gets an explanation for the specified quad
     *
     * @param quad A quad
     * @return an explanation, or null if the quad does not come from this engine
     */
    public RuleEngineExplanation explain(Quad quad) {
        for (ExecutedRule data : executed.values()) {
            if (data.changeset.getPositives().contains(quad) || data.changeset.getNegatives().contains(quad)) {
                RuleEngineExplanation explanation = new RuleEngineExplanation(data.rule, data.token, quad);
                for (Quad antecedent : explanation.getAntecedents()) {
                    RuleEngineExplanation parent = explain(antecedent);
                    if (parent != null)
                        explanation.getParents().add(parent);
                }
                return explanation;
            }
        }
        return null;
    }
}
