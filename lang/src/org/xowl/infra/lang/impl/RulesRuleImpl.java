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

import org.xowl.infra.lang.rules.*;

import java.util.*;

/**
 * The default implementation for Rule
 * Original OWL class is http://xowl.org/infra/lang/rules#Rule
 *
 * @author xOWL code generator
 */
public class RulesRuleImpl implements Rule {
    /**
     * The backing data for the property Antecedents
     * This implements the storage for original OWL property http://xowl.org/infra/lang/rules#antecedents
     */
    private List<Assertion> __implAntecedents;

    /**
     * Adds a value to the property Antecedents
     * Original OWL property is http://xowl.org/infra/lang/rules#antecedents
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAntecedents(Assertion elem) {
        __implAntecedents.add(elem);
    }

    /**
     * Removes a value from the property Antecedents
     * Original OWL property is http://xowl.org/infra/lang/rules#antecedents
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAntecedents(Assertion elem) {
        __implAntecedents.remove(elem);
    }

    /**
     * Adds a value to the property Antecedents
     * Original OWL property is http://xowl.org/infra/lang/rules#antecedents
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAntecedents(Assertion elem) {
        doSimpleAddAntecedents(elem);
    }

    /**
     * Removes a value from the property Antecedents
     * Original OWL property is http://xowl.org/infra/lang/rules#antecedents
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAntecedents(Assertion elem) {
        doSimpleRemoveAntecedents(elem);
    }

    /**
     * Tries to add a value to the property Antecedents and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/rules#antecedents
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAntecedents(Assertion elem) {
        doPropertyAddAntecedents(elem);
    }

    /**
     * Tries to remove a value from the property Antecedents and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/rules#antecedents
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAntecedents(Assertion elem) {
        doPropertyRemoveAntecedents(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Antecedents
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/rules#antecedents
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAntecedents(Assertion elem) {
        doGraphAddAntecedents(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Antecedents
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/rules#antecedents
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAntecedents(Assertion elem) {
        doGraphRemoveAntecedents(elem);
    }

    @Override
    public Collection<Assertion> getAllAntecedents() {
        return Collections.unmodifiableCollection(__implAntecedents);
    }

    @Override
    public boolean addAntecedents(Assertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implAntecedents.contains(elem))
            return false;
        doDispatchAddAntecedents(elem);
        return true;
    }

    @Override
    public boolean removeAntecedents(Assertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implAntecedents.contains(elem))
            return false;
        doDispatchRemoveAntecedents(elem);
        return true;
    }

    /**
     * The backing data for the property Consequents
     * This implements the storage for original OWL property http://xowl.org/infra/lang/rules#consequents
     */
    private List<Assertion> __implConsequents;

    /**
     * Adds a value to the property Consequents
     * Original OWL property is http://xowl.org/infra/lang/rules#consequents
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddConsequents(Assertion elem) {
        __implConsequents.add(elem);
    }

    /**
     * Removes a value from the property Consequents
     * Original OWL property is http://xowl.org/infra/lang/rules#consequents
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveConsequents(Assertion elem) {
        __implConsequents.remove(elem);
    }

    /**
     * Adds a value to the property Consequents
     * Original OWL property is http://xowl.org/infra/lang/rules#consequents
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddConsequents(Assertion elem) {
        doSimpleAddConsequents(elem);
    }

    /**
     * Removes a value from the property Consequents
     * Original OWL property is http://xowl.org/infra/lang/rules#consequents
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveConsequents(Assertion elem) {
        doSimpleRemoveConsequents(elem);
    }

    /**
     * Tries to add a value to the property Consequents and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/rules#consequents
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddConsequents(Assertion elem) {
        doPropertyAddConsequents(elem);
    }

    /**
     * Tries to remove a value from the property Consequents and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/rules#consequents
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveConsequents(Assertion elem) {
        doPropertyRemoveConsequents(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Consequents
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/rules#consequents
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddConsequents(Assertion elem) {
        doGraphAddConsequents(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Consequents
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/rules#consequents
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveConsequents(Assertion elem) {
        doGraphRemoveConsequents(elem);
    }

    @Override
    public Collection<Assertion> getAllConsequents() {
        return Collections.unmodifiableCollection(__implConsequents);
    }

    @Override
    public boolean addConsequents(Assertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implConsequents.contains(elem))
            return false;
        doDispatchAddConsequents(elem);
        return true;
    }

    @Override
    public boolean removeConsequents(Assertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implConsequents.contains(elem))
            return false;
        doDispatchRemoveConsequents(elem);
        return true;
    }

    /**
     * The backing data for the property Guard
     * This implements the storage for original OWL property http://xowl.org/infra/lang/rules#guard
     */
    private org.xowl.infra.lang.owl2.LiteralExpression __implGuard;

    /**
     * Adds a value to the property Guard
     * Original OWL property is http://xowl.org/infra/lang/rules#guard
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        __implGuard = elem;
    }

    /**
     * Removes a value from the property Guard
     * Original OWL property is http://xowl.org/infra/lang/rules#guard
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        __implGuard = null;
    }

    /**
     * Adds a value to the property Guard
     * Original OWL property is http://xowl.org/infra/lang/rules#guard
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doSimpleAddGuard(elem);
    }

    /**
     * Removes a value from the property Guard
     * Original OWL property is http://xowl.org/infra/lang/rules#guard
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doSimpleRemoveGuard(elem);
    }

    /**
     * Tries to add a value to the property Guard and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/rules#guard
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doPropertyAddGuard(elem);
    }

    /**
     * Tries to remove a value from the property Guard and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/rules#guard
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doPropertyRemoveGuard(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Guard
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/rules#guard
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddGuard(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doGraphAddGuard(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Guard
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/rules#guard
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
     * The backing data for the property HasIRI
     * This implements the storage for original OWL property http://xowl.org/infra/lang/rules#hasIRI
     */
    private org.xowl.infra.lang.owl2.IRI __implHasIRI;

    /**
     * Adds a value to the property HasIRI
     * Original OWL property is http://xowl.org/infra/lang/rules#hasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        __implHasIRI = elem;
    }

    /**
     * Removes a value from the property HasIRI
     * Original OWL property is http://xowl.org/infra/lang/rules#hasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        __implHasIRI = null;
    }

    /**
     * Adds a value to the property HasIRI
     * Original OWL property is http://xowl.org/infra/lang/rules#hasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddHasIRI(elem);
    }

    /**
     * Removes a value from the property HasIRI
     * Original OWL property is http://xowl.org/infra/lang/rules#hasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveHasIRI(elem);
    }

    /**
     * Tries to add a value to the property HasIRI and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/rules#hasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddHasIRI(elem);
    }

    /**
     * Tries to remove a value from the property HasIRI and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/rules#hasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveHasIRI(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property HasIRI
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/rules#hasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddHasIRI(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property HasIRI
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/rules#hasIRI
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
        // initialize property http://xowl.org/infra/lang/rules#antecedents
        this.__implAntecedents = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/rules#consequents
        this.__implConsequents = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/rules#guard
        this.__implGuard = null;
        // initialize property http://xowl.org/infra/lang/rules#hasIRI
        this.__implHasIRI = null;
    }
}
