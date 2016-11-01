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

import org.xowl.infra.lang.impl.*;

import java.util.*;

/**
 * The default implementation for the concrete OWL class actions
 *
 * @author xOWL code generator
 */
public class ActionsFactory {
    /**
     * Creates a new instance of FunctionDefinitionAxiom
     *
     * @return A new instance of FunctionDefinitionAxiom
     */
    public static FunctionDefinitionAxiom newFunctionDefinitionAxiom() {
        return new ActionsFunctionDefinitionAxiomImpl();
    }

    /**
     * Creates a new instance of OpaqueExpression
     *
     * @return A new instance of OpaqueExpression
     */
    public static OpaqueExpression newOpaqueExpression() {
        return new ActionsOpaqueExpressionImpl();
    }

    /**
     * Creates a new instance of QueryVariable
     *
     * @return A new instance of QueryVariable
     */
    public static QueryVariable newQueryVariable() {
        return new ActionsQueryVariableImpl();
    }

}
