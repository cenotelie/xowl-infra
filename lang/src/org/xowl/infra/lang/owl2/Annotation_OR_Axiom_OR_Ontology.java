/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.infra.lang.owl2;

public interface Annotation_OR_Axiom_OR_Ontology {
    // <editor-fold defaultstate="collapsed" desc="Property annotations">
    public static interface annotations {
        boolean check_contains(org.xowl.infra.lang.owl2.Annotation elem);
        boolean user_check_add(org.xowl.infra.lang.owl2.Annotation elem);
        boolean user_check_remove(org.xowl.infra.lang.owl2.Annotation elem);
        boolean user_check_replace(org.xowl.infra.lang.owl2.Annotation oldElem, org.xowl.infra.lang.owl2.Annotation  newElem);
        void user_add(org.xowl.infra.lang.owl2.Annotation elem);
        void user_remove(org.xowl.infra.lang.owl2.Annotation elem);
        boolean inverse_check_add(org.xowl.infra.lang.owl2.Annotation elem);
        boolean inverse_check_remove(org.xowl.infra.lang.owl2.Annotation elem);
        boolean inverse_check_replace(org.xowl.infra.lang.owl2.Annotation oldElem, org.xowl.infra.lang.owl2.Annotation  newElem);
        void inverse_add(org.xowl.infra.lang.owl2.Annotation elem);
        void inverse_remove(org.xowl.infra.lang.owl2.Annotation elem);
    }
    annotations __getImplOfannotations();
    boolean addAnnotations(org.xowl.infra.lang.owl2.Annotation elem);
    boolean removeAnnotations(org.xowl.infra.lang.owl2.Annotation elem);
    java.util.Collection<org.xowl.infra.lang.owl2.Annotation> getAllAnnotations();
    // </editor-fold>
}
