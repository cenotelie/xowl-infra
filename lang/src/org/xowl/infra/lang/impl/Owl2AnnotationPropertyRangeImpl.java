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
 * The default implementation for the concrete OWL class AnnotationPropertyRange
 *
 * @author xOWL code generator
 */
public class Owl2AnnotationPropertyRangeImpl implements org.xowl.infra.lang.owl2.AnnotationPropertyRange {
    /**
     * The backing data for the property AnnotProperty
     */
    private org.xowl.infra.lang.owl2.IRI __implAnnotProperty;

    /**
     * Adds a value to the property AnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        __implAnnotProperty = elem;
    }

    /**
     * Removes a value from the property AnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        __implAnnotProperty = null;
    }

    /**
     * Adds a value to the property AnnotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddAnnotProperty(elem);
    }

    /**
     * Removes a value from the property AnnotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveAnnotProperty(elem);
    }

    /**
     * Tries to add a value to the property AnnotProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddAnnotProperty(elem);
    }

    /**
     * Tries to remove a value from the property AnnotProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property AnnotProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property AnnotProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphRemoveAnnotProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IRI getAnnotProperty() {
        return __implAnnotProperty;
    }

    @Override
    public void setAnnotProperty(org.xowl.infra.lang.owl2.IRI elem) {
        if (__implAnnotProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveAnnotProperty(__implAnnotProperty);
        } else if (__implAnnotProperty == null) {
            doDispatchAddAnnotProperty(elem);
        } else {
            doDispatchRemoveAnnotProperty(__implAnnotProperty);
            doDispatchAddAnnotProperty(elem);
        }
    }

    /**
     * The backing data for the property AnnotRange
     */
    private org.xowl.infra.lang.owl2.IRI __implAnnotRange;

    /**
     * Adds a value to the property AnnotRange
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        __implAnnotRange = elem;
    }

    /**
     * Removes a value from the property AnnotRange
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        __implAnnotRange = null;
    }

    /**
     * Adds a value to the property AnnotRange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddAnnotRange(elem);
    }

    /**
     * Removes a value from the property AnnotRange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveAnnotRange(elem);
    }

    /**
     * Tries to add a value to the property AnnotRange and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddAnnotRange(elem);
    }

    /**
     * Tries to remove a value from the property AnnotRange and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveAnnotRange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property AnnotRange
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddAnnotRange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property AnnotRange
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphRemoveAnnotRange(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IRI getAnnotRange() {
        return __implAnnotRange;
    }

    @Override
    public void setAnnotRange(org.xowl.infra.lang.owl2.IRI elem) {
        if (__implAnnotRange == elem)
            return;
        if (elem == null) {
            doDispatchRemoveAnnotRange(__implAnnotRange);
        } else if (__implAnnotRange == null) {
            doDispatchAddAnnotRange(elem);
        } else {
            doDispatchRemoveAnnotRange(__implAnnotRange);
            doDispatchAddAnnotRange(elem);
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
     * Constructor for the implementation of AnnotationPropertyRange
     */
    public Owl2AnnotationPropertyRangeImpl() {
        this.__implAnnotProperty = null;
        this.__implAnnotRange = null;
        this.__implAnnotations = new ArrayList<>();
        this.__implFile = null;
        this.__implLine = 0;
    }
}
