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
 * Represents the base interface for the OWL class DataProperty
 *
 * @author xOWL code generator
 */
public interface DataProperty extends org.xowl.infra.lang.runtime.Property {
    /**
     * Adds an element to the property PropertyDisjointWith
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addPropertyDisjointWith(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Removes an element from the property PropertyDisjointWith
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removePropertyDisjointWith(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Gets all the elements for the property PropertyDisjointWith
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property PropertyDisjointWith
     */
    Collection<org.xowl.infra.lang.runtime.DataProperty> getAllPropertyDisjointWithAs(org.xowl.infra.lang.runtime.DataProperty type);

    /**
     * Adds an element to the property PropertyEquivalentTo
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addPropertyEquivalentTo(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Removes an element from the property PropertyEquivalentTo
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removePropertyEquivalentTo(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Gets all the elements for the property PropertyEquivalentTo
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property PropertyEquivalentTo
     */
    Collection<org.xowl.infra.lang.runtime.DataProperty> getAllPropertyEquivalentToAs(org.xowl.infra.lang.runtime.DataProperty type);

    /**
     * Sets the value for the property Range
     *
     * @param elem The value to set
     */
    void setRange(org.xowl.infra.lang.runtime.Datatype elem);

    /**
     * Gets the value for the property Range
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The value for the property Range
     */
    org.xowl.infra.lang.runtime.Datatype getRangeAs(org.xowl.infra.lang.runtime.Datatype type);

    /**
     * Adds an element to the property SubPropertyOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSubPropertyOf(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Removes an element from the property SubPropertyOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSubPropertyOf(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Gets all the elements for the property SubPropertyOf
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property SubPropertyOf
     */
    Collection<org.xowl.infra.lang.runtime.DataProperty> getAllSubPropertyOfAs(org.xowl.infra.lang.runtime.DataProperty type);

    /**
     * Adds an element to the property SuperPropertyOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSuperPropertyOf(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Removes an element from the property SuperPropertyOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSuperPropertyOf(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Gets all the elements for the property SuperPropertyOf
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property SuperPropertyOf
     */
    Collection<org.xowl.infra.lang.runtime.DataProperty> getAllSuperPropertyOfAs(org.xowl.infra.lang.runtime.DataProperty type);

}
