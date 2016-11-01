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
 * The default implementation for the concrete OWL class Function
 *
 * @author xOWL code generator
 */
public class RuntimeFunctionImpl implements org.xowl.infra.lang.runtime.Function {
    /**
     * The backing data for the property DefinedAs
     */
    private Object __implDefinedAs;

    /**
     * Adds a value to the property DefinedAs
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDefinedAs(Object elem) {
        __implDefinedAs = elem;
    }

    /**
     * Removes a value from the property DefinedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDefinedAs(Object elem) {
        __implDefinedAs = null;
    }

    /**
     * Adds a value to the property DefinedAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDefinedAs(Object elem) {
        doSimpleAddDefinedAs(elem);
    }

    /**
     * Removes a value from the property DefinedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDefinedAs(Object elem) {
        doSimpleRemoveDefinedAs(elem);
    }

    /**
     * Tries to add a value to the property DefinedAs and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDefinedAs(Object elem) {
        doPropertyAddDefinedAs(elem);
    }

    /**
     * Tries to remove a value from the property DefinedAs and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDefinedAs(Object elem) {
        doPropertyRemoveDefinedAs(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DefinedAs
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDefinedAs(Object elem) {
        doGraphAddDefinedAs(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DefinedAs
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDefinedAs(Object elem) {
        doGraphRemoveDefinedAs(elem);
    }

    @Override
    public Object getDefinedAs() {
        return __implDefinedAs;
    }

    @Override
    public void setDefinedAs(Object elem) {
        if (__implDefinedAs == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDefinedAs(__implDefinedAs);
        } else if (__implDefinedAs == null) {
            doDispatchAddDefinedAs(elem);
        } else {
            doDispatchRemoveDefinedAs(__implDefinedAs);
            doDispatchAddDefinedAs(elem);
        }
    }

    /**
     * The backing data for the property InterpretationOf
     */
    private org.xowl.infra.lang.runtime.Entity __implInterpretationOf;

    /**
     * Adds a value to the property InterpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        __implInterpretationOf = elem;
    }

    /**
     * Removes a value from the property InterpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        __implInterpretationOf = null;
    }

    /**
     * Adds a value to the property InterpretationOf
     * This method will also update the inverse property InterpretedAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doSimpleAddInterpretationOf(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleAddInterpretedAs(this);
    }

    /**
     * Removes a value from the property InterpretationOf
     * This method will also update the inverse property InterpretedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doSimpleRemoveInterpretationOf(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleRemoveInterpretedAs(this);
    }

    /**
     * Tries to add a value to the property InterpretationOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doPropertyAddInterpretationOf(elem);
    }

    /**
     * Tries to remove a value from the property InterpretationOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doPropertyRemoveInterpretationOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property InterpretationOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doGraphAddInterpretationOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property InterpretationOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        doGraphRemoveInterpretationOf(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Entity getInterpretationOf() {
        return __implInterpretationOf;
    }

    @Override
    public void setInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        if (__implInterpretationOf == elem)
            return;
        if (elem == null) {
            doDispatchRemoveInterpretationOf(__implInterpretationOf);
        } else if (__implInterpretationOf == null) {
            doDispatchAddInterpretationOf(elem);
        } else {
            doDispatchRemoveInterpretationOf(__implInterpretationOf);
            doDispatchAddInterpretationOf(elem);
        }
    }

    /**
     * Constructor for the implementation of Function
     */
    public RuntimeFunctionImpl() {
        this.__implDefinedAs = null;
        this.__implInterpretationOf = null;
    }
}
