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

import org.xowl.infra.store.rdf.LiteralNode;

import java.util.Objects;

/**
 * Persisted implementation of a literal node
 *
 * @author Laurent Wouters
 */
class PersistedLiteralNode extends LiteralNode implements PersistedNode {
    /**
     * The backend persisting the node
     */
    private final PersistedDatasetNodes nodes;
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
     * @param nodes The backend persisting the node
     * @param key   The key for this literal
     */
    public PersistedLiteralNode(PersistedDatasetNodes nodes, long key) {
        this.nodes = nodes;
        this.key = key;
    }

    /**
     * Caches the content of the literal
     */
    private void doCache() {
        cache = nodes.retrieveLiteral(key);
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
    public PersistedDatasetNodes getOwner() {
        return nodes;
    }

    @Override
    public long getKey() {
        return key;
    }

    @Override
    public void incrementRefCount() {
        nodes.onRefCountLiteral(key, 1);
    }

    @Override
    public void decrementRefCount() {
        nodes.onRefCountLiteral(key, -1);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersistedLiteralNode) {
            PersistedLiteralNode node = (PersistedLiteralNode) o;
            return (node.nodes == nodes && node.key == key);
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
