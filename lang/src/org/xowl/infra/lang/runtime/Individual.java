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
 * Represents the base interface for Individual
 * Original OWL class is http://xowl.org/infra/lang/runtime#Individual
 *
 * @author xOWL code generator
 */
public interface Individual {
    /**
     * Adds an element to the property Asserts
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addAsserts(PropertyAssertion elem);

    /**
     * Removes an element from the property Asserts
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeAsserts(PropertyAssertion elem);

    /**
     * Gets all the elements for the property Asserts
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @return The elements for the property Asserts
     */
    Collection<PropertyAssertion> getAllAsserts();

    /**
     * Adds an element to the property ClassifiedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassifiedBy(Class elem);

    /**
     * Removes an element from the property ClassifiedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassifiedBy(Class elem);

    /**
     * Gets all the elements for the property ClassifiedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @return The elements for the property ClassifiedBy
     */
    Collection<Class> getAllClassifiedBy();

    /**
     * Adds an element to the property DifferentFrom
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDifferentFrom(Individual elem);

    /**
     * Removes an element from the property DifferentFrom
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDifferentFrom(Individual elem);

    /**
     * Gets all the elements for the property DifferentFrom
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @return The elements for the property DifferentFrom
     */
    Collection<Individual> getAllDifferentFrom();

    /**
     * Adds an element to the property SameAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSameAs(Individual elem);

    /**
     * Removes an element from the property SameAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSameAs(Individual elem);

    /**
     * Gets all the elements for the property SameAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @return The elements for the property SameAs
     */
    Collection<Individual> getAllSameAs();

}
