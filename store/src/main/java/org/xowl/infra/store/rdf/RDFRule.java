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

import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.EvaluationException;
import org.xowl.infra.store.execution.EvaluationUtils;
import org.xowl.infra.store.execution.Evaluator;
import org.xowl.infra.store.storage.NodeManager;

import java.util.*;

/**
 * Represents a RDF rule for a RDF rule engine
 *
 * @author Laurent Wouters
 */
public abstract class RDFRule {
    /**
     * The rule's identifying IRI
     */
    protected final String iri;
    /**
     * The rule's guard, if any
     */
    protected final EvaluableExpression guard;
    /**
     * The source for this rule
     */
    protected final String source;
    /**
     * The variable resolver to use for the consequents
     */
    protected VariableResolver resolver;

    /**
     * Gets the rule's identifying IRI
     *
     * @return The rule's identifying IRI
     */
    public String getIRI() {
        return iri;
    }

    /**
     * Gets the source for this rule
     *
     * @return The source for this rule
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the associated variable resolver for the consequents
     *
     * @return The associated variable resolver for the consequents
     */
    public VariableResolver getResolver() {
        return resolver;
    }

    /**
     * Sets the associated variable resolver for the consequents
     *
     * @param resolver The resolver for the consequents
     */
    public void setResolver(VariableResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Initializes this rule
     *
     * @param iri   The rule's identifying iri
     * @param guard The rule's guard, if any
     */
    public RDFRule(String iri, EvaluableExpression guard) {
        this(iri, guard, null);
    }

    /**
     * Initializes this rule
     *
     * @param iri    The rule's identifying iri
     * @param guard  The rule's guard, if any
     * @param source The source for this rule
     */
    public RDFRule(String iri, EvaluableExpression guard, String source) {
        this.iri = iri;
        this.guard = guard;
        this.source = source;
        this.resolver = VariableResolveStandard.INSTANCE;
    }

    /**
     * Gets the patterns that are used for matching this rule
     *
     * @return The patterns
     */
    public abstract Collection<RDFPattern> getPatterns();

    /**
     * Gets the variables in the antacedents of this rule
     *
     * @return The variables
     */
    public abstract Collection<VariableNode> getAntecedentVariables();

    /**
     * When a pattern for this rule has been matched
     *
     * @param handler The production handler for this rule
     * @param pattern The matched pattern
     * @param match   The match
     */
    public abstract void onPatternMatched(RDFRuleEngine.ProductionHandler handler, RDFPattern pattern, RDFPatternMatch match);

    /**
     * When a match for a pattern in this rule has been invalidated
     *
     * @param handler The production handler for this rule
     * @param pattern The invalidated pattern
     * @param match   The invalidated match
     */
    public abstract void onPatternDematched(RDFRuleEngine.ProductionHandler handler, RDFPattern pattern, RDFPatternMatch match);

    /**
     * Gets the changeset for this rule's production for a specified execution
     *
     * @param execution The execution data
     * @param nodes     The node manager for producing the changeset
     * @param evaluator The current evaluator
     * @return The production's changeset
     */
    public abstract Changeset produce(RDFRuleExecution execution, NodeManager nodes, Evaluator evaluator);

    /**
     * Determines whether this rule can be fired for the specified execution
     *
     * @param execution The candidate rule's execution
     * @param evaluator The current evaluator
     * @return Whether the rule can be fired
     */
    protected boolean canFire(RDFRuleExecution execution, Evaluator evaluator) {
        if (guard != null && evaluator != null) {
            try {
                Object value = evaluator.eval(execution.getEvaluatorBindings(), guard);
                return EvaluationUtils.bool(value);
            } catch (EvaluationException exception) {
                Logging.get().error(exception);
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the unique variable nodes in a pattern of quads (only look in in the positive quads)
     *
     * @param variables The buffer for the result
     * @param pattern   The pattern to inspect
     */
    protected static void findVariables(Collection<VariableNode> variables, RDFPattern pattern) {
        for (Quad quad : pattern.getPositives())
            findVariables(variables, quad);
    }

    /**
     * Finds the unique variable nodes in a quad
     *
     * @param variables The buffer for the result
     * @param quad      The quad to inspect
     */
    protected static void findVariables(Collection<VariableNode> variables, Quad quad) {
        findVariables(variables, quad.getSubject());
        findVariables(variables, quad.getProperty());
        findVariables(variables, quad.getObject());
        findVariables(variables, quad.getGraph());
    }

    /**
     * Inspect a node when looking for variables
     *
     * @param variables The buffer for the result
     * @param node      The node to inspect
     */
    protected static void findVariables(Collection<VariableNode> variables, Node node) {
        if (node == null)
            return;
        if (node.getNodeType() != Node.TYPE_VARIABLE)
            return;
        VariableNode variable = (VariableNode) node;
        if (!variables.contains(variable))
            variables.add(variable);
    }

    /**
     * Processes the specified quads
     *
     * @param execution The execution data
     * @param nodes     The node manager for producing the changeset
     * @param evaluator The current evaluator
     * @param pattern   The quads to process
     * @return The processed quad
     */
    protected Changeset produceQuads(RDFRuleExecution execution, NodeManager nodes, Evaluator evaluator, RDFPattern pattern) {
        List<Quad> positives = new ArrayList<>();
        List<Quad> negatives = new ArrayList<>();
        Map<Node, Node> cache = new HashMap<>();
        RDFUtils.instantiateQuads(nodes, evaluator, resolver, execution.getSolution(), cache, pattern.getPositives(), positives, false);
        for (Collection<Quad> collection : pattern.getNegatives()) {
            RDFUtils.instantiateQuads(nodes, evaluator, resolver, execution.getSolution(), cache, collection, negatives, false);
        }
        if (negatives.isEmpty())
            return Changeset.fromAdded(positives);
        if (positives.isEmpty())
            return Changeset.fromRemoved(negatives);
        return Changeset.fromAddedRemoved(positives, negatives);
    }

    @Override
    public String toString() {
        return iri;
    }
}
