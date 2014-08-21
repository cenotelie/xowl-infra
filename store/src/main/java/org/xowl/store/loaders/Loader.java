/**********************************************************************
 * Copyright (c) 2014 Laurent Wouters
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
 **********************************************************************/

package org.xowl.store.loaders;

import org.w3c.dom.Node;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Ontology;
import org.xowl.utils.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a loader for a store
 *
 * @author Laurent Wouters
 */
public abstract class Loader {
    /**
     * Default URIs for the loaded ontologies
     */
    protected static final String DEFAULT_GRAPH_URIS = "http://xowl.org/store/rdfgraphs/";

    /**
     * Creates a new ontology based on the default URI prefix
     *
     * @return The new ontology
     */
    protected static Ontology createNewOntology() {
        java.util.Random rand = new java.util.Random();
        String value = DEFAULT_GRAPH_URIS + Integer.toHexString(rand.nextInt());
        IRI iri = new IRI();
        iri.setHasValue(value);
        Ontology ontology = new Ontology();
        ontology.setHasIRI(iri);
        return ontology;
    }

    /**
     * Gets a list of the Element child nodes in the specified node
     *
     * @param node A XML node
     * @return List of the Element child nodes
     */
    protected static List<Node> getElements(Node node) {
        List<org.w3c.dom.Node> list = new ArrayList<>();
        for (int i = 0; i != node.getChildNodes().getLength(); i++) {
            Node child = node.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE)
                list.add(child);
        }
        return list;
    }

    /**
     * Parses the specified input
     *
     * @param logger The logger to use
     * @param reader The input to parse
     * @return The result of the parsing operation
     */
    public abstract ParseResult parse(Logger logger, Reader reader);

    /**
     * Loads data into the store
     *
     * @param logger The logger to use
     * @param reader The resource's reader
     * @return The ontology containing the loaded data
     */
    public abstract Ontology load(Logger logger, Reader reader);
}
