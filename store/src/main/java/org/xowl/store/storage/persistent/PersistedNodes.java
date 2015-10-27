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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a persistent store of nodes
 *
 * @author Laurent Wouters
 */
public class PersistedNodes implements NodeManager, AutoCloseable {
    /**
     * The radical for the files associated to this store
     */
    private static final String FILE_RADICAL = "nodes";
    /**
     * The suffix for the index file
     */
    private static final String FILE_DATA = FILE_RADICAL + "_data.bin";
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
     * The backing storing the nodes' data
     */
    private final FileStore backend;
    /**
     * The charset to use for reading and writing the strings
     */
    private final Charset charset;
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
        backend = new FileStore(directory, FILE_DATA);
        charset = Charset.forName("UTF-8");
        database = DBMaker.fileDB(new File(directory, FILE_INDEX)).make();
        mapStrings = database.hashMap(NAME_STRING_MAP);
        nextBlank = database.atomicLong(NAME_NEXT_BLANK);
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

    /**
     * Reads the string at the specified index
     *
     * @param key The key to the string
     * @return The string
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public String retrieveString(long key) throws IOException, StorageException {
        try (IOElement element = backend.read(key)) {
            element.readLong();
            int length = element.readInt();
            byte[] data = element.readBytes(length);
            return new String(data, charset);
        }
    }

    /**
     * Retrieves the key for the specified string in a bucket
     *
     * @param bucket The key to the bucket for this string
     * @param data   The string to get the key for
     * @return The key for the string, or KEY_NOT_PRESENT if it is not in this store
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    private long getKeyForString(long bucket, String data) throws IOException, StorageException {
        byte[] buffer = charset.encode(data).array();
        long candidate = bucket;
        while (candidate != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(candidate)) {
                long next = entry.readLong();
                int size = entry.readInt();
                if (size == buffer.length) {
                    if (Arrays.equals(buffer, entry.readBytes(buffer.length)))
                        // the string is already there, return its key
                        return candidate;
                }
                candidate = next;
            }
        }
        return PersistedNode.KEY_NOT_PRESENT;
    }

    /**
     * Stores the specified string in this backend
     *
     * @param bucket The key to the bucket for this string, or KEY_NOT_PRESENT if it must be created
     * @param data   The string to store
     * @return The key to the stored string
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public long addString(long bucket, String data) throws IOException, StorageException {
        byte[] buffer = charset.encode(data).array();
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long candidate = bucket;
        while (candidate != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(candidate)) {
                long next = entry.readLong();
                int size = entry.readInt();
                if (size == buffer.length) {
                    if (Arrays.equals(buffer, entry.readBytes(buffer.length)))
                        // the string is already there, return its key
                        return candidate;
                }
                previous = candidate;
                candidate = next;
            }
        }
        long result = backend.add(buffer.length + 12);
        try (IOElement previousEntry = backend.access(previous)) {
            previousEntry.writeLong(result);
        }
        try (IOElement entry = backend.access(result)) {
            entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
            entry.writeInt(buffer.length);
            entry.writeBytes(buffer);
        }
        return result;
    }

    /**
     * Gets the key for the specified string
     *
     * @param data     The string to get a key for
     * @param doInsert Whether the string shall be inserted in the store if it is not already present
     * @return The key for the string
     */
    private long getKeyForString(String data, boolean doInsert) {
        if (data == null)
            return PersistedNode.KEY_NOT_PRESENT;
        int hash = hash(data);
        Long bucket = mapStrings.get(hash);
        if (bucket == null && !doInsert)
            return PersistedNode.KEY_NOT_PRESENT;
        if (doInsert) {
            try {
                return addString(bucket == null ? PersistedNode.KEY_NOT_PRESENT : bucket, data);
            } catch (IOException | StorageException exception) {
                return PersistedNode.KEY_NOT_PRESENT;
            }
        } else {
            try {
                return getKeyForString(bucket, data);
            } catch (IOException | StorageException exception) {
                return PersistedNode.KEY_NOT_PRESENT;
            }
        }
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
        long key = getKeyForString(iri, true);
        return (key == PersistedNode.KEY_NOT_PRESENT ? null : new PersistedIRINode(this, key));
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        long key = getKeyForString(iri, false);
        return (key == PersistedNode.KEY_NOT_PRESENT ? null : new PersistedIRINode(this, key));
    }

    @Override
    public BlankNode getBlankNode() {
        long id = nextBlank.getAndIncrement();
        return new PersistedBlankNode(id);
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        long keyLexical = getKeyForString(lex, true);
        long keyDatatype = getKeyForString(datatype, true);
        long keyLangTag = getKeyForString(lang, true);
        return new PersistedLiteralNode(sStore, keyLexical, keyDatatype, keyLangTag);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        long key = getKeyForString(individual.getNodeID(), true);
        return (key == PersistedNode.KEY_NOT_PRESENT ? null : new PersistedAnonNode(sStore, key, individual));
    }

    @Override
    public void close() throws Exception {
        backend.close();
        database.close();
    }
}
