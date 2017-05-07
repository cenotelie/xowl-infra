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
 * Represents a significant relation of a symbol with another symbol
 *
 * @author Laurent Wouters
 */
public class SymbolRelation implements Identifiable, Serializable {
    /**
     * The standard contains relation
     */
    public static final SymbolProperty RELATION_CONTAINS = new SymbolProperty("http://xowl.org/infra/denotation/relation/contains", "contains");
    /**
     * The standard contained by relation
     */
    public static final SymbolProperty RELATION_CONTAINED_BY = new SymbolProperty("http://xowl.org/infra/denotation/relation/containedBy", "containedBy");
    /**
     * The standard overlaps relation
     */
    public static final SymbolProperty RELATION_OVERLAPS = new SymbolProperty("http://xowl.org/infra/denotation/relation/overlaps", "overlaps");
    /**
     * The standard links relation
     */
    public static final SymbolProperty RELATION_LINKS = new SymbolProperty("http://xowl.org/infra/denotation/relation/links", "links");
    /**
     * The standard linked by relation
     */
    public static final SymbolProperty RELATION_LINKED_BY = new SymbolProperty("http://xowl.org/infra/denotation/relation/linkedBy", "linkedBy");


    /**
     * The identifier of this relation
     */
    private final String identifier;
    /**
     * The human-readable name of this relation
     */
    private final String name;

    /**
     * Initializes this relation
     *
     * @param identifier The identifier of this relation
     * @param name       The human-readable name of this relation
     */
    public SymbolRelation(String identifier, String name) {
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
                SymbolRelation.class.getCanonicalName() +
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
