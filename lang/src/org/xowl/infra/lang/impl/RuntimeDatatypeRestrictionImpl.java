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

import org.xowl.infra.lang.runtime.*;
import org.xowl.infra.lang.runtime.Class;

import java.util.*;

/**
 * The default implementation for DatatypeRestriction
 * Original OWL class is http://xowl.org/infra/lang/runtime#DatatypeRestriction
 *
 * @author xOWL code generator
 */
public class RuntimeDatatypeRestrictionImpl implements DatatypeRestriction {
    /**
     * The backing data for the property Facet
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#facet
     */
    private org.xowl.infra.lang.owl2.IRI __implFacet;

    /**
     * Adds a value to the property Facet
     * Original OWL property is http://xowl.org/infra/lang/runtime#facet
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddFacet(org.xowl.infra.lang.owl2.IRI elem) {
        __implFacet = elem;
    }

    /**
     * Removes a value from the property Facet
     * Original OWL property is http://xowl.org/infra/lang/runtime#facet
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveFacet(org.xowl.infra.lang.owl2.IRI elem) {
        __implFacet = null;
    }

    /**
     * Adds a value to the property Facet
     * Original OWL property is http://xowl.org/infra/lang/runtime#facet
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddFacet(elem);
    }

    /**
     * Removes a value from the property Facet
     * Original OWL property is http://xowl.org/infra/lang/runtime#facet
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveFacet(elem);
    }

    /**
     * Tries to add a value to the property Facet and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#facet
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddFacet(elem);
    }

    /**
     * Tries to remove a value from the property Facet and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#facet
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveFacet(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Facet
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#facet
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddFacet(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Facet
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#facet
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveFacet(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphRemoveFacet(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IRI getFacet() {
        return __implFacet;
    }

    @Override
    public void setFacet(org.xowl.infra.lang.owl2.IRI elem) {
        if (__implFacet == elem)
            return;
        if (elem == null) {
            doDispatchRemoveFacet(__implFacet);
        } else if (__implFacet == null) {
            doDispatchAddFacet(elem);
        } else {
            doDispatchRemoveFacet(__implFacet);
            doDispatchAddFacet(elem);
        }
    }

    /**
     * The backing data for the property ValueLiteral
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#valueLiteral
     */
    private Literal __implValueLiteral;

    /**
     * Adds a value to the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddValueLiteral(Literal elem) {
        __implValueLiteral = elem;
    }

    /**
     * Removes a value from the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveValueLiteral(Literal elem) {
        __implValueLiteral = null;
    }

    /**
     * Adds a value to the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddValueLiteral(Literal elem) {
        doSimpleAddValueLiteral(elem);
    }

    /**
     * Removes a value from the property ValueLiteral
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveValueLiteral(Literal elem) {
        doSimpleRemoveValueLiteral(elem);
    }

    /**
     * Tries to add a value to the property ValueLiteral and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddValueLiteral(Literal elem) {
        doPropertyAddValueLiteral(elem);
    }

    /**
     * Tries to remove a value from the property ValueLiteral and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveValueLiteral(Literal elem) {
        doPropertyRemoveValueLiteral(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ValueLiteral
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddValueLiteral(Literal elem) {
        doGraphAddValueLiteral(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ValueLiteral
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#valueLiteral
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveValueLiteral(Literal elem) {
        doGraphRemoveValueLiteral(elem);
    }

    @Override
    public Literal getValueLiteral() {
        return __implValueLiteral;
    }

    @Override
    public void setValueLiteral(Literal elem) {
        if (__implValueLiteral == elem)
            return;
        if (elem == null) {
            doDispatchRemoveValueLiteral(__implValueLiteral);
        } else if (__implValueLiteral == null) {
            doDispatchAddValueLiteral(elem);
        } else {
            doDispatchRemoveValueLiteral(__implValueLiteral);
            doDispatchAddValueLiteral(elem);
        }
    }

    /**
     * Constructor for the implementation of DatatypeRestriction
     */
    public RuntimeDatatypeRestrictionImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#facet
        this.__implFacet = null;
        // initialize property http://xowl.org/infra/lang/runtime#valueLiteral
        this.__implValueLiteral = null;
    }
}
