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
 * All RDF/XML loading tests
 *
 * @author Laurent Wouters
 */
public class RDFXMLTest extends BaseRDFXMLTest {
    @Test
    public void test_amp_in_url_test001() throws Exception {
        testXMLEval("amp-in-url/test001.rdf", "amp-in-url/test001.nt");
    }

    @Test
    public void test_datatypes_test001() throws Exception {
        testXMLEval("datatypes/test001.rdf", "datatypes/test001.nt");
    }

    @Test
    public void test_datatypes_test002() throws Exception {
        testXMLEval("datatypes/test002.rdf", "datatypes/test002.nt");
    }

    @Test
    public void test_rdf_charmod_literals_test001() throws Exception {
        testXMLEval("rdf-charmod-literals/test001.rdf", "rdf-charmod-literals/test001.nt");
    }

    @Test
    public void test_rdf_charmod_uris_test001() throws Exception {
        testXMLEval("rdf-charmod-uris/test001.rdf", "rdf-charmod-uris/test001.nt");
    }

    @Test
    public void test_rdf_charmod_uris_test002() throws Exception {
        testXMLEval("rdf-charmod-uris/test002.rdf", "rdf-charmod-uris/test002.nt");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_error001() throws Exception {
        testXMLNegativeSyntax("rdf-containers-syntax-vs-schema/error001.rdf");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_error002() throws Exception {
        testXMLNegativeSyntax("rdf-containers-syntax-vs-schema/error002.rdf");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_test001() throws Exception {
        testXMLEval("rdf-containers-syntax-vs-schema/test001.rdf", "rdf-containers-syntax-vs-schema/test001.nt");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_test002() throws Exception {
        testXMLEval("rdf-containers-syntax-vs-schema/test002.rdf", "rdf-containers-syntax-vs-schema/test002.nt");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_test003() throws Exception {
        testXMLEval("rdf-containers-syntax-vs-schema/test003.rdf", "rdf-containers-syntax-vs-schema/test003.nt");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_test004() throws Exception {
        testXMLEval("rdf-containers-syntax-vs-schema/test004.rdf", "rdf-containers-syntax-vs-schema/test004.nt");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_test006() throws Exception {
        testXMLEval("rdf-containers-syntax-vs-schema/test006.rdf", "rdf-containers-syntax-vs-schema/test006.nt");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_test007() throws Exception {
        testXMLEval("rdf-containers-syntax-vs-schema/test007.rdf", "rdf-containers-syntax-vs-schema/test007.nt");
    }

    @Test
    public void test_rdf_containers_syntax_vs_schema_test008() throws Exception {
        testXMLEval("rdf-containers-syntax-vs-schema/test008.rdf", "rdf-containers-syntax-vs-schema/test008.nt");
    }

    @Test
    public void test_rdf_element_not_mandatory_test001() throws Exception {
        testXMLEval("rdf-element-not-mandatory/test001.rdf", "rdf-element-not-mandatory/test001.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0001() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0001.rdf", "rdf-ns-prefix-confusion/test0001.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0003() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0003.rdf", "rdf-ns-prefix-confusion/test0003.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0004() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0004.rdf", "rdf-ns-prefix-confusion/test0004.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0005() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0005.rdf", "rdf-ns-prefix-confusion/test0005.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0006() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0006.rdf", "rdf-ns-prefix-confusion/test0006.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0009() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0009.rdf", "rdf-ns-prefix-confusion/test0009.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0010() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0010.rdf", "rdf-ns-prefix-confusion/test0010.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0011() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0011.rdf", "rdf-ns-prefix-confusion/test0011.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0012() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0012.rdf", "rdf-ns-prefix-confusion/test0012.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0013() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0013.rdf", "rdf-ns-prefix-confusion/test0013.nt");
    }

    @Test
    public void test_rdf_ns_prefix_confusion_test0014() throws Exception {
        testXMLEval("rdf-ns-prefix-confusion/test0014.rdf", "rdf-ns-prefix-confusion/test0014.nt");
    }

    @Test
    public void test_rdfms_abouteach_error001() throws Exception {
        testXMLNegativeSyntax("rdfms-abouteach/error001.rdf");
    }

    @Test
    public void test_rdfms_abouteach_error002() throws Exception {
        testXMLNegativeSyntax("rdfms-abouteach/error002.rdf");
    }

    @Test
    public void test_rdfms_difference_between_ID_and_about_error1() throws Exception {
        testXMLNegativeSyntax("rdfms-difference-between-ID-and-about/error1.rdf");
    }

    @Test
    public void test_rdfms_difference_between_ID_and_about_test1() throws Exception {
        testXMLEval("rdfms-difference-between-ID-and-about/test1.rdf", "rdfms-difference-between-ID-and-about/test1.nt");
    }

    @Test
    public void test_rdfms_difference_between_ID_and_about_test2() throws Exception {
        testXMLEval("rdfms-difference-between-ID-and-about/test2.rdf", "rdfms-difference-between-ID-and-about/test2.nt");
    }

    @Test
    public void test_rdfms_difference_between_ID_and_about_test3() throws Exception {
        testXMLEval("rdfms-difference-between-ID-and-about/test3.rdf", "rdfms-difference-between-ID-and-about/test3.nt");
    }

    @Test
    public void test_rdfms_duplicate_member_props_test001() throws Exception {
        testXMLEval("rdfms-duplicate-member-props/test001.rdf", "rdfms-duplicate-member-props/test001.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_error001() throws Exception {
        testXMLNegativeSyntax("rdfms-empty-property-elements/error001.rdf");
    }

    @Test
    public void test_rdfms_empty_property_elements_error002() throws Exception {
        testXMLNegativeSyntax("rdfms-empty-property-elements/error002.rdf");
    }

    @Test
    public void test_rdfms_empty_property_elements_test001() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test001.rdf", "rdfms-empty-property-elements/test001.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test002() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test002.rdf", "rdfms-empty-property-elements/test002.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test004() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test004.rdf", "rdfms-empty-property-elements/test004.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test005() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test005.rdf", "rdfms-empty-property-elements/test005.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test006() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test006.rdf", "rdfms-empty-property-elements/test006.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test007() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test007.rdf", "rdfms-empty-property-elements/test007.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test008() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test008.rdf", "rdfms-empty-property-elements/test008.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test010() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test010.rdf", "rdfms-empty-property-elements/test010.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test011() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test011.rdf", "rdfms-empty-property-elements/test011.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test012() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test012.rdf", "rdfms-empty-property-elements/test012.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test013() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test013.rdf", "rdfms-empty-property-elements/test013.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test014() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test014.rdf", "rdfms-empty-property-elements/test014.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test015() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test015.rdf", "rdfms-empty-property-elements/test015.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test016() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test016.rdf", "rdfms-empty-property-elements/test016.nt");
    }

    @Test
    public void test_rdfms_empty_property_elements_test017() throws Exception {
        testXMLEval("rdfms-empty-property-elements/test017.rdf", "rdfms-empty-property-elements/test017.nt");
    }

    @Test
    public void test_rdfms_identity_anon_resources_test001() throws Exception {
        testXMLEval("rdfms-identity-anon-resources/test001.rdf", "rdfms-identity-anon-resources/test001.nt");
    }

    @Test
    public void test_rdfms_identity_anon_resources_test002() throws Exception {
        testXMLEval("rdfms-identity-anon-resources/test002.rdf", "rdfms-identity-anon-resources/test002.nt");
    }

    @Test
    public void test_rdfms_identity_anon_resources_test003() throws Exception {
        testXMLEval("rdfms-identity-anon-resources/test003.rdf", "rdfms-identity-anon-resources/test003.nt");
    }

    @Test
    public void test_rdfms_identity_anon_resources_test004() throws Exception {
        testXMLEval("rdfms-identity-anon-resources/test004.rdf", "rdfms-identity-anon-resources/test004.nt");
    }

    @Test
    public void test_rdfms_identity_anon_resources_test005() throws Exception {
        testXMLEval("rdfms-identity-anon-resources/test005.rdf", "rdfms-identity-anon-resources/test005.nt");
    }

    @Test
    public void test_rdfms_not_id_and_resource_attr_test001() throws Exception {
        testXMLEval("rdfms-not-id-and-resource-attr/test001.rdf", "rdfms-not-id-and-resource-attr/test001.nt");
    }

    @Test
    public void test_rdfms_not_id_and_resource_attr_test002() throws Exception {
        testXMLEval("rdfms-not-id-and-resource-attr/test002.rdf", "rdfms-not-id-and-resource-attr/test002.nt");
    }

    @Test
    public void test_rdfms_not_id_and_resource_attr_test004() throws Exception {
        testXMLEval("rdfms-not-id-and-resource-attr/test004.rdf", "rdfms-not-id-and-resource-attr/test004.nt");
    }

    @Test
    public void test_rdfms_not_id_and_resource_attr_test005() throws Exception {
        testXMLEval("rdfms-not-id-and-resource-attr/test005.rdf", "rdfms-not-id-and-resource-attr/test005.nt");
    }

    @Test
    public void test_rdfms_para196_test001() throws Exception {
        testXMLEval("rdfms-para196/test001.rdf", "rdfms-para196/test001.nt");
    }

    @Test
    public void test_rdfms_rdf_id_error001() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-id/error001.rdf");
    }

    @Test
    public void test_rdfms_rdf_id_error002() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-id/error002.rdf");
    }

    @Test
    public void test_rdfms_rdf_id_error003() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-id/error003.rdf");
    }

    @Test
    public void test_rdfms_rdf_id_error004() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-id/error004.rdf");
    }

    @Test
    public void test_rdfms_rdf_id_error005() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-id/error005.rdf");
    }

    @Test
    public void test_rdfms_rdf_id_error006() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-id/error006.rdf");
    }

    @Test
    public void test_rdfms_rdf_id_error007() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-id/error007.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_001() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-001.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_002() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-002.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_003() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-003.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_004() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-004.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_005() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-005.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_006() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-006.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_007() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-007.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_008() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-008.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_009() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-009.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_010() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-010.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_011() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-011.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_012() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-012.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_013() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-013.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_014() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-014.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_015() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-015.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_016() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-016.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_017() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-017.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_018() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-018.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_019() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-019.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_error_020() throws Exception {
        testXMLNegativeSyntax("rdfms-rdf-names-use/error-020.rdf");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_001() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-001.rdf", "rdfms-rdf-names-use/test-001.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_002() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-002.rdf", "rdfms-rdf-names-use/test-002.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_003() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-003.rdf", "rdfms-rdf-names-use/test-003.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_004() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-004.rdf", "rdfms-rdf-names-use/test-004.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_005() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-005.rdf", "rdfms-rdf-names-use/test-005.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_006() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-006.rdf", "rdfms-rdf-names-use/test-006.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_007() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-007.rdf", "rdfms-rdf-names-use/test-007.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_008() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-008.rdf", "rdfms-rdf-names-use/test-008.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_009() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-009.rdf", "rdfms-rdf-names-use/test-009.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_010() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-010.rdf", "rdfms-rdf-names-use/test-010.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_011() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-011.rdf", "rdfms-rdf-names-use/test-011.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_012() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-012.rdf", "rdfms-rdf-names-use/test-012.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_013() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-013.rdf", "rdfms-rdf-names-use/test-013.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_014() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-014.rdf", "rdfms-rdf-names-use/test-014.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_015() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-015.rdf", "rdfms-rdf-names-use/test-015.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_016() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-016.rdf", "rdfms-rdf-names-use/test-016.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_017() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-017.rdf", "rdfms-rdf-names-use/test-017.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_018() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-018.rdf", "rdfms-rdf-names-use/test-018.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_019() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-019.rdf", "rdfms-rdf-names-use/test-019.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_020() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-020.rdf", "rdfms-rdf-names-use/test-020.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_021() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-021.rdf", "rdfms-rdf-names-use/test-021.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_022() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-022.rdf", "rdfms-rdf-names-use/test-022.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_023() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-023.rdf", "rdfms-rdf-names-use/test-023.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_024() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-024.rdf", "rdfms-rdf-names-use/test-024.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_025() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-025.rdf", "rdfms-rdf-names-use/test-025.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_026() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-026.rdf", "rdfms-rdf-names-use/test-026.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_027() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-027.rdf", "rdfms-rdf-names-use/test-027.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_028() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-028.rdf", "rdfms-rdf-names-use/test-028.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_029() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-029.rdf", "rdfms-rdf-names-use/test-029.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_030() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-030.rdf", "rdfms-rdf-names-use/test-030.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_031() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-031.rdf", "rdfms-rdf-names-use/test-031.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_032() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-032.rdf", "rdfms-rdf-names-use/test-032.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_033() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-033.rdf", "rdfms-rdf-names-use/test-033.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_034() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-034.rdf", "rdfms-rdf-names-use/test-034.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_035() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-035.rdf", "rdfms-rdf-names-use/test-035.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_036() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-036.rdf", "rdfms-rdf-names-use/test-036.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_test_037() throws Exception {
        testXMLEval("rdfms-rdf-names-use/test-037.rdf", "rdfms-rdf-names-use/test-037.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_warn_001() throws Exception {
        testXMLEval("rdfms-rdf-names-use/warn-001.rdf", "rdfms-rdf-names-use/warn-001.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_warn_002() throws Exception {
        testXMLEval("rdfms-rdf-names-use/warn-002.rdf", "rdfms-rdf-names-use/warn-002.nt");
    }

    @Test
    public void test_rdfms_rdf_names_use_warn_003() throws Exception {
        testXMLEval("rdfms-rdf-names-use/warn-003.rdf", "rdfms-rdf-names-use/warn-003.nt");
    }

    @Test
    public void test_rdfms_reification_required_test001() throws Exception {
        testXMLEval("rdfms-reification-required/test001.rdf", "rdfms-reification-required/test001.nt");
    }

    @Test
    public void test_rdfms_seq_representation_test001() throws Exception {
        testXMLEval("rdfms-seq-representation/test001.rdf", "rdfms-seq-representation/test001.nt");
    }

    @Test
    public void test_rdfms_syntax_incomplete_test001() throws Exception {
        testXMLEval("rdfms-syntax-incomplete/test001.rdf", "rdfms-syntax-incomplete/test001.nt");
    }

    @Test
    public void test_rdfms_syntax_incomplete_test002() throws Exception {
        testXMLEval("rdfms-syntax-incomplete/test002.rdf", "rdfms-syntax-incomplete/test002.nt");
    }

    @Test
    public void test_rdfms_syntax_incomplete_test003() throws Exception {
        testXMLEval("rdfms-syntax-incomplete/test003.rdf", "rdfms-syntax-incomplete/test003.nt");
    }

    @Test
    public void test_rdfms_syntax_incomplete_test004() throws Exception {
        testXMLEval("rdfms-syntax-incomplete/test004.rdf", "rdfms-syntax-incomplete/test004.nt");
    }

    @Test
    public void test_rdfms_syntax_incomplete_error001() throws Exception {
        testXMLNegativeSyntax("rdfms-syntax-incomplete/error001.rdf");
    }

    @Test
    public void test_rdfms_syntax_incomplete_error002() throws Exception {
        testXMLNegativeSyntax("rdfms-syntax-incomplete/error002.rdf");
    }

    @Test
    public void test_rdfms_syntax_incomplete_error003() throws Exception {
        testXMLNegativeSyntax("rdfms-syntax-incomplete/error003.rdf");
    }

    @Test
    public void test_rdfms_syntax_incomplete_error004() throws Exception {
        testXMLNegativeSyntax("rdfms-syntax-incomplete/error004.rdf");
    }

    @Test
    public void test_rdfms_syntax_incomplete_error005() throws Exception {
        testXMLNegativeSyntax("rdfms-syntax-incomplete/error005.rdf");
    }

    @Test
    public void test_rdfms_syntax_incomplete_error006() throws Exception {
        testXMLNegativeSyntax("rdfms-syntax-incomplete/error006.rdf");
    }

    @Test
    public void test_rdfms_uri_substructure_test001() throws Exception {
        testXMLEval("rdfms-uri-substructure/test001.rdf", "rdfms-uri-substructure/test001.nt");
    }

    @Test
    public void test_rdfms_xmllang_test003() throws Exception {
        testXMLEval("rdfms-xmllang/test003.rdf", "rdfms-xmllang/test003.nt");
    }

    @Test
    public void test_rdfms_xmllang_test004() throws Exception {
        testXMLEval("rdfms-xmllang/test004.rdf", "rdfms-xmllang/test004.nt");
    }

    @Test
    public void test_rdfms_xmllang_test005() throws Exception {
        testXMLEval("rdfms-xmllang/test005.rdf", "rdfms-xmllang/test005.nt");
    }

    @Test
    public void test_rdfms_xmllang_test006() throws Exception {
        testXMLEval("rdfms-xmllang/test006.rdf", "rdfms-xmllang/test006.nt");
    }

    @Test
    public void test_rdfs_domain_and_range_test001() throws Exception {
        testXMLEval("rdfs-domain-and-range/test001.rdf", "rdfs-domain-and-range/test001.nt");
    }

    @Test
    public void test_rdfs_domain_and_range_test002() throws Exception {
        testXMLEval("rdfs-domain-and-range/test002.rdf", "rdfs-domain-and-range/test002.nt");
    }

    @Test
    public void test_unrecognised_xml_attributes_test001() throws Exception {
        testXMLEval("unrecognised-xml-attributes/test001.rdf", "unrecognised-xml-attributes/test001.nt");
    }

    @Test
    public void test_unrecognised_xml_attributes_test002() throws Exception {
        testXMLEval("unrecognised-xml-attributes/test002.rdf", "unrecognised-xml-attributes/test002.nt");
    }

    @Test
    public void test_xml_canon_test001() throws Exception {
        testXMLEval("xml-canon/test001.rdf", "xml-canon/test001.nt");
    }

    @Test
    public void test_xmlbase_test001() throws Exception {
        testXMLEval("xmlbase/test001.rdf", "xmlbase/test001.nt");
    }

    @Test
    public void test_xmlbase_test002() throws Exception {
        testXMLEval("xmlbase/test002.rdf", "xmlbase/test002.nt");
    }

    @Test
    public void test_xmlbase_test003() throws Exception {
        testXMLEval("xmlbase/test003.rdf", "xmlbase/test003.nt");
    }

    @Test
    public void test_xmlbase_test004() throws Exception {
        testXMLEval("xmlbase/test004.rdf", "xmlbase/test004.nt");
    }

    @Test
    public void test_xmlbase_test006() throws Exception {
        testXMLEval("xmlbase/test006.rdf", "xmlbase/test006.nt");
    }

    @Test
    public void test_xmlbase_test007() throws Exception {
        testXMLEval("xmlbase/test007.rdf", "xmlbase/test007.nt");
    }

    @Test
    public void test_xmlbase_test008() throws Exception {
        testXMLEval("xmlbase/test008.rdf", "xmlbase/test008.nt");
    }

    @Test
    public void test_xmlbase_test009() throws Exception {
        testXMLEval("xmlbase/test009.rdf", "xmlbase/test009.nt");
    }

    @Test
    public void test_xmlbase_test010() throws Exception {
        testXMLEval("xmlbase/test010.rdf", "xmlbase/test010.nt");
    }

    @Test
    public void test_xmlbase_test011() throws Exception {
        testXMLEval("xmlbase/test011.rdf", "xmlbase/test011.nt");
    }

    @Test
    public void test_xmlbase_test013() throws Exception {
        testXMLEval("xmlbase/test013.rdf", "xmlbase/test013.nt");
    }

    @Test
    public void test_xmlbase_test014() throws Exception {
        testXMLEval("xmlbase/test014.rdf", "xmlbase/test014.nt");
    }
}
