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

import org.xowl.infra.lang.actions.*;

import java.util.*;

/**
 * The default implementation for FunctionDefinitionAxiom
 * Original OWL class is http://xowl.org/infra/lang/actions#FunctionDefinitionAxiom
 *
 * @author xOWL code generator
 */
public class ActionsFunctionDefinitionAxiomImpl implements FunctionDefinitionAxiom {
    /**
     * The backing data for the property Annotations
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#annotations
     */
    private List<org.xowl.infra.lang.owl2.Annotation> __implAnnotations;

    /**
     * Adds a value to the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        __implAnnotations.add(elem);
    }

    /**
     * Removes a value from the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        __implAnnotations.remove(elem);
    }

    /**
     * Adds a value to the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doSimpleAddAnnotations(elem);
    }

    /**
     * Removes a value from the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doSimpleRemoveAnnotations(elem);
    }

    /**
     * Tries to add a value to the property Annotations and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doPropertyAddAnnotations(elem);
    }

    /**
     * Tries to remove a value from the property Annotations and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doPropertyRemoveAnnotations(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Annotations
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotations(org.xowl.infra.lang.owl2.Annotation elem) {
        doGraphAddAnnotations(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Annotations
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
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
     * The backing data for the property Definition
     * This implements the storage for original OWL property http://xowl.org/infra/lang/actions#definition
     */
    private java.lang.Object __implDefinition;

    /**
     * Adds a value to the property Definition
     * Original OWL property is http://xowl.org/infra/lang/actions#definition
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDefinition(java.lang.Object elem) {
        __implDefinition = elem;
    }

    /**
     * Removes a value from the property Definition
     * Original OWL property is http://xowl.org/infra/lang/actions#definition
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDefinition(java.lang.Object elem) {
        __implDefinition = null;
    }

    /**
     * Adds a value to the property Definition
     * Original OWL property is http://xowl.org/infra/lang/actions#definition
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDefinition(java.lang.Object elem) {
        doSimpleAddDefinition(elem);
    }

    /**
     * Removes a value from the property Definition
     * Original OWL property is http://xowl.org/infra/lang/actions#definition
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDefinition(java.lang.Object elem) {
        doSimpleRemoveDefinition(elem);
    }

    /**
     * Tries to add a value to the property Definition and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/actions#definition
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDefinition(java.lang.Object elem) {
        doPropertyAddDefinition(elem);
    }

    /**
     * Tries to remove a value from the property Definition and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/actions#definition
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDefinition(java.lang.Object elem) {
        doPropertyRemoveDefinition(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Definition
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/actions#definition
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDefinition(java.lang.Object elem) {
        doGraphAddDefinition(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Definition
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/actions#definition
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDefinition(java.lang.Object elem) {
        doGraphRemoveDefinition(elem);
    }

    @Override
    public java.lang.Object getDefinition() {
        return __implDefinition;
    }

    @Override
    public void setDefinition(java.lang.Object elem) {
        if (__implDefinition == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDefinition(__implDefinition);
        } else if (__implDefinition == null) {
            doDispatchAddDefinition(elem);
        } else {
            doDispatchRemoveDefinition(__implDefinition);
            doDispatchAddDefinition(elem);
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
     * The backing data for the property Function
     * This implements the storage for original OWL property http://xowl.org/infra/lang/actions#function
     */
    private FunctionExpression __implFunction;

    /**
     * Adds a value to the property Function
     * Original OWL property is http://xowl.org/infra/lang/actions#function
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddFunction(FunctionExpression elem) {
        __implFunction = elem;
    }

    /**
     * Removes a value from the property Function
     * Original OWL property is http://xowl.org/infra/lang/actions#function
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveFunction(FunctionExpression elem) {
        __implFunction = null;
    }

    /**
     * Adds a value to the property Function
     * Original OWL property is http://xowl.org/infra/lang/actions#function
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddFunction(FunctionExpression elem) {
        doSimpleAddFunction(elem);
    }

    /**
     * Removes a value from the property Function
     * Original OWL property is http://xowl.org/infra/lang/actions#function
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveFunction(FunctionExpression elem) {
        doSimpleRemoveFunction(elem);
    }

    /**
     * Tries to add a value to the property Function and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/actions#function
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddFunction(FunctionExpression elem) {
        doPropertyAddFunction(elem);
    }

    /**
     * Tries to remove a value from the property Function and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/actions#function
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveFunction(FunctionExpression elem) {
        doPropertyRemoveFunction(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Function
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/actions#function
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddFunction(FunctionExpression elem) {
        doGraphAddFunction(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Function
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/actions#function
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveFunction(FunctionExpression elem) {
        doGraphRemoveFunction(elem);
    }

    @Override
    public FunctionExpression getFunction() {
        return __implFunction;
    }

    @Override
    public void setFunction(FunctionExpression elem) {
        if (__implFunction == elem)
            return;
        if (elem == null) {
            doDispatchRemoveFunction(__implFunction);
        } else if (__implFunction == null) {
            doDispatchAddFunction(elem);
        } else {
            doDispatchRemoveFunction(__implFunction);
            doDispatchAddFunction(elem);
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
     * Constructor for the implementation of FunctionDefinitionAxiom
     */
    public ActionsFunctionDefinitionAxiomImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/actions#definition
        this.__implDefinition = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#file
        this.__implFile = null;
        // initialize property http://xowl.org/infra/lang/actions#function
        this.__implFunction = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#line
        this.__implLine = 0;
    }
}
