/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 1.2.0.0
 */

package org.xowl.store.loaders;

import org.xowl.hime.redist.Symbol;
import org.xowl.hime.redist.parsers.LRkAutomaton;
import org.xowl.hime.redist.parsers.LRkParser;

/**
 * Represents a parser
 *
 * @author Laurent Wouters
 */
class NTriplesParser extends LRkParser {
    /**
     * The automaton for this parser
     */
    private static final LRkAutomaton automaton = LRkAutomaton.find(NTriplesParser.class, "/org/xowl/store/loaders/NTriplesParser.bin");
    /**
     * The collection of variables matched by this parser
     * <p/>
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
            new Symbol(0x0014, "_gen_V20"),
            new Symbol(0x0017, "_Axiom_")};
    /**
     * The collection of virtuals matched by this parser
     * <p/>
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
        super(automaton, variables, virtuals, null, lexer);
    }

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
         * The unique identifier for variable _Axiom_
         */
        public static final int _Axiom_ = 0x0017;
    }
}
