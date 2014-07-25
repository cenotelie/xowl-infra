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
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xowl.lang.owl2.IRI;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;
import org.xowl.store.voc.OWLDatatype;
import org.xowl.store.voc.RDF;
import org.xowl.utils.Logger;

import java.util.*;

/**
 * Loader for RDF XML sources
 *
 * @author Laurent Wouters
 */
public class RDFXMLLoader implements Loader {
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
     * Maps of the current prefixes
     */
    private Map<String, String> prefixes;
    /**
     * Base IRI
     */
    private String baseIRI;
    /**
     * The current ontology
     */
    private Ontology ontology;
    /**
     * Maps of the anonymous nodes
     */
    private Map<String, RDFAnonymousNode> anonymous;

    /**
     * Initializes this loader
     *
     * @param graph The graph to load in
     */
    public RDFXMLLoader(RDFGraph graph) {
        this.graph = graph;
        this.prefixes = new HashMap<>();
        this.anonymous = new HashMap<>();
    }

    /**
     * Gets a list of the Element child nodes in the specified node
     *
     * @param node A XML node
     * @return List of the Element child nodes
     */
    public static List<Node> getElements(Node node) {
        List<org.w3c.dom.Node> list = new ArrayList<>();
        for (int i = 0; i != node.getChildNodes().getLength(); i++) {
            Node child = node.getChildNodes().item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE)
                list.add(child);
        }
        return list;
    }

    /**
     * Gets the current ontology
     *
     * @return The current ontology
     */
    public Ontology getOntology() {
        return ontology;
    }

    @Override
    public void load(Logger logger, String name, java.io.Reader reader) {
        DOMParser parser = new DOMParser();
        try {
            parser.parse(new InputSource(reader));
            Document document = parser.getDocument();
            for (int i = 0; i != document.getChildNodes().getLength(); i++) {
                Node node = document.getChildNodes().item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                if (node.getNodeName().equals(rdfRDF)) {
                    load(node);
                }
            }
        } catch (Exception ex) {
            logger.error("Error while loading " + name + ": " + ex.getMessage());
            logger.error(ex);
        }
    }

    /**
     * Loads from the specified node
     *
     * @param node An XML node
     */
    private void load(Node node) {
        for (int i = 0; i != node.getAttributes().getLength(); i++) {
            String name = node.getAttributes().item(i).getNodeName();
            String value = node.getAttributes().item(i).getNodeValue();
            if (name.startsWith("xmlns:")) {
                prefixes.put(name.substring(6), value);
            } else if (name.equals("xmlns")) {
                setBaseIRI(value);
            }
        }
        if (baseIRI == null) {
            Random rand = new Random();
            String value = DEFAULT_GRAPH_URIS + Integer.toHexString(rand.nextInt()) + "#";
            setBaseIRI(value);
        }
        for (Node child : getElements(node))
            loadNode(child);
    }

    /**
     * Sets the base IRI
     *
     * @param value The base IRI to use
     */
    private void setBaseIRI(String value) {
        baseIRI = value;
        IRI iri = new IRI();
        iri.setHasValue(baseIRI.substring(0, baseIRI.length() - 1));
        ontology = new Ontology();
        ontology.setHasIRI(iri);
    }

    /**
     * Gets the RDF IRI node from the specified XML node
     *
     * @param node An XML node
     * @return The represented RDF IRI node
     */
    private RDFIRIReference getIRIForNode(Node node) {
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

    private RDFIRIReference getIRIForNode_GetIRI(String value) {
        if (value.contains("#")) {
            if (value.startsWith("#"))
                return graph.getNodeIRI(baseIRI + value.substring(1));
            else
                return graph.getNodeIRI(value);
        } else {
            return graph.getNodeIRI(baseIRI + value);
        }
    }

    private RDFIRIReference getIRIForName(String name) {
        if (name.contains(":")) {
            String parts[] = name.split(":");
            return graph.getNodeIRI(prefixes.get(parts[0]) + parts[1]);
        } else {
            return graph.getNodeIRI(baseIRI + name);
        }
    }

    private RDFBlankNode createBlank() {
        return graph.getBlankNode();
    }

    private RDFAnonymousNode getAnonymous(String nodeID) {
        RDFAnonymousNode node = anonymous.get(nodeID);
        if (node != null)
            return node;
        org.xowl.lang.owl2.AnonymousIndividual ind = new org.xowl.lang.owl2.AnonymousIndividual();
        ind.setNodeID(nodeID);
        node = graph.getAnonymousNode(ind);
        anonymous.put(nodeID, node);
        return node;
    }

    private RDFSubjectNode loadNode(org.w3c.dom.Node node) {
        RDFSubjectNode subject = null;
        String name = node.getNodeName();
        subject = getIRIForNode(node);
        if (subject == null) {
            org.w3c.dom.Node attID = node.getAttributes().getNamedItem(rdfNodeID);
            if (attID != null) {
                String valueID = attID.getNodeValue();
                subject = getAnonymous(valueID);
            } else
                subject = createBlank();
        }

        if (name.equals(rdfDescription)) {
            // Found untyped node
        } else {
            // Found typed node
            addTriple(subject, getIRIForName(rdfType), getIRIForName(name));
        }

        // Load properties expressed in node attributes
        for (int i = 0; i != node.getAttributes().getLength(); i++) {
            String attName = node.getAttributes().item(i).getNodeName();
            String attValue = node.getAttributes().item(i).getNodeValue();
            if (attName.equals(rdfAbout)) {
            } else if (attName.equals(rdfID)) {
            } else if (attName.equals(rdfResource)) {
            } else loadProperty(subject, attName, attValue);
        }

        // Load property nodes
        for (org.w3c.dom.Node child : getElements(node))
            loadProperty(subject, child);
        return subject;
    }

    private RDFSubjectNode loadBlankNode(org.w3c.dom.Node node) {
        RDFSubjectNode subject = createBlank();
        // Load property nodes
        for (org.w3c.dom.Node child : getElements(node))
            loadProperty(subject, child);
        return subject;
    }

    private void loadProperty(RDFSubjectNode subject, org.w3c.dom.Node node) {
        String name = node.getNodeName();
        RDFIRIReference property = getIRIForName(name);
        // Check attributes
        for (int i = 0; i != node.getAttributes().getLength(); i++) {
            String attName = node.getAttributes().item(i).getNodeName();
            if (attName.equals(rdfAbout) || attName.equals(rdfID) || attName.equals(rdfResource)) {
                // the value of the property is an RDF node in attribute
                RDFIRIReference object = getIRIForNode(node);
                if (object != null) {
                    addTriple(subject, property, object);
                    return;
                }
            } else if (attName.equals(rdfParseType)) {
                String attValue = node.getAttributes().item(i).getNodeValue();
                if (attValue.equals("Literal")) {
                    // Value is an XML Literal
                } else if (attValue.equals("Collection")) {
                    // Value is a collection
                    List<RDFNode> values = new ArrayList<RDFNode>();
                    List<RDFSubjectNode> proxies = new ArrayList<RDFSubjectNode>();
                    for (org.w3c.dom.Node child : getElements(node)) {
                        RDFNode value = loadNode(child);
                        RDFSubjectNode proxy = this.createBlank();
                        values.add(value);
                        proxies.add(proxy);
                        addTriple(proxy, getIRIForName(rdfFirst), value);
                    }
                    if (values.isEmpty()) {
                        addTriple(subject, property, getIRIForName(rdfNil));
                    } else {
                        addTriple(subject, property, proxies.get(0));
                        for (int n = 0; n != proxies.size() - 1; n++)
                            addTriple(proxies.get(n), getIRIForName(rdfRest), proxies.get(n + 1));
                        addTriple(proxies.get(proxies.size() - 1), getIRIForName(rdfRest), getIRIForName(rdfNil));
                    }
                    return;
                } else if (attValue.equals("Resource")) {
                    // Anonymous node
                    RDFNode object = loadBlankNode(node);
                    addTriple(subject, property, object);
                }
            } else if (attName.equals(rdfDatatype)) {
                String datatype = node.getAttributes().item(i).getNodeValue();
                String lex = node.getTextContent();
                RDFNode object = graph.getLiteralNode(lex, datatype);
                addTriple(subject, property, object);
                return;
            }
        }

        // Property value is a single node
        List<org.w3c.dom.Node> children = getElements(node);
        RDFNode object = null;
        if (children.isEmpty()) {
            // Plain literal value
            String lex = node.getTextContent();
            object = graph.getLiteralNode(lex, OWLDatatype.xsdString);
        } else {
            // Value is an RDF node
            object = loadNode(getElements(node).get(0));
        }
        addTriple(subject, property, object);
    }

    private void loadProperty(RDFSubjectNode subject, String name, String value) {
        RDFProperty property = graph.getNodeIRI(name);
        RDFNode object = graph.getLiteralNode(value, OWLDatatype.xsdString);
        addTriple(subject, property, object);
    }

    private void addTriple(RDFSubjectNode subject, RDFProperty property, RDFNode value) {
        try {
            graph.add(ontology, subject, property, value);
        } catch (UnsupportedNodeType ex) {
            //cannot happen
        }
    }
}
