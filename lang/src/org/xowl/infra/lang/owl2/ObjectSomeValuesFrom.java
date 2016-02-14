/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.infra.lang.owl2;

public class ObjectSomeValuesFrom implements org.xowl.infra.lang.owl2.ObjectPropertyRestriction, org.xowl.infra.lang.owl2.ClassAssertion_OR_ClassElement_OR_DataPropertyDomain_OR_DisjointUnion_OR_HasKey_OR_ObjectAllValuesF2 {
    // <editor-fold defaultstate="collapsed" desc="Property classe">
    private static class classe_impl implements org.xowl.infra.lang.owl2.ClassAssertion_OR_ClassElement_OR_DataPropertyDomain_OR_DisjointUnion_OR_HasKey_OR_ObjectAllValuesF2.classe {
        private org.xowl.infra.lang.owl2.ObjectSomeValuesFrom domain;
        private org.xowl.infra.lang.owl2.ClassExpression data;
        public org.xowl.infra.lang.owl2.ClassExpression get_raw() { return data; }
        public org.xowl.infra.lang.owl2.ClassExpression get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.infra.lang.owl2.ClassExpression elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.infra.lang.owl2.ClassExpression elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.infra.lang.owl2.ClassExpression elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.infra.lang.owl2.ClassExpression oldElem, org.xowl.infra.lang.owl2.ClassExpression  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.infra.lang.owl2.ClassExpression elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.infra.lang.owl2.ClassExpression elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.infra.lang.owl2.ClassExpression elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.infra.lang.owl2.ClassExpression elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.infra.lang.owl2.ClassExpression oldElem, org.xowl.infra.lang.owl2.ClassExpression  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.infra.lang.owl2.ClassExpression elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.infra.lang.owl2.ClassExpression elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.infra.lang.owl2.ClassExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.infra.lang.owl2.ClassExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.infra.lang.owl2.ClassExpression oldElem, org.xowl.infra.lang.owl2.ClassExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.infra.lang.owl2.ClassExpression elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.infra.lang.owl2.ClassExpression elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.infra.lang.owl2.ClassExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.infra.lang.owl2.ClassExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.infra.lang.owl2.ClassExpression oldElem, org.xowl.infra.lang.owl2.ClassExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.infra.lang.owl2.ClassExpression elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.infra.lang.owl2.ClassExpression elem) {
            tree_remove(elem);
        }
        public classe_impl(org.xowl.infra.lang.owl2.ObjectSomeValuesFrom domain) {
            this.domain = domain;
        }
    }
    private classe_impl dataClasse;
    public org.xowl.infra.lang.owl2.ClassAssertion_OR_ClassElement_OR_DataPropertyDomain_OR_DisjointUnion_OR_HasKey_OR_ObjectAllValuesF2.classe __getImplOfclasse() { return dataClasse; }
    public boolean setClasse(org.xowl.infra.lang.owl2.ClassExpression elem) {
        if (dataClasse.get() != null) {
            if (elem == null) {
                if (!dataClasse.user_check_remove(dataClasse.get())) return false;
                dataClasse.user_remove(dataClasse.get());
            } else {
                if (!dataClasse.user_check_replace(dataClasse.get(), elem)) return false;
                dataClasse.user_remove(dataClasse.get());
                dataClasse.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataClasse.user_check_add(elem)) return false;
            dataClasse.user_add(elem);
        }
        return true;
    }
    public org.xowl.infra.lang.owl2.ClassExpression getClasse() { return dataClasse.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property objectProperty">
    private static class objectProperty_impl implements org.xowl.infra.lang.owl2.AsymmetricObjectProperty_OR_FunctionalObjectProperty_OR_InverseFunctionalObjectProperty_OR_InverseO0.objectProperty {
        private org.xowl.infra.lang.owl2.ObjectSomeValuesFrom domain;
        private org.xowl.infra.lang.owl2.ObjectPropertyExpression data;
        public org.xowl.infra.lang.owl2.ObjectPropertyExpression get_raw() { return data; }
        public org.xowl.infra.lang.owl2.ObjectPropertyExpression get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.infra.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.infra.lang.owl2.ObjectPropertyExpression  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.infra.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.infra.lang.owl2.ObjectPropertyExpression  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.infra.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.infra.lang.owl2.ObjectPropertyExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.infra.lang.owl2.ObjectPropertyExpression oldElem, org.xowl.infra.lang.owl2.ObjectPropertyExpression  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
            tree_remove(elem);
        }
        public objectProperty_impl(org.xowl.infra.lang.owl2.ObjectSomeValuesFrom domain) {
            this.domain = domain;
        }
    }
    private objectProperty_impl dataObjectProperty;
    public org.xowl.infra.lang.owl2.AsymmetricObjectProperty_OR_FunctionalObjectProperty_OR_InverseFunctionalObjectProperty_OR_InverseO0.objectProperty __getImplOfobjectProperty() { return dataObjectProperty; }
    public boolean setObjectProperty(org.xowl.infra.lang.owl2.ObjectPropertyExpression elem) {
        if (dataObjectProperty.get() != null) {
            if (elem == null) {
                if (!dataObjectProperty.user_check_remove(dataObjectProperty.get())) return false;
                dataObjectProperty.user_remove(dataObjectProperty.get());
            } else {
                if (!dataObjectProperty.user_check_replace(dataObjectProperty.get(), elem)) return false;
                dataObjectProperty.user_remove(dataObjectProperty.get());
                dataObjectProperty.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataObjectProperty.user_check_add(elem)) return false;
            dataObjectProperty.user_add(elem);
        }
        return true;
    }
    public org.xowl.infra.lang.owl2.ObjectPropertyExpression getObjectProperty() { return dataObjectProperty.get(); }
    // </editor-fold>

    public ObjectSomeValuesFrom() {
        dataClasse = new classe_impl(this);
        dataObjectProperty = new objectProperty_impl(this);
    }

}