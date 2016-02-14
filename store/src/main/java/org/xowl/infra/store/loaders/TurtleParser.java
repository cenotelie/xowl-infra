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
class TurtleParser extends LRkParser {
    /**
     * The automaton for this parser
     */
    private static final LRkAutomaton commonAutomaton = LRkAutomaton.find(TurtleParser.class, "TurtleParser.bin");
    /**
     * Contains the constant IDs for the variables and virtuals in this parser
     */
    public static class ID {
        /**
         * The unique identifier for variable turtleDoc
         */
        public static final int turtleDoc = 0x001C;
        /**
         * The unique identifier for variable statement
         */
        public static final int statement = 0x001D;
        /**
         * The unique identifier for variable directive
         */
        public static final int directive = 0x001E;
        /**
         * The unique identifier for variable prefixID
         */
        public static final int prefixID = 0x001F;
        /**
         * The unique identifier for variable base
         */
        public static final int base = 0x0020;
        /**
         * The unique identifier for variable sparqlBase
         */
        public static final int sparqlBase = 0x0021;
        /**
         * The unique identifier for variable sparqlPrefix
         */
        public static final int sparqlPrefix = 0x0022;
        /**
         * The unique identifier for variable triples
         */
        public static final int triples = 0x0023;
        /**
         * The unique identifier for variable predicateObjectList
         */
        public static final int predicateObjectList = 0x0024;
        /**
         * The unique identifier for variable objectList
         */
        public static final int objectList = 0x0025;
        /**
         * The unique identifier for variable verb
         */
        public static final int verb = 0x0026;
        /**
         * The unique identifier for variable subject
         */
        public static final int subject = 0x0027;
        /**
         * The unique identifier for variable predicate
         */
        public static final int predicate = 0x0028;
        /**
         * The unique identifier for variable object
         */
        public static final int object = 0x0029;
        /**
         * The unique identifier for variable blankNodePropertyList
         */
        public static final int blankNodePropertyList = 0x002A;
        /**
         * The unique identifier for variable collection
         */
        public static final int collection = 0x002B;
        /**
         * The unique identifier for variable blankNode
         */
        public static final int blankNode = 0x002C;
        /**
         * The unique identifier for variable literal
         */
        public static final int literal = 0x002D;
        /**
         * The unique identifier for variable booleanLiteral
         */
        public static final int booleanLiteral = 0x002E;
        /**
         * The unique identifier for variable numericLiteral
         */
        public static final int numericLiteral = 0x002F;
        /**
         * The unique identifier for variable rdfLiteral
         */
        public static final int rdfLiteral = 0x0030;
        /**
         * The unique identifier for variable string
         */
        public static final int string = 0x0031;
        /**
         * The unique identifier for variable iri
         */
        public static final int iri = 0x0032;
        /**
         * The unique identifier for variable prefixedName
         */
        public static final int prefixedName = 0x0033;
        /**
         * The unique identifier for variable __axiom
         */
        public static final int __axiom = 0x0045;
    }
    /**
     * The collection of variables matched by this parser
     *
     * The variables are in an order consistent with the automaton,
     * so that variable indices in the automaton can be used to retrieve the variables in this table
     */
    private static final Symbol[] variables = {
        new Symbol(0x001C, "turtleDoc"), 
        new Symbol(0x001D, "statement"), 
        new Symbol(0x001E, "directive"), 
        new Symbol(0x001F, "prefixID"), 
        new Symbol(0x0020, "base"), 
        new Symbol(0x0021, "sparqlBase"), 
        new Symbol(0x0022, "sparqlPrefix"), 
        new Symbol(0x0023, "triples"), 
        new Symbol(0x0024, "predicateObjectList"), 
        new Symbol(0x0025, "objectList"), 
        new Symbol(0x0026, "verb"), 
        new Symbol(0x0027, "subject"), 
        new Symbol(0x0028, "predicate"), 
        new Symbol(0x0029, "object"), 
        new Symbol(0x002A, "blankNodePropertyList"), 
        new Symbol(0x002B, "collection"), 
        new Symbol(0x002C, "blankNode"), 
        new Symbol(0x002D, "literal"), 
        new Symbol(0x002E, "booleanLiteral"), 
        new Symbol(0x002F, "numericLiteral"), 
        new Symbol(0x0030, "rdfLiteral"), 
        new Symbol(0x0031, "string"), 
        new Symbol(0x0032, "iri"), 
        new Symbol(0x0033, "prefixedName"), 
        new Symbol(0x0034, "__V52"), 
        new Symbol(0x0039, "__V57"), 
        new Symbol(0x003B, "__V59"), 
        new Symbol(0x0040, "__V64"), 
        new Symbol(0x0045, "__axiom") };
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
    public TurtleParser(TurtleLexer lexer) {
        super(commonAutomaton, variables, virtuals, null, lexer);
    }
}