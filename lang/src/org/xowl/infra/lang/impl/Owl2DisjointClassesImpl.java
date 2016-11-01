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
 * The default implementation for DisjointClasses
 * Original OWL class is http://xowl.org/infra/lang/owl2#DisjointClasses
 *
 * @author xOWL code generator
 */
public class Owl2DisjointClassesImpl implements DisjointClasses {
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
     * The backing data for the property ClassSeq
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#classSeq
     */
    private ClassSequenceExpression __implClassSeq;

    /**
     * Adds a value to the property ClassSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassSeq(ClassSequenceExpression elem) {
        __implClassSeq = elem;
    }

    /**
     * Removes a value from the property ClassSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassSeq(ClassSequenceExpression elem) {
        __implClassSeq = null;
    }

    /**
     * Adds a value to the property ClassSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassSeq(ClassSequenceExpression elem) {
        doSimpleAddClassSeq(elem);
    }

    /**
     * Removes a value from the property ClassSeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassSeq(ClassSequenceExpression elem) {
        doSimpleRemoveClassSeq(elem);
    }

    /**
     * Tries to add a value to the property ClassSeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassSeq(ClassSequenceExpression elem) {
        doPropertyAddClassSeq(elem);
    }

    /**
     * Tries to remove a value from the property ClassSeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassSeq(ClassSequenceExpression elem) {
        doPropertyRemoveClassSeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassSeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassSeq(ClassSequenceExpression elem) {
        doGraphAddClassSeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassSeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#classSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassSeq(ClassSequenceExpression elem) {
        doGraphRemoveClassSeq(elem);
    }

    @Override
    public ClassSequenceExpression getClassSeq() {
        return __implClassSeq;
    }

    @Override
    public void setClassSeq(ClassSequenceExpression elem) {
        if (__implClassSeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveClassSeq(__implClassSeq);
        } else if (__implClassSeq == null) {
            doDispatchAddClassSeq(elem);
        } else {
            doDispatchRemoveClassSeq(__implClassSeq);
            doDispatchAddClassSeq(elem);
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
     * Constructor for the implementation of DisjointClasses
     */
    public Owl2DisjointClassesImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/owl2#classSeq
        this.__implClassSeq = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#file
        this.__implFile = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#line
        this.__implLine = 0;
    }
}
