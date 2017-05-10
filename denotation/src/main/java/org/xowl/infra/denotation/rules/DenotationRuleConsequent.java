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

import org.xowl.infra.denotation.Denotation;
import org.xowl.infra.denotation.phrases.SignProperty;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.rdf.SubjectNode;
import org.xowl.infra.store.storage.NodeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a consequent in a denotation rule
 *
 * @author Laurent Wouters
 */
public abstract class DenotationRuleConsequent {
    /**
     * The antecedent elements to bind to this consequent
     */
    protected Collection<DenotationRuleAntecedent> bindings;

    /**
     * Gets the antecedent elements to bind to this consequent
     *
     * @return The antecedent elements to bind to this consequent
     */
    public Collection<DenotationRuleAntecedent> getBindings() {
        if (bindings == null)
            return Collections.emptyList();
        return Collections.unmodifiableCollection(bindings);
    }

    /**
     * Adds an antecedent to bind to this consequent
     *
     * @param antecedent The antecedent to bind
     */
    public void addBindings(DenotationRuleAntecedent antecedent) {
        if (bindings == null)
            bindings = new ArrayList<>();
        bindings.add(antecedent);
    }

    /**
     * Removes an antecedent from the bindings to this consequent
     *
     * @param antecedent The antecedent to un-bind
     */
    public void removeBinding(DenotationRuleAntecedent antecedent) {
        if (bindings == null)
            return;
        bindings.remove(antecedent);
    }

    /**
     * Builds the RDF rule with this consequent
     *
     * @param graphSigns The graph for the signs
     * @param graphSemes The graph for the semes
     * @param graphMeta  The graph for the metadata
     * @param nodes      The node manager to use
     * @param context    The current context
     */
    public void buildRdf(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, DenotationRuleContext context) {
        buildRdfBindings(graphSigns, graphSemes, graphMeta, nodes, getSubject(nodes, context), context);
    }

    /**
     * Gets the subject for this consequent
     *
     * @param nodes   The node manager to use
     * @param context The current context
     * @return The subject
     */
    protected abstract SubjectNode getSubject(NodeManager nodes, DenotationRuleContext context);

    /**
     * Builds the RDF rule with this antecedent
     *
     * @param graphSigns The graph for the signs
     * @param graphSemes The graph for the semes
     * @param graphMeta  The graph for the metadata
     * @param nodes      The node manager to use
     * @param parent     The node representing this consequent
     * @param context    The current context
     */
    protected void buildRdfBindings(GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, NodeManager nodes, SubjectNode parent, DenotationRuleContext context) {
        if (bindings != null) {
            for (DenotationRuleAntecedent antecedent : bindings) {
                SubjectNode subject;
                if (antecedent instanceof SignReference) {
                    subject = nodes.getIRINode(((SignReference) antecedent).getSignId());
                } else {
                    subject = context.getVariable(((SignProperty) antecedent).getIdentifier());
                }
                context.getRdfRule().addConsequentPositive(new Quad(graphMeta,
                        subject,
                        nodes.getIRINode(Denotation.META_TRACE),
                        parent
                ));
            }
        }
    }
}
