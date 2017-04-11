/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 2.0.5.0
 */

package org.xowl.infra.store.loaders;

import org.xowl.hime.redist.Symbol;
import org.xowl.hime.redist.lexer.Automaton;
import org.xowl.hime.redist.lexer.ContextSensitiveLexer;

import java.io.InputStreamReader;

/**
 * Represents a lexer
 */
class xRDFLexer extends ContextSensitiveLexer {
    /**
     * The automaton for this lexer
     */
    private static final Automaton commonAutomaton = Automaton.find(xRDFLexer.class, "xRDFLexer.bin");
    /**
     * Contains the constant IDs for the terminals for this lexer
     */
    public static class ID {
        /**
         * The unique identifier for terminal CLJ_DEREF
         */
        public static final int CLJ_DEREF = 0x012A;
        /**
         * The unique identifier for terminal CLJ_SYMBOL
         */
        public static final int CLJ_SYMBOL = 0x0116;
        /**
         * The unique identifier for terminal OP_LESS
         */
        public static final int OP_LESS = 0x0024;
        /**
         * The unique identifier for terminal BUILTIN
         */
        public static final int BUILTIN = 0x002B;
        /**
         * The unique identifier for terminal CLJ_LB
         */
        public static final int CLJ_LB = 0x0123;
        /**
         * The unique identifier for terminal CLJ_QUOTE
         */
        public static final int CLJ_QUOTE = 0x0129;
        /**
         * The unique identifier for terminal OP_PLUS
         */
        public static final int OP_PLUS = 0x001B;
        /**
         * The unique identifier for terminal OP_MINUS
         */
        public static final int OP_MINUS = 0x001C;
        /**
         * The unique identifier for terminal OP_MULT
         */
        public static final int OP_MULT = 0x001D;
        /**
         * The unique identifier for terminal OP_DIV
         */
        public static final int OP_DIV = 0x001E;
        /**
         * The unique identifier for terminal OP_UNION
         */
        public static final int OP_UNION = 0x0029;
        /**
         * The unique identifier for terminal OP_NOT
         */
        public static final int OP_NOT = 0x0021;
        /**
         * The unique identifier for terminal OP_EQ
         */
        public static final int OP_EQ = 0x0022;
        /**
         * The unique identifier for terminal OP_GREAT
         */
        public static final int OP_GREAT = 0x0026;
        /**
         * The unique identifier for terminal OP_OPT
         */
        public static final int OP_OPT = 0x0028;
        /**
         * The unique identifier for terminal CLJ_METADATA
         */
        public static final int CLJ_METADATA = 0x012B;
        /**
         * The unique identifier for terminal OP_INV
         */
        public static final int OP_INV = 0x002A;
        /**
         * The unique identifier for terminal A
         */
        public static final int A = 0x002E;
        /**
         * The unique identifier for terminal CLJ_LP
         */
        public static final int CLJ_LP = 0x0121;
        /**
         * The unique identifier for terminal CLJ_RP
         */
        public static final int CLJ_RP = 0x0122;
        /**
         * The unique identifier for terminal CLJ_LC
         */
        public static final int CLJ_LC = 0x0125;
        /**
         * The unique identifier for terminal CLJ_RC
         */
        public static final int CLJ_RC = 0x0126;
        /**
         * The unique identifier for terminal CLJ_SET_SEPARATOR
         */
        public static final int CLJ_SET_SEPARATOR = 0x0128;
        /**
         * The unique identifier for terminal CLJ_RB
         */
        public static final int CLJ_RB = 0x0124;
        /**
         * The unique identifier for terminal PNAME_NS
         */
        public static final int PNAME_NS = 0x000F;
        /**
         * The unique identifier for terminal LITERAL_ARGUMENT
         */
        public static final int LITERAL_ARGUMENT = 0x0120;
        /**
         * The unique identifier for terminal CLJ_DISPATCH
         */
        public static final int CLJ_DISPATCH = 0x0127;
        /**
         * The unique identifier for terminal CLJ_SYNTAX_QUOTE
         */
        public static final int CLJ_SYNTAX_QUOTE = 0x012D;
        /**
         * The unique identifier for terminal CLJ_UNQUOTE
         */
        public static final int CLJ_UNQUOTE = 0x012E;
        /**
         * The unique identifier for terminal XOWL_OPAQUE_EXP
         */
        public static final int XOWL_OPAQUE_EXP = 0x0133;
        /**
         * The unique identifier for terminal LITERAL_FLOAT
         */
        public static final int LITERAL_FLOAT = 0x011F;
        /**
         * The unique identifier for terminal LITERAL_INTEGER
         */
        public static final int LITERAL_INTEGER = 0x011D;
        /**
         * The unique identifier for terminal INTEGER
         */
        public static final int INTEGER = 0x0014;
        /**
         * The unique identifier for terminal BLANK
         */
        public static final int BLANK = 0x0006;
        /**
         * The unique identifier for terminal LANGTAG
         */
        public static final int LANGTAG = 0x0007;
        /**
         * The unique identifier for terminal OP_LEQ
         */
        public static final int OP_LEQ = 0x0025;
        /**
         * The unique identifier for terminal IRIREF
         */
        public static final int IRIREF = 0x0008;
        /**
         * The unique identifier for terminal ANON
         */
        public static final int ANON = 0x0013;
        /**
         * The unique identifier for terminal LITERAL_STRING
         */
        public static final int LITERAL_STRING = 0x0118;
        /**
         * The unique identifier for terminal STRING_LITERAL_QUOTE
         */
        public static final int STRING_LITERAL_QUOTE = 0x0017;
        /**
         * The unique identifier for terminal STRING_LITERAL_SINGLE_QUOTE
         */
        public static final int STRING_LITERAL_SINGLE_QUOTE = 0x0018;
        /**
         * The unique identifier for terminal OP_BOR
         */
        public static final int OP_BOR = 0x001F;
        /**
         * The unique identifier for terminal OP_BAND
         */
        public static final int OP_BAND = 0x0020;
        /**
         * The unique identifier for terminal OP_NEQ
         */
        public static final int OP_NEQ = 0x0023;
        /**
         * The unique identifier for terminal OP_GEQ
         */
        public static final int OP_GEQ = 0x0027;
        /**
         * The unique identifier for terminal VARIABLE
         */
        public static final int VARIABLE = 0x0012;
        /**
         * The unique identifier for terminal TO
         */
        public static final int TO = 0x0045;
        /**
         * The unique identifier for terminal AS
         */
        public static final int AS = 0x0034;
        /**
         * The unique identifier for terminal BY
         */
        public static final int BY = 0x003C;
        /**
         * The unique identifier for terminal IN
         */
        public static final int IN = 0x005D;
        /**
         * The unique identifier for terminal DECIMAL
         */
        public static final int DECIMAL = 0x0015;
        /**
         * The unique identifier for terminal PNAME_LN
         */
        public static final int PNAME_LN = 0x0010;
        /**
         * The unique identifier for terminal CLJ_KEYWORD
         */
        public static final int CLJ_KEYWORD = 0x0117;
        /**
         * The unique identifier for terminal LITERAL_CHAR
         */
        public static final int LITERAL_CHAR = 0x0119;
        /**
         * The unique identifier for terminal CLJ_IGNORE
         */
        public static final int CLJ_IGNORE = 0x012C;
        /**
         * The unique identifier for terminal CLJ_CONDITIONAL
         */
        public static final int CLJ_CONDITIONAL = 0x0130;
        /**
         * The unique identifier for terminal CLJ_UNQUOTE_SPLICE
         */
        public static final int CLJ_UNQUOTE_SPLICE = 0x012F;
        /**
         * The unique identifier for terminal BLANK_NODE_LABEL
         */
        public static final int BLANK_NODE_LABEL = 0x0011;
        /**
         * The unique identifier for terminal ASK
         */
        public static final int ASK = 0x0038;
        /**
         * The unique identifier for terminal ASC
         */
        public static final int ASC = 0x003F;
        /**
         * The unique identifier for terminal ADD
         */
        public static final int ADD = 0x004B;
        /**
         * The unique identifier for terminal ALL
         */
        public static final int ALL = 0x0055;
        /**
         * The unique identifier for terminal NOT
         */
        public static final int NOT = 0x005E;
        /**
         * The unique identifier for terminal LITERAL_NIL
         */
        public static final int LITERAL_NIL = 0x011A;
        /**
         * The unique identifier for terminal DOUBLE
         */
        public static final int DOUBLE = 0x0016;
        /**
         * The unique identifier for terminal LITERAL_RATIO
         */
        public static final int LITERAL_RATIO = 0x011E;
        /**
         * The unique identifier for terminal LITERAL_TRUE
         */
        public static final int LITERAL_TRUE = 0x011B;
        /**
         * The unique identifier for terminal TRUE
         */
        public static final int TRUE = 0x002C;
        /**
         * The unique identifier for terminal FROM
         */
        public static final int FROM = 0x0039;
        /**
         * The unique identifier for terminal WITH
         */
        public static final int WITH = 0x0050;
        /**
         * The unique identifier for terminal BASE
         */
        public static final int BASE = 0x0030;
        /**
         * The unique identifier for terminal BIND
         */
        public static final int BIND = 0x0058;
        /**
         * The unique identifier for terminal DESC
         */
        public static final int DESC = 0x0040;
        /**
         * The unique identifier for terminal DROP
         */
        public static final int DROP = 0x0049;
        /**
         * The unique identifier for terminal DATA
         */
        public static final int DATA = 0x0051;
        /**
         * The unique identifier for terminal RULE
         */
        public static final int RULE = 0x0134;
        /**
         * The unique identifier for terminal COPY
         */
        public static final int COPY = 0x004D;
        /**
         * The unique identifier for terminal LOAD
         */
        public static final int LOAD = 0x0047;
        /**
         * The unique identifier for terminal INTO
         */
        public static final int INTO = 0x0046;
        /**
         * The unique identifier for terminal MOVE
         */
        public static final int MOVE = 0x004C;
        /**
         * The unique identifier for terminal META
         */
        public static final int META = 0x0135;
        /**
         * The unique identifier for terminal LITERAL_FALSE
         */
        public static final int LITERAL_FALSE = 0x011C;
        /**
         * The unique identifier for terminal FALSE
         */
        public static final int FALSE = 0x002D;
        /**
         * The unique identifier for terminal WHERE
         */
        public static final int WHERE = 0x0036;
        /**
         * The unique identifier for terminal NAMED
         */
        public static final int NAMED = 0x003A;
        /**
         * The unique identifier for terminal USING
         */
        public static final int USING = 0x0052;
        /**
         * The unique identifier for terminal UNION
         */
        public static final int UNION = 0x0059;
        /**
         * The unique identifier for terminal UNDEF
         */
        public static final int UNDEF = 0x005C;
        /**
         * The unique identifier for terminal GROUP
         */
        public static final int GROUP = 0x003B;
        /**
         * The unique identifier for terminal GRAPH
         */
        public static final int GRAPH = 0x0054;
        /**
         * The unique identifier for terminal ORDER
         */
        public static final int ORDER = 0x003E;
        /**
         * The unique identifier for terminal CLEAR
         */
        public static final int CLEAR = 0x0048;
        /**
         * The unique identifier for terminal LIMIT
         */
        public static final int LIMIT = 0x0041;
        /**
         * The unique identifier for terminal MINUS
         */
        public static final int MINUS = 0x005A;
        /**
         * The unique identifier for terminal STRING_LITERAL_LONG_QUOTE
         */
        public static final int STRING_LITERAL_LONG_QUOTE = 0x001A;
        /**
         * The unique identifier for terminal STRING_LITERAL_LONG_SINGLE_QUOTE
         */
        public static final int STRING_LITERAL_LONG_SINGLE_QUOTE = 0x0019;
        /**
         * The unique identifier for terminal FILTER
         */
        public static final int FILTER = 0x005B;
        /**
         * The unique identifier for terminal PREFIX
         */
        public static final int PREFIX = 0x002F;
        /**
         * The unique identifier for terminal VALUES
         */
        public static final int VALUES = 0x0043;
        /**
         * The unique identifier for terminal SELECT
         */
        public static final int SELECT = 0x0031;
        /**
         * The unique identifier for terminal SILENT
         */
        public static final int SILENT = 0x0044;
        /**
         * The unique identifier for terminal DELETE
         */
        public static final int DELETE = 0x004F;
        /**
         * The unique identifier for terminal OFFSET
         */
        public static final int OFFSET = 0x0042;
        /**
         * The unique identifier for terminal CREATE
         */
        public static final int CREATE = 0x004A;
        /**
         * The unique identifier for terminal EXISTS
         */
        public static final int EXISTS = 0x005F;
        /**
         * The unique identifier for terminal HAVING
         */
        public static final int HAVING = 0x003D;
        /**
         * The unique identifier for terminal INSERT
         */
        public static final int INSERT = 0x004E;
        /**
         * The unique identifier for terminal SERVICE
         */
        public static final int SERVICE = 0x0057;
        /**
         * The unique identifier for terminal DEFAULT
         */
        public static final int DEFAULT = 0x0053;
        /**
         * The unique identifier for terminal REDUCED
         */
        public static final int REDUCED = 0x0033;
        /**
         * The unique identifier for terminal DISTINCT
         */
        public static final int DISTINCT = 0x0032;
        /**
         * The unique identifier for terminal DESCRIBE
         */
        public static final int DESCRIBE = 0x0037;
        /**
         * The unique identifier for terminal OPTIONAL
         */
        public static final int OPTIONAL = 0x0056;
        /**
         * The unique identifier for terminal SEPARATOR
         */
        public static final int SEPARATOR = 0x0060;
        /**
         * The unique identifier for terminal CONSTRUCT
         */
        public static final int CONSTRUCT = 0x0035;
        /**
         * The unique identifier for terminal CLJ_XOWL_QUERY
         */
        public static final int CLJ_XOWL_QUERY = 0x0131;
        /**
         * The unique identifier for terminal CLJ_XOWL_INSERT
         */
        public static final int CLJ_XOWL_INSERT = 0x0132;
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
         * The unique identifier for context clojure
         */
        public static final int clojure = 0x0001;
        /**
         * The unique identifier for context xowl
         */
        public static final int xowl = 0x0002;
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
        new Symbol(0x012A, "CLJ_DEREF"),
        new Symbol(0x0116, "CLJ_SYMBOL"),
        new Symbol(0x0024, "OP_LESS"),
        new Symbol(0x002B, "BUILTIN"),
        new Symbol(0x0123, "CLJ_LB"),
        new Symbol(0x0106, "["),
        new Symbol(0x0129, "CLJ_QUOTE"),
        new Symbol(0x001B, "OP_PLUS"),
        new Symbol(0x001C, "OP_MINUS"),
        new Symbol(0x001D, "OP_MULT"),
        new Symbol(0x001E, "OP_DIV"),
        new Symbol(0x0029, "OP_UNION"),
        new Symbol(0x0021, "OP_NOT"),
        new Symbol(0x0110, "="),
        new Symbol(0x0022, "OP_EQ"),
        new Symbol(0x0026, "OP_GREAT"),
        new Symbol(0x0028, "OP_OPT"),
        new Symbol(0x012B, "CLJ_METADATA"),
        new Symbol(0x002A, "OP_INV"),
        new Symbol(0x002E, "A"),
        new Symbol(0x00EB, ";"),
        new Symbol(0x0121, "CLJ_LP"),
        new Symbol(0x00EF, "("),
        new Symbol(0x0122, "CLJ_RP"),
        new Symbol(0x00F0, ")"),
        new Symbol(0x0125, "CLJ_LC"),
        new Symbol(0x00F3, "{"),
        new Symbol(0x0126, "CLJ_RC"),
        new Symbol(0x00F4, "}"),
        new Symbol(0x00F5, "."),
        new Symbol(0x0128, "CLJ_SET_SEPARATOR"),
        new Symbol(0x0104, ","),
        new Symbol(0x0124, "CLJ_RB"),
        new Symbol(0x0107, "]"),
        new Symbol(0x000F, "PNAME_NS"),
        new Symbol(0x0120, "LITERAL_ARGUMENT"),
        new Symbol(0x0127, "CLJ_DISPATCH"),
        new Symbol(0x012D, "CLJ_SYNTAX_QUOTE"),
        new Symbol(0x012E, "CLJ_UNQUOTE"),
        new Symbol(0x0133, "XOWL_OPAQUE_EXP"),
        new Symbol(0x011F, "LITERAL_FLOAT"),
        new Symbol(0x011D, "LITERAL_INTEGER"),
        new Symbol(0x0014, "INTEGER"),
        new Symbol(0x0006, "BLANK"),
        new Symbol(0x0007, "LANGTAG"),
        new Symbol(0x0025, "OP_LEQ"),
        new Symbol(0x0008, "IRIREF"),
        new Symbol(0x0013, "ANON"),
        new Symbol(0x0118, "LITERAL_STRING"),
        new Symbol(0x0017, "STRING_LITERAL_QUOTE"),
        new Symbol(0x0018, "STRING_LITERAL_SINGLE_QUOTE"),
        new Symbol(0x001F, "OP_BOR"),
        new Symbol(0x0020, "OP_BAND"),
        new Symbol(0x0023, "OP_NEQ"),
        new Symbol(0x0167, "=>"),
        new Symbol(0x0027, "OP_GEQ"),
        new Symbol(0x0012, "VARIABLE"),
        new Symbol(0x0112, "^^"),
        new Symbol(0x0045, "TO"),
        new Symbol(0x0034, "AS"),
        new Symbol(0x003C, "BY"),
        new Symbol(0x005D, "IN"),
        new Symbol(0x0015, "DECIMAL"),
        new Symbol(0x0010, "PNAME_LN"),
        new Symbol(0x0117, "CLJ_KEYWORD"),
        new Symbol(0x0119, "LITERAL_CHAR"),
        new Symbol(0x012C, "CLJ_IGNORE"),
        new Symbol(0x0130, "CLJ_CONDITIONAL"),
        new Symbol(0x012F, "CLJ_UNQUOTE_SPLICE"),
        new Symbol(0x0011, "BLANK_NODE_LABEL"),
        new Symbol(0x0038, "ASK"),
        new Symbol(0x003F, "ASC"),
        new Symbol(0x004B, "ADD"),
        new Symbol(0x0055, "ALL"),
        new Symbol(0x005E, "NOT"),
        new Symbol(0x011A, "LITERAL_NIL"),
        new Symbol(0x0016, "DOUBLE"),
        new Symbol(0x011E, "LITERAL_RATIO"),
        new Symbol(0x011B, "LITERAL_TRUE"),
        new Symbol(0x002C, "TRUE"),
        new Symbol(0x0039, "FROM"),
        new Symbol(0x0050, "WITH"),
        new Symbol(0x0030, "BASE"),
        new Symbol(0x0058, "BIND"),
        new Symbol(0x0040, "DESC"),
        new Symbol(0x0049, "DROP"),
        new Symbol(0x0051, "DATA"),
        new Symbol(0x0134, "RULE"),
        new Symbol(0x004D, "COPY"),
        new Symbol(0x0047, "LOAD"),
        new Symbol(0x0046, "INTO"),
        new Symbol(0x004C, "MOVE"),
        new Symbol(0x0135, "META"),
        new Symbol(0x011C, "LITERAL_FALSE"),
        new Symbol(0x002D, "FALSE"),
        new Symbol(0x0036, "WHERE"),
        new Symbol(0x003A, "NAMED"),
        new Symbol(0x0052, "USING"),
        new Symbol(0x0059, "UNION"),
        new Symbol(0x005C, "UNDEF"),
        new Symbol(0x003B, "GROUP"),
        new Symbol(0x0054, "GRAPH"),
        new Symbol(0x003E, "ORDER"),
        new Symbol(0x0048, "CLEAR"),
        new Symbol(0x0041, "LIMIT"),
        new Symbol(0x005A, "MINUS"),
        new Symbol(0x001A, "STRING_LITERAL_LONG_QUOTE"),
        new Symbol(0x0019, "STRING_LITERAL_LONG_SINGLE_QUOTE"),
        new Symbol(0x005B, "FILTER"),
        new Symbol(0x002F, "PREFIX"),
        new Symbol(0x0043, "VALUES"),
        new Symbol(0x0031, "SELECT"),
        new Symbol(0x0044, "SILENT"),
        new Symbol(0x004F, "DELETE"),
        new Symbol(0x0042, "OFFSET"),
        new Symbol(0x004A, "CREATE"),
        new Symbol(0x005F, "EXISTS"),
        new Symbol(0x003D, "HAVING"),
        new Symbol(0x004E, "INSERT"),
        new Symbol(0x0057, "SERVICE"),
        new Symbol(0x0053, "DEFAULT"),
        new Symbol(0x0033, "REDUCED"),
        new Symbol(0x0032, "DISTINCT"),
        new Symbol(0x0037, "DESCRIBE"),
        new Symbol(0x0056, "OPTIONAL"),
        new Symbol(0x0060, "SEPARATOR"),
        new Symbol(0x0035, "CONSTRUCT"),
        new Symbol(0x0131, "CLJ_XOWL_QUERY"),
        new Symbol(0x0132, "CLJ_XOWL_INSERT") };
    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public xRDFLexer(String input) {
        super(commonAutomaton, terminals, 0x0006, input);
    }
    /**
     * Initializes a new instance of the lexer
     *
     * @param input The lexer's input
     */
    public xRDFLexer(InputStreamReader input) {
        super(commonAutomaton, terminals, 0x0006, input);
    }
}
