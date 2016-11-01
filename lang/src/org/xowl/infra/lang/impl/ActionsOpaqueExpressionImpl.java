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
 * The default implementation for the concrete OWL class OpaqueExpression
 *
 * @author xOWL code generator
 */
public class ActionsOpaqueExpressionImpl implements org.xowl.infra.lang.actions.OpaqueExpression {
    /**
     * The backing data for the property Value
     */
    private Object __implValue;

    /**
     * Adds a value to the property Value
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValue(Object elem) {
        __implValue = elem;
    }

    /**
     * Removes a value from the property Value
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValue(Object elem) {
        __implValue = null;
    }

    /**
     * Adds a value to the property Value
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValue(Object elem) {
        doSimpleAddValue(elem);
    }

    /**
     * Removes a value from the property Value
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValue(Object elem) {
        doSimpleRemoveValue(elem);
    }

    /**
     * Tries to add a value to the property Value and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValue(Object elem) {
        doPropertyAddValue(elem);
    }

    /**
     * Tries to remove a value from the property Value and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValue(Object elem) {
        doPropertyRemoveValue(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Value
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValue(Object elem) {
        doGraphAddValue(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Value
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValue(Object elem) {
        doGraphRemoveValue(elem);
    }

    @Override
    public Object getValue() {
        return __implValue;
    }

    @Override
    public void setValue(Object elem) {
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
        this.__implValue = null;
    }
}
