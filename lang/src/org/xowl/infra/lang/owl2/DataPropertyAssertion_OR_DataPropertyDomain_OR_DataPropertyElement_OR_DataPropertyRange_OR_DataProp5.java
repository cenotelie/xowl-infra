/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.infra.lang.owl2;

public interface DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp5 {
    // <editor-fold defaultstate="collapsed" desc="Property dataProperty">
    public static interface dataProperty {
        boolean check_contains(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
        boolean user_check_add(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
        boolean user_check_remove(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
        boolean user_check_replace(org.xowl.infra.lang.owl2.DataPropertyExpression oldElem, org.xowl.infra.lang.owl2.DataPropertyExpression  newElem);
        void user_add(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
        void user_remove(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
        boolean inverse_check_add(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
        boolean inverse_check_remove(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
        boolean inverse_check_replace(org.xowl.infra.lang.owl2.DataPropertyExpression oldElem, org.xowl.infra.lang.owl2.DataPropertyExpression  newElem);
        void inverse_add(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
        void inverse_remove(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
    }
    dataProperty __getImplOfdataProperty();
    boolean setDataProperty(org.xowl.infra.lang.owl2.DataPropertyExpression elem);
    org.xowl.infra.lang.owl2.DataPropertyExpression getDataProperty();
    // </editor-fold>
}
