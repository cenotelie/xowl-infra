/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.store.storage.persistent;

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.store.storage.impl.NodeManagerImpl;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.logging.Logging;
import org.xowl.infra.utils.metrics.Metric;
import org.xowl.infra.utils.metrics.MetricSnapshot;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Represents a persistent store of nodes
 *
 * @author Laurent Wouters
 */
public class PersistedNodes extends NodeManagerImpl implements AutoCloseable {
    /**
     * The common radical for the files that store the data
     */
    private static final String FILE_NAME = "nodes";
    /**
     * Entry for the next blank value data
     */
    private static final long DATA_NEXT_BLANK_ENTRY = FileBlock.BLOCK_SIZE + FileStoreFile.FILE_OBJECT_HEADER_SIZE;
    /**
     * Entry for the string map data
     */
    private static final long DATA_STRING_MAP_ENTRY = DATA_NEXT_BLANK_ENTRY + 8 + FileStoreFile.FILE_OBJECT_HEADER_SIZE;
    /**
     * Entry for the literal map data
     */
    private static final long DATA_LITERAL_MAP_ENTRY = DATA_STRING_MAP_ENTRY + PersistedMap.HEAD_SIZE + FileStoreFile.FILE_OBJECT_HEADER_SIZE;

    /**
     * The size of the overhead for a string entry
     * long: next entry
     * long: ref count
     * int: data length
     */
    private static final int ENTRY_STRING_OVERHEAD = 8 + 8 + 4;
    /**
     * The maximum length of a string before it is split
     */
    private static final int ENTRY_STRING_MAX_FIRST = FileStoreFile.FILE_OBJECT_MAX_SIZE - ENTRY_STRING_OVERHEAD;
    /**
     * The maximum length of the rest of a string, with a header as follow:
     * long: The next rest entry
     * int: The size of this rest
     */
    private static final int ENTRY_STRING_MAX_REST = FileStoreFile.FILE_OBJECT_MAX_SIZE - (8 + 4);

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
     * The backing store for the nodes' data
     */
    private final FileStore store;
    /**
     * The charset to use for reading and writing the strings
     */
    private final Charset charset;
    /**
     * The next blank value
     */
    private final PersistedLong nextBlank;
    /**
     * The hash map associating string hash code to their bucket
     */
    private final PersistedMap mapStrings;
    /**
     * The hash map associating the key to the lexical value of a literals to the bucket of literals with the same lexical value
     */
    private final PersistedMap mapLiterals;
    /**
     * Cache of instantiated IRI nodes
     */
    private final PersistedNodeCache<PersistedIRINode> cacheNodeIRIs;
    /**
     * Cache of instantiated Blank nodes
     */
    private final PersistedNodeCache<PersistedBlankNode> cacheNodeBlanks;
    /**
     * Cache of instantiated Anonymous nodes
     */
    private final PersistedNodeCache<PersistedAnonNode> cacheNodeAnons;
    /**
     * Cache of instantiated Literal nodes
     */
    private final PersistedNodeCache<PersistedLiteralNode> cacheNodeLiterals;

    /**
     * Initializes this store of nodes
     *
     * @param directory  The parent directory containing the backing files
     * @param isReadonly Whether this store is in readonly mode
     * @throws StorageException When the storage is in a bad state
     */
    public PersistedNodes(File directory, boolean isReadonly) throws StorageException {
        store = new FileStore(directory, FILE_NAME, isReadonly);
        charset = IOUtils.CHARSET;
        PersistedLong tempNextBlank;
        PersistedMap tempStringMap;
        PersistedMap tempLiteralsMap;
        if (store.isEmpty()) {
            tempNextBlank = PersistedLong.create(store, 0);
            tempStringMap = PersistedMap.create(store);
            tempLiteralsMap = PersistedMap.create(store);
        } else {
            tempNextBlank = new PersistedLong(store, DATA_NEXT_BLANK_ENTRY);
            tempStringMap = new PersistedMap(store, DATA_STRING_MAP_ENTRY);
            tempLiteralsMap = new PersistedMap(store, DATA_LITERAL_MAP_ENTRY);
        }
        nextBlank = tempNextBlank;
        mapStrings = tempStringMap;
        mapLiterals = tempLiteralsMap;
        cacheNodeIRIs = new PersistedNodeCache<>();
        cacheNodeBlanks = new PersistedNodeCache<>();
        cacheNodeAnons = new PersistedNodeCache<>();
        cacheNodeLiterals = new PersistedNodeCache<>();
    }

    /**
     * Gets the composite metric for this store
     *
     * @return The metric for this store
     */
    public Metric getMetric() {
        return store.getMetric();
    }

    /**
     * Gets a snapshot of the metrics for this store
     *
     * @param timestamp The timestamp to use
     * @return The snapshot
     */
    public MetricSnapshot getMetricSnapshot(long timestamp) {
        return store.getMetricSnapshot(timestamp);
    }

    /**
     * Reads the string at the specified index
     *
     * @param key The key to the string
     * @return The string
     * @throws StorageException When an IO operation failed
     */
    public String retrieveString(long key) throws StorageException {
        return new String(retrieveStringBytes(store.accessR(key)), charset);
    }

    /**
     * Reads the string bytes at the specified index
     *
     * @param firstElement The IO access for the first element
     * @return The string bytes
     * @throws StorageException When an IO operation failed
     */
    private byte[] retrieveStringBytes(IOAccess firstElement) throws StorageException {
        int length;
        long next;
        byte[] result;
        int index;

        try {
            length = firstElement.seek(16).readInt();
            if (length <= ENTRY_STRING_MAX_FIRST)
                // fast path for short strings
                return firstElement.readBytes(length);
            next = firstElement.readLong();
            result = new byte[length];
            firstElement.readBytes(result, 0, ENTRY_STRING_MAX_FIRST - 8);
            index = ENTRY_STRING_MAX_FIRST - 8;
        } finally {
            firstElement.close();
        }

        while (next != FileStore.KEY_NULL) {
            try (IOAccess element = store.accessR(next)) {
                next = element.readLong();
                int restLength = element.readInt();
                element.readBytes(result, index, restLength);
                index += restLength;
            }
        }
        return result;
    }


    /**
     * Updates the reference counter of a string entry
     *
     * @param key      The key to the string
     * @param modifier The modifier for the reference counter
     * @throws StorageException When an IO operation failed
     */
    void onRefCountString(long key, int modifier) throws StorageException {
        try (IOAccess element = store.accessW(key)) {
            long counter = element.seek(8).readLong();
            counter += modifier;
            element.seek(8).writeLong(counter);
        }
    }

    /**
     * Gets the key for the specified string
     *
     * @param data    The string to get the key for
     * @param resolve Whether to insert the string if it is not present
     * @return The key for the string, or KEY_NULL if it is not in this store
     * @throws StorageException When an IO operation failed
     */
    private long getKeyForString(String data, boolean resolve) throws StorageException {
        byte[] buffer = data.getBytes(charset);
        long current = mapStrings.get(data.hashCode());
        long allocated = FileStore.KEY_NULL;

        if (current == FileStore.KEY_NULL) {
            // the bucket for this hash code does not exist
            if (!resolve)
                // do not insert => did not found the key
                return FileStore.KEY_NULL;
            allocated = allocateString(buffer);
            if (mapStrings.tryPut(data.hashCode(), allocated)) {
                // successfully inserted the string as the bucket head into the map
                return allocated;
            }
            current = mapStrings.get(data.hashCode());
        }

        while (current != FileStore.KEY_NULL) {
            long next;
            IOAccess entry = store.accessR(current);
            try {
                next = entry.readLong();
                int size = entry.skip(8).readInt();
                if (size == buffer.length) {
                    byte[] content = retrieveStringBytes(entry);
                    entry = null; // entry is closed by the retrieveStringBytes method
                    if (Arrays.equals(buffer, content)) {
                        // the string is already there, return its key
                        // if the string was allocated we cannot do anything about it ...
                        return current;
                    }
                }
            } finally {
                if (entry != null)
                    entry.close();
            }
            if (next != FileStore.KEY_NULL) {
                // there is a next string => explore it
                current = next;
                continue;
            }
            if (!resolve)
                // do not insert => did not found the string
                return FileStore.KEY_NULL;
            // supposedly there is no next string
            if (allocated == FileStore.KEY_NULL)
                // not allocated yet
                allocated = allocateString(buffer);
            try (IOAccess currentEntry = store.accessW(current)) {
                next = currentEntry.readLong();
                if (next != FileStore.KEY_NULL) {
                    // there is now a new string ...
                    continue;
                }
                currentEntry.reset().writeLong(allocated);
            }
            return allocated;
        }
        return FileStore.KEY_NULL;
    }

    /**
     * Allocates a string entry for the specified bytes
     *
     * @param buffer The buffer containing the string
     * @return The key to the entry
     * @throws StorageException When an IO operation failed
     */
    private long allocateString(byte[] buffer) throws StorageException {
        if (buffer.length <= ENTRY_STRING_MAX_FIRST) {
            // fast path for short strings
            long result = store.allocateDirect(buffer.length + ENTRY_STRING_OVERHEAD);
            try (IOAccess entry = store.accessW(result)) {
                entry.writeLong(FileStore.KEY_NULL);
                entry.writeLong(0);
                entry.writeInt(buffer.length);
                entry.writeBytes(buffer);
            }
            return result;
        }

        long result = store.allocateDirect(FileStoreFile.FILE_OBJECT_MAX_SIZE);
        int index = ENTRY_STRING_MAX_FIRST - 8;
        int nextLength = buffer.length - index;
        if (nextLength > ENTRY_STRING_MAX_REST)
            nextLength = ENTRY_STRING_MAX_REST;
        long nextEntry = store.allocateDirect(nextLength + 8 + 4);

        // write the head entry for the string
        try (IOAccess entry = store.accessW(result)) {
            entry.writeLong(FileStore.KEY_NULL);
            entry.writeLong(0);
            entry.writeInt(buffer.length);
            entry.writeLong(nextEntry);
            entry.writeBytes(buffer, 0, index);
        }

        // write the rest entry
        while (nextEntry != FileStore.KEY_NULL) {
            int after = buffer.length - index - nextLength;
            if (after > ENTRY_STRING_MAX_REST)
                after = ENTRY_STRING_MAX_REST;
            long afterEntry = after <= 0 ? FileStore.KEY_NULL : store.allocateDirect(after + 8 + 4);
            try (IOAccess entry = store.accessW(nextEntry)) {
                entry.writeLong(afterEntry);
                entry.writeInt(nextLength);
                entry.writeBytes(buffer, index, nextLength);
            }
            index += nextLength;
            nextLength = after;
            nextEntry = afterEntry;
        }
        return result;
    }

    /**
     * Reads the literal at the specified index
     *
     * @param key The key to the string
     * @return The literal data
     * @throws StorageException When an IO operation failed
     */
    public String[] retrieveLiteral(long key) throws StorageException {
        long keyLexical;
        long keyDatatype;
        long keyLangTag;
        try (IOAccess entry = store.accessR(key)) {
            entry.seek(16);
            keyLexical = entry.readLong();
            keyDatatype = entry.readLong();
            keyLangTag = entry.readLong();
        }
        return new String[]{
                keyLexical == FileStore.KEY_NULL ? "" : retrieveString(keyLexical),
                keyDatatype == FileStore.KEY_NULL ? null : retrieveString(keyDatatype),
                keyLangTag == FileStore.KEY_NULL ? null : retrieveString(keyLangTag)
        };
    }

    /**
     * Updates the reference counter of a literal entry
     *
     * @param key      The key to the literal
     * @param modifier The modifier for the reference counter
     * @throws StorageException When an IO operation failed
     */
    void onRefCountLiteral(long key, int modifier) throws StorageException {
        try (IOAccess element = store.accessW(key)) {
            long counter = element.seek(8).readLong();
            counter += modifier;
            element.seek(8).writeLong(counter);
        }
    }

    /**
     * Gets the key for the specified literal
     *
     * @param lexical  The lexical part of the literal
     * @param datatype The literal's data-type
     * @param langTag  The literals' language tag
     * @param resolve  Whether to insert the literal if it is not present
     * @return The key for the literal, or KEY_NULL if it is not in this store
     * @throws StorageException When an IO operation failed
     */
    private long getKeyForLiteral(String lexical, String datatype, String langTag, boolean resolve) throws StorageException {
        lexical = lexical == null ? "" : lexical;
        long keyLexical = getKeyForString(lexical, resolve);
        long keyDatatype = datatype == null ? FileStore.KEY_NULL : getKeyForString(datatype, resolve);
        long keyLangTag = langTag == null ? FileStore.KEY_NULL : getKeyForString(langTag, resolve);
        if (!resolve
                && (keyLexical == FileStore.KEY_NULL
                || (datatype != null && keyDatatype == FileStore.KEY_NULL)
                || (langTag != null && keyLangTag == FileStore.KEY_NULL)))
            return FileStore.KEY_NULL;

        long current = mapLiterals.get(keyLexical);
        long allocated = FileStore.KEY_NULL;

        if (current == FileStore.KEY_NULL) {
            // the bucket for this lexical does not exist
            if (!resolve)
                // do not insert => did not found the key
                return FileStore.KEY_NULL;
            allocated = allocateLiteral(keyLexical, keyDatatype, keyLangTag);
            if (mapLiterals.tryPut(keyLexical, allocated)) {
                // successfully inserted the literal as the bucket head into the map
                return allocated;
            }
            current = mapLiterals.get(keyLexical);
        }

        while (current != FileStore.KEY_NULL) {
            long next;
            try (IOAccess entry = store.accessR(current)) {
                next = entry.readLong();
                long dt = entry.skip(8 + 8).readLong();
                long lt = entry.readLong();
                if (dt == keyDatatype && lt == keyLangTag) {
                    // the literal is already there, return its key
                    if (allocated != FileStore.KEY_NULL) {
                        // a literal was allocated in the meantime, free it
                        store.free(allocated, ENTRY_LITERAL_SIZE);
                    }
                    return current;
                }
            }
            if (next != FileStore.KEY_NULL) {
                // there is a next string => explore it
                current = next;
                continue;
            }
            if (!resolve)
                // do not insert => did not found the string
                return FileStore.KEY_NULL;
            // supposedly there is no next string
            if (allocated == FileStore.KEY_NULL)
                // not allocated yet
                allocated = allocateLiteral(keyLexical, keyDatatype, keyLangTag);
            try (IOAccess entry = store.accessW(current)) {
                next = entry.readLong();
                if (next != FileStore.KEY_NULL) {
                    // there is now a new literal ...
                    continue;
                }
                entry.reset().writeLong(allocated);
            }
            return allocated;
        }
        return FileStore.KEY_NULL;
    }

    /**
     * Allocates a literal entry
     *
     * @param keyLexical  The lexical part of the literal
     * @param keyDatatype The literal's data-type
     * @param keyLangTag  The literals' language tag
     * @return The key to the entry
     * @throws StorageException When an IO operation failed
     */
    private long allocateLiteral(long keyLexical, long keyDatatype, long keyLangTag) throws StorageException {
        long result = store.allocate(ENTRY_LITERAL_SIZE);
        try (IOAccess entry = store.accessW(result)) {
            entry.writeLong(FileStore.KEY_NULL);
            entry.writeLong(0);
            entry.writeLong(keyLexical);
            entry.writeLong(keyDatatype);
            entry.writeLong(keyLangTag);
        }
        return result;
    }

    /**
     * Gets the persistent version of a specified node
     *
     * @param node   A node
     * @param create Whether to create the node if it is node present in the store
     * @return The persisted equivalent
     * @throws UnsupportedNodeType When the node cannot be persisted
     */
    public PersistedNode getPersistent(Node node, boolean create) throws UnsupportedNodeType {
        if (node == null || node.getNodeType() == Node.TYPE_VARIABLE)
            return null;
        if (node instanceof PersistedNode) {
            PersistedNode persistedNode = ((PersistedNode) node);
            if (persistedNode.getStore() == this || persistedNode.getStore() == null)
                // it is persisted here
                return persistedNode;
            // not persisted here, we should resolve it here
        }
        switch (node.getNodeType()) {
            case Node.TYPE_IRI:
                if (create)
                    return (PersistedIRINode) getIRINode(((IRINode) node).getIRIValue());
                return (PersistedIRINode) getExistingIRINode(((IRINode) node).getIRIValue());
            case Node.TYPE_BLANK:
                return getBlankNodeFor(((BlankNode) node).getBlankID());
            case Node.TYPE_ANONYMOUS:
                if (create)
                    return (PersistedAnonNode) getAnonNode(((AnonymousNode) node).getIndividual());
                return (PersistedAnonNode) getExistingAnonNode(((AnonymousNode) node).getIndividual());
            case Node.TYPE_LITERAL:
                LiteralNode literal = (LiteralNode) node;
                if (create)
                    return (PersistedLiteralNode) getLiteralNode(literal.getLexicalValue(), literal.getDatatype(), literal.getLangTag());
                return (PersistedLiteralNode) getExistingLiteralNode(literal.getLexicalValue(), literal.getDatatype(), literal.getLangTag());
        }
        throw new UnsupportedNodeType(node, "Persistable nodes are IRI, Blank, Anonymous and Literal");
    }

    /**
     * Gets the IRI node for the specified key
     *
     * @param key The IRI node for the specified key
     * @return The IRI node for the specified key
     */
    public PersistedIRINode getIRINodeFor(long key) {
        if (key == FileStore.KEY_NULL)
            return null;
        PersistedIRINode result = cacheNodeIRIs.get(key);
        if (result == null) {
            result = new PersistedIRINode(this, key);
            cacheNodeIRIs.cache(result);
        }
        return result;
    }

    /**
     * Gets the Blank node for the specified key
     *
     * @param key The Blank node for the specified key
     * @return The Blank node for the specified key
     */
    public PersistedBlankNode getBlankNodeFor(long key) {
        if (key == FileStore.KEY_NULL)
            return null;
        PersistedBlankNode result = cacheNodeBlanks.get(key);
        if (result == null) {
            result = new PersistedBlankNode(key);
            cacheNodeBlanks.cache(result);
        }
        return result;
    }

    /**
     * Gets the Anonymous node for the specified key
     *
     * @param key The Anonymous node for the specified key
     * @return The Anonymous node for the specified key
     */
    public PersistedAnonNode getAnonNodeFor(long key) {
        if (key == FileStore.KEY_NULL)
            return null;
        PersistedAnonNode result = cacheNodeAnons.get(key);
        if (result == null) {
            result = new PersistedAnonNode(this, key);
            cacheNodeAnons.cache(result);
        }
        return result;
    }

    /**
     * Gets the Literal node for the specified key
     *
     * @param key The Literal node for the specified key
     * @return The Literal node for the specified key
     */
    public PersistedLiteralNode getLiteralNodeFor(long key) {
        if (key == FileStore.KEY_NULL)
            return null;
        PersistedLiteralNode result = cacheNodeLiterals.get(key);
        if (result == null) {
            result = new PersistedLiteralNode(this, key);
            cacheNodeLiterals.cache(result);
        }
        return result;
    }

    /**
     * Flushes any outstanding changes to the backing files
     *
     * @return Whether the operation succeeded
     */
    public boolean flush() {
        return store.flush();
    }

    @Override
    public IRINode getIRINode(String iri) {
        try {
            return getIRINodeFor(getKeyForString(iri, true));
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
            return null;
        }
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        try {
            return getIRINodeFor(getKeyForString(iri, false));
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
            return null;
        }
    }

    @Override
    public BlankNode getBlankNode() {
        try {
            return getBlankNodeFor(nextBlank.getAndIncrement());
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
            return null;
        }
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        try {
            return getLiteralNodeFor(getKeyForLiteral(lex, datatype, lang, true));
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
            return null;
        }
    }

    public LiteralNode getExistingLiteralNode(String lex, String datatype, String lang) {
        try {
            return getLiteralNodeFor(getKeyForLiteral(lex, datatype, lang, false));
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
            return null;
        }
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        try {
            return getAnonNodeFor(getKeyForString(individual.getNodeID(), true));
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
            return null;
        }
    }

    public AnonymousNode getExistingAnonNode(AnonymousIndividual individual) {
        try {
            return getAnonNodeFor(getKeyForString(individual.getNodeID(), false));
        } catch (StorageException exception) {
            Logging.getDefault().error(exception);
            return null;
        }
    }

    @Override
    public void close() {
        store.close();
    }
}
