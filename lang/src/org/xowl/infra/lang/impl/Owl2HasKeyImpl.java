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
 * The default implementation for HasKey
 * Original OWL class is http://xowl.org/infra/lang/owl2#HasKey
 *
 * @author xOWL code generator
 */
public class Owl2HasKeyImpl implements HasKey {
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
     * The backing data for the property Classe
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#classe
     */
    private ClassExpression __implClasse;

    /**
     * Adds a value to the property Classe
     * Original OWL property is http://xowl.org/infra/lang/owl2#classe
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClasse(ClassExpression elem) {
        __implClasse = elem;
    }

    /**
     * Removes a value from the property Classe
     * Original OWL property is http://xowl.org/infra/lang/owl2#classe
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClasse(ClassExpression elem) {
        __implClasse = null;
    }

    /**
     * Adds a value to the property Classe
     * Original OWL property is http://xowl.org/infra/lang/owl2#classe
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClasse(ClassExpression elem) {
        doSimpleAddClasse(elem);
    }

    /**
     * Removes a value from the property Classe
     * Original OWL property is http://xowl.org/infra/lang/owl2#classe
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClasse(ClassExpression elem) {
        doSimpleRemoveClasse(elem);
    }

    /**
     * Tries to add a value to the property Classe and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#classe
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClasse(ClassExpression elem) {
        doPropertyAddClasse(elem);
    }

    /**
     * Tries to remove a value from the property Classe and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#classe
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClasse(ClassExpression elem) {
        doPropertyRemoveClasse(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Classe
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#classe
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClasse(ClassExpression elem) {
        doGraphAddClasse(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Classe
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#classe
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClasse(ClassExpression elem) {
        doGraphRemoveClasse(elem);
    }

    @Override
    public ClassExpression getClasse() {
        return __implClasse;
    }

    @Override
    public void setClasse(ClassExpression elem) {
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
     * The backing data for the property DataPropertySeq
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#dataPropertySeq
     */
    private DataPropertySequenceExpression __implDataPropertySeq;

    /**
     * Adds a value to the property DataPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataPropertySeq(DataPropertySequenceExpression elem) {
        __implDataPropertySeq = elem;
    }

    /**
     * Removes a value from the property DataPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataPropertySeq(DataPropertySequenceExpression elem) {
        __implDataPropertySeq = null;
    }

    /**
     * Adds a value to the property DataPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataPropertySeq(DataPropertySequenceExpression elem) {
        doSimpleAddDataPropertySeq(elem);
    }

    /**
     * Removes a value from the property DataPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataPropertySeq(DataPropertySequenceExpression elem) {
        doSimpleRemoveDataPropertySeq(elem);
    }

    /**
     * Tries to add a value to the property DataPropertySeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataPropertySeq(DataPropertySequenceExpression elem) {
        doPropertyAddDataPropertySeq(elem);
    }

    /**
     * Tries to remove a value from the property DataPropertySeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataPropertySeq(DataPropertySequenceExpression elem) {
        doPropertyRemoveDataPropertySeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataPropertySeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataPropertySeq(DataPropertySequenceExpression elem) {
        doGraphAddDataPropertySeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataPropertySeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataPropertySeq(DataPropertySequenceExpression elem) {
        doGraphRemoveDataPropertySeq(elem);
    }

    @Override
    public DataPropertySequenceExpression getDataPropertySeq() {
        return __implDataPropertySeq;
    }

    @Override
    public void setDataPropertySeq(DataPropertySequenceExpression elem) {
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
     * The backing data for the property ObjectPropertySeq
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#objectPropertySeq
     */
    private ObjectPropertySequenceExpression __implObjectPropertySeq;

    /**
     * Adds a value to the property ObjectPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectPropertySeq(ObjectPropertySequenceExpression elem) {
        __implObjectPropertySeq = elem;
    }

    /**
     * Removes a value from the property ObjectPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectPropertySeq(ObjectPropertySequenceExpression elem) {
        __implObjectPropertySeq = null;
    }

    /**
     * Adds a value to the property ObjectPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectPropertySeq(ObjectPropertySequenceExpression elem) {
        doSimpleAddObjectPropertySeq(elem);
    }

    /**
     * Removes a value from the property ObjectPropertySeq
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectPropertySeq(ObjectPropertySequenceExpression elem) {
        doSimpleRemoveObjectPropertySeq(elem);
    }

    /**
     * Tries to add a value to the property ObjectPropertySeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectPropertySeq(ObjectPropertySequenceExpression elem) {
        doPropertyAddObjectPropertySeq(elem);
    }

    /**
     * Tries to remove a value from the property ObjectPropertySeq and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectPropertySeq(ObjectPropertySequenceExpression elem) {
        doPropertyRemoveObjectPropertySeq(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectPropertySeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertySeq
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectPropertySeq(ObjectPropertySequenceExpression elem) {
        doGraphAddObjectPropertySeq(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectPropertySeq
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectPropertySeq
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectPropertySeq(ObjectPropertySequenceExpression elem) {
        doGraphRemoveObjectPropertySeq(elem);
    }

    @Override
    public ObjectPropertySequenceExpression getObjectPropertySeq() {
        return __implObjectPropertySeq;
    }

    @Override
    public void setObjectPropertySeq(ObjectPropertySequenceExpression elem) {
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
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/owl2#classe
        this.__implClasse = null;
        // initialize property http://xowl.org/infra/lang/owl2#dataPropertySeq
        this.__implDataPropertySeq = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#file
        this.__implFile = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#line
        this.__implLine = 0;
        // initialize property http://xowl.org/infra/lang/owl2#objectPropertySeq
        this.__implObjectPropertySeq = null;
    }
}
