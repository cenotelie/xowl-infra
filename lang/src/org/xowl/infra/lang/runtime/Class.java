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
 * Represents the base interface for Class
 * Original OWL class is http://xowl.org/infra/lang/runtime#Class
 *
 * @author xOWL code generator
 */
public interface Class extends Interpretation, RangeOfRange {
    /**
     * Sets the value for the property ClassComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     *
     * @param elem The value to set
     */
    void setClassComplementOf(Class elem);

    /**
     * Gets the value for the property ClassComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     *
     * @return The value for the property ClassComplementOf
     */
    Class getClassComplementOf();

    /**
     * Adds an element to the property ClassDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassDisjointWith(Class elem);

    /**
     * Removes an element from the property ClassDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassDisjointWith(Class elem);

    /**
     * Gets all the elements for the property ClassDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @return The elements for the property ClassDisjointWith
     */
    Collection<Class> getAllClassDisjointWith();

    /**
     * Adds an element to the property ClassEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassEquivalentTo(Class elem);

    /**
     * Removes an element from the property ClassEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassEquivalentTo(Class elem);

    /**
     * Gets all the elements for the property ClassEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @return The elements for the property ClassEquivalentTo
     */
    Collection<Class> getAllClassEquivalentTo();

    /**
     * Adds an element to the property ClassIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassIntersectionOf(Class elem);

    /**
     * Removes an element from the property ClassIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassIntersectionOf(Class elem);

    /**
     * Gets all the elements for the property ClassIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @return The elements for the property ClassIntersectionOf
     */
    Collection<Class> getAllClassIntersectionOf();

    /**
     * Adds an element to the property ClassOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassOneOf(Individual elem);

    /**
     * Removes an element from the property ClassOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassOneOf(Individual elem);

    /**
     * Gets all the elements for the property ClassOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @return The elements for the property ClassOneOf
     */
    Collection<Individual> getAllClassOneOf();

    /**
     * Adds an element to the property ClassRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassRestrictions(ClassRestriction elem);

    /**
     * Removes an element from the property ClassRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassRestrictions(ClassRestriction elem);

    /**
     * Gets all the elements for the property ClassRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @return The elements for the property ClassRestrictions
     */
    Collection<ClassRestriction> getAllClassRestrictions();

    /**
     * Adds an element to the property ClassUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassUnionOf(Class elem);

    /**
     * Removes an element from the property ClassUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassUnionOf(Class elem);

    /**
     * Gets all the elements for the property ClassUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @return The elements for the property ClassUnionOf
     */
    Collection<Class> getAllClassUnionOf();

    /**
     * Adds an element to the property Classifies
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addClassifies(Individual elem);

    /**
     * Removes an element from the property Classifies
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeClassifies(Individual elem);

    /**
     * Gets all the elements for the property Classifies
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @return The elements for the property Classifies
     */
    Collection<Individual> getAllClassifies();

    /**
     * Adds an element to the property DomainOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDomainOf(Property elem);

    /**
     * Removes an element from the property DomainOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDomainOf(Property elem);

    /**
     * Gets all the elements for the property DomainOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @return The elements for the property DomainOf
     */
    Collection<Property> getAllDomainOf();

    /**
     * Adds an element to the property SubClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSubClassOf(Class elem);

    /**
     * Removes an element from the property SubClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSubClassOf(Class elem);

    /**
     * Gets all the elements for the property SubClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @return The elements for the property SubClassOf
     */
    Collection<Class> getAllSubClassOf();

    /**
     * Adds an element to the property SuperClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSuperClassOf(Class elem);

    /**
     * Removes an element from the property SuperClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSuperClassOf(Class elem);

    /**
     * Gets all the elements for the property SuperClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @return The elements for the property SuperClassOf
     */
    Collection<Class> getAllSuperClassOf();

}
