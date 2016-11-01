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
 * The default implementation for the concrete OWL class Entity
 *
 * @author xOWL code generator
 */
public class RuntimeEntityImpl implements org.xowl.infra.lang.runtime.Entity {
    /**
     * The backing data for the property ContainedBy
     */
    private org.xowl.infra.lang.owl2.Ontology __implContainedBy;

    /**
     * Adds a value to the property ContainedBy
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        __implContainedBy = elem;
    }

    /**
     * Removes a value from the property ContainedBy
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        __implContainedBy = null;
    }

    /**
     * Adds a value to the property ContainedBy
     * This method will also update the inverse property Contains
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        doSimpleAddContainedBy(elem);
        if (elem instanceof Owl2OntologyImpl)
            ((Owl2OntologyImpl) elem).doSimpleAddContains(this);
    }

    /**
     * Removes a value from the property ContainedBy
     * This method will also update the inverse property Contains
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        doSimpleRemoveContainedBy(elem);
        if (elem instanceof Owl2OntologyImpl)
            ((Owl2OntologyImpl) elem).doSimpleRemoveContains(this);
    }

    /**
     * Tries to add a value to the property ContainedBy and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        doPropertyAddContainedBy(elem);
    }

    /**
     * Tries to remove a value from the property ContainedBy and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        doPropertyRemoveContainedBy(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property ContainedBy
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        doGraphAddContainedBy(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property ContainedBy
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        doGraphRemoveContainedBy(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.Ontology getContainedBy() {
        return __implContainedBy;
    }

    @Override
    public void setContainedBy(org.xowl.infra.lang.owl2.Ontology elem) {
        if (__implContainedBy == elem)
            return;
        if (elem == null) {
            doDispatchRemoveContainedBy(__implContainedBy);
        } else if (__implContainedBy == null) {
            doDispatchAddContainedBy(elem);
        } else {
            doDispatchRemoveContainedBy(__implContainedBy);
            doDispatchAddContainedBy(elem);
        }
    }

    /**
     * The backing data for the property HasIRI
     */
    private org.xowl.infra.lang.owl2.IRI __implHasIRI;

    /**
     * Adds a value to the property HasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        __implHasIRI = elem;
    }

    /**
     * Removes a value from the property HasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        __implHasIRI = null;
    }

    /**
     * Adds a value to the property HasIRI
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleAddHasIRI(elem);
    }

    /**
     * Removes a value from the property HasIRI
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doSimpleRemoveHasIRI(elem);
    }

    /**
     * Tries to add a value to the property HasIRI and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyAddHasIRI(elem);
    }

    /**
     * Tries to remove a value from the property HasIRI and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doPropertyRemoveHasIRI(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property HasIRI
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphAddHasIRI(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property HasIRI
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        doGraphRemoveHasIRI(elem);
    }

    @Override
    public org.xowl.infra.lang.owl2.IRI getHasIRI() {
        return __implHasIRI;
    }

    @Override
    public void setHasIRI(org.xowl.infra.lang.owl2.IRI elem) {
        if (__implHasIRI == elem)
            return;
        if (elem == null) {
            doDispatchRemoveHasIRI(__implHasIRI);
        } else if (__implHasIRI == null) {
            doDispatchAddHasIRI(elem);
        } else {
            doDispatchRemoveHasIRI(__implHasIRI);
            doDispatchAddHasIRI(elem);
        }
    }

    /**
     * The backing data for the property InterpretedAs
     */
    private List<org.xowl.infra.lang.runtime.Interpretation> __implInterpretedAs;

    /**
     * Adds a value to the property InterpretedAs
     *
     * @param elem The element value to add (must not be null)
     */
    protected void doSimpleAddInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        __implInterpretedAs.add(elem);
    }

    /**
     * Removes a value from the property InterpretedAs
     *
     * @param elem The element value to remove (must not be null)
     */
    protected void doSimpleRemoveInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        __implInterpretedAs.remove(elem);
    }

    /**
     * Adds a value to the property InterpretedAs
     * This method will also update the inverse property InterpretationOf
     *
     * @param elem The element value to add (must not be null)
     */
    private void doPropertyAddInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        doSimpleAddInterpretedAs(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleAddInterpretationOf(this);
        else if (elem instanceof RuntimeFunctionImpl)
            ((RuntimeFunctionImpl) elem).doSimpleAddInterpretationOf(this);
        else if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleAddInterpretationOf(this);
        else if (elem instanceof RuntimeDatatypeImpl)
            ((RuntimeDatatypeImpl) elem).doSimpleAddInterpretationOf(this);
        else if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleAddInterpretationOf(this);
        else if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleAddInterpretationOf(this);
        else if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleAddInterpretationOf(this);
    }

    /**
     * Removes a value from the property InterpretedAs
     * This method will also update the inverse property InterpretationOf
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doPropertyRemoveInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        doSimpleRemoveInterpretedAs(elem);
        if (elem instanceof RuntimeAnnotationPropertyImpl)
            ((RuntimeAnnotationPropertyImpl) elem).doSimpleRemoveInterpretationOf(this);
        else if (elem instanceof RuntimeFunctionImpl)
            ((RuntimeFunctionImpl) elem).doSimpleRemoveInterpretationOf(this);
        else if (elem instanceof RuntimeClassImpl)
            ((RuntimeClassImpl) elem).doSimpleRemoveInterpretationOf(this);
        else if (elem instanceof RuntimeDatatypeImpl)
            ((RuntimeDatatypeImpl) elem).doSimpleRemoveInterpretationOf(this);
        else if (elem instanceof RuntimeNamedIndividualImpl)
            ((RuntimeNamedIndividualImpl) elem).doSimpleRemoveInterpretationOf(this);
        else if (elem instanceof RuntimeDataPropertyImpl)
            ((RuntimeDataPropertyImpl) elem).doSimpleRemoveInterpretationOf(this);
        else if (elem instanceof RuntimeObjectPropertyImpl)
            ((RuntimeObjectPropertyImpl) elem).doSimpleRemoveInterpretationOf(this);
    }

    /**
     * Tries to add a value to the property InterpretedAs and its super properties (if any)
     *
     * @param elem The element value to add (must not be null)
     */
    private void doGraphAddInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        doPropertyAddInterpretedAs(elem);
    }

    /**
     * Tries to remove a value from the property InterpretedAs and its super properties (if any)
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doGraphRemoveInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        doPropertyRemoveInterpretedAs(elem);
    }

    /**
     * Dispatches the request for the addition of a value to the property InterpretedAs
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to add (must not be null)
     */
    private void doDispatchAddInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        doGraphAddInterpretedAs(elem);
    }

    /**
     * Dispatches the request for the removal of a value from the property InterpretedAs
     * This method tries to delegate to a sub property, if any.
     *
     * @param elem The element value to remove (must not be null)
     */
    private void doDispatchRemoveInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        doGraphRemoveInterpretedAs(elem);
    }

    @Override
    public Collection<org.xowl.infra.lang.runtime.Interpretation> getAllInterpretedAs() {
        return Collections.unmodifiableCollection(__implInterpretedAs);
    }

    @Override
    public boolean addInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (__implInterpretedAs.contains(elem))
            return false;
        doDispatchAddInterpretedAs(elem);
        return true;
    }

    @Override
    public boolean removeInterpretedAs(org.xowl.infra.lang.runtime.Interpretation elem) {
        if (elem == null)
            throw new IllegalArgumentException("Expected a value");
        if (!__implInterpretedAs.contains(elem))
            return false;
        doDispatchRemoveInterpretedAs(elem);
        return true;
    }

    /**
     * Constructor for the implementation of Entity
     */
    public RuntimeEntityImpl() {
        this.__implContainedBy = null;
        this.__implHasIRI = null;
        this.__implInterpretedAs = new ArrayList<>();
    }
}
