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

import org.xowl.store.cache.StringStore;

/**
 * Represents a node associated to a literal value in a RDF graph
 *
 * @author Laurent Wouters
 */
class LiteralNodeImpl extends LiteralNode {
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
    public LiteralNodeImpl(StringStore store, int lexical, int type, int tag) {
        this.store = store;
        this.lexical = lexical;
        this.type = type;
        this.tag = tag;
    }

    @Override
    public String getLexicalValue() {
        return store.retrieve(lexical);
    }

    @Override
    public String getDatatype() {
        return store.retrieve(type);
    }

    @Override
    public String getLangTag() {
        return store.retrieve(tag);
    }

    @Override
    public int hashCode() {
        return lexical ^ type ^ tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LiteralNodeImpl) {
            LiteralNodeImpl node = (LiteralNodeImpl) obj;
            return (this.lexical == node.lexical && this.type == node.type && this.tag == node.tag);
        }
        return false;
    }
}
