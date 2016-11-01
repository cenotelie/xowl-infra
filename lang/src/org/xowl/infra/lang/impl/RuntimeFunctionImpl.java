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
 * The default implementation for Function
 * Original OWL class is http://xowl.org/infra/lang/runtime#Function
 *
 * @author xOWL code generator
 */
public class RuntimeFunctionImpl implements Function {
    /**
     * The backing data for the property DefinedAs
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#definedAs
     */
    private java.lang.Object __implDefinedAs;

    /**
     * Adds a value to the property DefinedAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#definedAs
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDefinedAs(java.lang.Object elem) {
        __implDefinedAs = elem;
    }

    /**
     * Removes a value from the property DefinedAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#definedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDefinedAs(java.lang.Object elem) {
        __implDefinedAs = null;
    }

    /**
     * Adds a value to the property DefinedAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#definedAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDefinedAs(java.lang.Object elem) {
        doSimpleAddDefinedAs(elem);
    }

    /**
     * Removes a value from the property DefinedAs
     * Original OWL property is http://xowl.org/infra/lang/runtime#definedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDefinedAs(java.lang.Object elem) {
        doSimpleRemoveDefinedAs(elem);
    }

    /**
     * Tries to add a value to the property DefinedAs and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#definedAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDefinedAs(java.lang.Object elem) {
        doPropertyAddDefinedAs(elem);
    }

    /**
     * Tries to remove a value from the property DefinedAs and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#definedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDefinedAs(java.lang.Object elem) {
        doPropertyRemoveDefinedAs(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DefinedAs
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#definedAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDefinedAs(java.lang.Object elem) {
        doGraphAddDefinedAs(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DefinedAs
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#definedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDefinedAs(java.lang.Object elem) {
        doGraphRemoveDefinedAs(elem);
    }

    @Override
    public java.lang.Object getDefinedAs() {
        return __implDefinedAs;
    }

    @Override
    public void setDefinedAs(java.lang.Object elem) {
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
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#interpretationOf
     */
    private Entity __implInterpretationOf;

    /**
     * Adds a value to the property InterpretationOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInterpretationOf(Entity elem) {
        __implInterpretationOf = elem;
    }

    /**
     * Removes a value from the property InterpretationOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInterpretationOf(Entity elem) {
        __implInterpretationOf = null;
    }

    /**
     * Adds a value to the property InterpretationOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     * This method will also update the inverse property InterpretedAs
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInterpretationOf(Entity elem) {
        doSimpleAddInterpretationOf(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleAddInterpretedAs(this);
    }

    /**
     * Removes a value from the property InterpretationOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     * This method will also update the inverse property InterpretedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInterpretationOf(Entity elem) {
        doSimpleRemoveInterpretationOf(elem);
        if (elem instanceof RuntimeEntityImpl)
            ((RuntimeEntityImpl) elem).doSimpleRemoveInterpretedAs(this);
    }

    /**
     * Tries to add a value to the property InterpretationOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInterpretationOf(Entity elem) {
        doPropertyAddInterpretationOf(elem);
    }

    /**
     * Tries to remove a value from the property InterpretationOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInterpretationOf(Entity elem) {
        doPropertyRemoveInterpretationOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property InterpretationOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInterpretationOf(Entity elem) {
        doGraphAddInterpretationOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property InterpretationOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#interpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInterpretationOf(Entity elem) {
        doGraphRemoveInterpretationOf(elem);
    }

    @Override
    public Entity getInterpretationOf() {
        return __implInterpretationOf;
    }

    @Override
    public void setInterpretationOf(Entity elem) {
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
        // initialize property http://xowl.org/infra/lang/runtime#definedAs
        this.__implDefinedAs = null;
        // initialize property http://xowl.org/infra/lang/runtime#interpretationOf
        this.__implInterpretationOf = null;
    }
}
