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
 * Represents a phrase produced by a parser from an input artifact
 *
 * @author Laurent Wouters
 */
public class Phrase implements Identifiable, Serializable {
    /**
     * The identifier of this phrase
     */
    private final String identifier;
    /**
     * The human-readable name of this phrase
     */
    private final String name;
    /**
     * The signs found in the phrase
     */
    private final List<Sign> signs;
    /**
     * The original input parsed to produce this phrase
     */
    private final byte[] inputContent;
    /**
     * The MIME type for the original input
     */
    private final String inputMime;

    /**
     * Initializes this phrase
     *
     * @param identifier   The identifier of this phrase
     * @param name         The human-readable name of this phrase
     * @param signs        The signs found in the phrase
     * @param inputContent The original input parsed to produce this phrase
     * @param inputMime    The MIME type for the original input
     */
    public Phrase(String identifier, String name, List<Sign> signs, byte[] inputContent, String inputMime) {
        this.identifier = identifier;
        this.name = name;
        this.signs = Collections.unmodifiableList(signs);
        this.inputContent = inputContent;
        this.inputMime = inputMime;
    }

    /**
     * Gets the signs found in the phrase
     *
     * @return The signs found in the phrase
     */
    public Collection<Sign> getSigns() {
        return signs;
    }

    /**
     * Gets the original input parsed to produce this phrase
     *
     * @return The original input parsed to produce this phrase
     */
    public byte[] getInputContent() {
        return inputContent;
    }

    /**
     * Gets the MIME type for the original input
     *
     * @return The MIME type for the original input
     */
    public String getInputMime() {
        return inputMime;
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
        builder.append("], \"inputMime\": \"");
        if (inputMime != null)
            builder.append(TextUtils.escapeStringJSON(inputMime));
        builder.append("\"}");
        return builder.toString();
    }
}
