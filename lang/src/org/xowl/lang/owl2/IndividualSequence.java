/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class IndividualSequence implements org.xowl.lang.owl2.IndividualSequenceExpression, org.xowl.lang.owl2.SequenceExpression {
    // <editor-fold defaultstate="collapsed" desc="Property individualElements">
    public static interface individualElements {
        boolean check_contains(org.xowl.lang.owl2.IndividualElement elem);
        boolean user_check_add(org.xowl.lang.owl2.IndividualElement elem);
        boolean user_check_remove(org.xowl.lang.owl2.IndividualElement elem);
        boolean user_check_replace(org.xowl.lang.owl2.IndividualElement oldElem, org.xowl.lang.owl2.IndividualElement  newElem);
        void user_add(org.xowl.lang.owl2.IndividualElement elem);
        void user_remove(org.xowl.lang.owl2.IndividualElement elem);
        boolean inverse_check_add(org.xowl.lang.owl2.IndividualElement elem);
        boolean inverse_check_remove(org.xowl.lang.owl2.IndividualElement elem);
        boolean inverse_check_replace(org.xowl.lang.owl2.IndividualElement oldElem, org.xowl.lang.owl2.IndividualElement  newElem);
        void inverse_add(org.xowl.lang.owl2.IndividualElement elem);
        void inverse_remove(org.xowl.lang.owl2.IndividualElement elem);
    }
    private static class individualElements_impl implements org.xowl.lang.owl2.IndividualSequence.individualElements {
        private org.xowl.lang.owl2.IndividualSequence domain;
        private java.util.List<org.xowl.lang.owl2.IndividualElement> data;
        public java.util.Collection<org.xowl.lang.owl2.IndividualElement> get_raw() { return new java.util.ArrayList<org.xowl.lang.owl2.IndividualElement>(data); }
        public java.util.Collection<org.xowl.lang.owl2.IndividualElement> get() { return new java.util.ArrayList<org.xowl.lang.owl2.IndividualElement>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.IndividualElement elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.owl2.IndividualElement elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.IndividualElement elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.IndividualElement oldElem, org.xowl.lang.owl2.IndividualElement  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.IndividualElement elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.owl2.IndividualElement elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.owl2.IndividualElement elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.IndividualElement elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.IndividualElement oldElem, org.xowl.lang.owl2.IndividualElement  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.IndividualElement elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.IndividualElement elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.IndividualElement elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.IndividualElement elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.IndividualElement oldElem, org.xowl.lang.owl2.IndividualElement  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.IndividualElement elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.IndividualElement elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.IndividualElement elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.IndividualElement elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.IndividualElement oldElem, org.xowl.lang.owl2.IndividualElement  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.IndividualElement elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.IndividualElement elem) {
            tree_remove(elem);
        }
        public individualElements_impl(org.xowl.lang.owl2.IndividualSequence domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.owl2.IndividualElement>();
        }
    }
    private individualElements_impl dataIndividualElements;
    public org.xowl.lang.owl2.IndividualSequence.individualElements __getImplOfindividualElements() { return dataIndividualElements; }
    public boolean addIndividualElements(org.xowl.lang.owl2.IndividualElement elem) {
        if (!dataIndividualElements.user_check_add(elem)) return false;
        dataIndividualElements.user_add(elem);
        return true;
    }
    public boolean removeIndividualElements(org.xowl.lang.owl2.IndividualElement elem) {
        if (!dataIndividualElements.user_check_remove(elem)) return false;
        dataIndividualElements.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.owl2.IndividualElement> getAllIndividualElements() { return dataIndividualElements.get(); }
    // </editor-fold>

    public IndividualSequence() {
        dataIndividualElements = new individualElements_impl(this);
    }
    
}
