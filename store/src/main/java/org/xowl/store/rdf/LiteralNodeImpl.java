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
import org.xowl.utils.data.Attribute;
import org.xowl.utils.data.Dataset;

/**
 * Represents a node associated to a literal value in a RDF graph
 *
 * @author Laurent Wouters
 */
class LiteralNodeImpl extends LiteralNode {
    /**
     * The identifier key for the serialization of the lexical attribute
     */
    public static final String SERIALIZATION_LEXICAL = "lexical";
    /**
     * The identifier key for the serialization of the datatype attribute
     */
    public static final String SERIALIZATION_DATATYPE = "datatype";
    /**
     * The identifier key for the serialization of the tag attribute
     */
    public static final String SERIALIZATION_TAG = "tag";

    /**
     * The string store
     */
    private final StringStore store;
    /**
     * Key to the lexical value
     */
    private final int lexical;
    /**
     * Key to the datatype IRI
     */
    private final int datatype;
    /**
     * Key to the language tag
     */
    private final int tag;

    /**
     * Initializes this node
     *
     * @param store    The string store
     * @param lexical  Key to the lexical value
     * @param datatype Key to the datatype IRI
     * @param tag      Key to the language tag
     */
    public LiteralNodeImpl(StringStore store, int lexical, int datatype, int tag) {
        this.store = store;
        this.lexical = lexical;
        this.datatype = datatype;
        this.tag = tag;
    }

    /**
     * Initializes this node from a dataset
     *
     * @param store The string store storing the IRI value
     * @param data  The node of serialized data
     */
    public LiteralNodeImpl(StringStore store, org.xowl.utils.data.Node data) {
        this.store = store;
        this.lexical = (int) data.attribute(SERIALIZATION_LEXICAL).getValue();
        this.datatype = (int) data.attribute(SERIALIZATION_DATATYPE).getValue();
        this.tag = (int) data.attribute(SERIALIZATION_TAG).getValue();
    }

    /**
     * Gets the key to the lexical value
     *
     * @return Key to the lexical value
     */
    protected int getLexicalKey() {
        return lexical;
    }

    @Override
    public String getLexicalValue() {
        return store.retrieve(lexical);
    }

    @Override
    public String getDatatype() {
        return store.retrieve(datatype);
    }

    @Override
    public String getLangTag() {
        return store.retrieve(tag);
    }

    @Override
    public org.xowl.utils.data.Node serialize(Dataset dataset) {
        org.xowl.utils.data.Node result = new org.xowl.utils.data.Node(dataset, SERIALIZATION_NAME);
        Attribute attributeType = new Attribute(dataset, SERIALIZATION_TYPE);
        attributeType.setValue(TYPE);
        result.getAttributes().add(attributeType);
        Attribute attributeLexical = new Attribute(dataset, SERIALIZATION_LEXICAL);
        attributeLexical.setValue(lexical);
        result.getAttributes().add(attributeLexical);
        Attribute attributeDatatype = new Attribute(dataset, SERIALIZATION_DATATYPE);
        attributeDatatype.setValue(datatype);
        result.getAttributes().add(attributeDatatype);
        Attribute attributeTag = new Attribute(dataset, SERIALIZATION_TAG);
        attributeTag.setValue(tag);
        result.getAttributes().add(attributeTag);
        return result;
    }

    @Override
    public int hashCode() {
        return lexical ^ datatype ^ tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LiteralNodeImpl) {
            LiteralNodeImpl node = (LiteralNodeImpl) obj;
            return (this.lexical == node.lexical && this.datatype == node.datatype && this.tag == node.tag);
        }
        return false;
    }
}
