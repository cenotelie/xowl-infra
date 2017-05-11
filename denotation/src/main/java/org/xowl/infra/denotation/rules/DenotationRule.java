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

package org.xowl.infra.denotation.rules;

import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.RDFRule;
import org.xowl.infra.store.rdf.RDFRuleSimple;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a denotation rule, i.e. a rule that associates signs in a user's phrases to meaning elements as ontological entities (semes)
 *
 * @author Laurent Wouters
 */
public class DenotationRule implements Identifiable {
    /**
     * The rule's URI
     */
    private final String uri;
    /**
     * The rule's title
     */
    private final String title;
    /**
     * The rule's antecedents
     */
    private final List<SignAntecedent> antecedents;
    /**
     * The rule's consequents
     */
    private final List<SemeConsequent> consequents;

    /**
     * Initializes this rule
     *
     * @param uri   The rule's uri
     * @param title The rule's title
     */
    public DenotationRule(String uri, String title) {
        this.uri = uri;
        this.title = title;
        this.antecedents = new ArrayList<>();
        this.consequents = new ArrayList<>();
    }

    @Override
    public String getIdentifier() {
        return uri;
    }

    @Override
    public String getName() {
        return title;
    }

    /**
     * Gets whether this rule can be reused
     *
     * @return Whether this rule can be reused
     */
    public boolean isReusable() {
        for (SignAntecedent antecedent : antecedents) {
            if (antecedent instanceof SignReference)
                return false;
        }
        for (SemeConsequent consequent : consequents) {
            for (SignAntecedent bound : consequent.getBindings()) {
                if (bound instanceof SignReference)
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets the rule's antecedents
     *
     * @return The rule's antecedents
     */
    public List<SignAntecedent> getAntecedents() {
        return Collections.unmodifiableList(antecedents);
    }

    /**
     * Adds an antecedent to this rule
     *
     * @param antecedent The antecedent to add
     */
    public void addAntecedent(SignAntecedent antecedent) {
        antecedents.add(antecedent);
    }

    /**
     * Removes an antecedent from this rule
     *
     * @param antecedent The antecedent to remove
     */
    public void removeAntecedent(SignAntecedent antecedent) {
        antecedents.remove(antecedent);
    }

    /**
     * Gets the rule's consequents
     *
     * @return The rule's consequents
     */
    public List<SemeConsequent> getConsequents() {
        return Collections.unmodifiableList(consequents);
    }

    /**
     * Adds a consequent to this rule
     *
     * @param consequent The consequent to add
     */
    public void addConsequent(SemeConsequent consequent) {
        consequents.add(consequent);
    }

    /**
     * Removes a consequent from this rule
     *
     * @param consequent The consequent to remove
     */
    public void removeConsequent(SemeConsequent consequent) {
        consequents.remove(consequent);
    }

    /**
     * Builds the RDF rule that implements this denotation rule
     *
     * @param graphSigns The graph for the signs
     * @param graphSemes The graph for the semes
     * @param graphMeta  The graph for the metadata
     * @param nodes      The node manager to use
     * @return The RDF rule
     */
    public RDFRule buildRdfRule(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes) {
        RDFRuleSimple result = new RDFRuleSimple(uri, true, null);
        DenotationRuleContext context = new DenotationRuleContext(result);
        for (SignAntecedent antecedent : antecedents)
            antecedent.buildRdf(graphSigns, graphSemes, graphMeta, nodes, context);
        for (SemeConsequent consequent : consequents)
            consequent.buildRdf(graphSigns, graphSemes, graphMeta, nodes, context);
        return result;
    }
}
