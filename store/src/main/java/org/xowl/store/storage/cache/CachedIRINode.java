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

import org.xowl.store.rdf.IRINode;

/**
 * Cached implementation of an IRI node
 *
 * @author Laurent Wouters
 */
class CachedIRINode extends IRINode {
    /**
     * The IRI value
     */
    private final String value;

    /**
     * Initializes this node
     *
     * @param value The IRI value
     */
    public CachedIRINode(String value) {
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException("IRI value must be non-null and non-empty");
        this.value = value;
    }

    @Override
    public String getIRIValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof IRINode) && (this.value.equals(((IRINode) o).getIRIValue()));
    }
}
