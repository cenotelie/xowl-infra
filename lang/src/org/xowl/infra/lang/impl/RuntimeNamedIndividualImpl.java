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
 * The default implementation for the concrete OWL class NamedIndividual
 *
 * @author xOWL code generator
 */
public class RuntimeNamedIndividualImpl implements org.xowl.infra.lang.runtime.NamedIndividual {
    /**
     * The backing data for the property Asserts
     */
    private List<org.xowl.infra.lang.runtime.PropertyAssertion> __implAsserts;

    /**
     * Adds a value to the property Asserts
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        __implAsserts.add(elem);
    }

    /**
     * Removes a value from the property Asserts
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        __implAsserts.remove(elem);
    }

    /**
     * Adds a value to the property Asserts
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        doSimpleAddAsserts(elem);
    }

    /**
     * Removes a value from the property Asserts
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        doSimpleRemoveAsserts(elem);
    }

    /**
     * Tries to add a value to the property Asserts and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        doPropertyAddAsserts(elem);
    }

    /**
     * Tries to remove a value from the property Asserts and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        doPropertyRemoveAsserts(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Asserts
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        doGraphAddAsserts(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Asserts
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        doGraphRemoveAsserts(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.PropertyAssertion> getAllAsserts() {
        return Collections.unmodifiableCollection(__implAsserts);
    }

    @Override
    public boolean addAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implAsserts.contains(elem))
            return false;
        doDispatchAddAsserts(elem);
        return true;
    }

    @Override
    public boolean removeAsserts(org.xowl.infra.lang.runtime.PropertyAssertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implAsserts.contains(elem))
            return false;
        doDispatchRemoveAsserts(elem);
        return true;
    }

    /**
     * The backing data for the property ClassifiedBy
     */
    private List<org.xowl.infra.lang.runtime.Class> __implClassifiedBy;

    /**
     * Adds a value to the property ClassifiedBy
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        __implClassifiedBy.add(elem);
    }

    /**
     * Removes a value from the property ClassifiedBy
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        __implClassifiedBy.remove(elem);
    }

    /**
     * Adds a value to the property ClassifiedBy
     * This method will also update the inverse property Classifies
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddClassifiedBy(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddClassifies(this);
    }

    /**
     * Removes a value from the property ClassifiedBy
     * This method will also update the inverse property Classifies
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveClassifiedBy(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveClassifies(this);
    }

    /**
     * Tries to add a value to the property ClassifiedBy and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddClassifiedBy(elem);
    }

    /**
     * Tries to remove a value from the property ClassifiedBy and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveClassifiedBy(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassifiedBy
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddClassifiedBy(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassifiedBy
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveClassifiedBy(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Class> getAllClassifiedBy() {
        return Collections.unmodifiableCollection(__implClassifiedBy);
    }

    @Override
    public boolean addClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassifiedBy.contains(elem))
            return false;
        doDispatchAddClassifiedBy(elem);
        return true;
    }

    @Override
    public boolean removeClassifiedBy(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassifiedBy.contains(elem))
            return false;
        doDispatchRemoveClassifiedBy(elem);
        return true;
    }

    /**
     * The backing data for the property DifferentFrom
     */
    private List<org.xowl.infra.lang.runtime.Individual> __implDifferentFrom;

    /**
     * Adds a value to the property DifferentFrom
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        __implDifferentFrom.add(elem);
    }

    /**
     * Removes a value from the property DifferentFrom
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        __implDifferentFrom.remove(elem);
    }

    /**
     * Adds a value to the property DifferentFrom
     * This method will also update the inverse property DifferentFrom
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleAddDifferentFrom(elem);
        if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleAddDifferentFrom(this);
        else if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleAddDifferentFrom(this);
    }

    /**
     * Removes a value from the property DifferentFrom
     * This method will also update the inverse property DifferentFrom
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleRemoveDifferentFrom(elem);
        if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleRemoveDifferentFrom(this);
        else if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleRemoveDifferentFrom(this);
    }

    /**
     * Tries to add a value to the property DifferentFrom and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyAddDifferentFrom(elem);
    }

    /**
     * Tries to remove a value from the property DifferentFrom and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyRemoveDifferentFrom(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DifferentFrom
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphAddDifferentFrom(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DifferentFrom
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphRemoveDifferentFrom(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Individual> getAllDifferentFrom() {
        return Collections.unmodifiableCollection(__implDifferentFrom);
    }

    @Override
    public boolean addDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDifferentFrom.contains(elem))
            return false;
        doDispatchAddDifferentFrom(elem);
        return true;
    }

    @Override
    public boolean removeDifferentFrom(org.xowl.infra.lang.runtime.Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDifferentFrom.contains(elem))
            return false;
        doDispatchRemoveDifferentFrom(elem);
        return true;
    }

    /**
     * The backing data for the property InterpretationOf
     */
    private org.xowl.infra.lang.runtime.Entity __implInterpretationOf;

    /**
     * Adds a value to the property InterpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        __implInterpretationOf = elem;
    }

    /**
     * Removes a value from the property InterpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        __implInterpretationOf = null;
    }

    /**
     * Adds a value to the property InterpretationOf
     * This method will also update the inverse property InterpretedAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doSimpleAddInterpretationOf(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleAddInterpretedAs(this);
    }

    /**
     * Removes a value from the property InterpretationOf
     * This method will also update the inverse property InterpretedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doSimpleRemoveInterpretationOf(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleRemoveInterpretedAs(this);
    }

    /**
     * Tries to add a value to the property InterpretationOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doPropertyAddInterpretationOf(elem);
    }

    /**
     * Tries to remove a value from the property InterpretationOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doPropertyRemoveInterpretationOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property InterpretationOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doGraphAddInterpretationOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property InterpretationOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doGraphRemoveInterpretationOf(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Entity getInterpretationOf() {
        return __implInterpretationOf;
    }

    @Override
    public void setInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        if (__implInterpretationOf == elem)
            return;
        if (elem == null) {
            doDispatchRemoveInterpretationOf(__implInterpretationOf);
        } else if (__implInterpretationOf == null) {
            doDispatchAddInterpretationOf(elem);
        } else {
            doDispatchRemoveInterpretationOf(__implInterpretationOf);
            doDispatchAddInterpretationOf(elem);
        }
    }

    /**
     * The backing data for the property SameAs
     */
    private List<org.xowl.infra.lang.runtime.Individual> __implSameAs;

    /**
     * Adds a value to the property SameAs
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        __implSameAs.add(elem);
    }

    /**
     * Removes a value from the property SameAs
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        __implSameAs.remove(elem);
    }

    /**
     * Adds a value to the property SameAs
     * This method will also update the inverse property SameAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleAddSameAs(elem);
        if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleAddSameAs(this);
        else if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleAddSameAs(this);
    }

    /**
     * Removes a value from the property SameAs
     * This method will also update the inverse property SameAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleRemoveSameAs(elem);
        if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleRemoveSameAs(this);
        else if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleRemoveSameAs(this);
    }

    /**
     * Tries to add a value to the property SameAs and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyAddSameAs(elem);
    }

    /**
     * Tries to remove a value from the property SameAs and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyRemoveSameAs(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SameAs
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphAddSameAs(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SameAs
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphRemoveSameAs(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Individual> getAllSameAs() {
        return Collections.unmodifiableCollection(__implSameAs);
    }

    @Override
    public boolean addSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSameAs.contains(elem))
            return false;
        doDispatchAddSameAs(elem);
        return true;
    }

    @Override
    public boolean removeSameAs(org.xowl.infra.lang.runtime.Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSameAs.contains(elem))
            return false;
        doDispatchRemoveSameAs(elem);
        return true;
    }

    /**
     * Constructor for the implementation of NamedIndividual
     */
    public RuntimeNamedIndividualImpl() {
        this.__implAsserts = new ArrayList<>();
        this.__implClassifiedBy = new ArrayList<>();
        this.__implDifferentFrom = new ArrayList<>();
        this.__implInterpretationOf = null;
        this.__implSameAs = new ArrayList<>();
    }
}