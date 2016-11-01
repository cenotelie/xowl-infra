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
 * The default implementation for ObjectPropertySequence
 * Original OWL class is http://xowl.org/infra/lang/owl2#ObjectPropertySequence
 *
 * @author xOWL code generator
 */
public class Owl2ObjectPropertySequenceImpl implements ObjectPropertySequence {
    /**
     * The backing data for the property ObjectPropertyElements
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#objectPropertyElements
     */
    private List<ObjectPropertyElement> __implObjectPropertyElements;

    /**
     * Adds a value to the property ObjectPropertyElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectPropertyElements(ObjectPropertyElement elem) {
        __implObjectPropertyElements.add(elem);
    }

    /**
     * Removes a value from the property ObjectPropertyElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectPropertyElements(ObjectPropertyElement elem) {
        __implObjectPropertyElements.remove(elem);
    }

    /**
     * Adds a value to the property ObjectPropertyElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectPropertyElements(ObjectPropertyElement elem) {
        doSimpleAddObjectPropertyElements(elem);
    }

    /**
     * Removes a value from the property ObjectPropertyElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectPropertyElements(ObjectPropertyElement elem) {
        doSimpleRemoveObjectPropertyElements(elem);
    }

    /**
     * Tries to add a value to the property ObjectPropertyElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectPropertyElements(ObjectPropertyElement elem) {
        doPropertyAddObjectPropertyElements(elem);
    }

    /**
     * Tries to remove a value from the property ObjectPropertyElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectPropertyElements(ObjectPropertyElement elem) {
        doPropertyRemoveObjectPropertyElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectPropertyElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectPropertyElements(ObjectPropertyElement elem) {
        doGraphAddObjectPropertyElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectPropertyElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectPropertyElements(ObjectPropertyElement elem) {
        doGraphRemoveObjectPropertyElements(elem);
    }

    @Override
    public Collection<ObjectPropertyElement> getAllObjectPropertyElements() {
        return Collections.unmodifiableCollection(__implObjectPropertyElements);
    }

    @Override
    public boolean addObjectPropertyElements(ObjectPropertyElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implObjectPropertyElements.contains(elem))
            return false;
        doDispatchAddObjectPropertyElements(elem);
        return true;
    }

    @Override
    public boolean removeObjectPropertyElements(ObjectPropertyElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implObjectPropertyElements.contains(elem))
            return false;
        doDispatchRemoveObjectPropertyElements(elem);
        return true;
    }

    /**
     * Constructor for the implementation of ObjectPropertySequence
     */
    public Owl2ObjectPropertySequenceImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#objectPropertyElements
        this.__implObjectPropertyElements = new ArrayList<>();
    }
}
