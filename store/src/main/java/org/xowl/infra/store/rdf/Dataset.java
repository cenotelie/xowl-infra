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

package org.xowl.infra.store.rdf;

import java.util.Collection;

/**
 * Represents the public API for a dataset of RDF quads
 *
 * @author Laurent Wouters
 */
public interface Dataset extends DatasetQuads, DatasetNodes, AutoCloseable {
    /**
     * Gets a named RDF resource
     *
     * @param iri The resource's IRI
     * @return The corresponding IRI node
     */
    IRINode getResource(String iri);

    /**
     * Gets all the values of a property for a subject
     *
     * @param subject  The subject
     * @param property The property
     * @return The values
     */
    Collection<Node> getValuesBy(IRINode subject, IRINode property);

    /**
     * Gets all the possible subjects with a property and a value
     *
     * @param property The property
     * @param object   The value
     * @return The possible subjects
     */
    Collection<SubjectNode> getSubjectsWith(IRINode property, Node object);
}
