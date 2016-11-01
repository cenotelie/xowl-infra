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
 * The default implementation for ObjectProperty
 * Original OWL class is http://xowl.org/infra/lang/runtime#ObjectProperty
 *
 * @author xOWL code generator
 */
public class RuntimeObjectPropertyImpl implements ObjectProperty {
    /**
     * The backing data for the property Chains
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#chains
     */
    private List<ObjectProperty> __implChains;

    /**
     * Adds a value to the property Chains
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddChains(ObjectProperty elem) {
        __implChains.add(elem);
    }

    /**
     * Removes a value from the property Chains
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveChains(ObjectProperty elem) {
        __implChains.remove(elem);
    }

    /**
     * Adds a value to the property Chains
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddChains(ObjectProperty elem) {
        doSimpleAddChains(elem);
    }

    /**
     * Removes a value from the property Chains
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveChains(ObjectProperty elem) {
        doSimpleRemoveChains(elem);
    }

    /**
     * Tries to add a value to the property Chains and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddChains(ObjectProperty elem) {
        doPropertyAddChains(elem);
    }

    /**
     * Tries to remove a value from the property Chains and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveChains(ObjectProperty elem) {
        doPropertyRemoveChains(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Chains
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddChains(ObjectProperty elem) {
        doGraphAddChains(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Chains
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#chains
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveChains(ObjectProperty elem) {
        doGraphRemoveChains(elem);
    }

    @Override
    public Collection<ObjectProperty> getAllChains() {
        return Collections.unmodifiableCollection(__implChains);
    }

    @Override
    public boolean addChains(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implChains.contains(elem))
            return false;
        doDispatchAddChains(elem);
        return true;
    }

    @Override
    public boolean removeChains(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implChains.contains(elem))
            return false;
        doDispatchRemoveChains(elem);
        return true;
    }

    /**
     * The backing data for the property Domain
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#domain
     */
    private Class __implDomain;

    /**
     * Adds a value to the property Domain
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDomain(Class elem) {
        __implDomain = elem;
    }

    /**
     * Removes a value from the property Domain
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDomain(Class elem) {
        __implDomain = null;
    }

    /**
     * Adds a value to the property Domain
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     * This method will also update the inverse property DomainOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDomain(Class elem) {
        doSimpleAddDomain(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddDomainOf(this);
    }

    /**
     * Removes a value from the property Domain
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     * This method will also update the inverse property DomainOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDomain(Class elem) {
        doSimpleRemoveDomain(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveDomainOf(this);
    }

    /**
     * Tries to add a value to the property Domain and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDomain(Class elem) {
        doPropertyAddDomain(elem);
    }

    /**
     * Tries to remove a value from the property Domain and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDomain(Class elem) {
        doPropertyRemoveDomain(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Domain
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDomain(Class elem) {
        doGraphAddDomain(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Domain
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#domain
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDomain(Class elem) {
        doGraphRemoveDomain(elem);
    }

    @Override
    public Class getDomain() {
        return __implDomain;
    }

    @Override
    public void setDomain(Class elem) {
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
     * The backing data for the property InverseOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#inverseOf
     */
    private ObjectProperty __implInverseOf;

    /**
     * Adds a value to the property InverseOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInverseOf(ObjectProperty elem) {
        __implInverseOf = elem;
    }

    /**
     * Removes a value from the property InverseOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInverseOf(ObjectProperty elem) {
        __implInverseOf = null;
    }

    /**
     * Adds a value to the property InverseOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     * This method will also update the inverse property InverseOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInverseOf(ObjectProperty elem) {
        doSimpleAddInverseOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddInverseOf(this);
    }

    /**
     * Removes a value from the property InverseOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     * This method will also update the inverse property InverseOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInverseOf(ObjectProperty elem) {
        doSimpleRemoveInverseOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveInverseOf(this);
    }

    /**
     * Tries to add a value to the property InverseOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInverseOf(ObjectProperty elem) {
        doPropertyAddInverseOf(elem);
    }

    /**
     * Tries to remove a value from the property InverseOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInverseOf(ObjectProperty elem) {
        doPropertyRemoveInverseOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property InverseOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInverseOf(ObjectProperty elem) {
        doGraphAddInverseOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property InverseOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#inverseOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInverseOf(ObjectProperty elem) {
        doGraphRemoveInverseOf(elem);
    }

    @Override
    public ObjectProperty getInverseOf() {
        return __implInverseOf;
    }

    @Override
    public void setInverseOf(ObjectProperty elem) {
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
     * The backing data for the property IsAsymmetric
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#isAsymmetric
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
     * The backing data for the property IsFunctional
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#isFunctional
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
     * The backing data for the property IsInverseFunctional
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#isInverseFunctional
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
     * The backing data for the property IsIrreflexive
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#isIrreflexive
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
     * The backing data for the property IsReflexive
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#isReflexive
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
     * The backing data for the property IsSymmetric
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#isSymmetric
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
     * The backing data for the property IsTransitive
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#isTransitive
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
     * The backing data for the property PropertyDisjointWith
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#propertyDisjointWith
     */
    private List<ObjectProperty> __implPropertyDisjointWith;

    /**
     * Adds a value to the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddPropertyDisjointWith(ObjectProperty elem) {
        __implPropertyDisjointWith.add(elem);
    }

    /**
     * Removes a value from the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemovePropertyDisjointWith(ObjectProperty elem) {
        __implPropertyDisjointWith.remove(elem);
    }

    /**
     * Adds a value to the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     * This method will also update the inverse property PropertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddPropertyDisjointWith(ObjectProperty elem) {
        doSimpleAddPropertyDisjointWith(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddPropertyDisjointWith(this);
    }

    /**
     * Removes a value from the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     * This method will also update the inverse property PropertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemovePropertyDisjointWith(ObjectProperty elem) {
        doSimpleRemovePropertyDisjointWith(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemovePropertyDisjointWith(this);
    }

    /**
     * Tries to add a value to the property PropertyDisjointWith and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddPropertyDisjointWith(ObjectProperty elem) {
        doPropertyAddPropertyDisjointWith(elem);
    }

    /**
     * Tries to remove a value from the property PropertyDisjointWith and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemovePropertyDisjointWith(ObjectProperty elem) {
        doPropertyRemovePropertyDisjointWith(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property PropertyDisjointWith
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddPropertyDisjointWith(ObjectProperty elem) {
        doGraphAddPropertyDisjointWith(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property PropertyDisjointWith
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemovePropertyDisjointWith(ObjectProperty elem) {
        doGraphRemovePropertyDisjointWith(elem);
    }

    @Override
    public Collection<Property> getAllPropertyDisjointWithAs(Property type) {
        return (Collection) Collections.unmodifiableCollection(__implPropertyDisjointWith);
    }

    @Override
    public boolean addPropertyDisjointWith(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof ObjectProperty))
            throw new IllegalArgumentException("Expected type ObjectProperty");
        if (__implPropertyDisjointWith.contains((ObjectProperty) elem))
            return false;
        doDispatchAddPropertyDisjointWith((ObjectProperty) elem);
        return true;
    }

    @Override
    public boolean removePropertyDisjointWith(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof ObjectProperty))
            throw new IllegalArgumentException("Expected type ObjectProperty");
        if (!__implPropertyDisjointWith.contains((ObjectProperty) elem))
            return false;
        doDispatchRemovePropertyDisjointWith((ObjectProperty) elem);
        return true;
    }

    @Override
    public Collection<ObjectProperty> getAllPropertyDisjointWithAs(ObjectProperty type) {
        return Collections.unmodifiableCollection(__implPropertyDisjointWith);
    }

    @Override
    public boolean addPropertyDisjointWith(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implPropertyDisjointWith.contains(elem))
            return false;
        doDispatchAddPropertyDisjointWith(elem);
        return true;
    }

    @Override
    public boolean removePropertyDisjointWith(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implPropertyDisjointWith.contains(elem))
            return false;
        doDispatchRemovePropertyDisjointWith(elem);
        return true;
    }

    /**
     * The backing data for the property PropertyEquivalentTo
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     */
    private List<ObjectProperty> __implPropertyEquivalentTo;

    /**
     * Adds a value to the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddPropertyEquivalentTo(ObjectProperty elem) {
        __implPropertyEquivalentTo.add(elem);
    }

    /**
     * Removes a value from the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemovePropertyEquivalentTo(ObjectProperty elem) {
        __implPropertyEquivalentTo.remove(elem);
    }

    /**
     * Adds a value to the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     * This method will also update the inverse property PropertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddPropertyEquivalentTo(ObjectProperty elem) {
        doSimpleAddPropertyEquivalentTo(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddPropertyEquivalentTo(this);
    }

    /**
     * Removes a value from the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     * This method will also update the inverse property PropertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemovePropertyEquivalentTo(ObjectProperty elem) {
        doSimpleRemovePropertyEquivalentTo(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemovePropertyEquivalentTo(this);
    }

    /**
     * Tries to add a value to the property PropertyEquivalentTo and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddPropertyEquivalentTo(ObjectProperty elem) {
        doPropertyAddPropertyEquivalentTo(elem);
    }

    /**
     * Tries to remove a value from the property PropertyEquivalentTo and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemovePropertyEquivalentTo(ObjectProperty elem) {
        doPropertyRemovePropertyEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property PropertyEquivalentTo
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddPropertyEquivalentTo(ObjectProperty elem) {
        doGraphAddPropertyEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property PropertyEquivalentTo
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemovePropertyEquivalentTo(ObjectProperty elem) {
        doGraphRemovePropertyEquivalentTo(elem);
    }

    @Override
    public Collection<Property> getAllPropertyEquivalentToAs(Property type) {
        return (Collection) Collections.unmodifiableCollection(__implPropertyEquivalentTo);
    }

    @Override
    public boolean addPropertyEquivalentTo(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof ObjectProperty))
            throw new IllegalArgumentException("Expected type ObjectProperty");
        if (__implPropertyEquivalentTo.contains((ObjectProperty) elem))
            return false;
        doDispatchAddPropertyEquivalentTo((ObjectProperty) elem);
        return true;
    }

    @Override
    public boolean removePropertyEquivalentTo(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof ObjectProperty))
            throw new IllegalArgumentException("Expected type ObjectProperty");
        if (!__implPropertyEquivalentTo.contains((ObjectProperty) elem))
            return false;
        doDispatchRemovePropertyEquivalentTo((ObjectProperty) elem);
        return true;
    }

    @Override
    public Collection<ObjectProperty> getAllPropertyEquivalentToAs(ObjectProperty type) {
        return Collections.unmodifiableCollection(__implPropertyEquivalentTo);
    }

    @Override
    public boolean addPropertyEquivalentTo(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implPropertyEquivalentTo.contains(elem))
            return false;
        doDispatchAddPropertyEquivalentTo(elem);
        return true;
    }

    @Override
    public boolean removePropertyEquivalentTo(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implPropertyEquivalentTo.contains(elem))
            return false;
        doDispatchRemovePropertyEquivalentTo(elem);
        return true;
    }

    /**
     * The backing data for the property Range
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#range
     */
    private Class __implRange;

    /**
     * Adds a value to the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddRange(Class elem) {
        __implRange = elem;
    }

    /**
     * Removes a value from the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveRange(Class elem) {
        __implRange = null;
    }

    /**
     * Adds a value to the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddRange(Class elem) {
        doSimpleAddRange(elem);
    }

    /**
     * Removes a value from the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveRange(Class elem) {
        doSimpleRemoveRange(elem);
    }

    /**
     * Tries to add a value to the property Range and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddRange(Class elem) {
        doPropertyAddRange(elem);
    }

    /**
     * Tries to remove a value from the property Range and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveRange(Class elem) {
        doPropertyRemoveRange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Range
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddRange(Class elem) {
        doGraphAddRange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Range
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveRange(Class elem) {
        doGraphRemoveRange(elem);
    }

    @Override
    public RangeOfRange getRangeAs(RangeOfRange type) {
        return __implRange;
    }

    @Override
    public void setRange(RangeOfRange elem) {
        if (__implRange == elem)
            return;
        if (elem == null) {
            doDispatchRemoveRange(__implRange);
        } else if (__implRange == null) {
            doDispatchAddRange((Class) elem);
        } else {
            if (!(elem instanceof Class))
                throw new IllegalArgumentException("Expected type Class");
            doDispatchRemoveRange(__implRange);
            doDispatchAddRange((Class) elem);
        }
    }

    @Override
    public Class getRangeAs(Class type) {
        return __implRange;
    }

    @Override
    public void setRange(Class elem) {
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
     * The backing data for the property SubPropertyOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#subPropertyOf
     */
    private List<ObjectProperty> __implSubPropertyOf;

    /**
     * Adds a value to the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSubPropertyOf(ObjectProperty elem) {
        __implSubPropertyOf.add(elem);
    }

    /**
     * Removes a value from the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSubPropertyOf(ObjectProperty elem) {
        __implSubPropertyOf.remove(elem);
    }

    /**
     * Adds a value to the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     * This method will also update the inverse property SuperPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSubPropertyOf(ObjectProperty elem) {
        doSimpleAddSubPropertyOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddSuperPropertyOf(this);
    }

    /**
     * Removes a value from the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     * This method will also update the inverse property SuperPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSubPropertyOf(ObjectProperty elem) {
        doSimpleRemoveSubPropertyOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveSuperPropertyOf(this);
    }

    /**
     * Tries to add a value to the property SubPropertyOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSubPropertyOf(ObjectProperty elem) {
        doPropertyAddSubPropertyOf(elem);
    }

    /**
     * Tries to remove a value from the property SubPropertyOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSubPropertyOf(ObjectProperty elem) {
        doPropertyRemoveSubPropertyOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SubPropertyOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSubPropertyOf(ObjectProperty elem) {
        doGraphAddSubPropertyOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SubPropertyOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSubPropertyOf(ObjectProperty elem) {
        doGraphRemoveSubPropertyOf(elem);
    }

    @Override
    public Collection<Property> getAllSubPropertyOfAs(Property type) {
        return (Collection) Collections.unmodifiableCollection(__implSubPropertyOf);
    }

    @Override
    public boolean addSubPropertyOf(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof ObjectProperty))
            throw new IllegalArgumentException("Expected type ObjectProperty");
        if (__implSubPropertyOf.contains((ObjectProperty) elem))
            return false;
        doDispatchAddSubPropertyOf((ObjectProperty) elem);
        return true;
    }

    @Override
    public boolean removeSubPropertyOf(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof ObjectProperty))
            throw new IllegalArgumentException("Expected type ObjectProperty");
        if (!__implSubPropertyOf.contains((ObjectProperty) elem))
            return false;
        doDispatchRemoveSubPropertyOf((ObjectProperty) elem);
        return true;
    }

    @Override
    public Collection<ObjectProperty> getAllSubPropertyOfAs(ObjectProperty type) {
        return Collections.unmodifiableCollection(__implSubPropertyOf);
    }

    @Override
    public boolean addSubPropertyOf(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSubPropertyOf.contains(elem))
            return false;
        doDispatchAddSubPropertyOf(elem);
        return true;
    }

    @Override
    public boolean removeSubPropertyOf(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSubPropertyOf.contains(elem))
            return false;
        doDispatchRemoveSubPropertyOf(elem);
        return true;
    }

    /**
     * The backing data for the property SuperPropertyOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#superPropertyOf
     */
    private List<ObjectProperty> __implSuperPropertyOf;

    /**
     * Adds a value to the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperPropertyOf(ObjectProperty elem) {
        __implSuperPropertyOf.add(elem);
    }

    /**
     * Removes a value from the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperPropertyOf(ObjectProperty elem) {
        __implSuperPropertyOf.remove(elem);
    }

    /**
     * Adds a value to the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     * This method will also update the inverse property SubPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperPropertyOf(ObjectProperty elem) {
        doSimpleAddSuperPropertyOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddSubPropertyOf(this);
    }

    /**
     * Removes a value from the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     * This method will also update the inverse property SubPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperPropertyOf(ObjectProperty elem) {
        doSimpleRemoveSuperPropertyOf(elem);
        if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveSubPropertyOf(this);
    }

    /**
     * Tries to add a value to the property SuperPropertyOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperPropertyOf(ObjectProperty elem) {
        doPropertyAddSuperPropertyOf(elem);
    }

    /**
     * Tries to remove a value from the property SuperPropertyOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperPropertyOf(ObjectProperty elem) {
        doPropertyRemoveSuperPropertyOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperPropertyOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperPropertyOf(ObjectProperty elem) {
        doGraphAddSuperPropertyOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperPropertyOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperPropertyOf(ObjectProperty elem) {
        doGraphRemoveSuperPropertyOf(elem);
    }

    @Override
    public Collection<Property> getAllSuperPropertyOfAs(Property type) {
        return (Collection) Collections.unmodifiableCollection(__implSuperPropertyOf);
    }

    @Override
    public boolean addSuperPropertyOf(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof ObjectProperty))
            throw new IllegalArgumentException("Expected type ObjectProperty");
        if (__implSuperPropertyOf.contains((ObjectProperty) elem))
            return false;
        doDispatchAddSuperPropertyOf((ObjectProperty) elem);
        return true;
    }

    @Override
    public boolean removeSuperPropertyOf(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof ObjectProperty))
            throw new IllegalArgumentException("Expected type ObjectProperty");
        if (!__implSuperPropertyOf.contains((ObjectProperty) elem))
            return false;
        doDispatchRemoveSuperPropertyOf((ObjectProperty) elem);
        return true;
    }

    @Override
    public Collection<ObjectProperty> getAllSuperPropertyOfAs(ObjectProperty type) {
        return Collections.unmodifiableCollection(__implSuperPropertyOf);
    }

    @Override
    public boolean addSuperPropertyOf(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSuperPropertyOf.contains(elem))
            return false;
        doDispatchAddSuperPropertyOf(elem);
        return true;
    }

    @Override
    public boolean removeSuperPropertyOf(ObjectProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSuperPropertyOf.contains(elem))
            return false;
        doDispatchRemoveSuperPropertyOf(elem);
        return true;
    }

    /**
     * Constructor for the implementation of ObjectProperty
     */
    public RuntimeObjectPropertyImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#chains
        this.__implChains = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#domain
        this.__implDomain = null;
        // initialize property http://xowl.org/infra/lang/runtime#interpretationOf
        this.__implInterpretationOf = null;
        // initialize property http://xowl.org/infra/lang/runtime#inverseOf
        this.__implInverseOf = null;
        // initialize property http://xowl.org/infra/lang/runtime#isAsymmetric
        this.__implIsAsymmetric = false;
        // initialize property http://xowl.org/infra/lang/runtime#isFunctional
        this.__implIsFunctional = false;
        // initialize property http://xowl.org/infra/lang/runtime#isInverseFunctional
        this.__implIsInverseFunctional = false;
        // initialize property http://xowl.org/infra/lang/runtime#isIrreflexive
        this.__implIsIrreflexive = false;
        // initialize property http://xowl.org/infra/lang/runtime#isReflexive
        this.__implIsReflexive = false;
        // initialize property http://xowl.org/infra/lang/runtime#isSymmetric
        this.__implIsSymmetric = false;
        // initialize property http://xowl.org/infra/lang/runtime#isTransitive
        this.__implIsTransitive = false;
        // initialize property http://xowl.org/infra/lang/runtime#propertyDisjointWith
        this.__implPropertyDisjointWith = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#propertyEquivalentTo
        this.__implPropertyEquivalentTo = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#range
        this.__implRange = null;
        // initialize property http://xowl.org/infra/lang/runtime#subPropertyOf
        this.__implSubPropertyOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#superPropertyOf
        this.__implSuperPropertyOf = new ArrayList<>();
    }
}
