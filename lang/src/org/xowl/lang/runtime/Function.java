/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.runtime;

public class Function implements org.xowl.lang.runtime.Interpretation {
    // <editor-fold defaultstate="collapsed" desc="Property interpretationOf">
    private static class interpretationOf_impl implements org.xowl.lang.runtime.Interpretation.interpretationOf {
        private org.xowl.lang.runtime.Function domain;
        private org.xowl.lang.runtime.Entity data;
        public org.xowl.lang.runtime.Entity get_raw() { return data; }
        public org.xowl.lang.runtime.Entity get() { return data; }
        private boolean check_card(int modifier) {
            int card = modifier + 0;
            if (data != null) card++;
            return (card >= 0 && card <= 1);
        }
        @Override public boolean check_contains(org.xowl.lang.runtime.Entity elem) { return (data == elem); }
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
            data = elem;
        }
        public void simple_remove(org.xowl.lang.runtime.Entity elem) {
            data = null;
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
            if (!elem.__getImplOfinterpretedAs().inverse_check_add(domain)) return false;
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.runtime.Entity elem) {
            if (!elem.__getImplOfinterpretedAs().inverse_check_remove(domain)) return false;
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.runtime.Entity oldElem, org.xowl.lang.runtime.Entity  newElem) {
            if (!oldElem.__getImplOfinterpretedAs().inverse_check_remove(domain)) return false;
            if (!newElem.__getImplOfinterpretedAs().inverse_check_add(domain)) return false;
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.runtime.Entity elem) {
            elem.__getImplOfinterpretedAs().inverse_add(domain);
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.runtime.Entity elem) {
            elem.__getImplOfinterpretedAs().inverse_remove(domain);
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
        public interpretationOf_impl(org.xowl.lang.runtime.Function domain) {
            this.domain = domain;
        }
    }
    private interpretationOf_impl dataInterpretationOf;
    public org.xowl.lang.runtime.Interpretation.interpretationOf __getImplOfinterpretationOf() { return dataInterpretationOf; }
    public boolean setInterpretationOf(org.xowl.lang.runtime.Entity elem) {
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
    public org.xowl.lang.runtime.Entity getInterpretationOf() { return dataInterpretationOf.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property definedAs">
    public static interface definedAs {
        boolean check_contains(org.xowl.lang.runtime.Closure elem);
        boolean user_check_add(org.xowl.lang.runtime.Closure elem);
        boolean user_check_remove(org.xowl.lang.runtime.Closure elem);
        boolean user_check_replace(org.xowl.lang.runtime.Closure oldElem, org.xowl.lang.runtime.Closure  newElem);
        void user_add(org.xowl.lang.runtime.Closure elem);
        void user_remove(org.xowl.lang.runtime.Closure elem);
        boolean inverse_check_add(org.xowl.lang.runtime.Closure elem);
        boolean inverse_check_remove(org.xowl.lang.runtime.Closure elem);
        boolean inverse_check_replace(org.xowl.lang.runtime.Closure oldElem, org.xowl.lang.runtime.Closure  newElem);
        void inverse_add(org.xowl.lang.runtime.Closure elem);
        void inverse_remove(org.xowl.lang.runtime.Closure elem);
    }
    private static class definedAs_impl implements org.xowl.lang.runtime.Function.definedAs {
        private org.xowl.lang.runtime.Function domain;
        private java.util.List<org.xowl.lang.runtime.Closure> data;
        public java.util.Collection<org.xowl.lang.runtime.Closure> get_raw() { return new java.util.ArrayList<org.xowl.lang.runtime.Closure>(data); }
        public java.util.Collection<org.xowl.lang.runtime.Closure> get() { return new java.util.ArrayList<org.xowl.lang.runtime.Closure>(data); }
        private boolean check_card(int modifier) {
            int card = data.size() + 0 + modifier;
            return (card >= 0 && card <= 2147483647);
        }
        @Override public boolean check_contains(org.xowl.lang.runtime.Closure elem) { return (data.contains(elem)); }
        public boolean simple_check_add(org.xowl.lang.runtime.Closure elem) {
            if (check_contains(elem)) return false;
            if (!check_card(1)) return false;
            return true;
        }
        public boolean simple_check_remove(org.xowl.lang.runtime.Closure elem) {
            if (!check_contains(elem)) return false;
            if (!check_card(-1)) return false;
            return true;
        }
        public boolean simple_check_replace(org.xowl.lang.runtime.Closure oldElem, org.xowl.lang.runtime.Closure  newElem) {
            if (check_contains(newElem)) return false;
            if (!check_contains(oldElem)) return false;
            return true;
        }
        public void simple_add(org.xowl.lang.runtime.Closure elem) {
            data.add(elem);
        }
        public void simple_remove(org.xowl.lang.runtime.Closure elem) {
            data.remove(elem);
        }
        private boolean tree_check_add(org.xowl.lang.runtime.Closure elem) {
            if (!simple_check_add(elem)) return false;
            return true;
        }
        private boolean tree_check_remove(org.xowl.lang.runtime.Closure elem) {
            if (!simple_check_remove(elem)) return false;
            return true;
        }
        private boolean tree_check_replace(org.xowl.lang.runtime.Closure oldElem, org.xowl.lang.runtime.Closure  newElem) {
            if (!simple_check_replace(oldElem, newElem)) return false;
            return true;
        }
        private void tree_add(org.xowl.lang.runtime.Closure elem) {
            simple_add(elem);
        }
        private void tree_remove(org.xowl.lang.runtime.Closure elem) {
            simple_remove(elem);
        }
        @Override public boolean user_check_add(org.xowl.lang.runtime.Closure elem) {
            return tree_check_add(elem);
        }
        @Override public boolean user_check_remove(org.xowl.lang.runtime.Closure elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean user_check_replace(org.xowl.lang.runtime.Closure oldElem, org.xowl.lang.runtime.Closure  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void user_add(org.xowl.lang.runtime.Closure elem) {
            tree_add(elem);
        }
        @Override public void user_remove(org.xowl.lang.runtime.Closure elem) {
            tree_remove(elem);
        }
        @Override public boolean inverse_check_add(org.xowl.lang.runtime.Closure elem) {
            return tree_check_add(elem);
        }
        @Override public boolean inverse_check_remove(org.xowl.lang.runtime.Closure elem) {
            return tree_check_remove(elem);
        }
        @Override public boolean inverse_check_replace(org.xowl.lang.runtime.Closure oldElem, org.xowl.lang.runtime.Closure  newElem) {
            return tree_check_replace(oldElem, newElem);
        }
        @Override public void inverse_add(org.xowl.lang.runtime.Closure elem) {
            tree_add(elem);
        }
        @Override public void inverse_remove(org.xowl.lang.runtime.Closure elem) {
            tree_remove(elem);
        }
        public definedAs_impl(org.xowl.lang.runtime.Function domain) {
            this.domain = domain;
            this.data = new java.util.ArrayList<org.xowl.lang.runtime.Closure>();
        }
    }
    private definedAs_impl dataDefinedAs;
    public org.xowl.lang.runtime.Function.definedAs __getImplOfdefinedAs() { return dataDefinedAs; }
    public boolean addDefinedAs(org.xowl.lang.runtime.Closure elem) {
        if (!dataDefinedAs.user_check_add(elem)) return false;
        dataDefinedAs.user_add(elem);
        return true;
    }
    public boolean removeDefinedAs(org.xowl.lang.runtime.Closure elem) {
        if (!dataDefinedAs.user_check_remove(elem)) return false;
        dataDefinedAs.user_remove(elem);
        return true;
    }
    public java.util.Collection<org.xowl.lang.runtime.Closure> getAllDefinedAs() { return dataDefinedAs.get(); }
    // </editor-fold>

    public Function() {
        dataInterpretationOf = new interpretationOf_impl(this);
        dataDefinedAs = new definedAs_impl(this);
    }
    
}
