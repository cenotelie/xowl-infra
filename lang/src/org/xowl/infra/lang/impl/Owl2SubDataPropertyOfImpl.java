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
 * The default implementation for the concrete OWL class SubDataPropertyOf
 *
 * @author xOWL code generator
 */
public class Owl2SubDataPropertyOfImpl implements org.xowl.infra.lang.owl2.SubDataPropertyOf {
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
     * The backing data for the property DataProperty
     */
    private org.xowl.infra.lang.owl2.DataPropertyExpression __implDataProperty;

    /**
     * Adds a value to the property DataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        __implDataProperty = elem;
    }

    /**
     * Removes a value from the property DataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        __implDataProperty = null;
    }

    /**
     * Adds a value to the property DataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doSimpleAddDataProperty(elem);
    }

    /**
     * Removes a value from the property DataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doSimpleRemoveDataProperty(elem);
    }

    /**
     * Tries to add a value to the property DataProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doPropertyAddDataProperty(elem);
    }

    /**
     * Tries to remove a value from the property DataProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doPropertyRemoveDataProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doGraphAddDataProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doGraphRemoveDataProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.DataPropertyExpression getDataProperty() {
        return __implDataProperty;
    }

    @Override
    public void setDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
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
     * The backing data for the property SuperDataProperty
     */
    private org.xowl.infra.lang.owl2.DataPropertyExpression __implSuperDataProperty;

    /**
     * Adds a value to the property SuperDataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        __implSuperDataProperty = elem;
    }

    /**
     * Removes a value from the property SuperDataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        __implSuperDataProperty = null;
    }

    /**
     * Adds a value to the property SuperDataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doSimpleAddSuperDataProperty(elem);
    }

    /**
     * Removes a value from the property SuperDataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doSimpleRemoveSuperDataProperty(elem);
    }

    /**
     * Tries to add a value to the property SuperDataProperty and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doPropertyAddSuperDataProperty(elem);
    }

    /**
     * Tries to remove a value from the property SuperDataProperty and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doPropertyRemoveSuperDataProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperDataProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doGraphAddSuperDataProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperDataProperty
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        doGraphRemoveSuperDataProperty(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.DataPropertyExpression getSuperDataProperty() {
        return __implSuperDataProperty;
    }

    @Override
    public void setSuperDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem) {
        if (__implSuperDataProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveSuperDataProperty(__implSuperDataProperty);
        } else if (__implSuperDataProperty == null) {
            doDispatchAddSuperDataProperty(elem);
        } else {
            doDispatchRemoveSuperDataProperty(__implSuperDataProperty);
            doDispatchAddSuperDataProperty(elem);
        }
    }

    /**
     * Constructor for the implementation of SubDataPropertyOf
     */
    public Owl2SubDataPropertyOfImpl() {
        this.__implLine = 0;
        this.__implDataProperty = null;
        this.__implAnnotations = new ArrayList<>();
        this.__implFile = null;
        this.__implSuperDataProperty = null;
    }
}
