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

import org.xowl.infra.lang.runtime.*;
import org.xowl.infra.lang.runtime.Class;

import java.util.*;

/**
 * The default implementation for DataPropertyAssertion
 * Original OWL class is http://xowl.org/infra/lang/runtime#DataPropertyAssertion
 *
 * @author xOWL code generator
 */
public class RuntimeDataPropertyAssertionImpl implements DataPropertyAssertion {
    /**
     * The backing data for the property IsNegative
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#isNegative
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
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#property
     */
    private DataProperty __implProperty;

    /**
     * Adds a value to the property Property
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddProperty(DataProperty elem) {
        __implProperty = elem;
    }

    /**
     * Removes a value from the property Property
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveProperty(DataProperty elem) {
        __implProperty = null;
    }

    /**
     * Adds a value to the property Property
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddProperty(DataProperty elem) {
        doSimpleAddProperty(elem);
    }

    /**
     * Removes a value from the property Property
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveProperty(DataProperty elem) {
        doSimpleRemoveProperty(elem);
    }

    /**
     * Tries to add a value to the property Property and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddProperty(DataProperty elem) {
        doPropertyAddProperty(elem);
    }

    /**
     * Tries to remove a value from the property Property and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveProperty(DataProperty elem) {
        doPropertyRemoveProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Property
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddProperty(DataProperty elem) {
        doGraphAddProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Property
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveProperty(DataProperty elem) {
        doGraphRemoveProperty(elem);
    }

    @Override
    public Property getPropertyAs(Property type) {
        return __implProperty;
    }

    @Override
    public void setProperty(Property elem) {
        if (__implProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveProperty(__implProperty);
        } else if (__implProperty == null) {
            doDispatchAddProperty((DataProperty) elem);
        } else {
            if (!(elem instanceof DataProperty))
                throw new IllegalArgumentException("Expected type DataProperty");
            doDispatchRemoveProperty(__implProperty);
            doDispatchAddProperty((DataProperty) elem);
        }
    }

    @Override
    public DataProperty getPropertyAs(DataProperty type) {
        return __implProperty;
    }

    @Override
    public void setProperty(DataProperty elem) {
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
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#valueLiteral
     */
    private Literal __implValueLiteral;

    /**
     * Adds a value to the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValueLiteral(Literal elem) {
        __implValueLiteral = elem;
    }

    /**
     * Removes a value from the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValueLiteral(Literal elem) {
        __implValueLiteral = null;
    }

    /**
     * Adds a value to the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValueLiteral(Literal elem) {
        doSimpleAddValueLiteral(elem);
    }

    /**
     * Removes a value from the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValueLiteral(Literal elem) {
        doSimpleRemoveValueLiteral(elem);
    }

    /**
     * Tries to add a value to the property ValueLiteral and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValueLiteral(Literal elem) {
        doPropertyAddValueLiteral(elem);
    }

    /**
     * Tries to remove a value from the property ValueLiteral and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValueLiteral(Literal elem) {
        doPropertyRemoveValueLiteral(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ValueLiteral
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValueLiteral(Literal elem) {
        doGraphAddValueLiteral(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ValueLiteral
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValueLiteral(Literal elem) {
        doGraphRemoveValueLiteral(elem);
    }

    @Override
    public Literal getValueLiteral() {
        return __implValueLiteral;
    }

    @Override
    public void setValueLiteral(Literal elem) {
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
        // initialize property http://xowl.org/infra/lang/runtime#isNegative
        this.__implIsNegative = false;
        // initialize property http://xowl.org/infra/lang/runtime#property
        this.__implProperty = null;
        // initialize property http://xowl.org/infra/lang/runtime#valueLiteral
        this.__implValueLiteral = null;
    }
}
