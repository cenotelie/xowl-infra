/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.actions;

public class SelectAll implements org.xowl.lang.actions.Select, org.xowl.lang.actions.Query, org.xowl.lang.actions.Action_OR_Query {
    // <editor-fold defaultstate="collapsed" desc="Property axioms">
    private static class axioms_impl implements org.xowl.lang.actions.Action_OR_Query.axioms {
        private org.xowl.lang.actions.SelectAll domain;
        private java.util.List<org.xowl.lang.owl2.Axiom> data;
        public java.util.Collection<org.xowl.lang.owl2.Axiom> get_raw() { return new java.util.ArrayList<org.xowl.lang.owl2.Axiom>(data); }
        public java.util.Collection<org.xowl.lang.owl2.Axiom> get() { return new java.util.ArrayList<org.xowl.lang.owl2.Axiom>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.Axiom elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.owl2.Axiom elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.Axiom elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.Axiom oldElem, org.xowl.lang.owl2.Axiom  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.Axiom elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.owl2.Axiom elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.owl2.Axiom elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.Axiom elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.Axiom oldElem, org.xowl.lang.owl2.Axiom  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.Axiom elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.Axiom elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.Axiom elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.Axiom elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.Axiom oldElem, org.xowl.lang.owl2.Axiom  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.Axiom elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.Axiom elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.Axiom elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.Axiom elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.Axiom oldElem, org.xowl.lang.owl2.Axiom  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.Axiom elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.Axiom elem) {
            tree_remove(elem);
        }
        public axioms_impl(org.xowl.lang.actions.SelectAll domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.owl2.Axiom>();
        }
    }
    private axioms_impl dataAxioms;
    public org.xowl.lang.actions.Action_OR_Query.axioms __getImplOfaxioms() { return dataAxioms; }
    public boolean addAxioms(org.xowl.lang.owl2.Axiom elem) {
        if (!dataAxioms.user_check_add(elem)) return false;
        dataAxioms.user_add(elem);
        return true;
    }
    public boolean removeAxioms(org.xowl.lang.owl2.Axiom elem) {
        if (!dataAxioms.user_check_remove(elem)) return false;
        dataAxioms.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.owl2.Axiom> getAllAxioms() { return dataAxioms.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property hasIRI">
    private static class hasIRI_impl implements org.xowl.lang.actions.Select.hasIRI {
        private org.xowl.lang.actions.SelectAll domain;
        private org.xowl.lang.owl2.IRI data;
        public org.xowl.lang.owl2.IRI get_raw() { return data; }
        public org.xowl.lang.owl2.IRI get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.IRI elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.lang.owl2.IRI elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.IRI elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.IRI oldElem, org.xowl.lang.owl2.IRI  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.IRI elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.lang.owl2.IRI elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.lang.owl2.IRI elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.IRI elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.IRI oldElem, org.xowl.lang.owl2.IRI  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.IRI elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.IRI elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.IRI elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.IRI elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.IRI oldElem, org.xowl.lang.owl2.IRI  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.IRI elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.IRI elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.IRI elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.IRI elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.IRI oldElem, org.xowl.lang.owl2.IRI  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.IRI elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.IRI elem) {
            tree_remove(elem);
        }
        public hasIRI_impl(org.xowl.lang.actions.SelectAll domain) {
            this.domain = domain;
        }
    }
    private hasIRI_impl dataHasIRI;
    public org.xowl.lang.actions.Select.hasIRI __getImplOfhasIRI() { return dataHasIRI; }
    public boolean setHasIRI(org.xowl.lang.owl2.IRI elem) {
        if (dataHasIRI.get() != null) {
            if (elem == null) {
                if (!dataHasIRI.user_check_remove(dataHasIRI.get())) return false;
                dataHasIRI.user_remove(dataHasIRI.get());
            } else {
                if (!dataHasIRI.user_check_replace(dataHasIRI.get(), elem)) return false;
                dataHasIRI.user_remove(dataHasIRI.get());
                dataHasIRI.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataHasIRI.user_check_add(elem)) return false;
            dataHasIRI.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.IRI getHasIRI() { return dataHasIRI.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property guards">
    private static class guards_impl implements org.xowl.lang.actions.Query.guards {
        private org.xowl.lang.actions.SelectAll domain;
        private java.util.List<org.xowl.lang.owl2.LiteralExpression> data;
        public java.util.Collection<org.xowl.lang.owl2.LiteralExpression> get_raw() { return new java.util.ArrayList<org.xowl.lang.owl2.LiteralExpression>(data); }
        public java.util.Collection<org.xowl.lang.owl2.LiteralExpression> get() { return new java.util.ArrayList<org.xowl.lang.owl2.LiteralExpression>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.LiteralExpression elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.owl2.LiteralExpression elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.LiteralExpression oldElem, org.xowl.lang.owl2.LiteralExpression  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.LiteralExpression elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.owl2.LiteralExpression elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.LiteralExpression oldElem, org.xowl.lang.owl2.LiteralExpression  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.LiteralExpression elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.LiteralExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.LiteralExpression oldElem, org.xowl.lang.owl2.LiteralExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.LiteralExpression elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.LiteralExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.LiteralExpression oldElem, org.xowl.lang.owl2.LiteralExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.LiteralExpression elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            tree_remove(elem);
        }
        public guards_impl(org.xowl.lang.actions.SelectAll domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.owl2.LiteralExpression>();
        }
    }
    private guards_impl dataGuards;
    public org.xowl.lang.actions.Query.guards __getImplOfguards() { return dataGuards; }
    public boolean addGuards(org.xowl.lang.owl2.LiteralExpression elem) {
        if (!dataGuards.user_check_add(elem)) return false;
        dataGuards.user_add(elem);
        return true;
    }
    public boolean removeGuards(org.xowl.lang.owl2.LiteralExpression elem) {
        if (!dataGuards.user_check_remove(elem)) return false;
        dataGuards.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.owl2.LiteralExpression> getAllGuards() { return dataGuards.get(); }
    // </editor-fold>

    public SelectAll() {
        dataAxioms = new axioms_impl(this);
        dataHasIRI = new hasIRI_impl(this);
        dataGuards = new guards_impl(this);
    }
    
}
