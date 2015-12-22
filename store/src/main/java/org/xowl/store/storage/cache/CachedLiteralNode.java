/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 ******************************************************************************/

package org.xowl.store.storage.cache;

import org.xowl.store.rdf.LiteralNode;

import java.util.Objects;

/**
 * Cached implementation of a literal node
 *
 * @author Laurent Wouters
 */
class CachedLiteralNode extends LiteralNode {
    /**
     * The lexical value of this literal
     */
    private final String lexical;
    /**
     * The IRI of the datatype of this literal
     */
    private final String datatype;
    /**
     * The language tag of this literal
     */
    private final String langTag;

    /**
     * Initializes this node
     *
     * @param lexical  The lexical value of this literal
     * @param datatype The IRI of the datatype of this literal
     * @param langTag  The language tag of this literal
     */
    public CachedLiteralNode(String lexical, String datatype, String langTag) {
        this.lexical = lexical;
        this.datatype = datatype;
        this.langTag = langTag;
    }

    @Override
    public String getLexicalValue() {
        return lexical;
    }

    @Override
    public String getDatatype() {
        return datatype;
    }

    @Override
    public String getLangTag() {
        return langTag;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LiteralNode) {
            LiteralNode node = (LiteralNode) o;
            return (Objects.equals(lexical, node.getLexicalValue())
                    && Objects.equals(datatype, node.getDatatype())
                    && Objects.equals(langTag, node.getLangTag()));
        }
        return false;
    }
}
