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
 * The default implementation for ClassSequence
 * Original OWL class is http://xowl.org/infra/lang/owl2#ClassSequence
 *
 * @author xOWL code generator
 */
public class Owl2ClassSequenceImpl implements ClassSequence {
    /**
     * The backing data for the property ClassElements
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#classElements
     */
    private List<ClassElement> __implClassElements;

    /**
     * Adds a value to the property ClassElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#classElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassElements(ClassElement elem) {
        __implClassElements.add(elem);
    }

    /**
     * Removes a value from the property ClassElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#classElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassElements(ClassElement elem) {
        __implClassElements.remove(elem);
    }

    /**
     * Adds a value to the property ClassElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#classElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassElements(ClassElement elem) {
        doSimpleAddClassElements(elem);
    }

    /**
     * Removes a value from the property ClassElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#classElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassElements(ClassElement elem) {
        doSimpleRemoveClassElements(elem);
    }

    /**
     * Tries to add a value to the property ClassElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#classElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassElements(ClassElement elem) {
        doPropertyAddClassElements(elem);
    }

    /**
     * Tries to remove a value from the property ClassElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#classElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassElements(ClassElement elem) {
        doPropertyRemoveClassElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#classElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassElements(ClassElement elem) {
        doGraphAddClassElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#classElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassElements(ClassElement elem) {
        doGraphRemoveClassElements(elem);
    }

    @Override
    public Collection<ClassElement> getAllClassElements() {
        return Collections.unmodifiableCollection(__implClassElements);
    }

    @Override
    public boolean addClassElements(ClassElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassElements.contains(elem))
            return false;
        doDispatchAddClassElements(elem);
        return true;
    }

    @Override
    public boolean removeClassElements(ClassElement elem) {
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
        // initialize property http://xowl.org/infra/lang/owl2#classElements
        this.__implClassElements = new ArrayList<>();
    }
}
