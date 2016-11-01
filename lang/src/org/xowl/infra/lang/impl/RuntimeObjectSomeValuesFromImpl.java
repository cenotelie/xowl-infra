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
 * The default implementation for ObjectSomeValuesFrom
 * Original OWL class is http://xowl.org/infra/lang/runtime#ObjectSomeValuesFrom
 *
 * @author xOWL code generator
 */
public class RuntimeObjectSomeValuesFromImpl implements ObjectSomeValuesFrom {
    /**
     * The backing data for the property Classe
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classe
     */
    private Class __implClasse;

    /**
     * Adds a value to the property Classe
     * Original OWL property is http://xowl.org/infra/lang/runtime#classe
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClasse(Class elem) {
        __implClasse = elem;
    }

    /**
     * Removes a value from the property Classe
     * Original OWL property is http://xowl.org/infra/lang/runtime#classe
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClasse(Class elem) {
        __implClasse = null;
    }

    /**
     * Adds a value to the property Classe
     * Original OWL property is http://xowl.org/infra/lang/runtime#classe
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClasse(Class elem) {
        doSimpleAddClasse(elem);
    }

    /**
     * Removes a value from the property Classe
     * Original OWL property is http://xowl.org/infra/lang/runtime#classe
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClasse(Class elem) {
        doSimpleRemoveClasse(elem);
    }

    /**
     * Tries to add a value to the property Classe and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classe
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClasse(Class elem) {
        doPropertyAddClasse(elem);
    }

    /**
     * Tries to remove a value from the property Classe and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classe
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClasse(Class elem) {
        doPropertyRemoveClasse(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Classe
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classe
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClasse(Class elem) {
        doGraphAddClasse(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Classe
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classe
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClasse(Class elem) {
        doGraphRemoveClasse(elem);
    }

    @Override
    public Class getClasse() {
        return __implClasse;
    }

    @Override
    public void setClasse(Class elem) {
        if (__implClasse == elem)
            return;
        if (elem == null) {
            doDispatchRemoveClasse(__implClasse);
        } else if (__implClasse == null) {
            doDispatchAddClasse(elem);
        } else {
            doDispatchRemoveClasse(__implClasse);
            doDispatchAddClasse(elem);
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
     * Constructor for the implementation of ObjectSomeValuesFrom
     */
    public RuntimeObjectSomeValuesFromImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#classe
        this.__implClasse = null;
        // initialize property http://xowl.org/infra/lang/runtime#objectProperty
        this.__implObjectProperty = null;
    }
}
