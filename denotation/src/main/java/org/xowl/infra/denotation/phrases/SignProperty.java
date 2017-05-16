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

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

/**
 * Represents a significant property of the sign
 *
 * @author Laurent Wouters
 */
public class SignProperty implements Identifiable, Serializable {
    /**
     * The uri of this property
     */
    private final String uri;
    /**
     * The human-readable name of this property
     */
    private final String name;
    /**
     * Whether this property is relevant for RDF serialization
     */
    private final boolean isRdfSerialized;

    /**
     * Initializes this property
     *
     * @param uri             The identifier of this property
     * @param name            The human-readable name of this property
     * @param isRdfSerialized Whether this property is relevant for RDF serialization
     */
    public SignProperty(String uri, String name, boolean isRdfSerialized) {
        this.uri = uri;
        this.name = name;
        this.isRdfSerialized = isRdfSerialized;
    }

    /**
     * Initializes this property
     *
     * @param definition The serialized definition
     */
    public SignProperty(ASTNode definition) {
        String identifier = null;
        String name = null;
        boolean isRdfSerialized = false;
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeHeader = child.getChildren().get(0);
            String memberName = TextUtils.unescape(nodeHeader.getValue());
            memberName = memberName.substring(1, memberName.length() - 1);
            switch (memberName) {
                case "identifier": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    identifier = TextUtils.unescape(nodeValue.getValue());
                    identifier = identifier.substring(1, identifier.length() - 1);
                    break;
                }
                case "name": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    name = TextUtils.unescape(nodeValue.getValue());
                    name = name.substring(1, name.length() - 1);
                    break;
                }
                case "isRdfSerialized": {
                    ASTNode nodeValue = child.getChildren().get(1);
                    String value = TextUtils.unescape(nodeValue.getValue());
                    value = value.substring(1, value.length() - 1);
                    isRdfSerialized = value.equalsIgnoreCase("true");
                    break;
                }
            }
        }
        this.uri = identifier;
        this.name = name;
        this.isRdfSerialized = isRdfSerialized;
    }

    /**
     * Gets whether this property is relevant for RDF serialization
     *
     * @return Whether this property is relevant for RDF serialization
     */
    public boolean isRdfSerialized() {
        return isRdfSerialized;
    }

    /**
     * Gets whether the specified value is a valid value for this property
     *
     * @param value The value to check
     * @return Whether the specified value is a valid value for this property
     */
    public boolean isValidValue(Object value) {
        return value != null && (value instanceof String);
    }

    /**
     * Serializes in JSON a value of this property
     *
     * @param builder The string builder to serialize to
     * @param value   The value to serialize
     */
    public void serializeValueJson(StringBuilder builder, Object value) {
        builder.append("\"");
        builder.append(TextUtils.escapeStringJSON(value.toString()));
        builder.append("\"");
    }

    /**
     * Serializes in RDF a value of this property
     *
     * @param nodes The node manager to use
     * @param value The value to serialize
     * @return The RDF node
     */
    public Node serializeValueRdf(NodeManager nodes, Object value) {
        return nodes.getLiteralNode(value.toString(), Vocabulary.xsdString, null);
    }

    /**
     * De-serializes a value of this property from the JSON definition
     *
     * @param definition The serialized definition
     * @return The original value
     */
    public Object deserializeValueJson(ASTNode definition) {
        String value = TextUtils.unescape(definition.getValue());
        value = value.substring(1, value.length() - 1);
        return value;
    }

    @Override
    public String getIdentifier() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serializedString() {
        return uri;
    }

    @Override
    public String serializedJSON() {
        return "{\"type\": \"" +
                SignProperty.class.getCanonicalName() +
                "\", \"identifier\": \"" +
                TextUtils.escapeStringJSON(uri) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(name) +
                "\", \"isRdfSerialized\": \"" +
                Boolean.toString(isRdfSerialized) +
                "\"}";
    }

    @Override
    public String toString() {
        return uri;
    }
}
