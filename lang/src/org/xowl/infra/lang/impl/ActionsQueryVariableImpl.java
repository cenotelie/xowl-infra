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

package org.xowl.infra.lang.impl;

import java.util.*;

/**
 * The default implementation for the concrete OWL class QueryVariable
 *
 * @author xOWL code generator
 */
public class ActionsQueryVariableImpl implements org.xowl.infra.lang.actions.QueryVariable {
    /**
     * The backing data for the property Name
     */
    private String __implName;

    @Override
    public String getName() {
        return __implName;
    }

    @Override
    public void setName(String elem) {
        __implName = elem;
    }

    /**
     * Constructor for the implementation of QueryVariable
     */
    public ActionsQueryVariableImpl() {
        this.__implName = null;
    }
}
