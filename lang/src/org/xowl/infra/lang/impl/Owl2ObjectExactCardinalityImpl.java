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
 * The default implementation for ObjectExactCardinality
 * Original OWL class is http://xowl.org/infra/lang/owl2#ObjectExactCardinality
 *
 * @author xOWL code generator
 */
public class Owl2ObjectExactCardinalityImpl implements ObjectExactCardinality {
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
     * The backing data for the property ObjectProperty
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#objectProperty
     */
    private ObjectPropertyExpression __implObjectProperty;

    /**
     * Adds a value to the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddObjectProperty(ObjectPropertyExpression elem) {
        __implObjectProperty = elem;
    }

    /**
     * Removes a value from the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveObjectProperty(ObjectPropertyExpression elem) {
        __implObjectProperty = null;
    }

    /**
     * Adds a value to the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddObjectProperty(ObjectPropertyExpression elem) {
        doSimpleAddObjectProperty(elem);
    }

    /**
     * Removes a value from the property ObjectProperty
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveObjectProperty(ObjectPropertyExpression elem) {
        doSimpleRemoveObjectProperty(elem);
    }

    /**
     * Tries to add a value to the property ObjectProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddObjectProperty(ObjectPropertyExpression elem) {
        doPropertyAddObjectProperty(elem);
    }

    /**
     * Tries to remove a value from the property ObjectProperty and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveObjectProperty(ObjectPropertyExpression elem) {
        doPropertyRemoveObjectProperty(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddObjectProperty(ObjectPropertyExpression elem) {
        doGraphAddObjectProperty(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ObjectProperty
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#objectProperty
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveObjectProperty(ObjectPropertyExpression elem) {
        doGraphRemoveObjectProperty(elem);
    }

    @Override
    public ObjectPropertyExpression getObjectProperty() {
        return __implObjectProperty;
    }

    @Override
    public void setObjectProperty(ObjectPropertyExpression elem) {
        if (__implObjectProperty == elem)
            return;
        if (elem == null) {
            doDispatchRemoveObjectProperty(__implObjectProperty);
        } else if (__implObjectProperty == null) {
            doDispatchAddObjectProperty(elem);
        } else {
            doDispatchRemoveObjectProperty(__implObjectProperty);
            doDispatchAddObjectProperty(elem);
        }
    }

    /**
     * Constructor for the implementation of ObjectExactCardinality
     */
    public Owl2ObjectExactCardinalityImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#cardinality
        this.__implCardinality = null;
        // initialize property http://xowl.org/infra/lang/owl2#classe
        this.__implClasse = null;
        // initialize property http://xowl.org/infra/lang/owl2#objectProperty
        this.__implObjectProperty = null;
    }
}
