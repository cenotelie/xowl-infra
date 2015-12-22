/*******************************************************************************
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
 ******************************************************************************/

package org.xowl.store;

/**
 * Defines vocabulary constants
 *
 * @author Laurent Wouters
 */
public class Vocabulary {
    public static final String rdf = IRIs.RDF + "#";
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
    public static final String rdfHTML = rdf + "HTML";
    public static final String rdfList = rdf + "List";
    public static final String rdfProperty = rdf + "Property";

    public static final String rdfs = IRIs.RDFS + "#";
    public static final String rdfsDatatype = rdfs + "Datatype";
    public static final String rdfsResource = rdfs + "Resource";
    public static final String rdfsSubClassOf = rdfs + "subClassOf";
    public static final String rdfsSubPropertyOf = rdfs + "subPropertyOf";
    public static final String rdfsDomain = rdfs + "domain";
    public static final String rdfsRange = rdfs + "range";

    public static final String xsd = IRIs.XSD + "#";
    public static final String xsdString = xsd + "string";
    public static final String xsdBoolean = xsd + "boolean";
    public static final String xsdDecimal = xsd + "decimal";
    public static final String xsdInteger = xsd + "integer";
    public static final String xsdDouble = xsd + "double";
    public static final String xsdFloat = xsd + "float";
    public static final String xsdDate = xsd + "date";
    public static final String xsdTime = xsd + "time";
    public static final String xsdDateTime = xsd + "dateTime";
    public static final String xsdDateTimeStamp = xsd + "dateTimeStamp";
    public static final String xsdGYear = xsd + "gYear";
    public static final String xsdGMonth = xsd + "gMonth";
    public static final String xsdGDay = xsd + "gDay";
    public static final String xsdGYearMonth = xsd + "gYearMonth";
    public static final String xsdGMonthDay = xsd + "gMonthDay";
    public static final String xsdDuration = xsd + "duration";
    public static final String wsdYearMonthDuration = xsd + "yearMonthDuration";
    public static final String wsdDayTimeDuration = xsd + "dayTimeDuration";
    public static final String xsdByte = xsd + "byte";
    public static final String xsdShort = xsd + "short";
    public static final String xsdInt = xsd + "int";
    public static final String xsdLong = xsd + "long";
    public static final String xsdUnsigedByte = xsd + "unsignedByte";
    public static final String xsdUnsignedShort = xsd + "unsignedShort";
    public static final String xsdUnsignedInteger = xsd + "unsignedInt";
    public static final String xsdUnsignedLong = xsd + "unsignedLong";
    public static final String xsdPositiveInteger = xsd + "positiveInteger";
    public static final String xsdNonNegativeInteger = xsd + "nonNegativeInteger";
    public static final String xsdNegativeInteger = xsd + "negativeInteger";
    public static final String xsdNonPositiveinteger = xsd + "nonPositiveInteger";
    public static final String xsdHexBinary = xsd + "hexBinary";
    public static final String xsdBase64Binary = xsd + "base64Binary";
    public static final String xsdAnyURI = xsd + "anyURI";
    public static final String xsdLanguage = xsd + "language";
    public static final String xsdNormalizedString = xsd + "normalizedString";
    public static final String xsdToken = xsd + "token";
    public static final String xsdNMTOOKEN = xsd + "NMTOKEN";
    public static final String xsdName = xsd + "Name";
    public static final String xsdNCNAme = xsd + "NCName";

    /**
     * OWL2 language elements
     */
    public static class OWL2 {
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

    /**
     * JSON-LD language elements
     */
    public static class JSONLD {
        /**
         * Markers that forbids the further expansion
         */
        public static final String null_ = "@null";
        /**
         * Property which value describes a context
         */
        public static final String context = "@context";
        /**
         * Property that defines the identifier of a JSON-LD object with an URI or a blank node
         */
        public static final String id = "@id";
        /**
         * Property that identifies the value of a literal expressed as a JSON object
         */
        public static final String value = "@value";
        /**
         * Property that identifies the language of a literal expressed as a JSON object
         */
        public static final String language = "@language";
        /**
         * Property that identifies the type of a datatype property
         */
        public static final String type = "@type";
        /**
         * Property that defines the type of container of another multi-valued property
         */
        public static final String container = "@container";
        /**
         * Value that identifies the list type of container for a multi-valued property
         */
        public static final String list = "@list";
        /**
         * Value that identifies the set type of container for a multi-valued property
         */
        public static final String set = "@set";
        /**
         * Property that specifies that another property is expressed in a reversed form
         */
        public static final String reverse = "@reverse";
        /**
         * Property that specifies the indexing of a property values
         */
        public static final String index = "@index";
        /**
         * Property that specifies the base URI for relative ones
         */
        public static final String base = "@base";
        /**
         * Property that specifies a common URI radical for a vocabulary
         */
        public static final String vocab = "@vocab";
        /**
         * Property for the expression of explicit graphs
         */
        public static final String graph = "@graph";
    }

    public static final String owl = IRIs.OWL2 + "#";
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
    public static final String owlThing = owl + "Thing";
    public static final String owlNothing = owl + "Nothing";
    public static final String owlImports = owl + "imports";
    public static final String owlReal = owl + "real";
    public static final String owlRational = owl + "rational";

    public static final String xowlDefinedAs = IRIs.XOWL_LANG + "actions#definedAs";
    public static final String xowlStatus = IRIs.XOWL_RULES_XOWL + "#status";
    public static final String xowlInconsistent = IRIs.XOWL_RULES_XOWL + "#inconsistent";
    public static final String xowlUnsat = IRIs.XOWL_RULES_XOWL + "#unsat";
}
