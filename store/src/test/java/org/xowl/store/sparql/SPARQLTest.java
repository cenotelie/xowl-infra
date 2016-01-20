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

package org.xowl.store.sparql;

import org.junit.Ignore;
import org.junit.Test;
import org.xowl.utils.collections.Couple;

/**
 * All SPARQL tests
 *
 * @author Laurent Wouters
 */
@Ignore("Not ready yet")
public class SPARQLTest extends BaseSPARQLTest {
    @Test
    public void testPositiveSyntax_syntax_aggregate_07_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-07.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_11_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-11.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_15_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-15.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_service_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/syntax-service-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_SELECTscope1_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-SELECTscope1.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope5_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope5.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-01.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_04() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-04.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_05a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-05a.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_01() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_subquery_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-subquery-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_10_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-10.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope1_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope1.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_subquery_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-subquery-03.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_05() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-05.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope4_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope4.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_06_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-06.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_SELECTscope3_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-SELECTscope3.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindingBINDscopes_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope3_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope3.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_hex_encoded_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-03.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_09() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-09.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_03() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-03.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_08() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-08.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-03.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_02() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_04_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-04.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_03a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-03a.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_subquery_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-subquery-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_05_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-05.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bind_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bind-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope2_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope2.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_service_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/syntax-service-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_12_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-12.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_05_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-05.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pp_in_collection() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pp-in-collection.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_08_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-08.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_propertyPaths_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-propertyPaths-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_09_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-09.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_construct_where_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-construct-where-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_13_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-13.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_02a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-02a.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_04_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-04.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_construct_where_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-construct-where-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-03.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_06() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-06.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_07() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-07.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_unescaped_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-03.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_backslash_escaped_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_14_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-14.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_minus_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-minus-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_service_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/syntax-service-03.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_03() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-03.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_08() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-08.rq");
    }

    @Test
    public void testNegativeSyntax_Group_7() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group07.rq");
    }

    @Test
    public void testNegativeSyntax_constructwhere05___CONSTRUCT_WHERE() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere05.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_07_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-07.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_04() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-04.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_08_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-08.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_05_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-05.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_02() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-02.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_03_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-03.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope8_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope8.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_06_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-06.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_7b() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-07b.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_01_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-01.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_06() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-06.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_5() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-05.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_07() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-07.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_SELECTscope2() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-SELECTscope2.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_11() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-11.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope6_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope6.rq");
    }

    @Test
    public void testNegativeSyntax_COUNT_11() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg11.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_6() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-05.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_01() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-01.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_7() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-07.ru");
    }

    @Test
    public void testNegativeSyntax_COUNT_12() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg12.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_3() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-03.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_04_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-04.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_8() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-08.ru");
    }

    @Test
    public void testNegativeSyntax_COUNT_10() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg10.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_3b() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-03b.ru");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_9() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-09.ru");
    }

    @Test
    public void testNegativeSyntax_Group_6() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group06.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_12() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-12.rq");
    }

    @Test
    public void testNegativeSyntax_COUNT_8() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg08.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope7_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope7.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_02_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-02.rq");
    }

    @Test
    public void testNegativeSyntax_COUNT_9() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg09.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_10() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-10.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_13() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-13.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_bindings_09_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-09.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_05() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-05.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_09() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-09.rq");
    }

    @Test
    public void testNegativeSyntax_constructwhere06___CONSTRUCT_WHERE() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere06.rq");
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_DATA_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01s.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_ADD_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_ADD_7() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-07.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_3__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_2__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_ALL() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-all-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_COPY_6() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DROP_SILENT_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/drop-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_COPY_SILENT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/copy-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_COPY_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_WHERE_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01f.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_4b() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-04b.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-02.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERT_same_bnode_twice() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05a.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05a-g1-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05a-g3-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_WHERE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-02s.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_WHERE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01s.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_COPY_SILENT_TO_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/copy-to-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_COPY_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_5() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-03-pre.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-03-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_5b() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-05b.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-05.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_LOAD_SILENT_INTO() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/load-silent-into.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_DATA_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01s.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_INSERT_same_bnode_twice_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05-g1-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05-g1-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05-g1-pre.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_ADD_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_6() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_7() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-07.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_WHERE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01s.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02s.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_Simple_insert_data_named_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-named1.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-02.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_6b() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-05b.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-06.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-06.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_3__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_INSERTing_the_same_bnode_with_two_INSERT_WHERE_statement_within_one_request_is_NOT_the_same_bnode() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-pre.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-g3-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_4__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-01.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-02.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERT_02() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-02-pre.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-02-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-02-g1-post.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_1__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s2.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_1b() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-01b.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-01b.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_DROP_GRAPH() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-graph-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_2__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02s.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_INSERT_USING_01() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-g1-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-g2-pre.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-g1-post.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-g2-post.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_4__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_SILENT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/move-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_INSERT_03() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03-g1-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03-g1-post.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_NAMED() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-named-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_insert_data_named_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-named1.ru", new Couple[]{}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_SILENT_TO_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/add-to-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_1c() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-01c.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-01b.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_SILENT_GRAPH_iri() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/clear-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_DATA_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01s.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_DROP_SILENT_GRAPH_iri() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/drop-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_WHERE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01s.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_2__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_COPY_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-post.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_INSERTing_the_same_bnode_with_INSERT_DATA_into_two_different_Graphs_is_the_same_bnode() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-same-bnode.ru", new Couple[]{}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05a-g3-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_2__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_ADD_8() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-08.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_DATA_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-02s.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_CREATE_SILENT_iri() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/create-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_1__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Simple_insert_data_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-spo1.ru", new Couple[]{}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_1__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s2.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_INSERT_04() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04-g1-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04-g1-post.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_WHERE_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01f.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_COPY_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_COPY_7() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-07.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_SILENT_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/clear-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_MOVE_7() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-07.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_SILENT_TO_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/move-to-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_ADD_SILENT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/add-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_insert_data_named_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-named2.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo2.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_GRAPH() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-graph-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_1__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_DATA_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01f.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_DROP_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_INSERTing_the_same_bnode_with_two_INSERT_WHERE_statement_within_one_request_is_NOT_the_same_bnode_even_if_both_WHERE_clauses_have_the_empty_solution_mapping_as_the_only_solution_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode2.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-pre.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-g3-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_DROP_ALL() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-all-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_DATA_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01f.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_DROP_NAMED() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-named-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERT_01() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-01-pre.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-01-post.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_ADD_6() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-02-pre.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-02-post.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_LOAD_SILENT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/load-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testQueryEvaluation_Error_in_AVG() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-err-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-err-01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-err-01.srx");
    }

    @Test
    public void testQueryEvaluation_COUNT_7() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg07.srx");
    }

    @Test
    public void testQueryEvaluation_constructwhere02___CONSTRUCT_WHERE() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere02result.ttl");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_subClassOf() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs04.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs04.srx");
    }

    @Test
    public void testQueryEvaluation_REPLACE___with_overlapping_pattern() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/replace02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data3.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/replace02.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_04_rq__bug_fixing_test() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-04.srx");
    }

    @Test
    public void testQueryEvaluation_tvs02___TSV_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv02.tsv");
    }

    @Test
    public void testQueryEvaluation_STRBEFORE___datatyping() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strbefore02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data4.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strbefore02.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_02_rq__simple_combined_query() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-02.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_03_rq__combined_query_with_complex_class_description() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-02.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-03.srx");
    }

    @Test
    public void testQueryEvaluation_Calculate_which_sets_are_subsets_of_others__exclude_A_subsetOf_A_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subset-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/set-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subset-02.srx");
    }

    @Test
    public void testQueryEvaluation_simple_5() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple5.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple5.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_to_show_that_neither_literals_in_subject_position_nor_newly_introduced_surrogate_blank_nodes_are_to_be_returned_in_query_answers() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs13.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs13.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs13.srx");
    }

    @Test
    public void testQueryEvaluation_Group_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group-data-1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group02.srx");
    }

    @Test
    public void testQueryEvaluation_bind05___BIND_fixed_data_for_OWL_DL() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind05.srx");
    }

    @Test
    public void testQueryEvaluation_Exists_with_one_constant() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists01.srx");
    }

    @Test
    public void testQueryEvaluation_UCASE__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/ucase01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/ucase01.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_13_rq__sameAs() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-13.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-08.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-13.srx");
    }

    @Test
    public void testQueryEvaluation_Plain_literals_with_language_tag_are_not_the_same_as_the_same_literal_without() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/plainLit.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/plainLit.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/plainLit.srx");
    }

    @Test
    public void testQueryEvaluation_paper_sparqldl_Q1_rdfs() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q1-rdfs.srx");
    }

    @Test
    public void testQueryEvaluation_LCASE__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/lcase01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/lcase01.srx");
    }

    @Test
    public void testQueryEvaluation_bind06___BIND_fixed_data_for_OWL_DL() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind06.srx");
    }

    @Test
    public void testQueryEvaluation_IF___error_propogation() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/if02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/if02.srx");
    }

    @Test
    public void testQueryEvaluation_agg_empty_group() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-empty-group.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/empty.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-empty-group.srx");
    }

    @Test
    public void testQueryEvaluation_bind03___BIND_fixed_data_for_OWL_DL() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind03.srx");
    }

    @Test
    public void testQueryEvaluation__pp31__Operator_precedence_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p2.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_containers() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs12.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs12.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs12.srx");
    }

    @Test
    public void testQueryEvaluation__pp25__Diamond__with_loop_____p_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-2-2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond-loop.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-loop-2.srx");
    }

    @Test
    public void testQueryEvaluation_isNumeric__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/isnumeric01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/isnumeric01.srx");
    }

    @Test
    public void testQueryEvaluation_SHA512___on_Unicode_data() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha512-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/hash-unicode.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha512-02.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_transitivity_of_subPropertyOf() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs10.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs10.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs10.srx");
    }

    @Test
    public void testQueryEvaluation_simple_7() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple7.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple7.srx");
    }

    @Test
    public void testQueryEvaluation_paper_sparqldl_Q4() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q4.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q4.srx");
    }

    @Test
    public void testQueryEvaluation_Post_query_VALUES_with_2_obj_vars__2_rows_with_UNDEF() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data05.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values05.srx");
    }

    @Test
    public void testQueryEvaluation_tsv03___TSV_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv03.tsv");
    }

    @Test
    public void testQueryEvaluation_SECONDS__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/seconds-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/seconds-01.srx");
    }

    @Test
    public void testQueryEvaluation_NOT_IN_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/notin01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/notin01.srx");
    }

    @Test
    public void testQueryEvaluation_sq10___Subquery_with_exists() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq10.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq10.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq10.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_09_rq__undist_vars_test() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-09.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-07.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-09.srx");
    }

    @Test
    public void testQueryEvaluation__pp05__Zero_length_path() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp05.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp05.srx");
    }

    @Test
    public void testQueryEvaluation_CONTAINS__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/contains01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/contains01.srx");
    }

    @Test
    public void testQueryEvaluation_IRI___URI__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/iri01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/iri01.srx");
    }

    @Test
    public void testQueryEvaluation_STRUUID___pattern_match() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/struuid01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data-empty.nt"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/struuid01.srx");
    }

    @Test
    public void testQueryEvaluation_plus_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/plus-1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data-builtin-3.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/plus-1.srx");
    }

    @Test
    public void testQueryEvaluation_Protect_from_error_in_AVG() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-err-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-err-02.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-err-02.srx");
    }

    @Test
    public void testQueryEvaluation_COUNT_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg02.srx");
    }

    @Test
    public void testQueryEvaluation_sq03___Subquery_within_graph_pattern__graph_variable_is_not_bound() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq01.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq03.srx");
    }

    @Test
    public void testQueryEvaluation__pp21__Diamond_____p_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-2-2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-2.srx");
    }

    @Test
    public void testQueryEvaluation__pp29__Diamond__with_loop_____p_2__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-3-4.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond-loop.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-loop-6.srx");
    }

    @Test
    public void testQueryEvaluation_sq09___Nested_Subqueries() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq09.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq09.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq09.srx");
    }

    @Test
    public void testQueryEvaluation_GROUP_CONCAT_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-2.srx");
    }

    @Test
    public void testQueryEvaluation_jsonres04___JSON_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/jsonres04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/jsonres04.srj");
    }

    @Test
    public void testQueryEvaluation_Post_subquery_VALUES() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/inline02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data02.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/inline02.srx");
    }

    @Test
    public void testQueryEvaluation_SUM_with_GROUP_BY() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-sum-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-sum-02.srx");
    }

    @Test
    public void testQueryEvaluation__pp11__Simple_path_and_two_paths_to_same_target_node() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp11.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp11.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp11.srx");
    }

    @Test
    public void testQueryEvaluation_STRBEFORE__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strbefore01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strbefore01a.srx");
    }

    @Test
    public void testQueryEvaluation_filtered_subclass_query_with__hasChild_some_Thing__restriction() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent10.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent10.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_07_rq__two_distinguished_variables___undist_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-06.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-07.srx");
    }

    @Test
    public void testQueryEvaluation_RAND__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/rand01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/rand01.srx");
    }

    @Test
    public void testQueryEvaluation_SHA1__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha1-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha1-01.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_transitivity_of_subClassOf() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs09.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs09.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs09.srx");
    }

    @Test
    public void testQueryEvaluation_Calculate_which_sets_have_the_same_elements() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/set-equals-1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/set-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/set-equals-1.srx");
    }

    @Test
    public void testQueryEvaluation_Post_query_VALUES_with_subj_obj_vars__2_rows_with_UNDEF() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values08.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data08.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values08.srx");
    }

    @Test
    public void testQueryEvaluation_Post_query_VALUES_with__OPTIONAL__obj_var__1_row() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data07.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values07.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_combining_subPropertyOf_and_domain() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs03.srx");
    }

    @Test
    public void testQueryEvaluation_Literal_with_language_tag_test() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/lang.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/lang.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/lang.srx");
    }

    @Test
    public void testQueryEvaluation__pp20__Diamond_____p_2_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-2-1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-1.srx");
    }

    @Test
    public void testQueryEvaluation_parent_query_with__hasChild_some_Thing__restriction() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent3.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent3.srx");
    }

    @Test
    public void testQueryEvaluation_TZ__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/tz-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/tz-01.srx");
    }

    @Test
    public void testQueryEvaluation_Medical__temporal_proximity_by_exclusion__NOT_EXISTS_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/temporalProximity01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/temporalProximity01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/temporalProximity01.srx");
    }

    @Test
    public void testQueryEvaluation_STRENDS__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/ends01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/ends01.srx");
    }

    @Test
    public void testQueryEvaluation_Group_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group-data-1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group01.srx");
    }

    @Test
    public void testQueryEvaluation_MIN() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-min-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-min-01.srx");
    }

    @Test
    public void testQueryEvaluation_Inline_VALUES_graph_pattern() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/inline01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/inline01.srx");
    }

    @Test
    public void testQueryEvaluation__pp09__Reverse_sequence_path() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp09.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp09.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp09.srx");
    }

    @Test
    public void testQueryEvaluation_sq06___Subquery_with_graph_pattern__from_named_applies() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq05.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq06.srx");
    }

    @Test
    public void testQueryEvaluation_parent_query_with__hasChild_min_1_Female__restriction() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent6.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent6.srx");
    }

    @Test
    public void testQueryEvaluation_REPLACE__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/replace01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data3.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/replace01.srx");
    }

    @Test
    public void testQueryEvaluation_Post_query_VALUES_with_2_obj_vars__1_row() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values03.srx");
    }

    @Test
    public void testQueryEvaluation__pp13__Zero_Length_Paths_with_Literals() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp13.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp13.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp13.srx");
    }

    @Test
    public void testQueryEvaluation__pp26__Diamond__with_loop_____p_2_4_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-3-1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond-loop.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-loop-3.srx");
    }

    @Test
    public void testQueryEvaluation__pp14__Star_path_over_foaf_knows() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp14.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp14.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp14.srx");
    }

    @Test
    public void testQueryEvaluation_sq02___Subquery_within_graph_pattern__graph_variable_is_bound() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq01.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq02.srx");
    }

    @Test
    public void testQueryEvaluation_bind03___BIND() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind03.srx");
    }

    @Test
    public void testQueryEvaluation_COUNT_8b() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg08b.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg08.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg08b.srx");
    }

    @Test
    public void testQueryEvaluation_RDF_test_for_blank_node_cardinalities() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf03.srx");
    }

    @Test
    public void testQueryEvaluation_REPLACE___with_captured_substring() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/replace03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data3.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/replace03.srx");
    }

    @Test
    public void testQueryEvaluation__pp24__Diamond__with_loop_____p_2_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-2-1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond-loop.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-loop-1.srx");
    }

    @Test
    public void testQueryEvaluation_FLOOR__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/floor01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/floor01.srx");
    }

    @Test
    public void testQueryEvaluation_SHA256___on_Unicode_data() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha256-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/hash-unicode.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha256-02.srx");
    }

    @Test
    public void testQueryEvaluation_Expression_raise_an_error() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp02.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp02.srx");
    }

    @Test
    public void testQueryEvaluation_ROUND__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/round01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/round01.srx");
    }

    @Test
    public void testQueryEvaluation_COUNT_5() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg05.srx");
    }

    @Test
    public void testQueryEvaluation_STRAFTER__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strafter01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strafter01a.srx");
    }

    @Test
    public void testQueryEvaluation__pp16__Duplicate_paths_and_cycles_through_foaf_knows_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp14.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp16.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp16.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_rdfs_subPropertyOf() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs02.srx");
    }

    @Test
    public void testQueryEvaluation_Calculate_which_sets_are_subsets_of_others__include_A_subsetOf_A_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subset-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/set-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subset-01.srx");
    }

    @Test
    public void testQueryEvaluation_IN_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/in01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/in01.srx");
    }

    @Test
    public void testQueryEvaluation_IN_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/in02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/in02.srx");
    }

    @Test
    public void testQueryEvaluation_sq11___Subquery_limit_per_resource() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq11.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq11.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq11.srx");
    }

    @Test
    public void testQueryEvaluation_tsv01___TSV_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv01.tsv");
    }

    @Test
    public void testQueryEvaluation_sparqldl_12_rq__range_test() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-12.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-11.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-12.srx");
    }

    @Test
    public void testQueryEvaluation_Post_query_VALUES_with_2_obj_vars__1_row_with_UNDEF() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data04.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values04.srx");
    }

    @Test
    public void testQueryEvaluation_SERVICE_test_3() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service03.rq", new String[]{}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service03.srx");
    }

    @Test
    public void testQueryEvaluation__pp08__Reverse_path() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp08.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp08.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp08.srx");
    }

    @Test
    public void testQueryEvaluation_simple_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple1.srx");
    }

    @Test
    public void testQueryEvaluation__pp15__Zero_Length_Paths_on_an_empty_graph() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp15.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/empty.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp15.srx");
    }

    @Test
    public void testQueryEvaluation_sq01___Subquery_within_graph_pattern() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq01.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq01.srx");
    }

    @Test
    public void testQueryEvaluation_bind07___BIND_fixed_data_for_OWL_DL() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind07.srx");
    }

    @Test
    public void testQueryEvaluation_STRDT___TypeErrors() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strdt03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strdt03.srx");
    }

    @Test
    public void testQueryEvaluation_CONCAT__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/concat01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/concat01.srx");
    }

    @Test
    public void testQueryEvaluation_parent_query_with_distinguished_variable() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent2.srx");
    }

    @Test
    public void testQueryEvaluation_SHA512__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha512-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha512-01.srx");
    }

    @Test
    public void testQueryEvaluation__pp01__Simple_path() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp01.srx");
    }

    @Test
    public void testQueryEvaluation_SERVICE_test_6() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service06.rq", new String[]{}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service06.srx");
    }

    @Test
    public void testQueryEvaluation_TIMEZONE__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/timezone-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/timezone-01.srx");
    }

    @Test
    public void testQueryEvaluation__pp07__Path_with_one_graph() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp07.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp07.srx");
    }

    @Test
    public void testQueryEvaluation_Nested_positive_exists() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists04.srx");
    }

    @Test
    public void testQueryEvaluation_Subtraction_with_MINUS_from_a_partially_bound_minuend() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/part-minuend.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/part-minuend.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/part-minuend.srx");
    }

    @Test
    public void testQueryEvaluation_bind01___BIND_fixed_data_for_OWL_DL() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind01.srx");
    }

    @Test
    public void testQueryEvaluation_D_Entailment_test_to_show_that__neither_literals_in_subject_position_nor_newly_introduced_surrogate_blank_nodes_are_to_be_returned_in_query_answers() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/d-ent-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/d-ent-01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/d-ent-01.srx");
    }

    @Test
    public void testQueryEvaluation_Nested_negative_exists_in_positive_exists() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists05.srx");
    }

    @Test
    public void testQueryEvaluation__pp03__Simple_path_with_loop() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp03.srx");
    }

    @Test
    public void testQueryEvaluation_GROUP_CONCAT_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-1.srx");
    }

    @Test
    public void testQueryEvaluation__pp12__Variable_length_path_and_two_paths_to_same_target_node() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp12.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp11.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp12.srx");
    }

    @Test
    public void testQueryEvaluation_sq08___Subquery_with_aggregate() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq08.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq08.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq08.srx");
    }

    @Test
    public void testQueryEvaluation_bind02___BIND() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind02.srx");
    }

    @Test
    public void testQueryEvaluation_BNODE__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/bnode02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/bnode02.srx");
    }

    @Test
    public void testQueryEvaluation__pp04__Variable_length_path_with_loop() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp04.srx");
    }

    @Test
    public void testQueryEvaluation_YEAR__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/year-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/year-01.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_range() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs07.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs07.srx");
    }

    @Test
    public void testQueryEvaluation_SERVICE_test_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/data01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service01.srx");
    }

    @Test
    public void testQueryEvaluation_Post_query_VALUES_with_obj_var__1_row() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data02.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values02.srx");
    }

    @Test
    public void testQueryEvaluation_RDF_inference_test() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf02.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf02.srx");
    }

    @Test
    public void testQueryEvaluation_parent_query_with__hasChild_min_1__restriction() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent4.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent4.srx");
    }

    @Test
    public void testQueryEvaluation__pp36__Arbitrary_path_with_bound_endpoints() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp36.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/clique3.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp36.srx");
    }

    @Test
    public void testQueryEvaluation_parent_query_with__hasChild_exactly_1_Female__restriction() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent8.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent8.srx");
    }

    @Test
    public void testQueryEvaluation_Positive_EXISTS_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/exists-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/set-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/exists-01.srx");
    }

    @Test
    public void testQueryEvaluation_sq05___Subquery_within_graph_pattern__from_named_applies() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq05.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq05.srx");
    }

    @Test
    public void testQueryEvaluation_Expression_has_variable_that_may_be_unbound() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp07.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp07.srx");
    }

    @Test
    public void testQueryEvaluation_Calculate_proper_subset() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subset-03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/set-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subset-03.srx");
    }

    @Test
    public void testQueryEvaluation_SERVICE_test_5() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/data05.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service05.srx");
    }

    @Test
    public void testQueryEvaluation_Positive_EXISTS_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/exists-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/set-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/exists-02.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_rdf_XMLLiteral() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs08.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs08.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs08.srx");
    }

    @Test
    public void testQueryEvaluation_Post_query_VALUES_with_subj_var__1_row() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values01.srx");
    }

    @Test
    public void testQueryEvaluation_Expression_is_equality() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp01.srx");
    }

    @Test
    public void testQueryEvaluation_bind08___BIND() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind08.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind08.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_subProperty_and_instances() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs11.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs11.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs11.srx");
    }

    @Test
    public void testQueryEvaluation_HOURS__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/hours-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/hours-01.srx");
    }

    @Test
    public void testQueryEvaluation_simple_8() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple8.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple8.srx");
    }

    @Test
    public void testQueryEvaluation_SHA256__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha256-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha256-01.srx");
    }

    @Test
    public void testQueryEvaluation_Post_query_VALUES_with_pred_var__1_row() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/data06.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bindings/values06.srx");
    }

    @Test
    public void testQueryEvaluation_simple_4() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple4.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple4.srx");
    }

    @Test
    public void testQueryEvaluation_BNODE_str_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/bnode01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/bnode01.srx");
    }

    @Test
    public void testQueryEvaluation_Exists_with_ground_triple() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists02.srx");
    }

    @Test
    public void testQueryEvaluation_parent_query_with__hasChild_some_Female__restriction() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent5.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent5.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_subClassOf_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs05.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs05.srx");
    }

    @Test
    public void testQueryEvaluation_bind04___BIND() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind04.srx");
    }

    @Test
    public void testQueryEvaluation_Expression_has_undefined_variable() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp06.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp06.srx");
    }

    @Test
    public void testQueryEvaluation__pp27__Diamond__with_loop_____p__3_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-3-2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond-loop.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-loop-4.srx");
    }

    @Test
    public void testQueryEvaluation_STRBEFORE___2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strbefore01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strbefore01.srx");
    }

    @Test
    public void testQueryEvaluation__pp35__Named_Graph_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-ng-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/ng-01.ttl", "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/ng-02.ttl", "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/ng-03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-ng-01.srx");
    }

    @Test
    public void testQueryEvaluation_parent_query_with__hasChild_max_1_Female__restriction() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent7.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent7.srx");
    }

    @Test
    public void testQueryEvaluation_SUM() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-sum-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-sum-01.srx");
    }

    @Test
    public void testQueryEvaluation__pp22__Diamond__with_tail_____p_3_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-2-3.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond-tail.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-tail-1.srx");
    }

    @Test
    public void testQueryEvaluation_jsonres01___JSON_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/jsonres01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/jsonres01.srj");
    }

    @Test
    public void testQueryEvaluation_Expression_may_return_no_value() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp05.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp05.srx");
    }

    @Test
    public void testQueryEvaluation_paper_sparqldl_Q5() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q5.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q5.srx");
    }

    @Test
    public void testQueryEvaluation__pp02__Star_path() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp02.srx");
    }

    @Test
    public void testQueryEvaluation_simple_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple2.srx");
    }

    @Test
    public void testQueryEvaluation_Group_4() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group-data-1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group04.srx");
    }

    @Test
    public void testQueryEvaluation_STRSTARTS__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/starts01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/starts01.srx");
    }

    @Test
    public void testQueryEvaluation_bind10___BIND_scoping___Variable_in_filter_not_in_scope() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind10.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind10.srx");
    }

    @Test
    public void testQueryEvaluation_simple_triple_pattern_match() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf04.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf04.srx");
    }

    @Test
    public void testQueryEvaluation__pp32__Operator_precedence_3() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p3.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p3.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p3.srx");
    }

    @Test
    public void testQueryEvaluation_Subsets_by_exclusion__MINUS_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subsetByExcl02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subsetByExcl.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subsetByExcl02.srx");
    }

    @Test
    public void testQueryEvaluation_COALESCE__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/coalesce01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data-coalesce.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/coalesce01.srx");
    }

    @Test
    public void testQueryEvaluation__pp30__Operator_precedence_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p1.srx");
    }

    @Test
    public void testQueryEvaluation_SHA1___on_Unicode_data() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha1-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/hash-unicode.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/sha1-02.srx");
    }

    @Test
    public void testQueryEvaluation_CONCAT___2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/concat02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/concat02.srx");
    }

    @Test
    public void testQueryEvaluation_subclass_query_with__hasChild_some_Thing__restriction() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent9.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/parent9.srx");
    }

    @Test
    public void testQueryEvaluation_paper_sparqldl_Q1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q1.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q1.srx");
    }

    @Test
    public void testQueryEvaluation_SERVICE_test_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service02.rq", new String[]{}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service02.srx");
    }

    @Test
    public void testQueryEvaluation_MINUTES__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/minutes-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/minutes-01.srx");
    }

    @Test
    public void testQueryEvaluation_SERVICE_test_7() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/data07.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service07.srx");
    }

    @Test
    public void testQueryEvaluation_MIN_with_GROUP_BY() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-min-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-min-02.srx");
    }

    @Test
    public void testQueryEvaluation_constructwhere03___CONSTRUCT_WHERE() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere03result.ttl");
    }

    @Test
    public void testQueryEvaluation_sparqldl_08_rq__two_distinguished_variables___undist_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-08.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-06.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-08.srx");
    }

    @Test
    public void testQueryEvaluation_Reuse_a_project_expression_variable_in_order_by() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp04.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp04.srx");
    }

    @Test
    public void testQueryEvaluation_MONTH__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/month-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/month-01.srx");
    }

    @Test
    public void testQueryEvaluation__pp33__Operator_precedence_4() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p4.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p3.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-p4.srx");
    }

    @Test
    public void testQueryEvaluation_jsonres03___JSON_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/jsonres03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/jsonres03.srj");
    }

    @Test
    public void testQueryEvaluation_bind02___BIND_fixed_data_for_OWL_DL() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind02.srx");
    }

    @Test
    public void testQueryEvaluation_Subtraction_with_MINUS_from_a_fully_bound_minuend() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/full-minuend.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/full-minuend.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/full-minuend.srx");
    }

    @Test
    public void testQueryEvaluation_CEIL__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/ceil01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/ceil01.srx");
    }

    @Test
    public void testQueryEvaluation_jsonres02___JSON_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/jsonres02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/json-res/jsonres02.srj");
    }

    @Test
    public void testQueryEvaluation__pp37__Nested_____() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp37.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp37.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp37.srx");
    }

    @Test
    public void testQueryEvaluation_bind06___BIND() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind06.srx");
    }

    @Test
    public void testQueryEvaluation_bind08___BIND_fixed_data_for_OWL_DL() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind08.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind08.srx");
    }

    @Test
    public void testQueryEvaluation_bind05___BIND() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind05.srx");
    }

    @Test
    public void testQueryEvaluation_COUNT_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.srx");
    }

    @Test
    public void testQueryEvaluation_bind04___BIND_fixed_data_for_OWL_DL() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/bind04.srx");
    }

    @Test
    public void testQueryEvaluation_DAY__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/day-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/day-01.srx");
    }

    @Test
    public void testQueryEvaluation_Group_3() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group-data-1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group03.srx");
    }

    @Test
    public void testQueryEvaluation_bind11___BIND_scoping___Variable_in_filter_in_scope() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind11.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind11.srx");
    }

    @Test
    public void testQueryEvaluation_NOW__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/now01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/now01.srx");
    }

    @Test
    public void testQueryEvaluation_IF__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/if01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/if01.srx");
    }

    @Test
    public void testQueryEvaluation_SAMPLE() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-sample-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-sample-01.srx");
    }

    @Test
    public void testQueryEvaluation_constructwhere01___CONSTRUCT_WHERE() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere01result.ttl");
    }

    @Test
    public void testQueryEvaluation_STRLEN__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/length01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/length01.srx");
    }

    @Test
    public void testQueryEvaluation_sq14___limit_by_resource() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq14.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq14.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq14-out.ttl");
    }

    @Test
    public void testQueryEvaluation_Exists_within_graph_pattern() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists01.ttl", "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists02.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/exists/exists03.srx");
    }

    @Test
    public void testQueryEvaluation_paper_sparqldl_Q3() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q3.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q3.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_10_rq__undist_vars_test() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-10.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-07.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-10.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_domain() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs06.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs06.srx");
    }

    @Test
    public void testQueryEvaluation__pp06__Path_with_two_graphs() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp061.ttl", "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp062.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp06.srx");
    }

    @Test
    public void testQueryEvaluation_Reuse_a_project_expression_variable_in_select() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/project-expression/projexp03.srx");
    }

    @Test
    public void testQueryEvaluation_COUNT_6() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg06.srx");
    }

    @Test
    public void testQueryEvaluation_simple_6() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple6.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple6.srx");
    }

    @Test
    public void testQueryEvaluation_UUID___pattern_match() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/uuid01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data-empty.nt"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/uuid01.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_01_rq__triple_pattern() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-01.srx");
    }

    @Test
    public void testQueryEvaluation__pp23__Diamond__with_tail_____p_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-2-2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond-tail.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-tail-2.srx");
    }

    @Test
    public void testQueryEvaluation_paper_sparqldl_Q2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/paper-sparqldl-Q2.srx");
    }

    @Test
    public void testQueryEvaluation_STRAFTER___2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strafter01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strafter01.srx");
    }

    @Test
    public void testQueryEvaluation_MD5___over_Unicode_data() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/md5-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/md5-02.srx");
    }

    @Test
    public void testQueryEvaluation_bind07___BIND() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind07.srx");
    }

    @Test
    public void testQueryEvaluation_NOT_IN_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/notin02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/notin02.srx");
    }

    @Test
    public void testQueryEvaluation_sq04___Subquery_within_graph_pattern__default_graph_does_not_apply() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq04.rdf", "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq01.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq04.srx");
    }

    @Test
    public void testQueryEvaluation_AVG() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-avg-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-avg-01.srx");
    }

    @Test
    public void testQueryEvaluation_STRLANG__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strlang01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strlang01.srx");
    }

    @Test
    public void testQueryEvaluation_bnodes_are_not_existentials() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/owlds01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/owlds01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/owlds01.srx");
    }

    @Test
    public void testQueryEvaluation_STRDT_STR___() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strdt02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strdt02.srx");
    }

    @Test
    public void testQueryEvaluation_STRAFTER___datatyping() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strafter02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data4.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strafter02.srx");
    }

    @Test
    public void testQueryEvaluation_COUNT_4() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg04.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg04.srx");
    }

    @Test
    public void testQueryEvaluation_sq07___Subquery_with_from_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq07.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq05.rdf"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq07.srx");
    }

    @Test
    public void testQueryEvaluation_sq12___Subquery_in_CONSTRUCT_with_built_ins() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq12.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq12.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq12_out.ttl");
    }

    @Test
    public void testQueryEvaluation_sparqldl_05_rq__simple_undistinguished_variable_test_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-05.srx");
    }

    @Test
    public void testQueryEvaluation_COUNT_3() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg03.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_11_rq__domain_test() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-11.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-11.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-11.srx");
    }

    @Test
    public void testQueryEvaluation_constructwhere04___CONSTRUCT_WHERE() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere04.rq", new String[]{}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/construct/constructwhere04result.ttl");
    }

    @Test
    public void testQueryEvaluation_Group_5() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group05.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group-data-2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/grouping/group05.srx");
    }

    @Test
    public void testQueryEvaluation_SUBSTR____2_argument_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/substring02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/substring02.srx");
    }

    @Test
    public void testQueryEvaluation_STRLANG_STR___() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strlang02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strlang02.srx");
    }

    @Test
    public void testQueryEvaluation_bnodes_are_not_existentials_with_answer() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/owlds02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/owlds02.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/owlds02.srx");
    }

    @Test
    public void testQueryEvaluation__pp28a__Diamond__with_loop______p__p__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-3-3.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/data-diamond-loop.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/diamond-loop-5a.srx");
    }

    @Test
    public void testQueryEvaluation_sq13___Subqueries_don_t_inject_bindings() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq11.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq11.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/subquery/sq11.srx");
    }

    @Test
    public void testQueryEvaluation_RDFS_inference_test_rdfs_subPropertyOf_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdfs01.srx");
    }

    @Test
    public void testQueryEvaluation__pp34__Named_Graph_1() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-ng-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/ng-01.ttl", "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/ng-02.ttl", "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/ng-03.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/path-ng-01.srx");
    }

    @Test
    public void testQueryEvaluation_GROUP_CONCAT_with_SEPARATOR() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-3.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-1.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-groupconcat-3.srx");
    }

    @Test
    public void testQueryEvaluation_sparqldl_06_rq__cycle_of_undistinguished_variables() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-06.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/data-06.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/sparqldl-06.srx");
    }

    @Test
    public void testQueryEvaluation_SUBSTR____3_argument_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/substring01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/substring01.srx");
    }

    @Test
    public void testQueryEvaluation_Subsets_by_exclusion__NOT_EXISTS_() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subsetByExcl01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subsetByExcl.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/negation/subsetByExcl01.srx");
    }

    @Test
    public void testQueryEvaluation_ABS__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/abs01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/abs01.srx");
    }

    @Test
    public void testQueryEvaluation__pp10__Path_with_negation() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp10.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp10.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/property-path/pp10.srx");
    }

    @Test
    public void testQueryEvaluation_SERVICE_test_4a_with_VALUES_clause() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service04a.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/data04.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/service/service04.srx");
    }

    @Test
    public void testQueryEvaluation_MAX_with_GROUP_BY() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-max-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-max-02.srx");
    }

    @Test
    public void testQueryEvaluation_RDF_inference_test_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf01.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/rdf01.srx");
    }

    @Test
    public void testQueryEvaluation_MD5__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/md5-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/md5-01.srx");
    }

    @Test
    public void testQueryEvaluation_ENCODE_FOR_URI__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/encode01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/encode01.srx");
    }

    @Test
    public void testQueryEvaluation_AVG_with_GROUP_BY() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-avg-02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-avg-02.srx");
    }

    @Test
    public void testQueryEvaluation_plus_2() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/plus-2.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data-builtin-3.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/plus-2.srx");
    }

    @Test
    public void testQueryEvaluation_bind01___BIND() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/bind/bind01.srx");
    }

    @Test
    public void testQueryEvaluation_STRDT__() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strdt01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strdt01.srx");
    }

    @Test
    public void testQueryEvaluation_simple_3() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple3.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/simple3.srx");
    }

    @Test
    public void testQueryEvaluation_STRLANG___TypeErrors() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strlang03.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/functions/strlang03.srx");
    }

    @Test
    public void testQueryEvaluation_MAX() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-max-01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-numeric.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/aggregates/agg-max-01.srx");
    }

    @Test
    public void testQueryEvaluation_csv03___CSV_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/data2.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv03.csv");
    }

    @Test
    public void testQueryEvaluation_csv01___CSV_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv01.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv01.csv");
    }

    @Test
    public void testQueryEvaluation_cvs02___CSV_Result_Format() {
        testQueryEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv02.rq", new String[]{"http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/data.ttl"}, "http://www.w3.org/2009/sparql/docs/tests/data-sparql11/csv-tsv-res/csvtsv02.csv");
    }
}
