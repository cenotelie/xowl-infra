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
 *     Stephen Creff - stephen.creff@gmail.com
 ******************************************************************************/

package org.xowl.store.writers;

import org.xowl.store.RDFUtils;
import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.utils.collections.Couple;
import org.xowl.utils.logging.Logger;

import java.util.*;


/**
 * Represents a structured serializer of RDF data
 *
 * @author Laurent Wouters
 * @author Stephen Creff
 */
public abstract class StructuredSerializer implements RDFSerializer {
    /**
     * Initial size of the buffer for the blank node map
     */
    private static final int BLANKS_MAP_INIT_SIZE = 256;
    /**
     * The radical for generated short names
     */
    private static final String NAMESPACE_RADICAL = "nmspce";

    /**
     * The namespaces in this document
     */
    protected final Map<String, String> namespaces;
    /**
     * Buffer for renaming blank nodes
     */
    private long[] blanks;
    /**
     * Index of the next blank node slot
     */
    private int nextBlank;
    /**
     * The initial content map
     */
    protected final Map<GraphNode, Map<SubjectNode, List<Couple<Property, Object>>>> content;
    /**
     * The RDF lists in the content
     */
    protected final Map<SubjectNode, List<Node>> contentLists;
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
        this.blanks = new long[BLANKS_MAP_INIT_SIZE];
        this.nextBlank = 0;
        this.content = new HashMap<>();
        this.contentLists = new HashMap<>();
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
     */
    protected void enqueue(Quad quad) {
        enqueueNode(quad.getGraph());
        enqueueNode(quad.getSubject());
        enqueueNode(quad.getProperty());
        enqueueNode(quad.getObject());

        Map<SubjectNode, List<Couple<Property, Object>>> sub1 = content.get(quad.getGraph());
        if (sub1 == null) {
            sub1 = new HashMap<>();
            content.put(quad.getGraph(), sub1);
        }
        List<Couple<Property, Object>> sub2 = sub1.get(quad.getSubject());
        if (sub2 == null) {
            sub2 = new ArrayList<>();
            sub1.put(quad.getSubject(), sub2);
        }
        for (int i = 0; i != sub2.size(); i++) {
            Couple<Property, Object> couple = sub2.get(i);
            if (RDFUtils.same(quad.getProperty(), couple.x) && RDFUtils.same(quad.getObject(), (Node) couple.y)) {
                // already in the data map
                return;
            }
        }
        sub2.add(new Couple<Property, Object>(quad.getProperty(), quad.getObject()));
    }

    /**
     * Enqueues the specified node
     *
     * @param node A node
     */
    protected void enqueueNode(Node node) {
        if (node.getNodeType() == Node.TYPE_IRI) {
            String iri = ((IRINode) node).getIRIValue();
            int index = iri.indexOf("#");
            if (index != -1) {
                String head = iri.substring(index + 1);
                String target = namespaces.get(head);
                if (target != null)
                    return;
                namespaces.put(head, NAMESPACE_RADICAL + Integer.toString(namespaces.size()));
            }
        }
    }

    /**
     * Builds the existing RDF lists in the data
     * This method is optional for the serialization
     */
    protected void buildRdfLists() {
        for (Map.Entry<GraphNode, Map<SubjectNode, List<Couple<Property, Object>>>> entryGraph : content.entrySet()) {
            List<SubjectNode> subjects = new ArrayList<>(entryGraph.getValue().keySet());
            for (SubjectNode subject : subjects) {
                List<Couple<Property, Object>> properties = entryGraph.getValue().get(subject);
                if (isRdfListProxy(properties))
                    continue;
                for (Couple<Property, Object> property : properties) {
                    property.y = getValueFor(entryGraph.getValue(), (Node) property.y);
                }
            }
        }
    }

    /**
     * Gets the replacing value for a node representing an object in a quad
     * When the node is the first proxy of a RDF list, this method tries to rebuild the list and return it
     *
     * @param subjects The map of the current subjects to look into for finding RDF list proxies
     * @param node     The node to replace
     * @return The replacing value
     */
    protected Object getValueFor(Map<SubjectNode, List<Couple<Property, Object>>> subjects, Node node) {
        if ((node.getNodeType() & Node.FLAG_SUBJECT) == 0)
            // not a subject
            return node;
        SubjectNode subjectNode = (SubjectNode) node;
        List<Couple<Property, Object>> properties = subjects.get(subjectNode);
        if (properties == null) {
            return node;
        } else if (isRdfListProxy(properties)) {
            // this is the first proxy of a list
            List<Node> list = contentLists.get(subjectNode);
            if (list != null)
                return list;
            list = new ArrayList<>();
            List<SubjectNode> proxiesToRemove = new ArrayList<>();
            proxiesToRemove.add(subjectNode);
            Couple<Node, Node> data = getRdfListProxyData(properties);
            list.add(data.x);
            Node head = data.y;
            while (head != null && !isRdfListNil(head)) {
                if ((head.getNodeType() & Node.FLAG_SUBJECT) == 0)
                    // not a proxy ...
                    return node;
                properties = subjects.get(head);
                if (properties == null || !isRdfListProxy(properties)) {
                    // not a correct RDF list, do nothing, return the node as is
                    return node;
                }
                data = getRdfListProxyData(properties);
                list.add(data.x);
                head = data.y;
            }
            contentLists.put((SubjectNode) node, list);
            for (SubjectNode proxy : proxiesToRemove)
                subjects.remove(proxy);
            return list;
        } else if (isRdfListNil(node)) {
            return new ArrayList<Node>();
        } else {
            return node;
        }
    }

    /**
     * Gets whether the specified properties describe a proxy in a RDF list
     *
     * @param properties The set of properties for a subject
     * @return Whether the properties describe a proxy in a RDF list
     */
    private static boolean isRdfListProxy(List<Couple<Property, Object>> properties) {
        return properties.size() == 2
                && ((isRdfListFirst(properties.get(0)) && isRdfListRest(properties.get(1)))
                || (isRdfListFirst(properties.get(1)) && isRdfListRest(properties.get(0))));

    }

    /**
     * Gets the data of the proxy object represented by the specified properties
     *
     * @param properties The properties of a proxy in a RDF list
     * @return The couple of the first proxied element and the following proxy in the list
     */
    private static Couple<Node, Node> getRdfListProxyData(List<Couple<Property, Object>> properties) {
        Node element;
        Node rest;
        if (isRdfListFirst(properties.get(0))) {
            element = (Node) properties.get(0).y;
            rest = (Node) properties.get(1).y;
        } else {
            rest = (Node) properties.get(0).y;
            element = (Node) properties.get(1).y;
        }
        return new Couple<>(element, rest);
    }

    /**
     * Gets whether the property is rdf:first
     *
     * @param property The property
     * @return Whether the property is rdf:first
     */
    private static boolean isRdfListFirst(Couple<Property, Object> property) {
        return property.x.getNodeType() == Node.TYPE_IRI && Vocabulary.rdfFirst.equals(((IRINode) property.x).getIRIValue());
    }

    /**
     * Gets whether the property is rdf:rest
     *
     * @param property The property
     * @return Whether the property is rdf:rest
     */
    private static boolean isRdfListRest(Couple<Property, Object> property) {
        return property.x.getNodeType() == Node.TYPE_IRI && Vocabulary.rdfRest.equals(((IRINode) property.x).getIRIValue());
    }

    /**
     * Gets whether the node is rdf:nil
     *
     * @param node The node
     * @return Whether the node is rdf:nil
     */
    private static boolean isRdfListNil(Node node) {
        return node.getNodeType() == Node.TYPE_IRI && Vocabulary.rdfNil.equals(((IRINode) node).getIRIValue());
    }

    /**
     * Gets the remapped identifier for the specified blank node
     *
     * @param node A blank node
     * @return The corresponding identifier
     */
    protected int getBlankID(BlankNode node) {
        long id = node.getBlankID();
        for (int i = 0; i != nextBlank; i++) {
            if (blanks[i] == id)
                return i;
        }
        if (nextBlank == blanks.length)
            blanks = Arrays.copyOf(blanks, blanks.length + BLANKS_MAP_INIT_SIZE);
        blanks[nextBlank] = id;
        int result = nextBlank;
        nextBlank++;
        return result;
    }

    /**
     * Gets the short name for an IRI, or null of there is none
     *
     * @param iri An IRI
     * @return The equivalent short name
     */
    protected String getShortName(String iri) {
        int index = iri.indexOf("#");
        return index == -1 ? null : namespaces.get(iri.substring(index + 1)) + ":" + iri.substring(index + 1);
    }
}
