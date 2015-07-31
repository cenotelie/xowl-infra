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

package org.xowl.store.writers;

import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Couple;

import java.io.IOException;
import java.util.*;

/**
 * Represents a structured serializer of RDF data
 *
 * @author Laurent Wouters
 */
public abstract class StructuredSerializer implements RDFSerializer {
    /**
     * Initial size of the buffer for the blank node map
     */
    private static final int BLANKS_MAP_INIT_SIZE = 256;
    /**
     * Prefix for the generated namespaces
     */
    private static final String NAMESPACE_PREFIX = "nm";

    /**
     * The namespaces in this document
     */
    protected final Map<String, String> namespaces;
    /**
     * The data to serialize
     */
    protected final Map<SubjectNode, List<Quad>> data;
    /**
     * Buffer for renaming blank nodes
     */
    private int[] blanks;
    /**
     * Index of the next blank node slot
     */
    private int nextBlank;
    /**
     * The index of the next namespace
     */
    private int nextNamespace;
    /**
     * A buffer of serialized property
     */
    protected final List<Property> bufferProperties;

    /**
     * Initializes this serializer
     */
    public StructuredSerializer() {
        this.namespaces = new HashMap<>();
        this.namespaces.put(Vocabulary.rdf, "rdf");
        this.namespaces.put(Vocabulary.rdfs, "rdfs");
        this.namespaces.put(Vocabulary.xsd, "xsd");
        this.namespaces.put(Vocabulary.owl, "owl");
        this.data = new HashMap<>();
        this.blanks = new int[BLANKS_MAP_INIT_SIZE];
        this.nextBlank = 0;
        this.nextNamespace = 0;
        this.bufferProperties = new ArrayList<>(5);
    }

    /**
     * Serializes the specified quads
     *
     * @param logger The logger to use
     * @param quads  The quads to serialize
     */
    public abstract void serialize(Logger logger, Iterator<Quad> quads);

    /**
     * Enqueue a quad into the dataset to serialize
     *
     * @param quad A quad
     * @throws UnsupportedNodeType
     */
    protected void enqueue(Quad quad) throws UnsupportedNodeType {
        // map the nodes
        mapNode(quad.getGraph());
        mapNode(quad.getSubject());
        mapNode(quad.getProperty());
        mapNode(quad.getObject());
        // register the quad as a property of its subject
        List<Quad> properties = data.get(quad.getSubject());
        if (properties == null) {
            properties = new ArrayList<>();
            data.put(quad.getSubject(), properties);
        }
        properties.add(quad);
    }

    /**
     * Maps the specified node
     *
     * @param node The node
     * @throws UnsupportedNodeType
     */
    private void mapNode(Node node) throws UnsupportedNodeType {
        switch (node.getNodeType()) {
            case IRINode.TYPE:
                mapIRI(node, ((IRINode) node).getIRIValue());
                break;
            case BlankNode.TYPE:
                mapBlank((BlankNode) node);
                break;
            case LiteralNode.TYPE:
                String datatype = ((LiteralNode) node).getDatatype();
                mapIRI(node, datatype);
                break;
            default:
                throw new UnsupportedNodeType(node, "RDF serialization only support IRI, Blank and Literal nodes");
        }
    }

    /**
     * Maps an IRI to its namespace
     *
     * @param node The containing node
     * @param iri  The IRI to map
     */
    private void mapIRI(Node node, String iri) throws UnsupportedNodeType {
        int index = iri.indexOf("#");
        if (index != -1) {
            mapNamespace(iri.substring(0, index + 1));
            return;
        }
        throw new UnsupportedNodeType(node, "IRI does not contain #");
    }

    /**
     * Maps the specified namespace
     *
     * @param namespace A namespace
     */
    private void mapNamespace(String namespace) {
        String compact = namespaces.get(namespace);
        if (compact == null) {
            compact = NAMESPACE_PREFIX + nextNamespace;
            nextNamespace++;
            namespaces.put(namespace, compact);
        }
    }

    /**
     * Maps this blank node for renaming
     *
     * @param node The blank node
     */
    private void mapBlank(BlankNode node) {
        int id = node.getBlankID();
        for (int i = 0; i != nextBlank; i++) {
            if (blanks[i] == id)
                return;
        }
        if (nextBlank == blanks.length)
            blanks = Arrays.copyOf(blanks, blanks.length + BLANKS_MAP_INIT_SIZE);
        blanks[nextBlank] = id;
        nextBlank++;
    }

    /**
     * Gets the compact IRI corresponding to the specified IRI
     *
     * @param node The containing node
     * @param iri  The IRI to compact
     * @return The corresponding compact IRI as couple (prefix, suffix) where prefix:suffix expands to the original IRI
     */
    protected Couple<String, String> getCompactIRI(Node node, String iri) throws UnsupportedNodeType {
        int index = iri.indexOf("#");
        if (index != -1) {
            String prefix = namespaces.get(iri.substring(0, index + 1));
            if (prefix == null)
                throw new UnsupportedNodeType(node, "Unmapped IRI node");
            return new Couple<>(prefix, index == iri.length() - 1 ? "" : iri.substring(index + 1));
        }
        throw new UnsupportedNodeType(node, "IRI does not contain #");
    }

    /**
     * Gets the remapped identifier for the specified blank node
     *
     * @param node A blank node
     * @return The corresponding identifier
     */
    protected int getBlankID(BlankNode node) throws IOException {
        int id = node.getBlankID();
        for (int i = 0; i != nextBlank; i++) {
            if (blanks[i] == id)
                return i;
        }
        throw new IOException("Unmapped blank node " + id);
    }
}
