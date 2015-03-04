/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/

package org.xowl.generator.model;

import org.xowl.lang.runtime.Class;
import org.xowl.lang.runtime.*;
import org.xowl.store.Vocabulary;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of data about a property
 *
 * @author Laurent Wouters
 */
public abstract class PropertyData {
    /**
     * The parent class model
     */
    protected ClassModel parentClass;
    /**
     * The associated property model
     */
    protected PropertyModel property;
    /**
     * The property's range as a class (for object properties)
     */
    protected ClassModel rangeClass;
    /**
     * The property's range as a datatype (for data properties)
     */
    protected DatatypeModel rangeDatatype;
    /**
     * The minimum cardinality of this property
     */
    protected int cardMin;
    /**
     * The maximum cardinality of this property
     */
    protected int cardMax;
    /**
     * Whether this is a reflexive property
     */
    protected boolean hasSelf;
    /**
     * The static values of this object property
     */
    protected List<InstanceModel> hasObjectValue;
    /**
     * The static values of this data property
     */
    protected List<Literal> hasDataValue;
    /**
     * Whether this property restricts its range over its parent
     */
    protected boolean restrictType;
    /**
     * Whether this property is in a range restriction chain
     */
    protected boolean inTypeRestrictionChain;

    /**
     * Gets the parent class model
     *
     * @return The parent class model
     */
    public ClassModel getParentClass() {
        return parentClass;
    }

    /**
     * Gets the associated property
     *
     * @return The associated property
     */
    public PropertyModel getProperty() {
        return property;
    }

    /**
     * Gets the range as a class
     *
     * @return The range as a class
     */
    public ClassModel getRangeClass() {
        return rangeClass;
    }

    /**
     * Gets the range as a datatype
     *
     * @return The range as a datatype
     */
    public DatatypeModel getRangeDatatype() {
        return rangeDatatype;
    }

    /**
     * Gets the minimum cardinality of this property
     *
     * @return The minimum cardinality of this property
     */
    public int getCardMin() {
        return cardMin;
    }

    /**
     * Gets the maximum cardinality of this property
     *
     * @return The maximum cardinality of this property
     */
    public int getCardMax() {
        return cardMax;
    }

    /**
     * Gets whether this property is reflexive
     *
     * @return Whether this property is reflexive
     */
    public boolean hasSelf() {
        return hasSelf;
    }

    /**
     * Gets whether this property is a vector, i.e. it has a maximum multiplicity greater than 1
     *
     * @return Whether this property is a vector
     */
    public boolean isVector() {
        return (cardMax > 1);
    }

    /**
     * Gets the string representation of the range's type
     *
     * @return The string representation of the range's type
     */
    public String getRepresentationRange() {
        if (property.isObjectProperty())
            return rangeClass.getJavaName();
        else
            return rangeDatatype.getJavaType();
    }

    /**
     * Gets whether this property has static values
     *
     * @return Whether this property has static values
     */
    public boolean hasValues() {
        return !(hasObjectValue.isEmpty() && hasDataValue.isEmpty());
    }

    /**
     * Gets the number of static values
     *
     * @return the number of static values
     */
    public int getValuesCount() {
        return hasObjectValue.size() + hasDataValue.size();
    }

    /**
     * Gets the static values in the case of an object property
     *
     * @return The static values
     */
    public List<InstanceModel> hasObjectValues() {
        return hasObjectValue;
    }

    /**
     * Gets the static values in the case of a data property
     *
     * @return The static values
     */
    public List<Literal> hasDataValues() {
        return hasDataValue;
    }

    /**
     * Gets whether this property restricts its range over its parent
     *
     * @return Whether this property restricts its range over its parent
     */
    public boolean restrictType() {
        return restrictType;
    }

    /**
     * Initializes this property data
     *
     * @param classe   The parent class model
     * @param property The associated property
     */
    public PropertyData(ClassModel classe, PropertyModel property) {
        this.parentClass = classe;
        this.property = property;
        this.rangeClass = property.getRangeClass();
        this.rangeDatatype = property.getRangeDatatype();
        this.cardMin = 0;
        this.cardMax = Integer.MAX_VALUE;
        this.hasSelf = false;
        this.hasObjectValue = new ArrayList<>();
        this.hasDataValue = new ArrayList<>();
        this.restrictType = false;
        this.inTypeRestrictionChain = false;
        if (this.property.isFunctional())
            cardMax = 1;
        applyRestrictions();
        if (this.property.isObjectProperty()) {
            if (rangeClass == null)
                rangeClass = parentClass.getModelFor(this.parentClass.getPackage().getModel().getRepository().interpretAsClass(parentClass.getPackage().getModel().getRepository().resolveEntity(Model.OWL_THING)));
        } else {
            if (rangeDatatype == null)
                rangeDatatype = property.getModelFor(parentClass.getPackage().getModel().getRepository().interpretAsDatatype(parentClass.getPackage().getModel().getRepository().resolveEntity(Vocabulary.xsdString)));
        }
    }

    /**
     * Applies the property restrictions
     */
    private void applyRestrictions() {
        for (ClassModel classModel : parentClass.getEquivalencyGroup()) {
            for (ClassRestriction restriction : classModel.getOWLClass().getAllClassRestrictions())
                applyRestriction(restriction);
            for (ClassModel superClass : classModel.getSuperClasses())
                for (ClassRestriction restriction : superClass.getOWLClass().getAllClassRestrictions())
                    applyRestriction(restriction);
        }
    }

    /**
     * Applies the specified restriction
     *
     * @param restriction A class restriction
     */
    private void applyRestriction(ClassRestriction restriction) {
        if (restriction instanceof ObjectPropertyRestriction)
            applyRestrictionOnObjectProperty((ObjectPropertyRestriction) restriction);
        if (restriction instanceof DataPropertyRestriction)
            applyRestrictionOnDataProperty((DataPropertyRestriction) restriction);
        if (restriction instanceof NAryDataPropertyRestriction)
            applyRestrictionOnNAryProperties((NAryDataPropertyRestriction) restriction);
    }

    /**
     * Applies the specified restriction
     *
     * @param restriction A class restriction on an object property
     */
    private void applyRestrictionOnObjectProperty(ObjectPropertyRestriction restriction) {
        if (property.getOWLProperty() != restriction.getObjectProperty())
            return;
        if (restriction instanceof ObjectAllValuesFrom) {
            ObjectAllValuesFrom objectAllValuesFrom = (ObjectAllValuesFrom) restriction;
            ClassModel classe = parentClass.getModelFor(objectAllValuesFrom.getClasse());
            if ((rangeClass == null) || rangeClass.getSubClasses().contains(classe)) {
                rangeClass = classe;
                restrictType = true;
            }
        } else if (restriction instanceof ObjectHasSelf) {
            hasSelf = true;
        } else if (restriction instanceof ObjectHasValue) {
            ObjectHasValue objectHasValue = (ObjectHasValue) restriction;
            Individual individual = objectHasValue.getIndividual();
            if (individual instanceof NamedIndividual) {
                Class classe = individual.getAllClassifiedBy().iterator().next();
                ClassModel classModel = parentClass.getModelFor(classe);
                InstanceModel inst = classModel.getStaticInstance((NamedIndividual) individual);
                hasObjectValue.add(inst);
            }
        } else if (restriction instanceof ObjectExactCardinality) {
            ObjectExactCardinality objectExactCardinality = (ObjectExactCardinality) restriction;
            int value = objectExactCardinality.getCardinality();
            cardMin = value;
            cardMax = value;
        } else if (restriction instanceof ObjectMinCardinality) {
            ObjectMinCardinality objectMinCardinality = (ObjectMinCardinality) restriction;
            int value = objectMinCardinality.getCardinality();
            if (cardMin < value)
                cardMin = value;
        } else if (restriction instanceof ObjectMaxCardinality) {
            ObjectMaxCardinality objectMaxCardinality = (ObjectMaxCardinality) restriction;
            int value = objectMaxCardinality.getCardinality();
            if (cardMax > value)
                cardMax = value;
        }
    }

    /**
     * Applies the specified restriction
     *
     * @param restriction A class restriction on a data property
     */
    private void applyRestrictionOnDataProperty(DataPropertyRestriction restriction) {
        if (property.getOWLProperty() != restriction.getDataProperty())
            return;
        if (restriction instanceof DataHasValue) {
            DataHasValue dataHasValue = (DataHasValue) restriction;
            Literal literal = dataHasValue.getLiteral();
            hasDataValue.add(literal);
        } else if (restriction instanceof DataExactCardinality) {
            DataExactCardinality dataExactCardinality = (DataExactCardinality) restriction;
            int value = dataExactCardinality.getCardinality();
            cardMin = value;
            cardMax = value;
        } else if (restriction instanceof DataMinCardinality) {
            DataMinCardinality dataMinCardinality = (DataMinCardinality) restriction;
            int value = dataMinCardinality.getCardinality();
            if (cardMin < value)
                cardMin = value;
        } else if (restriction instanceof DataMaxCardinality) {
            DataMaxCardinality dataMaxCardinality = (DataMaxCardinality) restriction;
            int value = dataMaxCardinality.getCardinality();
            if (cardMax > value)
                cardMax = value;
        }
    }

    /**
     * Applies the specified restriction
     *
     * @param restriction A class restriction on multiple properties
     */
    private void applyRestrictionOnNAryProperties(NAryDataPropertyRestriction restriction) {
        if (!restriction.getAllDataProperties().contains(property.getOWLProperty()))
            return;
        if (restriction instanceof DataAllValuesFrom) {
            Datatype datatype = restriction.getDatatype();
            rangeDatatype = property.getModelFor(datatype);
            restrictType = true;
        }
    }

    /**
     * Gets the property referenced by the specified class restriction
     *
     * @param restriction A class restriction
     * @return The referenced property
     */
    public static Property getRestrictedProperty(ClassRestriction restriction) {
        if (restriction instanceof ObjectPropertyRestriction)
            return ((ObjectPropertyRestriction) restriction).getObjectProperty();
        if (restriction instanceof DataPropertyRestriction)
            return ((DataPropertyRestriction) restriction).getDataProperty();
        return ((NAryDataPropertyRestriction) restriction).getAllDataProperties().iterator().next();
    }
}
