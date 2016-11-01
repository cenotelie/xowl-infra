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
 * The default implementation for the concrete OWL class HasKey
 *
 * @author xOWL code generator
 */
public class Owl2HasKeyImpl implements org.xowl.infra.lang.owl2.HasKey {
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
     * The backing data for the property Classe
     */
    private org.xowl.infra.lang.owl2.ClassExpression __implClasse;

    /**
     * Adds a value to the property Classe
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        __implClasse = elem;
    }

    /**
     * Removes a value from the property Classe
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        __implClasse = null;
    }

    /**
     * Adds a value to the property Classe
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        doSimpleAddClasse(elem);
    }

    /**
     * Removes a value from the property Classe
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        doSimpleRemoveClasse(elem);
    }

    /**
     * Tries to add a value to the property Classe and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        doPropertyAddClasse(elem);
    }

    /**
     * Tries to remove a value from the property Classe and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        doPropertyRemoveClasse(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Classe
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        doGraphAddClasse(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Classe
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        doGraphRemoveClasse(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.ClassExpression getClasse() {
        return __implClasse;
    }

    @Override
    public void setClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        if (__implClasse == elem)
            return;
        if (elem == null) {
            doDispatchRemoveClasse(__implClasse);
        } else if (__implClasse == null) {
            doDispatchAddClasse(elem);
        } else {
            doDispatchRemoveClasse(__implClasse);
            doDispatchAddClasse(elem);
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
     * The backing data for the property DataPropertySeq
     */
    private org.xowl.infra.lang.owl2.DataPropertySequenceExpression __implDataPropertySeq;

    /**
     * Adds a value to the property DataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        __implDataPropertySeq = elem;
    }

    /**
     * Removes a value from the property DataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        __implDataPropertySeq = null;
    }

    /**
     * Adds a value to the property DataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doSimpleAddDataPropertySeq(elem);
    }

    /**
     * Removes a value from the property DataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doSimpleRemoveDataPropertySeq(elem);
    }

    /**
     * Tries to add a value to the property DataPropertySeq and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doPropertyAddDataPropertySeq(elem);
    }

    /**
     * Tries to remove a value from the property DataPropertySeq and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doPropertyRemoveDataPropertySeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataPropertySeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doGraphAddDataPropertySeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataPropertySeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        doGraphRemoveDataPropertySeq(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.DataPropertySequenceExpression getDataPropertySeq() {
        return __implDataPropertySeq;
    }

    @Override
    public void setDataPropertySeq(org.xowl.infra.lang.owl2.DataPropertySequenceExpression elem) {
        if (__implDataPropertySeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDataPropertySeq(__implDataPropertySeq);
        } else if (__implDataPropertySeq == null) {
            doDispatchAddDataPropertySeq(elem);
        } else {
            doDispatchRemoveDataPropertySeq(__implDataPropertySeq);
            doDispatchAddDataPropertySeq(elem);
        }
    }

    /**
     * The backing data for the property ObjectPropertySeq
     */
    private org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression __implObjectPropertySeq;

    /**
     * Adds a value to the property ObjectPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        __implObjectPropertySeq = elem;
    }

    /**
     * Removes a value from the property ObjectPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        __implObjectPropertySeq = null;
    }

    /**
     * Adds a value to the property ObjectPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doSimpleAddObjectPropertySeq(elem);
    }

    /**
     * Removes a value from the property ObjectPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doSimpleRemoveObjectPropertySeq(elem);
    }

    /**
     * Tries to add a value to the property ObjectPropertySeq and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doPropertyAddObjectPropertySeq(elem);
    }

    /**
     * Tries to remove a value from the property ObjectPropertySeq and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doPropertyRemoveObjectPropertySeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectPropertySeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doGraphAddObjectPropertySeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectPropertySeq
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        doGraphRemoveObjectPropertySeq(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression getObjectPropertySeq() {
        return __implObjectPropertySeq;
    }

    @Override
    public void setObjectPropertySeq(org.xowl.infra.lang.owl2.ObjectPropertySequenceExpression elem) {
        if (__implObjectPropertySeq == elem)
            return;
        if (elem == null) {
            doDispatchRemoveObjectPropertySeq(__implObjectPropertySeq);
        } else if (__implObjectPropertySeq == null) {
            doDispatchAddObjectPropertySeq(elem);
        } else {
            doDispatchRemoveObjectPropertySeq(__implObjectPropertySeq);
            doDispatchAddObjectPropertySeq(elem);
        }
    }

    /**
     * Constructor for the implementation of HasKey
     */
    public Owl2HasKeyImpl() {
        this.__implLine = 0;
        this.__implClasse = null;
        this.__implAnnotations = new ArrayList<>();
        this.__implFile = null;
        this.__implDataPropertySeq = null;
        this.__implObjectPropertySeq = null;
    }
}
