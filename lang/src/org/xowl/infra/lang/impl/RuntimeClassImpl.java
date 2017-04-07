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
 * The default implementation for Class
 * Original OWL class is http://xowl.org/infra/lang/runtime#Class
 *
 * @author xOWL code generator
 */
public class RuntimeClassImpl implements Class {
    /**
     * The backing data for the property ClassComplementOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classComplementOf
     */
    private Class __implClassComplementOf;

    /**
     * Adds a value to the property ClassComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassComplementOf(Class elem) {
        __implClassComplementOf = elem;
    }

    /**
     * Removes a value from the property ClassComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassComplementOf(Class elem) {
        __implClassComplementOf = null;
    }

    /**
     * Adds a value to the property ClassComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     * This method will also update the inverse property ClassComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassComplementOf(Class elem) {
        doSimpleAddClassComplementOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddClassComplementOf(this);
    }

    /**
     * Removes a value from the property ClassComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     * This method will also update the inverse property ClassComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassComplementOf(Class elem) {
        doSimpleRemoveClassComplementOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveClassComplementOf(this);
    }

    /**
     * Tries to add a value to the property ClassComplementOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassComplementOf(Class elem) {
        doPropertyAddClassComplementOf(elem);
    }

    /**
     * Tries to remove a value from the property ClassComplementOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassComplementOf(Class elem) {
        doPropertyRemoveClassComplementOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassComplementOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassComplementOf(Class elem) {
        doGraphAddClassComplementOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassComplementOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassComplementOf(Class elem) {
        doGraphRemoveClassComplementOf(elem);
    }

    @Override
    public Class getClassComplementOf() {
        return __implClassComplementOf;
    }

    @Override
    public void setClassComplementOf(Class elem) {
        if (__implClassComplementOf == elem)
            return;
        if (elem == null) {
            doDispatchRemoveClassComplementOf(__implClassComplementOf);
        } else if (__implClassComplementOf == null) {
            doDispatchAddClassComplementOf(elem);
        } else {
            doDispatchRemoveClassComplementOf(__implClassComplementOf);
            doDispatchAddClassComplementOf(elem);
        }
    }

    /**
     * The backing data for the property ClassDisjointWith
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classDisjointWith
     */
    private List<Class> __implClassDisjointWith;

    /**
     * Adds a value to the property ClassDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassDisjointWith(Class elem) {
        __implClassDisjointWith.add(elem);
    }

    /**
     * Removes a value from the property ClassDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassDisjointWith(Class elem) {
        __implClassDisjointWith.remove(elem);
    }

    /**
     * Adds a value to the property ClassDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     * This method will also update the inverse property ClassDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassDisjointWith(Class elem) {
        doSimpleAddClassDisjointWith(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddClassDisjointWith(this);
    }

    /**
     * Removes a value from the property ClassDisjointWith
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     * This method will also update the inverse property ClassDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassDisjointWith(Class elem) {
        doSimpleRemoveClassDisjointWith(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveClassDisjointWith(this);
    }

    /**
     * Tries to add a value to the property ClassDisjointWith and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassDisjointWith(Class elem) {
        doPropertyAddClassDisjointWith(elem);
    }

    /**
     * Tries to remove a value from the property ClassDisjointWith and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassDisjointWith(Class elem) {
        doPropertyRemoveClassDisjointWith(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassDisjointWith
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassDisjointWith(Class elem) {
        doGraphAddClassDisjointWith(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassDisjointWith
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classDisjointWith
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassDisjointWith(Class elem) {
        doGraphRemoveClassDisjointWith(elem);
    }

    @Override
    public Collection<Class> getAllClassDisjointWith() {
        return Collections.unmodifiableCollection(__implClassDisjointWith);
    }

    @Override
    public boolean addClassDisjointWith(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassDisjointWith.contains(elem))
            return false;
        doDispatchAddClassDisjointWith(elem);
        return true;
    }

    @Override
    public boolean removeClassDisjointWith(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassDisjointWith.contains(elem))
            return false;
        doDispatchRemoveClassDisjointWith(elem);
        return true;
    }

    /**
     * The backing data for the property ClassEquivalentTo
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classEquivalentTo
     */
    private List<Class> __implClassEquivalentTo;

    /**
     * Adds a value to the property ClassEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassEquivalentTo(Class elem) {
        __implClassEquivalentTo.add(elem);
    }

    /**
     * Removes a value from the property ClassEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassEquivalentTo(Class elem) {
        __implClassEquivalentTo.remove(elem);
    }

    /**
     * Adds a value to the property ClassEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     * This method will also update the inverse property ClassEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassEquivalentTo(Class elem) {
        doSimpleAddClassEquivalentTo(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddClassEquivalentTo(this);
    }

    /**
     * Removes a value from the property ClassEquivalentTo
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     * This method will also update the inverse property ClassEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassEquivalentTo(Class elem) {
        doSimpleRemoveClassEquivalentTo(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveClassEquivalentTo(this);
    }

    /**
     * Tries to add a value to the property ClassEquivalentTo and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassEquivalentTo(Class elem) {
        doPropertyAddClassEquivalentTo(elem);
    }

    /**
     * Tries to remove a value from the property ClassEquivalentTo and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassEquivalentTo(Class elem) {
        doPropertyRemoveClassEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassEquivalentTo
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassEquivalentTo(Class elem) {
        doGraphAddClassEquivalentTo(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassEquivalentTo
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classEquivalentTo
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassEquivalentTo(Class elem) {
        doGraphRemoveClassEquivalentTo(elem);
    }

    @Override
    public Collection<Class> getAllClassEquivalentTo() {
        return Collections.unmodifiableCollection(__implClassEquivalentTo);
    }

    @Override
    public boolean addClassEquivalentTo(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassEquivalentTo.contains(elem))
            return false;
        doDispatchAddClassEquivalentTo(elem);
        return true;
    }

    @Override
    public boolean removeClassEquivalentTo(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassEquivalentTo.contains(elem))
            return false;
        doDispatchRemoveClassEquivalentTo(elem);
        return true;
    }

    /**
     * The backing data for the property ClassIntersectionOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classIntersectionOf
     */
    private List<Class> __implClassIntersectionOf;

    /**
     * Adds a value to the property ClassIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassIntersectionOf(Class elem) {
        __implClassIntersectionOf.add(elem);
    }

    /**
     * Removes a value from the property ClassIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassIntersectionOf(Class elem) {
        __implClassIntersectionOf.remove(elem);
    }

    /**
     * Adds a value to the property ClassIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassIntersectionOf(Class elem) {
        doSimpleAddClassIntersectionOf(elem);
    }

    /**
     * Removes a value from the property ClassIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassIntersectionOf(Class elem) {
        doSimpleRemoveClassIntersectionOf(elem);
    }

    /**
     * Tries to add a value to the property ClassIntersectionOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassIntersectionOf(Class elem) {
        doPropertyAddClassIntersectionOf(elem);
    }

    /**
     * Tries to remove a value from the property ClassIntersectionOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassIntersectionOf(Class elem) {
        doPropertyRemoveClassIntersectionOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassIntersectionOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassIntersectionOf(Class elem) {
        doGraphAddClassIntersectionOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassIntersectionOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassIntersectionOf(Class elem) {
        doGraphRemoveClassIntersectionOf(elem);
    }

    @Override
    public Collection<Class> getAllClassIntersectionOf() {
        return Collections.unmodifiableCollection(__implClassIntersectionOf);
    }

    @Override
    public boolean addClassIntersectionOf(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassIntersectionOf.contains(elem))
            return false;
        doDispatchAddClassIntersectionOf(elem);
        return true;
    }

    @Override
    public boolean removeClassIntersectionOf(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassIntersectionOf.contains(elem))
            return false;
        doDispatchRemoveClassIntersectionOf(elem);
        return true;
    }

    /**
     * The backing data for the property ClassOneOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classOneOf
     */
    private List<Individual> __implClassOneOf;

    /**
     * Adds a value to the property ClassOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassOneOf(Individual elem) {
        __implClassOneOf.add(elem);
    }

    /**
     * Removes a value from the property ClassOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassOneOf(Individual elem) {
        __implClassOneOf.remove(elem);
    }

    /**
     * Adds a value to the property ClassOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassOneOf(Individual elem) {
        doSimpleAddClassOneOf(elem);
    }

    /**
     * Removes a value from the property ClassOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassOneOf(Individual elem) {
        doSimpleRemoveClassOneOf(elem);
    }

    /**
     * Tries to add a value to the property ClassOneOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassOneOf(Individual elem) {
        doPropertyAddClassOneOf(elem);
    }

    /**
     * Tries to remove a value from the property ClassOneOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassOneOf(Individual elem) {
        doPropertyRemoveClassOneOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassOneOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassOneOf(Individual elem) {
        doGraphAddClassOneOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassOneOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassOneOf(Individual elem) {
        doGraphRemoveClassOneOf(elem);
    }

    @Override
    public Collection<Individual> getAllClassOneOf() {
        return Collections.unmodifiableCollection(__implClassOneOf);
    }

    @Override
    public boolean addClassOneOf(Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassOneOf.contains(elem))
            return false;
        doDispatchAddClassOneOf(elem);
        return true;
    }

    @Override
    public boolean removeClassOneOf(Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassOneOf.contains(elem))
            return false;
        doDispatchRemoveClassOneOf(elem);
        return true;
    }

    /**
     * The backing data for the property ClassRestrictions
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classRestrictions
     */
    private List<ClassRestriction> __implClassRestrictions;

    /**
     * Adds a value to the property ClassRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassRestrictions(ClassRestriction elem) {
        __implClassRestrictions.add(elem);
    }

    /**
     * Removes a value from the property ClassRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassRestrictions(ClassRestriction elem) {
        __implClassRestrictions.remove(elem);
    }

    /**
     * Adds a value to the property ClassRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassRestrictions(ClassRestriction elem) {
        doSimpleAddClassRestrictions(elem);
    }

    /**
     * Removes a value from the property ClassRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassRestrictions(ClassRestriction elem) {
        doSimpleRemoveClassRestrictions(elem);
    }

    /**
     * Tries to add a value to the property ClassRestrictions and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassRestrictions(ClassRestriction elem) {
        doPropertyAddClassRestrictions(elem);
    }

    /**
     * Tries to remove a value from the property ClassRestrictions and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassRestrictions(ClassRestriction elem) {
        doPropertyRemoveClassRestrictions(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassRestrictions
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassRestrictions(ClassRestriction elem) {
        doGraphAddClassRestrictions(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassRestrictions
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassRestrictions(ClassRestriction elem) {
        doGraphRemoveClassRestrictions(elem);
    }

    @Override
    public Collection<ClassRestriction> getAllClassRestrictions() {
        return Collections.unmodifiableCollection(__implClassRestrictions);
    }

    @Override
    public boolean addClassRestrictions(ClassRestriction elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassRestrictions.contains(elem))
            return false;
        doDispatchAddClassRestrictions(elem);
        return true;
    }

    @Override
    public boolean removeClassRestrictions(ClassRestriction elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassRestrictions.contains(elem))
            return false;
        doDispatchRemoveClassRestrictions(elem);
        return true;
    }

    /**
     * The backing data for the property ClassUnionOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classUnionOf
     */
    private List<Class> __implClassUnionOf;

    /**
     * Adds a value to the property ClassUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassUnionOf(Class elem) {
        __implClassUnionOf.add(elem);
    }

    /**
     * Removes a value from the property ClassUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassUnionOf(Class elem) {
        __implClassUnionOf.remove(elem);
    }

    /**
     * Adds a value to the property ClassUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassUnionOf(Class elem) {
        doSimpleAddClassUnionOf(elem);
    }

    /**
     * Removes a value from the property ClassUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassUnionOf(Class elem) {
        doSimpleRemoveClassUnionOf(elem);
    }

    /**
     * Tries to add a value to the property ClassUnionOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassUnionOf(Class elem) {
        doPropertyAddClassUnionOf(elem);
    }

    /**
     * Tries to remove a value from the property ClassUnionOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassUnionOf(Class elem) {
        doPropertyRemoveClassUnionOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ClassUnionOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassUnionOf(Class elem) {
        doGraphAddClassUnionOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ClassUnionOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassUnionOf(Class elem) {
        doGraphRemoveClassUnionOf(elem);
    }

    @Override
    public Collection<Class> getAllClassUnionOf() {
        return Collections.unmodifiableCollection(__implClassUnionOf);
    }

    @Override
    public boolean addClassUnionOf(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassUnionOf.contains(elem))
            return false;
        doDispatchAddClassUnionOf(elem);
        return true;
    }

    @Override
    public boolean removeClassUnionOf(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassUnionOf.contains(elem))
            return false;
        doDispatchRemoveClassUnionOf(elem);
        return true;
    }

    /**
     * The backing data for the property Classifies
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#classifies
     */
    private List<Individual> __implClassifies;

    /**
     * Adds a value to the property Classifies
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddClassifies(Individual elem) {
        __implClassifies.add(elem);
    }

    /**
     * Removes a value from the property Classifies
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveClassifies(Individual elem) {
        __implClassifies.remove(elem);
    }

    /**
     * Adds a value to the property Classifies
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     * This method will also update the inverse property ClassifiedBy
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddClassifies(Individual elem) {
        doSimpleAddClassifies(elem);
        if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleAddClassifiedBy(this);
        else if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleAddClassifiedBy(this);
    }

    /**
     * Removes a value from the property Classifies
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     * This method will also update the inverse property ClassifiedBy
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveClassifies(Individual elem) {
        doSimpleRemoveClassifies(elem);
        if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleRemoveClassifiedBy(this);
        else if (elem instanceof Owl2AnonymousIndividualImpl)
            ((Owl2AnonymousIndividualImpl) elem).doSimpleRemoveClassifiedBy(this);
    }

    /**
     * Tries to add a value to the property Classifies and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddClassifies(Individual elem) {
        doPropertyAddClassifies(elem);
    }

    /**
     * Tries to remove a value from the property Classifies and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveClassifies(Individual elem) {
        doPropertyRemoveClassifies(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Classifies
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddClassifies(Individual elem) {
        doGraphAddClassifies(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Classifies
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#classifies
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveClassifies(Individual elem) {
        doGraphRemoveClassifies(elem);
    }

    @Override
    public Collection<Individual> getAllClassifies() {
        return Collections.unmodifiableCollection(__implClassifies);
    }

    @Override
    public boolean addClassifies(Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implClassifies.contains(elem))
            return false;
        doDispatchAddClassifies(elem);
        return true;
    }

    @Override
    public boolean removeClassifies(Individual elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implClassifies.contains(elem))
            return false;
        doDispatchRemoveClassifies(elem);
        return true;
    }

    /**
     * The backing data for the property DomainOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#domainOf
     */
    private List<Property> __implDomainOf;

    /**
     * Adds a value to the property DomainOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDomainOf(Property elem) {
        __implDomainOf.add(elem);
    }

    /**
     * Removes a value from the property DomainOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDomainOf(Property elem) {
        __implDomainOf.remove(elem);
    }

    /**
     * Adds a value to the property DomainOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     * This method will also update the inverse property Domain
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDomainOf(Property elem) {
        doSimpleAddDomainOf(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleAddDomain(this);
        else if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddDomain(this);
    }

    /**
     * Removes a value from the property DomainOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     * This method will also update the inverse property Domain
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDomainOf(Property elem) {
        doSimpleRemoveDomainOf(elem);
        if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleRemoveDomain(this);
        else if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveDomain(this);
    }

    /**
     * Tries to add a value to the property DomainOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDomainOf(Property elem) {
        doPropertyAddDomainOf(elem);
    }

    /**
     * Tries to remove a value from the property DomainOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDomainOf(Property elem) {
        doPropertyRemoveDomainOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DomainOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDomainOf(Property elem) {
        doGraphAddDomainOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DomainOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#domainOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDomainOf(Property elem) {
        doGraphRemoveDomainOf(elem);
    }

    @Override
    public Collection<Property> getAllDomainOf() {
        return Collections.unmodifiableCollection(__implDomainOf);
    }

    @Override
    public boolean addDomainOf(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDomainOf.contains(elem))
            return false;
        doDispatchAddDomainOf(elem);
        return true;
    }

    @Override
    public boolean removeDomainOf(Property elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDomainOf.contains(elem))
            return false;
        doDispatchRemoveDomainOf(elem);
        return true;
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
     * The backing data for the property SubClassOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#subClassOf
     */
    private List<Class> __implSubClassOf;

    /**
     * Adds a value to the property SubClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSubClassOf(Class elem) {
        __implSubClassOf.add(elem);
    }

    /**
     * Removes a value from the property SubClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSubClassOf(Class elem) {
        __implSubClassOf.remove(elem);
    }

    /**
     * Adds a value to the property SubClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     * This method will also update the inverse property SuperClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSubClassOf(Class elem) {
        doSimpleAddSubClassOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddSuperClassOf(this);
    }

    /**
     * Removes a value from the property SubClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     * This method will also update the inverse property SuperClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSubClassOf(Class elem) {
        doSimpleRemoveSubClassOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveSuperClassOf(this);
    }

    /**
     * Tries to add a value to the property SubClassOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSubClassOf(Class elem) {
        doPropertyAddSubClassOf(elem);
    }

    /**
     * Tries to remove a value from the property SubClassOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSubClassOf(Class elem) {
        doPropertyRemoveSubClassOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SubClassOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSubClassOf(Class elem) {
        doGraphAddSubClassOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SubClassOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#subClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSubClassOf(Class elem) {
        doGraphRemoveSubClassOf(elem);
    }

    @Override
    public Collection<Class> getAllSubClassOf() {
        return Collections.unmodifiableCollection(__implSubClassOf);
    }

    @Override
    public boolean addSubClassOf(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSubClassOf.contains(elem))
            return false;
        doDispatchAddSubClassOf(elem);
        return true;
    }

    @Override
    public boolean removeSubClassOf(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSubClassOf.contains(elem))
            return false;
        doDispatchRemoveSubClassOf(elem);
        return true;
    }

    /**
     * The backing data for the property SuperClassOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#superClassOf
     */
    private List<Class> __implSuperClassOf;

    /**
     * Adds a value to the property SuperClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddSuperClassOf(Class elem) {
        __implSuperClassOf.add(elem);
    }

    /**
     * Removes a value from the property SuperClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveSuperClassOf(Class elem) {
        __implSuperClassOf.remove(elem);
    }

    /**
     * Adds a value to the property SuperClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     * This method will also update the inverse property SubClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddSuperClassOf(Class elem) {
        doSimpleAddSuperClassOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddSubClassOf(this);
    }

    /**
     * Removes a value from the property SuperClassOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     * This method will also update the inverse property SubClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveSuperClassOf(Class elem) {
        doSimpleRemoveSuperClassOf(elem);
        if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveSubClassOf(this);
    }

    /**
     * Tries to add a value to the property SuperClassOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddSuperClassOf(Class elem) {
        doPropertyAddSuperClassOf(elem);
    }

    /**
     * Tries to remove a value from the property SuperClassOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveSuperClassOf(Class elem) {
        doPropertyRemoveSuperClassOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property SuperClassOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddSuperClassOf(Class elem) {
        doGraphAddSuperClassOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property SuperClassOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#superClassOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveSuperClassOf(Class elem) {
        doGraphRemoveSuperClassOf(elem);
    }

    @Override
    public Collection<Class> getAllSuperClassOf() {
        return Collections.unmodifiableCollection(__implSuperClassOf);
    }

    @Override
    public boolean addSuperClassOf(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implSuperClassOf.contains(elem))
            return false;
        doDispatchAddSuperClassOf(elem);
        return true;
    }

    @Override
    public boolean removeSuperClassOf(Class elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implSuperClassOf.contains(elem))
            return false;
        doDispatchRemoveSuperClassOf(elem);
        return true;
    }

    /**
     * Constructor for the implementation of Class
     */
    public RuntimeClassImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#classComplementOf
        this.__implClassComplementOf = null;
        // initialize property http://xowl.org/infra/lang/runtime#classDisjointWith
        this.__implClassDisjointWith = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#classEquivalentTo
        this.__implClassEquivalentTo = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#classIntersectionOf
        this.__implClassIntersectionOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#classOneOf
        this.__implClassOneOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#classRestrictions
        this.__implClassRestrictions = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#classUnionOf
        this.__implClassUnionOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#classifies
        this.__implClassifies = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#domainOf
        this.__implDomainOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#interpretationOf
        this.__implInterpretationOf = null;
        // initialize property http://xowl.org/infra/lang/runtime#subClassOf
        this.__implSubClassOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#superClassOf
        this.__implSuperClassOf = new ArrayList<>();
    }
}
