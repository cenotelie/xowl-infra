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

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.Context;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.lang.owl2.Ontology;
import org.xowl.store.rdf.*;
import org.xowl.store.voc.OWLDatatype;
import org.xowl.store.voc.RDF;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a loader of Turtle syntax
 *
 * @author Laurent Wouters
 */
public class TurtleLoader extends Loader {
    /**
     * Singleton buffer
     */
    private List<RDFNode> singleton;
    /**
     * The RDF graph to load in
     */
    private RDFGraph graph;
    /**
     * The base URI for relative URIs
     */
    private String baseURI;
    /**
     * Map of the current namespaces
     */
    private Map<String, String> namespaces;
    /**
     * Map of the current blank nodes
     */
    private Map<String, RDFBlankNode> blanks;
    /**
     * The current ontology
     */
    private Ontology ontology;
    /**
     * The cached node for the RDF#type property
     */
    private RDFIRIReference cacheIsA;
    /**
     * The cached node for the literal true node
     */
    private RDFLiteralNode cacheTrue;
    /**
     * The cached node for the literal false node
     */
    private RDFLiteralNode cacheFalse;

    /**
     * Initializes this loader
     *
     * @param graph The RDF graph to load in
     */
    public TurtleLoader(RDFGraph graph) {
        this.singleton = new ArrayList<>();
        this.graph = graph;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result = null;
        try {
            String content = Files.read(reader);
            TurtleLexer lexer = new TurtleLexer(content);
            TurtleParser parser = new TurtleParser(lexer);
            parser.setRecover(false);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            Context context = result.getInput().getContext(error.getPosition());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    @Override
    public Ontology load(Logger logger, Reader reader) {
        baseURI = null;
        namespaces = new HashMap<>();
        blanks = new HashMap<>();
        ontology = createNewOntology();

        ParseResult result = parse(logger, reader);
        if (result == null || !result.isSuccess() || result.getErrors().size() > 0)
            return null;

        for (ASTNode node : result.getRoot().getChildren()) {
            switch (node.getSymbol().getID()) {
                case TurtleParser.ID.prefixID:
                case TurtleParser.ID.sparqlPrefix:
                    loadPrefixID(node);
                    break;
                case TurtleParser.ID.base:
                case TurtleParser.ID.sparqlBase:
                    loadBase(node);
                    break;
                default:
                    loadTriples(node);
                    break;
            }
        }

        return ontology;
    }

    /**
     * Loads a prefix and its associated namespace represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadPrefixID(ASTNode node) {
        String prefix = node.getChildren().get(0).getSymbol().getValue();
        String uri = node.getChildren().get(1).getSymbol().getValue();
        prefix = prefix.substring(0, prefix.length() - 1);
        uri = unescape(uri);
        namespaces.put(prefix, uri);
    }

    /**
     * Loads the base URI represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadBase(ASTNode node) {
        String value = node.getChildren().get(0).getSymbol().getValue();
        baseURI = unescape(value);
    }

    /**
     * Loads the triples represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadTriples(ASTNode node) {
        if (node.getChildren().get(0).getSymbol().getID() == TurtleParser.ID.blankNodePropertyList) {
            // the subject is a blank node
            RDFBlankNode subject = getNodeBlankWithProperties(node.getChildren().get(0));
            if (node.getChildren().size() > 1)
                applyProperties(subject, node.getChildren().get(1));
        } else {
            List<RDFNode> subjects = new ArrayList<>(getNodes(node.getChildren().get(0)));
            for (RDFNode subject : subjects) {
                applyProperties((RDFSubjectNode) subject, node.getChildren().get(1));
            }
        }
    }

    /**
     * Gets the list of RDF nodes equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF nodes
     */
    private List<RDFNode> getNodes(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case 0x003C: // a
                return encapsulate(getNodeIsA());
            case TurtleLexer.ID.IRIREF:
                return encapsulate(getNodeIRIRef(node));
            case TurtleLexer.ID.PNAME_LN:
                return encapsulate(getNodePNameLN(node));
            case TurtleLexer.ID.PNAME_NS:
                return encapsulate(getNodePNameNS(node));
            case TurtleLexer.ID.BLANK_NODE_LABEL:
                return encapsulate(getNodeBlank(node));
            case TurtleLexer.ID.ANON:
                return encapsulate(getNodeAnon());
            case 0x0042: // true
                return encapsulate(getNodeTrue());
            case 0x0043: // false
                return encapsulate(getNodeFalse());
            case TurtleLexer.ID.INTEGER:
                return encapsulate(getNodeInteger(node));
            case TurtleLexer.ID.DECIMAL:
                return encapsulate(getNodeDecimal(node));
            case TurtleLexer.ID.DOUBLE:
                return encapsulate(getNodeDouble(node));
            case TurtleParser.ID.rdfLiteral:
                return encapsulate(getNodeLiteral(node));
            case TurtleParser.ID.collection:
                return getNodeCollection(node);
            case TurtleParser.ID.predicateObjectList:
                return encapsulate(getNodeBlankWithProperties(node));
        }
        return null;
    }

    /**
     * Encapsulates the specified node in a singleton list
     *
     * @param single A node
     * @return The list with the specified element as the sole one
     */
    private List<RDFNode> encapsulate(RDFNode single) {
        singleton.set(0, single);
        return singleton;
    }

    /**
     * Gets the RDF IRI node for the RDF type element
     *
     * @return The RDF IRI node
     */
    private RDFIRIReference getNodeIsA() {
        if (cacheIsA == null)
            cacheIsA = graph.getNodeIRI(RDF.rdfType);
        return cacheIsA;
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private RDFIRIReference getNodeIRIRef(ASTNode node) {
        String value = node.getSymbol().getValue();
        return graph.getNodeIRI(getFullIRI(value));
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node (local name)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private RDFIRIReference getNodePNameLN(ASTNode node) {
        String value = node.getSymbol().getValue();
        return graph.getNodeIRI(getIRIForLocalName(value));
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node (namespace)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private RDFIRIReference getNodePNameNS(ASTNode node) {
        String value = node.getSymbol().getValue();
        value = unescape(value);
        value = namespaces.get(value.substring(0, value.length() - 1));
        return graph.getNodeIRI(value);
    }

    /**
     * Gets the RDF blank node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF blank node
     */
    private RDFBlankNode getNodeBlank(ASTNode node) {
        String value = node.getSymbol().getValue();
        value = unescape(value);
        value = value.substring(2);
        RDFBlankNode blank = blanks.get(value);
        if (blank != null)
            return blank;
        blank = graph.getBlankNode();
        blanks.put(value, blank);
        return blank;
    }

    /**
     * Gets a new (anonymous) blank node
     *
     * @return A new blank node
     */
    private RDFBlankNode getNodeAnon() {
        return graph.getBlankNode();
    }

    /**
     * Gets the RDF Literal node for the boolean true value
     *
     * @return The RDF Literal node
     */
    private RDFLiteralNode getNodeTrue() {
        if (cacheTrue == null)
            cacheTrue = graph.getLiteralNode("true", OWLDatatype.xsdBoolean, null);
        return cacheTrue;
    }

    /**
     * Gets the RDF Literal node for the boolean false value
     *
     * @return The RDF Literal node
     */
    private RDFLiteralNode getNodeFalse() {
        if (cacheFalse == null)
            cacheFalse = graph.getLiteralNode("false", OWLDatatype.xsdBoolean, null);
        return cacheFalse;
    }

    /**
     * Gets the RDF Integer Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Integer Literal node
     */
    private RDFLiteralNode getNodeInteger(ASTNode node) {
        String value = node.getSymbol().getValue();
        return graph.getLiteralNode(value, OWLDatatype.xsdInteger, null);
    }

    /**
     * Gets the RDF Decimal Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Decimal Literal node
     */
    private RDFLiteralNode getNodeDecimal(ASTNode node) {
        String value = node.getSymbol().getValue();
        return graph.getLiteralNode(value, OWLDatatype.xsdDecimal, null);
    }

    /**
     * Gets the RDF Double Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Double Literal node
     */
    private RDFLiteralNode getNodeDouble(ASTNode node) {
        String value = node.getSymbol().getValue();
        return graph.getLiteralNode(value, OWLDatatype.xsdDouble, null);
    }

    /**
     * Gets the RDF Literal node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Literal node
     */
    private RDFLiteralNode getNodeLiteral(ASTNode node) {
        // Compute the lexical value
        String value = null;
        ASTNode childString = node.getChildren().get(0);
        switch (childString.getSymbol().getID()) {
            case TurtleLexer.ID.STRING_LITERAL_SINGLE_QUOTE:
            case TurtleLexer.ID.STRING_LITERAL_QUOTE:
                value = unescape(childString.getSymbol().getValue());
                break;
            case TurtleLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
            case TurtleLexer.ID.STRING_LITERAL_LONG_QUOTE:
                value = childString.getSymbol().getValue();
                value = value.substring(2, value.length() - 4);
                value = unescape(value);
                break;
        }

        // No suffix, this is a naked string
        if (node.getChildren().size() <= 1)
            return graph.getLiteralNode(value, OWLDatatype.xsdString, null);

        ASTNode suffixChild = node.getChildren().get(1);
        if (suffixChild.getSymbol().getID() == TurtleLexer.ID.LANGTAG) {
            // This is a language-tagged string
            String tag = suffixChild.getSymbol().getValue();
            return graph.getLiteralNode(value, OWLDatatype.rdfLangString, tag.substring(1));
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.IRIREF) {
            // Datatype is specified with an IRI
            String iri = suffixChild.getSymbol().getValue();
            return graph.getLiteralNode(value, getFullIRI(iri), null);
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(suffixChild.getSymbol().getValue());
            return graph.getLiteralNode(value, getIRIForLocalName(local), null);
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getSymbol().getValue();
            ns = unescape(ns);
            ns = namespaces.get(ns.substring(0, ns.length() - 1));
            return graph.getLiteralNode(value, ns, null);
        }
        return null;
    }

    /**
     * Gets the list of RDF nodes equivalent to the specified AST node representing a collection of RDF nodes
     *
     * @param node An AST node
     * @return A list of RDF nodes
     */
    private List<RDFNode> getNodeCollection(ASTNode node) {
        List<RDFNode> result = new ArrayList<>();
        for (ASTNode child : node.getChildren())
            result.addAll(getNodes(child));
        return result;
    }

    /**
     * Gets the RDF blank node (with its properties) equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF blank node
     */
    private RDFBlankNode getNodeBlankWithProperties(ASTNode node) {
        RDFBlankNode subject = graph.getBlankNode();
        applyProperties(subject, node);
        return subject;
    }

    /**
     * Gets the full IRI for the specified escaped IRI, which may be relative
     *
     * @param iri An escaped IRI that can be relative
     * @return The equivalent full IRI
     */
    private String getFullIRI(String iri) {
        iri = unescape(iri);
        if (!iri.startsWith("http://"))
            return baseURI + iri;
        return iri;
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param value An escaped local name
     * @return The equivalent full IRI
     */
    private String getIRIForLocalName(String value) {
        value = unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return uri + name;
                }
            }
            index++;
        }
        return null;
    }

    /**
     * Applies the RDF verbs and properties described in the specified AST node to the given RDF subject node
     *
     * @param subject An RDF subject node
     * @param node    An AST node
     */
    private void applyProperties(RDFSubjectNode subject, ASTNode node) {
        int index = 0;
        List<ASTNode> children = node.getChildren();
        while (index != children.size()) {
            RDFProperty verb = (RDFProperty) getNodes(children.get(index)).get(0);
            for (ASTNode objectNode : children.get(index + 1).getChildren()) {
                List<RDFNode> objects = new ArrayList<>(getNodes(objectNode));
                for (RDFNode object : objects) {
                    try {
                        graph.add(new RDFTriple(ontology, subject, verb, object));
                    } catch (UnsupportedNodeType ex) {
                        // cannot happen ...
                    }
                }
            }
            index += 2;
        }
    }
}
