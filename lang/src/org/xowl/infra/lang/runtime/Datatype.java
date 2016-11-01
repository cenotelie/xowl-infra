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
 * Represents the base interface for the OWL class Datatype
 *
 * @author xOWL code generator
 */
public interface Datatype extends org.xowl.infra.lang.runtime.Interpretation, org.xowl.infra.lang.runtime.Class_OR_Datatype {
    /**
     * Sets the value for the property DataBase
     *
     * @param elem The value to set
     */
    void setDataBase(org.xowl.infra.lang.runtime.Datatype elem);

    /**
     * Gets the value for the property DataBase
     *
     * @return The value for the property DataBase
     */
    org.xowl.infra.lang.runtime.Datatype getDataBase();

    /**
     * Sets the value for the property DataComplementOf
     *
     * @param elem The value to set
     */
    void setDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem);

    /**
     * Gets the value for the property DataComplementOf
     *
     * @return The value for the property DataComplementOf
     */
    org.xowl.infra.lang.runtime.Datatype getDataComplementOf();

    /**
     * Adds an element to the property DataIntersectionOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem);

    /**
     * Removes an element from the property DataIntersectionOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem);

    /**
     * Gets all the elements for the property DataIntersectionOf
     *
     * @return The elements for the property DataIntersectionOf
     */
    Collection<org.xowl.infra.lang.runtime.Datatype> getAllDataIntersectionOf();

    /**
     * Adds an element to the property DataOneOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataOneOf(org.xowl.infra.lang.runtime.Literal elem);

    /**
     * Removes an element from the property DataOneOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataOneOf(org.xowl.infra.lang.runtime.Literal elem);

    /**
     * Gets all the elements for the property DataOneOf
     *
     * @return The elements for the property DataOneOf
     */
    Collection<org.xowl.infra.lang.runtime.Literal> getAllDataOneOf();

    /**
     * Adds an element to the property DataRestrictions
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem);

    /**
     * Removes an element from the property DataRestrictions
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem);

    /**
     * Gets all the elements for the property DataRestrictions
     *
     * @return The elements for the property DataRestrictions
     */
    Collection<org.xowl.infra.lang.runtime.DatatypeRestriction> getAllDataRestrictions();

    /**
     * Adds an element to the property DataUnionOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem);

    /**
     * Removes an element from the property DataUnionOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem);

    /**
     * Gets all the elements for the property DataUnionOf
     *
     * @return The elements for the property DataUnionOf
     */
    Collection<org.xowl.infra.lang.runtime.Datatype> getAllDataUnionOf();

}
