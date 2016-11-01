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
 * The default implementation for the concrete OWL class DifferentIndividuals
 *
 * @author xOWL code generator
 */
public class Owl2DifferentIndividualsImpl implements org.xowl.infra.lang.owl2.DifferentIndividuals {
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
     * The backing data for the property IndividualSeq
     */
    private org.xowl.infra.lang.owl2.IndividualSequenceExpression __implIndividualSeq;

    /**
     * Adds a value to the property IndividualSeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        __implIndividualSeq = elem;
    }

    /**
     * Removes a value from the property IndividualSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        __implIndividualSeq = null;
    }

    /**
     * Adds a value to the property IndividualSeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doSimpleAddIndividualSeq(elem);
    }

    /**
     * Removes a value from the property IndividualSeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doSimpleRemoveIndividualSeq(elem);
    }

    /**
     * Tries to add a value to the property IndividualSeq and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doPropertyAddIndividualSeq(elem);
    }

    /**
     * Tries to remove a value from the property IndividualSeq and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doPropertyRemoveIndividualSeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property IndividualSeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doGraphAddIndividualSeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property IndividualSeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        doGraphRemoveIndividualSeq(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IndividualSequenceExpression getIndividualSeq() {
        return __implIndividualSeq;
    }

    @Override
    public void setIndividualSeq(org.xowl.infra.lang.owl2.IndividualSequenceExpression elem) {
        if (__implIndividualSeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveIndividualSeq(__implIndividualSeq);
        } else if (__implIndividualSeq == null) {
            doDispatchAddIndividualSeq(elem);
        } else {
            doDispatchRemoveIndividualSeq(__implIndividualSeq);
            doDispatchAddIndividualSeq(elem);
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
     * Constructor for the implementation of DifferentIndividuals
     */
    public Owl2DifferentIndividualsImpl() {
        this.__implAnnotations = new ArrayList<>();
        this.__implFile = null;
        this.__implIndividualSeq = null;
        this.__implLine = 0;
    }
}
