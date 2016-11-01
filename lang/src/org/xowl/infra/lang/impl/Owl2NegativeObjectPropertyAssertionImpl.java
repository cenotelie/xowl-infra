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
 * The default implementation for the concrete OWL class NegativeObjectPropertyAssertion
 *
 * @author xOWL code generator
 */
public class Owl2NegativeObjectPropertyAssertionImpl implements org.xowl.infra.lang.owl2.NegativeObjectPropertyAssertion {
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
     * The backing data for the property Individual
     */
    private org.xowl.infra.lang.owl2.IndividualExpression __implIndividual;

    /**
     * Adds a value to the property Individual
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        __implIndividual = elem;
    }

    /**
     * Removes a value from the property Individual
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        __implIndividual = null;
    }

    /**
     * Adds a value to the property Individual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doSimpleAddIndividual(elem);
    }

    /**
     * Removes a value from the property Individual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doSimpleRemoveIndividual(elem);
    }

    /**
     * Tries to add a value to the property Individual and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doPropertyAddIndividual(elem);
    }

    /**
     * Tries to remove a value from the property Individual and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doPropertyRemoveIndividual(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Individual
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doGraphAddIndividual(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Individual
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doGraphRemoveIndividual(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IndividualExpression getIndividual() {
        return __implIndividual;
    }

    @Override
    public void setIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        if (__implIndividual == elem)
            return;
        if (elem == null) {
            doDispatchRemoveIndividual(__implIndividual);
        } else if (__implIndividual == null) {
            doDispatchAddIndividual(elem);
        } else {
            doDispatchRemoveIndividual(__implIndividual);
            doDispatchAddIndividual(elem);
        }
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
     * The backing data for the property ValueIndividual
     */
    private org.xowl.infra.lang.owl2.IndividualExpression __implValueIndividual;

    /**
     * Adds a value to the property ValueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        __implValueIndividual = elem;
    }

    /**
     * Removes a value from the property ValueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        __implValueIndividual = null;
    }

    /**
     * Adds a value to the property ValueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doSimpleAddValueIndividual(elem);
    }

    /**
     * Removes a value from the property ValueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doSimpleRemoveValueIndividual(elem);
    }

    /**
     * Tries to add a value to the property ValueIndividual and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doPropertyAddValueIndividual(elem);
    }

    /**
     * Tries to remove a value from the property ValueIndividual and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doPropertyRemoveValueIndividual(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ValueIndividual
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doGraphAddValueIndividual(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ValueIndividual
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        doGraphRemoveValueIndividual(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IndividualExpression getValueIndividual() {
        return __implValueIndividual;
    }

    @Override
    public void setValueIndividual(org.xowl.infra.lang.owl2.IndividualExpression elem) {
        if (__implValueIndividual == elem)
            return;
        if (elem == null) {
            doDispatchRemoveValueIndividual(__implValueIndividual);
        } else if (__implValueIndividual == null) {
            doDispatchAddValueIndividual(elem);
        } else {
            doDispatchRemoveValueIndividual(__implValueIndividual);
            doDispatchAddValueIndividual(elem);
        }
    }

    /**
     * Constructor for the implementation of NegativeObjectPropertyAssertion
     */
    public Owl2NegativeObjectPropertyAssertionImpl() {
        this.__implAnnotations = new ArrayList<>();
        this.__implFile = null;
        this.__implIndividual = null;
        this.__implLine = 0;
        this.__implObjectProperty = null;
        this.__implValueIndividual = null;
    }
}
