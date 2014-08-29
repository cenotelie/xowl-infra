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
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;
import org.xowl.store.voc.OWLDatatype;
import org.xowl.store.voc.RDF;
import org.xowl.utils.Logger;

import java.io.Reader;
import java.util.*;

/**
 * Loader for RDF XML sources
 *
 * @author Laurent Wouters
 */
public class RDFXMLLoader extends Loader {
    private static final String prefix = "rdf:";
    private static final String rdfRDF = prefix + RDF.nameRDF;
    private static final String rdfDescription = prefix + RDF.nameDescription;
    private static final String rdfAbout = prefix + RDF.nameAbout;
    private static final String rdfID = prefix + RDF.nameID;
    private static final String rdfNodeID = prefix + RDF.nameNodeID;
    private static final String rdfResource = prefix + RDF.nameResource;
    private static final String rdfDatatype = prefix + RDF.nameDatatype;
    private static final String rdfParseType = prefix + RDF.nameParseType;
    private static final String rdfType = prefix + RDF.nameType;
    private static final String rdfLI = prefix + "li";

    /**
     * Reserved core syntax terms
     */
    private static final String[] RESERVED_CORE_SYNTAX_TERMS = new String[]{
            rdfRDF,
            rdfID,
            rdfAbout,
            rdfParseType,
            rdfResource,
            rdfNodeID,
            rdfDatatype
    };
    /**
     * Reserved old terms
     */
    private static final String[] RESERVED_OLD_TERMS = new String[]{
            prefix + "aboutEach",
            prefix + "aboutEachPrefix",
            prefix + "bagID"
    };

    /**
     * Determines whether a list contains a specified value
     *
     * @param list  The list to look into
     * @param value the value to look for
     * @return <code>true</code> if the value is in the list
     */
    private static boolean contains(String[] list, String value) {
        for (int i = 0; i != list.length; i++)
            if (list[i].equals(value))
                return true;
        return false;
    }

    /**
     * Determines whether the specified XML node is a valid RDF resource element
     *
     * @param node A XML node
     * @return <code>true</code> if the node is valid
     */
    public static boolean isValidElement(org.w3c.dom.Node node) {
        return (!contains(RESERVED_CORE_SYNTAX_TERMS, node.getNodeName()) && !contains(RESERVED_OLD_TERMS, node.getNodeName()) && !rdfLI.equals(node.getNodeName()));
    }

    /**
     * Determines whether the specified XML node is a valid RDF property element
     *
     * @param node A XML node
     * @return <code>true</code> if the node is valid
     */
    public static boolean isValidPropertyElement(org.w3c.dom.Node node) {
        return (!contains(RESERVED_CORE_SYNTAX_TERMS, node.getNodeName()) && !contains(RESERVED_OLD_TERMS, node.getNodeName()) && !rdfDescription.equals(node.getNodeName()));
    }

    /**
     * Determines whether the specified XML node is a valid RDF property attribute
     *
     * @param node A XML node
     * @return <code>true</code> if the node is valid
     */
    public static boolean isValidPropertyAttribute(org.w3c.dom.Node node) {
        return (!contains(RESERVED_CORE_SYNTAX_TERMS, node.getNodeName()) && !contains(RESERVED_OLD_TERMS, node.getNodeName()) && !rdfLI.equals(node.getNodeName()) && !rdfDescription.equals(node.getNodeName()));
    }

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
        namespaces = new HashMap<>();
        blanks = new HashMap<>();

        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(reader));
            Document document = parser.getDocument();
            org.w3c.dom.Node root = document.getDocumentElement();
            if (rdfRDF.equals(root.getNodeName()))
                loadDocument(root);
            else
                loadElement(root, null, null);
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
     * Loads the specified document node (rdf:RDF node)
     *
     * @param node A RDF document node
     */
    private void loadDocument(org.w3c.dom.Node node) {
        XMLAttributes attributes = new XMLAttributes(node);
        String baseURI = null;
        String lang = null;

        org.w3c.dom.Node attribute = attributes.pop("xml:lang");
        if (attribute != null)
            lang = attribute.getNodeValue();
        attribute = attributes.pop("xml:base");
        if (attribute != null)
            baseURI = attribute.getNodeValue();
        attribute = attributes.pop("xmlns");
        if (attribute != null)
            baseURI = attribute.getNodeValue();
        Iterator<org.w3c.dom.Node> iterator = attributes.popAll("xmlns:");
        while (iterator.hasNext()) {
            attribute = iterator.next();
            namespaces.put(attribute.getNodeName().substring(6), attribute.getNodeValue());
        }

        Iterator<Element> children = getXMLChildren(node);
        while (children.hasNext())
            loadElement(children.next(), baseURI, lang);
    }

    /**
     * Loads the specified RDF resource node
     *
     * @param node    A RDF resource node
     * @param baseURI The current base URI
     * @param lang    The current language
     * @return The represented RDF node
     */
    private SubjectNode loadElement(org.w3c.dom.Node node, String baseURI, String lang) {
        if (!isValidElement(node))
            throw new IllegalArgumentException("Unexpected resource element " + node.getNodeName());

        SubjectNode subject = null;
        XMLAttributes attributes = new XMLAttributes(node);
        org.w3c.dom.Node attribute = attributes.pop("xml:lang");
        if (attribute != null)
            lang = attribute.getNodeValue();
        attribute = attributes.pop("xml:base");
        if (attribute != null)
            baseURI = attribute.getNodeValue();
        attribute = attributes.pop("xmlns");
        if (attribute != null)
            baseURI = attribute.getNodeValue();
        Iterator<org.w3c.dom.Node> xmlAttributes = attributes.popAll("xml");
        while (xmlAttributes.hasNext())
            xmlAttributes.next();

        attribute = attributes.pop(rdfID);
        if (attribute != null) {
            String iri = normalizeIRI(resource, baseURI, "#" + attribute.getNodeValue());
            subject = graph.getNodeIRI(iri);
        }
        attribute = attributes.pop(rdfNodeID);
        if (attribute != null) {
            subject = getBlank(attribute.getNodeValue());
        }
        attribute = attributes.pop(rdfAbout);
        if (attribute != null) {
            String iri = normalizeIRI(resource, baseURI, attribute.getNodeValue());
            subject = graph.getNodeIRI(iri);
        }

        if (subject == null)
            subject = graph.getBlankNode();

        if (!rdfDescription.equals(node.getNodeName())) {
            register(subject, RDF.rdfType, graph.getNodeIRI(getIRIFromNSName(baseURI, node.getNodeName())));
        }

        attribute = attributes.pop(rdfType);
        if (attribute != null) {
            register(subject, RDF.rdfType, graph.getNodeIRI(normalizeIRI(resource, baseURI, attribute.getNodeValue())));
        }

        for (org.w3c.dom.Node att : attributes) {
            if (!isValidPropertyAttribute(att))
                throw new IllegalArgumentException("Unexpected property attribute node " + att.getNodeName());
            IRINode property = graph.getNodeIRI(getIRIFromNSName(baseURI, att.getNodeName()));
            LiteralNode literal;
            if (lang != null)
                literal = graph.getLiteralNode(att.getNodeValue(), OWLDatatype.rdfLangString, lang);
            else
                literal = graph.getLiteralNode(att.getNodeValue(), OWLDatatype.xsdString, null);
            register(subject, property, literal);
        }

        Iterator<Element> children = getXMLChildren(node);
        while (children.hasNext())
            loadElementProperty(children.next(), subject, baseURI, lang);

        return subject;
    }

    /**
     * Loads the specified RDF property
     *
     * @param node    The XML node representing the property
     * @param subject The current RDF subject
     * @param baseURI The current base URI
     * @param lang    The current language
     */
    private void loadElementProperty(org.w3c.dom.Node node, SubjectNode subject, String baseURI, String lang) {
        if (!isValidPropertyElement(node))
            throw new IllegalArgumentException("Unexpected property element node " + node.getNodeName());

        XMLAttributes attributes = new XMLAttributes(node);
        org.w3c.dom.Node attributeParseType = attributes.pop(rdfParseType);
        if (attributeParseType == null) {
            if (node.getChildNodes().getLength() == 0) {
                loadElementPropertyEmpty(node, subject, baseURI, lang);
            } else {
                Iterator<Element> children = getXMLChildren(node);
                if (children.hasNext()) {
                    loadElementPropertyResource(node, subject, baseURI, lang);
                } else {
                    loadElementPropertyLiteral(node, subject, baseURI, lang);
                }
            }
        } else {
            String parseType = attributeParseType.getNodeValue();
            if ("Literal".equals(parseType)) {
                loadElementPropertyLiteralParseType(node, subject, baseURI, lang);
            } else if ("Resource".equals(parseType)) {
                loadElementPropertyResourceParseType(node, subject, baseURI, lang);
            } else if ("Collection".equals(parseType)) {
                loadElementPropertyCollectionParseType(node, subject, baseURI, lang);
            } else {
                loadElementPropertyLiteralParseType(node, subject, baseURI, lang);
            }
        }
    }

    /**
     * Gets the property IRI node for the specified XML node representing it
     *
     * @param node    An XML node representing an RDf property
     * @param baseURI The current base URI
     * @return The equivalent property IRI node
     */
    private IRINode getProperty(org.w3c.dom.Node node, String baseURI) {
        if (rdfLI.equals(node.getNodeName())) {
            Iterator<Element> chilren = getXMLChildren(node.getParentNode());
            int index = 1;
            while (chilren.hasNext()) {
                if (node == chilren.next())
                    break;
                index++;
            }
            return graph.getNodeIRI(RDF.rdf + "_" + Integer.toString(index));
        } else {
            return graph.getNodeIRI(getIRIFromNSName(baseURI, node.getNodeName()));
        }
    }

    /**
     * Loads the specified RDF property pointing to a resource
     *
     * @param node    The XML node representing the property
     * @param subject The current RDF subject
     * @param baseURI The current base URI
     * @param lang    The current language
     */
    private void loadElementPropertyResource(org.w3c.dom.Node node, SubjectNode subject, String baseURI, String lang) {
        IRINode property = getProperty(node, baseURI);
        Iterator<Element> children = getXMLChildren(node);
        SubjectNode value = loadElement(children.next(), baseURI, lang);
        register(subject, property, value);

        XMLAttributes attributes = new XMLAttributes(node);
        org.w3c.dom.Node attribute = attributes.pop(rdfID);
        if (attribute != null) {
            // reify the triple
            IRINode proxy = graph.getNodeIRI(normalizeIRI(resource, baseURI, "#" + attribute.getNodeValue()));
            register(proxy, RDF.rdfType, graph.getNodeIRI(RDF.rdfStatement));
            register(proxy, RDF.rdfSubject, subject);
            register(proxy, RDF.rdfPredicate, property);
            register(proxy, RDF.rdfObject, value);
        }
    }

    /**
     * Loads the specified RDF literal property
     *
     * @param node    The XML node representing the property
     * @param subject The current RDF subject
     * @param baseURI The current base URI
     * @param lang    The current language
     */
    private void loadElementPropertyLiteral(org.w3c.dom.Node node, SubjectNode subject, String baseURI, String lang) {
        IRINode property = getProperty(node, baseURI);
        String lexem = node.getChildNodes().item(0).getTextContent();
        LiteralNode value;
        XMLAttributes attributes = new XMLAttributes(node);
        org.w3c.dom.Node attribute = attributes.pop(rdfDatatype);
        if (attribute != null) {
            value = graph.getLiteralNode(lexem, attribute.getNodeValue(), null);
        } else if (lang != null) {
            value = graph.getLiteralNode(lexem, OWLDatatype.rdfLangString, lang);
        } else {
            value = graph.getLiteralNode(lexem, OWLDatatype.xsdString, null);
        }
        register(subject, property, value);

        attribute = attributes.pop(rdfID);
        if (attribute != null) {
            // reify the triple
            IRINode proxy = graph.getNodeIRI(normalizeIRI(resource, baseURI, "#" + attribute.getNodeValue()));
            register(proxy, RDF.rdfType, graph.getNodeIRI(RDF.rdfStatement));
            register(proxy, RDF.rdfSubject, subject);
            register(proxy, RDF.rdfPredicate, property);
            register(proxy, RDF.rdfObject, value);
        }
    }

    /**
     * Loads the specified RDF empty property
     *
     * @param node    The XML node representing the property
     * @param subject The current RDF subject
     * @param baseURI The current base URI
     * @param lang    The current language
     */
    private void loadElementPropertyEmpty(org.w3c.dom.Node node, SubjectNode subject, String baseURI, String lang) {
        IRINode property = getProperty(node, baseURI);
        XMLAttributes attributes = new XMLAttributes(node);
        org.w3c.dom.Node attributeID = attributes.pop(rdfID);
        org.w3c.dom.Node attribute;

        if (attributes.count() == 0) {
            LiteralNode value = graph.getLiteralNode("", OWLDatatype.rdfLangString, lang);
            register(subject, property, value);
            if (attributeID != null) {
                // reify the triple
                IRINode proxy = graph.getNodeIRI(normalizeIRI(resource, baseURI, "#" + attributeID.getNodeValue()));
                register(proxy, RDF.rdfType, graph.getNodeIRI(RDF.rdfStatement));
                register(proxy, RDF.rdfSubject, subject);
                register(proxy, RDF.rdfPredicate, property);
                register(proxy, RDF.rdfObject, value);
            }
        } else {
            SubjectNode value = null;
            attribute = attributes.pop(rdfResource);
            if (attribute != null) {
                value = graph.getNodeIRI(normalizeIRI(resource, baseURI, attribute.getNodeValue()));
            }
            attribute = attributes.pop(rdfNodeID);
            if (attribute != null) {
                value = getBlank(attribute.getNodeValue());
            }
            if (value == null) {
                value = graph.getBlankNode();
            }

            attribute = attributes.pop(rdfType);
            if (attribute != null) {
                register(subject, RDF.rdfType, graph.getNodeIRI(normalizeIRI(resource, baseURI, attribute.getNodeValue())));
            }

            for (org.w3c.dom.Node att : attributes) {
                if (!isValidPropertyAttribute(att))
                    throw new IllegalArgumentException("Unexpected property attribute node " + att.getNodeName());
                IRINode subProperty = graph.getNodeIRI(getIRIFromNSName(baseURI, att.getNodeName()));
                LiteralNode literal;
                if (lang != null)
                    literal = graph.getLiteralNode(att.getNodeValue(), OWLDatatype.rdfLangString, lang);
                else
                    literal = graph.getLiteralNode(att.getNodeValue(), OWLDatatype.xsdString, null);
                register(value, subProperty, literal);
            }

            register(subject, property, value);
            if (attributeID != null) {
                // reify the triple
                IRINode proxy = graph.getNodeIRI(normalizeIRI(resource, baseURI, "#" + attributeID.getNodeValue()));
                register(proxy, RDF.rdfType, graph.getNodeIRI(RDF.rdfStatement));
                register(proxy, RDF.rdfSubject, subject);
                register(proxy, RDF.rdfPredicate, property);
                register(proxy, RDF.rdfObject, value);
            }
        }
    }

    /**
     * Loads the specified XML literal RDF property
     *
     * @param node    The XML node representing the property
     * @param subject The current RDF subject
     * @param baseURI The current base URI
     * @param lang    The current language
     */
    private void loadElementPropertyLiteralParseType(org.w3c.dom.Node node, SubjectNode subject, String baseURI, String lang) {
        // XML Literal datatype
        node.getTextContent();
    }

    /**
     * Loads the specified resource RDF property
     *
     * @param node    The XML node representing the property
     * @param subject The current RDF subject
     * @param baseURI The current base URI
     * @param lang    The current language
     */
    private void loadElementPropertyResourceParseType(org.w3c.dom.Node node, SubjectNode subject, String baseURI, String lang) {
        IRINode property = getProperty(node, baseURI);
        SubjectNode value = graph.getBlankNode();
        register(subject, property, value);

        XMLAttributes attributes = new XMLAttributes(node);
        org.w3c.dom.Node attributeID = attributes.pop(rdfID);
        if (attributeID != null) {
            // reify the triple
            IRINode proxy = graph.getNodeIRI(normalizeIRI(resource, baseURI, "#" + attributeID.getNodeValue()));
            register(proxy, RDF.rdfType, graph.getNodeIRI(RDF.rdfStatement));
            register(proxy, RDF.rdfSubject, subject);
            register(proxy, RDF.rdfPredicate, property);
            register(proxy, RDF.rdfObject, value);
        }

        Iterator<Element> children = getXMLChildren(node);
        while (children.hasNext())
            loadElementProperty(children.next(), value, baseURI, lang);
    }

    /**
     * Loads the specified collection RDF property
     *
     * @param node    The XML node representing the property
     * @param subject The current RDF subject
     * @param baseURI The current base URI
     * @param lang    The current language
     */
    private void loadElementPropertyCollectionParseType(org.w3c.dom.Node node, SubjectNode subject, String baseURI, String lang) {
        IRINode property = getProperty(node, baseURI);
        XMLAttributes attributes = new XMLAttributes(node);
        org.w3c.dom.Node attributeID = attributes.pop(rdfID);
        Iterator<Element> children = getXMLChildren(node);

        SubjectNode head;
        if (!children.hasNext()) {
            // no children
            head = graph.getNodeIRI(RDF.rdfNil);
        } else {
            head = graph.getBlankNode();
        }
        register(subject, property, head);
        if (attributeID != null) {
            // reify the triple
            IRINode proxy = graph.getNodeIRI(normalizeIRI(resource, baseURI, "#" + attributeID.getNodeValue()));
            register(proxy, RDF.rdfType, graph.getNodeIRI(RDF.rdfStatement));
            register(proxy, RDF.rdfSubject, subject);
            register(proxy, RDF.rdfPredicate, property);
            register(proxy, RDF.rdfObject, head);
        }

        List<SubjectNode> values = new ArrayList<>();
        while (children.hasNext())
            values.add(loadElement(children.next(), baseURI, lang));
        for (int i = 0; i != values.size() - 1; i++) {
            register(head, RDF.rdfFirst, values.get(i));
            SubjectNode next = graph.getBlankNode();
            register(head, RDF.rdfRest, next);
            head = next;
        }
        register(head, RDF.rdfFirst, values.get(values.size() - 1));
        register(head, RDF.rdfRest, graph.getNodeIRI(RDF.rdfNil));
    }


    /**
     * Gets the normalized IRI for the specified local name
     *
     * @param baseURI The current base URI
     * @param value   A local name (ns:name)
     * @return The normalized IRI
     */
    private String getIRIFromNSName(String baseURI, String value) {
        value = unescape(value);
        if (!value.contains(":"))
            return normalizeIRI(resource, baseURI, value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return normalizeIRI(resource, baseURI, uri + name);
                }
            }
            index++;
        }
        throw new IllegalArgumentException("Failed to resolve local name " + value);
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
     * Registers the triple with the specified values
     *
     * @param subject  The triple's subject
     * @param property The triples's property
     * @param value    The triples's value
     */
    private void register(SubjectNode subject, String property, Node value) {
        triples.add(new Triple(ontology, subject, graph.getNodeIRI(property), value));
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
