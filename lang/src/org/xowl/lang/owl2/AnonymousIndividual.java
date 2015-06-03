/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class AnonymousIndividual implements org.xowl.lang.owl2.AnnotationSubject, org.xowl.lang.owl2.AnnotationValue, org.xowl.lang.owl2.IndividualExpression, org.xowl.lang.runtime.Individual, org.xowl.lang.runtime.Value {
    // <editor-fold defaultstate="collapsed" desc="Property classifiedBy">
    private static class classifiedBy_impl implements org.xowl.lang.runtime.Individual.classifiedBy {
        private org.xowl.lang.owl2.AnonymousIndividual domain;
        private java.util.List<org.xowl.lang.runtime.Class> data;
        public java.util.Collection<org.xowl.lang.runtime.Class> get_raw() { return new java.util.ArrayList<org.xowl.lang.runtime.Class>(data); }
        public java.util.Collection<org.xowl.lang.runtime.Class> get() { return new java.util.ArrayList<org.xowl.lang.runtime.Class>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.runtime.Class elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.runtime.Class elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.runtime.Class elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.runtime.Class oldElem, org.xowl.lang.runtime.Class  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.runtime.Class elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.runtime.Class elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.runtime.Class elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.runtime.Class elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.runtime.Class oldElem, org.xowl.lang.runtime.Class  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.runtime.Class elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.runtime.Class elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.runtime.Class elem) {
            if (!elem.__getImplOfclassifies().inverse_check_add(domain)) return false;
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.runtime.Class elem) {
            if (!elem.__getImplOfclassifies().inverse_check_remove(domain)) return false;
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.runtime.Class oldElem, org.xowl.lang.runtime.Class  newElem) {
            if (!oldElem.__getImplOfclassifies().inverse_check_remove(domain)) return false;
            if (!newElem.__getImplOfclassifies().inverse_check_add(domain)) return false;
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.runtime.Class elem) {
            elem.__getImplOfclassifies().inverse_add(domain);
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.runtime.Class elem) {
            elem.__getImplOfclassifies().inverse_remove(domain);
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.runtime.Class elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.runtime.Class elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.runtime.Class oldElem, org.xowl.lang.runtime.Class  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.runtime.Class elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.runtime.Class elem) {
            tree_remove(elem);
        }
        public classifiedBy_impl(org.xowl.lang.owl2.AnonymousIndividual domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.runtime.Class>();
        }
    }
    private classifiedBy_impl dataClassifiedBy;
    public org.xowl.lang.runtime.Individual.classifiedBy __getImplOfclassifiedBy() { return dataClassifiedBy; }
    public boolean addClassifiedBy(org.xowl.lang.runtime.Class elem) {
        if (!dataClassifiedBy.user_check_add(elem)) return false;
        dataClassifiedBy.user_add(elem);
        return true;
    }
    public boolean removeClassifiedBy(org.xowl.lang.runtime.Class elem) {
        if (!dataClassifiedBy.user_check_remove(elem)) return false;
        dataClassifiedBy.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.runtime.Class> getAllClassifiedBy() { return dataClassifiedBy.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property differentFrom">
    private static class differentFrom_impl implements org.xowl.lang.runtime.Individual.differentFrom {
        private org.xowl.lang.owl2.AnonymousIndividual domain;
        private java.util.List<org.xowl.lang.runtime.Individual> data;
        public java.util.Collection<org.xowl.lang.runtime.Individual> get_raw() { return new java.util.ArrayList<org.xowl.lang.runtime.Individual>(data); }
        public java.util.Collection<org.xowl.lang.runtime.Individual> get() { return new java.util.ArrayList<org.xowl.lang.runtime.Individual>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.runtime.Individual elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.runtime.Individual elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.runtime.Individual elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.runtime.Individual oldElem, org.xowl.lang.runtime.Individual  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.runtime.Individual elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.runtime.Individual elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.runtime.Individual elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.runtime.Individual elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.runtime.Individual oldElem, org.xowl.lang.runtime.Individual  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.runtime.Individual elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.runtime.Individual elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.runtime.Individual elem) {
            if (!elem.__getImplOfdifferentFrom().inverse_check_add(domain)) return false;
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.runtime.Individual elem) {
            if (!elem.__getImplOfdifferentFrom().inverse_check_remove(domain)) return false;
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.runtime.Individual oldElem, org.xowl.lang.runtime.Individual  newElem) {
            if (!oldElem.__getImplOfdifferentFrom().inverse_check_remove(domain)) return false;
            if (!newElem.__getImplOfdifferentFrom().inverse_check_add(domain)) return false;
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.runtime.Individual elem) {
            elem.__getImplOfdifferentFrom().inverse_add(domain);
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.runtime.Individual elem) {
            elem.__getImplOfdifferentFrom().inverse_remove(domain);
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.runtime.Individual elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.runtime.Individual elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.runtime.Individual oldElem, org.xowl.lang.runtime.Individual  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.runtime.Individual elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.runtime.Individual elem) {
            tree_remove(elem);
        }
        public differentFrom_impl(org.xowl.lang.owl2.AnonymousIndividual domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.runtime.Individual>();
        }
    }
    private differentFrom_impl dataDifferentFrom;
    public org.xowl.lang.runtime.Individual.differentFrom __getImplOfdifferentFrom() { return dataDifferentFrom; }
    public boolean addDifferentFrom(org.xowl.lang.runtime.Individual elem) {
        if (!dataDifferentFrom.user_check_add(elem)) return false;
        dataDifferentFrom.user_add(elem);
        return true;
    }
    public boolean removeDifferentFrom(org.xowl.lang.runtime.Individual elem) {
        if (!dataDifferentFrom.user_check_remove(elem)) return false;
        dataDifferentFrom.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.runtime.Individual> getAllDifferentFrom() { return dataDifferentFrom.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property nodeID">
    public static interface nodeID {
        boolean check_contains(java.lang.String elem);
        boolean user_check_add(java.lang.String elem);
        boolean user_check_remove(java.lang.String elem);
        boolean user_check_replace(java.lang.String oldElem, java.lang.String  newElem);
        void user_add(java.lang.String elem);
        void user_remove(java.lang.String elem);
        boolean inverse_check_add(java.lang.String elem);
        boolean inverse_check_remove(java.lang.String elem);
        boolean inverse_check_replace(java.lang.String oldElem, java.lang.String  newElem);
        void inverse_add(java.lang.String elem);
        void inverse_remove(java.lang.String elem);
    }
    private static class nodeID_impl implements org.xowl.lang.owl2.AnonymousIndividual.nodeID {
        private org.xowl.lang.owl2.AnonymousIndividual domain;
        private java.lang.String data;
        public java.lang.String get_raw() { return data; }
        public java.lang.String get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(java.lang.String elem) { return data.equals(elem); }
        public boolean simple_check_add(java.lang.String elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(java.lang.String elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(java.lang.String oldElem, java.lang.String  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(java.lang.String elem) {
            data = elem;
        }
        public void simple_remove(java.lang.String elem) {
            data = null;
        }
        private boolean tree_check_add(java.lang.String elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(java.lang.String elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(java.lang.String oldElem, java.lang.String  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(java.lang.String elem) {
            simple_add(elem);
        }
        private void tree_remove(java.lang.String elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(java.lang.String elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(java.lang.String elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(java.lang.String oldElem, java.lang.String  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(java.lang.String elem) {
            tree_add(elem);
        }
        @Override public void user_remove(java.lang.String elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(java.lang.String elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(java.lang.String elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(java.lang.String oldElem, java.lang.String  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(java.lang.String elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(java.lang.String elem) {
            tree_remove(elem);
        }
        public nodeID_impl(org.xowl.lang.owl2.AnonymousIndividual domain) {
            this.domain = domain;
        }
    }
    private nodeID_impl dataNodeID;
    public org.xowl.lang.owl2.AnonymousIndividual.nodeID __getImplOfnodeID() { return dataNodeID; }
    public boolean setNodeID(java.lang.String elem) {
        dataNodeID.simple_add(elem);
        return true;
    }
    public java.lang.String getNodeID() { return dataNodeID.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property asserts">
    private static class asserts_impl implements org.xowl.lang.runtime.Individual.asserts {
        private org.xowl.lang.owl2.AnonymousIndividual domain;
        private java.util.List<org.xowl.lang.runtime.PropertyAssertion> data;
        public java.util.Collection<org.xowl.lang.runtime.PropertyAssertion> get_raw() { return new java.util.ArrayList<org.xowl.lang.runtime.PropertyAssertion>(data); }
        public java.util.Collection<org.xowl.lang.runtime.PropertyAssertion> get() { return new java.util.ArrayList<org.xowl.lang.runtime.PropertyAssertion>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.runtime.PropertyAssertion elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.runtime.PropertyAssertion elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.runtime.PropertyAssertion elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.runtime.PropertyAssertion oldElem, org.xowl.lang.runtime.PropertyAssertion  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.runtime.PropertyAssertion elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.runtime.PropertyAssertion elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.runtime.PropertyAssertion elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.runtime.PropertyAssertion elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.runtime.PropertyAssertion oldElem, org.xowl.lang.runtime.PropertyAssertion  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.runtime.PropertyAssertion elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.runtime.PropertyAssertion elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.runtime.PropertyAssertion elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.runtime.PropertyAssertion elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.runtime.PropertyAssertion oldElem, org.xowl.lang.runtime.PropertyAssertion  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.runtime.PropertyAssertion elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.runtime.PropertyAssertion elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.runtime.PropertyAssertion elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.runtime.PropertyAssertion elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.runtime.PropertyAssertion oldElem, org.xowl.lang.runtime.PropertyAssertion  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.runtime.PropertyAssertion elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.runtime.PropertyAssertion elem) {
            tree_remove(elem);
        }
        public asserts_impl(org.xowl.lang.owl2.AnonymousIndividual domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.runtime.PropertyAssertion>();
        }
    }
    private asserts_impl dataAsserts;
    public org.xowl.lang.runtime.Individual.asserts __getImplOfasserts() { return dataAsserts; }
    public boolean addAsserts(org.xowl.lang.runtime.PropertyAssertion elem) {
        if (!dataAsserts.user_check_add(elem)) return false;
        dataAsserts.user_add(elem);
        return true;
    }
    public boolean removeAsserts(org.xowl.lang.runtime.PropertyAssertion elem) {
        if (!dataAsserts.user_check_remove(elem)) return false;
        dataAsserts.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.runtime.PropertyAssertion> getAllAsserts() { return dataAsserts.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property sameAs">
    private static class sameAs_impl implements org.xowl.lang.runtime.Individual.sameAs {
        private org.xowl.lang.owl2.AnonymousIndividual domain;
        private java.util.List<org.xowl.lang.runtime.Individual> data;
        public java.util.Collection<org.xowl.lang.runtime.Individual> get_raw() { return new java.util.ArrayList<org.xowl.lang.runtime.Individual>(data); }
        public java.util.Collection<org.xowl.lang.runtime.Individual> get() { return new java.util.ArrayList<org.xowl.lang.runtime.Individual>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.runtime.Individual elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.runtime.Individual elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.runtime.Individual elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.runtime.Individual oldElem, org.xowl.lang.runtime.Individual  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.runtime.Individual elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.runtime.Individual elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.runtime.Individual elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.runtime.Individual elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.runtime.Individual oldElem, org.xowl.lang.runtime.Individual  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.runtime.Individual elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.runtime.Individual elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.runtime.Individual elem) {
            if (!elem.__getImplOfsameAs().inverse_check_add(domain)) return false;
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.runtime.Individual elem) {
            if (!elem.__getImplOfsameAs().inverse_check_remove(domain)) return false;
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.runtime.Individual oldElem, org.xowl.lang.runtime.Individual  newElem) {
            if (!oldElem.__getImplOfsameAs().inverse_check_remove(domain)) return false;
            if (!newElem.__getImplOfsameAs().inverse_check_add(domain)) return false;
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.runtime.Individual elem) {
            elem.__getImplOfsameAs().inverse_add(domain);
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.runtime.Individual elem) {
            elem.__getImplOfsameAs().inverse_remove(domain);
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.runtime.Individual elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.runtime.Individual elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.runtime.Individual oldElem, org.xowl.lang.runtime.Individual  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.runtime.Individual elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.runtime.Individual elem) {
            tree_remove(elem);
        }
        public sameAs_impl(org.xowl.lang.owl2.AnonymousIndividual domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.runtime.Individual>();
        }
    }
    private sameAs_impl dataSameAs;
    public org.xowl.lang.runtime.Individual.sameAs __getImplOfsameAs() { return dataSameAs; }
    public boolean addSameAs(org.xowl.lang.runtime.Individual elem) {
        if (!dataSameAs.user_check_add(elem)) return false;
        dataSameAs.user_add(elem);
        return true;
    }
    public boolean removeSameAs(org.xowl.lang.runtime.Individual elem) {
        if (!dataSameAs.user_check_remove(elem)) return false;
        dataSameAs.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.runtime.Individual> getAllSameAs() { return dataSameAs.get(); }
    // </editor-fold>

    public AnonymousIndividual() {
        dataClassifiedBy = new classifiedBy_impl(this);
        dataDifferentFrom = new differentFrom_impl(this);
        dataNodeID = new nodeID_impl(this);
        dataAsserts = new asserts_impl(this);
        dataSameAs = new sameAs_impl(this);
    }

}
