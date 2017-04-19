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

import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.utils.collections.Couple;

import java.util.Collection;

/**
 * Represents the information about a name
 *
 * @author Laurent Wouters
 */
class JsonLdNameInfo {
    /**
     * The type of container for a multi-valued property
     */
    public JsonLdContainerType containerType = JsonLdContainerType.Undefined;
    /**
     * The full IRI of the name
     */
    public String fullIRI;
    /**
     * The full IRI of the type for values of this property
     */
    public String valueType;
    /**
     * The language associated to this property (implies the value type is xsd:string)
     */
    public String language;
    /**
     * The property that is reversed by this one
     */
    public String reversed;

    /**
     * Merges the specified attributes into this information object
     *
     * @param attributes The attributes
     */
    public void mergeWith(Collection<Couple<String, Object>> attributes) {
        for (Couple<String, Object> attribute : attributes)
            mergeWith(attribute);
    }

    /**
     * Merges the specified attribute into this information object
     *
     * @param attribute The attribute
     */
    public void mergeWith(Couple<String, Object> attribute) {
        if (!JsonLdLoader.KEYWORDS.contains(attribute.x))
            return;
        switch (attribute.x) {
            case Vocabulary.JSONLD.type:
                if (valueType == null)
                    valueType = attribute.y.toString();
                break;
            case Vocabulary.JSONLD.container:
                if (containerType == JsonLdContainerType.Undefined)
                    containerType = (JsonLdContainerType) attribute.y;
                break;
            case Vocabulary.JSONLD.language:
                if (language == null)
                    language = attribute.y.toString();
                break;
            case Vocabulary.JSONLD.reverse:
                if (reversed == null)
                    reversed = attribute.y.toString();
                break;
        }
    }
}
