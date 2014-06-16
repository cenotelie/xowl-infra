/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class Annotation implements org.xowl.lang.owl2.Annotation_OR_AnnotationAssertion, org.xowl.lang.owl2.Annotation_OR_Axiom_OR_Ontology, org.xowl.lang.owl2.Annotation_OR_AnnotationAssertion_OR_AnnotationPropertyDomain_OR_AnnotationPropertyRange_OR_SubAnno1 {
    // <editor-fold defaultstate="collapsed" desc="Property annotValue">
    private static class annotValue_impl implements org.xowl.lang.owl2.Annotation_OR_AnnotationAssertion.annotValue {
        private org.xowl.lang.owl2.Annotation domain;
        private org.xowl.lang.owl2.AnnotationValue data;
        public org.xowl.lang.owl2.AnnotationValue get_raw() { return data; }
        public org.xowl.lang.owl2.AnnotationValue get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.AnnotationValue elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.lang.owl2.AnnotationValue elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.AnnotationValue elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.AnnotationValue oldElem, org.xowl.lang.owl2.AnnotationValue  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.AnnotationValue elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.lang.owl2.AnnotationValue elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.lang.owl2.AnnotationValue elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.AnnotationValue elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.AnnotationValue oldElem, org.xowl.lang.owl2.AnnotationValue  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.AnnotationValue elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.AnnotationValue elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.AnnotationValue elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.AnnotationValue elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.AnnotationValue oldElem, org.xowl.lang.owl2.AnnotationValue  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.AnnotationValue elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.AnnotationValue elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.AnnotationValue elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.AnnotationValue elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.AnnotationValue oldElem, org.xowl.lang.owl2.AnnotationValue  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.AnnotationValue elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.AnnotationValue elem) {
            tree_remove(elem);
        }
        public annotValue_impl(org.xowl.lang.owl2.Annotation domain) {
            this.domain = domain;
        }
    }
    private annotValue_impl dataAnnotValue;
    public org.xowl.lang.owl2.Annotation_OR_AnnotationAssertion.annotValue __getImplOfannotValue() { return dataAnnotValue; }
    public boolean setAnnotValue(org.xowl.lang.owl2.AnnotationValue elem) {
        if (dataAnnotValue.get() != null) {
            if (elem == null) {
                if (!dataAnnotValue.user_check_remove(dataAnnotValue.get())) return false;
                dataAnnotValue.user_remove(dataAnnotValue.get());
            } else {
                if (!dataAnnotValue.user_check_replace(dataAnnotValue.get(), elem)) return false;
                dataAnnotValue.user_remove(dataAnnotValue.get());
                dataAnnotValue.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataAnnotValue.user_check_add(elem)) return false;
            dataAnnotValue.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.AnnotationValue getAnnotValue() { return dataAnnotValue.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property annotProperty">
    private static class annotProperty_impl implements org.xowl.lang.owl2.Annotation_OR_AnnotationAssertion_OR_AnnotationPropertyDomain_OR_AnnotationPropertyRange_OR_SubAnno1.annotProperty {
        private org.xowl.lang.owl2.Annotation domain;
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
        public annotProperty_impl(org.xowl.lang.owl2.Annotation domain) {
            this.domain = domain;
        }
    }
    private annotProperty_impl dataAnnotProperty;
    public org.xowl.lang.owl2.Annotation_OR_AnnotationAssertion_OR_AnnotationPropertyDomain_OR_AnnotationPropertyRange_OR_SubAnno1.annotProperty __getImplOfannotProperty() { return dataAnnotProperty; }
    public boolean setAnnotProperty(org.xowl.lang.owl2.IRI elem) {
        if (dataAnnotProperty.get() != null) {
            if (elem == null) {
                if (!dataAnnotProperty.user_check_remove(dataAnnotProperty.get())) return false;
                dataAnnotProperty.user_remove(dataAnnotProperty.get());
            } else {
                if (!dataAnnotProperty.user_check_replace(dataAnnotProperty.get(), elem)) return false;
                dataAnnotProperty.user_remove(dataAnnotProperty.get());
                dataAnnotProperty.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataAnnotProperty.user_check_add(elem)) return false;
            dataAnnotProperty.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.IRI getAnnotProperty() { return dataAnnotProperty.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property annotations">
    private static class annotations_impl implements org.xowl.lang.owl2.Annotation_OR_Axiom_OR_Ontology.annotations {
        private org.xowl.lang.owl2.Annotation domain;
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
        public annotations_impl(org.xowl.lang.owl2.Annotation domain) {
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

    public Annotation() {
        dataAnnotValue = new annotValue_impl(this);
        dataAnnotProperty = new annotProperty_impl(this);
        dataAnnotations = new annotations_impl(this);
    }
    
}
