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
 * The default implementation for the concrete OWL class ObjectInverseOf
 *
 * @author xOWL code generator
 */
public class Owl2ObjectInverseOfImpl implements org.xowl.infra.lang.owl2.ObjectInverseOf {
    /**
     * The backing data for the property Inverse
     */
    private org.xowl.infra.lang.owl2.ObjectPropertyExpression __implInverse;

    /**
     * Adds a value to the property Inverse
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        __implInverse = elem;
    }

    /**
     * Removes a value from the property Inverse
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        __implInverse = null;
    }

    /**
     * Adds a value to the property Inverse
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doSimpleAddInverse(elem);
    }

    /**
     * Removes a value from the property Inverse
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doSimpleRemoveInverse(elem);
    }

    /**
     * Tries to add a value to the property Inverse and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doPropertyAddInverse(elem);
    }

    /**
     * Tries to remove a value from the property Inverse and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doPropertyRemoveInverse(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Inverse
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doGraphAddInverse(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Inverse
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doGraphRemoveInverse(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.ObjectPropertyExpression getInverse() {
        return __implInverse;
    }

    @Override
    public void setInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        if (__implInverse == elem)
            return;
        if (elem == null) {
            doDispatchRemoveInverse(__implInverse);
        } else if (__implInverse == null) {
            doDispatchAddInverse(elem);
        } else {
            doDispatchRemoveInverse(__implInverse);
            doDispatchAddInverse(elem);
        }
    }

    /**
     * Constructor for the implementation of ObjectInverseOf
     */
    public Owl2ObjectInverseOfImpl() {
        this.__implInverse = null;
    }
}
