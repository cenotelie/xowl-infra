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

package org.xowl.infra.lang.owl2;

import java.util.*;

/**
 * Represents the base interface for the OWL class DatatypeRestriction
 *
 * @author xOWL code generator
 */
public interface DatatypeRestriction extends org.xowl.infra.lang.owl2.Datarange, org.xowl.infra.lang.owl2.DataCardinalityRestriction_OR_DataComplementOf_OR_DataPropertyRange_OR_DatarangeElement_OR_Datatype3 {
    /**
     * Adds an element to the property FacetRestrictions
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addFacetRestrictions(org.xowl.infra.lang.owl2.FacetRestriction elem);

    /**
     * Removes an element from the property FacetRestrictions
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeFacetRestrictions(org.xowl.infra.lang.owl2.FacetRestriction elem);

    /**
     * Gets all the elements for the property FacetRestrictions
     *
     * @return The elements for the property FacetRestrictions
     */
    Collection<org.xowl.infra.lang.owl2.FacetRestriction> getAllFacetRestrictions();

}
