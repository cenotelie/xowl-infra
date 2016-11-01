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
 * The default implementation for NegativeObjectPropertyAssertion
 * Original OWL class is http://xowl.org/infra/lang/owl2#NegativeObjectPropertyAssertion
 *
 * @author xOWL code generator
 */
public class Owl2NegativeObjectPropertyAssertionImpl implements NegativeObjectPropertyAssertion {
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
     * The backing data for the property Individual
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#individual
     */
    private IndividualExpression __implIndividual;

    /**
     * Adds a value to the property Individual
     * Original OWL property is http://xowl.org/infra/lang/owl2#individual
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividual(IndividualExpression elem) {
        __implIndividual = elem;
    }

    /**
     * Removes a value from the property Individual
     * Original OWL property is http://xowl.org/infra/lang/owl2#individual
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividual(IndividualExpression elem) {
        __implIndividual = null;
    }

    /**
     * Adds a value to the property Individual
     * Original OWL property is http://xowl.org/infra/lang/owl2#individual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividual(IndividualExpression elem) {
        doSimpleAddIndividual(elem);
    }

    /**
     * Removes a value from the property Individual
     * Original OWL property is http://xowl.org/infra/lang/owl2#individual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividual(IndividualExpression elem) {
        doSimpleRemoveIndividual(elem);
    }

    /**
     * Tries to add a value to the property Individual and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#individual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividual(IndividualExpression elem) {
        doPropertyAddIndividual(elem);
    }

    /**
     * Tries to remove a value from the property Individual and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#individual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividual(IndividualExpression elem) {
        doPropertyRemoveIndividual(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Individual
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#individual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividual(IndividualExpression elem) {
        doGraphAddIndividual(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Individual
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#individual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividual(IndividualExpression elem) {
        doGraphRemoveIndividual(elem);
    }

    @Override
    public IndividualExpression getIndividual() {
        return __implIndividual;
    }

    @Override
    public void setIndividual(IndividualExpression elem) {
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
     * The backing data for the property ObjectProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#objectProperty
     */
    private ObjectPropertyExpression __implObjectProperty;

    /**
     * Adds a value to the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectProperty(ObjectPropertyExpression elem) {
        __implObjectProperty = elem;
    }

    /**
     * Removes a value from the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectProperty(ObjectPropertyExpression elem) {
        __implObjectProperty = null;
    }

    /**
     * Adds a value to the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectProperty(ObjectPropertyExpression elem) {
        doSimpleAddObjectProperty(elem);
    }

    /**
     * Removes a value from the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectProperty(ObjectPropertyExpression elem) {
        doSimpleRemoveObjectProperty(elem);
    }

    /**
     * Tries to add a value to the property ObjectProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectProperty(ObjectPropertyExpression elem) {
        doPropertyAddObjectProperty(elem);
    }

    /**
     * Tries to remove a value from the property ObjectProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectProperty(ObjectPropertyExpression elem) {
        doPropertyRemoveObjectProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectProperty(ObjectPropertyExpression elem) {
        doGraphAddObjectProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectProperty(ObjectPropertyExpression elem) {
        doGraphRemoveObjectProperty(elem);
    }

    @Override
    public ObjectPropertyExpression getObjectProperty() {
        return __implObjectProperty;
    }

    @Override
    public void setObjectProperty(ObjectPropertyExpression elem) {
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
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#valueIndividual
     */
    private IndividualExpression __implValueIndividual;

    /**
     * Adds a value to the property ValueIndividual
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValueIndividual(IndividualExpression elem) {
        __implValueIndividual = elem;
    }

    /**
     * Removes a value from the property ValueIndividual
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValueIndividual(IndividualExpression elem) {
        __implValueIndividual = null;
    }

    /**
     * Adds a value to the property ValueIndividual
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValueIndividual(IndividualExpression elem) {
        doSimpleAddValueIndividual(elem);
    }

    /**
     * Removes a value from the property ValueIndividual
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValueIndividual(IndividualExpression elem) {
        doSimpleRemoveValueIndividual(elem);
    }

    /**
     * Tries to add a value to the property ValueIndividual and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValueIndividual(IndividualExpression elem) {
        doPropertyAddValueIndividual(elem);
    }

    /**
     * Tries to remove a value from the property ValueIndividual and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValueIndividual(IndividualExpression elem) {
        doPropertyRemoveValueIndividual(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ValueIndividual
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueIndividual
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValueIndividual(IndividualExpression elem) {
        doGraphAddValueIndividual(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ValueIndividual
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueIndividual
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValueIndividual(IndividualExpression elem) {
        doGraphRemoveValueIndividual(elem);
    }

    @Override
    public IndividualExpression getValueIndividual() {
        return __implValueIndividual;
    }

    @Override
    public void setValueIndividual(IndividualExpression elem) {
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
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/instrumentation#file
        this.__implFile = null;
        // initialize property http://xowl.org/infra/lang/owl2#individual
        this.__implIndividual = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#line
        this.__implLine = 0;
        // initialize property http://xowl.org/infra/lang/owl2#objectProperty
        this.__implObjectProperty = null;
        // initialize property http://xowl.org/infra/lang/owl2#valueIndividual
        this.__implValueIndividual = null;
    }
}
