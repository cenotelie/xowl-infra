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
 * The default implementation for the concrete OWL class LiteralElement
 *
 * @author xOWL code generator
 */
public class Owl2LiteralElementImpl implements org.xowl.infra.lang.owl2.LiteralElement {
    /**
     * The backing data for the property Index
     */
    private int __implIndex;

    @Override
    public int getIndex() {
        return __implIndex;
    }

    @Override
    public void setIndex(int elem) {
        __implIndex = elem;
    }

    /**
     * The backing data for the property Literal
     */
    private org.xowl.infra.lang.owl2.LiteralExpression __implLiteral;

    /**
     * Adds a value to the property Literal
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        __implLiteral = elem;
    }

    /**
     * Removes a value from the property Literal
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        __implLiteral = null;
    }

    /**
     * Adds a value to the property Literal
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doSimpleAddLiteral(elem);
    }

    /**
     * Removes a value from the property Literal
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doSimpleRemoveLiteral(elem);
    }

    /**
     * Tries to add a value to the property Literal and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doPropertyAddLiteral(elem);
    }

    /**
     * Tries to remove a value from the property Literal and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doPropertyRemoveLiteral(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Literal
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doGraphAddLiteral(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Literal
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
        doGraphRemoveLiteral(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.LiteralExpression getLiteral() {
        return __implLiteral;
    }

    @Override
    public void setLiteral(org.xowl.infra.lang.owl2.LiteralExpression elem) {
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
     * Constructor for the implementation of LiteralElement
     */
    public Owl2LiteralElementImpl() {
        this.__implIndex = 0;
        this.__implLiteral = null;
    }
}
