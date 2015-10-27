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

package org.xowl.store.storage.persistent;

import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.IRIs;
import org.xowl.store.owl.AnonymousNode;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.NodeManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a persistent store of nodes
 *
 * @author Laurent Wouters
 */
public class PersistedNodes implements NodeManager {
    /**
     * The radical for the files associated to this store
     */
    private static final String FILE_RADICAL = "nodes";
    /**
     * The suffix for the index file
     */
    private static final String FILE_INDEX = FILE_RADICAL + "_index.bin";
    /**
     * Name of the hash map for the strings
     */
    private static final String NAME_STRING_MAP = "string-buckets";
    /**
     * Name of the data for the next blank value
     */
    private static final String NAME_NEXT_BLANK = "blank-next";

    /**
     * The string store backend
     */
    private final StringStoreBackend sStore;
    /**
     * The database backing the index
     */
    private final DB database;
    /**
     * The hash map associating string hash code to their bucket
     */
    private final Map<Integer, Long> mapStrings;
    /**
     * The next blank value
     */
    private final Atomic.Long nextBlank;

    /**
     * Initializes this store of nodes
     *
     * @param directory The parent directory containing the backing files
     * @throws IOException      When the backing files cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public PersistedNodes(File directory) throws IOException, StorageException {
        this.sStore = new StringStoreBackend(directory, FILE_RADICAL);
        this.database = DBMaker.fileDB(new File(directory, FILE_INDEX)).make();
        this.mapStrings = database.hashMap(NAME_STRING_MAP);
        this.nextBlank = database.atomicLong(NAME_NEXT_BLANK);
    }

    /**
     * Gets the hash code for the string data
     *
     * @param data The string
     * @return The hash code
     */
    private static int hash(String data) {
        return data.hashCode();
    }

    @Override
    public IRINode getIRINode(GraphNode graph) {
        if (graph != null && graph.getNodeType() == Node.TYPE_IRI) {
            String value = ((IRINode) graph).getIRIValue();
            return getIRINode(value + "#" + UUID.randomUUID().toString());
        } else {
            return getIRINode(IRIs.GRAPH_DEFAULT + "#" + UUID.randomUUID().toString());
        }
    }

    @Override
    public IRINode getIRINode(String iri) {
        int hash = hash(iri);
        Long bucket = mapStrings.get(hash);
        try {
            long key = sStore.add(bucket == null ? -1 : bucket, iri);
            if (bucket == null)
                mapStrings.put(hash, key);
            return new PersistedIRINode(sStore, key);
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        int hash = hash(iri);
        Long bucket = mapStrings.get(hash);
        if (bucket == null)
            return null;
        try {
            long key = sStore.getKey(bucket, iri);
            return (key == -1 ? null : new PersistedIRINode(sStore, key));
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    public BlankNode getBlankNode() {
        long id = nextBlank.getAndIncrement();
        return new PersistedBlankNode(id);
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return null;
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return null;
    }
}
