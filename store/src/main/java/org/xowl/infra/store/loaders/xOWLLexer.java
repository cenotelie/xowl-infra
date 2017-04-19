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

/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 2.0.0.0
 */

package org.xowl.infra.store.loaders;

import org.xowl.hime.redist.Symbol;
import org.xowl.hime.redist.lexer.Automaton;
import org.xowl.hime.redist.lexer.ContextSensitiveLexer;

import java.io.InputStreamReader;

/**
 * Represents a lexer
 */
class xOWLLexer extends ContextSensitiveLexer {
    /**
     * The automaton for this lexer
     */
    private static final Automaton commonAutomaton = Automaton.find(xOWLLexer.class, "xOWLLexer.bin");
    /**
     * Contains the constant IDs for the terminals for this lexer
     */
    public static class ID {
        /**
         * The unique identifier for terminal CLJ_DEREF
         */
        public static final int CLJ_DEREF = 0x0038;
        /**
         * The unique identifier for terminal CLJ_SYMBOL
         */
        public static final int CLJ_SYMBOL = 0x0024;
        /**
         * The unique identifier for terminal CLJ_LP
         */
        public static final int CLJ_LP = 0x002F;
        /**
         * The unique identifier for terminal OWL2_LP
         */
        public static final int OWL2_LP = 0x0015;
        /**
         * The unique identifier for terminal CLJ_RP
         */
        public static final int CLJ_RP = 0x0030;
        /**
         * The unique identifier for terminal OWL2_RP
         */
        public static final int OWL2_RP = 0x0016;
        /**
         * The unique identifier for terminal OWL2_PREFIX_MAP
         */
        public static final int OWL2_PREFIX_MAP = 0x0017;
        /**
         * The unique identifier for terminal CLJ_METADATA
         */
        public static final int CLJ_METADATA = 0x0039;
        /**
         * The unique identifier for terminal XOWL_OPAQUE_EXP
         */
        public static final int XOWL_OPAQUE_EXP = 0x0019;
        /**
         * The unique identifier for terminal PNAME_NS
         */
        public static final int PNAME_NS = 0x0010;
        /**
         * The unique identifier for terminal LITERAL_ARGUMENT
         */
        public static final int LITERAL_ARGUMENT = 0x002E;
        /**
         * The unique identifier for terminal CLJ_LB
         */
        public static final int CLJ_LB = 0x0031;
        /**
         * The unique identifier for terminal CLJ_RB
         */
        public static final int CLJ_RB = 0x0032;
        /**
         * The unique identifier for terminal CLJ_LC
         */
        public static final int CLJ_LC = 0x0033;
        /**
         * The unique identifier for terminal CLJ_RC
         */
        public static final int CLJ_RC = 0x0034;
        /**
         * The unique identifier for terminal CLJ_DISPATCH
         */
        public static final int CLJ_DISPATCH = 0x0035;
        /**
         * The unique identifier for terminal CLJ_SET_SEPARATOR
         */
        public static final int CLJ_SET_SEPARATOR = 0x0036;
        /**
         * The unique identifier for terminal CLJ_QUOTE
         */
        public static final int CLJ_QUOTE = 0x0037;
        /**
         * The unique identifier for terminal CLJ_SYNTAX_QUOTE
         */
        public static final int CLJ_SYNTAX_QUOTE = 0x003B;
        /**
         * The unique identifier for terminal CLJ_UNQUOTE
         */
        public static final int CLJ_UNQUOTE = 0x003C;
        /**
         * The unique identifier for terminal LITERAL_FLOAT
         */
        public static final int LITERAL_FLOAT = 0x002D;
        /**
         * The unique identifier for terminal LITERAL_INTEGER
         */
        public static final int LITERAL_INTEGER = 0x002B;
        /**
         * The unique identifier for terminal OWL2_INTEGER
         */
        public static final int OWL2_INTEGER = 0x0013;
        /**
         * The unique identifier for terminal BLANK
         */
        public static final int BLANK = 0x0007;
        /**
         * The unique identifier for terminal LANGTAG
         */
        public static final int LANGTAG = 0x000E;
        /**
         * The unique identifier for terminal IRIREF
         */
        public static final int IRIREF = 0x000F;
        /**
         * The unique identifier for terminal LITERAL_STRING
         */
        public static final int LITERAL_STRING = 0x0026;
        /**
         * The unique identifier for terminal OWL2_STRING
         */
        public static final int OWL2_STRING = 0x0014;
        /**
         * The unique identifier for terminal OWL2_LITERAL_TYPE
         */
        public static final int OWL2_LITERAL_TYPE = 0x0018;
        /**
         * The unique identifier for terminal XOWL_QVAR
         */
        public static final int XOWL_QVAR = 0x001A;
        /**
         * The unique identifier for terminal PNAME_LN
         */
        public static final int PNAME_LN = 0x0011;
        /**
         * The unique identifier for terminal CLJ_KEYWORD
         */
        public static final int CLJ_KEYWORD = 0x0025;
        /**
         * The unique identifier for terminal LITERAL_CHAR
         */
        public static final int LITERAL_CHAR = 0x0027;
        /**
         * The unique identifier for terminal CLJ_IGNORE
         */
        public static final int CLJ_IGNORE = 0x003A;
        /**
         * The unique identifier for terminal CLJ_CONDITIONAL
         */
        public static final int CLJ_CONDITIONAL = 0x003E;
        /**
         * The unique identifier for terminal CLJ_UNQUOTE_SPLICE
         */
        public static final int CLJ_UNQUOTE_SPLICE = 0x003D;
        /**
         * The unique identifier for terminal BLANK_NODE_LABEL
         */
        public static final int BLANK_NODE_LABEL = 0x0012;
        /**
         * The unique identifier for terminal RULE_ELEMENT_NOT
         */
        public static final int RULE_ELEMENT_NOT = 0x0020;
        /**
         * The unique identifier for terminal LITERAL_NIL
         */
        public static final int LITERAL_NIL = 0x0028;
        /**
         * The unique identifier for terminal LITERAL_RATIO
         */
        public static final int LITERAL_RATIO = 0x002C;
        /**
         * The unique identifier for terminal RULE_BLOCK_RULE
         */
        public static final int RULE_BLOCK_RULE = 0x001B;
        /**
         * The unique identifier for terminal RULE_ELEMENT_META
         */
        public static final int RULE_ELEMENT_META = 0x001F;
        /**
         * The unique identifier for terminal LITERAL_TRUE
         */
        public static final int LITERAL_TRUE = 0x0029;
        /**
         * The unique identifier for terminal RULE_BLOCK_GUARD
         */
        public static final int RULE_BLOCK_GUARD = 0x001E;
        /**
         * The unique identifier for terminal LITERAL_FALSE
         */
        public static final int LITERAL_FALSE = 0x002A;
        /**
         * The unique identifier for terminal CLJ_XOWL_QUERY
         */
        public static final int CLJ_XOWL_QUERY = 0x003F;
        /**
         * The unique identifier for terminal RULE_BLOCK_ANTECEDENTS
         */
        public static final int RULE_BLOCK_ANTECEDENTS = 0x001C;
        /**
         * The unique identifier for terminal RULE_BLOCK_CONSEQUENTS
         */
        public static final int RULE_BLOCK_CONSEQUENTS = 0x001D;
        /**
         * The unique identifier for terminal CLJ_XOWL_INSERT
         */
        public static final int CLJ_XOWL_INSERT = 0x0040;
    }
    /**
     * Contains the constant IDs for the contexts for this lexer
     */
    public static class Context {
        /**
         * The unique identifier for the default context
         */
        public static final int DEFAULT = 0;
        /**
         * The unique identifier for context owl2
         */
        public static final int owl2 = 0x0001;
        /**
         * The unique identifier for context xowl
         */
        public static final int xowl = 0x0002;
        /**
         * The unique identifier for context clojure
         */
        public static final int clojure = 0x0003;
    }
    /**
     * The collection of terminals matched by this lexer
     *
     * The terminals are in an order consistent with the automaton,
     * so that terminal indices in the automaton can be used to retrieve the terminals in this table
     */
    private static final Symbol[] terminals = {
        new Symbol(0x0001, "ε"),
        new Symbol(0x0002, "$"),
        new Symbol(0x0038, "CLJ_DEREF"),
        new Symbol(0x0024, "CLJ_SYMBOL"),
        new Symbol(0x002F, "CLJ_LP"),
        new Symbol(0x0015, "OWL2_LP"),
        new Symbol(0x0030, "CLJ_RP"),
        new Symbol(0x0016, "OWL2_RP"),
        new Symbol(0x0017, "OWL2_PREFIX_MAP"),
        new Symbol(0x0039, "CLJ_METADATA"),
        new Symbol(0x0019, "XOWL_OPAQUE_EXP"),
        new Symbol(0x0010, "PNAME_NS"),
        new Symbol(0x002E, "LITERAL_ARGUMENT"),
        new Symbol(0x0031, "CLJ_LB"),
        new Symbol(0x0032, "CLJ_RB"),
        new Symbol(0x0033, "CLJ_LC"),
        new Symbol(0x0034, "CLJ_RC"),
        new Symbol(0x0035, "CLJ_DISPATCH"),
        new Symbol(0x0036, "CLJ_SET_SEPARATOR"),
        new Symbol(0x0037, "CLJ_QUOTE"),
        new Symbol(0x003B, "CLJ_SYNTAX_QUOTE"),
        new Symbol(0x003C, "CLJ_UNQUOTE"),
        new Symbol(0x002D, "LITERAL_FLOAT"),
        new Symbol(0x002B, "LITERAL_INTEGER"),
        new Symbol(0x0013, "OWL2_INTEGER"),
        new Symbol(0x0007, "BLANK"),
        new Symbol(0x000E, "LANGTAG"),
        new Symbol(0x000F, "IRIREF"),
        new Symbol(0x0026, "LITERAL_STRING"),
        new Symbol(0x0014, "OWL2_STRING"),
        new Symbol(0x0018, "OWL2_LITERAL_TYPE"),
        new Symbol(0x001A, "XOWL_QVAR"),
        new Symbol(0x0011, "PNAME_LN"),
        new Symbol(0x0025, "CLJ_KEYWORD"),
        new Symbol(0x0027, "LITERAL_CHAR"),
        new Symbol(0x003A, "CLJ_IGNORE"),
        new Symbol(0x003E, "CLJ_CONDITIONAL"),
        new Symbol(0x003D, "CLJ_UNQUOTE_SPLICE"),
        new Symbol(0x0012, "BLANK_NODE_LABEL"),
        new Symbol(0x0020, "RULE_ELEMENT_NOT"),
        new Symbol(0x0028, "LITERAL_NIL"),
        new Symbol(0x002C, "LITERAL_RATIO"),
        new Symbol(0x001B, "RULE_BLOCK_RULE"),
        new Symbol(0x001F, "RULE_ELEMENT_META"),
        new Symbol(0x0029, "LITERAL_TRUE"),
        new Symbol(0x00EF, "Class"),
        new Symbol(0x001E, "RULE_BLOCK_GUARD"),
        new Symbol(0x002A, "LITERAL_FALSE"),
        new Symbol(0x00C5, "Prefix"),
        new Symbol(0x00C8, "Import"),
        new Symbol(0x0114, "HasKey"),
        new Symbol(0x00C6, "Ontology"),
        new Symbol(0x00F0, "Datatype"),
        new Symbol(0x00D4, "DataOneOf"),
        new Symbol(0x00CB, "Annotation"),
        new Symbol(0x003F, "CLJ_XOWL_QUERY"),
        new Symbol(0x00F5, "SubClassOf"),
        new Symbol(0x001C, "RULE_BLOCK_ANTECEDENTS"),
        new Symbol(0x001D, "RULE_BLOCK_CONSEQUENTS"),
        new Symbol(0x0040, "CLJ_XOWL_INSERT"),
        new Symbol(0x00DD, "ObjectOneOf"),
        new Symbol(0x00D1, "DataUnionOf"),
        new Symbol(0x00EE, "Declaration"),
        new Symbol(0x00EA, "DataHasValue"),
        new Symbol(0x00F2, "DataProperty"),
        new Symbol(0x00DA, "ObjectUnionOf"),
        new Symbol(0x00E2, "ObjectHasSelf"),
        new Symbol(0x00FA, "DisjointUnion"),
        new Symbol(0x011B, "ClassAssertion"),
        new Symbol(0x00F1, "ObjectProperty"),
        new Symbol(0x00E1, "ObjectHasValue"),
        new Symbol(0x0117, "SameIndividual"),
        new Symbol(0x00F4, "NamedIndividual"),
        new Symbol(0x00CE, "ObjectInverseOf"),
        new Symbol(0x00F8, "DisjointClasses"),
        new Symbol(0x00D3, "DataComplementOf"),
        new Symbol(0x00E8, "DataAllValuesFrom"),
        new Symbol(0x0111, "DataPropertyRange"),
        new Symbol(0x010B, "SubDataPropertyOf"),
        new Symbol(0x00F6, "EquivalentClasses"),
        new Symbol(0x00F3, "AnnotationProperty"),
        new Symbol(0x00DC, "ObjectComplementOf"),
        new Symbol(0x00CF, "DataIntersectionOf"),
        new Symbol(0x0113, "DatatypeDefinition"),
        new Symbol(0x00E6, "DataSomeValuesFrom"),
        new Symbol(0x00EB, "DataMinCardinality"),
        new Symbol(0x00EC, "DataMaxCardinality"),
        new Symbol(0x0110, "DataPropertyDomain"),
        new Symbol(0x0133, "FunctionDefinition"),
        new Symbol(0x0120, "AnnotationAssertion"),
        new Symbol(0x00CC, "ObjectPropertyChain"),
        new Symbol(0x0103, "ObjectPropertyRange"),
        new Symbol(0x00E0, "ObjectAllValuesFrom"),
        new Symbol(0x00D6, "DatatypeRestriction"),
        new Symbol(0x00FC, "SubObjectPropertyOf"),
        new Symbol(0x0102, "ObjectPropertyDomain"),
        new Symbol(0x00D8, "ObjectIntersectionOf"),
        new Symbol(0x00DF, "ObjectSomeValuesFrom"),
        new Symbol(0x00E3, "ObjectMinCardinality"),
        new Symbol(0x00E4, "ObjectMaxCardinality"),
        new Symbol(0x00ED, "DataExactCardinality"),
        new Symbol(0x0119, "DifferentIndividuals"),
        new Symbol(0x011E, "DataPropertyAssertion"),
        new Symbol(0x00E5, "ObjectExactCardinality"),
        new Symbol(0x010E, "DisjointDataProperties"),
        new Symbol(0x0112, "FunctionalDataProperty"),
        new Symbol(0x0106, "ReflexiveObjectProperty"),
        new Symbol(0x0123, "AnnotationPropertyRange"),
        new Symbol(0x011C, "ObjectPropertyAssertion"),
        new Symbol(0x0101, "InverseObjectProperties"),
        new Symbol(0x0121, "SubAnnotationPropertyOf"),
        new Symbol(0x0108, "SymmetricObjectProperty"),
        new Symbol(0x0122, "AnnotationPropertyDomain"),
        new Symbol(0x0109, "AsymmetricObjectProperty"),
        new Symbol(0x00FF, "DisjointObjectProperties"),
        new Symbol(0x010C, "EquivalentDataProperties"),
        new Symbol(0x0104, "FunctionalObjectProperty"),
        new Symbol(0x010A, "TransitiveObjectProperty"),
        new Symbol(0x0107, "IrreflexiveObjectProperty"),
        new Symbol(0x00FD, "EquivalentObjectProperties"),
        new Symbol(0x011F, "NegativeDataPropertyAssertion"),
        new Symbol(0x011D, "NegativeObjectPropertyAssertion"),
        new Symbol(0x0105, "InverseFunctionalObjectProperty") };
    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public xOWLLexer(String input) {
        super(commonAutomaton, terminals, 0x0007, input);
    }
    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public xOWLLexer(InputStreamReader input) {
        super(commonAutomaton, terminals, 0x0007, input);
    }
}
