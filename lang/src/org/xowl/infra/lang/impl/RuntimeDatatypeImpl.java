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
 * The default implementation for the concrete OWL class Datatype
 *
 * @author xOWL code generator
 */
public class RuntimeDatatypeImpl implements org.xowl.infra.lang.runtime.Datatype {
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
     * The backing data for the property DataComplementOf
     */
    private org.xowl.infra.lang.runtime.Datatype __implDataComplementOf;

    /**
     * Adds a value to the property DataComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDataComplementOf = elem;
    }

    /**
     * Removes a value from the property DataComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDataComplementOf = null;
    }

    /**
     * Adds a value to the property DataComplementOf
     * This method will also update the inverse property DataComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleAddDataComplementOf(elem);
        if (elem instanceof RuntimeDatatypeImpl)
            ((RuntimeDatatypeImpl) elem).doSimpleAddDataComplementOf(this);
    }

    /**
     * Removes a value from the property DataComplementOf
     * This method will also update the inverse property DataComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleRemoveDataComplementOf(elem);
        if (elem instanceof RuntimeDatatypeImpl)
            ((RuntimeDatatypeImpl) elem).doSimpleRemoveDataComplementOf(this);
    }

    /**
     * Tries to add a value to the property DataComplementOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyAddDataComplementOf(elem);
    }

    /**
     * Tries to remove a value from the property DataComplementOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyRemoveDataComplementOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataComplementOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphAddDataComplementOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataComplementOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphRemoveDataComplementOf(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Datatype getDataComplementOf() {
        return __implDataComplementOf;
    }

    @Override
    public void setDataComplementOf(org.xowl.infra.lang.runtime.Datatype elem) {
        if (__implDataComplementOf == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDataComplementOf(__implDataComplementOf);
        } else if (__implDataComplementOf == null) {
            doDispatchAddDataComplementOf(elem);
        } else {
            doDispatchRemoveDataComplementOf(__implDataComplementOf);
            doDispatchAddDataComplementOf(elem);
        }
    }

    /**
     * The backing data for the property DataBase
     */
    private org.xowl.infra.lang.runtime.Datatype __implDataBase;

    /**
     * Adds a value to the property DataBase
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDataBase = elem;
    }

    /**
     * Removes a value from the property DataBase
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDataBase = null;
    }

    /**
     * Adds a value to the property DataBase
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleAddDataBase(elem);
    }

    /**
     * Removes a value from the property DataBase
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleRemoveDataBase(elem);
    }

    /**
     * Tries to add a value to the property DataBase and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyAddDataBase(elem);
    }

    /**
     * Tries to remove a value from the property DataBase and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyRemoveDataBase(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataBase
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphAddDataBase(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataBase
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphRemoveDataBase(elem);
    }

    @Override
    public org.xowl.infra.lang.runtime.Datatype getDataBase() {
        return __implDataBase;
    }

    @Override
    public void setDataBase(org.xowl.infra.lang.runtime.Datatype elem) {
        if (__implDataBase == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDataBase(__implDataBase);
        } else if (__implDataBase == null) {
            doDispatchAddDataBase(elem);
        } else {
            doDispatchRemoveDataBase(__implDataBase);
            doDispatchAddDataBase(elem);
        }
    }

    /**
     * The backing data for the property DataUnionOf
     */
    private List<org.xowl.infra.lang.runtime.Datatype> __implDataUnionOf;

    /**
     * Adds a value to the property DataUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDataUnionOf.add(elem);
    }

    /**
     * Removes a value from the property DataUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDataUnionOf.remove(elem);
    }

    /**
     * Adds a value to the property DataUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleAddDataUnionOf(elem);
    }

    /**
     * Removes a value from the property DataUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleRemoveDataUnionOf(elem);
    }

    /**
     * Tries to add a value to the property DataUnionOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyAddDataUnionOf(elem);
    }

    /**
     * Tries to remove a value from the property DataUnionOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyRemoveDataUnionOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataUnionOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphAddDataUnionOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataUnionOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphRemoveDataUnionOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Datatype> getAllDataUnionOf() {
        return Collections.unmodifiableCollection(__implDataUnionOf);
    }

    @Override
    public boolean addDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataUnionOf.contains(elem))
            return false;
        doDispatchAddDataUnionOf(elem);
        return true;
    }

    @Override
    public boolean removeDataUnionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataUnionOf.contains(elem))
            return false;
        doDispatchRemoveDataUnionOf(elem);
        return true;
    }

    /**
     * The backing data for the property DataRestrictions
     */
    private List<org.xowl.infra.lang.runtime.DatatypeRestriction> __implDataRestrictions;

    /**
     * Adds a value to the property DataRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        __implDataRestrictions.add(elem);
    }

    /**
     * Removes a value from the property DataRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        __implDataRestrictions.remove(elem);
    }

    /**
     * Adds a value to the property DataRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        doSimpleAddDataRestrictions(elem);
    }

    /**
     * Removes a value from the property DataRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        doSimpleRemoveDataRestrictions(elem);
    }

    /**
     * Tries to add a value to the property DataRestrictions and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        doPropertyAddDataRestrictions(elem);
    }

    /**
     * Tries to remove a value from the property DataRestrictions and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        doPropertyRemoveDataRestrictions(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataRestrictions
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        doGraphAddDataRestrictions(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataRestrictions
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        doGraphRemoveDataRestrictions(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.DatatypeRestriction> getAllDataRestrictions() {
        return Collections.unmodifiableCollection(__implDataRestrictions);
    }

    @Override
    public boolean addDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataRestrictions.contains(elem))
            return false;
        doDispatchAddDataRestrictions(elem);
        return true;
    }

    @Override
    public boolean removeDataRestrictions(org.xowl.infra.lang.runtime.DatatypeRestriction elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataRestrictions.contains(elem))
            return false;
        doDispatchRemoveDataRestrictions(elem);
        return true;
    }

    /**
     * The backing data for the property DataIntersectionOf
     */
    private List<org.xowl.infra.lang.runtime.Datatype> __implDataIntersectionOf;

    /**
     * Adds a value to the property DataIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDataIntersectionOf.add(elem);
    }

    /**
     * Removes a value from the property DataIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        __implDataIntersectionOf.remove(elem);
    }

    /**
     * Adds a value to the property DataIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleAddDataIntersectionOf(elem);
    }

    /**
     * Removes a value from the property DataIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doSimpleRemoveDataIntersectionOf(elem);
    }

    /**
     * Tries to add a value to the property DataIntersectionOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyAddDataIntersectionOf(elem);
    }

    /**
     * Tries to remove a value from the property DataIntersectionOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doPropertyRemoveDataIntersectionOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataIntersectionOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphAddDataIntersectionOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataIntersectionOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        doGraphRemoveDataIntersectionOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Datatype> getAllDataIntersectionOf() {
        return Collections.unmodifiableCollection(__implDataIntersectionOf);
    }

    @Override
    public boolean addDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataIntersectionOf.contains(elem))
            return false;
        doDispatchAddDataIntersectionOf(elem);
        return true;
    }

    @Override
    public boolean removeDataIntersectionOf(org.xowl.infra.lang.runtime.Datatype elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataIntersectionOf.contains(elem))
            return false;
        doDispatchRemoveDataIntersectionOf(elem);
        return true;
    }

    /**
     * The backing data for the property DataOneOf
     */
    private List<org.xowl.infra.lang.runtime.Literal> __implDataOneOf;

    /**
     * Adds a value to the property DataOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        __implDataOneOf.add(elem);
    }

    /**
     * Removes a value from the property DataOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        __implDataOneOf.remove(elem);
    }

    /**
     * Adds a value to the property DataOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        doSimpleAddDataOneOf(elem);
    }

    /**
     * Removes a value from the property DataOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        doSimpleRemoveDataOneOf(elem);
    }

    /**
     * Tries to add a value to the property DataOneOf and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        doPropertyAddDataOneOf(elem);
    }

    /**
     * Tries to remove a value from the property DataOneOf and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        doPropertyRemoveDataOneOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataOneOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        doGraphAddDataOneOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataOneOf
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        doGraphRemoveDataOneOf(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Literal> getAllDataOneOf() {
        return Collections.unmodifiableCollection(__implDataOneOf);
    }

    @Override
    public boolean addDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataOneOf.contains(elem))
            return false;
        doDispatchAddDataOneOf(elem);
        return true;
    }

    @Override
    public boolean removeDataOneOf(org.xowl.infra.lang.runtime.Literal elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataOneOf.contains(elem))
            return false;
        doDispatchRemoveDataOneOf(elem);
        return true;
    }

    /**
     * Constructor for the implementation of Datatype
     */
    public RuntimeDatatypeImpl() {
        this.__implInterpretationOf = null;
        this.__implDataComplementOf = null;
        this.__implDataBase = null;
        this.__implDataUnionOf = new ArrayList<>();
        this.__implDataRestrictions = new ArrayList<>();
        this.__implDataIntersectionOf = new ArrayList<>();
        this.__implDataOneOf = new ArrayList<>();
    }
}
