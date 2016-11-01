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

import org.xowl.infra.lang.owl2.*;

import java.util.*;

/**
 * The default implementation for IRI
 * Original OWL class is http://xowl.org/infra/lang/owl2#IRI
 *
 * @author xOWL code generator
 */
public class Owl2IRIImpl implements IRI {
    /**
     * The backing data for the property HasValue
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#hasValue
     */
    private String __implHasValue;

    @Override
    public String getHasValue() {
        return __implHasValue;
    }

    @Override
    public void setHasValue(String elem) {
        __implHasValue = elem;
    }

    /**
     * Constructor for the implementation of IRI
     */
    public Owl2IRIImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#hasValue
        this.__implHasValue = null;
    }
}
