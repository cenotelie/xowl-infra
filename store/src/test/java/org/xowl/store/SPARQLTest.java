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

import org.junit.Test;

/**
 * All SPARQL tests
 *
 * @author Laurent Wouters
 */
public class SPARQLTest extends BaseSPARQLTest {
    @Test
    public void testPositiveSyntax_syntax_aggregate_07_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-07.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_01() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-01.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_09() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-09.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_service_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/syntax-service-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-02.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_06() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-06.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_service_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/syntax-service-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_05_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-05.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_02a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-02a.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope1_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope1.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_04_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-04.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_subquery_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-subquery-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_06_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-06.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_11_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-11.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_subquery_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-subquery-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-01.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_hex_encoded_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_service_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/syntax-service-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_minus_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-minus-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_construct_where_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-construct-where-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_08_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-08.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_15_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-15.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_03a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-03a.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pp_in_collection() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pp-in-collection.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope5_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope5.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_SELECTscope1_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-SELECTscope1.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope2_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope2.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope4_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope4.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-03.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_04() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-04.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_02() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_propertyPaths_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-propertyPaths-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_10_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-10.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_05a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-05a.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindingBINDscopes_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_construct_where_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-construct-where-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_12_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-12.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-02.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_backslash_escaped_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-01.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_08() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-08.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_09_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-09.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bind_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bind-02.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_05() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-05.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_14_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-14.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_subquery_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-subquery-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_13_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-13.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_03() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_05_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-05.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_unescaped_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-03.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_07() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-07.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_04_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-04.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope3_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope3.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_SELECTscope3_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-SELECTscope3.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope6_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope6.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_06() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-06.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_07_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-07.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_08() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-08.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_05_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-05.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_bindings_09_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-09.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_01() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-01.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_07() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-07.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_12() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-12.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_09() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-09.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_08_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-08.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_10() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-10.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_SELECTscope2() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-SELECTscope2.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_04_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-04.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_06_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-06.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_05() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-05.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_03_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-03.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope7_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope7.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope8_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope8.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_13() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-13.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_01_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-01.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_03() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-03.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_11() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-11.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_02() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-02.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_04() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-04.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_02_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-02.rq");
    }
}
