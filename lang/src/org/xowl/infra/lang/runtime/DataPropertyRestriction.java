/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.infra.lang.runtime;

public interface DataPropertyRestriction extends org.xowl.infra.lang.runtime.ClassRestriction {
    // <editor-fold defaultstate="collapsed" desc="Property dataProperty">
    public static interface dataProperty {
        boolean check_contains(org.xowl.infra.lang.runtime.DataProperty elem);
        boolean user_check_add(org.xowl.infra.lang.runtime.DataProperty elem);
        boolean user_check_remove(org.xowl.infra.lang.runtime.DataProperty elem);
        boolean user_check_replace(org.xowl.infra.lang.runtime.DataProperty oldElem, org.xowl.infra.lang.runtime.DataProperty  newElem);
        void user_add(org.xowl.infra.lang.runtime.DataProperty elem);
        void user_remove(org.xowl.infra.lang.runtime.DataProperty elem);
        boolean inverse_check_add(org.xowl.infra.lang.runtime.DataProperty elem);
        boolean inverse_check_remove(org.xowl.infra.lang.runtime.DataProperty elem);
        boolean inverse_check_replace(org.xowl.infra.lang.runtime.DataProperty oldElem, org.xowl.infra.lang.runtime.DataProperty  newElem);
        void inverse_add(org.xowl.infra.lang.runtime.DataProperty elem);
        void inverse_remove(org.xowl.infra.lang.runtime.DataProperty elem);
    }
    dataProperty __getImplOfdataProperty();
    boolean setDataProperty(org.xowl.infra.lang.runtime.DataProperty elem);
    org.xowl.infra.lang.runtime.DataProperty getDataProperty();
    // </editor-fold>
}
