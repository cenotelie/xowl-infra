/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class ObjectInverseOf implements org.xowl.lang.owl2.ObjectPropertyExpression, org.xowl.lang.owl2.InverseObjectProperties_OR_ObjectInverseOf {
    // <editor-fold defaultstate="collapsed" desc="Property inverse">
    private static class inverse_impl implements org.xowl.lang.owl2.InverseObjectProperties_OR_ObjectInverseOf.inverse {
        private org.xowl.lang.owl2.ObjectInverseOf domain;
        private org.xowl.lang.owl2.ObjectPropertyExpression data;
        public org.xowl.lang.owl2.ObjectPropertyExpression get_raw() { return data; }
        public org.xowl.lang.owl2.ObjectPropertyExpression get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.ObjectPropertyExpression elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.lang.owl2.ObjectPropertyExpression  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.lang.owl2.ObjectPropertyExpression  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.lang.owl2.ObjectPropertyExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.lang.owl2.ObjectPropertyExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
            tree_remove(elem);
        }
        public inverse_impl(org.xowl.lang.owl2.ObjectInverseOf domain) {
            this.domain = domain;
        }
    }
    private inverse_impl dataInverse;
    public org.xowl.lang.owl2.InverseObjectProperties_OR_ObjectInverseOf.inverse __getImplOfinverse() { return dataInverse; }
    public boolean setInverse(org.xowl.lang.owl2.ObjectPropertyExpression elem) {
        if (dataInverse.get() != null) {
            if (elem == null) {
                if (!dataInverse.user_check_remove(dataInverse.get())) return false;
                dataInverse.user_remove(dataInverse.get());
            } else {
                if (!dataInverse.user_check_replace(dataInverse.get(), elem)) return false;
                dataInverse.user_remove(dataInverse.get());
                dataInverse.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataInverse.user_check_add(elem)) return false;
            dataInverse.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.ObjectPropertyExpression getInverse() { return dataInverse.get(); }
    // </editor-fold>

    public ObjectInverseOf() {
        dataInverse = new inverse_impl(this);
    }

}
