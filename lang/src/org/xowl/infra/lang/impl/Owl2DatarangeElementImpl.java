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
 * The default implementation for the concrete OWL class DatarangeElement
 *
 * @author xOWL code generator
 */
public class Owl2DatarangeElementImpl implements org.xowl.infra.lang.owl2.DatarangeElement {
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
     * The backing data for the property Index
     */
    private int __implIndex;

    @Override
    public int getIndex() {
        return __implIndex;
    }

    @Override
    public void setIndex(int elem) {
        __implIndex = elem;
    }

    /**
     * Constructor for the implementation of DatarangeElement
     */
    public Owl2DatarangeElementImpl() {
        this.__implDatarange = null;
        this.__implIndex = 0;
    }
}
