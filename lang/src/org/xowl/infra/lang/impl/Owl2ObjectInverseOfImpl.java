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
 * The default implementation for ObjectInverseOf
 * Original OWL class is http://xowl.org/infra/lang/owl2#ObjectInverseOf
 *
 * @author xOWL code generator
 */
public class Owl2ObjectInverseOfImpl implements ObjectInverseOf {
    /**
     * The backing data for the property Inverse
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#inverse
     */
    private ObjectPropertyExpression __implInverse;

    /**
     * Adds a value to the property Inverse
     * Original OWL property is http://xowl.org/infra/lang/owl2#inverse
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInverse(ObjectPropertyExpression elem) {
        __implInverse = elem;
    }

    /**
     * Removes a value from the property Inverse
     * Original OWL property is http://xowl.org/infra/lang/owl2#inverse
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInverse(ObjectPropertyExpression elem) {
        __implInverse = null;
    }

    /**
     * Adds a value to the property Inverse
     * Original OWL property is http://xowl.org/infra/lang/owl2#inverse
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInverse(ObjectPropertyExpression elem) {
        doSimpleAddInverse(elem);
    }

    /**
     * Removes a value from the property Inverse
     * Original OWL property is http://xowl.org/infra/lang/owl2#inverse
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInverse(ObjectPropertyExpression elem) {
        doSimpleRemoveInverse(elem);
    }

    /**
     * Tries to add a value to the property Inverse and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#inverse
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInverse(ObjectPropertyExpression elem) {
        doPropertyAddInverse(elem);
    }

    /**
     * Tries to remove a value from the property Inverse and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#inverse
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInverse(ObjectPropertyExpression elem) {
        doPropertyRemoveInverse(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Inverse
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#inverse
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInverse(ObjectPropertyExpression elem) {
        doGraphAddInverse(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Inverse
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#inverse
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInverse(ObjectPropertyExpression elem) {
        doGraphRemoveInverse(elem);
    }

    @Override
    public ObjectPropertyExpression getInverse() {
        return __implInverse;
    }

    @Override
    public void setInverse(ObjectPropertyExpression elem) {
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
        // initialize property http://xowl.org/infra/lang/owl2#inverse
        this.__implInverse = null;
    }
}
