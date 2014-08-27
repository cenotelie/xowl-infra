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

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;
import org.xowl.store.voc.OWLDatatype;
import org.xowl.store.voc.RDF;
import org.xowl.utils.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loader for RDF XML sources
 *
 * @author Laurent Wouters
 */
public class RDFXMLLoader extends Loader {
    private static final String prefix = "rdf:";
    private static final String rdfDescription = prefix + RDF.nameDescription;
    private static final String rdfAbout = prefix + RDF.nameAbout;
    private static final String rdfID = prefix + RDF.nameID;
    private static final String rdfNodeID = prefix + RDF.nameNodeID;
    private static final String rdfResource = prefix + RDF.nameResource;
    private static final String rdfDatatype = prefix + RDF.nameDatatype;
    private static final String rdfParseType = prefix + RDF.nameParseType;
    private static final String rdfType = prefix + RDF.nameType;
    private static final String rdfFirst = prefix + RDF.nameFirst;
    private static final String rdfRest = prefix + RDF.nameRest;
    private static final String rdfNil = prefix + RDF.nameNil;
    private static final String rdfRDF = "rdf:" + RDF.nameRDF;

    /**
     * The RDF graph to load into
     */
    private RDFGraph graph;
    /**
     * The loaded triples
     */
    private List<Triple> triples;
    /**
     * The URI of the resource currently being loaded
     */
    private String resource;
    /**
     * Base IRI
     */
    private String baseURI;
    /**
     * Maps of the current namespaces
     */
    private Map<String, String> namespaces;
    /**
     * Maps of the blanks nodes
     */
    private Map<String, BlankNode> blanks;
    /**
     * The current ontology
     */
    private Ontology ontology;

    /**
     * Initializes this loader
     *
     * @param graph The graph to load in
     */
    public RDFXMLLoader(RDFGraph graph) {
        this.graph = graph;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ontology load(Logger logger, java.io.Reader reader, String uri) {
        triples = new ArrayList<>();
        ontology = createNewOntology();
        resource = uri;
        baseURI = null;
        namespaces = new HashMap<>();
        blanks = new HashMap<>();

        DOMParser parser = new DOMParser();
        try {
            parser.parse(new InputSource(reader));
            Document document = parser.getDocument();
            for (int i = 0; i != document.getChildNodes().getLength(); i++) {
                org.w3c.dom.Node node = document.getChildNodes().item(i);
                if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
                    continue;
                if (node.getNodeName().equals(rdfRDF)) {
                    load(node);
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }

        try {
            for (Triple triple : triples)
                graph.add(triple);
        } catch (UnsupportedNodeType ex) {
            // cannot happen
        }

        return ontology;
    }

    /**
     * Loads from the specified node
     *
     * @param node An XML node
     */
    private void load(org.w3c.dom.Node node) {
        for (int i = 0; i != node.getAttributes().getLength(); i++) {
            String name = node.getAttributes().item(i).getNodeName();
            String value = node.getAttributes().item(i).getNodeValue();
            if (name.startsWith("xmlns:")) {
                namespaces.put(name.substring(6), value);
            } else if (name.equals("xmlns")) {
                baseURI = value;
            }
        }
        for (org.w3c.dom.Node child : getElements(node))
            loadNode(child);
    }

    /**
     * Gets the RDF IRI node from the specified XML node
     *
     * @param node An XML node
     * @return The represented RDF IRI node
     */
    private IRINode getIRIForNode(org.w3c.dom.Node node) {
        for (int i = 0; i != node.getAttributes().getLength(); i++) {
            String name = node.getAttributes().item(i).getNodeName();
            String value = node.getAttributes().item(i).getNodeValue();
            if (name.equals(rdfAbout)) {
                return getIRIForNode_GetIRI(value);
            } else if (name.equals(rdfID)) {
                return getIRIForNode_GetIRI(value);
            } else if (name.equals(rdfResource)) {
                return getIRIForNode_GetIRI(value);
            }
        }
        return null;
    }

    /**
     * Gets the IRI node for the specified IRI
     *
     * @param value An IRI
     * @return The associated IRI node
     */
    private IRINode getIRIForNode_GetIRI(String value) {
        value = normalizeIRI(resource, baseURI, value);
        return graph.getNodeIRI(value);
    }

    /**
     * Gets the IRI node for the specified local name
     *
     * @param value A local name
     * @return The associated IRI node
     */
    private IRINode getIRIForName(String value) {
        value = unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return graph.getNodeIRI(normalizeIRI(resource, baseURI, uri + name));
                }
            }
            index++;
        }
        throw new IllegalArgumentException("Failed to resolve local name " + value);
    }

    /**
     * Gets a new blank node
     *
     * @return A new blank node
     */
    private BlankNode getBlank() {
        return graph.getBlankNode();
    }

    /**
     * Resolves the blank node with the specified ID
     *
     * @param nodeID The required ID
     * @return The associated blank node
     */
    private BlankNode getBlank(String nodeID) {
        BlankNode node = blanks.get(nodeID);
        if (node != null)
            return node;
        node = graph.getBlankNode();
        blanks.put(nodeID, node);
        return node;
    }

    /**
     * Loads a RDF node
     *
     * @param node The XML node to load from
     * @return The loaded RDF node
     */
    private SubjectNode loadNode(org.w3c.dom.Node node) {
        SubjectNode subject = null;
        String name = node.getNodeName();
        subject = getIRIForNode(node);
        if (subject == null) {
            org.w3c.dom.Node attID = node.getAttributes().getNamedItem(rdfNodeID);
            if (attID != null) {
                String valueID = attID.getNodeValue();
                subject = getBlank(valueID);
            } else
                subject = getBlank();
        }

        if (name.equals(rdfDescription)) {
            // Found untyped node
        } else {
            // Found typed node
            register(subject, getIRIForName(rdfType), getIRIForName(name));
        }

        // Load properties expressed in node attributes
        for (int i = 0; i != node.getAttributes().getLength(); i++) {
            String attName = node.getAttributes().item(i).getNodeName();
            String attValue = node.getAttributes().item(i).getNodeValue();
            if (attName.equals(rdfAbout)) {
            } else if (attName.equals(rdfID)) {
            } else if (attName.equals(rdfResource)) {
            } else register(subject, attName, attValue);
        }

        // Load property nodes
        for (org.w3c.dom.Node child : getElements(node))
            loadProperty(subject, child);
        return subject;
    }

    /**
     * Loads RDF properties for the specified blank node
     *
     * @param node The XML node to load from
     * @return The blank node
     */
    private SubjectNode loadBlankNode(org.w3c.dom.Node node) {
        SubjectNode subject = getBlank();
        // Load property nodes
        for (org.w3c.dom.Node child : getElements(node))
            loadProperty(subject, child);
        return subject;
    }

    /**
     * Loads RDF properties for the specified node
     *
     * @param subject The current subject node
     * @param node    The XML node to load from
     */
    private void loadProperty(SubjectNode subject, org.w3c.dom.Node node) {
        String name = node.getNodeName();
        IRINode property = getIRIForName(name);
        // Check attributes
        for (int i = 0; i != node.getAttributes().getLength(); i++) {
            String attName = node.getAttributes().item(i).getNodeName();
            if (attName.equals(rdfAbout) || attName.equals(rdfID) || attName.equals(rdfResource)) {
                // the value of the property is an RDF node in attribute
                IRINode object = getIRIForNode(node);
                if (object != null) {
                    register(subject, property, object);
                    return;
                }
            } else if (attName.equals(rdfParseType)) {
                String attValue = node.getAttributes().item(i).getNodeValue();
                if (attValue.equals("Literal")) {
                    // Value is an XML Literal
                } else if (attValue.equals("Collection")) {
                    // Value is a collection
                    List<Node> values = new ArrayList<Node>();
                    List<SubjectNode> proxies = new ArrayList<SubjectNode>();
                    for (org.w3c.dom.Node child : getElements(node)) {
                        Node value = loadNode(child);
                        SubjectNode proxy = this.getBlank();
                        values.add(value);
                        proxies.add(proxy);
                        register(proxy, getIRIForName(rdfFirst), value);
                    }
                    if (values.isEmpty()) {
                        register(subject, property, getIRIForName(rdfNil));
                    } else {
                        register(subject, property, proxies.get(0));
                        for (int n = 0; n != proxies.size() - 1; n++)
                            register(proxies.get(n), getIRIForName(rdfRest), proxies.get(n + 1));
                        register(proxies.get(proxies.size() - 1), getIRIForName(rdfRest), getIRIForName(rdfNil));
                    }
                    return;
                } else if (attValue.equals("Resource")) {
                    // Anonymous node
                    Node object = loadBlankNode(node);
                    register(subject, property, object);
                }
            } else if (attName.equals(rdfDatatype)) {
                String datatype = node.getAttributes().item(i).getNodeValue();
                String lex = node.getTextContent();
                Node object = graph.getLiteralNode(lex, datatype, null);
                register(subject, property, object);
                return;
            }
        }

        // Property value is a single node
        List<org.w3c.dom.Node> children = getElements(node);
        Node object = null;
        if (children.isEmpty()) {
            // Plain literal value
            String lex = node.getTextContent();
            object = graph.getLiteralNode(lex, OWLDatatype.xsdString, null);
        } else {
            // Value is an RDF node
            object = loadNode(getElements(node).get(0));
        }
        register(subject, property, object);
    }

    /**
     * Loads the triple with the specified value
     *
     * @param subject  The triple's subject
     * @param property The triple's property
     * @param value    The triples's value
     */
    private void register(SubjectNode subject, String property, String value) {
        Property p = graph.getNodeIRI(property);
        Node object = graph.getLiteralNode(value, OWLDatatype.xsdString, null);
        register(subject, p, object);
    }

    /**
     * Registers the triple with the specified values
     *
     * @param subject  The triple's subject
     * @param property The triples's property
     * @param value    The triples's value
     */
    private void register(SubjectNode subject, Property property, Node value) {
        triples.add(new Triple(ontology, subject, property, value));
    }
}
