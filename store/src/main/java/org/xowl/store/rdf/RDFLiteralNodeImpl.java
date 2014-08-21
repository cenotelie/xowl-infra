/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/

package org.xowl.store.rdf;

import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Literal;
import org.xowl.store.cache.StringStore;

/**
 * Represents a node associated to a literal value in a RDF graph
 *
 * @author Laurent Wouters
 */
class RDFLiteralNodeImpl extends RDFLiteralNode {
    /**
     * The string store
     */
    private StringStore store;
    /**
     * Key to the lexical value
     */
    private int lexical;
    /**
     * Key to the datatype IRI
     */
    private int type;
    /**
     * Key to the language tag
     */
    private int tag;

    /**
     * Initializes this node
     *
     * @param store   The string store
     * @param lexical Key to the lexical value
     * @param type    Key to the datatype IRI
     * @param tag     Key to the language tag
     */
    public RDFLiteralNodeImpl(StringStore store, int lexical, int type, int tag) {
        this.store = store;
        this.lexical = lexical;
        this.type = type;
        this.tag = tag;
    }

    @Override
    public Literal getLiteralValue() {
        IRI datatype = new IRI();
        datatype.setHasValue(store.retrieve(type));
        Literal literal = new Literal();
        literal.setMemberOf(datatype);
        literal.setLexicalValue(store.retrieve(lexical));
        if (tag != -1) {
            literal.setLangTag(store.retrieve(tag));
        }
        return literal;
    }

    @Override
    public int hashCode() {
        return lexical ^ type ^ tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RDFLiteralNodeImpl) {
            RDFLiteralNodeImpl node = (RDFLiteralNodeImpl) obj;
            return (this.lexical == node.lexical && this.type == node.type && this.tag == node.tag);
        }
        return false;
    }
}
