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

package org.xowl.store;

import org.xowl.store.loaders.*;
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.RDFStore;
import org.xowl.store.rdf.UnsupportedNodeType;
import org.xowl.utils.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

/**
 * Represents a front end for managing a RDF store
 */
public class StoreManager {
    /**
     * The current logger
     */
    protected Logger logger;
    /**
     * the backend storage
     */
    protected RDFStore backend;

    /**
     * Initializes this manager
     *
     * @param logger The associated logger
     * @throws IOException When the backend could not be created
     */
    public StoreManager(Logger logger) throws IOException {
        this.logger = logger;
        this.backend = new RDFStore();
    }

    /**
     * Gets the backend storage
     *
     * @return the backend storage
     */
    public RDFStore getBackend() {
        return backend;
    }

    /**
     * Loads data from the specified file
     *
     * @param file The path to the file to load data from
     * @param uri  The URI associated to the file
     * @return <code>true</code> if the operation was successful
     */
    public boolean loadData(String file, String uri) {
        try {
            Reader reader = new FileReader(file);
            DataSyntax syntax = getDataSyntax(file);
            return loadData(reader, syntax, uri);
        } catch (Exception ex) {
            logger.error(ex);
            return false;
        }
    }

    /**
     * Loads data from the specified resources
     *
     * @param reader A resource reader
     * @param syntax The data syntax to use
     * @param uri    The URI associated to the resource
     * @return <code>true</code> if the operation was successful
     */
    public boolean loadData(Reader reader, DataSyntax syntax, String uri) {
        Loader loader = getLoader(syntax);
        Collection<Quad> quads = loader.loadQuads(logger, reader, uri);
        if (quads == null)
            return false;
        try {
            for (Quad quad : quads)
                backend.add(quad);
            return true;
        } catch (UnsupportedNodeType ex) {
            return false;
        }
    }

    /**
     * Gets a loader for the specified data syntax
     *
     * @param syntax A data syntax
     * @return A compatible loader, or <code>null</code> if none is found
     */
    private Loader getLoader(DataSyntax syntax) {
        switch (syntax) {
            case NTRIPLES:
                return new NTriplesLoader(backend);
            case NQUADS:
                return new NQuadsLoader(backend);
            case TURTLE:
                return new TurtleLoader(backend);
            case RDFXML:
                return new RDFXMLLoader(backend);
            default:
                return null;
        }
    }

    /**
     * Computes the appropriate data syntax for the specified file
     *
     * @param file A data file
     * @return The appropriate data syntax
     */
    private DataSyntax getDataSyntax(String file) {
        if (file.endsWith(".nt"))
            return DataSyntax.NTRIPLES;
        if (file.endsWith(".nq"))
            return DataSyntax.NQUADS;
        if (file.endsWith(".ttl"))
            return DataSyntax.TURTLE;
        if (file.endsWith(".rdf"))
            return DataSyntax.RDFXML;
        return DataSyntax.UNKNOWN;
    }
}
