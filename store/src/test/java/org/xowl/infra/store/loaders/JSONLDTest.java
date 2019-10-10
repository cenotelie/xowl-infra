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

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for JSON-LD
 *
 * @author Laurent Wouters
 * @author Stephen Creff
 */
public class JSONLDTest extends BaseJSONLDTest {

    /**
     * JSON-LD to RDF
     * toRdf tests have input and expected documents, and may have options.
     * The expected results can be compared using string comparison with the processor output.
     */

    @Test
    public void test_toRdf_t0055() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0055-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0055-in.jsonld");
    }

    @Test
    public void test_toRdf_t0001() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0001-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0001-in.jsonld");
    }

    @Test
    public void test_toRdf_t0107() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0107-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0107-in.jsonld");
    }

    @Test
    public void test_toRdf_t0056() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0056-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0056-in.jsonld");
    }

    @Test
    public void test_toRdf_t0108() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0108-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0108-in.jsonld");
    }

    @Test
    public void test_toRdf_t0057() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0057-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0057-in.jsonld");
    }

    @Test
    public void test_toRdf_t0109() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0109-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0109-in.jsonld");
    }

    @Test
    public void test_toRdf_t0058() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0058-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0058-in.jsonld");
    }

    @Test
    public void test_toRdf_t0002() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0002-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0002-in.jsonld");
    }

    @Test
    public void test_toRdf_t0110() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0110-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0110-in.jsonld");
    }

    @Test
    public void test_toRdf_t0059() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0059-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0059-in.jsonld");
    }

    @Test
    public void test_toRdf_t0003() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0003-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0003-in.jsonld");
    }

    @Test
    public void test_toRdf_t0111() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0111-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0111-in.jsonld");
    }

    @Test
    public void test_toRdf_t0060() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0060-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0060-in.jsonld");
    }

    @Test
    public void test_toRdf_t0004() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0004-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0004-in.jsonld");
    }

    @Test
    public void test_toRdf_t0112() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0112-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0112-in.jsonld");
    }

    @Test
    public void test_toRdf_t0061() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0061-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0061-in.jsonld");
    }

    @Test
    public void test_toRdf_t0005() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0005-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0005-in.jsonld");
    }

    @Test
    public void test_toRdf_t0113() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0113-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0113-in.jsonld");
    }

    @Test
    public void test_toRdf_t0062() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0062-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0062-in.jsonld");
    }

    @Test
    public void test_toRdf_t0006() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0006-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0006-in.jsonld");
    }

    @Test
    public void test_toRdf_t0114() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0114-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0114-in.jsonld");
    }

    @Test
    public void test_toRdf_t0063() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0063-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0063-in.jsonld");
    }

    @Test
    public void test_toRdf_t0007() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0007-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0007-in.jsonld");
    }

    @Test
    public void test_toRdf_t0115() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0115-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0115-in.jsonld");
    }

    @Test
    public void test_toRdf_t0064() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0064-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0064-in.jsonld");
    }

    @Test
    public void test_toRdf_t0008() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0008-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0008-in.jsonld");
    }

    @Test
    public void test_toRdf_t0116() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0116-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0116-in.jsonld");
    }

    @Test
    public void test_toRdf_t0065() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0065-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0065-in.jsonld");
    }

    @Test
    public void test_toRdf_t0009() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0009-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0009-in.jsonld");
    }

    @Test
    public void test_toRdf_t0117() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0117-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0117-in.jsonld");
    }

    @Test
    public void test_toRdf_t0066() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0066-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0066-in.jsonld");
    }

    @Test
    public void test_toRdf_t0010() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0010-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0010-in.jsonld");
    }

    @Ignore("Uses generalized RDF graphs")
    @Test
    public void test_toRdf_t0118() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0118-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0118-in.jsonld");
    }

    @Test
    public void test_toRdf_t0067() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0067-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0067-in.jsonld");
    }

    @Test
    public void test_toRdf_t0011() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0011-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0011-in.jsonld");
    }

    @Test
    public void test_toRdf_t0068() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0068-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0068-in.jsonld");
    }

    @Test
    public void test_toRdf_t0012() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0012-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0012-in.jsonld");
    }

    @Test
    public void test_toRdf_t0119() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0119-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0119-in.jsonld");
    }

    @Test
    public void test_toRdf_t0069() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0069-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0069-in.jsonld");
    }

    @Test
    public void test_toRdf_t0013() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0013-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0013-in.jsonld");
    }

    @Test
    public void test_toRdf_t0070() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0070-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0070-in.jsonld");
    }

    @Test
    public void test_toRdf_t0014() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0014-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0014-in.jsonld");
    }

    @Test
    public void test_toRdf_t0071() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0071-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0071-in.jsonld");
    }

    @Test
    public void test_toRdf_t0015() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0015-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0015-in.jsonld");
    }

    @Test
    public void test_toRdf_t0072() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0072-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0072-in.jsonld");
    }

    @Test
    public void test_toRdf_t0016() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0016-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0016-in.jsonld");
    }

    @Test
    public void test_toRdf_t0073() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0073-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0073-in.jsonld");
    }

    @Test
    public void test_toRdf_t0017() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0017-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0017-in.jsonld");
    }

    @Test
    public void test_toRdf_t0074() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0074-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0074-in.jsonld");
    }

    @Test
    public void test_toRdf_t0018() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0018-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0018-in.jsonld");
    }

    @Test
    public void test_toRdf_t0075() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0075-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0075-in.jsonld");
    }

    @Test
    public void test_toRdf_t0019() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0019-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0019-in.jsonld");
    }

    @Test
    public void test_toRdf_t0076() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0076-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0076-in.jsonld");
    }

    @Test
    public void test_toRdf_t0020() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0020-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0020-in.jsonld");
    }

    @Test
    public void test_toRdf_t0077() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0077-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0077-in.jsonld");
    }

    @Test
    public void test_toRdf_t0022() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0022-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0022-in.jsonld");
    }

    @Test
    public void test_toRdf_t0078() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0078-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0078-in.jsonld");
    }

    @Test
    public void test_toRdf_t0023() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0023-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0023-in.jsonld");
    }

    @Test
    public void test_toRdf_t0079() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0079-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0079-in.jsonld");
    }

    @Test
    public void test_toRdf_t0024() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0024-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0024-in.jsonld");
    }

    @Test
    public void test_toRdf_t0080() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0080-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0080-in.jsonld");
    }

    @Test
    public void test_toRdf_t0025() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0025-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0025-in.jsonld");
    }

    @Test
    public void test_toRdf_t0081() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0081-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0081-in.jsonld");
    }

    @Test
    public void test_toRdf_t0026() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0026-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0026-in.jsonld");
    }

    @Test
    public void test_toRdf_t0082() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0082-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0082-in.jsonld");
    }

    @Test
    public void test_toRdf_t0027() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0027-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0027-in.jsonld");
    }

    @Test
    public void test_toRdf_t0083() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0083-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0083-in.jsonld");
    }

    @Test
    public void test_toRdf_t0028() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0028-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0028-in.jsonld");
    }

    @Test
    public void test_toRdf_t0084() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0084-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0084-in.jsonld");
    }

    @Test
    public void test_toRdf_t0029() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0029-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0029-in.jsonld");
    }

    @Test
    public void test_toRdf_t0085() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0085-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0085-in.jsonld");
    }

    @Test
    public void test_toRdf_t0030() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0030-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0030-in.jsonld");
    }

    @Test
    public void test_toRdf_t0086() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0086-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0086-in.jsonld");
    }

    @Test
    public void test_toRdf_t0031() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0031-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0031-in.jsonld");
    }

    @Test
    public void test_toRdf_t0087() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0087-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0087-in.jsonld");
    }

    @Test
    public void test_toRdf_t0032() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0032-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0032-in.jsonld");
    }

    @Test
    public void test_toRdf_t0088() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0088-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0088-in.jsonld");
    }

    @Test
    public void test_toRdf_t0033() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0033-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0033-in.jsonld");
    }

    @Test
    public void test_toRdf_t0089() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0089-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0089-in.jsonld");
    }

    @Test
    public void test_toRdf_t0034() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0034-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0034-in.jsonld");
    }

    @Test
    public void test_toRdf_t0090() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0090-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0090-in.jsonld");
    }

    @Test
    public void test_toRdf_t0035() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0035-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0035-in.jsonld");
    }

    @Test
    public void test_toRdf_t0091() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0091-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0091-in.jsonld");
    }

    @Test
    public void test_toRdf_t0036() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0036-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0036-in.jsonld");
    }

    @Test
    public void test_toRdf_t0092() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0092-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0092-in.jsonld");
    }

    @Test
    public void test_toRdf_t0041() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0041-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0041-in.jsonld");
    }

    @Test
    public void test_toRdf_t0093() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0093-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0093-in.jsonld");
    }

    @Test
    public void test_toRdf_t0042() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0042-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0042-in.jsonld");
    }

    @Test
    public void test_toRdf_t0094() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0094-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0094-in.jsonld");
    }

    @Test
    public void test_toRdf_t0043() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0043-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0043-in.jsonld");
    }

    @Test
    public void test_toRdf_t0095() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0095-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0095-in.jsonld");
    }

    @Test
    public void test_toRdf_t0044() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0044-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0044-in.jsonld");
    }

    @Test
    public void test_toRdf_t0096() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0096-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0096-in.jsonld");
    }

    @Test
    public void test_toRdf_t0045() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0045-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0045-in.jsonld");
    }

    @Test
    public void test_toRdf_t0097() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0097-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0097-in.jsonld");
    }

    @Test
    public void test_toRdf_t0046() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0046-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0046-in.jsonld");
    }

    @Test
    public void test_toRdf_t0098() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0098-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0098-in.jsonld");
    }

    @Test
    public void test_toRdf_t0047() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0047-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0047-in.jsonld");
    }

    @Test
    public void test_toRdf_t0099() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0099-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0099-in.jsonld");
    }

    @Test
    public void test_toRdf_t0048() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0048-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0048-in.jsonld");
    }

    @Test
    public void test_toRdf_t0100() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0100-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0100-in.jsonld");
    }

    @Test
    public void test_toRdf_t0049() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0049-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0049-in.jsonld");
    }

    @Test
    public void test_toRdf_t0101() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0101-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0101-in.jsonld");
    }

    @Test
    public void test_toRdf_t0050() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0050-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0050-in.jsonld");
    }

    @Test
    public void test_toRdf_t0102() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0102-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0102-in.jsonld");
    }

    @Test
    public void test_toRdf_t0051() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0051-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0051-in.jsonld");
    }

    @Test
    public void test_toRdf_t0103() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0103-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0103-in.jsonld");
    }

    @Test
    public void test_toRdf_t0052() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0052-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0052-in.jsonld");
    }

    @Test
    public void test_toRdf_t0104() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0104-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0104-in.jsonld");
    }

    @Test
    public void test_toRdf_t0053() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0053-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0053-in.jsonld");
    }

    @Test
    public void test_toRdf_t0105() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0105-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0105-in.jsonld");
    }

    @Test
    public void test_toRdf_t0054() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0054-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0054-in.jsonld");
    }

    @Test
    public void test_toRdf_t0106() throws Exception {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0106-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0106-in.jsonld");
    }

    /**
     * JSON-LD from RDF
     * fromRdf tests have input and expected documents, and may have options.
     * The expected results can be compared using JSON object comparison with the processor output.
     */
    @Test
    public void test_fromRdf_t0001() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0001-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0001-in.nq");
    }

    @Test
    public void test_fromRdf_t0002() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0002-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0002-in.nq");
    }

    @Test
    public void test_fromRdf_t0003() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0003-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0003-in.nq");
    }

    @Test
    public void test_fromRdf_t0004() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0004-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0004-in.nq");
    }

    @Test
    public void test_fromRdf_t0005() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0005-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0005-in.nq");
    }

    @Test
    public void test_fromRdf_t0022() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0022-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0022-in.nq");
    }

    @Test
    public void test_fromRdf_t0015() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0015-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0015-in.nq");
    }

    @Test
    public void test_fromRdf_t0009() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0009-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0009-in.nq");
    }

    @Test
    public void test_fromRdf_t0018() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0018-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0018-in.nq");
    }

    @Test
    public void test_fromRdf_t0016() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0016-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0016-in.nq");
    }

    @Test
    public void test_fromRdf_t0013() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0013-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0013-in.nq");
    }

    @Test
    public void test_fromRdf_t0020() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0020-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0020-in.nq");
    }

    @Test
    public void test_fromRdf_t0010() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0010-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0010-in.nq");
    }

    @Test
    public void test_fromRdf_t0017() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0017-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0017-in.nq");
    }

    @Test
    public void test_fromRdf_t0008() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0008-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0008-in.nq");
    }

    @Test
    public void test_fromRdf_t0011() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0011-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0011-in.nq");
    }

    @Test
    public void test_fromRdf_t0007() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0007-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0007-in.nq");
    }

    @Test
    public void test_fromRdf_t0019() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0019-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0019-in.nq");
    }

    @Test
    public void test_fromRdf_t0012() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0012-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0012-in.nq");
    }

    @Test
    public void test_fromRdf_t0006() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0006-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0006-in.nq");
    }

    @Test
    public void test_fromRdf_t0014() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0014-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0014-in.nq");
    }

    @Test
    public void test_fromRdf_t0021() throws Exception {
        fromRdfTest("http://json-ld.org/test-suite/tests/fromRdf-0021-out.jsonld", "http://json-ld.org/test-suite/tests/fromRdf-0021-in.nq");
    }
}
