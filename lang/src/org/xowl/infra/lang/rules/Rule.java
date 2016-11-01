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
 * Represents the base interface for the OWL class Rule
 *
 * @author xOWL code generator
 */
public interface Rule {
    /**
     * Adds an element to the property Antecedents
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addAntecedents(org.xowl.infra.lang.rules.Assertion elem);

    /**
     * Removes an element from the property Antecedents
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeAntecedents(org.xowl.infra.lang.rules.Assertion elem);

    /**
     * Gets all the elements for the property Antecedents
     *
     * @return The elements for the property Antecedents
     */
    Collection<org.xowl.infra.lang.rules.Assertion> getAllAntecedents();

    /**
     * Sets the value for the property Guard
     *
     * @param elem The value to set
     */
    void setGuard(org.xowl.infra.lang.owl2.LiteralExpression elem);

    /**
     * Gets the value for the property Guard
     *
     * @return The value for the property Guard
     */
    org.xowl.infra.lang.owl2.LiteralExpression getGuard();

    /**
     * Sets the value for the property HasIRI
     *
     * @param elem The value to set
     */
    void setHasIRI(org.xowl.infra.lang.owl2.IRI elem);

    /**
     * Gets the value for the property HasIRI
     *
     * @return The value for the property HasIRI
     */
    org.xowl.infra.lang.owl2.IRI getHasIRI();

    /**
     * Adds an element to the property Consequents
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addConsequents(org.xowl.infra.lang.rules.Assertion elem);

    /**
     * Removes an element from the property Consequents
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeConsequents(org.xowl.infra.lang.rules.Assertion elem);

    /**
     * Gets all the elements for the property Consequents
     *
     * @return The elements for the property Consequents
     */
    Collection<org.xowl.infra.lang.rules.Assertion> getAllConsequents();

}
