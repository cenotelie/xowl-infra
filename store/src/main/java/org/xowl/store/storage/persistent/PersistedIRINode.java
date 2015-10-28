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

import org.xowl.store.rdf.IRINode;

import java.io.IOException;

/**
 * Implementation of a persisted IRI node
 *
 * @author Laurent Wouters
 */
class PersistedIRINode extends IRINode implements PersistedNode {
    /**
     * The backend persisting the strings
     */
    private final PersistedNodes backend;
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
     * @param backend The backend persisting the strings
     * @param key     The key to the IRI value
     */
    public PersistedIRINode(PersistedNodes backend, long key) {
        this.backend = backend;
        this.key = key;
    }

    @Override
    public String getIRIValue() {
        if (value == null) {
            try {
                value = backend.retrieveString(key);
            } catch (IOException | StorageException exception) {
                value = "#error#";
            }
        }
        return value;
    }

    @Override
    public void serialize(IOElement ioElement) throws IOException {
        ioElement.writeInt(IRINode.TYPE_IRI);
        ioElement.writeLong(key);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersistedIRINode) {
            PersistedIRINode node = (PersistedIRINode) o;
            if (node.backend == this.backend)
                return node.key == this.key;
        }
        return (o instanceof IRINode) && (getIRIValue().equals(((IRINode) o).getIRIValue()));
    }
}
