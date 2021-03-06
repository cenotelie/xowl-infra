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

package org.xowl.infra.generator.model;

import fr.cenotelie.commons.utils.logging.Logging;
import org.xowl.infra.lang.runtime.Datatype;
import org.xowl.infra.store.Vocabulary;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the implementation in Java of an OWL datatype
 *
 * @author Laurent Wouters
 */
public abstract class DatatypeModel {
    /**
     * Datatype for real numbers
     */
    public static final String OWL_REAL = Vocabulary.owl + "real";
    /**
     * Datatype for rational numbers
     */
    public static final String OWL_RATIONAL = Vocabulary.owl + "rational";

    /**
     * The implemented OWL2 datatype
     */
    protected final Datatype datatype;
    /**
     * IRI of the implemented OWL2 datatype
     */
    protected final String iri;
    /**
     * The implementing Java object class
     */
    protected final String javaObjectType;
    /**
     * The implementing Java primitive type
     */
    protected final String javaPrimitiveType;
    /**
     * The default value for this datatype
     */
    protected final String javaDefault;

    /**
     * Gets the scalar type for this datatype
     *
     * @return The scalar type for this datatype
     */
    public String getScalarType() {
        return javaPrimitiveType != null ? javaPrimitiveType : javaObjectType;
    }

    /**
     * Gets the vector type for this datatype
     *
     * @return Vector type
     */
    public String getVectorType() {
        return javaObjectType;
    }

    /**
     * Gets the default value for this datatype
     *
     * @return The default value
     */
    public String getDefaultValue() {
        return javaDefault;
    }

    /**
     * Gets the piece of code converting the expression to a String
     *
     * @param expression An expression
     * @return The piece of code
     */
    public abstract String getToString(String expression);

    /**
     * Gets the piece of code converting an expression to a value of this type
     *
     * @param expression An expression
     * @return The piece of code
     */
    public abstract String getToValue(String expression);

    /**
     * Initializes this implementation
     *
     * @param datatype          The implemented OWL2 datatype
     * @param iri               The IRI of the implemented OWL2 datatype
     * @param javaObjectType    The implementing Java object class
     * @param javaPrimitiveType The implementing Java primitive type
     *                          The default value for this datatype
     */
    public DatatypeModel(Datatype datatype, String iri, String javaObjectType, String javaPrimitiveType, String javaDefault) {
        this.datatype = datatype;
        this.iri = iri;
        this.javaObjectType = javaObjectType;
        this.javaPrimitiveType = javaPrimitiveType;
        this.javaDefault = javaDefault;
    }

    /**
     * Implementation as a Byte
     */
    private static class DatatypeModelByte extends DatatypeModel {
        public DatatypeModelByte(Datatype datatype, String iri) {
            super(datatype, iri, "Byte", "byte", "0");
        }

        @Override
        public String getToString(String expression) {
            return expression + ".toString()";
        }

        @Override
        public String getToValue(String expression) {
            return "Byte.valueOf(" + expression + ")";
        }
    }

    /**
     * Implementation as a Short
     */
    private static class DatatypeModelShort extends DatatypeModel {
        public DatatypeModelShort(Datatype datatype, String iri) {
            super(datatype, iri, "Short", "short", "0");
        }

        @Override
        public String getToString(String expression) {
            return expression + ".toString()";
        }

        @Override
        public String getToValue(String expression) {
            return "Short.valueOf(" + expression + ")";
        }
    }

    /**
     * Implementation as an Integer
     */
    private static class DatatypeModelInteger extends DatatypeModel {
        public DatatypeModelInteger(Datatype datatype, String iri) {
            super(datatype, iri, "Integer", "int", "0");
        }

        @Override
        public String getToString(String expression) {
            return expression + ".toString()";
        }

        @Override
        public String getToValue(String expression) {
            return "Integer.valueOf(" + expression + ")";
        }
    }

    /**
     * Implementation as a Long
     */
    private static class DatatypeModelLong extends DatatypeModel {
        public DatatypeModelLong(Datatype datatype, String iri) {
            super(datatype, iri, "Long", "long", "0");
        }

        @Override
        public String getToString(String expression) {
            return expression + ".toString()";
        }

        @Override
        public String getToValue(String expression) {
            return "Long.valueOf(" + expression + ")";
        }
    }

    /**
     * Implementation as a Float
     */
    private static class DatatypeModelFloat extends DatatypeModel {
        public DatatypeModelFloat(Datatype datatype, String iri) {
            super(datatype, iri, "Float", "float", "0");
        }

        @Override
        public String getToString(String expression) {
            return expression + ".toString()";
        }

        @Override
        public String getToValue(String expression) {
            return "Float.valueOf(" + expression + ")";
        }
    }

    /**
     * Implementation as a Double
     */
    private static class DatatypeModelDouble extends DatatypeModel {
        public DatatypeModelDouble(Datatype datatype, String iri) {
            super(datatype, iri, "Double", "double", "0");
        }

        @Override
        public String getToString(String expression) {
            return expression + ".toString()";
        }

        @Override
        public String getToValue(String expression) {
            return "Double.valueOf(" + expression + ")";
        }
    }

    /**
     * Implementation as a Boolean
     */
    private static class DatatypeModelBoolean extends DatatypeModel {
        public DatatypeModelBoolean(Datatype datatype, String iri) {
            super(datatype, iri, "Boolean", "boolean", "false");
        }

        @Override
        public String getToString(String expression) {
            return expression + ".toString()";
        }

        @Override
        public String getToValue(String expression) {
            return "Boolean.valueOf(" + expression + ")";
        }
    }

    /**
     * Implementation as a String
     */
    private static class DatatypeModelString extends DatatypeModel {
        public DatatypeModelString(Datatype datatype, String iri) {
            super(datatype, iri, "String", null, "null");
        }

        @Override
        public String getToString(String expression) {
            return expression;
        }

        @Override
        public String getToValue(String expression) {
            return expression;
        }
    }

    /**
     * Implementation as a Date
     */
    private static class DatatypeModelDate extends DatatypeModel {
        public DatatypeModelDate(Datatype datatype, String iri) {
            super(datatype, iri, "Date", null, "null");
        }

        @Override
        public String getToString(String expression) {
            return expression + ".toString()";
        }

        @Override
        public String getToValue(String expression) {
            return expression;
        }
    }

    /**
     * The register of known implementations
     */
    private static final Map<String, Class<? extends DatatypeModel>> REGISTER = new HashMap<>();

    static {
        REGISTER.put(Vocabulary.xsdByte, DatatypeModelByte.class);
        REGISTER.put(Vocabulary.xsdUnsigedByte, DatatypeModelByte.class);
        REGISTER.put(Vocabulary.xsdShort, DatatypeModelShort.class);
        REGISTER.put(Vocabulary.xsdUnsignedShort, DatatypeModelShort.class);
        REGISTER.put(Vocabulary.xsdInt, DatatypeModelInteger.class);
        REGISTER.put(Vocabulary.xsdInteger, DatatypeModelInteger.class);
        REGISTER.put(Vocabulary.xsdUnsignedInteger, DatatypeModelInteger.class);
        REGISTER.put(Vocabulary.xsdNonNegativeInteger, DatatypeModelInteger.class);
        REGISTER.put(Vocabulary.xsdNonPositiveinteger, DatatypeModelInteger.class);
        REGISTER.put(Vocabulary.xsdPositiveInteger, DatatypeModelInteger.class);
        REGISTER.put(Vocabulary.xsdNegativeInteger, DatatypeModelInteger.class);
        REGISTER.put(Vocabulary.xsdLong, DatatypeModelLong.class);
        REGISTER.put(Vocabulary.xsdUnsignedLong, DatatypeModelLong.class);
        REGISTER.put(Vocabulary.xsdDouble, DatatypeModelDouble.class);
        REGISTER.put(Vocabulary.xsdFloat, DatatypeModelFloat.class);
        REGISTER.put(Vocabulary.xsdDecimal, DatatypeModelDouble.class);
        REGISTER.put(Vocabulary.xsdBoolean, DatatypeModelBoolean.class);
        REGISTER.put(Vocabulary.xsdString, DatatypeModelString.class);
        REGISTER.put(Vocabulary.xsdDate, DatatypeModelDate.class);
        REGISTER.put(Vocabulary.xsdDateTime, DatatypeModelDate.class);
        REGISTER.put(Vocabulary.xsdDuration, DatatypeModelDate.class);
        REGISTER.put(Vocabulary.xsdTime, DatatypeModelDate.class);
        REGISTER.put(Vocabulary.rdfPlainLiteral, DatatypeModelString.class);
        REGISTER.put(OWL_REAL, DatatypeModelDouble.class);
        REGISTER.put(OWL_RATIONAL, DatatypeModelDouble.class);
    }

    /**
     * Gets the implementation of the specified datatype
     *
     * @param datatype A datatype
     * @return The corresponding implementation
     */
    public static DatatypeModel get(Datatype datatype) {
        String iri;
        if (datatype == null)
            iri = Vocabulary.xsdString;
        else
            iri = datatype.getInterpretationOf().getHasIRI().getHasValue();
        Class<? extends DatatypeModel> datatypeClass = REGISTER.get(iri);
        DatatypeModel datatypeModel = null;
        try {
            Constructor constructor = datatypeClass.getConstructor(Datatype.class, String.class);
            datatypeModel = (DatatypeModel) constructor.newInstance(datatype, iri);
        } catch (Exception exception) {
            Logging.get().error(exception);
        }
        return datatypeModel;
    }
}
