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

package org.xowl.infra.lang.actions;

import java.util.*;

/**
 * Represents the base interface for QueryVariable
 * Original OWL class is http://xowl.org/infra/lang/actions#QueryVariable
 *
 * @author xOWL code generator
 */
public interface QueryVariable extends DynamicExpression {
    /**
     * Sets the value for the property Name
     * Original OWL property is http://xowl.org/infra/lang/actions#name
     *
     * @param elem The value to set
     */
    void setName(String elem);

    /**
     * Gets the value for the property Name
     * Original OWL property is http://xowl.org/infra/lang/actions#name
     *
     * @return The value for the property Name
     */
    String getName();

}
