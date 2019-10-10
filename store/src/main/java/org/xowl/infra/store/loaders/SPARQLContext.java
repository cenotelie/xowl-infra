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

package org.xowl.infra.store.loaders;

import org.xowl.infra.store.rdf.BlankNode;
import org.xowl.infra.store.rdf.DatasetNodes;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.VariableNode;

import java.util.*;

/**
 * Represents the lexical context of a SPARQL loader
 *
 * @author Laurent Wouters
 */
class SPARQLContext {
    /**
     * The prefix to use in the name of variables emitted in place of blank nodes
     */
    private static final String GEN_VAR_PREFIX = "__blank_";

    /**
     * The RDF nodes management
     */
    private final DatasetNodes nodes;
    /**
     * Whether to emit a variable when a blank node is requested.
     * This flag is used to build quad templates that existing blank nodes in a dataset.
     */
    private final boolean emitVariableForBlank;
    /**
     * The context's default IRIs
     */
    private final Collection<String> defaultIRIs;
    /**
     * The context's named IRIs
     */
    private final Collection<String> namedIRIs;
    /**
     * Map of the current blank nodes
     */
    private final Map<String, BlankNode> blanks;
    /**
     * Map of the current variable nodes
     */
    private final Map<String, VariableNode> variables;

    /**
     * Initializes an empty context
     *
     * @param nodes The RDF nodes management
     */
    public SPARQLContext(DatasetNodes nodes) {
        this.nodes = nodes;
        this.emitVariableForBlank = false;
        this.defaultIRIs = new ArrayList<>();
        this.namedIRIs = new ArrayList<>();
        this.blanks = new HashMap<>();
        this.variables = new HashMap<>();
    }

    /**
     * Initializes an empty context
     *
     * @param nodes                The RDF nodes management
     * @param emitVariableForBlank Whether to emit a variable when a blank node is requested.
     */
    public SPARQLContext(DatasetNodes nodes, boolean emitVariableForBlank) {
        this.nodes = nodes;
        this.emitVariableForBlank = emitVariableForBlank;
        this.defaultIRIs = new ArrayList<>();
        this.namedIRIs = new ArrayList<>();
        this.blanks = new HashMap<>();
        this.variables = new HashMap<>();
    }

    /**
     * Initializes this context from the specified parent
     *
     * @param parent A parent context
     */
    public SPARQLContext(SPARQLContext parent) {
        this.nodes = parent.nodes;
        this.emitVariableForBlank = parent.emitVariableForBlank;
        this.defaultIRIs = new ArrayList<>(parent.defaultIRIs);
        this.namedIRIs = new ArrayList<>(parent.namedIRIs);
        this.blanks = new HashMap<>(parent.blanks);
        this.variables = new HashMap<>(parent.variables);
    }

    /**
     * Gets whether the current context defines a custom dataset
     *
     * @return true if the context defines a custom dataset
     */
    public boolean isDatasetDefined() {
        return (!defaultIRIs.isEmpty() || !namedIRIs.isEmpty());
    }

    /**
     * Gets the IRIs of the DEFAULT graphs (if any) in the custom dataset defined by this context
     *
     * @return The IRIs of the DEFAULT graphs
     */
    public Collection<String> getDefaultGraphs() {
        return Collections.unmodifiableCollection(defaultIRIs);
    }

    /**
     * Gets the IRIs of the NAMED graphs (if any) in the custom dataset defined by this context
     *
     * @return The IRIs of the NAMED graphs
     */
    public Collection<String> getNamedGraphs() {
        return Collections.unmodifiableCollection(namedIRIs);
    }

    /**
     * Adds the IRI of a new graph to set of DEFAULT graphs
     *
     * @param iri The IRI of a new graph
     */
    public void addDefaultGraph(String iri) {
        defaultIRIs.add(iri);
    }

    /**
     * Adds the IRI of a new graph to set of NAMED graphs
     *
     * @param iri The IRI of a new graph
     */
    public void addNamedIRI(String iri) {
        namedIRIs.add(iri);
    }

    /**
     * Resolves the blank node for the specified identifier
     *
     * @param id The identifier of a blank node
     * @return The blank node with the specified identifier
     */
    public Node resolveBlankNode(String id) {
        if (emitVariableForBlank)
            return resolveVariable(GEN_VAR_PREFIX + id);
        BlankNode blank = blanks.get(id);
        if (blank != null)
            return blank;
        blank = nodes.getBlankNode();
        blanks.put(id, blank);
        return blank;
    }

    /**
     * Resolves the variable name with the specified name
     *
     * @param name The name of a variable
     * @return The associated variable
     */
    public Node resolveVariable(String name) {
        VariableNode var = variables.get(name);
        if (var != null)
            return var;
        var = new VariableNode(name);
        variables.put(name, var);
        return var;
    }
}
