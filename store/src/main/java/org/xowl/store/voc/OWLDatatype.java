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
public class OWLDatatype {
    // XML Schema datatypes
    // Numeric datatypes
    public static final String xsdByte = RDF.xsd + "byte";
    public static final String xsdUnsigedByte = RDF.xsd + "unsignedByte";
    public static final String xsdShort = RDF.xsd + "short";
    public static final String xsdUnsignedShort = RDF.xsd + "unsignedShort";
    public static final String xsdInt = RDF.xsd + "int";
    public static final String xsdInteger = RDF.xsd + "integer";
    public static final String xsdUnsignedInteger = RDF.xsd + "unsignedInt";
    public static final String xsdNonNegativeInteger = RDF.xsd + "nonNegativeInteger";
    public static final String xsdNonPositiveinteger = RDF.xsd + "nonPositiveInteger";
    public static final String xsdPositiveInteger = RDF.xsd + "positiveInteger";
    public static final String xsdNegativeInteger = RDF.xsd + "negativeInteger";
    public static final String xsdLong = RDF.xsd + "long";
    public static final String xsdUnsignedLong = RDF.xsd + "unsignedLong";
    public static final String xsdDouble = RDF.xsd + "double";
    public static final String xsdFloat = RDF.xsd + "float";
    public static final String xsdDecimal = RDF.xsd + "decimal";
    // Boolean
    public static final String xsdBoolean = RDF.xsd + "boolean";
    // String
    public static final String xsdString = RDF.xsd + "string";
    // Date and times
    public static final String xsdDate = RDF.xsd + "date";
    public static final String xsdDateTime = RDF.xsd + "dateTime";
    public static final String xsdDuration = RDF.xsd + "duration";
    public static final String xsdTime = RDF.xsd + "time";
    // RDF Datatypes
    public static final String rdfPlainLiteral = RDF.rdf + "PlainLiteral";
    public static final String rdfLangString = RDF.rdf + "langString";
    // OWL Datatypes
    public static final String owlReal = RDF.owl + "real";
    public static final String owlRational = RDF.owl + "rational";
}
