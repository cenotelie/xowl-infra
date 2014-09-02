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

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xowl.store.Vocabulary;
import org.xowl.utils.collections.Adapter;
import org.xowl.utils.collections.AdaptingIterator;
import org.xowl.utils.collections.Couple;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Represents an XML element with its contextual information
 *
 * @author Laurent Wouters
 */
class XMLElement {
    /**
     * Reserved core syntax terms
     */
    private static final String[] RESERVED_CORE_SYNTAX_TERMS = new String[]{
            Vocabulary.rdfRDF,
            Vocabulary.rdfID,
            Vocabulary.rdfAbout,
            Vocabulary.rdfParseType,
            Vocabulary.rdfResource,
            Vocabulary.rdfNodeID,
            Vocabulary.rdfDatatype
    };
    /**
     * Reserved old terms
     */
    private static final String[] RESERVED_OLD_TERMS = new String[]{
            Vocabulary.rdf + "aboutEach",
            Vocabulary.rdf + "aboutEachPrefix",
            Vocabulary.rdf + "bagID"
    };

    /**
     * The parent contextual element
     */
    private XMLElement parent;
    /**
     * The represented XML element node
     */
    private Element node;
    /**
     * The node complete IRI
     */
    private String nodeIRI;
    /**
     * The current list of attributes
     */
    private Map<String, Node> attributes;
    /**
     * IRI of the document's parent
     */
    private String resource;
    /**
     * Maps of the current namespaces
     */
    private Map<String, String> namespaces;
    /**
     * The current XML namespace
     */
    private String currentNamespace;
    /**
     * The current base URI
     */
    private String baseURI;
    /**
     * The current language
     */
    private String language;

    /**
     * Gets the IRI of this node
     *
     * @return The IRI of this node
     */
    public String getNodeIRI() {
        return nodeIRI;
    }

    /**
     * Gets the resource's IRI
     *
     * @return The resource's IRI
     */
    public String getResourceIRI() {
        return resource;
    }

    /**
     * Gets the current base URI
     *
     * @return The current base URI
     */
    public String getBaseURI() {
        return baseURI;
    }

    /**
     * Gets the current language
     *
     * @return The current language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Gets whether this node is empty, i.e. it has no children at all
     *
     * @return <code>true</code> if this node is empty
     */
    public boolean isEmpty() {
        for (int i = 0; i != node.getChildNodes().getLength(); i++) {
            int type = node.getChildNodes().item(i).getNodeType();
            switch (type) {
                case Node.ELEMENT_NODE:
                case Node.TEXT_NODE:
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets the index of this node relative to its siblings
     *
     * @return The index of this node
     */
    public int getIndex() {
        Iterator<XMLElement> siblings = parent.getChildren();
        int index = 1;
        while (siblings.hasNext()) {
            if (siblings.next().node == this.node)
                return index;
            index++;
        }
        return 0;
    }

    /**
     * Gets the inner text content
     *
     * @return The inner text content
     */
    public String getContent() {
        return node.getChildNodes().item(0).getNodeValue();
    }

    /**
     * Gets the XML literal representation of the content of this node
     *
     * @return The XML literal representation of the content of this node
     */
    public String getXMLLiteral() {
        try {
            Init.init();
            Canonicalizer canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS);
            byte[] buffer = canonicalizer.canonicalizeXPathNodeSet(node.getChildNodes());
            String value = new String(buffer, Charset.forName("UTF-8"));
            return value;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unsupported literal node", ex);
        }
    }

    /**
     * Initializes this root node
     *
     * @param node     The represented XML node
     * @param resource The resource's URI
     */
    public XMLElement(Element node, String resource) {
        this.node = node;
        this.resource = resource;
        this.namespaces = new HashMap<>();
        init();
    }

    /**
     * Initializes this node with a parent node
     *
     * @param parent The parent contextual node
     * @param node   The represented XML node
     */
    public XMLElement(XMLElement parent, Element node) {
        this.parent = parent;
        this.node = node;
        this.resource = parent.resource;
        this.namespaces = new HashMap<>();
        this.currentNamespace = parent.currentNamespace;
        this.baseURI = parent.baseURI;
        this.language = parent.language;
        init();
    }

    /**
     * Initializes the contextual data
     */
    private void init() {
        List<Node> userAttributes = new ArrayList<>();
        for (int i = 0; i != node.getAttributes().getLength(); i++) {
            org.w3c.dom.Node attribute = node.getAttributes().item(i);
            String name = attribute.getNodeName();
            if ("xml:lang".equals(name)) {
                language = attribute.getNodeValue();
            } else if ("xml:base".equals(name)) {
                baseURI = sanitizeBaseURI(attribute.getNodeValue());
            } else if ("xmlns".equals(name)) {
                currentNamespace = attribute.getNodeValue();
            } else if (name.startsWith("xmlns:")) {
                namespaces.put(name.substring(6), attribute.getNodeValue());
            } else if (!name.startsWith("xml")) {
                userAttributes.add(attribute);
            }
        }

        nodeIRI = resolveLocalName(node.getNodeName());

        attributes = new HashMap<>();
        for (org.w3c.dom.Node attribute : userAttributes) {
            String name = attribute.getNodeName();
            name = resolveLocalName(name);
            attributes.put(name, attribute);
        }
    }

    /**
     * Sanitizes the specified base URI
     * @param value A base URI
     * @return The equivalent sanitized base URI
     */
    private String sanitizeBaseURI(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            String authority = uri.getRawAuthority();
            String path = uri.getRawPath();
            String query = uri.getRawQuery();
            if (path == null || path.isEmpty())
                path = "/";
            return (new URI(scheme, authority, path, query, null)).toString();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed base URI", ex);
        }
    }

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
     * Determines whether this element is a valid RDF resource element
     *
     * @return <code>true</code> if the node is valid
     */
    public boolean isValidElement() {
        return (!contains(RESERVED_CORE_SYNTAX_TERMS, nodeIRI) && !contains(RESERVED_OLD_TERMS, nodeIRI) && !Vocabulary.rdfLI.equals(nodeIRI));
    }

    /**
     * Determines whether this element is a valid RDF property element
     *
     * @return <code>true</code> if the node is valid
     */
    public boolean isValidPropertyElement() {
        return (!contains(RESERVED_CORE_SYNTAX_TERMS, nodeIRI) && !contains(RESERVED_OLD_TERMS, nodeIRI) && !Vocabulary.rdfDescription.equals(nodeIRI));
    }

    /**
     * Determines whether the specified IRI is a valid RDF property attribute
     *
     * @param iri A XML node's IRI
     * @return <code>true</code> if the node is valid
     */
    public boolean isValidPropertyAttribute(String iri) {
        return (!contains(RESERVED_CORE_SYNTAX_TERMS, iri) && !contains(RESERVED_OLD_TERMS, iri) && !Vocabulary.rdfLI.equals(iri) && !Vocabulary.rdfDescription.equals(iri));
    }

    /**
     * Resolves the specified local name with the contextual elements of this node and its ancestors
     *
     * @param localName A local name
     * @return The resulting resolved URI
     */
    private String resolveLocalName(String localName) {
        XMLElement current = this;
        localName = Utils.unescape(localName);
        if (!localName.contains(":"))
            return currentNamespace + localName;
        while (current != null) {
            int index = 0;
            while (index != localName.length()) {
                if (localName.charAt(index) == ':') {
                    String prefix = localName.substring(0, index);
                    String uri = current.namespaces.get(prefix);
                    if (uri != null) {
                        String name = localName.substring(index + 1);
                        return Utils.normalizeIRI(resource, baseURI, uri + name);
                    }
                }
                index++;
            }
            current = current.parent;
        }
        throw new IllegalArgumentException("Failed to resolve local name " + localName);
    }

    /**
     * Resolves a possibly relative IRI against this element
     *
     * @param iri A possibly relative IRI
     * @return The resolved and normalized IRI
     */
    public String resolve(String iri) {
        return Utils.normalizeIRI(resource, baseURI, iri);
    }

    /**
     * Gets the attributes with the specified name and removes it
     *
     * @param name An attributes's name
     * @return The corresponding attribute, or <code>null</code> if none is found
     */
    public String getAttribute(String name) {
        Node attribute = attributes.get(name);
        if (attribute != null)
            attributes.remove(name);
        return (attribute != null) ? attribute.getNodeValue() : null;
    }

    /**
     * Gets an iterator over the attributes of this element
     *
     * @return An iterator over the remaining attributes of this element
     */
    public Iterator<Couple<String, String>> getAttributes() {
        return new AdaptingIterator<>(attributes.keySet().iterator(), new Adapter<Couple<String, String>>() {
            @Override
            public <X> Couple<String, String> adapt(X element) {
                Couple<String, String> result = new Couple<>();
                result.x = (String) element;
                result.y = attributes.get(element).getNodeValue();
                return result;
            }
        });
    }

    /**
     * Gets an iterator over the Element children of the specified XML node
     *
     * @return An iterator over the Element children
     */
    public Iterator<XMLElement> getChildren() {
        final NodeList list = node.getChildNodes();
        return new Iterator<XMLElement>() {
            int index = getNext(0);

            private int getNext(int start) {
                for (int i = start; i != list.getLength(); i++)
                    if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                        return i;
                return list.getLength();
            }

            @Override
            public boolean hasNext() {
                return (index != list.getLength());
            }

            @Override
            public XMLElement next() {
                Element result = (Element) list.item(index);
                index = getNext(index + 1);
                return new XMLElement(XMLElement.this, result);
            }
        };
    }
}
