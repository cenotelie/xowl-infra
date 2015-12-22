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

package org.xowl.store.owl;

import org.xowl.lang.owl2.AnonymousIndividual;
import org.xowl.store.rdf.SubjectNode;

/**
 * Represents a RDF store associated to an anonymous individual
 *
 * @author Laurent Wouters
 */
public abstract class AnonymousNode implements SubjectNode {
    @Override
    public int getNodeType() {
        return TYPE_ANONYMOUS;
    }

    @Override
    public int hashCode() {
        return getNodeID().hashCode();
    }

    @Override
    public String toString() {
        return "_:" + getNodeID();
    }

    /**
     * Gets the identifier of this anonymous node
     *
     * @return The identifier of this anonymous node
     */
    public abstract String getNodeID();

    /**
     * Gets the anonymous individual represented by this node
     *
     * @return The anonymous individual represented by this node
     */
    public abstract AnonymousIndividual getIndividual();
}
