/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.runtime;

public interface DataCardinalityRestriction_OR_NAryDataPropertyRestriction {
    // <editor-fold defaultstate="collapsed" desc="Property datatype">
    public static interface datatype {
        boolean check_contains(org.xowl.lang.runtime.Datatype elem);
        boolean user_check_add(org.xowl.lang.runtime.Datatype elem);
        boolean user_check_remove(org.xowl.lang.runtime.Datatype elem);
        boolean user_check_replace(org.xowl.lang.runtime.Datatype oldElem, org.xowl.lang.runtime.Datatype  newElem);
        void user_add(org.xowl.lang.runtime.Datatype elem);
        void user_remove(org.xowl.lang.runtime.Datatype elem);
        boolean inverse_check_add(org.xowl.lang.runtime.Datatype elem);
        boolean inverse_check_remove(org.xowl.lang.runtime.Datatype elem);
        boolean inverse_check_replace(org.xowl.lang.runtime.Datatype oldElem, org.xowl.lang.runtime.Datatype  newElem);
        void inverse_add(org.xowl.lang.runtime.Datatype elem);
        void inverse_remove(org.xowl.lang.runtime.Datatype elem);
    }
    datatype __getImplOfdatatype();
    boolean setDatatype(org.xowl.lang.runtime.Datatype elem);
    org.xowl.lang.runtime.Datatype getDatatype();
    // </editor-fold>
}
