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

package org.xowl.infra.denotation;

import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.utils.Identifiable;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents an artifact produced by a user, as read by a parser
 *
 * @author Laurent Wouters
 */
public class Artifact implements Identifiable {
    /**
     * The identifier of this artifact
     */
    private final String identifier;
    /**
     * The human-readable name of this artifact
     */
    private final String name;
    /**
     * The quads describing the symbols in this artifact
     */
    private final Collection<Quad> symbols;
    /**
     * The representation of this artifact
     */
    private final byte[] representationContent;
    /**
     * The MIME type for the representation
     */
    private final String representationMime;

    /**
     * Initializes this artifact
     *
     * @param identifier            The identifier of this artifact
     * @param name                  The human-readable name of this artifact
     * @param symbols               The quads describing the symbols in this artifact
     * @param representationContent The representation of this artifact
     * @param representationMime    The MIME type for the representation
     */
    public Artifact(String identifier, String name, Collection<Quad> symbols, byte[] representationContent, String representationMime) {
        this.identifier = identifier;
        this.name = name;
        this.symbols = Collections.unmodifiableCollection(symbols);
        this.representationContent = representationContent;
        this.representationMime = representationMime;
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
    public String toString() {
        return identifier;
    }

    /**
     * Gets the quads describing the symbols in this artifact
     *
     * @return The quads describing the symbols in this artifact
     */
    public Collection<Quad> getSymbols() {
        return symbols;
    }

    /**
     * Gets the content of the artifact's representation
     *
     * @return The artifact's representation
     */
    public byte[] getRepresentationContent() {
        return representationContent;
    }

    /**
     * Gets the MIME type of the artifact's representation
     *
     * @return The MIME type of the artifact's representation
     */
    public String getRepresentationMime() {
        return representationMime;
    }
}
