/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.store.loaders;

import org.xowl.utils.collections.Couple;

import java.util.Collection;

/**
 * Represents the information about a name
 *
 * @author Laurent Wouters
 */
class JSONLDNameInfo {
    /**
     * The type of container for a multi-valued property
     */
    public JSONLDContainerType containerType = JSONLDContainerType.Undefined;
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
        if (!JSONLDLoader.KEYWORDS.contains(attribute.x))
            return;
        switch (attribute.x) {
            case JSONLDLoader.KEYWORD_TYPE:
                if (valueType == null)
                    valueType = attribute.y.toString();
                break;
            case JSONLDLoader.KEYWORD_CONTAINER:
                if (containerType == JSONLDContainerType.Undefined)
                    containerType = (JSONLDContainerType) attribute.y;
                break;
            case JSONLDLoader.KEYWORD_LANGUAGE:
                if (language == null)
                    language = attribute.y.toString();
                break;
            case JSONLDLoader.KEYWORD_REVERSE:
                if (reversed == null)
                    reversed = attribute.y.toString();
                break;
        }
    }
}
