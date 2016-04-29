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
import org.xowl.hime.redist.parsers.LRkAutomaton;
import org.xowl.hime.redist.parsers.LRkParser;

/**
 * Represents a parser
 */
class RDFTParser extends LRkParser {
    /**
     * The automaton for this parser
     */
    private static final LRkAutomaton commonAutomaton = LRkAutomaton.find(RDFTParser.class, "RDFTParser.bin");
    /**
     * Contains the constant IDs for the variables and virtuals in this parser
     */
    public static class ID {
        /**
         * The unique identifier for variable document
         */
        public static final int document = 0x0020;
        /**
         * The unique identifier for variable directives
         */
        public static final int directives = 0x0021;
        /**
         * The unique identifier for variable directive
         */
        public static final int directive = 0x0022;
        /**
         * The unique identifier for variable prefixID
         */
        public static final int prefixID = 0x0023;
        /**
         * The unique identifier for variable base
         */
        public static final int base = 0x0024;
        /**
         * The unique identifier for variable sparqlBase
         */
        public static final int sparqlBase = 0x0025;
        /**
         * The unique identifier for variable sparqlPrefix
         */
        public static final int sparqlPrefix = 0x0026;
        /**
         * The unique identifier for variable rdfrules
         */
        public static final int rdfrules = 0x0027;
        /**
         * The unique identifier for variable rule
         */
        public static final int rule = 0x0028;
        /**
         * The unique identifier for variable modifiers
         */
        public static final int modifiers = 0x0029;
        /**
         * The unique identifier for variable assertions
         */
        public static final int assertions = 0x002A;
        /**
         * The unique identifier for variable assertion
         */
        public static final int assertion = 0x002B;
        /**
         * The unique identifier for variable triple
         */
        public static final int triple = 0x002C;
        /**
         * The unique identifier for variable subject
         */
        public static final int subject = 0x002D;
        /**
         * The unique identifier for variable predicate
         */
        public static final int predicate = 0x002E;
        /**
         * The unique identifier for variable object
         */
        public static final int object = 0x002F;
        /**
         * The unique identifier for variable iri
         */
        public static final int iri = 0x0030;
        /**
         * The unique identifier for variable prefixedName
         */
        public static final int prefixedName = 0x0031;
        /**
         * The unique identifier for variable blankNode
         */
        public static final int blankNode = 0x0032;
        /**
         * The unique identifier for variable literal
         */
        public static final int literal = 0x0033;
        /**
         * The unique identifier for variable booleanLiteral
         */
        public static final int booleanLiteral = 0x0034;
        /**
         * The unique identifier for variable numericLiteral
         */
        public static final int numericLiteral = 0x0035;
        /**
         * The unique identifier for variable rdfLiteral
         */
        public static final int rdfLiteral = 0x0036;
        /**
         * The unique identifier for variable string
         */
        public static final int string = 0x0037;
        /**
         * The unique identifier for variable variable
         */
        public static final int variable = 0x0038;
        /**
         * The unique identifier for variable __axiom
         */
        public static final int __axiom = 0x004A;
    }
    /**
     * The collection of variables matched by this parser
     *
     * The variables are in an order consistent with the automaton,
     * so that variable indices in the automaton can be used to retrieve the variables in this table
     */
    private static final Symbol[] variables = {
        new Symbol(0x0020, "document"), 
        new Symbol(0x0021, "directives"), 
        new Symbol(0x0022, "directive"), 
        new Symbol(0x0023, "prefixID"), 
        new Symbol(0x0024, "base"), 
        new Symbol(0x0025, "sparqlBase"), 
        new Symbol(0x0026, "sparqlPrefix"), 
        new Symbol(0x0027, "rdfrules"), 
        new Symbol(0x0028, "rule"), 
        new Symbol(0x0029, "modifiers"), 
        new Symbol(0x002A, "assertions"), 
        new Symbol(0x002B, "assertion"), 
        new Symbol(0x002C, "triple"), 
        new Symbol(0x002D, "subject"), 
        new Symbol(0x002E, "predicate"), 
        new Symbol(0x002F, "object"), 
        new Symbol(0x0030, "iri"), 
        new Symbol(0x0031, "prefixedName"), 
        new Symbol(0x0032, "blankNode"), 
        new Symbol(0x0033, "literal"), 
        new Symbol(0x0034, "booleanLiteral"), 
        new Symbol(0x0035, "numericLiteral"), 
        new Symbol(0x0036, "rdfLiteral"), 
        new Symbol(0x0037, "string"), 
        new Symbol(0x0038, "variable"), 
        new Symbol(0x0039, "__V57"), 
        new Symbol(0x003D, "__V61"), 
        new Symbol(0x0041, "__V65"), 
        new Symbol(0x0044, "__V68"), 
        new Symbol(0x0046, "__V70"), 
        new Symbol(0x004A, "__axiom") };
    /**
     * The collection of virtuals matched by this parser
     *
     * The virtuals are in an order consistent with the automaton,
     * so that virtual indices in the automaton can be used to retrieve the virtuals in this table
     */
    private static final Symbol[] virtuals = {
 };
    /**
     * Initializes a new instance of the parser
     *
     * @param lexer The input lexer
     */
    public RDFTParser(RDFTLexer lexer) {
        super(commonAutomaton, variables, virtuals, null, lexer);
    }
}
