/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.store.loaders;

import org.junit.Test;

/**
 * All TriG loading tests
 *
 * @author Laurent Wouters
 */
public class TriGTest extends BaseTriGTest {
    @Test
    public void test_anonymous_blank_node_graph() {
        testTrigEval("anonymous_blank_node_graph.trig", "labeled_blank_node_graph.nq");
    }

    @Test
    public void test_labeled_blank_node_graph() {
        testTrigEval("labeled_blank_node_graph.trig", "labeled_blank_node_graph.nq");
    }

    @Test
    public void test_alternating_iri_graphs() {
        testTrigEval("alternating_iri_graphs.trig", "alternating_iri_graphs.nq");
    }

    @Test
    public void test_alternating_bnode_graphs() {
        testTrigEval("alternating_bnode_graphs.trig", "alternating_bnode_graphs.nq");
    }

    @Test
    public void test_trig_syntax_bad_base_04() {
        testTrigNegativeSyntax("trig-syntax-bad-base-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_base_05() {
        testTrigNegativeSyntax("trig-syntax-bad-base-05.trig");
    }

    @Test
    public void test_trig_syntax_bad_prefix_06() {
        testTrigNegativeSyntax("trig-syntax-bad-prefix-06.trig");
    }

    @Test
    public void test_trig_syntax_bad_prefix_07() {
        testTrigNegativeSyntax("trig-syntax-bad-prefix-07.trig");
    }

    @Test
    public void test_trig_syntax_struct_06() {
        testTrigPositiveSyntax("trig-syntax-struct-06.trig");
    }

    @Test
    public void test_trig_syntax_struct_07() {
        testTrigPositiveSyntax("trig-syntax-struct-07.trig");
    }

    @Test
    public void test_trig_syntax_minimal_whitespace_01() {
        testTrigPositiveSyntax("trig-syntax-minimal-whitespace-01.trig");
    }

    @Test
    public void test_IRI_subject() {
        testTrigEval("IRI_subject.trig", "IRI_spo.nq");
    }

    @Test
    public void test_IRI_with_four_digit_numeric_escape() {
        testTrigEval("IRI_with_four_digit_numeric_escape.trig", "IRI_spo.nq");
    }

    @Test
    public void test_IRI_with_eight_digit_numeric_escape() {
        testTrigEval("IRI_with_eight_digit_numeric_escape.trig", "IRI_spo.nq");
    }

    @Test
    public void test_IRI_with_all_punctuation() {
        testTrigEval("IRI_with_all_punctuation.trig", "IRI_with_all_punctuation.nq");
    }

    @Test
    public void test_bareword_a_predicate() {
        testTrigEval("bareword_a_predicate.trig", "bareword_a_predicate.nq");
    }

    @Test
    public void test_old_style_prefix() {
        testTrigEval("old_style_prefix.trig", "IRI_spo.nq");
    }

    @Test
    public void test_SPARQL_style_prefix() {
        testTrigEval("SPARQL_style_prefix.trig", "IRI_spo.nq");
    }

    @Test
    public void test_prefixed_IRI_predicate() {
        testTrigEval("prefixed_IRI_predicate.trig", "IRI_spo.nq");
    }

    @Test
    public void test_prefixed_IRI_object() {
        testTrigEval("prefixed_IRI_object.trig", "IRI_spo.nq");
    }

    @Test
    public void test_prefix_only_IRI() {
        testTrigEval("prefix_only_IRI.trig", "IRI_spo.nq");
    }

    @Test
    public void test_prefix_with_PN_CHARS_BASE_character_boundaries() {
        testTrigEval("prefix_with_PN_CHARS_BASE_character_boundaries.trig", "IRI_spo.nq");
    }

    @Test
    public void test_prefix_with_non_leading_extras() {
        testTrigEval("prefix_with_non_leading_extras.trig", "IRI_spo.nq");
    }

    @Test
    public void test_default_namespace_IRI() {
        testTrigEval("default_namespace_IRI.trig", "IRI_spo.nq");
    }

    @Test
    public void test_prefix_reassigned_and_used() {
        testTrigEval("prefix_reassigned_and_used.trig", "prefix_reassigned_and_used.nq");
    }

    @Test
    public void test_reserved_escaped_localName() {
        testTrigEval("reserved_escaped_localName.trig", "reserved_escaped_localName.nq");
    }

    @Test
    public void test_percent_escaped_localName() {
        testTrigEval("percent_escaped_localName.trig", "percent_escaped_localName.nq");
    }

    @Test
    public void test_HYPHEN_MINUS_in_localName() {
        testTrigEval("HYPHEN_MINUS_in_localName.trig", "HYPHEN_MINUS_in_localName.nq");
    }

    @Test
    public void test_underscore_in_localName() {
        testTrigEval("underscore_in_localName.trig", "underscore_in_localName.nq");
    }

    @Test
    public void test_localname_with_COLON() {
        testTrigEval("localname_with_COLON.trig", "localname_with_COLON.nq");
    }

    @Test
    public void test_localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries() {
        testTrigEval("localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries.trig", "localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries.nq");
    }

    @Test
    public void test_localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries() {
        testTrigEval("localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries.trig", "localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries.nq");
    }

    @Test
    public void test_localName_with_nfc_PN_CHARS_BASE_character_boundaries() {
        testTrigEval("localName_with_nfc_PN_CHARS_BASE_character_boundaries.trig", "localName_with_nfc_PN_CHARS_BASE_character_boundaries.nq");
    }

    @Test
    public void test_localName_with_leading_underscore() {
        testTrigEval("localName_with_leading_underscore.trig", "localName_with_leading_underscore.nq");
    }

    @Test
    public void test_localName_with_leading_digit() {
        testTrigEval("localName_with_leading_digit.trig", "localName_with_leading_digit.nq");
    }

    @Test
    public void test_localName_with_non_leading_extras() {
        testTrigEval("localName_with_non_leading_extras.trig", "localName_with_non_leading_extras.nq");
    }

    @Test
    public void test_old_style_base() {
        testTrigEval("old_style_base.trig", "IRI_spo.nq");
    }

    @Test
    public void test_SPARQL_style_base() {
        testTrigEval("SPARQL_style_base.trig", "IRI_spo.nq");
    }

    @Test
    public void test_labeled_blank_node_subject() {
        testTrigEval("labeled_blank_node_subject.trig", "labeled_blank_node_subject.nq");
    }

    @Test
    public void test_labeled_blank_node_object() {
        testTrigEval("labeled_blank_node_object.trig", "labeled_blank_node_object.nq");
    }

    @Test
    public void test_labeled_blank_node_with_PN_CHARS_BASE_character_boundaries() {
        testTrigEval("labeled_blank_node_with_PN_CHARS_BASE_character_boundaries.trig", "labeled_blank_node_object.nq");
    }

    @Test
    public void test_labeled_blank_node_with_leading_underscore() {
        testTrigEval("labeled_blank_node_with_leading_underscore.trig", "labeled_blank_node_object.nq");
    }

    @Test
    public void test_labeled_blank_node_with_leading_digit() {
        testTrigEval("labeled_blank_node_with_leading_digit.trig", "labeled_blank_node_object.nq");
    }

    @Test
    public void test_labeled_blank_node_with_non_leading_extras() {
        testTrigEval("labeled_blank_node_with_non_leading_extras.trig", "labeled_blank_node_object.nq");
    }

    @Test
    public void test_anonymous_blank_node_subject() {
        testTrigEval("anonymous_blank_node_subject.trig", "anonymous_blank_node_subject.nq");
    }

    @Test
    public void test_anonymous_blank_node_object() {
        testTrigEval("anonymous_blank_node_object.trig", "anonymous_blank_node_object.nq");
    }

    @Test
    public void test_sole_blankNodePropertyList() {
        testTrigEval("sole_blankNodePropertyList.trig", "sole_blankNodePropertyList.nq");
    }

    @Test
    public void test_blankNodePropertyList_as_subject() {
        testTrigEval("blankNodePropertyList_as_subject.trig", "blankNodePropertyList_as_subject.nq");
    }

    @Test
    public void test_blankNodePropertyList_as_object() {
        testTrigEval("blankNodePropertyList_as_object.trig", "blankNodePropertyList_as_object.nq");
    }

    @Test
    public void test_blankNodePropertyList_with_multiple_triples() {
        testTrigEval("blankNodePropertyList_with_multiple_triples.trig", "blankNodePropertyList_with_multiple_triples.nq");
    }

    @Test
    public void test_nested_blankNodePropertyLists() {
        testTrigEval("nested_blankNodePropertyLists.trig", "nested_blankNodePropertyLists.nq");
    }

    @Test
    public void test_blankNodePropertyList_containing_collection() {
        testTrigEval("blankNodePropertyList_containing_collection.trig", "blankNodePropertyList_containing_collection.nq");
    }

    @Test
    public void test_collection_subject() {
        testTrigEval("collection_subject.trig", "collection_subject.nq");
    }

    @Test
    public void test_collection_object() {
        testTrigEval("collection_object.trig", "collection_object.nq");
    }

    @Test
    public void test_empty_collection() {
        testTrigEval("empty_collection.trig", "empty_collection.nq");
    }

    @Test
    public void test_nested_collection() {
        testTrigEval("nested_collection.trig", "nested_collection.nq");
    }

    @Test
    public void test_first() {
        testTrigEval("first.trig", "first.nq");
    }

    @Test
    public void test_last() {
        testTrigEval("last.trig", "last.nq");
    }

    @Test
    public void test_LITERAL1() {
        testTrigEval("LITERAL1.trig", "LITERAL1.nq");
    }

    @Test
    public void test_LITERAL1_ascii_boundaries() {
        testTrigEval("LITERAL1_ascii_boundaries.trig", "LITERAL1_ascii_boundaries.nq");
    }

    @Test
    public void test_LITERAL1_with_UTF8_boundaries() {
        testTrigEval("LITERAL1_with_UTF8_boundaries.trig", "LITERAL_with_UTF8_boundaries.nq");
    }

    @Test
    public void test_LITERAL1_all_controls() {
        testTrigEval("LITERAL1_all_controls.trig", "LITERAL1_all_controls.nq");
    }

    @Test
    public void test_LITERAL1_all_punctuation() {
        testTrigEval("LITERAL1_all_punctuation.trig", "LITERAL1_all_punctuation.nq");
    }

    @Test
    public void test_LITERAL_LONG1() {
        testTrigEval("LITERAL_LONG1.trig", "LITERAL1.nq");
    }

    @Test
    public void test_LITERAL_LONG1_ascii_boundaries() {
        testTrigEval("LITERAL_LONG1_ascii_boundaries.trig", "LITERAL_LONG1_ascii_boundaries.nq");
    }

    @Test
    public void test_LITERAL_LONG1_with_UTF8_boundaries() {
        testTrigEval("LITERAL_LONG1_with_UTF8_boundaries.trig", "LITERAL_with_UTF8_boundaries.nq");
    }

    @Test
    public void test_LITERAL_LONG1_with_1_squote() {
        testTrigEval("LITERAL_LONG1_with_1_squote.trig", "LITERAL_LONG1_with_1_squote.nq");
    }

    @Test
    public void test_LITERAL_LONG1_with_2_squotes() {
        testTrigEval("LITERAL_LONG1_with_2_squotes.trig", "LITERAL_LONG1_with_2_squotes.nq");
    }

    @Test
    public void test_LITERAL2() {
        testTrigEval("LITERAL2.trig", "LITERAL1.nq");
    }

    @Test
    public void test_LITERAL2_ascii_boundaries() {
        testTrigEval("LITERAL2_ascii_boundaries.trig", "LITERAL2_ascii_boundaries.nq");
    }

    @Test
    public void test_LITERAL2_with_UTF8_boundaries() {
        testTrigEval("LITERAL2_with_UTF8_boundaries.trig", "LITERAL_with_UTF8_boundaries.nq");
    }

    @Test
    public void test_LITERAL_LONG2() {
        testTrigEval("LITERAL_LONG2.trig", "LITERAL1.nq");
    }

    @Test
    public void test_LITERAL_LONG2_ascii_boundaries() {
        testTrigEval("LITERAL_LONG2_ascii_boundaries.trig", "LITERAL_LONG2_ascii_boundaries.nq");
    }

    @Test
    public void test_LITERAL_LONG2_with_UTF8_boundaries() {
        testTrigEval("LITERAL_LONG2_with_UTF8_boundaries.trig", "LITERAL_with_UTF8_boundaries.nq");
    }

    @Test
    public void test_LITERAL_LONG2_with_1_squote() {
        testTrigEval("LITERAL_LONG2_with_1_squote.trig", "LITERAL_LONG2_with_1_squote.nq");
    }

    @Test
    public void test_LITERAL_LONG2_with_2_squotes() {
        testTrigEval("LITERAL_LONG2_with_2_squotes.trig", "LITERAL_LONG2_with_2_squotes.nq");
    }

    @Test
    public void test_literal_with_CHARACTER_TABULATION() {
        testTrigEval("literal_with_CHARACTER_TABULATION.trig", "literal_with_CHARACTER_TABULATION.nq");
    }

    @Test
    public void test_literal_with_BACKSPACE() {
        testTrigEval("literal_with_BACKSPACE.trig", "literal_with_BACKSPACE.nq");
    }

    @Test
    public void test_literal_with_LINE_FEED() {
        testTrigEval("literal_with_LINE_FEED.trig", "literal_with_LINE_FEED.nq");
    }

    @Test
    public void test_literal_with_CARRIAGE_RETURN() {
        testTrigEval("literal_with_CARRIAGE_RETURN.trig", "literal_with_CARRIAGE_RETURN.nq");
    }

    @Test
    public void test_literal_with_FORM_FEED() {
        testTrigEval("literal_with_FORM_FEED.trig", "literal_with_FORM_FEED.nq");
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS() {
        testTrigEval("literal_with_REVERSE_SOLIDUS.trig", "literal_with_REVERSE_SOLIDUS.nq");
    }

    @Test
    public void test_literal_with_escaped_CHARACTER_TABULATION() {
        testTrigEval("literal_with_escaped_CHARACTER_TABULATION.trig", "literal_with_CHARACTER_TABULATION.nq");
    }

    @Test
    public void test_literal_with_escaped_BACKSPACE() {
        testTrigEval("literal_with_escaped_BACKSPACE.trig", "literal_with_BACKSPACE.nq");
    }

    @Test
    public void test_literal_with_escaped_LINE_FEED() {
        testTrigEval("literal_with_escaped_LINE_FEED.trig", "literal_with_LINE_FEED.nq");
    }

    @Test
    public void test_literal_with_escaped_CARRIAGE_RETURN() {
        testTrigEval("literal_with_escaped_CARRIAGE_RETURN.trig", "literal_with_CARRIAGE_RETURN.nq");
    }

    @Test
    public void test_literal_with_escaped_FORM_FEED() {
        testTrigEval("literal_with_escaped_FORM_FEED.trig", "literal_with_FORM_FEED.nq");
    }

    @Test
    public void test_literal_with_numeric_escape4() {
        testTrigEval("literal_with_numeric_escape4.trig", "literal_with_numeric_escape4.nq");
    }

    @Test
    public void test_literal_with_numeric_escape8() {
        testTrigEval("literal_with_numeric_escape8.trig", "literal_with_numeric_escape4.nq");
    }

    @Test
    public void test_IRIREF_datatype() {
        testTrigEval("IRIREF_datatype.trig", "IRIREF_datatype.nq");
    }

    @Test
    public void test_prefixed_name_datatype() {
        testTrigEval("prefixed_name_datatype.trig", "IRIREF_datatype.nq");
    }

    @Test
    public void test_bareword_integer() {
        testTrigEval("bareword_integer.trig", "IRIREF_datatype.nq");
    }

    @Test
    public void test_bareword_decimal() {
        testTrigEval("bareword_decimal.trig", "bareword_decimal.nq");
    }

    @Test
    public void test_bareword_double() {
        testTrigEval("bareword_double.trig", "bareword_double.nq");
    }

    @Test
    public void test_double_lower_case_e() {
        testTrigEval("double_lower_case_e.trig", "double_lower_case_e.nq");
    }

    @Test
    public void test_negative_numeric() {
        testTrigEval("negative_numeric.trig", "negative_numeric.nq");
    }

    @Test
    public void test_positive_numeric() {
        testTrigEval("positive_numeric.trig", "positive_numeric.nq");
    }

    @Test
    public void test_numeric_with_leading_0() {
        testTrigEval("numeric_with_leading_0.trig", "numeric_with_leading_0.nq");
    }

    @Test
    public void test_literal_true() {
        testTrigEval("literal_true.trig", "literal_true.nq");
    }

    @Test
    public void test_literal_false() {
        testTrigEval("literal_false.trig", "literal_false.nq");
    }

    @Test
    public void test_langtagged_non_LONG() {
        testTrigEval("langtagged_non_LONG.trig", "langtagged_non_LONG.nq");
    }

    @Test
    public void test_langtagged_LONG() {
        testTrigEval("langtagged_LONG.trig", "langtagged_non_LONG.nq");
    }

    @Test
    public void test_lantag_with_subtag() {
        testTrigEval("lantag_with_subtag.trig", "lantag_with_subtag.nq");
    }

    @Test
    public void test_objectList_with_two_objects() {
        testTrigEval("objectList_with_two_objects.trig", "objectList_with_two_objects.nq");
    }

    @Test
    public void test_predicateObjectList_with_two_objectLists() {
        testTrigEval("predicateObjectList_with_two_objectLists.trig", "predicateObjectList_with_two_objectLists.nq");
    }

    @Test
    public void test_repeated_semis_at_end() {
        testTrigEval("repeated_semis_at_end.trig", "predicateObjectList_with_two_objectLists.nq");
    }

    @Test
    public void test_repeated_semis_not_at_end() {
        testTrigEval("repeated_semis_not_at_end.trig", "repeated_semis_not_at_end.nq");
    }

    @Test
    public void test_comment_following_localName() {
        testTrigEval("comment_following_localName.trig", "IRI_spo.nq");
    }

    @Test
    public void test_number_sign_following_localName() {
        testTrigEval("number_sign_following_localName.trig", "number_sign_following_localName.nq");
    }

    @Test
    public void test_comment_following_PNAME_NS() {
        testTrigEval("comment_following_PNAME_NS.trig", "comment_following_PNAME_NS.nq");
    }

    @Test
    public void test_number_sign_following_PNAME_NS() {
        testTrigEval("number_sign_following_PNAME_NS.trig", "number_sign_following_PNAME_NS.nq");
    }

    @Test
    public void test_LITERAL_LONG2_with_REVERSE_SOLIDUS() {
        testTrigEval("LITERAL_LONG2_with_REVERSE_SOLIDUS.trig", "LITERAL_LONG2_with_REVERSE_SOLIDUS.nq");
    }

    @Test
    public void test_trig_syntax_bad_num_05() {
        testTrigNegativeSyntax("trig-syntax-bad-LITERAL2_with_langtag_and_datatype.trig");
    }

    @Test
    public void test_two_LITERAL_LONG2s() {
        testTrigEval("two_LITERAL_LONG2s.trig", "two_LITERAL_LONG2s.nq");
    }

    @Test
    public void test_langtagged_LONG_with_subtag() {
        testTrigEval("langtagged_LONG_with_subtag.trig", "langtagged_LONG_with_subtag.nq");
    }

    @Test
    public void test_trig_syntax_file_01() {
        testTrigPositiveSyntax("trig-syntax-file-01.trig");
    }

    @Test
    public void test_trig_syntax_file_02() {
        testTrigPositiveSyntax("trig-syntax-file-02.trig");
    }

    @Test
    public void test_trig_syntax_file_03() {
        testTrigPositiveSyntax("trig-syntax-file-03.trig");
    }

    @Test
    public void test_trig_syntax_uri_01() {
        testTrigPositiveSyntax("trig-syntax-uri-01.trig");
    }

    @Test
    public void test_trig_syntax_uri_02() {
        testTrigPositiveSyntax("trig-syntax-uri-02.trig");
    }

    @Test
    public void test_trig_syntax_uri_03() {
        testTrigPositiveSyntax("trig-syntax-uri-03.trig");
    }

    @Test
    public void test_trig_syntax_uri_04() {
        testTrigPositiveSyntax("trig-syntax-uri-04.trig");
    }

    @Test
    public void test_trig_syntax_base_01() {
        testTrigPositiveSyntax("trig-syntax-base-01.trig");
    }

    @Test
    public void test_trig_syntax_base_02() {
        testTrigPositiveSyntax("trig-syntax-base-02.trig");
    }

    @Test
    public void test_trig_syntax_base_03() {
        testTrigPositiveSyntax("trig-syntax-base-03.trig");
    }

    @Test
    public void test_trig_syntax_base_04() {
        testTrigPositiveSyntax("trig-syntax-base-04.trig");
    }

    @Test
    public void test_trig_syntax_prefix_01() {
        testTrigPositiveSyntax("trig-syntax-prefix-01.trig");
    }

    @Test
    public void test_trig_syntax_prefix_02() {
        testTrigPositiveSyntax("trig-syntax-prefix-02.trig");
    }

    @Test
    public void test_trig_syntax_prefix_03() {
        testTrigPositiveSyntax("trig-syntax-prefix-03.trig");
    }

    @Test
    public void test_trig_syntax_prefix_04() {
        testTrigPositiveSyntax("trig-syntax-prefix-04.trig");
    }

    @Test
    public void test_trig_syntax_prefix_05() {
        testTrigPositiveSyntax("trig-syntax-prefix-05.trig");
    }

    @Test
    public void test_trig_syntax_prefix_06() {
        testTrigPositiveSyntax("trig-syntax-prefix-06.trig");
    }

    @Test
    public void test_trig_syntax_prefix_07() {
        testTrigPositiveSyntax("trig-syntax-prefix-07.trig");
    }

    @Test
    public void test_trig_syntax_prefix_08() {
        testTrigPositiveSyntax("trig-syntax-prefix-08.trig");
    }

    @Test
    public void test_trig_syntax_prefix_09() {
        testTrigPositiveSyntax("trig-syntax-prefix-09.trig");
    }

    @Test
    public void test_trig_syntax_string_01() {
        testTrigPositiveSyntax("trig-syntax-string-01.trig");
    }

    @Test
    public void test_trig_syntax_string_02() {
        testTrigPositiveSyntax("trig-syntax-string-02.trig");
    }

    @Test
    public void test_trig_syntax_string_03() {
        testTrigPositiveSyntax("trig-syntax-string-03.trig");
    }

    @Test
    public void test_trig_syntax_string_04() {
        testTrigPositiveSyntax("trig-syntax-string-04.trig");
    }

    @Test
    public void test_trig_syntax_string_05() {
        testTrigPositiveSyntax("trig-syntax-string-05.trig");
    }

    @Test
    public void test_trig_syntax_string_06() {
        testTrigPositiveSyntax("trig-syntax-string-06.trig");
    }

    @Test
    public void test_trig_syntax_string_07() {
        testTrigPositiveSyntax("trig-syntax-string-07.trig");
    }

    @Test
    public void test_trig_syntax_string_08() {
        testTrigPositiveSyntax("trig-syntax-string-08.trig");
    }

    @Test
    public void test_trig_syntax_string_09() {
        testTrigPositiveSyntax("trig-syntax-string-09.trig");
    }

    @Test
    public void test_trig_syntax_string_10() {
        testTrigPositiveSyntax("trig-syntax-string-10.trig");
    }

    @Test
    public void test_trig_syntax_string_11() {
        testTrigPositiveSyntax("trig-syntax-string-11.trig");
    }

    @Test
    public void test_trig_syntax_str_esc_01() {
        testTrigPositiveSyntax("trig-syntax-str-esc-01.trig");
    }

    @Test
    public void test_trig_syntax_str_esc_02() {
        testTrigPositiveSyntax("trig-syntax-str-esc-02.trig");
    }

    @Test
    public void test_trig_syntax_str_esc_03() {
        testTrigPositiveSyntax("trig-syntax-str-esc-03.trig");
    }

    @Test
    public void test_trig_syntax_pname_esc_01() {
        testTrigPositiveSyntax("trig-syntax-pname-esc-01.trig");
    }

    @Test
    public void test_trig_syntax_pname_esc_02() {
        testTrigPositiveSyntax("trig-syntax-pname-esc-02.trig");
    }

    @Test
    public void test_trig_syntax_pname_esc_03() {
        testTrigPositiveSyntax("trig-syntax-pname-esc-03.trig");
    }

    @Test
    public void test_trig_syntax_bnode_01() {
        testTrigPositiveSyntax("trig-syntax-bnode-01.trig");
    }

    @Test
    public void test_trig_syntax_bnode_02() {
        testTrigPositiveSyntax("trig-syntax-bnode-02.trig");
    }

    @Test
    public void test_trig_syntax_bnode_03() {
        testTrigPositiveSyntax("trig-syntax-bnode-03.trig");
    }

    @Test
    public void test_trig_syntax_bnode_04() {
        testTrigPositiveSyntax("trig-syntax-bnode-04.trig");
    }

    @Test
    public void test_trig_syntax_bnode_05() {
        testTrigPositiveSyntax("trig-syntax-bnode-05.trig");
    }

    @Test
    public void test_trig_syntax_bnode_06() {
        testTrigPositiveSyntax("trig-syntax-bnode-06.trig");
    }

    @Test
    public void test_trig_syntax_bnode_07() {
        testTrigPositiveSyntax("trig-syntax-bnode-07.trig");
    }

    @Test
    public void test_trig_syntax_bnode_08() {
        testTrigPositiveSyntax("trig-syntax-bnode-08.trig");
    }

    @Test
    public void test_trig_syntax_bnode_09() {
        testTrigPositiveSyntax("trig-syntax-bnode-09.trig");
    }

    @Test
    public void test_trig_syntax_bnode_10() {
        testTrigPositiveSyntax("trig-syntax-bnode-10.trig");
    }

    @Test
    public void test_trig_syntax_number_01() {
        testTrigPositiveSyntax("trig-syntax-number-01.trig");
    }

    @Test
    public void test_trig_syntax_number_02() {
        testTrigPositiveSyntax("trig-syntax-number-02.trig");
    }

    @Test
    public void test_trig_syntax_number_03() {
        testTrigPositiveSyntax("trig-syntax-number-03.trig");
    }

    @Test
    public void test_trig_syntax_number_04() {
        testTrigPositiveSyntax("trig-syntax-number-04.trig");
    }

    @Test
    public void test_trig_syntax_number_05() {
        testTrigPositiveSyntax("trig-syntax-number-05.trig");
    }

    @Test
    public void test_trig_syntax_number_06() {
        testTrigPositiveSyntax("trig-syntax-number-06.trig");
    }

    @Test
    public void test_trig_syntax_number_07() {
        testTrigPositiveSyntax("trig-syntax-number-07.trig");
    }

    @Test
    public void test_trig_syntax_number_08() {
        testTrigPositiveSyntax("trig-syntax-number-08.trig");
    }

    @Test
    public void test_trig_syntax_number_09() {
        testTrigPositiveSyntax("trig-syntax-number-09.trig");
    }

    @Test
    public void test_trig_syntax_number_10() {
        testTrigPositiveSyntax("trig-syntax-number-10.trig");
    }

    @Test
    public void test_trig_syntax_number_11() {
        testTrigPositiveSyntax("trig-syntax-number-11.trig");
    }

    @Test
    public void test_trig_syntax_datatypes_01() {
        testTrigPositiveSyntax("trig-syntax-datatypes-01.trig");
    }

    @Test
    public void test_trig_syntax_datatypes_02() {
        testTrigPositiveSyntax("trig-syntax-datatypes-02.trig");
    }

    @Test
    public void test_trig_syntax_kw_01() {
        testTrigPositiveSyntax("trig-syntax-kw-01.trig");
    }

    @Test
    public void test_trig_syntax_kw_02() {
        testTrigPositiveSyntax("trig-syntax-kw-02.trig");
    }

    @Test
    public void test_trig_syntax_kw_03() {
        testTrigPositiveSyntax("trig-syntax-kw-03.trig");
    }

    @Test
    public void test_trig_syntax_struct_01() {
        testTrigPositiveSyntax("trig-syntax-struct-01.trig");
    }

    @Test
    public void test_trig_syntax_struct_02() {
        testTrigPositiveSyntax("trig-syntax-struct-02.trig");
    }

    @Test
    public void test_trig_syntax_struct_03() {
        testTrigPositiveSyntax("trig-syntax-struct-03.trig");
    }

    @Test
    public void test_trig_syntax_struct_04() {
        testTrigPositiveSyntax("trig-syntax-struct-04.trig");
    }

    @Test
    public void test_trig_syntax_struct_05() {
        testTrigPositiveSyntax("trig-syntax-struct-05.trig");
    }

    @Test
    public void test_trig_syntax_lists_01() {
        testTrigPositiveSyntax("trig-syntax-lists-01.trig");
    }

    @Test
    public void test_trig_syntax_lists_02() {
        testTrigPositiveSyntax("trig-syntax-lists-02.trig");
    }

    @Test
    public void test_trig_syntax_lists_03() {
        testTrigPositiveSyntax("trig-syntax-lists-03.trig");
    }

    @Test
    public void test_trig_syntax_lists_04() {
        testTrigPositiveSyntax("trig-syntax-lists-04.trig");
    }

    @Test
    public void test_trig_syntax_lists_05() {
        testTrigPositiveSyntax("trig-syntax-lists-05.trig");
    }

    @Test
    public void test_trig_syntax_bad_uri_01() {
        testTrigNegativeSyntax("trig-syntax-bad-uri-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_uri_02() {
        testTrigNegativeSyntax("trig-syntax-bad-uri-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_uri_03() {
        testTrigNegativeSyntax("trig-syntax-bad-uri-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_uri_04() {
        testTrigNegativeSyntax("trig-syntax-bad-uri-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_uri_05() {
        testTrigNegativeSyntax("trig-syntax-bad-uri-05.trig");
    }

    @Test
    public void test_trig_syntax_bad_prefix_01() {
        testTrigNegativeSyntax("trig-syntax-bad-prefix-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_prefix_02() {
        testTrigNegativeSyntax("trig-syntax-bad-prefix-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_prefix_03() {
        testTrigNegativeSyntax("trig-syntax-bad-prefix-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_prefix_04() {
        testTrigNegativeSyntax("trig-syntax-bad-prefix-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_prefix_05() {
        testTrigNegativeSyntax("trig-syntax-bad-prefix-05.trig");
    }

    @Test
    public void test_trig_syntax_bad_base_01() {
        testTrigNegativeSyntax("trig-syntax-bad-base-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_base_02() {
        testTrigNegativeSyntax("trig-syntax-bad-base-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_base_03() {
        testTrigNegativeSyntax("trig-syntax-bad-base-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_02() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_03() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_04() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_05() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-05.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_06() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-06.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_07() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-07.trig");
    }

    @Test
    public void test_trig_syntax_bad_kw_01() {
        testTrigNegativeSyntax("trig-syntax-bad-kw-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_kw_02() {
        testTrigNegativeSyntax("trig-syntax-bad-kw-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_kw_03() {
        testTrigNegativeSyntax("trig-syntax-bad-kw-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_kw_04() {
        testTrigNegativeSyntax("trig-syntax-bad-kw-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_kw_05() {
        testTrigNegativeSyntax("trig-syntax-bad-kw-05.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_01() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_02() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_03() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_04() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_05() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-05.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_06() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-06.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_07() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-07.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_08() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-08.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_09() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-09.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_10() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-10.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_11() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-11.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_12() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-12.trig");
    }

    @Test
    public void test_trig_syntax_bad_n3_extras_13() {
        testTrigNegativeSyntax("trig-syntax-bad-n3-extras-13.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_09() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-09.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_10() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-10.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_12() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-12.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_13() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-13.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_14() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-14.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_15() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-15.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_16() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-16.trig");
    }

    @Test
    public void test_trig_syntax_bad_struct_17() {
        testTrigNegativeSyntax("trig-syntax-bad-struct-17.trig");
    }

    @Test
    public void test_trig_syntax_bad_lang_01() {
        testTrigNegativeSyntax("trig-syntax-bad-lang-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_esc_01() {
        testTrigNegativeSyntax("trig-syntax-bad-esc-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_esc_02() {
        testTrigNegativeSyntax("trig-syntax-bad-esc-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_esc_03() {
        testTrigNegativeSyntax("trig-syntax-bad-esc-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_esc_04() {
        testTrigNegativeSyntax("trig-syntax-bad-esc-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_pname_01() {
        testTrigNegativeSyntax("trig-syntax-bad-pname-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_pname_02() {
        testTrigNegativeSyntax("trig-syntax-bad-pname-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_pname_03() {
        testTrigNegativeSyntax("trig-syntax-bad-pname-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_string_01() {
        testTrigNegativeSyntax("trig-syntax-bad-string-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_string_02() {
        testTrigNegativeSyntax("trig-syntax-bad-string-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_string_03() {
        testTrigNegativeSyntax("trig-syntax-bad-string-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_string_04() {
        testTrigNegativeSyntax("trig-syntax-bad-string-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_string_05() {
        testTrigNegativeSyntax("trig-syntax-bad-string-05.trig");
    }

    @Test
    public void test_trig_syntax_bad_string_06() {
        testTrigNegativeSyntax("trig-syntax-bad-string-06.trig");
    }

    @Test
    public void test_trig_syntax_bad_string_07() {
        testTrigNegativeSyntax("trig-syntax-bad-string-07.trig");
    }

    @Test
    public void test_trig_syntax_bad_num_01() {
        testTrigNegativeSyntax("trig-syntax-bad-num-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_num_02() {
        testTrigNegativeSyntax("trig-syntax-bad-num-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_num_03() {
        testTrigNegativeSyntax("trig-syntax-bad-num-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_num_04() {
        testTrigNegativeSyntax("trig-syntax-bad-num-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_num_05_2() {
        testTrigNegativeSyntax("trig-syntax-bad-num-05.trig");
    }

    @Test
    public void test_trig_eval_struct_01() {
        testTrigEval("trig-eval-struct-01.trig", "trig-eval-struct-01.nq");
    }

    @Test
    public void test_trig_eval_struct_02() {
        testTrigEval("trig-eval-struct-02.trig", "trig-eval-struct-02.nq");
    }

    @Test
    public void test_trig_subm_01() {
        testTrigEval("trig-subm-01.trig", "trig-subm-01.nq");
    }

    @Test
    public void test_trig_subm_02() {
        testTrigEval("trig-subm-02.trig", "trig-subm-02.nq");
    }

    @Test
    public void test_trig_subm_03() {
        testTrigEval("trig-subm-03.trig", "trig-subm-03.nq");
    }

    @Test
    public void test_trig_subm_04() {
        testTrigEval("trig-subm-04.trig", "trig-subm-04.nq");
    }

    @Test
    public void test_trig_subm_05() {
        testTrigEval("trig-subm-05.trig", "trig-subm-05.nq");
    }

    @Test
    public void test_trig_subm_06() {
        testTrigEval("trig-subm-06.trig", "trig-subm-06.nq");
    }

    @Test
    public void test_trig_subm_07() {
        testTrigEval("trig-subm-07.trig", "trig-subm-07.nq");
    }

    @Test
    public void test_trig_subm_08() {
        testTrigEval("trig-subm-08.trig", "trig-subm-08.nq");
    }

    @Test
    public void test_trig_subm_09() {
        testTrigEval("trig-subm-09.trig", "trig-subm-09.nq");
    }

    @Test
    public void test_trig_subm_10() {
        testTrigEval("trig-subm-10.trig", "trig-subm-10.nq");
    }

    @Test
    public void test_trig_subm_11() {
        testTrigEval("trig-subm-11.trig", "trig-subm-11.nq");
    }

    @Test
    public void test_trig_subm_12() {
        testTrigEval("trig-subm-12.trig", "trig-subm-12.nq");
    }

    @Test
    public void test_trig_subm_13() {
        testTrigEval("trig-subm-13.trig", "trig-subm-13.nq");
    }

    @Test
    public void test_trig_subm_14() {
        testTrigEval("trig-subm-14.trig", "trig-subm-14.nq");
    }

    @Test
    public void test_trig_subm_15() {
        testTrigEval("trig-subm-15.trig", "trig-subm-15.nq");
    }

    @Test
    public void test_trig_subm_16() {
        testTrigEval("trig-subm-16.trig", "trig-subm-16.nq");
    }

    @Test
    public void test_trig_subm_17() {
        testTrigEval("trig-subm-17.trig", "trig-subm-17.nq");
    }

    @Test
    public void test_trig_subm_18() {
        testTrigEval("trig-subm-18.trig", "trig-subm-18.nq");
    }

    @Test
    public void test_trig_subm_19() {
        testTrigEval("trig-subm-19.trig", "trig-subm-19.nq");
    }

    @Test
    public void test_trig_subm_20() {
        testTrigEval("trig-subm-20.trig", "trig-subm-20.nq");
    }

    @Test
    public void test_trig_subm_21() {
        testTrigEval("trig-subm-21.trig", "trig-subm-21.nq");
    }

    @Test
    public void test_trig_subm_22() {
        testTrigEval("trig-subm-22.trig", "trig-subm-22.nq");
    }

    @Test
    public void test_trig_subm_23() {
        testTrigEval("trig-subm-23.trig", "trig-subm-23.nq");
    }

    @Test
    public void test_trig_subm_24() {
        testTrigEval("trig-subm-24.trig", "trig-subm-24.nq");
    }

    @Test
    public void test_trig_subm_25() {
        testTrigEval("trig-subm-25.trig", "trig-subm-25.nq");
    }

    @Test
    public void test_trig_subm_26() {
        testTrigEval("trig-subm-26.trig", "trig-subm-26.nq");
    }

    @Test
    public void test_trig_subm_27() {
        testTrigEval("trig-subm-27.trig", "trig-subm-27.nq");
    }

    @Test
    public void test_trig_eval_bad_01() {
        testTrigNegativeSyntax("trig-eval-bad-01.trig");
    }

    @Test
    public void test_trig_eval_bad_02() {
        testTrigNegativeSyntax("trig-eval-bad-02.trig");
    }

    @Test
    public void test_trig_eval_bad_03() {
        testTrigNegativeSyntax("trig-eval-bad-03.trig");
    }

    @Test
    public void test_trig_eval_bad_04() {
        testTrigNegativeSyntax("trig-eval-bad-04.trig");
    }

    @Test
    public void test_trig_syntax_bad_blank_label_dot_end() {
        testTrigNegativeSyntax("trig-syntax-bad-blank-label-dot-end.trig");
    }

    @Test
    public void test_trig_syntax_bad_ln_dash_start() {
        testTrigNegativeSyntax("trig-syntax-bad-ln-dash-start.trig");
    }

    @Test
    public void test_trig_syntax_bad_ln_escape_start() {
        testTrigNegativeSyntax("trig-syntax-bad-ln-escape-start.trig");
    }

    @Test
    public void test_trig_syntax_bad_ln_escape() {
        testTrigNegativeSyntax("trig-syntax-bad-ln-escape.trig");
    }

    @Test
    public void test_trig_syntax_bad_missing_ns_dot_end() {
        testTrigNegativeSyntax("trig-syntax-bad-missing-ns-dot-end.trig");
    }

    @Test
    public void test_trig_syntax_bad_missing_ns_dot_start() {
        testTrigNegativeSyntax("trig-syntax-bad-missing-ns-dot-start.trig");
    }

    @Test
    public void test_trig_syntax_bad_ns_dot_end() {
        testTrigNegativeSyntax("trig-syntax-bad-ns-dot-end.trig");
    }

    @Test
    public void test_trig_syntax_bad_ns_dot_start() {
        testTrigNegativeSyntax("trig-syntax-bad-ns-dot-start.trig");
    }

    @Test
    public void test_trig_syntax_bad_number_dot_in_anon() {
        testTrigNegativeSyntax("trig-syntax-bad-number-dot-in-anon.trig");
    }

    @Test
    public void test_trig_syntax_bad_list_01() {
        testTrigNegativeSyntax("trig-syntax-bad-list-01.trig");
    }

    @Test
    public void test_trig_syntax_bad_list_02() {
        testTrigNegativeSyntax("trig-syntax-bad-list-02.trig");
    }

    @Test
    public void test_trig_syntax_bad_list_03() {
        testTrigNegativeSyntax("trig-syntax-bad-list-03.trig");
    }

    @Test
    public void test_trig_syntax_bad_list_04() {
        testTrigNegativeSyntax("trig-syntax-bad-list-04.trig");
    }

    @Test
    public void test_trig_syntax_blank_label() {
        testTrigPositiveSyntax("trig-syntax-blank-label.trig");
    }

    @Test
    public void test_trig_syntax_ln_colons() {
        testTrigPositiveSyntax("trig-syntax-ln-colons.trig");
    }

    @Test
    public void test_trig_syntax_ln_dots() {
        testTrigPositiveSyntax("trig-syntax-ln-dots.trig");
    }

    @Test
    public void test_trig_syntax_ns_dots() {
        testTrigPositiveSyntax("trig-syntax-ns-dots.trig");
    }

    @Test
    public void test_trig_kw_graph_01() {
        testTrigPositiveSyntax("trig-kw-graph-01.trig");
    }

    @Test
    public void test_trig_kw_graph_02() {
        testTrigPositiveSyntax("trig-kw-graph-02.trig");
    }

    @Test
    public void test_trig_kw_graph_03() {
        testTrigPositiveSyntax("trig-kw-graph-03.trig");
    }

    @Test
    public void test_trig_kw_graph_04() {
        testTrigPositiveSyntax("trig-kw-graph-04.trig");
    }

    @Test
    public void test_trig_kw_graph_05() {
        testTrigPositiveSyntax("trig-kw-graph-05.trig");
    }

    @Test
    public void test_trig_kw_graph_06() {
        testTrigPositiveSyntax("trig-kw-graph-06.trig");
    }

    @Test
    public void test_trig_kw_graph_07() {
        testTrigPositiveSyntax("trig-kw-graph-07.trig");
    }

    @Test
    public void test_trig_kw_graph_08() {
        testTrigPositiveSyntax("trig-kw-graph-08.trig");
    }

    @Test
    public void test_trig_kw_graph_09() {
        testTrigPositiveSyntax("trig-kw-graph-09.trig");
    }

    @Test
    public void test_trig_kw_graph_10() {
        testTrigPositiveSyntax("trig-kw-graph-10.trig");
    }

    @Test
    public void test_trig_graph_bad_01() {
        testTrigNegativeSyntax("trig-graph-bad-01.trig");
    }

    @Test
    public void test_trig_graph_bad_02() {
        testTrigNegativeSyntax("trig-graph-bad-02.trig");
    }

    @Test
    public void test_trig_graph_bad_03() {
        testTrigNegativeSyntax("trig-graph-bad-03.trig");
    }

    @Test
    public void test_trig_graph_bad_04() {
        testTrigNegativeSyntax("trig-graph-bad-04.trig");
    }

    @Test
    public void test_trig_graph_bad_05() {
        testTrigNegativeSyntax("trig-graph-bad-05.trig");
    }

    @Test
    public void test_trig_graph_bad_06() {
        testTrigNegativeSyntax("trig-graph-bad-06.trig");
    }

    @Test
    public void test_trig_graph_bad_07() {
        testTrigNegativeSyntax("trig-graph-bad-07.trig");
    }

    @Test
    public void test_trig_graph_bad_08() {
        testTrigNegativeSyntax("trig-graph-bad-08.trig");
    }

    @Test
    public void test_trig_graph_bad_09() {
        testTrigNegativeSyntax("trig-graph-bad-09.trig");
    }

    @Test
    public void test_trig_graph_bad_10() {
        testTrigNegativeSyntax("trig-graph-bad-10.trig");
    }

    @Test
    public void test_trig_graph_bad_11() {
        testTrigNegativeSyntax("trig-graph-bad-11.trig");
    }

    @Test
    public void test_trig_bnodeplist_graph_bad_01() {
        testTrigNegativeSyntax("trig-bnodeplist-graph-bad-01.trig");
    }

    @Test
    public void test_trig_collection_graph_bad_01() {
        testTrigNegativeSyntax("trig-collection-graph-bad-01.trig");
    }

    @Test
    public void test_trig_collection_graph_bad_02() {
        testTrigNegativeSyntax("trig-collection-graph-bad-02.trig");
    }

    @Test
    public void test_trig_turtle_01() {
        testTrigPositiveSyntax("trig-turtle-01.trig");
    }

    @Test
    public void test_trig_turtle_02() {
        testTrigPositiveSyntax("trig-turtle-02.trig");
    }

    @Test
    public void test_trig_turtle_03() {
        testTrigPositiveSyntax("trig-turtle-03.trig");
    }

    @Test
    public void test_trig_turtle_04() {
        testTrigPositiveSyntax("trig-turtle-04.trig");
    }

    @Test
    public void test_trig_turtle_05() {
        testTrigPositiveSyntax("trig-turtle-05.trig");
    }

    @Test
    public void test_trig_turtle_06() {
        testTrigPositiveSyntax("trig-turtle-06.trig");
    }

    @Test
    public void test_trig_turtle_bad_01() {
        testTrigNegativeSyntax("trig-turtle-bad-01.trig");
    }

    @Test
    public void test_trig_turtle_bad_02() {
        testTrigNegativeSyntax("trig-turtle-bad-02.trig");
    }
}
