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
    public static final String rdfsDatatype = rdfs + "Datatype";
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
}
