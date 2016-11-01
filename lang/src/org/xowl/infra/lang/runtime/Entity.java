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
 * Represents the base interface for Entity
 * Original OWL class is http://xowl.org/infra/lang/runtime#Entity
 *
 * @author xOWL code generator
 */
public interface Entity extends Value {
    /**
     * Sets the value for the property ContainedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#containedBy
     *
     * @param elem The value to set
     */
    void setContainedBy(org.xowl.infra.lang.owl2.Ontology elem);

    /**
     * Gets the value for the property ContainedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#containedBy
     *
     * @return The value for the property ContainedBy
     */
    org.xowl.infra.lang.owl2.Ontology getContainedBy();

    /**
     * Sets the value for the property HasIRI
     * Original OWL property is http://xowl.org/infra/lang/runtime#hasIRI
     *
     * @param elem The value to set
     */
    void setHasIRI(org.xowl.infra.lang.owl2.IRI elem);

    /**
     * Gets the value for the property HasIRI
     * Original OWL property is http://xowl.org/infra/lang/runtime#hasIRI
     *
     * @return The value for the property HasIRI
     */
    org.xowl.infra.lang.owl2.IRI getHasIRI();

    /**
     * Adds an element to the property InterpretedAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretedAs
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addInterpretedAs(Interpretation elem);

    /**
     * Removes an element from the property InterpretedAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretedAs
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeInterpretedAs(Interpretation elem);

    /**
     * Gets all the elements for the property InterpretedAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretedAs
     *
     * @return The elements for the property InterpretedAs
     */
    Collection<Interpretation> getAllInterpretedAs();

}
