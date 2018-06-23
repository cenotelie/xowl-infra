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
package org.xowl.infra.store.entailment;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Entailment tests for the built-in reasoning rules
 *
 * @author Laurent Wouters
 */
@Ignore("Not ready yet")
public class OWLReasoningTest extends BaseOWLReasoningTest {
    /**
     * The reason for ignoring an entailment test is that its conclusions are not present in the result due to a rule, but are loaded as part of a standard schema (RDF, RDF, OWL, etc.)
     */
    private static final String IGNORE_EXTERNALLY_LOADED = "IGNORE_EXTERNALLY_LOADED";
    /**
     * We chose to ignore this test because we will not fix it
     */
    private static final String IGNORE_WONT_FIX = "IGNORE_WONT_FIX";

    @Test
    public void testConsistency_WebOnt_I5_3_008() throws Exception {
        testConsistency("WebOnt_I5_3_008.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_3_010() throws Exception {
        testConsistency("WebOnt_I5_3_010.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_3_011() throws Exception {
        testConsistency("WebOnt_I5_3_011.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_5_005() throws Exception {
        testPositiveEntailment("WebOnt_I5_5_005.premise.rdf", "WebOnt_I5_5_005.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_5_005() throws Exception {
        testConsistency("WebOnt_I5_5_005.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_006() throws Exception {
        testPositiveEntailment("WebOnt_I5_8_006.premise.rdf", "WebOnt_I5_8_006.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_006() throws Exception {
        testConsistency("WebOnt_I5_8_006.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_I5_8_007() throws Exception {
        testNegativeEntailment("WebOnt_I5_8_007.premise.rdf", "WebOnt_I5_8_007.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_007() throws Exception {
        testConsistency("WebOnt_I5_8_007.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_008() throws Exception {
        testPositiveEntailment("WebOnt_I5_8_008.premise.rdf", "WebOnt_I5_8_008.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_008() throws Exception {
        testConsistency("WebOnt_I5_8_008.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_009() throws Exception {
        testPositiveEntailment("WebOnt_I5_8_009.premise.rdf", "WebOnt_I5_8_009.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_009() throws Exception {
        testConsistency("WebOnt_I5_8_009.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_010() throws Exception {
        testPositiveEntailment("WebOnt_I5_8_010.premise.rdf", "WebOnt_I5_8_010.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_010() throws Exception {
        testConsistency("WebOnt_I5_8_010.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_011() throws Exception {
        testPositiveEntailment("WebOnt_I5_8_011.premise.rdf", "WebOnt_I5_8_011.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_011() throws Exception {
        testConsistency("WebOnt_I5_8_011.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_InverseFunctionalProperty_001() throws Exception {
        testPositiveEntailment("WebOnt_InverseFunctionalProperty_001.premise.rdf", "WebOnt_InverseFunctionalProperty_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_InverseFunctionalProperty_001() throws Exception {
        testConsistency("WebOnt_InverseFunctionalProperty_001.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_Nothing_001() throws Exception {
        testInconsistency("WebOnt_Nothing_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_Ontology_001() throws Exception {
        testPositiveEntailment("WebOnt_Ontology_001.premise.rdf", "WebOnt_Ontology_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_Ontology_001() throws Exception {
        testConsistency("WebOnt_Ontology_001.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_Restriction_001() throws Exception {
        testInconsistency("WebOnt_Restriction_001.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_Restriction_002() throws Exception {
        testInconsistency("WebOnt_Restriction_002.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_Restriction_003() throws Exception {
        testConsistency("WebOnt_Restriction_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_Restriction_004() throws Exception {
        testConsistency("WebOnt_Restriction_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_SymmetricProperty_002() throws Exception {
        testPositiveEntailment("WebOnt_SymmetricProperty_002.premise.rdf", "WebOnt_SymmetricProperty_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_SymmetricProperty_002() throws Exception {
        testConsistency("WebOnt_SymmetricProperty_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_SymmetricProperty_003() throws Exception {
        testPositiveEntailment("WebOnt_SymmetricProperty_003.premise.rdf", "WebOnt_SymmetricProperty_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_SymmetricProperty_003() throws Exception {
        testConsistency("WebOnt_SymmetricProperty_003.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_Thing_003() throws Exception {
        testInconsistency("WebOnt_Thing_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_Thing_004() throws Exception {
        testConsistency("WebOnt_Thing_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_bnode2somevaluesfrom() throws Exception {
        testPositiveEntailment("bnode2somevaluesfrom.premise.rdf", "bnode2somevaluesfrom.conclusion.rdf");
    }

    @Test
    public void testConsistency_bnode2somevaluesfrom() throws Exception {
        testConsistency("bnode2somevaluesfrom.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_TransitiveProperty_002() throws Exception {
        testPositiveEntailment("WebOnt_TransitiveProperty_002.premise.rdf", "WebOnt_TransitiveProperty_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_TransitiveProperty_002() throws Exception {
        testConsistency("WebOnt_TransitiveProperty_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_allValuesFrom_001() throws Exception {
        testPositiveEntailment("WebOnt_allValuesFrom_001.premise.rdf", "WebOnt_allValuesFrom_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_allValuesFrom_001() throws Exception {
        testConsistency("WebOnt_allValuesFrom_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_chain2trans1() throws Exception {
        testPositiveEntailment("chain2trans1.premise.rdf", "chain2trans1.conclusion.rdf");
    }

    @Test
    public void testConsistency_chain2trans1() throws Exception {
        testConsistency("chain2trans1.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_allValuesFrom_002() throws Exception {
        testNegativeEntailment("WebOnt_allValuesFrom_002.premise.rdf", "WebOnt_allValuesFrom_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_allValuesFrom_002() throws Exception {
        testConsistency("WebOnt_allValuesFrom_002.premise.rdf");
    }

    @Test
    public void testConsistency_Consistent_Datatype_restrictions_with_Different_Types() throws Exception {
        testConsistency("Consistent_Datatype_restrictions_with_Different_Types.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_backwardCompatibleWith_002() throws Exception {
        testConsistency("WebOnt_backwardCompatibleWith_002.premise.rdf");
    }

    @Test
    public void testConsistency_Consistent_owl_real_range_with_DataOneOf() throws Exception {
        testConsistency("Consistent_owl_real_range_with_DataOneOf.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_cardinality_001() throws Exception {
        testPositiveEntailment("WebOnt_cardinality_001.premise.rdf", "WebOnt_cardinality_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_cardinality_001() throws Exception {
        testConsistency("WebOnt_cardinality_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_Consistent_but_all_unsat() throws Exception {
        testPositiveEntailment("Consistent_but_all_unsat.premise.rdf", "Consistent_but_all_unsat.conclusion.rdf");
    }

    @Test
    public void testConsistency_Consistent_but_all_unsat() throws Exception {
        testConsistency("Consistent_but_all_unsat.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_cardinality_002() throws Exception {
        testPositiveEntailment("WebOnt_cardinality_002.premise.rdf", "WebOnt_cardinality_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_cardinality_002() throws Exception {
        testConsistency("WebOnt_cardinality_002.premise.rdf");
    }

    @Test
    public void testConsistency_consistent_dataproperty_disjointness() throws Exception {
        testConsistency("consistent_dataproperty_disjointness.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_cardinality_003() throws Exception {
        testPositiveEntailment("WebOnt_cardinality_003.premise.rdf", "WebOnt_cardinality_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_cardinality_003() throws Exception {
        testConsistency("WebOnt_cardinality_003.premise.rdf");
    }

    @Test
    public void testConsistency_Consistent_dateTime() throws Exception {
        testConsistency("Consistent_dateTime.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_cardinality_004() throws Exception {
        testPositiveEntailment("WebOnt_cardinality_004.premise.rdf", "WebOnt_cardinality_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_cardinality_004() throws Exception {
        testConsistency("WebOnt_cardinality_004.premise.rdf");
    }

    @Test
    public void testConsistency_consistent_integer_filler() throws Exception {
        testConsistency("consistent_integer_filler.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_001() throws Exception {
        testInconsistency("WebOnt_description_logic_001.premise.rdf");
    }

    @Test
    public void testInconsistency_Contradicting_datatype_Restrictions() throws Exception {
        testInconsistency("Contradicting_datatype_Restrictions.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_002() throws Exception {
        testInconsistency("WebOnt_description_logic_002.premise.rdf");
    }

    @Test
    public void testInconsistency_Contradicting_dateTime_restrictions() throws Exception {
        testInconsistency("Contradicting_dateTime_restrictions.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_003() throws Exception {
        testInconsistency("WebOnt_description_logic_003.premise.rdf");
    }

    @Test
    public void testConsistency_Datatype_DataComplementOf_001() throws Exception {
        testConsistency("Datatype_DataComplementOf_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_004() throws Exception {
        testInconsistency("WebOnt_description_logic_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_005() throws Exception {
        testConsistency("WebOnt_description_logic_005.premise.rdf");
    }

    @Test
    public void testInconsistency_Datatype_Float_Discrete_001() throws Exception {
        testInconsistency("Datatype_Float_Discrete_001.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_006() throws Exception {
        testConsistency("WebOnt_description_logic_006.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_007() throws Exception {
        testInconsistency("WebOnt_description_logic_007.premise.rdf");
    }

    @Test
    public void testInconsistency_datatype_restriction_min_max_inconsistency() throws Exception {
        testInconsistency("datatype_restriction_min_max_inconsistency.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_008() throws Exception {
        testInconsistency("WebOnt_description_logic_008.premise.rdf");
    }

    @Test
    public void testInconsistency_Different_types_in_Datatype_Restrictions_and_Complement() throws Exception {
        testInconsistency("Different_types_in_Datatype_Restrictions_and_Complement.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_009() throws Exception {
        testConsistency("WebOnt_description_logic_009.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_DisjointClasses_001() throws Exception {
        testPositiveEntailment("DisjointClasses_001.premise.fs", "DisjointClasses_001.conclusion.fs");
    }

    @Test
    public void testConsistency_DisjointClasses_001() throws Exception {
        testConsistency("DisjointClasses_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_010() throws Exception {
        testInconsistency("WebOnt_description_logic_010.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_011() throws Exception {
        testInconsistency("WebOnt_description_logic_011.premise.rdf");
    }

    @Test
    public void testInconsistency_DisjointClasses_002() throws Exception {
        testInconsistency("DisjointClasses_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_012() throws Exception {
        testInconsistency("WebOnt_description_logic_012.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_DisjointClasses_003() throws Exception {
        testPositiveEntailment("DisjointClasses_003.premise.fs", "DisjointClasses_003.conclusion.fs");
    }

    @Test
    public void testConsistency_DisjointClasses_003() throws Exception {
        testConsistency("DisjointClasses_003.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_013() throws Exception {
        testInconsistency("WebOnt_description_logic_013.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_014() throws Exception {
        testInconsistency("WebOnt_description_logic_014.premise.rdf");
    }

    @Test
    public void testConsistency_FS2RDF_different_individuals_2_ar() throws Exception {
        testConsistency("FS2RDF_different_individuals_2_ar.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_015() throws Exception {
        testInconsistency("WebOnt_description_logic_015.premise.rdf");
    }

    @Test
    public void testConsistency_FS2RDF_different_individuals_3_ar() throws Exception {
        testConsistency("FS2RDF_different_individuals_3_ar.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_016() throws Exception {
        testConsistency("WebOnt_description_logic_016.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_017() throws Exception {
        testInconsistency("WebOnt_description_logic_017.premise.rdf");
    }

    @Test
    public void testConsistency_FS2RDF_no_builtin_prefixes_ar() throws Exception {
        testConsistency("FS2RDF_no_builtin_prefixes_ar.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_018() throws Exception {
        testConsistency("WebOnt_description_logic_018.premise.rdf");
    }

    @Test
    public void testConsistency_FS2RDF_same_individual_2_ar() throws Exception {
        testConsistency("FS2RDF_same_individual_2_ar.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_019() throws Exception {
        testInconsistency("WebOnt_description_logic_019.premise.rdf");
    }

    @Test
    public void testInconsistency_functionality_clash() throws Exception {
        testInconsistency("functionality_clash.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_020() throws Exception {
        testConsistency("WebOnt_description_logic_020.premise.rdf");
    }

    @Test
    public void testInconsistency_Inconsistent_Byte_Filler() throws Exception {
        testInconsistency("Inconsistent_Byte_Filler.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_021() throws Exception {
        testConsistency("WebOnt_description_logic_021.premise.rdf");
    }

    @Test
    public void testInconsistency_Inconsistent_Data_Complement_with_the_Restrictions() throws Exception {
        testInconsistency("Inconsistent_Data_Complement_with_the_Restrictions.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_022() throws Exception {
        testInconsistency("WebOnt_description_logic_022.premise.rdf");
    }

    @Test
    public void testInconsistency_Inconsistent_Disjoint_Dataproperties() throws Exception {
        testInconsistency("Inconsistent_Disjoint_Dataproperties.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_023() throws Exception {
        testInconsistency("WebOnt_description_logic_023.premise.rdf");
    }

    @Test
    public void testInconsistency_inconsistent_integer_filler() throws Exception {
        testInconsistency("inconsistent_integer_filler.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_024() throws Exception {
        testConsistency("WebOnt_description_logic_024.premise.rdf");
    }

    @Test
    public void testInconsistency_inconsistent_datatypes() throws Exception {
        testInconsistency("inconsistent_datatypes.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_025() throws Exception {
        testConsistency("WebOnt_description_logic_025.premise.rdf");
    }

    @Test
    public void testInconsistency_Minus_Infinity_is_not_in_owl_real() throws Exception {
        testInconsistency("Minus_Infinity_is_not_in_owl_real.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_026() throws Exception {
        testInconsistency("WebOnt_description_logic_026.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_AnnotationAnnotations_001() throws Exception {
        testConsistency("New_Feature_AnnotationAnnotations_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_027() throws Exception {
        testInconsistency("WebOnt_description_logic_027.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_028() throws Exception {
        testConsistency("WebOnt_description_logic_028.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_AsymmetricProperty_001() throws Exception {
        testInconsistency("New_Feature_AsymmetricProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_029() throws Exception {
        testInconsistency("WebOnt_description_logic_029.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_AxiomAnnotations_001() throws Exception {
        testConsistency("New_Feature_AxiomAnnotations_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_030() throws Exception {
        testInconsistency("WebOnt_description_logic_030.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_031() throws Exception {
        testConsistency("WebOnt_description_logic_031.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_BottomDataProperty_001() throws Exception {
        testInconsistency("New_Feature_BottomDataProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_032() throws Exception {
        testInconsistency("WebOnt_description_logic_032.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_BottomObjectProperty_001() throws Exception {
        testInconsistency("New_Feature_BottomObjectProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_033() throws Exception {
        testInconsistency("WebOnt_description_logic_033.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DataQCR_001() throws Exception {
        testPositiveEntailment("New_Feature_DataQCR_001.premise.fs", "New_Feature_DataQCR_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DataQCR_001() throws Exception {
        testConsistency("New_Feature_DataQCR_001.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_034() throws Exception {
        testConsistency("WebOnt_description_logic_034.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_035() throws Exception {
        testInconsistency("WebOnt_description_logic_035.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_DisjointDataProperties_001() throws Exception {
        testInconsistency("New_Feature_DisjointDataProperties_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_040() throws Exception {
        testInconsistency("WebOnt_description_logic_040.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_101() throws Exception {
        testInconsistency("WebOnt_description_logic_101.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DisjointDataProperties_002() throws Exception {
        testPositiveEntailment("New_Feature_DisjointDataProperties_002.premise.fs", "New_Feature_DisjointDataProperties_002.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DisjointDataProperties_002() throws Exception {
        testConsistency("New_Feature_DisjointDataProperties_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_102() throws Exception {
        testInconsistency("WebOnt_description_logic_102.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_103() throws Exception {
        testInconsistency("WebOnt_description_logic_103.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DisjointObjectProperties_001() throws Exception {
        testPositiveEntailment("New_Feature_DisjointObjectProperties_001.premise.fs", "New_Feature_DisjointObjectProperties_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DisjointObjectProperties_001() throws Exception {
        testConsistency("New_Feature_DisjointObjectProperties_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_104() throws Exception {
        testInconsistency("WebOnt_description_logic_104.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_105() throws Exception {
        testInconsistency("WebOnt_description_logic_105.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DisjointObjectProperties_002() throws Exception {
        testPositiveEntailment("New_Feature_DisjointObjectProperties_002.premise.fs", "New_Feature_DisjointObjectProperties_002.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DisjointObjectProperties_002() throws Exception {
        testConsistency("New_Feature_DisjointObjectProperties_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_106() throws Exception {
        testInconsistency("WebOnt_description_logic_106.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DisjointUnion_001() throws Exception {
        testPositiveEntailment("New_Feature_DisjointUnion_001.premise.fs", "New_Feature_DisjointUnion_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DisjointUnion_001() throws Exception {
        testConsistency("New_Feature_DisjointUnion_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_107() throws Exception {
        testInconsistency("WebOnt_description_logic_107.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_108() throws Exception {
        testInconsistency("WebOnt_description_logic_108.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_IrreflexiveProperty_001() throws Exception {
        testInconsistency("New_Feature_IrreflexiveProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_109() throws Exception {
        testInconsistency("WebOnt_description_logic_109.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_110() throws Exception {
        testInconsistency("WebOnt_description_logic_110.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_Keys_001() throws Exception {
        testPositiveEntailment("New_Feature_Keys_001.premise.fs", "New_Feature_Keys_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_Keys_001() throws Exception {
        testConsistency("New_Feature_Keys_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_111() throws Exception {
        testInconsistency("WebOnt_description_logic_111.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_201() throws Exception {
        testPositiveEntailment("WebOnt_description_logic_201.premise.rdf", "WebOnt_description_logic_201.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_201() throws Exception {
        testConsistency("WebOnt_description_logic_201.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_Keys_002() throws Exception {
        testInconsistency("New_Feature_Keys_002.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_205() throws Exception {
        testPositiveEntailment("WebOnt_description_logic_205.premise.rdf", "WebOnt_description_logic_205.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_205() throws Exception {
        testConsistency("WebOnt_description_logic_205.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_Keys_003() throws Exception {
        testPositiveEntailment("New_Feature_Keys_003.premise.fs", "New_Feature_Keys_003.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_Keys_003() throws Exception {
        testConsistency("New_Feature_Keys_003.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_207() throws Exception {
        testPositiveEntailment("WebOnt_description_logic_207.premise.rdf", "WebOnt_description_logic_207.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_207() throws Exception {
        testConsistency("WebOnt_description_logic_207.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_New_Feature_Keys_004() throws Exception {
        testNegativeEntailment("New_Feature_Keys_004.premise.fs", "New_Feature_Keys_004.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_Keys_004() throws Exception {
        testConsistency("New_Feature_Keys_004.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_208() throws Exception {
        testPositiveEntailment("WebOnt_description_logic_208.premise.rdf", "WebOnt_description_logic_208.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_208() throws Exception {
        testConsistency("WebOnt_description_logic_208.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_Keys_005() throws Exception {
        testConsistency("New_Feature_Keys_005.premise.fs");
    }

    @Test
    public void testNegativeEntailment_WebOnt_description_logic_209() throws Exception {
        testNegativeEntailment("WebOnt_description_logic_209.premise.rdf", "WebOnt_description_logic_209.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_209() throws Exception {
        testConsistency("WebOnt_description_logic_209.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_Keys_006() throws Exception {
        testInconsistency("New_Feature_Keys_006.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_501() throws Exception {
        testConsistency("WebOnt_description_logic_501.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_New_Feature_Keys_007() throws Exception {
        testNegativeEntailment("New_Feature_Keys_007.premise.fs", "New_Feature_Keys_007.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_Keys_007() throws Exception {
        testConsistency("New_Feature_Keys_007.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_502() throws Exception {
        testInconsistency("WebOnt_description_logic_502.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_503() throws Exception {
        testConsistency("WebOnt_description_logic_503.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_NegativeDataPropertyAssertion_001() throws Exception {
        testInconsistency("New_Feature_NegativeDataPropertyAssertion_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_504() throws Exception {
        testInconsistency("WebOnt_description_logic_504.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_NegativeObjectPropertyAssertion_001() throws Exception {
        testInconsistency("New_Feature_NegativeObjectPropertyAssertion_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_601() throws Exception {
        testInconsistency("WebOnt_description_logic_601.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_602() throws Exception {
        testInconsistency("WebOnt_description_logic_602.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ObjectPropertyChain_001() throws Exception {
        testPositiveEntailment("New_Feature_ObjectPropertyChain_001.premise.fs", "New_Feature_ObjectPropertyChain_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_ObjectPropertyChain_001() throws Exception {
        testConsistency("New_Feature_ObjectPropertyChain_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_603() throws Exception {
        testInconsistency("WebOnt_description_logic_603.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_604() throws Exception {
        testInconsistency("WebOnt_description_logic_604.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_605() throws Exception {
        testConsistency("WebOnt_description_logic_605.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ObjectPropertyChain_BJP_003() throws Exception {
        testPositiveEntailment("New_Feature_ObjectPropertyChain_BJP_003.premise.rdf", "New_Feature_ObjectPropertyChain_BJP_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_New_Feature_ObjectPropertyChain_BJP_003() throws Exception {
        testConsistency("New_Feature_ObjectPropertyChain_BJP_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_606() throws Exception {
        testConsistency("WebOnt_description_logic_606.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_New_Feature_ObjectPropertyChain_BJP_004() throws Exception {
        testNegativeEntailment("New_Feature_ObjectPropertyChain_BJP_004.premise.rdf", "New_Feature_ObjectPropertyChain_BJP_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_New_Feature_ObjectPropertyChain_BJP_004() throws Exception {
        testConsistency("New_Feature_ObjectPropertyChain_BJP_004.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_608() throws Exception {
        testInconsistency("WebOnt_description_logic_608.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_609() throws Exception {
        testConsistency("WebOnt_description_logic_609.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ObjectQCR_001() throws Exception {
        testPositiveEntailment("New_Feature_ObjectQCR_001.premise.fs", "New_Feature_ObjectQCR_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_ObjectQCR_001() throws Exception {
        testConsistency("New_Feature_ObjectQCR_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_610() throws Exception {
        testInconsistency("WebOnt_description_logic_610.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_611() throws Exception {
        testInconsistency("WebOnt_description_logic_611.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_612() throws Exception {
        testInconsistency("WebOnt_description_logic_612.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ObjectQCR_002() throws Exception {
        testPositiveEntailment("New_Feature_ObjectQCR_002.premise.fs", "New_Feature_ObjectQCR_002.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_ObjectQCR_002() throws Exception {
        testConsistency("New_Feature_ObjectQCR_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_613() throws Exception {
        testInconsistency("WebOnt_description_logic_613.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_614() throws Exception {
        testInconsistency("WebOnt_description_logic_614.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_Rational_001() throws Exception {
        testConsistency("New_Feature_Rational_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_615() throws Exception {
        testInconsistency("WebOnt_description_logic_615.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_616() throws Exception {
        testConsistency("WebOnt_description_logic_616.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_Rational_002() throws Exception {
        testInconsistency("New_Feature_Rational_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_617() throws Exception {
        testInconsistency("WebOnt_description_logic_617.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_623() throws Exception {
        testInconsistency("WebOnt_description_logic_623.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_Rational_003() throws Exception {
        testConsistency("New_Feature_Rational_003.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_624() throws Exception {
        testConsistency("WebOnt_description_logic_624.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_625() throws Exception {
        testConsistency("WebOnt_description_logic_625.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ReflexiveProperty_001() throws Exception {
        testPositiveEntailment("New_Feature_ReflexiveProperty_001.premise.fs", "New_Feature_ReflexiveProperty_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_ReflexiveProperty_001() throws Exception {
        testConsistency("New_Feature_ReflexiveProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_626() throws Exception {
        testInconsistency("WebOnt_description_logic_626.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_627() throws Exception {
        testInconsistency("WebOnt_description_logic_627.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_SelfRestriction_001() throws Exception {
        testPositiveEntailment("New_Feature_SelfRestriction_001.premise.fs", "New_Feature_SelfRestriction_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_SelfRestriction_001() throws Exception {
        testConsistency("New_Feature_SelfRestriction_001.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_628() throws Exception {
        testConsistency("WebOnt_description_logic_628.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_629() throws Exception {
        testInconsistency("WebOnt_description_logic_629.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_630() throws Exception {
        testInconsistency("WebOnt_description_logic_630.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_SelfRestriction_002() throws Exception {
        testPositiveEntailment("New_Feature_SelfRestriction_002.premise.fs", "New_Feature_SelfRestriction_002.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_SelfRestriction_002() throws Exception {
        testConsistency("New_Feature_SelfRestriction_002.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_631() throws Exception {
        testConsistency("WebOnt_description_logic_631.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_TopObjectProperty_001() throws Exception {
        testInconsistency("New_Feature_TopObjectProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_632() throws Exception {
        testInconsistency("WebOnt_description_logic_632.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_633() throws Exception {
        testInconsistency("WebOnt_description_logic_633.premise.rdf");
    }

    @Test
    public void testInconsistency_one_two() throws Exception {
        testInconsistency("one_two.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_634() throws Exception {
        testConsistency("WebOnt_description_logic_634.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_anonymous_individual() throws Exception {
        testConsistency("owl2_rl_anonymous_individual.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_641() throws Exception {
        testInconsistency("WebOnt_description_logic_641.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_642() throws Exception {
        testInconsistency("WebOnt_description_logic_642.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_leftside_allvaluesfrom() throws Exception {
        testConsistency("owl2_rl_invalid_leftside_allvaluesfrom.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_643() throws Exception {
        testInconsistency("WebOnt_description_logic_643.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_leftside_maxcard() throws Exception {
        testConsistency("owl2_rl_invalid_leftside_maxcard.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_644() throws Exception {
        testInconsistency("WebOnt_description_logic_644.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_oneof() throws Exception {
        testConsistency("owl2_rl_invalid_oneof.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_646() throws Exception {
        testInconsistency("WebOnt_description_logic_646.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_650() throws Exception {
        testInconsistency("WebOnt_description_logic_650.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_owlreal() throws Exception {
        testConsistency("owl2_rl_invalid_owlreal.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_661() throws Exception {
        testPositiveEntailment("WebOnt_description_logic_661.premise.rdf", "WebOnt_description_logic_661.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_661() throws Exception {
        testConsistency("WebOnt_description_logic_661.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_rightside_somevaluesfrom() throws Exception {
        testConsistency("owl2_rl_invalid_rightside_somevaluesfrom.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_665() throws Exception {
        testPositiveEntailment("WebOnt_description_logic_665.premise.rdf", "WebOnt_description_logic_665.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_665() throws Exception {
        testConsistency("WebOnt_description_logic_665.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_rightside_unionof() throws Exception {
        testConsistency("owl2_rl_invalid_rightside_unionof.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_667() throws Exception {
        testPositiveEntailment("WebOnt_description_logic_667.premise.rdf", "WebOnt_description_logic_667.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_667() throws Exception {
        testConsistency("WebOnt_description_logic_667.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_unionof() throws Exception {
        testConsistency("owl2_rl_invalid_unionof.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_901() throws Exception {
        testPositiveEntailment("WebOnt_description_logic_901.premise.rdf", "WebOnt_description_logic_901.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_901() throws Exception {
        testConsistency("WebOnt_description_logic_901.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_owl2_rl_rules_fp_differentFrom() throws Exception {
        testPositiveEntailment("owl2_rl_rules_fp_differentFrom.premise.rdf", "owl2_rl_rules_fp_differentFrom.conclusion.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_rules_fp_differentFrom() throws Exception {
        testConsistency("owl2_rl_rules_fp_differentFrom.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_description_logic_902() throws Exception {
        testNegativeEntailment("WebOnt_description_logic_902.premise.rdf", "WebOnt_description_logic_902.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_902() throws Exception {
        testConsistency("WebOnt_description_logic_902.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_owl2_rl_rules_ifp_differentFrom() throws Exception {
        testPositiveEntailment("owl2_rl_rules_ifp_differentFrom.premise.rdf", "owl2_rl_rules_ifp_differentFrom.conclusion.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_rules_ifp_differentFrom() throws Exception {
        testConsistency("owl2_rl_rules_ifp_differentFrom.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_905() throws Exception {
        testConsistency("WebOnt_description_logic_905.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_valid_mincard() throws Exception {
        testConsistency("owl2_rl_valid_mincard.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_908() throws Exception {
        testConsistency("WebOnt_description_logic_908.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_valid_oneof() throws Exception {
        testConsistency("owl2_rl_valid_oneof.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_differentFrom_001() throws Exception {
        testPositiveEntailment("WebOnt_differentFrom_001.premise.rdf", "WebOnt_differentFrom_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_differentFrom_001() throws Exception {
        testConsistency("WebOnt_differentFrom_001.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_valid_rightside_allvaluesfrom() throws Exception {
        testConsistency("owl2_rl_valid_rightside_allvaluesfrom.premise.rdf");
    }

    @Test
    public void testConsistency_Plus_and_Minus_Zero_Integer() throws Exception {
        testConsistency("Plus_and_Minus_Zero_Integer.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_disjointWith_001() throws Exception {
        testPositiveEntailment("WebOnt_disjointWith_001.premise.rdf", "WebOnt_disjointWith_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_001() throws Exception {
        testConsistency("WebOnt_disjointWith_001.premise.rdf");
    }

    @Test
    public void testInconsistency_Plus_and_Minus_Zero_are_Distinct() throws Exception {
        testInconsistency("Plus_and_Minus_Zero_are_Distinct.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_003() throws Exception {
        testConsistency("WebOnt_disjointWith_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_Qualified_cardinality_boolean() throws Exception {
        testPositiveEntailment("Qualified_cardinality_boolean.premise.fs", "Qualified_cardinality_boolean.conclusion.fs");
    }

    @Test
    public void testConsistency_Qualified_cardinality_boolean() throws Exception {
        testConsistency("Qualified_cardinality_boolean.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_004() throws Exception {
        testConsistency("WebOnt_disjointWith_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_005() throws Exception {
        testConsistency("WebOnt_disjointWith_005.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_bool_complement_inst() throws Exception {
        testInconsistency("rdfbased_sem_bool_complement_inst.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_006() throws Exception {
        testConsistency("WebOnt_disjointWith_006.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_intersection_inst_comp() throws Exception {
        testPositiveEntailment("rdfbased_sem_bool_intersection_inst_comp.premise.rdf", "rdfbased_sem_bool_intersection_inst_comp.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_intersection_inst_comp() throws Exception {
        testConsistency("rdfbased_sem_bool_intersection_inst_comp.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_007() throws Exception {
        testConsistency("WebOnt_disjointWith_007.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_intersection_inst_expr() throws Exception {
        testPositiveEntailment("rdfbased_sem_bool_intersection_inst_expr.premise.rdf", "rdfbased_sem_bool_intersection_inst_expr.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_intersection_inst_expr() throws Exception {
        testConsistency("rdfbased_sem_bool_intersection_inst_expr.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_008() throws Exception {
        testConsistency("WebOnt_disjointWith_008.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_009() throws Exception {
        testConsistency("WebOnt_disjointWith_009.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_intersection_term() throws Exception {
        testPositiveEntailment("rdfbased_sem_bool_intersection_term.premise.rdf", "rdfbased_sem_bool_intersection_term.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_intersection_term() throws Exception {
        testConsistency("rdfbased_sem_bool_intersection_term.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_001() throws Exception {
        testPositiveEntailment("WebOnt_equivalentClass_001.premise.rdf", "WebOnt_equivalentClass_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_001() throws Exception {
        testConsistency("WebOnt_equivalentClass_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_union_inst_comp() throws Exception {
        testPositiveEntailment("rdfbased_sem_bool_union_inst_comp.premise.rdf", "rdfbased_sem_bool_union_inst_comp.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_union_inst_comp() throws Exception {
        testConsistency("rdfbased_sem_bool_union_inst_comp.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_002() throws Exception {
        testPositiveEntailment("WebOnt_equivalentClass_002.premise.rdf", "WebOnt_equivalentClass_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_002() throws Exception {
        testConsistency("WebOnt_equivalentClass_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_union_term() throws Exception {
        testPositiveEntailment("rdfbased_sem_bool_union_term.premise.rdf", "rdfbased_sem_bool_union_term.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_union_term() throws Exception {
        testConsistency("rdfbased_sem_bool_union_term.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_003() throws Exception {
        testPositiveEntailment("WebOnt_equivalentClass_003.premise.rdf", "WebOnt_equivalentClass_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_003() throws Exception {
        testConsistency("WebOnt_equivalentClass_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_chain_def() throws Exception {
        testPositiveEntailment("rdfbased_sem_chain_def.premise.rdf", "rdfbased_sem_chain_def.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_chain_def() throws Exception {
        testConsistency("rdfbased_sem_chain_def.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_004() throws Exception {
        testPositiveEntailment("WebOnt_equivalentClass_004.premise.rdf", "WebOnt_equivalentClass_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_004() throws Exception {
        testConsistency("WebOnt_equivalentClass_004.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_char_asymmetric_inst() throws Exception {
        testInconsistency("rdfbased_sem_char_asymmetric_inst.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_equivalentClass_005() throws Exception {
        testNegativeEntailment("WebOnt_equivalentClass_005.premise.rdf", "WebOnt_equivalentClass_005.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_005() throws Exception {
        testConsistency("WebOnt_equivalentClass_005.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_char_asymmetric_term() throws Exception {
        testInconsistency("rdfbased_sem_char_asymmetric_term.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_006() throws Exception {
        testPositiveEntailment("WebOnt_equivalentClass_006.premise.rdf", "WebOnt_equivalentClass_006.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_006() throws Exception {
        testConsistency("WebOnt_equivalentClass_006.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_functional_inst() throws Exception {
        testPositiveEntailment("rdfbased_sem_char_functional_inst.premise.rdf", "rdfbased_sem_char_functional_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_functional_inst() throws Exception {
        testConsistency("rdfbased_sem_char_functional_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_008_Direct() throws Exception {
        testPositiveEntailment("WebOnt_equivalentClass_008_Direct.premise.rdf", "WebOnt_equivalentClass_008_Direct.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_008_Direct() throws Exception {
        testConsistency("WebOnt_equivalentClass_008_Direct.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_inversefunc_data() throws Exception {
        testPositiveEntailment("rdfbased_sem_char_inversefunc_data.premise.rdf", "rdfbased_sem_char_inversefunc_data.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_inversefunc_data() throws Exception {
        testConsistency("rdfbased_sem_char_inversefunc_data.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_inversefunc_inst() throws Exception {
        testPositiveEntailment("rdfbased_sem_char_inversefunc_inst.premise.rdf", "rdfbased_sem_char_inversefunc_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_inversefunc_inst() throws Exception {
        testConsistency("rdfbased_sem_char_inversefunc_inst.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_009() throws Exception {
        testConsistency("WebOnt_equivalentClass_009.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_char_irreflexive_inst() throws Exception {
        testInconsistency("rdfbased_sem_char_irreflexive_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentProperty_001() throws Exception {
        testPositiveEntailment("WebOnt_equivalentProperty_001.premise.rdf", "WebOnt_equivalentProperty_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentProperty_001() throws Exception {
        testConsistency("WebOnt_equivalentProperty_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_symmetric_inst() throws Exception {
        testPositiveEntailment("rdfbased_sem_char_symmetric_inst.premise.rdf", "rdfbased_sem_char_symmetric_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_symmetric_inst() throws Exception {
        testConsistency("rdfbased_sem_char_symmetric_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentProperty_002() throws Exception {
        testPositiveEntailment("WebOnt_equivalentProperty_002.premise.rdf", "WebOnt_equivalentProperty_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentProperty_002() throws Exception {
        testConsistency("WebOnt_equivalentProperty_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_transitive_inst() throws Exception {
        testPositiveEntailment("rdfbased_sem_char_transitive_inst.premise.rdf", "rdfbased_sem_char_transitive_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_transitive_inst() throws Exception {
        testConsistency("rdfbased_sem_char_transitive_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentProperty_003() throws Exception {
        testPositiveEntailment("WebOnt_equivalentProperty_003.premise.rdf", "WebOnt_equivalentProperty_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentProperty_003() throws Exception {
        testConsistency("WebOnt_equivalentProperty_003.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_class_nothing_ext() throws Exception {
        testInconsistency("rdfbased_sem_class_nothing_ext.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentProperty_004() throws Exception {
        testPositiveEntailment("WebOnt_equivalentProperty_004.premise.rdf", "WebOnt_equivalentProperty_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentProperty_004() throws Exception {
        testConsistency("WebOnt_equivalentProperty_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_class_nothing_term() throws Exception {
        testPositiveEntailment("rdfbased_sem_class_nothing_term.premise.rdf", "rdfbased_sem_class_nothing_term.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_class_nothing_term() throws Exception {
        testConsistency("rdfbased_sem_class_nothing_term.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_imports_011() throws Exception {
        testPositiveEntailment("WebOnt_imports_011.premise.rdf", "WebOnt_imports_011.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_imports_011() throws Exception {
        testConsistency("WebOnt_imports_011.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_class_nothing_type() throws Exception {
        testPositiveEntailment("rdfbased_sem_class_nothing_type.premise.rdf", "rdfbased_sem_class_nothing_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_class_nothing_type() throws Exception {
        testConsistency("rdfbased_sem_class_nothing_type.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_class_thing_term() throws Exception {
        testPositiveEntailment("rdfbased_sem_class_thing_term.premise.rdf", "rdfbased_sem_class_thing_term.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_class_thing_term() throws Exception {
        testConsistency("rdfbased_sem_class_thing_term.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_maxCardinality_001() throws Exception {
        testInconsistency("WebOnt_maxCardinality_001.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_class_thing_type() throws Exception {
        testPositiveEntailment("rdfbased_sem_class_thing_type.premise.rdf", "rdfbased_sem_class_thing_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_class_thing_type() throws Exception {
        testConsistency("rdfbased_sem_class_thing_type.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_001() throws Exception {
        testConsistency("WebOnt_miscellaneous_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_enum_inst_included() throws Exception {
        testPositiveEntailment("rdfbased_sem_enum_inst_included.premise.rdf", "rdfbased_sem_enum_inst_included.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_enum_inst_included() throws Exception {
        testConsistency("rdfbased_sem_enum_inst_included.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_different_irrflxv() throws Exception {
        testInconsistency("rdfbased_sem_eqdis_different_irrflxv.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_002() throws Exception {
        testConsistency("WebOnt_miscellaneous_002.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_different_sameas() throws Exception {
        testInconsistency("rdfbased_sem_eqdis_different_sameas.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disclass_eqclass() throws Exception {
        testInconsistency("rdfbased_sem_eqdis_disclass_eqclass.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_102() throws Exception {
        testConsistency("WebOnt_miscellaneous_102.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disclass_inst() throws Exception {
        testInconsistency("rdfbased_sem_eqdis_disclass_inst.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_103() throws Exception {
        testConsistency("WebOnt_miscellaneous_103.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disclass_irrflxv() throws Exception {
        testInconsistency("rdfbased_sem_eqdis_disclass_irrflxv.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_202() throws Exception {
        testConsistency("WebOnt_miscellaneous_202.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disprop_eqprop() throws Exception {
        testInconsistency("rdfbased_sem_eqdis_disprop_eqprop.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_miscellaneous_203() throws Exception {
        testInconsistency("WebOnt_miscellaneous_203.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disprop_inst() throws Exception {
        testInconsistency("rdfbased_sem_eqdis_disprop_inst.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_miscellaneous_204() throws Exception {
        testInconsistency("WebOnt_miscellaneous_204.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disprop_irrflxv() throws Exception {
        testInconsistency("rdfbased_sem_eqdis_disprop_irrflxv.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_miscellaneous_302_Direct() throws Exception {
        testPositiveEntailment("WebOnt_miscellaneous_302_Direct.premise.rdf", "WebOnt_miscellaneous_302_Direct.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_302_Direct() throws Exception {
        testConsistency("WebOnt_miscellaneous_302_Direct.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_inst() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_inst.premise.rdf", "rdfbased_sem_eqdis_eqclass_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_inst() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqclass_inst.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_303() throws Exception {
        testConsistency("WebOnt_miscellaneous_303.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_rflxv() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_rflxv.premise.rdf", "rdfbased_sem_eqdis_eqclass_rflxv.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_rflxv() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqclass_rflxv.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_oneOf_001() throws Exception {
        testConsistency("WebOnt_oneOf_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_subclass_1() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_subclass_1.premise.rdf", "rdfbased_sem_eqdis_eqclass_subclass_1.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_subclass_1() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqclass_subclass_1.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_oneOf_004() throws Exception {
        testPositiveEntailment("WebOnt_oneOf_004.premise.rdf", "WebOnt_oneOf_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_oneOf_004() throws Exception {
        testConsistency("WebOnt_oneOf_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_subclass_2() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_subclass_2.premise.rdf", "rdfbased_sem_eqdis_eqclass_subclass_2.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_subclass_2() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqclass_subclass_2.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_sameAs_001() throws Exception {
        testPositiveEntailment("WebOnt_sameAs_001.premise.rdf", "WebOnt_sameAs_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_sameAs_001() throws Exception {
        testConsistency("WebOnt_sameAs_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_subst() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_subst.premise.rdf", "rdfbased_sem_eqdis_eqclass_subst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_subst() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqclass_subst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_someValuesFrom_003() throws Exception {
        testPositiveEntailment("WebOnt_someValuesFrom_003.premise.rdf", "WebOnt_someValuesFrom_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_someValuesFrom_003() throws Exception {
        testConsistency("WebOnt_someValuesFrom_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_sym() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_sym.premise.rdf", "rdfbased_sem_eqdis_eqclass_sym.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_sym() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqclass_sym.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_trans() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_trans.premise.rdf", "rdfbased_sem_eqdis_eqclass_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_trans() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqclass_trans.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_unionOf_003() throws Exception {
        testPositiveEntailment("WebOnt_unionOf_003.premise.rdf", "WebOnt_unionOf_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_unionOf_003() throws Exception {
        testConsistency("WebOnt_unionOf_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_inst() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_inst.premise.rdf", "rdfbased_sem_eqdis_eqprop_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_inst() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqprop_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_unionOf_004() throws Exception {
        testPositiveEntailment("WebOnt_unionOf_004.premise.rdf", "WebOnt_unionOf_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_unionOf_004() throws Exception {
        testConsistency("WebOnt_unionOf_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_rflxv() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_rflxv.premise.rdf", "rdfbased_sem_eqdis_eqprop_rflxv.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_rflxv() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqprop_rflxv.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_subprop_1() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_subprop_1.premise.rdf", "rdfbased_sem_eqdis_eqprop_subprop_1.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_subprop_1() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqprop_subprop_1.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_subprop_2() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_subprop_2.premise.rdf", "rdfbased_sem_eqdis_eqprop_subprop_2.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_subprop_2() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqprop_subprop_2.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_subst() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_subst.premise.rdf", "rdfbased_sem_eqdis_eqprop_subst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_subst() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqprop_subst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_sym() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_sym.premise.rdf", "rdfbased_sem_eqdis_eqprop_sym.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_sym() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqprop_sym.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_trans() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_trans.premise.rdf", "rdfbased_sem_eqdis_eqprop_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_trans() throws Exception {
        testConsistency("rdfbased_sem_eqdis_eqprop_trans.premise.rdf");
    }

    @Ignore(IGNORE_WONT_FIX)
    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_sameas_rflxv() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_sameas_rflxv.premise.rdf", "rdfbased_sem_eqdis_sameas_rflxv.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_sameas_rflxv() throws Exception {
        testConsistency("rdfbased_sem_eqdis_sameas_rflxv.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_sameas_subst() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_sameas_subst.premise.rdf", "rdfbased_sem_eqdis_sameas_subst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_sameas_subst() throws Exception {
        testConsistency("rdfbased_sem_eqdis_sameas_subst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_sameas_sym() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_sameas_sym.premise.rdf", "rdfbased_sem_eqdis_sameas_sym.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_sameas_sym() throws Exception {
        testConsistency("rdfbased_sem_eqdis_sameas_sym.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_sameas_trans() throws Exception {
        testPositiveEntailment("rdfbased_sem_eqdis_sameas_trans.premise.rdf", "rdfbased_sem_eqdis_sameas_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_sameas_trans() throws Exception {
        testConsistency("rdfbased_sem_eqdis_sameas_trans.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_inv_inst() throws Exception {
        testPositiveEntailment("rdfbased_sem_inv_inst.premise.rdf", "rdfbased_sem_inv_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_inv_inst() throws Exception {
        testConsistency("rdfbased_sem_inv_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_key_def() throws Exception {
        testPositiveEntailment("rdfbased_sem_key_def.premise.rdf", "rdfbased_sem_key_def.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_key_def() throws Exception {
        testConsistency("rdfbased_sem_key_def.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_ndis_alldifferent_fw() throws Exception {
        testInconsistency("rdfbased_sem_ndis_alldifferent_fw.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_ndis_alldifferent_fw_distinctmembers() throws Exception {
        testInconsistency("rdfbased_sem_ndis_alldifferent_fw_distinctmembers.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_ndis_alldisjointclasses_fw() throws Exception {
        testInconsistency("rdfbased_sem_ndis_alldisjointclasses_fw.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_ndis_alldisjointproperties_fw() throws Exception {
        testInconsistency("rdfbased_sem_ndis_alldisjointproperties_fw.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_npa_dat_fw() throws Exception {
        testInconsistency("rdfbased_sem_npa_dat_fw.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_npa_ind_fw() throws Exception {
        testInconsistency("rdfbased_sem_npa_ind_fw.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_backwardcompatiblewith_type_annot() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_backwardcompatiblewith_type_annot.premise.rdf", "rdfbased_sem_prop_backwardcompatiblewith_type_annot.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_backwardcompatiblewith_type_annot() throws Exception {
        testConsistency("rdfbased_sem_prop_backwardcompatiblewith_type_annot.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_comment_type() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_comment_type.premise.rdf", "rdfbased_sem_prop_comment_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_comment_type() throws Exception {
        testConsistency("rdfbased_sem_prop_comment_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_deprecated_type() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_deprecated_type.premise.rdf", "rdfbased_sem_prop_deprecated_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_deprecated_type() throws Exception {
        testConsistency("rdfbased_sem_prop_deprecated_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_incompatiblewith_type_annot() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_incompatiblewith_type_annot.premise.rdf", "rdfbased_sem_prop_incompatiblewith_type_annot.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_incompatiblewith_type_annot() throws Exception {
        testConsistency("rdfbased_sem_prop_incompatiblewith_type_annot.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_isdefinedby_type() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_isdefinedby_type.premise.rdf", "rdfbased_sem_prop_isdefinedby_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_isdefinedby_type() throws Exception {
        testConsistency("rdfbased_sem_prop_isdefinedby_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_label_type() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_label_type.premise.rdf", "rdfbased_sem_prop_label_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_label_type() throws Exception {
        testConsistency("rdfbased_sem_prop_label_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_priorversion_type_annot() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_priorversion_type_annot.premise.rdf", "rdfbased_sem_prop_priorversion_type_annot.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_priorversion_type_annot() throws Exception {
        testConsistency("rdfbased_sem_prop_priorversion_type_annot.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_seealso_type() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_seealso_type.premise.rdf", "rdfbased_sem_prop_seealso_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_seealso_type() throws Exception {
        testConsistency("rdfbased_sem_prop_seealso_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_versioninfo_type() throws Exception {
        testPositiveEntailment("rdfbased_sem_prop_versioninfo_type.premise.rdf", "rdfbased_sem_prop_versioninfo_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_versioninfo_type() throws Exception {
        testConsistency("rdfbased_sem_prop_versioninfo_type.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_domain_cond() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfs_domain_cond.premise.rdf", "rdfbased_sem_rdfs_domain_cond.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_domain_cond() throws Exception {
        testConsistency("rdfbased_sem_rdfs_domain_cond.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_range_cond() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfs_range_cond.premise.rdf", "rdfbased_sem_rdfs_range_cond.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_range_cond() throws Exception {
        testConsistency("rdfbased_sem_rdfs_range_cond.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_subclass_cond() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfs_subclass_cond.premise.rdf", "rdfbased_sem_rdfs_subclass_cond.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_subclass_cond() throws Exception {
        testConsistency("rdfbased_sem_rdfs_subclass_cond.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_subclass_trans() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfs_subclass_trans.premise.rdf", "rdfbased_sem_rdfs_subclass_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_subclass_trans() throws Exception {
        testConsistency("rdfbased_sem_rdfs_subclass_trans.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_subprop_cond() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfs_subprop_cond.premise.rdf", "rdfbased_sem_rdfs_subprop_cond.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_subprop_cond() throws Exception {
        testConsistency("rdfbased_sem_rdfs_subprop_cond.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_subprop_trans() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfs_subprop_trans.premise.rdf", "rdfbased_sem_rdfs_subprop_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_subprop_trans() throws Exception {
        testConsistency("rdfbased_sem_rdfs_subprop_trans.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfsext_domain_subprop() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfsext_domain_subprop.premise.rdf", "rdfbased_sem_rdfsext_domain_subprop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfsext_domain_subprop() throws Exception {
        testConsistency("rdfbased_sem_rdfsext_domain_subprop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfsext_domain_superclass() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfsext_domain_superclass.premise.rdf", "rdfbased_sem_rdfsext_domain_superclass.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfsext_domain_superclass() throws Exception {
        testConsistency("rdfbased_sem_rdfsext_domain_superclass.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfsext_range_subprop() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfsext_range_subprop.premise.rdf", "rdfbased_sem_rdfsext_range_subprop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfsext_range_subprop() throws Exception {
        testConsistency("rdfbased_sem_rdfsext_range_subprop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfsext_range_superclass() throws Exception {
        testPositiveEntailment("rdfbased_sem_rdfsext_range_superclass.premise.rdf", "rdfbased_sem_rdfsext_range_superclass.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfsext_range_superclass() throws Exception {
        testConsistency("rdfbased_sem_rdfsext_range_superclass.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_allvalues_cmp_class() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_allvalues_cmp_class.premise.rdf", "rdfbased_sem_restrict_allvalues_cmp_class.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_allvalues_cmp_class() throws Exception {
        testConsistency("rdfbased_sem_restrict_allvalues_cmp_class.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_allvalues_cmp_prop() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_allvalues_cmp_prop.premise.rdf", "rdfbased_sem_restrict_allvalues_cmp_prop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_allvalues_cmp_prop() throws Exception {
        testConsistency("rdfbased_sem_restrict_allvalues_cmp_prop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_allvalues_inst_obj() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_allvalues_inst_obj.premise.rdf", "rdfbased_sem_restrict_allvalues_inst_obj.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_allvalues_inst_obj() throws Exception {
        testConsistency("rdfbased_sem_restrict_allvalues_inst_obj.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_hasvalue_cmp_prop() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_hasvalue_cmp_prop.premise.rdf", "rdfbased_sem_restrict_hasvalue_cmp_prop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_hasvalue_cmp_prop() throws Exception {
        testConsistency("rdfbased_sem_restrict_hasvalue_cmp_prop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_hasvalue_inst_obj() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_hasvalue_inst_obj.premise.rdf", "rdfbased_sem_restrict_hasvalue_inst_obj.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_hasvalue_inst_obj() throws Exception {
        testConsistency("rdfbased_sem_restrict_hasvalue_inst_obj.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_hasvalue_inst_subj() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_hasvalue_inst_subj.premise.rdf", "rdfbased_sem_restrict_hasvalue_inst_subj.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_hasvalue_inst_subj() throws Exception {
        testConsistency("rdfbased_sem_restrict_hasvalue_inst_subj.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_maxcard_inst_obj_one() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_maxcard_inst_obj_one.premise.rdf", "rdfbased_sem_restrict_maxcard_inst_obj_one.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_maxcard_inst_obj_one() throws Exception {
        testConsistency("rdfbased_sem_restrict_maxcard_inst_obj_one.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_restrict_maxcard_inst_obj_zero() throws Exception {
        testInconsistency("rdfbased_sem_restrict_maxcard_inst_obj_zero.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_maxqcr_inst_obj_one() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_maxqcr_inst_obj_one.premise.rdf", "rdfbased_sem_restrict_maxqcr_inst_obj_one.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_maxqcr_inst_obj_one() throws Exception {
        testConsistency("rdfbased_sem_restrict_maxqcr_inst_obj_one.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_restrict_maxqcr_inst_obj_zero() throws Exception {
        testInconsistency("rdfbased_sem_restrict_maxqcr_inst_obj_zero.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_somevalues_cmp_class() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_somevalues_cmp_class.premise.rdf", "rdfbased_sem_restrict_somevalues_cmp_class.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_somevalues_cmp_class() throws Exception {
        testConsistency("rdfbased_sem_restrict_somevalues_cmp_class.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_somevalues_cmp_prop() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_somevalues_cmp_prop.premise.rdf", "rdfbased_sem_restrict_somevalues_cmp_prop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_somevalues_cmp_prop() throws Exception {
        testConsistency("rdfbased_sem_restrict_somevalues_cmp_prop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_somevalues_inst_subj() throws Exception {
        testPositiveEntailment("rdfbased_sem_restrict_somevalues_inst_subj.premise.rdf", "rdfbased_sem_restrict_somevalues_inst_subj.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_somevalues_inst_subj() throws Exception {
        testConsistency("rdfbased_sem_restrict_somevalues_inst_subj.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_somevaluesfrom2bnode() throws Exception {
        testPositiveEntailment("somevaluesfrom2bnode.premise.rdf", "somevaluesfrom2bnode.conclusion.rdf");
    }

    @Test
    public void testConsistency_somevaluesfrom2bnode() throws Exception {
        testConsistency("somevaluesfrom2bnode.premise.rdf");
    }

    @Test
    public void testInconsistency_string_integer_clash() throws Exception {
        testInconsistency("string_integer_clash.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_AnnotationProperty_002() throws Exception {
        testPositiveEntailment("WebOnt_AnnotationProperty_002.premise.rdf", "WebOnt_AnnotationProperty_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_AnnotationProperty_002() throws Exception {
        testConsistency("WebOnt_AnnotationProperty_002.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_AnnotationProperty_003() throws Exception {
        testConsistency("WebOnt_AnnotationProperty_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_AnnotationProperty_004() throws Exception {
        testConsistency("WebOnt_AnnotationProperty_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I4_5_001() throws Exception {
        testPositiveEntailment("WebOnt_I4_5_001.premise.rdf", "WebOnt_I4_5_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I4_5_001() throws Exception {
        testConsistency("WebOnt_I4_5_001.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_I4_5_002() throws Exception {
        testInconsistency("WebOnt_I4_5_002.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_I4_6_004() throws Exception {
        testNegativeEntailment("WebOnt_I4_6_004.premise.rdf", "WebOnt_I4_6_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I4_6_004() throws Exception {
        testConsistency("WebOnt_I4_6_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I4_6_005_Direct() throws Exception {
        testPositiveEntailment("WebOnt_I4_6_005_Direct.premise.rdf", "WebOnt_I4_6_005_Direct.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I4_6_005_Direct() throws Exception {
        testConsistency("WebOnt_I4_6_005_Direct.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_001() throws Exception {
        testConsistency("WebOnt_I5_2_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_2_002() throws Exception {
        testPositiveEntailment("WebOnt_I5_2_002.premise.rdf", "WebOnt_I5_2_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_002() throws Exception {
        testConsistency("WebOnt_I5_2_002.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_003() throws Exception {
        testConsistency("WebOnt_I5_2_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_2_004() throws Exception {
        testPositiveEntailment("WebOnt_I5_2_004.premise.rdf", "WebOnt_I5_2_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_004() throws Exception {
        testConsistency("WebOnt_I5_2_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_005() throws Exception {
        testConsistency("WebOnt_I5_2_005.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_2_006() throws Exception {
        testPositiveEntailment("WebOnt_I5_2_006.premise.rdf", "WebOnt_I5_2_006.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_006() throws Exception {
        testConsistency("WebOnt_I5_2_006.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_21_002() throws Exception {
        testPositiveEntailment("WebOnt_I5_21_002.premise.rdf", "WebOnt_I5_21_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_21_002() throws Exception {
        testConsistency("WebOnt_I5_21_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_24_003() throws Exception {
        testPositiveEntailment("WebOnt_I5_24_003.premise.rdf", "WebOnt_I5_24_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_24_003() throws Exception {
        testConsistency("WebOnt_I5_24_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_24_004() throws Exception {
        testPositiveEntailment("WebOnt_I5_24_004.premise.rdf", "WebOnt_I5_24_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_24_004() throws Exception {
        testConsistency("WebOnt_I5_24_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_001() throws Exception {
        testConsistency("WebOnt_I5_26_001.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_002() throws Exception {
        testConsistency("WebOnt_I5_26_002.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_003() throws Exception {
        testConsistency("WebOnt_I5_26_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_004() throws Exception {
        testConsistency("WebOnt_I5_26_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_005() throws Exception {
        testConsistency("WebOnt_I5_26_005.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_26_009() throws Exception {
        testPositiveEntailment("WebOnt_I5_26_009.premise.rdf", "WebOnt_I5_26_009.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_009() throws Exception {
        testConsistency("WebOnt_I5_26_009.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_26_010() throws Exception {
        testPositiveEntailment("WebOnt_I5_26_010.premise.rdf", "WebOnt_I5_26_010.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_010() throws Exception {
        testConsistency("WebOnt_I5_26_010.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_3_006() throws Exception {
        testConsistency("WebOnt_I5_3_006.premise.rdf");
    }
}
