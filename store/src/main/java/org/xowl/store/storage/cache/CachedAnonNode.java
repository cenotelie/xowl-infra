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

package org.xowl.store.storage.cache;

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.owl.AnonymousNode;

/**
 * Cached implementation of an anonymous node
 *
 * @author Laurent Wouters
 */
class CachedAnonNode extends AnonymousNode {
    /**
     * The individual backing this node
     */
    private final AnonymousIndividual individual;

    /**
     * Initializes this node
     *
     * @param individual The individual backing this node
     */
    public CachedAnonNode(AnonymousIndividual individual) {
        this.individual = individual;
    }

    @Override
    public String getNodeID() {
        return individual.getNodeID();
    }

    @Override
    public AnonymousIndividual getIndividual() {
        return individual;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof AnonymousNode) && (getNodeID().equals(((AnonymousNode) o).getNodeID())));
    }
}
