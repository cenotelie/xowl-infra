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
 * Test suite for N-Quads
 *
 * @author Laurent Wouters
 */
public class NQuadsTest extends BaseNQuadsTest {
    @Test
    public void test_nq_syntax_uri_01() {
        testNQuadsPositiveSyntax("nq-syntax-uri-01.nq");
    }

    @Test
    public void test_nq_syntax_uri_02() {
        testNQuadsPositiveSyntax("nq-syntax-uri-02.nq");
    }

    @Test
    public void test_nq_syntax_uri_03() {
        testNQuadsPositiveSyntax("nq-syntax-uri-03.nq");
    }

    @Test
    public void test_nq_syntax_uri_04() {
        testNQuadsPositiveSyntax("nq-syntax-uri-04.nq");
    }

    @Test
    public void test_nq_syntax_uri_05() {
        testNQuadsPositiveSyntax("nq-syntax-uri-05.nq");
    }

    @Test
    public void test_nq_syntax_uri_06() {
        testNQuadsPositiveSyntax("nq-syntax-uri-06.nq");
    }

    @Test
    public void test_nq_syntax_bnode_01() {
        testNQuadsPositiveSyntax("nq-syntax-bnode-01.nq");
    }

    @Test
    public void test_nq_syntax_bnode_02() {
        testNQuadsPositiveSyntax("nq-syntax-bnode-02.nq");
    }

    @Test
    public void test_nq_syntax_bnode_03() {
        testNQuadsPositiveSyntax("nq-syntax-bnode-03.nq");
    }

    @Test
    public void test_nq_syntax_bnode_04() {
        testNQuadsPositiveSyntax("nq-syntax-bnode-04.nq");
    }

    @Test
    public void test_nq_syntax_bnode_05() {
        testNQuadsPositiveSyntax("nq-syntax-bnode-05.nq");
    }

    @Test
    public void test_nq_syntax_bnode_06() {
        testNQuadsPositiveSyntax("nq-syntax-bnode-06.nq");
    }

    @Test
    public void test_nq_syntax_bad_literal_01() {
        testNQuadsNegativeSyntax("nq-syntax-bad-literal-01.nq");
    }

    @Test
    public void test_nq_syntax_bad_literal_02() {
        testNQuadsNegativeSyntax("nq-syntax-bad-literal-02.nq");
    }

    @Test
    public void test_nq_syntax_bad_literal_03() {
        testNQuadsNegativeSyntax("nq-syntax-bad-literal-03.nq");
    }

    @Test
    public void test_nq_syntax_bad_uri_01() {
        testNQuadsNegativeSyntax("nq-syntax-bad-uri-01.nq");
    }

    @Test
    public void test_nq_syntax_bad_quint_01() {
        testNQuadsNegativeSyntax("nq-syntax-bad-quint-01.nq");
    }

    @Test
    public void test_nt_syntax_file_01() {
        testNQuadsPositiveSyntax("nt-syntax-file-01.nq");
    }

    @Test
    public void test_nt_syntax_file_02() {
        testNQuadsPositiveSyntax("nt-syntax-file-02.nq");
    }

    @Test
    public void test_nt_syntax_file_03() {
        testNQuadsPositiveSyntax("nt-syntax-file-03.nq");
    }

    @Test
    public void test_nt_syntax_uri_01() {
        testNQuadsPositiveSyntax("nt-syntax-uri-01.nq");
    }

    @Test
    public void test_nt_syntax_uri_02() {
        testNQuadsPositiveSyntax("nt-syntax-uri-02.nq");
    }

    @Test
    public void test_nt_syntax_uri_03() {
        testNQuadsPositiveSyntax("nt-syntax-uri-03.nq");
    }

    @Test
    public void test_nt_syntax_uri_04() {
        testNQuadsPositiveSyntax("nt-syntax-uri-04.nq");
    }

    @Test
    public void test_nt_syntax_string_01() {
        testNQuadsPositiveSyntax("nt-syntax-string-01.nq");
    }

    @Test
    public void test_nt_syntax_string_02() {
        testNQuadsPositiveSyntax("nt-syntax-string-02.nq");
    }

    @Test
    public void test_nt_syntax_string_03() {
        testNQuadsPositiveSyntax("nt-syntax-string-03.nq");
    }

    @Test
    public void test_nt_syntax_str_esc_01() {
        testNQuadsPositiveSyntax("nt-syntax-str-esc-01.nq");
    }

    @Test
    public void test_nt_syntax_str_esc_02() {
        testNQuadsPositiveSyntax("nt-syntax-str-esc-02.nq");
    }

    @Test
    public void test_nt_syntax_str_esc_03() {
        testNQuadsPositiveSyntax("nt-syntax-str-esc-03.nq");
    }

    @Test
    public void test_nt_syntax_bnode_01() {
        testNQuadsPositiveSyntax("nt-syntax-bnode-01.nq");
    }

    @Test
    public void test_nt_syntax_bnode_02() {
        testNQuadsPositiveSyntax("nt-syntax-bnode-02.nq");
    }

    @Test
    public void test_nt_syntax_bnode_03() {
        testNQuadsPositiveSyntax("nt-syntax-bnode-03.nq");
    }

    @Test
    public void test_nt_syntax_datatypes_01() {
        testNQuadsPositiveSyntax("nt-syntax-datatypes-01.nq");
    }

    @Test
    public void test_nt_syntax_datatypes_02() {
        testNQuadsPositiveSyntax("nt-syntax-datatypes-02.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_01() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-01.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_02() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-02.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_03() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-03.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_04() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-04.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_05() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-05.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_06() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-06.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_07() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-07.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_08() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-08.nq");
    }

    @Test
    public void test_nt_syntax_bad_uri_09() {
        testNQuadsNegativeSyntax("nt-syntax-bad-uri-09.nq");
    }

    @Test
    public void test_nt_syntax_bad_prefix_01() {
        testNQuadsNegativeSyntax("nt-syntax-bad-prefix-01.nq");
    }

    @Test
    public void test_nt_syntax_bad_base_01() {
        testNQuadsNegativeSyntax("nt-syntax-bad-base-01.nq");
    }

    @Test
    public void test_nt_syntax_bad_struct_01() {
        testNQuadsNegativeSyntax("nt-syntax-bad-struct-01.nq");
    }

    @Test
    public void test_nt_syntax_bad_struct_02() {
        testNQuadsNegativeSyntax("nt-syntax-bad-struct-02.nq");
    }

    @Test
    public void test_nt_syntax_bad_lang_01() {
        testNQuadsNegativeSyntax("nt-syntax-bad-lang-01.nq");
    }

    @Test
    public void test_nt_syntax_bad_esc_01() {
        testNQuadsNegativeSyntax("nt-syntax-bad-esc-01.nq");
    }

    @Test
    public void test_nt_syntax_bad_esc_02() {
        testNQuadsNegativeSyntax("nt-syntax-bad-esc-02.nq");
    }

    @Test
    public void test_nt_syntax_bad_esc_03() {
        testNQuadsNegativeSyntax("nt-syntax-bad-esc-03.nq");
    }

    @Test
    public void test_nt_syntax_bad_string_01() {
        testNQuadsNegativeSyntax("nt-syntax-bad-string-01.nq");
    }

    @Test
    public void test_nt_syntax_bad_string_02() {
        testNQuadsNegativeSyntax("nt-syntax-bad-string-02.nq");
    }

    @Test
    public void test_nt_syntax_bad_string_03() {
        testNQuadsNegativeSyntax("nt-syntax-bad-string-03.nq");
    }

    @Test
    public void test_nt_syntax_bad_string_04() {
        testNQuadsNegativeSyntax("nt-syntax-bad-string-04.nq");
    }

    @Test
    public void test_nt_syntax_bad_string_05() {
        testNQuadsNegativeSyntax("nt-syntax-bad-string-05.nq");
    }

    @Test
    public void test_nt_syntax_bad_string_06() {
        testNQuadsNegativeSyntax("nt-syntax-bad-string-06.nq");
    }

    @Test
    public void test_nt_syntax_bad_string_07() {
        testNQuadsNegativeSyntax("nt-syntax-bad-string-07.nq");
    }

    @Test
    public void test_nt_syntax_bad_num_01() {
        testNQuadsNegativeSyntax("nt-syntax-bad-num-01.nq");
    }

    @Test
    public void test_nt_syntax_bad_num_02() {
        testNQuadsNegativeSyntax("nt-syntax-bad-num-02.nq");
    }

    @Test
    public void test_nt_syntax_bad_num_03() {
        testNQuadsNegativeSyntax("nt-syntax-bad-num-03.nq");
    }

    @Test
    public void test_nt_syntax_subm_01() {
        testNQuadsPositiveSyntax("nt-syntax-subm-01.nq");
    }

    @Test
    public void test_comment_following_triple() {
        testNQuadsPositiveSyntax("comment_following_triple.nq");
    }

    @Test
    public void test_literal() {
        testNQuadsPositiveSyntax("literal.nq");
    }

    @Test
    public void test_literal_all_controls() {
        testNQuadsPositiveSyntax("literal_all_controls.nq");
    }

    @Test
    public void test_literal_all_punctuation() {
        testNQuadsPositiveSyntax("literal_all_punctuation.nq");
    }

    @Test
    public void test_literal_ascii_boundaries() {
        testNQuadsPositiveSyntax("literal_ascii_boundaries.nq");
    }

    @Test
    public void test_literal_with_2_dquotes() {
        testNQuadsPositiveSyntax("literal_with_2_dquotes.nq");
    }

    @Test
    public void test_literal_with_2_squotes() {
        testNQuadsPositiveSyntax("literal_with_2_squotes.nq");
    }

    @Test
    public void test_literal_with_BACKSPACE() {
        testNQuadsPositiveSyntax("literal_with_BACKSPACE.nq");
    }

    @Test
    public void test_literal_with_CARRIAGE_RETURN() {
        testNQuadsPositiveSyntax("literal_with_CARRIAGE_RETURN.nq");
    }

    @Test
    public void test_literal_with_CHARACTER_TABULATION() {
        testNQuadsPositiveSyntax("literal_with_CHARACTER_TABULATION.nq");
    }

    @Test
    public void test_literal_with_dquote() {
        testNQuadsPositiveSyntax("literal_with_dquote.nq");
    }

    @Test
    public void test_literal_with_FORM_FEED() {
        testNQuadsPositiveSyntax("literal_with_FORM_FEED.nq");
    }

    @Test
    public void test_literal_with_LINE_FEED() {
        testNQuadsPositiveSyntax("literal_with_LINE_FEED.nq");
    }

    @Test
    public void test_literal_with_numeric_escape4() {
        testNQuadsPositiveSyntax("literal_with_numeric_escape4.nq");
    }

    @Test
    public void test_literal_with_numeric_escape8() {
        testNQuadsPositiveSyntax("literal_with_numeric_escape8.nq");
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS() {
        testNQuadsPositiveSyntax("literal_with_REVERSE_SOLIDUS.nq");
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS2() {
        testNQuadsPositiveSyntax("literal_with_REVERSE_SOLIDUS2.nq");
    }

    @Test
    public void test_literal_with_squote() {
        testNQuadsPositiveSyntax("literal_with_squote.nq");
    }

    @Test
    public void test_literal_with_UTF8_boundaries() {
        testNQuadsPositiveSyntax("literal_with_UTF8_boundaries.nq");
    }

    @Test
    public void test_langtagged_string() {
        testNQuadsPositiveSyntax("langtagged_string.nq");
    }

    @Test
    public void test_lantag_with_subtag() {
        testNQuadsPositiveSyntax("lantag_with_subtag.nq");
    }

    @Test
    public void test_minimal_whitespace() {
        testNQuadsPositiveSyntax("minimal_whitespace.nq");
    }
}
