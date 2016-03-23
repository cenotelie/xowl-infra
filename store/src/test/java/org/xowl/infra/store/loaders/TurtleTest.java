/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters and others
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
package org.xowl.infra.store.loaders;

import org.junit.Test;

/**
 * All Turtle loading tests
 *
 * @author Laurent Wouters
 */
public class TurtleTest extends BaseTurtleTest {
    @Test
    public void test_IRI_subject() {
        testTurtleEval("IRI_subject.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_IRI_with_four_digit_numeric_escape() {
        testTurtleEval("IRI_with_four_digit_numeric_escape.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_IRI_with_eight_digit_numeric_escape() {
        testTurtleEval("IRI_with_eight_digit_numeric_escape.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_IRI_with_all_punctuation() {
        testTurtleEval("IRI_with_all_punctuation.ttl", "IRI_with_all_punctuation.nt");
    }

    @Test
    public void test_bareword_a_predicate() {
        testTurtleEval("bareword_a_predicate.ttl", "bareword_a_predicate.nt");
    }

    @Test
    public void test_old_style_prefix() {
        testTurtleEval("old_style_prefix.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_SPARQL_style_prefix() {
        testTurtleEval("SPARQL_style_prefix.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefixed_IRI_predicate() {
        testTurtleEval("prefixed_IRI_predicate.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefixed_IRI_object() {
        testTurtleEval("prefixed_IRI_object.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefix_only_IRI() {
        testTurtleEval("prefix_only_IRI.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefix_with_PN_CHARS_BASE_character_boundaries() {
        testTurtleEval("prefix_with_PN_CHARS_BASE_character_boundaries.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefix_with_non_leading_extras() {
        testTurtleEval("prefix_with_non_leading_extras.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_default_namespace_IRI() {
        testTurtleEval("default_namespace_IRI.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_prefix_reassigned_and_used() {
        testTurtleEval("prefix_reassigned_and_used.ttl", "prefix_reassigned_and_used.nt");
    }

    @Test
    public void test_reserved_escaped_localName() {
        testTurtleEval("reserved_escaped_localName.ttl", "reserved_escaped_localName.nt");
    }

    @Test
    public void test_percent_escaped_localName() {
        testTurtleEval("percent_escaped_localName.ttl", "percent_escaped_localName.nt");
    }

    @Test
    public void test_HYPHEN_MINUS_in_localName() {
        testTurtleEval("HYPHEN_MINUS_in_localName.ttl", "HYPHEN_MINUS_in_localName.nt");
    }

    @Test
    public void test_underscore_in_localName() {
        testTurtleEval("underscore_in_localName.ttl", "underscore_in_localName.nt");
    }

    @Test
    public void test_localname_with_COLON() {
        testTurtleEval("localname_with_COLON.ttl", "localname_with_COLON.nt");
    }

    @Test
    public void test_localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries() {
        testTurtleEval("localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries.ttl", "localName_with_assigned_nfc_bmp_PN_CHARS_BASE_character_boundaries.nt");
    }

    @Test
    public void test_localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries() {
        testTurtleEval("localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries.ttl", "localName_with_assigned_nfc_PN_CHARS_BASE_character_boundaries.nt");
    }

    @Test
    public void test_localName_with_nfc_PN_CHARS_BASE_character_boundaries() {
        testTurtleEval("localName_with_nfc_PN_CHARS_BASE_character_boundaries.ttl", "localName_with_nfc_PN_CHARS_BASE_character_boundaries.nt");
    }

    @Test
    public void test_localName_with_leading_underscore() {
        testTurtleEval("localName_with_leading_underscore.ttl", "localName_with_leading_underscore.nt");
    }

    @Test
    public void test_localName_with_leading_digit() {
        testTurtleEval("localName_with_leading_digit.ttl", "localName_with_leading_digit.nt");
    }

    @Test
    public void test_localName_with_non_leading_extras() {
        testTurtleEval("localName_with_non_leading_extras.ttl", "localName_with_non_leading_extras.nt");
    }

    @Test
    public void test_old_style_base() {
        testTurtleEval("old_style_base.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_SPARQL_style_base() {
        testTurtleEval("SPARQL_style_base.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_labeled_blank_node_subject() {
        testTurtleEval("labeled_blank_node_subject.ttl", "labeled_blank_node_subject.nt");
    }

    @Test
    public void test_labeled_blank_node_object() {
        testTurtleEval("labeled_blank_node_object.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_labeled_blank_node_with_PN_CHARS_BASE_character_boundaries() {
        testTurtleEval("labeled_blank_node_with_PN_CHARS_BASE_character_boundaries.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_labeled_blank_node_with_leading_underscore() {
        testTurtleEval("labeled_blank_node_with_leading_underscore.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_labeled_blank_node_with_leading_digit() {
        testTurtleEval("labeled_blank_node_with_leading_digit.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_labeled_blank_node_with_non_leading_extras() {
        testTurtleEval("labeled_blank_node_with_non_leading_extras.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_anonymous_blank_node_subject() {
        testTurtleEval("anonymous_blank_node_subject.ttl", "labeled_blank_node_subject.nt");
    }

    @Test
    public void test_anonymous_blank_node_object() {
        testTurtleEval("anonymous_blank_node_object.ttl", "labeled_blank_node_object.nt");
    }

    @Test
    public void test_sole_blankNodePropertyList() {
        testTurtleEval("sole_blankNodePropertyList.ttl", "labeled_blank_node_subject.nt");
    }

    @Test
    public void test_blankNodePropertyList_as_subject() {
        testTurtleEval("blankNodePropertyList_as_subject.ttl", "blankNodePropertyList_as_subject.nt");
    }

    @Test
    public void test_blankNodePropertyList_as_object() {
        testTurtleEval("blankNodePropertyList_as_object.ttl", "blankNodePropertyList_as_object.nt");
    }

    @Test
    public void test_blankNodePropertyList_with_multiple_triples() {
        testTurtleEval("blankNodePropertyList_with_multiple_triples.ttl", "blankNodePropertyList_with_multiple_triples.nt");
    }

    @Test
    public void test_nested_blankNodePropertyLists() {
        testTurtleEval("nested_blankNodePropertyLists.ttl", "nested_blankNodePropertyLists.nt");
    }

    @Test
    public void test_blankNodePropertyList_containing_collection() {
        testTurtleEval("blankNodePropertyList_containing_collection.ttl", "blankNodePropertyList_containing_collection.nt");
    }

    @Test
    public void test_collection_subject() {
        testTurtleEval("collection_subject.ttl", "collection_subject.nt");
    }

    @Test
    public void test_collection_object() {
        testTurtleEval("collection_object.ttl", "collection_object.nt");
    }

    @Test
    public void test_empty_collection() {
        testTurtleEval("empty_collection.ttl", "empty_collection.nt");
    }

    @Test
    public void test_nested_collection() {
        testTurtleEval("nested_collection.ttl", "nested_collection.nt");
    }

    @Test
    public void test_first() {
        testTurtleEval("first.ttl", "first.nt");
    }

    @Test
    public void test_last() {
        testTurtleEval("last.ttl", "last.nt");
    }

    @Test
    public void test_LITERAL1() {
        testTurtleEval("LITERAL1.ttl", "LITERAL1.nt");
    }

    @Test
    public void test_LITERAL1_ascii_boundaries() {
        testTurtleEval("LITERAL1_ascii_boundaries.ttl", "LITERAL1_ascii_boundaries.nt");
    }

    @Test
    public void test_LITERAL1_with_UTF8_boundaries() {
        testTurtleEval("LITERAL1_with_UTF8_boundaries.ttl", "LITERAL_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_LITERAL1_all_controls() {
        testTurtleEval("LITERAL1_all_controls.ttl", "LITERAL1_all_controls.nt");
    }

    @Test
    public void test_LITERAL1_all_punctuation() {
        testTurtleEval("LITERAL1_all_punctuation.ttl", "LITERAL1_all_punctuation.nt");
    }

    @Test
    public void test_LITERAL_LONG1() {
        testTurtleEval("LITERAL_LONG1.ttl", "LITERAL1.nt");
    }

    @Test
    public void test_LITERAL_LONG1_ascii_boundaries() {
        testTurtleEval("LITERAL_LONG1_ascii_boundaries.ttl", "LITERAL_LONG1_ascii_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG1_with_UTF8_boundaries() {
        testTurtleEval("LITERAL_LONG1_with_UTF8_boundaries.ttl", "LITERAL_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG1_with_1_squote() {
        testTurtleEval("LITERAL_LONG1_with_1_squote.ttl", "LITERAL_LONG1_with_1_squote.nt");
    }

    @Test
    public void test_LITERAL_LONG1_with_2_squotes() {
        testTurtleEval("LITERAL_LONG1_with_2_squotes.ttl", "LITERAL_LONG1_with_2_squotes.nt");
    }

    @Test
    public void test_LITERAL2() {
        testTurtleEval("LITERAL2.ttl", "LITERAL1.nt");
    }

    @Test
    public void test_LITERAL2_ascii_boundaries() {
        testTurtleEval("LITERAL2_ascii_boundaries.ttl", "LITERAL2_ascii_boundaries.nt");
    }

    @Test
    public void test_LITERAL2_with_UTF8_boundaries() {
        testTurtleEval("LITERAL2_with_UTF8_boundaries.ttl", "LITERAL_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG2() {
        testTurtleEval("LITERAL_LONG2.ttl", "LITERAL1.nt");
    }

    @Test
    public void test_LITERAL_LONG2_ascii_boundaries() {
        testTurtleEval("LITERAL_LONG2_ascii_boundaries.ttl", "LITERAL_LONG2_ascii_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG2_with_UTF8_boundaries() {
        testTurtleEval("LITERAL_LONG2_with_UTF8_boundaries.ttl", "LITERAL_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_LITERAL_LONG2_with_1_squote() {
        testTurtleEval("LITERAL_LONG2_with_1_squote.ttl", "LITERAL_LONG2_with_1_squote.nt");
    }

    @Test
    public void test_LITERAL_LONG2_with_2_squotes() {
        testTurtleEval("LITERAL_LONG2_with_2_squotes.ttl", "LITERAL_LONG2_with_2_squotes.nt");
    }

    @Test
    public void test_literal_with_CHARACTER_TABULATION() {
        testTurtleEval("literal_with_CHARACTER_TABULATION.ttl", "literal_with_CHARACTER_TABULATION.nt");
    }

    @Test
    public void test_literal_with_BACKSPACE() {
        testTurtleEval("literal_with_BACKSPACE.ttl", "literal_with_BACKSPACE.nt");
    }

    @Test
    public void test_literal_with_LINE_FEED() {
        testTurtleEval("literal_with_LINE_FEED.ttl", "literal_with_LINE_FEED.nt");
    }

    @Test
    public void test_literal_with_CARRIAGE_RETURN() {
        testTurtleEval("literal_with_CARRIAGE_RETURN.ttl", "literal_with_CARRIAGE_RETURN.nt");
    }

    @Test
    public void test_literal_with_FORM_FEED() {
        testTurtleEval("literal_with_FORM_FEED.ttl", "literal_with_FORM_FEED.nt");
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS() {
        testTurtleEval("literal_with_REVERSE_SOLIDUS.ttl", "literal_with_REVERSE_SOLIDUS.nt");
    }

    @Test
    public void test_literal_with_escaped_CHARACTER_TABULATION() {
        testTurtleEval("literal_with_escaped_CHARACTER_TABULATION.ttl", "literal_with_CHARACTER_TABULATION.nt");
    }

    @Test
    public void test_literal_with_escaped_BACKSPACE() {
        testTurtleEval("literal_with_escaped_BACKSPACE.ttl", "literal_with_BACKSPACE.nt");
    }

    @Test
    public void test_literal_with_escaped_LINE_FEED() {
        testTurtleEval("literal_with_escaped_LINE_FEED.ttl", "literal_with_LINE_FEED.nt");
    }

    @Test
    public void test_literal_with_escaped_CARRIAGE_RETURN() {
        testTurtleEval("literal_with_escaped_CARRIAGE_RETURN.ttl", "literal_with_CARRIAGE_RETURN.nt");
    }

    @Test
    public void test_literal_with_escaped_FORM_FEED() {
        testTurtleEval("literal_with_escaped_FORM_FEED.ttl", "literal_with_FORM_FEED.nt");
    }

    @Test
    public void test_literal_with_numeric_escape4() {
        testTurtleEval("literal_with_numeric_escape4.ttl", "literal_with_numeric_escape4.nt");
    }

    @Test
    public void test_literal_with_numeric_escape8() {
        testTurtleEval("literal_with_numeric_escape8.ttl", "literal_with_numeric_escape4.nt");
    }

    @Test
    public void test_IRIREF_datatype() {
        testTurtleEval("IRIREF_datatype.ttl", "IRIREF_datatype.nt");
    }

    @Test
    public void test_prefixed_name_datatype() {
        testTurtleEval("prefixed_name_datatype.ttl", "IRIREF_datatype.nt");
    }

    @Test
    public void test_bareword_integer() {
        testTurtleEval("bareword_integer.ttl", "IRIREF_datatype.nt");
    }

    @Test
    public void test_bareword_decimal() {
        testTurtleEval("bareword_decimal.ttl", "bareword_decimal.nt");
    }

    @Test
    public void test_bareword_double() {
        testTurtleEval("bareword_double.ttl", "bareword_double.nt");
    }

    @Test
    public void test_double_lower_case_e() {
        testTurtleEval("double_lower_case_e.ttl", "double_lower_case_e.nt");
    }

    @Test
    public void test_negative_numeric() {
        testTurtleEval("negative_numeric.ttl", "negative_numeric.nt");
    }

    @Test
    public void test_positive_numeric() {
        testTurtleEval("positive_numeric.ttl", "positive_numeric.nt");
    }

    @Test
    public void test_numeric_with_leading_0() {
        testTurtleEval("numeric_with_leading_0.ttl", "numeric_with_leading_0.nt");
    }

    @Test
    public void test_literal_true() {
        testTurtleEval("literal_true.ttl", "literal_true.nt");
    }

    @Test
    public void test_literal_false() {
        testTurtleEval("literal_false.ttl", "literal_false.nt");
    }

    @Test
    public void test_langtagged_non_LONG() {
        testTurtleEval("langtagged_non_LONG.ttl", "langtagged_non_LONG.nt");
    }

    @Test
    public void test_langtagged_LONG() {
        testTurtleEval("langtagged_LONG.ttl", "langtagged_non_LONG.nt");
    }

    @Test
    public void test_lantag_with_subtag() {
        testTurtleEval("lantag_with_subtag.ttl", "lantag_with_subtag.nt");
    }

    @Test
    public void test_objectList_with_two_objects() {
        testTurtleEval("objectList_with_two_objects.ttl", "objectList_with_two_objects.nt");
    }

    @Test
    public void test_predicateObjectList_with_two_objectLists() {
        testTurtleEval("predicateObjectList_with_two_objectLists.ttl", "predicateObjectList_with_two_objectLists.nt");
    }

    @Test
    public void test_repeated_semis_at_end() {
        testTurtleEval("repeated_semis_at_end.ttl", "predicateObjectList_with_two_objectLists.nt");
    }

    @Test
    public void test_repeated_semis_not_at_end() {
        testTurtleEval("repeated_semis_not_at_end.ttl", "repeated_semis_not_at_end.nt");
    }

    @Test
    public void test_comment_following_localName() {
        testTurtleEval("comment_following_localName.ttl", "IRI_spo.nt");
    }

    @Test
    public void test_number_sign_following_localName() {
        testTurtleEval("number_sign_following_localName.ttl", "number_sign_following_localName.nt");
    }

    @Test
    public void test_comment_following_PNAME_NS() {
        testTurtleEval("comment_following_PNAME_NS.ttl", "comment_following_PNAME_NS.nt");
    }

    @Test
    public void test_number_sign_following_PNAME_NS() {
        testTurtleEval("number_sign_following_PNAME_NS.ttl", "number_sign_following_PNAME_NS.nt");
    }

    @Test
    public void test_LITERAL_LONG2_with_REVERSE_SOLIDUS() {
        testTurtleEval("LITERAL_LONG2_with_REVERSE_SOLIDUS.ttl", "LITERAL_LONG2_with_REVERSE_SOLIDUS.nt");
    }

    @Test
    public void test_turtle_syntax_bad_LITERAL2_with_langtag_and_datatype() {
        testTurtleNegativeSyntax("turtle-syntax-bad-LITERAL2_with_langtag_and_datatype.ttl");
    }

    @Test
    public void test_two_LITERAL_LONG2s() {
        testTurtleEval("two_LITERAL_LONG2s.ttl", "two_LITERAL_LONG2s.nt");
    }

    @Test
    public void test_langtagged_LONG_with_subtag() {
        testTurtleEval("langtagged_LONG_with_subtag.ttl", "langtagged_LONG_with_subtag.nt");
    }

    @Test
    public void test_turtle_syntax_file_01() {
        testTurtlePositiveSyntax("turtle-syntax-file-01.ttl");
    }

    @Test
    public void test_turtle_syntax_file_02() {
        testTurtlePositiveSyntax("turtle-syntax-file-02.ttl");
    }

    @Test
    public void test_turtle_syntax_file_03() {
        testTurtlePositiveSyntax("turtle-syntax-file-03.ttl");
    }

    @Test
    public void test_turtle_syntax_uri_01() {
        testTurtlePositiveSyntax("turtle-syntax-uri-01.ttl");
    }

    @Test
    public void test_turtle_syntax_uri_02() {
        testTurtlePositiveSyntax("turtle-syntax-uri-02.ttl");
    }

    @Test
    public void test_turtle_syntax_uri_03() {
        testTurtlePositiveSyntax("turtle-syntax-uri-03.ttl");
    }

    @Test
    public void test_turtle_syntax_uri_04() {
        testTurtlePositiveSyntax("turtle-syntax-uri-04.ttl");
    }

    @Test
    public void test_turtle_syntax_base_01() {
        testTurtlePositiveSyntax("turtle-syntax-base-01.ttl");
    }

    @Test
    public void test_turtle_syntax_base_02() {
        testTurtlePositiveSyntax("turtle-syntax-base-02.ttl");
    }

    @Test
    public void test_turtle_syntax_base_03() {
        testTurtlePositiveSyntax("turtle-syntax-base-03.ttl");
    }

    @Test
    public void test_turtle_syntax_base_04() {
        testTurtlePositiveSyntax("turtle-syntax-base-04.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_01() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-01.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_02() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-02.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_03() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-03.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_04() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-04.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_05() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-05.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_06() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-06.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_07() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-07.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_08() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-08.ttl");
    }

    @Test
    public void test_turtle_syntax_prefix_09() {
        testTurtlePositiveSyntax("turtle-syntax-prefix-09.ttl");
    }

    @Test
    public void test_turtle_syntax_string_01() {
        testTurtlePositiveSyntax("turtle-syntax-string-01.ttl");
    }

    @Test
    public void test_turtle_syntax_string_02() {
        testTurtlePositiveSyntax("turtle-syntax-string-02.ttl");
    }

    @Test
    public void test_turtle_syntax_string_03() {
        testTurtlePositiveSyntax("turtle-syntax-string-03.ttl");
    }

    @Test
    public void test_turtle_syntax_string_04() {
        testTurtlePositiveSyntax("turtle-syntax-string-04.ttl");
    }

    @Test
    public void test_turtle_syntax_string_05() {
        testTurtlePositiveSyntax("turtle-syntax-string-05.ttl");
    }

    @Test
    public void test_turtle_syntax_string_06() {
        testTurtlePositiveSyntax("turtle-syntax-string-06.ttl");
    }

    @Test
    public void test_turtle_syntax_string_07() {
        testTurtlePositiveSyntax("turtle-syntax-string-07.ttl");
    }

    @Test
    public void test_turtle_syntax_string_08() {
        testTurtlePositiveSyntax("turtle-syntax-string-08.ttl");
    }

    @Test
    public void test_turtle_syntax_string_09() {
        testTurtlePositiveSyntax("turtle-syntax-string-09.ttl");
    }

    @Test
    public void test_turtle_syntax_string_10() {
        testTurtlePositiveSyntax("turtle-syntax-string-10.ttl");
    }

    @Test
    public void test_turtle_syntax_string_11() {
        testTurtlePositiveSyntax("turtle-syntax-string-11.ttl");
    }

    @Test
    public void test_turtle_syntax_str_esc_01() {
        testTurtlePositiveSyntax("turtle-syntax-str-esc-01.ttl");
    }

    @Test
    public void test_turtle_syntax_str_esc_02() {
        testTurtlePositiveSyntax("turtle-syntax-str-esc-02.ttl");
    }

    @Test
    public void test_turtle_syntax_str_esc_03() {
        testTurtlePositiveSyntax("turtle-syntax-str-esc-03.ttl");
    }

    @Test
    public void test_turtle_syntax_pname_esc_01() {
        testTurtlePositiveSyntax("turtle-syntax-pname-esc-01.ttl");
    }

    @Test
    public void test_turtle_syntax_pname_esc_02() {
        testTurtlePositiveSyntax("turtle-syntax-pname-esc-02.ttl");
    }

    @Test
    public void test_turtle_syntax_pname_esc_03() {
        testTurtlePositiveSyntax("turtle-syntax-pname-esc-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_01() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_02() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_03() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_04() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_05() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_06() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-06.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_07() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-07.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_08() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-08.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_09() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-09.ttl");
    }

    @Test
    public void test_turtle_syntax_bnode_10() {
        testTurtlePositiveSyntax("turtle-syntax-bnode-10.ttl");
    }

    @Test
    public void test_turtle_syntax_number_01() {
        testTurtlePositiveSyntax("turtle-syntax-number-01.ttl");
    }

    @Test
    public void test_turtle_syntax_number_02() {
        testTurtlePositiveSyntax("turtle-syntax-number-02.ttl");
    }

    @Test
    public void test_turtle_syntax_number_03() {
        testTurtlePositiveSyntax("turtle-syntax-number-03.ttl");
    }

    @Test
    public void test_turtle_syntax_number_04() {
        testTurtlePositiveSyntax("turtle-syntax-number-04.ttl");
    }

    @Test
    public void test_turtle_syntax_number_05() {
        testTurtlePositiveSyntax("turtle-syntax-number-05.ttl");
    }

    @Test
    public void test_turtle_syntax_number_06() {
        testTurtlePositiveSyntax("turtle-syntax-number-06.ttl");
    }

    @Test
    public void test_turtle_syntax_number_07() {
        testTurtlePositiveSyntax("turtle-syntax-number-07.ttl");
    }

    @Test
    public void test_turtle_syntax_number_08() {
        testTurtlePositiveSyntax("turtle-syntax-number-08.ttl");
    }

    @Test
    public void test_turtle_syntax_number_09() {
        testTurtlePositiveSyntax("turtle-syntax-number-09.ttl");
    }

    @Test
    public void test_turtle_syntax_number_10() {
        testTurtlePositiveSyntax("turtle-syntax-number-10.ttl");
    }

    @Test
    public void test_turtle_syntax_number_11() {
        testTurtlePositiveSyntax("turtle-syntax-number-11.ttl");
    }

    @Test
    public void test_turtle_syntax_datatypes_01() {
        testTurtlePositiveSyntax("turtle-syntax-datatypes-01.ttl");
    }

    @Test
    public void test_turtle_syntax_datatypes_02() {
        testTurtlePositiveSyntax("turtle-syntax-datatypes-02.ttl");
    }

    @Test
    public void test_turtle_syntax_kw_01() {
        testTurtlePositiveSyntax("turtle-syntax-kw-01.ttl");
    }

    @Test
    public void test_turtle_syntax_kw_02() {
        testTurtlePositiveSyntax("turtle-syntax-kw-02.ttl");
    }

    @Test
    public void test_turtle_syntax_kw_03() {
        testTurtlePositiveSyntax("turtle-syntax-kw-03.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_01() {
        testTurtlePositiveSyntax("turtle-syntax-struct-01.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_02() {
        testTurtlePositiveSyntax("turtle-syntax-struct-02.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_03() {
        testTurtlePositiveSyntax("turtle-syntax-struct-03.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_04() {
        testTurtlePositiveSyntax("turtle-syntax-struct-04.ttl");
    }

    @Test
    public void test_turtle_syntax_struct_05() {
        testTurtlePositiveSyntax("turtle-syntax-struct-05.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_01() {
        testTurtlePositiveSyntax("turtle-syntax-lists-01.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_02() {
        testTurtlePositiveSyntax("turtle-syntax-lists-02.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_03() {
        testTurtlePositiveSyntax("turtle-syntax-lists-03.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_04() {
        testTurtlePositiveSyntax("turtle-syntax-lists-04.ttl");
    }

    @Test
    public void test_turtle_syntax_lists_05() {
        testTurtlePositiveSyntax("turtle-syntax-lists-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_04() {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_uri_05() {
        testTurtleNegativeSyntax("turtle-syntax-bad-uri-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_04() {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_prefix_05() {
        testTurtleNegativeSyntax("turtle-syntax-bad-prefix-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_base_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-base-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_base_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-base-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_base_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-base-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_04() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_05() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_06() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-06.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_07() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-07.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_04() {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_kw_05() {
        testTurtleNegativeSyntax("turtle-syntax-bad-kw-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_04() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_05() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_06() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-06.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_07() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-07.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_08() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-08.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_09() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-09.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_10() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-10.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_11() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-11.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_12() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-12.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_n3_extras_13() {
        testTurtleNegativeSyntax("turtle-syntax-bad-n3-extras-13.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_08() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-08.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_09() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-09.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_10() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-10.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_11() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-11.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_12() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-12.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_13() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-13.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_14() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-14.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_15() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-15.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_16() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-16.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_struct_17() {
        testTurtleNegativeSyntax("turtle-syntax-bad-struct-17.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_lang_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-lang-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_esc_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-esc-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_esc_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-esc-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_esc_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-esc-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_esc_04() {
        testTurtleNegativeSyntax("turtle-syntax-bad-esc-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_pname_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-pname-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_pname_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-pname-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_pname_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-pname-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_04() {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_05() {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-05.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_06() {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-06.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_string_07() {
        testTurtleNegativeSyntax("turtle-syntax-bad-string-07.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_01() {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-01.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_02() {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-02.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_03() {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-03.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_04() {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_num_05() {
        testTurtleNegativeSyntax("turtle-syntax-bad-num-05.ttl");
    }

    @Test
    public void test_turtle_eval_struct_01() {
        testTurtleEval("turtle-eval-struct-01.ttl", "turtle-eval-struct-01.nt");
    }

    @Test
    public void test_turtle_eval_struct_02() {
        testTurtleEval("turtle-eval-struct-02.ttl", "turtle-eval-struct-02.nt");
    }

    @Test
    public void test_turtle_subm_01() {
        testTurtleEval("turtle-subm-01.ttl", "turtle-subm-01.nt");
    }

    @Test
    public void test_turtle_subm_02() {
        testTurtleEval("turtle-subm-02.ttl", "turtle-subm-02.nt");
    }

    @Test
    public void test_turtle_subm_03() {
        testTurtleEval("turtle-subm-03.ttl", "turtle-subm-03.nt");
    }

    @Test
    public void test_turtle_subm_04() {
        testTurtleEval("turtle-subm-04.ttl", "turtle-subm-04.nt");
    }

    @Test
    public void test_turtle_subm_05() {
        testTurtleEval("turtle-subm-05.ttl", "turtle-subm-05.nt");
    }

    @Test
    public void test_turtle_subm_06() {
        testTurtleEval("turtle-subm-06.ttl", "turtle-subm-06.nt");
    }

    @Test
    public void test_turtle_subm_07() {
        testTurtleEval("turtle-subm-07.ttl", "turtle-subm-07.nt");
    }

    @Test
    public void test_turtle_subm_08() {
        testTurtleEval("turtle-subm-08.ttl", "turtle-subm-08.nt");
    }

    @Test
    public void test_turtle_subm_09() {
        testTurtleEval("turtle-subm-09.ttl", "turtle-subm-09.nt");
    }

    @Test
    public void test_turtle_subm_10() {
        testTurtleEval("turtle-subm-10.ttl", "turtle-subm-10.nt");
    }

    @Test
    public void test_turtle_subm_11() {
        testTurtleEval("turtle-subm-11.ttl", "turtle-subm-11.nt");
    }

    @Test
    public void test_turtle_subm_12() {
        testTurtleEval("turtle-subm-12.ttl", "turtle-subm-12.nt");
    }

    @Test
    public void test_turtle_subm_13() {
        testTurtleEval("turtle-subm-13.ttl", "turtle-subm-13.nt");
    }

    @Test
    public void test_turtle_subm_14() {
        testTurtleEval("turtle-subm-14.ttl", "turtle-subm-14.nt");
    }

    @Test
    public void test_turtle_subm_15() {
        testTurtleEval("turtle-subm-15.ttl", "turtle-subm-15.nt");
    }

    @Test
    public void test_turtle_subm_16() {
        testTurtleEval("turtle-subm-16.ttl", "turtle-subm-16.nt");
    }

    @Test
    public void test_turtle_subm_17() {
        testTurtleEval("turtle-subm-17.ttl", "turtle-subm-17.nt");
    }

    @Test
    public void test_turtle_subm_18() {
        testTurtleEval("turtle-subm-18.ttl", "turtle-subm-18.nt");
    }

    @Test
    public void test_turtle_subm_19() {
        testTurtleEval("turtle-subm-19.ttl", "turtle-subm-19.nt");
    }

    @Test
    public void test_turtle_subm_20() {
        testTurtleEval("turtle-subm-20.ttl", "turtle-subm-20.nt");
    }

    @Test
    public void test_turtle_subm_21() {
        testTurtleEval("turtle-subm-21.ttl", "turtle-subm-21.nt");
    }

    @Test
    public void test_turtle_subm_22() {
        testTurtleEval("turtle-subm-22.ttl", "turtle-subm-22.nt");
    }

    @Test
    public void test_turtle_subm_23() {
        testTurtleEval("turtle-subm-23.ttl", "turtle-subm-23.nt");
    }

    @Test
    public void test_turtle_subm_24() {
        testTurtleEval("turtle-subm-24.ttl", "turtle-subm-24.nt");
    }

    @Test
    public void test_turtle_subm_25() {
        testTurtleEval("turtle-subm-25.ttl", "turtle-subm-25.nt");
    }

    @Test
    public void test_turtle_subm_26() {
        testTurtleEval("turtle-subm-26.ttl", "turtle-subm-26.nt");
    }

    @Test
    public void test_turtle_subm_27() {
        testTurtleEval("turtle-subm-27.ttl", "turtle-subm-27.nt");
    }

    @Test
    public void test_turtle_eval_bad_01() {
        testTurtleNegativeSyntax("turtle-eval-bad-01.ttl");
    }

    @Test
    public void test_turtle_eval_bad_02() {
        testTurtleNegativeSyntax("turtle-eval-bad-02.ttl");
    }

    @Test
    public void test_turtle_eval_bad_03() {
        testTurtleNegativeSyntax("turtle-eval-bad-03.ttl");
    }

    @Test
    public void test_turtle_eval_bad_04() {
        testTurtleNegativeSyntax("turtle-eval-bad-04.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_blank_label_dot_end() {
        testTurtleNegativeSyntax("turtle-syntax-bad-blank-label-dot-end.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ln_dash_start() {
        testTurtleNegativeSyntax("turtle-syntax-bad-ln-dash-start.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ln_escape_start() {
        testTurtleNegativeSyntax("turtle-syntax-bad-ln-escape-start.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ln_escape() {
        testTurtleNegativeSyntax("turtle-syntax-bad-ln-escape.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_missing_ns_dot_end() {
        testTurtleNegativeSyntax("turtle-syntax-bad-missing-ns-dot-end.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_missing_ns_dot_start() {
        testTurtleNegativeSyntax("turtle-syntax-bad-missing-ns-dot-start.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ns_dot_end() {
        testTurtleNegativeSyntax("turtle-syntax-bad-ns-dot-end.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_ns_dot_start() {
        testTurtleNegativeSyntax("turtle-syntax-bad-ns-dot-start.ttl");
    }

    @Test
    public void test_turtle_syntax_bad_number_dot_in_anon() {
        testTurtleNegativeSyntax("turtle-syntax-bad-number-dot-in-anon.ttl");
    }

    @Test
    public void test_turtle_syntax_blank_label() {
        testTurtlePositiveSyntax("turtle-syntax-blank-label.ttl");
    }

    @Test
    public void test_turtle_syntax_ln_colons() {
        testTurtlePositiveSyntax("turtle-syntax-ln-colons.ttl");
    }

    @Test
    public void test_turtle_syntax_ln_dots() {
        testTurtlePositiveSyntax("turtle-syntax-ln-dots.ttl");
    }

    @Test
    public void test_turtle_syntax_ns_dots() {
        testTurtlePositiveSyntax("turtle-syntax-ns-dots.ttl");
    }
}
