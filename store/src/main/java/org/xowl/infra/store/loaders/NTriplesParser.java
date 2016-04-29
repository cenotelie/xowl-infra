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
class NTriplesParser extends LRkParser {
    /**
     * The automaton for this parser
     */
    private static final LRkAutomaton commonAutomaton = LRkAutomaton.find(NTriplesParser.class, "NTriplesParser.bin");
    /**
     * Contains the constant IDs for the variables and virtuals in this parser
     */
    public static class ID {
        /**
         * The unique identifier for variable ntriplesDoc
         */
        public static final int ntriplesDoc = 0x000E;
        /**
         * The unique identifier for variable triple
         */
        public static final int triple = 0x000F;
        /**
         * The unique identifier for variable subject
         */
        public static final int subject = 0x0010;
        /**
         * The unique identifier for variable predicate
         */
        public static final int predicate = 0x0011;
        /**
         * The unique identifier for variable object
         */
        public static final int object = 0x0012;
        /**
         * The unique identifier for variable literal
         */
        public static final int literal = 0x0013;
        /**
         * The unique identifier for variable __axiom
         */
        public static final int __axiom = 0x0017;
    }
    /**
     * The collection of variables matched by this parser
     *
     * The variables are in an order consistent with the automaton,
     * so that variable indices in the automaton can be used to retrieve the variables in this table
     */
    private static final Symbol[] variables = {
        new Symbol(0x000E, "ntriplesDoc"), 
        new Symbol(0x000F, "triple"), 
        new Symbol(0x0010, "subject"), 
        new Symbol(0x0011, "predicate"), 
        new Symbol(0x0012, "object"), 
        new Symbol(0x0013, "literal"), 
        new Symbol(0x0014, "__V20"), 
        new Symbol(0x0017, "__axiom") };
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
    public NTriplesParser(NTriplesLexer lexer) {
        super(commonAutomaton, variables, virtuals, null, lexer);
    }
}
