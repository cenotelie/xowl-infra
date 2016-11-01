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
 * The default implementation for DataProperty
 * Original OWL class is http://xowl.org/infra/lang/runtime#DataProperty
 *
 * @author xOWL code generator
 */
public class RuntimeDataPropertyImpl implements DataProperty {
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
     * The backing data for the property PropertyDisjointWith
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#propertyDisjointWith
     */
    private List<DataProperty> __implPropertyDisjointWith;

    /**
     * Adds a value to the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddPropertyDisjointWith(DataProperty elem) {
        __implPropertyDisjointWith.add(elem);
    }

    /**
     * Removes a value from the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemovePropertyDisjointWith(DataProperty elem) {
        __implPropertyDisjointWith.remove(elem);
    }

    /**
     * Adds a value to the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     * This method will also update the inverse property PropertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddPropertyDisjointWith(DataProperty elem) {
        doSimpleAddPropertyDisjointWith(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleAddPropertyDisjointWith(this);
    }

    /**
     * Removes a value from the property PropertyDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     * This method will also update the inverse property PropertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemovePropertyDisjointWith(DataProperty elem) {
        doSimpleRemovePropertyDisjointWith(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleRemovePropertyDisjointWith(this);
    }

    /**
     * Tries to add a value to the property PropertyDisjointWith and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddPropertyDisjointWith(DataProperty elem) {
        doPropertyAddPropertyDisjointWith(elem);
    }

    /**
     * Tries to remove a value from the property PropertyDisjointWith and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemovePropertyDisjointWith(DataProperty elem) {
        doPropertyRemovePropertyDisjointWith(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property PropertyDisjointWith
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddPropertyDisjointWith(DataProperty elem) {
        doGraphAddPropertyDisjointWith(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property PropertyDisjointWith
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemovePropertyDisjointWith(DataProperty elem) {
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
        if (!(elem instanceof DataProperty))
            throw new IllegalArgumentException("Expected type DataProperty");
        if (__implPropertyDisjointWith.contains((DataProperty) elem))
            return false;
        doDispatchAddPropertyDisjointWith((DataProperty) elem);
        return true;
    }

    @Override
    public boolean removePropertyDisjointWith(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof DataProperty))
            throw new IllegalArgumentException("Expected type DataProperty");
        if (!__implPropertyDisjointWith.contains((DataProperty) elem))
            return false;
        doDispatchRemovePropertyDisjointWith((DataProperty) elem);
        return true;
    }

    @Override
    public Collection<DataProperty> getAllPropertyDisjointWithAs(DataProperty type) {
        return Collections.unmodifiableCollection(__implPropertyDisjointWith);
    }

    @Override
    public boolean addPropertyDisjointWith(DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implPropertyDisjointWith.contains(elem))
            return false;
        doDispatchAddPropertyDisjointWith(elem);
        return true;
    }

    @Override
    public boolean removePropertyDisjointWith(DataProperty elem) {
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
    private List<DataProperty> __implPropertyEquivalentTo;

    /**
     * Adds a value to the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddPropertyEquivalentTo(DataProperty elem) {
        __implPropertyEquivalentTo.add(elem);
    }

    /**
     * Removes a value from the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemovePropertyEquivalentTo(DataProperty elem) {
        __implPropertyEquivalentTo.remove(elem);
    }

    /**
     * Adds a value to the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     * This method will also update the inverse property PropertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddPropertyEquivalentTo(DataProperty elem) {
        doSimpleAddPropertyEquivalentTo(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleAddPropertyEquivalentTo(this);
    }

    /**
     * Removes a value from the property PropertyEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     * This method will also update the inverse property PropertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemovePropertyEquivalentTo(DataProperty elem) {
        doSimpleRemovePropertyEquivalentTo(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleRemovePropertyEquivalentTo(this);
    }

    /**
     * Tries to add a value to the property PropertyEquivalentTo and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddPropertyEquivalentTo(DataProperty elem) {
        doPropertyAddPropertyEquivalentTo(elem);
    }

    /**
     * Tries to remove a value from the property PropertyEquivalentTo and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemovePropertyEquivalentTo(DataProperty elem) {
        doPropertyRemovePropertyEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property PropertyEquivalentTo
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddPropertyEquivalentTo(DataProperty elem) {
        doGraphAddPropertyEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property PropertyEquivalentTo
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#propertyEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemovePropertyEquivalentTo(DataProperty elem) {
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
        if (!(elem instanceof DataProperty))
            throw new IllegalArgumentException("Expected type DataProperty");
        if (__implPropertyEquivalentTo.contains((DataProperty) elem))
            return false;
        doDispatchAddPropertyEquivalentTo((DataProperty) elem);
        return true;
    }

    @Override
    public boolean removePropertyEquivalentTo(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof DataProperty))
            throw new IllegalArgumentException("Expected type DataProperty");
        if (!__implPropertyEquivalentTo.contains((DataProperty) elem))
            return false;
        doDispatchRemovePropertyEquivalentTo((DataProperty) elem);
        return true;
    }

    @Override
    public Collection<DataProperty> getAllPropertyEquivalentToAs(DataProperty type) {
        return Collections.unmodifiableCollection(__implPropertyEquivalentTo);
    }

    @Override
    public boolean addPropertyEquivalentTo(DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implPropertyEquivalentTo.contains(elem))
            return false;
        doDispatchAddPropertyEquivalentTo(elem);
        return true;
    }

    @Override
    public boolean removePropertyEquivalentTo(DataProperty elem) {
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
    private Datatype __implRange;

    /**
     * Adds a value to the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddRange(Datatype elem) {
        __implRange = elem;
    }

    /**
     * Removes a value from the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveRange(Datatype elem) {
        __implRange = null;
    }

    /**
     * Adds a value to the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddRange(Datatype elem) {
        doSimpleAddRange(elem);
    }

    /**
     * Removes a value from the property Range
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveRange(Datatype elem) {
        doSimpleRemoveRange(elem);
    }

    /**
     * Tries to add a value to the property Range and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddRange(Datatype elem) {
        doPropertyAddRange(elem);
    }

    /**
     * Tries to remove a value from the property Range and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveRange(Datatype elem) {
        doPropertyRemoveRange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Range
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddRange(Datatype elem) {
        doGraphAddRange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Range
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#range
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveRange(Datatype elem) {
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
            doDispatchAddRange((Datatype) elem);
        } else {
            if (!(elem instanceof Datatype))
                throw new IllegalArgumentException("Expected type Datatype");
            doDispatchRemoveRange(__implRange);
            doDispatchAddRange((Datatype) elem);
        }
    }

    @Override
    public Datatype getRangeAs(Datatype type) {
        return __implRange;
    }

    @Override
    public void setRange(Datatype elem) {
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
    private List<DataProperty> __implSubPropertyOf;

    /**
     * Adds a value to the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSubPropertyOf(DataProperty elem) {
        __implSubPropertyOf.add(elem);
    }

    /**
     * Removes a value from the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSubPropertyOf(DataProperty elem) {
        __implSubPropertyOf.remove(elem);
    }

    /**
     * Adds a value to the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     * This method will also update the inverse property SuperPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSubPropertyOf(DataProperty elem) {
        doSimpleAddSubPropertyOf(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleAddSuperPropertyOf(this);
    }

    /**
     * Removes a value from the property SubPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     * This method will also update the inverse property SuperPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSubPropertyOf(DataProperty elem) {
        doSimpleRemoveSubPropertyOf(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleRemoveSuperPropertyOf(this);
    }

    /**
     * Tries to add a value to the property SubPropertyOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSubPropertyOf(DataProperty elem) {
        doPropertyAddSubPropertyOf(elem);
    }

    /**
     * Tries to remove a value from the property SubPropertyOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSubPropertyOf(DataProperty elem) {
        doPropertyRemoveSubPropertyOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SubPropertyOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSubPropertyOf(DataProperty elem) {
        doGraphAddSubPropertyOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SubPropertyOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#subPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSubPropertyOf(DataProperty elem) {
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
        if (!(elem instanceof DataProperty))
            throw new IllegalArgumentException("Expected type DataProperty");
        if (__implSubPropertyOf.contains((DataProperty) elem))
            return false;
        doDispatchAddSubPropertyOf((DataProperty) elem);
        return true;
    }

    @Override
    public boolean removeSubPropertyOf(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof DataProperty))
            throw new IllegalArgumentException("Expected type DataProperty");
        if (!__implSubPropertyOf.contains((DataProperty) elem))
            return false;
        doDispatchRemoveSubPropertyOf((DataProperty) elem);
        return true;
    }

    @Override
    public Collection<DataProperty> getAllSubPropertyOfAs(DataProperty type) {
        return Collections.unmodifiableCollection(__implSubPropertyOf);
    }

    @Override
    public boolean addSubPropertyOf(DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSubPropertyOf.contains(elem))
            return false;
        doDispatchAddSubPropertyOf(elem);
        return true;
    }

    @Override
    public boolean removeSubPropertyOf(DataProperty elem) {
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
    private List<DataProperty> __implSuperPropertyOf;

    /**
     * Adds a value to the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperPropertyOf(DataProperty elem) {
        __implSuperPropertyOf.add(elem);
    }

    /**
     * Removes a value from the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperPropertyOf(DataProperty elem) {
        __implSuperPropertyOf.remove(elem);
    }

    /**
     * Adds a value to the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     * This method will also update the inverse property SubPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperPropertyOf(DataProperty elem) {
        doSimpleAddSuperPropertyOf(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleAddSubPropertyOf(this);
    }

    /**
     * Removes a value from the property SuperPropertyOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     * This method will also update the inverse property SubPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperPropertyOf(DataProperty elem) {
        doSimpleRemoveSuperPropertyOf(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleRemoveSubPropertyOf(this);
    }

    /**
     * Tries to add a value to the property SuperPropertyOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperPropertyOf(DataProperty elem) {
        doPropertyAddSuperPropertyOf(elem);
    }

    /**
     * Tries to remove a value from the property SuperPropertyOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperPropertyOf(DataProperty elem) {
        doPropertyRemoveSuperPropertyOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperPropertyOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperPropertyOf(DataProperty elem) {
        doGraphAddSuperPropertyOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperPropertyOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#superPropertyOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperPropertyOf(DataProperty elem) {
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
        if (!(elem instanceof DataProperty))
            throw new IllegalArgumentException("Expected type DataProperty");
        if (__implSuperPropertyOf.contains((DataProperty) elem))
            return false;
        doDispatchAddSuperPropertyOf((DataProperty) elem);
        return true;
    }

    @Override
    public boolean removeSuperPropertyOf(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!(elem instanceof DataProperty))
            throw new IllegalArgumentException("Expected type DataProperty");
        if (!__implSuperPropertyOf.contains((DataProperty) elem))
            return false;
        doDispatchRemoveSuperPropertyOf((DataProperty) elem);
        return true;
    }

    @Override
    public Collection<DataProperty> getAllSuperPropertyOfAs(DataProperty type) {
        return Collections.unmodifiableCollection(__implSuperPropertyOf);
    }

    @Override
    public boolean addSuperPropertyOf(DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSuperPropertyOf.contains(elem))
            return false;
        doDispatchAddSuperPropertyOf(elem);
        return true;
    }

    @Override
    public boolean removeSuperPropertyOf(DataProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSuperPropertyOf.contains(elem))
            return false;
        doDispatchRemoveSuperPropertyOf(elem);
        return true;
    }

    /**
     * Constructor for the implementation of DataProperty
     */
    public RuntimeDataPropertyImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#domain
        this.__implDomain = null;
        // initialize property http://xowl.org/infra/lang/runtime#interpretationOf
        this.__implInterpretationOf = null;
        // initialize property http://xowl.org/infra/lang/runtime#isFunctional
        this.__implIsFunctional = false;
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
