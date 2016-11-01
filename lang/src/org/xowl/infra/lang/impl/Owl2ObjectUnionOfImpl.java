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
 * The default implementation for ObjectUnionOf
 * Original OWL class is http://xowl.org/infra/lang/owl2#ObjectUnionOf
 *
 * @author xOWL code generator
 */
public class Owl2ObjectUnionOfImpl implements ObjectUnionOf {
    /**
     * The backing data for the property ClassSeq
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#classSeq
     */
    private ClassSequenceExpression __implClassSeq;

    /**
     * Adds a value to the property ClassSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassSeq(ClassSequenceExpression elem) {
        __implClassSeq = elem;
    }

    /**
     * Removes a value from the property ClassSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassSeq(ClassSequenceExpression elem) {
        __implClassSeq = null;
    }

    /**
     * Adds a value to the property ClassSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassSeq(ClassSequenceExpression elem) {
        doSimpleAddClassSeq(elem);
    }

    /**
     * Removes a value from the property ClassSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassSeq(ClassSequenceExpression elem) {
        doSimpleRemoveClassSeq(elem);
    }

    /**
     * Tries to add a value to the property ClassSeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassSeq(ClassSequenceExpression elem) {
        doPropertyAddClassSeq(elem);
    }

    /**
     * Tries to remove a value from the property ClassSeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassSeq(ClassSequenceExpression elem) {
        doPropertyRemoveClassSeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassSeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassSeq(ClassSequenceExpression elem) {
        doGraphAddClassSeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassSeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassSeq(ClassSequenceExpression elem) {
        doGraphRemoveClassSeq(elem);
    }

    @Override
    public ClassSequenceExpression getClassSeq() {
        return __implClassSeq;
    }

    @Override
    public void setClassSeq(ClassSequenceExpression elem) {
        if (__implClassSeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveClassSeq(__implClassSeq);
        } else if (__implClassSeq == null) {
            doDispatchAddClassSeq(elem);
        } else {
            doDispatchRemoveClassSeq(__implClassSeq);
            doDispatchAddClassSeq(elem);
        }
    }

    /**
     * Constructor for the implementation of ObjectUnionOf
     */
    public Owl2ObjectUnionOfImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#classSeq
        this.__implClassSeq = null;
    }
}
