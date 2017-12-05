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
package org.xowl.infra.store.owl;

import fr.cenotelie.commons.utils.collections.Couple;
import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.lang.owl2.Axiom;
import org.xowl.infra.lang.owl2.Ontology;
import org.xowl.infra.lang.rules.Assertion;
import org.xowl.infra.lang.rules.Rule;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.execution.Evaluator;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.rdf.Dataset;

import java.util.*;

/**
 * Represents a rule engine operating over an OWL dataset
 *
 * @author Laurent Wouters
 */
public class OWLRuleEngine {
    /**
     * The XOWL store for the output
     */
    private final Dataset outputStore;
    /**
     * The RDF backend
     */
    private final RDFRuleEngine backend;
    /**
     * The mapping of OWL to RDF rules
     */
    private final Map<Rule, RDFRule> rdfRules;
    /**
     * The mapping of RDF to OWL rules
     */
    private final Map<RDFRule, Couple<TranslationContext, Rule>> owlRules;

    /**
     * Gets the RDF backend
     *
     * @return The RDF backend
     */
    public RDFRuleEngine getBackend() {
        return backend;
    }

    /**
     * Initializes this engine
     *
     * @param inputStore  The store to operate over
     * @param outputStore The store to output produced axioms
     * @param evaluator   The evaluator
     */
    public OWLRuleEngine(Dataset inputStore, Dataset outputStore, Evaluator evaluator) {
        this.outputStore = outputStore;
        this.backend = new RDFRuleEngine(inputStore, outputStore, evaluator);
        this.rdfRules = new HashMap<>();
        this.owlRules = new HashMap<>();
    }

    /**
     * Adds the specified rule
     *
     * @param rule   The rule to add
     * @param source The source ontology (where axioms are matched)
     * @param target the target ontology (where axioms are produced)
     * @param meta   The meta ontology (where axioms are both matched and produced)
     */
    public void add(Rule rule, Ontology source, Ontology target, Ontology meta) {
        TranslationContext translationContext = new TranslationContext();
        GraphNode graphSource = getGraph(source, true);
        GraphNode graphTarget = getGraph(target, false);
        GraphNode graphMeta = getGraph(meta, false);
        RDFRuleSimple rdfRule = new RDFRuleSimple(rule.getHasIRI().getHasValue(), false, null);
        Translator translator = new Translator(translationContext, outputStore);
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
                        rdfRule.addAntecedentNegatives(translator.translate(assertion.getAllAxioms(), graphMeta));
                    } else {
                        rdfRule.addAntecedentNegatives(translator.translate(assertion.getAllAxioms(), graphSource));
                    }
                }
            }
            for (Quad quad : translator.translate(positiveNormal, graphSource))
                rdfRule.addAntecedentPositive(quad);
            for (Quad quad : translator.translate(positiveMeta, graphMeta))
                rdfRule.addAntecedentPositive(quad);
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
                        for (Quad quad : translator.translate(assertion.getAllAxioms(), graphMeta))
                            rdfRule.addConsequentNegative(quad);
                    } else {
                        for (Quad quad : translator.translate(assertion.getAllAxioms(), graphTarget))
                            rdfRule.addConsequentNegative(quad);
                    }
                }
            }
            for (Quad quad : translator.translate(positiveNormal, graphTarget))
                rdfRule.addConsequentPositive(quad);
            for (Quad quad : translator.translate(positiveMeta, graphMeta))
                rdfRule.addConsequentPositive(quad);
            positiveNormal.clear();
            positiveMeta.clear();
        } catch (TranslationException exception) {
            Logging.get().error(exception);
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
                return outputStore.getIRINode(IRIs.GRAPH_DEFAULT + "/" + UUID.randomUUID());
        }
        return outputStore.getIRINode(ontology.getHasIRI().getHasValue());
    }
}
