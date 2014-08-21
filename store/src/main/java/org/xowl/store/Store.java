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

import org.xowl.store.loaders.Loader;
import org.xowl.store.loaders.NTriplesLoader;
import org.xowl.store.loaders.RDFXMLLoader;
import org.xowl.store.rdf.RDFGraph;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Represents a store for ontologies backed by a RDF graph
 *
 * @author Laurent Wouters
 */
public class Store {
    /**
     * The logger
     */
    private Logger logger;
    /**
     * The backing RDF graph
     */
    private RDFGraph graph;

    /**
     * Initializes this store
     *
     * @param logger The logger to use
     * @throws java.io.IOException when the store cannot allocate a temporary file
     */
    public Store(Logger logger) throws IOException {
        this.logger = logger;
        this.graph = new RDFGraph();
    }

    /**
     * Gets the RDF graph backing this store
     *
     * @return The RDF graph
     */
    public RDFGraph getGraph() {
        return graph;
    }

    /**
     * Loads the data from the specified file
     *
     * @param file   The file to load from
     * @param syntax The syntax to use
     */
    public void load(String file, OWL2Syntax syntax) {
        Reader reader = null;
        try {
            reader = Files.getReader(file);
        } catch (IOException ex) {
            logger.error("Error accessing the file " + file);
            logger.error(ex);
            return;
        }
        load(file, reader, syntax);
        try {
            reader.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * Loads the data from the specified gzipped file
     *
     * @param file   The file to load from
     * @param syntax The syntax to use
     */
    public void loadGZip(String file, OWL2Syntax syntax) {
        InputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (IOException ex) {
            logger.error("Cannot open gzip file " + file);
            logger.error(ex);
            return;
        }
        java.util.zip.GZIPInputStream stream = null;
        try {
            stream = new java.util.zip.GZIPInputStream(fs);
        } catch (java.io.IOException ex) {
            try {
                fs.close();
            } catch (java.io.IOException ex2) {
                logger.error(ex);
            }
            logger.error("Cannot open gzip stream to " + file);
            logger.error(ex);
            return;
        }
        Reader reader = new InputStreamReader(stream, java.nio.charset.Charset.forName("UTF-8"));
        load(file, reader, syntax);
        try {
            reader.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
        try {
            fs.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * Loads the data from the specified zipped file at the specified entry
     *
     * @param zipFile The file to load from
     * @param entry   The entry to read from
     * @param syntax  The syntax to use
     */
    public void loadZip(String zipFile, String entry, OWL2Syntax syntax) {
        ZipFile zf = null;
        try {
            zf = new ZipFile(zipFile);
        } catch (IOException ex) {
            logger.error("Cannot open zip file " + zipFile);
            logger.error(ex);
            return;
        }
        ZipEntry ze = zf.getEntry(entry);
        if (ze == null) {
            try {
                zf.close();
            } catch (IOException ex) {
                logger.error(ex);
            }
            logger.error("Cannot find entry " + entry);
            return;
        }
        InputStream stream = null;
        try {
            stream = zf.getInputStream(ze);
        } catch (IOException ex) {
            try {
                zf.close();
            } catch (java.io.IOException ex2) {
                logger.error(ex);
            }
            logger.error("Cannot open entry " + entry);
            logger.error(ex);
            return;
        }
        Reader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        load(zipFile, reader, syntax);
        try {
            reader.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
        try {
            zf.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * Loads from the specified reader
     *
     * @param name   The resource's name
     * @param reader The reader to load from
     * @param syntax The syntax to use
     */
    private void load(String name, Reader reader, OWL2Syntax syntax) {
        Loader loader = null;
        switch (syntax) {
            case NTriples:
                loader = new NTriplesLoader(graph);
                break;
            case RDFXML:
                loader = new RDFXMLLoader(graph);
                break;
        }
        if (loader == null) {
            logger.error("Unsupported syntax " + syntax);
            return;
        }
        loader.load(logger, reader);
    }
}
