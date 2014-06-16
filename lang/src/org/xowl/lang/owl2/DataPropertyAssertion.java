/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class DataPropertyAssertion implements org.xowl.lang.owl2.IndividualAxiom, org.xowl.lang.owl2.ClassAssertion_OR_DataPropertyAssertion_OR_IndividualElement_OR_NegativeDataPropertyAssertion_OR_Ne3, org.xowl.lang.owl2.DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp2, org.xowl.lang.owl2.DataPropertyAssertion_OR_NegativeDataPropertyAssertion, org.xowl.lang.owl2.Axiom, org.xowl.lang.owl2.Annotation_OR_Axiom_OR_Ontology, org.xowl.lang.owl2.Axiom_OR_Statement {
    // <editor-fold defaultstate="collapsed" desc="Property line">
    private static class line_impl implements org.xowl.lang.owl2.Axiom_OR_Statement.line {
        private org.xowl.lang.owl2.DataPropertyAssertion domain;
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
        public line_impl(org.xowl.lang.owl2.DataPropertyAssertion domain) {
            this.domain = domain;
        }
    }
    private line_impl dataLine;
    public org.xowl.lang.owl2.Axiom_OR_Statement.line __getImplOfline() { return dataLine; }
    public boolean setLine(java.lang.Integer elem) {
        dataLine.simple_add(elem);
        return true;
    }
    public java.lang.Integer getLine() { return dataLine.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property valueLiteral">
    private static class valueLiteral_impl implements org.xowl.lang.owl2.DataPropertyAssertion_OR_NegativeDataPropertyAssertion.valueLiteral {
        private org.xowl.lang.owl2.DataPropertyAssertion domain;
        private org.xowl.lang.owl2.LiteralExpression data;
        public org.xowl.lang.owl2.LiteralExpression get_raw() { return data; }
        public org.xowl.lang.owl2.LiteralExpression get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.LiteralExpression elem) { return (data == elem); }
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
            data = elem;
        }
        public void simple_remove(org.xowl.lang.owl2.LiteralExpression elem) {
            data = null;
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
        public valueLiteral_impl(org.xowl.lang.owl2.DataPropertyAssertion domain) {
            this.domain = domain;
        }
    }
    private valueLiteral_impl dataValueLiteral;
    public org.xowl.lang.owl2.DataPropertyAssertion_OR_NegativeDataPropertyAssertion.valueLiteral __getImplOfvalueLiteral() { return dataValueLiteral; }
    public boolean setValueLiteral(org.xowl.lang.owl2.LiteralExpression elem) {
        if (dataValueLiteral.get() != null) {
            if (elem == null) {
                if (!dataValueLiteral.user_check_remove(dataValueLiteral.get())) return false;
                dataValueLiteral.user_remove(dataValueLiteral.get());
            } else {
                if (!dataValueLiteral.user_check_replace(dataValueLiteral.get(), elem)) return false;
                dataValueLiteral.user_remove(dataValueLiteral.get());
                dataValueLiteral.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataValueLiteral.user_check_add(elem)) return false;
            dataValueLiteral.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.LiteralExpression getValueLiteral() { return dataValueLiteral.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property annotations">
    private static class annotations_impl implements org.xowl.lang.owl2.Annotation_OR_Axiom_OR_Ontology.annotations {
        private org.xowl.lang.owl2.DataPropertyAssertion domain;
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
        public annotations_impl(org.xowl.lang.owl2.DataPropertyAssertion domain) {
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

    // <editor-fold defaultstate="collapsed" desc="Property individual">
    private static class individual_impl implements org.xowl.lang.owl2.ClassAssertion_OR_DataPropertyAssertion_OR_IndividualElement_OR_NegativeDataPropertyAssertion_OR_Ne3.individual {
        private org.xowl.lang.owl2.DataPropertyAssertion domain;
        private org.xowl.lang.owl2.IndividualExpression data;
        public org.xowl.lang.owl2.IndividualExpression get_raw() { return data; }
        public org.xowl.lang.owl2.IndividualExpression get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.lang.owl2.IndividualExpression elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.lang.owl2.IndividualExpression elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.owl2.IndividualExpression elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.owl2.IndividualExpression oldElem, org.xowl.lang.owl2.IndividualExpression  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.owl2.IndividualExpression elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.lang.owl2.IndividualExpression elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.lang.owl2.IndividualExpression elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.owl2.IndividualExpression elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.owl2.IndividualExpression oldElem, org.xowl.lang.owl2.IndividualExpression  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.owl2.IndividualExpression elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.owl2.IndividualExpression elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.owl2.IndividualExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.owl2.IndividualExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.owl2.IndividualExpression oldElem, org.xowl.lang.owl2.IndividualExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.owl2.IndividualExpression elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.owl2.IndividualExpression elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.owl2.IndividualExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.owl2.IndividualExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.owl2.IndividualExpression oldElem, org.xowl.lang.owl2.IndividualExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.owl2.IndividualExpression elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.owl2.IndividualExpression elem) {
            tree_remove(elem);
        }
        public individual_impl(org.xowl.lang.owl2.DataPropertyAssertion domain) {
            this.domain = domain;
        }
    }
    private individual_impl dataIndividual;
    public org.xowl.lang.owl2.ClassAssertion_OR_DataPropertyAssertion_OR_IndividualElement_OR_NegativeDataPropertyAssertion_OR_Ne3.individual __getImplOfindividual() { return dataIndividual; }
    public boolean setIndividual(org.xowl.lang.owl2.IndividualExpression elem) {
        if (dataIndividual.get() != null) {
            if (elem == null) {
                if (!dataIndividual.user_check_remove(dataIndividual.get())) return false;
                dataIndividual.user_remove(dataIndividual.get());
            } else {
                if (!dataIndividual.user_check_replace(dataIndividual.get(), elem)) return false;
                dataIndividual.user_remove(dataIndividual.get());
                dataIndividual.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataIndividual.user_check_add(elem)) return false;
            dataIndividual.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.IndividualExpression getIndividual() { return dataIndividual.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property file">
    private static class file_impl implements org.xowl.lang.owl2.Axiom_OR_Statement.file {
        private org.xowl.lang.owl2.DataPropertyAssertion domain;
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
        public file_impl(org.xowl.lang.owl2.DataPropertyAssertion domain) {
            this.domain = domain;
        }
    }
    private file_impl dataFile;
    public org.xowl.lang.owl2.Axiom_OR_Statement.file __getImplOffile() { return dataFile; }
    public boolean setFile(java.lang.String elem) {
        dataFile.simple_add(elem);
        return true;
    }
    public java.lang.String getFile() { return dataFile.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property dataProperty">
    private static class dataProperty_impl implements org.xowl.lang.owl2.DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp2.dataProperty {
        private org.xowl.lang.owl2.DataPropertyAssertion domain;
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
        public dataProperty_impl(org.xowl.lang.owl2.DataPropertyAssertion domain) {
            this.domain = domain;
        }
    }
    private dataProperty_impl dataDataProperty;
    public org.xowl.lang.owl2.DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp2.dataProperty __getImplOfdataProperty() { return dataDataProperty; }
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

    public DataPropertyAssertion() {
        dataLine = new line_impl(this);
        dataValueLiteral = new valueLiteral_impl(this);
        dataAnnotations = new annotations_impl(this);
        dataIndividual = new individual_impl(this);
        dataFile = new file_impl(this);
        dataDataProperty = new dataProperty_impl(this);
    }
    
}
