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
 * The default implementation for the concrete OWL class DataPropertyElement
 *
 * @author xOWL code generator
 */
public class Owl2DataPropertyElementImpl implements org.xowl.infra.lang.owl2.DataPropertyElement {
    /**
     * The backing data for the property DataProperty
     */
    private org.xowl.infra.lang.owl2.DataPropertyExpression __implDataProperty;

    /**
     * Adds a value to the property DataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        __implDataProperty = elem;
    }

    /**
     * Removes a value from the property DataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        __implDataProperty = null;
    }

    /**
     * Adds a value to the property DataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doSimpleAddDataProperty(elem);
    }

    /**
     * Removes a value from the property DataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doSimpleRemoveDataProperty(elem);
    }

    /**
     * Tries to add a value to the property DataProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doPropertyAddDataProperty(elem);
    }

    /**
     * Tries to remove a value from the property DataProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doPropertyRemoveDataProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doGraphAddDataProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doGraphRemoveDataProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.DataPropertyExpression getDataProperty() {
        return __implDataProperty;
    }

    @Override
    public void setDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
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
     * The backing data for the property Index
     */
    private int __implIndex;

    @Override
    public int getIndex() {
        return __implIndex;
    }

    @Override
    public void setIndex(int elem) {
        __implIndex = elem;
    }

    /**
     * Constructor for the implementation of DataPropertyElement
     */
    public Owl2DataPropertyElementImpl() {
        this.__implDataProperty = null;
        this.__implIndex = 0;
    }
}