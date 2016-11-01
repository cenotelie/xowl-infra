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
 * The default implementation for the concrete OWL class Literal
 *
 * @author xOWL code generator
 */
public class RuntimeLiteralImpl implements org.xowl.infra.lang.runtime.Literal {
    /**
     * The backing data for the property LangTag
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
     */
    private org.xowl.infra.lang.runtime.Datatype __implMemberOf;

    /**
     * Adds a value to the property MemberOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
        __implMemberOf = elem;
    }

    /**
     * Removes a value from the property MemberOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
        __implMemberOf = null;
    }

    /**
     * Adds a value to the property MemberOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleAddMemberOf(elem);
    }

    /**
     * Removes a value from the property MemberOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleRemoveMemberOf(elem);
    }

    /**
     * Tries to add a value to the property MemberOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyAddMemberOf(elem);
    }

    /**
     * Tries to remove a value from the property MemberOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyRemoveMemberOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property MemberOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphAddMemberOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property MemberOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphRemoveMemberOf(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Datatype getMemberOf() {
        return __implMemberOf;
    }

    @Override
    public void setMemberOf(org.xowl.infra.lang.runtime.Datatype elem) {
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
        this.__implLangTag = null;
        this.__implLexicalValue = null;
        this.__implMemberOf = null;
    }
}
