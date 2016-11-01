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
 * The default implementation for the concrete OWL class DataUnionOf
 *
 * @author xOWL code generator
 */
public class Owl2DataUnionOfImpl implements org.xowl.infra.lang.owl2.DataUnionOf {
    /**
     * The backing data for the property DatarangeSeq
     */
    private org.xowl.infra.lang.owl2.DatarangeSequenceExpression __implDatarangeSeq;

    /**
     * Adds a value to the property DatarangeSeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        __implDatarangeSeq = elem;
    }

    /**
     * Removes a value from the property DatarangeSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        __implDatarangeSeq = null;
    }

    /**
     * Adds a value to the property DatarangeSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        doSimpleAddDatarangeSeq(elem);
    }

    /**
     * Removes a value from the property DatarangeSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        doSimpleRemoveDatarangeSeq(elem);
    }

    /**
     * Tries to add a value to the property DatarangeSeq and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        doPropertyAddDatarangeSeq(elem);
    }

    /**
     * Tries to remove a value from the property DatarangeSeq and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        doPropertyRemoveDatarangeSeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DatarangeSeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        doGraphAddDatarangeSeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DatarangeSeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        doGraphRemoveDatarangeSeq(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.DatarangeSequenceExpression getDatarangeSeq() {
        return __implDatarangeSeq;
    }

    @Override
    public void setDatarangeSeq(org.xowl.infra.lang.owl2.DatarangeSequenceExpression elem) {
        if (__implDatarangeSeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDatarangeSeq(__implDatarangeSeq);
        } else if (__implDatarangeSeq == null) {
            doDispatchAddDatarangeSeq(elem);
        } else {
            doDispatchRemoveDatarangeSeq(__implDatarangeSeq);
            doDispatchAddDatarangeSeq(elem);
        }
    }

    /**
     * Constructor for the implementation of DataUnionOf
     */
    public Owl2DataUnionOfImpl() {
        this.__implDatarangeSeq = null;
    }
}
