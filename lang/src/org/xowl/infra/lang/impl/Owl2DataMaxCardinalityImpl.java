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
 * The default implementation for DataMaxCardinality
 * Original OWL class is http://xowl.org/infra/lang/owl2#DataMaxCardinality
 *
 * @author xOWL code generator
 */
public class Owl2DataMaxCardinalityImpl implements DataMaxCardinality {
    /**
     * The backing data for the property Cardinality
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#cardinality
     */
    private LiteralExpression __implCardinality;

    /**
     * Adds a value to the property Cardinality
     * Original OWL property is http://xowl.org/infra/lang/owl2#cardinality
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddCardinality(LiteralExpression elem) {
        __implCardinality = elem;
    }

    /**
     * Removes a value from the property Cardinality
     * Original OWL property is http://xowl.org/infra/lang/owl2#cardinality
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveCardinality(LiteralExpression elem) {
        __implCardinality = null;
    }

    /**
     * Adds a value to the property Cardinality
     * Original OWL property is http://xowl.org/infra/lang/owl2#cardinality
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddCardinality(LiteralExpression elem) {
        doSimpleAddCardinality(elem);
    }

    /**
     * Removes a value from the property Cardinality
     * Original OWL property is http://xowl.org/infra/lang/owl2#cardinality
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveCardinality(LiteralExpression elem) {
        doSimpleRemoveCardinality(elem);
    }

    /**
     * Tries to add a value to the property Cardinality and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#cardinality
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddCardinality(LiteralExpression elem) {
        doPropertyAddCardinality(elem);
    }

    /**
     * Tries to remove a value from the property Cardinality and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#cardinality
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveCardinality(LiteralExpression elem) {
        doPropertyRemoveCardinality(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Cardinality
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#cardinality
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddCardinality(LiteralExpression elem) {
        doGraphAddCardinality(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Cardinality
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#cardinality
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveCardinality(LiteralExpression elem) {
        doGraphRemoveCardinality(elem);
    }

    @Override
    public LiteralExpression getCardinality() {
        return __implCardinality;
    }

    @Override
    public void setCardinality(LiteralExpression elem) {
        if (__implCardinality == elem)
            return;
        if (elem == null) {
            doDispatchRemoveCardinality(__implCardinality);
        } else if (__implCardinality == null) {
            doDispatchAddCardinality(elem);
        } else {
            doDispatchRemoveCardinality(__implCardinality);
            doDispatchAddCardinality(elem);
        }
    }

    /**
     * The backing data for the property DataProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#dataProperty
     */
    private DataPropertyExpression __implDataProperty;

    /**
     * Adds a value to the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataProperty(DataPropertyExpression elem) {
        __implDataProperty = elem;
    }

    /**
     * Removes a value from the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataProperty(DataPropertyExpression elem) {
        __implDataProperty = null;
    }

    /**
     * Adds a value to the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataProperty(DataPropertyExpression elem) {
        doSimpleAddDataProperty(elem);
    }

    /**
     * Removes a value from the property DataProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataProperty(DataPropertyExpression elem) {
        doSimpleRemoveDataProperty(elem);
    }

    /**
     * Tries to add a value to the property DataProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataProperty(DataPropertyExpression elem) {
        doPropertyAddDataProperty(elem);
    }

    /**
     * Tries to remove a value from the property DataProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataProperty(DataPropertyExpression elem) {
        doPropertyRemoveDataProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataProperty(DataPropertyExpression elem) {
        doGraphAddDataProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#dataProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataProperty(DataPropertyExpression elem) {
        doGraphRemoveDataProperty(elem);
    }

    @Override
    public DataPropertyExpression getDataProperty() {
        return __implDataProperty;
    }

    @Override
    public void setDataProperty(DataPropertyExpression elem) {
        if (__implDataProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDataProperty(__implDataProperty);
        } else if (__implDataProperty == null) {
            doDispatchAddDataProperty(elem);
        } else {
            doDispatchRemoveDataProperty(__implDataProperty);
            doDispatchAddDataProperty(elem);
        }
    }

    /**
     * The backing data for the property Datarange
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#datarange
     */
    private Datarange __implDatarange;

    /**
     * Adds a value to the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatarange(Datarange elem) {
        __implDatarange = elem;
    }

    /**
     * Removes a value from the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatarange(Datarange elem) {
        __implDatarange = null;
    }

    /**
     * Adds a value to the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatarange(Datarange elem) {
        doSimpleAddDatarange(elem);
    }

    /**
     * Removes a value from the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatarange(Datarange elem) {
        doSimpleRemoveDatarange(elem);
    }

    /**
     * Tries to add a value to the property Datarange and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatarange(Datarange elem) {
        doPropertyAddDatarange(elem);
    }

    /**
     * Tries to remove a value from the property Datarange and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatarange(Datarange elem) {
        doPropertyRemoveDatarange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Datarange
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatarange(Datarange elem) {
        doGraphAddDatarange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Datarange
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatarange(Datarange elem) {
        doGraphRemoveDatarange(elem);
    }

    @Override
    public Datarange getDatarange() {
        return __implDatarange;
    }

    @Override
    public void setDatarange(Datarange elem) {
        if (__implDatarange == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDatarange(__implDatarange);
        } else if (__implDatarange == null) {
            doDispatchAddDatarange(elem);
        } else {
            doDispatchRemoveDatarange(__implDatarange);
            doDispatchAddDatarange(elem);
        }
    }

    /**
     * Constructor for the implementation of DataMaxCardinality
     */
    public Owl2DataMaxCardinalityImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#cardinality
        this.__implCardinality = null;
        // initialize property http://xowl.org/infra/lang/owl2#dataProperty
        this.__implDataProperty = null;
        // initialize property http://xowl.org/infra/lang/owl2#datarange
        this.__implDatarange = null;
    }
}
