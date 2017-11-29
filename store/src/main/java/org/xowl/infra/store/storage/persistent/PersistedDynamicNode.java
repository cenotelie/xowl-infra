/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.rdf.DynamicNode;

import java.util.Objects;

/**
 * Persisted implementation of a dynamic node
 *
 * @author Laurent Wouters
 */
public class PersistedDynamicNode extends DynamicNode implements PersistedNode {
    /**
     * The backend persisting the dynamic node
     */
    private final PersistedNodes backend;
    /**
     * The key for this dynamic node
     */
    private final long key;
    /**
     * The cached content
     */
    private EvaluableExpression cache;

    /**
     * Initializes this node
     *
     * @param backend The backend persisting the dynamic node
     * @param key     The key for this dynamic node
     */
    public PersistedDynamicNode(PersistedNodes backend, long key) {
        this.backend = backend;
        this.key = key;
    }

    /**
     * Caches the content of the literal
     */
    private void doCache() {
        String source = backend.retrieveString(key);
        cache = backend.getEvaluableExpression(source);
    }

    @Override
    public EvaluableExpression getEvaluable() {
        if (cache == null)
            doCache();
        return cache;
    }

    @Override
    public PersistedNodes getStore() {
        return backend;
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
    public boolean equals(Object o) {
        if (o instanceof PersistedDynamicNode) {
            PersistedDynamicNode node = (PersistedDynamicNode) o;
            return (node.backend == backend && node.key == key);
        }
        if (o instanceof DynamicNode) {
            DynamicNode node = (DynamicNode) o;
            return Objects.equals(getEvaluable(), node.getEvaluable());
        }
        return false;
    }
}
