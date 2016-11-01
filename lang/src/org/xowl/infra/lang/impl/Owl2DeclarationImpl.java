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
 * The default implementation for the concrete OWL class Declaration
 *
 * @author xOWL code generator
 */
public class Owl2DeclarationImpl implements org.xowl.infra.lang.owl2.Declaration {
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
     * The backing data for the property Entity
     */
    private org.xowl.infra.lang.owl2.IRI __implEntity;

    /**
     * Adds a value to the property Entity
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddEntity(org.xowl.infra.lang.owl2.IRI elem) {
        __implEntity = elem;
    }

    /**
     * Removes a value from the property Entity
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveEntity(org.xowl.infra.lang.owl2.IRI elem) {
        __implEntity = null;
    }

    /**
     * Adds a value to the property Entity
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddEntity(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddEntity(elem);
    }

    /**
     * Removes a value from the property Entity
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveEntity(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveEntity(elem);
    }

    /**
     * Tries to add a value to the property Entity and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddEntity(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddEntity(elem);
    }

    /**
     * Tries to remove a value from the property Entity and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveEntity(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveEntity(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Entity
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddEntity(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddEntity(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Entity
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveEntity(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphRemoveEntity(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IRI getEntity() {
        return __implEntity;
    }

    @Override
    public void setEntity(org.xowl.infra.lang.owl2.IRI elem) {
        if (__implEntity == elem)
            return;
        if (elem == null) {
            doDispatchRemoveEntity(__implEntity);
        } else if (__implEntity == null) {
            doDispatchAddEntity(elem);
        } else {
            doDispatchRemoveEntity(__implEntity);
            doDispatchAddEntity(elem);
        }
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
     * The backing data for the property Type
     */
    private String __implType;

    @Override
    public String getType() {
        return __implType;
    }

    @Override
    public void setType(String elem) {
        __implType = elem;
    }

    /**
     * Constructor for the implementation of Declaration
     */
    public Owl2DeclarationImpl() {
        this.__implAnnotations = new ArrayList<>();
        this.__implEntity = null;
        this.__implFile = null;
        this.__implLine = 0;
        this.__implType = null;
    }
}
