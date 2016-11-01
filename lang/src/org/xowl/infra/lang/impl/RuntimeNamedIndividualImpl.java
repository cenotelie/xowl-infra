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

import org.xowl.infra.lang.runtime.*;
import org.xowl.infra.lang.runtime.Class;

import java.util.*;

/**
 * The default implementation for NamedIndividual
 * Original OWL class is http://xowl.org/infra/lang/runtime#NamedIndividual
 *
 * @author xOWL code generator
 */
public class RuntimeNamedIndividualImpl implements NamedIndividual {
    /**
     * The backing data for the property Asserts
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#asserts
     */
    private List<PropertyAssertion> __implAsserts;

    /**
     * Adds a value to the property Asserts
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAsserts(PropertyAssertion elem) {
        __implAsserts.add(elem);
    }

    /**
     * Removes a value from the property Asserts
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAsserts(PropertyAssertion elem) {
        __implAsserts.remove(elem);
    }

    /**
     * Adds a value to the property Asserts
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAsserts(PropertyAssertion elem) {
        doSimpleAddAsserts(elem);
    }

    /**
     * Removes a value from the property Asserts
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAsserts(PropertyAssertion elem) {
        doSimpleRemoveAsserts(elem);
    }

    /**
     * Tries to add a value to the property Asserts and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAsserts(PropertyAssertion elem) {
        doPropertyAddAsserts(elem);
    }

    /**
     * Tries to remove a value from the property Asserts and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAsserts(PropertyAssertion elem) {
        doPropertyRemoveAsserts(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Asserts
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAsserts(PropertyAssertion elem) {
        doGraphAddAsserts(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Asserts
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#asserts
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAsserts(PropertyAssertion elem) {
        doGraphRemoveAsserts(elem);
    }

    @Override
    public Collection<PropertyAssertion> getAllAsserts() {
        return Collections.unmodifiableCollection(__implAsserts);
    }

    @Override
    public boolean addAsserts(PropertyAssertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implAsserts.contains(elem))
            return false;
        doDispatchAddAsserts(elem);
        return true;
    }

    @Override
    public boolean removeAsserts(PropertyAssertion elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implAsserts.contains(elem))
            return false;
        doDispatchRemoveAsserts(elem);
        return true;
    }

    /**
     * The backing data for the property ClassifiedBy
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classifiedBy
     */
    private List<Class> __implClassifiedBy;

    /**
     * Adds a value to the property ClassifiedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassifiedBy(Class elem) {
        __implClassifiedBy.add(elem);
    }

    /**
     * Removes a value from the property ClassifiedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassifiedBy(Class elem) {
        __implClassifiedBy.remove(elem);
    }

    /**
     * Adds a value to the property ClassifiedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     * This method will also update the inverse property Classifies
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassifiedBy(Class elem) {
        doSimpleAddClassifiedBy(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddClassifies(this);
    }

    /**
     * Removes a value from the property ClassifiedBy
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     * This method will also update the inverse property Classifies
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassifiedBy(Class elem) {
        doSimpleRemoveClassifiedBy(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveClassifies(this);
    }

    /**
     * Tries to add a value to the property ClassifiedBy and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassifiedBy(Class elem) {
        doPropertyAddClassifiedBy(elem);
    }

    /**
     * Tries to remove a value from the property ClassifiedBy and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassifiedBy(Class elem) {
        doPropertyRemoveClassifiedBy(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassifiedBy
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassifiedBy(Class elem) {
        doGraphAddClassifiedBy(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassifiedBy
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifiedBy
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassifiedBy(Class elem) {
        doGraphRemoveClassifiedBy(elem);
    }

    @Override
    public Collection<Class> getAllClassifiedBy() {
        return Collections.unmodifiableCollection(__implClassifiedBy);
    }

    @Override
    public boolean addClassifiedBy(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassifiedBy.contains(elem))
            return false;
        doDispatchAddClassifiedBy(elem);
        return true;
    }

    @Override
    public boolean removeClassifiedBy(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassifiedBy.contains(elem))
            return false;
        doDispatchRemoveClassifiedBy(elem);
        return true;
    }

    /**
     * The backing data for the property DifferentFrom
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#differentFrom
     */
    private List<Individual> __implDifferentFrom;

    /**
     * Adds a value to the property DifferentFrom
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDifferentFrom(Individual elem) {
        __implDifferentFrom.add(elem);
    }

    /**
     * Removes a value from the property DifferentFrom
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDifferentFrom(Individual elem) {
        __implDifferentFrom.remove(elem);
    }

    /**
     * Adds a value to the property DifferentFrom
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     * This method will also update the inverse property DifferentFrom
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDifferentFrom(Individual elem) {
        doSimpleAddDifferentFrom(elem);
        if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleAddDifferentFrom(this);
        else if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleAddDifferentFrom(this);
    }

    /**
     * Removes a value from the property DifferentFrom
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     * This method will also update the inverse property DifferentFrom
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDifferentFrom(Individual elem) {
        doSimpleRemoveDifferentFrom(elem);
        if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleRemoveDifferentFrom(this);
        else if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleRemoveDifferentFrom(this);
    }

    /**
     * Tries to add a value to the property DifferentFrom and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDifferentFrom(Individual elem) {
        doPropertyAddDifferentFrom(elem);
    }

    /**
     * Tries to remove a value from the property DifferentFrom and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDifferentFrom(Individual elem) {
        doPropertyRemoveDifferentFrom(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DifferentFrom
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDifferentFrom(Individual elem) {
        doGraphAddDifferentFrom(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DifferentFrom
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#differentFrom
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDifferentFrom(Individual elem) {
        doGraphRemoveDifferentFrom(elem);
    }

    @Override
    public Collection<Individual> getAllDifferentFrom() {
        return Collections.unmodifiableCollection(__implDifferentFrom);
    }

    @Override
    public boolean addDifferentFrom(Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDifferentFrom.contains(elem))
            return false;
        doDispatchAddDifferentFrom(elem);
        return true;
    }

    @Override
    public boolean removeDifferentFrom(Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDifferentFrom.contains(elem))
            return false;
        doDispatchRemoveDifferentFrom(elem);
        return true;
    }

    /**
     * The backing data for the property InterpretationOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#interpretationOf
     */
    private Entity __implInterpretationOf;

    /**
     * Adds a value to the property InterpretationOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInterpretationOf(Entity elem) {
        __implInterpretationOf = elem;
    }

    /**
     * Removes a value from the property InterpretationOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInterpretationOf(Entity elem) {
        __implInterpretationOf = null;
    }

    /**
     * Adds a value to the property InterpretationOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     * This method will also update the inverse property InterpretedAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInterpretationOf(Entity elem) {
        doSimpleAddInterpretationOf(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleAddInterpretedAs(this);
    }

    /**
     * Removes a value from the property InterpretationOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     * This method will also update the inverse property InterpretedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInterpretationOf(Entity elem) {
        doSimpleRemoveInterpretationOf(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleRemoveInterpretedAs(this);
    }

    /**
     * Tries to add a value to the property InterpretationOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInterpretationOf(Entity elem) {
        doPropertyAddInterpretationOf(elem);
    }

    /**
     * Tries to remove a value from the property InterpretationOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInterpretationOf(Entity elem) {
        doPropertyRemoveInterpretationOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property InterpretationOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInterpretationOf(Entity elem) {
        doGraphAddInterpretationOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property InterpretationOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInterpretationOf(Entity elem) {
        doGraphRemoveInterpretationOf(elem);
    }

    @Override
    public Entity getInterpretationOf() {
        return __implInterpretationOf;
    }

    @Override
    public void setInterpretationOf(Entity elem) {
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
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#sameAs
     */
    private List<Individual> __implSameAs;

    /**
     * Adds a value to the property SameAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSameAs(Individual elem) {
        __implSameAs.add(elem);
    }

    /**
     * Removes a value from the property SameAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSameAs(Individual elem) {
        __implSameAs.remove(elem);
    }

    /**
     * Adds a value to the property SameAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     * This method will also update the inverse property SameAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSameAs(Individual elem) {
        doSimpleAddSameAs(elem);
        if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleAddSameAs(this);
        else if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleAddSameAs(this);
    }

    /**
     * Removes a value from the property SameAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     * This method will also update the inverse property SameAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSameAs(Individual elem) {
        doSimpleRemoveSameAs(elem);
        if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleRemoveSameAs(this);
        else if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleRemoveSameAs(this);
    }

    /**
     * Tries to add a value to the property SameAs and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSameAs(Individual elem) {
        doPropertyAddSameAs(elem);
    }

    /**
     * Tries to remove a value from the property SameAs and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSameAs(Individual elem) {
        doPropertyRemoveSameAs(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SameAs
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSameAs(Individual elem) {
        doGraphAddSameAs(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SameAs
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#sameAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSameAs(Individual elem) {
        doGraphRemoveSameAs(elem);
    }

    @Override
    public Collection<Individual> getAllSameAs() {
        return Collections.unmodifiableCollection(__implSameAs);
    }

    @Override
    public boolean addSameAs(Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSameAs.contains(elem))
            return false;
        doDispatchAddSameAs(elem);
        return true;
    }

    @Override
    public boolean removeSameAs(Individual elem) {
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
        // initialize property http://xowl.org/infra/lang/runtime#asserts
        this.__implAsserts = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#classifiedBy
        this.__implClassifiedBy = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#differentFrom
        this.__implDifferentFrom = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#interpretationOf
        this.__implInterpretationOf = null;
        // initialize property http://xowl.org/infra/lang/runtime#sameAs
        this.__implSameAs = new ArrayList<>();
    }
}
