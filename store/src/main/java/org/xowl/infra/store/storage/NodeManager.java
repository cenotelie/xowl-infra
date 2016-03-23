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

package org.xowl.infra.store.storage;

import org.xowl.infra.lang.owl2.AnonymousIndividual;
import org.xowl.infra.store.owl.AnonymousNode;
import org.xowl.infra.store.rdf.BlankNode;
import org.xowl.infra.store.rdf.GraphNode;
import org.xowl.infra.store.rdf.IRINode;
import org.xowl.infra.store.rdf.LiteralNode;

/**
 * Represents the public API of an entity that stores and manages nodes for a dataset
 *
 * @author Laurent Wouters
 */
public interface NodeManager {
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

    /**
     * Gets the anonymous node for the specified anonymous individual
     *
     * @param individual An anonymous individual
     * @return The associated anonymous node
     */
    AnonymousNode getAnonNode(AnonymousIndividual individual);
}
