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
 * The default implementation for the concrete OWL class Rule
 *
 * @author xOWL code generator
 */
public class RulesRuleImpl implements org.xowl.infra.lang.rules.Rule {
    /**
     * The backing data for the property Guard
     */
    private org.xowl.infra.lang.owl2.LiteralExpression __implGuard;

    /**
     * Adds a value to the property Guard
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        __implGuard = elem;
    }

    /**
     * Removes a value from the property Guard
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        __implGuard = null;
    }

    /**
     * Adds a value to the property Guard
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doSimpleAddGuard(elem);
    }

    /**
     * Removes a value from the property Guard
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doSimpleRemoveGuard(elem);
    }

    /**
     * Tries to add a value to the property Guard and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doPropertyAddGuard(elem);
    }

    /**
     * Tries to remove a value from the property Guard and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doPropertyRemoveGuard(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Guard
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doGraphAddGuard(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Guard
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doGraphRemoveGuard(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.LiteralExpression getGuard() {
        return __implGuard;
    }

    @Override
    public void setGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        if (__implGuard == elem)
            return;
        if (elem == null) {
            doDispatchRemoveGuard(__implGuard);
        } else if (__implGuard == null) {
            doDispatchAddGuard(elem);
        } else {
            doDispatchRemoveGuard(__implGuard);
            doDispatchAddGuard(elem);
        }
    }

    /**
     * The backing data for the property Antecedents
     */
    private List<org.xowl.infra.lang.rules.Assertion> __implAntecedents;

    /**
     * Adds a value to the property Antecedents
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        __implAntecedents.add(elem);
    }

    /**
     * Removes a value from the property Antecedents
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        __implAntecedents.remove(elem);
    }

    /**
     * Adds a value to the property Antecedents
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        doSimpleAddAntecedents(elem);
    }

    /**
     * Removes a value from the property Antecedents
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        doSimpleRemoveAntecedents(elem);
    }

    /**
     * Tries to add a value to the property Antecedents and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        doPropertyAddAntecedents(elem);
    }

    /**
     * Tries to remove a value from the property Antecedents and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        doPropertyRemoveAntecedents(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Antecedents
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        doGraphAddAntecedents(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Antecedents
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        doGraphRemoveAntecedents(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.rules.Assertion> getAllAntecedents() {
        return Collections.unmodifiableCollection(__implAntecedents);
    }

    @Override
    public boolean addAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implAntecedents.contains(elem))
            return false;
        doDispatchAddAntecedents(elem);
        return true;
    }

    @Override
    public boolean removeAntecedents(org.xowl.infra.lang.rules.Assertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implAntecedents.contains(elem))
            return false;
        doDispatchRemoveAntecedents(elem);
        return true;
    }

    /**
     * The backing data for the property Consequents
     */
    private List<org.xowl.infra.lang.rules.Assertion> __implConsequents;

    /**
     * Adds a value to the property Consequents
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        __implConsequents.add(elem);
    }

    /**
     * Removes a value from the property Consequents
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        __implConsequents.remove(elem);
    }

    /**
     * Adds a value to the property Consequents
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        doSimpleAddConsequents(elem);
    }

    /**
     * Removes a value from the property Consequents
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        doSimpleRemoveConsequents(elem);
    }

    /**
     * Tries to add a value to the property Consequents and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        doPropertyAddConsequents(elem);
    }

    /**
     * Tries to remove a value from the property Consequents and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        doPropertyRemoveConsequents(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Consequents
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        doGraphAddConsequents(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Consequents
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        doGraphRemoveConsequents(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.rules.Assertion> getAllConsequents() {
        return Collections.unmodifiableCollection(__implConsequents);
    }

    @Override
    public boolean addConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implConsequents.contains(elem))
            return false;
        doDispatchAddConsequents(elem);
        return true;
    }

    @Override
    public boolean removeConsequents(org.xowl.infra.lang.rules.Assertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implConsequents.contains(elem))
            return false;
        doDispatchRemoveConsequents(elem);
        return true;
    }

    /**
     * The backing data for the property HasIRI
     */
    private org.xowl.infra.lang.owl2.IRI __implHasIRI;

    /**
     * Adds a value to the property HasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        __implHasIRI = elem;
    }

    /**
     * Removes a value from the property HasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        __implHasIRI = null;
    }

    /**
     * Adds a value to the property HasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddHasIRI(elem);
    }

    /**
     * Removes a value from the property HasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveHasIRI(elem);
    }

    /**
     * Tries to add a value to the property HasIRI and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddHasIRI(elem);
    }

    /**
     * Tries to remove a value from the property HasIRI and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveHasIRI(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property HasIRI
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddHasIRI(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property HasIRI
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphRemoveHasIRI(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IRI getHasIRI() {
        return __implHasIRI;
    }

    @Override
    public void setHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        if (__implHasIRI == elem)
            return;
        if (elem == null) {
            doDispatchRemoveHasIRI(__implHasIRI);
        } else if (__implHasIRI == null) {
            doDispatchAddHasIRI(elem);
        } else {
            doDispatchRemoveHasIRI(__implHasIRI);
            doDispatchAddHasIRI(elem);
        }
    }

    /**
     * Constructor for the implementation of Rule
     */
    public RulesRuleImpl() {
        this.__implGuard = null;
        this.__implAntecedents = new ArrayList<>();
        this.__implConsequents = new ArrayList<>();
        this.__implHasIRI = null;
    }
}
