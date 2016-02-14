/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.infra.lang.owl2;

public class DataOneOf implements org.xowl.infra.lang.owl2.Datarange {
    // <editor-fold defaultstate="collapsed" desc="Property literalSeq">
    public static interface literalSeq {
        boolean check_contains(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
        boolean user_check_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
        boolean user_check_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
        boolean user_check_replace(org.xowl.infra.lang.owl2.LiteralSequenceExpression oldElem, org.xowl.infra.lang.owl2.LiteralSequenceExpression  newElem);
        void user_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
        void user_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
        boolean inverse_check_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
        boolean inverse_check_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
        boolean inverse_check_replace(org.xowl.infra.lang.owl2.LiteralSequenceExpression oldElem, org.xowl.infra.lang.owl2.LiteralSequenceExpression  newElem);
        void inverse_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
        void inverse_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem);
    }
    private static class literalSeq_impl implements org.xowl.infra.lang.owl2.DataOneOf.literalSeq {
        private org.xowl.infra.lang.owl2.DataOneOf domain;
        private org.xowl.infra.lang.owl2.LiteralSequenceExpression data;
        public org.xowl.infra.lang.owl2.LiteralSequenceExpression get_raw() { return data; }
        public org.xowl.infra.lang.owl2.LiteralSequenceExpression get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.infra.lang.owl2.LiteralSequenceExpression oldElem, org.xowl.infra.lang.owl2.LiteralSequenceExpression  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.infra.lang.owl2.LiteralSequenceExpression oldElem, org.xowl.infra.lang.owl2.LiteralSequenceExpression  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.infra.lang.owl2.LiteralSequenceExpression oldElem, org.xowl.infra.lang.owl2.LiteralSequenceExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.infra.lang.owl2.LiteralSequenceExpression oldElem, org.xowl.infra.lang.owl2.LiteralSequenceExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
            tree_remove(elem);
        }
        public literalSeq_impl(org.xowl.infra.lang.owl2.DataOneOf domain) {
            this.domain = domain;
        }
    }
    private literalSeq_impl dataLiteralSeq;
    public org.xowl.infra.lang.owl2.DataOneOf.literalSeq __getImplOfliteralSeq() { return dataLiteralSeq; }
    public boolean setLiteralSeq(org.xowl.infra.lang.owl2.LiteralSequenceExpression elem) {
        if (dataLiteralSeq.get() != null) {
            if (elem == null) {
                if (!dataLiteralSeq.user_check_remove(dataLiteralSeq.get())) return false;
                dataLiteralSeq.user_remove(dataLiteralSeq.get());
            } else {
                if (!dataLiteralSeq.user_check_replace(dataLiteralSeq.get(), elem)) return false;
                dataLiteralSeq.user_remove(dataLiteralSeq.get());
                dataLiteralSeq.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataLiteralSeq.user_check_add(elem)) return false;
            dataLiteralSeq.user_add(elem);
        }
        return true;
    }
    public org.xowl.infra.lang.owl2.LiteralSequenceExpression getLiteralSeq() { return dataLiteralSeq.get(); }
    // </editor-fold>

    public DataOneOf() {
        dataLiteralSeq = new literalSeq_impl(this);
    }

}