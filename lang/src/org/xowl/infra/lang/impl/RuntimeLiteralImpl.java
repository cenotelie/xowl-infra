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
 * The default implementation for Literal
 * Original OWL class is http://xowl.org/infra/lang/runtime#Literal
 *
 * @author xOWL code generator
 */
public class RuntimeLiteralImpl implements Literal {
    /**
     * The backing data for the property LangTag
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#langTag
     */
    private String __implLangTag;

    @Override
    public String getLangTag() {
        return __implLangTag;
    }

    @Override
    public void setLangTag(String elem) {
        __implLangTag = elem;
    }

    /**
     * The backing data for the property LexicalValue
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#lexicalValue
     */
    private String __implLexicalValue;

    @Override
    public String getLexicalValue() {
        return __implLexicalValue;
    }

    @Override
    public void setLexicalValue(String elem) {
        __implLexicalValue = elem;
    }

    /**
     * The backing data for the property MemberOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#memberOf
     */
    private Datatype __implMemberOf;

    /**
     * Adds a value to the property MemberOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#memberOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddMemberOf(Datatype elem) {
        __implMemberOf = elem;
    }

    /**
     * Removes a value from the property MemberOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#memberOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveMemberOf(Datatype elem) {
        __implMemberOf = null;
    }

    /**
     * Adds a value to the property MemberOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#memberOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddMemberOf(Datatype elem) {
        doSimpleAddMemberOf(elem);
    }

    /**
     * Removes a value from the property MemberOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#memberOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveMemberOf(Datatype elem) {
        doSimpleRemoveMemberOf(elem);
    }

    /**
     * Tries to add a value to the property MemberOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#memberOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddMemberOf(Datatype elem) {
        doPropertyAddMemberOf(elem);
    }

    /**
     * Tries to remove a value from the property MemberOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#memberOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveMemberOf(Datatype elem) {
        doPropertyRemoveMemberOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property MemberOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#memberOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddMemberOf(Datatype elem) {
        doGraphAddMemberOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property MemberOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#memberOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveMemberOf(Datatype elem) {
        doGraphRemoveMemberOf(elem);
    }

    @Override
    public Datatype getMemberOf() {
        return __implMemberOf;
    }

    @Override
    public void setMemberOf(Datatype elem) {
        if (__implMemberOf == elem)
            return;
        if (elem == null) {
            doDispatchRemoveMemberOf(__implMemberOf);
        } else if (__implMemberOf == null) {
            doDispatchAddMemberOf(elem);
        } else {
            doDispatchRemoveMemberOf(__implMemberOf);
            doDispatchAddMemberOf(elem);
        }
    }

    /**
     * Constructor for the implementation of Literal
     */
    public RuntimeLiteralImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#langTag
        this.__implLangTag = null;
        // initialize property http://xowl.org/infra/lang/runtime#lexicalValue
        this.__implLexicalValue = null;
        // initialize property http://xowl.org/infra/lang/runtime#memberOf
        this.__implMemberOf = null;
    }
}
