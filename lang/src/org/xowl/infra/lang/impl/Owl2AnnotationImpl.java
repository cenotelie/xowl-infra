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

import org.xowl.infra.lang.owl2.*;

import java.util.*;

/**
 * The default implementation for Annotation
 * Original OWL class is http://xowl.org/infra/lang/owl2#Annotation
 *
 * @author xOWL code generator
 */
public class Owl2AnnotationImpl implements Annotation {
    /**
     * The backing data for the property AnnotProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#annotProperty
     */
    private IRI __implAnnotProperty;

    /**
     * Adds a value to the property AnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotProperty(IRI elem) {
        __implAnnotProperty = elem;
    }

    /**
     * Removes a value from the property AnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotProperty(IRI elem) {
        __implAnnotProperty = null;
    }

    /**
     * Adds a value to the property AnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotProperty(IRI elem) {
        doSimpleAddAnnotProperty(elem);
    }

    /**
     * Removes a value from the property AnnotProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotProperty(IRI elem) {
        doSimpleRemoveAnnotProperty(elem);
    }

    /**
     * Tries to add a value to the property AnnotProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotProperty(IRI elem) {
        doPropertyAddAnnotProperty(elem);
    }

    /**
     * Tries to remove a value from the property AnnotProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotProperty(IRI elem) {
        doPropertyRemoveAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property AnnotProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotProperty(IRI elem) {
        doGraphAddAnnotProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property AnnotProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAnnotProperty(IRI elem) {
        doGraphRemoveAnnotProperty(elem);
    }

    @Override
    public IRI getAnnotProperty() {
        return __implAnnotProperty;
    }

    @Override
    public void setAnnotProperty(IRI elem) {
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
     * The backing data for the property AnnotValue
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#annotValue
     */
    private AnnotationValue __implAnnotValue;

    /**
     * Adds a value to the property AnnotValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotValue
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotValue(AnnotationValue elem) {
        __implAnnotValue = elem;
    }

    /**
     * Removes a value from the property AnnotValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotValue
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotValue(AnnotationValue elem) {
        __implAnnotValue = null;
    }

    /**
     * Adds a value to the property AnnotValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotValue
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotValue(AnnotationValue elem) {
        doSimpleAddAnnotValue(elem);
    }

    /**
     * Removes a value from the property AnnotValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotValue
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotValue(AnnotationValue elem) {
        doSimpleRemoveAnnotValue(elem);
    }

    /**
     * Tries to add a value to the property AnnotValue and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotValue
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotValue(AnnotationValue elem) {
        doPropertyAddAnnotValue(elem);
    }

    /**
     * Tries to remove a value from the property AnnotValue and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotValue
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotValue(AnnotationValue elem) {
        doPropertyRemoveAnnotValue(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property AnnotValue
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotValue
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotValue(AnnotationValue elem) {
        doGraphAddAnnotValue(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property AnnotValue
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotValue
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAnnotValue(AnnotationValue elem) {
        doGraphRemoveAnnotValue(elem);
    }

    @Override
    public AnnotationValue getAnnotValue() {
        return __implAnnotValue;
    }

    @Override
    public void setAnnotValue(AnnotationValue elem) {
        if (__implAnnotValue == elem)
            return;
        if (elem == null) {
            doDispatchRemoveAnnotValue(__implAnnotValue);
        } else if (__implAnnotValue == null) {
            doDispatchAddAnnotValue(elem);
        } else {
            doDispatchRemoveAnnotValue(__implAnnotValue);
            doDispatchAddAnnotValue(elem);
        }
    }

    /**
     * The backing data for the property Annotations
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#annotations
     */
    private List<Annotation> __implAnnotations;

    /**
     * Adds a value to the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotations(Annotation elem) {
        __implAnnotations.add(elem);
    }

    /**
     * Removes a value from the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotations(Annotation elem) {
        __implAnnotations.remove(elem);
    }

    /**
     * Adds a value to the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotations(Annotation elem) {
        doSimpleAddAnnotations(elem);
    }

    /**
     * Removes a value from the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotations(Annotation elem) {
        doSimpleRemoveAnnotations(elem);
    }

    /**
     * Tries to add a value to the property Annotations and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotations(Annotation elem) {
        doPropertyAddAnnotations(elem);
    }

    /**
     * Tries to remove a value from the property Annotations and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotations(Annotation elem) {
        doPropertyRemoveAnnotations(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Annotations
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotations(Annotation elem) {
        doGraphAddAnnotations(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Annotations
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAnnotations(Annotation elem) {
        doGraphRemoveAnnotations(elem);
    }

    @Override
    public Collection<Annotation> getAllAnnotations() {
        return Collections.unmodifiableCollection(__implAnnotations);
    }

    @Override
    public boolean addAnnotations(Annotation elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implAnnotations.contains(elem))
            return false;
        doDispatchAddAnnotations(elem);
        return true;
    }

    @Override
    public boolean removeAnnotations(Annotation elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implAnnotations.contains(elem))
            return false;
        doDispatchRemoveAnnotations(elem);
        return true;
    }

    /**
     * Constructor for the implementation of Annotation
     */
    public Owl2AnnotationImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#annotProperty
        this.__implAnnotProperty = null;
        // initialize property http://xowl.org/infra/lang/owl2#annotValue
        this.__implAnnotValue = null;
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
    }
}
