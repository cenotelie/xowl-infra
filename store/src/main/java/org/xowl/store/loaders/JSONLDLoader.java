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
     * Property which value describes a context
     */
    private static final String KEYWORD_CONTEXT = "@context";
    /**
     * Property that defines the identifier of a JSON-LD object with an URI or a blank node
     */
    private static final String KEYWORD_ID = "@id";
    /**
     * Property that identifies the value of a literal expressed as a JSON object
     */
    private static final String KEYWORD_VALUE = "@value";
    /**
     * Property that identifies the language of a literal expressed as a JSON object
     */
    private static final String KEYWORD_LANGUAGE = "@language";
    /**
     * Property that identifies the type of a datatype property
     */
    private static final String KEYWORD_TYPE = "@type";
    /**
     * Property that defines the type of container of another multi-valued property
     */
    private static final String KEYWORD_CONTAINER = "@container";
    /**
     * Value that identifies the list type of container for a multi-valued property
     */
    private static final String KEYWORD_LIST = "@list";
    /**
     * Value that identifies the set type of container for a multi-valued property
     */
    private static final String KEYWORD_SET = "@set";
    /**
     * Property that specifies that another property is expressed in a reversed form
     */
    private static final String KEYWORD_REVERSE = "@reverse";
    /**
     * Property that specifies the index of an object in a container
     */
    private static final String KEYWORD_INDEX = "@index";
    /**
     * Property that specifies the base URI for relative ones
     */
    private static final String KEYWORD_BASE = "@base";
    /**
     * Property that specifies a common URI radical for a vocabulary
     */
    private static final String KEYWORD_VOCAB = "@vocab";
    /**
     * Property for the expression of explicit graphs
     */
    private static final String KEYWORD_GRAPH = "@graph";

    /**
     * List of the reversed keywords
     */
    private static final List<String> KEYWORDS = Arrays.asList(
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
     * The type of a container for a multi-valued property
     */
    private enum ContainerType {
        /**
         * Undefined for a single-valued property
         */
        Undefined,
        /**
         * Use a list
         */
        List,
        /**
         * Use a set
         */
        Set,
        /**
         * Use an index
         */
        Index,
        /**
         * Multilingual property
         */
        Language
    }

    /**
     * Represents the information about a name
     */
    private static class NameInfo {
        /**
         * The type of container for a multi-valued property
         */
        public final ContainerType containerType;
        /**
         * The full IRI of the name
         */
        public final String fullIRI;
        /**
         * The full IRI of the type for values of this property
         */
        public final String valueType;
        /**
         * The language associated to this property (implies the value type is xsd:string)
         */
        public final String language;

        /**
         * Initializes the content of this object
         *
         * @param containerType The type of container for a multi-valued property
         * @param fullIRI       The full IRI of the name
         * @param valueType     The full IRI of the type for values of this property
         * @param language      The language associated to this property (implies the value type is xsd:string)
         */
        public NameInfo(ContainerType containerType, String fullIRI, String valueType, String language) {
            this.containerType = containerType;
            this.fullIRI = fullIRI;
            this.valueType = valueType;
            this.language = language;
        }

        /**
         * Initializes this name information
         *
         * @param definition The definition to load from
         * @param parent     The parent context
         * @param iri        The assumed full IRI, if any
         */
        public NameInfo(ASTNode definition, Context parent, String iri) {
            switch (definition.getSymbol().getID()) {
                case JSONLDLexer.ID.LITERAL_STRING:
                    // this is an IRI
                    containerType = ContainerType.Undefined;
                    fullIRI = parent.expandIRI(getValue(definition));
                    valueType = null;
                    language = null;
                    break;
                case JSONLDParser.ID.object:
                    // this is an object
                    String container = null;
                    String id = null;
                    String type = null;
                    String lang = null;
                    for (ASTNode member : definition.getChildren()) {
                        String key = getValue(member.getChildren().get(0));
                        if (KEYWORD_ID.equals(key)) {
                            id = parent.expandIRI(getValue(member.getChildren().get(1)));
                        } else if (KEYWORD_TYPE.equals(key)) {
                            type = getValue(member.getChildren().get(1));
                        } else if (KEYWORD_CONTAINER.equals(key)) {
                            container = getValue(member.getChildren().get(1));
                        } else if (KEYWORD_LANGUAGE.equals(key)) {
                            lang = getValue(member.getChildren().get(1));
                            lang = lang == null ? "null" : lang;
                        }
                    }
                    fullIRI = id != null ? id : iri;
                    valueType = KEYWORD_ID.equals(type) ? KEYWORD_ID : parent.expandIRI(type);
                    language = lang;
                    if (KEYWORD_LIST.equals(container))
                        containerType = ContainerType.List;
                    else if (KEYWORD_SET.equals(container))
                        containerType = ContainerType.Set;
                    else if (KEYWORD_INDEX.equals(container))
                        containerType = ContainerType.Index;
                    else
                        containerType = ContainerType.Undefined;
                    break;
                default:
                    containerType = ContainerType.Undefined;
                    fullIRI = iri;
                    valueType = null;
                    language = null;
            }
        }
    }

    /**
     * Represents a fragment of context
     */
    private static final class ContextFragment {
        /**
         * The current names
         */
        public final Map<String, NameInfo> names;
        /**
         * The current language
         */
        public final String language;
        /**
         * The current base URI
         */
        public final String base;
        /**
         * The current vocabulary radical
         */
        public final String vocab;

        /**
         * Initializes this fragment
         *
         * @param definition The AST node to load from
         */
        public ContextFragment(ASTNode definition) {
            this.names = new HashMap<>();
            String tLanguage = null;
            String tBase = null;
            String tVocab = null;
            for (ASTNode member : definition.getChildren()) {
                String key = getValue(member.getChildren().get(0));
                if (KEYWORD_LANGUAGE.equals(key)) {
                    tLanguage = getValue(member.getChildren().get(1));
                    // sets special language value for resetting the default language
                    tLanguage = tLanguage == null ? "null" : tLanguage;
                } else if (KEYWORD_BASE.equals(key)) {
                    tBase = getValue(member.getChildren().get(1));
                } else if (KEYWORD_VOCAB.equals(key)) {
                    tVocab = getValue(member.getChildren().get(1));
                }
            }
            this.language = tLanguage;
            this.base = tBase;
            this.vocab = tVocab;
        }

        /**
         * Loads the name definitions for this fragment
         *
         * @param parent     The parent context
         * @param definition The AST node to load from
         */
        public void loadNames(Context parent, ASTNode definition) {
            for (ASTNode member : definition.getChildren()) {
                String key = getValue(member.getChildren().get(0));
                if (key != null && KEYWORDS.contains(key)) {
                    if (key.contains(":")) {
                        String[] parts = key.split(":");
                        // assume only two parts
                        names.put(parts[1], new NameInfo(member.getChildren().get(1), parent, parent.expandIRI(key)));
                    } else {
                        names.put(key, new NameInfo(member.getChildren().get(1), parent, null));
                    }
                }
            }
        }
    }


    /**
     * Represents a context for the loader
     */
    private class Context {
        /**
         * The parent context
         */
        private final Context parent;
        /**
         * The fragments in this context
         */
        private final List<ContextFragment> fragments;

        /**
         * Initializes an empty context
         */
        public Context() {
            this.parent = null;
            this.fragments = Collections.emptyList();
        }

        /**
         * Initializes this context with the specified parent
         *
         * @param parent     The parent context
         * @param definition The AST node to load from
         */
        public Context(Context parent, ASTNode definition) {
            this.parent = definition.getSymbol().getID() == JSONLDLexer.ID.LITERAL_NULL ? null : parent;
            this.fragments = new ArrayList<>();
            List<ASTNode> definitions = new ArrayList<>();
            definitions.add(definition);
            for (int i = 0; i != definitions.size(); i++) {
                definition = definitions.get(i);
                if (definition.getSymbol().getID() == JSONLDLexer.ID.LITERAL_STRING) {
                    // external document
                    definition = getExternalContextDefinition(getValue(definition));
                    if (definition != null)
                        definitions.add(definition);
                } else if (definition.getSymbol().getID() == JSONLDParser.ID.array) {
                    // combined definitions
                    definitions.addAll(definition.getChildren());
                } else if (definition.getSymbol().getID() == JSONLDParser.ID.object) {
                    // inline definition
                    ContextFragment fragment = new ContextFragment(definition);
                    fragments.add(fragment);
                    fragment.loadNames(this, definition);
                }
            }
        }

        /**
         * Expands an IRI from the specified term, or null if it fails to
         *
         * @param term A term
         * @return The corresponding IRI, or null if it cannot be expanded
         */
        public String expandIRI(String term) {
            if (term.startsWith("_:"))
                return null;
            if (term.contains(":")) {
                String[] parts = term.split(":");
                for (int i = 1; i != parts.length; i++) {
                    String prefix = rebuild(parts, 0, i - 1);
                    String suffix = rebuild(parts, i, parts.length - 1);
                    if (suffix.startsWith("//"))
                        return prefix + ":" + suffix;
                    String expandedPrefix = expandNamespace(prefix);
                    if (expandedPrefix != null)
                        return expandedPrefix + suffix;
                }
                return null;
            } else {
                Context current = this;
                while (current != null) {
                    for (int i = current.fragments.size(); i != -1; i--) {
                        ContextFragment fragment = current.fragments.get(i);
                        NameInfo info = fragment.names.get(term);
                        if (info.fullIRI == null)
                            // explicitly forbids the expansion
                            return term;
                        if (fragment.vocab != null)
                            // expand using the vocabulary radical
                            return fragment.vocab + term;
                        if (fragment.base != null)
                            // found a base IRI
                            return Utils.normalizeIRI(resource, fragment.base, term);
                    }
                    current = current.parent;
                }
                // could not either forbid the expansion or find a vocabulary or a base URI
                return term;
            }
        }

        /**
         * Gets the information about the specified name
         *
         * @param term A name
         * @return The associated information
         */
        public NameInfo getInfoFor(String term) {
            String fullIRI = expandIRI(term);
            if (fullIRI == null)
                return null;
            ContainerType containerType = ContainerType.Undefined;
            String valueType = null;
            String language = null;
            Context current = this;
            while (current != null) {
                for (int i = current.fragments.size(); i != -1; i--) {
                    for (NameInfo info : current.fragments.get(i).names.values()) {
                        if (fullIRI.equals(info.fullIRI)) {
                            if (containerType == ContainerType.Undefined)
                                containerType = info.containerType;
                            if (valueType == null)
                                valueType = info.valueType;
                            if (language == null)
                                language = info.language;
                        }
                    }
                }
                current = current.parent;
            }
            return new NameInfo(containerType, fullIRI, valueType, language);
        }

        /**
         * Gets the current language
         *
         * @return The current language, if any
         */
        public String getLanguage() {
            Context current = this;
            while (current != null) {
                for (int i = current.fragments.size(); i != -1; i--) {
                    String language = current.fragments.get(i).language;
                    if (language != null) {
                        if ("null".equals(language)) {
                            // explicit reset
                            return null;
                        } else {
                            // found  a defined language
                            return language;
                        }
                    }
                }
                current = current.parent;
            }
            return null;
        }

        /**
         * Rebuilds a split string
         *
         * @param parts The parts
         * @param start The index of the first part
         * @param end   The index of the last part
         * @return The rebuilt string
         */
        private String rebuild(String[] parts, int start, int end) {
            if (start == end)
                return parts[start];
            StringBuilder buffer = new StringBuilder();
            for (int i = start; i != end + 1; i++) {
                if (i != start)
                    buffer.append(':');
                buffer.append(parts[i]);
            }
            return buffer.toString();
        }

        /**
         * Expands a name with the available namespaces
         *
         * @param name A name
         * @return The full name, or null if it cannot be expanded
         */
        private String expandNamespace(String name) {
            Context current = this;
            while (current != null) {
                for (int i = current.fragments.size(); i != -1; i--) {
                    ContextFragment fragment = current.fragments.get(i);
                    NameInfo result = fragment.names.get(name);
                    if (result != null)
                        return result.fullIRI;
                }
                current = current.parent;
            }
            return null;
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
        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;
        loadDocument(parseResult.getRoot(), store.getNodeIRI(uri), new Context());
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
    private ASTNode getExternalContextDefinition(String iri) {
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
    private void loadDocument(ASTNode node, GraphNode graph, Context context) {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object:
                loadObject(node, graph, context);
                break;
            case JSONLDParser.ID.array:
                loadArray(node, graph, context, null);
                break;
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
    private SubjectNode loadObject(ASTNode node, GraphNode graph, Context context) {
        Map<String, ASTNode> members = new HashMap<>();
        for (ASTNode child : node.getChildren()) {
            String key = getValue(child.getChildren().get(0));
            members.put(key, child.getChildren().get(1));
        }

        // load the new context
        Context current = context;
        ASTNode contextNode = members.get(KEYWORD_CONTEXT);
        if (contextNode != null)
            current = new Context(context, contextNode);

        // setup the subject from an id
        ASTNode idNode = members.get(KEYWORD_ID);
        SubjectNode subject = idNode != null ? getSubjectFor(idNode, current) : store.newNodeBlank();

        // setup the type
        ASTNode typeNode = members.get(KEYWORD_TYPE);
        if (typeNode != null) {
            Couple<Node, List<Node>> couple = loadValue(typeNode, graph, current, new NameInfo(ContainerType.Undefined, Vocabulary.rdfType, KEYWORD_ID, null));
            if (couple != null) {
                if (couple.x != null)
                    quads.add(new Quad(graph, subject, getNodeRDFType(), couple.x));
                else if (couple.y != null) {
                    for (Node value : couple.y) {
                        if (value != null)
                            quads.add(new Quad(graph, subject, getNodeRDFType(), couple.x));
                    }
                }
            }
        }

        // load the rest of the values
        for (Map.Entry<String, ASTNode> entry : members.entrySet()) {
            if (KEYWORDS.contains(entry.getKey()))
                // this is a keyword, drop it
                continue;
            NameInfo propertyInfo = current.getInfoFor(entry.getKey());
            if (propertyInfo == null)
                // drop this undefined property
                continue;
            IRINode property = store.getNodeIRI(propertyInfo.fullIRI);
            ASTNode definition = entry.getValue();
            Couple<Node, List<Node>> result = loadValue(definition, graph, current, propertyInfo);
            if (result == null)
                // weird result
                continue;
            if (result.x != null)
                quads.add(new Quad(graph, subject, property, result.x));
            else if (result.y != null) {
                // this was a multilingual property
                for (Node value : result.y)
                    quads.add(new Quad(graph, subject, property, value));
            }
        }
        return subject;
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
    private List<Node> loadArray(ASTNode node, GraphNode graph, Context context, NameInfo info) {
        List<Node> result = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            Couple<Node, List<Node>> couple = loadValue(child, graph, context, info);
            if (couple == null)
                continue;
            if (couple.x != null)
                result.add(couple.x);
            else if (couple.y != null) {
                for (Node value : couple.y) {
                    if (value != null)
                        result.add(value);
                }
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
    private Couple<Node, List<Node>> loadValue(ASTNode node, GraphNode graph, Context context, NameInfo info) {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object: {
                if (isValueNode(node))
                    return new Couple<>(loadValueNode(node, context, info), null);
                else if (info.containerType == ContainerType.Language)
                    return new Couple<>(null, loadMultilingualValues(node));
                return new Couple<Node, List<Node>>(loadObject(node, graph, context), null);
            }
            case JSONLDParser.ID.array:
                return new Couple<>(null, loadArray(node, graph, context, info));
            case JSONLDLexer.ID.LITERAL_INTEGER:
                return new Couple<Node, List<Node>>(loadLiteralInteger(node), null);
            case JSONLDLexer.ID.LITERAL_DECIMAL:
                return new Couple<Node, List<Node>>(loadLiteralDecimal(node), null);
            case JSONLDLexer.ID.LITERAL_DOUBLE:
                return new Couple<Node, List<Node>>(loadLiteralDouble(node), null);
            case JSONLDLexer.ID.LITERAL_STRING:
                return new Couple<>(loadLiteralString(node, context, info), null);
            case JSONLDLexer.ID.LITERAL_NULL:
                return new Couple<>(null, null);
            case JSONLDLexer.ID.LITERAL_TRUE:
                return new Couple<Node, List<Node>>(getNodeTrue(), null);
            case JSONLDLexer.ID.LITERAL_FALSE:
                return new Couple<Node, List<Node>>(getNodeFalse(), null);
        }
        return null;
    }

    /**
     * Gets the subject node defined by the specified AST node
     *
     * @param definition The AST node definition
     * @param context    The current context
     * @return The subject node, or null if it cannot be loaded
     */
    private SubjectNode getSubjectFor(ASTNode definition, Context context) {
        String value = getValue(definition);
        if (value == null)
            // subject is invalid
            return null;
        if (value.startsWith("_:")) {
            // this is blank node
            return resolveBlank(value.substring(2));
        } else {
            // this is an IRI
            return store.getNodeIRI(context.expandIRI(value));
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
     * Gets the RDF IRI node for the RDF type element
     *
     * @return The RDF IRI node
     */
    private IRINode getNodeRDFType() {
        if (cacheIsA == null)
            cacheIsA = store.getNodeIRI(Vocabulary.rdfType);
        return cacheIsA;
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
    private LiteralNode loadLiteralInteger(ASTNode node) {
        String value = node.getValue();
        return store.getLiteralNode(value, Vocabulary.xsdInteger, null);
    }

    /**
     * Gets the RDF Decimal Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Decimal Literal node
     */
    private LiteralNode loadLiteralDecimal(ASTNode node) {
        String value = node.getValue();
        return store.getLiteralNode(value, Vocabulary.xsdDecimal, null);
    }

    /**
     * Gets the RDF Double Literal equivalent to the specified AST node
     *
     * @param node An AST node
     * @return The equivalent RDF Double Literal node
     */
    private LiteralNode loadLiteralDouble(ASTNode node) {
        String value = node.getValue();
        return store.getLiteralNode(value, Vocabulary.xsdDouble, null);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF node
     */
    private Node loadLiteralString(ASTNode node, Context context, NameInfo info) {
        String value = getValue(node);
        if (info.valueType != null) {
            // coerced type
            if (KEYWORD_ID.equals(info.valueType))
                // this is an identification property
                return getSubjectFor(node, context);
            // this is a typed literal
            return store.getLiteralNode(value, info.valueType, null);
        }
        String language = info.language != null ? info.language : context.getLanguage();
        if (language != null && "null".equals(language))
            // explicit reset
            language = null;
        return store.getLiteralNode(value, Vocabulary.xsdString, language);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF node
     */
    private Node loadValueNode(ASTNode node, Context context, NameInfo info) {
        String value = null;
        String type = null;
        String language = "null";
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            if (KEYWORD_VALUE.equals(key))
                value = getValue(member.getChildren().get(1));
            else if (KEYWORD_TYPE.equals(key))
                type = getValue(member.getChildren().get(1));
            else if (KEYWORD_LANGUAGE.equals(key))
                language = getValue(member.getChildren().get(1));
        }
        if (type != null) {
            // coerced type
            return store.getLiteralNode(value, type, null);
        } else if (info.valueType != null) {
            // coerced type
            return store.getLiteralNode(value, info.valueType, null);
        } else {
            if (language == null)
                language = info.language != null ? info.language : context.getLanguage();
            if (language != null && "null".equals(language))
                // explicit reset
                language = null;
            return store.getLiteralNode(value, Vocabulary.xsdString, language);
        }
    }

    /**
     * Gets the RDF nodes equivalent to the specified AST node that represents the values of a multilingual property
     *
     * @param node An AST node
     * @return The equivalent RDF nodes
     */
    private List<Node> loadMultilingualValues(ASTNode node) {
        List<Node> result = new ArrayList<>();
        for (ASTNode member : node.getChildren()) {
            String language = getValue(member.getChildren().get(0));
            String value = getValue(member.getChildren().get(1));
            result.add(store.getLiteralNode(value, Vocabulary.xsdString, language));
        }
        return result;
    }

    /**
     * Gets the decoded value of the specified AST node that holds a literal string
     *
     * @param node An AST node that holds a literal string
     * @return The decoded value of the string
     */
    private static String getValue(ASTNode node) {
        if (node.getSymbol().getID() != JSONLDLexer.ID.LITERAL_STRING)
            return null;
        String value = node.getValue();
        value = value.substring(1, value.length() - 1);
        return Utils.unescape(value);
    }

    /**
     * Determines whether the specified AST node defines a value node (as opposed to an object node)
     *
     * @param node An AST node
     * @return true if this is a value node
     */
    private static boolean isValueNode(ASTNode node) {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            if (KEYWORD_VALUE.equals(key))
                return true;
        }
        return false;
    }
}
