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
 * The default implementation for the concrete OWL class DataOneOf
 *
 * @author xOWL code generator
 */
public class Owl2DataOneOfImpl implements org.xowl.infra.lang.owl2.DataOneOf {
    /**
     * The backing data for the property LiteralSeq
     */
    private org.xowl.infra.lang.owl2.LiteralSequenceExpression __implLiteralSeq;

    /**
     * Adds a value to the property LiteralSeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        __implLiteralSeq = elem;
    }

    /**
     * Removes a value from the property LiteralSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        __implLiteralSeq = null;
    }

    /**
     * Adds a value to the property LiteralSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        doSimpleAddLiteralSeq(elem);
    }

    /**
     * Removes a value from the property LiteralSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        doSimpleRemoveLiteralSeq(elem);
    }

    /**
     * Tries to add a value to the property LiteralSeq and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        doPropertyAddLiteralSeq(elem);
    }

    /**
     * Tries to remove a value from the property LiteralSeq and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        doPropertyRemoveLiteralSeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property LiteralSeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        doGraphAddLiteralSeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property LiteralSeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        doGraphRemoveLiteralSeq(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.LiteralSequenceExpression getLiteralSeq() {
        return __implLiteralSeq;
    }

    @Override
    public void setLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        if (__implLiteralSeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveLiteralSeq(__implLiteralSeq);
        } else if (__implLiteralSeq == null) {
            doDispatchAddLiteralSeq(elem);
        } else {
            doDispatchRemoveLiteralSeq(__implLiteralSeq);
            doDispatchAddLiteralSeq(elem);
        }
    }

    /**
     * Constructor for the implementation of DataOneOf
     */
    public Owl2DataOneOfImpl() {
        this.__implLiteralSeq = null;
    }
}
