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

package org.xowl.store;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for JSON-LD
 *
 * @author Laurent Wouters
 */
public class JSONLDTest extends BaseJSONLDTest {

    @Test
    public void test_toRdf_t0055() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0055-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0055-in.jsonld");
    }

    @Test
    public void test_toRdf_t0001() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0001-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0001-in.jsonld");
    }

    @Test
    public void test_toRdf_t0107() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0107-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0107-in.jsonld");
    }

    @Test
    public void test_toRdf_t0056() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0056-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0056-in.jsonld");
    }

    @Test
    public void test_toRdf_t0108() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0108-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0108-in.jsonld");
    }

    @Test
    public void test_toRdf_t0057() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0057-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0057-in.jsonld");
    }

    @Test
    public void test_toRdf_t0109() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0109-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0109-in.jsonld");
    }

    @Test
    public void test_toRdf_t0058() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0058-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0058-in.jsonld");
    }

    @Test
    public void test_toRdf_t0002() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0002-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0002-in.jsonld");
    }

    @Test
    public void test_toRdf_t0110() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0110-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0110-in.jsonld");
    }

    @Test
    public void test_toRdf_t0059() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0059-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0059-in.jsonld");
    }

    @Test
    public void test_toRdf_t0003() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0003-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0003-in.jsonld");
    }

    @Test
    public void test_toRdf_t0111() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0111-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0111-in.jsonld");
    }

    @Test
    public void test_toRdf_t0060() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0060-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0060-in.jsonld");
    }

    @Test
    public void test_toRdf_t0004() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0004-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0004-in.jsonld");
    }

    @Test
    public void test_toRdf_t0112() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0112-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0112-in.jsonld");
    }

    @Test
    public void test_toRdf_t0061() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0061-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0061-in.jsonld");
    }

    @Test
    public void test_toRdf_t0005() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0005-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0005-in.jsonld");
    }

    @Test
    public void test_toRdf_t0113() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0113-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0113-in.jsonld");
    }

    @Test
    public void test_toRdf_t0062() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0062-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0062-in.jsonld");
    }

    @Test
    public void test_toRdf_t0006() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0006-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0006-in.jsonld");
    }

    @Test
    public void test_toRdf_t0114() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0114-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0114-in.jsonld");
    }

    @Test
    public void test_toRdf_t0063() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0063-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0063-in.jsonld");
    }

    @Test
    public void test_toRdf_t0007() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0007-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0007-in.jsonld");
    }

    @Test
    public void test_toRdf_t0115() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0115-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0115-in.jsonld");
    }

    @Test
    public void test_toRdf_t0064() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0064-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0064-in.jsonld");
    }

    @Test
    public void test_toRdf_t0008() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0008-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0008-in.jsonld");
    }

    @Test
    public void test_toRdf_t0116() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0116-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0116-in.jsonld");
    }

    @Test
    public void test_toRdf_t0065() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0065-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0065-in.jsonld");
    }

    @Test
    public void test_toRdf_t0009() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0009-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0009-in.jsonld");
    }

    @Test
    public void test_toRdf_t0117() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0117-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0117-in.jsonld");
    }

    @Test
    public void test_toRdf_t0066() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0066-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0066-in.jsonld");
    }

    @Test
    public void test_toRdf_t0010() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0010-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0010-in.jsonld");
    }

    @Ignore("Uses generalized RDF graphs")
    @Test
    public void test_toRdf_t0118() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0118-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0118-in.jsonld");
    }

    @Test
    public void test_toRdf_t0067() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0067-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0067-in.jsonld");
    }

    @Test
    public void test_toRdf_t0011() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0011-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0011-in.jsonld");
    }

    @Test
    public void test_toRdf_t0068() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0068-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0068-in.jsonld");
    }

    @Test
    public void test_toRdf_t0012() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0012-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0012-in.jsonld");
    }

    @Test
    public void test_toRdf_t0119() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0119-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0119-in.jsonld");
    }

    @Test
    public void test_toRdf_t0069() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0069-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0069-in.jsonld");
    }

    @Test
    public void test_toRdf_t0013() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0013-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0013-in.jsonld");
    }

    @Test
    public void test_toRdf_t0070() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0070-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0070-in.jsonld");
    }

    @Test
    public void test_toRdf_t0014() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0014-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0014-in.jsonld");
    }

    @Test
    public void test_toRdf_t0071() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0071-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0071-in.jsonld");
    }

    @Test
    public void test_toRdf_t0015() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0015-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0015-in.jsonld");
    }

    @Test
    public void test_toRdf_t0072() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0072-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0072-in.jsonld");
    }

    @Test
    public void test_toRdf_t0016() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0016-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0016-in.jsonld");
    }

    @Test
    public void test_toRdf_t0073() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0073-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0073-in.jsonld");
    }

    @Test
    public void test_toRdf_t0017() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0017-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0017-in.jsonld");
    }

    @Test
    public void test_toRdf_t0074() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0074-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0074-in.jsonld");
    }

    @Test
    public void test_toRdf_t0018() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0018-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0018-in.jsonld");
    }

    @Test
    public void test_toRdf_t0075() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0075-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0075-in.jsonld");
    }

    @Test
    public void test_toRdf_t0019() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0019-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0019-in.jsonld");
    }

    @Test
    public void test_toRdf_t0076() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0076-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0076-in.jsonld");
    }

    @Test
    public void test_toRdf_t0020() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0020-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0020-in.jsonld");
    }

    @Test
    public void test_toRdf_t0077() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0077-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0077-in.jsonld");
    }

    @Test
    public void test_toRdf_t0022() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0022-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0022-in.jsonld");
    }

    @Test
    public void test_toRdf_t0078() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0078-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0078-in.jsonld");
    }

    @Test
    public void test_toRdf_t0023() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0023-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0023-in.jsonld");
    }

    @Test
    public void test_toRdf_t0079() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0079-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0079-in.jsonld");
    }

    @Test
    public void test_toRdf_t0024() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0024-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0024-in.jsonld");
    }

    @Test
    public void test_toRdf_t0080() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0080-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0080-in.jsonld");
    }

    @Test
    public void test_toRdf_t0025() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0025-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0025-in.jsonld");
    }

    @Test
    public void test_toRdf_t0081() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0081-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0081-in.jsonld");
    }

    @Test
    public void test_toRdf_t0026() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0026-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0026-in.jsonld");
    }

    @Test
    public void test_toRdf_t0082() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0082-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0082-in.jsonld");
    }

    @Test
    public void test_toRdf_t0027() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0027-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0027-in.jsonld");
    }

    @Test
    public void test_toRdf_t0083() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0083-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0083-in.jsonld");
    }

    @Test
    public void test_toRdf_t0028() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0028-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0028-in.jsonld");
    }

    @Test
    public void test_toRdf_t0084() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0084-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0084-in.jsonld");
    }

    @Test
    public void test_toRdf_t0029() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0029-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0029-in.jsonld");
    }

    @Test
    public void test_toRdf_t0085() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0085-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0085-in.jsonld");
    }

    @Test
    public void test_toRdf_t0030() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0030-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0030-in.jsonld");
    }

    @Test
    public void test_toRdf_t0086() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0086-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0086-in.jsonld");
    }

    @Test
    public void test_toRdf_t0031() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0031-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0031-in.jsonld");
    }

    @Test
    public void test_toRdf_t0087() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0087-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0087-in.jsonld");
    }

    @Test
    public void test_toRdf_t0032() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0032-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0032-in.jsonld");
    }

    @Test
    public void test_toRdf_t0088() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0088-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0088-in.jsonld");
    }

    @Test
    public void test_toRdf_t0033() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0033-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0033-in.jsonld");
    }

    @Test
    public void test_toRdf_t0089() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0089-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0089-in.jsonld");
    }

    @Test
    public void test_toRdf_t0034() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0034-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0034-in.jsonld");
    }

    @Test
    public void test_toRdf_t0090() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0090-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0090-in.jsonld");
    }

    @Test
    public void test_toRdf_t0035() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0035-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0035-in.jsonld");
    }

    @Test
    public void test_toRdf_t0091() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0091-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0091-in.jsonld");
    }

    @Test
    public void test_toRdf_t0036() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0036-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0036-in.jsonld");
    }

    @Test
    public void test_toRdf_t0092() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0092-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0092-in.jsonld");
    }

    @Test
    public void test_toRdf_t0041() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0041-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0041-in.jsonld");
    }

    @Test
    public void test_toRdf_t0093() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0093-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0093-in.jsonld");
    }

    @Test
    public void test_toRdf_t0042() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0042-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0042-in.jsonld");
    }

    @Test
    public void test_toRdf_t0094() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0094-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0094-in.jsonld");
    }

    @Test
    public void test_toRdf_t0043() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0043-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0043-in.jsonld");
    }

    @Test
    public void test_toRdf_t0095() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0095-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0095-in.jsonld");
    }

    @Test
    public void test_toRdf_t0044() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0044-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0044-in.jsonld");
    }

    @Test
    public void test_toRdf_t0096() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0096-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0096-in.jsonld");
    }

    @Test
    public void test_toRdf_t0045() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0045-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0045-in.jsonld");
    }

    @Test
    public void test_toRdf_t0097() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0097-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0097-in.jsonld");
    }

    @Test
    public void test_toRdf_t0046() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0046-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0046-in.jsonld");
    }

    @Test
    public void test_toRdf_t0098() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0098-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0098-in.jsonld");
    }

    @Test
    public void test_toRdf_t0047() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0047-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0047-in.jsonld");
    }

    @Test
    public void test_toRdf_t0099() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0099-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0099-in.jsonld");
    }

    @Test
    public void test_toRdf_t0048() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0048-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0048-in.jsonld");
    }

    @Test
    public void test_toRdf_t0100() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0100-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0100-in.jsonld");
    }

    @Test
    public void test_toRdf_t0049() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0049-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0049-in.jsonld");
    }

    @Test
    public void test_toRdf_t0101() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0101-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0101-in.jsonld");
    }

    @Test
    public void test_toRdf_t0050() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0050-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0050-in.jsonld");
    }

    @Test
    public void test_toRdf_t0102() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0102-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0102-in.jsonld");
    }

    @Test
    public void test_toRdf_t0051() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0051-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0051-in.jsonld");
    }

    @Test
    public void test_toRdf_t0103() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0103-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0103-in.jsonld");
    }

    @Test
    public void test_toRdf_t0052() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0052-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0052-in.jsonld");
    }

    @Test
    public void test_toRdf_t0104() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0104-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0104-in.jsonld");
    }

    @Test
    public void test_toRdf_t0053() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0053-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0053-in.jsonld");
    }

    @Test
    public void test_toRdf_t0105() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0105-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0105-in.jsonld");
    }

    @Test
    public void test_toRdf_t0054() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0054-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0054-in.jsonld");
    }

    @Test
    public void test_toRdf_t0106() {
        toRdfTest("http://json-ld.org/test-suite/tests/toRdf-0106-out.nq", "http://json-ld.org/test-suite/tests/toRdf-0106-in.jsonld");
    }

    @Test
    public void test_normalize_t0045() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0045-out.nq", "http://json-ld.org/test-suite/tests/normalize-0045-in.jsonld");
    }

    @Test
    public void test_normalize_t0046() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0046-out.nq", "http://json-ld.org/test-suite/tests/normalize-0046-in.jsonld");
    }

    @Test
    public void test_normalize_t0047() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0047-out.nq", "http://json-ld.org/test-suite/tests/normalize-0047-in.jsonld");
    }

    @Test
    public void test_normalize_t0048() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0048-out.nq", "http://json-ld.org/test-suite/tests/normalize-0048-in.jsonld");
    }

    @Test
    public void test_normalize_t0049() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0049-out.nq", "http://json-ld.org/test-suite/tests/normalize-0049-in.jsonld");
    }

    @Test
    public void test_normalize_t0050() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0050-out.nq", "http://json-ld.org/test-suite/tests/normalize-0050-in.jsonld");
    }

    @Test
    public void test_normalize_t0051() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0051-out.nq", "http://json-ld.org/test-suite/tests/normalize-0051-in.jsonld");
    }

    @Test
    public void test_normalize_t0052() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0052-out.nq", "http://json-ld.org/test-suite/tests/normalize-0052-in.jsonld");
    }

    @Test
    public void test_normalize_t0053() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0053-out.nq", "http://json-ld.org/test-suite/tests/normalize-0053-in.jsonld");
    }

    @Test
    public void test_normalize_t0054() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0054-out.nq", "http://json-ld.org/test-suite/tests/normalize-0054-in.jsonld");
    }

    @Test
    public void test_normalize_t0055() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0055-out.nq", "http://json-ld.org/test-suite/tests/normalize-0055-in.jsonld");
    }

    @Test
    public void test_normalize_t0056() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0056-out.nq", "http://json-ld.org/test-suite/tests/normalize-0056-in.jsonld");
    }

    @Test
    public void test_normalize_t0057() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0057-out.nq", "http://json-ld.org/test-suite/tests/normalize-0057-in.jsonld");
    }

    @Test
    public void test_normalize_t0001() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0001-out.nq", "http://json-ld.org/test-suite/tests/normalize-0001-in.jsonld");
    }

    @Test
    public void test_normalize_t0002() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0002-out.nq", "http://json-ld.org/test-suite/tests/normalize-0002-in.jsonld");
    }

    @Test
    public void test_normalize_t0003() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0003-out.nq", "http://json-ld.org/test-suite/tests/normalize-0003-in.jsonld");
    }

    @Test
    public void test_normalize_t0004() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0004-out.nq", "http://json-ld.org/test-suite/tests/normalize-0004-in.jsonld");
    }

    @Test
    public void test_normalize_t0005() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0005-out.nq", "http://json-ld.org/test-suite/tests/normalize-0005-in.jsonld");
    }

    @Test
    public void test_normalize_t0006() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0006-out.nq", "http://json-ld.org/test-suite/tests/normalize-0006-in.jsonld");
    }

    @Test
    public void test_normalize_t0007() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0007-out.nq", "http://json-ld.org/test-suite/tests/normalize-0007-in.jsonld");
    }

    @Test
    public void test_normalize_t0008() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0008-out.nq", "http://json-ld.org/test-suite/tests/normalize-0008-in.jsonld");
    }

    @Test
    public void test_normalize_t0009() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0009-out.nq", "http://json-ld.org/test-suite/tests/normalize-0009-in.jsonld");
    }

    @Test
    public void test_normalize_t0010() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0010-out.nq", "http://json-ld.org/test-suite/tests/normalize-0010-in.jsonld");
    }

    @Test
    public void test_normalize_t0011() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0011-out.nq", "http://json-ld.org/test-suite/tests/normalize-0011-in.jsonld");
    }

    @Test
    public void test_normalize_t0012() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0012-out.nq", "http://json-ld.org/test-suite/tests/normalize-0012-in.jsonld");
    }

    @Test
    public void test_normalize_t0013() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0013-out.nq", "http://json-ld.org/test-suite/tests/normalize-0013-in.jsonld");
    }

    @Test
    public void test_normalize_t0014() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0014-out.nq", "http://json-ld.org/test-suite/tests/normalize-0014-in.jsonld");
    }

    @Test
    public void test_normalize_t0015() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0015-out.nq", "http://json-ld.org/test-suite/tests/normalize-0015-in.jsonld");
    }

    @Test
    public void test_normalize_t0016() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0016-out.nq", "http://json-ld.org/test-suite/tests/normalize-0016-in.jsonld");
    }

    @Test
    public void test_normalize_t0017() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0017-out.nq", "http://json-ld.org/test-suite/tests/normalize-0017-in.jsonld");
    }

    @Test
    public void test_normalize_t0018() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0018-out.nq", "http://json-ld.org/test-suite/tests/normalize-0018-in.jsonld");
    }

    @Test
    public void test_normalize_t0019() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0019-out.nq", "http://json-ld.org/test-suite/tests/normalize-0019-in.jsonld");
    }

    @Test
    public void test_normalize_t0020() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0020-out.nq", "http://json-ld.org/test-suite/tests/normalize-0020-in.jsonld");
    }

    @Test
    public void test_normalize_t0021() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0021-out.nq", "http://json-ld.org/test-suite/tests/normalize-0021-in.jsonld");
    }

    @Test
    public void test_normalize_t0022() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0022-out.nq", "http://json-ld.org/test-suite/tests/normalize-0022-in.jsonld");
    }

    @Test
    public void test_normalize_t0023() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0023-out.nq", "http://json-ld.org/test-suite/tests/normalize-0023-in.jsonld");
    }

    @Test
    public void test_normalize_t0024() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0024-out.nq", "http://json-ld.org/test-suite/tests/normalize-0024-in.jsonld");
    }

    @Test
    public void test_normalize_t0025() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0025-out.nq", "http://json-ld.org/test-suite/tests/normalize-0025-in.jsonld");
    }

    @Test
    public void test_normalize_t0026() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0026-out.nq", "http://json-ld.org/test-suite/tests/normalize-0026-in.jsonld");
    }

    @Test
    public void test_normalize_t0027() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0027-out.nq", "http://json-ld.org/test-suite/tests/normalize-0027-in.jsonld");
    }

    @Test
    public void test_normalize_t0028() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0028-out.nq", "http://json-ld.org/test-suite/tests/normalize-0028-in.jsonld");
    }

    @Test
    public void test_normalize_t0029() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0029-out.nq", "http://json-ld.org/test-suite/tests/normalize-0029-in.jsonld");
    }

    @Test
    public void test_normalize_t0030() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0030-out.nq", "http://json-ld.org/test-suite/tests/normalize-0030-in.jsonld");
    }

    @Test
    public void test_normalize_t0031() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0031-out.nq", "http://json-ld.org/test-suite/tests/normalize-0031-in.jsonld");
    }

    @Test
    public void test_normalize_t0032() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0032-out.nq", "http://json-ld.org/test-suite/tests/normalize-0032-in.jsonld");
    }

    @Test
    public void test_normalize_t0033() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0033-out.nq", "http://json-ld.org/test-suite/tests/normalize-0033-in.jsonld");
    }

    @Test
    public void test_normalize_t0034() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0034-out.nq", "http://json-ld.org/test-suite/tests/normalize-0034-in.jsonld");
    }

    @Test
    public void test_normalize_t0035() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0035-out.nq", "http://json-ld.org/test-suite/tests/normalize-0035-in.jsonld");
    }

    @Test
    public void test_normalize_t0036() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0036-out.nq", "http://json-ld.org/test-suite/tests/normalize-0036-in.jsonld");
    }

    @Test
    public void test_normalize_t0037() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0037-out.nq", "http://json-ld.org/test-suite/tests/normalize-0037-in.jsonld");
    }

    @Test
    public void test_normalize_t0038() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0038-out.nq", "http://json-ld.org/test-suite/tests/normalize-0038-in.jsonld");
    }

    @Test
    public void test_normalize_t0039() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0039-out.nq", "http://json-ld.org/test-suite/tests/normalize-0039-in.jsonld");
    }

    @Test
    public void test_normalize_t0040() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0040-out.nq", "http://json-ld.org/test-suite/tests/normalize-0040-in.jsonld");
    }

    @Test
    public void test_normalize_t0041() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0041-out.nq", "http://json-ld.org/test-suite/tests/normalize-0041-in.jsonld");
    }

    @Test
    public void test_normalize_t0042() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0042-out.nq", "http://json-ld.org/test-suite/tests/normalize-0042-in.jsonld");
    }

    @Test
    public void test_normalize_t0043() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0043-out.nq", "http://json-ld.org/test-suite/tests/normalize-0043-in.jsonld");
    }

    @Test
    public void test_normalize_t0044() {
        normalizeTest("http://json-ld.org/test-suite/tests/normalize-0044-out.nq", "http://json-ld.org/test-suite/tests/normalize-0044-in.jsonld");
    }
}
