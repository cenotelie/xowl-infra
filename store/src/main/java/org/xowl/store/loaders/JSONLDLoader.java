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
import org.xowl.utils.collections.Couple;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Loader for JSON LD sources
 *
 * @author Laurent Wouters
 */
public abstract class JSONLDLoader implements Loader {
    /**
     * Markers that forbids the further expansion
     */
    public static final String MARKER_NULL = "@null";
    /**
     * Property which value describes a context
     */
    public static final String KEYWORD_CONTEXT = "@context";
    /**
     * Property that defines the identifier of a JSON-LD object with an URI or a blank node
     */
    public static final String KEYWORD_ID = "@id";
    /**
     * Property that identifies the value of a literal expressed as a JSON object
     */
    public static final String KEYWORD_VALUE = "@value";
    /**
     * Property that identifies the language of a literal expressed as a JSON object
     */
    public static final String KEYWORD_LANGUAGE = "@language";
    /**
     * Property that identifies the type of a datatype property
     */
    public static final String KEYWORD_TYPE = "@type";
    /**
     * Property that defines the type of container of another multi-valued property
     */
    public static final String KEYWORD_CONTAINER = "@container";
    /**
     * Value that identifies the list type of container for a multi-valued property
     */
    public static final String KEYWORD_LIST = "@list";
    /**
     * Value that identifies the set type of container for a multi-valued property
     */
    public static final String KEYWORD_SET = "@set";
    /**
     * Property that specifies that another property is expressed in a reversed form
     */
    public static final String KEYWORD_REVERSE = "@reverse";
    /**
     * Property that specifies the indexing of a propert values
     */
    public static final String KEYWORD_INDEX = "@index";
    /**
     * Property that specifies the base URI for relative ones
     */
    public static final String KEYWORD_BASE = "@base";
    /**
     * Property that specifies a common URI radical for a vocabulary
     */
    public static final String KEYWORD_VOCAB = "@vocab";
    /**
     * Property for the expression of explicit graphs
     */
    public static final String KEYWORD_GRAPH = "@graph";

    /**
     * List of the reversed keywords
     */
    public static final List<String> KEYWORDS = Arrays.asList(
            KEYWORD_CONTEXT,
            KEYWORD_ID,
            KEYWORD_VALUE,
            KEYWORD_LANGUAGE,
            KEYWORD_TYPE,
            KEYWORD_CONTAINER,
            KEYWORD_LIST,
            KEYWORD_SET,
            KEYWORD_REVERSE,
            KEYWORD_INDEX,
            KEYWORD_BASE,
            KEYWORD_VOCAB,
            KEYWORD_GRAPH
    );

    /**
     * The property info for the rdf:type property
     */
    private static final JSONLDNameInfo PROPERTY_TYPE_INFO = new JSONLDNameInfo();

    static {
        PROPERTY_TYPE_INFO.fullIRI = Vocabulary.rdfType;
        PROPERTY_TYPE_INFO.valueType = KEYWORD_ID;
    }

    /**
     * Gets the decoded value of the specified AST node that holds a literal string
     *
     * @param node An AST node that holds a literal string
     * @return The decoded value of the string
     */
    public static String getValue(ASTNode node) throws JSONLDLoadingException {
        switch (node.getSymbol().getID()) {
            case JSONLDLexer.ID.LITERAL_INTEGER:
            case JSONLDLexer.ID.LITERAL_DECIMAL:
            case JSONLDLexer.ID.LITERAL_DOUBLE:
                return node.getValue();
            case JSONLDLexer.ID.LITERAL_STRING:
                String value = node.getValue();
                value = value.substring(1, value.length() - 1);
                return Utils.unescape(value);
            case JSONLDLexer.ID.LITERAL_NULL:
                return null;
            case JSONLDLexer.ID.LITERAL_TRUE:
                return "true";
            case JSONLDLexer.ID.LITERAL_FALSE:
                return "false";
            default:
                throw new JSONLDLoadingException("Unrecognized input " + node.getSymbol().getName(), node);
        }
    }


    /**
     * The RDF store to create nodes from
     */
    private final RDFStore store;
    /**
     * The current logger
     */
    private Logger logger;
    /**
     * The loaded triples
     */
    private List<Quad> quads;
    /**
     * The URI of the resource currently being loaded
     */
    private String resource;
    /**
     * Maps of blanks nodes
     */
    private Map<String, BlankNode> blanks;
    /**
     * Marker for the top-level object
     */
    private boolean markerTopLevel;

    /**
     * Gets the URI of the resource currently being loaded
     *
     * @return The URI of the resource currently being loaded
     */
    public String getCurrentResource() {
        return resource;
    }

    /**
     * Initializes this loader
     *
     * @param store The RDF store used to create nodes
     */
    public JSONLDLoader(RDFStore store) {
        this.store = store;
    }

    @Override
    public ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = Files.read(reader);
            JSONLDLexer lexer = new JSONLDLexer(content);
            JSONLDParser parser = new JSONLDParser(lexer);
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
        this.logger = logger;
        quads = result.getQuads();
        resource = uri;
        blanks = new HashMap<>();
        markerTopLevel = true;
        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        try {
            loadDocument(parseResult.getRoot(), store.getNodeIRI(uri), new JSONLDContext(this));
        } catch (JSONLDLoadingException exception) {
            logger.error("@" + exception.getOrigin().getPosition() + " " + exception.getMessage());
            TextContext context = exception.getOrigin().getContext();
            logger.error(context.getContent());
            logger.error(context.getPointer());
        }
        return result;
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads a context definition in an external document at the specified IRI
     *
     * @param iri The IRI of an auxiliary document
     * @return The parsed external document, or null if an error occured
     */
    protected ASTNode getExternalContextDefinition(String iri) throws JSONLDLoadingException {
        Reader reader = getReaderFor(logger, iri);
        if (reader == null)
            return null;
        ParseResult result = parse(logger, reader);
        if (result == null)
            return null;
        ASTNode root = result.getRoot();
        if (root.getSymbol().getID() != JSONLDParser.ID.object)
            return null;
        for (ASTNode member : root.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            if (KEYWORD_CONTEXT.equals(key))
                return member.getChildren().get(1);
        }
        return null;
    }

    /**
     * Gets a reader for the external document at the specified IRI
     *
     * @param logger The logger to use
     * @param iri    The IRI of an auxiliary document
     * @return The corresponding reader, or null if it cannot be resolved
     */
    protected abstract Reader getReaderFor(Logger logger, String iri);

    /**
     * Loads a JSON-LD document from the specified AST node
     *
     * @param node    The AST node
     * @param graph   The default graph for this document
     * @param context The root context
     */
    private void loadDocument(ASTNode node, GraphNode graph, JSONLDContext context) throws JSONLDLoadingException {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object:
                loadObject(node, graph, context);
                break;
            case JSONLDParser.ID.array:
                loadArray(node, graph, context, null);
                break;
            default:
                throw new JSONLDLoadingException("Unrecognized input " + node.getSymbol().getName(), node);
        }
    }

    /**
     * Loads a JSON-LD object from the specified AST node
     *
     * @param node    The AST node
     * @param graph   The parent graph
     * @param context The parent context
     * @return The corresponding RDF node
     */
    private SubjectNode loadObject(ASTNode node, GraphNode graph, JSONLDContext context) throws JSONLDLoadingException {
        // load the new context
        ASTNode contextNode = null;
        for (ASTNode child : node.getChildren()) {
            String key = getValue(child.getChildren().get(0));
            if (KEYWORD_CONTEXT.equals(key)) {
                contextNode = child.getChildren().get(1);
                break;
            }
        }
        JSONLDContext current = context;
        if (contextNode != null)
            current = new JSONLDContext(context, contextNode);

        // build the members
        ASTNode idNode = null;
        ASTNode graphNode = null;
        ASTNode typeNode = null;
        ASTNode reverseNode = null;
        List<Couple<String, ASTNode>> members = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            String key = getValue(child.getChildren().get(0));
            String expanded = current.expandName(key);
            if (KEYWORD_ID.equals(key) || KEYWORD_ID.equals(expanded)) {
                idNode = child.getChildren().get(1);
            } else if (KEYWORD_GRAPH.equals(key) || KEYWORD_GRAPH.equals(expanded)) {
                graphNode = child.getChildren().get(1);
            } else if (KEYWORD_TYPE.equals(key) || KEYWORD_TYPE.equals(expanded)) {
                typeNode = child.getChildren().get(1);
            } else if (KEYWORD_REVERSE.equals(key) || KEYWORD_REVERSE.equals(expanded)) {
                reverseNode = child.getChildren().get(1);
            } else if (!KEYWORDS.contains(key) && !KEYWORDS.contains(expanded)) {
                members.add(new Couple<>(key, child.getChildren().get(1)));
            }
        }

        // setup the subject from an id
        SubjectNode subject = null;
        if (idNode != null) {
            String value = getValue(idNode);
            if (value == null)
                // subject is invalid
                throw new JSONLDLoadingException("Expected a valid node id", idNode);
            if (value.startsWith("_:")) {
                // this is blank node
                subject = resolveBlank(value.substring(2));
            } else {
                // this is an IRI
                subject = store.getNodeIRI(current.expandID(value));
            }
        }

        if (graphNode != null) {
            GraphNode targetGraph;
            if (typeNode != null || reverseNode != null || !members.isEmpty() || !markerTopLevel) {
                // this object has properties, or it is not the top level, use the subject
                if (subject == null)
                    subject = store.newNodeBlank();
                targetGraph = (GraphNode) subject;
            } else {
                targetGraph = graph;
            }
            markerTopLevel = false;
            loadValue(graphNode, targetGraph, current, null);
        }
        markerTopLevel = false;

        if (subject == null)
            subject = store.newNodeBlank();

        // setup the type
        if (typeNode != null) {
            Object value = loadValue(typeNode, graph, current, PROPERTY_TYPE_INFO);
            if (value != null) {
                if (value instanceof List) {
                    for (Node type : ((List<Node>) value))
                        quads.add(new Quad(graph, subject, store.getNodeIRI(Vocabulary.rdfType), type));
                } else {
                    quads.add(new Quad(graph, subject, store.getNodeIRI(Vocabulary.rdfType), (Node) value));
                }
            }
        }

        // load the rest of the values
        for (Couple<String, ASTNode> entry : members) {
            loadMember(entry.x, entry.y, subject, graph, current, false);
        }

        // inline reversed properties
        if (reverseNode != null) {
            for (ASTNode member : reverseNode.getChildren()) {
                String key = getValue(member.getChildren().get(0));
                loadMember(key, member.getChildren().get(1), subject, graph, current, true);
            }
        }

        return subject;
    }

    /**
     * Loads the member of an object
     *
     * @param key        The property key
     * @param definition The AST definition to load from
     * @param subject    The subject node
     * @param graph      The current graph
     * @param context    The current context
     * @param reversed   Whether the property is reversed
     */
    private void loadMember(String key, ASTNode definition, SubjectNode subject, GraphNode graph, JSONLDContext context, boolean reversed) throws JSONLDLoadingException {
        if (KEYWORDS.contains(key))
            // this is a keyword, drop it
            return;
        JSONLDNameInfo propertyInfo = context.getInfoFor(key);
        String propertyIRI = propertyInfo.reversed != null ? propertyInfo.reversed : propertyInfo.fullIRI;
        if (propertyIRI == null || propertyIRI.startsWith("_:") || !isFullyExpanded(propertyIRI))
            // property is undefined or
            // this is a blank node identifier, do not handle generalized RDF graphs
            return;
        IRINode property = store.getNodeIRI(propertyIRI);
        Object value = loadValue(definition, graph, context, propertyInfo);
        if (value == null)
            return;
        if (value instanceof JSONLDExplicitList || (value instanceof List && propertyInfo.containerType == JSONLDContainerType.List)) {
            // explicit, or coerced to
            Node target = createRDFList(graph, (List<Node>) value);
            if (reversed || propertyInfo.reversed != null)
                quads.add(new Quad(graph, (SubjectNode) target, property, subject));
            else
                quads.add(new Quad(graph, subject, property, target));
        } else if (value instanceof List) {
            List<Node> targets = (List<Node>) value;
            for (Node target : targets) {
                if (reversed || propertyInfo.reversed != null)
                    quads.add(new Quad(graph, (SubjectNode) target, property, subject));
                else
                    quads.add(new Quad(graph, subject, property, target));
            }
        } else {
            Node target = (Node) value;
            if (propertyInfo.containerType == JSONLDContainerType.List)
                // coerce to list
                target = createRDFList(graph, Collections.singletonList(target));
            if (reversed || propertyInfo.reversed != null)
                quads.add(new Quad(graph, (SubjectNode) target, property, subject));
            else
                quads.add(new Quad(graph, subject, property, target));
        }
    }

    /**
     * Loads a JSON-LD array from the specified AST node
     *
     * @param node    The AST node
     * @param graph   The parent graph
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The corresponding RDF nodes
     */
    private List<Node> loadArray(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws JSONLDLoadingException {
        List<Node> result = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            Object value = loadValue(child, graph, context, info);
            if (value != null) {
                if (value instanceof List)
                    result.addAll((List<Node>) value);
                else
                    result.add((Node) value);
            }
        }
        return result;
    }

    /**
     * Loads a JSON-LD value from the specified AST node
     *
     * @param node    The AST node
     * @param graph   The parent graph
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The corresponding RDF node, or a collection od nodes in the case of some properties
     */
    private Object loadValue(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws JSONLDLoadingException {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object: {
                if (isValueNode(node, context))
                    return loadValueNode(node, graph, context, info);
                else if (isListNode(node, context))
                    return loadListNode(node, graph, context, info);
                else if (isSetNode(node, context))
                    return loadSetNode(node, graph, context, info);
                else if (info != null && info.containerType == JSONLDContainerType.Language)
                    return loadMultilingualValues(node);
                else if (info != null && info.containerType == JSONLDContainerType.Index)
                    return loadIndexedValues(node, graph, context);
                else
                    return loadObject(node, graph, context);
            }
            case JSONLDParser.ID.array:
                return loadArray(node, graph, context, info);
            case JSONLDLexer.ID.LITERAL_INTEGER:
            case JSONLDLexer.ID.LITERAL_DECIMAL:
            case JSONLDLexer.ID.LITERAL_DOUBLE:
                return loadLiteralNumeric(node, context, info);
            case JSONLDLexer.ID.LITERAL_STRING:
                return loadLiteralString(node, context, info);
            case JSONLDLexer.ID.LITERAL_NULL:
                return null;
            case JSONLDLexer.ID.LITERAL_TRUE:
                return store.getLiteralNode("true", Vocabulary.xsdBoolean, null);
            case JSONLDLexer.ID.LITERAL_FALSE:
                return store.getLiteralNode("false", Vocabulary.xsdBoolean, null);
            default:
                throw new JSONLDLoadingException("Unrecognized input " + node.getSymbol().getName(), node);
        }
    }

    /**
     * Resolves the blank node with the specified identifier
     *
     * @param identifier The identifier of a blank node
     * @return The associated blank node
     */
    private BlankNode resolveBlank(String identifier) {
        BlankNode result = blanks.get(identifier);
        if (result == null) {
            result = store.newNodeBlank();
            blanks.put(identifier, result);
        }
        return result;
    }

    /**
     * Creates a RDF list for the specified elements
     *
     * @param graph    The current graph
     * @param elements The elements of the list
     * @return The list's head
     */
    private Node createRDFList(GraphNode graph, List<Node> elements) {
        List<Node> filtered = new ArrayList<>(elements.size());
        for (Node node : elements)
            if (node != null)
                filtered.add(node);
        if (filtered.isEmpty())
            return store.getNodeIRI(Vocabulary.rdfNil);
        SubjectNode head = store.newNodeBlank();
        SubjectNode current = head;
        quads.add(new Quad(graph, current, store.getNodeIRI(Vocabulary.rdfFirst), filtered.get(0)));
        for (int i = 1; i != filtered.size(); i++) {
            Node element = filtered.get(i);
            SubjectNode proxy = store.newNodeBlank();
            quads.add(new Quad(graph, current, store.getNodeIRI(Vocabulary.rdfRest), proxy));
            current = proxy;
            quads.add(new Quad(graph, current, store.getNodeIRI(Vocabulary.rdfFirst), element));
        }
        quads.add(new Quad(graph, current, store.getNodeIRI(Vocabulary.rdfRest), store.getNodeExistingIRI(Vocabulary.rdfNil)));
        return head;
    }

    /**
     * Gets the RDF numeric Literal equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF numeric Literal node
     */
    private LiteralNode loadLiteralNumeric(ASTNode node, JSONLDContext context, JSONLDNameInfo info) throws JSONLDLoadingException {
        switch (node.getSymbol().getID()) {
            case JSONLDLexer.ID.LITERAL_INTEGER: {
                String value = node.getValue();
                if (info != null && info.valueType != null) {
                    // coerced type
                    switch (info.valueType) {
                        case Vocabulary.xsdFloat:
                        case Vocabulary.xsdDouble:
                        case Vocabulary.xsdDecimal:
                            return store.getLiteralNode(Utils.canonicalDouble(value), context.expandName(info.valueType), null);
                        default:
                            return store.getLiteralNode(value, context.expandName(info.valueType), null);
                    }
                }
                return store.getLiteralNode(value, Vocabulary.xsdInteger, null);
            }
            case JSONLDLexer.ID.LITERAL_DECIMAL:
            case JSONLDLexer.ID.LITERAL_DOUBLE: {
                String value = Utils.canonicalDouble(node.getValue());
                if (info != null && info.valueType != null) {
                    // coerced type
                    return store.getLiteralNode(Utils.canonicalDouble(value), context.expandName(info.valueType), null);
                }
                return store.getLiteralNode(value, Vocabulary.xsdDouble, null);
            }
        }
        throw new JSONLDLoadingException("Unexpected literal numeric", node);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF node
     */
    private Node loadLiteralString(ASTNode node, JSONLDContext context, JSONLDNameInfo info) throws JSONLDLoadingException {
        String value = getValue(node);
        if (info != null && info.valueType != null) {
            // coerced type
            if (KEYWORD_ID.equals(info.valueType)) {
                if (value == null)
                    // subject is invalid
                    throw new JSONLDLoadingException("Expected a valid node id", node);
                if (value.startsWith("_:")) {
                    // this is blank node
                    return resolveBlank(value.substring(2));
                } else {
                    // this is an IRI
                    return store.getNodeIRI(context.expandResource(value));
                }
            }
            // this is a typed literal
            return store.getLiteralNode(value, context.expandName(info.valueType), null);
        }
        String language = (info != null && info.language != null) ? info.language : context.getLanguage();
        if (language != null && MARKER_NULL.equals(language))
            // explicit reset
            language = null;
        return store.getLiteralNode(value, language == null ? Vocabulary.xsdString : Vocabulary.rdfLangString, language);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param graph   The parent graph
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF node
     */
    private Node loadValueNode(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws JSONLDLoadingException {
        // make a copy of the property info
        JSONLDNameInfo current = new JSONLDNameInfo();
        if (info != null) {
            current.fullIRI = info.fullIRI;
            current.containerType = info.containerType;
            current.valueType = info.valueType;
            current.language = info.language;
            current.reversed = info.reversed;
        }
        // update it with the overriding info for this node
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (KEYWORD_TYPE.equals(key) || KEYWORD_TYPE.equals(expandedKey))
                current.valueType = getValue(member.getChildren().get(1));
            else if (KEYWORD_LANGUAGE.equals(key) || KEYWORD_LANGUAGE.equals(expandedKey))
                current.language = getValue(member.getChildren().get(1));
        }
        // load the value
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (KEYWORD_VALUE.equals(key) || KEYWORD_VALUE.equals(expandedKey))
                return (Node) loadValue(member.getChildren().get(1), graph, context, current);
        }
        return null;
    }

    /**
     * Gets the RDF nodes equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF nodes
     */
    private JSONLDExplicitList loadListNode(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws JSONLDLoadingException {
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (KEYWORD_LIST.equals(key) || KEYWORD_LIST.equals(expandedKey)) {
                ASTNode valueNode = member.getChildren().get(1);
                Object value = loadValue(valueNode, graph, context, info);
                JSONLDExplicitList result = new JSONLDExplicitList();
                if (value != null) {
                    if (value instanceof List)
                        result.addAll((List<Node>) value);
                    else
                        result.add((Node) value);
                }
                return result;
            }
        }
        return null;
    }

    /**
     * Gets the RDF nodes equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF nodes
     */
    private List<Node> loadSetNode(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws JSONLDLoadingException {
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (KEYWORD_SET.equals(key) || KEYWORD_SET.equals(expandedKey)) {
                ASTNode valueNode = member.getChildren().get(1);
                Object value = loadValue(valueNode, graph, context, info);
                List<Node> result = new ArrayList<>();
                if (value != null) {
                    if (value instanceof List)
                        result.addAll((List<Node>) value);
                    else
                        result.add((Node) value);
                }
                return result;
            }
        }
        return null;
    }

    /**
     * Gets the RDF nodes equivalent to the specified AST node that represents the values of a multilingual property
     *
     * @param node An AST node
     * @return The equivalent RDF nodes
     */
    private List<Node> loadMultilingualValues(ASTNode node) throws JSONLDLoadingException {
        List<Node> result = new ArrayList<>();
        for (ASTNode member : node.getChildren()) {
            String language = getValue(member.getChildren().get(0));
            ASTNode definition = member.getChildren().get(1);
            if (definition.getSymbol().getID() == JSONLDParser.ID.array) {
                for (ASTNode element : definition.getChildren()) {
                    result.add(loadMultilingualValue(element, language));
                }
            } else {
                result.add(loadMultilingualValue(definition, language));
            }
        }
        return result;
    }

    /**
     * Gets the RDF node equivalent to the specified AST node that represents the value of a multilingual property
     *
     * @param definition The AST node defining the value
     * @param language   The current language
     * @return The equivalent RDF node
     */
    private Node loadMultilingualValue(ASTNode definition, String language) throws JSONLDLoadingException {
        String value = getValue(definition);
        return store.getLiteralNode(value, Vocabulary.rdfLangString, language);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node that represents an indexed collection
     *
     * @param node    An AST node
     * @param graph   The current graph
     * @param context The current context
     * @return The values
     */
    private List<Node> loadIndexedValues(ASTNode node, GraphNode graph, JSONLDContext context) throws JSONLDLoadingException {
        List<Node> result = new ArrayList<>();
        for (ASTNode member : node.getChildren()) {
            // the index does not translate to RDF
            Object value = loadValue(member.getChildren().get(1), graph, context, null);
            if (value != null) {
                if (value instanceof List)
                    result.addAll((List<Node>) value);
                else
                    result.add((Node) value);
            }
        }
        return result;
    }

    /**
     * Determines whether the specified name is a fully expanded URI
     *
     * @param name A name
     * @return true if this is a fully expanded URI
     */
    private static boolean isFullyExpanded(String name) {
        return name.contains(":");
    }

    /**
     * Determines whether the specified AST node defines a value node (as opposed to an object node)
     *
     * @param node    An AST node
     * @param context The current context
     * @return true if this is a value node
     */
    private static boolean isValueNode(ASTNode node, JSONLDContext context) throws JSONLDLoadingException {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (KEYWORD_VALUE.equals(key) || KEYWORD_VALUE.equals(expandedKey))
                return true;
            if (KEYWORD_LANGUAGE.equals(key) || KEYWORD_LANGUAGE.equals(expandedKey))
                return true;
        }
        return false;
    }

    /**
     * Determines whether the specified AST node defines a list
     *
     * @param node An AST node
     * @return true if this is a list node
     */
    private static boolean isListNode(ASTNode node, JSONLDContext context) throws JSONLDLoadingException {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (KEYWORD_LIST.equals(key) || KEYWORD_LIST.equals(expandedKey))
                return true;
        }
        return false;
    }

    /**
     * Determines whether the specified AST node defines a set
     *
     * @param node An AST node
     * @return true if this is a set node
     */
    private static boolean isSetNode(ASTNode node, JSONLDContext context) throws JSONLDLoadingException {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (KEYWORD_SET.equals(key) || KEYWORD_SET.equals(expandedKey))
                return true;
        }
        return false;
    }
}
