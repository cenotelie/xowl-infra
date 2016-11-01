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
 * The default implementation for the concrete OWL class IndividualSequence
 *
 * @author xOWL code generator
 */
public class Owl2IndividualSequenceImpl implements org.xowl.infra.lang.owl2.IndividualSequence {
    /**
     * The backing data for the property IndividualElements
     */
    private List<org.xowl.infra.lang.owl2.IndividualElement> __implIndividualElements;

    /**
     * Adds a value to the property IndividualElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        __implIndividualElements.add(elem);
    }

    /**
     * Removes a value from the property IndividualElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        __implIndividualElements.remove(elem);
    }

    /**
     * Adds a value to the property IndividualElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        doSimpleAddIndividualElements(elem);
    }

    /**
     * Removes a value from the property IndividualElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        doSimpleRemoveIndividualElements(elem);
    }

    /**
     * Tries to add a value to the property IndividualElements and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        doPropertyAddIndividualElements(elem);
    }

    /**
     * Tries to remove a value from the property IndividualElements and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        doPropertyRemoveIndividualElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property IndividualElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        doGraphAddIndividualElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property IndividualElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        doGraphRemoveIndividualElements(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.owl2.IndividualElement> getAllIndividualElements() {
        return Collections.unmodifiableCollection(__implIndividualElements);
    }

    @Override
    public boolean addIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implIndividualElements.contains(elem))
            return false;
        doDispatchAddIndividualElements(elem);
        return true;
    }

    @Override
    public boolean removeIndividualElements(org.xowl.infra.lang.owl2.IndividualElement elem) {
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
        this.__implIndividualElements = new ArrayList<>();
    }
}
