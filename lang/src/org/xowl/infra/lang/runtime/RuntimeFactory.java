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

package org.xowl.infra.lang.runtime;

import org.xowl.infra.lang.impl.*;

import java.util.*;

/**
 * The default implementation for the concrete OWL class runtime
 *
 * @author xOWL code generator
 */
public class RuntimeFactory {
    /**
     * Creates a new instance of AnnotationProperty
     *
     * @return A new instance of AnnotationProperty
     */
    public static AnnotationProperty newAnnotationProperty() {
        return new RuntimeAnnotationPropertyImpl();
    }

    /**
     * Creates a new instance of Class
     *
     * @return A new instance of Class
     */
    public static Class newClass() {
        return new RuntimeClassImpl();
    }

    /**
     * Creates a new instance of DataAllValuesFrom
     *
     * @return A new instance of DataAllValuesFrom
     */
    public static DataAllValuesFrom newDataAllValuesFrom() {
        return new RuntimeDataAllValuesFromImpl();
    }

    /**
     * Creates a new instance of DataExactCardinality
     *
     * @return A new instance of DataExactCardinality
     */
    public static DataExactCardinality newDataExactCardinality() {
        return new RuntimeDataExactCardinalityImpl();
    }

    /**
     * Creates a new instance of DataHasValue
     *
     * @return A new instance of DataHasValue
     */
    public static DataHasValue newDataHasValue() {
        return new RuntimeDataHasValueImpl();
    }

    /**
     * Creates a new instance of DataMaxCardinality
     *
     * @return A new instance of DataMaxCardinality
     */
    public static DataMaxCardinality newDataMaxCardinality() {
        return new RuntimeDataMaxCardinalityImpl();
    }

    /**
     * Creates a new instance of DataMinCardinality
     *
     * @return A new instance of DataMinCardinality
     */
    public static DataMinCardinality newDataMinCardinality() {
        return new RuntimeDataMinCardinalityImpl();
    }

    /**
     * Creates a new instance of DataProperty
     *
     * @return A new instance of DataProperty
     */
    public static DataProperty newDataProperty() {
        return new RuntimeDataPropertyImpl();
    }

    /**
     * Creates a new instance of DataPropertyAssertion
     *
     * @return A new instance of DataPropertyAssertion
     */
    public static DataPropertyAssertion newDataPropertyAssertion() {
        return new RuntimeDataPropertyAssertionImpl();
    }

    /**
     * Creates a new instance of DataSomeValuesFrom
     *
     * @return A new instance of DataSomeValuesFrom
     */
    public static DataSomeValuesFrom newDataSomeValuesFrom() {
        return new RuntimeDataSomeValuesFromImpl();
    }

    /**
     * Creates a new instance of Datatype
     *
     * @return A new instance of Datatype
     */
    public static Datatype newDatatype() {
        return new RuntimeDatatypeImpl();
    }

    /**
     * Creates a new instance of DatatypeRestriction
     *
     * @return A new instance of DatatypeRestriction
     */
    public static DatatypeRestriction newDatatypeRestriction() {
        return new RuntimeDatatypeRestrictionImpl();
    }

    /**
     * Creates a new instance of Entity
     *
     * @return A new instance of Entity
     */
    public static Entity newEntity() {
        return new RuntimeEntityImpl();
    }

    /**
     * Creates a new instance of Function
     *
     * @return A new instance of Function
     */
    public static Function newFunction() {
        return new RuntimeFunctionImpl();
    }

    /**
     * Creates a new instance of Literal
     *
     * @return A new instance of Literal
     */
    public static Literal newLiteral() {
        return new RuntimeLiteralImpl();
    }

    /**
     * Creates a new instance of NamedIndividual
     *
     * @return A new instance of NamedIndividual
     */
    public static NamedIndividual newNamedIndividual() {
        return new RuntimeNamedIndividualImpl();
    }

    /**
     * Creates a new instance of ObjectAllValuesFrom
     *
     * @return A new instance of ObjectAllValuesFrom
     */
    public static ObjectAllValuesFrom newObjectAllValuesFrom() {
        return new RuntimeObjectAllValuesFromImpl();
    }

    /**
     * Creates a new instance of ObjectExactCardinality
     *
     * @return A new instance of ObjectExactCardinality
     */
    public static ObjectExactCardinality newObjectExactCardinality() {
        return new RuntimeObjectExactCardinalityImpl();
    }

    /**
     * Creates a new instance of ObjectHasSelf
     *
     * @return A new instance of ObjectHasSelf
     */
    public static ObjectHasSelf newObjectHasSelf() {
        return new RuntimeObjectHasSelfImpl();
    }

    /**
     * Creates a new instance of ObjectHasValue
     *
     * @return A new instance of ObjectHasValue
     */
    public static ObjectHasValue newObjectHasValue() {
        return new RuntimeObjectHasValueImpl();
    }

    /**
     * Creates a new instance of ObjectMaxCardinality
     *
     * @return A new instance of ObjectMaxCardinality
     */
    public static ObjectMaxCardinality newObjectMaxCardinality() {
        return new RuntimeObjectMaxCardinalityImpl();
    }

    /**
     * Creates a new instance of ObjectMinCardinality
     *
     * @return A new instance of ObjectMinCardinality
     */
    public static ObjectMinCardinality newObjectMinCardinality() {
        return new RuntimeObjectMinCardinalityImpl();
    }

    /**
     * Creates a new instance of ObjectProperty
     *
     * @return A new instance of ObjectProperty
     */
    public static ObjectProperty newObjectProperty() {
        return new RuntimeObjectPropertyImpl();
    }

    /**
     * Creates a new instance of ObjectPropertyAssertion
     *
     * @return A new instance of ObjectPropertyAssertion
     */
    public static ObjectPropertyAssertion newObjectPropertyAssertion() {
        return new RuntimeObjectPropertyAssertionImpl();
    }

    /**
     * Creates a new instance of ObjectSomeValuesFrom
     *
     * @return A new instance of ObjectSomeValuesFrom
     */
    public static ObjectSomeValuesFrom newObjectSomeValuesFrom() {
        return new RuntimeObjectSomeValuesFromImpl();
    }

}
