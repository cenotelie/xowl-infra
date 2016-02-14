/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.infra.lang.owl2;

public class ClassSequence implements org.xowl.infra.lang.owl2.ClassSequenceExpression {
    // <editor-fold defaultstate="collapsed" desc="Property classElements">
    public static interface classElements {
        boolean check_contains(org.xowl.infra.lang.owl2.ClassElement elem);
        boolean user_check_add(org.xowl.infra.lang.owl2.ClassElement elem);
        boolean user_check_remove(org.xowl.infra.lang.owl2.ClassElement elem);
        boolean user_check_replace(org.xowl.infra.lang.owl2.ClassElement oldElem, org.xowl.infra.lang.owl2.ClassElement  newElem);
        void user_add(org.xowl.infra.lang.owl2.ClassElement elem);
        void user_remove(org.xowl.infra.lang.owl2.ClassElement elem);
        boolean inverse_check_add(org.xowl.infra.lang.owl2.ClassElement elem);
        boolean inverse_check_remove(org.xowl.infra.lang.owl2.ClassElement elem);
        boolean inverse_check_replace(org.xowl.infra.lang.owl2.ClassElement oldElem, org.xowl.infra.lang.owl2.ClassElement  newElem);
        void inverse_add(org.xowl.infra.lang.owl2.ClassElement elem);
        void inverse_remove(org.xowl.infra.lang.owl2.ClassElement elem);
    }
    private static class classElements_impl implements org.xowl.infra.lang.owl2.ClassSequence.classElements {
        private org.xowl.infra.lang.owl2.ClassSequence domain;
        private java.util.List<org.xowl.infra.lang.owl2.ClassElement> data;
        public java.util.Collection<org.xowl.infra.lang.owl2.ClassElement> get_raw() { return new java.util.ArrayList<org.xowl.infra.lang.owl2.ClassElement>(data); }
        public java.util.Collection<org.xowl.infra.lang.owl2.ClassElement> get() { return new java.util.ArrayList<org.xowl.infra.lang.owl2.ClassElement>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.infra.lang.owl2.ClassElement elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.infra.lang.owl2.ClassElement elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.infra.lang.owl2.ClassElement elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.infra.lang.owl2.ClassElement oldElem, org.xowl.infra.lang.owl2.ClassElement  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.infra.lang.owl2.ClassElement elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.infra.lang.owl2.ClassElement elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.infra.lang.owl2.ClassElement elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.infra.lang.owl2.ClassElement elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.infra.lang.owl2.ClassElement oldElem, org.xowl.infra.lang.owl2.ClassElement  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.infra.lang.owl2.ClassElement elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.infra.lang.owl2.ClassElement elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.infra.lang.owl2.ClassElement elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.infra.lang.owl2.ClassElement elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.infra.lang.owl2.ClassElement oldElem, org.xowl.infra.lang.owl2.ClassElement  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.infra.lang.owl2.ClassElement elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.infra.lang.owl2.ClassElement elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.infra.lang.owl2.ClassElement elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.infra.lang.owl2.ClassElement elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.infra.lang.owl2.ClassElement oldElem, org.xowl.infra.lang.owl2.ClassElement  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.infra.lang.owl2.ClassElement elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.infra.lang.owl2.ClassElement elem) {
            tree_remove(elem);
        }
        public classElements_impl(org.xowl.infra.lang.owl2.ClassSequence domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.infra.lang.owl2.ClassElement>();
        }
    }
    private classElements_impl dataClassElements;
    public org.xowl.infra.lang.owl2.ClassSequence.classElements __getImplOfclassElements() { return dataClassElements; }
    public boolean addClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        if (!dataClassElements.user_check_add(elem)) return false;
        dataClassElements.user_add(elem);
        return true;
    }
    public boolean removeClassElements(org.xowl.infra.lang.owl2.ClassElement elem) {
        if (!dataClassElements.user_check_remove(elem)) return false;
        dataClassElements.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.infra.lang.owl2.ClassElement> getAllClassElements() { return dataClassElements.get(); }
    // </editor-fold>

    public ClassSequence() {
        dataClassElements = new classElements_impl(this);
    }

}