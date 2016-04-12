/*******************************************************************************
 * Copyright (c) 2016 Laurent Wouters
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

package org.xowl.infra.store.storage.persistent;

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.owl.AnonymousNode;
import org.xowl.infra.store.rdf.BlankNode;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.LiteralNode;
import org.xowl.infra.store.rdf.Node;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.store.storage.impl.NodeManagerImpl;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.Logger;

import java.io.File;
import java.io.IOException;
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
    private static final long DATA_NEXT_BLANK_ENTRY = 0x00010000L;
    /**
     * Entry for the string map data
     */
    private static final long DATA_STRING_MAP_ENTRY = 0x00010001L;
    /**
     * Entry for the literal map data
     */
    private static final long DATA_LITERAL_MAP_ENTRY = 0x00010002L;

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
     * @throws IOException      When the backing files cannot be accessed
     * @throws StorageException When the storage is in a bad state
     */
    public PersistedNodes(File directory, boolean isReadonly) throws IOException, StorageException {
        store = new FileStore(directory, FILE_NAME, isReadonly);
        charset = Files.CHARSET;
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
        try (IOTransaction element = store.read(key)) {
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
        try (IOTransaction element = store.access(key)) {
            long counter = element.seek(8).readLong();
            counter += modifier;
            element.seek(8).writeLong(counter);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
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
        byte[] buffer = data.getBytes(charset);
        long candidate = bucket;
        while (candidate != PersistedNode.KEY_NOT_PRESENT) {
            try (IOTransaction entry = store.read(candidate)) {
                long next = entry.readLong();
                long count = entry.readLong();
                int size = entry.readInt();
                if (count > 0 && size == buffer.length) {
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
        byte[] buffer = data.getBytes(charset);
        long previous = PersistedNode.KEY_NOT_PRESENT;
        long candidate = bucket;
        while (candidate != PersistedNode.KEY_NOT_PRESENT) {
            try (IOTransaction entry = store.read(candidate)) {
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
        long result = store.add(buffer.length + ENTRY_STRING_OVERHEAD);
        try (IOTransaction entry = store.access(result)) {
            entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
            entry.writeLong(0);
            entry.writeInt(buffer.length);
            entry.writeBytes(buffer);
        }
        if (previous != PersistedNode.KEY_NOT_PRESENT) {
            try (IOTransaction previousEntry = store.access(previous)) {
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
                Logger.DEFAULT.error(exception);
                return PersistedNode.KEY_NOT_PRESENT;
            }
        } else {
            try {
                return lookupString(bucket, data);
            } catch (IOException | StorageException exception) {
                Logger.DEFAULT.error(exception);
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
        try (IOTransaction entry = store.read(key)) {
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
        try (IOTransaction element = store.access(key)) {
            long counter = element.seek(8).readLong();
            counter += modifier;
            element.seek(8).writeLong(counter);
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
        }
    }

    /**
     * Gets the key for the specified literal
     * The literal is inserted if it is not already present
     *
     * @param lexical  The lexical part of the literal
     * @param datatype The literal's data-type
     * @param langTag  The literals' language tag
     * @param doInsert Whether the string shall be inserted in the store if it is not already present
     * @return The key for the specified literal
     */
    private long getKeyForLiteral(String lexical, String datatype, String langTag, boolean doInsert) {
        lexical = lexical == null ? "" : lexical;
        long keyLexical = getKeyForString(lexical, doInsert);
        long keyDatatype = datatype == null ? PersistedNode.KEY_NOT_PRESENT : getKeyForString(datatype, doInsert);
        long keyLangTag = langTag == null ? PersistedNode.KEY_NOT_PRESENT : getKeyForString(langTag, doInsert);
        if (!doInsert
                && (keyLexical == PersistedNode.KEY_NOT_PRESENT
                || (datatype != null && keyDatatype == PersistedNode.KEY_NOT_PRESENT)
                || (langTag != null && keyLangTag == PersistedNode.KEY_NOT_PRESENT)))
            return PersistedNode.KEY_NOT_PRESENT;
        Long bucket = mapLiterals.get(keyLexical);
        if (bucket == null) {
            // this is the first literal with this lexem
            if (!doInsert)
                return PersistedNode.KEY_NOT_PRESENT;
            try {
                long result = store.add(ENTRY_LITERAL_SIZE);
                try (IOTransaction entry = store.access(result)) {
                    entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
                    entry.writeLong(0);
                    entry.writeLong(keyLexical);
                    entry.writeLong(keyDatatype);
                    entry.writeLong(keyLangTag);
                }
                mapLiterals.put(keyLexical, result);
                return result;
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
                return PersistedNode.KEY_NOT_PRESENT;
            }
        } else {
            long previous = PersistedNode.KEY_NOT_PRESENT;
            long candidate = bucket;
            while (candidate != PersistedNode.KEY_NOT_PRESENT) {
                try (IOTransaction entry = store.access(candidate)) {
                    long next = entry.readLong();
                    long count = entry.readLong();
                    entry.seek(24);
                    long candidateDatatype = entry.readLong();
                    long candidateLangTag = entry.readLong();
                    if ((doInsert || count > 0) && keyDatatype == candidateDatatype && keyLangTag == candidateLangTag)
                        return candidate;
                    previous = candidate;
                    candidate = next;
                } catch (StorageException exception) {
                    Logger.DEFAULT.error(exception);
                    return PersistedNode.KEY_NOT_PRESENT;
                }
            }
            // did not found an existing literal
            if (!doInsert)
                return PersistedNode.KEY_NOT_PRESENT;
            try {
                long result = store.add(ENTRY_LITERAL_SIZE);
                try (IOTransaction entry = store.access(previous)) {
                    entry.writeLong(result);
                }
                try (IOTransaction entry = store.access(result)) {
                    entry.writeLong(PersistedNode.KEY_NOT_PRESENT);
                    entry.writeLong(0);
                    entry.writeLong(keyLexical);
                    entry.writeLong(keyDatatype);
                    entry.writeLong(keyLangTag);
                }
                return result;
            } catch (StorageException exception) {
                Logger.DEFAULT.error(exception);
                return PersistedNode.KEY_NOT_PRESENT;
            }
        }
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
        if (key == PersistedNode.KEY_NOT_PRESENT)
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
        if (key == PersistedNode.KEY_NOT_PRESENT)
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
        if (key == PersistedNode.KEY_NOT_PRESENT)
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
        if (key == PersistedNode.KEY_NOT_PRESENT)
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
        return getIRINodeFor(getKeyForString(iri, true));
    }

    @Override
    public IRINode getExistingIRINode(String iri) {
        return getIRINodeFor(getKeyForString(iri, false));
    }

    @Override
    public BlankNode getBlankNode() {
        try {
            return getBlankNodeFor(nextBlank.getAndIncrement());
        } catch (StorageException exception) {
            Logger.DEFAULT.error(exception);
            return null;
        }
    }

    @Override
    public LiteralNode getLiteralNode(String lex, String datatype, String lang) {
        return getLiteralNodeFor(getKeyForLiteral(lex, datatype, lang, true));
    }

    public LiteralNode getExistingLiteralNode(String lex, String datatype, String lang) {
        return getLiteralNodeFor(getKeyForLiteral(lex, datatype, lang, false));
    }

    @Override
    public AnonymousNode getAnonNode(AnonymousIndividual individual) {
        return getAnonNodeFor(getKeyForString(individual.getNodeID(), true));
    }

    public AnonymousNode getExistingAnonNode(AnonymousIndividual individual) {
        return getAnonNodeFor(getKeyForString(individual.getNodeID(), false));
    }

    @Override
    public void close() throws Exception {
        store.close();
    }
}
