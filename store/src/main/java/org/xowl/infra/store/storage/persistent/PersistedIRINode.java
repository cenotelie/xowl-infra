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

import org.xowl.infra.store.rdf.IRINode;

/**
 * Implementation of a persisted IRI node
 *
 * @author Laurent Wouters
 */
class PersistedIRINode extends IRINode implements PersistedNode {
    /**
     * The backend persisting the node
     */
    private final PersistedDatasetNodes nodes;
    /**
     * The key to the IRI value
     */
    private final long key;
    /**
     * The cached IRI value, if any
     */
    private String value;

    /**
     * Initializes this node
     *
     * @param nodes The backend persisting the node
     * @param key   The key to the IRI value
     */
    public PersistedIRINode(PersistedDatasetNodes nodes, long key) {
        this.nodes = nodes;
        this.key = key;
    }

    @Override
    public String getIRIValue() {
        if (value == null)
            value = nodes.retrieveString(key);
        return value;
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
        nodes.onRefCountString(key, 1);
    }

    @Override
    public void decrementRefCount() {
        nodes.onRefCountString(key, -1);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersistedIRINode) {
            PersistedIRINode node = (PersistedIRINode) o;
            if (node.nodes == this.nodes)
                return node.key == this.key;
        }
        return (o instanceof IRINode) && (getIRIValue().equals(((IRINode) o).getIRIValue()));
    }
}
