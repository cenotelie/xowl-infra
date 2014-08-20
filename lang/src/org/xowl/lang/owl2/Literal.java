/* This file has been generated by:
   xOWL Code Generator
*/
package org.xowl.lang.owl2;

public class Literal implements org.xowl.lang.owl2.AnnotationValue, org.xowl.lang.owl2.LiteralExpression, org.xowl.lang.owl2.Expression {
    // <editor-fold defaultstate="collapsed" desc="Property lexicalValue">
    public static interface lexicalValue {
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
    private static class lexicalValue_impl implements org.xowl.lang.owl2.Literal.lexicalValue {
        private org.xowl.lang.owl2.Literal domain;
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
        public lexicalValue_impl(org.xowl.lang.owl2.Literal domain) {
            this.domain = domain;
        }
    }
    private lexicalValue_impl dataLexicalValue;
    public org.xowl.lang.owl2.Literal.lexicalValue __getImplOflexicalValue() { return dataLexicalValue; }
    public boolean setLexicalValue(java.lang.String elem) {
        dataLexicalValue.simple_add(elem);
        return true;
    }
    public java.lang.String getLexicalValue() { return dataLexicalValue.get(); }
    // </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Property langTag">
    public static interface langTag {
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
    private static class langTag_impl implements org.xowl.lang.owl2.Literal.langTag {
        private org.xowl.lang.owl2.Literal domain;
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
        public langTag_impl(org.xowl.lang.owl2.Literal domain) {
            this.domain = domain;
        }
    }
    private langTag_impl dataLangTag;
    public org.xowl.lang.owl2.Literal.langTag __getImplOflangTag() { return dataLangTag; }
    public boolean setLangTag(java.lang.String elem) {
        dataLangTag.simple_add(elem);
        return true;
    }
    public java.lang.String getLangTag() { return dataLangTag.get(); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property memberOf">
    public static interface memberOf {
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
    private static class memberOf_impl implements org.xowl.lang.owl2.Literal.memberOf {
        private org.xowl.lang.owl2.Literal domain;
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
        public memberOf_impl(org.xowl.lang.owl2.Literal domain) {
            this.domain = domain;
        }
    }
    private memberOf_impl dataMemberOf;
    public org.xowl.lang.owl2.Literal.memberOf __getImplOfmemberOf() { return dataMemberOf; }
    public boolean setMemberOf(org.xowl.lang.owl2.IRI elem) {
        if (dataMemberOf.get() != null) {
            if (elem == null) {
                if (!dataMemberOf.user_check_remove(dataMemberOf.get())) return false;
                dataMemberOf.user_remove(dataMemberOf.get());
            } else {
                if (!dataMemberOf.user_check_replace(dataMemberOf.get(), elem)) return false;
                dataMemberOf.user_remove(dataMemberOf.get());
                dataMemberOf.user_add(elem);
            }
        } else {
            if (elem == null) return true;
            if (!dataMemberOf.user_check_add(elem)) return false;
            dataMemberOf.user_add(elem);
        }
        return true;
    }
    public org.xowl.lang.owl2.IRI getMemberOf() { return dataMemberOf.get(); }
    // </editor-fold>

    public Literal() {
        dataLexicalValue = new lexicalValue_impl(this);
        dataLangTag = new langTag_impl(this);
        dataMemberOf = new memberOf_impl(this);
    }
    
}
