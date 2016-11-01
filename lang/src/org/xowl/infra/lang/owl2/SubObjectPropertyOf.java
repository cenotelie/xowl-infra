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
 * Represents the base interface for the OWL class SubObjectPropertyOf
 *
 * @author xOWL code generator
 */
public interface SubObjectPropertyOf extends org.xowl.infra.lang.owl2.ObjectPropertyAxiom, org.xowl.infra.lang.owl2.AsymmetricObjectProperty_OR_FunctionalObjectProperty_OR_InverseFunctionalObjectProperty_OR_InverseO4 {
    /**
     * Sets the value for the property SuperObjectProperty
     *
     * @param elem The value to set
     */
    void setSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);

    /**
     * Gets the value for the property SuperObjectProperty
     *
     * @return The value for the property SuperObjectProperty
     */
    org.xowl.infra.lang.owl2.ObjectPropertyExpression getSuperObjectProperty();

    /**
     * Sets the value for the property ObjectPropertyChain
     *
     * @param elem The value to set
     */
    void setObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem);

    /**
     * Gets the value for the property ObjectPropertyChain
     *
     * @return The value for the property ObjectPropertyChain
     */
    org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression getObjectPropertyChain();

}
