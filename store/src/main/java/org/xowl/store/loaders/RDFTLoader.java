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
import org.xowl.store.Vocabulary;
import org.xowl.store.rdf.*;
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
    private final RDFStore store;
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
    public RDFTLoader(RDFStore store) {
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
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String uri) {
        RDFLoaderResult result = new RDFLoaderResult();
        rules = result.getRules();
        if (graphMeta == null)
            graphMeta = store.getDefaultGraph();
        if (graphTarget == null)
            graphTarget = store.getDefaultGraph();
        resource = uri;
        baseURI = resource;
        namespaces = new HashMap<>();
        blanks = new HashMap<>();

        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        loadDocument(parseResult.getRoot());
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
    private void loadDocument(ASTNode node) {
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
        uri = Utils.unescape(uri.substring(1, uri.length() - 1));
        namespaces.put(prefix, uri);
    }

    /**
     * Loads the base URI represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadBase(ASTNode node) {
        String value = node.getChildren().get(0).getValue();
        value = Utils.unescape(value.substring(1, value.length() - 1));
        baseURI = Utils.uriResolveRelative(baseURI, value);
    }

    /**
     * Loads the rule represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadRule(ASTNode node) {
        String name = null;
        switch (node.getChildren().get(0).getSymbol().getID()) {
            case RDFTLexer.ID.IRIREF: {
                name = node.getChildren().get(0).getValue();
                name = Utils.unescape(name.substring(1, name.length() - 1));
                name = Utils.uriResolveRelative(baseURI, name);
                break;
            }
            case RDFTLexer.ID.PNAME_LN: {
                name = node.getChildren().get(0).getValue();
                name = getIRIForLocalName(name);
                break;
            }
            case RDFTLexer.ID.PNAME_NS: {
                name = node.getChildren().get(0).getValue();
                name = Utils.unescape(name.substring(0, name.length() - 1));
                name = namespaces.get(name);
                break;
            }
        }
        Rule rule = new Rule(name);
        Map<String, VariableNode> variables = new HashMap<>();

        // add the antecedents
        for (ASTNode child : node.getChildren().get(1).getChildren()) {
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
    private Node getNode(ASTNode node, Map<String, VariableNode> variables) {
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
        throw new IllegalArgumentException("Unexpected node " + node.getValue());
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private IRINode getNodeIRIRef(ASTNode node) {
        String value = node.getValue();
        value = Utils.unescape(value.substring(1, value.length() - 1));
        return store.getNodeIRI(Utils.uriResolveRelative(baseURI, value));
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node (local name)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private IRINode getNodePNameLN(ASTNode node) {
        String value = node.getValue();
        return store.getNodeIRI(getIRIForLocalName(value));
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node (namespace)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private IRINode getNodePNameNS(ASTNode node) {
        String value = node.getValue();
        value = Utils.unescape(value.substring(0, value.length() - 1));
        value = namespaces.get(value);
        return store.getNodeIRI(value);
    }

    /**
     * Gets the RDF blank node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF blank node
     */
    private BlankNode getNodeBlank(ASTNode node) {
        String value = node.getValue();
        value = Utils.unescape(value.substring(2));
        BlankNode blank = blanks.get(value);
        if (blank != null)
            return blank;
        blank = store.newNodeBlank();
        blanks.put(value, blank);
        return blank;
    }

    /**
     * Gets a new (anonymous) blank node
     *
     * @return A new blank node
     */
    private BlankNode getNodeAnon() {
        return store.newNodeBlank();
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
    private LiteralNode getNodeLiteral(ASTNode node) {
        // Compute the lexical value
        String value = null;
        ASTNode childString = node.getChildren().get(0);
        switch (childString.getSymbol().getID()) {
            case RDFTLexer.ID.STRING_LITERAL_SINGLE_QUOTE:
            case RDFTLexer.ID.STRING_LITERAL_QUOTE:
                value = childString.getValue();
                value = value.substring(1, value.length() - 1);
                value = Utils.unescape(value);
                break;
            case RDFTLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
            case RDFTLexer.ID.STRING_LITERAL_LONG_QUOTE:
                value = childString.getValue();
                value = value.substring(3, value.length() - 3);
                value = Utils.unescape(value);
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
            iri = Utils.unescape(iri.substring(1, iri.length() - 1));
            return store.getLiteralNode(value, Utils.uriResolveRelative(baseURI, iri), null);
        } else if (suffixChild.getSymbol().getID() == RDFTLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(suffixChild.getValue());
            return store.getLiteralNode(value, local, null);
        } else if (suffixChild.getSymbol().getID() == RDFTLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getValue();
            ns = Utils.unescape(ns.substring(0, ns.length() - 1));
            ns = namespaces.get(ns);
            return store.getLiteralNode(value, ns, null);
        }
        throw new IllegalArgumentException("Unexpected node " + node.getValue());
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
     * @param value An escaped local name
     * @return The equivalent full IRI
     */
    private String getIRIForLocalName(String value) {
        value = Utils.unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return Utils.uriResolveRelative(baseURI, Utils.unescape(uri + name));
                }
            }
            index++;
        }
        throw new IllegalArgumentException("Failed to resolve local name " + value);
    }
}
