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

package org.xowl.infra.lang.owl2;

import org.xowl.infra.lang.impl.*;

import java.util.*;

/**
 * The default implementation for the concrete OWL class owl2
 *
 * @author xOWL code generator
 */
public class Owl2Factory {
    /**
     * Creates a new instance of Annotation
     *
     * @return A new instance of Annotation
     */
    public static Annotation newAnnotation() {
        return new Owl2AnnotationImpl();
    }

    /**
     * Creates a new instance of AnnotationAssertion
     *
     * @return A new instance of AnnotationAssertion
     */
    public static AnnotationAssertion newAnnotationAssertion() {
        return new Owl2AnnotationAssertionImpl();
    }

    /**
     * Creates a new instance of AnnotationPropertyDomain
     *
     * @return A new instance of AnnotationPropertyDomain
     */
    public static AnnotationPropertyDomain newAnnotationPropertyDomain() {
        return new Owl2AnnotationPropertyDomainImpl();
    }

    /**
     * Creates a new instance of AnnotationPropertyRange
     *
     * @return A new instance of AnnotationPropertyRange
     */
    public static AnnotationPropertyRange newAnnotationPropertyRange() {
        return new Owl2AnnotationPropertyRangeImpl();
    }

    /**
     * Creates a new instance of AnonymousIndividual
     *
     * @return A new instance of AnonymousIndividual
     */
    public static AnonymousIndividual newAnonymousIndividual() {
        return new Owl2AnonymousIndividualImpl();
    }

    /**
     * Creates a new instance of AsymmetricObjectProperty
     *
     * @return A new instance of AsymmetricObjectProperty
     */
    public static AsymmetricObjectProperty newAsymmetricObjectProperty() {
        return new Owl2AsymmetricObjectPropertyImpl();
    }

    /**
     * Creates a new instance of ClassAssertion
     *
     * @return A new instance of ClassAssertion
     */
    public static ClassAssertion newClassAssertion() {
        return new Owl2ClassAssertionImpl();
    }

    /**
     * Creates a new instance of ClassElement
     *
     * @return A new instance of ClassElement
     */
    public static ClassElement newClassElement() {
        return new Owl2ClassElementImpl();
    }

    /**
     * Creates a new instance of ClassSequence
     *
     * @return A new instance of ClassSequence
     */
    public static ClassSequence newClassSequence() {
        return new Owl2ClassSequenceImpl();
    }

    /**
     * Creates a new instance of DataAllValuesFrom
     *
     * @return A new instance of DataAllValuesFrom
     */
    public static DataAllValuesFrom newDataAllValuesFrom() {
        return new Owl2DataAllValuesFromImpl();
    }

    /**
     * Creates a new instance of DataComplementOf
     *
     * @return A new instance of DataComplementOf
     */
    public static DataComplementOf newDataComplementOf() {
        return new Owl2DataComplementOfImpl();
    }

    /**
     * Creates a new instance of DataExactCardinality
     *
     * @return A new instance of DataExactCardinality
     */
    public static DataExactCardinality newDataExactCardinality() {
        return new Owl2DataExactCardinalityImpl();
    }

    /**
     * Creates a new instance of DataHasValue
     *
     * @return A new instance of DataHasValue
     */
    public static DataHasValue newDataHasValue() {
        return new Owl2DataHasValueImpl();
    }

    /**
     * Creates a new instance of DataIntersectionOf
     *
     * @return A new instance of DataIntersectionOf
     */
    public static DataIntersectionOf newDataIntersectionOf() {
        return new Owl2DataIntersectionOfImpl();
    }

    /**
     * Creates a new instance of DataMaxCardinality
     *
     * @return A new instance of DataMaxCardinality
     */
    public static DataMaxCardinality newDataMaxCardinality() {
        return new Owl2DataMaxCardinalityImpl();
    }

    /**
     * Creates a new instance of DataMinCardinality
     *
     * @return A new instance of DataMinCardinality
     */
    public static DataMinCardinality newDataMinCardinality() {
        return new Owl2DataMinCardinalityImpl();
    }

    /**
     * Creates a new instance of DataOneOf
     *
     * @return A new instance of DataOneOf
     */
    public static DataOneOf newDataOneOf() {
        return new Owl2DataOneOfImpl();
    }

    /**
     * Creates a new instance of DataPropertyAssertion
     *
     * @return A new instance of DataPropertyAssertion
     */
    public static DataPropertyAssertion newDataPropertyAssertion() {
        return new Owl2DataPropertyAssertionImpl();
    }

    /**
     * Creates a new instance of DataPropertyDomain
     *
     * @return A new instance of DataPropertyDomain
     */
    public static DataPropertyDomain newDataPropertyDomain() {
        return new Owl2DataPropertyDomainImpl();
    }

    /**
     * Creates a new instance of DataPropertyElement
     *
     * @return A new instance of DataPropertyElement
     */
    public static DataPropertyElement newDataPropertyElement() {
        return new Owl2DataPropertyElementImpl();
    }

    /**
     * Creates a new instance of DataPropertyRange
     *
     * @return A new instance of DataPropertyRange
     */
    public static DataPropertyRange newDataPropertyRange() {
        return new Owl2DataPropertyRangeImpl();
    }

    /**
     * Creates a new instance of DataPropertySequence
     *
     * @return A new instance of DataPropertySequence
     */
    public static DataPropertySequence newDataPropertySequence() {
        return new Owl2DataPropertySequenceImpl();
    }

    /**
     * Creates a new instance of DataSomeValuesFrom
     *
     * @return A new instance of DataSomeValuesFrom
     */
    public static DataSomeValuesFrom newDataSomeValuesFrom() {
        return new Owl2DataSomeValuesFromImpl();
    }

    /**
     * Creates a new instance of DataUnionOf
     *
     * @return A new instance of DataUnionOf
     */
    public static DataUnionOf newDataUnionOf() {
        return new Owl2DataUnionOfImpl();
    }

    /**
     * Creates a new instance of DatarangeElement
     *
     * @return A new instance of DatarangeElement
     */
    public static DatarangeElement newDatarangeElement() {
        return new Owl2DatarangeElementImpl();
    }

    /**
     * Creates a new instance of DatarangeSequence
     *
     * @return A new instance of DatarangeSequence
     */
    public static DatarangeSequence newDatarangeSequence() {
        return new Owl2DatarangeSequenceImpl();
    }

    /**
     * Creates a new instance of DatatypeDefinition
     *
     * @return A new instance of DatatypeDefinition
     */
    public static DatatypeDefinition newDatatypeDefinition() {
        return new Owl2DatatypeDefinitionImpl();
    }

    /**
     * Creates a new instance of DatatypeRestriction
     *
     * @return A new instance of DatatypeRestriction
     */
    public static DatatypeRestriction newDatatypeRestriction() {
        return new Owl2DatatypeRestrictionImpl();
    }

    /**
     * Creates a new instance of Declaration
     *
     * @return A new instance of Declaration
     */
    public static Declaration newDeclaration() {
        return new Owl2DeclarationImpl();
    }

    /**
     * Creates a new instance of DifferentIndividuals
     *
     * @return A new instance of DifferentIndividuals
     */
    public static DifferentIndividuals newDifferentIndividuals() {
        return new Owl2DifferentIndividualsImpl();
    }

    /**
     * Creates a new instance of DisjointClasses
     *
     * @return A new instance of DisjointClasses
     */
    public static DisjointClasses newDisjointClasses() {
        return new Owl2DisjointClassesImpl();
    }

    /**
     * Creates a new instance of DisjointDataProperties
     *
     * @return A new instance of DisjointDataProperties
     */
    public static DisjointDataProperties newDisjointDataProperties() {
        return new Owl2DisjointDataPropertiesImpl();
    }

    /**
     * Creates a new instance of DisjointObjectProperties
     *
     * @return A new instance of DisjointObjectProperties
     */
    public static DisjointObjectProperties newDisjointObjectProperties() {
        return new Owl2DisjointObjectPropertiesImpl();
    }

    /**
     * Creates a new instance of DisjointUnion
     *
     * @return A new instance of DisjointUnion
     */
    public static DisjointUnion newDisjointUnion() {
        return new Owl2DisjointUnionImpl();
    }

    /**
     * Creates a new instance of EquivalentClasses
     *
     * @return A new instance of EquivalentClasses
     */
    public static EquivalentClasses newEquivalentClasses() {
        return new Owl2EquivalentClassesImpl();
    }

    /**
     * Creates a new instance of EquivalentDataProperties
     *
     * @return A new instance of EquivalentDataProperties
     */
    public static EquivalentDataProperties newEquivalentDataProperties() {
        return new Owl2EquivalentDataPropertiesImpl();
    }

    /**
     * Creates a new instance of EquivalentObjectProperties
     *
     * @return A new instance of EquivalentObjectProperties
     */
    public static EquivalentObjectProperties newEquivalentObjectProperties() {
        return new Owl2EquivalentObjectPropertiesImpl();
    }

    /**
     * Creates a new instance of FacetRestriction
     *
     * @return A new instance of FacetRestriction
     */
    public static FacetRestriction newFacetRestriction() {
        return new Owl2FacetRestrictionImpl();
    }

    /**
     * Creates a new instance of FunctionalDataProperty
     *
     * @return A new instance of FunctionalDataProperty
     */
    public static FunctionalDataProperty newFunctionalDataProperty() {
        return new Owl2FunctionalDataPropertyImpl();
    }

    /**
     * Creates a new instance of FunctionalObjectProperty
     *
     * @return A new instance of FunctionalObjectProperty
     */
    public static FunctionalObjectProperty newFunctionalObjectProperty() {
        return new Owl2FunctionalObjectPropertyImpl();
    }

    /**
     * Creates a new instance of HasKey
     *
     * @return A new instance of HasKey
     */
    public static HasKey newHasKey() {
        return new Owl2HasKeyImpl();
    }

    /**
     * Creates a new instance of IRI
     *
     * @return A new instance of IRI
     */
    public static IRI newIRI() {
        return new Owl2IRIImpl();
    }

    /**
     * Creates a new instance of IndividualElement
     *
     * @return A new instance of IndividualElement
     */
    public static IndividualElement newIndividualElement() {
        return new Owl2IndividualElementImpl();
    }

    /**
     * Creates a new instance of IndividualSequence
     *
     * @return A new instance of IndividualSequence
     */
    public static IndividualSequence newIndividualSequence() {
        return new Owl2IndividualSequenceImpl();
    }

    /**
     * Creates a new instance of InverseFunctionalObjectProperty
     *
     * @return A new instance of InverseFunctionalObjectProperty
     */
    public static InverseFunctionalObjectProperty newInverseFunctionalObjectProperty() {
        return new Owl2InverseFunctionalObjectPropertyImpl();
    }

    /**
     * Creates a new instance of InverseObjectProperties
     *
     * @return A new instance of InverseObjectProperties
     */
    public static InverseObjectProperties newInverseObjectProperties() {
        return new Owl2InverseObjectPropertiesImpl();
    }

    /**
     * Creates a new instance of IrreflexiveObjectProperty
     *
     * @return A new instance of IrreflexiveObjectProperty
     */
    public static IrreflexiveObjectProperty newIrreflexiveObjectProperty() {
        return new Owl2IrreflexiveObjectPropertyImpl();
    }

    /**
     * Creates a new instance of Literal
     *
     * @return A new instance of Literal
     */
    public static Literal newLiteral() {
        return new Owl2LiteralImpl();
    }

    /**
     * Creates a new instance of LiteralElement
     *
     * @return A new instance of LiteralElement
     */
    public static LiteralElement newLiteralElement() {
        return new Owl2LiteralElementImpl();
    }

    /**
     * Creates a new instance of LiteralSequence
     *
     * @return A new instance of LiteralSequence
     */
    public static LiteralSequence newLiteralSequence() {
        return new Owl2LiteralSequenceImpl();
    }

    /**
     * Creates a new instance of NegativeDataPropertyAssertion
     *
     * @return A new instance of NegativeDataPropertyAssertion
     */
    public static NegativeDataPropertyAssertion newNegativeDataPropertyAssertion() {
        return new Owl2NegativeDataPropertyAssertionImpl();
    }

    /**
     * Creates a new instance of NegativeObjectPropertyAssertion
     *
     * @return A new instance of NegativeObjectPropertyAssertion
     */
    public static NegativeObjectPropertyAssertion newNegativeObjectPropertyAssertion() {
        return new Owl2NegativeObjectPropertyAssertionImpl();
    }

    /**
     * Creates a new instance of ObjectAllValuesFrom
     *
     * @return A new instance of ObjectAllValuesFrom
     */
    public static ObjectAllValuesFrom newObjectAllValuesFrom() {
        return new Owl2ObjectAllValuesFromImpl();
    }

    /**
     * Creates a new instance of ObjectComplementOf
     *
     * @return A new instance of ObjectComplementOf
     */
    public static ObjectComplementOf newObjectComplementOf() {
        return new Owl2ObjectComplementOfImpl();
    }

    /**
     * Creates a new instance of ObjectExactCardinality
     *
     * @return A new instance of ObjectExactCardinality
     */
    public static ObjectExactCardinality newObjectExactCardinality() {
        return new Owl2ObjectExactCardinalityImpl();
    }

    /**
     * Creates a new instance of ObjectHasSelf
     *
     * @return A new instance of ObjectHasSelf
     */
    public static ObjectHasSelf newObjectHasSelf() {
        return new Owl2ObjectHasSelfImpl();
    }

    /**
     * Creates a new instance of ObjectHasValue
     *
     * @return A new instance of ObjectHasValue
     */
    public static ObjectHasValue newObjectHasValue() {
        return new Owl2ObjectHasValueImpl();
    }

    /**
     * Creates a new instance of ObjectIntersectionOf
     *
     * @return A new instance of ObjectIntersectionOf
     */
    public static ObjectIntersectionOf newObjectIntersectionOf() {
        return new Owl2ObjectIntersectionOfImpl();
    }

    /**
     * Creates a new instance of ObjectInverseOf
     *
     * @return A new instance of ObjectInverseOf
     */
    public static ObjectInverseOf newObjectInverseOf() {
        return new Owl2ObjectInverseOfImpl();
    }

    /**
     * Creates a new instance of ObjectMaxCardinality
     *
     * @return A new instance of ObjectMaxCardinality
     */
    public static ObjectMaxCardinality newObjectMaxCardinality() {
        return new Owl2ObjectMaxCardinalityImpl();
    }

    /**
     * Creates a new instance of ObjectMinCardinality
     *
     * @return A new instance of ObjectMinCardinality
     */
    public static ObjectMinCardinality newObjectMinCardinality() {
        return new Owl2ObjectMinCardinalityImpl();
    }

    /**
     * Creates a new instance of ObjectOneOf
     *
     * @return A new instance of ObjectOneOf
     */
    public static ObjectOneOf newObjectOneOf() {
        return new Owl2ObjectOneOfImpl();
    }

    /**
     * Creates a new instance of ObjectPropertyAssertion
     *
     * @return A new instance of ObjectPropertyAssertion
     */
    public static ObjectPropertyAssertion newObjectPropertyAssertion() {
        return new Owl2ObjectPropertyAssertionImpl();
    }

    /**
     * Creates a new instance of ObjectPropertyDomain
     *
     * @return A new instance of ObjectPropertyDomain
     */
    public static ObjectPropertyDomain newObjectPropertyDomain() {
        return new Owl2ObjectPropertyDomainImpl();
    }

    /**
     * Creates a new instance of ObjectPropertyElement
     *
     * @return A new instance of ObjectPropertyElement
     */
    public static ObjectPropertyElement newObjectPropertyElement() {
        return new Owl2ObjectPropertyElementImpl();
    }

    /**
     * Creates a new instance of ObjectPropertyRange
     *
     * @return A new instance of ObjectPropertyRange
     */
    public static ObjectPropertyRange newObjectPropertyRange() {
        return new Owl2ObjectPropertyRangeImpl();
    }

    /**
     * Creates a new instance of ObjectPropertySequence
     *
     * @return A new instance of ObjectPropertySequence
     */
    public static ObjectPropertySequence newObjectPropertySequence() {
        return new Owl2ObjectPropertySequenceImpl();
    }

    /**
     * Creates a new instance of ObjectSomeValuesFrom
     *
     * @return A new instance of ObjectSomeValuesFrom
     */
    public static ObjectSomeValuesFrom newObjectSomeValuesFrom() {
        return new Owl2ObjectSomeValuesFromImpl();
    }

    /**
     * Creates a new instance of ObjectUnionOf
     *
     * @return A new instance of ObjectUnionOf
     */
    public static ObjectUnionOf newObjectUnionOf() {
        return new Owl2ObjectUnionOfImpl();
    }

    /**
     * Creates a new instance of Ontology
     *
     * @return A new instance of Ontology
     */
    public static Ontology newOntology() {
        return new Owl2OntologyImpl();
    }

    /**
     * Creates a new instance of ReflexiveObjectProperty
     *
     * @return A new instance of ReflexiveObjectProperty
     */
    public static ReflexiveObjectProperty newReflexiveObjectProperty() {
        return new Owl2ReflexiveObjectPropertyImpl();
    }

    /**
     * Creates a new instance of SameIndividual
     *
     * @return A new instance of SameIndividual
     */
    public static SameIndividual newSameIndividual() {
        return new Owl2SameIndividualImpl();
    }

    /**
     * Creates a new instance of SubAnnotationPropertyOf
     *
     * @return A new instance of SubAnnotationPropertyOf
     */
    public static SubAnnotationPropertyOf newSubAnnotationPropertyOf() {
        return new Owl2SubAnnotationPropertyOfImpl();
    }

    /**
     * Creates a new instance of SubClassOf
     *
     * @return A new instance of SubClassOf
     */
    public static SubClassOf newSubClassOf() {
        return new Owl2SubClassOfImpl();
    }

    /**
     * Creates a new instance of SubDataPropertyOf
     *
     * @return A new instance of SubDataPropertyOf
     */
    public static SubDataPropertyOf newSubDataPropertyOf() {
        return new Owl2SubDataPropertyOfImpl();
    }

    /**
     * Creates a new instance of SubObjectPropertyOf
     *
     * @return A new instance of SubObjectPropertyOf
     */
    public static SubObjectPropertyOf newSubObjectPropertyOf() {
        return new Owl2SubObjectPropertyOfImpl();
    }

    /**
     * Creates a new instance of SymmetricObjectProperty
     *
     * @return A new instance of SymmetricObjectProperty
     */
    public static SymmetricObjectProperty newSymmetricObjectProperty() {
        return new Owl2SymmetricObjectPropertyImpl();
    }

    /**
     * Creates a new instance of TransitiveObjectProperty
     *
     * @return A new instance of TransitiveObjectProperty
     */
    public static TransitiveObjectProperty newTransitiveObjectProperty() {
        return new Owl2TransitiveObjectPropertyImpl();
    }

}
