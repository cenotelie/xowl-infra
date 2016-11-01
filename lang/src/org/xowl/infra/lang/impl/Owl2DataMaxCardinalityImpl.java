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
 * The default implementation for the concrete OWL class DataMaxCardinality
 *
 * @author xOWL code generator
 */
public class Owl2DataMaxCardinalityImpl implements org.xowl.infra.lang.owl2.DataMaxCardinality {
    /**
     * The backing data for the property Cardinality
     */
    private org.xowl.infra.lang.owl2.LiteralExpression __implCardinality;

    /**
     * Adds a value to the property Cardinality
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        __implCardinality = elem;
    }

    /**
     * Removes a value from the property Cardinality
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        __implCardinality = null;
    }

    /**
     * Adds a value to the property Cardinality
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doSimpleAddCardinality(elem);
    }

    /**
     * Removes a value from the property Cardinality
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doSimpleRemoveCardinality(elem);
    }

    /**
     * Tries to add a value to the property Cardinality and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doPropertyAddCardinality(elem);
    }

    /**
     * Tries to remove a value from the property Cardinality and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doPropertyRemoveCardinality(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Cardinality
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doGraphAddCardinality(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Cardinality
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doGraphRemoveCardinality(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.LiteralExpression getCardinality() {
        return __implCardinality;
    }

    @Override
    public void setCardinality(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        if (__implCardinality == elem)
            return;
        if (elem == null) {
            doDispatchRemoveCardinality(__implCardinality);
        } else if (__implCardinality == null) {
            doDispatchAddCardinality(elem);
        } else {
            doDispatchRemoveCardinality(__implCardinality);
            doDispatchAddCardinality(elem);
        }
    }

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
     * The backing data for the property Datarange
     */
    private org.xowl.infra.lang.owl2.Datarange __implDatarange;

    /**
     * Adds a value to the property Datarange
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        __implDatarange = elem;
    }

    /**
     * Removes a value from the property Datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        __implDatarange = null;
    }

    /**
     * Adds a value to the property Datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        doSimpleAddDatarange(elem);
    }

    /**
     * Removes a value from the property Datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        doSimpleRemoveDatarange(elem);
    }

    /**
     * Tries to add a value to the property Datarange and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        doPropertyAddDatarange(elem);
    }

    /**
     * Tries to remove a value from the property Datarange and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        doPropertyRemoveDatarange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Datarange
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        doGraphAddDatarange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Datarange
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        doGraphRemoveDatarange(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.Datarange getDatarange() {
        return __implDatarange;
    }

    @Override
    public void setDatarange(org.xowl.infra.lang.owl2.Datarange elem) {
        if (__implDatarange == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDatarange(__implDatarange);
        } else if (__implDatarange == null) {
            doDispatchAddDatarange(elem);
        } else {
            doDispatchRemoveDatarange(__implDatarange);
            doDispatchAddDatarange(elem);
        }
    }

    /**
     * Constructor for the implementation of DataMaxCardinality
     */
    public Owl2DataMaxCardinalityImpl() {
        this.__implCardinality = null;
        this.__implDataProperty = null;
        this.__implDatarange = null;
    }
}
