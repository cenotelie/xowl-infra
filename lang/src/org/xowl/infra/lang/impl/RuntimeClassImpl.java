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
 * The default implementation for the concrete OWL class Class
 *
 * @author xOWL code generator
 */
public class RuntimeClassImpl implements org.xowl.infra.lang.runtime.Class {
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
     * The backing data for the property DomainOf
     */
    private List<org.xowl.infra.lang.runtime.Property> __implDomainOf;

    /**
     * Adds a value to the property DomainOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        __implDomainOf.add(elem);
    }

    /**
     * Removes a value from the property DomainOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        __implDomainOf.remove(elem);
    }

    /**
     * Adds a value to the property DomainOf
     * This method will also update the inverse property Domain
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        doSimpleAddDomainOf(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleAddDomain(this);
        else if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddDomain(this);
    }

    /**
     * Removes a value from the property DomainOf
     * This method will also update the inverse property Domain
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        doSimpleRemoveDomainOf(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleRemoveDomain(this);
        else if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveDomain(this);
    }

    /**
     * Tries to add a value to the property DomainOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        doPropertyAddDomainOf(elem);
    }

    /**
     * Tries to remove a value from the property DomainOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        doPropertyRemoveDomainOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DomainOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        doGraphAddDomainOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DomainOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        doGraphRemoveDomainOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Property> getAllDomainOf() {
        return Collections.unmodifiableCollection(__implDomainOf);
    }

    @Override
    public boolean addDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDomainOf.contains(elem))
            return false;
        doDispatchAddDomainOf(elem);
        return true;
    }

    @Override
    public boolean removeDomainOf(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDomainOf.contains(elem))
            return false;
        doDispatchRemoveDomainOf(elem);
        return true;
    }

    /**
     * The backing data for the property ClassOneOf
     */
    private List<org.xowl.infra.lang.runtime.Individual> __implClassOneOf;

    /**
     * Adds a value to the property ClassOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        __implClassOneOf.add(elem);
    }

    /**
     * Removes a value from the property ClassOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        __implClassOneOf.remove(elem);
    }

    /**
     * Adds a value to the property ClassOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleAddClassOneOf(elem);
    }

    /**
     * Removes a value from the property ClassOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleRemoveClassOneOf(elem);
    }

    /**
     * Tries to add a value to the property ClassOneOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyAddClassOneOf(elem);
    }

    /**
     * Tries to remove a value from the property ClassOneOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyRemoveClassOneOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassOneOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphAddClassOneOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassOneOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphRemoveClassOneOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Individual> getAllClassOneOf() {
        return Collections.unmodifiableCollection(__implClassOneOf);
    }

    @Override
    public boolean addClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassOneOf.contains(elem))
            return false;
        doDispatchAddClassOneOf(elem);
        return true;
    }

    @Override
    public boolean removeClassOneOf(org.xowl.infra.lang.runtime.Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassOneOf.contains(elem))
            return false;
        doDispatchRemoveClassOneOf(elem);
        return true;
    }

    /**
     * The backing data for the property ClassDisjointWith
     */
    private List<org.xowl.infra.lang.runtime.Class> __implClassDisjointWith;

    /**
     * Adds a value to the property ClassDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        __implClassDisjointWith.add(elem);
    }

    /**
     * Removes a value from the property ClassDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        __implClassDisjointWith.remove(elem);
    }

    /**
     * Adds a value to the property ClassDisjointWith
     * This method will also update the inverse property ClassDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddClassDisjointWith(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddClassDisjointWith(this);
    }

    /**
     * Removes a value from the property ClassDisjointWith
     * This method will also update the inverse property ClassDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveClassDisjointWith(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveClassDisjointWith(this);
    }

    /**
     * Tries to add a value to the property ClassDisjointWith and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddClassDisjointWith(elem);
    }

    /**
     * Tries to remove a value from the property ClassDisjointWith and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveClassDisjointWith(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassDisjointWith
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddClassDisjointWith(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassDisjointWith
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveClassDisjointWith(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Class> getAllClassDisjointWith() {
        return Collections.unmodifiableCollection(__implClassDisjointWith);
    }

    @Override
    public boolean addClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassDisjointWith.contains(elem))
            return false;
        doDispatchAddClassDisjointWith(elem);
        return true;
    }

    @Override
    public boolean removeClassDisjointWith(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassDisjointWith.contains(elem))
            return false;
        doDispatchRemoveClassDisjointWith(elem);
        return true;
    }

    /**
     * The backing data for the property Classifies
     */
    private List<org.xowl.infra.lang.runtime.Individual> __implClassifies;

    /**
     * Adds a value to the property Classifies
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        __implClassifies.add(elem);
    }

    /**
     * Removes a value from the property Classifies
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        __implClassifies.remove(elem);
    }

    /**
     * Adds a value to the property Classifies
     * This method will also update the inverse property ClassifiedBy
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleAddClassifies(elem);
        if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleAddClassifiedBy(this);
        else if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleAddClassifiedBy(this);
    }

    /**
     * Removes a value from the property Classifies
     * This method will also update the inverse property ClassifiedBy
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        doSimpleRemoveClassifies(elem);
        if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleRemoveClassifiedBy(this);
        else if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleRemoveClassifiedBy(this);
    }

    /**
     * Tries to add a value to the property Classifies and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyAddClassifies(elem);
    }

    /**
     * Tries to remove a value from the property Classifies and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        doPropertyRemoveClassifies(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Classifies
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphAddClassifies(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Classifies
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        doGraphRemoveClassifies(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Individual> getAllClassifies() {
        return Collections.unmodifiableCollection(__implClassifies);
    }

    @Override
    public boolean addClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassifies.contains(elem))
            return false;
        doDispatchAddClassifies(elem);
        return true;
    }

    @Override
    public boolean removeClassifies(org.xowl.infra.lang.runtime.Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassifies.contains(elem))
            return false;
        doDispatchRemoveClassifies(elem);
        return true;
    }

    /**
     * The backing data for the property SubClassOf
     */
    private List<org.xowl.infra.lang.runtime.Class> __implSubClassOf;

    /**
     * Adds a value to the property SubClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        __implSubClassOf.add(elem);
    }

    /**
     * Removes a value from the property SubClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        __implSubClassOf.remove(elem);
    }

    /**
     * Adds a value to the property SubClassOf
     * This method will also update the inverse property SuperClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddSubClassOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddSuperClassOf(this);
    }

    /**
     * Removes a value from the property SubClassOf
     * This method will also update the inverse property SuperClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveSubClassOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveSuperClassOf(this);
    }

    /**
     * Tries to add a value to the property SubClassOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddSubClassOf(elem);
    }

    /**
     * Tries to remove a value from the property SubClassOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveSubClassOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SubClassOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddSubClassOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SubClassOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveSubClassOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Class> getAllSubClassOf() {
        return Collections.unmodifiableCollection(__implSubClassOf);
    }

    @Override
    public boolean addSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSubClassOf.contains(elem))
            return false;
        doDispatchAddSubClassOf(elem);
        return true;
    }

    @Override
    public boolean removeSubClassOf(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSubClassOf.contains(elem))
            return false;
        doDispatchRemoveSubClassOf(elem);
        return true;
    }

    /**
     * The backing data for the property ClassEquivalentTo
     */
    private List<org.xowl.infra.lang.runtime.Class> __implClassEquivalentTo;

    /**
     * Adds a value to the property ClassEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        __implClassEquivalentTo.add(elem);
    }

    /**
     * Removes a value from the property ClassEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        __implClassEquivalentTo.remove(elem);
    }

    /**
     * Adds a value to the property ClassEquivalentTo
     * This method will also update the inverse property ClassEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddClassEquivalentTo(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddClassEquivalentTo(this);
    }

    /**
     * Removes a value from the property ClassEquivalentTo
     * This method will also update the inverse property ClassEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveClassEquivalentTo(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveClassEquivalentTo(this);
    }

    /**
     * Tries to add a value to the property ClassEquivalentTo and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddClassEquivalentTo(elem);
    }

    /**
     * Tries to remove a value from the property ClassEquivalentTo and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveClassEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassEquivalentTo
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddClassEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassEquivalentTo
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveClassEquivalentTo(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Class> getAllClassEquivalentTo() {
        return Collections.unmodifiableCollection(__implClassEquivalentTo);
    }

    @Override
    public boolean addClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassEquivalentTo.contains(elem))
            return false;
        doDispatchAddClassEquivalentTo(elem);
        return true;
    }

    @Override
    public boolean removeClassEquivalentTo(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassEquivalentTo.contains(elem))
            return false;
        doDispatchRemoveClassEquivalentTo(elem);
        return true;
    }

    /**
     * The backing data for the property ClassIntersectionOf
     */
    private List<org.xowl.infra.lang.runtime.Class> __implClassIntersectionOf;

    /**
     * Adds a value to the property ClassIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        __implClassIntersectionOf.add(elem);
    }

    /**
     * Removes a value from the property ClassIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        __implClassIntersectionOf.remove(elem);
    }

    /**
     * Adds a value to the property ClassIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddClassIntersectionOf(elem);
    }

    /**
     * Removes a value from the property ClassIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveClassIntersectionOf(elem);
    }

    /**
     * Tries to add a value to the property ClassIntersectionOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddClassIntersectionOf(elem);
    }

    /**
     * Tries to remove a value from the property ClassIntersectionOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveClassIntersectionOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassIntersectionOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddClassIntersectionOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassIntersectionOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveClassIntersectionOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Class> getAllClassIntersectionOf() {
        return Collections.unmodifiableCollection(__implClassIntersectionOf);
    }

    @Override
    public boolean addClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassIntersectionOf.contains(elem))
            return false;
        doDispatchAddClassIntersectionOf(elem);
        return true;
    }

    @Override
    public boolean removeClassIntersectionOf(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassIntersectionOf.contains(elem))
            return false;
        doDispatchRemoveClassIntersectionOf(elem);
        return true;
    }

    /**
     * The backing data for the property ClassComplementOf
     */
    private org.xowl.infra.lang.runtime.Class __implClassComplementOf;

    /**
     * Adds a value to the property ClassComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        __implClassComplementOf = elem;
    }

    /**
     * Removes a value from the property ClassComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        __implClassComplementOf = null;
    }

    /**
     * Adds a value to the property ClassComplementOf
     * This method will also update the inverse property ClassComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddClassComplementOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddClassComplementOf(this);
    }

    /**
     * Removes a value from the property ClassComplementOf
     * This method will also update the inverse property ClassComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveClassComplementOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveClassComplementOf(this);
    }

    /**
     * Tries to add a value to the property ClassComplementOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddClassComplementOf(elem);
    }

    /**
     * Tries to remove a value from the property ClassComplementOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveClassComplementOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassComplementOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddClassComplementOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassComplementOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveClassComplementOf(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Class getClassComplementOf() {
        return __implClassComplementOf;
    }

    @Override
    public void setClassComplementOf(org.xowl.infra.lang.runtime.Class elem) {
        if (__implClassComplementOf == elem)
            return;
        if (elem == null) {
            doDispatchRemoveClassComplementOf(__implClassComplementOf);
        } else if (__implClassComplementOf == null) {
            doDispatchAddClassComplementOf(elem);
        } else {
            doDispatchRemoveClassComplementOf(__implClassComplementOf);
            doDispatchAddClassComplementOf(elem);
        }
    }

    /**
     * The backing data for the property ClassUnionOf
     */
    private List<org.xowl.infra.lang.runtime.Class> __implClassUnionOf;

    /**
     * Adds a value to the property ClassUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        __implClassUnionOf.add(elem);
    }

    /**
     * Removes a value from the property ClassUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        __implClassUnionOf.remove(elem);
    }

    /**
     * Adds a value to the property ClassUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddClassUnionOf(elem);
    }

    /**
     * Removes a value from the property ClassUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveClassUnionOf(elem);
    }

    /**
     * Tries to add a value to the property ClassUnionOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddClassUnionOf(elem);
    }

    /**
     * Tries to remove a value from the property ClassUnionOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveClassUnionOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassUnionOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddClassUnionOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassUnionOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveClassUnionOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Class> getAllClassUnionOf() {
        return Collections.unmodifiableCollection(__implClassUnionOf);
    }

    @Override
    public boolean addClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassUnionOf.contains(elem))
            return false;
        doDispatchAddClassUnionOf(elem);
        return true;
    }

    @Override
    public boolean removeClassUnionOf(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassUnionOf.contains(elem))
            return false;
        doDispatchRemoveClassUnionOf(elem);
        return true;
    }

    /**
     * The backing data for the property SuperClassOf
     */
    private List<org.xowl.infra.lang.runtime.Class> __implSuperClassOf;

    /**
     * Adds a value to the property SuperClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        __implSuperClassOf.add(elem);
    }

    /**
     * Removes a value from the property SuperClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        __implSuperClassOf.remove(elem);
    }

    /**
     * Adds a value to the property SuperClassOf
     * This method will also update the inverse property SubClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddSuperClassOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddSubClassOf(this);
    }

    /**
     * Removes a value from the property SuperClassOf
     * This method will also update the inverse property SubClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveSuperClassOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveSubClassOf(this);
    }

    /**
     * Tries to add a value to the property SuperClassOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddSuperClassOf(elem);
    }

    /**
     * Tries to remove a value from the property SuperClassOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveSuperClassOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperClassOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddSuperClassOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperClassOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveSuperClassOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Class> getAllSuperClassOf() {
        return Collections.unmodifiableCollection(__implSuperClassOf);
    }

    @Override
    public boolean addSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSuperClassOf.contains(elem))
            return false;
        doDispatchAddSuperClassOf(elem);
        return true;
    }

    @Override
    public boolean removeSuperClassOf(org.xowl.infra.lang.runtime.Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSuperClassOf.contains(elem))
            return false;
        doDispatchRemoveSuperClassOf(elem);
        return true;
    }

    /**
     * The backing data for the property ClassRestrictions
     */
    private List<org.xowl.infra.lang.runtime.ClassRestriction> __implClassRestrictions;

    /**
     * Adds a value to the property ClassRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        __implClassRestrictions.add(elem);
    }

    /**
     * Removes a value from the property ClassRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        __implClassRestrictions.remove(elem);
    }

    /**
     * Adds a value to the property ClassRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        doSimpleAddClassRestrictions(elem);
    }

    /**
     * Removes a value from the property ClassRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        doSimpleRemoveClassRestrictions(elem);
    }

    /**
     * Tries to add a value to the property ClassRestrictions and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        doPropertyAddClassRestrictions(elem);
    }

    /**
     * Tries to remove a value from the property ClassRestrictions and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        doPropertyRemoveClassRestrictions(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassRestrictions
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        doGraphAddClassRestrictions(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassRestrictions
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        doGraphRemoveClassRestrictions(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.ClassRestriction> getAllClassRestrictions() {
        return Collections.unmodifiableCollection(__implClassRestrictions);
    }

    @Override
    public boolean addClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassRestrictions.contains(elem))
            return false;
        doDispatchAddClassRestrictions(elem);
        return true;
    }

    @Override
    public boolean removeClassRestrictions(org.xowl.infra.lang.runtime.ClassRestriction elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassRestrictions.contains(elem))
            return false;
        doDispatchRemoveClassRestrictions(elem);
        return true;
    }

    /**
     * Constructor for the implementation of Class
     */
    public RuntimeClassImpl() {
        this.__implInterpretationOf = null;
        this.__implDomainOf = new ArrayList<>();
        this.__implClassOneOf = new ArrayList<>();
        this.__implClassDisjointWith = new ArrayList<>();
        this.__implClassifies = new ArrayList<>();
        this.__implSubClassOf = new ArrayList<>();
        this.__implClassEquivalentTo = new ArrayList<>();
        this.__implClassIntersectionOf = new ArrayList<>();
        this.__implClassComplementOf = null;
        this.__implClassUnionOf = new ArrayList<>();
        this.__implSuperClassOf = new ArrayList<>();
        this.__implClassRestrictions = new ArrayList<>();
    }
}
