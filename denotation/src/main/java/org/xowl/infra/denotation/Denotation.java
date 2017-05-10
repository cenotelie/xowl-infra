/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.denotation;

import org.xowl.infra.denotation.phrases.Phrase;
import org.xowl.infra.denotation.phrases.Sign;
import org.xowl.infra.denotation.rules.DenotationRule;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.StoreFactory;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.utils.logging.Logging;

import java.util.*;

/**
 * Represents the user's denotation of a specific phrase
 *
 * @author Laurent Wouters
 */
public class Denotation {
    /**
     * The default graphs for the signs
     */
    public static final String GRAPH_SIGNS = "http://xowl.org/infra/denotation/signs";
    /**
     * The default graphs for the semes
     */
    public static final String GRAPH_SEMES = "http://xowl.org/infra/denotation/semes";
    /**
     * The trace relation from a sign to a seme
     */
    public static final String META_TRACE = "http://xowl.org/infra/denotation/schema#trace";
    /**
     * The matchedBy relation from a sign to a matching rule
     */
    public static final String META_MATCHED_BY = "http://xowl.org/infra/denotation/schema#matchedBy";

    /**
     * The input phrase
     */
    private final Phrase phrase;
    /**
     * The denotation rules for this denotation
     */
    private final Map<String, DenotationRule> rules;
    /**
     * The map to the RDF rules
     */
    private final Map<DenotationRule, RDFRule> rdfRules;
    /**
     * The backend repository
     */
    private final RepositoryRDF repository;
    /**
     * The graphs for the signs
     */
    private final GraphNode graphSigns;
    /**
     * The graph for the semes
     */
    private final GraphNode graphSemes;
    /**
     * The graph for the metadata
     */
    private final GraphNode graphMetadata;

    /**
     * Initializes an empty denotation
     *
     * @param phrase The input phrase
     */
    public Denotation(Phrase phrase) {
        this(phrase, GRAPH_SEMES);
    }

    /**
     * Initializes an empty denotation
     *
     * @param phrase     The input phrase
     * @param graphSemes The graph of the semes
     */
    public Denotation(Phrase phrase, String graphSemes) {
        this.phrase = phrase;
        this.rules = new HashMap<>();
        this.rdfRules = new HashMap<>();
        this.repository = new RepositoryRDF(StoreFactory.create().inMemory().withReasoning().make());
        this.graphSigns = repository.getStore().getIRINode(GRAPH_SIGNS);
        this.graphSemes = repository.getStore().getIRINode(graphSemes);
        this.graphMetadata = repository.getStore().getIRINode(IRIs.GRAPH_META);
        try {
            Collection<Quad> buffer = new ArrayList<>();
            phrase.serializeRdf(repository.getStore(), graphSigns, buffer);
            repository.getStore().insert(Changeset.fromAdded(buffer));
        } catch (UnsupportedNodeType exception) {
            // cannot happen
            Logging.get().error(exception);
        }
    }

    /**
     * Gets the input phrase
     *
     * @return The input phrase
     */
    public Phrase getPhrase() {
        return phrase;
    }

    /**
     * Gets the denotation rules for this denotation
     *
     * @return The denotation rules for this denotation
     */
    public Collection<DenotationRule> getRules() {
        return Collections.unmodifiableCollection(rules.values());
    }

    /**
     * Gets the denotation rule for the specified identifier
     *
     * @param identifier The identifier of a rule
     * @return The rule, or null if there is none
     */
    public DenotationRule getRule(String identifier) {
        return rules.get(identifier);
    }

    /**
     * Adds a rule to this denotation
     *
     * @param rule The rule to add
     */
    public void addRule(DenotationRule rule) {
        rules.put(rule.getIdentifier(), rule);
        RDFRule rdfRule = rule.buildRdfRule(graphSigns, graphSemes, graphMetadata, repository.getStore());
        if (rdfRule != null) {
            rdfRules.put(rule, rdfRule);
            repository.getRDFRuleEngine().add(rdfRule);
            repository.getRDFRuleEngine().flush();
        }
    }

    /**
     * Removes a rule from this denotation
     *
     * @param rule The rule to remove
     */
    public void removeRule(DenotationRule rule) {
        if (rules.remove(rule.getIdentifier()) != null) {
            RDFRule rdfRule = rdfRules.remove(rule);
            if (rdfRule != null) {
                repository.getRDFRuleEngine().remove(rdfRule);
            }
        }
    }

    /**
     * Gets the rules matching the specified sign
     *
     * @param sign A sign
     * @return The rules matching the specified sign
     */
    public Collection<DenotationRule> getRulesMatching(Sign sign) {
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(graphMetadata,
                    repository.getStore().getIRINode(sign.getIdentifier()),
                    repository.getStore().getIRINode(META_MATCHED_BY),
                    null);
            if (!iterator.hasNext())
                return Collections.emptyList();
            Collection<DenotationRule> result = new ArrayList<>();
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                String ruleIri = ((IRINode) quad.getObject()).getIRIValue();
                DenotationRule rule = rules.get(ruleIri);
                if (rule != null)
                    result.add(rule);
            }
            return result;
        } catch (UnsupportedNodeType exception) {
            // should not happen
            Logging.get().error(exception);
            return Collections.emptyList();
        }
    }

    /**
     * Gets the signs matched by the specified rule
     *
     * @param rule A rule
     * @return The signs matched by the specified rule
     */
    public Collection<Sign> getSignsMatchedBy(DenotationRule rule) {
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(graphMetadata,
                    null,
                    repository.getStore().getIRINode(META_MATCHED_BY),
                    repository.getStore().getIRINode(rule.getIdentifier()));
            if (!iterator.hasNext())
                return Collections.emptyList();
            Collection<Sign> result = new ArrayList<>();
            while (iterator.hasNext()) {
                Quad quad = iterator.next();
                String signIri = ((IRINode) quad.getSubject()).getIRIValue();
                Sign sign = phrase.getSign(signIri);
                if (sign != null)
                    result.add(sign);
            }
            return result;
        } catch (UnsupportedNodeType exception) {
            // should not happen
            Logging.get().error(exception);
            return Collections.emptyList();
        }
    }

    /**
     * Gets the quads representing the semantics of the input phrase
     *
     * @return The quads representing the semantics of the input phrase
     */
    public Collection<Quad> getSemantic() {
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(graphSemes);
            if (!iterator.hasNext())
                return Collections.emptyList();
            Collection<Quad> quads = new ArrayList<>();
            while (iterator.hasNext())
                quads.add(iterator.next());
            return quads;
        } catch (UnsupportedNodeType exception) {
            // should not happen
            Logging.get().error(exception);
            return Collections.emptyList();
        }
    }

    /**
     * Gets the quads representing the semantics of the specified sign
     *
     * @param sign A sign
     * @return The quads representing the semantics of the sign
     */
    public Collection<Quad> getSemanticsOf(Sign sign) {
        try {
            Iterator<Quad> iterator = repository.getStore().getAll(graphMetadata,
                    repository.getStore().getIRINode(sign.getIdentifier()),
                    repository.getStore().getIRINode(META_TRACE),
                    null);
            if (!iterator.hasNext())
                return Collections.emptyList();
            Collection<SubjectNode> semes = new ArrayList<>();
            while (iterator.hasNext())
                semes.add((SubjectNode) iterator.next().getObject());
            if (semes.isEmpty())
                return Collections.emptyList();
            Collection<Quad> result = new ArrayList<>();
            for (SubjectNode seme : semes) {
                iterator = repository.getStore().getAll(graphSemes,
                        seme,
                        null,
                        null);
                while (iterator.hasNext())
                    result.add(iterator.next());
            }
            return result;
        } catch (UnsupportedNodeType exception) {
            // should not happen
            Logging.get().error(exception);
            return Collections.emptyList();
        }
    }
}
