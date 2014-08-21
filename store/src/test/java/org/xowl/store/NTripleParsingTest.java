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
 * Test suite for N-Triples
 *
 * @author Laurent Wouters
 */
public class NTripleParsingTest extends W3CTestSuite {

    public NTripleParsingTest() throws IOException {

    }

    @Test
    public void test_comment_following_triple() throws IOException {
        test_parsing("/ntriples/comment_following_triple.nt", true);
    }

    @Test
    public void test_langtagged_string() throws IOException {
        test_parsing("/ntriples/langtagged_string.nt", true);
    }

    @Test
    public void test_lantag_with_subtag() throws IOException {
        test_parsing("/ntriples/lantag_with_subtag.nt", true);
    }

    @Test
    public void test_literal() throws IOException {
        test_parsing("/ntriples/literal.nt", true);
    }

    @Test
    public void test_literal_all_controls() throws IOException {
        test_parsing("/ntriples/literal_all_controls.nt", true);
    }

    @Test
    public void test_literal_all_punctuation() throws IOException {
        test_parsing("/ntriples/literal_all_punctuation.nt", true);
    }

    @Test
    public void test_literal_ascii_boundaries() throws IOException {
        test_parsing("/ntriples/literal_ascii_boundaries.nt", true);
    }

    @Test
    public void test_literal_false() throws IOException {
        test_parsing("/ntriples/literal_false.nt", true);
    }

    @Test
    public void test_literal_true() throws IOException {
        test_parsing("/ntriples/literal_true.nt", true);
    }

    @Test
    public void test_literal_with_2_dquotes() throws IOException {
        test_parsing("/ntriples/literal_with_2_dquotes.nt", true);
    }

    @Test
    public void test_literal_with_2_squotes() throws IOException {
        test_parsing("/ntriples/literal_with_2_squotes.nt", true);
    }

    @Test
    public void test_literal_with_BACKSPACE() throws IOException {
        test_parsing("/ntriples/literal_with_BACKSPACE.nt", true);
    }

    @Test
    public void test_literal_with_CARRIAGE_RETURN() throws IOException {
        test_parsing("/ntriples/literal_with_CARRIAGE_RETURN.nt", true);
    }

    @Test
    public void test_literal_with_CHARACTER_TABULATION() throws IOException {
        test_parsing("/ntriples/literal_with_CHARACTER_TABULATION.nt", true);
    }

    @Test
    public void test_literal_with_dquote() throws IOException {
        test_parsing("/ntriples/literal_with_dquote.nt", true);
    }

    @Test
    public void test_literal_with_FORM_FEED() throws IOException {
        test_parsing("/ntriples/literal_with_FORM_FEED.nt", true);
    }

    @Test
    public void test_literal_with_LINE_FEED() throws IOException {
        test_parsing("/ntriples/literal_with_LINE_FEED.nt", true);
    }

    @Test
    public void test_literal_with_numeric_escape4() throws IOException {
        test_parsing("/ntriples/literal_with_numeric_escape4.nt", true);
    }

    @Test
    public void test_literal_with_numeric_escape8() throws IOException {
        test_parsing("/ntriples/literal_with_numeric_escape8.nt", true);
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS() throws IOException {
        test_parsing("/ntriples/literal_with_REVERSE_SOLIDUS.nt", true);
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS2() throws IOException {
        test_parsing("/ntriples/literal_with_REVERSE_SOLIDUS2.nt", true);
    }

    @Test
    public void test_literal_with_squote() throws IOException {
        test_parsing("/ntriples/literal_with_squote.nt", true);
    }

    @Test
    public void test_literal_with_UTF8_boundaries() throws IOException {
        test_parsing("/ntriples/literal_with_UTF8_boundaries.nt", true);
    }

    @Test
    public void test_minimal_whitespace() throws IOException {
        test_parsing("/ntriples/minimal_whitespace.nt", true);
    }

    @Test
    public void test_nt_syntax_bad_base_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-base-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_esc_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-esc-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_esc_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-esc-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_esc_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-esc-03.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_lang_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-lang-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_num_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-num-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_num_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-num-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_num_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-num-03.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_prefix_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-prefix-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-string-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-string-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-string-03.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_04() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-string-04.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_05() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-string-05.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_06() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-string-06.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_07() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-string-07.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_struct_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-struct-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_struct_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-struct-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-uri-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-uri-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-uri-03.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_04() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-uri-04.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_05() throws IOException {
        test_parsing("/ntriples/nt-syntax-bad-uri-05.nt", false);
    }

    @Test
    public void test_nt_syntax_bnode_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-bnode-01.nt", true);
    }

    @Test
    public void test_nt_syntax_bnode_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-bnode-02.nt", true);
    }

    @Test
    public void test_nt_syntax_bnode_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-bnode-03.nt", true);
    }

    @Test
    public void test_nt_syntax_datatypes_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-datatypes-01.nt", true);
    }

    @Test
    public void test_nt_syntax_datatypes_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-datatypes-02.nt", true);
    }

    @Test
    public void test_nt_syntax_file_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-file-01.nt", true);
    }

    @Test
    public void test_nt_syntax_file_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-file-02.nt", true);
    }

    @Test
    public void test_nt_syntax_file_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-file-03.nt", true);
    }

    @Test
    public void test_nt_syntax_str_esc_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-str-esc-01.nt", true);
    }

    @Test
    public void test_nt_syntax_str_esc_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-str-esc-02.nt", true);
    }

    @Test
    public void test_nt_syntax_str_esc_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-str-esc-03.nt", true);
    }

    @Test
    public void test_nt_syntax_string_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-string-01.nt", true);
    }

    @Test
    public void test_nt_syntax_string_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-string-02.nt", true);
    }

    @Test
    public void test_nt_syntax_string_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-string-03.nt", true);
    }

    @Test
    public void test_nt_syntax_subm_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-subm-01.nt", true);
    }

    @Test
    public void test_nt_syntax_uri_01() throws IOException {
        test_parsing("/ntriples/nt-syntax-uri-01.nt", true);
    }

    @Test
    public void test_nt_syntax_uri_02() throws IOException {
        test_parsing("/ntriples/nt-syntax-uri-02.nt", true);
    }

    @Test
    public void test_nt_syntax_uri_03() throws IOException {
        test_parsing("/ntriples/nt-syntax-uri-03.nt", true);
    }

    @Test
    public void test_nt_syntax_uri_04() throws IOException {
        test_parsing("/ntriples/nt-syntax-uri-04.nt", true);
    }
}
