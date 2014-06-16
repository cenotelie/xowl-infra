/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class DataMinCardinality implements org.xowl.lang.owl2.DataCardinalityRestriction, org.xowl.lang.owl2.CardinalityRestriction, org.xowl.lang.owl2.DataPropertyRestriction, org.xowl.lang.owl2.DataCardinalityRestriction_OR_DataComplementOf_OR_DataPropertyRange_OR_DatarangeElement_OR_Datatype4, org.xowl.lang.owl2.ClassRestriction, org.xowl.lang.owl2.DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp2, org.xowl.lang.owl2.ClassExpression, org.xowl.lang.owl2.Expression {
    // <editor-fold defaultstate="collapsed" desc="Property cardinality">
    private static class cardinality_impl implements org.xowl.lang.owl2.CardinalityRestriction.cardinality {
        private org.xowl.lang.owl2.DataMinCardinality domain;
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
        public cardinality_impl(org.xowl.lang.owl2.DataMinCardinality domain) {
            this.domain = domain;
        }
    }
    private cardinality_impl dataCardinality;
    public org.xowl.lang.owl2.CardinalityRestriction.cardinality __getImplOfcardinality() { return dataCardinality; }
    public boolean setCardinality(org.xowl.lang.owl2.LiteralExpression elem) {
        if (dataCardinality.get() != null) {
            if (elem == null) {
                if (!dataCardinality.user_check_remove(dataCardinality.get())) return false;
                dataCardinality.user_remove(dataCardinality.get());
            } else {
                if (!dataCardinality.user_check_replace(dataCardinality.get(), elem)) return false;
                dataCardinality.user_remove(dataCardinality.get());
                dataCardinality.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataCardinality.user_check_add(elem)) return false;
            dataCardinality.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.LiteralExpression getCardinality() { return dataCardinality.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property datarange">
    private static class datarange_impl implements org.xowl.lang.owl2.DataCardinalityRestriction_OR_DataComplementOf_OR_DataPropertyRange_OR_DatarangeElement_OR_Datatype4.datarange {
        private org.xowl.lang.owl2.DataMinCardinality domain;
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
        public datarange_impl(org.xowl.lang.owl2.DataMinCardinality domain) {
            this.domain = domain;
        }
    }
    private datarange_impl dataDatarange;
    public org.xowl.lang.owl2.DataCardinalityRestriction_OR_DataComplementOf_OR_DataPropertyRange_OR_DatarangeElement_OR_Datatype4.datarange __getImplOfdatarange() { return dataDatarange; }
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
    private static class dataProperty_impl implements org.xowl.lang.owl2.DataPropertyAssertion_OR_DataPropertyDomain_OR_DataPropertyElement_OR_DataPropertyRange_OR_DataProp2.dataProperty {
        private org.xowl.lang.owl2.DataMinCardinality domain;
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
        public dataProperty_impl(org.xowl.lang.owl2.DataMinCardinality domain) {
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

    public DataMinCardinality() {
        dataCardinality = new cardinality_impl(this);
        dataDatarange = new datarange_impl(this);
        dataDataProperty = new dataProperty_impl(this);
    }
    
}
