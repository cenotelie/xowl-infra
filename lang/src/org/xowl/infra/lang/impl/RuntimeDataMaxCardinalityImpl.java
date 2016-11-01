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
 * The default implementation for DataMaxCardinality
 * Original OWL class is http://xowl.org/infra/lang/runtime#DataMaxCardinality
 *
 * @author xOWL code generator
 */
public class RuntimeDataMaxCardinalityImpl implements DataMaxCardinality {
    /**
     * The backing data for the property Cardinality
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#cardinality
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
     * The backing data for the property DataProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#dataProperty
     */
    private DataProperty __implDataProperty;

    /**
     * Adds a value to the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataProperty(DataProperty elem) {
        __implDataProperty = elem;
    }

    /**
     * Removes a value from the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataProperty(DataProperty elem) {
        __implDataProperty = null;
    }

    /**
     * Adds a value to the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataProperty(DataProperty elem) {
        doSimpleAddDataProperty(elem);
    }

    /**
     * Removes a value from the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataProperty(DataProperty elem) {
        doSimpleRemoveDataProperty(elem);
    }

    /**
     * Tries to add a value to the property DataProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataProperty(DataProperty elem) {
        doPropertyAddDataProperty(elem);
    }

    /**
     * Tries to remove a value from the property DataProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataProperty(DataProperty elem) {
        doPropertyRemoveDataProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataProperty(DataProperty elem) {
        doGraphAddDataProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataProperty(DataProperty elem) {
        doGraphRemoveDataProperty(elem);
    }

    @Override
    public DataProperty getDataProperty() {
        return __implDataProperty;
    }

    @Override
    public void setDataProperty(DataProperty elem) {
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
     * The backing data for the property Datatype
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#datatype
     */
    private Datatype __implDatatype;

    /**
     * Adds a value to the property Datatype
     * Original OWL property is http://xowl.org/infra/lang/runtime#datatype
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatatype(Datatype elem) {
        __implDatatype = elem;
    }

    /**
     * Removes a value from the property Datatype
     * Original OWL property is http://xowl.org/infra/lang/runtime#datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatatype(Datatype elem) {
        __implDatatype = null;
    }

    /**
     * Adds a value to the property Datatype
     * Original OWL property is http://xowl.org/infra/lang/runtime#datatype
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatatype(Datatype elem) {
        doSimpleAddDatatype(elem);
    }

    /**
     * Removes a value from the property Datatype
     * Original OWL property is http://xowl.org/infra/lang/runtime#datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatatype(Datatype elem) {
        doSimpleRemoveDatatype(elem);
    }

    /**
     * Tries to add a value to the property Datatype and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#datatype
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatatype(Datatype elem) {
        doPropertyAddDatatype(elem);
    }

    /**
     * Tries to remove a value from the property Datatype and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatatype(Datatype elem) {
        doPropertyRemoveDatatype(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Datatype
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#datatype
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatatype(Datatype elem) {
        doGraphAddDatatype(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Datatype
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatatype(Datatype elem) {
        doGraphRemoveDatatype(elem);
    }

    @Override
    public Datatype getDatatype() {
        return __implDatatype;
    }

    @Override
    public void setDatatype(Datatype elem) {
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
     * Constructor for the implementation of DataMaxCardinality
     */
    public RuntimeDataMaxCardinalityImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#cardinality
        this.__implCardinality = 0;
        // initialize property http://xowl.org/infra/lang/runtime#dataProperty
        this.__implDataProperty = null;
        // initialize property http://xowl.org/infra/lang/runtime#datatype
        this.__implDatatype = null;
    }
}
