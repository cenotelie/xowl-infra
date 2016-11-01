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
 * The default implementation for Datatype
 * Original OWL class is http://xowl.org/infra/lang/runtime#Datatype
 *
 * @author xOWL code generator
 */
public class RuntimeDatatypeImpl implements Datatype {
    /**
     * The backing data for the property DataBase
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#dataBase
     */
    private Datatype __implDataBase;

    /**
     * Adds a value to the property DataBase
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataBase(Datatype elem) {
        __implDataBase = elem;
    }

    /**
     * Removes a value from the property DataBase
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataBase(Datatype elem) {
        __implDataBase = null;
    }

    /**
     * Adds a value to the property DataBase
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataBase(Datatype elem) {
        doSimpleAddDataBase(elem);
    }

    /**
     * Removes a value from the property DataBase
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataBase(Datatype elem) {
        doSimpleRemoveDataBase(elem);
    }

    /**
     * Tries to add a value to the property DataBase and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataBase(Datatype elem) {
        doPropertyAddDataBase(elem);
    }

    /**
     * Tries to remove a value from the property DataBase and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataBase(Datatype elem) {
        doPropertyRemoveDataBase(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataBase
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataBase(Datatype elem) {
        doGraphAddDataBase(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataBase
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataBase
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataBase(Datatype elem) {
        doGraphRemoveDataBase(elem);
    }

    @Override
    public Datatype getDataBase() {
        return __implDataBase;
    }

    @Override
    public void setDataBase(Datatype elem) {
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
     * The backing data for the property DataComplementOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#dataComplementOf
     */
    private Datatype __implDataComplementOf;

    /**
     * Adds a value to the property DataComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataComplementOf(Datatype elem) {
        __implDataComplementOf = elem;
    }

    /**
     * Removes a value from the property DataComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataComplementOf(Datatype elem) {
        __implDataComplementOf = null;
    }

    /**
     * Adds a value to the property DataComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     * This method will also update the inverse property DataComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataComplementOf(Datatype elem) {
        doSimpleAddDataComplementOf(elem);
        if (elem instanceof RuntimeDatatypeImpl)
            ((RuntimeDatatypeImpl) elem).doSimpleAddDataComplementOf(this);
    }

    /**
     * Removes a value from the property DataComplementOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     * This method will also update the inverse property DataComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataComplementOf(Datatype elem) {
        doSimpleRemoveDataComplementOf(elem);
        if (elem instanceof RuntimeDatatypeImpl)
            ((RuntimeDatatypeImpl) elem).doSimpleRemoveDataComplementOf(this);
    }

    /**
     * Tries to add a value to the property DataComplementOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataComplementOf(Datatype elem) {
        doPropertyAddDataComplementOf(elem);
    }

    /**
     * Tries to remove a value from the property DataComplementOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataComplementOf(Datatype elem) {
        doPropertyRemoveDataComplementOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataComplementOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataComplementOf(Datatype elem) {
        doGraphAddDataComplementOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataComplementOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataComplementOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataComplementOf(Datatype elem) {
        doGraphRemoveDataComplementOf(elem);
    }

    @Override
    public Datatype getDataComplementOf() {
        return __implDataComplementOf;
    }

    @Override
    public void setDataComplementOf(Datatype elem) {
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
     * The backing data for the property DataIntersectionOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#dataIntersectionOf
     */
    private List<Datatype> __implDataIntersectionOf;

    /**
     * Adds a value to the property DataIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataIntersectionOf(Datatype elem) {
        __implDataIntersectionOf.add(elem);
    }

    /**
     * Removes a value from the property DataIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataIntersectionOf(Datatype elem) {
        __implDataIntersectionOf.remove(elem);
    }

    /**
     * Adds a value to the property DataIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataIntersectionOf(Datatype elem) {
        doSimpleAddDataIntersectionOf(elem);
    }

    /**
     * Removes a value from the property DataIntersectionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataIntersectionOf(Datatype elem) {
        doSimpleRemoveDataIntersectionOf(elem);
    }

    /**
     * Tries to add a value to the property DataIntersectionOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataIntersectionOf(Datatype elem) {
        doPropertyAddDataIntersectionOf(elem);
    }

    /**
     * Tries to remove a value from the property DataIntersectionOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataIntersectionOf(Datatype elem) {
        doPropertyRemoveDataIntersectionOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataIntersectionOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataIntersectionOf(Datatype elem) {
        doGraphAddDataIntersectionOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataIntersectionOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataIntersectionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataIntersectionOf(Datatype elem) {
        doGraphRemoveDataIntersectionOf(elem);
    }

    @Override
    public Collection<Datatype> getAllDataIntersectionOf() {
        return Collections.unmodifiableCollection(__implDataIntersectionOf);
    }

    @Override
    public boolean addDataIntersectionOf(Datatype elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataIntersectionOf.contains(elem))
            return false;
        doDispatchAddDataIntersectionOf(elem);
        return true;
    }

    @Override
    public boolean removeDataIntersectionOf(Datatype elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataIntersectionOf.contains(elem))
            return false;
        doDispatchRemoveDataIntersectionOf(elem);
        return true;
    }

    /**
     * The backing data for the property DataOneOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#dataOneOf
     */
    private List<Literal> __implDataOneOf;

    /**
     * Adds a value to the property DataOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataOneOf(Literal elem) {
        __implDataOneOf.add(elem);
    }

    /**
     * Removes a value from the property DataOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataOneOf(Literal elem) {
        __implDataOneOf.remove(elem);
    }

    /**
     * Adds a value to the property DataOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataOneOf(Literal elem) {
        doSimpleAddDataOneOf(elem);
    }

    /**
     * Removes a value from the property DataOneOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataOneOf(Literal elem) {
        doSimpleRemoveDataOneOf(elem);
    }

    /**
     * Tries to add a value to the property DataOneOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataOneOf(Literal elem) {
        doPropertyAddDataOneOf(elem);
    }

    /**
     * Tries to remove a value from the property DataOneOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataOneOf(Literal elem) {
        doPropertyRemoveDataOneOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataOneOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataOneOf(Literal elem) {
        doGraphAddDataOneOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataOneOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataOneOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataOneOf(Literal elem) {
        doGraphRemoveDataOneOf(elem);
    }

    @Override
    public Collection<Literal> getAllDataOneOf() {
        return Collections.unmodifiableCollection(__implDataOneOf);
    }

    @Override
    public boolean addDataOneOf(Literal elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataOneOf.contains(elem))
            return false;
        doDispatchAddDataOneOf(elem);
        return true;
    }

    @Override
    public boolean removeDataOneOf(Literal elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataOneOf.contains(elem))
            return false;
        doDispatchRemoveDataOneOf(elem);
        return true;
    }

    /**
     * The backing data for the property DataRestrictions
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#dataRestrictions
     */
    private List<DatatypeRestriction> __implDataRestrictions;

    /**
     * Adds a value to the property DataRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataRestrictions(DatatypeRestriction elem) {
        __implDataRestrictions.add(elem);
    }

    /**
     * Removes a value from the property DataRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataRestrictions(DatatypeRestriction elem) {
        __implDataRestrictions.remove(elem);
    }

    /**
     * Adds a value to the property DataRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataRestrictions(DatatypeRestriction elem) {
        doSimpleAddDataRestrictions(elem);
    }

    /**
     * Removes a value from the property DataRestrictions
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataRestrictions(DatatypeRestriction elem) {
        doSimpleRemoveDataRestrictions(elem);
    }

    /**
     * Tries to add a value to the property DataRestrictions and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataRestrictions(DatatypeRestriction elem) {
        doPropertyAddDataRestrictions(elem);
    }

    /**
     * Tries to remove a value from the property DataRestrictions and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataRestrictions(DatatypeRestriction elem) {
        doPropertyRemoveDataRestrictions(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataRestrictions
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataRestrictions(DatatypeRestriction elem) {
        doGraphAddDataRestrictions(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataRestrictions
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataRestrictions
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataRestrictions(DatatypeRestriction elem) {
        doGraphRemoveDataRestrictions(elem);
    }

    @Override
    public Collection<DatatypeRestriction> getAllDataRestrictions() {
        return Collections.unmodifiableCollection(__implDataRestrictions);
    }

    @Override
    public boolean addDataRestrictions(DatatypeRestriction elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataRestrictions.contains(elem))
            return false;
        doDispatchAddDataRestrictions(elem);
        return true;
    }

    @Override
    public boolean removeDataRestrictions(DatatypeRestriction elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataRestrictions.contains(elem))
            return false;
        doDispatchRemoveDataRestrictions(elem);
        return true;
    }

    /**
     * The backing data for the property DataUnionOf
     * This implements the storage for original OWL property http://xowl.org/infra/lang/runtime#dataUnionOf
     */
    private List<Datatype> __implDataUnionOf;

    /**
     * Adds a value to the property DataUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDataUnionOf(Datatype elem) {
        __implDataUnionOf.add(elem);
    }

    /**
     * Removes a value from the property DataUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDataUnionOf(Datatype elem) {
        __implDataUnionOf.remove(elem);
    }

    /**
     * Adds a value to the property DataUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDataUnionOf(Datatype elem) {
        doSimpleAddDataUnionOf(elem);
    }

    /**
     * Removes a value from the property DataUnionOf
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDataUnionOf(Datatype elem) {
        doSimpleRemoveDataUnionOf(elem);
    }

    /**
     * Tries to add a value to the property DataUnionOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDataUnionOf(Datatype elem) {
        doPropertyAddDataUnionOf(elem);
    }

    /**
     * Tries to remove a value from the property DataUnionOf and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDataUnionOf(Datatype elem) {
        doPropertyRemoveDataUnionOf(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property DataUnionOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDataUnionOf(Datatype elem) {
        doGraphAddDataUnionOf(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property DataUnionOf
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/runtime#dataUnionOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDataUnionOf(Datatype elem) {
        doGraphRemoveDataUnionOf(elem);
    }

    @Override
    public Collection<Datatype> getAllDataUnionOf() {
        return Collections.unmodifiableCollection(__implDataUnionOf);
    }

    @Override
    public boolean addDataUnionOf(Datatype elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implDataUnionOf.contains(elem))
            return false;
        doDispatchAddDataUnionOf(elem);
        return true;
    }

    @Override
    public boolean removeDataUnionOf(Datatype elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implDataUnionOf.contains(elem))
            return false;
        doDispatchRemoveDataUnionOf(elem);
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
     * Constructor for the implementation of Datatype
     */
    public RuntimeDatatypeImpl() {
        // initialize property http://xowl.org/infra/lang/runtime#dataBase
        this.__implDataBase = null;
        // initialize property http://xowl.org/infra/lang/runtime#dataComplementOf
        this.__implDataComplementOf = null;
        // initialize property http://xowl.org/infra/lang/runtime#dataIntersectionOf
        this.__implDataIntersectionOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#dataOneOf
        this.__implDataOneOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#dataRestrictions
        this.__implDataRestrictions = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#dataUnionOf
        this.__implDataUnionOf = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/runtime#interpretationOf
        this.__implInterpretationOf = null;
    }
}
