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
 * The default implementation for the concrete OWL class InverseObjectProperties
 *
 * @author xOWL code generator
 */
public class Owl2InverseObjectPropertiesImpl implements org.xowl.infra.lang.owl2.InverseObjectProperties {
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
     * The backing data for the property Inverse
     */
    private org.xowl.infra.lang.owl2.ObjectPropertyExpression __implInverse;

    /**
     * Adds a value to the property Inverse
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        __implInverse = elem;
    }

    /**
     * Removes a value from the property Inverse
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        __implInverse = null;
    }

    /**
     * Adds a value to the property Inverse
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doSimpleAddInverse(elem);
    }

    /**
     * Removes a value from the property Inverse
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doSimpleRemoveInverse(elem);
    }

    /**
     * Tries to add a value to the property Inverse and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doPropertyAddInverse(elem);
    }

    /**
     * Tries to remove a value from the property Inverse and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doPropertyRemoveInverse(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Inverse
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doGraphAddInverse(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Inverse
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        doGraphRemoveInverse(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.ObjectPropertyExpression getInverse() {
        return __implInverse;
    }

    @Override
    public void setInverse(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        if (__implInverse == elem)
            return;
        if (elem == null) {
            doDispatchRemoveInverse(__implInverse);
        } else if (__implInverse == null) {
            doDispatchAddInverse(elem);
        } else {
            doDispatchRemoveInverse(__implInverse);
            doDispatchAddInverse(elem);
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
     * Constructor for the implementation of InverseObjectProperties
     */
    public Owl2InverseObjectPropertiesImpl() {
        this.__implLine = 0;
        this.__implObjectProperty = null;
        this.__implInverse = null;
        this.__implAnnotations = new ArrayList<>();
        this.__implFile = null;
    }
}
