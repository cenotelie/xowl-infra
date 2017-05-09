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

package org.xowl.infra.denotation.artifact;

import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

import java.util.*;

/**
 * Represents a symbol in a user's artifact
 *
 * @author Laurent Wouters
 */
public class Symbol implements Identifiable, Serializable {
    /**
     * The URI for the Symbol type
     */
    public static final String TYPE_SYMBOL = "http://xowl.org/infra/denotation/Symbol";

    /**
     * The symbol's identifier that can be traced back in the original's artifact
     */
    private final String identifier;
    /**
     * The symbol's name, if any
     */
    private final String name;
    /**
     * The symbol's properties
     */
    private Map<SymbolProperty, Object> properties;
    /**
     * The relations of this symbols with other symbols
     */
    private Map<SymbolRelation, List<Symbol>> relations;
    /**
     * The IRI node for the RDF representation of this symbol
     */
    private IRINode rdfNode;

    /**
     * Initializes this symbol
     *
     * @param identifier The symbol's identifier that can be traced back in the original's artifact
     * @param name       The symbol's name, if any
     */
    public Symbol(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    /**
     * Gets the properties of this symbol
     *
     * @return The properties of this symbol
     */
    public Collection<SymbolProperty> getProperties() {
        if (properties == null)
            return Collections.emptyList();
        return properties.keySet();
    }

    /**
     * Gets the value associated to the specified property
     *
     * @param property A symbol property
     * @return The associated value, or null if there is none
     */
    public Object getPropertyValue(SymbolProperty property) {
        if (properties == null)
            return null;
        return properties.get(property);
    }

    /**
     * Associates a value to a property for this symbol
     *
     * @param property The property to set
     * @param value    The value to be associated
     */
    public void addPropertyValue(SymbolProperty property, Object value) {
        if (!property.isValidValue(value))
            return;
        if (properties == null)
            properties = new HashMap<>();
        properties.put(property, value);
    }

    /**
     * Gets the relations of this symbol
     *
     * @return The relations of this symbol
     */
    public Collection<SymbolRelation> getRelations() {
        if (relations == null)
            return Collections.emptyList();
        return relations.keySet();
    }

    /**
     * Gets the first symbol associated to this one for the specified relation
     *
     * @param relation A relation
     * @return The first related symbol
     */
    public Symbol getRelationSymbol(SymbolRelation relation) {
        if (relations == null)
            return null;
        List<Symbol> result = relations.get(relation);
        if (result == null || result.isEmpty())
            return null;
        return result.get(0);
    }

    /**
     * Gets all the symbols associated to this one for the specified relartion
     *
     * @param relation A relation
     * @return The related symbols
     */
    public List<Symbol> getRelationSymbols(SymbolRelation relation) {
        if (relations == null)
            return Collections.emptyList();
        List<Symbol> result = relations.get(relation);
        if (result == null || result.isEmpty())
            return Collections.emptyList();
        return Collections.unmodifiableList(result);
    }

    /**
     * Adds a related symbol
     *
     * @param relation The relation
     * @param symbol   The related symbol
     */
    public void addRelationSymbol(SymbolRelation relation, Symbol symbol) {
        if (relations == null)
            relations = new HashMap<>();
        List<Symbol> result = relations.get(relation);
        if (result == null) {
            result = new ArrayList<>();
            relations.put(relation, result);
        }
        result.add(symbol);
    }

    /**
     * Gets the RDF node for the RDF representation of this symbol
     *
     * @param nodes The node manager to use
     * @return The RDF node
     */
    private IRINode getRdfNode(NodeManager nodes) {
        if (rdfNode != null)
            return rdfNode;
        rdfNode = nodes.getIRINode(identifier);
        return rdfNode;
    }

    /**
     * Builds the RDF serialization of this symbol
     *
     * @param nodes  The node manager to use
     * @param graph  The target graph
     * @param buffer The buffer for the produced quads
     */
    public void serializeRdf(NodeManager nodes, GraphNode graph, Collection<Quad> buffer) {
        IRINode subject = getRdfNode(nodes);
        buffer.add(new Quad(graph, subject, nodes.getIRINode(Vocabulary.rdfType), nodes.getIRINode(TYPE_SYMBOL)));
        if (name != null)
            buffer.add(new Quad(graph, subject, nodes.getIRINode(SymbolPropertyName.URI), nodes.getLiteralNode(name, Vocabulary.xsdString, null)));
        if (properties != null) {
            for (Map.Entry<SymbolProperty, Object> property : properties.entrySet()) {
                if (property.getKey().isRdfSerialized())
                    buffer.add(new Quad(graph, subject, nodes.getIRINode(property.getKey().getIdentifier()), property.getKey().serializeValueRdf(nodes, property.getValue())));
            }
        }
        if (relations != null) {
            for (Map.Entry<SymbolRelation, List<Symbol>> relation : relations.entrySet()) {
                for (Symbol target : relation.getValue()) {
                    buffer.add(new Quad(graph, subject, nodes.getIRINode(relation.getKey().getIdentifier()), target.getRdfNode(nodes)));
                }
            }
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serializedString() {
        return identifier;
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"");
        builder.append(Symbol.class.getCanonicalName());
        builder.append("\", \"identifier\": \"");
        builder.append(TextUtils.serializeJSON(identifier));
        builder.append("\", \"name\": \"");
        if (name != null)
            builder.append(TextUtils.serializeJSON(name));
        builder.append("\", \"properties\": {");
        if (properties != null) {
            boolean first = true;
            for (Map.Entry<SymbolProperty, Object> property : properties.entrySet()) {
                if (!first)
                    builder.append(", ");
                first = false;
                builder.append("\"");
                builder.append(TextUtils.escapeStringJSON(property.getKey().getIdentifier()));
                builder.append("\": ");
                property.getKey().serializeValueJson(builder, property.getValue());
            }
        }
        builder.append("}, \"relations\": {");
        if (relations != null) {
            boolean first = true;
            for (Map.Entry<SymbolRelation, List<Symbol>> relation : relations.entrySet()) {
                if (!first)
                    builder.append(", ");
                first = false;
                builder.append("\"");
                builder.append(TextUtils.escapeStringJSON(relation.getKey().getIdentifier()));
                builder.append("\": [");
                for (int i = 0; i != relation.getValue().size(); i++) {
                    if (i != 0)
                        builder.append(", ");
                    builder.append("\"");
                    builder.append(TextUtils.escapeStringJSON(relation.getValue().get(i).getIdentifier()));
                    builder.append("\"");
                }
                builder.append("]");
            }
        }
        builder.append("}}");
        return builder.toString();
    }
}
