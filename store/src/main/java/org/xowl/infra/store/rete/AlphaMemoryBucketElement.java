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

package org.xowl.infra.store.rete;

import org.xowl.infra.store.rdf.Quad;
import org.xowl.infra.store.storage.Dataset;

/**
 * Represents a element in a bucket of alpha memories
 *
 * @author Laurent Wouters
 */
interface AlphaMemoryBucketElement {
    /**
     * Retrieve the matching memories associated to the specified data
     *
     * @param buffer The buffer to fill
     * @param quad   The data to match
     */
    void matchMemories(AlphaMemoryBuffer buffer, Quad quad);

    /**
     * Resolves the alpha memory associated to the specified data
     *
     * @param pattern The data to match
     * @param store   The RDF data
     * @return The associated memory
     */
    AlphaMemory resolveMemory(Quad pattern, Dataset store);
}
