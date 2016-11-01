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
 * Represents the base interface for the OWL class Individual
 *
 * @author xOWL code generator
 */
public interface Individual {
    /**
     * Adds an element to the property Asserts
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem);

    /**
     * Removes an element from the property Asserts
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem);

    /**
     * Gets all the elements for the property Asserts
     *
     * @return The elements for the property Asserts
     */
    Collection<org.xowl.infra.lang.runtime.PropertyAssertion> getAllAsserts();

    /**
     * Adds an element to the property ClassifiedBy
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassifiedBy(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Removes an element from the property ClassifiedBy
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassifiedBy(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Gets all the elements for the property ClassifiedBy
     *
     * @return The elements for the property ClassifiedBy
     */
    Collection<org.xowl.infra.lang.runtime.Class> getAllClassifiedBy();

    /**
     * Adds an element to the property DifferentFrom
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDifferentFrom(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Removes an element from the property DifferentFrom
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDifferentFrom(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Gets all the elements for the property DifferentFrom
     *
     * @return The elements for the property DifferentFrom
     */
    Collection<org.xowl.infra.lang.runtime.Individual> getAllDifferentFrom();

    /**
     * Adds an element to the property SameAs
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSameAs(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Removes an element from the property SameAs
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSameAs(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Gets all the elements for the property SameAs
     *
     * @return The elements for the property SameAs
     */
    Collection<org.xowl.infra.lang.runtime.Individual> getAllSameAs();

}
