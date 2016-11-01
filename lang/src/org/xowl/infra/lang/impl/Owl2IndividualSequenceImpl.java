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
 * The default implementation for IndividualSequence
 * Original OWL class is http://xowl.org/infra/lang/owl2#IndividualSequence
 *
 * @author xOWL code generator
 */
public class Owl2IndividualSequenceImpl implements IndividualSequence {
    /**
     * The backing data for the property IndividualElements
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#individualElements
     */
    private List<IndividualElement> __implIndividualElements;

    /**
     * Adds a value to the property IndividualElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividualElements(IndividualElement elem) {
        __implIndividualElements.add(elem);
    }

    /**
     * Removes a value from the property IndividualElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividualElements(IndividualElement elem) {
        __implIndividualElements.remove(elem);
    }

    /**
     * Adds a value to the property IndividualElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividualElements(IndividualElement elem) {
        doSimpleAddIndividualElements(elem);
    }

    /**
     * Removes a value from the property IndividualElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividualElements(IndividualElement elem) {
        doSimpleRemoveIndividualElements(elem);
    }

    /**
     * Tries to add a value to the property IndividualElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividualElements(IndividualElement elem) {
        doPropertyAddIndividualElements(elem);
    }

    /**
     * Tries to remove a value from the property IndividualElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividualElements(IndividualElement elem) {
        doPropertyRemoveIndividualElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property IndividualElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividualElements(IndividualElement elem) {
        doGraphAddIndividualElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property IndividualElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#individualElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividualElements(IndividualElement elem) {
        doGraphRemoveIndividualElements(elem);
    }

    @Override
    public Collection<IndividualElement> getAllIndividualElements() {
        return Collections.unmodifiableCollection(__implIndividualElements);
    }

    @Override
    public boolean addIndividualElements(IndividualElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implIndividualElements.contains(elem))
            return false;
        doDispatchAddIndividualElements(elem);
        return true;
    }

    @Override
    public boolean removeIndividualElements(IndividualElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implIndividualElements.contains(elem))
            return false;
        doDispatchRemoveIndividualElements(elem);
        return true;
    }

    /**
     * Constructor for the implementation of IndividualSequence
     */
    public Owl2IndividualSequenceImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#individualElements
        this.__implIndividualElements = new ArrayList<>();
    }
}
