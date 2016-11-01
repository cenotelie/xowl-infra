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
 * The default implementation for LiteralSequence
 * Original OWL class is http://xowl.org/infra/lang/owl2#LiteralSequence
 *
 * @author xOWL code generator
 */
public class Owl2LiteralSequenceImpl implements LiteralSequence {
    /**
     * The backing data for the property LiteralElements
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#literalElements
     */
    private List<LiteralElement> __implLiteralElements;

    /**
     * Adds a value to the property LiteralElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#literalElements
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddLiteralElements(LiteralElement elem) {
        __implLiteralElements.add(elem);
    }

    /**
     * Removes a value from the property LiteralElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#literalElements
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveLiteralElements(LiteralElement elem) {
        __implLiteralElements.remove(elem);
    }

    /**
     * Adds a value to the property LiteralElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#literalElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddLiteralElements(LiteralElement elem) {
        doSimpleAddLiteralElements(elem);
    }

    /**
     * Removes a value from the property LiteralElements
     * Original OWL property is http://xowl.org/infra/lang/owl2#literalElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveLiteralElements(LiteralElement elem) {
        doSimpleRemoveLiteralElements(elem);
    }

    /**
     * Tries to add a value to the property LiteralElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#literalElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddLiteralElements(LiteralElement elem) {
        doPropertyAddLiteralElements(elem);
    }

    /**
     * Tries to remove a value from the property LiteralElements and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#literalElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveLiteralElements(LiteralElement elem) {
        doPropertyRemoveLiteralElements(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property LiteralElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#literalElements
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddLiteralElements(LiteralElement elem) {
        doGraphAddLiteralElements(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property LiteralElements
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#literalElements
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveLiteralElements(LiteralElement elem) {
        doGraphRemoveLiteralElements(elem);
    }

    @Override
    public Collection<LiteralElement> getAllLiteralElements() {
        return Collections.unmodifiableCollection(__implLiteralElements);
    }

    @Override
    public boolean addLiteralElements(LiteralElement elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implLiteralElements.contains(elem))
            return false;
        doDispatchAddLiteralElements(elem);
        return true;
    }

    @Override
    public boolean removeLiteralElements(LiteralElement elem) {
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
        // initialize property http://xowl.org/infra/lang/owl2#literalElements
        this.__implLiteralElements = new ArrayList<>();
    }
}
