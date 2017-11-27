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

import fr.cenotelie.commons.storage.Access;
import fr.cenotelie.commons.storage.Constants;
import fr.cenotelie.commons.storage.stores.ObjectStore;
import fr.cenotelie.commons.storage.stores.StoredLong;
import fr.cenotelie.commons.storage.stores.StoredMap;
import fr.cenotelie.commons.utils.IOUtils;
import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.UnsupportedNodeType;
import org.xowl.infra.store.storage.impl.NodeManagerImpl;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Represents a persistent store of nodes
 *
 * @author Laurent Wouters
 */
public class PersistedNodes extends NodeManagerImpl {
    /**
     * Entry for the next blank value data
     */
    private static final long DATA_NEXT_BLANK_ENTRY = Constants.PAGE_SIZE + ObjectStore.OBJECT_HEADER_SIZE;
    /**
     * Entry for the string map data
     */
    private static final long DATA_STRING_MAP_ENTRY = DATA_NEXT_BLANK_ENTRY + 8 + ObjectStore.OBJECT_HEADER_SIZE;
    /**
     * Entry for the literal map data
     */
    private static final long DATA_LITERAL_MAP_ENTRY = DATA_STRING_MAP_ENTRY + StoredMap.NODE_SIZE + ObjectStore.OBJECT_HEADER_SIZE;

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
    private static final int ENTRY_STRING_MAX_FIRST = ObjectStore.OBJECT_MAX_SIZE - ENTRY_STRING_OVERHEAD;
    /**
     * The maximum length of the rest of a string, with a header as follow:
     * long: The next rest entry
     * int: The size of this rest
     */
    private static final int ENTRY_STRING_MAX_REST = ObjectStore.OBJECT_MAX_SIZE - (8 + 4);

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
     * The execution manager to use
     */
    private ExecutionManager executionManager;
    /**
     * The backing store for the nodes' data
     */
    private final ObjectStore store;
    /**
     * The charset to use for reading and writing the strings
     */
    private final Charset charset;
    /**
     * The next blank value
     */
    private final StoredLong nextBlank;
    /**
     * The hash map associating string hash code to their bucket
     */
    private final StoredMap mapStrings;
    /**
     * The hash map associating the key to the lexical value of a literals to the bucket of literals with the same lexical value
     */
    private final StoredMap mapLiterals;
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
     * Cache of instantiated dynamic nodes
     */
    private final PersistedNodeCache<PersistedDynamicNode> cacheNodeDynamics;

    /**
     * Initializes this store of nodes
     *
     * @param store      backing store for the nodes' data
     * @param initialize Whether to initialize the store
     */
    public PersistedNodes(ObjectStore store, boolean initialize) {
        this.store = store;
        this.charset = IOUtils.CHARSET;
        StoredLong tempNextBlank;
        StoredMap tempStringMap;
        StoredMap tempLiteralsMap;
        if (store.getSize() <= Constants.PAGE_SIZE) {
            tempNextBlank = StoredLong.create(store, 0);
            tempStringMap = StoredMap.create(store);
            tempLiteralsMap = StoredMap.create(store);
        } else {
            tempNextBlank = new StoredLong(store, DATA_NEXT_BLANK_ENTRY);
            tempStringMap = new StoredMap(store, DATA_STRING_MAP_ENTRY);
            tempLiteralsMap = new StoredMap(store, DATA_LITERAL_MAP_ENTRY);
        }
        nextBlank = tempNextBlank;
        mapStrings = tempStringMap;
        mapLiterals = tempLiteralsMap;
        cacheNodeIRIs = new PersistedNodeCache<>();
        cacheNodeBlanks = new PersistedNodeCache<>();
        cacheNodeAnons = new PersistedNodeCache<>();
        cacheNodeLiterals = new PersistedNodeCache<>();
        cacheNodeDynamics = new PersistedNodeCache<>();
    }

    /**
     * Reads the string at the specified index
     *
     * @param key The key to the string
     * @return The string
     */
    public String retrieveString(long key) {
        return new String(retrieveStringBytes(store.access(key, false)), charset);
    }

    /**
     * Reads the string bytes at the specified index
     *
     * @param firstElement The IO access for the first element
     * @return The string bytes
     */
    private byte[] retrieveStringBytes(Access firstElement) {
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

        while (next != Constants.KEY_NULL) {
            try (Access element = store.access(next, false)) {
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
     */
    void onRefCountString(long key, int modifier) {
        try (Access element = store.access(key, true)) {
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
     */
    private long getKeyForString(String data, boolean resolve) {
        byte[] buffer = data.getBytes(charset);
        long current = mapStrings.get(data.hashCode());
        long allocated = Constants.KEY_NULL;

        if (current == Constants.KEY_NULL) {
            // the bucket for this hash code does not exist
            if (!resolve)
                // do not insert => did not found the key
                return Constants.KEY_NULL;
            allocated = allocateString(buffer);
            if (mapStrings.tryPut(data.hashCode(), allocated)) {
                // successfully inserted the string as the bucket head into the map
                return allocated;
            }
            current = mapStrings.get(data.hashCode());
        }

        while (current != Constants.KEY_NULL) {
            long next;
            Access entry = store.access(current, false);
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
            if (next != Constants.KEY_NULL) {
                // there is a next string => explore it
                current = next;
                continue;
            }
            if (!resolve)
                // do not insert => did not found the string
                return Constants.KEY_NULL;
            // supposedly there is no next string
            if (allocated == Constants.KEY_NULL)
                // not allocated yet
                allocated = allocateString(buffer);
            try (Access currentEntry = store.access(current, true)) {
                next = currentEntry.readLong();
                if (next != Constants.KEY_NULL) {
                    // there is now a new string ...
                    continue;
                }
                currentEntry.reset().writeLong(allocated);
            }
            return allocated;
        }
        return Constants.KEY_NULL;
    }

    /**
     * Allocates a string entry for the specified bytes
     *
     * @param buffer The buffer containing the string
     * @return The key to the entry
     */
    private long allocateString(byte[] buffer) {
        if (buffer.length <= ENTRY_STRING_MAX_FIRST) {
            // fast path for short strings
            long result = store.allocateDirect(buffer.length + ENTRY_STRING_OVERHEAD);
            try (Access entry = store.access(result, true)) {
                entry.writeLong(Constants.KEY_NULL);
                entry.writeLong(0);
                entry.writeInt(buffer.length);
                entry.writeBytes(buffer);
            }
            return result;
        }

        long result = store.allocateDirect(ObjectStore.OBJECT_MAX_SIZE);
        int index = ENTRY_STRING_MAX_FIRST - 8;
        int nextLength = buffer.length - index;
        if (nextLength > ENTRY_STRING_MAX_REST)
            nextLength = ENTRY_STRING_MAX_REST;
        long nextEntry = store.allocateDirect(nextLength + 8 + 4);

        // write the head entry for the string
        try (Access entry = store.access(result, true)) {
            entry.writeLong(Constants.KEY_NULL);
            entry.writeLong(0);
            entry.writeInt(buffer.length);
            entry.writeLong(nextEntry);
            entry.writeBytes(buffer, 0, index);
        }

        // write the rest entry
        while (nextEntry != Constants.KEY_NULL) {
            int after = buffer.length - index - nextLength;
            if (after > ENTRY_STRING_MAX_REST)
                after = ENTRY_STRING_MAX_REST;
            long afterEntry = after <= 0 ? Constants.KEY_NULL : store.allocateDirect(after + 8 + 4);
            try (Access entry = store.access(nextEntry, true)) {
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
     */
    public String[] retrieveLiteral(long key) {
        long keyLexical;
        long keyDatatype;
        long keyLangTag;
        try (Access entry = store.access(key, false)) {
            entry.seek(16);
            keyLexical = entry.readLong();
            keyDatatype = entry.readLong();
            keyLangTag = entry.readLong();
        }
        return new String[]{
                keyLexical == Constants.KEY_NULL ? "" : retrieveString(keyLexical),
                keyDatatype == Constants.KEY_NULL ? null : retrieveString(keyDatatype),
                keyLangTag == Constants.KEY_NULL ? null : retrieveString(keyLangTag)
        };
    }

    /**
     * Updates the reference counter of a literal entry
     *
     * @param key      The key to the literal
     * @param modifier The modifier for the reference counter
     */
    void onRefCountLiteral(long key, int modifier) {
        try (Access element = store.access(key, true)) {
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
     */
    private long getKeyForLiteral(String lexical, String datatype, String langTag, boolean resolve) {
        lexical = lexical == null ? "" : lexical;
        long keyLexical = getKeyForString(lexical, resolve);
        long keyDatatype = datatype == null ? Constants.KEY_NULL : getKeyForString(datatype, resolve);
        long keyLangTag = langTag == null ? Constants.KEY_NULL : getKeyForString(langTag, resolve);
        if (!resolve
                && (keyLexical == Constants.KEY_NULL
                || (datatype != null && keyDatatype == Constants.KEY_NULL)
                || (langTag != null && keyLangTag == Constants.KEY_NULL)))
            return Constants.KEY_NULL;

        long current = mapLiterals.get(keyLexical);
        long allocated = Constants.KEY_NULL;

        if (current == Constants.KEY_NULL) {
            // the bucket for this lexical does not exist
            if (!resolve)
                // do not insert => did not found the key
                return Constants.KEY_NULL;
            allocated = allocateLiteral(keyLexical, keyDatatype, keyLangTag);
            if (mapLiterals.tryPut(keyLexical, allocated)) {
                // successfully inserted the literal as the bucket head into the map
                return allocated;
            }
            current = mapLiterals.get(keyLexical);
        }

        while (current != Constants.KEY_NULL) {
            long next;
            try (Access entry = store.access(current, false)) {
                next = entry.readLong();
                long dt = entry.skip(8 + 8).readLong();
                long lt = entry.readLong();
                if (dt == keyDatatype && lt == keyLangTag) {
                    // the literal is already there, return its key
                    if (allocated != Constants.KEY_NULL) {
                        // a literal was allocated in the meantime, free it
                        store.free(allocated);
                    }
                    return current;
                }
            }
            if (next != Constants.KEY_NULL) {
                // there is a next string => explore it
                current = next;
                continue;
            }
            if (!resolve)
                // do not insert => did not found the string
                return Constants.KEY_NULL;
            // supposedly there is no next string
            if (allocated == Constants.KEY_NULL)
                // not allocated yet
                allocated = allocateLiteral(keyLexical, keyDatatype, keyLangTag);
            try (Access entry = store.access(current, true)) {
                next = entry.readLong();
                if (next != Constants.KEY_NULL) {
                    // there is now a new literal ...
                    continue;
                }
                entry.reset().writeLong(allocated);
            }
            return allocated;
        }
        return Constants.KEY_NULL;
    }

    /**
     * Allocates a literal entry
     *
     * @param keyLexical  The lexical part of the literal
     * @param keyDatatype The literal's data-type
     * @param keyLangTag  The literals' language tag
     * @return The key to the entry
     */
    private long allocateLiteral(long keyLexical, long keyDatatype, long keyLangTag) {
        long result = store.allocate(ENTRY_LITERAL_SIZE);
        try (Access entry = store.access(result, true)) {
            entry.writeLong(Constants.KEY_NULL);
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
            case Node.TYPE_DYNAMIC:
                if (create)
                    return (PersistedDynamicNode) getDynamicNode(((DynamicNode) node).getEvaluable());
                return (PersistedDynamicNode) getExistingDynamicNode(((DynamicNode) node).getEvaluable());
        }
        throw new UnsupportedNodeType(node, "Persistable nodes are IRI, Blank, Anonymous, Literal and Dynamic");
    }

    /**
     * Gets the IRI node for the specified key
     *
     * @param key The IRI node for the specified key
     * @return The IRI node for the specified key
     */
    public PersistedIRINode getIRINodeFor(long key) {
        if (key == Constants.KEY_NULL)
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
        if (key == Constants.KEY_NULL)
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
        if (key == Constants.KEY_NULL)
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
        if (key == Constants.KEY_NULL)
            return null;
        PersistedLiteralNode result = cacheNodeLiterals.get(key);
        if (result == null) {
            result = new PersistedLiteralNode(this, key);
            cacheNodeLiterals.cache(result);
        }
        return result;
    }

    /**
     * Gets the dynamic node for the specified key
     *
     * @param key The dynamic node for the specified key
     * @return The dynamic node for the specified key
     */
    public PersistedDynamicNode getDynamicNodeFor(long key) {
        if (key == Constants.KEY_NULL)
            return null;
        PersistedDynamicNode result = cacheNodeDynamics.get(key);
        if (result == null) {
            result = new PersistedDynamicNode(this, key);
            cacheNodeDynamics.cache(result);
        }
        return result;
    }

    /**
     * Loads an evaluable expression from the specified source
     *
     * @param source The source of an evaluable expression
     * @return The expression
     */
    EvaluableExpression getEvaluableExpression(String source) {
        return executionManager.loadExpression(source);
    }

    @Override
    public void setExecutionManager(ExecutionManager executionManager) {
        this.executionManager = executionManager;
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
        return getBlankNodeFor(nextBlank.getAndIncrement());
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
    public DynamicNode getDynamicNode(EvaluableExpression evaluable) {
        return getDynamicNodeFor(getKeyForString(evaluable.getSource(), true));
    }

    public DynamicNode getExistingDynamicNode(EvaluableExpression evaluable) {
        return getDynamicNodeFor(getKeyForString(evaluable.getSource(), false));
    }
}
