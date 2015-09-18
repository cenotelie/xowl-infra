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

package org.xowl.store.loaders;

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.TextContext;
import org.xowl.store.IOUtils;
import org.xowl.store.URIUtils;
import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
import org.xowl.store.storage.NodeManager;
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
public class TurtleLoader implements Loader {
    /**
     * The RDF store to create nodes from
     */
    private final NodeManager store;
    /**
     * The loaded triples
     */
    private List<Quad> quads;
    /**
     * The URI of the resource currently being loaded
     */
    private String resource;
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
    private Map<String, BlankNode> blanks;
    /**
     * The current graph
     */
    private GraphNode graph;
    /**
     * The cached node for the RDF#type property
     */
    private IRINode cacheIsA;
    /**
     * The cached node for the literal true node
     */
    private LiteralNode cacheTrue;
    /**
     * The cached node for the literal false node
     */
    private LiteralNode cacheFalse;

    /**
     * Initializes this loader
     *
     * @param store The RDF store used to create nodes
     */
    public TurtleLoader(NodeManager store) {
        this.store = store;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = Files.read(reader);
            TurtleLexer lexer = new TurtleLexer(content);
            TurtleParser parser = new TurtleParser(lexer);
            parser.setModeRecoverErrors(false);
            result = parser.parse();
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
        for (ParseError error : result.getErrors()) {
            logger.error(error);
            TextContext context = result.getInput().getContext(error.getPosition(), error.getLength());
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    @Override
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String resourceIRI, String graphIRI) {
        RDFLoaderResult result = new RDFLoaderResult();
        quads = result.getQuads();
        graph = store.getIRINode(graphIRI);
        resource = resourceIRI;
        baseURI = resource;
        namespaces = new HashMap<>();
        blanks = new HashMap<>();

        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;

        for (ASTNode node : parseResult.getRoot().getChildren()) {
            try {
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
            } catch (LoaderException exception) {
                logger.error(exception);
                logger.error("@" + exception.getOrigin().getPosition());
                TextContext context = exception.getOrigin().getContext();
                logger.error(context.getContent());
                logger.error(context.getPointer());
                return null;
            } catch (IllegalArgumentException exception) {
                logger.error(exception);
                return null;
            }
        }

        return result;
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads a prefix and its associated namespace represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadPrefixID(ASTNode node) {
        String prefix = node.getChildren().get(0).getValue();
        String uri = node.getChildren().get(1).getValue();
        prefix = prefix.substring(0, prefix.length() - 1);
        uri = IOUtils.unescape(uri.substring(1, uri.length() - 1));
        namespaces.put(prefix, uri);
    }

    /**
     * Loads the base URI represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadBase(ASTNode node) {
        String value = node.getChildren().get(0).getValue();
        value = IOUtils.unescape(value.substring(1, value.length() - 1));
        baseURI = URIUtils.resolveRelative(baseURI, value);
    }

    /**
     * Loads the triples represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadTriples(ASTNode node) throws LoaderException {
        if (node.getChildren().get(0).getSymbol().getID() == TurtleParser.ID.predicateObjectList) {
            // the subject is a blank node
            BlankNode subject = getNodeBlankWithProperties(node.getChildren().get(0));
            if (node.getChildren().size() > 1)
                applyProperties(subject, node.getChildren().get(1));
        } else {
            Node subject = getNode(node.getChildren().get(0));
            applyProperties((SubjectNode) subject, node.getChildren().get(1));
        }
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF nodes
     */
    private Node getNode(ASTNode node) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case 0x003C: // a
                return getNodeIsA();
            case TurtleLexer.ID.IRIREF:
                return getNodeIRIRef(node);
            case TurtleLexer.ID.PNAME_LN:
                return getNodePNameLN(node);
            case TurtleLexer.ID.PNAME_NS:
                return getNodePNameNS(node);
            case TurtleLexer.ID.BLANK_NODE_LABEL:
                return getNodeBlank(node);
            case TurtleLexer.ID.ANON:
                return getNodeAnon();
            case 0x0042: // true
                return getNodeTrue();
            case 0x0043: // false
                return getNodeFalse();
            case TurtleLexer.ID.INTEGER:
                return getNodeInteger(node);
            case TurtleLexer.ID.DECIMAL:
                return getNodeDecimal(node);
            case TurtleLexer.ID.DOUBLE:
                return getNodeDouble(node);
            case TurtleParser.ID.rdfLiteral:
                return getNodeLiteral(node);
            case TurtleParser.ID.collection:
                return getNodeCollection(node);
            case TurtleParser.ID.predicateObjectList:
                return getNodeBlankWithProperties(node);
        }
        throw new LoaderException("Unexpected node " + node.getValue(), node);
    }

    /**
     * Gets the RDF IRI node for the RDF type element
     *
     * @return The RDF IRI node
     */
    private IRINode getNodeIsA() {
        if (cacheIsA == null)
            cacheIsA = store.getIRINode(Vocabulary.rdfType);
        return cacheIsA;
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private IRINode getNodeIRIRef(ASTNode node) {
        String value = node.getValue();
        value = IOUtils.unescape(value.substring(1, value.length() - 1));
        return store.getIRINode(URIUtils.resolveRelative(baseURI, value));
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node (local name)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private IRINode getNodePNameLN(ASTNode node) throws LoaderException {
        String value = node.getValue();
        return store.getIRINode(getIRIForLocalName(node, value));
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node (namespace)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private IRINode getNodePNameNS(ASTNode node) {
        String value = node.getValue();
        value = IOUtils.unescape(value.substring(0, value.length() - 1));
        value = namespaces.get(value);
        return store.getIRINode(value);
    }

    /**
     * Gets the RDF blank node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF blank node
     */
    private BlankNode getNodeBlank(ASTNode node) {
        String value = node.getValue();
        value = IOUtils.unescape(value.substring(2));
        BlankNode blank = blanks.get(value);
        if (blank != null)
            return blank;
        blank = store.getBlankNode();
        blanks.put(value, blank);
        return blank;
    }

    /**
     * Gets a new (anonymous) blank node
     *
     * @return A new blank node
     */
    private BlankNode getNodeAnon() {
        return store.getBlankNode();
    }

    /**
     * Gets the RDF Literal node for the boolean true value
     *
     * @return The RDF Literal node
     */
    private LiteralNode getNodeTrue() {
        if (cacheTrue == null)
            cacheTrue = store.getLiteralNode("true", Vocabulary.xsdBoolean, null);
        return cacheTrue;
    }

    /**
     * Gets the RDF Literal node for the boolean false value
     *
     * @return The RDF Literal node
     */
    private LiteralNode getNodeFalse() {
        if (cacheFalse == null)
            cacheFalse = store.getLiteralNode("false", Vocabulary.xsdBoolean, null);
        return cacheFalse;
    }

    /**
     * Gets the RDF Integer Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Integer Literal node
     */
    private LiteralNode getNodeInteger(ASTNode node) {
        String value = node.getValue();
        return store.getLiteralNode(value, Vocabulary.xsdInteger, null);
    }

    /**
     * Gets the RDF Decimal Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Decimal Literal node
     */
    private LiteralNode getNodeDecimal(ASTNode node) {
        String value = node.getValue();
        return store.getLiteralNode(value, Vocabulary.xsdDecimal, null);
    }

    /**
     * Gets the RDF Double Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Double Literal node
     */
    private LiteralNode getNodeDouble(ASTNode node) {
        String value = node.getValue();
        return store.getLiteralNode(value, Vocabulary.xsdDouble, null);
    }

    /**
     * Gets the RDF Literal node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Literal node
     */
    private LiteralNode getNodeLiteral(ASTNode node) throws LoaderException {
        // Compute the lexical value
        String value = null;
        ASTNode childString = node.getChildren().get(0);
        switch (childString.getSymbol().getID()) {
            case TurtleLexer.ID.STRING_LITERAL_SINGLE_QUOTE:
            case TurtleLexer.ID.STRING_LITERAL_QUOTE:
                value = childString.getValue();
                value = value.substring(1, value.length() - 1);
                value = IOUtils.unescape(value);
                break;
            case TurtleLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
            case TurtleLexer.ID.STRING_LITERAL_LONG_QUOTE:
                value = childString.getValue();
                value = value.substring(3, value.length() - 3);
                value = IOUtils.unescape(value);
                break;
        }

        // No suffix, this is a naked string
        if (node.getChildren().size() <= 1)
            return store.getLiteralNode(value, Vocabulary.xsdString, null);

        ASTNode suffixChild = node.getChildren().get(1);
        if (suffixChild.getSymbol().getID() == TurtleLexer.ID.LANGTAG) {
            // This is a language-tagged string
            String tag = suffixChild.getValue();
            return store.getLiteralNode(value, Vocabulary.rdfLangString, tag.substring(1));
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.IRIREF) {
            // Datatype is specified with an IRI
            String iri = suffixChild.getValue();
            iri = IOUtils.unescape(iri.substring(1, iri.length() - 1));
            return store.getLiteralNode(value, URIUtils.resolveRelative(baseURI, iri), null);
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(suffixChild, suffixChild.getValue());
            return store.getLiteralNode(value, local, null);
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getValue();
            ns = IOUtils.unescape(ns.substring(0, ns.length() - 1));
            ns = namespaces.get(ns);
            return store.getLiteralNode(value, ns, null);
        }
        throw new LoaderException("Unexpected node " + node.getValue(), node);
    }

    /**
     * Gets the RDF list node equivalent to the specified AST node representing a collection of RDF nodes
     *
     * @param node An AST node
     * @return A RDF list node
     */
    private Node getNodeCollection(ASTNode node) throws LoaderException {
        List<Node> elements = new ArrayList<>();
        for (ASTNode child : node.getChildren())
            elements.add(getNode(child));
        if (elements.isEmpty())
            return store.getIRINode(Vocabulary.rdfNil);

        BlankNode[] proxies = new BlankNode[elements.size()];
        for (int i = 0; i != proxies.length; i++) {
            proxies[i] = store.getBlankNode();
            quads.add(new Quad(graph, proxies[i], store.getIRINode(Vocabulary.rdfFirst), elements.get(i)));
        }
        for (int i = 0; i != proxies.length - 1; i++) {
            quads.add(new Quad(graph, proxies[i], store.getIRINode(Vocabulary.rdfRest), proxies[i + 1]));
        }
        quads.add(new Quad(graph, proxies[proxies.length - 1], store.getIRINode(Vocabulary.rdfRest), store.getIRINode(Vocabulary.rdfNil)));
        return proxies[0];
    }

    /**
     * Gets the RDF blank node (with its properties) equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF blank node
     */
    private BlankNode getNodeBlankWithProperties(ASTNode node) throws LoaderException {
        BlankNode subject = store.getBlankNode();
        applyProperties(subject, node);
        return subject;
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param node  The parent ASt node
     * @param value An escaped local name
     * @return The equivalent full IRI
     */
    private String getIRIForLocalName(ASTNode node, String value) throws LoaderException {
        value = IOUtils.unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return URIUtils.resolveRelative(baseURI, IOUtils.unescape(uri + name));
                }
            }
            index++;
        }
        throw new LoaderException("Failed to resolve local name " + value, node);
    }

    /**
     * Applies the RDF verbs and properties described in the specified AST node to the given RDF subject node
     *
     * @param subject An RDF subject node
     * @param node    An AST node
     */
    private void applyProperties(SubjectNode subject, ASTNode node) throws LoaderException {
        int index = 0;
        List<ASTNode> children = node.getChildren();
        while (index != children.size()) {
            Property verb = (Property) getNode(children.get(index));
            for (ASTNode objectNode : children.get(index + 1).getChildren()) {
                Node object = getNode(objectNode);
                quads.add(new Quad(graph, subject, verb, object));
            }
            index += 2;
        }
    }
}
