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
 * Represents the base interface for the OWL class FunctionDefinitionAxiom
 *
 * @author xOWL code generator
 */
public interface FunctionDefinitionAxiom extends org.xowl.infra.lang.owl2.Axiom {
    /**
     * Sets the value for the property Definition
     *
     * @param elem The value to set
     */
    void setDefinition(Object elem);

    /**
     * Gets the value for the property Definition
     *
     * @return The value for the property Definition
     */
    Object getDefinition();

    /**
     * Sets the value for the property Function
     *
     * @param elem The value to set
     */
    void setFunction(org.xowl.infra.lang.actions.FunctionExpression elem);

    /**
     * Gets the value for the property Function
     *
     * @return The value for the property Function
     */
    org.xowl.infra.lang.actions.FunctionExpression getFunction();

}
