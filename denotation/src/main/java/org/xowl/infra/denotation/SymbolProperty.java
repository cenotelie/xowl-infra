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

package org.xowl.infra.denotation;

import org.xowl.infra.store.RDFUtils;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

/**
 * Represents a significant property of the symbol
 *
 * @author Laurent Wouters
 */
public class SymbolProperty implements Identifiable, Serializable {
    /**
     * The standard name property
     */
    public static final SymbolProperty PROPERTY_NAME = new SymbolProperty("http://xowl.org/infra/denotation/property/name", "name");
    /**
     * The standard position property
     */
    public static final SymbolProperty PROPERTY_POSITION = new SymbolProperty("http://xowl.org/infra/denotation/property/position", "position");
    /**
     * The standard shape property
     */
    public static final SymbolProperty PROPERTY_SHAPE = new SymbolProperty("http://xowl.org/infra/denotation/property/shape", "shape");
    /**
     * The standard size property
     */
    public static final SymbolProperty PROPERTY_SIZE = new SymbolProperty("http://xowl.org/infra/denotation/property/size", "size");
    /**
     * The standard color property
     */
    public static final SymbolProperty PROPERTY_COLOR = new SymbolProperty("http://xowl.org/infra/denotation/property/color", "color");
    /**
     * The standard brightness property
     */
    public static final SymbolProperty PROPERTY_BRIGHTNESS = new SymbolProperty("http://xowl.org/infra/denotation/property/brightness", "brightness");
    /**
     * The standard orientation property
     */
    public static final SymbolProperty PROPERTY_ORIENTATION = new SymbolProperty("http://xowl.org/infra/denotation/property/orientation", "orientation");
    /**
     * The standard texture property
     */
    public static final SymbolProperty PROPERTY_TEXTURE = new SymbolProperty("http://xowl.org/infra/denotation/property/texture", "texture");


    /**
     * The uri of this property
     */
    private final String uri;
    /**
     * The human-readable name of this property
     */
    private final String name;

    /**
     * Initializes this property
     *
     * @param uri  The identifier of this property
     * @param name The human-readable name of this property
     */
    public SymbolProperty(String uri, String name) {
        this.uri = uri;
        this.name = name;
    }

    /**
     * Serializes in JSON a value of this property
     *
     * @param builder The string builder to serialize to
     * @param value   The value to serialize
     */
    public void serializeValueJson(StringBuilder builder, Object value) {
        builder.append("\"");
        if (value != null)
            builder.append(TextUtils.serializeJSON(value.toString()));
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
        return RDFUtils.getRDF(nodes, value);
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
                SymbolProperty.class.getCanonicalName() +
                "\", \"identifier\": \"" +
                TextUtils.escapeStringJSON(uri) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(name) +
                "\"}";
    }

    @Override
    public String toString() {
        return uri;
    }
}
