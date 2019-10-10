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
package org.xowl.infra.store.loaders;

import org.junit.Test;

/**
 * All Turtle loading tests
 *
 * @author Laurent Wouters
 */
public class TurtleTest extends BaseTurtleTest {
    @Test
    public void test_IRI_subject() throws Exception {
        testTurtleEval("IRI_subject.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_IRI_with_four_digit_numeric_escape() throws Exception {
        testTurtleEval("IRI_with_four_digit_numeric_escape.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_IRI_with_eight_digit_numeric_escape() throws Exception {
        testTurtleEval("IRI_with_eight_digit_numeric_escape.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_IRI_with_all_punctuation() throws Exception {
        testTurtleEval("IRI_with_all_punctuation.ttl", "IRI_with_all_punctuation.nt");
    }

    @Test
    public void test_bareword_a_predicate() throws Exception {
        testTurtleEval("bareword_a_predicate.ttl", "bareword_a_predicate.nt");
    }

    @Test
    public void test_old_style_prefix() throws Exception {
        testTurtleEval("old_style_prefix.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_SPARQL_style_prefix() throws Exception {
        testTurtleEval("SPARQL_style_prefix.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefixed_IRI_predicate() throws Exception {
        testTurtleEval("prefixed_IRI_predicate.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefixed_IRI_object() throws Exception {
        testTurtleEval("prefixed_IRI_object.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefix_only_IRI() throws Exception {
        testTurtleEval("prefix_only_IRI.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefix_with_PN_CHARS_BASE_character_boundaries() throws Exception {
        testTurtleEval("prefix_with_PN_CHARS_BASE_character_boundaries.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefix_with_non_leading_extras() throws Exception {
        testTurtleEval("prefix_with_non_leading_extras.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_default_namespace_IRI() throws Exception {
        testTurtleEval("default_namespace_IRI.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefix_reassigned_and_used() throws Exception {
        testTurtleEval("prefix_reassigned_and_used.ttl", "prefix_reassigned_and_used.nt");
    }

    @Test
    public void test_reserved_escaped_localName() throws Exception {
        testTurtleEval("reserved_escaped_localName.ttl", "reserved_escaped_localName.nt");
    }

    @Test
    public void test_percent_escaped_localName() throws Exception {
        testTurtleEval("percent_escaped_localName.ttl", "percent_escaped_localName.nt");
    }

    @Test
    public void test_HYPHEN_MINUS_in_localName() throws Exception {
        testTurtleEval("HYPHEN_MINUS_in_localName.ttl", "HYPHEN_MINUS_in_localName.nt");
    }

    @Test
    public void test_underscore_in_localName() throws Exception {
        testTurtleEval("underscore_in_localName.ttl", "underscore_in_localName.nt");
    }

    @Test
    public void test_localname_with_COLON() throws Exception {
        testTurtleEval("localname_with_COLON.ttl", "localname_with_COLON.nt");
    }

    @Test
    public void test_localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries() throws Exception {
        testTurtleEval("localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries.ttl", "localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries.nt");
    }

    @Test
    public void test_localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries() throws Exception {
        testTurtleEval("localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries.ttl", "localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries.nt");
    }

    @Test
    public void test_localName_with_nfc_PN_CHARS_BASE_character_boundaries() throws Exception {
        testTurtleEval("localName_with_nfc_PN_CHARS_BASE_character_boundaries.ttl", "localName_with_nfc_PN_CHARS_BASE_character_boundaries.nt");
    }

    @Test
    public void test_localName_with_leading_underscore() throws Exception {
        testTurtleEval("localName_with_leading_underscore.ttl", "localName_with_leading_underscore.nt");
    }

    @Test
    public void test_localName_with_leading_digit() throws Exception {
        testTurtleEval("localName_with_leading_digit.ttl", "localName_with_leading_digit.nt");
    }

    @Test
    public void test_localName_with_non_leading_extras() throws Exception {
        testTurtleEval("localName_with_non_leading_extras.ttl", "localName_with_non_leading_extras.nt");
    }

    @Test
    public void test_old_style_base() throws Exception {
        testTurtleEval("old_style_base.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_SPARQL_style_base() throws Exception {
        testTurtleEval("SPARQL_style_base.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_labeled_blank_node_subject() throws Exception {
        testTurtleEval("labeled_blank_node_subject.ttl", "labeled_blank_node_subject.nt");
    }

    @Test
    public void test_labeled_blank_node_object() throws Exception {
        testTurtleEval("labeled_blank_node_object.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_labeled_blank_node_with_PN_CHARS_BASE_character_boundaries() throws Exception {
        testTurtleEval("labeled_blank_node_with_PN_CHARS_BASE_character_boundaries.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_labeled_blank_node_with_leading_underscore() throws Exception {
        testTurtleEval("labeled_blank_node_with_leading_underscore.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_labeled_blank_node_with_leading_digit() throws Exception {
        testTurtleEval("labeled_blank_node_with_leading_digit.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_labeled_blank_node_with_non_leading_extras() throws Exception {
        testTurtleEval("labeled_blank_node_with_non_leading_extras.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_anonymous_blank_node_subject() throws Exception {
        testTurtleEval("anonymous_blank_node_subject.ttl", "labeled_blank_node_subject.nt");
    }

    @Test
    public void test_anonymous_blank_node_object() throws Exception {
        testTurtleEval("anonymous_blank_node_object.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_sole_blankNodePropertyList() throws Exception {
        testTurtleEval("sole_blankNodePropertyList.ttl", "labeled_blank_node_subject.nt");
    }

    @Test
    public void test_blankNodePropertyList_as_subject() throws Exception {
        testTurtleEval("blankNodePropertyList_as_subject.ttl", "blankNodePropertyList_as_subject.nt");
    }

    @Test
    public void test_blankNodePropertyList_as_object() throws Exception {
        testTurtleEval("blankNodePropertyList_as_object.ttl", "blankNodePropertyList_as_object.nt");
    }

    @Test
    public void test_blankNodePropertyList_with_multiple_triples() throws Exception {
        testTurtleEval("blankNodePropertyList_with_multiple_triples.ttl", "blankNodePropertyList_with_multiple_triples.nt");
    }

    @Test
    public void test_nested_blankNodePropertyLists() throws Exception {
        testTurtleEval("nested_blankNodePropertyLists.ttl", "nested_blankNodePropertyLists.nt");
    }

    @Test
    public void test_blankNodePropertyList_containing_collection() throws Exception {
        testTurtleEval("blankNodePropertyList_containing_collection.ttl", "blankNodePropertyList_containing_collection.nt");
    }

    @Test
    public void test_collection_subject() throws Exception {
        testTurtleEval("collection_subject.ttl", "collection_subject.nt");
    }

    @Test
    public void test_collection_object() throws Exception {
        testTurtleEval("collection_object.ttl", "collection_object.nt");
    }

    @Test
    public void test_empty_collection() throws Exception {
        testTurtleEval("empty_collection.ttl", "empty_collection.nt");
    }

    @Test
    public void test_nested_collection() throws Exception {
        testTurtleEval("nested_collection.ttl", "nested_collection.nt");
    }

    @Test
    public void test_first() throws Exception {
        testTurtleEval("first.ttl", "first.nt");
    }

    @Test
    public void test_last() throws Exception {
        testTurtleEval("last.ttl", "last.nt");
    }

    @Test
    public void test_LITERAL1() throws Exception {
        testTurtleEval("LITERAL1.ttl", "LITERAL1.nt");
    }

    @Test
    public void test_LITERAL1_ascii_boundaries() throws Exception {
        testTurtleEval("LITERAL1_ascii_boundaries.ttl", "LITERAL1_ascii_boundaries.nt");
    }

    @Test
    public void test_LITERAL1_with_UTF8_boundaries() throws Exception {
        testTurtleEval("LITERAL1_with_UTF8_boundaries.ttl", "LITERAL_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_LITERAL1_all_controls() throws Exception {
        testTurtleEval("LITERAL1_all_controls.ttl", "LITERAL1_all_controls.nt");
    }

    @Test
    public void test_LITERAL1_all_punctuation() throws Exception {
        testTurtleEval("LITERAL1_all_punctuation.ttl", "LITERAL1_all_punctuation.nt");
    }

    @Test
    public void test_LITERAL_LONG1() throws Exception {
        testTurtleEval("LITERAL_LONG1.ttl", "LITERAL1.nt");
    }

    @Test
    public void test_LITERAL_LONG1_ascii_boundaries() throws Exception {
        testTurtleEval("LITERAL_LONG1_ascii_boundaries.ttl", "LITERAL_LONG1_ascii_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG1_with_UTF8_boundaries() throws Exception {
        testTurtleEval("LITERAL_LONG1_with_UTF8_boundaries.ttl", "LITERAL_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG1_with_1_squote() throws Exception {
        testTurtleEval("LITERAL_LONG1_with_1_squote.ttl", "LITERAL_LONG1_with_1_squote.nt");
    }

    @Test
    public void test_LITERAL_LONG1_with_2_squotes() throws Exception {
        testTurtleEval("LITERAL_LONG1_with_2_squotes.ttl", "LITERAL_LONG1_with_2_squotes.nt");
    }

    @Test
    public void test_LITERAL2() throws Exception {
        testTurtleEval("LITERAL2.ttl", "LITERAL1.nt");
    }

    @Test
    public void test_LITERAL2_ascii_boundaries() throws Exception {
        testTurtleEval("LITERAL2_ascii_boundaries.ttl", "LITERAL2_ascii_boundaries.nt");
    }

    @Test
    public void test_LITERAL2_with_UTF8_boundaries() throws Exception {
        testTurtleEval("LITERAL2_with_UTF8_boundaries.ttl", "LITERAL_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG2() throws Exception {
        testTurtleEval("LITERAL_LONG2.ttl", "LITERAL1.nt");
    }

    @Test
    public void test_LITERAL_LONG2_ascii_boundaries() throws Exception {
        testTurtleEval("LITERAL_LONG2_ascii_boundaries.ttl", "LITERAL_LONG2_ascii_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG2_with_UTF8_boundaries() throws Exception {
        testTurtleEval("LITERAL_LONG2_with_UTF8_boundaries.ttl", "LITERAL_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG2_with_1_squote() throws Exception {
        testTurtleEval("LITERAL_LONG2_with_1_squote.ttl", "LITERAL_LONG2_with_1_squote.nt");
    }

    @Test
    public void test_LITERAL_LONG2_with_2_squotes() throws Exception {
        testTurtleEval("LITERAL_LONG2_with_2_squotes.ttl", "LITERAL_LONG2_with_2_squotes.nt");
    }

    @Test
    public void test_literal_with_CHARACTER_TABULATION() throws Exception {
        testTurtleEval("literal_with_CHARACTER_TABULATION.ttl", "literal_with_CHARACTER_TABULATION.nt");
    }

    @Test
    public void test_literal_with_BACKSPACE() throws Exception {
        testTurtleEval("literal_with_BACKSPACE.ttl", "literal_with_BACKSPACE.nt");
    }

    @Test
    public void test_literal_with_LINE_FEED() throws Exception {
        testTurtleEval("literal_with_LINE_FEED.ttl", "literal_with_LINE_FEED.nt");
    }

    @Test
    public void test_literal_with_CARRIAGE_RETURN() throws Exception {
        testTurtleEval("literal_with_CARRIAGE_RETURN.ttl", "literal_with_CARRIAGE_RETURN.nt");
    }

    @Test
    public void test_literal_with_FORM_FEED() throws Exception {
        testTurtleEval("literal_with_FORM_FEED.ttl", "literal_with_FORM_FEED.nt");
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS() throws Exception {
        testTurtleEval("literal_with_REVERSE_SOLIDUS.ttl", "literal_with_REVERSE_SOLIDUS.nt");
    }

    @Test
    public void test_literal_with_escaped_CHARACTER_TABULATION() throws Exception {
        testTurtleEval("literal_with_escaped_CHARACTER_TABULATION.ttl", "literal_with_CHARACTER_TABULATION.nt");
    }

    @Test
    public void test_literal_with_escaped_BACKSPACE() throws Exception {
        testTurtleEval("literal_with_escaped_BACKSPACE.ttl", "literal_with_BACKSPACE.nt");
    }

    @Test
    public void test_literal_with_escaped_LINE_FEED() throws Exception {
        testTurtleEval("literal_with_escaped_LINE_FEED.ttl", "literal_with_LINE_FEED.nt");
    }

    @Test
    public void test_literal_with_escaped_CARRIAGE_RETURN() throws Exception {
        testTurtleEval("literal_with_escaped_CARRIAGE_RETURN.ttl", "literal_with_CARRIAGE_RETURN.nt");
    }

    @Test
    public void test_literal_with_escaped_FORM_FEED() throws Exception {
        testTurtleEval("literal_with_escaped_FORM_FEED.ttl", "literal_with_FORM_FEED.nt");
    }

    @Test
    public void test_literal_with_numeric_escape4() throws Exception {
        testTurtleEval("literal_with_numeric_escape4.ttl", "literal_with_numeric_escape4.nt");
    }

    @Test
    public void test_literal_with_numeric_escape8() throws Exception {
        testTurtleEval("literal_with_numeric_escape8.ttl", "literal_with_numeric_escape4.nt");
    }

    @Test
    public void test_IRIREF_datatype() throws Exception {
        testTurtleEval("IRIREF_datatype.ttl", "IRIREF_datatype.nt");
    }

    @Test
    public void test_prefixed_name_datatype() throws Exception {
        testTurtleEval("prefixed_name_datatype.ttl", "IRIREF_datatype.nt");
    }

    @Test
    public void test_bareword_integer() throws Exception {
        testTurtleEval("bareword_integer.ttl", "IRIREF_datatype.nt");
    }

    @Test
    public void test_bareword_decimal() throws Exception {
        testTurtleEval("bareword_decimal.ttl", "bareword_decimal.nt");
    }

    @Test
    public void test_bareword_double() throws Exception {
        testTurtleEval("bareword_double.ttl", "bareword_double.nt");
    }

    @Test
    public void test_double_lower_case_e() throws Exception {
        testTurtleEval("double_lower_case_e.ttl", "double_lower_case_e.nt");
    }

    @Test
    public void test_negative_numeric() throws Exception {
        testTurtleEval("negative_numeric.ttl", "negative_numeric.nt");
    }

    @Test
    public void test_positive_numeric() throws Exception {
        testTurtleEval("positive_numeric.ttl", "positive_numeric.nt");
    }

    @Test
    public void test_numeric_with_leading_0() throws Exception {
        testTurtleEval("numeric_with_leading_0.ttl", "numeric_with_leading_0.nt");
    }

    @Test
    public void test_literal_true() throws Exception {
        testTurtleEval("literal_true.ttl", "literal_true.nt");
    }

    @Test
    public void test_literal_false() throws Exception {
        testTurtleEval("literal_false.ttl", "literal_false.nt");
    }

    @Test
    public void test_langtagged_non_LONG() throws Exception {
        testTurtleEval("langtagged_non_LONG.ttl", "langtagged_non_LONG.nt");
    }

    @Test
    public void test_langtagged_LONG() throws Exception {
        testTurtleEval("langtagged_LONG.ttl", "langtagged_non_LONG.nt");
    }

    @Test
    public void test_lantag_with_subtag() throws Exception {
        testTurtleEval("lantag_with_subtag.ttl", "lantag_with_subtag.nt");
    }

    @Test
    public void test_objectList_with_two_objects() throws Exception {
        testTurtleEval("objectList_with_two_objects.ttl", "objectList_with_two_objects.nt");
    }

    @Test
    public void test_predicateObjectList_with_two_objectLists() throws Exception {
        testTurtleEval("predicateObjectList_with_two_objectLists.ttl", "predicateObjectList_with_two_objectLists.nt");
    }

    @Test
    public void test_repeated_semis_at_end() throws Exception {
        testTurtleEval("repeated_semis_at_end.ttl", "predicateObjectList_with_two_objectLists.nt");
    }

    @Test
    public void test_repeated_semis_not_at_end() throws Exception {
        testTurtleEval("repeated_semis_not_at_end.ttl", "repeated_semis_not_at_end.nt");
    }

    @Test
    public void test_comment_following_localName() throws Exception {
        testTurtleEval("comment_following_localName.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_number_sign_following_localName() throws Exception {
        testTurtleEval("number_sign_following_localName.ttl", "number_sign_following_localName.nt");
    }

    @Test
    public void test_comment_following_PNAME_NS() throws Exception {
        testTurtleEval("comment_following_PNAME_NS.ttl", "comment_following_PNAME_NS.nt");
    }

    @Test
    public void test_number_sign_following_PNAME_NS() throws Exception {
        testTurtleEval("number_sign_following_PNAME_NS.ttl", "number_sign_following_PNAME_NS.nt");
    }

    @Test
    public void test_LITERAL_LONG2_with_REVERSE_SOLIDUS() throws Exception {
        testTurtleEval("LITERAL_LONG2_with_REVERSE_SOLIDUS.ttl", "LITERAL_LONG2_with_REVERSE_SOLIDUS.nt");
    }

    @Test
    public void test_turtle_syntax_bad_LITERAL2_with_langtag_and_datatype() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-LITERAL2_with_langtag_and_datatype.ttl");
    }

    @Test
    public void test_two_LITERAL_LONG2s() throws Exception {
        testTurtleEval("two_LITERAL_LONG2s.ttl", "two_LITERAL_LONG2s.nt");
    }

    @Test
    public void test_langtagged_LONG_with_subtag() throws Exception {
        testTurtleEval("langtagged_LONG_with_subtag.ttl", "langtagged_LONG_with_subtag.nt");
    }

    @Test
    public void test_turtle_syntax_file_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-file-01.ttl");
    }

    @Test
    public void test_turtle_syntax_file_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-file-02.ttl");
    }

    @Test
    public void test_turtle_syntax_file_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-file-03.ttl");
    }

    @Test
    public void test_turtle_syntax_uri_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-uri-01.ttl");
    }

    @Test
    public void test_turtle_syntax_uri_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-uri-02.ttl");
    }

    @Test
    public void test_turtle_syntax_uri_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-uri-03.ttl");
    }

    @Test
    public void test_turtle_syntax_uri_04() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-uri-04.ttl");
    }

    @Test
    public void test_turtle_syntax_base_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-base-01.ttl");
    }

    @Test
    public void test_turtle_syntax_base_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-base-02.ttl");
    }

    @Test
    public void test_turtle_syntax_base_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-base-03.ttl");
    }

    @Test
    public void test_turtle_syntax_base_04() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-base-04.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-01.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-02.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-03.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_04() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-04.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_05() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-05.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_06() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-06.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_07() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-07.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_08() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-08.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_09() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-prefix-09.ttl");
    }

    @Test
    public void test_turtle_syntax_string_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-01.ttl");
    }

    @Test
    public void test_turtle_syntax_string_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-02.ttl");
    }

    @Test
    public void test_turtle_syntax_string_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-03.ttl");
    }

    @Test
    public void test_turtle_syntax_string_04() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-04.ttl");
    }

    @Test
    public void test_turtle_syntax_string_05() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-05.ttl");
    }

    @Test
    public void test_turtle_syntax_string_06() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-06.ttl");
    }

    @Test
    public void test_turtle_syntax_string_07() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-07.ttl");
    }

    @Test
    public void test_turtle_syntax_string_08() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-08.ttl");
    }

    @Test
    public void test_turtle_syntax_string_09() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-09.ttl");
    }

    @Test
    public void test_turtle_syntax_string_10() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-10.ttl");
    }

    @Test
    public void test_turtle_syntax_string_11() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-string-11.ttl");
    }

    @Test
    public void test_turtle_syntax_str_esc_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-str-esc-01.ttl");
    }

    @Test
    public void test_turtle_syntax_str_esc_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-str-esc-02.ttl");
    }

    @Test
    public void test_turtle_syntax_str_esc_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-str-esc-03.ttl");
    }

    @Test
    public void test_turtle_syntax_pname_esc_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-pname-esc-01.ttl");
    }

    @Test
    public void test_turtle_syntax_pname_esc_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-pname-esc-02.ttl");
    }

    @Test
    public void test_turtle_syntax_pname_esc_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-pname-esc-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_04() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_05() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_06() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-06.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_07() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-07.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_08() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-08.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_09() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-09.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_10() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-bnode-10.ttl");
    }

    @Test
    public void test_turtle_syntax_number_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-01.ttl");
    }

    @Test
    public void test_turtle_syntax_number_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-02.ttl");
    }

    @Test
    public void test_turtle_syntax_number_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-03.ttl");
    }

    @Test
    public void test_turtle_syntax_number_04() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-04.ttl");
    }

    @Test
    public void test_turtle_syntax_number_05() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-05.ttl");
    }

    @Test
    public void test_turtle_syntax_number_06() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-06.ttl");
    }

    @Test
    public void test_turtle_syntax_number_07() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-07.ttl");
    }

    @Test
    public void test_turtle_syntax_number_08() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-08.ttl");
    }

    @Test
    public void test_turtle_syntax_number_09() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-09.ttl");
    }

    @Test
    public void test_turtle_syntax_number_10() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-10.ttl");
    }

    @Test
    public void test_turtle_syntax_number_11() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-number-11.ttl");
    }

    @Test
    public void test_turtle_syntax_datatypes_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-datatypes-01.ttl");
    }

    @Test
    public void test_turtle_syntax_datatypes_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-datatypes-02.ttl");
    }

    @Test
    public void test_turtle_syntax_kw_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-kw-01.ttl");
    }

    @Test
    public void test_turtle_syntax_kw_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-kw-02.ttl");
    }

    @Test
    public void test_turtle_syntax_kw_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-kw-03.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-struct-01.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-struct-02.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-struct-03.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_04() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-struct-04.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_05() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-struct-05.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_01() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-lists-01.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_02() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-lists-02.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_03() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-lists-03.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_04() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-lists-04.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_05() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-lists-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_04() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_05() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_04() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_05() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_base_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-base-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_base_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-base-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_base_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-base-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_04() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_05() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_06() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-06.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_07() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-07.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_04() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_05() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_04() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_05() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_06() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-06.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_07() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-07.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_08() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-08.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_09() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-09.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_10() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-10.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_11() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-11.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_12() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-12.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_13() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-13.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_08() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-08.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_09() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-09.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_10() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-10.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_11() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-11.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_12() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-12.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_13() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-13.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_14() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-14.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_15() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-15.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_16() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-16.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_17() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-17.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_lang_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-lang-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_esc_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-esc-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_esc_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-esc-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_esc_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-esc-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_esc_04() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-esc-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_pname_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-pname-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_pname_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-pname-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_pname_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-pname-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_04() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_05() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_06() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-06.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_07() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-07.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_01() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_02() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_03() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_04() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_05() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-05.ttl");
    }

    @Test
    public void test_turtle_eval_struct_01() throws Exception {
        testTurtleEval("turtle-eval-struct-01.ttl", "turtle-eval-struct-01.nt");
    }

    @Test
    public void test_turtle_eval_struct_02() throws Exception {
        testTurtleEval("turtle-eval-struct-02.ttl", "turtle-eval-struct-02.nt");
    }

    @Test
    public void test_turtle_subm_01() throws Exception {
        testTurtleEval("turtle-subm-01.ttl", "turtle-subm-01.nt");
    }

    @Test
    public void test_turtle_subm_02() throws Exception {
        testTurtleEval("turtle-subm-02.ttl", "turtle-subm-02.nt");
    }

    @Test
    public void test_turtle_subm_03() throws Exception {
        testTurtleEval("turtle-subm-03.ttl", "turtle-subm-03.nt");
    }

    @Test
    public void test_turtle_subm_04() throws Exception {
        testTurtleEval("turtle-subm-04.ttl", "turtle-subm-04.nt");
    }

    @Test
    public void test_turtle_subm_05() throws Exception {
        testTurtleEval("turtle-subm-05.ttl", "turtle-subm-05.nt");
    }

    @Test
    public void test_turtle_subm_06() throws Exception {
        testTurtleEval("turtle-subm-06.ttl", "turtle-subm-06.nt");
    }

    @Test
    public void test_turtle_subm_07() throws Exception {
        testTurtleEval("turtle-subm-07.ttl", "turtle-subm-07.nt");
    }

    @Test
    public void test_turtle_subm_08() throws Exception {
        testTurtleEval("turtle-subm-08.ttl", "turtle-subm-08.nt");
    }

    @Test
    public void test_turtle_subm_09() throws Exception {
        testTurtleEval("turtle-subm-09.ttl", "turtle-subm-09.nt");
    }

    @Test
    public void test_turtle_subm_10() throws Exception {
        testTurtleEval("turtle-subm-10.ttl", "turtle-subm-10.nt");
    }

    @Test
    public void test_turtle_subm_11() throws Exception {
        testTurtleEval("turtle-subm-11.ttl", "turtle-subm-11.nt");
    }

    @Test
    public void test_turtle_subm_12() throws Exception {
        testTurtleEval("turtle-subm-12.ttl", "turtle-subm-12.nt");
    }

    @Test
    public void test_turtle_subm_13() throws Exception {
        testTurtleEval("turtle-subm-13.ttl", "turtle-subm-13.nt");
    }

    @Test
    public void test_turtle_subm_14() throws Exception {
        testTurtleEval("turtle-subm-14.ttl", "turtle-subm-14.nt");
    }

    @Test
    public void test_turtle_subm_15() throws Exception {
        testTurtleEval("turtle-subm-15.ttl", "turtle-subm-15.nt");
    }

    @Test
    public void test_turtle_subm_16() throws Exception {
        testTurtleEval("turtle-subm-16.ttl", "turtle-subm-16.nt");
    }

    @Test
    public void test_turtle_subm_17() throws Exception {
        testTurtleEval("turtle-subm-17.ttl", "turtle-subm-17.nt");
    }

    @Test
    public void test_turtle_subm_18() throws Exception {
        testTurtleEval("turtle-subm-18.ttl", "turtle-subm-18.nt");
    }

    @Test
    public void test_turtle_subm_19() throws Exception {
        testTurtleEval("turtle-subm-19.ttl", "turtle-subm-19.nt");
    }

    @Test
    public void test_turtle_subm_20() throws Exception {
        testTurtleEval("turtle-subm-20.ttl", "turtle-subm-20.nt");
    }

    @Test
    public void test_turtle_subm_21() throws Exception {
        testTurtleEval("turtle-subm-21.ttl", "turtle-subm-21.nt");
    }

    @Test
    public void test_turtle_subm_22() throws Exception {
        testTurtleEval("turtle-subm-22.ttl", "turtle-subm-22.nt");
    }

    @Test
    public void test_turtle_subm_23() throws Exception {
        testTurtleEval("turtle-subm-23.ttl", "turtle-subm-23.nt");
    }

    @Test
    public void test_turtle_subm_24() throws Exception {
        testTurtleEval("turtle-subm-24.ttl", "turtle-subm-24.nt");
    }

    @Test
    public void test_turtle_subm_25() throws Exception {
        testTurtleEval("turtle-subm-25.ttl", "turtle-subm-25.nt");
    }

    @Test
    public void test_turtle_subm_26() throws Exception {
        testTurtleEval("turtle-subm-26.ttl", "turtle-subm-26.nt");
    }

    @Test
    public void test_turtle_subm_27() throws Exception {
        testTurtleEval("turtle-subm-27.ttl", "turtle-subm-27.nt");
    }

    @Test
    public void test_turtle_eval_bad_01() throws Exception {
        testTurtleNegativeSyntax("turtle-eval-bad-01.ttl");
    }

    @Test
    public void test_turtle_eval_bad_02() throws Exception {
        testTurtleNegativeSyntax("turtle-eval-bad-02.ttl");
    }

    @Test
    public void test_turtle_eval_bad_03() throws Exception {
        testTurtleNegativeSyntax("turtle-eval-bad-03.ttl");
    }

    @Test
    public void test_turtle_eval_bad_04() throws Exception {
        testTurtleNegativeSyntax("turtle-eval-bad-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_blank_label_dot_end() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-blank-label-dot-end.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ln_dash_start() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-ln-dash-start.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ln_escape_start() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-ln-escape-start.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ln_escape() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-ln-escape.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_missing_ns_dot_end() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-missing-ns-dot-end.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_missing_ns_dot_start() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-missing-ns-dot-start.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ns_dot_end() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-ns-dot-end.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ns_dot_start() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-ns-dot-start.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_number_dot_in_anon() throws Exception {
        testTurtleNegativeSyntax("turtle-syntax-bad-number-dot-in-anon.ttl");
    }

    @Test
    public void test_turtle_syntax_blank_label() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-blank-label.ttl");
    }

    @Test
    public void test_turtle_syntax_ln_colons() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-ln-colons.ttl");
    }

    @Test
    public void test_turtle_syntax_ln_dots() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-ln-dots.ttl");
    }

    @Test
    public void test_turtle_syntax_ns_dots() throws Exception {
        testTurtlePositiveSyntax("turtle-syntax-ns-dots.ttl");
    }
}
