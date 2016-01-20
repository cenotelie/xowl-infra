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

package org.xowl.store.entailment;

import org.junit.Ignore;
import org.junit.Test;
import org.xowl.store.EntailmentRegime;

/**
 * Test suite for the RDF entailment regimes
 *
 * @author Laurent Wouters
 */
@Ignore("Not ready yet")
public class RDFTest extends BaseRDFTest {
    @Test
    public void test_datatypes_intensional_xsd_integer_decimal_compatible() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes-intensional/test001.nt", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_datatypes_non_well_formed_literal_1() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test002.nt", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_datatypes_non_well_formed_literal_2() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test002.nt", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_datatypes_semantic_equivalence_within_type_1() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test003a.nt", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/datatypes/test003b.nt");
    }

    @Test
    public void test_datatypes_semantic_equivalence_within_type_2() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test003b.nt", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/datatypes/test003a.nt");
    }

    @Test
    public void test_datatypes_semantic_equivalence_between_datatypes() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test005a.nt", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/datatypes/test005b.nt");
    }

    @Test
    public void test_datatypes_range_clash() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test006.nt", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_datatypes_test008() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test008a.nt", EntailmentRegime.simple, "http://www.w3.org/2013/rdf-mt-tests/datatypes/test008b.nt");
    }

    @Test
    public void test_datatypes_test009() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test009a.nt", EntailmentRegime.simple, "http://www.w3.org/2013/rdf-mt-tests/datatypes/test009b.nt");
    }

    @Test
    public void test_datatypes_test010() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test010.nt", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_datatypes_plain_literal_and_xsd_string() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/datatypes/test011a.nt", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/datatypes/test011b.nt");
    }

    @Test
    public void test_horst_01_subClassOf_intensional() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/horst-01/test001.ttl", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/horst-01/test002.ttl");
    }

    @Test
    public void test_horst_01_subPropertyOf_intensional() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/horst-01/test003.ttl", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/horst-01/test004.ttl");
    }

    @Test
    public void test_rdf_charmod_uris_test003() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdf-charmod-uris/test001.ttl", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/rdf-charmod-uris/test002.ttl");
    }

    @Test
    public void test_rdf_charmod_uris_test004() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdf-charmod-uris/test002.ttl", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/rdf-charmod-uris/test001.ttl");
    }

    @Test
    public void test_rdfms_seq_representation_test002() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfms-seq-representation/empty.nt", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfms-seq-representation/test002.nt");
    }

    @Test
    public void test_rdfms_seq_representation_test003() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfms-seq-representation/test003a.nt", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfms-seq-representation/test003b.nt");
    }

    @Test
    public void test_rdfms_seq_representation_test004() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfms-seq-representation/empty.nt", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfms-seq-representation/test004.nt");
    }

    @Test
    public void test_rdfms_xmllang_test007a() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfms-xmllang/test007a.nt", EntailmentRegime.simple, "http://www.w3.org/2013/rdf-mt-tests/rdfms-xmllang/test007b.nt");
    }

    @Test
    public void test_rdfms_xmllang_test007b() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfms-xmllang/test007b.nt", EntailmentRegime.simple, "http://www.w3.org/2013/rdf-mt-tests/rdfms-xmllang/test007c.nt");
    }

    @Test
    public void test_rdfms_xmllang_test007c() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfms-xmllang/test007c.nt", EntailmentRegime.simple, "http://www.w3.org/2013/rdf-mt-tests/rdfms-xmllang/test007a.nt");
    }

    @Test
    public void test_rdfs_container_membership_superProperty_test001() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-container-membership-superProperty/not1P.ttl", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfs-container-membership-superProperty/not1C.ttl");
    }

    @Test
    public void test_rdfs_domain_and_range_intensionality_range() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-domain-and-range/premises005.ttl", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfs-domain-and-range/nonconclusions005.ttl");
    }

    @Test
    public void test_rdfs_domain_and_range_intensionality_domain() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-domain-and-range/premises006.ttl", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfs-domain-and-range/nonconclusions006.ttl");
    }

    @Test
    public void test_rdfs_entailment_test001() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-entailment/test001.nt", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_rdfs_entailment_test002() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-entailment/test002p.nt", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_rdfs_no_cycles_in_subClassOf_test001() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-no-cycles-in-subClassOf/test001.ttl", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfs-no-cycles-in-subClassOf/test001.nt");
    }

    @Test
    public void test_rdfs_no_cycles_in_subPropertyOf_test001() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-no-cycles-in-subPropertyOf/test001.ttl", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfs-no-cycles-in-subPropertyOf/test001.nt");
    }

    @Test
    public void test_rdfs_subClassOf_a_Property_test001() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-subClassOf-a-Property/test001.nt", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_rdfs_subPropertyOf_semantics_test001() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/rdfs-subPropertyOf-semantics/test001.nt", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/rdfs-subPropertyOf-semantics/test002.nt");
    }

    @Test
    public void test_statement_entailment_test001() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/statement-entailment/test001a.nt", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/statement-entailment/test001b.nt");
    }

    @Test
    public void test_statement_entailment_test002() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/statement-entailment/test002a.nt", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/statement-entailment/test002b.nt");
    }

    @Test
    public void test_statement_entailment_test003() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/statement-entailment/test001a.nt", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/statement-entailment/test001b.nt");
    }

    @Test
    public void test_statement_entailment_test004() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/statement-entailment/test002a.nt", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/statement-entailment/test002b.nt");
    }

    @Test
    public void test_tex_01_language_tag_case_1() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/tex-01/test001.ttl", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/tex-01/test002.ttl");
    }

    @Test
    public void test_tex_01_language_tag_case_2() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/tex-01/test002.ttl", EntailmentRegime.RDF, "http://www.w3.org/2013/rdf-mt-tests/tex-01/test001.ttl");
    }

    @Test
    public void test_xmlsch_02_whitespace_facet_1() {
        testNegativeEntailment("http://www.w3.org/2013/rdf-mt-tests/xmlsch-02/test001.ttl", EntailmentRegime.RDFS, "http://www.w3.org/2013/rdf-mt-tests/xmlsch-02/test002.ttl");
    }

    @Test
    public void test_xmlsch_02_whitespace_facet_2() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/xmlsch-02/test002.ttl", EntailmentRegime.RDFS, null);
    }

    @Test
    public void test_xmlsch_02_whitespace_facet_4() {
        testPositiveEntailment("http://www.w3.org/2013/rdf-mt-tests/xmlsch-02/test002.ttl", EntailmentRegime.RDFS, null);
    }
}
