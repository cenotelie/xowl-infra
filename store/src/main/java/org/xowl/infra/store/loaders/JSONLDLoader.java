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

import org.xowl.hime.redist.ASTNode;
import org.xowl.hime.redist.ParseError;
import org.xowl.hime.redist.ParseResult;
import org.xowl.hime.redist.TextContext;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.collections.Couple;
import org.xowl.infra.utils.http.URIUtils;
import org.xowl.infra.utils.logging.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * Loader for JSON LD sources
 *
 * @author Laurent Wouters
 */
public abstract class JSONLDLoader implements Loader {

    /**
     * List of the reversed keywords
     */
    public static final List<String> KEYWORDS = Arrays.asList(
            Vocabulary.JSONLD.context,
            Vocabulary.JSONLD.id,
            Vocabulary.JSONLD.value,
            Vocabulary.JSONLD.language,
            Vocabulary.JSONLD.type,
            Vocabulary.JSONLD.container,
            Vocabulary.JSONLD.list,
            Vocabulary.JSONLD.set,
            Vocabulary.JSONLD.reverse,
            Vocabulary.JSONLD.index,
            Vocabulary.JSONLD.base,
            Vocabulary.JSONLD.vocab,
            Vocabulary.JSONLD.graph
    );

    /**
     * The property info for the rdf:type property
     */
    private static final JSONLDNameInfo PROPERTY_TYPE_INFO = new JSONLDNameInfo();

    static {
        PROPERTY_TYPE_INFO.fullIRI = Vocabulary.rdfType;
        PROPERTY_TYPE_INFO.valueType = Vocabulary.JSONLD.vocab;
    }

    /**
     * Gets the canonical lexical form of a double value
     *
     * @param doubleString A serialized double value
     * @return The canonical lexical form
     */
    private static String canonicalDouble(String doubleString) {
        double value = Double.parseDouble(doubleString);
        if (value == 0)
            return "0.0E1";
        String result = Double.toString(value);
        if (result.contains("E"))
            return result;
        int length = result.length();
        char[] chars = new char[length + 3];
        result.getChars(0, length, chars, 0);
        int canonicalDecimalPoint = chars[0] == '-' ? 2 : 1;
        if (value >= 1 || value <= -1) {
            int decimalPoint = result.indexOf('.');
            System.arraycopy(chars, canonicalDecimalPoint, chars, canonicalDecimalPoint + 1, decimalPoint - canonicalDecimalPoint);
            chars[canonicalDecimalPoint] = '.';
            while (chars[length - 1] == '0')
                length--;
            if (chars[length - 1] == '.')
                length++;
            chars[length++] = 'E';
            int shift = decimalPoint - canonicalDecimalPoint;
            chars[length++] = (char) (shift + '0');
        } else {
            int nonZero = canonicalDecimalPoint + 1;
            while (chars[nonZero] == '0')
                nonZero++;
            chars[canonicalDecimalPoint - 1] = chars[nonZero];
            chars[canonicalDecimalPoint] = '.';
            for (int i = nonZero + 1, j = canonicalDecimalPoint + 1; i < length; i++, j++)
                chars[j] = chars[i];
            length -= nonZero - canonicalDecimalPoint;
            if (length == canonicalDecimalPoint + 1)
                chars[length++] = '0';
            chars[length++] = 'E';
            chars[length++] = '-';
            int shift = nonZero - canonicalDecimalPoint;
            chars[length++] = (char) (shift + '0');
        }
        return new String(chars, 0, length);
    }

    /**
     * Gets the decoded value of the specified AST node that holds a literal string
     *
     * @param node An AST node that holds a literal string
     * @return The decoded value of the string
     * @throws LoaderException On unrecognized input
     */
    public static String getValue(ASTNode node) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case JSONLDLexer.ID.LITERAL_INTEGER:
            case JSONLDLexer.ID.LITERAL_DECIMAL:
            case JSONLDLexer.ID.LITERAL_DOUBLE:
                return node.getValue();
            case JSONLDLexer.ID.LITERAL_STRING:
                String value = node.getValue();
                value = value.substring(1, value.length() - 1);
                return TextUtils.unescape(value);
            case JSONLDLexer.ID.LITERAL_NULL:
                return null;
            case JSONLDLexer.ID.LITERAL_TRUE:
                return "true";
            case JSONLDLexer.ID.LITERAL_FALSE:
                return "false";
            default:
                throw new LoaderException("Unrecognized input " + node.getSymbol().getName(), node);
        }
    }

    /**
     * Parses the JSON content
     *
     * @param logger  The logger to use
     * @param content The content to parse
     * @return The AST root node, or null of the parsing failed
     */
    public static ASTNode parseJSON(Logger logger, String content) {
        JSONLDLoader loader = new JSONLDLoader(null) {
            @Override
            protected Reader getReaderFor(Logger logger, String iri) {
                return null;
            }
        };
        ParseResult result = loader.parse(logger, new StringReader(content));
        if (result == null)
            return null;
        if (!result.getErrors().isEmpty()) {
            for (ParseError error : result.getErrors())
                logger.error(error);
            return null;
        }
        return result.getRoot();
    }

    /**
     * The RDF store to create nodes from
     */
    private final NodeManager store;
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
    public JSONLDLoader(NodeManager store) {
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
    public RDFLoaderResult loadRDF(Logger logger, Reader reader, String resourceIRI, String graphIRI) {
        RDFLoaderResult result = new RDFLoaderResult();
        this.logger = logger;
        quads = result.getQuads();
        resource = resourceIRI;
        blanks = new HashMap<>();
        markerTopLevel = true;
        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        try {
            loadDocument(parseResult.getRoot(), store.getIRINode(graphIRI), new JSONLDContext(this));
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
     * Loads a context definition in an external document at the specified IRI
     *
     * @param iri The IRI of an auxiliary document
     * @return The parsed external document, or null if an error occured
     * @throws LoaderException On unrecognized input
     */
    protected ASTNode getExternalContextDefinition(String iri) throws LoaderException {
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
            if (Vocabulary.JSONLD.context.equals(key))
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
    private void loadDocument(ASTNode node, GraphNode graph, JSONLDContext context) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object:
                loadObject(node, graph, context);
                break;
            case JSONLDParser.ID.array:
                loadArray(node, graph, context, null);
                break;
            default:
                throw new LoaderException("Unrecognized input " + node.getSymbol().getName(), node);
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
    private SubjectNode loadObject(ASTNode node, GraphNode graph, JSONLDContext context) throws LoaderException {
        // load the new context
        ASTNode contextNode = null;
        for (ASTNode child : node.getChildren()) {
            String key = getValue(child.getChildren().get(0));
            if (Vocabulary.JSONLD.context.equals(key)) {
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
            if (Vocabulary.JSONLD.id.equals(key) || Vocabulary.JSONLD.id.equals(expanded)) {
                idNode = child.getChildren().get(1);
            } else if (Vocabulary.JSONLD.graph.equals(key) || Vocabulary.JSONLD.graph.equals(expanded)) {
                graphNode = child.getChildren().get(1);
            } else if (Vocabulary.JSONLD.type.equals(key) || Vocabulary.JSONLD.type.equals(expanded)) {
                typeNode = child.getChildren().get(1);
            } else if (Vocabulary.JSONLD.reverse.equals(key) || Vocabulary.JSONLD.reverse.equals(expanded)) {
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
                throw new LoaderException("Expected a valid node id", idNode);
            if (value.startsWith("_:")) {
                // this is blank node
                subject = resolveBlank(value.substring(2));
            } else {
                // this is an IRI
                value = current.expandID(value);
                if (!URIUtils.isAbsolute(value))
                    return null;
                subject = store.getIRINode(value);
            }
        }

        if (graphNode != null) {
            GraphNode targetGraph;
            if (typeNode != null || reverseNode != null || !members.isEmpty() || !markerTopLevel) {
                // this object has properties, or it is not the top level, use the subject
                if (subject == null)
                    subject = store.getBlankNode();
                targetGraph = (GraphNode) subject;
            } else if (subject != null) {
                targetGraph = (GraphNode) subject;
            } else {
                targetGraph = graph;
            }
            markerTopLevel = false;
            loadValue(graphNode, targetGraph, current, null);
        }
        markerTopLevel = false;

        if (subject == null)
            subject = store.getBlankNode();

        // setup the type
        if (typeNode != null) {
            Object value = loadValue(typeNode, graph, current, PROPERTY_TYPE_INFO);
            if (value != null) {
                if (value instanceof List) {
                    for (Node type : ((List<Node>) value))
                        quads.add(new Quad(graph, subject, store.getIRINode(Vocabulary.rdfType), type));
                } else {
                    quads.add(new Quad(graph, subject, store.getIRINode(Vocabulary.rdfType), (Node) value));
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
    private void loadMember(String key, ASTNode definition, SubjectNode subject, GraphNode graph, JSONLDContext context, boolean reversed) throws LoaderException {
        if (KEYWORDS.contains(key))
            // this is a keyword, drop it
            return;
        JSONLDNameInfo propertyInfo = context.getInfoFor(key);
        String propertyIRI = propertyInfo.fullIRI;
        if (propertyInfo.reversed != null) {
            propertyIRI = propertyInfo.reversed;
            reversed = !reversed;
        }
        if (propertyIRI == null || propertyIRI.startsWith("_:") || !URIUtils.isAbsolute(propertyIRI))
            // property is undefined or
            // this is a blank node identifier, do not handle generalized RDF graphs
            return;
        IRINode property = store.getIRINode(propertyIRI);
        Object value = loadValue(definition, graph, context, propertyInfo);
        if (value == null)
            return;
        if (value instanceof JSONLDExplicitList || (value instanceof List && propertyInfo.containerType == JSONLDContainerType.List)) {
            // explicit, or coerced to
            Node target = createRDFList(graph, (List<Node>) value);
            if (reversed)
                quads.add(new Quad(graph, (SubjectNode) target, property, subject));
            else
                quads.add(new Quad(graph, subject, property, target));
        } else if (value instanceof List) {
            List<Node> targets = (List<Node>) value;
            for (Node target : targets) {
                if (reversed)
                    quads.add(new Quad(graph, (SubjectNode) target, property, subject));
                else
                    quads.add(new Quad(graph, subject, property, target));
            }
        } else {
            Node target = (Node) value;
            if (propertyInfo.containerType == JSONLDContainerType.List)
                // coerce to list
                target = createRDFList(graph, Collections.singletonList(target));
            if (reversed)
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
    private List<Node> loadArray(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
        List<Node> result = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            Object value = loadValue(child, graph, context, info);
            if (value != null) {
                if (value instanceof JSONLDExplicitList)
                    result.add(createRDFList(graph, (List<Node>) value));
                else if (value instanceof List)
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
    private Object loadValue(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object: {
                if (isValueNode(node, context))
                    return loadValueNode(node, graph, context, info);
                else if (isListNode(node, context)) {
                    if (info != null)
                        // only load the list if it is the value of a property
                        return loadListNode(node, graph, context, info);
                    return null;
                } else if (isSetNode(node, context))
                    return loadSetNode(node, graph, context, info);
                else if (info != null && info.containerType == JSONLDContainerType.Language)
                    return loadMultilingualValues(node);
                else if (info != null && info.containerType == JSONLDContainerType.Index)
                    return loadIndexedValues(node, graph, context, info);
                else
                    return loadObject(node, graph, context);
            }
            case JSONLDParser.ID.array:
                if (info != null && info.containerType == JSONLDContainerType.Index)
                    return loadIndexedValues(node, graph, context, info);
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
            case JSONLDLexer.ID.LITERAL_FALSE:
                return loadLiteralBoolean(node, context, info);
            default:
                throw new LoaderException("Unrecognized input " + node.getSymbol().getName(), node);
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
            result = store.getBlankNode();
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
            return store.getIRINode(Vocabulary.rdfNil);
        SubjectNode head = store.getBlankNode();
        SubjectNode current = head;
        quads.add(new Quad(graph, current, store.getIRINode(Vocabulary.rdfFirst), filtered.get(0)));
        for (int i = 1; i != filtered.size(); i++) {
            Node element = filtered.get(i);
            SubjectNode proxy = store.getBlankNode();
            quads.add(new Quad(graph, current, store.getIRINode(Vocabulary.rdfRest), proxy));
            current = proxy;
            quads.add(new Quad(graph, current, store.getIRINode(Vocabulary.rdfFirst), element));
        }
        quads.add(new Quad(graph, current, store.getIRINode(Vocabulary.rdfRest), store.getIRINode(Vocabulary.rdfNil)));
        return head;
    }

    /**
     * Gets the RDF boolean Literal equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF boolean Literal node
     */
    private LiteralNode loadLiteralBoolean(ASTNode node, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
        String value = node.getValue();
        if (info != null && info.valueType != null) {
            // coerced type
            return store.getLiteralNode(value, context.expandName(info.valueType), null);
        }
        return store.getLiteralNode(value, Vocabulary.xsdBoolean, null);
    }

    /**
     * Gets the RDF numeric Literal equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF numeric Literal node
     */
    private LiteralNode loadLiteralNumeric(ASTNode node, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case JSONLDLexer.ID.LITERAL_INTEGER: {
                String value = node.getValue();
                if (info != null && info.valueType != null) {
                    // coerced type
                    switch (info.valueType) {
                        case Vocabulary.xsdFloat:
                        case Vocabulary.xsdDouble:
                        case Vocabulary.xsdDecimal:
                            return store.getLiteralNode(canonicalDouble(value), context.expandName(info.valueType), null);
                        default:
                            return store.getLiteralNode(value, context.expandName(info.valueType), null);
                    }
                }
                return store.getLiteralNode(value, Vocabulary.xsdInteger, null);
            }
            case JSONLDLexer.ID.LITERAL_DECIMAL:
            case JSONLDLexer.ID.LITERAL_DOUBLE: {
                String value = canonicalDouble(node.getValue());
                if (info != null && info.valueType != null) {
                    // coerced type
                    return store.getLiteralNode(canonicalDouble(value), context.expandName(info.valueType), null);
                }
                return store.getLiteralNode(value, Vocabulary.xsdDouble, null);
            }
        }
        throw new LoaderException("Unexpected literal numeric", node);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF node
     */
    private Node loadLiteralString(ASTNode node, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
        String value = getValue(node);
        if (info != null && info.valueType != null) {
            // coerced type
            if (Vocabulary.JSONLD.id.equals(info.valueType) || Vocabulary.JSONLD.vocab.equals(info.valueType)) {
                if (value == null)
                    // subject is invalid
                    throw new LoaderException("Expected a valid node id", node);
                if (value.startsWith("_:")) {
                    // this is blank node
                    return resolveBlank(value.substring(2));
                } else if (Vocabulary.JSONLD.id.equals(info.valueType)) {
                    // this is an id
                    return store.getIRINode(context.expandID(value));
                } else {
                    // this is a resource
                    return store.getIRINode(context.expandResource(value));
                }
            }
            // this is a typed literal
            return store.getLiteralNode(value, context.expandName(info.valueType), null);
        }
        String language = (info != null && info.language != null) ? info.language : context.getLanguage();
        if (language != null && Vocabulary.JSONLD.null_.equals(language))
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
    private Node loadValueNode(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
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
            if (Vocabulary.JSONLD.type.equals(key) || Vocabulary.JSONLD.type.equals(expandedKey))
                current.valueType = getValue(member.getChildren().get(1));
            else if (Vocabulary.JSONLD.language.equals(key) || Vocabulary.JSONLD.language.equals(expandedKey))
                current.language = getValue(member.getChildren().get(1));
        }
        // load the value
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (Vocabulary.JSONLD.value.equals(key) || Vocabulary.JSONLD.value.equals(expandedKey))
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
    private JSONLDExplicitList loadListNode(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (Vocabulary.JSONLD.list.equals(key) || Vocabulary.JSONLD.list.equals(expandedKey)) {
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
    private List<Node> loadSetNode(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (Vocabulary.JSONLD.set.equals(key) || Vocabulary.JSONLD.set.equals(expandedKey)) {
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
    private List<Node> loadMultilingualValues(ASTNode node) throws LoaderException {
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
    private Node loadMultilingualValue(ASTNode definition, String language) throws LoaderException {
        String value = getValue(definition);
        return store.getLiteralNode(value, Vocabulary.rdfLangString, language);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node that represents an indexed collection
     *
     * @param node    An AST node
     * @param graph   The current graph
     * @param context The current context
     * @param info    The information on the current name (property)
     * @return The values
     */
    private List<Node> loadIndexedValues(ASTNode node, GraphNode graph, JSONLDContext context, JSONLDNameInfo info) throws LoaderException {
        // make a copy of the property info
        JSONLDNameInfo current = new JSONLDNameInfo();
        if (info != null) {
            current.fullIRI = info.fullIRI;
            current.containerType = info.containerType;
            current.valueType = info.valueType;
            current.language = info.language;
            current.reversed = info.reversed;
        }
        current.containerType = JSONLDContainerType.Undefined;
        List<ASTNode> definitions = new ArrayList<>();
        if (node.getSymbol().getID() == JSONLDParser.ID.object) {
            for (ASTNode member : node.getChildren()) {
                // the index does not translate to RDF
                definitions.add(member.getChildren().get(1));
            }
        } else {
            // this is an array
            definitions.addAll(node.getChildren());
        }
        List<Node> result = new ArrayList<>();
        for (ASTNode definition : definitions) {
            Object value = loadValue(definition, graph, context, current);
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
     * Determines whether the specified AST node defines a value node (as opposed to an object node)
     *
     * @param node    An AST node
     * @param context The current context
     * @return true if this is a value node
     */
    private static boolean isValueNode(ASTNode node, JSONLDContext context) throws LoaderException {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (Vocabulary.JSONLD.value.equals(key) || Vocabulary.JSONLD.value.equals(expandedKey))
                return true;
            if (Vocabulary.JSONLD.language.equals(key) || Vocabulary.JSONLD.language.equals(expandedKey))
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
    private static boolean isListNode(ASTNode node, JSONLDContext context) throws LoaderException {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (Vocabulary.JSONLD.list.equals(key) || Vocabulary.JSONLD.list.equals(expandedKey))
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
    private static boolean isSetNode(ASTNode node, JSONLDContext context) throws LoaderException {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            String expandedKey = context.expandName(key);
            if (Vocabulary.JSONLD.set.equals(key) || Vocabulary.JSONLD.set.equals(expandedKey))
                return true;
        }
        return false;
    }
}
