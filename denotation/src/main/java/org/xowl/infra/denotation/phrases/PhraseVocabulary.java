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

import org.xowl.infra.utils.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the vocabulary that can be used for a phrase,
 * i.e. the set of possible properties and relations for the signs in the phrase.
 *
 * @author Laurent Wouters
 */
public class PhraseVocabulary implements Serializable {
    /**
     * The global register of vocabulary elements
     */
    public static final PhraseVocabulary REGISTER = newRegister();

    /**
     * Produces the register vocabulary
     *
     * @return The register vocabulary
     */
    private static PhraseVocabulary newRegister() {
        PhraseVocabulary result = new PhraseVocabulary();
        result.properties.put(SignPropertyBrightness.URI, SignPropertyBrightness.INSTANCE);
        result.properties.put(SignPropertyColor.URI, SignPropertyColor.INSTANCE);
        result.properties.put(SignPropertyName.URI, SignPropertyName.INSTANCE);
        result.properties.put(SignPropertyOrientation.URI, SignPropertyOrientation.INSTANCE);
        result.properties.put(SignPropertyPosition2D.URI, SignPropertyPosition2D.INSTANCE);
        result.properties.put(SignPropertyPositionText.URI, SignPropertyPositionText.INSTANCE);
        result.properties.put(SignPropertyShape.URI, SignPropertyShape.INSTANCE);
        result.properties.put(SignPropertySize.URI, SignPropertySize.INSTANCE);
        result.properties.put(SignPropertyTexture.URI, SignPropertyTexture.INSTANCE);
        result.properties.put(SignPropertyZone2D.URI, SignPropertyZone2D.INSTANCE);
        result.relations.put(SignRelation.RELATION_CONTAINED_BY.getIdentifier(), SignRelation.RELATION_CONTAINED_BY);
        result.relations.put(SignRelation.RELATION_CONTAINS.getIdentifier(), SignRelation.RELATION_CONTAINS);
        result.relations.put(SignRelation.RELATION_LINKED_BY.getIdentifier(), SignRelation.RELATION_LINKED_BY);
        result.relations.put(SignRelation.RELATION_LINKS.getIdentifier(), SignRelation.RELATION_LINKS);
        result.relations.put(SignRelation.RELATION_OVERLAPS.getIdentifier(), SignRelation.RELATION_OVERLAPS);
        return result;
    }

    /**
     * The sign properties
     */
    private final Map<String, SignProperty> properties;
    /**
     * The sign relations
     */
    private final Map<String, SignRelation> relations;

    /**
     * Initializes an empty vocabulary
     */
    public PhraseVocabulary() {
        this.properties = new HashMap<>();
        this.relations = new HashMap<>();
    }

    /**
     * Initializes this vocabulary as a copy of the specified one
     *
     * @param copied The vocabulary to copy
     */
    public PhraseVocabulary(PhraseVocabulary copied) {
        this.properties = new HashMap<>(copied.properties);
        this.relations = new HashMap<>(copied.relations);
    }

    /**
     * Gets the properties in this vocabulary
     *
     * @return The properties in this vocabulary
     */
    public Collection<SignProperty> getProperties() {
        return properties.values();
    }

    /**
     * Gets the property for the specified identifier
     *
     * @param identifier The identifier of the property
     * @return The property, or null if it is not in this vocabulary
     */
    public SignProperty getProperty(String identifier) {
        return properties.get(identifier);
    }

    /**
     * Adds a property to this vocabulary
     *
     * @param property The property to add
     */
    public void addProperty(SignProperty property) {
        properties.put(property.getIdentifier(), property);
    }

    /**
     * Gets the relations in this vocabulary
     *
     * @return The relations in this vocabulary
     */
    public Collection<SignRelation> getRelations() {
        return relations.values();
    }

    /**
     * Gets the relation for the specified identifier
     *
     * @param identifier The identifier of a relation
     * @return The relation, or null if it is not in this vocabulary
     */
    public SignRelation getRelation(String identifier) {
        return relations.get(identifier);
    }

    /**
     * Adds a relation to this vocabulary
     *
     * @param relation The relation to add
     */
    public void addRelation(SignRelation relation) {
        relations.put(relation.getIdentifier(), relation);
    }

    @Override
    public String serializedString() {
        return serializedJSON();
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"");
        builder.append(PhraseVocabulary.class.getCanonicalName());
        builder.append("\", \"properties\": [");
        boolean first = true;
        for (SignProperty property : properties.values()) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(property.serializedJSON());
        }
        builder.append("], \"relations\": [");
        first = true;
        for (SignRelation relation : relations.values()) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(relation.serializedJSON());
        }
        builder.append("]}");
        return builder.toString();
    }
}
