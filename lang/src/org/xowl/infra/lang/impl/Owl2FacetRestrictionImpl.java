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
 * The default implementation for the concrete OWL class FacetRestriction
 *
 * @author xOWL code generator
 */
public class Owl2FacetRestrictionImpl implements org.xowl.infra.lang.owl2.FacetRestriction {
    /**
     * The backing data for the property ConstrainingFacet
     */
    private org.xowl.infra.lang.owl2.IRI __implConstrainingFacet;

    /**
     * Adds a value to the property ConstrainingFacet
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        __implConstrainingFacet = elem;
    }

    /**
     * Removes a value from the property ConstrainingFacet
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        __implConstrainingFacet = null;
    }

    /**
     * Adds a value to the property ConstrainingFacet
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddConstrainingFacet(elem);
    }

    /**
     * Removes a value from the property ConstrainingFacet
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveConstrainingFacet(elem);
    }

    /**
     * Tries to add a value to the property ConstrainingFacet and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddConstrainingFacet(elem);
    }

    /**
     * Tries to remove a value from the property ConstrainingFacet and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveConstrainingFacet(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ConstrainingFacet
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddConstrainingFacet(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ConstrainingFacet
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphRemoveConstrainingFacet(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IRI getConstrainingFacet() {
        return __implConstrainingFacet;
    }

    @Override
    public void setConstrainingFacet(org.xowl.infra.lang.owl2.IRI elem) {
        if (__implConstrainingFacet == elem)
            return;
        if (elem == null) {
            doDispatchRemoveConstrainingFacet(__implConstrainingFacet);
        } else if (__implConstrainingFacet == null) {
            doDispatchAddConstrainingFacet(elem);
        } else {
            doDispatchRemoveConstrainingFacet(__implConstrainingFacet);
            doDispatchAddConstrainingFacet(elem);
        }
    }

    /**
     * The backing data for the property ConstrainingValue
     */
    private org.xowl.infra.lang.owl2.Literal __implConstrainingValue;

    /**
     * Adds a value to the property ConstrainingValue
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        __implConstrainingValue = elem;
    }

    /**
     * Removes a value from the property ConstrainingValue
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        __implConstrainingValue = null;
    }

    /**
     * Adds a value to the property ConstrainingValue
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        doSimpleAddConstrainingValue(elem);
    }

    /**
     * Removes a value from the property ConstrainingValue
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        doSimpleRemoveConstrainingValue(elem);
    }

    /**
     * Tries to add a value to the property ConstrainingValue and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        doPropertyAddConstrainingValue(elem);
    }

    /**
     * Tries to remove a value from the property ConstrainingValue and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        doPropertyRemoveConstrainingValue(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ConstrainingValue
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        doGraphAddConstrainingValue(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ConstrainingValue
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        doGraphRemoveConstrainingValue(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.Literal getConstrainingValue() {
        return __implConstrainingValue;
    }

    @Override
    public void setConstrainingValue(org.xowl.infra.lang.owl2.Literal elem) {
        if (__implConstrainingValue == elem)
            return;
        if (elem == null) {
            doDispatchRemoveConstrainingValue(__implConstrainingValue);
        } else if (__implConstrainingValue == null) {
            doDispatchAddConstrainingValue(elem);
        } else {
            doDispatchRemoveConstrainingValue(__implConstrainingValue);
            doDispatchAddConstrainingValue(elem);
        }
    }

    /**
     * Constructor for the implementation of FacetRestriction
     */
    public Owl2FacetRestrictionImpl() {
        this.__implConstrainingFacet = null;
        this.__implConstrainingValue = null;
    }
}
