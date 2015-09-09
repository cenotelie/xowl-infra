/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 2.0.0.0
 */

package org.xowl.store.loaders;

import org.xowl.hime.redist.Symbol;
import org.xowl.hime.redist.parsers.LRkAutomaton;
import org.xowl.hime.redist.parsers.LRkParser;

/**
 * Represents a parser
 */
class NQuadsParser extends LRkParser {
    /**
     * The automaton for this parser
     */
    private static final LRkAutomaton commonAutomaton = LRkAutomaton.find(NQuadsParser.class, "NQuadsParser.bin");
    /**
     * Contains the constant IDs for the variables and virtuals in this parser
     */
    public static class ID {
        /**
         * The unique identifier for variable nquadsDoc
         */
        public static final int nquadsDoc = 0x000E;
        /**
         * The unique identifier for variable statement
         */
        public static final int statement = 0x000F;
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
         * The unique identifier for variable graphLabel
         */
        public static final int graphLabel = 0x0013;
        /**
         * The unique identifier for variable literal
         */
        public static final int literal = 0x0014;
        /**
         * The unique identifier for variable __axiom
         */
        public static final int __axiom = 0x0018;
    }
    /**
     * The collection of variables matched by this parser
     *
     * The variables are in an order consistent with the automaton,
     * so that variable indices in the automaton can be used to retrieve the variables in this table
     */
    private static final Symbol[] variables = {
        new Symbol(0x000E, "nquadsDoc"), 
        new Symbol(0x000F, "statement"), 
        new Symbol(0x0010, "subject"), 
        new Symbol(0x0011, "predicate"), 
        new Symbol(0x0012, "object"), 
        new Symbol(0x0013, "graphLabel"), 
        new Symbol(0x0014, "literal"), 
        new Symbol(0x0015, "__V21"), 
        new Symbol(0x0018, "__axiom") };
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
    public NQuadsParser(NQuadsLexer lexer) {
        super(commonAutomaton, variables, virtuals, null, lexer);
    }
}
