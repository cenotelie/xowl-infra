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
 * The default implementation for the concrete OWL class ObjectOneOf
 *
 * @author xOWL code generator
 */
public class Owl2ObjectOneOfImpl implements org.xowl.infra.lang.owl2.ObjectOneOf {
    /**
     * The backing data for the property IndividualSeq
     */
    private org.xowl.infra.lang.owl2.IndividualSequenceExpression __implIndividualSeq;

    /**
     * Adds a value to the property IndividualSeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        __implIndividualSeq = elem;
    }

    /**
     * Removes a value from the property IndividualSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        __implIndividualSeq = null;
    }

    /**
     * Adds a value to the property IndividualSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doSimpleAddIndividualSeq(elem);
    }

    /**
     * Removes a value from the property IndividualSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doSimpleRemoveIndividualSeq(elem);
    }

    /**
     * Tries to add a value to the property IndividualSeq and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doPropertyAddIndividualSeq(elem);
    }

    /**
     * Tries to remove a value from the property IndividualSeq and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doPropertyRemoveIndividualSeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property IndividualSeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doGraphAddIndividualSeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property IndividualSeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doGraphRemoveIndividualSeq(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IndividualSequenceExpression getIndividualSeq() {
        return __implIndividualSeq;
    }

    @Override
    public void setIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
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
        this.__implIndividualSeq = null;
    }
}
