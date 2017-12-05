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
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.rdf.DatasetNodes;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the lexical context of a rule
 *
 * @author Laurent Wouters
 */
public class DenotationRuleContext {
    /**
     * The current node manager
     */
    private final DatasetNodes nodes;
    /**
     * The graph for the signs
     */
    private final GraphNode graphSigns;
    /**
     * The graph for the semes
     */
    private final GraphNode graphSemes;
    /**
     * The graph for the metadata
     */
    private final GraphNode graphMeta;
    /**
     * The variable nodes
     */
    private final Map<String, VariableNode> variables;
    /**
     * The counter for generated variables
     */
    private int counter;
    /**
     * The RDF rule
     */
    private final RDFRuleSimple rule;
    /**
     * Special resolvers to use for the rule
     */
    private final Map<VariableNode, VariableResolver> resolvers;

    /**
     * Initializes this context
     *
     * @param nodes      The node manager to use
     *                   param graphSigns The graph for the signs
     * @param graphSemes The graph for the semes
     * @param graphMeta  The graph for the metadata
     * @param rule       The RDF rule
     */
    public DenotationRuleContext(DatasetNodes nodes, GraphNode graphSigns, GraphNode graphSemes, GraphNode graphMeta, RDFRuleSimple rule) {
        this.nodes = nodes;
        this.graphSigns = graphSigns;
        this.graphSemes = graphSemes;
        this.graphMeta = graphMeta;
        this.variables = new HashMap<>();
        this.counter = 0;
        this.rule = rule;
        this.resolvers = new HashMap<>();
    }

    /**
     * Gets the current node manager
     *
     * @return The current node manager
     */
    public DatasetNodes getNodes() {
        return nodes;
    }

    /**
     * Gets the graph for the signs
     *
     * @return The graph for the signs
     */
    public GraphNode getGraphSigns() {
        return graphSigns;
    }

    /**
     * Gets the graph for the semes
     *
     * @return The graph for the semes
     */
    public GraphNode getGraphSemes() {
        return graphSemes;
    }

    /**
     * Gets the graph for the metadata
     *
     * @return The graph for the metadata
     */
    public GraphNode getGraphMeta() {
        return graphMeta;
    }

    /**
     * Gets the current RDF rule
     *
     * @return The current RDF rule
     */
    public RDFRuleSimple getRdfRule() {
        return rule;
    }

    /**
     * Gets the variable for the specified identifier
     *
     * @param identifier The identifier
     * @return The associated variable
     */
    public VariableNode getVariable(String identifier) {
        VariableNode variable = variables.get(identifier);
        if (variable == null) {
            variable = new VariableNode(identifier);
            variables.put(identifier, variable);
        }
        return variable;
    }

    /**
     * Creates a new unique variable
     *
     * @return The variable
     */
    public VariableNode getVariable() {
        VariableNode variable = new VariableNode("__gen_" + counter);
        counter++;
        return variable;
    }

    /**
     * Adds a special variable resolver to be attached to the RDF rule
     *
     * @param variable The target variable node
     * @param resolver The associated resolver
     */
    public void addResolver(VariableNode variable, VariableResolver resolver) {
        resolvers.put(variable, resolver);
    }

    /**
     * Attaches the special resolvers to the rule
     */
    public void attachResolvers() {
        VariableResolver iriResolver = new VariableResolverIrisOf(nodes.getIRINode(Denotation.GRAPH_SEMES));
        if (resolvers.isEmpty())
            rule.setResolver(iriResolver);
        else {
            VariableResolverComposite composite = new VariableResolverComposite(iriResolver);
            for (Map.Entry<VariableNode, VariableResolver> entry : resolvers.entrySet()) {
                composite.addResolver(entry.getKey(), entry.getValue());
            }
            rule.setResolver(composite);
        }
    }
}
