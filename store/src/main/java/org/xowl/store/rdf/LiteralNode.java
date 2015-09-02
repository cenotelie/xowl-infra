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

package org.xowl.store.rdf;

/**
 * Represents a node associated to a literal value in a RDF graph
 *
 * @author Laurent Wouters
 */
public abstract class LiteralNode implements Node {
    /**
     * The type of node
     */
    public static final int TYPE = 2;

    @Override
    public int getNodeType() {
        return TYPE;
    }

    /**
     * Gets the lexical value of this literal
     *
     * @return The lexical value of this literal
     */
    public abstract String getLexicalValue();

    /**
     * Gets the IRI of the datatype of this literal
     *
     * @return The IRI of the datatype of this literal
     */
    public abstract String getDatatype();

    /**
     * Gets the language tag of this literal
     *
     * @return The language tag of this literal
     */
    public abstract String getLangTag();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\"");
        builder.append(getLexicalValue());
        builder.append("\"");
        String lang = getLangTag();
        String datatype = getDatatype();
        if (lang != null) {
            builder.append("@");
            builder.append(lang);
        } else if (datatype != null) {
            builder.append("^^");
            builder.append(datatype);
        }
        return builder.toString();
    }
}
