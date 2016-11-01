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
 * Represents the base interface for AnnotationProperty
 * Original OWL class is http://xowl.org/infra/lang/runtime#AnnotationProperty
 *
 * @author xOWL code generator
 */
public interface AnnotationProperty extends Interpretation {
    /**
     * Adds an element to the property SubAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSubAnnotProperty(AnnotationProperty elem);

    /**
     * Removes an element from the property SubAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSubAnnotProperty(AnnotationProperty elem);

    /**
     * Gets all the elements for the property SubAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @return The elements for the property SubAnnotProperty
     */
    Collection<AnnotationProperty> getAllSubAnnotProperty();

    /**
     * Adds an element to the property SuperAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @param elem The element to add
     * @return Whether the operation resulted in a new element (false if the element was already there)
     */
    boolean addSuperAnnotProperty(AnnotationProperty elem);

    /**
     * Removes an element from the property SuperAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @param elem The element to remove
     * @return Whether the operation resulted in the element being removed
     */
    boolean removeSuperAnnotProperty(AnnotationProperty elem);

    /**
     * Gets all the elements for the property SuperAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @return The elements for the property SuperAnnotProperty
     */
    Collection<AnnotationProperty> getAllSuperAnnotProperty();

}
