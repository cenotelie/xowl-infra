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
 * The default implementation for ObjectPropertyAssertion
 * Original OWL class is http://xowl.org/infra/lang/runtime#ObjectPropertyAssertion
 *
 * @author xOWL code generator
 */
public class RuntimeObjectPropertyAssertionImpl implements ObjectPropertyAssertion {
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
    private ObjectProperty __implProperty;

    /**
     * Adds a value to the property Property
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddProperty(ObjectProperty elem) {
        __implProperty = elem;
    }

    /**
     * Removes a value from the property Property
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveProperty(ObjectProperty elem) {
        __implProperty = null;
    }

    /**
     * Adds a value to the property Property
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddProperty(ObjectProperty elem) {
        doSimpleAddProperty(elem);
    }

    /**
     * Removes a value from the property Property
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveProperty(ObjectProperty elem) {
        doSimpleRemoveProperty(elem);
    }

    /**
     * Tries to add a value to the property Property and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddProperty(ObjectProperty elem) {
        doPropertyAddProperty(elem);
    }

    /**
     * Tries to remove a value from the property Property and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveProperty(ObjectProperty elem) {
        doPropertyRemoveProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Property
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddProperty(ObjectProperty elem) {
        doGraphAddProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Property
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#property
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveProperty(ObjectProperty elem) {
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
            doDispatchAddProperty((ObjectProperty) elem);
        } else {
            if (!(elem instanceof ObjectProperty))
                throw new IllegalArgumentException("Expected type ObjectProperty");
            doDispatchRemoveProperty(__implProperty);
            doDispatchAddProperty((ObjectProperty) elem);
        }
    }

    @Override
    public ObjectProperty getPropertyAs(ObjectProperty type) {
        return __implProperty;
    }

    @Override
    public void setProperty(ObjectProperty elem) {
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
     * The backing data for the property ValueIndividual
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#valueIndividual
     */
    private Individual __implValueIndividual;

    /**
     * Adds a value to the property ValueIndividual
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValueIndividual(Individual elem) {
        __implValueIndividual = elem;
    }

    /**
     * Removes a value from the property ValueIndividual
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValueIndividual(Individual elem) {
        __implValueIndividual = null;
    }

    /**
     * Adds a value to the property ValueIndividual
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValueIndividual(Individual elem) {
        doSimpleAddValueIndividual(elem);
    }

    /**
     * Removes a value from the property ValueIndividual
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValueIndividual(Individual elem) {
        doSimpleRemoveValueIndividual(elem);
    }

    /**
     * Tries to add a value to the property ValueIndividual and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValueIndividual(Individual elem) {
        doPropertyAddValueIndividual(elem);
    }

    /**
     * Tries to remove a value from the property ValueIndividual and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValueIndividual(Individual elem) {
        doPropertyRemoveValueIndividual(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ValueIndividual
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValueIndividual(Individual elem) {
        doGraphAddValueIndividual(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ValueIndividual
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValueIndividual(Individual elem) {
        doGraphRemoveValueIndividual(elem);
    }

    @Override
    public Individual getValueIndividual() {
        return __implValueIndividual;
    }

    @Override
    public void setValueIndividual(Individual elem) {
        if (__implValueIndividual == elem)
            return;
        if (elem == null) {
            doDispatchRemoveValueIndividual(__implValueIndividual);
        } else if (__implValueIndividual == null) {
            doDispatchAddValueIndividual(elem);
        } else {
            doDispatchRemoveValueIndividual(__implValueIndividual);
            doDispatchAddValueIndividual(elem);
        }
    }

    /**
     * Constructor for the implementation of ObjectPropertyAssertion
     */
    public RuntimeObjectPropertyAssertionImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#isNegative
        this.__implIsNegative = false;
        // initialize property http://xowl.org/infra/lang/runtime#property
        this.__implProperty = null;
        // initialize property http://xowl.org/infra/lang/runtime#valueIndividual
        this.__implValueIndividual = null;
    }
}
