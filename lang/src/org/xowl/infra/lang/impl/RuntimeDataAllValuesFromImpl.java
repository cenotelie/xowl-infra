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
 * The default implementation for DataAllValuesFrom
 * Original OWL class is http://xowl.org/infra/lang/runtime#DataAllValuesFrom
 *
 * @author xOWL code generator
 */
public class RuntimeDataAllValuesFromImpl implements DataAllValuesFrom {
    /**
     * The backing data for the property DataProperties
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#dataProperties
     */
    private List<DataProperty> __implDataProperties;

    /**
     * Adds a value to the property DataProperties
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataProperties(DataProperty elem) {
        __implDataProperties.add(elem);
    }

    /**
     * Removes a value from the property DataProperties
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataProperties(DataProperty elem) {
        __implDataProperties.remove(elem);
    }

    /**
     * Adds a value to the property DataProperties
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataProperties(DataProperty elem) {
        doSimpleAddDataProperties(elem);
    }

    /**
     * Removes a value from the property DataProperties
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataProperties(DataProperty elem) {
        doSimpleRemoveDataProperties(elem);
    }

    /**
     * Tries to add a value to the property DataProperties and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataProperties(DataProperty elem) {
        doPropertyAddDataProperties(elem);
    }

    /**
     * Tries to remove a value from the property DataProperties and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataProperties(DataProperty elem) {
        doPropertyRemoveDataProperties(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataProperties
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataProperties(DataProperty elem) {
        doGraphAddDataProperties(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataProperties
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataProperties
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataProperties(DataProperty elem) {
        doGraphRemoveDataProperties(elem);
    }

    @Override
    public Collection<DataProperty> getAllDataProperties() {
        return Collections.unmodifiableCollection(__implDataProperties);
    }

    @Override
    public boolean addDataProperties(DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataProperties.contains(elem))
            return false;
        doDispatchAddDataProperties(elem);
        return true;
    }

    @Override
    public boolean removeDataProperties(DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataProperties.contains(elem))
            return false;
        doDispatchRemoveDataProperties(elem);
        return true;
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
     * Constructor for the implementation of DataAllValuesFrom
     */
    public RuntimeDataAllValuesFromImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#dataProperties
        this.__implDataProperties = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#datatype
        this.__implDatatype = null;
    }
}
