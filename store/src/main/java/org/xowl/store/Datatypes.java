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

import org.xowl.lang.owl2.Literal;
import org.xowl.store.rdf.LiteralNode;
import org.xowl.utils.collections.Couple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility API for mapping RDF literals to native values
 *
 * @author Laurent Wouters
 */
public class Datatypes {
    /**
     * Represents an unsupported value
     */
    private static class UnsupportedValue {
        /**
         * URI of the datatype
         */
        private final String datatypeURI;
        /**
         * Lexical value
         */
        private final String lexical;

        /**
         * Initializes this value
         *
         * @param datatypeURI URI of the datatype
         * @param lexical     Lexical value
         */
        public UnsupportedValue(String datatypeURI, String lexical) {
            this.datatypeURI = datatypeURI;
            this.lexical = lexical;
        }
    }

    /**
     * Represents the implementation of a datatype in the current runtime
     *
     * @param <T> The native type implementing the datatype
     */
    private static abstract class DatatypeImpl<T> {
        /**
         * Gets the default datatype for this implementation
         *
         * @return The default datatype for this implementation
         */
        public abstract String getDatatype();

        /**
         * Encodes the specified native value into a lexical value
         *
         * @param value The value to encode
         * @return The corresponding lexical value
         */
        public abstract String encode(T value);

        /**
         * Decodes the specified lexical value into a native value
         *
         * @param lexical The lexical value to decode
         * @return The corresponding native value
         */
        public abstract T decode(String lexical);
    }

    /**
     * The non-implementation of an unsupported datatype
     */
    private static class DatatypeModelUnsupported extends DatatypeImpl<Object> {
        /**
         * URI of the datatype
         */
        private final String uri;

        /**
         * Initializes this model
         *
         * @param uri URI of the datatype
         */
        public DatatypeModelUnsupported(String uri) {
            this.uri = uri;
        }

        @Override
        public String getDatatype() {
            return uri;
        }

        @Override
        public String encode(Object value) {
            return ((UnsupportedValue) value).lexical;
        }

        @Override
        public Object decode(String lexical) {
            return new UnsupportedValue(uri, lexical);
        }
    }

    /**
     * Implementation as a Byte
     */
    private static class DatatypeModelByte extends DatatypeImpl<Byte> {
        /**
         * The instance
         */
        public static final DatatypeModelByte INSTANCE = new DatatypeModelByte();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdByte;
        }

        @Override
        public String encode(Byte value) {
            return value.toString();
        }

        @Override
        public Byte decode(String lexical) {
            return Byte.valueOf(lexical);
        }
    }

    /**
     * Implementation as a Short
     */
    private static class DatatypeModelShort extends DatatypeImpl<Short> {
        /**
         * The instance
         */
        public static final DatatypeModelShort INSTANCE = new DatatypeModelShort();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdShort;
        }

        @Override
        public String encode(Short value) {
            return value.toString();
        }

        @Override
        public Short decode(String lexical) {
            return Short.valueOf(lexical);
        }
    }

    /**
     * Implementation as an Integer
     */
    private static class DatatypeModelInteger extends DatatypeImpl<Integer> {
        /**
         * The instance
         */
        public static final DatatypeModelInteger INSTANCE = new DatatypeModelInteger();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdInteger;
        }

        @Override
        public String encode(Integer value) {
            return value.toString();
        }

        @Override
        public Integer decode(String lexical) {
            return Integer.valueOf(lexical);
        }
    }

    /**
     * Implementation as a Long
     */
    private static class DatatypeModelLong extends DatatypeImpl<Long> {
        /**
         * The instance
         */
        public static final DatatypeModelLong INSTANCE = new DatatypeModelLong();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdLong;
        }

        @Override
        public String encode(Long value) {
            return value.toString();
        }

        @Override
        public Long decode(String lexical) {
            return Long.valueOf(lexical);
        }
    }

    /**
     * Implementation as a Float
     */
    private static class DatatypeModelFloat extends DatatypeImpl<Float> {
        /**
         * The instance
         */
        public static final DatatypeModelFloat INSTANCE = new DatatypeModelFloat();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdFloat;
        }

        @Override
        public String encode(Float value) {
            return value.toString();
        }

        @Override
        public Float decode(String lexical) {
            return Float.valueOf(lexical);
        }
    }

    /**
     * Implementation as a Double
     */
    private static class DatatypeModelDouble extends DatatypeImpl<Double> {
        /**
         * The instance
         */
        public static final DatatypeModelDouble INSTANCE = new DatatypeModelDouble();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdDouble;
        }

        @Override
        public String encode(Double value) {
            return value.toString();
        }

        @Override
        public Double decode(String lexical) {
            return Double.valueOf(lexical);
        }
    }

    /**
     * Implementation as a Boolean
     */
    private static class DatatypeModelBoolean extends DatatypeImpl<Boolean> {
        /**
         * The instance
         */
        public static final DatatypeModelBoolean INSTANCE = new DatatypeModelBoolean();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdBoolean;
        }

        @Override
        public String encode(Boolean value) {
            return value.toString();
        }

        @Override
        public Boolean decode(String lexical) {
            return Boolean.valueOf(lexical);
        }
    }

    /**
     * Implementation as a String
     */
    private static class DatatypeModelString extends DatatypeImpl<String> {
        /**
         * The instance
         */
        public static final DatatypeModelString INSTANCE = new DatatypeModelString();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdString;
        }

        @Override
        public String encode(String value) {
            return value;
        }

        @Override
        public String decode(String lexical) {
            return lexical;
        }
    }

    /**
     * Implementation as a Date
     */
    private static class DatatypeModelDate extends DatatypeImpl<Date> {
        /**
         * The instance
         */
        public static final DatatypeModelDate INSTANCE = new DatatypeModelDate();

        @Override
        public String getDatatype() {
            return Vocabulary.xsdDate;
        }

        @Override
        public String encode(Date value) {
            return value.toString();
        }

        @Override
        public Date decode(String lexical) {
            try {
                return SimpleDateFormat.getDateInstance().parse(lexical);
            } catch (ParseException exception) {
                return null;
            }
        }
    }

    /**
     * The register of known implementations by datatype
     */
    private static final Map<String, DatatypeImpl> REGISTER_IRI = new HashMap<>();

    /**
     * The register of known implementations by native implementation class
     */
    private static final Map<Class, DatatypeImpl> REGISTER_CLASS = new HashMap<>();

    static {
        REGISTER_IRI.put(Vocabulary.xsdString, DatatypeModelString.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdBoolean, DatatypeModelBoolean.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDecimal, DatatypeModelDouble.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDouble, DatatypeModelDouble.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdFloat, DatatypeModelFloat.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDate, DatatypeModelDate.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdTime, DatatypeModelDate.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDateTime, DatatypeModelDate.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDateTimeStamp, DatatypeModelDate.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdGYear, new DatatypeModelUnsupported(Vocabulary.xsdGYear));
        REGISTER_IRI.put(Vocabulary.xsdGMonth, new DatatypeModelUnsupported(Vocabulary.xsdGMonth));
        REGISTER_IRI.put(Vocabulary.xsdGDay, new DatatypeModelUnsupported(Vocabulary.xsdGDay));
        REGISTER_IRI.put(Vocabulary.xsdGYearMonth, new DatatypeModelUnsupported(Vocabulary.xsdGYearMonth));
        REGISTER_IRI.put(Vocabulary.xsdGMonthDay, new DatatypeModelUnsupported(Vocabulary.xsdGMonthDay));
        REGISTER_IRI.put(Vocabulary.xsdDuration, new DatatypeModelUnsupported(Vocabulary.xsdDuration));
        REGISTER_IRI.put(Vocabulary.wsdYearMonthDuration, new DatatypeModelUnsupported(Vocabulary.wsdYearMonthDuration));
        REGISTER_IRI.put(Vocabulary.wsdDayTimeDuration, new DatatypeModelUnsupported(Vocabulary.wsdDayTimeDuration));
        REGISTER_IRI.put(Vocabulary.xsdByte, DatatypeModelByte.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdShort, DatatypeModelShort.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdInt, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdLong, DatatypeModelLong.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdUnsigedByte, DatatypeModelByte.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdUnsignedShort, DatatypeModelShort.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdUnsignedInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdUnsignedLong, DatatypeModelLong.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdPositiveInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdNonNegativeInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdNegativeInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdNonPositiveinteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdHexBinary, new DatatypeModelUnsupported(Vocabulary.xsdHexBinary));
        REGISTER_IRI.put(Vocabulary.xsdBase64Binary, new DatatypeModelUnsupported(Vocabulary.xsdBase64Binary));
        REGISTER_IRI.put(Vocabulary.xsdAnyURI, new DatatypeModelUnsupported(Vocabulary.xsdAnyURI));
        REGISTER_IRI.put(Vocabulary.xsdLanguage, new DatatypeModelUnsupported(Vocabulary.xsdLanguage));
        REGISTER_IRI.put(Vocabulary.xsdNormalizedString, new DatatypeModelUnsupported(Vocabulary.xsdNormalizedString));
        REGISTER_IRI.put(Vocabulary.xsdToken, new DatatypeModelUnsupported(Vocabulary.xsdToken));
        REGISTER_IRI.put(Vocabulary.xsdNMTOOKEN, new DatatypeModelUnsupported(Vocabulary.xsdNMTOOKEN));
        REGISTER_IRI.put(Vocabulary.xsdName, new DatatypeModelUnsupported(Vocabulary.xsdName));
        REGISTER_IRI.put(Vocabulary.xsdNCNAme, new DatatypeModelUnsupported(Vocabulary.xsdNCNAme));
        REGISTER_IRI.put(Vocabulary.rdfPlainLiteral, DatatypeModelString.INSTANCE);
        REGISTER_IRI.put(Vocabulary.owlRational, DatatypeModelDouble.INSTANCE);
        REGISTER_IRI.put(Vocabulary.owlReal, DatatypeModelDouble.INSTANCE);

        REGISTER_CLASS.put(Byte.class, DatatypeModelByte.INSTANCE);
        REGISTER_CLASS.put(Short.class, DatatypeModelShort.INSTANCE);
        REGISTER_CLASS.put(Integer.class, DatatypeModelInteger.INSTANCE);
        REGISTER_CLASS.put(Long.class, DatatypeModelLong.INSTANCE);
        REGISTER_CLASS.put(Float.class, DatatypeModelFloat.INSTANCE);
        REGISTER_CLASS.put(Double.class, DatatypeModelDouble.INSTANCE);
        REGISTER_CLASS.put(Boolean.class, DatatypeModelBoolean.INSTANCE);
        REGISTER_CLASS.put(String.class, DatatypeModelString.INSTANCE);
        REGISTER_CLASS.put(Date.class, DatatypeModelDate.INSTANCE);
    }

    /**
     * Encodes the specified native value into a literal
     *
     * @param value The value to encode
     * @return The corresponding literal
     */
    public static Couple<String, String> toLiteral(Object value) {
        DatatypeImpl impl = REGISTER_CLASS.get(value.getClass());
        if (impl == null)
            return new Couple<>(value.toString(), Vocabulary.xsdString);
        return new Couple<>(impl.encode(value), impl.getDatatype());
    }

    /**
     * Decodes the specified literal into a native value
     *
     * @param lexical  The literal's lexical value
     * @param datatype the literal's datatype
     * @return The corresponding native value
     */
    public static Object toNative(String lexical, String datatype) {
        DatatypeImpl impl = REGISTER_IRI.get(datatype);
        if (impl == null)
            return lexical;
        return impl.decode(lexical);
    }

    /**
     * Decodes the specified literal into a native value
     *
     * @param literal The literal
     * @return The corresponding native value
     */
    public static Object toNative(Literal literal) {
        return toNative(literal.getLexicalValue(), literal.getMemberOf().getHasValue());
    }

    /**
     * Decodes the specified literal into a native value
     *
     * @param literal The literal
     * @return The corresponding native value
     */
    public static Object toNative(org.xowl.lang.runtime.Literal literal) {
        return toNative(literal.getLexicalValue(), literal.getMemberOf().getInterpretationOf().getHasIRI().getHasValue());
    }

    /**
     * Decodes the specified literal into a native value
     *
     * @param literal The literal
     * @return The corresponding native value
     */
    public static Object toNative(LiteralNode literal) {
        return toNative(literal.getLexicalValue(), literal.getDatatype());
    }
}
