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
import org.xowl.hime.redist.lexer.ContextFreeLexer;

import java.io.InputStreamReader;

/**
 * Represents a lexer
 */
class JSONLDLexer extends ContextFreeLexer {
    /**
     * The automaton for this lexer
     */
    private static final Automaton commonAutomaton = Automaton.find(JSONLDLexer.class, "JSONLDLexer.bin");
    /**
     * Contains the constant IDs for the terminals for this lexer
     */
    public static class ID {
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
         * The unique identifier for terminal LITERAL_INTEGER
         */
        public static final int LITERAL_INTEGER = 0x0007;
        /**
         * The unique identifier for terminal COMMENT
         */
        public static final int COMMENT = 0x0005;
        /**
         * The unique identifier for terminal LITERAL_STRING
         */
        public static final int LITERAL_STRING = 0x000A;
        /**
         * The unique identifier for terminal LITERAL_DECIMAL
         */
        public static final int LITERAL_DECIMAL = 0x0008;
        /**
         * The unique identifier for terminal LITERAL_DOUBLE
         */
        public static final int LITERAL_DOUBLE = 0x0009;
        /**
         * The unique identifier for terminal LITERAL_NULL
         */
        public static final int LITERAL_NULL = 0x000B;
        /**
         * The unique identifier for terminal LITERAL_TRUE
         */
        public static final int LITERAL_TRUE = 0x000C;
        /**
         * The unique identifier for terminal LITERAL_FALSE
         */
        public static final int LITERAL_FALSE = 0x000D;
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
     *
     * The terminals are in an order consistent with the automaton,
     * so that terminal indices in the automaton can be used to retrieve the terminals in this table
     */
    private static final Symbol[] terminals = {
        new Symbol(0x0001, "ε"),
        new Symbol(0x0002, "$"),
        new Symbol(0x0013, "{"),
        new Symbol(0x0014, ","),
        new Symbol(0x0016, "}"),
        new Symbol(0x0017, ":"),
        new Symbol(0x0018, "["),
        new Symbol(0x001A, "]"),
        new Symbol(0x0006, "BLANK"),
        new Symbol(0x0003, "NEW_LINE"),
        new Symbol(0x0004, "WHITE_SPACE"),
        new Symbol(0x0007, "LITERAL_INTEGER"),
        new Symbol(0x0005, "COMMENT"),
        new Symbol(0x000A, "LITERAL_STRING"),
        new Symbol(0x0008, "LITERAL_DECIMAL"),
        new Symbol(0x0009, "LITERAL_DOUBLE"),
        new Symbol(0x000B, "LITERAL_NULL"),
        new Symbol(0x000C, "LITERAL_TRUE"),
        new Symbol(0x000D, "LITERAL_FALSE") };
    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public JSONLDLexer(String input) {
        super(commonAutomaton, terminals, 0x0006, input);
    }
    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public JSONLDLexer(InputStreamReader input) {
        super(commonAutomaton, terminals, 0x0006, input);
    }
}
