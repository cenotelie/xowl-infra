/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 2.0.0.0
 */

package org.xowl.store.loaders;

import org.xowl.hime.redist.Symbol;
import org.xowl.hime.redist.lexer.Automaton;
import org.xowl.hime.redist.lexer.ContextFreeLexer;

import java.io.InputStreamReader;

/**
 * Represents a lexer
 */
class NQuadsLexer extends ContextFreeLexer {
    /**
     * The automaton for this lexer
     */
    private static final Automaton commonAutomaton = Automaton.find(NQuadsLexer.class, "NQuadsLexer.bin");

    /**
     * Contains the constant IDs for the terminals for this lexer
     */
    public static class ID {
        /**
         * The unique identifier for terminal PN_CHARS
         */
        public static final int PN_CHARS = 0x000C;
        /**
         * The unique identifier for terminal PN_CHARS_U
         */
        public static final int PN_CHARS_U = 0x000B;
        /**
         * The unique identifier for terminal BLANK
         */
        public static final int BLANK = 0x0006;
        /**
         * The unique identifier for terminal NEW_LINE
         */
        public static final int NEW_LINE = 0x0003;
        /**
         * The unique identifier for terminal WHITE_SPACE
         */
        public static final int WHITE_SPACE = 0x0004;
        /**
         * The unique identifier for terminal PN_CHARS_BASE
         */
        public static final int PN_CHARS_BASE = 0x000A;
        /**
         * The unique identifier for terminal COMMENT
         */
        public static final int COMMENT = 0x0005;
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
     * Contains the constant IDs for the contexts for this lexer
     */
    public static class Context {
        /**
         * The unique identifier for the default context
         */
        public static final int DEFAULT = 0;
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
            new Symbol(0x000B, "PN_CHARS_U"),
            new Symbol(0x0016, "."),
            new Symbol(0x0006, "BLANK"),
            new Symbol(0x0003, "NEW_LINE"),
            new Symbol(0x0004, "WHITE_SPACE"),
            new Symbol(0x000A, "PN_CHARS_BASE"),
            new Symbol(0x0005, "COMMENT"),
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
        super(commonAutomaton, terminals, 0x0006, input);
    }

    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public NQuadsLexer(InputStreamReader input) {
        super(commonAutomaton, terminals, 0x0006, input);
    }
}
