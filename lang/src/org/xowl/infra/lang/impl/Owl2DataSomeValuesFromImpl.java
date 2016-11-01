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
 * The default implementation for the concrete OWL class DataSomeValuesFrom
 *
 * @author xOWL code generator
 */
public class Owl2DataSomeValuesFromImpl implements org.xowl.infra.lang.owl2.DataSomeValuesFrom {
    /**
     * The backing data for the property DataPropertySeq
     */
    private org.xowl.infra.lang.owl2.DataPropertySequenceExpression __implDataPropertySeq;

    /**
     * Adds a value to the property DataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        __implDataPropertySeq = elem;
    }

    /**
     * Removes a value from the property DataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        __implDataPropertySeq = null;
    }

    /**
     * Adds a value to the property DataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doSimpleAddDataPropertySeq(elem);
    }

    /**
     * Removes a value from the property DataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doSimpleRemoveDataPropertySeq(elem);
    }

    /**
     * Tries to add a value to the property DataPropertySeq and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doPropertyAddDataPropertySeq(elem);
    }

    /**
     * Tries to remove a value from the property DataPropertySeq and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doPropertyRemoveDataPropertySeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataPropertySeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doGraphAddDataPropertySeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataPropertySeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doGraphRemoveDataPropertySeq(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.DataPropertySequenceExpression getDataPropertySeq() {
        return __implDataPropertySeq;
    }

    @Override
    public void setDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        if (__implDataPropertySeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDataPropertySeq(__implDataPropertySeq);
        } else if (__implDataPropertySeq == null) {
            doDispatchAddDataPropertySeq(elem);
        } else {
            doDispatchRemoveDataPropertySeq(__implDataPropertySeq);
            doDispatchAddDataPropertySeq(elem);
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
     * Constructor for the implementation of DataSomeValuesFrom
     */
    public Owl2DataSomeValuesFromImpl() {
        this.__implDataPropertySeq = null;
        this.__implDatarange = null;
    }
}
