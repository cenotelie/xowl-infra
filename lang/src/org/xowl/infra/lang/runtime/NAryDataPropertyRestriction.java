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

package org.xowl.infra.lang.runtime;

import java.util.*;

/**
 * Represents the base interface for NAryDataPropertyRestriction
 * Original OWL class is http://xowl.org/infra/lang/runtime#NAryDataPropertyRestriction
 *
 * @author xOWL code generator
 */
public interface NAryDataPropertyRestriction extends ClassRestriction, DomainOfDatatype {
    /**
     * Adds an element to the property DataProperties
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataProperties(DataProperty elem);

    /**
     * Removes an element from the property DataProperties
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataProperties(DataProperty elem);

    /**
     * Gets all the elements for the property DataProperties
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @return The elements for the property DataProperties
     */
    Collection<DataProperty> getAllDataProperties();

}
