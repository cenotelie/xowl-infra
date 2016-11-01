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
 * The default implementation for AnnotationProperty
 * Original OWL class is http://xowl.org/infra/lang/runtime#AnnotationProperty
 *
 * @author xOWL code generator
 */
public class RuntimeAnnotationPropertyImpl implements AnnotationProperty {
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
     * The backing data for the property SubAnnotProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#subAnnotProperty
     */
    private List<AnnotationProperty> __implSubAnnotProperty;

    /**
     * Adds a value to the property SubAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSubAnnotProperty(AnnotationProperty elem) {
        __implSubAnnotProperty.add(elem);
    }

    /**
     * Removes a value from the property SubAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSubAnnotProperty(AnnotationProperty elem) {
        __implSubAnnotProperty.remove(elem);
    }

    /**
     * Adds a value to the property SubAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     * This method will also update the inverse property SuperAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSubAnnotProperty(AnnotationProperty elem) {
        doSimpleAddSubAnnotProperty(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleAddSuperAnnotProperty(this);
    }

    /**
     * Removes a value from the property SubAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     * This method will also update the inverse property SuperAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSubAnnotProperty(AnnotationProperty elem) {
        doSimpleRemoveSubAnnotProperty(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleRemoveSuperAnnotProperty(this);
    }

    /**
     * Tries to add a value to the property SubAnnotProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSubAnnotProperty(AnnotationProperty elem) {
        doPropertyAddSubAnnotProperty(elem);
    }

    /**
     * Tries to remove a value from the property SubAnnotProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSubAnnotProperty(AnnotationProperty elem) {
        doPropertyRemoveSubAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SubAnnotProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSubAnnotProperty(AnnotationProperty elem) {
        doGraphAddSubAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SubAnnotProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#subAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSubAnnotProperty(AnnotationProperty elem) {
        doGraphRemoveSubAnnotProperty(elem);
    }

    @Override
    public Collection<AnnotationProperty> getAllSubAnnotProperty() {
        return Collections.unmodifiableCollection(__implSubAnnotProperty);
    }

    @Override
    public boolean addSubAnnotProperty(AnnotationProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSubAnnotProperty.contains(elem))
            return false;
        doDispatchAddSubAnnotProperty(elem);
        return true;
    }

    @Override
    public boolean removeSubAnnotProperty(AnnotationProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSubAnnotProperty.contains(elem))
            return false;
        doDispatchRemoveSubAnnotProperty(elem);
        return true;
    }

    /**
     * The backing data for the property SuperAnnotProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#superAnnotProperty
     */
    private List<AnnotationProperty> __implSuperAnnotProperty;

    /**
     * Adds a value to the property SuperAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperAnnotProperty(AnnotationProperty elem) {
        __implSuperAnnotProperty.add(elem);
    }

    /**
     * Removes a value from the property SuperAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperAnnotProperty(AnnotationProperty elem) {
        __implSuperAnnotProperty.remove(elem);
    }

    /**
     * Adds a value to the property SuperAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     * This method will also update the inverse property SubAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperAnnotProperty(AnnotationProperty elem) {
        doSimpleAddSuperAnnotProperty(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleAddSubAnnotProperty(this);
    }

    /**
     * Removes a value from the property SuperAnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     * This method will also update the inverse property SubAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperAnnotProperty(AnnotationProperty elem) {
        doSimpleRemoveSuperAnnotProperty(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleRemoveSubAnnotProperty(this);
    }

    /**
     * Tries to add a value to the property SuperAnnotProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperAnnotProperty(AnnotationProperty elem) {
        doPropertyAddSuperAnnotProperty(elem);
    }

    /**
     * Tries to remove a value from the property SuperAnnotProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperAnnotProperty(AnnotationProperty elem) {
        doPropertyRemoveSuperAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperAnnotProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperAnnotProperty(AnnotationProperty elem) {
        doGraphAddSuperAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperAnnotProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#superAnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperAnnotProperty(AnnotationProperty elem) {
        doGraphRemoveSuperAnnotProperty(elem);
    }

    @Override
    public Collection<AnnotationProperty> getAllSuperAnnotProperty() {
        return Collections.unmodifiableCollection(__implSuperAnnotProperty);
    }

    @Override
    public boolean addSuperAnnotProperty(AnnotationProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSuperAnnotProperty.contains(elem))
            return false;
        doDispatchAddSuperAnnotProperty(elem);
        return true;
    }

    @Override
    public boolean removeSuperAnnotProperty(AnnotationProperty elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSuperAnnotProperty.contains(elem))
            return false;
        doDispatchRemoveSuperAnnotProperty(elem);
        return true;
    }

    /**
     * Constructor for the implementation of AnnotationProperty
     */
    public RuntimeAnnotationPropertyImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#interpretationOf
        this.__implInterpretationOf = null;
        // initialize property http://xowl.org/infra/lang/runtime#subAnnotProperty
        this.__implSubAnnotProperty = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#superAnnotProperty
        this.__implSuperAnnotProperty = new ArrayList<>();
    }
}
