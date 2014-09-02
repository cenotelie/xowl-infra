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

package org.xowl.store;

import org.xowl.store.voc.OWL2;

/**
 * Defines vocabulary constants
 *
 * @author Laurent Wouters
 */
public class Vocabulary {
    public static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String rdfLangString = rdf + "langString";
    public static final String rdfPlainLiteral = rdf + "PlainLiteral";
    public static final String rdfRDF = rdf + "RDF";
    public static final String rdfDescription = rdf + "Description";
    public static final String rdfAbout = rdf + "about";
    public static final String rdfID = rdf + "ID";
    public static final String rdfNodeID = rdf + "nodeID";
    public static final String rdfResource = rdf + "resource";
    public static final String rdfDatatype = rdf + "datatype";
    public static final String rdfParseType = rdf + "parseType";
    public static final String rdfType = rdf + "type";
    public static final String rdfFirst = rdf + "first";
    public static final String rdfRest = rdf + "rest";
    public static final String rdfNil = rdf + "nil";
    public static final String rdfSubject = rdf + "subject";
    public static final String rdfPredicate = rdf + "predicate";
    public static final String rdfObject = rdf + "object";
    public static final String rdfStatement = rdf + "Statement";
    public static final String rdfLI = rdf + "li";
    public static final String rdfXMLLiteral = rdf + "XMLLiteral";

    public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String rdfsDatatype = rdfs + OWL2.entityDatatype;
    public static final String rdfsSubClassOf = rdfs + "subClassOf";
    public static final String rdfsSubPropertyOf = rdfs + "subPropertyOf";
    public static final String rdfsDomain = rdfs + "domain";
    public static final String rdfsRange = rdfs + "range";

    public static final String xsd = "http://www.w3.org/2001/XMLSchema#";
    public static final String xsdTime = xsd + "time";
    public static final String xsdDuration = xsd + "duration";
    public static final String xsdDateTime = xsd + "dateTime";
    public static final String xsdDate = xsd + "date";
    public static final String xsdString = xsd + "string";
    public static final String xsdBoolean = xsd + "boolean";
    public static final String xsdDecimal = xsd + "decimal";
    public static final String xsdFloat = xsd + "float";
    public static final String xsdDouble = xsd + "double";
    public static final String xsdUnsignedLong = xsd + "unsignedLong";
    public static final String xsdLong = xsd + "long";
    public static final String xsdNegativeInteger = xsd + "negativeInteger";
    public static final String xsdPositiveInteger = xsd + "positiveInteger";
    public static final String xsdNonPositiveinteger = xsd + "nonPositiveInteger";
    public static final String xsdNonNegativeInteger = xsd + "nonNegativeInteger";
    public static final String xsdUnsignedInteger = xsd + "unsignedInt";
    public static final String xsdInteger = xsd + "integer";
    public static final String xsdInt = xsd + "int";
    public static final String xsdUnsignedShort = xsd + "unsignedShort";
    public static final String xsdShort = xsd + "short";
    public static final String xsdUnsigedByte = xsd + "unsignedByte";
    public static final String xsdByte = xsd + "byte";

    public static final String owl = "http://www.w3.org/2002/07/owl#";
    public static final String owlRational = owl + "rational";
    public static final String owlReal = owl + "real";
    public static final String owlOntology = owl + OWL2.ontoOntology;
    public static final String owlClass = owl + OWL2.entityClass;
    public static final String owlNamedIndividual = owl + OWL2.entityNamedIndividual;
    public static final String owlObjectProperty = owl + OWL2.entityObjectProperty;
    public static final String owlDataProperty = owl + "DatatypeProperty";
    public static final String owlAnnotationProperty = owl + OWL2.entityAnnotationProperty;
    public static final String owlFunctionalProperty = owl + "FunctionalProperty";
    public static final String owlInverseFunctionalProperty = owl + "InverseFunctionalProperty";
    public static final String owlReflexiveProperty = owl + "ReflexiveProperty";
    public static final String owlIrreflexiveProperty = owl + "IrreflexiveProperty";
    public static final String owlSymmetricProperty = owl + "SymmetricProperty";
    public static final String owlAsymmetricProperty = owl + "AsymmetricProperty";
    public static final String owlTransitiveProperty = owl + "TransitiveProperty";
    public static final String owlAnnotation = owl + "Annotation";
    public static final String owlAxiom = owl + "Axiom";
    public static final String owlUnionOf = owl + "unionOf";
    public static final String owlIntersectionOf = owl + "intersectionOf";
    public static final String owlOneOf = owl + "oneOf";
    public static final String owlComplementOf = owl + "complementOf";
    public static final String owlRestriction = owl + "Restriction";
    public static final String owlOnProperty = owl + "onProperty";
    public static final String owlOnProperties = owl + "onProperties";
    public static final String owlAllValuesFrom = owl + "allValuesFrom";
    public static final String owlSomeValuesFrom = owl + "someValuesFrom";
    public static final String owlCardinality = owl + "cardinality";
    public static final String owlQualifiedCardinality = owl + "qualifiedCardinality";
    public static final String owlMinCardinality = owl + "minCardinality";
    public static final String owlMinQualifiedCardinality = owl + "minQualifiedCardinality";
    public static final String owlMaxCardinality = owl + "maxCardinality";
    public static final String owlMaxQualifiedCardinality = owl + "maxQualifiedCardinality";
    public static final String owlOnDatarange = owl + "onDatarange";
    public static final String owlOnClass = owl + "onClass";
    public static final String owlHasValue = owl + "hasValue";
    public static final String owlHasSelf = owl + "hasSelf";
    public static final String owlInverseOf = owl + "inverseOf";
    public static final String owlDatatypeComplementOf = owl + "datatypeComplementOf";
    public static final String owlOnDatatype = owl + "onDatatype";
    public static final String owlWithRestrictions = owl + "withRestrictions";
    public static final String owlEquivalentClass = owl + "equivalentClass";
    public static final String owlMembers = owl + "members";
    public static final String owlDisjointWith = owl + "disjointWith";
    public static final String owlAllDisjointClasses = owl + "AllDisjointClasses";
    public static final String owlDisjointUnionOf = owl + "disjointUnionOf";
    public static final String owlPropertyChainAxiom = owl + "propertyChainAxiom";
    public static final String owlEquivalentProperty = owl + "equivalentProperty";
    public static final String owlPropertyDisjointWith = owl + "propertyDisjointWith";
    public static final String owlAllDisjointProperties = owl + "AllDisjointProperties";
    public static final String owlSameAs = owl + "sameAs";
    public static final String owlDifferentFrom = owl + "differentFrom";
    public static final String owlAllDifferent = owl + "AllDifferent";
    public static final String owlNegativePropertyAssertion = owl + "NegativePropertyAssertion";
    public static final String owlSourceIndividual = owl + "sourceIndividual";
    public static final String owlAssertionProperty = owl + "assertionProperty";
    public static final String owlTargetIndividual = owl + "targetIndividual";
    public static final String owlTargetValue = owl + "targetValue";
    public static final String owlHasKey = owl + "hasKey";
    public static final String owlAnnotatedSource = owl + "annotatedSource";
    public static final String owlAnnotatedProperty = owl + "annotatedProperty";
    public static final String owlAnnotatedTarget = owl + "annotatedTarget";
}
