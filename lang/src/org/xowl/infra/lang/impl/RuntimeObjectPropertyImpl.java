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
 * The default implementation for the concrete OWL class ObjectProperty
 *
 * @author xOWL code generator
 */
public class RuntimeObjectPropertyImpl implements org.xowl.infra.lang.runtime.ObjectProperty {
    /**
     * The backing data for the property IsInverseFunctional
     */
    private boolean __implIsInverseFunctional;

    @Override
    public boolean getIsInverseFunctional() {
        return __implIsInverseFunctional;
    }

    @Override
    public void setIsInverseFunctional(boolean elem) {
        __implIsInverseFunctional = elem;
    }

    /**
     * The backing data for the property PropertyDisjointWith
     */
    private List<org.xowl.infra.lang.runtime.ObjectProperty> __implPropertyDisjointWith;

    /**
     * Adds a value to the property PropertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddPropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implPropertyDisjointWith.add(elem);
    }

    /**
     * Removes a value from the property PropertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemovePropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implPropertyDisjointWith.remove(elem);
    }

    /**
     * Adds a value to the property PropertyDisjointWith
     * This method will also update the inverse property PropertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddPropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleAddPropertyDisjointWith(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddPropertyDisjointWith(this);
    }

    /**
     * Removes a value from the property PropertyDisjointWith
     * This method will also update the inverse property PropertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemovePropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleRemovePropertyDisjointWith(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemovePropertyDisjointWith(this);
    }

    /**
     * Tries to add a value to the property PropertyDisjointWith and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddPropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyAddPropertyDisjointWith(elem);
    }

    /**
     * Tries to remove a value from the property PropertyDisjointWith and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemovePropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyRemovePropertyDisjointWith(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property PropertyDisjointWith
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddPropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphAddPropertyDisjointWith(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property PropertyDisjointWith
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemovePropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphRemovePropertyDisjointWith(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Property> getAllPropertyDisjointWithAs(org.xowl.infra.lang.runtime.Property type) {
        return (Collection) Collections.unmodifiableCollection(__implPropertyDisjointWith);
    }

    @Override
    public boolean addPropertyDisjointWith(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
            throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
        if (__implPropertyDisjointWith.contains((org.xowl.infra.lang.runtime.ObjectProperty) elem))
            return false;
        doDispatchAddPropertyDisjointWith((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        return true;
    }

    @Override
    public boolean removePropertyDisjointWith(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
            throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
        if (!__implPropertyDisjointWith.contains((org.xowl.infra.lang.runtime.ObjectProperty) elem))
            return false;
        doDispatchRemovePropertyDisjointWith((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        return true;
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.ObjectProperty> getAllPropertyDisjointWithAs(org.xowl.infra.lang.runtime.ObjectProperty type) {
        return Collections.unmodifiableCollection(__implPropertyDisjointWith);
    }

    @Override
    public boolean addPropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implPropertyDisjointWith.contains(elem))
            return false;
        doDispatchAddPropertyDisjointWith(elem);
        return true;
    }

    @Override
    public boolean removePropertyDisjointWith(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implPropertyDisjointWith.contains(elem))
            return false;
        doDispatchRemovePropertyDisjointWith(elem);
        return true;
    }

    /**
     * The backing data for the property IsIrreflexive
     */
    private boolean __implIsIrreflexive;

    @Override
    public boolean getIsIrreflexive() {
        return __implIsIrreflexive;
    }

    @Override
    public void setIsIrreflexive(boolean elem) {
        __implIsIrreflexive = elem;
    }

    /**
     * The backing data for the property IsTransitive
     */
    private boolean __implIsTransitive;

    @Override
    public boolean getIsTransitive() {
        return __implIsTransitive;
    }

    @Override
    public void setIsTransitive(boolean elem) {
        __implIsTransitive = elem;
    }

    /**
     * The backing data for the property Range
     */
    private org.xowl.infra.lang.runtime.Class __implRange;

    /**
     * Adds a value to the property Range
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddRange(org.xowl.infra.lang.runtime.Class elem) {
        __implRange = elem;
    }

    /**
     * Removes a value from the property Range
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveRange(org.xowl.infra.lang.runtime.Class elem) {
        __implRange = null;
    }

    /**
     * Adds a value to the property Range
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddRange(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddRange(elem);
    }

    /**
     * Removes a value from the property Range
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveRange(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveRange(elem);
    }

    /**
     * Tries to add a value to the property Range and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddRange(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddRange(elem);
    }

    /**
     * Tries to remove a value from the property Range and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveRange(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveRange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Range
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddRange(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddRange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Range
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveRange(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveRange(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Class_OR_Datatype getRangeAs(org.xowl.infra.lang.runtime.Class_OR_Datatype type) {
        return __implRange;
    }

    @Override
    public void setRange(org.xowl.infra.lang.runtime.Class_OR_Datatype elem) {
        if (__implRange == elem)
            return;
        if (elem == null) {
            doDispatchRemoveRange(__implRange);
        } else if (__implRange == null) {
            doDispatchAddRange((org.xowl.infra.lang.runtime.Class) elem);
        } else {
            if (!(elem instanceof org.xowl.infra.lang.runtime.Class))
                throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.Class");
            doDispatchRemoveRange(__implRange);
            doDispatchAddRange((org.xowl.infra.lang.runtime.Class) elem);
        }
    }

    @Override
    public org.xowl.infra.lang.runtime.Class getRangeAs(org.xowl.infra.lang.runtime.Class type) {
        return __implRange;
    }

    @Override
    public void setRange(org.xowl.infra.lang.runtime.Class elem) {
        if (__implRange == elem)
            return;
        if (elem == null) {
            doDispatchRemoveRange(__implRange);
        } else if (__implRange == null) {
            doDispatchAddRange(elem);
        } else {
            doDispatchRemoveRange(__implRange);
            doDispatchAddRange(elem);
        }
    }

    /**
     * The backing data for the property IsSymmetric
     */
    private boolean __implIsSymmetric;

    @Override
    public boolean getIsSymmetric() {
        return __implIsSymmetric;
    }

    @Override
    public void setIsSymmetric(boolean elem) {
        __implIsSymmetric = elem;
    }

    /**
     * The backing data for the property IsAsymmetric
     */
    private boolean __implIsAsymmetric;

    @Override
    public boolean getIsAsymmetric() {
        return __implIsAsymmetric;
    }

    @Override
    public void setIsAsymmetric(boolean elem) {
        __implIsAsymmetric = elem;
    }

    /**
     * The backing data for the property IsReflexive
     */
    private boolean __implIsReflexive;

    @Override
    public boolean getIsReflexive() {
        return __implIsReflexive;
    }

    @Override
    public void setIsReflexive(boolean elem) {
        __implIsReflexive = elem;
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
     * The backing data for the property Domain
     */
    private org.xowl.infra.lang.runtime.Class __implDomain;

    /**
     * Adds a value to the property Domain
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDomain(org.xowl.infra.lang.runtime.Class elem) {
        __implDomain = elem;
    }

    /**
     * Removes a value from the property Domain
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDomain(org.xowl.infra.lang.runtime.Class elem) {
        __implDomain = null;
    }

    /**
     * Adds a value to the property Domain
     * This method will also update the inverse property DomainOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDomain(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleAddDomain(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddDomainOf(this);
    }

    /**
     * Removes a value from the property Domain
     * This method will also update the inverse property DomainOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDomain(org.xowl.infra.lang.runtime.Class elem) {
        doSimpleRemoveDomain(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveDomainOf(this);
    }

    /**
     * Tries to add a value to the property Domain and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDomain(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyAddDomain(elem);
    }

    /**
     * Tries to remove a value from the property Domain and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDomain(org.xowl.infra.lang.runtime.Class elem) {
        doPropertyRemoveDomain(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Domain
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDomain(org.xowl.infra.lang.runtime.Class elem) {
        doGraphAddDomain(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Domain
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDomain(org.xowl.infra.lang.runtime.Class elem) {
        doGraphRemoveDomain(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Class getDomain() {
        return __implDomain;
    }

    @Override
    public void setDomain(org.xowl.infra.lang.runtime.Class elem) {
        if (__implDomain == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDomain(__implDomain);
        } else if (__implDomain == null) {
            doDispatchAddDomain(elem);
        } else {
            doDispatchRemoveDomain(__implDomain);
            doDispatchAddDomain(elem);
        }
    }

    /**
     * The backing data for the property InverseOf
     */
    private org.xowl.infra.lang.runtime.ObjectProperty __implInverseOf;

    /**
     * Adds a value to the property InverseOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implInverseOf = elem;
    }

    /**
     * Removes a value from the property InverseOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implInverseOf = null;
    }

    /**
     * Adds a value to the property InverseOf
     * This method will also update the inverse property InverseOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleAddInverseOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddInverseOf(this);
    }

    /**
     * Removes a value from the property InverseOf
     * This method will also update the inverse property InverseOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleRemoveInverseOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveInverseOf(this);
    }

    /**
     * Tries to add a value to the property InverseOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyAddInverseOf(elem);
    }

    /**
     * Tries to remove a value from the property InverseOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyRemoveInverseOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property InverseOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphAddInverseOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property InverseOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphRemoveInverseOf(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.ObjectProperty getInverseOf() {
        return __implInverseOf;
    }

    @Override
    public void setInverseOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (__implInverseOf == elem)
            return;
        if (elem == null) {
            doDispatchRemoveInverseOf(__implInverseOf);
        } else if (__implInverseOf == null) {
            doDispatchAddInverseOf(elem);
        } else {
            doDispatchRemoveInverseOf(__implInverseOf);
            doDispatchAddInverseOf(elem);
        }
    }

    /**
     * The backing data for the property SubPropertyOf
     */
    private List<org.xowl.infra.lang.runtime.ObjectProperty> __implSubPropertyOf;

    /**
     * Adds a value to the property SubPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implSubPropertyOf.add(elem);
    }

    /**
     * Removes a value from the property SubPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implSubPropertyOf.remove(elem);
    }

    /**
     * Adds a value to the property SubPropertyOf
     * This method will also update the inverse property SuperPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleAddSubPropertyOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddSuperPropertyOf(this);
    }

    /**
     * Removes a value from the property SubPropertyOf
     * This method will also update the inverse property SuperPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleRemoveSubPropertyOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveSuperPropertyOf(this);
    }

    /**
     * Tries to add a value to the property SubPropertyOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyAddSubPropertyOf(elem);
    }

    /**
     * Tries to remove a value from the property SubPropertyOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyRemoveSubPropertyOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SubPropertyOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphAddSubPropertyOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SubPropertyOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphRemoveSubPropertyOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Property> getAllSubPropertyOfAs(org.xowl.infra.lang.runtime.Property type) {
        return (Collection) Collections.unmodifiableCollection(__implSubPropertyOf);
    }

    @Override
    public boolean addSubPropertyOf(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
            throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
        if (__implSubPropertyOf.contains((org.xowl.infra.lang.runtime.ObjectProperty) elem))
            return false;
        doDispatchAddSubPropertyOf((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        return true;
    }

    @Override
    public boolean removeSubPropertyOf(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
            throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
        if (!__implSubPropertyOf.contains((org.xowl.infra.lang.runtime.ObjectProperty) elem))
            return false;
        doDispatchRemoveSubPropertyOf((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        return true;
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.ObjectProperty> getAllSubPropertyOfAs(org.xowl.infra.lang.runtime.ObjectProperty type) {
        return Collections.unmodifiableCollection(__implSubPropertyOf);
    }

    @Override
    public boolean addSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSubPropertyOf.contains(elem))
            return false;
        doDispatchAddSubPropertyOf(elem);
        return true;
    }

    @Override
    public boolean removeSubPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSubPropertyOf.contains(elem))
            return false;
        doDispatchRemoveSubPropertyOf(elem);
        return true;
    }

    /**
     * The backing data for the property SuperPropertyOf
     */
    private List<org.xowl.infra.lang.runtime.ObjectProperty> __implSuperPropertyOf;

    /**
     * Adds a value to the property SuperPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implSuperPropertyOf.add(elem);
    }

    /**
     * Removes a value from the property SuperPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implSuperPropertyOf.remove(elem);
    }

    /**
     * Adds a value to the property SuperPropertyOf
     * This method will also update the inverse property SubPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleAddSuperPropertyOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddSubPropertyOf(this);
    }

    /**
     * Removes a value from the property SuperPropertyOf
     * This method will also update the inverse property SubPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleRemoveSuperPropertyOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveSubPropertyOf(this);
    }

    /**
     * Tries to add a value to the property SuperPropertyOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyAddSuperPropertyOf(elem);
    }

    /**
     * Tries to remove a value from the property SuperPropertyOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyRemoveSuperPropertyOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperPropertyOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphAddSuperPropertyOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperPropertyOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphRemoveSuperPropertyOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Property> getAllSuperPropertyOfAs(org.xowl.infra.lang.runtime.Property type) {
        return (Collection) Collections.unmodifiableCollection(__implSuperPropertyOf);
    }

    @Override
    public boolean addSuperPropertyOf(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
            throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
        if (__implSuperPropertyOf.contains((org.xowl.infra.lang.runtime.ObjectProperty) elem))
            return false;
        doDispatchAddSuperPropertyOf((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        return true;
    }

    @Override
    public boolean removeSuperPropertyOf(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
            throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
        if (!__implSuperPropertyOf.contains((org.xowl.infra.lang.runtime.ObjectProperty) elem))
            return false;
        doDispatchRemoveSuperPropertyOf((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        return true;
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.ObjectProperty> getAllSuperPropertyOfAs(org.xowl.infra.lang.runtime.ObjectProperty type) {
        return Collections.unmodifiableCollection(__implSuperPropertyOf);
    }

    @Override
    public boolean addSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSuperPropertyOf.contains(elem))
            return false;
        doDispatchAddSuperPropertyOf(elem);
        return true;
    }

    @Override
    public boolean removeSuperPropertyOf(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSuperPropertyOf.contains(elem))
            return false;
        doDispatchRemoveSuperPropertyOf(elem);
        return true;
    }

    /**
     * The backing data for the property Chains
     */
    private List<org.xowl.infra.lang.runtime.ObjectProperty> __implChains;

    /**
     * Adds a value to the property Chains
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implChains.add(elem);
    }

    /**
     * Removes a value from the property Chains
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implChains.remove(elem);
    }

    /**
     * Adds a value to the property Chains
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleAddChains(elem);
    }

    /**
     * Removes a value from the property Chains
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleRemoveChains(elem);
    }

    /**
     * Tries to add a value to the property Chains and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyAddChains(elem);
    }

    /**
     * Tries to remove a value from the property Chains and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyRemoveChains(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Chains
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphAddChains(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Chains
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphRemoveChains(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.ObjectProperty> getAllChains() {
        return Collections.unmodifiableCollection(__implChains);
    }

    @Override
    public boolean addChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implChains.contains(elem))
            return false;
        doDispatchAddChains(elem);
        return true;
    }

    @Override
    public boolean removeChains(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implChains.contains(elem))
            return false;
        doDispatchRemoveChains(elem);
        return true;
    }

    /**
     * The backing data for the property IsFunctional
     */
    private boolean __implIsFunctional;

    @Override
    public boolean getIsFunctional() {
        return __implIsFunctional;
    }

    @Override
    public void setIsFunctional(boolean elem) {
        __implIsFunctional = elem;
    }

    /**
     * The backing data for the property PropertyEquivalentTo
     */
    private List<org.xowl.infra.lang.runtime.ObjectProperty> __implPropertyEquivalentTo;

    /**
     * Adds a value to the property PropertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddPropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implPropertyEquivalentTo.add(elem);
    }

    /**
     * Removes a value from the property PropertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemovePropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        __implPropertyEquivalentTo.remove(elem);
    }

    /**
     * Adds a value to the property PropertyEquivalentTo
     * This method will also update the inverse property PropertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddPropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleAddPropertyEquivalentTo(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddPropertyEquivalentTo(this);
    }

    /**
     * Removes a value from the property PropertyEquivalentTo
     * This method will also update the inverse property PropertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemovePropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doSimpleRemovePropertyEquivalentTo(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemovePropertyEquivalentTo(this);
    }

    /**
     * Tries to add a value to the property PropertyEquivalentTo and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddPropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyAddPropertyEquivalentTo(elem);
    }

    /**
     * Tries to remove a value from the property PropertyEquivalentTo and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemovePropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doPropertyRemovePropertyEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property PropertyEquivalentTo
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddPropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphAddPropertyEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property PropertyEquivalentTo
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemovePropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        doGraphRemovePropertyEquivalentTo(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Property> getAllPropertyEquivalentToAs(org.xowl.infra.lang.runtime.Property type) {
        return (Collection) Collections.unmodifiableCollection(__implPropertyEquivalentTo);
    }

    @Override
    public boolean addPropertyEquivalentTo(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
            throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
        if (__implPropertyEquivalentTo.contains((org.xowl.infra.lang.runtime.ObjectProperty) elem))
            return false;
        doDispatchAddPropertyEquivalentTo((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        return true;
    }

    @Override
    public boolean removePropertyEquivalentTo(org.xowl.infra.lang.runtime.Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof org.xowl.infra.lang.runtime.ObjectProperty))
            throw new IllegalArgumentException("Expected type org.xowl.infra.lang.runtime.ObjectProperty");
        if (!__implPropertyEquivalentTo.contains((org.xowl.infra.lang.runtime.ObjectProperty) elem))
            return false;
        doDispatchRemovePropertyEquivalentTo((org.xowl.infra.lang.runtime.ObjectProperty) elem);
        return true;
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.ObjectProperty> getAllPropertyEquivalentToAs(org.xowl.infra.lang.runtime.ObjectProperty type) {
        return Collections.unmodifiableCollection(__implPropertyEquivalentTo);
    }

    @Override
    public boolean addPropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implPropertyEquivalentTo.contains(elem))
            return false;
        doDispatchAddPropertyEquivalentTo(elem);
        return true;
    }

    @Override
    public boolean removePropertyEquivalentTo(org.xowl.infra.lang.runtime.ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implPropertyEquivalentTo.contains(elem))
            return false;
        doDispatchRemovePropertyEquivalentTo(elem);
        return true;
    }

    /**
     * Constructor for the implementation of ObjectProperty
     */
    public RuntimeObjectPropertyImpl() {
        this.__implIsInverseFunctional = false;
        this.__implPropertyDisjointWith = new ArrayList<>();
        this.__implIsIrreflexive = false;
        this.__implIsTransitive = false;
        this.__implRange = null;
        this.__implIsSymmetric = false;
        this.__implIsAsymmetric = false;
        this.__implIsReflexive = false;
        this.__implInterpretationOf = null;
        this.__implDomain = null;
        this.__implInverseOf = null;
        this.__implSubPropertyOf = new ArrayList<>();
        this.__implSuperPropertyOf = new ArrayList<>();
        this.__implChains = new ArrayList<>();
        this.__implIsFunctional = false;
        this.__implPropertyEquivalentTo = new ArrayList<>();
    }
}
