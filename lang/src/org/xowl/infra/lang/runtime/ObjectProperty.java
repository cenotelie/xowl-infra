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
 * Represents the base interface for ObjectProperty
 * Original OWL class is http://xowl.org/infra/lang/runtime#ObjectProperty
 *
 * @author xOWL code generator
 */
public interface ObjectProperty extends Property {
    /**
     * Adds an element to the property Chains
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addChains(ObjectProperty elem);

    /**
     * Removes an element from the property Chains
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeChains(ObjectProperty elem);

    /**
     * Gets all the elements for the property Chains
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @return The elements for the property Chains
     */
    Collection<ObjectProperty> getAllChains();

    /**
     * Sets the value for the property InverseOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     *
     * @param elem The value to set
     */
    void setInverseOf(ObjectProperty elem);

    /**
     * Gets the value for the property InverseOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     *
     * @return The value for the property InverseOf
     */
    ObjectProperty getInverseOf();

    /**
     * Sets the value for the property IsAsymmetric
     * Original OWL property is http://xowl.org/infra/lang/runtime#isAsymmetric
     *
     * @param elem The value to set
     */
    void setIsAsymmetric(boolean elem);

    /**
     * Gets the value for the property IsAsymmetric
     * Original OWL property is http://xowl.org/infra/lang/runtime#isAsymmetric
     *
     * @return The value for the property IsAsymmetric
     */
    boolean getIsAsymmetric();

    /**
     * Sets the value for the property IsInverseFunctional
     * Original OWL property is http://xowl.org/infra/lang/runtime#isInverseFunctional
     *
     * @param elem The value to set
     */
    void setIsInverseFunctional(boolean elem);

    /**
     * Gets the value for the property IsInverseFunctional
     * Original OWL property is http://xowl.org/infra/lang/runtime#isInverseFunctional
     *
     * @return The value for the property IsInverseFunctional
     */
    boolean getIsInverseFunctional();

    /**
     * Sets the value for the property IsIrreflexive
     * Original OWL property is http://xowl.org/infra/lang/runtime#isIrreflexive
     *
     * @param elem The value to set
     */
    void setIsIrreflexive(boolean elem);

    /**
     * Gets the value for the property IsIrreflexive
     * Original OWL property is http://xowl.org/infra/lang/runtime#isIrreflexive
     *
     * @return The value for the property IsIrreflexive
     */
    boolean getIsIrreflexive();

    /**
     * Sets the value for the property IsReflexive
     * Original OWL property is http://xowl.org/infra/lang/runtime#isReflexive
     *
     * @param elem The value to set
     */
    void setIsReflexive(boolean elem);

    /**
     * Gets the value for the property IsReflexive
     * Original OWL property is http://xowl.org/infra/lang/runtime#isReflexive
     *
     * @return The value for the property IsReflexive
     */
    boolean getIsReflexive();

    /**
     * Sets the value for the property IsSymmetric
     * Original OWL property is http://xowl.org/infra/lang/runtime#isSymmetric
     *
     * @param elem The value to set
     */
    void setIsSymmetric(boolean elem);

    /**
     * Gets the value for the property IsSymmetric
     * Original OWL property is http://xowl.org/infra/lang/runtime#isSymmetric
     *
     * @return The value for the property IsSymmetric
     */
    boolean getIsSymmetric();

    /**
     * Sets the value for the property IsTransitive
     * Original OWL property is http://xowl.org/infra/lang/runtime#isTransitive
     *
     * @param elem The value to set
     */
    void setIsTransitive(boolean elem);

    /**
     * Gets the value for the property IsTransitive
     * Original OWL property is http://xowl.org/infra/lang/runtime#isTransitive
     *
     * @return The value for the property IsTransitive
     */
    boolean getIsTransitive();

    /**
     * Adds an element to the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addPropertyDisjointWith(ObjectProperty elem);

    /**
     * Removes an element from the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removePropertyDisjointWith(ObjectProperty elem);

    /**
     * Gets all the elements for the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property PropertyDisjointWith
     */
    Collection<ObjectProperty> getAllPropertyDisjointWithAs(ObjectProperty type);

    /**
     * Adds an element to the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addPropertyEquivalentTo(ObjectProperty elem);

    /**
     * Removes an element from the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removePropertyEquivalentTo(ObjectProperty elem);

    /**
     * Gets all the elements for the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property PropertyEquivalentTo
     */
    Collection<ObjectProperty> getAllPropertyEquivalentToAs(ObjectProperty type);

    /**
     * Sets the value for the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The value to set
     */
    void setRange(Class elem);

    /**
     * Gets the value for the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The value for the property Range
     */
    Class getRangeAs(Class type);

    /**
     * Adds an element to the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSubPropertyOf(ObjectProperty elem);

    /**
     * Removes an element from the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSubPropertyOf(ObjectProperty elem);

    /**
     * Gets all the elements for the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property SubPropertyOf
     */
    Collection<ObjectProperty> getAllSubPropertyOfAs(ObjectProperty type);

    /**
     * Adds an element to the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSuperPropertyOf(ObjectProperty elem);

    /**
     * Removes an element from the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSuperPropertyOf(ObjectProperty elem);

    /**
     * Gets all the elements for the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The elements for the property SuperPropertyOf
     */
    Collection<ObjectProperty> getAllSuperPropertyOfAs(ObjectProperty type);

}
