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
 * The default implementation for the concrete OWL class ObjectMaxCardinality
 *
 * @author xOWL code generator
 */
public class RuntimeObjectMaxCardinalityImpl implements org.xowl.infra.lang.runtime.ObjectMaxCardinality {
    /**
     * The backing data for the property Cardinality
     */
    private int __implCardinality;

    @Override
    public int getCardinality() {
        return __implCardinality;
    }

    @Override
    public void setCardinality(int elem) {
        __implCardinality = elem;
    }

    /**
     * The backing data for the property Classe
     */
    private org.xowl.infra.lang.runtime.Class __implClasse;

    /**
     * Adds a value to the property Classe
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClasse(org.xowl.infra.lang.runtime.Class elem) {
        __implClasse = elem;
    }

    /**
     * Removes a value from the property Classe
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClasse(org.xowl.infra.lang.runtime.Class elem) {
        __implClasse = null;
    }

    /**
     * Adds a value to the property Classe
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClasse(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddClasse(elem);
    }

    /**
     * Removes a value from the property Classe
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClasse(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveClasse(elem);
    }

    /**
     * Tries to add a value to the property Classe and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClasse(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddClasse(elem);
    }

    /**
     * Tries to remove a value from the property Classe and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClasse(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveClasse(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Classe
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClasse(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddClasse(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Classe
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClasse(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveClasse(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Class getClasse() {
        return __implClasse;
    }

    @Override
    public void setClasse(org.xowl.infra.lang.runtime.Class elem) {
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
     */
    private org.xowl.infra.lang.runtime.ObjectProperty __implObjectProperty;

    /**
     * Adds a value to the property ObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implObjectProperty = elem;
    }

    /**
     * Removes a value from the property ObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implObjectProperty = null;
    }

    /**
     * Adds a value to the property ObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleAddObjectProperty(elem);
    }

    /**
     * Removes a value from the property ObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleRemoveObjectProperty(elem);
    }

    /**
     * Tries to add a value to the property ObjectProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyAddObjectProperty(elem);
    }

    /**
     * Tries to remove a value from the property ObjectProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyRemoveObjectProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphAddObjectProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphRemoveObjectProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.ObjectProperty getObjectProperty() {
        return __implObjectProperty;
    }

    @Override
    public void setObjectProperty(org.xowl.infra.lang.runtime.ObjectProperty elem) {
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
     * Constructor for the implementation of ObjectMaxCardinality
     */
    public RuntimeObjectMaxCardinalityImpl() {
        this.__implCardinality = 0;
        this.__implClasse = null;
        this.__implObjectProperty = null;
    }
}
