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
 * The default implementation for the concrete OWL class Assertion
 *
 * @author xOWL code generator
 */
public class RulesAssertionImpl implements org.xowl.infra.lang.rules.Assertion {
    /**
     * The backing data for the property Axioms
     */
    private List<org.xowl.infra.lang.owl2.Axiom> __implAxioms;

    /**
     * Adds a value to the property Axioms
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        __implAxioms.add(elem);
    }

    /**
     * Removes a value from the property Axioms
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        __implAxioms.remove(elem);
    }

    /**
     * Adds a value to the property Axioms
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        doSimpleAddAxioms(elem);
    }

    /**
     * Removes a value from the property Axioms
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        doSimpleRemoveAxioms(elem);
    }

    /**
     * Tries to add a value to the property Axioms and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        doPropertyAddAxioms(elem);
    }

    /**
     * Tries to remove a value from the property Axioms and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        doPropertyRemoveAxioms(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Axioms
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        doGraphAddAxioms(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Axioms
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        doGraphRemoveAxioms(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.owl2.Axiom> getAllAxioms() {
        return Collections.unmodifiableCollection(__implAxioms);
    }

    @Override
    public boolean addAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implAxioms.contains(elem))
            return false;
        doDispatchAddAxioms(elem);
        return true;
    }

    @Override
    public boolean removeAxioms(org.xowl.infra.lang.owl2.Axiom elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implAxioms.contains(elem))
            return false;
        doDispatchRemoveAxioms(elem);
        return true;
    }

    /**
     * The backing data for the property IsMeta
     */
    private boolean __implIsMeta;

    @Override
    public boolean getIsMeta() {
        return __implIsMeta;
    }

    @Override
    public void setIsMeta(boolean elem) {
        __implIsMeta = elem;
    }

    /**
     * The backing data for the property IsPositive
     */
    private boolean __implIsPositive;

    @Override
    public boolean getIsPositive() {
        return __implIsPositive;
    }

    @Override
    public void setIsPositive(boolean elem) {
        __implIsPositive = elem;
    }

    /**
     * Constructor for the implementation of Assertion
     */
    public RulesAssertionImpl() {
        this.__implAxioms = new ArrayList<>();
        this.__implIsMeta = false;
        this.__implIsPositive = false;
    }
}
