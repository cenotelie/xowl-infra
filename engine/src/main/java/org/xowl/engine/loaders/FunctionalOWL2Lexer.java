/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 1.3.2.0
 */

package org.xowl.engine.loaders;

import org.xowl.hime.redist.Symbol;
import org.xowl.hime.redist.lexer.Automaton;
import org.xowl.hime.redist.lexer.PrefetchedLexer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a lexer
 */
class FunctionalOWL2Lexer extends PrefetchedLexer {
    /**
     * The automaton for this lexer
     */
    private static final Automaton automaton = Automaton.find(FunctionalOWL2Lexer.class, "FunctionalOWL2Lexer.bin");

    /**
     * Contains the constant IDs for the terminals for this lexer
     */
    public static class ID {
        /**
         * The unique identifier for terminal PN_LOCAL
         */
        public static final int PN_LOCAL = 0x000E;
        /**
         * The unique identifier for terminal BLANK
         */
        public static final int BLANK = 0x0006;
        /**
         * The unique identifier for terminal PNAME_NS
         */
        public static final int PNAME_NS = 0x000F;
        /**
         * The unique identifier for terminal PN_CHARS
         */
        public static final int PN_CHARS = 0x000C;
        /**
         * The unique identifier for terminal INTEGER
         */
        public static final int INTEGER = 0x0012;
        /**
         * The unique identifier for terminal LANGTAG
         */
        public static final int LANGTAG = 0x0007;
        /**
         * The unique identifier for terminal IRIREF
         */
        public static final int IRIREF = 0x0008;
        /**
         * The unique identifier for terminal STRING
         */
        public static final int STRING = 0x0013;
        /**
         * The unique identifier for terminal PNAME_LN
         */
        public static final int PNAME_LN = 0x0010;
        /**
         * The unique identifier for terminal BLANK_NODE_LABEL
         */
        public static final int BLANK_NODE_LABEL = 0x0011;
    }

    /**
     * The collection of terminals matched by this lexer
     * <p/>
     * The terminals are in an order consistent with the automaton,
     * so that terminal indices in the automaton can be used to retrieve the terminals in this table
     */
    private static final Symbol[] terminals = {
            new Symbol(0x0001, "ε"),
            new Symbol(0x0002, "$"),
            new Symbol(0x000E, "PN_LOCAL"),
            new Symbol(0x0077, "("),
            new Symbol(0x0078, "="),
            new Symbol(0x0079, ")"),
            new Symbol(0x0006, "BLANK"),
            new Symbol(0x000F, "PNAME_NS"),
            new Symbol(0x000C, "PN_CHARS"),
            new Symbol(0x0012, "INTEGER"),
            new Symbol(0x0007, "LANGTAG"),
            new Symbol(0x0008, "IRIREF"),
            new Symbol(0x0013, "STRING"),
            new Symbol(0x0080, "^^"),
            new Symbol(0x0010, "PNAME_LN"),
            new Symbol(0x0011, "BLANK_NODE_LABEL"),
            new Symbol(0x00A4, "Class"),
            new Symbol(0x0076, "Prefix"),
            new Symbol(0x007C, "Import"),
            new Symbol(0x00C9, "HasKey"),
            new Symbol(0x007A, "Ontology"),
            new Symbol(0x00A5, "Datatype"),
            new Symbol(0x0089, "DataOneOf"),
            new Symbol(0x007F, "Annotation"),
            new Symbol(0x00AA, "SubClassOf"),
            new Symbol(0x0092, "ObjectOneOf"),
            new Symbol(0x0086, "DataUnionOf"),
            new Symbol(0x00A3, "Declaration"),
            new Symbol(0x009F, "DataHasValue"),
            new Symbol(0x00A7, "DataProperty"),
            new Symbol(0x008F, "ObjectUnionOf"),
            new Symbol(0x0097, "ObjectHasSelf"),
            new Symbol(0x00AF, "DisjointUnion"),
            new Symbol(0x00A6, "ObjectProperty"),
            new Symbol(0x0096, "ObjectHasValue"),
            new Symbol(0x00D0, "ClassAssertion"),
            new Symbol(0x00CC, "SameIndividual"),
            new Symbol(0x0083, "ObjectInverseOf"),
            new Symbol(0x00AD, "DisjointClasses"),
            new Symbol(0x00A9, "NamedIndividual"),
            new Symbol(0x0088, "DataComplementOf"),
            new Symbol(0x009D, "DataAllValuesFrom"),
            new Symbol(0x00C6, "DataPropertyRange"),
            new Symbol(0x00C0, "SubDataPropertyOf"),
            new Symbol(0x00AB, "EquivalentClasses"),
            new Symbol(0x0091, "ObjectComplementOf"),
            new Symbol(0x00A8, "AnnotationProperty"),
            new Symbol(0x0084, "DataIntersectionOf"),
            new Symbol(0x00C8, "DatatypeDefinition"),
            new Symbol(0x009B, "DataSomeValuesFrom"),
            new Symbol(0x00A0, "DataMinCardinality"),
            new Symbol(0x00A1, "DataMaxCardinality"),
            new Symbol(0x00C5, "DataPropertyDomain"),
            new Symbol(0x0081, "ObjectPropertyChain"),
            new Symbol(0x00B8, "ObjectPropertyRange"),
            new Symbol(0x0095, "ObjectAllValuesFrom"),
            new Symbol(0x00D5, "AnnotationAssertion"),
            new Symbol(0x008B, "DatatypeRestriction"),
            new Symbol(0x00B1, "SubObjectPropertyOf"),
            new Symbol(0x00B7, "ObjectPropertyDomain"),
            new Symbol(0x008D, "ObjectIntersectionOf"),
            new Symbol(0x0094, "ObjectSomeValuesFrom"),
            new Symbol(0x0098, "ObjectMinCardinality"),
            new Symbol(0x0099, "ObjectMaxCardinality"),
            new Symbol(0x00A2, "DataExactCardinality"),
            new Symbol(0x00CE, "DifferentIndividuals"),
            new Symbol(0x00D3, "DataPropertyAssertion"),
            new Symbol(0x009A, "ObjectExactCardinality"),
            new Symbol(0x00C3, "DisjointDataProperties"),
            new Symbol(0x00C7, "FunctionalDataProperty"),
            new Symbol(0x00D1, "ObjectPropertyAssertion"),
            new Symbol(0x00B6, "InverseObjectProperties"),
            new Symbol(0x00D8, "AnnotationPropertyRange"),
            new Symbol(0x00D6, "SubAnnotationPropertyOf"),
            new Symbol(0x00BD, "SymmetricObjectProperty"),
            new Symbol(0x00BB, "ReflexiveObjectProperty"),
            new Symbol(0x00D7, "AnnotationPropertyDomain"),
            new Symbol(0x00BE, "AsymmetricObjectProperty"),
            new Symbol(0x00B4, "DisjointObjectProperties"),
            new Symbol(0x00C1, "EquivalentDataProperties"),
            new Symbol(0x00B9, "FunctionalObjectProperty"),
            new Symbol(0x00BF, "TransitiveObjectProperty"),
            new Symbol(0x00BC, "IrreflexiveObjectProperty"),
            new Symbol(0x00B2, "EquivalentObjectProperties"),
            new Symbol(0x00D4, "NegativeDataPropertyAssertion"),
            new Symbol(0x00BA, "InverseFunctionalObjectProperty"),
            new Symbol(0x00D2, "NegativeObjectPropertyAssertion")};

    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public FunctionalOWL2Lexer(String input) {
        super(automaton, terminals, 0x0006, input);
    }

    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public FunctionalOWL2Lexer(InputStream input) throws IOException {
        super(automaton, terminals, 0x0006, input);
    }
}
