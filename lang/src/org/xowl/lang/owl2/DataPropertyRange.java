/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class DataPropertyRange implements org.xowl.lang.owl2.DataPropertyAxiom, org.xowl.lang.owl2.DataCardinalityRestriction_OR_DataComplementOf_OR_DataPropertyRange_OR_DatarangeElement_OR_Datatype3, org.xowl.lang.owl2.DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp5 {
    // <editor-fold defaultstate="collapsed" desc="Property datarange">
    private static class datarange_impl implements org.xowl.lang.owl2.DataCardinalityRestriction_OR_DataComplementOf_OR_DataPropertyRange_OR_DatarangeElement_OR_Datatype3.datarange {
        private org.xowl.lang.owl2.DataPropertyRange domain;
        private org.xowl.lang.owl2.Datarange data;
        public org.xowl.lang.owl2.Datarange get_raw() { return data; }
        public org.xowl.lang.owl2.Datarange get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.Datarange elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.lang.owl2.Datarange elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.Datarange elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.Datarange oldElem, org.xowl.lang.owl2.Datarange  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.Datarange elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.lang.owl2.Datarange elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.lang.owl2.Datarange elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.Datarange elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.Datarange oldElem, org.xowl.lang.owl2.Datarange  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.Datarange elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.Datarange elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.Datarange elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.Datarange elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.Datarange oldElem, org.xowl.lang.owl2.Datarange  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.Datarange elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.Datarange elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.Datarange elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.Datarange elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.Datarange oldElem, org.xowl.lang.owl2.Datarange  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.Datarange elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.Datarange elem) {
            tree_remove(elem);
        }
        public datarange_impl(org.xowl.lang.owl2.DataPropertyRange domain) {
            this.domain = domain;
        }
    }
    private datarange_impl dataDatarange;
    public org.xowl.lang.owl2.DataCardinalityRestriction_OR_DataComplementOf_OR_DataPropertyRange_OR_DatarangeElement_OR_Datatype3.datarange __getImplOfdatarange() { return dataDatarange; }
    public boolean setDatarange(org.xowl.lang.owl2.Datarange elem) {
        if (dataDatarange.get() != null) {
            if (elem == null) {
                if (!dataDatarange.user_check_remove(dataDatarange.get())) return false;
                dataDatarange.user_remove(dataDatarange.get());
            } else {
                if (!dataDatarange.user_check_replace(dataDatarange.get(), elem)) return false;
                dataDatarange.user_remove(dataDatarange.get());
                dataDatarange.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataDatarange.user_check_add(elem)) return false;
            dataDatarange.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.Datarange getDatarange() { return dataDatarange.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property dataProperty">
    private static class dataProperty_impl implements org.xowl.lang.owl2.DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp5.dataProperty {
        private org.xowl.lang.owl2.DataPropertyRange domain;
        private org.xowl.lang.owl2.DataPropertyExpression data;
        public org.xowl.lang.owl2.DataPropertyExpression get_raw() { return data; }
        public org.xowl.lang.owl2.DataPropertyExpression get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.DataPropertyExpression elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.lang.owl2.DataPropertyExpression elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.DataPropertyExpression elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.DataPropertyExpression oldElem, org.xowl.lang.owl2.DataPropertyExpression  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.DataPropertyExpression elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.lang.owl2.DataPropertyExpression elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.lang.owl2.DataPropertyExpression elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.DataPropertyExpression elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.DataPropertyExpression oldElem, org.xowl.lang.owl2.DataPropertyExpression  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.DataPropertyExpression elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.DataPropertyExpression elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.DataPropertyExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.DataPropertyExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.DataPropertyExpression oldElem, org.xowl.lang.owl2.DataPropertyExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.DataPropertyExpression elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.DataPropertyExpression elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.DataPropertyExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.DataPropertyExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.DataPropertyExpression oldElem, org.xowl.lang.owl2.DataPropertyExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.DataPropertyExpression elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.DataPropertyExpression elem) {
            tree_remove(elem);
        }
        public dataProperty_impl(org.xowl.lang.owl2.DataPropertyRange domain) {
            this.domain = domain;
        }
    }
    private dataProperty_impl dataDataProperty;
    public org.xowl.lang.owl2.DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp5.dataProperty __getImplOfdataProperty() { return dataDataProperty; }
    public boolean setDataProperty(org.xowl.lang.owl2.DataPropertyExpression elem) {
        if (dataDataProperty.get() != null) {
            if (elem == null) {
                if (!dataDataProperty.user_check_remove(dataDataProperty.get())) return false;
                dataDataProperty.user_remove(dataDataProperty.get());
            } else {
                if (!dataDataProperty.user_check_replace(dataDataProperty.get(), elem)) return false;
                dataDataProperty.user_remove(dataDataProperty.get());
                dataDataProperty.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataDataProperty.user_check_add(elem)) return false;
            dataDataProperty.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.DataPropertyExpression getDataProperty() { return dataDataProperty.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property file">
    private static class file_impl implements org.xowl.lang.owl2.Axiom.file {
        private org.xowl.lang.owl2.DataPropertyRange domain;
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
        public file_impl(org.xowl.lang.owl2.DataPropertyRange domain) {
            this.domain = domain;
        }
    }
    private file_impl dataFile;
    public org.xowl.lang.owl2.Axiom.file __getImplOffile() { return dataFile; }
    public boolean setFile(java.lang.String elem) {
        dataFile.simple_add(elem);
        return true;
    }
    public java.lang.String getFile() { return dataFile.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property line">
    private static class line_impl implements org.xowl.lang.owl2.Axiom.line {
        private org.xowl.lang.owl2.DataPropertyRange domain;
        private java.lang.Integer data;
        public java.lang.Integer get_raw() { return data; }
        public java.lang.Integer get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(java.lang.Integer elem) { return data.equals(elem); }
        public boolean simple_check_add(java.lang.Integer elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(java.lang.Integer elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(java.lang.Integer oldElem, java.lang.Integer  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(java.lang.Integer elem) {
            data = elem;
        }
        public void simple_remove(java.lang.Integer elem) {
            data = null;
        }
        private boolean tree_check_add(java.lang.Integer elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(java.lang.Integer elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(java.lang.Integer oldElem, java.lang.Integer  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(java.lang.Integer elem) {
            simple_add(elem);
        }
        private void tree_remove(java.lang.Integer elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(java.lang.Integer elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(java.lang.Integer elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(java.lang.Integer oldElem, java.lang.Integer  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(java.lang.Integer elem) {
            tree_add(elem);
        }
        @Override public void user_remove(java.lang.Integer elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(java.lang.Integer elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(java.lang.Integer elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(java.lang.Integer oldElem, java.lang.Integer  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(java.lang.Integer elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(java.lang.Integer elem) {
            tree_remove(elem);
        }
        public line_impl(org.xowl.lang.owl2.DataPropertyRange domain) {
            this.domain = domain;
        }
    }
    private line_impl dataLine;
    public org.xowl.lang.owl2.Axiom.line __getImplOfline() { return dataLine; }
    public boolean setLine(java.lang.Integer elem) {
        dataLine.simple_add(elem);
        return true;
    }
    public java.lang.Integer getLine() { return dataLine.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property annotations">
    private static class annotations_impl implements org.xowl.lang.owl2.Annotation_OR_Axiom_OR_Ontology.annotations {
        private org.xowl.lang.owl2.DataPropertyRange domain;
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
        public annotations_impl(org.xowl.lang.owl2.DataPropertyRange domain) {
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

    public DataPropertyRange() {
        dataDatarange = new datarange_impl(this);
        dataDataProperty = new dataProperty_impl(this);
        dataFile = new file_impl(this);
        dataLine = new line_impl(this);
        dataAnnotations = new annotations_impl(this);
    }

}
