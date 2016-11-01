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
 * The default implementation for the concrete OWL class ClassSequence
 *
 * @author xOWL code generator
 */
public class Owl2ClassSequenceImpl implements org.xowl.infra.lang.owl2.ClassSequence {
    /**
     * The backing data for the property ClassElements
     */
    private List<org.xowl.infra.lang.owl2.ClassElement> __implClassElements;

    /**
     * Adds a value to the property ClassElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        __implClassElements.add(elem);
    }

    /**
     * Removes a value from the property ClassElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        __implClassElements.remove(elem);
    }

    /**
     * Adds a value to the property ClassElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        doSimpleAddClassElements(elem);
    }

    /**
     * Removes a value from the property ClassElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        doSimpleRemoveClassElements(elem);
    }

    /**
     * Tries to add a value to the property ClassElements and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        doPropertyAddClassElements(elem);
    }

    /**
     * Tries to remove a value from the property ClassElements and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        doPropertyRemoveClassElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        doGraphAddClassElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        doGraphRemoveClassElements(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.owl2.ClassElement> getAllClassElements() {
        return Collections.unmodifiableCollection(__implClassElements);
    }

    @Override
    public boolean addClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassElements.contains(elem))
            return false;
        doDispatchAddClassElements(elem);
        return true;
    }

    @Override
    public boolean removeClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassElements.contains(elem))
            return false;
        doDispatchRemoveClassElements(elem);
        return true;
    }

    /**
     * Constructor for the implementation of ClassSequence
     */
    public Owl2ClassSequenceImpl() {
        this.__implClassElements = new ArrayList<>();
    }
}
