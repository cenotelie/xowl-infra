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

package org.xowl.infra.lang.rules;

import java.util.*;

/**
 * Represents the base interface for Assertion
 * Original OWL class is http://xowl.org/infra/lang/rules#Assertion
 *
 * @author xOWL code generator
 */
public interface Assertion {
    /**
     * Adds an element to the property Axioms
     * Original OWL property is http://xowl.org/infra/lang/rules#axioms
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addAxioms(org.xowl.infra.lang.owl2.Axiom elem);

    /**
     * Removes an element from the property Axioms
     * Original OWL property is http://xowl.org/infra/lang/rules#axioms
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeAxioms(org.xowl.infra.lang.owl2.Axiom elem);

    /**
     * Gets all the elements for the property Axioms
     * Original OWL property is http://xowl.org/infra/lang/rules#axioms
     *
     * @return The elements for the property Axioms
     */
    Collection<org.xowl.infra.lang.owl2.Axiom> getAllAxioms();

    /**
     * Sets the value for the property IsMeta
     * Original OWL property is http://xowl.org/infra/lang/rules#isMeta
     *
     * @param elem The value to set
     */
    void setIsMeta(boolean elem);

    /**
     * Gets the value for the property IsMeta
     * Original OWL property is http://xowl.org/infra/lang/rules#isMeta
     *
     * @return The value for the property IsMeta
     */
    boolean getIsMeta();

    /**
     * Sets the value for the property IsPositive
     * Original OWL property is http://xowl.org/infra/lang/rules#isPositive
     *
     * @param elem The value to set
     */
    void setIsPositive(boolean elem);

    /**
     * Gets the value for the property IsPositive
     * Original OWL property is http://xowl.org/infra/lang/rules#isPositive
     *
     * @return The value for the property IsPositive
     */
    boolean getIsPositive();

}
