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

package org.xowl.store.storage.impl;

import org.xowl.store.rdf.*;

/**
 * A quad supplemented by a multiplicity information
 *
 * @author Laurent Wouters
 */
public class MQuad extends Quad {
    /**
     * The quad multiplicity
     */
    private long multiplicity;

    /**
     * Initializes this quad
     *
     * @param graph        The containing graph
     * @param multiplicity The quad's multiplicity
     */
    public MQuad(GraphNode graph, long multiplicity) {
        super(graph, null, null, null);
        this.multiplicity = multiplicity;
    }

    /**
     * Gets the multiplicity of this quad
     *
     * @return The multiplicity of this quad
     */
    public long getMultiplicity() {
        return multiplicity;
    }

    /**
     * Updates the multiplicity of this quad
     *
     * @param offset The modifier
     * @return The multiplicity of this quad
     */
    public long modifyMultiplicity(long offset) {
        multiplicity += offset;
        return multiplicity;
    }

    /**
     * Sets the containing graph
     *
     * @param graph The containing graph
     */
    public void setGraph(GraphNode graph) {
        this.graph = graph;
    }

    /**
     * Sets the subject node
     *
     * @param subject The subject node
     */
    public void setSubject(SubjectNode subject) {
        this.subject = subject;
    }

    /**
     * Gets the property
     *
     * @param property The property
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Sets the object node
     *
     * @param object The object node
     */
    public void setObject(Node object) {
        this.object = object;
    }
}
