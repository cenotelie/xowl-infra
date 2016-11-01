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
 * The default implementation for DataPropertySequence
 * Original OWL class is http://xowl.org/infra/lang/owl2#DataPropertySequence
 *
 * @author xOWL code generator
 */
public class Owl2DataPropertySequenceImpl implements DataPropertySequence {
    /**
     * The backing data for the property DataPropertyElements
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#dataPropertyElements
     */
    private List<DataPropertyElement> __implDataPropertyElements;

    /**
     * Adds a value to the property DataPropertyElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataPropertyElements(DataPropertyElement elem) {
        __implDataPropertyElements.add(elem);
    }

    /**
     * Removes a value from the property DataPropertyElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataPropertyElements(DataPropertyElement elem) {
        __implDataPropertyElements.remove(elem);
    }

    /**
     * Adds a value to the property DataPropertyElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataPropertyElements(DataPropertyElement elem) {
        doSimpleAddDataPropertyElements(elem);
    }

    /**
     * Removes a value from the property DataPropertyElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataPropertyElements(DataPropertyElement elem) {
        doSimpleRemoveDataPropertyElements(elem);
    }

    /**
     * Tries to add a value to the property DataPropertyElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataPropertyElements(DataPropertyElement elem) {
        doPropertyAddDataPropertyElements(elem);
    }

    /**
     * Tries to remove a value from the property DataPropertyElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataPropertyElements(DataPropertyElement elem) {
        doPropertyRemoveDataPropertyElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataPropertyElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataPropertyElements(DataPropertyElement elem) {
        doGraphAddDataPropertyElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataPropertyElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataPropertyElements(DataPropertyElement elem) {
        doGraphRemoveDataPropertyElements(elem);
    }

    @Override
    public Collection<DataPropertyElement> getAllDataPropertyElements() {
        return Collections.unmodifiableCollection(__implDataPropertyElements);
    }

    @Override
    public boolean addDataPropertyElements(DataPropertyElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataPropertyElements.contains(elem))
            return false;
        doDispatchAddDataPropertyElements(elem);
        return true;
    }

    @Override
    public boolean removeDataPropertyElements(DataPropertyElement elem) {
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
        // initialize property http://xowl.org/infra/lang/owl2#dataPropertyElements
        this.__implDataPropertyElements = new ArrayList<>();
    }
}
