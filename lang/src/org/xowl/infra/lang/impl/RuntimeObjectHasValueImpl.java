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
 * The default implementation for ObjectHasValue
 * Original OWL class is http://xowl.org/infra/lang/runtime#ObjectHasValue
 *
 * @author xOWL code generator
 */
public class RuntimeObjectHasValueImpl implements ObjectHasValue {
    /**
     * The backing data for the property Individual
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#individual
     */
    private Individual __implIndividual;

    /**
     * Adds a value to the property Individual
     * Original OWL property is http://xowl.org/infra/lang/runtime#individual
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividual(Individual elem) {
        __implIndividual = elem;
    }

    /**
     * Removes a value from the property Individual
     * Original OWL property is http://xowl.org/infra/lang/runtime#individual
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividual(Individual elem) {
        __implIndividual = null;
    }

    /**
     * Adds a value to the property Individual
     * Original OWL property is http://xowl.org/infra/lang/runtime#individual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividual(Individual elem) {
        doSimpleAddIndividual(elem);
    }

    /**
     * Removes a value from the property Individual
     * Original OWL property is http://xowl.org/infra/lang/runtime#individual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividual(Individual elem) {
        doSimpleRemoveIndividual(elem);
    }

    /**
     * Tries to add a value to the property Individual and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#individual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividual(Individual elem) {
        doPropertyAddIndividual(elem);
    }

    /**
     * Tries to remove a value from the property Individual and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#individual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividual(Individual elem) {
        doPropertyRemoveIndividual(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Individual
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#individual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividual(Individual elem) {
        doGraphAddIndividual(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Individual
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#individual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividual(Individual elem) {
        doGraphRemoveIndividual(elem);
    }

    @Override
    public Individual getIndividual() {
        return __implIndividual;
    }

    @Override
    public void setIndividual(Individual elem) {
        if (__implIndividual == elem)
            return;
        if (elem == null) {
            doDispatchRemoveIndividual(__implIndividual);
        } else if (__implIndividual == null) {
            doDispatchAddIndividual(elem);
        } else {
            doDispatchRemoveIndividual(__implIndividual);
            doDispatchAddIndividual(elem);
        }
    }

    /**
     * The backing data for the property ObjectProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#objectProperty
     */
    private ObjectProperty __implObjectProperty;

    /**
     * Adds a value to the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectProperty(ObjectProperty elem) {
        __implObjectProperty = elem;
    }

    /**
     * Removes a value from the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectProperty(ObjectProperty elem) {
        __implObjectProperty = null;
    }

    /**
     * Adds a value to the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectProperty(ObjectProperty elem) {
        doSimpleAddObjectProperty(elem);
    }

    /**
     * Removes a value from the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectProperty(ObjectProperty elem) {
        doSimpleRemoveObjectProperty(elem);
    }

    /**
     * Tries to add a value to the property ObjectProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectProperty(ObjectProperty elem) {
        doPropertyAddObjectProperty(elem);
    }

    /**
     * Tries to remove a value from the property ObjectProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectProperty(ObjectProperty elem) {
        doPropertyRemoveObjectProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectProperty(ObjectProperty elem) {
        doGraphAddObjectProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectProperty(ObjectProperty elem) {
        doGraphRemoveObjectProperty(elem);
    }

    @Override
    public ObjectProperty getObjectProperty() {
        return __implObjectProperty;
    }

    @Override
    public void setObjectProperty(ObjectProperty elem) {
        if (__implObjectProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveObjectProperty(__implObjectProperty);
        } else if (__implObjectProperty == null) {
            doDispatchAddObjectProperty(elem);
        } else {
            doDispatchRemoveObjectProperty(__implObjectProperty);
            doDispatchAddObjectProperty(elem);
        }
    }

    /**
     * Constructor for the implementation of ObjectHasValue
     */
    public RuntimeObjectHasValueImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#individual
        this.__implIndividual = null;
        // initialize property http://xowl.org/infra/lang/runtime#objectProperty
        this.__implObjectProperty = null;
    }
}
