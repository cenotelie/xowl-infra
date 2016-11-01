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
 * The default implementation for the concrete OWL class LiteralSequence
 *
 * @author xOWL code generator
 */
public class Owl2LiteralSequenceImpl implements org.xowl.infra.lang.owl2.LiteralSequence {
    /**
     * The backing data for the property LiteralElements
     */
    private List<org.xowl.infra.lang.owl2.LiteralElement> __implLiteralElements;

    /**
     * Adds a value to the property LiteralElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        __implLiteralElements.add(elem);
    }

    /**
     * Removes a value from the property LiteralElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        __implLiteralElements.remove(elem);
    }

    /**
     * Adds a value to the property LiteralElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        doSimpleAddLiteralElements(elem);
    }

    /**
     * Removes a value from the property LiteralElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        doSimpleRemoveLiteralElements(elem);
    }

    /**
     * Tries to add a value to the property LiteralElements and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        doPropertyAddLiteralElements(elem);
    }

    /**
     * Tries to remove a value from the property LiteralElements and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        doPropertyRemoveLiteralElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property LiteralElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        doGraphAddLiteralElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property LiteralElements
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        doGraphRemoveLiteralElements(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.owl2.LiteralElement> getAllLiteralElements() {
        return Collections.unmodifiableCollection(__implLiteralElements);
    }

    @Override
    public boolean addLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implLiteralElements.contains(elem))
            return false;
        doDispatchAddLiteralElements(elem);
        return true;
    }

    @Override
    public boolean removeLiteralElements(org.xowl.infra.lang.owl2.LiteralElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implLiteralElements.contains(elem))
            return false;
        doDispatchRemoveLiteralElements(elem);
        return true;
    }

    /**
     * Constructor for the implementation of LiteralSequence
     */
    public Owl2LiteralSequenceImpl() {
        this.__implLiteralElements = new ArrayList<>();
    }
}
