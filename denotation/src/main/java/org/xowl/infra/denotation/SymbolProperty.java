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
     * The identifier of this property
     */
    private final String identifier;
    /**
     * The human-readable name of this property
     */
    private final String name;

    /**
     * Initializes this property
     *
     * @param identifier The identifier of this property
     * @param name       The human-readable name of this property
     */
    public SymbolProperty(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
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
        return "{\"type\": \"" +
                SymbolProperty.class.getCanonicalName() +
                "\", \"identifier\": \"" +
                TextUtils.escapeStringJSON(identifier) +
                "\", \"name\": \"" +
                TextUtils.escapeStringJSON(name) +
                "\"}";
    }

    @Override
    public String toString() {
        return identifier;
    }
}
