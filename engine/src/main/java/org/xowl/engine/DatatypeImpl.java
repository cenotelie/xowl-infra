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

package org.xowl.engine;

import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Literal;
import org.xowl.store.Vocabulary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the implementation of a datatype in the current runtime
 *
 * @param <T> The native type implementing the datatype
 * @author Laurent Wouters
 */
abstract class DatatypeImpl<T> {
    /**
     * Datatype for real numbers
     */
    private static final String OWL_REAL = Vocabulary.owl + "real";
    /**
     * Datatype for rational numbers
     */
    private static final String OWL_RATIONAL = Vocabulary.owl + "rational";

    /**
     * Encodes the specified native value into a literal
     *
     * @param value The value to encode
     * @return The corresponding literal
     */
    public abstract Literal encode(T value);

    /**
     * Decodes the specified literal into a native value
     *
     * @param literal The literal to decode
     * @return The corresponding native value
     */
    public abstract T decode(Literal literal);

    /**
     * Gets the literal for the specified data
     *
     * @param lexical The lexical value
     * @param iri     The datatype's IRI
     * @return The literal
     */
    protected Literal getLiteral(String lexical, String iri) {
        Literal literal = new Literal();
        IRI datatypeIRI = new IRI();
        datatypeIRI.setHasValue(iri);
        literal.setLexicalValue(lexical);
        literal.setMemberOf(datatypeIRI);
        return literal;
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
        public Literal encode(Byte value) {
            return getLiteral(value.toString(), Vocabulary.xsdByte);
        }

        @Override
        public Byte decode(Literal literal) {
            return Byte.valueOf(literal.getLexicalValue());
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
        public Literal encode(Short value) {
            return getLiteral(value.toString(), Vocabulary.xsdShort);
        }

        @Override
        public Short decode(Literal literal) {
            return Short.valueOf(literal.getLexicalValue());
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
        public Literal encode(Integer value) {
            return getLiteral(value.toString(), Vocabulary.xsdInt);
        }

        @Override
        public Integer decode(Literal literal) {
            return Integer.valueOf(literal.getLexicalValue());
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
        public Literal encode(Long value) {
            return getLiteral(value.toString(), Vocabulary.xsdLong);
        }

        @Override
        public Long decode(Literal literal) {
            return Long.valueOf(literal.getLexicalValue());
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
        public Literal encode(Float value) {
            return getLiteral(value.toString(), Vocabulary.xsdFloat);
        }

        @Override
        public Float decode(Literal literal) {
            return Float.valueOf(literal.getLexicalValue());
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
        public Literal encode(Double value) {
            return getLiteral(value.toString(), Vocabulary.xsdDouble);
        }

        @Override
        public Double decode(Literal literal) {
            return Double.valueOf(literal.getLexicalValue());
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
        public Literal encode(Boolean value) {
            return getLiteral(value.toString(), Vocabulary.xsdBoolean);
        }

        @Override
        public Boolean decode(Literal literal) {
            return Boolean.valueOf(literal.getLexicalValue());
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
        public Literal encode(String value) {
            return getLiteral(value, Vocabulary.xsdString);
        }

        @Override
        public String decode(Literal literal) {
            return literal.getLexicalValue();
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
        public Literal encode(Date value) {
            return getLiteral(value.toString(), Vocabulary.xsdDate);
        }

        @Override
        public Date decode(Literal literal) {
            try {
                return SimpleDateFormat.getDateInstance().parse(literal.getLexicalValue());
            } catch (ParseException exception) {
                exception.printStackTrace();
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
        REGISTER_IRI.put(Vocabulary.xsdByte, DatatypeModelByte.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdUnsigedByte, DatatypeModelByte.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdShort, DatatypeModelShort.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdUnsignedShort, DatatypeModelShort.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdInt, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdUnsignedInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdNonNegativeInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdNonPositiveinteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdPositiveInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdNegativeInteger, DatatypeModelInteger.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdLong, DatatypeModelLong.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdUnsignedLong, DatatypeModelLong.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDouble, DatatypeModelDouble.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdFloat, DatatypeModelFloat.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDecimal, DatatypeModelDouble.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdBoolean, DatatypeModelBoolean.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdString, DatatypeModelString.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDate, DatatypeModelDate.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDateTime, DatatypeModelDate.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdDuration, DatatypeModelDate.INSTANCE);
        REGISTER_IRI.put(Vocabulary.xsdTime, DatatypeModelDate.INSTANCE);
        REGISTER_IRI.put(Vocabulary.rdfPlainLiteral, DatatypeModelString.INSTANCE);
        REGISTER_IRI.put(OWL_REAL, DatatypeModelDouble.INSTANCE);
        REGISTER_IRI.put(OWL_RATIONAL, DatatypeModelDouble.INSTANCE);

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
    public static Literal get(Object value) {
        return REGISTER_CLASS.get(value.getClass()).encode(value);
    }

    /**
     * Decodes the specified literal into a native value
     *
     * @param literal The literal to decode
     * @return The corresponding native value
     */
    public static Object get(Literal literal) {
        return REGISTER_IRI.get(literal.getMemberOf().getHasValue()).decode(literal);
    }

    /**
     * Gets whether the specified object can be converted to an OWL2 literal
     *
     * @param object An object
     * @return true if an literal can be produced
     */
    public static boolean handles(Object object) {
        return REGISTER_CLASS.containsKey(object.getClass());
    }
}
