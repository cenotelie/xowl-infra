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
    private String identifier;
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
    private Location definitionLocation;
    /**
     * The various references to this symbol by the URI of the referencing files
     */
    private final Map<String, Collection<Location>> references;
    /**
     * The parent symbol, if any
     */
    private Symbol parent;

    /**
     * Initializes this symbol
     *
     * @param identifier The unique identifier of this symbol
     * @param name       The name of this symbol
     */
    public Symbol(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
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
    public Location getDefinitionLocation() {
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
    public Collection<Location> getReferencesIn(String uri) {
        return references.get(uri);
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
    public void setDefinition(String uri, Location location) {
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
    public void addReference(String uri, Location location) {
        Collection<Location> locations = references.get(uri);
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

    /**
     * Merges the data of this symbol with the provided ones (most likely references)
     *
     * @param symbol The data of a symbol
     */
    public void merge(Symbol symbol) {
        if (symbol.kind != this.kind)
            this.kind = symbol.kind;
        if (symbol.definitionFileUri != null)
            this.definitionFileUri = symbol.definitionFileUri;
        if (symbol.definitionLocation != null)
            this.definitionLocation = symbol.definitionLocation;
        if (symbol.parent != null)
            this.parent = symbol.parent;
        for (Map.Entry<String, Collection<Location>> entry : symbol.references.entrySet()) {
            this.references.put(entry.getKey(), entry.getValue());
        }
    }
}
