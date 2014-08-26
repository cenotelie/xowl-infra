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

import org.junit.Assert;
import org.junit.Test;
import org.xowl.hime.redist.ParseResult;
import org.xowl.store.loaders.Loader;
import org.xowl.store.loaders.NTriplesLoader;
import org.xowl.store.rdf.RDFGraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Test suite for N-Triples
 *
 * @author Laurent Wouters
 */
public class NTripleParsingTest {

    /**
     * The RDF graph for the parsing tests
     */
    protected RDFGraph graph;

    /**
     * Logger for the tests
     */
    protected TestLogger logger;

    /**
     * Initializes this test suite
     *
     * @throws IOException
     */
    public NTripleParsingTest() throws IOException {
        graph = new RDFGraph();
        logger = new TestLogger();
    }

    /**
     * Tests the parsing of a resource
     *
     * @param resource     A resource
     * @param shallSucceed Whether the loading is expected to succeed
     */
    protected void doTest(String resource, boolean shallSucceed) {
        logger.reset();
        Loader loader = new NTriplesLoader(graph);
        InputStream stream = NTripleParsingTest.class.getResourceAsStream(resource);
        Reader reader = new InputStreamReader(stream);

        ParseResult result = loader.parse(logger, reader);
        if (shallSucceed) {
            Assert.assertTrue("Failed to parse resource " + resource, result.isSuccess());
            Assert.assertEquals("Failed to parse resource " + resource, 0, result.getErrors().size());
        } else {
            Assert.assertNotEquals("No error reported while parsing " + resource, 0, result.getErrors().size());
        }

        try {
            reader.close();
        } catch (IOException ex) {
            Assert.fail("Failed to close the resource " + resource);
        }
    }

    @Test
    public void test_comment_following_triple() throws IOException {
        doTest("/ntriples/comment_following_triple.nt", true);
    }

    @Test
    public void test_langtagged_string() throws IOException {
        doTest("/ntriples/langtagged_string.nt", true);
    }

    @Test
    public void test_lantag_with_subtag() throws IOException {
        doTest("/ntriples/lantag_with_subtag.nt", true);
    }

    @Test
    public void test_literal() throws IOException {
        doTest("/ntriples/literal.nt", true);
    }

    @Test
    public void test_literal_all_controls() throws IOException {
        doTest("/ntriples/literal_all_controls.nt", true);
    }

    @Test
    public void test_literal_all_punctuation() throws IOException {
        doTest("/ntriples/literal_all_punctuation.nt", true);
    }

    @Test
    public void test_literal_ascii_boundaries() throws IOException {
        doTest("/ntriples/literal_ascii_boundaries.nt", true);
    }

    @Test
    public void test_literal_false() throws IOException {
        doTest("/ntriples/literal_false.nt", true);
    }

    @Test
    public void test_literal_true() throws IOException {
        doTest("/ntriples/literal_true.nt", true);
    }

    @Test
    public void test_literal_with_2_dquotes() throws IOException {
        doTest("/ntriples/literal_with_2_dquotes.nt", true);
    }

    @Test
    public void test_literal_with_2_squotes() throws IOException {
        doTest("/ntriples/literal_with_2_squotes.nt", true);
    }

    @Test
    public void test_literal_with_BACKSPACE() throws IOException {
        doTest("/ntriples/literal_with_BACKSPACE.nt", true);
    }

    @Test
    public void test_literal_with_CARRIAGE_RETURN() throws IOException {
        doTest("/ntriples/literal_with_CARRIAGE_RETURN.nt", true);
    }

    @Test
    public void test_literal_with_CHARACTER_TABULATION() throws IOException {
        doTest("/ntriples/literal_with_CHARACTER_TABULATION.nt", true);
    }

    @Test
    public void test_literal_with_dquote() throws IOException {
        doTest("/ntriples/literal_with_dquote.nt", true);
    }

    @Test
    public void test_literal_with_FORM_FEED() throws IOException {
        doTest("/ntriples/literal_with_FORM_FEED.nt", true);
    }

    @Test
    public void test_literal_with_LINE_FEED() throws IOException {
        doTest("/ntriples/literal_with_LINE_FEED.nt", true);
    }

    @Test
    public void test_literal_with_numeric_escape4() throws IOException {
        doTest("/ntriples/literal_with_numeric_escape4.nt", true);
    }

    @Test
    public void test_literal_with_numeric_escape8() throws IOException {
        doTest("/ntriples/literal_with_numeric_escape8.nt", true);
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS() throws IOException {
        doTest("/ntriples/literal_with_REVERSE_SOLIDUS.nt", true);
    }

    @Test
    public void test_literal_with_REVERSE_SOLIDUS2() throws IOException {
        doTest("/ntriples/literal_with_REVERSE_SOLIDUS2.nt", true);
    }

    @Test
    public void test_literal_with_squote() throws IOException {
        doTest("/ntriples/literal_with_squote.nt", true);
    }

    @Test
    public void test_literal_with_UTF8_boundaries() throws IOException {
        doTest("/ntriples/literal_with_UTF8_boundaries.nt", true);
    }

    @Test
    public void test_minimal_whitespace() throws IOException {
        doTest("/ntriples/minimal_whitespace.nt", true);
    }

    @Test
    public void test_nt_syntax_bad_base_01() throws IOException {
        doTest("/ntriples/nt-syntax-bad-base-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_esc_01() throws IOException {
        doTest("/ntriples/nt-syntax-bad-esc-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_esc_02() throws IOException {
        doTest("/ntriples/nt-syntax-bad-esc-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_esc_03() throws IOException {
        doTest("/ntriples/nt-syntax-bad-esc-03.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_lang_01() throws IOException {
        doTest("/ntriples/nt-syntax-bad-lang-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_num_01() throws IOException {
        doTest("/ntriples/nt-syntax-bad-num-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_num_02() throws IOException {
        doTest("/ntriples/nt-syntax-bad-num-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_num_03() throws IOException {
        doTest("/ntriples/nt-syntax-bad-num-03.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_prefix_01() throws IOException {
        doTest("/ntriples/nt-syntax-bad-prefix-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_01() throws IOException {
        doTest("/ntriples/nt-syntax-bad-string-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_02() throws IOException {
        doTest("/ntriples/nt-syntax-bad-string-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_03() throws IOException {
        doTest("/ntriples/nt-syntax-bad-string-03.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_04() throws IOException {
        doTest("/ntriples/nt-syntax-bad-string-04.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_05() throws IOException {
        doTest("/ntriples/nt-syntax-bad-string-05.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_06() throws IOException {
        doTest("/ntriples/nt-syntax-bad-string-06.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_string_07() throws IOException {
        doTest("/ntriples/nt-syntax-bad-string-07.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_struct_01() throws IOException {
        doTest("/ntriples/nt-syntax-bad-struct-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_struct_02() throws IOException {
        doTest("/ntriples/nt-syntax-bad-struct-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_01() throws IOException {
        doTest("/ntriples/nt-syntax-bad-uri-01.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_02() throws IOException {
        doTest("/ntriples/nt-syntax-bad-uri-02.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_03() throws IOException {
        doTest("/ntriples/nt-syntax-bad-uri-03.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_04() throws IOException {
        doTest("/ntriples/nt-syntax-bad-uri-04.nt", false);
    }

    @Test
    public void test_nt_syntax_bad_uri_05() throws IOException {
        doTest("/ntriples/nt-syntax-bad-uri-05.nt", false);
    }

    @Test
    public void test_nt_syntax_bnode_01() throws IOException {
        doTest("/ntriples/nt-syntax-bnode-01.nt", true);
    }

    @Test
    public void test_nt_syntax_bnode_02() throws IOException {
        doTest("/ntriples/nt-syntax-bnode-02.nt", true);
    }

    @Test
    public void test_nt_syntax_bnode_03() throws IOException {
        doTest("/ntriples/nt-syntax-bnode-03.nt", true);
    }

    @Test
    public void test_nt_syntax_datatypes_01() throws IOException {
        doTest("/ntriples/nt-syntax-datatypes-01.nt", true);
    }

    @Test
    public void test_nt_syntax_datatypes_02() throws IOException {
        doTest("/ntriples/nt-syntax-datatypes-02.nt", true);
    }

    @Test
    public void test_nt_syntax_file_01() throws IOException {
        doTest("/ntriples/nt-syntax-file-01.nt", true);
    }

    @Test
    public void test_nt_syntax_file_02() throws IOException {
        doTest("/ntriples/nt-syntax-file-02.nt", true);
    }

    @Test
    public void test_nt_syntax_file_03() throws IOException {
        doTest("/ntriples/nt-syntax-file-03.nt", true);
    }

    @Test
    public void test_nt_syntax_str_esc_01() throws IOException {
        doTest("/ntriples/nt-syntax-str-esc-01.nt", true);
    }

    @Test
    public void test_nt_syntax_str_esc_02() throws IOException {
        doTest("/ntriples/nt-syntax-str-esc-02.nt", true);
    }

    @Test
    public void test_nt_syntax_str_esc_03() throws IOException {
        doTest("/ntriples/nt-syntax-str-esc-03.nt", true);
    }

    @Test
    public void test_nt_syntax_string_01() throws IOException {
        doTest("/ntriples/nt-syntax-string-01.nt", true);
    }

    @Test
    public void test_nt_syntax_string_02() throws IOException {
        doTest("/ntriples/nt-syntax-string-02.nt", true);
    }

    @Test
    public void test_nt_syntax_string_03() throws IOException {
        doTest("/ntriples/nt-syntax-string-03.nt", true);
    }

    @Test
    public void test_nt_syntax_subm_01() throws IOException {
        doTest("/ntriples/nt-syntax-subm-01.nt", true);
    }

    @Test
    public void test_nt_syntax_uri_01() throws IOException {
        doTest("/ntriples/nt-syntax-uri-01.nt", true);
    }

    @Test
    public void test_nt_syntax_uri_02() throws IOException {
        doTest("/ntriples/nt-syntax-uri-02.nt", true);
    }

    @Test
    public void test_nt_syntax_uri_03() throws IOException {
        doTest("/ntriples/nt-syntax-uri-03.nt", true);
    }

    @Test
    public void test_nt_syntax_uri_04() throws IOException {
        doTest("/ntriples/nt-syntax-uri-04.nt", true);
    }
}
