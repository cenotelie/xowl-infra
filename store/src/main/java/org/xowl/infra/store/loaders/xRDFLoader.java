/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
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
 ******************************************************************************/

package org.xowl.infra.store.loaders;

import fr.cenotelie.commons.utils.IOUtils;
import fr.cenotelie.commons.utils.TextUtils;
import fr.cenotelie.commons.utils.http.URIUtils;
import fr.cenotelie.commons.utils.logging.Logger;
import fr.cenotelie.hime.redist.*;
import org.xowl.infra.store.IRIs;
import org.xowl.infra.store.RepositoryRDF;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.execution.EvaluableExpression;
import org.xowl.infra.store.execution.ExecutionManager;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.sparql.GraphPattern;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Implements a loader of xRDF syntax
 */
public class xRDFLoader implements Loader {
    /**
     * The RDF nodes management
     */
    private final DatasetNodes nodes;
    /**
     * The execution manager to use
     */
    private final ExecutionManager executionManager;
    /**
     * The inner SPARQL loader
     */
    private final SPARQLLoader sparql;
    /**
     * Map of the current namespaces
     */
    private final Map<String, String> namespaces;
    /**
     * The base URI for relative URIs
     */
    private String baseURI;
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
     * The prepared source for the prologue
     */
    private String prologueSource;

    /**
     * Initializes this loader
     */
    public xRDFLoader() {
        this(new RepositoryRDF());
    }

    /**
     * Initializes this loader
     *
     * @param repository The repository to use
     */
    public xRDFLoader(RepositoryRDF repository) {
        this(repository.getStore().getTransaction().getDataset(), repository.getExecutionManager());
    }

    /**
     * Initializes this loader
     *
     * @param store            The RDF nodes management
     * @param executionManager The execution manager to use
     */
    public xRDFLoader(DatasetNodes store, ExecutionManager executionManager) {
        this.nodes = store;
        this.executionManager = executionManager;
        this.sparql = new SPARQLLoader(store);
        this.namespaces = new HashMap<>();
    }

    /**
     * Re-serializes the specified AST node into a string for the Clojure reader
     *
     * @param node An AST node
     * @return The serialized Clojure source
     */
    private static String serializeClojure(ASTNode node) {
        StringBuilder builder = new StringBuilder();
        serializeClojure(builder, node);
        return builder.toString();
    }

    /**
     * Re-serializes the specified AST node into a string for the Clojure reader
     *
     * @param builder The string builder for the result
     * @param node    An AST node
     */
    private static void serializeClojure(StringBuilder builder, ASTNode node) {
        switch (node.getSymbol().getID()) {
            case xRDFLexer.ID.CLJ_SYMBOL:
            case xRDFLexer.ID.CLJ_KEYWORD:
            case xRDFLexer.ID.LITERAL_STRING:
            case xRDFLexer.ID.LITERAL_CHAR:
            case xRDFLexer.ID.LITERAL_NIL:
            case xRDFLexer.ID.LITERAL_TRUE:
            case xRDFLexer.ID.LITERAL_FALSE:
            case xRDFLexer.ID.LITERAL_INTEGER:
            case xRDFLexer.ID.LITERAL_FLOAT:
            case xRDFLexer.ID.LITERAL_RATIO:
            case xRDFLexer.ID.LITERAL_ARGUMENT:
                builder.append(node.getValue());
                break;
            case xRDFParser.ID.clj_list:
                builder.append("( ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append(")");
                break;
            case xRDFParser.ID.clj_vector:
                builder.append("[ ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append("]");
                break;
            case xRDFParser.ID.clj_map:
                builder.append("{ ");
                for (ASTNode couple : node.getChildren()) {
                    serializeClojure(builder, couple.getChildren().get(0));
                    serializeClojure(builder, couple.getChildren().get(1));
                }
                builder.append("}");
                break;
            case xRDFParser.ID.clj_set:
                builder.append("#{ ");
                for (ASTNode child : node.getChildren())
                    serializeClojure(builder, child);
                builder.append("}");
                break;
            case xRDFParser.ID.clj_constructor:
                builder.append("#");
                serializeClojure(builder, node.getChildren().get(0));
                serializeClojure(builder, node.getChildren().get(1));
                break;
            case xRDFParser.ID.clj_quote:
                builder.append("'");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_deref:
                builder.append("@");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_metadata:
                builder.append("^");
                serializeClojure(builder, node.getChildren().get(0));
                serializeClojure(builder, node.getChildren().get(1));
                break;
            case xRDFParser.ID.clj_regexp:
                builder.append("#");
                builder.append(node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_var_quote:
                builder.append("#'");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_anon_function:
                builder.append("#");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_ignore:
                builder.append("#_");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_syntax_quote:
                builder.append("`");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_unquote:
                builder.append("~");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_unquote_splicing:
                builder.append("~@");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            case xRDFParser.ID.clj_conditional:
                builder.append("#?");
                serializeClojure(builder, node.getChildren().get(0));
                break;
            default:
                throw new Error("Unsupported construct: " + node.getSymbol().getName());
        }
        builder.append(" ");
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = IOUtils.read(reader);
            xRDFLexer lexer = new xRDFLexer(content);
            xRDFParser parser = new xRDFParser(lexer);
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
        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        try {
            baseURI = resourceIRI;
            graph = nodes.getIRINode(graphIRI);
            return loadDocument(parseResult.getRoot(), parseResult.getInput());
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

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads the document represented by the specified AST node
     *
     * @param node  An AST node
     * @param input The input text that was parsed
     * @return The loaded elements
     * @throws LoaderException When failing to load the input
     */
    private RDFLoaderResult loadDocument(ASTNode node, Text input) throws LoaderException {
        RDFLoaderResult result = new RDFLoaderResult();
        sparql.loadPrologue(node.getChildren().get(0));
        loadPrologue(node.getChildren().get(0));
        prologueSource = generatePrologueSource();
        for (ASTNode nodeElement : node.getChildren().get(1).getChildren()) {
            switch (nodeElement.getSymbol().getID()) {
                case xRDFParser.ID.xowl_triples: {
                    loadTriples(new SPARQLContext(nodes), nodeElement, graph, result.getQuads());
                    break;
                }
                case xRDFParser.ID.xowl_graph_anon: {
                    loadGraphContent(new SPARQLContext(nodes), nodeElement, graph, result.getQuads());
                    break;
                }
                case xRDFParser.ID.xowl_graph_named: {
                    loadGraphNamed(new SPARQLContext(nodes), nodeElement, result.getQuads());
                    break;
                }
                case xRDFParser.ID.xowl_rule_simple: {
                    result.getRules().add(loadRuleSimple(nodeElement, input));
                    break;
                }
                case xRDFParser.ID.xowl_rule_sparql: {
                    result.getRules().add(loadRuleSPARQL(nodeElement, input));
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Loads the prologue from the corresponding AST node
     *
     * @param node The AST node
     */
    private void loadPrologue(ASTNode node) {
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case xRDFParser.ID.decl_base:
                    loadBase(child);
                    break;
                case xRDFParser.ID.decl_prefix:
                    loadPrefixID(child);
                    break;
            }
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
        uri = TextUtils.unescape(uri.substring(1, uri.length() - 1));
        namespaces.put(prefix, uri);
    }

    /**
     * Loads the base URI represented by the specified AST node
     *
     * @param node An AST node
     */
    private void loadBase(ASTNode node) {
        String value = node.getChildren().get(0).getValue();
        value = TextUtils.unescape(value.substring(1, value.length() - 1));
        baseURI = URIUtils.resolveRelative(baseURI, value);
    }

    /**
     * Generates the source for the prologue
     *
     * @return The source for the prologue
     */
    private String generatePrologueSource() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            builder.append("PREFIX ");
            builder.append(entry.getKey());
            builder.append(": <");
            builder.append(TextUtils.escapeAbsoluteURIW3C(entry.getValue()));
            builder.append(">");
            builder.append(IOUtils.LINE_SEPARATOR);
        }
        if (baseURI != null) {
            builder.append("BASE <");
            builder.append(TextUtils.escapeAbsoluteURIW3C(baseURI));
            builder.append(">");
            builder.append(IOUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    /**
     * Loads the content of graph from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @throws LoaderException When failing to load the input
     */
    protected void loadGraphContent(SPARQLContext context, ASTNode node, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        for (ASTNode child : node.getChildren())
            loadTriples(context, child, graph, buffer);
    }

    /**
     * Loads a named graph from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param buffer  The buffer of quads
     * @throws LoaderException When failing to load the input
     */
    protected void loadGraphNamed(SPARQLContext context, ASTNode node, Collection<Quad> buffer) throws LoaderException {
        GraphNode graph = (GraphNode) getNode(node.getChildren().get(0), context, this.graph, buffer);
        loadGraphContent(context, node.getChildren().get(1), graph, buffer);
    }

    /**
     * Loads quads from triples in the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @throws LoaderException When failing to load the input
     */
    private void loadTriples(SPARQLContext context, ASTNode node, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        if (node.getChildren().get(0).getSymbol().getID() == xRDFParser.ID.xowl_blank_property_list) {
            // the subject is a blank node
            BlankNode subject = getNodeBlankWithProperties(context, node.getChildren().get(0), graph, buffer);
            if (node.getChildren().size() > 1)
                applyProperties(subject, node.getChildren().get(1), context, graph, buffer);
        } else {
            Node subject = getNode(node.getChildren().get(0), context, graph, buffer);
            applyProperties((SubjectNode) subject, node.getChildren().get(1), context, graph, buffer);
        }
    }

    /**
     * Gets the RDF blank node (with its properties) equivalent to the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @return The equivalent RDF blank node
     * @throws LoaderException When failing to load the input
     */
    private BlankNode getNodeBlankWithProperties(SPARQLContext context, ASTNode node, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        BlankNode subject = nodes.getBlankNode();
        applyProperties(subject, node, context, graph, buffer);
        return subject;
    }

    /**
     * Applies the RDF verbs and properties described in the specified AST node to the given RDF subject node
     *
     * @param subject An RDF subject node
     * @param node    An AST node
     * @param context The current context
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @throws LoaderException When failing to load the input
     */
    private void applyProperties(SubjectNode subject, ASTNode node, SPARQLContext context, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        int index = 0;
        List<ASTNode> children = node.getChildren();
        while (index != children.size()) {
            Property verb = (Property) getNode(children.get(index), context, graph, buffer);
            for (ASTNode objectNode : children.get(index + 1).getChildren()) {
                Node object = getNode(objectNode, context, graph, buffer);
                buffer.add(new Quad(graph, subject, verb, object));
            }
            index += 2;
        }
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The current context
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @return The equivalent RDF nodes
     * @throws LoaderException When failing to load the input
     */
    private Node getNode(ASTNode node, SPARQLContext context, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case xRDFLexer.ID.A:
                return getNodeIsA();
            case xRDFLexer.ID.IRIREF:
                return getNodeIRIRef(node);
            case xRDFLexer.ID.PNAME_LN:
                return getNodePNameLN(node);
            case xRDFLexer.ID.PNAME_NS:
                return getNodePNameNS(node);
            case xRDFLexer.ID.BLANK_NODE_LABEL:
                return getNodeBlank(node, context);
            case xRDFLexer.ID.ANON:
                return getNodeAnon();
            case xRDFLexer.ID.TRUE:
                return getNodeTrue();
            case xRDFLexer.ID.FALSE:
                return getNodeFalse();
            case xRDFLexer.ID.INTEGER:
                return getNodeInteger(node);
            case xRDFLexer.ID.DECIMAL:
                return getNodeDecimal(node);
            case xRDFLexer.ID.DOUBLE:
                return getNodeDouble(node);
            case xRDFParser.ID.literal_rdf:
                return getNodeLiteral(node);
            case xRDFLexer.ID.VARIABLE:
                return getNodeVariable(node, context);
            case xRDFLexer.ID.XOWL_OPAQUE_EXP:
                return getNodeDynamic(node);
            case xRDFParser.ID.xowl_collection:
                return getNodeCollection(node, context, graph, buffer);
            case xRDFParser.ID.xowl_blank_property_list:
                return getNodeBlankWithProperties(context, node, graph, buffer);
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
            cacheIsA = nodes.getIRINode(Vocabulary.rdfType);
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
        value = TextUtils.unescape(value.substring(1, value.length() - 1));
        return nodes.getIRINode(URIUtils.resolveRelative(baseURI, value));
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node (local name)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     * @throws LoaderException When failing to load the input
     */
    private IRINode getNodePNameLN(ASTNode node) throws LoaderException {
        String value = node.getValue();
        return nodes.getIRINode(getIRIForLocalName(node, value));
    }

    /**
     * Gets the RDF IRI node equivalent to the specified AST node (namespace)
     *
     * @param node An AST node
     * @return The equivalent RDF IRI node
     */
    private IRINode getNodePNameNS(ASTNode node) {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(0, value.length() - 1));
        value = namespaces.get(value);
        return nodes.getIRINode(value);
    }

    /**
     * Gets the RDF blank node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The current context
     * @return The equivalent RDF blank node
     */
    private Node getNodeBlank(ASTNode node, SPARQLContext context) {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(2));
        return context.resolveBlankNode(value);
    }

    /**
     * Gets a new (anonymous) blank node
     *
     * @return A new blank node
     */
    private BlankNode getNodeAnon() {
        return nodes.getBlankNode();
    }

    /**
     * Gets the RDF Literal node for the boolean true value
     *
     * @return The RDF Literal node
     */
    private LiteralNode getNodeTrue() {
        if (cacheTrue == null)
            cacheTrue = nodes.getLiteralNode("true", Vocabulary.xsdBoolean, null);
        return cacheTrue;
    }

    /**
     * Gets the RDF Literal node for the boolean false value
     *
     * @return The RDF Literal node
     */
    private LiteralNode getNodeFalse() {
        if (cacheFalse == null)
            cacheFalse = nodes.getLiteralNode("false", Vocabulary.xsdBoolean, null);
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
        return nodes.getLiteralNode(value, Vocabulary.xsdInteger, null);
    }

    /**
     * Gets the RDF Decimal Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Decimal Literal node
     */
    private LiteralNode getNodeDecimal(ASTNode node) {
        String value = node.getValue();
        return nodes.getLiteralNode(value, Vocabulary.xsdDecimal, null);
    }

    /**
     * Gets the RDF Double Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Double Literal node
     */
    private LiteralNode getNodeDouble(ASTNode node) {
        String value = node.getValue();
        return nodes.getLiteralNode(value, Vocabulary.xsdDouble, null);
    }

    /**
     * Gets the RDF Literal node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Literal node
     * @throws LoaderException When failing to load the input
     */
    private LiteralNode getNodeLiteral(ASTNode node) throws LoaderException {
        // Compute the lexical value
        String value = null;
        ASTNode childString = node.getChildren().get(0);
        switch (childString.getSymbol().getID()) {
            case xRDFLexer.ID.STRING_LITERAL_SINGLE_QUOTE:
            case xRDFLexer.ID.STRING_LITERAL_QUOTE:
                value = childString.getValue();
                value = value.substring(1, value.length() - 1);
                value = TextUtils.unescape(value);
                break;
            case xRDFLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
            case xRDFLexer.ID.STRING_LITERAL_LONG_QUOTE:
                value = childString.getValue();
                value = value.substring(3, value.length() - 3);
                value = TextUtils.unescape(value);
                break;
        }

        // No suffix, this is a naked string
        if (node.getChildren().size() <= 1)
            return nodes.getLiteralNode(value, Vocabulary.xsdString, null);

        ASTNode suffixChild = node.getChildren().get(1);
        if (suffixChild.getSymbol().getID() == xRDFLexer.ID.LANGTAG) {
            // This is a language-tagged string
            String tag = suffixChild.getValue();
            return nodes.getLiteralNode(value, Vocabulary.rdfLangString, tag.substring(1));
        } else if (suffixChild.getSymbol().getID() == xRDFLexer.ID.IRIREF) {
            // Datatype is specified with an IRI
            String iri = suffixChild.getValue();
            iri = TextUtils.unescape(iri.substring(1, iri.length() - 1));
            return nodes.getLiteralNode(value, URIUtils.resolveRelative(baseURI, iri), null);
        } else if (suffixChild.getSymbol().getID() == xRDFLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(suffixChild, suffixChild.getValue());
            return nodes.getLiteralNode(value, local, null);
        } else if (suffixChild.getSymbol().getID() == xRDFLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getValue();
            ns = TextUtils.unescape(ns.substring(0, ns.length() - 1));
            ns = namespaces.get(ns);
            return nodes.getLiteralNode(value, ns, null);
        }
        throw new LoaderException("Unexpected node " + node.getValue(), node);
    }

    /**
     * Gets the variable node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The current context
     * @return The equivalent variable node
     */
    private Node getNodeVariable(ASTNode node, SPARQLContext context) {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(1));
        return context.resolveVariable(value);
    }

    /**
     * Gets the dynamic node equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent dynamic node
     */
    private DynamicNode getNodeDynamic(ASTNode node) {
        return nodes.getDynamicNode(executionManager.loadExpression(serializeClojure(node.getChildren().get(0))));
    }

    /**
     * Gets the RDF list node equivalent to the specified AST node representing a collection of RDF nodes
     *
     * @param node    An AST node
     * @param context The current context
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @return A RDF list node
     * @throws LoaderException When failing to load the input
     */
    private Node getNodeCollection(ASTNode node, SPARQLContext context, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        List<Node> elements = new ArrayList<>();
        for (ASTNode child : node.getChildren())
            elements.add(getNode(child, context, graph, buffer));
        if (elements.isEmpty())
            return nodes.getIRINode(Vocabulary.rdfNil);

        BlankNode[] proxies = new BlankNode[elements.size()];
        for (int i = 0; i != proxies.length; i++) {
            proxies[i] = nodes.getBlankNode();
            buffer.add(new Quad(graph, proxies[i], nodes.getIRINode(Vocabulary.rdfFirst), elements.get(i)));
        }
        for (int i = 0; i != proxies.length - 1; i++) {
            buffer.add(new Quad(graph, proxies[i], nodes.getIRINode(Vocabulary.rdfRest), proxies[i + 1]));
        }
        buffer.add(new Quad(graph, proxies[proxies.length - 1], nodes.getIRINode(Vocabulary.rdfRest), nodes.getIRINode(Vocabulary.rdfNil)));
        return proxies[0];
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param node  The parent ASt node
     * @param value An escaped local name
     * @return The equivalent full IRI
     * @throws LoaderException When failing to load the input
     */
    private String getIRIForLocalName(ASTNode node, String value) throws LoaderException {
        value = TextUtils.unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return URIUtils.resolveRelative(baseURI, uri + name);
                }
            }
            index++;
        }
        throw new LoaderException("Failed to resolve local name " + value, node);
    }

    /**
     * Loads a simple rule
     *
     * @param node  The AST node
     * @param input The input text that was parsed
     * @return The loaded rule
     * @throws LoaderException When failing to load the input
     */
    private RDFRule loadRuleSimple(ASTNode node, Text input) throws LoaderException {
        // initial context setup
        SPARQLContext context = new SPARQLContext(nodes, true);
        GraphNode graph = (GraphNode) context.resolveVariable("__graph");
        // load basic info
        boolean isDistinct = (node.getChildren().get(1).getChildren().size() > 0);
        String iri = sparql.getNodeIRI(node.getChildren().get(2)).getIRIValue();
        int sourceBegin = node.getChildren().get(0).getSpan().getIndex();
        int sourceEnd = node.getChildren().get(6).getSpan().getIndex() + 1;
        String source = input.getValue(sourceBegin, sourceEnd - sourceBegin);

        // load the guard
        EvaluableExpression guard = null;
        if (!node.getChildren().get(4).getChildren().isEmpty())
            guard = executionManager.loadExpression(serializeClojure(node.getChildren().get(4).getChildren().get(0)));
        RDFRuleSimple result = new RDFRuleSimple(iri, isDistinct, guard, prologueSource + source);

        // load the antecedents
        Collection<Quad> positives = new ArrayList<>();
        Collection<Collection<Quad>> negatives = new ArrayList<>();
        for (ASTNode part : node.getChildren().get(3).getChildren())
            loadRulePart(context, part, graph, positives, negatives);
        for (Quad quad : positives)
            result.addAntecedentPositive(quad);
        for (Collection<Quad> conjunction : negatives)
            result.addAntecedentNegatives(conjunction);
        positives.clear();
        negatives.clear();

        // load the consequents
        graph = nodes.getIRINode(IRIs.GRAPH_INFERENCE);
        for (ASTNode part : node.getChildren().get(5).getChildren())
            loadRulePart(context, part, graph, positives, negatives);
        for (Quad quad : positives)
            result.addConsequentPositive(quad);
        for (Collection<Quad> conjunction : negatives)
            for (Quad quad : conjunction)
                result.addConsequentNegative(quad);
        return result;
    }

    /**
     * Loads a rule that is base on a SPARQL query
     *
     * @param node  The AST node
     * @param input The input text that was parsed
     * @return The loaded rule
     * @throws LoaderException When failing to load the input
     */
    private RDFRule loadRuleSPARQL(ASTNode node, Text input) throws LoaderException {
        // initial context setup
        SPARQLContext context = new SPARQLContext(nodes, true);
        GraphNode graph = (GraphNode) context.resolveVariable("__graph");
        // load basic info
        String iri = sparql.getNodeIRI(node.getChildren().get(2)).getIRIValue();
        int sourceBegin = node.getChildren().get(0).getSpan().getIndex();
        int sourceEnd = node.getChildren().get(6).getSpan().getIndex() + 1;
        String source = input.getValue(sourceBegin, sourceEnd - sourceBegin);

        // load the antecedents
        GraphPattern pattern = sparql.loadGraphPatternSubSelect(context, graph, node.getChildren().get(3));

        // load the guard
        EvaluableExpression guard = null;
        if (!node.getChildren().get(4).getChildren().isEmpty())
            guard = executionManager.loadExpression(serializeClojure(node.getChildren().get(4).getChildren().get(0)));
        RDFRuleSelect result = new RDFRuleSelect(iri, pattern, guard, prologueSource + source);

        // load the consequents
        Collection<Quad> positives = new ArrayList<>();
        Collection<Collection<Quad>> negatives = new ArrayList<>();
        graph = nodes.getIRINode(IRIs.GRAPH_INFERENCE);
        for (ASTNode part : node.getChildren().get(5).getChildren())
            loadRulePart(context, part, graph, positives, negatives);
        for (Quad quad : positives)
            result.addConsequentPositive(quad);
        for (Collection<Quad> conjunction : negatives)
            for (Quad quad : conjunction)
                result.addConsequentNegative(quad);
        return result;
    }

    /**
     * Loads a part of a rule
     *
     * @param context   The current context
     * @param node      The AST node to load from
     * @param graph     The current graph
     * @param positives The buffer of positive quads
     * @param negatives The buffer of negative quads
     * @throws LoaderException When failing to load the input
     */
    private void loadRulePart(SPARQLContext context, ASTNode node, GraphNode graph, Collection<Quad> positives, Collection<Collection<Quad>> negatives) throws LoaderException {
        Collection<Quad> buffer = positives;
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case xRDFLexer.ID.NOT: {
                    buffer = new ArrayList<>();
                    negatives.add(buffer);
                    break;
                }
                case xRDFLexer.ID.META: {
                    graph = nodes.getIRINode(IRIs.GRAPH_META);
                    break;
                }
                case xRDFParser.ID.xowl_triples: {
                    loadTriples(context, child, graph, buffer);
                    break;
                }
                case xRDFParser.ID.xowl_graph_anon: {
                    loadGraphContent(context, child, graph, buffer);
                    break;
                }
                case xRDFParser.ID.xowl_graph_named: {
                    loadGraphNamed(context, child, buffer);
                    break;
                }
            }
        }
    }
}
