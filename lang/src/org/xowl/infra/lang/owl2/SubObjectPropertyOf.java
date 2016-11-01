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
 * Represents the base interface for SubObjectPropertyOf
 * Original OWL class is http://xowl.org/infra/lang/owl2#SubObjectPropertyOf
 *
 * @author xOWL code generator
 */
public interface SubObjectPropertyOf extends ObjectPropertyAxiom, DomainOfObjectProperty {
    /**
     * Sets the value for the property ObjectPropertyChain
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The value to set
     */
    void setObjectPropertyChain(ObjectPropertySequenceExpression elem);

    /**
     * Gets the value for the property ObjectPropertyChain
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @return The value for the property ObjectPropertyChain
     */
    ObjectPropertySequenceExpression getObjectPropertyChain();

    /**
     * Sets the value for the property SuperObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The value to set
     */
    void setSuperObjectProperty(ObjectPropertyExpression elem);

    /**
     * Gets the value for the property SuperObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @return The value for the property SuperObjectProperty
     */
    ObjectPropertyExpression getSuperObjectProperty();

}
