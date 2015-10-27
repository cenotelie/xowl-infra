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

package org.xowl.store.storage.persistent;

import org.xowl.store.rdf.LiteralNode;

import java.io.IOException;
import java.util.Objects;

/**
 * Persisted implementation of a literal node
 *
 * @author Laurent Wouters
 */
class PersistedLiteralNode extends LiteralNode implements PersistedNode {
    /**
     * The backend persisting the strings
     */
    private final StringStoreBackend backend;
    /**
     * The key for the lexical value of this literal
     */
    private final long keyLexical;
    /**
     * The key for the IRI of the datatype of this literal
     */
    private final long keyDatatype;
    /**
     * The key for the language tag of this literal
     */
    private final long keyLangTag;
    /**
     * The cached lexical value of this literal
     */
    private String lexical;
    /**
     * The cached IRI of the datatype of this literal
     */
    private String datatype;
    /**
     * The cached language tag of this literal
     */
    private String langTag;

    /**
     * Initializes this node
     *
     * @param backend     The backend persisting the strings
     * @param keyLexical  The key for the lexical value of this literal
     * @param keyDatatype The key for the IRI of the datatype of this literal
     * @param keyLangTag  The key for the language tag of this literal
     */
    public PersistedLiteralNode(StringStoreBackend backend, long keyLexical, long keyDatatype, long keyLangTag) {
        this.backend = backend;
        this.keyLexical = keyLexical;
        this.keyDatatype = keyDatatype;
        this.keyLangTag = keyLangTag;
    }

    @Override
    public String getLexicalValue() {
        if (lexical == null) {
            try {
                lexical = backend.read(keyLexical);
            } catch (IOException exception) {
                lexical = "#error#";
            }
        }
        return lexical;
    }

    @Override
    public String getDatatype() {
        if (datatype == null && keyDatatype != StringStoreBackend.KEY_NOT_PRESENT) {
            try {
                datatype = backend.read(keyDatatype);
            } catch (IOException exception) {
                datatype = "#error#";
            }
        }
        return datatype;
    }

    @Override
    public String getLangTag() {
        if (langTag == null && keyLangTag != StringStoreBackend.KEY_NOT_PRESENT) {
            try {
                langTag = backend.read(keyLangTag);
            } catch (IOException exception) {
                langTag = "#error#";
            }
        }
        return langTag;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersistedLiteralNode) {
            PersistedLiteralNode node = (PersistedLiteralNode) o;
            return (node.backend == backend && node.keyLexical == keyLexical && node.keyDatatype == keyDatatype && node.keyLangTag == keyLangTag);
        }
        if (o instanceof LiteralNode) {
            LiteralNode node = (LiteralNode) o;
            return (Objects.equals(getLexicalValue(), node.getLexicalValue())
                    && Objects.equals(getDatatype(), node.getDatatype())
                    && Objects.equals(getLangTag(), node.getLangTag()));
        }
        return false;
    }
}
