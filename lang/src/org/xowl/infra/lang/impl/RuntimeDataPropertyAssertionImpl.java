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
 * The default implementation for the concrete OWL class DataPropertyAssertion
 *
 * @author xOWL code generator
 */
public class RuntimeDataPropertyAssertionImpl implements org.xowl.infra.lang.runtime.DataPropertyAssertion {
    /**
     * The backing data for the property IsNegative
     */
    private boolean __implIsNegative;

    @Override
    public boolean getIsNegative() {
        return __implIsNegative;
    }

    @Override
    public void setIsNegative(boolean elem) {
        __implIsNegative = elem;
    }

    /**
     * The backing data for the property Property
     */
    private org.xowl.infra.lang.runtime.DataProperty __implProperty;

    /**
     * Adds a value to the property Property
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        __implProperty = elem;
    }

    /**
     * Removes a value from the property Property
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        __implProperty = null;
    }

    /**
     * Adds a value to the property Property
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doSimpleAddProperty(elem);
    }

    /**
     * Removes a value from the property Property
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doSimpleRemoveProperty(elem);
    }

    /**
     * Tries to add a value to the property Property and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doPropertyAddProperty(elem);
    }

    /**
     * Tries to remove a value from the property Property and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doPropertyRemoveProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Property
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doGraphAddProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Property
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doGraphRemoveProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Property getPropertyAs(org.xowl.infra.lang.runtime.Property type) {
        return __implProperty;
    }

    @Override
    public void setProperty(org.xowl.infra.lang.runtime.Property elem) {
        if (__implProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveProperty(__implProperty);
        } else if (__implProperty == null) {
            doDispatchAddProperty((org.xowl.infra.lang.runtime.DataProperty) elem);
        } else {
            if (!(elem instanceof org.xowl.infra.lang.runtime.DataProperty))
                throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.DataProperty");
            doDispatchRemoveProperty(__implProperty);
            doDispatchAddProperty((org.xowl.infra.lang.runtime.DataProperty) elem);
        }
    }

    @Override
    public org.xowl.infra.lang.runtime.DataProperty getPropertyAs(org.xowl.infra.lang.runtime.DataProperty type) {
        return __implProperty;
    }

    @Override
    public void setProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        if (__implProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveProperty(__implProperty);
        } else if (__implProperty == null) {
            doDispatchAddProperty(elem);
        } else {
            doDispatchRemoveProperty(__implProperty);
            doDispatchAddProperty(elem);
        }
    }

    /**
     * The backing data for the property ValueLiteral
     */
    private org.xowl.infra.lang.runtime.Literal __implValueLiteral;

    /**
     * Adds a value to the property ValueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        __implValueLiteral = elem;
    }

    /**
     * Removes a value from the property ValueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        __implValueLiteral = null;
    }

    /**
     * Adds a value to the property ValueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        doSimpleAddValueLiteral(elem);
    }

    /**
     * Removes a value from the property ValueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        doSimpleRemoveValueLiteral(elem);
    }

    /**
     * Tries to add a value to the property ValueLiteral and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        doPropertyAddValueLiteral(elem);
    }

    /**
     * Tries to remove a value from the property ValueLiteral and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        doPropertyRemoveValueLiteral(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ValueLiteral
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        doGraphAddValueLiteral(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ValueLiteral
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        doGraphRemoveValueLiteral(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Literal getValueLiteral() {
        return __implValueLiteral;
    }

    @Override
    public void setValueLiteral(org.xowl.infra.lang.runtime.Literal elem) {
        if (__implValueLiteral == elem)
            return;
        if (elem == null) {
            doDispatchRemoveValueLiteral(__implValueLiteral);
        } else if (__implValueLiteral == null) {
            doDispatchAddValueLiteral(elem);
        } else {
            doDispatchRemoveValueLiteral(__implValueLiteral);
            doDispatchAddValueLiteral(elem);
        }
    }

    /**
     * Constructor for the implementation of DataPropertyAssertion
     */
    public RuntimeDataPropertyAssertionImpl() {
        this.__implIsNegative = false;
        this.__implProperty = null;
        this.__implValueLiteral = null;
    }
}
