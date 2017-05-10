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

package org.xowl.infra.denotation.phrases;

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
 * Represents a sign in a user's phrase
 *
 * @author Laurent Wouters
 */
public class Sign implements Identifiable, Serializable {
    /**
     * The URI for the Sign type
     */
    public static final String TYPE_SYMBOL = "http://xowl.org/infra/denotation/Sign";

    /**
     * The sign's identifier that can be traced back in the original's input
     */
    private final String identifier;
    /**
     * The sign's name, if any
     */
    private final String name;
    /**
     * The sign's properties
     */
    private Map<SignProperty, Object> properties;
    /**
     * The relations of this signs with other signs
     */
    private Map<SignRelation, List<Sign>> relations;
    /**
     * The IRI node for the RDF representation of this sign
     */
    private IRINode rdfNode;

    /**
     * Initializes this sign
     *
     * @param identifier The sign's identifier that can be traced back in the original's phrase
     * @param name       The sign's name, if any
     */
    public Sign(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    /**
     * Gets the properties of this sign
     *
     * @return The properties of this sign
     */
    public Collection<SignProperty> getProperties() {
        if (properties == null)
            return Collections.emptyList();
        return properties.keySet();
    }

    /**
     * Gets the value associated to the specified property
     *
     * @param property A sign property
     * @return The associated value, or null if there is none
     */
    public Object getPropertyValue(SignProperty property) {
        if (properties == null)
            return null;
        return properties.get(property);
    }

    /**
     * Associates a value to a property for this sign
     *
     * @param property The property to set
     * @param value    The value to be associated
     */
    public void addPropertyValue(SignProperty property, Object value) {
        if (!property.isValidValue(value))
            return;
        if (properties == null)
            properties = new HashMap<>();
        properties.put(property, value);
    }

    /**
     * Gets the relations of this sign
     *
     * @return The relations of this sign
     */
    public Collection<SignRelation> getRelations() {
        if (relations == null)
            return Collections.emptyList();
        return relations.keySet();
    }

    /**
     * Gets the first sign associated to this one for the specified relation
     *
     * @param relation A relation
     * @return The first related sign
     */
    public Sign getRelationSign(SignRelation relation) {
        if (relations == null)
            return null;
        List<Sign> result = relations.get(relation);
        if (result == null || result.isEmpty())
            return null;
        return result.get(0);
    }

    /**
     * Gets all the signs associated to this one for the specified relartion
     *
     * @param relation A relation
     * @return The related signs
     */
    public List<Sign> getRelationSigns(SignRelation relation) {
        if (relations == null)
            return Collections.emptyList();
        List<Sign> result = relations.get(relation);
        if (result == null || result.isEmpty())
            return Collections.emptyList();
        return Collections.unmodifiableList(result);
    }

    /**
     * Adds a related sign
     *
     * @param relation The relation
     * @param sign     The related sign
     */
    public void addRelationSign(SignRelation relation, Sign sign) {
        if (relations == null)
            relations = new HashMap<>();
        List<Sign> result = relations.get(relation);
        if (result == null) {
            result = new ArrayList<>();
            relations.put(relation, result);
        }
        result.add(sign);
    }

    /**
     * Gets the RDF node for the RDF representation of this sign
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
     * Builds the RDF serialization of this sign
     *
     * @param nodes  The node manager to use
     * @param graph  The target graph
     * @param buffer The buffer for the produced quads
     */
    public void serializeRdf(NodeManager nodes, GraphNode graph, Collection<Quad> buffer) {
        IRINode subject = getRdfNode(nodes);
        buffer.add(new Quad(graph, subject, nodes.getIRINode(Vocabulary.rdfType), nodes.getIRINode(TYPE_SYMBOL)));
        if (name != null)
            buffer.add(new Quad(graph, subject, nodes.getIRINode(SignPropertyName.URI), nodes.getLiteralNode(name, Vocabulary.xsdString, null)));
        if (properties != null) {
            for (Map.Entry<SignProperty, Object> property : properties.entrySet()) {
                if (property.getKey().isRdfSerialized())
                    buffer.add(new Quad(graph, subject, nodes.getIRINode(property.getKey().getIdentifier()), property.getKey().serializeValueRdf(nodes, property.getValue())));
            }
        }
        if (relations != null) {
            for (Map.Entry<SignRelation, List<Sign>> relation : relations.entrySet()) {
                for (Sign target : relation.getValue()) {
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
        builder.append(Sign.class.getCanonicalName());
        builder.append("\", \"identifier\": \"");
        builder.append(TextUtils.serializeJSON(identifier));
        builder.append("\", \"name\": \"");
        if (name != null)
            builder.append(TextUtils.serializeJSON(name));
        builder.append("\", \"properties\": {");
        if (properties != null) {
            boolean first = true;
            for (Map.Entry<SignProperty, Object> property : properties.entrySet()) {
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
            for (Map.Entry<SignRelation, List<Sign>> relation : relations.entrySet()) {
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
