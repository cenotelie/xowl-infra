/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.store;

import org.junit.Test;

import java.io.IOException;

/**
 * Test suite for Turtle
 *
 * @author Laurent Wouters
 */
public class TurtleParsingTest extends W3CTestSuite {

    public TurtleParsingTest() throws IOException {

    }

    @Test
    public void test_HYPHEN_MINUS_in_localName() {
        test_parsing("/turtle/HYPHEN_MINUS_in_localName.ttl", true);
    }

    @Test
    public void test_IRIREF_datatype() {
        test_parsing("/turtle/IRIREF_datatype.ttl", true);
    }

    @Test
    public void test_IRI_subject() {
        test_parsing("/turtle/IRI_subject.ttl", true);
    }

    @Test
    public void test_IRI_with_all_punctuation() {
        test_parsing("/turtle/IRI_with_all_punctuation.ttl", true);
    }

    @Test
    public void test_IRI_with_eight_digit_numeric_escape() {
        test_parsing("/turtle/IRI_with_eight_digit_numeric_escape.ttl", true);
    }

    @Test
    public void test_IRI_with_four_digit_numeric_escape() {
        test_parsing("/turtle/IRI_with_four_digit_numeric_escape.ttl", true);
    }

    @Test
    public void test_LITERAL1() {
        test_parsing("/turtle/LITERAL1.ttl", true);
    }

    @Test
    public void test_LITERAL1_all_controls() {
        test_parsing("/turtle/LITERAL1_all_controls.ttl", true);
    }

    @Test
    public void test_LITERAL1_all_punctuation() {
        test_parsing("/turtle/LITERAL1_all_punctuation.ttl", true);
    }

    @Test
    public void test_LITERAL1_ascii_boundaries() {
        test_parsing("/turtle/LITERAL1_ascii_boundaries.ttl", true);
    }

    @Test
    public void test_LITERAL1_with_UTF8_boundaries() {
        test_parsing("/turtle/LITERAL1_with_UTF8_boundaries.ttl", true);
    }

    @Test
    public void test_LITERAL2() {
        test_parsing("/turtle/LITERAL2.ttl", true);
    }

    @Test
    public void test_LITERAL2_ascii_boundaries() {
        test_parsing("/turtle/LITERAL2_ascii_boundaries.ttl", true);
    }

    @Test
    public void test_LITERAL2_with_UTF8_boundaries() {
        test_parsing("/turtle/LITERAL2_with_UTF8_boundaries.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG1() {
        test_parsing("/turtle/LITERAL_LONG1.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG1_ascii_boundaries() {
        test_parsing("/turtle/LITERAL_LONG1_ascii_boundaries.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG1_with_1_squote() {
        test_parsing("/turtle/LITERAL_LONG1_with_1_squote.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG1_with_2_squotes() {
        test_parsing("/turtle/LITERAL_LONG1_with_2_squotes.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG1_with_UTF8_boundaries() {
        test_parsing("/turtle/LITERAL_LONG1_with_UTF8_boundaries.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG2() {
        test_parsing("/turtle/LITERAL_LONG2.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG2_ascii_boundaries() {
        test_parsing("/turtle/LITERAL_LONG2_ascii_boundaries.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG2_with_1_squote() {
        test_parsing("/turtle/LITERAL_LONG2_with_1_squote.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG2_with_2_squotes() {
        test_parsing("/turtle/LITERAL_LONG2_with_2_squotes.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG2_with_REVERSE_SOLIDUS() {
        test_parsing("/turtle/LITERAL_LONG2_with_REVERSE_SOLIDUS.ttl", true);
    }

    @Test
    public void test_LITERAL_LONG2_with_UTF8_boundaries() {
        test_parsing("/turtle/LITERAL_LONG2_with_UTF8_boundaries.ttl", true);
    }

    @Test
    public void test_SPARQL_style_base() {
        test_parsing("/turtle/SPARQL_style_base.ttl", true);
    }

    @Test
    public void test_SPARQL_style_prefix() {
        test_parsing("/turtle/SPARQL_style_prefix.ttl", true);
    }

    @Test
    public void test_anonymous_blank_node_object() {
        test_parsing("/turtle/anonymous_blank_node_object.ttl", true);
    }

    @Test
    public void test_anonymous_blank_node_subject() {
        test_parsing("/turtle/anonymous_blank_node_subject.ttl", true);
    }

    @Test
    public void test_bareword_a_predicate() {
        test_parsing("/turtle/bareword_a_predicate.ttl", true);
    }

    @Test
    public void test_bareword_decimal() {
        test_parsing("/turtle/bareword_decimal.ttl", true);
    }

    @Test
    public void test_bareword_double() {
        test_parsing("/turtle/bareword_double.ttl", true);
    }

    @Test
    public void test_bareword_integer() {
        test_parsing("/turtle/bareword_integer.ttl", true);
    }

    @Test
    public void test_blankNodePropertyList_as_object() {
        test_parsing("/turtle/blankNodePropertyList_as_object.ttl", true);
    }

    @Test
    public void test_blankNodePropertyList_as_subject() {
        test_parsing("/turtle/blankNodePropertyList_as_subject.ttl", true);
    }

    @Test
    public void test_blankNodePropertyList_containing_collection() {
        test_parsing("/turtle/blankNodePropertyList_containing_collection.ttl", true);
    }

    @Test
    public void test_blankNodePropertyList_with_multiple_triples() {
        test_parsing("/turtle/blankNodePropertyList_with_multiple_triples.ttl", true);
    }

    @Test
    public void test_collection_object() {
        test_parsing("/turtle/collection_object.ttl", true);
    }

    @Test
    public void test_collection_subject() {
        test_parsing("/turtle/collection_subject.ttl", true);
    }

    @Test
    public void test_comment_following_PNAME_NS() {
        test_parsing("/turtle/comment_following_PNAME_NS.ttl", true);
    }

    @Test
    public void test_comment_following_localName() {
        test_parsing("/turtle/comment_following_localName.ttl", true);
    }

    @Test
    public void test_default_namespace_IRI() {
        test_parsing("/turtle/default_namespace_IRI.ttl", true);
    }

    @Test
    public void test_double_lower_case_e() {
        test_parsing("/turtle/double_lower_case_e.ttl", true);
    }

    @Test
    public void test_empty_collection() {
        test_parsing("/turtle/empty_collection.ttl", true);
    }

    @Test
    public void test_first() {
        test_parsing("/turtle/first.ttl", true);
    }

    @Test
    public void test_labeled_blank_node_object() {
        test_parsing("/turtle/labeled_blank_node_object.ttl", true);
    }

    @Test
    public void test_labeled_blank_node_subject() {
        test_parsing("/turtle/labeled_blank_node_subject.ttl", true);
    }

    @Test
    public void test_labeled_blank_node_with_PN_CHARS_BASE_character_boundaries() {
        test_parsing("/turtle/labeled_blank_node_with_PN_CHARS_BASE_character_boundaries.ttl", true);
    }

    @Test
    public void test_labeled_blank_node_with_leading_digit() {
        test_parsing("/turtle/labeled_blank_node_with_leading_digit.ttl", true);
    }

    @Test
    public void test_labeled_blank_node_with_leading_underscore() {
        test_parsing("/turtle/labeled_blank_node_with_leading_underscore.ttl", true);
    }

    @Test
    public void test_labeled_blank_node_with_non_leading_extras() {
        test_parsing("/turtle/labeled_blank_node_with_non_leading_extras.ttl", true);
    }

    @Test
    public void test_langtagged_LONG() {
        test_parsing("/turtle/langtagged_LONG.ttl", true);
    }

    @Test
    public void test_langtagged_LONG_with_subtag() {
        test_parsing("/turtle/langtagged_LONG_with_subtag.ttl", true);
    }

    @Test
    public void test_langtagged_non_LONG() {
        test_parsing("/turtle/langtagged_non_LONG.ttl", true);
    }

    @Test
    public void test_lantag_with_subtag() {
        test_parsing("/turtle/lantag_with_subtag.ttl", true);
    }

    @Test
    public void test_last() {
        test_parsing("/turtle/last.ttl", true);
    }

    @Test
    public void test_literal_false() {
        test_parsing("/turtle/literal_false.ttl", true);
    }

    @Test
    public void test_literal_true() {
        test_parsing("/turtle/literal_true.ttl", true);
    }

    @Test
    public void test_literal_with_BACKSPACE() {
        test_parsing("/turtle/literal_with_BACKSPACE.ttl", true);
    }

    @Test
    public void test_literal_with_CARRIAGE_RETURN() {
        test_parsing("/turtle/literal_with_CARRIAGE_RETURN.ttl", true);
    }

    @Test
    public void test_literal_with_CHARACTER_TABULATION() {
        test_parsing("/turtle/literal_with_CHARACTER_TABULATION.ttl", true);
    }

    @Test
    public void test_literal_with_FORM_FEED() {
        test_parsing("/turtle/literal_with_FORM_FEED.ttl", true);
    }

    @Test
    public void test_literal_with_LINE_FEED() {
        test_parsing("/turtle/literal_with_LINE_FEED.ttl", true);
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS() {
        test_parsing("/turtle/literal_with_REVERSE_SOLIDUS.ttl", true);
    }

    @Test
    public void test_literal_with_escaped_BACKSPACE() {
        test_parsing("/turtle/literal_with_escaped_BACKSPACE.ttl", true);
    }

    @Test
    public void test_literal_with_escaped_CARRIAGE_RETURN() {
        test_parsing("/turtle/literal_with_escaped_CARRIAGE_RETURN.ttl", true);
    }

    @Test
    public void test_literal_with_escaped_CHARACTER_TABULATION() {
        test_parsing("/turtle/literal_with_escaped_CHARACTER_TABULATION.ttl", true);
    }

    @Test
    public void test_literal_with_escaped_FORM_FEED() {
        test_parsing("/turtle/literal_with_escaped_FORM_FEED.ttl", true);
    }

    @Test
    public void test_literal_with_escaped_LINE_FEED() {
        test_parsing("/turtle/literal_with_escaped_LINE_FEED.ttl", true);
    }

    @Test
    public void test_literal_with_numeric_escape4() {
        test_parsing("/turtle/literal_with_numeric_escape4.ttl", true);
    }

    @Test
    public void test_literal_with_numeric_escape8() {
        test_parsing("/turtle/literal_with_numeric_escape8.ttl", true);
    }

    @Test
    public void test_localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries() {
        test_parsing("/turtle/localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries.ttl", true);
    }

    @Test
    public void test_localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries() {
        test_parsing("/turtle/localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries.ttl", true);
    }

    @Test
    public void test_localName_with_leading_digit() {
        test_parsing("/turtle/localName_with_leading_digit.ttl", true);
    }

    @Test
    public void test_localName_with_leading_underscore() {
        test_parsing("/turtle/localName_with_leading_underscore.ttl", true);
    }

    @Test
    public void test_localName_with_nfc_PN_CHARS_BASE_character_boundaries() {
        test_parsing("/turtle/localName_with_nfc_PN_CHARS_BASE_character_boundaries.ttl", true);
    }

    @Test
    public void test_localName_with_non_leading_extras() {
        test_parsing("/turtle/localName_with_non_leading_extras.ttl", true);
    }

    @Test
    public void test_localname_with_COLON() {
        test_parsing("/turtle/localname_with_COLON.ttl", true);
    }

    @Test
    public void test_manifest() {
        test_parsing("/turtle/manifest.ttl", true);
    }

    @Test
    public void test_negative_numeric() {
        test_parsing("/turtle/negative_numeric.ttl", true);
    }

    @Test
    public void test_nested_blankNodePropertyLists() {
        test_parsing("/turtle/nested_blankNodePropertyLists.ttl", true);
    }

    @Test
    public void test_nested_collection() {
        test_parsing("/turtle/nested_collection.ttl", true);
    }

    @Test
    public void test_number_sign_following_PNAME_NS() {
        test_parsing("/turtle/number_sign_following_PNAME_NS.ttl", true);
    }

    @Test
    public void test_number_sign_following_localName() {
        test_parsing("/turtle/number_sign_following_localName.ttl", true);
    }

    @Test
    public void test_numeric_with_leading_0() {
        test_parsing("/turtle/numeric_with_leading_0.ttl", true);
    }

    @Test
    public void test_objectList_with_two_objects() {
        test_parsing("/turtle/objectList_with_two_objects.ttl", true);
    }

    @Test
    public void test_old_style_base() {
        test_parsing("/turtle/old_style_base.ttl", true);
    }

    @Test
    public void test_old_style_prefix() {
        test_parsing("/turtle/old_style_prefix.ttl", true);
    }

    @Test
    public void test_percent_escaped_localName() {
        test_parsing("/turtle/percent_escaped_localName.ttl", true);
    }

    @Test
    public void test_positive_numeric() {
        test_parsing("/turtle/positive_numeric.ttl", true);
    }

    @Test
    public void test_predicateObjectList_with_two_objectLists() {
        test_parsing("/turtle/predicateObjectList_with_two_objectLists.ttl", true);
    }

    @Test
    public void test_prefix_only_IRI() {
        test_parsing("/turtle/prefix_only_IRI.ttl", true);
    }

    @Test
    public void test_prefix_reassigned_and_used() {
        test_parsing("/turtle/prefix_reassigned_and_used.ttl", true);
    }

    @Test
    public void test_prefix_with_PN_CHARS_BASE_character_boundaries() {
        test_parsing("/turtle/prefix_with_PN_CHARS_BASE_character_boundaries.ttl", true);
    }

    @Test
    public void test_prefix_with_non_leading_extras() {
        test_parsing("/turtle/prefix_with_non_leading_extras.ttl", true);
    }

    @Test
    public void test_prefixed_IRI_object() {
        test_parsing("/turtle/prefixed_IRI_object.ttl", true);
    }

    @Test
    public void test_prefixed_IRI_predicate() {
        test_parsing("/turtle/prefixed_IRI_predicate.ttl", true);
    }

    @Test
    public void test_prefixed_name_datatype() {
        test_parsing("/turtle/prefixed_name_datatype.ttl", true);
    }

    @Test
    public void test_repeated_semis_at_end() {
        test_parsing("/turtle/repeated_semis_at_end.ttl", true);
    }

    @Test
    public void test_repeated_semis_not_at_end() {
        test_parsing("/turtle/repeated_semis_not_at_end.ttl", true);
    }

    @Test
    public void test_reserved_escaped_localName() {
        test_parsing("/turtle/reserved_escaped_localName.ttl", true);
    }

    @Test
    public void test_sole_blankNodePropertyList() {
        test_parsing("/turtle/sole_blankNodePropertyList.ttl", true);
    }

    @Test
    public void test_turtle_eval_bad_01() {
        test_parsing("/turtle/turtle-eval-bad-01.ttl", false);
    }

    @Test
    public void test_turtle_eval_bad_02() {
        test_parsing("/turtle/turtle-eval-bad-02.ttl", false);
    }

    @Test
    public void test_turtle_eval_bad_03() {
        test_parsing("/turtle/turtle-eval-bad-03.ttl", false);
    }

    @Test
    public void test_turtle_eval_bad_04() {
        test_parsing("/turtle/turtle-eval-bad-04.ttl", false);
    }

    @Test
    public void test_turtle_eval_struct_01() {
        test_parsing("/turtle/turtle-eval-struct-01.ttl", true);
    }

    @Test
    public void test_turtle_eval_struct_02() {
        test_parsing("/turtle/turtle-eval-struct-02.ttl", true);
    }

    @Test
    public void test_turtle_subm_01() {
        test_parsing("/turtle/turtle-subm-01.ttl", true);
    }

    @Test
    public void test_turtle_subm_02() {
        test_parsing("/turtle/turtle-subm-02.ttl", true);
    }

    @Test
    public void test_turtle_subm_03() {
        test_parsing("/turtle/turtle-subm-03.ttl", true);
    }

    @Test
    public void test_turtle_subm_04() {
        test_parsing("/turtle/turtle-subm-04.ttl", true);
    }

    @Test
    public void test_turtle_subm_05() {
        test_parsing("/turtle/turtle-subm-05.ttl", true);
    }

    @Test
    public void test_turtle_subm_06() {
        test_parsing("/turtle/turtle-subm-06.ttl", true);
    }

    @Test
    public void test_turtle_subm_07() {
        test_parsing("/turtle/turtle-subm-07.ttl", true);
    }

    @Test
    public void test_turtle_subm_08() {
        test_parsing("/turtle/turtle-subm-08.ttl", true);
    }

    @Test
    public void test_turtle_subm_09() {
        test_parsing("/turtle/turtle-subm-09.ttl", true);
    }

    @Test
    public void test_turtle_subm_10() {
        test_parsing("/turtle/turtle-subm-10.ttl", true);
    }

    @Test
    public void test_turtle_subm_11() {
        test_parsing("/turtle/turtle-subm-11.ttl", true);
    }

    @Test
    public void test_turtle_subm_12() {
        test_parsing("/turtle/turtle-subm-12.ttl", true);
    }

    @Test
    public void test_turtle_subm_13() {
        test_parsing("/turtle/turtle-subm-13.ttl", true);
    }

    @Test
    public void test_turtle_subm_14() {
        test_parsing("/turtle/turtle-subm-14.ttl", true);
    }

    @Test
    public void test_turtle_subm_15() {
        test_parsing("/turtle/turtle-subm-15.ttl", true);
    }

    @Test
    public void test_turtle_subm_16() {
        test_parsing("/turtle/turtle-subm-16.ttl", true);
    }

    @Test
    public void test_turtle_subm_17() {
        test_parsing("/turtle/turtle-subm-17.ttl", true);
    }

    @Test
    public void test_turtle_subm_18() {
        test_parsing("/turtle/turtle-subm-18.ttl", true);
    }

    @Test
    public void test_turtle_subm_19() {
        test_parsing("/turtle/turtle-subm-19.ttl", true);
    }

    @Test
    public void test_turtle_subm_20() {
        test_parsing("/turtle/turtle-subm-20.ttl", true);
    }

    @Test
    public void test_turtle_subm_21() {
        test_parsing("/turtle/turtle-subm-21.ttl", true);
    }

    @Test
    public void test_turtle_subm_22() {
        test_parsing("/turtle/turtle-subm-22.ttl", true);
    }

    @Test
    public void test_turtle_subm_23() {
        test_parsing("/turtle/turtle-subm-23.ttl", true);
    }

    @Test
    public void test_turtle_subm_24() {
        test_parsing("/turtle/turtle-subm-24.ttl", true);
    }

    @Test
    public void test_turtle_subm_25() {
        test_parsing("/turtle/turtle-subm-25.ttl", true);
    }

    @Test
    public void test_turtle_subm_26() {
        test_parsing("/turtle/turtle-subm-26.ttl", true);
    }

    @Test
    public void test_turtle_subm_27() {
        test_parsing("/turtle/turtle-subm-27.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bad_LITERAL2_with_langtag_and_datatype() {
        test_parsing("/turtle/turtle-syntax-bad-LITERAL2_with_langtag_and_datatype.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_base_01() {
        test_parsing("/turtle/turtle-syntax-bad-base-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_base_02() {
        test_parsing("/turtle/turtle-syntax-bad-base-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_base_03() {
        test_parsing("/turtle/turtle-syntax-bad-base-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_blank_label_dot_end() {
        test_parsing("/turtle/turtle-syntax-bad-blank-label-dot-end.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_esc_01() {
        test_parsing("/turtle/turtle-syntax-bad-esc-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_esc_02() {
        test_parsing("/turtle/turtle-syntax-bad-esc-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_esc_03() {
        test_parsing("/turtle/turtle-syntax-bad-esc-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_esc_04() {
        test_parsing("/turtle/turtle-syntax-bad-esc-04.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_kw_01() {
        test_parsing("/turtle/turtle-syntax-bad-kw-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_kw_02() {
        test_parsing("/turtle/turtle-syntax-bad-kw-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_kw_03() {
        test_parsing("/turtle/turtle-syntax-bad-kw-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_kw_04() {
        test_parsing("/turtle/turtle-syntax-bad-kw-04.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_kw_05() {
        test_parsing("/turtle/turtle-syntax-bad-kw-05.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_lang_01() {
        test_parsing("/turtle/turtle-syntax-bad-lang-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_ln_dash_start() {
        test_parsing("/turtle/turtle-syntax-bad-ln-dash-start.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_ln_escape_start() {
        test_parsing("/turtle/turtle-syntax-bad-ln-escape-start.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_ln_escape() {
        test_parsing("/turtle/turtle-syntax-bad-ln-escape.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_missing_ns_dot_end() {
        test_parsing("/turtle/turtle-syntax-bad-missing-ns-dot-end.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_missing_ns_dot_start() {
        test_parsing("/turtle/turtle-syntax-bad-missing-ns-dot-start.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_01() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_02() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_03() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_04() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-04.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_05() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-05.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_06() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-06.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_07() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-07.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_08() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-08.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_09() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-09.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_10() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-10.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_11() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-11.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_12() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-12.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_13() {
        test_parsing("/turtle/turtle-syntax-bad-n3-extras-13.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_ns_dot_end() {
        test_parsing("/turtle/turtle-syntax-bad-ns-dot-end.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_ns_dot_start() {
        test_parsing("/turtle/turtle-syntax-bad-ns-dot-start.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_num_01() {
        test_parsing("/turtle/turtle-syntax-bad-num-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_num_02() {
        test_parsing("/turtle/turtle-syntax-bad-num-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_num_03() {
        test_parsing("/turtle/turtle-syntax-bad-num-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_num_04() {
        test_parsing("/turtle/turtle-syntax-bad-num-04.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_num_05() {
        test_parsing("/turtle/turtle-syntax-bad-num-05.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_number_dot_in_anon() {
        test_parsing("/turtle/turtle-syntax-bad-number-dot-in-anon.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_pname_01() {
        test_parsing("/turtle/turtle-syntax-bad-pname-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_pname_02() {
        test_parsing("/turtle/turtle-syntax-bad-pname-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_pname_03() {
        test_parsing("/turtle/turtle-syntax-bad-pname-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_prefix_01() {
        test_parsing("/turtle/turtle-syntax-bad-prefix-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_prefix_02() {
        test_parsing("/turtle/turtle-syntax-bad-prefix-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_prefix_03() {
        test_parsing("/turtle/turtle-syntax-bad-prefix-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_prefix_04() {
        test_parsing("/turtle/turtle-syntax-bad-prefix-04.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_prefix_05() {
        test_parsing("/turtle/turtle-syntax-bad-prefix-05.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_string_01() {
        test_parsing("/turtle/turtle-syntax-bad-string-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_string_02() {
        test_parsing("/turtle/turtle-syntax-bad-string-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_string_03() {
        test_parsing("/turtle/turtle-syntax-bad-string-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_string_04() {
        test_parsing("/turtle/turtle-syntax-bad-string-04.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_string_05() {
        test_parsing("/turtle/turtle-syntax-bad-string-05.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_string_06() {
        test_parsing("/turtle/turtle-syntax-bad-string-06.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_string_07() {
        test_parsing("/turtle/turtle-syntax-bad-string-07.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_01() {
        test_parsing("/turtle/turtle-syntax-bad-struct-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_02() {
        test_parsing("/turtle/turtle-syntax-bad-struct-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_03() {
        test_parsing("/turtle/turtle-syntax-bad-struct-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_04() {
        test_parsing("/turtle/turtle-syntax-bad-struct-04.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_05() {
        test_parsing("/turtle/turtle-syntax-bad-struct-05.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_06() {
        test_parsing("/turtle/turtle-syntax-bad-struct-06.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_07() {
        test_parsing("/turtle/turtle-syntax-bad-struct-07.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_08() {
        test_parsing("/turtle/turtle-syntax-bad-struct-08.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_09() {
        test_parsing("/turtle/turtle-syntax-bad-struct-09.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_10() {
        test_parsing("/turtle/turtle-syntax-bad-struct-10.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_11() {
        test_parsing("/turtle/turtle-syntax-bad-struct-11.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_12() {
        test_parsing("/turtle/turtle-syntax-bad-struct-12.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_13() {
        test_parsing("/turtle/turtle-syntax-bad-struct-13.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_14() {
        test_parsing("/turtle/turtle-syntax-bad-struct-14.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_15() {
        test_parsing("/turtle/turtle-syntax-bad-struct-15.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_16() {
        test_parsing("/turtle/turtle-syntax-bad-struct-16.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_struct_17() {
        test_parsing("/turtle/turtle-syntax-bad-struct-17.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_uri_01() {
        test_parsing("/turtle/turtle-syntax-bad-uri-01.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_uri_02() {
        test_parsing("/turtle/turtle-syntax-bad-uri-02.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_uri_03() {
        test_parsing("/turtle/turtle-syntax-bad-uri-03.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_uri_04() {
        test_parsing("/turtle/turtle-syntax-bad-uri-04.ttl", false);
    }

    @Test
    public void test_turtle_syntax_bad_uri_05() {
        test_parsing("/turtle/turtle-syntax-bad-uri-05.ttl", false);
    }

    @Test
    public void test_turtle_syntax_base_01() {
        test_parsing("/turtle/turtle-syntax-base-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_base_02() {
        test_parsing("/turtle/turtle-syntax-base-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_base_03() {
        test_parsing("/turtle/turtle-syntax-base-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_base_04() {
        test_parsing("/turtle/turtle-syntax-base-04.ttl", true);
    }

    @Test
    public void test_turtle_syntax_blank_label() {
        test_parsing("/turtle/turtle-syntax-blank-label.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_01() {
        test_parsing("/turtle/turtle-syntax-bnode-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_02() {
        test_parsing("/turtle/turtle-syntax-bnode-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_03() {
        test_parsing("/turtle/turtle-syntax-bnode-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_04() {
        test_parsing("/turtle/turtle-syntax-bnode-04.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_05() {
        test_parsing("/turtle/turtle-syntax-bnode-05.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_06() {
        test_parsing("/turtle/turtle-syntax-bnode-06.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_07() {
        test_parsing("/turtle/turtle-syntax-bnode-07.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_08() {
        test_parsing("/turtle/turtle-syntax-bnode-08.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_09() {
        test_parsing("/turtle/turtle-syntax-bnode-09.ttl", true);
    }

    @Test
    public void test_turtle_syntax_bnode_10() {
        test_parsing("/turtle/turtle-syntax-bnode-10.ttl", true);
    }

    @Test
    public void test_turtle_syntax_datatypes_01() {
        test_parsing("/turtle/turtle-syntax-datatypes-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_datatypes_02() {
        test_parsing("/turtle/turtle-syntax-datatypes-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_file_01() {
        test_parsing("/turtle/turtle-syntax-file-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_file_02() {
        test_parsing("/turtle/turtle-syntax-file-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_file_03() {
        test_parsing("/turtle/turtle-syntax-file-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_kw_01() {
        test_parsing("/turtle/turtle-syntax-kw-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_kw_02() {
        test_parsing("/turtle/turtle-syntax-kw-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_kw_03() {
        test_parsing("/turtle/turtle-syntax-kw-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_lists_01() {
        test_parsing("/turtle/turtle-syntax-lists-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_lists_02() {
        test_parsing("/turtle/turtle-syntax-lists-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_lists_03() {
        test_parsing("/turtle/turtle-syntax-lists-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_lists_04() {
        test_parsing("/turtle/turtle-syntax-lists-04.ttl", true);
    }

    @Test
    public void test_turtle_syntax_lists_05() {
        test_parsing("/turtle/turtle-syntax-lists-05.ttl", true);
    }

    @Test
    public void test_turtle_syntax_ln_colons() {
        test_parsing("/turtle/turtle-syntax-ln-colons.ttl", true);
    }

    @Test
    public void test_turtle_syntax_ln_dots() {
        test_parsing("/turtle/turtle-syntax-ln-dots.ttl", true);
    }

    @Test
    public void test_turtle_syntax_ns_dots() {
        test_parsing("/turtle/turtle-syntax-ns-dots.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_01() {
        test_parsing("/turtle/turtle-syntax-number-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_02() {
        test_parsing("/turtle/turtle-syntax-number-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_03() {
        test_parsing("/turtle/turtle-syntax-number-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_04() {
        test_parsing("/turtle/turtle-syntax-number-04.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_05() {
        test_parsing("/turtle/turtle-syntax-number-05.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_06() {
        test_parsing("/turtle/turtle-syntax-number-06.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_07() {
        test_parsing("/turtle/turtle-syntax-number-07.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_08() {
        test_parsing("/turtle/turtle-syntax-number-08.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_09() {
        test_parsing("/turtle/turtle-syntax-number-09.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_10() {
        test_parsing("/turtle/turtle-syntax-number-10.ttl", true);
    }

    @Test
    public void test_turtle_syntax_number_11() {
        test_parsing("/turtle/turtle-syntax-number-11.ttl", true);
    }

    @Test
    public void test_turtle_syntax_pname_esc_01() {
        test_parsing("/turtle/turtle-syntax-pname-esc-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_pname_esc_02() {
        test_parsing("/turtle/turtle-syntax-pname-esc-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_pname_esc_03() {
        test_parsing("/turtle/turtle-syntax-pname-esc-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_01() {
        test_parsing("/turtle/turtle-syntax-prefix-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_02() {
        test_parsing("/turtle/turtle-syntax-prefix-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_03() {
        test_parsing("/turtle/turtle-syntax-prefix-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_04() {
        test_parsing("/turtle/turtle-syntax-prefix-04.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_05() {
        test_parsing("/turtle/turtle-syntax-prefix-05.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_06() {
        test_parsing("/turtle/turtle-syntax-prefix-06.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_07() {
        test_parsing("/turtle/turtle-syntax-prefix-07.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_08() {
        test_parsing("/turtle/turtle-syntax-prefix-08.ttl", true);
    }

    @Test
    public void test_turtle_syntax_prefix_09() {
        test_parsing("/turtle/turtle-syntax-prefix-09.ttl", true);
    }

    @Test
    public void test_turtle_syntax_str_esc_01() {
        test_parsing("/turtle/turtle-syntax-str-esc-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_str_esc_02() {
        test_parsing("/turtle/turtle-syntax-str-esc-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_str_esc_03() {
        test_parsing("/turtle/turtle-syntax-str-esc-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_01() {
        test_parsing("/turtle/turtle-syntax-string-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_02() {
        test_parsing("/turtle/turtle-syntax-string-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_03() {
        test_parsing("/turtle/turtle-syntax-string-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_04() {
        test_parsing("/turtle/turtle-syntax-string-04.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_05() {
        test_parsing("/turtle/turtle-syntax-string-05.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_06() {
        test_parsing("/turtle/turtle-syntax-string-06.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_07() {
        test_parsing("/turtle/turtle-syntax-string-07.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_08() {
        test_parsing("/turtle/turtle-syntax-string-08.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_09() {
        test_parsing("/turtle/turtle-syntax-string-09.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_10() {
        test_parsing("/turtle/turtle-syntax-string-10.ttl", true);
    }

    @Test
    public void test_turtle_syntax_string_11() {
        test_parsing("/turtle/turtle-syntax-string-11.ttl", true);
    }

    @Test
    public void test_turtle_syntax_struct_01() {
        test_parsing("/turtle/turtle-syntax-struct-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_struct_02() {
        test_parsing("/turtle/turtle-syntax-struct-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_struct_03() {
        test_parsing("/turtle/turtle-syntax-struct-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_struct_04() {
        test_parsing("/turtle/turtle-syntax-struct-04.ttl", true);
    }

    @Test
    public void test_turtle_syntax_struct_05() {
        test_parsing("/turtle/turtle-syntax-struct-05.ttl", true);
    }

    @Test
    public void test_turtle_syntax_uri_01() {
        test_parsing("/turtle/turtle-syntax-uri-01.ttl", true);
    }

    @Test
    public void test_turtle_syntax_uri_02() {
        test_parsing("/turtle/turtle-syntax-uri-02.ttl", true);
    }

    @Test
    public void test_turtle_syntax_uri_03() {
        test_parsing("/turtle/turtle-syntax-uri-03.ttl", true);
    }

    @Test
    public void test_turtle_syntax_uri_04() {
        test_parsing("/turtle/turtle-syntax-uri-04.ttl", true);
    }

    @Test
    public void test_two_LITERAL_LONG2s() {
        test_parsing("/turtle/two_LITERAL_LONG2s.ttl", true);
    }

    @Test
    public void test_underscore_in_localName() {
        test_parsing("/turtle/underscore_in_localName.ttl", true);
    }
}
