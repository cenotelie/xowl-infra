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

package org.xowl.infra.lang.rules;

import org.xowl.infra.lang.impl.*;

import java.util.*;

/**
 * The default implementation for the concrete OWL class rules
 *
 * @author xOWL code generator
 */
public class RulesFactory {
    /**
     * Creates a new instance of Assertion
     *
     * @return A new instance of Assertion
     */
    public static Assertion newAssertion() {
        return new RulesAssertionImpl();
    }

    /**
     * Creates a new instance of Rule
     *
     * @return A new instance of Rule
     */
    public static Rule newRule() {
        return new RulesRuleImpl();
    }

}
