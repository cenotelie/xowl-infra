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

package org.xowl.infra.lang.owl2;

import java.util.*;

/**
 * Represents the base interface for Ontology
 * Original OWL class is http://xowl.org/infra/lang/owl2#Ontology
 *
 * @author xOWL code generator
 */
public interface Ontology extends DomainOfAnnotations {
    /**
     * Adds an element to the property Contains
     * Original OWL property is http://xowl.org/infra/lang/runtime#contains
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addContains(org.xowl.infra.lang.runtime.Entity elem);

    /**
     * Removes an element from the property Contains
     * Original OWL property is http://xowl.org/infra/lang/runtime#contains
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeContains(org.xowl.infra.lang.runtime.Entity elem);

    /**
     * Gets all the elements for the property Contains
     * Original OWL property is http://xowl.org/infra/lang/runtime#contains
     *
     * @return The elements for the property Contains
     */
    Collection<org.xowl.infra.lang.runtime.Entity> getAllContains();

    /**
     * Sets the value for the property HasIRI
     * Original OWL property is http://xowl.org/infra/lang/owl2#hasIRI
     *
     * @param elem The value to set
     */
    void setHasIRI(IRI elem);

    /**
     * Gets the value for the property HasIRI
     * Original OWL property is http://xowl.org/infra/lang/owl2#hasIRI
     *
     * @return The value for the property HasIRI
     */
    IRI getHasIRI();

}
