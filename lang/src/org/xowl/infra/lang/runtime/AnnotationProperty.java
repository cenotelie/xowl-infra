/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.infra.lang.runtime;

public class AnnotationProperty implements org.xowl.infra.lang.runtime.Interpretation {
    // <editor-fold defaultstate="collapsed" desc="Property superAnnotProperty">
    public static interface superAnnotProperty {
        boolean check_contains(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean user_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean user_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean user_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem);
        void user_add(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        void user_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean inverse_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean inverse_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean inverse_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem);
        void inverse_add(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        void inverse_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem);
    }
    private static class superAnnotProperty_impl implements org.xowl.infra.lang.runtime.AnnotationProperty.superAnnotProperty {
        private org.xowl.infra.lang.runtime.AnnotationProperty domain;
        private java.util.List<org.xowl.infra.lang.runtime.AnnotationProperty> data;
        public java.util.Collection<org.xowl.infra.lang.runtime.AnnotationProperty> get_raw() { return new java.util.ArrayList<org.xowl.infra.lang.runtime.AnnotationProperty>(data); }
        public java.util.Collection<org.xowl.infra.lang.runtime.AnnotationProperty> get() { return new java.util.ArrayList<org.xowl.infra.lang.runtime.AnnotationProperty>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.infra.lang.runtime.AnnotationProperty elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!elem.__getImplOfsubAnnotProperty().inverse_check_add(domain)) return false;
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!elem.__getImplOfsubAnnotProperty().inverse_check_remove(domain)) return false;
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem) {
            if (!oldElem.__getImplOfsubAnnotProperty().inverse_check_remove(domain)) return false;
            if (!newElem.__getImplOfsubAnnotProperty().inverse_check_add(domain)) return false;
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            elem.__getImplOfsubAnnotProperty().inverse_add(domain);
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            elem.__getImplOfsubAnnotProperty().inverse_remove(domain);
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            tree_remove(elem);
        }
        public superAnnotProperty_impl(org.xowl.infra.lang.runtime.AnnotationProperty domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.infra.lang.runtime.AnnotationProperty>();
        }
    }
    private superAnnotProperty_impl dataSuperAnnotProperty;
    public org.xowl.infra.lang.runtime.AnnotationProperty.superAnnotProperty __getImplOfsuperAnnotProperty() { return dataSuperAnnotProperty; }
    public boolean addSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        if (!dataSuperAnnotProperty.user_check_add(elem)) return false;
        dataSuperAnnotProperty.user_add(elem);
        return true;
    }
    public boolean removeSuperAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        if (!dataSuperAnnotProperty.user_check_remove(elem)) return false;
        dataSuperAnnotProperty.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.infra.lang.runtime.AnnotationProperty> getAllSuperAnnotProperty() { return dataSuperAnnotProperty.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property interpretationOf">
    private static class interpretationOf_impl implements org.xowl.infra.lang.runtime.Interpretation.interpretationOf {
        private org.xowl.infra.lang.runtime.AnnotationProperty domain;
        private org.xowl.infra.lang.runtime.Entity data;
        public org.xowl.infra.lang.runtime.Entity get_raw() { return data; }
        public org.xowl.infra.lang.runtime.Entity get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.infra.lang.runtime.Entity elem) { return (data == elem); }
        public boolean simple_check_add(org.xowl.infra.lang.runtime.Entity elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.infra.lang.runtime.Entity elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.infra.lang.runtime.Entity oldElem, org.xowl.infra.lang.runtime.Entity  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.infra.lang.runtime.Entity elem) {
            data = elem;
        }
        public void simple_remove(org.xowl.infra.lang.runtime.Entity elem) {
            data = null;
        }
        private boolean tree_check_add(org.xowl.infra.lang.runtime.Entity elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.infra.lang.runtime.Entity elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.infra.lang.runtime.Entity oldElem, org.xowl.infra.lang.runtime.Entity  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.infra.lang.runtime.Entity elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.infra.lang.runtime.Entity elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.infra.lang.runtime.Entity elem) {
            if (!elem.__getImplOfinterpretedAs().inverse_check_add(domain)) return false;
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.infra.lang.runtime.Entity elem) {
            if (!elem.__getImplOfinterpretedAs().inverse_check_remove(domain)) return false;
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.infra.lang.runtime.Entity oldElem, org.xowl.infra.lang.runtime.Entity  newElem) {
            if (!oldElem.__getImplOfinterpretedAs().inverse_check_remove(domain)) return false;
            if (!newElem.__getImplOfinterpretedAs().inverse_check_add(domain)) return false;
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.infra.lang.runtime.Entity elem) {
            elem.__getImplOfinterpretedAs().inverse_add(domain);
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.infra.lang.runtime.Entity elem) {
            elem.__getImplOfinterpretedAs().inverse_remove(domain);
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.infra.lang.runtime.Entity elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.infra.lang.runtime.Entity elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.infra.lang.runtime.Entity oldElem, org.xowl.infra.lang.runtime.Entity  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.infra.lang.runtime.Entity elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.infra.lang.runtime.Entity elem) {
            tree_remove(elem);
        }
        public interpretationOf_impl(org.xowl.infra.lang.runtime.AnnotationProperty domain) {
            this.domain = domain;
        }
    }
    private interpretationOf_impl dataInterpretationOf;
    public org.xowl.infra.lang.runtime.Interpretation.interpretationOf __getImplOfinterpretationOf() { return dataInterpretationOf; }
    public boolean setInterpretationOf(org.xowl.infra.lang.runtime.Entity elem) {
        if (dataInterpretationOf.get() != null) {
            if (elem == null) {
                if (!dataInterpretationOf.user_check_remove(dataInterpretationOf.get())) return false;
                dataInterpretationOf.user_remove(dataInterpretationOf.get());
            } else {
                if (!dataInterpretationOf.user_check_replace(dataInterpretationOf.get(), elem)) return false;
                dataInterpretationOf.user_remove(dataInterpretationOf.get());
                dataInterpretationOf.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataInterpretationOf.user_check_add(elem)) return false;
            dataInterpretationOf.user_add(elem);
        }
        return true;
    }
    public org.xowl.infra.lang.runtime.Entity getInterpretationOf() { return dataInterpretationOf.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property subAnnotProperty">
    public static interface subAnnotProperty {
        boolean check_contains(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean user_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean user_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean user_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem);
        void user_add(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        void user_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean inverse_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean inverse_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        boolean inverse_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem);
        void inverse_add(org.xowl.infra.lang.runtime.AnnotationProperty elem);
        void inverse_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem);
    }
    private static class subAnnotProperty_impl implements org.xowl.infra.lang.runtime.AnnotationProperty.subAnnotProperty {
        private org.xowl.infra.lang.runtime.AnnotationProperty domain;
        private java.util.List<org.xowl.infra.lang.runtime.AnnotationProperty> data;
        public java.util.Collection<org.xowl.infra.lang.runtime.AnnotationProperty> get_raw() { return new java.util.ArrayList<org.xowl.infra.lang.runtime.AnnotationProperty>(data); }
        public java.util.Collection<org.xowl.infra.lang.runtime.AnnotationProperty> get() { return new java.util.ArrayList<org.xowl.infra.lang.runtime.AnnotationProperty>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.infra.lang.runtime.AnnotationProperty elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!elem.__getImplOfsuperAnnotProperty().inverse_check_add(domain)) return false;
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            if (!elem.__getImplOfsuperAnnotProperty().inverse_check_remove(domain)) return false;
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem) {
            if (!oldElem.__getImplOfsuperAnnotProperty().inverse_check_remove(domain)) return false;
            if (!newElem.__getImplOfsuperAnnotProperty().inverse_check_add(domain)) return false;
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            elem.__getImplOfsuperAnnotProperty().inverse_add(domain);
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            elem.__getImplOfsuperAnnotProperty().inverse_remove(domain);
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.infra.lang.runtime.AnnotationProperty oldElem, org.xowl.infra.lang.runtime.AnnotationProperty  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
            tree_remove(elem);
        }
        public subAnnotProperty_impl(org.xowl.infra.lang.runtime.AnnotationProperty domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.infra.lang.runtime.AnnotationProperty>();
        }
    }
    private subAnnotProperty_impl dataSubAnnotProperty;
    public org.xowl.infra.lang.runtime.AnnotationProperty.subAnnotProperty __getImplOfsubAnnotProperty() { return dataSubAnnotProperty; }
    public boolean addSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        if (!dataSubAnnotProperty.user_check_add(elem)) return false;
        dataSubAnnotProperty.user_add(elem);
        return true;
    }
    public boolean removeSubAnnotProperty(org.xowl.infra.lang.runtime.AnnotationProperty elem) {
        if (!dataSubAnnotProperty.user_check_remove(elem)) return false;
        dataSubAnnotProperty.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.infra.lang.runtime.AnnotationProperty> getAllSubAnnotProperty() { return dataSubAnnotProperty.get(); }
    // </editor-fold>

    public AnnotationProperty() {
        dataSuperAnnotProperty = new superAnnotProperty_impl(this);
        dataInterpretationOf = new interpretationOf_impl(this);
        dataSubAnnotProperty = new subAnnotProperty_impl(this);
    }

}
