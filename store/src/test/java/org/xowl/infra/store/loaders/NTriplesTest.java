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
 * Test suite for N-Triples
 *
 * @author Laurent Wouters
 */
public class NTriplesTest extends BaseNTripleTest {
    @Test
    public void test_nt_syntax_file_01() {
        testNTriplesPositiveSyntax("nt-syntax-file-01.nt");
    }

    @Test
    public void test_nt_syntax_file_02() {
        testNTriplesPositiveSyntax("nt-syntax-file-02.nt");
    }

    @Test
    public void test_nt_syntax_file_03() {
        testNTriplesPositiveSyntax("nt-syntax-file-03.nt");
    }

    @Test
    public void test_nt_syntax_uri_01() {
        testNTriplesPositiveSyntax("nt-syntax-uri-01.nt");
    }

    @Test
    public void test_nt_syntax_uri_02() {
        testNTriplesPositiveSyntax("nt-syntax-uri-02.nt");
    }

    @Test
    public void test_nt_syntax_uri_03() {
        testNTriplesPositiveSyntax("nt-syntax-uri-03.nt");
    }

    @Test
    public void test_nt_syntax_uri_04() {
        testNTriplesPositiveSyntax("nt-syntax-uri-04.nt");
    }

    @Test
    public void test_nt_syntax_string_01() {
        testNTriplesPositiveSyntax("nt-syntax-string-01.nt");
    }

    @Test
    public void test_nt_syntax_string_02() {
        testNTriplesPositiveSyntax("nt-syntax-string-02.nt");
    }

    @Test
    public void test_nt_syntax_string_03() {
        testNTriplesPositiveSyntax("nt-syntax-string-03.nt");
    }

    @Test
    public void test_nt_syntax_str_esc_01() {
        testNTriplesPositiveSyntax("nt-syntax-str-esc-01.nt");
    }

    @Test
    public void test_nt_syntax_str_esc_02() {
        testNTriplesPositiveSyntax("nt-syntax-str-esc-02.nt");
    }

    @Test
    public void test_nt_syntax_str_esc_03() {
        testNTriplesPositiveSyntax("nt-syntax-str-esc-03.nt");
    }

    @Test
    public void test_nt_syntax_bnode_01() {
        testNTriplesPositiveSyntax("nt-syntax-bnode-01.nt");
    }

    @Test
    public void test_nt_syntax_bnode_02() {
        testNTriplesPositiveSyntax("nt-syntax-bnode-02.nt");
    }

    @Test
    public void test_nt_syntax_bnode_03() {
        testNTriplesPositiveSyntax("nt-syntax-bnode-03.nt");
    }

    @Test
    public void test_nt_syntax_datatypes_01() {
        testNTriplesPositiveSyntax("nt-syntax-datatypes-01.nt");
    }

    @Test
    public void test_nt_syntax_datatypes_02() {
        testNTriplesPositiveSyntax("nt-syntax-datatypes-02.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_01() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-01.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_02() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-02.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_03() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-03.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_04() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-04.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_05() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-05.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_06() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-06.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_07() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-07.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_08() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-08.nt");
    }

    @Test
    public void test_nt_syntax_bad_uri_09() {
        testNTriplesNegativeSyntax("nt-syntax-bad-uri-09.nt");
    }

    @Test
    public void test_nt_syntax_bad_prefix_01() {
        testNTriplesNegativeSyntax("nt-syntax-bad-prefix-01.nt");
    }

    @Test
    public void test_nt_syntax_bad_base_01() {
        testNTriplesNegativeSyntax("nt-syntax-bad-base-01.nt");
    }

    @Test
    public void test_nt_syntax_bad_struct_01() {
        testNTriplesNegativeSyntax("nt-syntax-bad-struct-01.nt");
    }

    @Test
    public void test_nt_syntax_bad_struct_02() {
        testNTriplesNegativeSyntax("nt-syntax-bad-struct-02.nt");
    }

    @Test
    public void test_nt_syntax_bad_lang_01() {
        testNTriplesNegativeSyntax("nt-syntax-bad-lang-01.nt");
    }

    @Test
    public void test_nt_syntax_bad_esc_01() {
        testNTriplesNegativeSyntax("nt-syntax-bad-esc-01.nt");
    }

    @Test
    public void test_nt_syntax_bad_esc_02() {
        testNTriplesNegativeSyntax("nt-syntax-bad-esc-02.nt");
    }

    @Test
    public void test_nt_syntax_bad_esc_03() {
        testNTriplesNegativeSyntax("nt-syntax-bad-esc-03.nt");
    }

    @Test
    public void test_nt_syntax_bad_string_01() {
        testNTriplesNegativeSyntax("nt-syntax-bad-string-01.nt");
    }

    @Test
    public void test_nt_syntax_bad_string_02() {
        testNTriplesNegativeSyntax("nt-syntax-bad-string-02.nt");
    }

    @Test
    public void test_nt_syntax_bad_string_03() {
        testNTriplesNegativeSyntax("nt-syntax-bad-string-03.nt");
    }

    @Test
    public void test_nt_syntax_bad_string_04() {
        testNTriplesNegativeSyntax("nt-syntax-bad-string-04.nt");
    }

    @Test
    public void test_nt_syntax_bad_string_05() {
        testNTriplesNegativeSyntax("nt-syntax-bad-string-05.nt");
    }

    @Test
    public void test_nt_syntax_bad_string_06() {
        testNTriplesNegativeSyntax("nt-syntax-bad-string-06.nt");
    }

    @Test
    public void test_nt_syntax_bad_string_07() {
        testNTriplesNegativeSyntax("nt-syntax-bad-string-07.nt");
    }

    @Test
    public void test_nt_syntax_bad_num_01() {
        testNTriplesNegativeSyntax("nt-syntax-bad-num-01.nt");
    }

    @Test
    public void test_nt_syntax_bad_num_02() {
        testNTriplesNegativeSyntax("nt-syntax-bad-num-02.nt");
    }

    @Test
    public void test_nt_syntax_bad_num_03() {
        testNTriplesNegativeSyntax("nt-syntax-bad-num-03.nt");
    }

    @Test
    public void test_nt_syntax_subm_01() {
        testNTriplesPositiveSyntax("nt-syntax-subm-01.nt");
    }

    @Test
    public void test_comment_following_triple() {
        testNTriplesPositiveSyntax("comment_following_triple.nt");
    }

    @Test
    public void test_literal() {
        testNTriplesPositiveSyntax("literal.nt");
    }

    @Test
    public void test_literal_all_controls() {
        testNTriplesPositiveSyntax("literal_all_controls.nt");
    }

    @Test
    public void test_literal_all_punctuation() {
        testNTriplesPositiveSyntax("literal_all_punctuation.nt");
    }

    @Test
    public void test_literal_ascii_boundaries() {
        testNTriplesPositiveSyntax("literal_ascii_boundaries.nt");
    }

    @Test
    public void test_literal_with_2_dquotes() {
        testNTriplesPositiveSyntax("literal_with_2_dquotes.nt");
    }

    @Test
    public void test_literal_with_2_squotes() {
        testNTriplesPositiveSyntax("literal_with_2_squotes.nt");
    }

    @Test
    public void test_literal_with_BACKSPACE() {
        testNTriplesPositiveSyntax("literal_with_BACKSPACE.nt");
    }

    @Test
    public void test_literal_with_CARRIAGE_RETURN() {
        testNTriplesPositiveSyntax("literal_with_CARRIAGE_RETURN.nt");
    }

    @Test
    public void test_literal_with_CHARACTER_TABULATION() {
        testNTriplesPositiveSyntax("literal_with_CHARACTER_TABULATION.nt");
    }

    @Test
    public void test_literal_with_dquote() {
        testNTriplesPositiveSyntax("literal_with_dquote.nt");
    }

    @Test
    public void test_literal_with_FORM_FEED() {
        testNTriplesPositiveSyntax("literal_with_FORM_FEED.nt");
    }

    @Test
    public void test_literal_with_LINE_FEED() {
        testNTriplesPositiveSyntax("literal_with_LINE_FEED.nt");
    }

    @Test
    public void test_literal_with_numeric_escape4() {
        testNTriplesPositiveSyntax("literal_with_numeric_escape4.nt");
    }

    @Test
    public void test_literal_with_numeric_escape8() {
        testNTriplesPositiveSyntax("literal_with_numeric_escape8.nt");
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS() {
        testNTriplesPositiveSyntax("literal_with_REVERSE_SOLIDUS.nt");
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS2() {
        testNTriplesPositiveSyntax("literal_with_REVERSE_SOLIDUS2.nt");
    }

    @Test
    public void test_literal_with_squote() {
        testNTriplesPositiveSyntax("literal_with_squote.nt");
    }

    @Test
    public void test_literal_with_UTF8_boundaries() {
        testNTriplesPositiveSyntax("literal_with_UTF8_boundaries.nt");
    }

    @Test
    public void test_langtagged_string() {
        testNTriplesPositiveSyntax("langtagged_string.nt");
    }

    @Test
    public void test_lantag_with_subtag() {
        testNTriplesPositiveSyntax("lantag_with_subtag.nt");
    }

    @Test
    public void test_minimal_whitespace() {
        testNTriplesPositiveSyntax("minimal_whitespace.nt");
    }
}
