/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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

package org.xowl.store.voc;

/**
 * Defines constants for xOWL language concepts
 *
 * @author Laurent Wouters
 */
public class OWL2 {
    // Ontology-level vocabulary
    public static final String ontoOntology = "Ontology";
    public static final String ontoPrefix = "Prefix";
    public static final String ontoImport = "Import";
    public static final String ontoAnnotation = "Annotation";

    // Axioms
    public static final String axiomDeclaration = "Declaration";
    public static final String axiomDisjointClasses = "DisjointClasses";
    public static final String axiomDisjointUnion = "DisjointUnion";
    public static final String axiomEquivalentClasses = "EquivalentClasses";
    public static final String axiomSubClassOf = "SubClassOf";
    public static final String axiomDataPropertyDomain = "DataPropertyDomain";
    public static final String axiomDataPropertyRange = "DataPropertyRange";
    public static final String axiomDisjointDataProperties = "DisjointDataProperties";
    public static final String axiomEquivalentDataProperties = "EquivalentDataProperties";
    public static final String axiomFunctionalDataProperty = "FunctionalDataProperty";
    public static final String axiomSubDataPropertyOf = "SubDataPropertyOf";
    public static final String axiomDatatypeDefinition = "DatatypeDefinition";
    public static final String axiomClassAssertion = "ClassAssertion";
    public static final String axiomDataPropertyAssertion = "DataPropertyAssertion";
    public static final String axiomDifferentIndividuals = "DifferentIndividuals";
    public static final String axiomNegativeDataPropertyAssertion = "NegativeDataPropertyAssertion";
    public static final String axiomNegativeObjectPropertyAssertion = "NegativeObjectPropertyAssertion";
    public static final String axiomObjectPropertyAssertion = "ObjectPropertyAssertion";
    public static final String axiomSameIndividual = "SameIndividual";
    public static final String axiomAsymmetricObjectProperty = "AsymmetricObjectProperty";
    public static final String axiomDisjointObjectProperties = "DisjointObjectProperties";
    public static final String axiomEquivalentObjectProperties = "EquivalentObjectProperties";
    public static final String axiomFunctionalObjectProperty = "FunctionalObjectProperty";
    public static final String axiomInverseFunctionalObjectProperty = "InverseFunctionalObjectProperty";
    public static final String axiomInverseObjectProperties = "InverseObjectProperties";
    public static final String axiomIrreflexiveObjectProperty = "IrreflexiveObjectProperty";
    public static final String axiomObjectPropertyDomain = "ObjectPropertyDomain";
    public static final String axiomObjectPropertyRange = "ObjectPropertyRange";
    public static final String axiomReflexiveObjectProperty = "ReflexiveObjectProperty";
    public static final String axiomSubObjectPropertyOf = "SubObjectPropertyOf";
    public static final String axiomSymmetricObjectProperty = "SymmetricObjectProperty";
    public static final String axiomTransitiveObjectProperty = "TransitiveObjectProperty";
    public static final String axiomHasKey = "HasKey";
    public static final String axiomSubAnnotationPropertyOf = "SubAnnotationPropertyOf";
    public static final String axiomAnnotationPropertyDomain = "AnnotationPropertyDomain";
    public static final String axiomAnnotationPropertyRange = "AnnotationPropertyRange";
    public static final String axiomAnnotationAssertion = "AnnotationAssertion";

    // Ontological entities
    public static final String entityEntity = "Entity";
    public static final String entityClass = "Class";
    public static final String entityDatatype = "Datatype";
    public static final String entityObjectProperty = "ObjectProperty";
    public static final String entityDataProperty = "DataProperty";
    public static final String entityNamedIndividual = "NamedIndividual";
    public static final String entityAnonymousIndividual = "AnonymousIndividual";
    public static final String entityAnnotationProperty = "AnnotationProperty";

    // Expressions
    // Class expressions
    public static final String expObjectComplementOf = "ObjectComplementOf";
    public static final String expObjectIntersectionOf = "ObjectIntersectionOf";
    public static final String expObjectOneOf = "ObjectOneOf";
    public static final String expObjectUnionOf = "ObjectUnionOf";
    public static final String expDataAllValuesFrom = "DataAllValuesFrom";
    public static final String expDataExactCardinality = "DataExactCardinality";
    public static final String expDataHasValue = "DataHasValue";
    public static final String expDataMaxCardinality = "DataMaxCardinality";
    public static final String expDataMinCardinality = "DataMinCardinality";
    public static final String expDataSomeValuesFrom = "DataSomeValuesFrom";
    public static final String expObjectAllValuesFrom = "ObjectAllValuesFrom";
    public static final String expObjectExactCardinality = "ObjectExactCardinality";
    public static final String expObjectHasSelf = "ObjectHasSelf";
    public static final String expObjectHasValue = "ObjectHasValue";
    public static final String expObjectMaxCardinality = "ObjectMaxCardinality";
    public static final String expObjectMinCardinality = "ObjectMinCardinality";
    public static final String expObjectSomeValuesFrom = "ObjectSomeValuesFrom";
    // Datatype expressions
    public static final String expDataComplementOf = "DataComplementOf";
    public static final String expDataIntersectionOf = "DataIntersectionOf";
    public static final String expDataOneOf = "DataOneOf";
    public static final String expDatatypeRestriction = "DatatypeRestriction";
    public static final String expDataUnionOf = "DataUnionOf";
    // Literal expressions
    public static final String expLiteral = "Literal";
    // Object Property expressions
    public static final String expObjectInverseOf = "ObjectInverseOf";
    public static final String expObjectPropertyChain = "ObjectPropertyChain";
}
