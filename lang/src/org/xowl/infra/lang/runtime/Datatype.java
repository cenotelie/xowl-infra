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
 * Represents the base interface for Datatype
 * Original OWL class is http://xowl.org/infra/lang/runtime#Datatype
 *
 * @author xOWL code generator
 */
public interface Datatype extends Interpretation, RangeOfRange {
    /**
     * Sets the value for the property DataBase
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The value to set
     */
    void setDataBase(Datatype elem);

    /**
     * Gets the value for the property DataBase
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @return The value for the property DataBase
     */
    Datatype getDataBase();

    /**
     * Sets the value for the property DataComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     *
     * @param elem The value to set
     */
    void setDataComplementOf(Datatype elem);

    /**
     * Gets the value for the property DataComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     *
     * @return The value for the property DataComplementOf
     */
    Datatype getDataComplementOf();

    /**
     * Adds an element to the property DataIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataIntersectionOf(Datatype elem);

    /**
     * Removes an element from the property DataIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataIntersectionOf(Datatype elem);

    /**
     * Gets all the elements for the property DataIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @return The elements for the property DataIntersectionOf
     */
    Collection<Datatype> getAllDataIntersectionOf();

    /**
     * Adds an element to the property DataOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataOneOf(Literal elem);

    /**
     * Removes an element from the property DataOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataOneOf(Literal elem);

    /**
     * Gets all the elements for the property DataOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @return The elements for the property DataOneOf
     */
    Collection<Literal> getAllDataOneOf();

    /**
     * Adds an element to the property DataRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataRestrictions(DatatypeRestriction elem);

    /**
     * Removes an element from the property DataRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataRestrictions(DatatypeRestriction elem);

    /**
     * Gets all the elements for the property DataRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @return The elements for the property DataRestrictions
     */
    Collection<DatatypeRestriction> getAllDataRestrictions();

    /**
     * Adds an element to the property DataUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addDataUnionOf(Datatype elem);

    /**
     * Removes an element from the property DataUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeDataUnionOf(Datatype elem);

    /**
     * Gets all the elements for the property DataUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @return The elements for the property DataUnionOf
     */
    Collection<Datatype> getAllDataUnionOf();

}
