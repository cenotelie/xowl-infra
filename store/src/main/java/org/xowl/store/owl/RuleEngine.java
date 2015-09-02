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
package org.xowl.store.owl;

import org.xowl.lang.actions.QueryVariable;
import org.xowl.lang.owl2.Axiom;
import org.xowl.lang.owl2.Ontology;
import org.xowl.lang.rules.Assertion;
import org.xowl.lang.rules.Rule;
import org.xowl.store.rdf.GraphNode;
import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.VariableNode;
import org.xowl.store.rete.Token;
import org.xowl.store.storage.BaseStore;
import org.xowl.store.storage.NodeManager;
import org.xowl.utils.collections.Couple;

import java.util.*;

/**
 * Represents a rule engine operating over an OWL dataset
 *
 * @author Laurent Wouters
 */
public class RuleEngine {
    /**
     * The specialized RDF rule engine backend
     */
    private class Backend extends org.xowl.store.rdf.RuleEngine {
        /**
         * Initializes this engine
         *
         * @param inputStore  The RDF store serving as input
         * @param outputStore The RDF store for the output
         */
        public Backend(BaseStore inputStore, BaseStore outputStore) {
            super(inputStore, outputStore);
        }

        @Override
        protected Node processOtherNode(org.xowl.store.rdf.Rule rule, Node node, Token token, Map<Node, Node> specials) {
            if (node.getNodeType() != DynamicNode.TYPE || evaluator == null)
                return node;
            Node result = specials.get(node);
            if (result != null)
                return result;
            evaluator.push(buildBindings(owlRules.get(rule).x, token, specials));
            result = Utils.getRDF(outputStore, evaluator.eval(((DynamicNode) node).getDynamicExpression()));
            specials.put(node, result);
            evaluator.pop();
            return result;
        }

        /**
         * Builds the bindings data for the evaluator from the specified information
         *
         * @param context  The translation context
         * @param token    The matching token in the rule engine
         * @param specials The existing special bindings
         * @return The bindings for the evaluator
         */
        private Bindings buildBindings(TranslationContext context, Token token, Map<Node, Node> specials) {
            Bindings bindings = new Bindings();
            for (Map.Entry<VariableNode, Node> entry : token.getBindings().entrySet()) {
                QueryVariable qvar = context.get(entry.getKey());
                Object value = Utils.getOWL(entry.getValue());
                bindings.bind(qvar, value);
            }
            for (Map.Entry<Node, Node> entry : specials.entrySet()) {
                if (entry.getKey().getNodeType() == VariableNode.TYPE) {
                    QueryVariable qvar = context.get((VariableNode) entry.getKey());
                    Object value = Utils.getOWL(entry.getValue());
                    bindings.bind(qvar, value);
                }
            }
            return bindings;
        }
    }

    /**
     * The XOWL store for the output
     */
    private final BaseStore outputStore;
    /**
     * The current evaluator
     */
    private final Evaluator evaluator;
    /**
     * The RDF backend
     */
    private final Backend backend;
    /**
     * The mapping of OWL to RDF rules
     */
    private final Map<Rule, org.xowl.store.rdf.Rule> rdfRules;
    /**
     * The mapping of RDF to OWL rules
     */
    private final Map<org.xowl.store.rdf.Rule, Couple<TranslationContext, Rule>> owlRules;

    /**
     * Gets the RDF backend
     *
     * @return The RDF backend
     */
    public org.xowl.store.rdf.RuleEngine getBackend() {
        return backend;
    }

    /**
     * Initializes this engine
     *
     * @param inputStore The store to operate over
     * @param evaluator  The evaluator
     */
    public RuleEngine(BaseStore inputStore, BaseStore outputStore, Evaluator evaluator) {
        this.outputStore = outputStore;
        this.evaluator = evaluator;
        this.backend = new Backend(inputStore, outputStore);
        this.rdfRules = new HashMap<>();
        this.owlRules = new HashMap<>();
    }

    /**
     * Adds the specified rule
     *
     * @param rule The rule to add
     */
    public void add(Rule rule, Ontology source, Ontology target, Ontology meta) {
        TranslationContext translationContext = new TranslationContext();
        GraphNode graphSource = getGraph(source, true);
        GraphNode graphTarget = getGraph(target, false);
        GraphNode graphMeta = getGraph(meta, false);
        org.xowl.store.rdf.Rule rdfRule = new org.xowl.store.rdf.Rule(rule.getHasIRI().getHasValue());
        Translator translator = new Translator(translationContext, outputStore, null);
        List<Axiom> positiveNormal = new ArrayList<>();
        List<Axiom> positiveMeta = new ArrayList<>();
        try {
            for (Assertion assertion : rule.getAllAntecedents()) {
                if (assertion.getIsPositive()) {
                    if (assertion.getIsMeta()) {
                        positiveMeta.addAll(assertion.getAllAxioms());
                    } else {
                        positiveNormal.addAll(assertion.getAllAxioms());
                    }
                } else {
                    if (assertion.getIsMeta()) {
                        rdfRule.getAntecedentMetaNegatives().add(translator.translate(assertion.getAllAxioms(), graphMeta));
                    } else {
                        rdfRule.getAntecedentSourceNegatives().add(translator.translate(assertion.getAllAxioms(), graphSource));
                    }
                }
            }
            rdfRule.getAntecedentSourcePositives().addAll(translator.translate(positiveNormal, graphSource));
            rdfRule.getAntecedentMetaPositives().addAll(translator.translate(positiveMeta, graphMeta));
            positiveNormal.clear();
            positiveMeta.clear();
            for (Assertion assertion : rule.getAllConsequents()) {
                if (assertion.getIsPositive()) {
                    if (assertion.getIsMeta()) {
                        positiveMeta.addAll(assertion.getAllAxioms());
                    } else {
                        positiveNormal.addAll(assertion.getAllAxioms());
                    }
                } else {
                    if (assertion.getIsMeta()) {
                        rdfRule.getConsequentMetaNegatives().addAll(translator.translate(assertion.getAllAxioms(), graphMeta));
                    } else {
                        rdfRule.getConsequentTargetNegatives().addAll(translator.translate(assertion.getAllAxioms(), graphTarget));
                    }
                }
            }
            rdfRule.getConsequentTargetPositives().addAll(translator.translate(positiveNormal, graphTarget));
            rdfRule.getConsequentMetaPositives().addAll(translator.translate(positiveMeta, graphMeta));
            positiveNormal.clear();
            positiveMeta.clear();
        } catch (TranslationException ex) {
            // TODO: report this
        }
        rdfRules.put(rule, rdfRule);
        owlRules.put(rdfRule, new Couple<>(translationContext, rule));
        backend.add(rdfRule);
    }

    /**
     * Removes the specified rule
     *
     * @param rule The rule to remove
     */
    public void remove(Rule rule) {
        backend.remove(rdfRules.get(rule));
    }

    /**
     * Removes the specified rule
     *
     * @param iri The IRI of the rule to remove
     */
    public void remove(String iri) {
        for (Rule rule : rdfRules.keySet()) {
            if (rule.getHasIRI().getHasValue().equals(iri)) {
                remove(rule);
                return;
            }
        }
    }

    /**
     * Flushes any outstanding changes in the input or the output
     */
    public void flush() {
        backend.flush();
    }

    /**
     * Gets the graph node for the specified ontology
     *
     * @param ontology      An ontology
     * @param allowsPattern Whether to allow the graph to be part of the pattern
     * @return The associated graph node
     */
    private GraphNode getGraph(Ontology ontology, boolean allowsPattern) {
        if (ontology == null) {
            if (allowsPattern)
                return null;
            else
                return outputStore.getIRINode(NodeManager.DEFAULT_GRAPH + "/" + UUID.randomUUID());
        }
        return outputStore.getIRINode(ontology.getHasIRI().getHasValue());
    }
}
