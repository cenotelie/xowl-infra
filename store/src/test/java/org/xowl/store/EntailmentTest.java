/**********************************************************************
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
 **********************************************************************/
package org.xowl.store;

import org.junit.Test;

/**
 * Entailment tests for the built-in reasoning rules
 *
 * @author Laurent Wouters
 */
public class EntailmentTest extends BaseEntailmentTest {
    @Test
    public void test_WebOnt_I5_5_005() {
        testPositiveEntailment("WebOnt-I5.5-005.premise.rdf", "WebOnt-I5.5-005.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_8_006() {
        testPositiveEntailment("WebOnt-I5.8-006.premise.rdf", "WebOnt-I5.8-006.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_8_008() {
        testPositiveEntailment("WebOnt-I5.8-008.premise.rdf", "WebOnt-I5.8-008.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_8_009() {
        testPositiveEntailment("WebOnt-I5.8-009.premise.rdf", "WebOnt-I5.8-009.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_8_010() {
        testPositiveEntailment("WebOnt-I5.8-010.premise.rdf", "WebOnt-I5.8-010.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_8_011() {
        testPositiveEntailment("WebOnt-I5.8-011.premise.rdf", "WebOnt-I5.8-011.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_InverseFunctionalProperty_001() {
        testPositiveEntailment("WebOnt-InverseFunctionalProperty-001.premise.rdf", "WebOnt-InverseFunctionalProperty-001.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_Ontology_001() {
        testPositiveEntailment("WebOnt-Ontology-001.premise.rdf", "WebOnt-Ontology-001.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_SymmetricProperty_002() {
        testPositiveEntailment("WebOnt-SymmetricProperty-002.premise.rdf", "WebOnt-SymmetricProperty-002.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_SymmetricProperty_003() {
        testPositiveEntailment("WebOnt-SymmetricProperty-003.premise.rdf", "WebOnt-SymmetricProperty-003.conclusion.rdf");
    }

    @Test
    public void test_bnode2somevaluesfrom() {
        testPositiveEntailment("bnode2somevaluesfrom.premise.rdf", "bnode2somevaluesfrom.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_TransitiveProperty_002() {
        testPositiveEntailment("WebOnt-TransitiveProperty-002.premise.rdf", "WebOnt-TransitiveProperty-002.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_allValuesFrom_001() {
        testPositiveEntailment("WebOnt-allValuesFrom-001.premise.rdf", "WebOnt-allValuesFrom-001.conclusion.rdf");
    }

    @Test
    public void test_chain2trans1() {
        testPositiveEntailment("chain2trans1.premise.rdf", "chain2trans1.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_cardinality_001() {
        testPositiveEntailment("WebOnt-cardinality-001.premise.rdf", "WebOnt-cardinality-001.conclusion.rdf");
    }

    @Test
    public void test_Consistent_but_all_unsat() {
        testPositiveEntailment("Consistent-but-all-unsat.premise.rdf", "Consistent-but-all-unsat.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_cardinality_002() {
        testPositiveEntailment("WebOnt-cardinality-002.premise.rdf", "WebOnt-cardinality-002.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_cardinality_003() {
        testPositiveEntailment("WebOnt-cardinality-003.premise.rdf", "WebOnt-cardinality-003.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_cardinality_004() {
        testPositiveEntailment("WebOnt-cardinality-004.premise.rdf", "WebOnt-cardinality-004.conclusion.rdf");
    }

    @Test
    public void test_DisjointClasses_001() {
        testPositiveEntailment("DisjointClasses-001.premise.fs", "DisjointClasses-001.conclusion.fs");
    }

    @Test
    public void test_DisjointClasses_003() {
        testPositiveEntailment("DisjointClasses-003.premise.fs", "DisjointClasses-003.conclusion.fs");
    }

    @Test
    public void test_New_Feature_DataQCR_001() {
        testPositiveEntailment("New-Feature-DataQCR-001.premise.fs", "New-Feature-DataQCR-001.conclusion.fs");
    }

    @Test
    public void test_New_Feature_DisjointDataProperties_002() {
        testPositiveEntailment("New-Feature-DisjointDataProperties-002.premise.fs", "New-Feature-DisjointDataProperties-002.conclusion.fs");
    }

    @Test
    public void test_New_Feature_DisjointObjectProperties_001() {
        testPositiveEntailment("New-Feature-DisjointObjectProperties-001.premise.fs", "New-Feature-DisjointObjectProperties-001.conclusion.fs");
    }

    @Test
    public void test_New_Feature_DisjointObjectProperties_002() {
        testPositiveEntailment("New-Feature-DisjointObjectProperties-002.premise.fs", "New-Feature-DisjointObjectProperties-002.conclusion.fs");
    }

    @Test
    public void test_New_Feature_DisjointUnion_001() {
        testPositiveEntailment("New-Feature-DisjointUnion-001.premise.fs", "New-Feature-DisjointUnion-001.conclusion.fs");
    }

    @Test
    public void test_New_Feature_Keys_001() {
        testPositiveEntailment("New-Feature-Keys-001.premise.fs", "New-Feature-Keys-001.conclusion.fs");
    }

    @Test
    public void test_WebOnt_description_logic_201() {
        testPositiveEntailment("WebOnt-description-logic-201.premise.rdf", "WebOnt-description-logic-201.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_description_logic_205() {
        testPositiveEntailment("WebOnt-description-logic-205.premise.rdf", "WebOnt-description-logic-205.conclusion.rdf");
    }

    @Test
    public void test_New_Feature_Keys_003() {
        testPositiveEntailment("New-Feature-Keys-003.premise.fs", "New-Feature-Keys-003.conclusion.fs");
    }

    @Test
    public void test_WebOnt_description_logic_207() {
        testPositiveEntailment("WebOnt-description-logic-207.premise.rdf", "WebOnt-description-logic-207.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_description_logic_208() {
        testPositiveEntailment("WebOnt-description-logic-208.premise.rdf", "WebOnt-description-logic-208.conclusion.rdf");
    }

    @Test
    public void test_New_Feature_ObjectPropertyChain_001() {
        testPositiveEntailment("New-Feature-ObjectPropertyChain-001.premise.fs", "New-Feature-ObjectPropertyChain-001.conclusion.fs");
    }

    @Test
    public void test_New_Feature_ObjectPropertyChain_BJP_003() {
        testPositiveEntailment("New-Feature-ObjectPropertyChain-BJP-003.premise.rdf", "New-Feature-ObjectPropertyChain-BJP-003.conclusion.rdf");
    }

    @Test
    public void test_New_Feature_ObjectQCR_001() {
        testPositiveEntailment("New-Feature-ObjectQCR-001.premise.fs", "New-Feature-ObjectQCR-001.conclusion.fs");
    }

    @Test
    public void test_New_Feature_ObjectQCR_002() {
        testPositiveEntailment("New-Feature-ObjectQCR-002.premise.fs", "New-Feature-ObjectQCR-002.conclusion.fs");
    }

    @Test
    public void test_New_Feature_ReflexiveProperty_001() {
        testPositiveEntailment("New-Feature-ReflexiveProperty-001.premise.fs", "New-Feature-ReflexiveProperty-001.conclusion.fs");
    }

    @Test
    public void test_New_Feature_SelfRestriction_001() {
        testPositiveEntailment("New-Feature-SelfRestriction-001.premise.fs", "New-Feature-SelfRestriction-001.conclusion.fs");
    }

    @Test
    public void test_New_Feature_SelfRestriction_002() {
        testPositiveEntailment("New-Feature-SelfRestriction-002.premise.fs", "New-Feature-SelfRestriction-002.conclusion.fs");
    }

    @Test
    public void test_WebOnt_description_logic_661() {
        testPositiveEntailment("WebOnt-description-logic-661.premise.rdf", "WebOnt-description-logic-661.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_description_logic_665() {
        testPositiveEntailment("WebOnt-description-logic-665.premise.rdf", "WebOnt-description-logic-665.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_description_logic_667() {
        testPositiveEntailment("WebOnt-description-logic-667.premise.rdf", "WebOnt-description-logic-667.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_description_logic_901() {
        testPositiveEntailment("WebOnt-description-logic-901.premise.rdf", "WebOnt-description-logic-901.conclusion.rdf");
    }

    @Test
    public void test_owl2_rl_rules_fp_differentFrom() {
        testPositiveEntailment("owl2-rl-rules-fp-differentFrom.premise.rdf", "owl2-rl-rules-fp-differentFrom.conclusion.rdf");
    }

    @Test
    public void test_owl2_rl_rules_ifp_differentFrom() {
        testPositiveEntailment("owl2-rl-rules-ifp-differentFrom.premise.rdf", "owl2-rl-rules-ifp-differentFrom.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_differentFrom_001() {
        testPositiveEntailment("WebOnt-differentFrom-001.premise.rdf", "WebOnt-differentFrom-001.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_disjointWith_001() {
        testPositiveEntailment("WebOnt-disjointWith-001.premise.rdf", "WebOnt-disjointWith-001.conclusion.rdf");
    }

    @Test
    public void test_Qualified_cardinality_boolean() {
        testPositiveEntailment("Qualified-cardinality-boolean.premise.fs", "Qualified-cardinality-boolean.conclusion.fs");
    }

    @Test
    public void test_rdfbased_sem_bool_intersection_inst_comp() {
        testPositiveEntailment("rdfbased-sem-bool-intersection-inst-comp.premise.rdf", "rdfbased-sem-bool-intersection-inst-comp.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_bool_intersection_inst_expr() {
        testPositiveEntailment("rdfbased-sem-bool-intersection-inst-expr.premise.rdf", "rdfbased-sem-bool-intersection-inst-expr.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_bool_intersection_term() {
        testPositiveEntailment("rdfbased-sem-bool-intersection-term.premise.rdf", "rdfbased-sem-bool-intersection-term.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentClass_001() {
        testPositiveEntailment("WebOnt-equivalentClass-001.premise.rdf", "WebOnt-equivalentClass-001.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_bool_union_inst_comp() {
        testPositiveEntailment("rdfbased-sem-bool-union-inst-comp.premise.rdf", "rdfbased-sem-bool-union-inst-comp.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentClass_002() {
        testPositiveEntailment("WebOnt-equivalentClass-002.premise.rdf", "WebOnt-equivalentClass-002.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_bool_union_term() {
        testPositiveEntailment("rdfbased-sem-bool-union-term.premise.rdf", "rdfbased-sem-bool-union-term.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentClass_003() {
        testPositiveEntailment("WebOnt-equivalentClass-003.premise.rdf", "WebOnt-equivalentClass-003.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_chain_def() {
        testPositiveEntailment("rdfbased-sem-chain-def.premise.rdf", "rdfbased-sem-chain-def.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentClass_004() {
        testPositiveEntailment("WebOnt-equivalentClass-004.premise.rdf", "WebOnt-equivalentClass-004.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentClass_006() {
        testPositiveEntailment("WebOnt-equivalentClass-006.premise.rdf", "WebOnt-equivalentClass-006.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_char_functional_inst() {
        testPositiveEntailment("rdfbased-sem-char-functional-inst.premise.rdf", "rdfbased-sem-char-functional-inst.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentClass_008_Direct() {
        testPositiveEntailment("WebOnt-equivalentClass-008-Direct.premise.rdf", "WebOnt-equivalentClass-008-Direct.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_char_inversefunc_data() {
        testPositiveEntailment("rdfbased-sem-char-inversefunc-data.premise.rdf", "rdfbased-sem-char-inversefunc-data.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_char_inversefunc_inst() {
        testPositiveEntailment("rdfbased-sem-char-inversefunc-inst.premise.rdf", "rdfbased-sem-char-inversefunc-inst.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentProperty_001() {
        testPositiveEntailment("WebOnt-equivalentProperty-001.premise.rdf", "WebOnt-equivalentProperty-001.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_char_symmetric_inst() {
        testPositiveEntailment("rdfbased-sem-char-symmetric-inst.premise.rdf", "rdfbased-sem-char-symmetric-inst.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentProperty_002() {
        testPositiveEntailment("WebOnt-equivalentProperty-002.premise.rdf", "WebOnt-equivalentProperty-002.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_char_transitive_inst() {
        testPositiveEntailment("rdfbased-sem-char-transitive-inst.premise.rdf", "rdfbased-sem-char-transitive-inst.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentProperty_003() {
        testPositiveEntailment("WebOnt-equivalentProperty-003.premise.rdf", "WebOnt-equivalentProperty-003.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_equivalentProperty_004() {
        testPositiveEntailment("WebOnt-equivalentProperty-004.premise.rdf", "WebOnt-equivalentProperty-004.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_class_nothing_term() {
        testPositiveEntailment("rdfbased-sem-class-nothing-term.premise.rdf", "rdfbased-sem-class-nothing-term.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_imports_011() {
        testPositiveEntailment("WebOnt-imports-011.premise.rdf", "WebOnt-imports-011.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_class_nothing_type() {
        testPositiveEntailment("rdfbased-sem-class-nothing-type.premise.rdf", "rdfbased-sem-class-nothing-type.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_class_thing_term() {
        testPositiveEntailment("rdfbased-sem-class-thing-term.premise.rdf", "rdfbased-sem-class-thing-term.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_class_thing_type() {
        testPositiveEntailment("rdfbased-sem-class-thing-type.premise.rdf", "rdfbased-sem-class-thing-type.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_enum_inst_included() {
        testPositiveEntailment("rdfbased-sem-enum-inst-included.premise.rdf", "rdfbased-sem-enum-inst-included.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_miscellaneous_302_Direct() {
        testPositiveEntailment("WebOnt-miscellaneous-302-Direct.premise.rdf", "WebOnt-miscellaneous-302-Direct.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqclass_inst() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqclass-inst.premise.rdf", "rdfbased-sem-eqdis-eqclass-inst.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqclass_rflxv() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqclass-rflxv.premise.rdf", "rdfbased-sem-eqdis-eqclass-rflxv.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqclass_subclass_1() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqclass-subclass-1.premise.rdf", "rdfbased-sem-eqdis-eqclass-subclass-1.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_oneOf_004() {
        testPositiveEntailment("WebOnt-oneOf-004.premise.rdf", "WebOnt-oneOf-004.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqclass_subclass_2() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqclass-subclass-2.premise.rdf", "rdfbased-sem-eqdis-eqclass-subclass-2.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_sameAs_001() {
        testPositiveEntailment("WebOnt-sameAs-001.premise.rdf", "WebOnt-sameAs-001.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqclass_subst() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqclass-subst.premise.rdf", "rdfbased-sem-eqdis-eqclass-subst.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_someValuesFrom_003() {
        testPositiveEntailment("WebOnt-someValuesFrom-003.premise.rdf", "WebOnt-someValuesFrom-003.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqclass_sym() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqclass-sym.premise.rdf", "rdfbased-sem-eqdis-eqclass-sym.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqclass_trans() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqclass-trans.premise.rdf", "rdfbased-sem-eqdis-eqclass-trans.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_unionOf_003() {
        testPositiveEntailment("WebOnt-unionOf-003.premise.rdf", "WebOnt-unionOf-003.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqprop_inst() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqprop-inst.premise.rdf", "rdfbased-sem-eqdis-eqprop-inst.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_unionOf_004() {
        testPositiveEntailment("WebOnt-unionOf-004.premise.rdf", "WebOnt-unionOf-004.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqprop_rflxv() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqprop-rflxv.premise.rdf", "rdfbased-sem-eqdis-eqprop-rflxv.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqprop_subprop_1() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqprop-subprop-1.premise.rdf", "rdfbased-sem-eqdis-eqprop-subprop-1.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqprop_subprop_2() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqprop-subprop-2.premise.rdf", "rdfbased-sem-eqdis-eqprop-subprop-2.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqprop_subst() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqprop-subst.premise.rdf", "rdfbased-sem-eqdis-eqprop-subst.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqprop_sym() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqprop-sym.premise.rdf", "rdfbased-sem-eqdis-eqprop-sym.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_eqprop_trans() {
        testPositiveEntailment("rdfbased-sem-eqdis-eqprop-trans.premise.rdf", "rdfbased-sem-eqdis-eqprop-trans.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_sameas_rflxv() {
        testPositiveEntailment("rdfbased-sem-eqdis-sameas-rflxv.premise.rdf", "rdfbased-sem-eqdis-sameas-rflxv.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_sameas_subst() {
        testPositiveEntailment("rdfbased-sem-eqdis-sameas-subst.premise.rdf", "rdfbased-sem-eqdis-sameas-subst.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_sameas_sym() {
        testPositiveEntailment("rdfbased-sem-eqdis-sameas-sym.premise.rdf", "rdfbased-sem-eqdis-sameas-sym.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_eqdis_sameas_trans() {
        testPositiveEntailment("rdfbased-sem-eqdis-sameas-trans.premise.rdf", "rdfbased-sem-eqdis-sameas-trans.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_inv_inst() {
        testPositiveEntailment("rdfbased-sem-inv-inst.premise.rdf", "rdfbased-sem-inv-inst.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_key_def() {
        testPositiveEntailment("rdfbased-sem-key-def.premise.rdf", "rdfbased-sem-key-def.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_backwardcompatiblewith_type_annot() {
        testPositiveEntailment("rdfbased-sem-prop-backwardcompatiblewith-type-annot.premise.rdf", "rdfbased-sem-prop-backwardcompatiblewith-type-annot.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_comment_type() {
        testPositiveEntailment("rdfbased-sem-prop-comment-type.premise.rdf", "rdfbased-sem-prop-comment-type.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_deprecated_type() {
        testPositiveEntailment("rdfbased-sem-prop-deprecated-type.premise.rdf", "rdfbased-sem-prop-deprecated-type.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_incompatiblewith_type_annot() {
        testPositiveEntailment("rdfbased-sem-prop-incompatiblewith-type-annot.premise.rdf", "rdfbased-sem-prop-incompatiblewith-type-annot.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_isdefinedby_type() {
        testPositiveEntailment("rdfbased-sem-prop-isdefinedby-type.premise.rdf", "rdfbased-sem-prop-isdefinedby-type.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_label_type() {
        testPositiveEntailment("rdfbased-sem-prop-label-type.premise.rdf", "rdfbased-sem-prop-label-type.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_priorversion_type_annot() {
        testPositiveEntailment("rdfbased-sem-prop-priorversion-type-annot.premise.rdf", "rdfbased-sem-prop-priorversion-type-annot.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_seealso_type() {
        testPositiveEntailment("rdfbased-sem-prop-seealso-type.premise.rdf", "rdfbased-sem-prop-seealso-type.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_prop_versioninfo_type() {
        testPositiveEntailment("rdfbased-sem-prop-versioninfo-type.premise.rdf", "rdfbased-sem-prop-versioninfo-type.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfs_domain_cond() {
        testPositiveEntailment("rdfbased-sem-rdfs-domain-cond.premise.rdf", "rdfbased-sem-rdfs-domain-cond.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfs_range_cond() {
        testPositiveEntailment("rdfbased-sem-rdfs-range-cond.premise.rdf", "rdfbased-sem-rdfs-range-cond.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfs_subclass_cond() {
        testPositiveEntailment("rdfbased-sem-rdfs-subclass-cond.premise.rdf", "rdfbased-sem-rdfs-subclass-cond.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfs_subclass_trans() {
        testPositiveEntailment("rdfbased-sem-rdfs-subclass-trans.premise.rdf", "rdfbased-sem-rdfs-subclass-trans.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfs_subprop_cond() {
        testPositiveEntailment("rdfbased-sem-rdfs-subprop-cond.premise.rdf", "rdfbased-sem-rdfs-subprop-cond.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfs_subprop_trans() {
        testPositiveEntailment("rdfbased-sem-rdfs-subprop-trans.premise.rdf", "rdfbased-sem-rdfs-subprop-trans.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfsext_domain_subprop() {
        testPositiveEntailment("rdfbased-sem-rdfsext-domain-subprop.premise.rdf", "rdfbased-sem-rdfsext-domain-subprop.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfsext_domain_superclass() {
        testPositiveEntailment("rdfbased-sem-rdfsext-domain-superclass.premise.rdf", "rdfbased-sem-rdfsext-domain-superclass.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfsext_range_subprop() {
        testPositiveEntailment("rdfbased-sem-rdfsext-range-subprop.premise.rdf", "rdfbased-sem-rdfsext-range-subprop.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_rdfsext_range_superclass() {
        testPositiveEntailment("rdfbased-sem-rdfsext-range-superclass.premise.rdf", "rdfbased-sem-rdfsext-range-superclass.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_allvalues_cmp_class() {
        testPositiveEntailment("rdfbased-sem-restrict-allvalues-cmp-class.premise.rdf", "rdfbased-sem-restrict-allvalues-cmp-class.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_allvalues_cmp_prop() {
        testPositiveEntailment("rdfbased-sem-restrict-allvalues-cmp-prop.premise.rdf", "rdfbased-sem-restrict-allvalues-cmp-prop.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_allvalues_inst_obj() {
        testPositiveEntailment("rdfbased-sem-restrict-allvalues-inst-obj.premise.rdf", "rdfbased-sem-restrict-allvalues-inst-obj.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_hasvalue_cmp_prop() {
        testPositiveEntailment("rdfbased-sem-restrict-hasvalue-cmp-prop.premise.rdf", "rdfbased-sem-restrict-hasvalue-cmp-prop.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_hasvalue_inst_obj() {
        testPositiveEntailment("rdfbased-sem-restrict-hasvalue-inst-obj.premise.rdf", "rdfbased-sem-restrict-hasvalue-inst-obj.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_hasvalue_inst_subj() {
        testPositiveEntailment("rdfbased-sem-restrict-hasvalue-inst-subj.premise.rdf", "rdfbased-sem-restrict-hasvalue-inst-subj.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_maxcard_inst_obj_one() {
        testPositiveEntailment("rdfbased-sem-restrict-maxcard-inst-obj-one.premise.rdf", "rdfbased-sem-restrict-maxcard-inst-obj-one.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_maxqcr_inst_obj_one() {
        testPositiveEntailment("rdfbased-sem-restrict-maxqcr-inst-obj-one.premise.rdf", "rdfbased-sem-restrict-maxqcr-inst-obj-one.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_somevalues_cmp_class() {
        testPositiveEntailment("rdfbased-sem-restrict-somevalues-cmp-class.premise.rdf", "rdfbased-sem-restrict-somevalues-cmp-class.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_somevalues_cmp_prop() {
        testPositiveEntailment("rdfbased-sem-restrict-somevalues-cmp-prop.premise.rdf", "rdfbased-sem-restrict-somevalues-cmp-prop.conclusion.rdf");
    }

    @Test
    public void test_rdfbased_sem_restrict_somevalues_inst_subj() {
        testPositiveEntailment("rdfbased-sem-restrict-somevalues-inst-subj.premise.rdf", "rdfbased-sem-restrict-somevalues-inst-subj.conclusion.rdf");
    }

    @Test
    public void test_somevaluesfrom2bnode() {
        testPositiveEntailment("somevaluesfrom2bnode.premise.rdf", "somevaluesfrom2bnode.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_AnnotationProperty_002() {
        testPositiveEntailment("WebOnt-AnnotationProperty-002.premise.rdf", "WebOnt-AnnotationProperty-002.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I4_5_001() {
        testPositiveEntailment("WebOnt-I4.5-001.premise.rdf", "WebOnt-I4.5-001.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I4_6_005_Direct() {
        testPositiveEntailment("WebOnt-I4.6-005-Direct.premise.rdf", "WebOnt-I4.6-005-Direct.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_2_002() {
        testPositiveEntailment("WebOnt-I5.2-002.premise.rdf", "WebOnt-I5.2-002.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_2_004() {
        testPositiveEntailment("WebOnt-I5.2-004.premise.rdf", "WebOnt-I5.2-004.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_2_006() {
        testPositiveEntailment("WebOnt-I5.2-006.premise.rdf", "WebOnt-I5.2-006.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_21_002() {
        testPositiveEntailment("WebOnt-I5.21-002.premise.rdf", "WebOnt-I5.21-002.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_24_003() {
        testPositiveEntailment("WebOnt-I5.24-003.premise.rdf", "WebOnt-I5.24-003.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_24_004() {
        testPositiveEntailment("WebOnt-I5.24-004.premise.rdf", "WebOnt-I5.24-004.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_26_009() {
        testPositiveEntailment("WebOnt-I5.26-009.premise.rdf", "WebOnt-I5.26-009.conclusion.rdf");
    }

    @Test
    public void test_WebOnt_I5_26_010() {
        testPositiveEntailment("WebOnt-I5.26-010.premise.rdf", "WebOnt-I5.26-010.conclusion.rdf");
    }
}
