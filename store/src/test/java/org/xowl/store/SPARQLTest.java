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
import org.xowl.utils.collections.Couple;

/**
 * All SPARQL tests
 *
 * @author Laurent Wouters
 */
public class SPARQLTest extends BaseSPARQLTest {
    @Test
    public void testPositiveSyntax_syntax_BINDscope5_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope5.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_02a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-02a.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_06_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-06.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_10_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-10.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_14_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-14.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_09() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-09.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_subquery_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-subquery-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_service_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/syntax-service-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_propertyPaths_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-propertyPaths-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_04_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-04.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope4_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope4.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_05a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-05a.rq");
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
    public void testPositiveSyntax_syntax_minus_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-minus-01.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_03() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindings_03a_rq_with_VALUES_clause() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-03a.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_SELECTscope3_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-SELECTscope3.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_11_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-11.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_04_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-04.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_07_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-07.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_service_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-fed/syntax-service-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-02.rq");
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
    public void testPositiveSyntax_syntax_aggregate_15_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-15.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_09_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-09.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_unescaped_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_subquery_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-subquery-02.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_04() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-04.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_construct_where_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-construct-where-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope3_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope3.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_12_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-12.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bindingBINDscopes_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_05_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-05.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_05_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-05.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_BINDscope2_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope2.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_bind_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bind-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_construct_where_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-construct-where-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_select_expr_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-select-expr-02.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_05() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-05.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_06() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-06.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_backslash_escaped_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-02.rq");
    }

    @Test
    public void testPositiveSyntax_PrefixName_with_hex_encoded_colons() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/qname-escape-02.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_08() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-08.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_02() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-02.rq");
    }

    @Test
    public void testPositiveSyntax_syn_pname_07() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-07.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_not_exists_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-not-exists-02.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_13_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-13.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_01_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_exists_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-exists-03.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_oneof_02_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-oneof-02.rq");
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
    public void testPositiveSyntax_syn_pname_01() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-pname-01.rq");
    }

    @Test
    public void testPositiveSyntax_syntax_aggregate_03_rq() {
        testPositiveSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-aggregate-03.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_02() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-02.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_10() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-10.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_9() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-09.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_07() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-07.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_06_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-06.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_03_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-03.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_03() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-03.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_07_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-07.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_04_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-04.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_13() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-13.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope8_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope8.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_7b() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-07b.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_01() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-01.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_02_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-02.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_3() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-03.ru");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_8() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-08.ru");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope7_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope7.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_11() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-11.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_05_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-05.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_7() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-07.ru");
    }

    @Test
    public void testNegativeSyntax_syntax_SELECTscope2() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-SELECTscope2.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_BINDscope6_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-BINDscope6.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_5() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-05.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_01_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-01.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_05() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-05.rq");
    }

    @Test
    public void testNegativeSyntax_syntax_bindings_09_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syntax-bindings-09.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_6() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-05.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_09() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-09.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_12() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-12.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_08_rq() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-08.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_06() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-06.rq");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_04() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-04.rq");
    }

    @Test
    public void testNegativeSyntax_DELETE_INSERT_3b() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-03b.ru");
    }

    @Test
    public void testNegativeSyntax_syn_bad_pname_08() {
        testNegativeSyntax("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/syntax-query/syn-bad-pname-08.rq");
    }

    @Test
    public void testUpdateEvaluation_COPY_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02s.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_ADD_6() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_2__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02s.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_DROP_NAMED() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-named-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERT_USING_01() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-g1-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-g2-pre.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-g1-post.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-using-01-g2-post.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_WHERE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01s.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_WHERE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-02s.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_LOAD_SILENT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/load-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_MOVE_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_1c() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-01c.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-01b.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_COPY_SILENT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/copy-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_2__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_WHERE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01s.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERT_02() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-02-pre.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-02-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-02-g1-post.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_SILENT_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/clear-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_3__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_5b() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-05b.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-05.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERT_01() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-01-pre.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-01-post.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_MOVE_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_GRAPH() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-graph-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_4b() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-04b.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-02.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_COPY_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_4__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-02.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_WHERE_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01f.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_COPY_6() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_7() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-07.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_4__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_ADD_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-post.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_SILENT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/add-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_1b() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-01b.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-01b.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERT_same_bnode_twice() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05-g1-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05-g1-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05-g1-pre.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_INSERTing_the_same_bnode_with_INSERT_DATA_into_two_different_Graphs_is_the_same_bnode() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-same-bnode.ru", new Couple[]{}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05a-g3-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_DROP_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_insert_data_named_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-named2.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo2.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_2__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_DATA_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01s.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_DROP_ALL() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-all-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_MOVE_SILENT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/move-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_insert_data_named_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-named1.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_insert_data_named_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-named1.ru", new Couple[]{}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_DATA_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01s.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_2__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_ADD_8() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-08.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_WHERE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01s.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_1__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s2.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_INSERT_04() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04-g1-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-04-g1-post.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_DROP_GRAPH() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-graph-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/drop/drop-g2.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_CREATE_SILENT_iri() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/create-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_COPY_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_6b() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-05b.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-06.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-06.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_MOVE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_1__USING_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-using-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-01.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_3__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_SILENT_TO_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/move-to-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_Simple_insert_data_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-data-spo1.ru", new Couple[]{}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/spo.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERTing_the_same_bnode_with_two_INSERT_WHERE_statement_within_one_request_is_NOT_the_same_bnode_even_if_both_WHERE_clauses_have_the_empty_solution_mapping_as_the_only_solution_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode2.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-pre.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-g3-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_DROP_SILENT_GRAPH_iri() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/drop-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_INSERTing_the_same_bnode_with_two_INSERT_WHERE_statement_within_one_request_is_NOT_the_same_bnode() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-pre.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-where-same-bnode-g3-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_DELETE_INSERT_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-insert/delete-insert-post-02.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_7() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-07.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01f.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_COPY_SILENT_TO_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/copy-to-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_ADD_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_MOVE_7() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-07.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_NAMED() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-named-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-02.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_WHERE_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-where-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-where/delete-post-01f.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_DROP_SILENT_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/drop-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_1__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_DATA_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01f.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_DATA_1() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01s.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_COPY_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_DATA_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01f.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-02s.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_ALL() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-all-01.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g1.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/clear-g2.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/clear/empty.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_Graph_specific_DELETE_1__WITH_() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-with-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-01.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-02.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-pre-03.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-01s2.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-02f.ttl", "http://example.org/g2"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete/delete-post-03f.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_CLEAR_SILENT_GRAPH_iri() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/clear-silent.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", null)}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/spo.ttl", null)});
    }

    @Test
    public void testUpdateEvaluation_LOAD_SILENT_INTO() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/load-silent-into.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_Simple_DELETE_DATA_4() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-data-04.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-pre-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/delete-data/delete-post-01f.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_INSERT_03() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03-pre.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03-g1-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03-post.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-03-g1-post.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_5() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-05.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-03-pre.ttl", "http://example.org/g3")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-03-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_INSERT_same_bnode_twice_2() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05a.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05a-g1-pre.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/basic-update/insert-05a-g3-post.ttl", "http://example.org/g3")});
    }

    @Test
    public void testUpdateEvaluation_COPY_7() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-07.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/copy/copy-01.ttl", "http://example.org/g1")});
    }

    @Test
    public void testUpdateEvaluation_ADD_3() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-03.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-02-pre.ttl", "http://example.org/g2")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-01-pre.ttl", "http://example.org/g1"), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/add/add-02-post.ttl", "http://example.org/g2")});
    }

    @Test
    public void testUpdateEvaluation_ADD_SILENT_TO_DEFAULT() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/update-silent/add-to-default-silent.ru", new Couple[]{}, new Couple[]{});
    }

    @Test
    public void testUpdateEvaluation_MOVE_6() {
        testUpdateEvaluation("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-06.ru", new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-default.ttl", null), new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", "http://example.org/g1")}, new Couple[]{new Couple<String, String>("http://www.w3.org/2009/sparql/docs/tests/data-sparql11/move/move-01.ttl", null)});
    }

}
