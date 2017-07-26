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

package org.xowl.infra.lsp.structures;

import fr.cenotelie.hime.redist.ASTNode;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.json.JsonDeserializer;
import org.xowl.infra.utils.json.JsonLexer;
import org.xowl.infra.utils.json.JsonParser;
import org.xowl.infra.utils.json.SerializedUnknown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a generic capabilities-holding object
 *
 * @author Laurent Wouters
 */
public class Capabilities implements Serializable {
    /**
     * The capabilities
     */
    private final List<String> capabilities;
    /**
     * The options for capabilities
     */
    private final Map<String, Object> options;

    /**
     * Initializes this structure
     */
    public Capabilities() {
        this.capabilities = new ArrayList<>();
        this.options = new HashMap<>();
    }

    /**
     * Initializes this structure
     *
     * @param definition   The serialized definition
     * @param deserializer The deserializer to use
     */
    public Capabilities(ASTNode definition, JsonDeserializer deserializer) {
        this.capabilities = new ArrayList<>();
        this.options = new HashMap<>();
        loadCapabilities("", definition, deserializer);
    }

    /**
     * Loads capabilities in this node
     *
     * @param prefix       The current prefix
     * @param definition   The serialized definition
     * @param deserializer The deserializer to use
     */
    private void loadCapabilities(String prefix, ASTNode definition, JsonDeserializer deserializer) {
        for (ASTNode child : definition.getChildren()) {
            ASTNode nodeMemberName = child.getChildren().get(0);
            String name = TextUtils.unescape(nodeMemberName.getValue());
            name = name.substring(1, name.length() - 1);
            ASTNode nodeValue = child.getChildren().get(1);
            if (nodeValue.getSymbol().getID() == JsonParser.ID.object) {
                loadCapabilities(prefix + "." + name, child, deserializer);
            } else if (nodeValue.getSymbol().getID() == JsonLexer.ID.LITERAL_TRUE) {
                this.capabilities.add(prefix + "." + name);
            } else {
                this.options.put(prefix + "." + name, deserializer.deserialize(nodeValue, null));
            }
        }
    }

    /**
     * Adds a capability
     *
     * @param capability The capability to add
     */
    public void addCapability(String capability) {
        capabilities.add(capability);
    }

    /**
     * Determines whether the specified capability is supported
     *
     * @param capability A capability
     * @return Whether the specified capability is supported
     */
    public boolean supports(String capability) {
        return capabilities.contains(capability);
    }

    /**
     * Gets the value associated to the specified option name
     *
     * @param optionName The name of an option
     * @return The associated value
     */
    public Object getOption(String optionName) {
        return options.get(optionName);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        SerializedUnknown root = new SerializedUnknown();
        for (String capability : capabilities) {
            String[] parts = capability.split("\\.");
            SerializedUnknown target = root;
            for (int i = 0; i != parts.length - 1; i++) {
                Object object = target.getValueFor(parts[i]);
                if (object == null) {
                    object = new SerializedUnknown();
                    target.addProperty(parts[i], object);
                }
                target = ((SerializedUnknown) object);
            }
            target.addProperty(parts[parts.length - 1], true);
        }
        for (Map.Entry<String, Object> option : options.entrySet()) {
            String[] parts = option.getKey().split("\\.");
            SerializedUnknown target = root;
            for (int i = 0; i != parts.length - 1; i++) {
                Object object = target.getValueFor(parts[i]);
                if (object == null) {
                    object = new SerializedUnknown();
                    target.addProperty(parts[i], object);
                }
                target = ((SerializedUnknown) object);
            }
            target.addProperty(parts[parts.length - 1], option.getValue());
        }
        return root.serializedJSON();
    }
}
