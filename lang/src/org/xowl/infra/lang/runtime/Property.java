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
 * Represents the base interface for Property
 * Original OWL class is http://xowl.org/infra/lang/runtime#Property
 *
 * @author xOWL code generator
 */
public interface Property extends Interpretation {
    /**
     * Sets the value for the property Domain
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     *
     * @param elem The value to set
     */
    void setDomain(Class elem);

    /**
     * Gets the value for the property Domain
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     *
     * @return The value for the property Domain
     */
    Class getDomain();

    /**
     * Sets the value for the property IsFunctional
     * Original OWL property is http://xowl.org/infra/lang/runtime#isFunctional
     *
     * @param elem The value to set
     */
    void setIsFunctional(boolean elem);

    /**
     * Gets the value for the property IsFunctional
     * Original OWL property is http://xowl.org/infra/lang/runtime#isFunctional
     *
     * @return The value for the property IsFunctional
     */
    boolean getIsFunctional();

    /**
     * Adds an element to the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addPropertyDisjointWith(Property elem);

    /**
     * Removes an element from the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removePropertyDisjointWith(Property elem);

    /**
     * Gets all the elements for the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property PropertyDisjointWith
     */
    Collection<Property> getAllPropertyDisjointWithAs(Property type);

    /**
     * Adds an element to the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addPropertyEquivalentTo(Property elem);

    /**
     * Removes an element from the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removePropertyEquivalentTo(Property elem);

    /**
     * Gets all the elements for the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property PropertyEquivalentTo
     */
    Collection<Property> getAllPropertyEquivalentToAs(Property type);

    /**
     * Sets the value for the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The value to set
     */
    void setRange(RangeOfRange elem);

    /**
     * Gets the value for the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The value for the property Range
     */
    RangeOfRange getRangeAs(RangeOfRange type);

    /**
     * Adds an element to the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSubPropertyOf(Property elem);

    /**
     * Removes an element from the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSubPropertyOf(Property elem);

    /**
     * Gets all the elements for the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property SubPropertyOf
     */
    Collection<Property> getAllSubPropertyOfAs(Property type);

    /**
     * Adds an element to the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSuperPropertyOf(Property elem);

    /**
     * Removes an element from the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSuperPropertyOf(Property elem);

    /**
     * Gets all the elements for the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property SuperPropertyOf
     */
    Collection<Property> getAllSuperPropertyOfAs(Property type);

}
