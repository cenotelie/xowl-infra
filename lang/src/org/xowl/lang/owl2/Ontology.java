/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class Ontology implements org.xowl.lang.owl2.Annotation_OR_Axiom_OR_Ontology {
    // <editor-fold defaultstate="collapsed" desc="Property hasIRI">
    public static interface hasIRI {
        boolean check_contains(org.xowl.lang.owl2.IRI elem);
        boolean user_check_add(org.xowl.lang.owl2.IRI elem);
        boolean user_check_remove(org.xowl.lang.owl2.IRI elem);
        boolean user_check_replace(org.xowl.lang.owl2.IRI oldElem, org.xowl.lang.owl2.IRI  newElem);
        void user_add(org.xowl.lang.owl2.IRI elem);
        void user_remove(org.xowl.lang.owl2.IRI elem);
        boolean inverse_check_add(org.xowl.lang.owl2.IRI elem);
        boolean inverse_check_remove(org.xowl.lang.owl2.IRI elem);
        boolean inverse_check_replace(org.xowl.lang.owl2.IRI oldElem, org.xowl.lang.owl2.IRI  newElem);
        void inverse_add(org.xowl.lang.owl2.IRI elem);
        void inverse_remove(org.xowl.lang.owl2.IRI elem);
    }
    private static class hasIRI_impl implements org.xowl.lang.owl2.Ontology.hasIRI {
        private org.xowl.lang.owl2.Ontology domain;
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
        public hasIRI_impl(org.xowl.lang.owl2.Ontology domain) {
            this.domain = domain;
        }
    }
    private hasIRI_impl dataHasIRI;
    public org.xowl.lang.owl2.Ontology.hasIRI __getImplOfhasIRI() { return dataHasIRI; }
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

    // <editor-fold defaultstate="collapsed" desc="Property contains">
    public static interface contains {
        boolean check_contains(org.xowl.lang.runtime.Entity elem);
        boolean user_check_add(org.xowl.lang.runtime.Entity elem);
        boolean user_check_remove(org.xowl.lang.runtime.Entity elem);
        boolean user_check_replace(org.xowl.lang.runtime.Entity oldElem, org.xowl.lang.runtime.Entity  newElem);
        void user_add(org.xowl.lang.runtime.Entity elem);
        void user_remove(org.xowl.lang.runtime.Entity elem);
        boolean inverse_check_add(org.xowl.lang.runtime.Entity elem);
        boolean inverse_check_remove(org.xowl.lang.runtime.Entity elem);
        boolean inverse_check_replace(org.xowl.lang.runtime.Entity oldElem, org.xowl.lang.runtime.Entity  newElem);
        void inverse_add(org.xowl.lang.runtime.Entity elem);
        void inverse_remove(org.xowl.lang.runtime.Entity elem);
    }
    private static class contains_impl implements org.xowl.lang.owl2.Ontology.contains {
        private org.xowl.lang.owl2.Ontology domain;
        private java.util.List<org.xowl.lang.runtime.Entity> data;
        public java.util.Collection<org.xowl.lang.runtime.Entity> get_raw() { return new java.util.ArrayList<org.xowl.lang.runtime.Entity>(data); }
        public java.util.Collection<org.xowl.lang.runtime.Entity> get() { return new java.util.ArrayList<org.xowl.lang.runtime.Entity>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.runtime.Entity elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.runtime.Entity elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.runtime.Entity elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.runtime.Entity oldElem, org.xowl.lang.runtime.Entity  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.runtime.Entity elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.runtime.Entity elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.runtime.Entity elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.runtime.Entity elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.runtime.Entity oldElem, org.xowl.lang.runtime.Entity  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.runtime.Entity elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.runtime.Entity elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.runtime.Entity elem) {
            if (!elem.__getImplOfcontainedBy().inverse_check_add(domain)) return false;
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.runtime.Entity elem) {
            if (!elem.__getImplOfcontainedBy().inverse_check_remove(domain)) return false;
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.runtime.Entity oldElem, org.xowl.lang.runtime.Entity  newElem) {
            if (!oldElem.__getImplOfcontainedBy().inverse_check_remove(domain)) return false;
            if (!newElem.__getImplOfcontainedBy().inverse_check_add(domain)) return false;
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.runtime.Entity elem) {
            elem.__getImplOfcontainedBy().inverse_add(domain);
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.runtime.Entity elem) {
            elem.__getImplOfcontainedBy().inverse_remove(domain);
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.runtime.Entity elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.runtime.Entity elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.runtime.Entity oldElem, org.xowl.lang.runtime.Entity  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.runtime.Entity elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.runtime.Entity elem) {
            tree_remove(elem);
        }
        public contains_impl(org.xowl.lang.owl2.Ontology domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.runtime.Entity>();
        }
    }
    private contains_impl dataContains;
    public org.xowl.lang.owl2.Ontology.contains __getImplOfcontains() { return dataContains; }
    public boolean addContains(org.xowl.lang.runtime.Entity elem) {
        if (!dataContains.user_check_add(elem)) return false;
        dataContains.user_add(elem);
        return true;
    }
    public boolean removeContains(org.xowl.lang.runtime.Entity elem) {
        if (!dataContains.user_check_remove(elem)) return false;
        dataContains.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.runtime.Entity> getAllContains() { return dataContains.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property annotations">
    private static class annotations_impl implements org.xowl.lang.owl2.Annotation_OR_Axiom_OR_Ontology.annotations {
        private org.xowl.lang.owl2.Ontology domain;
        private java.util.List<org.xowl.lang.owl2.Annotation> data;
        public java.util.Collection<org.xowl.lang.owl2.Annotation> get_raw() { return new java.util.ArrayList<org.xowl.lang.owl2.Annotation>(data); }
        public java.util.Collection<org.xowl.lang.owl2.Annotation> get() { return new java.util.ArrayList<org.xowl.lang.owl2.Annotation>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.Annotation elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.owl2.Annotation elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.Annotation elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.Annotation oldElem, org.xowl.lang.owl2.Annotation  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.Annotation elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.owl2.Annotation elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.owl2.Annotation elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.Annotation elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.Annotation oldElem, org.xowl.lang.owl2.Annotation  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.Annotation elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.Annotation elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.Annotation elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.Annotation elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.Annotation oldElem, org.xowl.lang.owl2.Annotation  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.Annotation elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.Annotation elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.Annotation elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.Annotation elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.Annotation oldElem, org.xowl.lang.owl2.Annotation  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.Annotation elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.Annotation elem) {
            tree_remove(elem);
        }
        public annotations_impl(org.xowl.lang.owl2.Ontology domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.owl2.Annotation>();
        }
    }
    private annotations_impl dataAnnotations;
    public org.xowl.lang.owl2.Annotation_OR_Axiom_OR_Ontology.annotations __getImplOfannotations() { return dataAnnotations; }
    public boolean addAnnotations(org.xowl.lang.owl2.Annotation elem) {
        if (!dataAnnotations.user_check_add(elem)) return false;
        dataAnnotations.user_add(elem);
        return true;
    }
    public boolean removeAnnotations(org.xowl.lang.owl2.Annotation elem) {
        if (!dataAnnotations.user_check_remove(elem)) return false;
        dataAnnotations.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.owl2.Annotation> getAllAnnotations() { return dataAnnotations.get(); }
    // </editor-fold>

    public Ontology() {
        dataHasIRI = new hasIRI_impl(this);
        dataContains = new contains_impl(this);
        dataAnnotations = new annotations_impl(this);
    }

}
