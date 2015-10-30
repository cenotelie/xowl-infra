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

import org.xowl.store.rdf.BlankNode;

/**
 * Implementation of a persisted Blank node
 *
 * @author Laurent Wouters
 */
class PersistedBlankNode extends BlankNode implements PersistedNode {
    /**
     * Initializes this node
     *
     * @param id The unique identifier for this node
     */
    public PersistedBlankNode(long id) {
        super(id);
    }

    @Override
    public long getKey() {
        return getBlankID();
    }

    @Override
    public void incrementRefCount() {
    }

    @Override
    public void decrementRefCount() {
    }

    @Override
    public void modifyRefCount(int modifier) {
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof BlankNode) && (getBlankID() == ((BlankNode) o).getBlankID());
    }
}
