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
 * Represents the base interface for the OWL class ObjectPropertyAssertion
 *
 * @author xOWL code generator
 */
public interface ObjectPropertyAssertion extends org.xowl.infra.lang.runtime.PropertyAssertion {
    /**
     * Sets the value for the property Property
     *
     * @param elem The value to set
     */
    void setProperty(org.xowl.infra.lang.runtime.ObjectProperty elem);

    /**
     * Gets the value for the property Property
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The value for the property Property
     */
    org.xowl.infra.lang.runtime.ObjectProperty getPropertyAs(org.xowl.infra.lang.runtime.ObjectProperty type);

    /**
     * Sets the value for the property ValueIndividual
     *
     * @param elem The value to set
     */
    void setValueIndividual(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Gets the value for the property ValueIndividual
     *
     * @return The value for the property ValueIndividual
     */
    org.xowl.infra.lang.runtime.Individual getValueIndividual();

}
