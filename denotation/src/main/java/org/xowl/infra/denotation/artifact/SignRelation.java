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

import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

/**
 * Represents a significant relation of a symbol with another symbol
 *
 * @author Laurent Wouters
 */
public class SignRelation implements Identifiable, Serializable {
    /**
     * The standard contains relation
     */
    public static final SignRelation RELATION_CONTAINS = new SignRelation("http://xowl.org/infra/denotation/relation/contains", "contains");
    /**
     * The standard contained by relation
     */
    public static final SignRelation RELATION_CONTAINED_BY = new SignRelation("http://xowl.org/infra/denotation/relation/containedBy", "containedBy");
    /**
     * The standard overlaps relation
     */
    public static final SignRelation RELATION_OVERLAPS = new SignRelation("http://xowl.org/infra/denotation/relation/overlaps", "overlaps");
    /**
     * The standard links relation
     */
    public static final SignRelation RELATION_LINKS = new SignRelation("http://xowl.org/infra/denotation/relation/links", "links");
    /**
     * The standard linked by relation
     */
    public static final SignRelation RELATION_LINKED_BY = new SignRelation("http://xowl.org/infra/denotation/relation/linkedBy", "linkedBy");


    /**
     * The identifier of this relation
     */
    private final String uri;
    /**
     * The human-readable name of this relation
     */
    private final String name;

    /**
     * Initializes this relation
     *
     * @param uri  The uri of this relation
     * @param name The human-readable name of this relation
     */
    public SignRelation(String uri, String name) {
        this.uri = uri;
        this.name = name;
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
                SignRelation.class.getCanonicalName() +
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
