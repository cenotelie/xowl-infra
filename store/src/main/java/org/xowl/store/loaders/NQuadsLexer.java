/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 1.2.0.0
 */

package org.xowl.store.loaders;

import org.xowl.hime.redist.Symbol;
import org.xowl.hime.redist.lexer.Automaton;
import org.xowl.hime.redist.lexer.PrefetchedLexer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a lexer
 */
class NQuadsLexer extends PrefetchedLexer {
    /**
     * The automaton for this lexer
     */
    private static final Automaton automaton = Automaton.find(NQuadsLexer.class, "NQuadsLexer.bin");

    /**
     * Contains the constant IDs for the terminals for this lexer
     */
    public static class ID {
        /**
         * The unique identifier for terminal PN_CHARS
         */
        public static final int PN_CHARS = 0x000C;
        /**
         * The unique identifier for terminal BLANK
         */
        public static final int BLANK = 0x0006;
        /**
         * The unique identifier for terminal LANGTAG
         */
        public static final int LANGTAG = 0x0007;
        /**
         * The unique identifier for terminal IRIREF
         */
        public static final int IRIREF = 0x0008;
        /**
         * The unique identifier for terminal STRING_LITERAL_QUOTE
         */
        public static final int STRING_LITERAL_QUOTE = 0x0009;
        /**
         * The unique identifier for terminal BLANK_NODE_LABEL
         */
        public static final int BLANK_NODE_LABEL = 0x000D;
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
            new Symbol(0x000C, "PN_CHARS"),
            new Symbol(0x0016, "."),
            new Symbol(0x0006, "BLANK"),
            new Symbol(0x0007, "LANGTAG"),
            new Symbol(0x0008, "IRIREF"),
            new Symbol(0x0009, "STRING_LITERAL_QUOTE"),
            new Symbol(0x0017, "^^"),
            new Symbol(0x000D, "BLANK_NODE_LABEL")};

    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public NQuadsLexer(String input) {
        super(automaton, terminals, 0x0006, input);
    }

    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public NQuadsLexer(InputStream input) throws IOException {
        super(automaton, terminals, 0x0006, input);
    }
}
