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

package org.xowl.store.storage;

import org.xowl.store.rdf.BlankNode;
import org.xowl.store.rdf.GraphNode;
import org.xowl.store.rdf.IRINode;
import org.xowl.store.rdf.LiteralNode;

/**
 * Represents the public API of an entity that stores and manages nodes for a dataset
 *
 * @author Laurent Wouters
 */
public interface NodeStore {
    /**
     * Creates a new node with the specified graph as a prefix
     *
     * @param graph A graph node
     * @return The new IRI node
     */
    IRINode getIRINode(GraphNode graph);

    /**
     * Resolves the IRI node for the specified IRI
     * If the node does not exist, it is created
     *
     * @param iri An IRI
     * @return The associated IRI node
     */
    IRINode getIRINode(String iri);

    /**
     * Gets the IRI node for the specified IRI
     * If the node does not exist, null is returned
     *
     * @param iri An IRI
     * @return The associated IRI node, or null if the node does not exist
     */
    IRINode getExistingIRINode(String iri);

    /**
     * Gets a new RDF blank node
     *
     * @return A new RDF blank node
     */
    BlankNode getBlankNode();

    /**
     * Gets the RDF node for the specified literal
     *
     * @param lex      The lexical part of the literal
     * @param datatype The literal's data-type
     * @param lang     The literals' language tag
     * @return The associated RDF node
     */
    LiteralNode getLiteralNode(String lex, String datatype, String lang);
}
