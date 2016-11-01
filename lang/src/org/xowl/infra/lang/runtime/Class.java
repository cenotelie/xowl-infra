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
 * Represents the base interface for the OWL class Class
 *
 * @author xOWL code generator
 */
public interface Class extends org.xowl.infra.lang.runtime.Interpretation, org.xowl.infra.lang.runtime.Class_OR_Datatype {
    /**
     * Adds an element to the property ClassEquivalentTo
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Removes an element from the property ClassEquivalentTo
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Gets all the elements for the property ClassEquivalentTo
     *
     * @return The elements for the property ClassEquivalentTo
     */
    Collection<org.xowl.infra.lang.runtime.Class> getAllClassEquivalentTo();

    /**
     * Adds an element to the property ClassRestrictions
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem);

    /**
     * Removes an element from the property ClassRestrictions
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem);

    /**
     * Gets all the elements for the property ClassRestrictions
     *
     * @return The elements for the property ClassRestrictions
     */
    Collection<org.xowl.infra.lang.runtime.ClassRestriction> getAllClassRestrictions();

    /**
     * Adds an element to the property DomainOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDomainOf(org.xowl.infra.lang.runtime.Property elem);

    /**
     * Removes an element from the property DomainOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDomainOf(org.xowl.infra.lang.runtime.Property elem);

    /**
     * Gets all the elements for the property DomainOf
     *
     * @return The elements for the property DomainOf
     */
    Collection<org.xowl.infra.lang.runtime.Property> getAllDomainOf();

    /**
     * Adds an element to the property ClassOneOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassOneOf(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Removes an element from the property ClassOneOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassOneOf(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Gets all the elements for the property ClassOneOf
     *
     * @return The elements for the property ClassOneOf
     */
    Collection<org.xowl.infra.lang.runtime.Individual> getAllClassOneOf();

    /**
     * Adds an element to the property ClassIntersectionOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Removes an element from the property ClassIntersectionOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Gets all the elements for the property ClassIntersectionOf
     *
     * @return The elements for the property ClassIntersectionOf
     */
    Collection<org.xowl.infra.lang.runtime.Class> getAllClassIntersectionOf();

    /**
     * Sets the value for the property ClassComplementOf
     *
     * @param elem The value to set
     */
    void setClassComplementOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Gets the value for the property ClassComplementOf
     *
     * @return The value for the property ClassComplementOf
     */
    org.xowl.infra.lang.runtime.Class getClassComplementOf();

    /**
     * Adds an element to the property ClassDisjointWith
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassDisjointWith(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Removes an element from the property ClassDisjointWith
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassDisjointWith(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Gets all the elements for the property ClassDisjointWith
     *
     * @return The elements for the property ClassDisjointWith
     */
    Collection<org.xowl.infra.lang.runtime.Class> getAllClassDisjointWith();

    /**
     * Adds an element to the property Classifies
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassifies(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Removes an element from the property Classifies
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassifies(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Gets all the elements for the property Classifies
     *
     * @return The elements for the property Classifies
     */
    Collection<org.xowl.infra.lang.runtime.Individual> getAllClassifies();

    /**
     * Adds an element to the property SuperClassOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSuperClassOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Removes an element from the property SuperClassOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSuperClassOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Gets all the elements for the property SuperClassOf
     *
     * @return The elements for the property SuperClassOf
     */
    Collection<org.xowl.infra.lang.runtime.Class> getAllSuperClassOf();

    /**
     * Adds an element to the property SubClassOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSubClassOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Removes an element from the property SubClassOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSubClassOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Gets all the elements for the property SubClassOf
     *
     * @return The elements for the property SubClassOf
     */
    Collection<org.xowl.infra.lang.runtime.Class> getAllSubClassOf();

    /**
     * Adds an element to the property ClassUnionOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassUnionOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Removes an element from the property ClassUnionOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassUnionOf(org.xowl.infra.lang.runtime.Class elem);

    /**
     * Gets all the elements for the property ClassUnionOf
     *
     * @return The elements for the property ClassUnionOf
     */
    Collection<org.xowl.infra.lang.runtime.Class> getAllClassUnionOf();

}
