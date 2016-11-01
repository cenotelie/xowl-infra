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
 * The default implementation for the concrete OWL class DataPropertySequence
 *
 * @author xOWL code generator
 */
public class Owl2DataPropertySequenceImpl implements org.xowl.infra.lang.owl2.DataPropertySequence {
    /**
     * The backing data for the property DataPropertyElements
     */
    private List<org.xowl.infra.lang.owl2.DataPropertyElement> __implDataPropertyElements;

    /**
     * Adds a value to the property DataPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        __implDataPropertyElements.add(elem);
    }

    /**
     * Removes a value from the property DataPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        __implDataPropertyElements.remove(elem);
    }

    /**
     * Adds a value to the property DataPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        doSimpleAddDataPropertyElements(elem);
    }

    /**
     * Removes a value from the property DataPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        doSimpleRemoveDataPropertyElements(elem);
    }

    /**
     * Tries to add a value to the property DataPropertyElements and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        doPropertyAddDataPropertyElements(elem);
    }

    /**
     * Tries to remove a value from the property DataPropertyElements and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        doPropertyRemoveDataPropertyElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataPropertyElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        doGraphAddDataPropertyElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataPropertyElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        doGraphRemoveDataPropertyElements(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.owl2.DataPropertyElement> getAllDataPropertyElements() {
        return Collections.unmodifiableCollection(__implDataPropertyElements);
    }

    @Override
    public boolean addDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataPropertyElements.contains(elem))
            return false;
        doDispatchAddDataPropertyElements(elem);
        return true;
    }

    @Override
    public boolean removeDataPropertyElements(org.xowl.infra.lang.owl2.DataPropertyElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataPropertyElements.contains(elem))
            return false;
        doDispatchRemoveDataPropertyElements(elem);
        return true;
    }

    /**
     * Constructor for the implementation of DataPropertySequence
     */
    public Owl2DataPropertySequenceImpl() {
        this.__implDataPropertyElements = new ArrayList<>();
    }
}
