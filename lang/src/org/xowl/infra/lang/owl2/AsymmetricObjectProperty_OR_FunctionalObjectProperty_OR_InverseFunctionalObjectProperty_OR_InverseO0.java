/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.infra.lang.owl2;

public interface AsymmetricObjectProperty_OR_FunctionalObjectProperty_OR_InverseFunctionalObjectProperty_OR_InverseO0 {
    // <editor-fold defaultstate="collapsed" desc="Property objectProperty">
    public static interface objectProperty {
        boolean check_contains(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
        boolean user_check_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
        boolean user_check_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
        boolean user_check_replace(org.xowl.infra.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.infra.lang.owl2.ObjectPropertyExpression  newElem);
        void user_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
        void user_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
        boolean inverse_check_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
        boolean inverse_check_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
        boolean inverse_check_replace(org.xowl.infra.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.infra.lang.owl2.ObjectPropertyExpression  newElem);
        void inverse_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
        void inverse_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
    }
    objectProperty __getImplOfobjectProperty();
    boolean setObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem);
    org.xowl.infra.lang.owl2.ObjectPropertyExpression getObjectProperty();
    // </editor-fold>
}