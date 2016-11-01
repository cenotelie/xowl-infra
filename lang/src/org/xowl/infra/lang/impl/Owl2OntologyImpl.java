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
 * The default implementation for the concrete OWL class Ontology
 *
 * @author xOWL code generator
 */
public class Owl2OntologyImpl implements org.xowl.infra.lang.owl2.Ontology {
    /**
     * The backing data for the property Contains
     */
    private List<org.xowl.infra.lang.runtime.Entity> __implContains;

    /**
     * Adds a value to the property Contains
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddContains(org.xowl.infra.lang.runtime.Entity elem) {
        __implContains.add(elem);
    }

    /**
     * Removes a value from the property Contains
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveContains(org.xowl.infra.lang.runtime.Entity elem) {
        __implContains.remove(elem);
    }

    /**
     * Adds a value to the property Contains
     * This method will also update the inverse property ContainedBy
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddContains(org.xowl.infra.lang.runtime.Entity elem) {
        doSimpleAddContains(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleAddContainedBy(this);
    }

    /**
     * Removes a value from the property Contains
     * This method will also update the inverse property ContainedBy
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveContains(org.xowl.infra.lang.runtime.Entity elem) {
        doSimpleRemoveContains(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleRemoveContainedBy(this);
    }

    /**
     * Tries to add a value to the property Contains and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddContains(org.xowl.infra.lang.runtime.Entity elem) {
        doPropertyAddContains(elem);
    }

    /**
     * Tries to remove a value from the property Contains and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveContains(org.xowl.infra.lang.runtime.Entity elem) {
        doPropertyRemoveContains(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Contains
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddContains(org.xowl.infra.lang.runtime.Entity elem) {
        doGraphAddContains(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Contains
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveContains(org.xowl.infra.lang.runtime.Entity elem) {
        doGraphRemoveContains(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Entity> getAllContains() {
        return Collections.unmodifiableCollection(__implContains);
    }

    @Override
    public boolean addContains(org.xowl.infra.lang.runtime.Entity elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implContains.contains(elem))
            return false;
        doDispatchAddContains(elem);
        return true;
    }

    @Override
    public boolean removeContains(org.xowl.infra.lang.runtime.Entity elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implContains.contains(elem))
            return false;
        doDispatchRemoveContains(elem);
        return true;
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
     * The backing data for the property HasIRI
     */
    private org.xowl.infra.lang.owl2.IRI __implHasIRI;

    /**
     * Adds a value to the property HasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        __implHasIRI = elem;
    }

    /**
     * Removes a value from the property HasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        __implHasIRI = null;
    }

    /**
     * Adds a value to the property HasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddHasIRI(elem);
    }

    /**
     * Removes a value from the property HasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveHasIRI(elem);
    }

    /**
     * Tries to add a value to the property HasIRI and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddHasIRI(elem);
    }

    /**
     * Tries to remove a value from the property HasIRI and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveHasIRI(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property HasIRI
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddHasIRI(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property HasIRI
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphRemoveHasIRI(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IRI getHasIRI() {
        return __implHasIRI;
    }

    @Override
    public void setHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        if (__implHasIRI == elem)
            return;
        if (elem == null) {
            doDispatchRemoveHasIRI(__implHasIRI);
        } else if (__implHasIRI == null) {
            doDispatchAddHasIRI(elem);
        } else {
            doDispatchRemoveHasIRI(__implHasIRI);
            doDispatchAddHasIRI(elem);
        }
    }

    /**
     * Constructor for the implementation of Ontology
     */
    public Owl2OntologyImpl() {
        this.__implContains = new ArrayList<>();
        this.__implAnnotations = new ArrayList<>();
        this.__implHasIRI = null;
    }
}
