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
import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Couple;

import java.io.Reader;
import java.util.*;

/**
 * Loader for RDF XML sources
 *
 * @author Laurent Wouters
 */
public class RDFXMLLoader extends Loader {
    /**
     * The RDF graph to load into
     */
    private RDFGraph graph;
    /**
     * The loaded triples
     */
    private List<Triple> triples;
    /**
     * Maps of the blanks nodes
     */
    private Map<String, BlankNode> blanks;
    /**
     * The current ontology
     */
    private Ontology ontology;
    /**
     * List of all the known IDs so far
     */
    private List<String> knownIDs;

    /**
     * Initializes this loader
     *
     * @param graph The graph to load in
     */
    public RDFXMLLoader(RDFGraph graph) {
        this.graph = graph;
    }


    /**
     * Determines whether a specified name is a valid XML name
     *
     * @param name A name
     * @return <code>true</code> if the name is valid
     */
    public static boolean isValidXMLName(String name) {
        if (name.isEmpty())
            return false;
        if (!isValidXMLNameFirstChar(name.charAt(0)))
            return false;
        for (int i = 1; i != name.length(); i++)
            if (!isValidXMLNameChar(name.charAt(i)))
                return false;
        return true;
    }

    /**
     * Determines whether a specified character is a valid first character in an XML name
     *
     * @param c A character
     * @return <code>true</code> if the character is valid
     */
    private static boolean isValidXMLNameFirstChar(char c) {
        if (c >= 'a' && c <= 'z')
            return true;
        if (c >= 'A' && c <= 'Z')
            return true;
        if (c == '_')
            return true;
        if (c >= 0xC0 && c <= 0xD6)
            return true;
        if (c >= 0xD8 && c <= 0xF6)
            return true;
        if (c >= 0xF8 && c <= 0x2FF)
            return true;
        if (c >= 0x370 && c <= 0x37D)
            return true;
        if (c >= 0x37F && c <= 0x1FFF)
            return true;
        if (c >= 0x200C && c <= 0x200D)
            return true;
        if (c >= 0x2070 && c <= 0x218F)
            return true;
        if (c >= 0x2C00 && c <= 0x2FEF)
            return true;
        if (c >= 0x3001 && c <= 0xD7FF)
            return true;
        if (c >= 0xF900 && c <= 0xFDCF)
            return true;
        if (c >= 0xFDF0 && c <= 0xFFFD)
            return true;
        return false;
    }

    /**
     * Determines whether a specified character is a valid character in an XML name
     *
     * @param c A character
     * @return <code>true</code> if the character is valid
     */
    private static boolean isValidXMLNameChar(char c) {
        if (isValidXMLNameFirstChar(c))
            return true;
        // "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
        if (c == '-' || c == '.')
            return true;
        if (c >= '0' && c <= '9')
            return true;
        if (c >= 0x0300 && c <= 0x036F)
            return true;
        if (c >= 0x203F && c <= 0x2040)
            return true;
        return false;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ontology load(Logger logger, java.io.Reader reader, String uri) {
        triples = new ArrayList<>();
        ontology = Utils.createNewOntology();
        blanks = new HashMap<>();
        knownIDs = new ArrayList<>();

        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(reader));
            Document document = parser.getDocument();
            XMLElement root = new XMLElement(document.getDocumentElement(), uri);
            if (Vocabulary.rdfRDF.equals(root.getNodeIRI()))
                loadDocument(root);
            else
                loadElement(root);
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
     * @param element A RDF document node
     */
    private void loadDocument(XMLElement element) {
        Iterator<XMLElement> children = element.getChildren();
        while (children.hasNext())
            loadElement(children.next());
    }

    /**
     * Loads the specified RDF resource node
     *
     * @param element A RDF resource node
     * @return The represented RDF node
     */
    private SubjectNode loadElement(XMLElement element) {
        if (!element.isValidElement())
            throw new IllegalArgumentException("Unexpected resource element " + element.getNodeIRI());

        SubjectNode subject = null;

        String attribute = element.getAttribute(Vocabulary.rdfID);
        boolean hasID = false;
        if (attribute != null) {
            if (!isValidXMLName(attribute))
                throw new IllegalArgumentException("Illegal rdf:ID " + attribute);
            if (knownIDs.contains(attribute))
                throw new IllegalArgumentException("Duplicate rdf:ID " + attribute);
            hasID = true;
            knownIDs.add(attribute);
            String iri = Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), "#" + attribute);
            subject = graph.getNodeIRI(iri);
        }
        attribute = element.getAttribute(Vocabulary.rdfNodeID);
        if (attribute != null) {
            if (hasID)
                throw new IllegalArgumentException("Node cannot have both rdf:ID and rdf:nodeID attributes");
            if (!isValidXMLName(attribute))
                throw new IllegalArgumentException("Illegal rdf:nodeID " + attribute);
            subject = getBlank(attribute);
        }
        attribute = element.getAttribute(Vocabulary.rdfAbout);
        if (attribute != null) {
            if (hasID)
                throw new IllegalArgumentException("Node cannot have both rdf:ID and rdf:about attributes");
            String iri = Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), attribute);
            subject = graph.getNodeIRI(iri);
        }

        if (subject == null)
            subject = graph.getBlankNode();

        if (!Vocabulary.rdfDescription.equals(element.getNodeIRI())) {
            register(subject, Vocabulary.rdfType, graph.getNodeIRI(element.getNodeIRI()));
        }

        attribute = element.getAttribute(Vocabulary.rdfType);
        if (attribute != null) {
            register(subject, Vocabulary.rdfType, graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), attribute)));
        }

        Iterator<Couple<String, String>> attributes = element.getAttributes();
        while (attributes.hasNext()) {
            Couple<String, String> couple = attributes.next();
            if (!element.isValidPropertyAttribute(couple.x))
                throw new IllegalArgumentException("Unexpected property attribute node " + couple.x);
            IRINode property = graph.getNodeIRI(couple.x);
            LiteralNode literal;
            if (element.getLanguage() != null)
                literal = graph.getLiteralNode(couple.y, Vocabulary.rdfLangString, element.getLanguage());
            else
                literal = graph.getLiteralNode(couple.y, Vocabulary.xsdString, null);
            register(subject, property, literal);
        }

        Iterator<XMLElement> children = element.getChildren();
        while (children.hasNext())
            loadElementProperty(children.next(), subject);

        return subject;
    }

    /**
     * Loads the specified RDF property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementProperty(XMLElement element, SubjectNode subject) {
        if (!element.isValidPropertyElement())
            throw new IllegalArgumentException("Unexpected property element node " + element.getNodeIRI());

        String attribute = element.getAttribute(Vocabulary.rdfParseType);
        if (attribute == null) {
            if (element.isEmpty()) {
                loadElementPropertyEmpty(element, subject);
            } else {
                Iterator<XMLElement> children = element.getChildren();
                if (children.hasNext()) {
                    loadElementPropertyResource(element, subject);
                } else {
                    loadElementPropertyLiteral(element, subject);
                }
            }
        } else {
            if ("Literal".equals(attribute)) {
                loadElementPropertyLiteralParseType(element, subject);
            } else if ("Resource".equals(attribute)) {
                loadElementPropertyResourceParseType(element, subject);
            } else if ("Collection".equals(attribute)) {
                loadElementPropertyCollectionParseType(element, subject);
            } else {
                loadElementPropertyLiteralParseType(element, subject);
            }
        }
    }

    /**
     * Gets the property IRI node for the specified XML node representing an element property
     *
     * @param element An XML node representing an RDF property
     * @return The equivalent property IRI node
     */
    private IRINode getProperty(XMLElement element) {
        if (Vocabulary.rdfLI.equals(element.getNodeIRI())) {
            int index = element.getIndex();
            return graph.getNodeIRI(Vocabulary.rdf + "_" + Integer.toString(index));
        } else {
            return graph.getNodeIRI(element.getNodeIRI());
        }
    }

    /**
     * Loads the specified RDF property pointing to a resource
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyResource(XMLElement element, SubjectNode subject) {
        IRINode property = getProperty(element);
        Iterator<XMLElement> children = element.getChildren();
        SubjectNode value = loadElement(children.next());
        register(subject, property, value);

        String attribute = element.getAttribute(Vocabulary.rdfID);
        if (attribute != null) {
            if (!isValidXMLName(attribute))
                throw new IllegalArgumentException("Illegal rdf:ID " + attribute);
            if (knownIDs.contains(attribute))
                throw new IllegalArgumentException("Duplicate rdf:ID " + attribute);
            knownIDs.add(attribute);
            // reify the triple
            IRINode proxy = graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), "#" + attribute));
            register(proxy, Vocabulary.rdfType, graph.getNodeIRI(Vocabulary.rdfStatement));
            register(proxy, Vocabulary.rdfSubject, subject);
            register(proxy, Vocabulary.rdfPredicate, property);
            register(proxy, Vocabulary.rdfObject, value);
        }
    }

    /**
     * Loads the specified RDF literal property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyLiteral(XMLElement element, SubjectNode subject) {
        IRINode property = getProperty(element);
        String lexem = element.getContent();
        LiteralNode value;
        String attribute = element.getAttribute(Vocabulary.rdfDatatype);
        if (attribute != null) {
            value = graph.getLiteralNode(lexem, attribute, null);
        } else if (element.getLanguage() != null) {
            value = graph.getLiteralNode(lexem, Vocabulary.rdfLangString, element.getLanguage());
        } else {
            value = graph.getLiteralNode(lexem, Vocabulary.xsdString, null);
        }
        register(subject, property, value);

        attribute = element.getAttribute(Vocabulary.rdfID);
        if (attribute != null) {
            if (!isValidXMLName(attribute))
                throw new IllegalArgumentException("Illegal rdf:ID " + attribute);
            if (knownIDs.contains(attribute))
                throw new IllegalArgumentException("Duplicate rdf:ID " + attribute);
            knownIDs.add(attribute);
            // reify the triple
            IRINode proxy = graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), "#" + attribute));
            register(proxy, Vocabulary.rdfType, graph.getNodeIRI(Vocabulary.rdfStatement));
            register(proxy, Vocabulary.rdfSubject, subject);
            register(proxy, Vocabulary.rdfPredicate, property);
            register(proxy, Vocabulary.rdfObject, value);
        }
    }

    /**
     * Loads the specified RDF empty property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyEmpty(XMLElement element, SubjectNode subject) {
        IRINode property = getProperty(element);
        String attributeID = element.getAttribute(Vocabulary.rdfID);
        String attribute;
        Iterator<Couple<String, String>> attributes = element.getAttributes();

        if (!attributes.hasNext()) {
            LiteralNode value = graph.getLiteralNode("", Vocabulary.rdfLangString, element.getLanguage());
            register(subject, property, value);
            if (attributeID != null) {
                if (!isValidXMLName(attributeID))
                    throw new IllegalArgumentException("Illegal rdf:ID " + attributeID);
                if (knownIDs.contains(attributeID))
                    throw new IllegalArgumentException("Duplicate rdf:ID " + attributeID);
                knownIDs.add(attributeID);
                // reify the triple
                IRINode proxy = graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), "#" + attributeID));
                register(proxy, Vocabulary.rdfType, graph.getNodeIRI(Vocabulary.rdfStatement));
                register(proxy, Vocabulary.rdfSubject, subject);
                register(proxy, Vocabulary.rdfPredicate, property);
                register(proxy, Vocabulary.rdfObject, value);
            }
        } else {
            SubjectNode value = null;
            attribute = element.getAttribute(Vocabulary.rdfResource);
            if (attribute != null) {
                value = graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), attribute));
            }
            attribute = element.getAttribute(Vocabulary.rdfNodeID);
            if (attribute != null) {
                if (attributeID != null)
                    throw new IllegalArgumentException("Node cannot have both rdf:ID and rdf:nodeID attributes");
                if (!isValidXMLName(attribute))
                    throw new IllegalArgumentException("Illegal rdf:nodeID " + attribute);
                value = getBlank(attribute);
            }
            if (value == null) {
                value = graph.getBlankNode();
            }

            attribute = element.getAttribute(Vocabulary.rdfType);
            if (attribute != null) {
                register(subject, Vocabulary.rdfType, graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), attribute)));
            }

            attributes = element.getAttributes();
            while (attributes.hasNext()) {
                Couple<String, String> att = attributes.next();
                if (!element.isValidPropertyAttribute(att.x))
                    throw new IllegalArgumentException("Unexpected property attribute node " + att.x);
                IRINode subProperty = graph.getNodeIRI(att.x);
                LiteralNode literal;
                if (element.getLanguage() != null)
                    literal = graph.getLiteralNode(att.y, Vocabulary.rdfLangString, element.getLanguage());
                else
                    literal = graph.getLiteralNode(att.y, Vocabulary.xsdString, null);
                register(value, subProperty, literal);
            }

            register(subject, property, value);
            if (attributeID != null) {
                // reify the triple
                IRINode proxy = graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), "#" + attributeID));
                register(proxy, Vocabulary.rdfType, graph.getNodeIRI(Vocabulary.rdfStatement));
                register(proxy, Vocabulary.rdfSubject, subject);
                register(proxy, Vocabulary.rdfPredicate, property);
                register(proxy, Vocabulary.rdfObject, value);
            }
        }
    }

    /**
     * Loads the specified XML literal RDF property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyLiteralParseType(XMLElement element, SubjectNode subject) {
        // XML Literal datatype
    }

    /**
     * Loads the specified resource RDF property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyResourceParseType(XMLElement element, SubjectNode subject) {
        IRINode property = getProperty(element);
        SubjectNode value = graph.getBlankNode();
        register(subject, property, value);
        String attributeID = element.getAttribute(Vocabulary.rdfID);
        if (attributeID != null) {
            if (!isValidXMLName(attributeID))
                throw new IllegalArgumentException("Illegal rdf:ID " + attributeID);
            if (knownIDs.contains(attributeID))
                throw new IllegalArgumentException("Duplicate rdf:ID " + attributeID);
            knownIDs.add(attributeID);
            // reify the triple
            IRINode proxy = graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), "#" + attributeID));
            register(proxy, Vocabulary.rdfType, graph.getNodeIRI(Vocabulary.rdfStatement));
            register(proxy, Vocabulary.rdfSubject, subject);
            register(proxy, Vocabulary.rdfPredicate, property);
            register(proxy, Vocabulary.rdfObject, value);
        }

        Iterator<XMLElement> children = element.getChildren();
        while (children.hasNext())
            loadElementProperty(children.next(), value);
    }

    /**
     * Loads the specified collection RDF property
     *
     * @param element The XML node representing the property
     * @param subject The current RDF subject
     */
    private void loadElementPropertyCollectionParseType(XMLElement element, SubjectNode subject) {
        IRINode property = getProperty(element);
        String attributeID = element.getAttribute(Vocabulary.rdfID);
        Iterator<XMLElement> children = element.getChildren();

        SubjectNode head;
        if (!children.hasNext()) {
            // no children
            head = graph.getNodeIRI(Vocabulary.rdfNil);
        } else {
            head = graph.getBlankNode();
        }
        register(subject, property, head);
        if (attributeID != null) {
            if (!isValidXMLName(attributeID))
                throw new IllegalArgumentException("Illegal rdf:ID " + attributeID);
            if (knownIDs.contains(attributeID))
                throw new IllegalArgumentException("Duplicate rdf:ID " + attributeID);
            knownIDs.add(attributeID);
            // reify the triple
            IRINode proxy = graph.getNodeIRI(Utils.normalizeIRI(element.getResourceIRI(), element.getBaseURI(), "#" + attributeID));
            register(proxy, Vocabulary.rdfType, graph.getNodeIRI(Vocabulary.rdfStatement));
            register(proxy, Vocabulary.rdfSubject, subject);
            register(proxy, Vocabulary.rdfPredicate, property);
            register(proxy, Vocabulary.rdfObject, head);
        }

        List<SubjectNode> values = new ArrayList<>();
        while (children.hasNext())
            values.add(loadElement(children.next()));
        for (int i = 0; i != values.size() - 1; i++) {
            register(head, Vocabulary.rdfFirst, values.get(i));
            SubjectNode next = graph.getBlankNode();
            register(head, Vocabulary.rdfRest, next);
            head = next;
        }
        register(head, Vocabulary.rdfFirst, values.get(values.size() - 1));
        register(head, Vocabulary.rdfRest, graph.getNodeIRI(Vocabulary.rdfNil));
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
