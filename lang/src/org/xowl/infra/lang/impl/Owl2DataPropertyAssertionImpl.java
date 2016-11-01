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
 * The default implementation for DataPropertyAssertion
 * Original OWL class is http://xowl.org/infra/lang/owl2#DataPropertyAssertion
 *
 * @author xOWL code generator
 */
public class Owl2DataPropertyAssertionImpl implements DataPropertyAssertion {
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
     * The backing data for the property DataProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#dataProperty
     */
    private DataPropertyExpression __implDataProperty;

    /**
     * Adds a value to the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataProperty(DataPropertyExpression elem) {
        __implDataProperty = elem;
    }

    /**
     * Removes a value from the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataProperty(DataPropertyExpression elem) {
        __implDataProperty = null;
    }

    /**
     * Adds a value to the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataProperty(DataPropertyExpression elem) {
        doSimpleAddDataProperty(elem);
    }

    /**
     * Removes a value from the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataProperty(DataPropertyExpression elem) {
        doSimpleRemoveDataProperty(elem);
    }

    /**
     * Tries to add a value to the property DataProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataProperty(DataPropertyExpression elem) {
        doPropertyAddDataProperty(elem);
    }

    /**
     * Tries to remove a value from the property DataProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataProperty(DataPropertyExpression elem) {
        doPropertyRemoveDataProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataProperty(DataPropertyExpression elem) {
        doGraphAddDataProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataProperty(DataPropertyExpression elem) {
        doGraphRemoveDataProperty(elem);
    }

    @Override
    public DataPropertyExpression getDataProperty() {
        return __implDataProperty;
    }

    @Override
    public void setDataProperty(DataPropertyExpression elem) {
        if (__implDataProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDataProperty(__implDataProperty);
        } else if (__implDataProperty == null) {
            doDispatchAddDataProperty(elem);
        } else {
            doDispatchRemoveDataProperty(__implDataProperty);
            doDispatchAddDataProperty(elem);
        }
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
     * The backing data for the property ValueLiteral
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#valueLiteral
     */
    private LiteralExpression __implValueLiteral;

    /**
     * Adds a value to the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValueLiteral(LiteralExpression elem) {
        __implValueLiteral = elem;
    }

    /**
     * Removes a value from the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValueLiteral(LiteralExpression elem) {
        __implValueLiteral = null;
    }

    /**
     * Adds a value to the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValueLiteral(LiteralExpression elem) {
        doSimpleAddValueLiteral(elem);
    }

    /**
     * Removes a value from the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValueLiteral(LiteralExpression elem) {
        doSimpleRemoveValueLiteral(elem);
    }

    /**
     * Tries to add a value to the property ValueLiteral and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValueLiteral(LiteralExpression elem) {
        doPropertyAddValueLiteral(elem);
    }

    /**
     * Tries to remove a value from the property ValueLiteral and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValueLiteral(LiteralExpression elem) {
        doPropertyRemoveValueLiteral(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ValueLiteral
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValueLiteral(LiteralExpression elem) {
        doGraphAddValueLiteral(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ValueLiteral
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValueLiteral(LiteralExpression elem) {
        doGraphRemoveValueLiteral(elem);
    }

    @Override
    public LiteralExpression getValueLiteral() {
        return __implValueLiteral;
    }

    @Override
    public void setValueLiteral(LiteralExpression elem) {
        if (__implValueLiteral == elem)
            return;
        if (elem == null) {
            doDispatchRemoveValueLiteral(__implValueLiteral);
        } else if (__implValueLiteral == null) {
            doDispatchAddValueLiteral(elem);
        } else {
            doDispatchRemoveValueLiteral(__implValueLiteral);
            doDispatchAddValueLiteral(elem);
        }
    }

    /**
     * Constructor for the implementation of DataPropertyAssertion
     */
    public Owl2DataPropertyAssertionImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/owl2#dataProperty
        this.__implDataProperty = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#file
        this.__implFile = null;
        // initialize property http://xowl.org/infra/lang/owl2#individual
        this.__implIndividual = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#line
        this.__implLine = 0;
        // initialize property http://xowl.org/infra/lang/owl2#valueLiteral
        this.__implValueLiteral = null;
    }
}
