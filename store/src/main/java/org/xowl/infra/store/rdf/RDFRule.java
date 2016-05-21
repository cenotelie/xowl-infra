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
import org.xowl.infra.store.rete.Token;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.collections.Couple;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Gets the pattern parts that can are used to match this rule
     *
     * @return The pattern parts
     */
    public abstract List<RDFRulePatternPart> getPatternParts();

    /**
     * Gets whether this rule is indeed triggered by the specified potential matches of its patterns
     *
     * @param executions The previous executions
     * @param tokens     The matching tokens
     * @return The rule corresponding execution if the rule is triggered, null otherwise
     */
    public abstract RDFRuleExecution isTriggered(Collection<RDFRuleExecution> executions, Token[] tokens);

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
        result = execution.specials.get(variable);
        if (result != null)
            return result;
        if (createIRI)
            result = nodes.getIRINode((GraphNode) null);
        else
            result = nodes.getBlankNode();
        execution.specials.put(variable, result);
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
        Node result = execution.specials.get(dynamicNode);
        if (result != null)
            return result;
        evaluator.push(buildBindings(execution));
        result = RDFUtils.getRDF(nodes, evaluator.eval(dynamicNode.getDynamicExpression()));
        execution.specials.put(dynamicNode, result);
        evaluator.pop();
        return result;
    }

    /**
     * Builds the bindings data for the evaluator from the specified information
     *
     * @param execution The execution data
     * @return The bindings for the evaluator
     */
    private static Map<String, Object> buildBindings(RDFRuleExecution execution) {
        Map<String, Object> bindings = new HashMap<>();
        for (Token token : execution.tokens) {
            for (Couple<VariableNode, Node> entry : token.getBindings()) {
                if (!bindings.containsKey(entry.x.getName()))
                    bindings.put(entry.x.getName(), RDFUtils.getNative(entry.y));
            }
        }
        for (Map.Entry<Node, Node> entry : execution.specials.entrySet()) {
            if (entry.getKey().getNodeType() == Node.TYPE_VARIABLE) {
                bindings.put(((VariableNode) entry.getValue()).getName(), RDFUtils.getNative(entry.getValue()));
            }
        }
        return bindings;
    }

    @Override
    public String toString() {
        return iri;
    }
}
