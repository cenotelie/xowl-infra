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
 * The default implementation for ObjectOneOf
 * Original OWL class is http://xowl.org/infra/lang/owl2#ObjectOneOf
 *
 * @author xOWL code generator
 */
public class Owl2ObjectOneOfImpl implements ObjectOneOf {
    /**
     * The backing data for the property IndividualSeq
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#individualSeq
     */
    private IndividualSequenceExpression __implIndividualSeq;

    /**
     * Adds a value to the property IndividualSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualSeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividualSeq(IndividualSequenceExpression elem) {
        __implIndividualSeq = elem;
    }

    /**
     * Removes a value from the property IndividualSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividualSeq(IndividualSequenceExpression elem) {
        __implIndividualSeq = null;
    }

    /**
     * Adds a value to the property IndividualSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividualSeq(IndividualSequenceExpression elem) {
        doSimpleAddIndividualSeq(elem);
    }

    /**
     * Removes a value from the property IndividualSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividualSeq(IndividualSequenceExpression elem) {
        doSimpleRemoveIndividualSeq(elem);
    }

    /**
     * Tries to add a value to the property IndividualSeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividualSeq(IndividualSequenceExpression elem) {
        doPropertyAddIndividualSeq(elem);
    }

    /**
     * Tries to remove a value from the property IndividualSeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividualSeq(IndividualSequenceExpression elem) {
        doPropertyRemoveIndividualSeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property IndividualSeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividualSeq(IndividualSequenceExpression elem) {
        doGraphAddIndividualSeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property IndividualSeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividualSeq(IndividualSequenceExpression elem) {
        doGraphRemoveIndividualSeq(elem);
    }

    @Override
    public IndividualSequenceExpression getIndividualSeq() {
        return __implIndividualSeq;
    }

    @Override
    public void setIndividualSeq(IndividualSequenceExpression elem) {
        if (__implIndividualSeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveIndividualSeq(__implIndividualSeq);
        } else if (__implIndividualSeq == null) {
            doDispatchAddIndividualSeq(elem);
        } else {
            doDispatchRemoveIndividualSeq(__implIndividualSeq);
            doDispatchAddIndividualSeq(elem);
        }
    }

    /**
     * Constructor for the implementation of ObjectOneOf
     */
    public Owl2ObjectOneOfImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#individualSeq
        this.__implIndividualSeq = null;
    }
}
