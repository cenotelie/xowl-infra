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
 * The default implementation for the concrete OWL class ObjectPropertyAssertion
 *
 * @author xOWL code generator
 */
public class RuntimeObjectPropertyAssertionImpl implements org.xowl.infra.lang.runtime.ObjectPropertyAssertion {
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
    private org.xowl.infra.lang.runtime.ObjectProperty __implProperty;

    /**
     * Adds a value to the property Property
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implProperty = elem;
    }

    /**
     * Removes a value from the property Property
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implProperty = null;
    }

    /**
     * Adds a value to the property Property
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleAddProperty(elem);
    }

    /**
     * Removes a value from the property Property
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleRemoveProperty(elem);
    }

    /**
     * Tries to add a value to the property Property and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyAddProperty(elem);
    }

    /**
     * Tries to remove a value from the property Property and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyRemoveProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Property
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphAddProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Property
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
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
            doDispatchAddProperty((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        } else {
            if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
                throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
            doDispatchRemoveProperty(__implProperty);
            doDispatchAddProperty((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        }
    }

    @Override
    public org.xowl.infra.lang.runtime.ObjectProperty getPropertyAs(org.xowl.infra.lang.runtime.ObjectProperty type) {
        return __implProperty;
    }

    @Override
    public void setProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
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
     */
    private org.xowl.infra.lang.runtime.Individual __implValueIndividual;

    /**
     * Adds a value to the property ValueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
        __implValueIndividual = elem;
    }

    /**
     * Removes a value from the property ValueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
        __implValueIndividual = null;
    }

    /**
     * Adds a value to the property ValueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleAddValueIndividual(elem);
    }

    /**
     * Removes a value from the property ValueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleRemoveValueIndividual(elem);
    }

    /**
     * Tries to add a value to the property ValueIndividual and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyAddValueIndividual(elem);
    }

    /**
     * Tries to remove a value from the property ValueIndividual and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyRemoveValueIndividual(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ValueIndividual
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphAddValueIndividual(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ValueIndividual
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphRemoveValueIndividual(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Individual getValueIndividual() {
        return __implValueIndividual;
    }

    @Override
    public void setValueIndividual(org.xowl.infra.lang.runtime.Individual elem) {
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
        this.__implIsNegative = false;
        this.__implProperty = null;
        this.__implValueIndividual = null;
    }
}
