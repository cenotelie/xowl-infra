/*
 * WARNING: this file has been generated by
 * Hime Parser Generator 2.0.5.0
 */

package org.xowl.infra.store.loaders;

import fr.cenotelie.hime.redist.SemanticAction;
import fr.cenotelie.hime.redist.SemanticBody;
import fr.cenotelie.hime.redist.Symbol;
import fr.cenotelie.hime.redist.parsers.InitializationException;
import fr.cenotelie.hime.redist.parsers.LRkAutomaton;
import fr.cenotelie.hime.redist.parsers.LRkParser;

import java.util.Map;

/**
 * Represents a parser
 */
public class xRDFParser extends LRkParser {
    /**
     * The automaton for this parser
     */
    private static final LRkAutomaton commonAutomaton = LRkAutomaton.find(xRDFParser.class, "xRDFParser.bin");
    /**
     * Contains the constant IDs for the variables and virtuals in this parser
     */
    public static class ID {
        /**
         * The unique identifier for variable unit
         */
        public static final int unit = 0x0061;
        /**
         * The unique identifier for variable query
         */
        public static final int query = 0x0062;
        /**
         * The unique identifier for variable update
         */
        public static final int update = 0x0063;
        /**
         * The unique identifier for variable update1
         */
        public static final int update1 = 0x0064;
        /**
         * The unique identifier for variable prologue
         */
        public static final int prologue = 0x0065;
        /**
         * The unique identifier for variable decl_base
         */
        public static final int decl_base = 0x0066;
        /**
         * The unique identifier for variable decl_prefix
         */
        public static final int decl_prefix = 0x0067;
        /**
         * The unique identifier for variable select
         */
        public static final int select = 0x0068;
        /**
         * The unique identifier for variable sub_select
         */
        public static final int sub_select = 0x0069;
        /**
         * The unique identifier for variable clause_select
         */
        public static final int clause_select = 0x006A;
        /**
         * The unique identifier for variable clause_select_mod
         */
        public static final int clause_select_mod = 0x006B;
        /**
         * The unique identifier for variable clause_select_vars
         */
        public static final int clause_select_vars = 0x006C;
        /**
         * The unique identifier for variable clause_select_var
         */
        public static final int clause_select_var = 0x006D;
        /**
         * The unique identifier for variable construct
         */
        public static final int construct = 0x006E;
        /**
         * The unique identifier for variable construct1
         */
        public static final int construct1 = 0x006F;
        /**
         * The unique identifier for variable construct2
         */
        public static final int construct2 = 0x0070;
        /**
         * The unique identifier for variable construct_template
         */
        public static final int construct_template = 0x0071;
        /**
         * The unique identifier for variable construct_triples
         */
        public static final int construct_triples = 0x0072;
        /**
         * The unique identifier for variable describe
         */
        public static final int describe = 0x0073;
        /**
         * The unique identifier for variable describe_vars
         */
        public static final int describe_vars = 0x0074;
        /**
         * The unique identifier for variable ask
         */
        public static final int ask = 0x0075;
        /**
         * The unique identifier for variable clause_dataset
         */
        public static final int clause_dataset = 0x0076;
        /**
         * The unique identifier for variable clause_graph_default
         */
        public static final int clause_graph_default = 0x0077;
        /**
         * The unique identifier for variable clause_graph_named
         */
        public static final int clause_graph_named = 0x0078;
        /**
         * The unique identifier for variable source_selector
         */
        public static final int source_selector = 0x0079;
        /**
         * The unique identifier for variable clause_where
         */
        public static final int clause_where = 0x007A;
        /**
         * The unique identifier for variable modifier
         */
        public static final int modifier = 0x007B;
        /**
         * The unique identifier for variable clause_group
         */
        public static final int clause_group = 0x007C;
        /**
         * The unique identifier for variable clause_group_cond
         */
        public static final int clause_group_cond = 0x007D;
        /**
         * The unique identifier for variable clause_having
         */
        public static final int clause_having = 0x007E;
        /**
         * The unique identifier for variable clause_having_cond
         */
        public static final int clause_having_cond = 0x007F;
        /**
         * The unique identifier for variable clause_order
         */
        public static final int clause_order = 0x0080;
        /**
         * The unique identifier for variable clause_order_cond
         */
        public static final int clause_order_cond = 0x0081;
        /**
         * The unique identifier for variable clauses_limit_offset
         */
        public static final int clauses_limit_offset = 0x0082;
        /**
         * The unique identifier for variable clause_limit
         */
        public static final int clause_limit = 0x0083;
        /**
         * The unique identifier for variable clause_offset
         */
        public static final int clause_offset = 0x0084;
        /**
         * The unique identifier for variable clause_values
         */
        public static final int clause_values = 0x0085;
        /**
         * The unique identifier for variable load
         */
        public static final int load = 0x0086;
        /**
         * The unique identifier for variable clear
         */
        public static final int clear = 0x0087;
        /**
         * The unique identifier for variable drop
         */
        public static final int drop = 0x0088;
        /**
         * The unique identifier for variable create
         */
        public static final int create = 0x0089;
        /**
         * The unique identifier for variable add
         */
        public static final int add = 0x008A;
        /**
         * The unique identifier for variable move
         */
        public static final int move = 0x008B;
        /**
         * The unique identifier for variable copy
         */
        public static final int copy = 0x008C;
        /**
         * The unique identifier for variable insert
         */
        public static final int insert = 0x008D;
        /**
         * The unique identifier for variable delete
         */
        public static final int delete = 0x008E;
        /**
         * The unique identifier for variable deleteWhere
         */
        public static final int deleteWhere = 0x008F;
        /**
         * The unique identifier for variable modify
         */
        public static final int modify = 0x0090;
        /**
         * The unique identifier for variable clause_delete
         */
        public static final int clause_delete = 0x0091;
        /**
         * The unique identifier for variable clause_insert
         */
        public static final int clause_insert = 0x0092;
        /**
         * The unique identifier for variable clause_using
         */
        public static final int clause_using = 0x0093;
        /**
         * The unique identifier for variable graph_or_default
         */
        public static final int graph_or_default = 0x0094;
        /**
         * The unique identifier for variable graph_ref
         */
        public static final int graph_ref = 0x0095;
        /**
         * The unique identifier for variable graph_ref_all
         */
        public static final int graph_ref_all = 0x0096;
        /**
         * The unique identifier for variable graph_pattern
         */
        public static final int graph_pattern = 0x0097;
        /**
         * The unique identifier for variable graph_pattern_group
         */
        public static final int graph_pattern_group = 0x0098;
        /**
         * The unique identifier for variable graph_pattern_other
         */
        public static final int graph_pattern_other = 0x0099;
        /**
         * The unique identifier for variable graph_pattern_optional
         */
        public static final int graph_pattern_optional = 0x009A;
        /**
         * The unique identifier for variable graph_pattern_minus
         */
        public static final int graph_pattern_minus = 0x009B;
        /**
         * The unique identifier for variable graph_pattern_graph
         */
        public static final int graph_pattern_graph = 0x009C;
        /**
         * The unique identifier for variable graph_pattern_service
         */
        public static final int graph_pattern_service = 0x009D;
        /**
         * The unique identifier for variable graph_pattern_filter
         */
        public static final int graph_pattern_filter = 0x009E;
        /**
         * The unique identifier for variable graph_pattern_bind
         */
        public static final int graph_pattern_bind = 0x009F;
        /**
         * The unique identifier for variable graph_pattern_data
         */
        public static final int graph_pattern_data = 0x00A0;
        /**
         * The unique identifier for variable graph_pattern_union
         */
        public static final int graph_pattern_union = 0x00A1;
        /**
         * The unique identifier for variable data_block
         */
        public static final int data_block = 0x00A2;
        /**
         * The unique identifier for variable inline_data_one
         */
        public static final int inline_data_one = 0x00A3;
        /**
         * The unique identifier for variable inline_data_full
         */
        public static final int inline_data_full = 0x00A4;
        /**
         * The unique identifier for variable inline_data_full_vars
         */
        public static final int inline_data_full_vars = 0x00A5;
        /**
         * The unique identifier for variable inline_data_full_val
         */
        public static final int inline_data_full_val = 0x00A6;
        /**
         * The unique identifier for variable data_block_value
         */
        public static final int data_block_value = 0x00A7;
        /**
         * The unique identifier for variable constraint
         */
        public static final int constraint = 0x00A8;
        /**
         * The unique identifier for variable quad_pattern
         */
        public static final int quad_pattern = 0x00A9;
        /**
         * The unique identifier for variable quad_data
         */
        public static final int quad_data = 0x00AA;
        /**
         * The unique identifier for variable quads
         */
        public static final int quads = 0x00AB;
        /**
         * The unique identifier for variable quads_supp
         */
        public static final int quads_supp = 0x00AC;
        /**
         * The unique identifier for variable quads_not_triples
         */
        public static final int quads_not_triples = 0x00AD;
        /**
         * The unique identifier for variable triples_template
         */
        public static final int triples_template = 0x00AE;
        /**
         * The unique identifier for variable triples_block
         */
        public static final int triples_block = 0x00AF;
        /**
         * The unique identifier for variable triples_same_subj
         */
        public static final int triples_same_subj = 0x00B0;
        /**
         * The unique identifier for variable property_list
         */
        public static final int property_list = 0x00B1;
        /**
         * The unique identifier for variable property_list_not_empty
         */
        public static final int property_list_not_empty = 0x00B2;
        /**
         * The unique identifier for variable verb
         */
        public static final int verb = 0x00B3;
        /**
         * The unique identifier for variable object_list
         */
        public static final int object_list = 0x00B4;
        /**
         * The unique identifier for variable object
         */
        public static final int object = 0x00B5;
        /**
         * The unique identifier for variable triples_node
         */
        public static final int triples_node = 0x00B6;
        /**
         * The unique identifier for variable blank_node_property_list
         */
        public static final int blank_node_property_list = 0x00B7;
        /**
         * The unique identifier for variable collection
         */
        public static final int collection = 0x00B8;
        /**
         * The unique identifier for variable graph_node
         */
        public static final int graph_node = 0x00B9;
        /**
         * The unique identifier for variable var_or_term
         */
        public static final int var_or_term = 0x00BA;
        /**
         * The unique identifier for variable var_or_iri
         */
        public static final int var_or_iri = 0x00BB;
        /**
         * The unique identifier for variable graph_term
         */
        public static final int graph_term = 0x00BC;
        /**
         * The unique identifier for variable triples_same_subj_path
         */
        public static final int triples_same_subj_path = 0x00BD;
        /**
         * The unique identifier for variable property_list_path
         */
        public static final int property_list_path = 0x00BE;
        /**
         * The unique identifier for variable property_list_path_ne
         */
        public static final int property_list_path_ne = 0x00BF;
        /**
         * The unique identifier for variable verb_path
         */
        public static final int verb_path = 0x00C0;
        /**
         * The unique identifier for variable verb_simple
         */
        public static final int verb_simple = 0x00C1;
        /**
         * The unique identifier for variable object_list_path
         */
        public static final int object_list_path = 0x00C2;
        /**
         * The unique identifier for variable object_path
         */
        public static final int object_path = 0x00C3;
        /**
         * The unique identifier for variable triples_node_path
         */
        public static final int triples_node_path = 0x00C4;
        /**
         * The unique identifier for variable blank_node_property_list_path
         */
        public static final int blank_node_property_list_path = 0x00C5;
        /**
         * The unique identifier for variable collection_path
         */
        public static final int collection_path = 0x00C6;
        /**
         * The unique identifier for variable graph_node_path
         */
        public static final int graph_node_path = 0x00C7;
        /**
         * The unique identifier for variable path
         */
        public static final int path = 0x00C8;
        /**
         * The unique identifier for variable path_alt
         */
        public static final int path_alt = 0x00C9;
        /**
         * The unique identifier for variable path_seq
         */
        public static final int path_seq = 0x00CA;
        /**
         * The unique identifier for variable path_elt_or_inv
         */
        public static final int path_elt_or_inv = 0x00CB;
        /**
         * The unique identifier for variable path_elt
         */
        public static final int path_elt = 0x00CC;
        /**
         * The unique identifier for variable path_primary
         */
        public static final int path_primary = 0x00CD;
        /**
         * The unique identifier for variable path_neg
         */
        public static final int path_neg = 0x00CE;
        /**
         * The unique identifier for variable path_in
         */
        public static final int path_in = 0x00CF;
        /**
         * The unique identifier for variable expression_list
         */
        public static final int expression_list = 0x00D0;
        /**
         * The unique identifier for variable expression
         */
        public static final int expression = 0x00D1;
        /**
         * The unique identifier for variable exp_or
         */
        public static final int exp_or = 0x00D2;
        /**
         * The unique identifier for variable exp_and
         */
        public static final int exp_and = 0x00D3;
        /**
         * The unique identifier for variable exp_logical
         */
        public static final int exp_logical = 0x00D4;
        /**
         * The unique identifier for variable exp_relational
         */
        public static final int exp_relational = 0x00D5;
        /**
         * The unique identifier for variable exp_numeric
         */
        public static final int exp_numeric = 0x00D6;
        /**
         * The unique identifier for variable exp_add
         */
        public static final int exp_add = 0x00D7;
        /**
         * The unique identifier for variable exp_mult
         */
        public static final int exp_mult = 0x00D8;
        /**
         * The unique identifier for variable exp_unary
         */
        public static final int exp_unary = 0x00D9;
        /**
         * The unique identifier for variable exp_primary
         */
        public static final int exp_primary = 0x00DA;
        /**
         * The unique identifier for variable exp_bracketted
         */
        public static final int exp_bracketted = 0x00DB;
        /**
         * The unique identifier for variable built_in_call
         */
        public static final int built_in_call = 0x00DC;
        /**
         * The unique identifier for variable built_in_call_distinct
         */
        public static final int built_in_call_distinct = 0x00DD;
        /**
         * The unique identifier for variable built_in_call_args
         */
        public static final int built_in_call_args = 0x00DE;
        /**
         * The unique identifier for variable built_in_call_sep
         */
        public static final int built_in_call_sep = 0x00DF;
        /**
         * The unique identifier for variable iri_or_function
         */
        public static final int iri_or_function = 0x00E0;
        /**
         * The unique identifier for variable function_call
         */
        public static final int function_call = 0x00E1;
        /**
         * The unique identifier for variable arg_list
         */
        public static final int arg_list = 0x00E2;
        /**
         * The unique identifier for variable blank_node
         */
        public static final int blank_node = 0x00E3;
        /**
         * The unique identifier for variable literal
         */
        public static final int literal = 0x00E4;
        /**
         * The unique identifier for variable literal_bool
         */
        public static final int literal_bool = 0x00E5;
        /**
         * The unique identifier for variable literal_numeric
         */
        public static final int literal_numeric = 0x00E6;
        /**
         * The unique identifier for variable literal_rdf
         */
        public static final int literal_rdf = 0x00E7;
        /**
         * The unique identifier for variable string
         */
        public static final int string = 0x00E8;
        /**
         * The unique identifier for variable iri
         */
        public static final int iri = 0x00E9;
        /**
         * The unique identifier for variable prefixedName
         */
        public static final int prefixedName = 0x00EA;
        /**
         * The unique identifier for variable document
         */
        public static final int document = 0x0136;
        /**
         * The unique identifier for variable xowl_statements
         */
        public static final int xowl_statements = 0x0137;
        /**
         * The unique identifier for variable xowl_statement
         */
        public static final int xowl_statement = 0x0138;
        /**
         * The unique identifier for variable xowl_data
         */
        public static final int xowl_data = 0x0139;
        /**
         * The unique identifier for variable xowl_graph_anon
         */
        public static final int xowl_graph_anon = 0x013A;
        /**
         * The unique identifier for variable xowl_graph_named
         */
        public static final int xowl_graph_named = 0x013B;
        /**
         * The unique identifier for variable xowl_triples
         */
        public static final int xowl_triples = 0x013C;
        /**
         * The unique identifier for variable xowl_predicate_object_list
         */
        public static final int xowl_predicate_object_list = 0x013D;
        /**
         * The unique identifier for variable xowl_object_list
         */
        public static final int xowl_object_list = 0x013E;
        /**
         * The unique identifier for variable xowl_verb
         */
        public static final int xowl_verb = 0x013F;
        /**
         * The unique identifier for variable xowl_subject
         */
        public static final int xowl_subject = 0x0140;
        /**
         * The unique identifier for variable xowl_predicate
         */
        public static final int xowl_predicate = 0x0141;
        /**
         * The unique identifier for variable xowl_object
         */
        public static final int xowl_object = 0x0142;
        /**
         * The unique identifier for variable xowl_graph_node
         */
        public static final int xowl_graph_node = 0x0143;
        /**
         * The unique identifier for variable xowl_blank_property_list
         */
        public static final int xowl_blank_property_list = 0x0144;
        /**
         * The unique identifier for variable xowl_collection
         */
        public static final int xowl_collection = 0x0145;
        /**
         * The unique identifier for variable xowl_dynamic
         */
        public static final int xowl_dynamic = 0x0146;
        /**
         * The unique identifier for variable xowl_rule
         */
        public static final int xowl_rule = 0x0147;
        /**
         * The unique identifier for variable xowl_rule_simple
         */
        public static final int xowl_rule_simple = 0x0148;
        /**
         * The unique identifier for variable xowl_rule_sparql
         */
        public static final int xowl_rule_sparql = 0x0149;
        /**
         * The unique identifier for variable xowl_rule_mod
         */
        public static final int xowl_rule_mod = 0x014A;
        /**
         * The unique identifier for variable xowl_rule_parts
         */
        public static final int xowl_rule_parts = 0x014B;
        /**
         * The unique identifier for variable xowl_rule_part
         */
        public static final int xowl_rule_part = 0x014C;
        /**
         * The unique identifier for variable xowl_rule_guard
         */
        public static final int xowl_rule_guard = 0x014D;
        /**
         * The unique identifier for variable clj_atom
         */
        public static final int clj_atom = 0x014E;
        /**
         * The unique identifier for variable clj_list
         */
        public static final int clj_list = 0x014F;
        /**
         * The unique identifier for variable clj_vector
         */
        public static final int clj_vector = 0x0150;
        /**
         * The unique identifier for variable clj_map
         */
        public static final int clj_map = 0x0151;
        /**
         * The unique identifier for variable clj_couple
         */
        public static final int clj_couple = 0x0152;
        /**
         * The unique identifier for variable clj_set
         */
        public static final int clj_set = 0x0153;
        /**
         * The unique identifier for variable clj_constructor
         */
        public static final int clj_constructor = 0x0154;
        /**
         * The unique identifier for variable clj_quote
         */
        public static final int clj_quote = 0x0155;
        /**
         * The unique identifier for variable clj_deref
         */
        public static final int clj_deref = 0x0156;
        /**
         * The unique identifier for variable clj_metadata
         */
        public static final int clj_metadata = 0x0157;
        /**
         * The unique identifier for variable clj_regexp
         */
        public static final int clj_regexp = 0x0158;
        /**
         * The unique identifier for variable clj_var_quote
         */
        public static final int clj_var_quote = 0x0159;
        /**
         * The unique identifier for variable clj_anon_function
         */
        public static final int clj_anon_function = 0x015A;
        /**
         * The unique identifier for variable clj_ignore
         */
        public static final int clj_ignore = 0x015B;
        /**
         * The unique identifier for variable clj_syntax_quote
         */
        public static final int clj_syntax_quote = 0x015C;
        /**
         * The unique identifier for variable clj_unquote
         */
        public static final int clj_unquote = 0x015D;
        /**
         * The unique identifier for variable clj_unquote_splicing
         */
        public static final int clj_unquote_splicing = 0x015E;
        /**
         * The unique identifier for variable clj_conditional
         */
        public static final int clj_conditional = 0x015F;
        /**
         * The unique identifier for variable clj_form
         */
        public static final int clj_form = 0x0160;
        /**
         * The unique identifier for variable __axiom
         */
        public static final int __axiom = 0x016F;
        /**
         * The unique identifier for virtual nil
         */
        public static final int nil = 0x0109;
    }
    /**
     * The collection of variables matched by this parser
     *
     * The variables are in an order consistent with the automaton,
     * so that variable indices in the automaton can be used to retrieve the variables in this table
     */
    private static final Symbol[] variables = {
        new Symbol(0x0061, "unit"), 
        new Symbol(0x0062, "query"), 
        new Symbol(0x0063, "update"), 
        new Symbol(0x0064, "update1"), 
        new Symbol(0x0065, "prologue"), 
        new Symbol(0x0066, "decl_base"), 
        new Symbol(0x0067, "decl_prefix"), 
        new Symbol(0x0068, "select"), 
        new Symbol(0x0069, "sub_select"), 
        new Symbol(0x006A, "clause_select"), 
        new Symbol(0x006B, "clause_select_mod"), 
        new Symbol(0x006C, "clause_select_vars"), 
        new Symbol(0x006D, "clause_select_var"), 
        new Symbol(0x006E, "construct"), 
        new Symbol(0x006F, "construct1"), 
        new Symbol(0x0070, "construct2"), 
        new Symbol(0x0071, "construct_template"), 
        new Symbol(0x0072, "construct_triples"), 
        new Symbol(0x0073, "describe"), 
        new Symbol(0x0074, "describe_vars"), 
        new Symbol(0x0075, "ask"), 
        new Symbol(0x0076, "clause_dataset"), 
        new Symbol(0x0077, "clause_graph_default"), 
        new Symbol(0x0078, "clause_graph_named"), 
        new Symbol(0x0079, "source_selector"), 
        new Symbol(0x007A, "clause_where"), 
        new Symbol(0x007B, "modifier"), 
        new Symbol(0x007C, "clause_group"), 
        new Symbol(0x007D, "clause_group_cond"), 
        new Symbol(0x007E, "clause_having"), 
        new Symbol(0x007F, "clause_having_cond"), 
        new Symbol(0x0080, "clause_order"), 
        new Symbol(0x0081, "clause_order_cond"), 
        new Symbol(0x0082, "clauses_limit_offset"), 
        new Symbol(0x0083, "clause_limit"), 
        new Symbol(0x0084, "clause_offset"), 
        new Symbol(0x0085, "clause_values"), 
        new Symbol(0x0086, "load"), 
        new Symbol(0x0087, "clear"), 
        new Symbol(0x0088, "drop"), 
        new Symbol(0x0089, "create"), 
        new Symbol(0x008A, "add"), 
        new Symbol(0x008B, "move"), 
        new Symbol(0x008C, "copy"), 
        new Symbol(0x008D, "insert"), 
        new Symbol(0x008E, "delete"), 
        new Symbol(0x008F, "deleteWhere"), 
        new Symbol(0x0090, "modify"), 
        new Symbol(0x0091, "clause_delete"), 
        new Symbol(0x0092, "clause_insert"), 
        new Symbol(0x0093, "clause_using"), 
        new Symbol(0x0094, "graph_or_default"), 
        new Symbol(0x0095, "graph_ref"), 
        new Symbol(0x0096, "graph_ref_all"), 
        new Symbol(0x0097, "graph_pattern"), 
        new Symbol(0x0098, "graph_pattern_group"), 
        new Symbol(0x0099, "graph_pattern_other"), 
        new Symbol(0x009A, "graph_pattern_optional"), 
        new Symbol(0x009B, "graph_pattern_minus"), 
        new Symbol(0x009C, "graph_pattern_graph"), 
        new Symbol(0x009D, "graph_pattern_service"), 
        new Symbol(0x009E, "graph_pattern_filter"), 
        new Symbol(0x009F, "graph_pattern_bind"), 
        new Symbol(0x00A0, "graph_pattern_data"), 
        new Symbol(0x00A1, "graph_pattern_union"), 
        new Symbol(0x00A2, "data_block"), 
        new Symbol(0x00A3, "inline_data_one"), 
        new Symbol(0x00A4, "inline_data_full"), 
        new Symbol(0x00A5, "inline_data_full_vars"), 
        new Symbol(0x00A6, "inline_data_full_val"), 
        new Symbol(0x00A7, "data_block_value"), 
        new Symbol(0x00A8, "constraint"), 
        new Symbol(0x00A9, "quad_pattern"), 
        new Symbol(0x00AA, "quad_data"), 
        new Symbol(0x00AB, "quads"), 
        new Symbol(0x00AC, "quads_supp"), 
        new Symbol(0x00AD, "quads_not_triples"), 
        new Symbol(0x00AE, "triples_template"), 
        new Symbol(0x00AF, "triples_block"), 
        new Symbol(0x00B0, "triples_same_subj"), 
        new Symbol(0x00B1, "property_list"), 
        new Symbol(0x00B2, "property_list_not_empty"), 
        new Symbol(0x00B3, "verb"), 
        new Symbol(0x00B4, "object_list"), 
        new Symbol(0x00B5, "object"), 
        new Symbol(0x00B6, "triples_node"), 
        new Symbol(0x00B7, "blank_node_property_list"), 
        new Symbol(0x00B8, "collection"), 
        new Symbol(0x00B9, "graph_node"), 
        new Symbol(0x00BA, "var_or_term"), 
        new Symbol(0x00BB, "var_or_iri"), 
        new Symbol(0x00BC, "graph_term"), 
        new Symbol(0x00BD, "triples_same_subj_path"), 
        new Symbol(0x00BE, "property_list_path"), 
        new Symbol(0x00BF, "property_list_path_ne"), 
        new Symbol(0x00C0, "verb_path"), 
        new Symbol(0x00C1, "verb_simple"), 
        new Symbol(0x00C2, "object_list_path"), 
        new Symbol(0x00C3, "object_path"), 
        new Symbol(0x00C4, "triples_node_path"), 
        new Symbol(0x00C5, "blank_node_property_list_path"), 
        new Symbol(0x00C6, "collection_path"), 
        new Symbol(0x00C7, "graph_node_path"), 
        new Symbol(0x00C8, "path"), 
        new Symbol(0x00C9, "path_alt"), 
        new Symbol(0x00CA, "path_seq"), 
        new Symbol(0x00CB, "path_elt_or_inv"), 
        new Symbol(0x00CC, "path_elt"), 
        new Symbol(0x00CD, "path_primary"), 
        new Symbol(0x00CE, "path_neg"), 
        new Symbol(0x00CF, "path_in"), 
        new Symbol(0x00D0, "expression_list"), 
        new Symbol(0x00D1, "expression"), 
        new Symbol(0x00D2, "exp_or"), 
        new Symbol(0x00D3, "exp_and"), 
        new Symbol(0x00D4, "exp_logical"), 
        new Symbol(0x00D5, "exp_relational"), 
        new Symbol(0x00D6, "exp_numeric"), 
        new Symbol(0x00D7, "exp_add"), 
        new Symbol(0x00D8, "exp_mult"), 
        new Symbol(0x00D9, "exp_unary"), 
        new Symbol(0x00DA, "exp_primary"), 
        new Symbol(0x00DB, "exp_bracketted"), 
        new Symbol(0x00DC, "built_in_call"), 
        new Symbol(0x00DD, "built_in_call_distinct"), 
        new Symbol(0x00DE, "built_in_call_args"), 
        new Symbol(0x00DF, "built_in_call_sep"), 
        new Symbol(0x00E0, "iri_or_function"), 
        new Symbol(0x00E1, "function_call"), 
        new Symbol(0x00E2, "arg_list"), 
        new Symbol(0x00E3, "blank_node"), 
        new Symbol(0x00E4, "literal"), 
        new Symbol(0x00E5, "literal_bool"), 
        new Symbol(0x00E6, "literal_numeric"), 
        new Symbol(0x00E7, "literal_rdf"), 
        new Symbol(0x00E8, "string"), 
        new Symbol(0x00E9, "iri"), 
        new Symbol(0x00EA, "prefixedName"), 
        new Symbol(0x00EC, "__V236"), 
        new Symbol(0x00ED, "__V237"), 
        new Symbol(0x00EE, "__V238"), 
        new Symbol(0x00F1, "__V241"), 
        new Symbol(0x00F2, "__V242"), 
        new Symbol(0x00F6, "__V246"), 
        new Symbol(0x00F7, "__V247"), 
        new Symbol(0x00F8, "__V248"), 
        new Symbol(0x00F9, "__V249"), 
        new Symbol(0x00FA, "__V250"), 
        new Symbol(0x00FB, "__V251"), 
        new Symbol(0x00FC, "__V252"), 
        new Symbol(0x00FD, "__V253"), 
        new Symbol(0x00FE, "__V254"), 
        new Symbol(0x00FF, "__V255"), 
        new Symbol(0x0100, "__V256"), 
        new Symbol(0x0101, "__V257"), 
        new Symbol(0x0102, "__V258"), 
        new Symbol(0x0103, "__V259"), 
        new Symbol(0x0105, "__V261"), 
        new Symbol(0x0108, "__V264"), 
        new Symbol(0x010A, "__V266"), 
        new Symbol(0x010B, "__V267"), 
        new Symbol(0x010C, "__V268"), 
        new Symbol(0x010D, "__V269"), 
        new Symbol(0x010E, "__V270"), 
        new Symbol(0x010F, "__V271"), 
        new Symbol(0x0111, "__V273"), 
        new Symbol(0x0136, "document"), 
        new Symbol(0x0137, "xowl_statements"), 
        new Symbol(0x0138, "xowl_statement"), 
        new Symbol(0x0139, "xowl_data"), 
        new Symbol(0x013A, "xowl_graph_anon"), 
        new Symbol(0x013B, "xowl_graph_named"), 
        new Symbol(0x013C, "xowl_triples"), 
        new Symbol(0x013D, "xowl_predicate_object_list"), 
        new Symbol(0x013E, "xowl_object_list"), 
        new Symbol(0x013F, "xowl_verb"), 
        new Symbol(0x0140, "xowl_subject"), 
        new Symbol(0x0141, "xowl_predicate"), 
        new Symbol(0x0142, "xowl_object"), 
        new Symbol(0x0143, "xowl_graph_node"), 
        new Symbol(0x0144, "xowl_blank_property_list"), 
        new Symbol(0x0145, "xowl_collection"), 
        new Symbol(0x0146, "xowl_dynamic"), 
        new Symbol(0x0147, "xowl_rule"), 
        new Symbol(0x0148, "xowl_rule_simple"), 
        new Symbol(0x0149, "xowl_rule_sparql"), 
        new Symbol(0x014A, "xowl_rule_mod"), 
        new Symbol(0x014B, "xowl_rule_parts"), 
        new Symbol(0x014C, "xowl_rule_part"), 
        new Symbol(0x014D, "xowl_rule_guard"), 
        new Symbol(0x014E, "clj_atom"), 
        new Symbol(0x014F, "clj_list"), 
        new Symbol(0x0150, "clj_vector"), 
        new Symbol(0x0151, "clj_map"), 
        new Symbol(0x0152, "clj_couple"), 
        new Symbol(0x0153, "clj_set"), 
        new Symbol(0x0154, "clj_constructor"), 
        new Symbol(0x0155, "clj_quote"), 
        new Symbol(0x0156, "clj_deref"), 
        new Symbol(0x0157, "clj_metadata"), 
        new Symbol(0x0158, "clj_regexp"), 
        new Symbol(0x0159, "clj_var_quote"), 
        new Symbol(0x015A, "clj_anon_function"), 
        new Symbol(0x015B, "clj_ignore"), 
        new Symbol(0x015C, "clj_syntax_quote"), 
        new Symbol(0x015D, "clj_unquote"), 
        new Symbol(0x015E, "clj_unquote_splicing"), 
        new Symbol(0x015F, "clj_conditional"), 
        new Symbol(0x0160, "clj_form"), 
        new Symbol(0x0161, "__V353"), 
        new Symbol(0x0162, "__V354"), 
        new Symbol(0x0163, "__V355"), 
        new Symbol(0x0164, "__V356"), 
        new Symbol(0x0165, "__V357"), 
        new Symbol(0x0166, "__V358"), 
        new Symbol(0x0167, "__V359"), 
        new Symbol(0x0169, "__V361"), 
        new Symbol(0x016A, "__V362"), 
        new Symbol(0x016B, "__V363"), 
        new Symbol(0x016C, "__V364"), 
        new Symbol(0x016D, "__V365"), 
        new Symbol(0x016E, "__V366"), 
        new Symbol(0x016F, "__axiom") };
    /**
     * The collection of virtuals matched by this parser
     *
     * The virtuals are in an order consistent with the automaton,
     * so that virtual indices in the automaton can be used to retrieve the virtuals in this table
     */
    private static final Symbol[] virtuals = {
        new Symbol(0x0109, "nil") };
    /**
     * Initializes a new instance of the parser
     *
     * @param lexer The input lexer
     */
    public xRDFParser(xRDFLexer lexer) {
        super(commonAutomaton, variables, virtuals, null, lexer);
    }
}
