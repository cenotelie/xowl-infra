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
 * The default implementation for DatatypeDefinition
 * Original OWL class is http://xowl.org/infra/lang/owl2#DatatypeDefinition
 *
 * @author xOWL code generator
 */
public class Owl2DatatypeDefinitionImpl implements DatatypeDefinition {
    /**
     * The backing data for the property Annotations
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#annotations
     */
    private List<Annotation> __implAnnotations;

    /**
     * Adds a value to the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddAnnotations(Annotation elem) {
        __implAnnotations.add(elem);
    }

    /**
     * Removes a value from the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveAnnotations(Annotation elem) {
        __implAnnotations.remove(elem);
    }

    /**
     * Adds a value to the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddAnnotations(Annotation elem) {
        doSimpleAddAnnotations(elem);
    }

    /**
     * Removes a value from the property Annotations
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveAnnotations(Annotation elem) {
        doSimpleRemoveAnnotations(elem);
    }

    /**
     * Tries to add a value to the property Annotations and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddAnnotations(Annotation elem) {
        doPropertyAddAnnotations(elem);
    }

    /**
     * Tries to remove a value from the property Annotations and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveAnnotations(Annotation elem) {
        doPropertyRemoveAnnotations(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Annotations
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddAnnotations(Annotation elem) {
        doGraphAddAnnotations(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Annotations
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#annotations
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveAnnotations(Annotation elem) {
        doGraphRemoveAnnotations(elem);
    }

    @Override
    public Collection<Annotation> getAllAnnotations() {
        return Collections.unmodifiableCollection(__implAnnotations);
    }

    @Override
    public boolean addAnnotations(Annotation elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implAnnotations.contains(elem))
            return false;
        doDispatchAddAnnotations(elem);
        return true;
    }

    @Override
    public boolean removeAnnotations(Annotation elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implAnnotations.contains(elem))
            return false;
        doDispatchRemoveAnnotations(elem);
        return true;
    }

    /**
     * The backing data for the property Datarange
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#datarange
     */
    private Datarange __implDatarange;

    /**
     * Adds a value to the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatarange(Datarange elem) {
        __implDatarange = elem;
    }

    /**
     * Removes a value from the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatarange(Datarange elem) {
        __implDatarange = null;
    }

    /**
     * Adds a value to the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatarange(Datarange elem) {
        doSimpleAddDatarange(elem);
    }

    /**
     * Removes a value from the property Datarange
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatarange(Datarange elem) {
        doSimpleRemoveDatarange(elem);
    }

    /**
     * Tries to add a value to the property Datarange and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatarange(Datarange elem) {
        doPropertyAddDatarange(elem);
    }

    /**
     * Tries to remove a value from the property Datarange and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatarange(Datarange elem) {
        doPropertyRemoveDatarange(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Datarange
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatarange(Datarange elem) {
        doGraphAddDatarange(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Datarange
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datarange
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatarange(Datarange elem) {
        doGraphRemoveDatarange(elem);
    }

    @Override
    public Datarange getDatarange() {
        return __implDatarange;
    }

    @Override
    public void setDatarange(Datarange elem) {
        if (__implDatarange == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDatarange(__implDatarange);
        } else if (__implDatarange == null) {
            doDispatchAddDatarange(elem);
        } else {
            doDispatchRemoveDatarange(__implDatarange);
            doDispatchAddDatarange(elem);
        }
    }

    /**
     * The backing data for the property Datatype
     * This implements the storage for original OWL property http://xowl.org/infra/lang/owl2#datatype
     */
    private Datarange __implDatatype;

    /**
     * Adds a value to the property Datatype
     * Original OWL property is http://xowl.org/infra/lang/owl2#datatype
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddDatatype(Datarange elem) {
        __implDatatype = elem;
    }

    /**
     * Removes a value from the property Datatype
     * Original OWL property is http://xowl.org/infra/lang/owl2#datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveDatatype(Datarange elem) {
        __implDatatype = null;
    }

    /**
     * Adds a value to the property Datatype
     * Original OWL property is http://xowl.org/infra/lang/owl2#datatype
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddDatatype(Datarange elem) {
        doSimpleAddDatatype(elem);
    }

    /**
     * Removes a value from the property Datatype
     * Original OWL property is http://xowl.org/infra/lang/owl2#datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveDatatype(Datarange elem) {
        doSimpleRemoveDatatype(elem);
    }

    /**
     * Tries to add a value to the property Datatype and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datatype
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddDatatype(Datarange elem) {
        doPropertyAddDatatype(elem);
    }

    /**
     * Tries to remove a value from the property Datatype and its super properties (if any)
     * Original OWL property is http://xowl.org/infra/lang/owl2#datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveDatatype(Datarange elem) {
        doPropertyRemoveDatatype(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property Datatype
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datatype
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddDatatype(Datarange elem) {
        doGraphAddDatatype(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property Datatype
     * This method tries to delegate to a sub property, if any.
     * Original OWL property is http://xowl.org/infra/lang/owl2#datatype
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveDatatype(Datarange elem) {
        doGraphRemoveDatatype(elem);
    }

    @Override
    public Datarange getDatatype() {
        return __implDatatype;
    }

    @Override
    public void setDatatype(Datarange elem) {
        if (__implDatatype == elem)
            return;
        if (elem == null) {
            doDispatchRemoveDatatype(__implDatatype);
        } else if (__implDatatype == null) {
            doDispatchAddDatatype(elem);
        } else {
            doDispatchRemoveDatatype(__implDatatype);
            doDispatchAddDatatype(elem);
        }
    }

    /**
     * The backing data for the property File
     * This implements the storage for original OWL property http://xowl.org/infra/lang/instrumentation#file
     */
    private String __implFile;

    @Override
    public String getFile() {
        return __implFile;
    }

    @Override
    public void setFile(String elem) {
        __implFile = elem;
    }

    /**
     * The backing data for the property Line
     * This implements the storage for original OWL property http://xowl.org/infra/lang/instrumentation#line
     */
    private int __implLine;

    @Override
    public int getLine() {
        return __implLine;
    }

    @Override
    public void setLine(int elem) {
        __implLine = elem;
    }

    /**
     * Constructor for the implementation of DatatypeDefinition
     */
    public Owl2DatatypeDefinitionImpl() {
        // initialize property http://xowl.org/infra/lang/owl2#annotations
        this.__implAnnotations = new ArrayList<>();
        // initialize property http://xowl.org/infra/lang/owl2#datarange
        this.__implDatarange = null;
        // initialize property http://xowl.org/infra/lang/owl2#datatype
        this.__implDatatype = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#file
        this.__implFile = null;
        // initialize property http://xowl.org/infra/lang/instrumentation#line
        this.__implLine = 0;
    }
}
