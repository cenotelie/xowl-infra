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
 * The default implementation for the concrete OWL class AnnotationProperty
 *
 * @author xOWL code generator
 */
public class RuntimeAnnotationPropertyImpl implements org.xowl.infra.lang.runtime.AnnotationProperty {
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
     * The backing data for the property SuperAnnotProperty
     */
    private List<org.xowl.infra.lang.runtime.AnnotationProperty> __implSuperAnnotProperty;

    /**
     * Adds a value to the property SuperAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        __implSuperAnnotProperty.add(elem);
    }

    /**
     * Removes a value from the property SuperAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        __implSuperAnnotProperty.remove(elem);
    }

    /**
     * Adds a value to the property SuperAnnotProperty
     * This method will also update the inverse property SubAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doSimpleAddSuperAnnotProperty(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleAddSubAnnotProperty(this);
    }

    /**
     * Removes a value from the property SuperAnnotProperty
     * This method will also update the inverse property SubAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doSimpleRemoveSuperAnnotProperty(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleRemoveSubAnnotProperty(this);
    }

    /**
     * Tries to add a value to the property SuperAnnotProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doPropertyAddSuperAnnotProperty(elem);
    }

    /**
     * Tries to remove a value from the property SuperAnnotProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doPropertyRemoveSuperAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperAnnotProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doGraphAddSuperAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperAnnotProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doGraphRemoveSuperAnnotProperty(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.AnnotationProperty> getAllSuperAnnotProperty() {
        return Collections.unmodifiableCollection(__implSuperAnnotProperty);
    }

    @Override
    public boolean addSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSuperAnnotProperty.contains(elem))
            return false;
        doDispatchAddSuperAnnotProperty(elem);
        return true;
    }

    @Override
    public boolean removeSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSuperAnnotProperty.contains(elem))
            return false;
        doDispatchRemoveSuperAnnotProperty(elem);
        return true;
    }

    /**
     * The backing data for the property SubAnnotProperty
     */
    private List<org.xowl.infra.lang.runtime.AnnotationProperty> __implSubAnnotProperty;

    /**
     * Adds a value to the property SubAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        __implSubAnnotProperty.add(elem);
    }

    /**
     * Removes a value from the property SubAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        __implSubAnnotProperty.remove(elem);
    }

    /**
     * Adds a value to the property SubAnnotProperty
     * This method will also update the inverse property SuperAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doSimpleAddSubAnnotProperty(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleAddSuperAnnotProperty(this);
    }

    /**
     * Removes a value from the property SubAnnotProperty
     * This method will also update the inverse property SuperAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doSimpleRemoveSubAnnotProperty(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleRemoveSuperAnnotProperty(this);
    }

    /**
     * Tries to add a value to the property SubAnnotProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doPropertyAddSubAnnotProperty(elem);
    }

    /**
     * Tries to remove a value from the property SubAnnotProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doPropertyRemoveSubAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SubAnnotProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doGraphAddSubAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SubAnnotProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        doGraphRemoveSubAnnotProperty(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.AnnotationProperty> getAllSubAnnotProperty() {
        return Collections.unmodifiableCollection(__implSubAnnotProperty);
    }

    @Override
    public boolean addSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSubAnnotProperty.contains(elem))
            return false;
        doDispatchAddSubAnnotProperty(elem);
        return true;
    }

    @Override
    public boolean removeSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSubAnnotProperty.contains(elem))
            return false;
        doDispatchRemoveSubAnnotProperty(elem);
        return true;
    }

    /**
     * Constructor for the implementation of AnnotationProperty
     */
    public RuntimeAnnotationPropertyImpl() {
        this.__implInterpretationOf = null;
        this.__implSuperAnnotProperty = new ArrayList<>();
        this.__implSubAnnotProperty = new ArrayList<>();
    }
}
