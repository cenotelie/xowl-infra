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
    public void testConsistency_WebOnt_I5_3_008() {
        testConsistency("WebOnt_I5_3_008.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_3_010() {
        testConsistency("WebOnt_I5_3_010.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_3_011() {
        testConsistency("WebOnt_I5_3_011.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_5_005() {
        testPositiveEntailment("WebOnt_I5_5_005.premise.rdf", "WebOnt_I5_5_005.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_5_005() {
        testConsistency("WebOnt_I5_5_005.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_006() {
        testPositiveEntailment("WebOnt_I5_8_006.premise.rdf", "WebOnt_I5_8_006.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_006() {
        testConsistency("WebOnt_I5_8_006.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_I5_8_007() {
        testNegativeEntailment("WebOnt_I5_8_007.premise.rdf", "WebOnt_I5_8_007.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_007() {
        testConsistency("WebOnt_I5_8_007.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_008() {
        testPositiveEntailment("WebOnt_I5_8_008.premise.rdf", "WebOnt_I5_8_008.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_008() {
        testConsistency("WebOnt_I5_8_008.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_009() {
        testPositiveEntailment("WebOnt_I5_8_009.premise.rdf", "WebOnt_I5_8_009.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_009() {
        testConsistency("WebOnt_I5_8_009.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_010() {
        testPositiveEntailment("WebOnt_I5_8_010.premise.rdf", "WebOnt_I5_8_010.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_010() {
        testConsistency("WebOnt_I5_8_010.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_8_011() {
        testPositiveEntailment("WebOnt_I5_8_011.premise.rdf", "WebOnt_I5_8_011.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_8_011() {
        testConsistency("WebOnt_I5_8_011.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_InverseFunctionalProperty_001() {
        testPositiveEntailment("WebOnt_InverseFunctionalProperty_001.premise.rdf", "WebOnt_InverseFunctionalProperty_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_InverseFunctionalProperty_001() {
        testConsistency("WebOnt_InverseFunctionalProperty_001.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_Nothing_001() {
        testInconsistency("WebOnt_Nothing_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_Ontology_001() {
        testPositiveEntailment("WebOnt_Ontology_001.premise.rdf", "WebOnt_Ontology_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_Ontology_001() {
        testConsistency("WebOnt_Ontology_001.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_Restriction_001() {
        testInconsistency("WebOnt_Restriction_001.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_Restriction_002() {
        testInconsistency("WebOnt_Restriction_002.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_Restriction_003() {
        testConsistency("WebOnt_Restriction_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_Restriction_004() {
        testConsistency("WebOnt_Restriction_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_SymmetricProperty_002() {
        testPositiveEntailment("WebOnt_SymmetricProperty_002.premise.rdf", "WebOnt_SymmetricProperty_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_SymmetricProperty_002() {
        testConsistency("WebOnt_SymmetricProperty_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_SymmetricProperty_003() {
        testPositiveEntailment("WebOnt_SymmetricProperty_003.premise.rdf", "WebOnt_SymmetricProperty_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_SymmetricProperty_003() {
        testConsistency("WebOnt_SymmetricProperty_003.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_Thing_003() {
        testInconsistency("WebOnt_Thing_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_Thing_004() {
        testConsistency("WebOnt_Thing_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_bnode2somevaluesfrom() {
        testPositiveEntailment("bnode2somevaluesfrom.premise.rdf", "bnode2somevaluesfrom.conclusion.rdf");
    }

    @Test
    public void testConsistency_bnode2somevaluesfrom() {
        testConsistency("bnode2somevaluesfrom.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_TransitiveProperty_002() {
        testPositiveEntailment("WebOnt_TransitiveProperty_002.premise.rdf", "WebOnt_TransitiveProperty_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_TransitiveProperty_002() {
        testConsistency("WebOnt_TransitiveProperty_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_allValuesFrom_001() {
        testPositiveEntailment("WebOnt_allValuesFrom_001.premise.rdf", "WebOnt_allValuesFrom_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_allValuesFrom_001() {
        testConsistency("WebOnt_allValuesFrom_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_chain2trans1() {
        testPositiveEntailment("chain2trans1.premise.rdf", "chain2trans1.conclusion.rdf");
    }

    @Test
    public void testConsistency_chain2trans1() {
        testConsistency("chain2trans1.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_allValuesFrom_002() {
        testNegativeEntailment("WebOnt_allValuesFrom_002.premise.rdf", "WebOnt_allValuesFrom_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_allValuesFrom_002() {
        testConsistency("WebOnt_allValuesFrom_002.premise.rdf");
    }

    @Test
    public void testConsistency_Consistent_Datatype_restrictions_with_Different_Types() {
        testConsistency("Consistent_Datatype_restrictions_with_Different_Types.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_backwardCompatibleWith_002() {
        testConsistency("WebOnt_backwardCompatibleWith_002.premise.rdf");
    }

    @Test
    public void testConsistency_Consistent_owl_real_range_with_DataOneOf() {
        testConsistency("Consistent_owl_real_range_with_DataOneOf.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_cardinality_001() {
        testPositiveEntailment("WebOnt_cardinality_001.premise.rdf", "WebOnt_cardinality_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_cardinality_001() {
        testConsistency("WebOnt_cardinality_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_Consistent_but_all_unsat() {
        testPositiveEntailment("Consistent_but_all_unsat.premise.rdf", "Consistent_but_all_unsat.conclusion.rdf");
    }

    @Test
    public void testConsistency_Consistent_but_all_unsat() {
        testConsistency("Consistent_but_all_unsat.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_cardinality_002() {
        testPositiveEntailment("WebOnt_cardinality_002.premise.rdf", "WebOnt_cardinality_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_cardinality_002() {
        testConsistency("WebOnt_cardinality_002.premise.rdf");
    }

    @Test
    public void testConsistency_consistent_dataproperty_disjointness() {
        testConsistency("consistent_dataproperty_disjointness.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_cardinality_003() {
        testPositiveEntailment("WebOnt_cardinality_003.premise.rdf", "WebOnt_cardinality_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_cardinality_003() {
        testConsistency("WebOnt_cardinality_003.premise.rdf");
    }

    @Test
    public void testConsistency_Consistent_dateTime() {
        testConsistency("Consistent_dateTime.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_cardinality_004() {
        testPositiveEntailment("WebOnt_cardinality_004.premise.rdf", "WebOnt_cardinality_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_cardinality_004() {
        testConsistency("WebOnt_cardinality_004.premise.rdf");
    }

    @Test
    public void testConsistency_consistent_integer_filler() {
        testConsistency("consistent_integer_filler.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_001() {
        testInconsistency("WebOnt_description_logic_001.premise.rdf");
    }

    @Test
    public void testInconsistency_Contradicting_datatype_Restrictions() {
        testInconsistency("Contradicting_datatype_Restrictions.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_002() {
        testInconsistency("WebOnt_description_logic_002.premise.rdf");
    }

    @Test
    public void testInconsistency_Contradicting_dateTime_restrictions() {
        testInconsistency("Contradicting_dateTime_restrictions.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_003() {
        testInconsistency("WebOnt_description_logic_003.premise.rdf");
    }

    @Test
    public void testConsistency_Datatype_DataComplementOf_001() {
        testConsistency("Datatype_DataComplementOf_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_004() {
        testInconsistency("WebOnt_description_logic_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_005() {
        testConsistency("WebOnt_description_logic_005.premise.rdf");
    }

    @Test
    public void testInconsistency_Datatype_Float_Discrete_001() {
        testInconsistency("Datatype_Float_Discrete_001.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_006() {
        testConsistency("WebOnt_description_logic_006.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_007() {
        testInconsistency("WebOnt_description_logic_007.premise.rdf");
    }

    @Test
    public void testInconsistency_datatype_restriction_min_max_inconsistency() {
        testInconsistency("datatype_restriction_min_max_inconsistency.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_008() {
        testInconsistency("WebOnt_description_logic_008.premise.rdf");
    }

    @Test
    public void testInconsistency_Different_types_in_Datatype_Restrictions_and_Complement() {
        testInconsistency("Different_types_in_Datatype_Restrictions_and_Complement.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_009() {
        testConsistency("WebOnt_description_logic_009.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_DisjointClasses_001() {
        testPositiveEntailment("DisjointClasses_001.premise.fs", "DisjointClasses_001.conclusion.fs");
    }

    @Test
    public void testConsistency_DisjointClasses_001() {
        testConsistency("DisjointClasses_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_010() {
        testInconsistency("WebOnt_description_logic_010.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_011() {
        testInconsistency("WebOnt_description_logic_011.premise.rdf");
    }

    @Test
    public void testInconsistency_DisjointClasses_002() {
        testInconsistency("DisjointClasses_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_012() {
        testInconsistency("WebOnt_description_logic_012.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_DisjointClasses_003() {
        testPositiveEntailment("DisjointClasses_003.premise.fs", "DisjointClasses_003.conclusion.fs");
    }

    @Test
    public void testConsistency_DisjointClasses_003() {
        testConsistency("DisjointClasses_003.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_013() {
        testInconsistency("WebOnt_description_logic_013.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_014() {
        testInconsistency("WebOnt_description_logic_014.premise.rdf");
    }

    @Test
    public void testConsistency_FS2RDF_different_individuals_2_ar() {
        testConsistency("FS2RDF_different_individuals_2_ar.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_015() {
        testInconsistency("WebOnt_description_logic_015.premise.rdf");
    }

    @Test
    public void testConsistency_FS2RDF_different_individuals_3_ar() {
        testConsistency("FS2RDF_different_individuals_3_ar.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_016() {
        testConsistency("WebOnt_description_logic_016.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_017() {
        testInconsistency("WebOnt_description_logic_017.premise.rdf");
    }

    @Test
    public void testConsistency_FS2RDF_no_builtin_prefixes_ar() {
        testConsistency("FS2RDF_no_builtin_prefixes_ar.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_018() {
        testConsistency("WebOnt_description_logic_018.premise.rdf");
    }

    @Test
    public void testConsistency_FS2RDF_same_individual_2_ar() {
        testConsistency("FS2RDF_same_individual_2_ar.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_019() {
        testInconsistency("WebOnt_description_logic_019.premise.rdf");
    }

    @Test
    public void testInconsistency_functionality_clash() {
        testInconsistency("functionality_clash.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_020() {
        testConsistency("WebOnt_description_logic_020.premise.rdf");
    }

    @Test
    public void testInconsistency_Inconsistent_Byte_Filler() {
        testInconsistency("Inconsistent_Byte_Filler.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_021() {
        testConsistency("WebOnt_description_logic_021.premise.rdf");
    }

    @Test
    public void testInconsistency_Inconsistent_Data_Complement_with_the_Restrictions() {
        testInconsistency("Inconsistent_Data_Complement_with_the_Restrictions.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_022() {
        testInconsistency("WebOnt_description_logic_022.premise.rdf");
    }

    @Test
    public void testInconsistency_Inconsistent_Disjoint_Dataproperties() {
        testInconsistency("Inconsistent_Disjoint_Dataproperties.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_023() {
        testInconsistency("WebOnt_description_logic_023.premise.rdf");
    }

    @Test
    public void testInconsistency_inconsistent_integer_filler() {
        testInconsistency("inconsistent_integer_filler.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_024() {
        testConsistency("WebOnt_description_logic_024.premise.rdf");
    }

    @Test
    public void testInconsistency_inconsistent_datatypes() {
        testInconsistency("inconsistent_datatypes.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_025() {
        testConsistency("WebOnt_description_logic_025.premise.rdf");
    }

    @Test
    public void testInconsistency_Minus_Infinity_is_not_in_owl_real() {
        testInconsistency("Minus_Infinity_is_not_in_owl_real.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_026() {
        testInconsistency("WebOnt_description_logic_026.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_AnnotationAnnotations_001() {
        testConsistency("New_Feature_AnnotationAnnotations_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_027() {
        testInconsistency("WebOnt_description_logic_027.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_028() {
        testConsistency("WebOnt_description_logic_028.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_AsymmetricProperty_001() {
        testInconsistency("New_Feature_AsymmetricProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_029() {
        testInconsistency("WebOnt_description_logic_029.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_AxiomAnnotations_001() {
        testConsistency("New_Feature_AxiomAnnotations_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_030() {
        testInconsistency("WebOnt_description_logic_030.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_031() {
        testConsistency("WebOnt_description_logic_031.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_BottomDataProperty_001() {
        testInconsistency("New_Feature_BottomDataProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_032() {
        testInconsistency("WebOnt_description_logic_032.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_BottomObjectProperty_001() {
        testInconsistency("New_Feature_BottomObjectProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_033() {
        testInconsistency("WebOnt_description_logic_033.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DataQCR_001() {
        testPositiveEntailment("New_Feature_DataQCR_001.premise.fs", "New_Feature_DataQCR_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DataQCR_001() {
        testConsistency("New_Feature_DataQCR_001.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_034() {
        testConsistency("WebOnt_description_logic_034.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_035() {
        testInconsistency("WebOnt_description_logic_035.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_DisjointDataProperties_001() {
        testInconsistency("New_Feature_DisjointDataProperties_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_040() {
        testInconsistency("WebOnt_description_logic_040.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_101() {
        testInconsistency("WebOnt_description_logic_101.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DisjointDataProperties_002() {
        testPositiveEntailment("New_Feature_DisjointDataProperties_002.premise.fs", "New_Feature_DisjointDataProperties_002.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DisjointDataProperties_002() {
        testConsistency("New_Feature_DisjointDataProperties_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_102() {
        testInconsistency("WebOnt_description_logic_102.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_103() {
        testInconsistency("WebOnt_description_logic_103.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DisjointObjectProperties_001() {
        testPositiveEntailment("New_Feature_DisjointObjectProperties_001.premise.fs", "New_Feature_DisjointObjectProperties_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DisjointObjectProperties_001() {
        testConsistency("New_Feature_DisjointObjectProperties_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_104() {
        testInconsistency("WebOnt_description_logic_104.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_105() {
        testInconsistency("WebOnt_description_logic_105.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DisjointObjectProperties_002() {
        testPositiveEntailment("New_Feature_DisjointObjectProperties_002.premise.fs", "New_Feature_DisjointObjectProperties_002.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DisjointObjectProperties_002() {
        testConsistency("New_Feature_DisjointObjectProperties_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_106() {
        testInconsistency("WebOnt_description_logic_106.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_DisjointUnion_001() {
        testPositiveEntailment("New_Feature_DisjointUnion_001.premise.fs", "New_Feature_DisjointUnion_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_DisjointUnion_001() {
        testConsistency("New_Feature_DisjointUnion_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_107() {
        testInconsistency("WebOnt_description_logic_107.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_108() {
        testInconsistency("WebOnt_description_logic_108.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_IrreflexiveProperty_001() {
        testInconsistency("New_Feature_IrreflexiveProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_109() {
        testInconsistency("WebOnt_description_logic_109.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_110() {
        testInconsistency("WebOnt_description_logic_110.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_Keys_001() {
        testPositiveEntailment("New_Feature_Keys_001.premise.fs", "New_Feature_Keys_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_Keys_001() {
        testConsistency("New_Feature_Keys_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_111() {
        testInconsistency("WebOnt_description_logic_111.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_201() {
        testPositiveEntailment("WebOnt_description_logic_201.premise.rdf", "WebOnt_description_logic_201.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_201() {
        testConsistency("WebOnt_description_logic_201.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_Keys_002() {
        testInconsistency("New_Feature_Keys_002.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_205() {
        testPositiveEntailment("WebOnt_description_logic_205.premise.rdf", "WebOnt_description_logic_205.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_205() {
        testConsistency("WebOnt_description_logic_205.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_Keys_003() {
        testPositiveEntailment("New_Feature_Keys_003.premise.fs", "New_Feature_Keys_003.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_Keys_003() {
        testConsistency("New_Feature_Keys_003.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_207() {
        testPositiveEntailment("WebOnt_description_logic_207.premise.rdf", "WebOnt_description_logic_207.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_207() {
        testConsistency("WebOnt_description_logic_207.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_New_Feature_Keys_004() {
        testNegativeEntailment("New_Feature_Keys_004.premise.fs", "New_Feature_Keys_004.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_Keys_004() {
        testConsistency("New_Feature_Keys_004.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_208() {
        testPositiveEntailment("WebOnt_description_logic_208.premise.rdf", "WebOnt_description_logic_208.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_208() {
        testConsistency("WebOnt_description_logic_208.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_Keys_005() {
        testConsistency("New_Feature_Keys_005.premise.fs");
    }

    @Test
    public void testNegativeEntailment_WebOnt_description_logic_209() {
        testNegativeEntailment("WebOnt_description_logic_209.premise.rdf", "WebOnt_description_logic_209.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_209() {
        testConsistency("WebOnt_description_logic_209.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_Keys_006() {
        testInconsistency("New_Feature_Keys_006.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_501() {
        testConsistency("WebOnt_description_logic_501.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_New_Feature_Keys_007() {
        testNegativeEntailment("New_Feature_Keys_007.premise.fs", "New_Feature_Keys_007.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_Keys_007() {
        testConsistency("New_Feature_Keys_007.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_502() {
        testInconsistency("WebOnt_description_logic_502.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_503() {
        testConsistency("WebOnt_description_logic_503.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_NegativeDataPropertyAssertion_001() {
        testInconsistency("New_Feature_NegativeDataPropertyAssertion_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_504() {
        testInconsistency("WebOnt_description_logic_504.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_NegativeObjectPropertyAssertion_001() {
        testInconsistency("New_Feature_NegativeObjectPropertyAssertion_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_601() {
        testInconsistency("WebOnt_description_logic_601.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_602() {
        testInconsistency("WebOnt_description_logic_602.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ObjectPropertyChain_001() {
        testPositiveEntailment("New_Feature_ObjectPropertyChain_001.premise.fs", "New_Feature_ObjectPropertyChain_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_ObjectPropertyChain_001() {
        testConsistency("New_Feature_ObjectPropertyChain_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_603() {
        testInconsistency("WebOnt_description_logic_603.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_604() {
        testInconsistency("WebOnt_description_logic_604.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_605() {
        testConsistency("WebOnt_description_logic_605.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ObjectPropertyChain_BJP_003() {
        testPositiveEntailment("New_Feature_ObjectPropertyChain_BJP_003.premise.rdf", "New_Feature_ObjectPropertyChain_BJP_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_New_Feature_ObjectPropertyChain_BJP_003() {
        testConsistency("New_Feature_ObjectPropertyChain_BJP_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_606() {
        testConsistency("WebOnt_description_logic_606.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_New_Feature_ObjectPropertyChain_BJP_004() {
        testNegativeEntailment("New_Feature_ObjectPropertyChain_BJP_004.premise.rdf", "New_Feature_ObjectPropertyChain_BJP_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_New_Feature_ObjectPropertyChain_BJP_004() {
        testConsistency("New_Feature_ObjectPropertyChain_BJP_004.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_608() {
        testInconsistency("WebOnt_description_logic_608.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_609() {
        testConsistency("WebOnt_description_logic_609.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ObjectQCR_001() {
        testPositiveEntailment("New_Feature_ObjectQCR_001.premise.fs", "New_Feature_ObjectQCR_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_ObjectQCR_001() {
        testConsistency("New_Feature_ObjectQCR_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_610() {
        testInconsistency("WebOnt_description_logic_610.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_611() {
        testInconsistency("WebOnt_description_logic_611.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_612() {
        testInconsistency("WebOnt_description_logic_612.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ObjectQCR_002() {
        testPositiveEntailment("New_Feature_ObjectQCR_002.premise.fs", "New_Feature_ObjectQCR_002.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_ObjectQCR_002() {
        testConsistency("New_Feature_ObjectQCR_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_613() {
        testInconsistency("WebOnt_description_logic_613.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_614() {
        testInconsistency("WebOnt_description_logic_614.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_Rational_001() {
        testConsistency("New_Feature_Rational_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_615() {
        testInconsistency("WebOnt_description_logic_615.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_616() {
        testConsistency("WebOnt_description_logic_616.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_Rational_002() {
        testInconsistency("New_Feature_Rational_002.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_617() {
        testInconsistency("WebOnt_description_logic_617.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_623() {
        testInconsistency("WebOnt_description_logic_623.premise.rdf");
    }

    @Test
    public void testConsistency_New_Feature_Rational_003() {
        testConsistency("New_Feature_Rational_003.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_624() {
        testConsistency("WebOnt_description_logic_624.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_625() {
        testConsistency("WebOnt_description_logic_625.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_ReflexiveProperty_001() {
        testPositiveEntailment("New_Feature_ReflexiveProperty_001.premise.fs", "New_Feature_ReflexiveProperty_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_ReflexiveProperty_001() {
        testConsistency("New_Feature_ReflexiveProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_626() {
        testInconsistency("WebOnt_description_logic_626.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_627() {
        testInconsistency("WebOnt_description_logic_627.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_SelfRestriction_001() {
        testPositiveEntailment("New_Feature_SelfRestriction_001.premise.fs", "New_Feature_SelfRestriction_001.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_SelfRestriction_001() {
        testConsistency("New_Feature_SelfRestriction_001.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_628() {
        testConsistency("WebOnt_description_logic_628.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_629() {
        testInconsistency("WebOnt_description_logic_629.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_630() {
        testInconsistency("WebOnt_description_logic_630.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_New_Feature_SelfRestriction_002() {
        testPositiveEntailment("New_Feature_SelfRestriction_002.premise.fs", "New_Feature_SelfRestriction_002.conclusion.fs");
    }

    @Test
    public void testConsistency_New_Feature_SelfRestriction_002() {
        testConsistency("New_Feature_SelfRestriction_002.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_631() {
        testConsistency("WebOnt_description_logic_631.premise.rdf");
    }

    @Test
    public void testInconsistency_New_Feature_TopObjectProperty_001() {
        testInconsistency("New_Feature_TopObjectProperty_001.premise.fs");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_632() {
        testInconsistency("WebOnt_description_logic_632.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_633() {
        testInconsistency("WebOnt_description_logic_633.premise.rdf");
    }

    @Test
    public void testInconsistency_one_two() {
        testInconsistency("one_two.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_634() {
        testConsistency("WebOnt_description_logic_634.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_anonymous_individual() {
        testConsistency("owl2_rl_anonymous_individual.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_641() {
        testInconsistency("WebOnt_description_logic_641.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_642() {
        testInconsistency("WebOnt_description_logic_642.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_leftside_allvaluesfrom() {
        testConsistency("owl2_rl_invalid_leftside_allvaluesfrom.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_643() {
        testInconsistency("WebOnt_description_logic_643.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_leftside_maxcard() {
        testConsistency("owl2_rl_invalid_leftside_maxcard.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_644() {
        testInconsistency("WebOnt_description_logic_644.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_oneof() {
        testConsistency("owl2_rl_invalid_oneof.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_646() {
        testInconsistency("WebOnt_description_logic_646.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_description_logic_650() {
        testInconsistency("WebOnt_description_logic_650.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_owlreal() {
        testConsistency("owl2_rl_invalid_owlreal.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_661() {
        testPositiveEntailment("WebOnt_description_logic_661.premise.rdf", "WebOnt_description_logic_661.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_661() {
        testConsistency("WebOnt_description_logic_661.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_rightside_somevaluesfrom() {
        testConsistency("owl2_rl_invalid_rightside_somevaluesfrom.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_665() {
        testPositiveEntailment("WebOnt_description_logic_665.premise.rdf", "WebOnt_description_logic_665.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_665() {
        testConsistency("WebOnt_description_logic_665.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_rightside_unionof() {
        testConsistency("owl2_rl_invalid_rightside_unionof.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_667() {
        testPositiveEntailment("WebOnt_description_logic_667.premise.rdf", "WebOnt_description_logic_667.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_667() {
        testConsistency("WebOnt_description_logic_667.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_invalid_unionof() {
        testConsistency("owl2_rl_invalid_unionof.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_description_logic_901() {
        testPositiveEntailment("WebOnt_description_logic_901.premise.rdf", "WebOnt_description_logic_901.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_901() {
        testConsistency("WebOnt_description_logic_901.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_owl2_rl_rules_fp_differentFrom() {
        testPositiveEntailment("owl2_rl_rules_fp_differentFrom.premise.rdf", "owl2_rl_rules_fp_differentFrom.conclusion.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_rules_fp_differentFrom() {
        testConsistency("owl2_rl_rules_fp_differentFrom.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_description_logic_902() {
        testNegativeEntailment("WebOnt_description_logic_902.premise.rdf", "WebOnt_description_logic_902.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_902() {
        testConsistency("WebOnt_description_logic_902.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_owl2_rl_rules_ifp_differentFrom() {
        testPositiveEntailment("owl2_rl_rules_ifp_differentFrom.premise.rdf", "owl2_rl_rules_ifp_differentFrom.conclusion.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_rules_ifp_differentFrom() {
        testConsistency("owl2_rl_rules_ifp_differentFrom.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_905() {
        testConsistency("WebOnt_description_logic_905.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_valid_mincard() {
        testConsistency("owl2_rl_valid_mincard.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_description_logic_908() {
        testConsistency("WebOnt_description_logic_908.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_valid_oneof() {
        testConsistency("owl2_rl_valid_oneof.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_differentFrom_001() {
        testPositiveEntailment("WebOnt_differentFrom_001.premise.rdf", "WebOnt_differentFrom_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_differentFrom_001() {
        testConsistency("WebOnt_differentFrom_001.premise.rdf");
    }

    @Test
    public void testConsistency_owl2_rl_valid_rightside_allvaluesfrom() {
        testConsistency("owl2_rl_valid_rightside_allvaluesfrom.premise.rdf");
    }

    @Test
    public void testConsistency_Plus_and_Minus_Zero_Integer() {
        testConsistency("Plus_and_Minus_Zero_Integer.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_disjointWith_001() {
        testPositiveEntailment("WebOnt_disjointWith_001.premise.rdf", "WebOnt_disjointWith_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_001() {
        testConsistency("WebOnt_disjointWith_001.premise.rdf");
    }

    @Test
    public void testInconsistency_Plus_and_Minus_Zero_are_Distinct() {
        testInconsistency("Plus_and_Minus_Zero_are_Distinct.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_003() {
        testConsistency("WebOnt_disjointWith_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_Qualified_cardinality_boolean() {
        testPositiveEntailment("Qualified_cardinality_boolean.premise.fs", "Qualified_cardinality_boolean.conclusion.fs");
    }

    @Test
    public void testConsistency_Qualified_cardinality_boolean() {
        testConsistency("Qualified_cardinality_boolean.premise.fs");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_004() {
        testConsistency("WebOnt_disjointWith_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_005() {
        testConsistency("WebOnt_disjointWith_005.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_bool_complement_inst() {
        testInconsistency("rdfbased_sem_bool_complement_inst.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_006() {
        testConsistency("WebOnt_disjointWith_006.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_intersection_inst_comp() {
        testPositiveEntailment("rdfbased_sem_bool_intersection_inst_comp.premise.rdf", "rdfbased_sem_bool_intersection_inst_comp.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_intersection_inst_comp() {
        testConsistency("rdfbased_sem_bool_intersection_inst_comp.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_007() {
        testConsistency("WebOnt_disjointWith_007.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_intersection_inst_expr() {
        testPositiveEntailment("rdfbased_sem_bool_intersection_inst_expr.premise.rdf", "rdfbased_sem_bool_intersection_inst_expr.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_intersection_inst_expr() {
        testConsistency("rdfbased_sem_bool_intersection_inst_expr.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_008() {
        testConsistency("WebOnt_disjointWith_008.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_disjointWith_009() {
        testConsistency("WebOnt_disjointWith_009.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_intersection_term() {
        testPositiveEntailment("rdfbased_sem_bool_intersection_term.premise.rdf", "rdfbased_sem_bool_intersection_term.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_intersection_term() {
        testConsistency("rdfbased_sem_bool_intersection_term.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_001() {
        testPositiveEntailment("WebOnt_equivalentClass_001.premise.rdf", "WebOnt_equivalentClass_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_001() {
        testConsistency("WebOnt_equivalentClass_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_union_inst_comp() {
        testPositiveEntailment("rdfbased_sem_bool_union_inst_comp.premise.rdf", "rdfbased_sem_bool_union_inst_comp.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_union_inst_comp() {
        testConsistency("rdfbased_sem_bool_union_inst_comp.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_002() {
        testPositiveEntailment("WebOnt_equivalentClass_002.premise.rdf", "WebOnt_equivalentClass_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_002() {
        testConsistency("WebOnt_equivalentClass_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_bool_union_term() {
        testPositiveEntailment("rdfbased_sem_bool_union_term.premise.rdf", "rdfbased_sem_bool_union_term.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_bool_union_term() {
        testConsistency("rdfbased_sem_bool_union_term.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_003() {
        testPositiveEntailment("WebOnt_equivalentClass_003.premise.rdf", "WebOnt_equivalentClass_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_003() {
        testConsistency("WebOnt_equivalentClass_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_chain_def() {
        testPositiveEntailment("rdfbased_sem_chain_def.premise.rdf", "rdfbased_sem_chain_def.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_chain_def() {
        testConsistency("rdfbased_sem_chain_def.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_004() {
        testPositiveEntailment("WebOnt_equivalentClass_004.premise.rdf", "WebOnt_equivalentClass_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_004() {
        testConsistency("WebOnt_equivalentClass_004.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_char_asymmetric_inst() {
        testInconsistency("rdfbased_sem_char_asymmetric_inst.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_equivalentClass_005() {
        testNegativeEntailment("WebOnt_equivalentClass_005.premise.rdf", "WebOnt_equivalentClass_005.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_005() {
        testConsistency("WebOnt_equivalentClass_005.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_char_asymmetric_term() {
        testInconsistency("rdfbased_sem_char_asymmetric_term.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_006() {
        testPositiveEntailment("WebOnt_equivalentClass_006.premise.rdf", "WebOnt_equivalentClass_006.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_006() {
        testConsistency("WebOnt_equivalentClass_006.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_functional_inst() {
        testPositiveEntailment("rdfbased_sem_char_functional_inst.premise.rdf", "rdfbased_sem_char_functional_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_functional_inst() {
        testConsistency("rdfbased_sem_char_functional_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentClass_008_Direct() {
        testPositiveEntailment("WebOnt_equivalentClass_008_Direct.premise.rdf", "WebOnt_equivalentClass_008_Direct.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_008_Direct() {
        testConsistency("WebOnt_equivalentClass_008_Direct.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_inversefunc_data() {
        testPositiveEntailment("rdfbased_sem_char_inversefunc_data.premise.rdf", "rdfbased_sem_char_inversefunc_data.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_inversefunc_data() {
        testConsistency("rdfbased_sem_char_inversefunc_data.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_inversefunc_inst() {
        testPositiveEntailment("rdfbased_sem_char_inversefunc_inst.premise.rdf", "rdfbased_sem_char_inversefunc_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_inversefunc_inst() {
        testConsistency("rdfbased_sem_char_inversefunc_inst.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentClass_009() {
        testConsistency("WebOnt_equivalentClass_009.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_char_irreflexive_inst() {
        testInconsistency("rdfbased_sem_char_irreflexive_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentProperty_001() {
        testPositiveEntailment("WebOnt_equivalentProperty_001.premise.rdf", "WebOnt_equivalentProperty_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentProperty_001() {
        testConsistency("WebOnt_equivalentProperty_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_symmetric_inst() {
        testPositiveEntailment("rdfbased_sem_char_symmetric_inst.premise.rdf", "rdfbased_sem_char_symmetric_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_symmetric_inst() {
        testConsistency("rdfbased_sem_char_symmetric_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentProperty_002() {
        testPositiveEntailment("WebOnt_equivalentProperty_002.premise.rdf", "WebOnt_equivalentProperty_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentProperty_002() {
        testConsistency("WebOnt_equivalentProperty_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_char_transitive_inst() {
        testPositiveEntailment("rdfbased_sem_char_transitive_inst.premise.rdf", "rdfbased_sem_char_transitive_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_char_transitive_inst() {
        testConsistency("rdfbased_sem_char_transitive_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentProperty_003() {
        testPositiveEntailment("WebOnt_equivalentProperty_003.premise.rdf", "WebOnt_equivalentProperty_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentProperty_003() {
        testConsistency("WebOnt_equivalentProperty_003.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_class_nothing_ext() {
        testInconsistency("rdfbased_sem_class_nothing_ext.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_equivalentProperty_004() {
        testPositiveEntailment("WebOnt_equivalentProperty_004.premise.rdf", "WebOnt_equivalentProperty_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_equivalentProperty_004() {
        testConsistency("WebOnt_equivalentProperty_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_class_nothing_term() {
        testPositiveEntailment("rdfbased_sem_class_nothing_term.premise.rdf", "rdfbased_sem_class_nothing_term.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_class_nothing_term() {
        testConsistency("rdfbased_sem_class_nothing_term.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_imports_011() {
        testPositiveEntailment("WebOnt_imports_011.premise.rdf", "WebOnt_imports_011.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_imports_011() {
        testConsistency("WebOnt_imports_011.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_class_nothing_type() {
        testPositiveEntailment("rdfbased_sem_class_nothing_type.premise.rdf", "rdfbased_sem_class_nothing_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_class_nothing_type() {
        testConsistency("rdfbased_sem_class_nothing_type.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_class_thing_term() {
        testPositiveEntailment("rdfbased_sem_class_thing_term.premise.rdf", "rdfbased_sem_class_thing_term.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_class_thing_term() {
        testConsistency("rdfbased_sem_class_thing_term.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_maxCardinality_001() {
        testInconsistency("WebOnt_maxCardinality_001.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_class_thing_type() {
        testPositiveEntailment("rdfbased_sem_class_thing_type.premise.rdf", "rdfbased_sem_class_thing_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_class_thing_type() {
        testConsistency("rdfbased_sem_class_thing_type.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_001() {
        testConsistency("WebOnt_miscellaneous_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_enum_inst_included() {
        testPositiveEntailment("rdfbased_sem_enum_inst_included.premise.rdf", "rdfbased_sem_enum_inst_included.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_enum_inst_included() {
        testConsistency("rdfbased_sem_enum_inst_included.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_different_irrflxv() {
        testInconsistency("rdfbased_sem_eqdis_different_irrflxv.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_002() {
        testConsistency("WebOnt_miscellaneous_002.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_different_sameas() {
        testInconsistency("rdfbased_sem_eqdis_different_sameas.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disclass_eqclass() {
        testInconsistency("rdfbased_sem_eqdis_disclass_eqclass.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_102() {
        testConsistency("WebOnt_miscellaneous_102.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disclass_inst() {
        testInconsistency("rdfbased_sem_eqdis_disclass_inst.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_103() {
        testConsistency("WebOnt_miscellaneous_103.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disclass_irrflxv() {
        testInconsistency("rdfbased_sem_eqdis_disclass_irrflxv.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_202() {
        testConsistency("WebOnt_miscellaneous_202.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disprop_eqprop() {
        testInconsistency("rdfbased_sem_eqdis_disprop_eqprop.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_miscellaneous_203() {
        testInconsistency("WebOnt_miscellaneous_203.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disprop_inst() {
        testInconsistency("rdfbased_sem_eqdis_disprop_inst.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_miscellaneous_204() {
        testInconsistency("WebOnt_miscellaneous_204.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_eqdis_disprop_irrflxv() {
        testInconsistency("rdfbased_sem_eqdis_disprop_irrflxv.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_miscellaneous_302_Direct() {
        testPositiveEntailment("WebOnt_miscellaneous_302_Direct.premise.rdf", "WebOnt_miscellaneous_302_Direct.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_302_Direct() {
        testConsistency("WebOnt_miscellaneous_302_Direct.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_inst() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_inst.premise.rdf", "rdfbased_sem_eqdis_eqclass_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_inst() {
        testConsistency("rdfbased_sem_eqdis_eqclass_inst.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_miscellaneous_303() {
        testConsistency("WebOnt_miscellaneous_303.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_rflxv() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_rflxv.premise.rdf", "rdfbased_sem_eqdis_eqclass_rflxv.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_rflxv() {
        testConsistency("rdfbased_sem_eqdis_eqclass_rflxv.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_oneOf_001() {
        testConsistency("WebOnt_oneOf_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_subclass_1() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_subclass_1.premise.rdf", "rdfbased_sem_eqdis_eqclass_subclass_1.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_subclass_1() {
        testConsistency("rdfbased_sem_eqdis_eqclass_subclass_1.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_oneOf_004() {
        testPositiveEntailment("WebOnt_oneOf_004.premise.rdf", "WebOnt_oneOf_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_oneOf_004() {
        testConsistency("WebOnt_oneOf_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_subclass_2() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_subclass_2.premise.rdf", "rdfbased_sem_eqdis_eqclass_subclass_2.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_subclass_2() {
        testConsistency("rdfbased_sem_eqdis_eqclass_subclass_2.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_sameAs_001() {
        testPositiveEntailment("WebOnt_sameAs_001.premise.rdf", "WebOnt_sameAs_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_sameAs_001() {
        testConsistency("WebOnt_sameAs_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_subst() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_subst.premise.rdf", "rdfbased_sem_eqdis_eqclass_subst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_subst() {
        testConsistency("rdfbased_sem_eqdis_eqclass_subst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_someValuesFrom_003() {
        testPositiveEntailment("WebOnt_someValuesFrom_003.premise.rdf", "WebOnt_someValuesFrom_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_someValuesFrom_003() {
        testConsistency("WebOnt_someValuesFrom_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_sym() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_sym.premise.rdf", "rdfbased_sem_eqdis_eqclass_sym.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_sym() {
        testConsistency("rdfbased_sem_eqdis_eqclass_sym.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqclass_trans() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqclass_trans.premise.rdf", "rdfbased_sem_eqdis_eqclass_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqclass_trans() {
        testConsistency("rdfbased_sem_eqdis_eqclass_trans.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_unionOf_003() {
        testPositiveEntailment("WebOnt_unionOf_003.premise.rdf", "WebOnt_unionOf_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_unionOf_003() {
        testConsistency("WebOnt_unionOf_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_inst() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_inst.premise.rdf", "rdfbased_sem_eqdis_eqprop_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_inst() {
        testConsistency("rdfbased_sem_eqdis_eqprop_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_unionOf_004() {
        testPositiveEntailment("WebOnt_unionOf_004.premise.rdf", "WebOnt_unionOf_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_unionOf_004() {
        testConsistency("WebOnt_unionOf_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_rflxv() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_rflxv.premise.rdf", "rdfbased_sem_eqdis_eqprop_rflxv.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_rflxv() {
        testConsistency("rdfbased_sem_eqdis_eqprop_rflxv.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_subprop_1() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_subprop_1.premise.rdf", "rdfbased_sem_eqdis_eqprop_subprop_1.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_subprop_1() {
        testConsistency("rdfbased_sem_eqdis_eqprop_subprop_1.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_subprop_2() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_subprop_2.premise.rdf", "rdfbased_sem_eqdis_eqprop_subprop_2.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_subprop_2() {
        testConsistency("rdfbased_sem_eqdis_eqprop_subprop_2.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_subst() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_subst.premise.rdf", "rdfbased_sem_eqdis_eqprop_subst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_subst() {
        testConsistency("rdfbased_sem_eqdis_eqprop_subst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_sym() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_sym.premise.rdf", "rdfbased_sem_eqdis_eqprop_sym.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_sym() {
        testConsistency("rdfbased_sem_eqdis_eqprop_sym.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_eqprop_trans() {
        testPositiveEntailment("rdfbased_sem_eqdis_eqprop_trans.premise.rdf", "rdfbased_sem_eqdis_eqprop_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_eqprop_trans() {
        testConsistency("rdfbased_sem_eqdis_eqprop_trans.premise.rdf");
    }

    @Ignore(IGNORE_WONT_FIX)
    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_sameas_rflxv() {
        testPositiveEntailment("rdfbased_sem_eqdis_sameas_rflxv.premise.rdf", "rdfbased_sem_eqdis_sameas_rflxv.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_sameas_rflxv() {
        testConsistency("rdfbased_sem_eqdis_sameas_rflxv.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_sameas_subst() {
        testPositiveEntailment("rdfbased_sem_eqdis_sameas_subst.premise.rdf", "rdfbased_sem_eqdis_sameas_subst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_sameas_subst() {
        testConsistency("rdfbased_sem_eqdis_sameas_subst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_sameas_sym() {
        testPositiveEntailment("rdfbased_sem_eqdis_sameas_sym.premise.rdf", "rdfbased_sem_eqdis_sameas_sym.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_sameas_sym() {
        testConsistency("rdfbased_sem_eqdis_sameas_sym.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_eqdis_sameas_trans() {
        testPositiveEntailment("rdfbased_sem_eqdis_sameas_trans.premise.rdf", "rdfbased_sem_eqdis_sameas_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_eqdis_sameas_trans() {
        testConsistency("rdfbased_sem_eqdis_sameas_trans.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_inv_inst() {
        testPositiveEntailment("rdfbased_sem_inv_inst.premise.rdf", "rdfbased_sem_inv_inst.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_inv_inst() {
        testConsistency("rdfbased_sem_inv_inst.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_key_def() {
        testPositiveEntailment("rdfbased_sem_key_def.premise.rdf", "rdfbased_sem_key_def.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_key_def() {
        testConsistency("rdfbased_sem_key_def.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_ndis_alldifferent_fw() {
        testInconsistency("rdfbased_sem_ndis_alldifferent_fw.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_ndis_alldifferent_fw_distinctmembers() {
        testInconsistency("rdfbased_sem_ndis_alldifferent_fw_distinctmembers.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_ndis_alldisjointclasses_fw() {
        testInconsistency("rdfbased_sem_ndis_alldisjointclasses_fw.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_ndis_alldisjointproperties_fw() {
        testInconsistency("rdfbased_sem_ndis_alldisjointproperties_fw.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_npa_dat_fw() {
        testInconsistency("rdfbased_sem_npa_dat_fw.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_npa_ind_fw() {
        testInconsistency("rdfbased_sem_npa_ind_fw.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_backwardcompatiblewith_type_annot() {
        testPositiveEntailment("rdfbased_sem_prop_backwardcompatiblewith_type_annot.premise.rdf", "rdfbased_sem_prop_backwardcompatiblewith_type_annot.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_backwardcompatiblewith_type_annot() {
        testConsistency("rdfbased_sem_prop_backwardcompatiblewith_type_annot.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_comment_type() {
        testPositiveEntailment("rdfbased_sem_prop_comment_type.premise.rdf", "rdfbased_sem_prop_comment_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_comment_type() {
        testConsistency("rdfbased_sem_prop_comment_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_deprecated_type() {
        testPositiveEntailment("rdfbased_sem_prop_deprecated_type.premise.rdf", "rdfbased_sem_prop_deprecated_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_deprecated_type() {
        testConsistency("rdfbased_sem_prop_deprecated_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_incompatiblewith_type_annot() {
        testPositiveEntailment("rdfbased_sem_prop_incompatiblewith_type_annot.premise.rdf", "rdfbased_sem_prop_incompatiblewith_type_annot.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_incompatiblewith_type_annot() {
        testConsistency("rdfbased_sem_prop_incompatiblewith_type_annot.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_isdefinedby_type() {
        testPositiveEntailment("rdfbased_sem_prop_isdefinedby_type.premise.rdf", "rdfbased_sem_prop_isdefinedby_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_isdefinedby_type() {
        testConsistency("rdfbased_sem_prop_isdefinedby_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_label_type() {
        testPositiveEntailment("rdfbased_sem_prop_label_type.premise.rdf", "rdfbased_sem_prop_label_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_label_type() {
        testConsistency("rdfbased_sem_prop_label_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_priorversion_type_annot() {
        testPositiveEntailment("rdfbased_sem_prop_priorversion_type_annot.premise.rdf", "rdfbased_sem_prop_priorversion_type_annot.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_priorversion_type_annot() {
        testConsistency("rdfbased_sem_prop_priorversion_type_annot.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_seealso_type() {
        testPositiveEntailment("rdfbased_sem_prop_seealso_type.premise.rdf", "rdfbased_sem_prop_seealso_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_seealso_type() {
        testConsistency("rdfbased_sem_prop_seealso_type.premise.rdf");
    }

    @Ignore(IGNORE_EXTERNALLY_LOADED)
    @Test
    public void testPositiveEntailment_rdfbased_sem_prop_versioninfo_type() {
        testPositiveEntailment("rdfbased_sem_prop_versioninfo_type.premise.rdf", "rdfbased_sem_prop_versioninfo_type.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_prop_versioninfo_type() {
        testConsistency("rdfbased_sem_prop_versioninfo_type.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_domain_cond() {
        testPositiveEntailment("rdfbased_sem_rdfs_domain_cond.premise.rdf", "rdfbased_sem_rdfs_domain_cond.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_domain_cond() {
        testConsistency("rdfbased_sem_rdfs_domain_cond.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_range_cond() {
        testPositiveEntailment("rdfbased_sem_rdfs_range_cond.premise.rdf", "rdfbased_sem_rdfs_range_cond.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_range_cond() {
        testConsistency("rdfbased_sem_rdfs_range_cond.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_subclass_cond() {
        testPositiveEntailment("rdfbased_sem_rdfs_subclass_cond.premise.rdf", "rdfbased_sem_rdfs_subclass_cond.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_subclass_cond() {
        testConsistency("rdfbased_sem_rdfs_subclass_cond.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_subclass_trans() {
        testPositiveEntailment("rdfbased_sem_rdfs_subclass_trans.premise.rdf", "rdfbased_sem_rdfs_subclass_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_subclass_trans() {
        testConsistency("rdfbased_sem_rdfs_subclass_trans.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_subprop_cond() {
        testPositiveEntailment("rdfbased_sem_rdfs_subprop_cond.premise.rdf", "rdfbased_sem_rdfs_subprop_cond.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_subprop_cond() {
        testConsistency("rdfbased_sem_rdfs_subprop_cond.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfs_subprop_trans() {
        testPositiveEntailment("rdfbased_sem_rdfs_subprop_trans.premise.rdf", "rdfbased_sem_rdfs_subprop_trans.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfs_subprop_trans() {
        testConsistency("rdfbased_sem_rdfs_subprop_trans.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfsext_domain_subprop() {
        testPositiveEntailment("rdfbased_sem_rdfsext_domain_subprop.premise.rdf", "rdfbased_sem_rdfsext_domain_subprop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfsext_domain_subprop() {
        testConsistency("rdfbased_sem_rdfsext_domain_subprop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfsext_domain_superclass() {
        testPositiveEntailment("rdfbased_sem_rdfsext_domain_superclass.premise.rdf", "rdfbased_sem_rdfsext_domain_superclass.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfsext_domain_superclass() {
        testConsistency("rdfbased_sem_rdfsext_domain_superclass.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfsext_range_subprop() {
        testPositiveEntailment("rdfbased_sem_rdfsext_range_subprop.premise.rdf", "rdfbased_sem_rdfsext_range_subprop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfsext_range_subprop() {
        testConsistency("rdfbased_sem_rdfsext_range_subprop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_rdfsext_range_superclass() {
        testPositiveEntailment("rdfbased_sem_rdfsext_range_superclass.premise.rdf", "rdfbased_sem_rdfsext_range_superclass.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_rdfsext_range_superclass() {
        testConsistency("rdfbased_sem_rdfsext_range_superclass.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_allvalues_cmp_class() {
        testPositiveEntailment("rdfbased_sem_restrict_allvalues_cmp_class.premise.rdf", "rdfbased_sem_restrict_allvalues_cmp_class.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_allvalues_cmp_class() {
        testConsistency("rdfbased_sem_restrict_allvalues_cmp_class.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_allvalues_cmp_prop() {
        testPositiveEntailment("rdfbased_sem_restrict_allvalues_cmp_prop.premise.rdf", "rdfbased_sem_restrict_allvalues_cmp_prop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_allvalues_cmp_prop() {
        testConsistency("rdfbased_sem_restrict_allvalues_cmp_prop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_allvalues_inst_obj() {
        testPositiveEntailment("rdfbased_sem_restrict_allvalues_inst_obj.premise.rdf", "rdfbased_sem_restrict_allvalues_inst_obj.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_allvalues_inst_obj() {
        testConsistency("rdfbased_sem_restrict_allvalues_inst_obj.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_hasvalue_cmp_prop() {
        testPositiveEntailment("rdfbased_sem_restrict_hasvalue_cmp_prop.premise.rdf", "rdfbased_sem_restrict_hasvalue_cmp_prop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_hasvalue_cmp_prop() {
        testConsistency("rdfbased_sem_restrict_hasvalue_cmp_prop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_hasvalue_inst_obj() {
        testPositiveEntailment("rdfbased_sem_restrict_hasvalue_inst_obj.premise.rdf", "rdfbased_sem_restrict_hasvalue_inst_obj.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_hasvalue_inst_obj() {
        testConsistency("rdfbased_sem_restrict_hasvalue_inst_obj.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_hasvalue_inst_subj() {
        testPositiveEntailment("rdfbased_sem_restrict_hasvalue_inst_subj.premise.rdf", "rdfbased_sem_restrict_hasvalue_inst_subj.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_hasvalue_inst_subj() {
        testConsistency("rdfbased_sem_restrict_hasvalue_inst_subj.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_maxcard_inst_obj_one() {
        testPositiveEntailment("rdfbased_sem_restrict_maxcard_inst_obj_one.premise.rdf", "rdfbased_sem_restrict_maxcard_inst_obj_one.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_maxcard_inst_obj_one() {
        testConsistency("rdfbased_sem_restrict_maxcard_inst_obj_one.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_restrict_maxcard_inst_obj_zero() {
        testInconsistency("rdfbased_sem_restrict_maxcard_inst_obj_zero.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_maxqcr_inst_obj_one() {
        testPositiveEntailment("rdfbased_sem_restrict_maxqcr_inst_obj_one.premise.rdf", "rdfbased_sem_restrict_maxqcr_inst_obj_one.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_maxqcr_inst_obj_one() {
        testConsistency("rdfbased_sem_restrict_maxqcr_inst_obj_one.premise.rdf");
    }

    @Test
    public void testInconsistency_rdfbased_sem_restrict_maxqcr_inst_obj_zero() {
        testInconsistency("rdfbased_sem_restrict_maxqcr_inst_obj_zero.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_somevalues_cmp_class() {
        testPositiveEntailment("rdfbased_sem_restrict_somevalues_cmp_class.premise.rdf", "rdfbased_sem_restrict_somevalues_cmp_class.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_somevalues_cmp_class() {
        testConsistency("rdfbased_sem_restrict_somevalues_cmp_class.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_somevalues_cmp_prop() {
        testPositiveEntailment("rdfbased_sem_restrict_somevalues_cmp_prop.premise.rdf", "rdfbased_sem_restrict_somevalues_cmp_prop.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_somevalues_cmp_prop() {
        testConsistency("rdfbased_sem_restrict_somevalues_cmp_prop.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_rdfbased_sem_restrict_somevalues_inst_subj() {
        testPositiveEntailment("rdfbased_sem_restrict_somevalues_inst_subj.premise.rdf", "rdfbased_sem_restrict_somevalues_inst_subj.conclusion.rdf");
    }

    @Test
    public void testConsistency_rdfbased_sem_restrict_somevalues_inst_subj() {
        testConsistency("rdfbased_sem_restrict_somevalues_inst_subj.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_somevaluesfrom2bnode() {
        testPositiveEntailment("somevaluesfrom2bnode.premise.rdf", "somevaluesfrom2bnode.conclusion.rdf");
    }

    @Test
    public void testConsistency_somevaluesfrom2bnode() {
        testConsistency("somevaluesfrom2bnode.premise.rdf");
    }

    @Test
    public void testInconsistency_string_integer_clash() {
        testInconsistency("string_integer_clash.premise.fs");
    }

    @Test
    public void testPositiveEntailment_WebOnt_AnnotationProperty_002() {
        testPositiveEntailment("WebOnt_AnnotationProperty_002.premise.rdf", "WebOnt_AnnotationProperty_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_AnnotationProperty_002() {
        testConsistency("WebOnt_AnnotationProperty_002.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_AnnotationProperty_003() {
        testConsistency("WebOnt_AnnotationProperty_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_AnnotationProperty_004() {
        testConsistency("WebOnt_AnnotationProperty_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I4_5_001() {
        testPositiveEntailment("WebOnt_I4_5_001.premise.rdf", "WebOnt_I4_5_001.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I4_5_001() {
        testConsistency("WebOnt_I4_5_001.premise.rdf");
    }

    @Test
    public void testInconsistency_WebOnt_I4_5_002() {
        testInconsistency("WebOnt_I4_5_002.premise.rdf");
    }

    @Test
    public void testNegativeEntailment_WebOnt_I4_6_004() {
        testNegativeEntailment("WebOnt_I4_6_004.premise.rdf", "WebOnt_I4_6_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I4_6_004() {
        testConsistency("WebOnt_I4_6_004.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I4_6_005_Direct() {
        testPositiveEntailment("WebOnt_I4_6_005_Direct.premise.rdf", "WebOnt_I4_6_005_Direct.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I4_6_005_Direct() {
        testConsistency("WebOnt_I4_6_005_Direct.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_001() {
        testConsistency("WebOnt_I5_2_001.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_2_002() {
        testPositiveEntailment("WebOnt_I5_2_002.premise.rdf", "WebOnt_I5_2_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_002() {
        testConsistency("WebOnt_I5_2_002.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_003() {
        testConsistency("WebOnt_I5_2_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_2_004() {
        testPositiveEntailment("WebOnt_I5_2_004.premise.rdf", "WebOnt_I5_2_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_004() {
        testConsistency("WebOnt_I5_2_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_005() {
        testConsistency("WebOnt_I5_2_005.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_2_006() {
        testPositiveEntailment("WebOnt_I5_2_006.premise.rdf", "WebOnt_I5_2_006.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_2_006() {
        testConsistency("WebOnt_I5_2_006.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_21_002() {
        testPositiveEntailment("WebOnt_I5_21_002.premise.rdf", "WebOnt_I5_21_002.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_21_002() {
        testConsistency("WebOnt_I5_21_002.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_24_003() {
        testPositiveEntailment("WebOnt_I5_24_003.premise.rdf", "WebOnt_I5_24_003.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_24_003() {
        testConsistency("WebOnt_I5_24_003.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_24_004() {
        testPositiveEntailment("WebOnt_I5_24_004.premise.rdf", "WebOnt_I5_24_004.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_24_004() {
        testConsistency("WebOnt_I5_24_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_001() {
        testConsistency("WebOnt_I5_26_001.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_002() {
        testConsistency("WebOnt_I5_26_002.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_003() {
        testConsistency("WebOnt_I5_26_003.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_004() {
        testConsistency("WebOnt_I5_26_004.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_005() {
        testConsistency("WebOnt_I5_26_005.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_26_009() {
        testPositiveEntailment("WebOnt_I5_26_009.premise.rdf", "WebOnt_I5_26_009.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_009() {
        testConsistency("WebOnt_I5_26_009.premise.rdf");
    }

    @Test
    public void testPositiveEntailment_WebOnt_I5_26_010() {
        testPositiveEntailment("WebOnt_I5_26_010.premise.rdf", "WebOnt_I5_26_010.conclusion.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_26_010() {
        testConsistency("WebOnt_I5_26_010.premise.rdf");
    }

    @Test
    public void testConsistency_WebOnt_I5_3_006() {
        testConsistency("WebOnt_I5_3_006.premise.rdf");
    }
}
