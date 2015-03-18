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
package org.xowl.store.owl;

import org.xowl.lang.owl2.Axiom;
import org.xowl.lang.owl2.Ontology;
import org.xowl.lang.rules.Assertion;
import org.xowl.lang.rules.Rule;
import org.xowl.store.rdf.GraphNode;
import org.xowl.store.rdf.Node;
import org.xowl.store.rdf.RDFStore;
import org.xowl.store.rdf.VariableNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a rule engine operating over an OWL dataset
 *
 * @author Laurent Wouters
 */
public class RuleEngine {
    /**
     * The XOWL store to operate over
     */
    private XOWLStore store;
    /**
     * The current evaluator
     */
    private Evaluator evaluator;
    /**
     * The RDF backend
     */
    private org.xowl.store.rdf.RuleEngine backend;
    /**
     * The current rules
     */
    private Map<Rule, org.xowl.store.rdf.Rule> rules;

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
     * @param store     The store to operate over
     * @param evaluator The evaluator
     */
    public RuleEngine(XOWLStore store, final Evaluator evaluator) {
        this.store = store;
        this.evaluator = evaluator;
        this.backend = new org.xowl.store.rdf.RuleEngine(store) {
            protected Node processOtherNode(Node node) {
                if (node.getNodeType() == DynamicNode.TYPE) {
                    DynamicNode dynamicNode = (DynamicNode) node;
                    if (RuleEngine.this.evaluator == null)
                        return dynamicNode;
                    return RuleEngine.this.store.getRDF(RuleEngine.this.evaluator.eval(dynamicNode.getDynamicExpression()));
                }
                return node;
            }
        };
        this.rules = new HashMap<>();
    }

    /**
     * Adds the specified rule
     *
     * @param rule The rule to add
     */
    public void add(Rule rule, Ontology source, Ontology target, Ontology meta) {
        GraphNode graphSource = getGraph(source, true);
        GraphNode graphTarget = getGraph(target, false);
        GraphNode graphMeta = getGraph(meta, false);
        org.xowl.store.rdf.Rule rdfRule = new org.xowl.store.rdf.Rule(rule.getHasIRI().getHasValue());
        Translator translator = new Translator(new TranslationContext(), store, evaluator);
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
        rules.put(rule, rdfRule);
        backend.add(rdfRule);
    }

    /**
     * Removes the specified rule
     *
     * @param rule The rule to remove
     */
    public void remove(Rule rule) {
        backend.remove(rules.get(rule));
    }

    /**
     * Removes the specified rule
     *
     * @param iri The IRI of the rule to remove
     */
    public void remove(String iri) {
        for (Rule rule : rules.keySet()) {
            if (rule.getHasIRI().getHasValue().equals(iri)) {
                remove(rule);
                return;
            }
        }
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
                return new VariableNode("__graph__");
            else
                return store.getNodeIRI(RDFStore.createAnonymousGraph());
        }
        return store.getNodeIRI(ontology.getHasIRI().getHasValue());
    }
}
