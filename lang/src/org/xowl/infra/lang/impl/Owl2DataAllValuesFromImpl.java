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

import org.xowl.infra.lang.owl2.*;

import java.util.*;

/**
 * The default implementation for DataAllValuesFrom
 * Original OWL class is http://xowl.org/infra/lang/owl2#DataAllValuesFrom
 *
 * @author xOWL code generator
 */
public class Owl2DataAllValuesFromImpl implements DataAllValuesFrom {
    /**
     * The backing data for the property DataPropertySeq
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#dataPropertySeq
     */
    private DataPropertySequenceExpression __implDataPropertySeq;

    /**
     * Adds a value to the property DataPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataPropertySeq(DataPropertySequenceExpression elem) {
        __implDataPropertySeq = elem;
    }

    /**
     * Removes a value from the property DataPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataPropertySeq(DataPropertySequenceExpression elem) {
        __implDataPropertySeq = null;
    }

    /**
     * Adds a value to the property DataPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataPropertySeq(DataPropertySequenceExpression elem) {
        doSimpleAddDataPropertySeq(elem);
    }

    /**
     * Removes a value from the property DataPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataPropertySeq(DataPropertySequenceExpression elem) {
        doSimpleRemoveDataPropertySeq(elem);
    }

    /**
     * Tries to add a value to the property DataPropertySeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataPropertySeq(DataPropertySequenceExpression elem) {
        doPropertyAddDataPropertySeq(elem);
    }

    /**
     * Tries to remove a value from the property DataPropertySeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataPropertySeq(DataPropertySequenceExpression elem) {
        doPropertyRemoveDataPropertySeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataPropertySeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataPropertySeq(DataPropertySequenceExpression elem) {
        doGraphAddDataPropertySeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataPropertySeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataPropertySeq(DataPropertySequenceExpression elem) {
        doGraphRemoveDataPropertySeq(elem);
    }

    @Override
    public DataPropertySequenceExpression getDataPropertySeq() {
        return __implDataPropertySeq;
    }

    @Override
    public void setDataPropertySeq(DataPropertySequenceExpression elem) {
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
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#datarange
     */
    private Datarange __implDatarange;

    /**
     * Adds a value to the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatarange(Datarange elem) {
        __implDatarange = elem;
    }

    /**
     * Removes a value from the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatarange(Datarange elem) {
        __implDatarange = null;
    }

    /**
     * Adds a value to the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatarange(Datarange elem) {
        doSimpleAddDatarange(elem);
    }

    /**
     * Removes a value from the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatarange(Datarange elem) {
        doSimpleRemoveDatarange(elem);
    }

    /**
     * Tries to add a value to the property Datarange and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatarange(Datarange elem) {
        doPropertyAddDatarange(elem);
    }

    /**
     * Tries to remove a value from the property Datarange and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatarange(Datarange elem) {
        doPropertyRemoveDatarange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Datarange
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatarange(Datarange elem) {
        doGraphAddDatarange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Datarange
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatarange(Datarange elem) {
        doGraphRemoveDatarange(elem);
    }

    @Override
    public Datarange getDatarange() {
        return __implDatarange;
    }

    @Override
    public void setDatarange(Datarange elem) {
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
     * Constructor for the implementation of DataAllValuesFrom
     */
    public Owl2DataAllValuesFromImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#dataPropertySeq
        this.__implDataPropertySeq = null;
        // initialize property http://xowl.org/infra/lang/owl2#datarange
        this.__implDatarange = null;
    }
}
