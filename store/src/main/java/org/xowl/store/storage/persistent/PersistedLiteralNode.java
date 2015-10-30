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

import org.xowl.store.rdf.LiteralNode;

import java.io.IOException;
import java.util.Objects;

/**
 * Persisted implementation of a literal node
 *
 * @author Laurent Wouters
 */
class PersistedLiteralNode extends LiteralNode implements PersistedNode {
    /**
     * The backend persisting the literals
     */
    private final PersistedNodes backend;
    /**
     * The key for this literal
     */
    private final long key;
    /**
     * The cached content
     */
    private String[] cache;

    /**
     * Initializes this node
     *
     * @param backend The backend persisting the literals
     * @param key     The key for this literal
     */
    public PersistedLiteralNode(PersistedNodes backend, long key) {
        this.backend = backend;
        this.key = key;
    }

    /**
     * Caches the content of the literal
     */
    private void doCache() {
        try {
            cache = backend.retrieveLiteral(key);
        } catch (IOException | StorageException exception) {
            cache = new String[]{"", null, null};
        }
    }

    @Override
    public String getLexicalValue() {
        if (cache == null)
            doCache();
        return cache[0];
    }

    @Override
    public String getDatatype() {
        if (cache == null)
            doCache();
        return cache[1];
    }

    @Override
    public String getLangTag() {
        if (cache == null)
            doCache();
        return cache[2];
    }

    @Override
    public long getKey() {
        return key;
    }

    @Override
    public void incrementRefCount() {
        backend.onRefCountLiteral(key, 1);
    }

    @Override
    public void decrementRefCount() {
        backend.onRefCountLiteral(key, -1);
    }

    @Override
    public void modifyRefCount(int modifier) {
        backend.onRefCountLiteral(key, modifier);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersistedLiteralNode) {
            PersistedLiteralNode node = (PersistedLiteralNode) o;
            return (node.backend == backend && node.key == key);
        }
        if (o instanceof LiteralNode) {
            LiteralNode node = (LiteralNode) o;
            return (Objects.equals(getLexicalValue(), node.getLexicalValue())
                    && Objects.equals(getDatatype(), node.getDatatype())
                    && Objects.equals(getLangTag(), node.getLangTag()));
        }
        return false;
    }
}
