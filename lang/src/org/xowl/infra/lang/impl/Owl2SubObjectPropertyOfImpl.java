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
 * The default implementation for the concrete OWL class SubObjectPropertyOf
 *
 * @author xOWL code generator
 */
public class Owl2SubObjectPropertyOfImpl implements org.xowl.infra.lang.owl2.SubObjectPropertyOf {
    /**
     * The backing data for the property Line
     */
    private int __implLine;

    @Override
    public int getLine() {
        return __implLine;
    }

    @Override
    public void setLine(int elem) {
        __implLine = elem;
    }

    /**
     * The backing data for the property ObjectProperty
     */
    private org.xowl.infra.lang.owl2.ObjectPropertyExpression __implObjectProperty;

    /**
     * Adds a value to the property ObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        __implObjectProperty = elem;
    }

    /**
     * Removes a value from the property ObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        __implObjectProperty = null;
    }

    /**
     * Adds a value to the property ObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doSimpleAddObjectProperty(elem);
    }

    /**
     * Removes a value from the property ObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doSimpleRemoveObjectProperty(elem);
    }

    /**
     * Tries to add a value to the property ObjectProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doPropertyAddObjectProperty(elem);
    }

    /**
     * Tries to remove a value from the property ObjectProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doPropertyRemoveObjectProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doGraphAddObjectProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doGraphRemoveObjectProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.ObjectPropertyExpression getObjectProperty() {
        return __implObjectProperty;
    }

    @Override
    public void setObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        if (__implObjectProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveObjectProperty(__implObjectProperty);
        } else if (__implObjectProperty == null) {
            doDispatchAddObjectProperty(elem);
        } else {
            doDispatchRemoveObjectProperty(__implObjectProperty);
            doDispatchAddObjectProperty(elem);
        }
    }

    /**
     * The backing data for the property SuperObjectProperty
     */
    private org.xowl.infra.lang.owl2.ObjectPropertyExpression __implSuperObjectProperty;

    /**
     * Adds a value to the property SuperObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        __implSuperObjectProperty = elem;
    }

    /**
     * Removes a value from the property SuperObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        __implSuperObjectProperty = null;
    }

    /**
     * Adds a value to the property SuperObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doSimpleAddSuperObjectProperty(elem);
    }

    /**
     * Removes a value from the property SuperObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doSimpleRemoveSuperObjectProperty(elem);
    }

    /**
     * Tries to add a value to the property SuperObjectProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doPropertyAddSuperObjectProperty(elem);
    }

    /**
     * Tries to remove a value from the property SuperObjectProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doPropertyRemoveSuperObjectProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperObjectProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doGraphAddSuperObjectProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperObjectProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doGraphRemoveSuperObjectProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.ObjectPropertyExpression getSuperObjectProperty() {
        return __implSuperObjectProperty;
    }

    @Override
    public void setSuperObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        if (__implSuperObjectProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveSuperObjectProperty(__implSuperObjectProperty);
        } else if (__implSuperObjectProperty == null) {
            doDispatchAddSuperObjectProperty(elem);
        } else {
            doDispatchRemoveSuperObjectProperty(__implSuperObjectProperty);
            doDispatchAddSuperObjectProperty(elem);
        }
    }

    /**
     * The backing data for the property ObjectPropertyChain
     */
    private org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression __implObjectPropertyChain;

    /**
     * Adds a value to the property ObjectPropertyChain
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        __implObjectPropertyChain = elem;
    }

    /**
     * Removes a value from the property ObjectPropertyChain
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        __implObjectPropertyChain = null;
    }

    /**
     * Adds a value to the property ObjectPropertyChain
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doSimpleAddObjectPropertyChain(elem);
    }

    /**
     * Removes a value from the property ObjectPropertyChain
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doSimpleRemoveObjectPropertyChain(elem);
    }

    /**
     * Tries to add a value to the property ObjectPropertyChain and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doPropertyAddObjectPropertyChain(elem);
    }

    /**
     * Tries to remove a value from the property ObjectPropertyChain and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doPropertyRemoveObjectPropertyChain(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectPropertyChain
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doGraphAddObjectPropertyChain(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectPropertyChain
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doGraphRemoveObjectPropertyChain(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression getObjectPropertyChain() {
        return __implObjectPropertyChain;
    }

    @Override
    public void setObjectPropertyChain(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        if (__implObjectPropertyChain == elem)
            return;
        if (elem == null) {
            doDispatchRemoveObjectPropertyChain(__implObjectPropertyChain);
        } else if (__implObjectPropertyChain == null) {
            doDispatchAddObjectPropertyChain(elem);
        } else {
            doDispatchRemoveObjectPropertyChain(__implObjectPropertyChain);
            doDispatchAddObjectPropertyChain(elem);
        }
    }

    /**
     * The backing data for the property Annotations
     */
    private List<org.xowl.infra.lang.owl2.Annotation> __implAnnotations;

    /**
     * Adds a value to the property Annotations
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        __implAnnotations.add(elem);
    }

    /**
     * Removes a value from the property Annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        __implAnnotations.remove(elem);
    }

    /**
     * Adds a value to the property Annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doSimpleAddAnnotations(elem);
    }

    /**
     * Removes a value from the property Annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doSimpleRemoveAnnotations(elem);
    }

    /**
     * Tries to add a value to the property Annotations and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doPropertyAddAnnotations(elem);
    }

    /**
     * Tries to remove a value from the property Annotations and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doPropertyRemoveAnnotations(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Annotations
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doGraphAddAnnotations(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Annotations
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doGraphRemoveAnnotations(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.owl2.Annotation> getAllAnnotations() {
        return Collections.unmodifiableCollection(__implAnnotations);
    }

    @Override
    public boolean addAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implAnnotations.contains(elem))
            return false;
        doDispatchAddAnnotations(elem);
        return true;
    }

    @Override
    public boolean removeAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implAnnotations.contains(elem))
            return false;
        doDispatchRemoveAnnotations(elem);
        return true;
    }

    /**
     * The backing data for the property File
     */
    private String __implFile;

    @Override
    public String getFile() {
        return __implFile;
    }

    @Override
    public void setFile(String elem) {
        __implFile = elem;
    }

    /**
     * Constructor for the implementation of SubObjectPropertyOf
     */
    public Owl2SubObjectPropertyOfImpl() {
        this.__implLine = 0;
        this.__implObjectProperty = null;
        this.__implSuperObjectProperty = null;
        this.__implObjectPropertyChain = null;
        this.__implAnnotations = new ArrayList<>();
        this.__implFile = null;
    }
}
