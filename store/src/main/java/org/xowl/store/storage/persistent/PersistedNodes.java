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
import org.xowl.store.storage.UnsupportedNodeType;

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
     * The suffix for the index file
     */
    private static final String FILE_DATA = "nodes_data.bin";
    /**
     * The suffix for the index file
     */
    private static final String FILE_INDEX = "nodes_index.bin";
    /**
     * Name of the hash map for the strings
     */
    private static final String NAME_STRING_MAP = "string-buckets";
    /**
     * Name of the hash map for the literals
     */
    private static final String NAME_LITERAL_MAP = "literal-buckets";
    /**
     * Name of the data for the next blank value
     */
    private static final String NAME_NEXT_BLANK = "blank-next";

    /**
     * The size of the overhead for a string entry
     * long: next entry
     * long: ref count
     * int: data length
     */
    private static final int ENTRY_STRING_OVERHEAD = 8 + 8 + 4;
    /**
     * The size of an entry for a literal
     * long: next entry
     * long: ref count
     * long: key to lexical value
     * long: key to datatype
     * long: key to lang tag
     */
    private static final int ENTRY_LITERAL_SIZE = 8 + 8 + 8 + 8 + 8;

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
     * The hash map associating the key to the lexical value of a literals to the bucket of literals with the same lexical value
     */
    private final Map<Long, Long> mapLiterals;
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
        mapLiterals = database.hashMap(NAME_LITERAL_MAP);
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
            int length = element.seek(16).readInt();
            byte[] data = element.readBytes(length);
            return new String(data, charset);
        }
    }

    /**
     * Updates the reference counter of a string entry
     *
     * @param key      The key to the string
     * @param modifier The modifier for the reference counter
     */
    public void onRefCountString(long key, int modifier) {
        try (IOElement element = backend.access(key)) {
            long counter = element.seek(8).readLong();
            counter += modifier;
            if (counter <= 0) {
                // removes the entry
                // resolve the bucket
                int length = element.seek(16).readInt();
                byte[] data = element.readBytes(length);
                int hash = hash(new String(data, charset));
                long bucket = mapStrings.get(hash);
                // resolve the previous item in the linked list
                long previous = PersistedNode.KEY_NOT_PRESENT;
                long candidate = bucket;
                while (candidate != key) {
                    try (IOElement entry = backend.read(candidate)) {
                        previous = candidate;
                        candidate = entry.readLong();
                    }
                }
                long next = element.seek(0).readLong();
                if (previous == PersistedNode.KEY_NOT_PRESENT) {
                    // this is the first element
                    if (next == PersistedNode.KEY_NOT_PRESENT) {
                        // sole element of the list
                        mapStrings.remove(hash);
                    } else {
                        // point the next element
                        mapStrings.put(hash, next);
                    }
                } else {
                    // remove the element from the list
                    try (IOElement entry = backend.access(previous)) {
                        entry.writeLong(element.seek(0).readLong());
                    }
                }
                // delete the entry
                backend.remove(key);
            } else {
                element.seek(8).writeLong(counter);
            }
        } catch (IOException | StorageException exception) {
            // do nothing
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
    private long lookupString(long bucket, String data) throws IOException, StorageException {
        byte[] buffer = charset.encode(data).array();
        long candidate = bucket;
        while (candidate != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(candidate)) {
                long next = entry.readLong();
                int size = entry.seek(16).readInt();
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
    private long addString(long bucket, String data) throws IOException, StorageException {
        byte[] buffer = charset.encode(data).array();
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long candidate = bucket;
        while (candidate != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement entry = backend.read(candidate)) {
                long next = entry.readLong();
                int size = entry.seek(16).readInt();
                if (size == buffer.length) {
                    if (Arrays.equals(buffer, entry.readBytes(buffer.length)))
                        // the string is already there, return its key
                        return candidate;
                }
                previous = candidate;
                candidate = next;
            }
        }
        long result = backend.add(buffer.length + ENTRY_STRING_OVERHEAD);
        try (IOElement entry = backend.access(result)) {
            entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
            entry.writeLong(0);
            entry.writeInt(buffer.length);
            entry.writeBytes(buffer);
        }
        if (previous != PersistedNode.KEY_NOT_PRESENT) {
            try (IOElement previousEntry = backend.access(previous)) {
                previousEntry.writeLong(result);
            }
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
                long result = addString(bucket == null ? PersistedNode.KEY_NOT_PRESENT : bucket, data);
                if (bucket == null)
                    mapStrings.put(hash, result);
                return result;
            } catch (IOException | StorageException exception) {
                return PersistedNode.KEY_NOT_PRESENT;
            }
        } else {
            try {
                return lookupString(bucket, data);
            } catch (IOException | StorageException exception) {
                return PersistedNode.KEY_NOT_PRESENT;
            }
        }
    }

    /**
     * Reads the literal at the specified index
     *
     * @param key The key to the string
     * @return The literal data
     * @throws IOException      When an IO operation failed
     * @throws StorageException When the page version does not match the expected one
     */
    public String[] retrieveLiteral(long key) throws IOException, StorageException {
        long keyLexical;
        long keyDatatype;
        long keyLangTag;
        try (IOElement entry = backend.read(key)) {
            entry.seek(16);
            keyLexical = entry.readLong();
            keyDatatype = entry.readLong();
            keyLangTag = entry.readLong();
        }
        return new String[]{
                keyLexical == PersistedNode.KEY_NOT_PRESENT ? "" : retrieveString(keyLexical),
                keyDatatype == PersistedNode.KEY_NOT_PRESENT ? null : retrieveString(keyDatatype),
                keyLangTag == PersistedNode.KEY_NOT_PRESENT ? null : retrieveString(keyLangTag)
        };
    }

    /**
     * Updates the reference counter of a literal entry
     *
     * @param key      The key to the literal
     * @param modifier The modifier for the reference counter
     */
    public void onRefCountLiteral(long key, int modifier) {
        try (IOElement element = backend.access(key)) {
            long counter = element.seek(8).readLong();
            counter += modifier;
            if (counter <= 0) {
                // removes the entry
                // resolve the bucket
                long bucketKey = element.seek(16).readLong();
                long bucket = mapLiterals.get(bucketKey);
                // resolve the previous item in the linked list
                long previous = PersistedNode.KEY_NOT_PRESENT;
                long candidate = bucket;
                while (candidate != key) {
                    try (IOElement entry = backend.read(candidate)) {
                        previous = candidate;
                        candidate = entry.readLong();
                    }
                }
                long next = element.seek(0).readLong();
                if (previous == PersistedNode.KEY_NOT_PRESENT) {
                    // this is the first element
                    if (next == PersistedNode.KEY_NOT_PRESENT) {
                        // sole element of the list
                        mapLiterals.remove(bucketKey);
                    } else {
                        // point the next element
                        mapLiterals.put(bucketKey, next);
                    }
                } else {
                    // remove the element from the list
                    try (IOElement entry = backend.access(previous)) {
                        entry.writeLong(element.seek(0).readLong());
                    }
                }
                // delete the entry
                backend.remove(key);
            } else {
                element.seek(8).writeLong(counter);
            }
        } catch (IOException | StorageException exception) {
            // do nothing
        }
    }

    /**
     * Gets the key for the specified literal
     * The literal is inserted if it is not already present
     *
     * @param lexical  The lexical part of the literal
     * @param datatype The literal's data-type
     * @param langTag  The literals' language tag
     * @return The key for the specified literal
     */
    private long getKeyForLiteral(String lexical, String datatype, String langTag) {
        lexical = lexical == null ? "" : lexical;
        long keyLexical = getKeyForString(lexical, true);
        long keyDatatype = datatype == null ? PersistedNode.KEY_NOT_PRESENT : getKeyForString(datatype, true);
        long keyLangTag = langTag == null ? PersistedNode.KEY_NOT_PRESENT : getKeyForString(langTag, true);
        Long bucket = mapLiterals.get(keyLexical);
        if (bucket == null) {
            // this is the first literal with this lexem
            try {
                long result = backend.add(ENTRY_LITERAL_SIZE);
                try (IOElement entry = backend.access(result)) {
                    entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
                    entry.writeLong(0);
                    entry.writeLong(keyLexical);
                    entry.writeLong(keyDatatype);
                    entry.writeLong(keyLangTag);
                }
                mapLiterals.put(keyLexical, result);
                return result;
            } catch (IOException | StorageException exception) {
                return PersistedNode.KEY_NOT_PRESENT;
            }
        } else {
            long previous = PersistedNode.KEY_NOT_PRESENT;
            long candidate = bucket;
            while (candidate != PersistedNode.KEY_NOT_PRESENT) {
                try (IOElement entry = backend.access(candidate)) {
                    long next = entry.readLong();
                    entry.seek(24);
                    long candidateDatatype = entry.readLong();
                    long candidateLangTag = entry.readLong();
                    if (keyDatatype == candidateDatatype && keyLangTag == candidateLangTag)
                        return candidate;
                    previous = candidate;
                    candidate = next;
                } catch (IOException | StorageException exception) {
                    return PersistedNode.KEY_NOT_PRESENT;
                }
            }
            // did not found an existing literal
            try {
                long result = backend.add(ENTRY_LITERAL_SIZE);
                try (IOElement entry = backend.access(previous)) {
                    entry.writeLong(result);
                }
                try (IOElement entry = backend.access(result)) {
                    entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
                    entry.writeLong(0);
                    entry.writeLong(keyLexical);
                    entry.writeLong(keyDatatype);
                    entry.writeLong(keyLangTag);
                }
                return result;
            } catch (IOException | StorageException exception) {
                return PersistedNode.KEY_NOT_PRESENT;
            }
        }
    }

    /**
     * Persists a node in this store
     *
     * @param node A node
     * @return The persisted equivalent
     * @throws UnsupportedNodeType When the node cannot be persisted
     */
    public PersistedNode persist(Node node) throws UnsupportedNodeType {
        if (node instanceof PersistedNode)
            return ((PersistedNode) node);
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                return (PersistedIRINode) getIRINode(((IRINode) node).getIRIValue());
            case Node.TYPE_BLANK:
                return new PersistedBlankNode(((BlankNode) node).getBlankID());
            case Node.TYPE_ANONYMOUS:
                return (PersistedAnonNode) getAnonNode(((AnonymousNode) node).getIndividual());
            case Node.TYPE_LITERAL:
                LiteralNode literal = (LiteralNode) node;
                return (PersistedLiteralNode) getLiteralNode(literal.getLexicalValue(), literal.getDatatype(), literal.getLangTag());
        }
        throw new UnsupportedNodeType(node, "Persistable nodes are IRI, Blank, Anonymous and Literal");
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
        long key = getKeyForLiteral(lex, datatype, lang);
        return new PersistedLiteralNode(this, key);
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        long key = getKeyForString(individual.getNodeID(), true);
        return (key == PersistedNode.KEY_NOT_PRESENT ? null : new PersistedAnonNode(this, key, individual));
    }

    @Override
    public void close() throws Exception {
        backend.close();
        database.close();
    }
}
