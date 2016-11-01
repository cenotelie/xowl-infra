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
 * Represents the base interface for the OWL class DataPropertyAssertion
 *
 * @author xOWL code generator
 */
public interface DataPropertyAssertion extends org.xowl.infra.lang.runtime.PropertyAssertion, org.xowl.infra.lang.runtime.DataPropertyAssertion_OR_DatatypeRestriction {
    /**
     * Sets the value for the property Property
     *
     * @param elem The value to set
     */
    void setProperty(org.xowl.infra.lang.runtime.DataProperty elem);

    /**
     * Gets the value for the property Property
     *
     * @param type An element of the type expected in result (may be null)
     *             This parameter is used to disambiguate among overloads.
     * @return The value for the property Property
     */
    org.xowl.infra.lang.runtime.DataProperty getPropertyAs(org.xowl.infra.lang.runtime.DataProperty type);

}
