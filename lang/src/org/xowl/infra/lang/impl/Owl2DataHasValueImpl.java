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
 * The default implementation for DataHasValue
 * Original OWL class is http://xowl.org/infra/lang/owl2#DataHasValue
 *
 * @author xOWL code generator
 */
public class Owl2DataHasValueImpl implements DataHasValue {
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
     * The backing data for the property Literal
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#literal
     */
    private LiteralExpression __implLiteral;

    /**
     * Adds a value to the property Literal
     * Original OWL property is http://xowl.org/infra/lang/owl2#literal
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddLiteral(LiteralExpression elem) {
        __implLiteral = elem;
    }

    /**
     * Removes a value from the property Literal
     * Original OWL property is http://xowl.org/infra/lang/owl2#literal
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveLiteral(LiteralExpression elem) {
        __implLiteral = null;
    }

    /**
     * Adds a value to the property Literal
     * Original OWL property is http://xowl.org/infra/lang/owl2#literal
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddLiteral(LiteralExpression elem) {
        doSimpleAddLiteral(elem);
    }

    /**
     * Removes a value from the property Literal
     * Original OWL property is http://xowl.org/infra/lang/owl2#literal
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveLiteral(LiteralExpression elem) {
        doSimpleRemoveLiteral(elem);
    }

    /**
     * Tries to add a value to the property Literal and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#literal
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddLiteral(LiteralExpression elem) {
        doPropertyAddLiteral(elem);
    }

    /**
     * Tries to remove a value from the property Literal and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#literal
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveLiteral(LiteralExpression elem) {
        doPropertyRemoveLiteral(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Literal
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#literal
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddLiteral(LiteralExpression elem) {
        doGraphAddLiteral(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Literal
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#literal
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveLiteral(LiteralExpression elem) {
        doGraphRemoveLiteral(elem);
    }

    @Override
    public LiteralExpression getLiteral() {
        return __implLiteral;
    }

    @Override
    public void setLiteral(LiteralExpression elem) {
        if (__implLiteral == elem)
            return;
        if (elem == null) {
            doDispatchRemoveLiteral(__implLiteral);
        } else if (__implLiteral == null) {
            doDispatchAddLiteral(elem);
        } else {
            doDispatchRemoveLiteral(__implLiteral);
            doDispatchAddLiteral(elem);
        }
    }

    /**
     * Constructor for the implementation of DataHasValue
     */
    public Owl2DataHasValueImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#dataProperty
        this.__implDataProperty = null;
        // initialize property http://xowl.org/infra/lang/owl2#literal
        this.__implLiteral = null;
    }
}
