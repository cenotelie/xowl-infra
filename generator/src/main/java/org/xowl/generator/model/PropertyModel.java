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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the model of a property
 *
 * @author Laurent Wouters
 */
public class PropertyModel {
    /**
     * The parent package
     */
    private PackageModel parent;
    /**
     * The associated OWL property
     */
    private Property property;
    /**
     * The property's name
     */
    private String name;
    /**
     * The property's domain
     */
    private ClassModel domain;
    /**
     * The property's range as a class
     */
    private ClassModel rangeClass;
    /**
     * The property's range as a datatype
     */
    private DatatypeModel rangeDatatype;
    /**
     * The equivalent properties
     */
    private List<PropertyModel> equivalents;
    /**
     * The super-properties
     */
    private List<PropertyModel> superProperties;
    /**
     * The sub-properties
     */
    private List<PropertyModel> subProperties;
    /**
     * The inverse property
     */
    private PropertyModel inverse;

    /**
     * Gets the parent package
     *
     * @return The parent package
     */
    public PackageModel getPackage() {
        return parent;
    }

    /**
     * Gets the represented OWL property
     *
     * @return The represented OWL property
     */
    public Property getOWLProperty() {
        return property;
    }

    /**
     * Gets the property's name
     *
     * @return The property's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the property's domain
     *
     * @return The property's domain
     */
    public ClassModel getDomain() {
        if (domain != null)
            return domain;
        for (PropertyModel sup : superProperties) {
            ClassModel supDomain = sup.getDomain();
            if (supDomain != null)
                return supDomain;
        }
        return null;
    }

    /**
     * Gets the property's range as a class
     *
     * @return The property's range as a class
     */
    public ClassModel getRangeClass() {
        if (rangeClass != null)
            return rangeClass;
        for (PropertyModel sup : superProperties) {
            ClassModel range = sup.getRangeClass();
            if (range != null)
                return range;
        }
        return null;
    }

    /**
     * Gets the property's range as a datatype
     *
     * @return The property's range as a datatype
     */
    public DatatypeModel getRangeDatatype() {
        if (rangeDatatype != null)
            return rangeDatatype;
        for (PropertyModel sup : superProperties) {
            DatatypeModel range = sup.getRangeDatatype();
            if (range != null)
                return range;
        }
        return null;
    }

    /**
     * Gets the equivalent properties
     *
     * @return The equivalent properties
     */
    public Collection<PropertyModel> getEquivalents() {
        return equivalents;
    }

    /**
     * Gets the equivalency group for this property, i.e. this property and its equivalents
     *
     * @return The equivalenct group for this property
     */
    public Collection<PropertyModel> getEquivalencyGroup() {
        List<PropertyModel> group = new ArrayList<>(equivalents);
        group.add(this);
        return group;
    }

    /**
     * Gets the direct super properties
     *
     * @return The direct super properties
     */
    public List<PropertyModel> getDirectSuperProperties() {
        return superProperties;
    }

    /**
     * Gets all the super properties, direct or not
     *
     * @return All the super properties
     */
    public java.util.List<PropertyModel> getSuperProperties() {
        List<PropertyModel> ancestors = new ArrayList<>(superProperties);
        for (int i = 0; i != ancestors.size(); i++) {
            for (PropertyModel ancestor : ancestors.get(i).superProperties) {
                if (!ancestors.contains(ancestor))
                    ancestors.add(ancestor);
            }
        }
        return ancestors;
    }

    /**
     * Gets the direct sub properties
     *
     * @return The direct sub properties
     */
    public List<PropertyModel> getDirectSubProperties() {
        return subProperties;
    }

    /**
     * Gets all the sub properties, direct or not
     *
     * @return All the sub properties
     */
    public List<PropertyModel> getSubProperties() {
        List<PropertyModel> descendants = new ArrayList<>(subProperties);
        for (int i = 0; i != descendants.size(); i++) {
            for (PropertyModel descendant : descendants.get(i).subProperties) {
                if (!descendants.contains(descendant))
                    descendants.add(descendant);
            }
        }
        return descendants;
    }

    /**
     * Gets the inverse property
     *
     * @return The inverse property
     */
    public PropertyModel getInverse() {
        return inverse;
    }

    /**
     * Gets whether this is an object property
     *
     * @return Whether this is an object property
     */
    public boolean isObjectProperty() {
        return (property instanceof ObjectProperty);
    }

    /**
     * Gets whether this is a functional property
     *
     * @return Whether this is a functional property
     */
    public boolean isFunctional() {
        Boolean value = property.getIsFunctional();
        if (value == null) return false;
        return value;
    }

    /**
     * Gets whether this property has an inverse
     *
     * @return Whether this property has an inverse
     */
    public boolean hasInverse() {
        return (inverse != null);
    }

    /**
     * Gets whether the inverse of this property is functional
     *
     * @return Whether the inverse of this property is functional
     */
    public boolean isInverseFunctional() {
        Boolean value = ((ObjectProperty) property).getIsInverseFunctional();
        if (value == null) return false;
        return value;
    }

    /**
     * Gets whether this property is symmetric
     *
     * @return Whether this property is symmetric
     */
    public boolean isSymmetric() {
        Boolean value = ((ObjectProperty) property).getIsSymmetric();
        if (value == null) return false;
        return value;
    }

    /**
     * Gets whether this property is asymmetric
     *
     * @return Whether this property is asymmetric
     */
    public boolean isAsymmetric() {
        Boolean value = ((ObjectProperty) property).getIsAsymmetric();
        if (value == null) return false;
        return value;
    }

    /**
     * Gets whether this property is reflexive
     *
     * @return Whether this property is reflexive
     */
    public boolean isReflexive() {
        Boolean value = ((ObjectProperty) property).getIsReflexive();
        if (value == null) return false;
        return value;
    }

    /**
     * Gets whether this property is irreflexive
     *
     * @return Whether this property is irreflexive
     */
    public boolean isIrreflexive() {
        Boolean value = ((ObjectProperty) property).getIsIrreflexive();
        if (value == null) return false;
        return value;
    }

    /**
     * Gets whether this property is transitive
     *
     * @return Whether this property is transitive
     */
    public boolean isTransitive() {
        Boolean value = ((ObjectProperty) property).getIsTransitive();
        if (value == null) return false;
        return value;
    }

    /**
     * Gets the model for the specified OWL class
     *
     * @param classe An OWL class
     * @return The associated model
     */
    protected ClassModel getModelFor(Class classe) {
        return parent.getModel().getModelFor(classe);
    }

    /**
     * Gets the model for the specified OWL property
     *
     * @param property An OWL property
     * @return The associated model
     */
    protected PropertyModel getModelFor(Property property) {
        return parent.getModel().getModelFor(property);
    }

    /**
     * Gets the model for the specified OWL datatype
     *
     * @param datatype An OWL datatype
     * @return The associated model
     */
    protected DatatypeModel getModelFor(Datatype datatype) {
        return parent.getModel().getModelFor(datatype);
    }

    /**
     * Initializes this property model
     *
     * @param parent   The parent package
     * @param property The represented OWL property
     */
    public PropertyModel(PackageModel parent, Property property) {
        this.parent = parent;
        this.property = property;
        String iri = this.property.getInterpretationOf().getHasIRI().getHasValue();
        String[] parts = iri.split("#");
        name = parts[parts.length - 1];
        equivalents = new ArrayList<>();
        superProperties = new ArrayList<>();
        subProperties = new ArrayList<>();
    }

    /**
     * Builds the domain and range
     */
    public void buildDomainRange() {
        domain = getModelFor(property.getDomain());
        if (domain != null)
            domain.addProperty(this);
        if (isObjectProperty()) {
            rangeClass = getModelFor(((ObjectProperty) property).getRangeAs(null));
        } else {
            rangeDatatype = getModelFor(((DataProperty) property).getRangeAs(null));
        }
    }

    /**
     * Builds the inverses
     */
    public void buildInverses() {
        if (property instanceof ObjectProperty) {
            ObjectProperty inverseOf = ((ObjectProperty) property).getInverseOf();
            if (inverseOf != null) {
                this.inverse = getModelFor(inverseOf);
                if (this.inverse != null)
                    this.inverse.inverse = this;
            }
        }
    }

    /**
     * Builds the equivalents
     */
    public void buildEquivalents() {
        for (Property property : this.property.getAllPropertyEquivalentToAs(null)) {
            PropertyModel propertyModel = getModelFor(property);
            if (propertyModel != null && !equivalents.contains(propertyModel)) {
                equivalents.add(propertyModel);
                propertyModel.equivalents.add(this);
            }
        }
    }

    /**
     * Builds the hierarchy
     */
    public void buildHierarchy() {
        for (Property parent : property.getAllSubPropertyOfAs(null)) {
            PropertyModel propertyModel = getModelFor(parent);
            if (propertyModel != null && !superProperties.contains(propertyModel)) {
                superProperties.add(propertyModel);
                propertyModel.subProperties.add(this);
            }
        }
    }
}
