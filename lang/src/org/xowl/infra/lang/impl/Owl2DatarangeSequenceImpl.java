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
 * The default implementation for DatarangeSequence
 * Original OWL class is http://xowl.org/infra/lang/owl2#DatarangeSequence
 *
 * @author xOWL code generator
 */
public class Owl2DatarangeSequenceImpl implements DatarangeSequence {
    /**
     * The backing data for the property DatarangeElements
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#datarangeElements
     */
    private List<DatarangeElement> __implDatarangeElements;

    /**
     * Adds a value to the property DatarangeElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatarangeElements(DatarangeElement elem) {
        __implDatarangeElements.add(elem);
    }

    /**
     * Removes a value from the property DatarangeElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatarangeElements(DatarangeElement elem) {
        __implDatarangeElements.remove(elem);
    }

    /**
     * Adds a value to the property DatarangeElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatarangeElements(DatarangeElement elem) {
        doSimpleAddDatarangeElements(elem);
    }

    /**
     * Removes a value from the property DatarangeElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatarangeElements(DatarangeElement elem) {
        doSimpleRemoveDatarangeElements(elem);
    }

    /**
     * Tries to add a value to the property DatarangeElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatarangeElements(DatarangeElement elem) {
        doPropertyAddDatarangeElements(elem);
    }

    /**
     * Tries to remove a value from the property DatarangeElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatarangeElements(DatarangeElement elem) {
        doPropertyRemoveDatarangeElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DatarangeElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatarangeElements(DatarangeElement elem) {
        doGraphAddDatarangeElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DatarangeElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarangeElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatarangeElements(DatarangeElement elem) {
        doGraphRemoveDatarangeElements(elem);
    }

    @Override
    public Collection<DatarangeElement> getAllDatarangeElements() {
        return Collections.unmodifiableCollection(__implDatarangeElements);
    }

    @Override
    public boolean addDatarangeElements(DatarangeElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDatarangeElements.contains(elem))
            return false;
        doDispatchAddDatarangeElements(elem);
        return true;
    }

    @Override
    public boolean removeDatarangeElements(DatarangeElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDatarangeElements.contains(elem))
            return false;
        doDispatchRemoveDatarangeElements(elem);
        return true;
    }

    /**
     * Constructor for the implementation of DatarangeSequence
     */
    public Owl2DatarangeSequenceImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#datarangeElements
        this.__implDatarangeElements = new ArrayList<>();
    }
}
