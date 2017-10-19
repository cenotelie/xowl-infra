/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.denotation.domain;

import fr.cenotelie.commons.utils.Identifiable;
import fr.cenotelie.commons.utils.Serializable;
import fr.cenotelie.commons.utils.TextUtils;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.LiteralNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.rdf.Quad;

import java.util.Collection;

/**
 * Represents the description of a domain entity
 *
 * @author Laurent Wouters
 */
public class DomainEntity implements Identifiable, Serializable {
    /**
     * The entity's IRI
     */
    protected final String iri;
    /**
     * The entity's name
     */
    protected final String name;
    /**
     * The entity's description
     */
    protected final String description;
    /**
     * The quads describing the entity
     */
    protected final Collection<Quad> quads;

    /**
     * Initializes this entity
     *
     * @param quads The quads describing the entity
     */
    public DomainEntity(Collection<Quad> quads) {
        String iri = null;
        String name = null;
        String description = null;
        for (Quad quad : quads) {
            if (iri == null)
                iri = ((IRINode) quad.getSubject()).getIRIValue();
            if (quad.getProperty().getNodeType() == Node.TYPE_IRI) {
                String property = ((IRINode) quad.getProperty()).getIRIValue();
                if (name == null && Vocabulary.rdfsLabel.equals(property))
                    name = ((LiteralNode) quad.getObject()).getLexicalValue();
                if (description == null && Vocabulary.rdfsComment.equals(property))
                    description = ((LiteralNode) quad.getObject()).getLexicalValue();
                if (name == null && Vocabulary.dcTitle.equals(property))
                    name = ((LiteralNode) quad.getObject()).getLexicalValue();
                if (description == null && Vocabulary.dcDescription.equals(property))
                    description = ((LiteralNode) quad.getObject()).getLexicalValue();
            }
        }
        this.iri = iri;
        this.name = name;
        this.description = description;
        this.quads = quads;
    }

    @Override
    public String getIdentifier() {
        return iri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serializedString() {
        return iri;
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"");
        builder.append(DomainEntity.class.getCanonicalName());
        builder.append("\"");
        serializeJsonBase(builder);
        builder.append("}");
        return builder.toString();
    }

    /**
     * Performs the JSON serialization of the basic properties
     *
     * @param builder The string builder to use
     */
    protected void serializeJsonBase(StringBuilder builder) {
        builder.append(", \"identifier\": \"");
        builder.append(TextUtils.escapeStringJSON(iri));
        builder.append("\", \"name\": \"");
        builder.append(TextUtils.escapeStringJSON(name));
        builder.append("\", \"description\": \"");
        builder.append(TextUtils.escapeStringJSON(description));
        builder.append("\"");
    }
}
