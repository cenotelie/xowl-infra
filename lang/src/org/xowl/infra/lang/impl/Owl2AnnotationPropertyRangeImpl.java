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
 * The default implementation for AnnotationPropertyRange
 * Original OWL class is http://xowl.org/infra/lang/owl2#AnnotationPropertyRange
 *
 * @author xOWL code generator
 */
public class Owl2AnnotationPropertyRangeImpl implements AnnotationPropertyRange {
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
     * The backing data for the property AnnotRange
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#annotRange
     */
    private IRI __implAnnotRange;

    /**
     * Adds a value to the property AnnotRange
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotRange
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotRange(IRI elem) {
        __implAnnotRange = elem;
    }

    /**
     * Removes a value from the property AnnotRange
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotRange
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotRange(IRI elem) {
        __implAnnotRange = null;
    }

    /**
     * Adds a value to the property AnnotRange
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotRange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotRange(IRI elem) {
        doSimpleAddAnnotRange(elem);
    }

    /**
     * Removes a value from the property AnnotRange
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotRange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotRange(IRI elem) {
        doSimpleRemoveAnnotRange(elem);
    }

    /**
     * Tries to add a value to the property AnnotRange and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotRange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotRange(IRI elem) {
        doPropertyAddAnnotRange(elem);
    }

    /**
     * Tries to remove a value from the property AnnotRange and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotRange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotRange(IRI elem) {
        doPropertyRemoveAnnotRange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property AnnotRange
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotRange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotRange(IRI elem) {
        doGraphAddAnnotRange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property AnnotRange
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotRange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAnnotRange(IRI elem) {
        doGraphRemoveAnnotRange(elem);
    }

    @Override
    public IRI getAnnotRange() {
        return __implAnnotRange;
    }

    @Override
    public void setAnnotRange(IRI elem) {
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
     * The backing data for the property File
     * This implements the storage for original OWL property http://xowl.org/infra/lang/instrumentation#file
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
     * This implements the storage for original OWL property http://xowl.org/infra/lang/instrumentation#line
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
        // initialize property http://xowl.org/infra/lang/owl2#annotProperty
        this.__implAnnotProperty = null;
        // initialize property http://xowl.org/infra/lang/owl2#annotRange
        this.__implAnnotRange = null;
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/instrumentation#file
        this.__implFile = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#line
        this.__implLine = 0;
    }
}
