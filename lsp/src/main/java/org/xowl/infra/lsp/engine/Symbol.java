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

package org.xowl.infra.lsp.engine;

import org.xowl.infra.lsp.structures.Location;
import org.xowl.infra.lsp.structures.Range;

import java.util.*;

/**
 * Represents the data of a symbol
 *
 * @author Laurent Wouters
 */
public class Symbol {
    /**
     * The unique identifier of this symbol
     */
    private final String identifier;
    /**
     * The name of this symbol
     */
    private String name;
    /**
     * The kind of this symbol
     */
    private int kind;
    /**
     * The URI of the file that contains the symbol's definition
     */
    private String definitionFileUri;
    /**
     * The location of the definition within the defining file
     */
    private Range definitionLocation;
    /**
     * The various references to this symbol by the URI of the referencing files
     */
    private final Map<String, Collection<Range>> references;
    /**
     * The parent symbol, if any
     */
    private Symbol parent;

    /**
     * Initializes this symbol
     *
     * @param identifier The unique identifier of this symbol
     */
    public Symbol(String identifier) {
        this.identifier = identifier;
        this.name = identifier;
        this.kind = 0;
        this.definitionFileUri = null;
        this.definitionLocation = null;
        this.references = new HashMap<>();
        this.parent = null;
    }

    /**
     * Gets whether this symbol still exists
     *
     * @return Whether this symbol still exists
     */
    public boolean exists() {
        return definitionFileUri != null || !references.isEmpty();
    }

    /**
     * Gets the unique identifier of this symbol
     *
     * @return The unique identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the name of this symbol
     *
     * @return The name of this symbol
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the kind of this symbol
     *
     * @return The kind of this symbol
     */
    public int getKind() {
        return kind;
    }

    /**
     * Gets the URI of the defining document
     *
     * @return the URI of the defining document
     */
    public String getDefinitionFileUri() {
        return definitionFileUri;
    }

    /**
     * Gets the location of the symbol definition within the defining document
     *
     * @return The location of the symbol definition within the defining document
     */
    public Range getDefinitionLocation() {
        return definitionLocation;
    }

    /**
     * Gets the URI of the documents that reference this symbol
     *
     * @return The URI of the referencing documents
     */
    public Collection<String> getReferencingDocuments() {
        return references.keySet();
    }

    /**
     * Gets the location of the references to this symbol within a document
     *
     * @param uri The URI of the document
     * @return The location of the references to this symbol within the document
     */
    public Collection<Range> getReferencesIn(String uri) {
        return references.get(uri);
    }

    /**
     * Gets all the references to this symbol
     *
     * @return The references to this symbol
     */
    public Collection<Location> getAllReferences() {
        Collection<Location> locations = new ArrayList<>();
        for (Map.Entry<String, Collection<Range>> entry : references.entrySet()) {
            for (Range range : entry.getValue()) {
                locations.add(new Location(entry.getKey(), range));
            }
        }
        return locations;
    }

    /**
     * Gets the parent symbol
     *
     * @return The parent symbol
     */
    public Symbol getParent() {
        return parent;
    }

    /**
     * Sets th user name of this symbol
     *
     * @param name the user name of this symbol
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the kind of this symbol
     *
     * @param kind The kind of this symbol
     */
    public void setKind(int kind) {
        this.kind = kind;
    }

    /**
     * Sets the data for the definition of this symbol
     *
     * @param uri      The URI of the defining file
     * @param location The location within the defining file
     */
    public void setDefinition(String uri, Range location) {
        this.definitionFileUri = uri;
        this.definitionLocation = location;
    }

    /**
     * Sets the parent symbol
     *
     * @param parent The parent symbol
     */
    public void setParent(Symbol parent) {
        this.parent = parent;
    }

    /**
     * Adds a reference to this symbol
     *
     * @param uri      The referencing document
     * @param location The location in the document
     */
    public void addReference(String uri, Range location) {
        Collection<Range> locations = references.get(uri);
        if (locations == null) {
            locations = new ArrayList<>();
            references.put(uri, locations);
        }
        locations.add(location);
    }

    /**
     * When the specified file has been removed
     *
     * @param uri The uri of the removed file
     * @return Whether the symbol still exists
     */
    public boolean onFileRemoved(String uri) {
        if (Objects.equals(definitionFileUri, uri)) {
            definitionFileUri = null;
            definitionLocation = null;
        }
        references.remove(uri);
        return exists();
    }
}
