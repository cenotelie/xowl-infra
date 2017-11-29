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
import org.xowl.infra.lang.owl2.Owl2Factory;
import org.xowl.infra.store.rdf.AnonymousNode;

/**
 * Implementation of a persisted anonymous node
 *
 * @author Laurent Wouters
 */
class PersistedAnonNode extends AnonymousNode implements PersistedNode {
    /**
     * The backend persisting the strings
     */
    private final PersistedNodes backend;
    /**
     * The key to the anonymous id
     */
    private final long key;
    /**
     * The cached individual, if any
     */
    private AnonymousIndividual individual;

    /**
     * Initializes this node
     *
     * @param backend    The backend persisting the strings
     * @param key        The key to the anonymous id
     * @param individual The cached individual, if any
     */
    public PersistedAnonNode(PersistedNodes backend, long key, AnonymousIndividual individual) {
        this.backend = backend;
        this.key = key;
        this.individual = individual;
    }

    /**
     * Initializes this node
     *
     * @param backend The backend persisting the strings
     * @param key     The key to the anonymous id
     */
    public PersistedAnonNode(PersistedNodes backend, long key) {
        this.backend = backend;
        this.key = key;
    }

    @Override
    public String getNodeID() {
        return getIndividual().getNodeID();
    }

    @Override
    public AnonymousIndividual getIndividual() {
        if (individual == null) {
            individual = Owl2Factory.newAnonymousIndividual();
            individual.setNodeID(backend.retrieveString(key));
        }
        return individual;
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
        backend.onRefCountString(key, 1);
    }

    @Override
    public void decrementRefCount() {
        backend.onRefCountString(key, -1);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersistedAnonNode) {
            PersistedAnonNode node = (PersistedAnonNode) o;
            if (node.backend == this.backend)
                return node.key == this.key;
        }
        return ((o instanceof AnonymousNode) && (getNodeID().equals(((AnonymousNode) o).getNodeID())));
    }
}
