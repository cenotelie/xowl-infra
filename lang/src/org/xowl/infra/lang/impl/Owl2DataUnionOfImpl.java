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
 * The default implementation for DataUnionOf
 * Original OWL class is http://xowl.org/infra/lang/owl2#DataUnionOf
 *
 * @author xOWL code generator
 */
public class Owl2DataUnionOfImpl implements DataUnionOf {
    /**
     * The backing data for the property DatarangeSeq
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#datarangeSeq
     */
    private DatarangeSequenceExpression __implDatarangeSeq;

    /**
     * Adds a value to the property DatarangeSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeSeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatarangeSeq(DatarangeSequenceExpression elem) {
        __implDatarangeSeq = elem;
    }

    /**
     * Removes a value from the property DatarangeSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatarangeSeq(DatarangeSequenceExpression elem) {
        __implDatarangeSeq = null;
    }

    /**
     * Adds a value to the property DatarangeSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatarangeSeq(DatarangeSequenceExpression elem) {
        doSimpleAddDatarangeSeq(elem);
    }

    /**
     * Removes a value from the property DatarangeSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatarangeSeq(DatarangeSequenceExpression elem) {
        doSimpleRemoveDatarangeSeq(elem);
    }

    /**
     * Tries to add a value to the property DatarangeSeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatarangeSeq(DatarangeSequenceExpression elem) {
        doPropertyAddDatarangeSeq(elem);
    }

    /**
     * Tries to remove a value from the property DatarangeSeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatarangeSeq(DatarangeSequenceExpression elem) {
        doPropertyRemoveDatarangeSeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DatarangeSeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatarangeSeq(DatarangeSequenceExpression elem) {
        doGraphAddDatarangeSeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DatarangeSeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatarangeSeq(DatarangeSequenceExpression elem) {
        doGraphRemoveDatarangeSeq(elem);
    }

    @Override
    public DatarangeSequenceExpression getDatarangeSeq() {
        return __implDatarangeSeq;
    }

    @Override
    public void setDatarangeSeq(DatarangeSequenceExpression elem) {
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
        // initialize property http://xowl.org/infra/lang/owl2#datarangeSeq
        this.__implDatarangeSeq = null;
    }
}
