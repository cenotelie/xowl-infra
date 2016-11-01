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
 * The default implementation for FacetRestriction
 * Original OWL class is http://xowl.org/infra/lang/owl2#FacetRestriction
 *
 * @author xOWL code generator
 */
public class Owl2FacetRestrictionImpl implements FacetRestriction {
    /**
     * The backing data for the property ConstrainingFacet
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#constrainingFacet
     */
    private IRI __implConstrainingFacet;

    /**
     * Adds a value to the property ConstrainingFacet
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingFacet
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddConstrainingFacet(IRI elem) {
        __implConstrainingFacet = elem;
    }

    /**
     * Removes a value from the property ConstrainingFacet
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingFacet
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveConstrainingFacet(IRI elem) {
        __implConstrainingFacet = null;
    }

    /**
     * Adds a value to the property ConstrainingFacet
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingFacet
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddConstrainingFacet(IRI elem) {
        doSimpleAddConstrainingFacet(elem);
    }

    /**
     * Removes a value from the property ConstrainingFacet
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingFacet
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveConstrainingFacet(IRI elem) {
        doSimpleRemoveConstrainingFacet(elem);
    }

    /**
     * Tries to add a value to the property ConstrainingFacet and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingFacet
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddConstrainingFacet(IRI elem) {
        doPropertyAddConstrainingFacet(elem);
    }

    /**
     * Tries to remove a value from the property ConstrainingFacet and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingFacet
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveConstrainingFacet(IRI elem) {
        doPropertyRemoveConstrainingFacet(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ConstrainingFacet
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingFacet
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddConstrainingFacet(IRI elem) {
        doGraphAddConstrainingFacet(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ConstrainingFacet
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingFacet
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveConstrainingFacet(IRI elem) {
        doGraphRemoveConstrainingFacet(elem);
    }

    @Override
    public IRI getConstrainingFacet() {
        return __implConstrainingFacet;
    }

    @Override
    public void setConstrainingFacet(IRI elem) {
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
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#constrainingValue
     */
    private Literal __implConstrainingValue;

    /**
     * Adds a value to the property ConstrainingValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingValue
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddConstrainingValue(Literal elem) {
        __implConstrainingValue = elem;
    }

    /**
     * Removes a value from the property ConstrainingValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingValue
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveConstrainingValue(Literal elem) {
        __implConstrainingValue = null;
    }

    /**
     * Adds a value to the property ConstrainingValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingValue
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddConstrainingValue(Literal elem) {
        doSimpleAddConstrainingValue(elem);
    }

    /**
     * Removes a value from the property ConstrainingValue
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingValue
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveConstrainingValue(Literal elem) {
        doSimpleRemoveConstrainingValue(elem);
    }

    /**
     * Tries to add a value to the property ConstrainingValue and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingValue
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddConstrainingValue(Literal elem) {
        doPropertyAddConstrainingValue(elem);
    }

    /**
     * Tries to remove a value from the property ConstrainingValue and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingValue
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveConstrainingValue(Literal elem) {
        doPropertyRemoveConstrainingValue(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ConstrainingValue
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingValue
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddConstrainingValue(Literal elem) {
        doGraphAddConstrainingValue(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ConstrainingValue
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#constrainingValue
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveConstrainingValue(Literal elem) {
        doGraphRemoveConstrainingValue(elem);
    }

    @Override
    public Literal getConstrainingValue() {
        return __implConstrainingValue;
    }

    @Override
    public void setConstrainingValue(Literal elem) {
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
        // initialize property http://xowl.org/infra/lang/owl2#constrainingFacet
        this.__implConstrainingFacet = null;
        // initialize property http://xowl.org/infra/lang/owl2#constrainingValue
        this.__implConstrainingValue = null;
    }
}
