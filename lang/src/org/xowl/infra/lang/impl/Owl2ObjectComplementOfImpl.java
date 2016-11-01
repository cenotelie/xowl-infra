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
 * The default implementation for the concrete OWL class ObjectComplementOf
 *
 * @author xOWL code generator
 */
public class Owl2ObjectComplementOfImpl implements org.xowl.infra.lang.owl2.ObjectComplementOf {
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
     * Constructor for the implementation of ObjectComplementOf
     */
    public Owl2ObjectComplementOfImpl() {
        this.__implClasse = null;
    }
}
