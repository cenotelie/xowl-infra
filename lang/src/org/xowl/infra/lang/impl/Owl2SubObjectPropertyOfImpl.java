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
 * The default implementation for SubObjectPropertyOf
 * Original OWL class is http://xowl.org/infra/lang/owl2#SubObjectPropertyOf
 *
 * @author xOWL code generator
 */
public class Owl2SubObjectPropertyOfImpl implements SubObjectPropertyOf {
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
     * The backing data for the property ObjectPropertyChain
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#objectPropertyChain
     */
    private ObjectPropertySequenceExpression __implObjectPropertyChain;

    /**
     * Adds a value to the property ObjectPropertyChain
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectPropertyChain(ObjectPropertySequenceExpression elem) {
        __implObjectPropertyChain = elem;
    }

    /**
     * Removes a value from the property ObjectPropertyChain
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectPropertyChain(ObjectPropertySequenceExpression elem) {
        __implObjectPropertyChain = null;
    }

    /**
     * Adds a value to the property ObjectPropertyChain
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectPropertyChain(ObjectPropertySequenceExpression elem) {
        doSimpleAddObjectPropertyChain(elem);
    }

    /**
     * Removes a value from the property ObjectPropertyChain
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectPropertyChain(ObjectPropertySequenceExpression elem) {
        doSimpleRemoveObjectPropertyChain(elem);
    }

    /**
     * Tries to add a value to the property ObjectPropertyChain and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectPropertyChain(ObjectPropertySequenceExpression elem) {
        doPropertyAddObjectPropertyChain(elem);
    }

    /**
     * Tries to remove a value from the property ObjectPropertyChain and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectPropertyChain(ObjectPropertySequenceExpression elem) {
        doPropertyRemoveObjectPropertyChain(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectPropertyChain
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectPropertyChain(ObjectPropertySequenceExpression elem) {
        doGraphAddObjectPropertyChain(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectPropertyChain
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertyChain
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectPropertyChain(ObjectPropertySequenceExpression elem) {
        doGraphRemoveObjectPropertyChain(elem);
    }

    @Override
    public ObjectPropertySequenceExpression getObjectPropertyChain() {
        return __implObjectPropertyChain;
    }

    @Override
    public void setObjectPropertyChain(ObjectPropertySequenceExpression elem) {
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
     * The backing data for the property SuperObjectProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#superObjectProperty
     */
    private ObjectPropertyExpression __implSuperObjectProperty;

    /**
     * Adds a value to the property SuperObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperObjectProperty(ObjectPropertyExpression elem) {
        __implSuperObjectProperty = elem;
    }

    /**
     * Removes a value from the property SuperObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperObjectProperty(ObjectPropertyExpression elem) {
        __implSuperObjectProperty = null;
    }

    /**
     * Adds a value to the property SuperObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperObjectProperty(ObjectPropertyExpression elem) {
        doSimpleAddSuperObjectProperty(elem);
    }

    /**
     * Removes a value from the property SuperObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperObjectProperty(ObjectPropertyExpression elem) {
        doSimpleRemoveSuperObjectProperty(elem);
    }

    /**
     * Tries to add a value to the property SuperObjectProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperObjectProperty(ObjectPropertyExpression elem) {
        doPropertyAddSuperObjectProperty(elem);
    }

    /**
     * Tries to remove a value from the property SuperObjectProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperObjectProperty(ObjectPropertyExpression elem) {
        doPropertyRemoveSuperObjectProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperObjectProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperObjectProperty(ObjectPropertyExpression elem) {
        doGraphAddSuperObjectProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperObjectProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#superObjectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperObjectProperty(ObjectPropertyExpression elem) {
        doGraphRemoveSuperObjectProperty(elem);
    }

    @Override
    public ObjectPropertyExpression getSuperObjectProperty() {
        return __implSuperObjectProperty;
    }

    @Override
    public void setSuperObjectProperty(ObjectPropertyExpression elem) {
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
     * Constructor for the implementation of SubObjectPropertyOf
     */
    public Owl2SubObjectPropertyOfImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/instrumentation#file
        this.__implFile = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#line
        this.__implLine = 0;
        // initialize property http://xowl.org/infra/lang/owl2#objectProperty
        this.__implObjectProperty = null;
        // initialize property http://xowl.org/infra/lang/owl2#objectPropertyChain
        this.__implObjectPropertyChain = null;
        // initialize property http://xowl.org/infra/lang/owl2#superObjectProperty
        this.__implSuperObjectProperty = null;
    }
}
