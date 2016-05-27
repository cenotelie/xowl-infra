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

import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.owl.DynamicNode;
import org.xowl.infra.store.storage.NodeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
     * Gets the rule's identifying IRI
     *
     * @return The rule's identifying IRI
     */
    public String getIRI() {
        return iri;
    }

    /**
     * Initializes this rule
     *
     * @param iri The rule's identifying iri
     */
    public RDFRule(String iri) {
        this.iri = iri;
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
        for (Quad quad : pattern.getPositives()) {
            Quad result = produceQuad(execution, nodes, evaluator, quad);
            if (result == null)
                return null;
            positives.add(result);
        }
        for (Collection<Quad> collection : pattern.getNegatives()) {
            for (Quad quad : collection) {
                Quad result = produceQuad(execution, nodes, evaluator, quad);
                if (result == null)
                    return null;
                negatives.add(result);
            }
        }
        if (negatives.isEmpty())
            return Changeset.fromAdded(positives);
        if (positives.isEmpty())
            return Changeset.fromRemoved(negatives);
        return Changeset.fromAddedRemoved(positives, negatives);
    }

    /**
     * Processes the specified quad
     *
     * @param execution The execution data
     * @param nodes     The node manager for producing the changeset
     * @param evaluator The current evaluator
     * @param quad      The quad to process
     * @return The processed quad
     */
    protected Quad produceQuad(RDFRuleExecution execution, NodeManager nodes, Evaluator evaluator, Quad quad) {
        Node nodeGraph = produceResolveNode(execution, nodes, evaluator, quad.getGraph(), true);
        Node nodeSubject = produceResolveNode(execution, nodes, evaluator, quad.getSubject(), false);
        Node nodeProperty = produceResolveNode(execution, nodes, evaluator, quad.getProperty(), false);
        Node nodeObject = produceResolveNode(execution, nodes, evaluator, quad.getObject(), false);
        if ((!(nodeGraph instanceof GraphNode)) || (!(nodeSubject instanceof SubjectNode)) || (!(nodeProperty instanceof Property)))
            return null;
        return new Quad((GraphNode) nodeGraph, (SubjectNode) nodeSubject, (Property) nodeProperty, nodeObject);
    }

    /**
     * Processes the specified node
     *
     * @param execution The execution data
     * @param nodes     The node manager for producing the changeset
     * @param evaluator The current evaluator
     * @param node      The node to process
     * @param createIRI Whether to create an IRI node or a blank node in the case of an unbound variable
     * @return The processed node
     */
    private Node produceResolveNode(RDFRuleExecution execution, NodeManager nodes, Evaluator evaluator, Node node, boolean createIRI) {
        if (node == null)
            return produceResolveVariable(execution, nodes, null, createIRI);
        switch (node.getNodeType()) {
            case Node.TYPE_VARIABLE:
                return produceResolveVariable(execution, nodes, (VariableNode) node, createIRI);
            case Node.TYPE_DYNAMIC:
                return produceResolveDynamic(execution, nodes, evaluator, (DynamicNode) node);
            default:
                return node;
        }
    }

    /**
     * Resolves the specified variable node
     *
     * @param execution The execution data
     * @param nodes     The node manager for producing the changeset
     * @param variable  A variable node
     * @param createIRI Whether to create an IRI node or a blank node in the case of an unbound variable
     * @return The variable value
     */
    protected Node produceResolveVariable(RDFRuleExecution execution, NodeManager nodes, VariableNode variable, boolean createIRI) {
        Node result = execution.getBinding(variable);
        if (result != null)
            return result;
        result = execution.getSpecial(variable);
        if (result != null)
            return result;
        if (createIRI)
            result = nodes.getIRINode((GraphNode) null);
        else
            result = nodes.getBlankNode();
        execution.bindSpecial(variable, result);
        return result;
    }

    /**
     * Resolves the specified dynamic node node
     *
     * @param execution   The execution data
     * @param nodes       The node manager for producing the changeset
     * @param evaluator   The current evaluator
     * @param dynamicNode A dynamic node
     * @return The variable value
     */
    protected Node produceResolveDynamic(RDFRuleExecution execution, NodeManager nodes, Evaluator evaluator, DynamicNode dynamicNode) {
        if (evaluator == null)
            return dynamicNode;
        Node result = execution.getSpecial(dynamicNode);
        if (result != null)
            return result;
        evaluator.push(execution.getEvaluatorBindings());
        result = RDFUtils.getRDF(nodes, evaluator.eval(dynamicNode.getDynamicExpression()));
        execution.bindSpecial(dynamicNode, result);
        evaluator.pop();
        return result;
    }

    @Override
    public String toString() {
        return iri;
    }
}
