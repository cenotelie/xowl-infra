/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.interop;

public interface JavaInvokation_OR_ObjectField {
    // <editor-fold defaultstate="collapsed" desc="Property on">
    public static interface on {
        boolean check_contains(org.xowl.lang.interop.JavaObjectExpression elem);
        boolean user_check_add(org.xowl.lang.interop.JavaObjectExpression elem);
        boolean user_check_remove(org.xowl.lang.interop.JavaObjectExpression elem);
        boolean user_check_replace(org.xowl.lang.interop.JavaObjectExpression oldElem, org.xowl.lang.interop.JavaObjectExpression  newElem);
        void user_add(org.xowl.lang.interop.JavaObjectExpression elem);
        void user_remove(org.xowl.lang.interop.JavaObjectExpression elem);
        boolean inverse_check_add(org.xowl.lang.interop.JavaObjectExpression elem);
        boolean inverse_check_remove(org.xowl.lang.interop.JavaObjectExpression elem);
        boolean inverse_check_replace(org.xowl.lang.interop.JavaObjectExpression oldElem, org.xowl.lang.interop.JavaObjectExpression  newElem);
        void inverse_add(org.xowl.lang.interop.JavaObjectExpression elem);
        void inverse_remove(org.xowl.lang.interop.JavaObjectExpression elem);
    }
    on __getImplOfon();
    boolean setOn(org.xowl.lang.interop.JavaObjectExpression elem);
    org.xowl.lang.interop.JavaObjectExpression getOn();
    // </editor-fold>
}
