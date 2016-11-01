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
 * The default implementation for the concrete OWL class IndividualElement
 *
 * @author xOWL code generator
 */
public class Owl2IndividualElementImpl implements org.xowl.infra.lang.owl2.IndividualElement {
    /**
     * The backing data for the property Index
     */
    private int __implIndex;

    @Override
    public int getIndex() {
        return __implIndex;
    }

    @Override
    public void setIndex(int elem) {
        __implIndex = elem;
    }

    /**
     * The backing data for the property Individual
     */
    private org.xowl.infra.lang.owl2.IndividualExpression __implIndividual;

    /**
     * Adds a value to the property Individual
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        __implIndividual = elem;
    }

    /**
     * Removes a value from the property Individual
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        __implIndividual = null;
    }

    /**
     * Adds a value to the property Individual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doSimpleAddIndividual(elem);
    }

    /**
     * Removes a value from the property Individual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doSimpleRemoveIndividual(elem);
    }

    /**
     * Tries to add a value to the property Individual and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doPropertyAddIndividual(elem);
    }

    /**
     * Tries to remove a value from the property Individual and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doPropertyRemoveIndividual(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Individual
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doGraphAddIndividual(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Individual
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doGraphRemoveIndividual(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IndividualExpression getIndividual() {
        return __implIndividual;
    }

    @Override
    public void setIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        if (__implIndividual == elem)
            return;
        if (elem == null) {
            doDispatchRemoveIndividual(__implIndividual);
        } else if (__implIndividual == null) {
            doDispatchAddIndividual(elem);
        } else {
            doDispatchRemoveIndividual(__implIndividual);
            doDispatchAddIndividual(elem);
        }
    }

    /**
     * Constructor for the implementation of IndividualElement
     */
    public Owl2IndividualElementImpl() {
        this.__implIndex = 0;
        this.__implIndividual = null;
    }
}
