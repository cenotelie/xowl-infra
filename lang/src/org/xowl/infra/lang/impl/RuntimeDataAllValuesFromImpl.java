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
 * The default implementation for the concrete OWL class DataAllValuesFrom
 *
 * @author xOWL code generator
 */
public class RuntimeDataAllValuesFromImpl implements org.xowl.infra.lang.runtime.DataAllValuesFrom {
    /**
     * The backing data for the property DataProperties
     */
    private List<org.xowl.infra.lang.runtime.DataProperty> __implDataProperties;

    /**
     * Adds a value to the property DataProperties
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        __implDataProperties.add(elem);
    }

    /**
     * Removes a value from the property DataProperties
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        __implDataProperties.remove(elem);
    }

    /**
     * Adds a value to the property DataProperties
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        doSimpleAddDataProperties(elem);
    }

    /**
     * Removes a value from the property DataProperties
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        doSimpleRemoveDataProperties(elem);
    }

    /**
     * Tries to add a value to the property DataProperties and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        doPropertyAddDataProperties(elem);
    }

    /**
     * Tries to remove a value from the property DataProperties and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        doPropertyRemoveDataProperties(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataProperties
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        doGraphAddDataProperties(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataProperties
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        doGraphRemoveDataProperties(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.DataProperty> getAllDataProperties() {
        return Collections.unmodifiableCollection(__implDataProperties);
    }

    @Override
    public boolean addDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataProperties.contains(elem))
            return false;
        doDispatchAddDataProperties(elem);
        return true;
    }

    @Override
    public boolean removeDataProperties(org.xowl.infra.lang.runtime.DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataProperties.contains(elem))
            return false;
        doDispatchRemoveDataProperties(elem);
        return true;
    }

    /**
     * The backing data for the property Datatype
     */
    private org.xowl.infra.lang.runtime.Datatype __implDatatype;

    /**
     * Adds a value to the property Datatype
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDatatype = elem;
    }

    /**
     * Removes a value from the property Datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDatatype = null;
    }

    /**
     * Adds a value to the property Datatype
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleAddDatatype(elem);
    }

    /**
     * Removes a value from the property Datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleRemoveDatatype(elem);
    }

    /**
     * Tries to add a value to the property Datatype and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyAddDatatype(elem);
    }

    /**
     * Tries to remove a value from the property Datatype and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyRemoveDatatype(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Datatype
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphAddDatatype(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Datatype
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphRemoveDatatype(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Datatype getDatatype() {
        return __implDatatype;
    }

    @Override
    public void setDatatype(org.xowl.infra.lang.runtime.Datatype elem) {
        if (__implDatatype == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDatatype(__implDatatype);
        } else if (__implDatatype == null) {
            doDispatchAddDatatype(elem);
        } else {
            doDispatchRemoveDatatype(__implDatatype);
            doDispatchAddDatatype(elem);
        }
    }

    /**
     * Constructor for the implementation of DataAllValuesFrom
     */
    public RuntimeDataAllValuesFromImpl() {
        this.__implDataProperties = new ArrayList<>();
        this.__implDatatype = null;
    }
}
