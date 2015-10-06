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
import java.util.*;

/**
 * Represents a loader of RDFT syntax
 *
 * @author Laurent Wouters
 */
public class RDFTLoader implements Loader {
    /**
     * The RDF store to create nodes from
     */
    private final NodeManager store;
    /**
     * The loaded rules
     */
    private List<Rule> rules;
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
     * The graph node for the source
     */
    private GraphNode graphSource;
    /**
     * The graph node for the meta
     */
    private GraphNode graphMeta;
    /**
     * The graph node for the target
     */
    private GraphNode graphTarget;
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
    public RDFTLoader(NodeManager store) {
        this.store = store;
    }

    /**
     * Setups this loader regarding the source, meta and target graphs for the rules
     *
     * @param source The graph node for the source
     * @param meta   The graph node for the meta
     * @param target The graph node for the target
     */
    public void setup(GraphNode source, GraphNode meta, GraphNode target) {
        graphSource = source;
        graphMeta = meta;
        graphTarget = target;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = Files.read(reader);
            RDFTLexer lexer = new RDFTLexer(content);
            RDFTParser parser = new RDFTParser(lexer);
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
        rules = result.getRules();
        if (graphMeta == null)
            graphMeta = store.getIRINode(NodeManager.META_GRAPH);
        if (graphTarget == null)
            graphTarget = store.getIRINode(NodeManager.INFERENCE_GRAPH);
        resource = resourceIRI;
        baseURI = resource;
        namespaces = new HashMap<>();
        blanks = new HashMap<>();

        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        try {
            loadDocument(parseResult.getRoot());
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
        return result;
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads the document represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadDocument(ASTNode node) throws LoaderException {
        for (ASTNode child : node.getChildren().get(0).getChildren())
            loadDirective(child);
        for (ASTNode child : node.getChildren().get(1).getChildren())
            loadRule(child);
    }

    /**
     * Loads the directive represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadDirective(ASTNode node) {
        switch (node.getSymbol().getID()) {
            case RDFTParser.ID.prefixID:
            case RDFTParser.ID.sparqlPrefix:
                loadPrefixID(node);
                break;
            case RDFTParser.ID.base:
            case RDFTParser.ID.sparqlBase:
                loadBase(node);
                break;
        }
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
     * Loads the rule represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadRule(ASTNode node) throws LoaderException {
        String name = null;
        switch (node.getChildren().get(0).getSymbol().getID()) {
            case RDFTLexer.ID.IRIREF: {
                name = node.getChildren().get(0).getValue();
                name = IOUtils.unescape(name.substring(1, name.length() - 1));
                name = URIUtils.resolveRelative(baseURI, name);
                break;
            }
            case RDFTLexer.ID.PNAME_LN: {
                name = node.getChildren().get(0).getValue();
                name = getIRIForLocalName(node.getChildren().get(0), name);
                break;
            }
            case RDFTLexer.ID.PNAME_NS: {
                name = node.getChildren().get(0).getValue();
                name = IOUtils.unescape(name.substring(0, name.length() - 1));
                name = namespaces.get(name);
                break;
            }
        }

        boolean distinct = false;
        for (ASTNode child : node.getChildren().get(1).getChildren()) {
            if (child.getSymbol().getID() == RDFTLexer.ID.DISTINCT)
                distinct = true;
        }

        Rule rule = new Rule(name, distinct);
        Map<String, VariableNode> variables = new HashMap<>();

        // add the antecedents
        for (ASTNode child : node.getChildren().get(2).getChildren()) {
            boolean positive = true;
            boolean meta = false;
            Collection<Quad> quads = new ArrayList<>();
            for (ASTNode element : child.getChildren()) {
                switch (element.getSymbol().getID()) {
                    case RDFTLexer.ID.MARKER_NOT:
                        positive = false;
                        break;
                    case RDFTLexer.ID.MARKER_META:
                        meta = true;
                        break;
                    default:
                        quads.add(new Quad(
                                meta ? graphMeta : graphSource,
                                (SubjectNode) getNode(element.getChildren().get(0), variables),
                                (Property) getNode(element.getChildren().get(1), variables),
                                getNode(element.getChildren().get(2), variables)
                        ));
                        break;
                }
            }
            if (positive) {
                if (meta)
                    rule.getAntecedentMetaPositives().addAll(quads);
                else
                    rule.getAntecedentSourcePositives().addAll(quads);
            } else {
                if (meta)
                    rule.getAntecedentMetaNegatives().add(quads);
                else
                    rule.getAntecedentSourceNegatives().add(quads);
            }
        }

        // add the consequents
        for (ASTNode child : node.getChildren().get(3).getChildren()) {
            boolean positive = true;
            boolean meta = false;
            Collection<Quad> quads = new ArrayList<>();
            for (ASTNode element : child.getChildren()) {
                switch (element.getSymbol().getID()) {
                    case RDFTLexer.ID.MARKER_NOT:
                        positive = false;
                        break;
                    case RDFTLexer.ID.MARKER_META:
                        meta = true;
                        break;
                    default:
                        quads.add(new Quad(
                                meta ? graphMeta : graphTarget,
                                (SubjectNode) getNode(element.getChildren().get(0), variables),
                                (Property) getNode(element.getChildren().get(1), variables),
                                getNode(element.getChildren().get(2), variables)
                        ));
                        break;
                }
            }
            if (positive) {
                if (meta)
                    rule.getConsequentMetaPositives().addAll(quads);
                else
                    rule.getConsequentTargetPositives().addAll(quads);
            } else {
                if (meta)
                    rule.getConsequentMetaNegatives().addAll(quads);
                else
                    rule.getConsequentTargetNegatives().addAll(quads);
            }
        }
        rules.add(rule);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node      An AST node
     * @param variables The current variables
     * @return The equivalent RDF nodes
     */
    private Node getNode(ASTNode node, Map<String, VariableNode> variables) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case RDFTLexer.ID.IRIREF:
                return getNodeIRIRef(node);
            case RDFTLexer.ID.PNAME_LN:
                return getNodePNameLN(node);
            case RDFTLexer.ID.PNAME_NS:
                return getNodePNameNS(node);
            case RDFTLexer.ID.BLANK_NODE_LABEL:
                return getNodeBlank(node);
            case RDFTLexer.ID.ANON:
                return getNodeAnon();
            case 0x0045: // true
                return getNodeTrue();
            case 0x0046: // false
                return getNodeFalse();
            case RDFTLexer.ID.INTEGER:
                return getNodeInteger(node);
            case RDFTLexer.ID.DECIMAL:
                return getNodeDecimal(node);
            case RDFTLexer.ID.DOUBLE:
                return getNodeDouble(node);
            case RDFTParser.ID.rdfLiteral:
                return getNodeLiteral(node);
            case RDFTLexer.ID.QVAR:
                return getNodeVariable(node, variables);
        }
        throw new LoaderException("Unexpected node " + node.getValue(), node);
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
            case RDFTLexer.ID.STRING_LITERAL_SINGLE_QUOTE:
            case RDFTLexer.ID.STRING_LITERAL_QUOTE:
                value = childString.getValue();
                value = value.substring(1, value.length() - 1);
                value = IOUtils.unescape(value);
                break;
            case RDFTLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
            case RDFTLexer.ID.STRING_LITERAL_LONG_QUOTE:
                value = childString.getValue();
                value = value.substring(3, value.length() - 3);
                value = IOUtils.unescape(value);
                break;
        }

        // No suffix, this is a naked string
        if (node.getChildren().size() <= 1)
            return store.getLiteralNode(value, Vocabulary.xsdString, null);

        ASTNode suffixChild = node.getChildren().get(1);
        if (suffixChild.getSymbol().getID() == RDFTLexer.ID.LANGTAG) {
            // This is a language-tagged string
            String tag = suffixChild.getValue();
            return store.getLiteralNode(value, Vocabulary.rdfLangString, tag.substring(1));
        } else if (suffixChild.getSymbol().getID() == RDFTLexer.ID.IRIREF) {
            // Datatype is specified with an IRI
            String iri = suffixChild.getValue();
            iri = IOUtils.unescape(iri.substring(1, iri.length() - 1));
            return store.getLiteralNode(value, URIUtils.resolveRelative(baseURI, iri), null);
        } else if (suffixChild.getSymbol().getID() == RDFTLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(suffixChild, suffixChild.getValue());
            return store.getLiteralNode(value, local, null);
        } else if (suffixChild.getSymbol().getID() == RDFTLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getValue();
            ns = IOUtils.unescape(ns.substring(0, ns.length() - 1));
            ns = namespaces.get(ns);
            return store.getLiteralNode(value, ns, null);
        }
        throw new LoaderException("Unexpected node " + node.getValue(), node);
    }

    /**
     * Gets the RDF variable node equivalent to the specified AST node
     *
     * @param node      An AST node
     * @param variables The current variables
     * @return The equivalent RDF variable node
     */
    private VariableNode getNodeVariable(ASTNode node, Map<String, VariableNode> variables) {
        String name = node.getValue();
        name = name.substring(1);
        VariableNode variable = variables.get(name);
        if (variable == null) {
            variable = new VariableNode(name);
            variables.put(name, variable);
        }
        return variable;
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param node  The parent AST node
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
}
