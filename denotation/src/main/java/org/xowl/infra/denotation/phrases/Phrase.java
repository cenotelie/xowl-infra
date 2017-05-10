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

package org.xowl.infra.denotation.phrases;

import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.Serializable;
import org.xowl.infra.utils.TextUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents an phrases produced by a user, as read by a parser
 *
 * @author Laurent Wouters
 */
public class Phrase implements Identifiable, Serializable {
    /**
     * The identifier of this phrases
     */
    private final String identifier;
    /**
     * The human-readable name of this phrases
     */
    private final String name;
    /**
     * The signs found in the phrases
     */
    private final List<Sign> signs;
    /**
     * The representation of this phrases
     */
    private final byte[] representationContent;
    /**
     * The MIME type for the representation
     */
    private final String representationMime;

    /**
     * Initializes this phrases
     *
     * @param identifier            The identifier of this phrases
     * @param name                  The human-readable name of this phrases
     * @param signs                 The signs found in the phrases
     * @param representationContent The representation of this phrases
     * @param representationMime    The MIME type for the representation
     */
    public Phrase(String identifier, String name, List<Sign> signs, byte[] representationContent, String representationMime) {
        this.identifier = identifier;
        this.name = name;
        this.signs = Collections.unmodifiableList(signs);
        this.representationContent = representationContent;
        this.representationMime = representationMime;
    }

    /**
     * Gets the signs found in the phrases
     *
     * @return The signs found in the phrases
     */
    public Collection<Sign> getSigns() {
        return signs;
    }

    /**
     * Gets the content of the phrases's representation
     *
     * @return The phrases's representation
     */
    public byte[] getRepresentationContent() {
        return representationContent;
    }

    /**
     * Gets the MIME type of the phrases's representation
     *
     * @return The MIME type of the phrases's representation
     */
    public String getRepresentationMime() {
        return representationMime;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serializedString() {
        return identifier;
    }

    @Override
    public String serializedJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"");
        builder.append(Phrase.class.getCanonicalName());
        builder.append("\", \"identifier\": \"");
        builder.append(TextUtils.serializeJSON(identifier));
        builder.append("\", \"name\": \"");
        builder.append(TextUtils.serializeJSON(name));
        builder.append("\", \"signs\": [");
        boolean first = true;
        for (Sign sign : signs) {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(sign.serializedJSON());
        }
        builder.append("], \"representationMime\": \"");
        if (representationMime != null)
            builder.append(TextUtils.escapeStringJSON(representationMime));
        builder.append("\"}");
        return builder.toString();
    }
}
