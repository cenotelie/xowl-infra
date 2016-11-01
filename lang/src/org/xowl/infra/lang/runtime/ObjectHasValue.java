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
 * Represents the base interface for the OWL class ObjectHasValue
 *
 * @author xOWL code generator
 */
public interface ObjectHasValue extends org.xowl.infra.lang.runtime.ObjectPropertyRestriction {
    /**
     * Sets the value for the property Individual
     *
     * @param elem The value to set
     */
    void setIndividual(org.xowl.infra.lang.runtime.Individual elem);

    /**
     * Gets the value for the property Individual
     *
     * @return The value for the property Individual
     */
    org.xowl.infra.lang.runtime.Individual getIndividual();

}
