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
 * The default implementation for the concrete OWL class ObjectPropertySequence
 *
 * @author xOWL code generator
 */
public class Owl2ObjectPropertySequenceImpl implements org.xowl.infra.lang.owl2.ObjectPropertySequence {
    /**
     * The backing data for the property ObjectPropertyElements
     */
    private List<org.xowl.infra.lang.owl2.ObjectPropertyElement> __implObjectPropertyElements;

    /**
     * Adds a value to the property ObjectPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        __implObjectPropertyElements.add(elem);
    }

    /**
     * Removes a value from the property ObjectPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        __implObjectPropertyElements.remove(elem);
    }

    /**
     * Adds a value to the property ObjectPropertyElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        doSimpleAddObjectPropertyElements(elem);
    }

    /**
     * Removes a value from the property ObjectPropertyElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        doSimpleRemoveObjectPropertyElements(elem);
    }

    /**
     * Tries to add a value to the property ObjectPropertyElements and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        doPropertyAddObjectPropertyElements(elem);
    }

    /**
     * Tries to remove a value from the property ObjectPropertyElements and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        doPropertyRemoveObjectPropertyElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectPropertyElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        doGraphAddObjectPropertyElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectPropertyElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        doGraphRemoveObjectPropertyElements(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.owl2.ObjectPropertyElement> getAllObjectPropertyElements() {
        return Collections.unmodifiableCollection(__implObjectPropertyElements);
    }

    @Override
    public boolean addObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implObjectPropertyElements.contains(elem))
            return false;
        doDispatchAddObjectPropertyElements(elem);
        return true;
    }

    @Override
    public boolean removeObjectPropertyElements(org.xowl.infra.lang.owl2.ObjectPropertyElement elem) {
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
        this.__implObjectPropertyElements = new ArrayList<>();
    }
}
