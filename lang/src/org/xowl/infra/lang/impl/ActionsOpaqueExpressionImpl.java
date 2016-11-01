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

import org.xowl.infra.lang.actions.*;

import java.util.*;

/**
 * The default implementation for OpaqueExpression
 * Original OWL class is http://xowl.org/infra/lang/actions#OpaqueExpression
 *
 * @author xOWL code generator
 */
public class ActionsOpaqueExpressionImpl implements OpaqueExpression {
    /**
     * The backing data for the property Value
     * This implements the storage for original OWL property http://xowl.org/infra/lang/actions#value
     */
    private java.lang.Object __implValue;

    /**
     * Adds a value to the property Value
     * Original OWL property is http://xowl.org/infra/lang/actions#value
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValue(java.lang.Object elem) {
        __implValue = elem;
    }

    /**
     * Removes a value from the property Value
     * Original OWL property is http://xowl.org/infra/lang/actions#value
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValue(java.lang.Object elem) {
        __implValue = null;
    }

    /**
     * Adds a value to the property Value
     * Original OWL property is http://xowl.org/infra/lang/actions#value
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValue(java.lang.Object elem) {
        doSimpleAddValue(elem);
    }

    /**
     * Removes a value from the property Value
     * Original OWL property is http://xowl.org/infra/lang/actions#value
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValue(java.lang.Object elem) {
        doSimpleRemoveValue(elem);
    }

    /**
     * Tries to add a value to the property Value and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/actions#value
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValue(java.lang.Object elem) {
        doPropertyAddValue(elem);
    }

    /**
     * Tries to remove a value from the property Value and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/actions#value
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValue(java.lang.Object elem) {
        doPropertyRemoveValue(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Value
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/actions#value
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValue(java.lang.Object elem) {
        doGraphAddValue(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Value
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/actions#value
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValue(java.lang.Object elem) {
        doGraphRemoveValue(elem);
    }

    @Override
    public java.lang.Object getValue() {
        return __implValue;
    }

    @Override
    public void setValue(java.lang.Object elem) {
        if (__implValue == elem)
            return;
        if (elem == null) {
            doDispatchRemoveValue(__implValue);
        } else if (__implValue == null) {
            doDispatchAddValue(elem);
        } else {
            doDispatchRemoveValue(__implValue);
            doDispatchAddValue(elem);
        }
    }

    /**
     * Constructor for the implementation of OpaqueExpression
     */
    public ActionsOpaqueExpressionImpl() {
        // initialize property http://xowl.org/infra/lang/actions#value
        this.__implValue = null;
    }
}
