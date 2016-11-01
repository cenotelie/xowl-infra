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
 * The default implementation for the concrete OWL class DataExactCardinality
 *
 * @author xOWL code generator
 */
public class RuntimeDataExactCardinalityImpl implements org.xowl.infra.lang.runtime.DataExactCardinality {
    /**
     * The backing data for the property DataProperty
     */
    private org.xowl.infra.lang.runtime.DataProperty __implDataProperty;

    /**
     * Adds a value to the property DataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        __implDataProperty = elem;
    }

    /**
     * Removes a value from the property DataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        __implDataProperty = null;
    }

    /**
     * Adds a value to the property DataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doSimpleAddDataProperty(elem);
    }

    /**
     * Removes a value from the property DataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doSimpleRemoveDataProperty(elem);
    }

    /**
     * Tries to add a value to the property DataProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doPropertyAddDataProperty(elem);
    }

    /**
     * Tries to remove a value from the property DataProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doPropertyRemoveDataProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doGraphAddDataProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        doGraphRemoveDataProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.DataProperty getDataProperty() {
        return __implDataProperty;
    }

    @Override
    public void setDataProperty(org.xowl.infra.lang.runtime.DataProperty elem) {
        if (__implDataProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDataProperty(__implDataProperty);
        } else if (__implDataProperty == null) {
            doDispatchAddDataProperty(elem);
        } else {
            doDispatchRemoveDataProperty(__implDataProperty);
            doDispatchAddDataProperty(elem);
        }
    }

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
     * Constructor for the implementation of DataExactCardinality
     */
    public RuntimeDataExactCardinalityImpl() {
        this.__implDataProperty = null;
        this.__implCardinality = 0;
        this.__implDatatype = null;
    }
}
