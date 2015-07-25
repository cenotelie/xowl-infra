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
     * Represents an exception that occurs while loading some JSON-LD data due to some error in the input data
     */
    private static final class LoadingException extends Exception {
        /**
         * The AST node from which the error originated
         */
        private final ASTNode origin;

        /**
         * Gets the AST node from which the error originated
         *
         * @return The AST node from which the error originated
         */
        public ASTNode getOrigin() {
            return origin;
        }

        /**
         * Initializes this exception
         *
         * @param description The description for this exception
         * @param origin      The AST node that from which the error originated
         */
        public LoadingException(String description, ASTNode origin) {
            super(description);
            this.origin = origin;
        }
    }

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
         * The property that is reversed by this one
         */
        public final String reversed;

        /**
         * Initializes the content of this object
         *
         * @param containerType The type of container for a multi-valued property
         * @param fullIRI       The full IRI of the name
         * @param valueType     The full IRI of the type for values of this property
         * @param language      The language associated to this property (implies the value type is xsd:string)
         * @param reversed      The property that is reversed by this one
         */
        public NameInfo(ContainerType containerType, String fullIRI, String valueType, String language, String reversed) {
            this.containerType = containerType;
            this.fullIRI = fullIRI;
            this.valueType = valueType;
            this.language = language;
            this.reversed = reversed;
        }

        /**
         * Initializes this name information
         *
         * @param definition The definition to load from
         * @param parent     The parent context
         * @param iri        The assumed full IRI, if any
         */
        public NameInfo(ASTNode definition, Context parent, String iri) throws LoadingException {
            switch (definition.getSymbol().getID()) {
                case JSONLDLexer.ID.LITERAL_STRING:
                    // this is an IRI
                    containerType = ContainerType.Undefined;
                    fullIRI = parent.expandIRI(getValue(definition));
                    valueType = null;
                    language = null;
                    reversed = null;
                    break;
                case JSONLDParser.ID.object:
                    // this is an object
                    String container = null;
                    String id = null;
                    String type = null;
                    String lang = null;
                    String rev = null;
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
                        } else if (KEYWORD_REVERSE.equals(key)) {
                            rev = parent.expandIRI(getValue(member.getChildren().get(1)));
                        }
                    }
                    fullIRI = id != null ? id : iri;
                    valueType = KEYWORD_ID.equals(type) ? KEYWORD_ID : parent.expandIRI(type);
                    language = lang;
                    reversed = rev;
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
                    reversed = null;
            }
        }
    }

    /**
     * The property info for the rdf:type property
     */
    private static final NameInfo PROPERTY_TYPE_INFO = new NameInfo(ContainerType.Undefined, Vocabulary.rdfType, KEYWORD_ID, null, null);

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
        public ContextFragment(ASTNode definition) throws LoadingException {
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
        public void loadNames(Context parent, ASTNode definition) throws LoadingException {
            for (ASTNode member : definition.getChildren()) {
                String key = getValue(member.getChildren().get(0));
                if (key == null)
                    throw new LoadingException("Expected valid key", definition);
                if (KEYWORDS.contains(key)) {
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
        public Context(Context parent, ASTNode definition) throws LoadingException {
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
            String reversed = null;
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
                            if (reversed == null)
                                reversed = info.reversed;
                        }
                    }
                }
                current = current.parent;
            }
            return new NameInfo(containerType, fullIRI, valueType, language, reversed);
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
        try {
            loadDocument(parseResult.getRoot(), store.getNodeIRI(uri), new Context());
        } catch (LoadingException exception) {
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
    private ASTNode getExternalContextDefinition(String iri) throws LoadingException {
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
    private void loadDocument(ASTNode node, GraphNode graph, Context context) throws LoadingException {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object:
                loadObject(node, graph, context);
                break;
            case JSONLDParser.ID.array:
                loadArray(node, graph, context, null);
                break;
            default:
                throw new LoadingException("Unrecognized input " + node.getSymbol().getName(), node);
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
    private SubjectNode loadObject(ASTNode node, GraphNode graph, Context context) throws LoadingException {
        Map<String, ASTNode> members = new HashMap<>();
        for (ASTNode child : node.getChildren()) {
            String key = getValue(child.getChildren().get(0));
            members.put(key, child.getChildren().get(1));
        }

        // load the new context
        Context current = context;
        ASTNode contextNode = members.remove(KEYWORD_CONTEXT);
        if (contextNode != null)
            current = new Context(context, contextNode);

        // setup the subject from an id
        ASTNode idNode = members.remove(KEYWORD_ID);
        SubjectNode subject = idNode != null ? getSubjectFor(idNode, current) : null;

        ASTNode graphNode = members.remove(KEYWORD_GRAPH);
        if (graphNode != null) {
            // this is a graph
            loadValue(graphNode, subject != null ? (GraphNode) subject : store.getNodeIRI(resource), current, null);
        }

        if (members.isEmpty())
            return subject;

        if (subject == null)
            subject = store.newNodeBlank();

        // setup the type
        ASTNode typeNode = members.remove(KEYWORD_TYPE);
        if (typeNode != null) {
            Couple<Node, List<Node>> couple = loadValue(typeNode, graph, current, PROPERTY_TYPE_INFO);
            if (couple.x != null)
                quads.add(new Quad(graph, subject, store.getNodeIRI(Vocabulary.rdfType), couple.x));
            else if (couple.y != null) {
                for (Node value : couple.y) {
                    if (value != null)
                        quads.add(new Quad(graph, subject, store.getNodeIRI(Vocabulary.rdfType), couple.x));
                }
            }
        }

        // load the rest of the values
        for (Map.Entry<String, ASTNode> entry : members.entrySet()) {
            loadMember(entry.getKey(), entry.getValue(), subject, graph, context, false);
        }

        // inline reversed properties
        for (Map.Entry<String, ASTNode> entry : members.entrySet()) {
            if (!KEYWORD_REVERSE.equals(entry.getKey()))
                continue;
            for (ASTNode member : entry.getValue().getChildren()) {
                String key = getValue(member.getChildren().get(0));
                loadMember(key, member.getChildren().get(1), subject, graph, context, true);
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
    private void loadMember(String key, ASTNode definition, SubjectNode subject, GraphNode graph, Context context, boolean reversed) throws LoadingException {
        if (KEYWORDS.contains(key))
            // this is a keyword, drop it
            return;
        NameInfo propertyInfo = context.getInfoFor(key);
        if (propertyInfo == null)
            // drop this undefined property
            return;
        IRINode property = store.getNodeIRI(propertyInfo.fullIRI);
        Couple<Node, List<Node>> result = loadValue(definition, graph, context, propertyInfo);
        if (result.x != null) {
            if (propertyInfo.reversed != null)
                quads.add(new Quad(graph, (SubjectNode) result.x, store.getNodeIRI(propertyInfo.reversed), subject));
            else if (reversed)
                quads.add(new Quad(graph, (SubjectNode) result.x, property, subject));
            else
                quads.add(new Quad(graph, subject, property, result.x));
        } else if (result.y != null) {
            for (Node value : result.y) {
                if (propertyInfo.reversed != null)
                    quads.add(new Quad(graph, (SubjectNode) value, store.getNodeIRI(propertyInfo.reversed), subject));
                else if (reversed)
                    quads.add(new Quad(graph, (SubjectNode) value, property, subject));
                else
                    quads.add(new Quad(graph, subject, property, value));
            }
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
    private List<Node> loadArray(ASTNode node, GraphNode graph, Context context, NameInfo info) throws LoadingException {
        List<Node> result = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            Couple<Node, List<Node>> couple = loadValue(child, graph, context, info);
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
    private Couple<Node, List<Node>> loadValue(ASTNode node, GraphNode graph, Context context, NameInfo info) throws LoadingException {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object: {
                if (isValueNode(node))
                    return new Couple<>(loadValueNode(node, context, info), null);
                else if (isListNode(node))
                    return new Couple<>(loadListNode(node, graph, context, info), null);
                else if (info != null && info.containerType == ContainerType.Language)
                    return new Couple<>(null, loadMultilingualValues(node));
                return new Couple<Node, List<Node>>(loadObject(node, graph, context), null);
            }
            case JSONLDParser.ID.array:
                if (info != null && info.containerType == ContainerType.List)
                    return new Couple<>(createRDFList(graph, loadArray(node, graph, context, info)), null);
                else
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
                return new Couple<Node, List<Node>>(store.getLiteralNode("true", Vocabulary.xsdBoolean, null), null);
            case JSONLDLexer.ID.LITERAL_FALSE:
                return new Couple<Node, List<Node>>(store.getLiteralNode("false", Vocabulary.xsdBoolean, null), null);
            default:
                throw new LoadingException("Unrecognized input " + node.getSymbol().getName(), node);
        }
    }

    /**
     * Gets the subject node defined by the specified AST node
     *
     * @param definition The AST node definition
     * @param context    The current context
     * @return The subject node, or null if it cannot be loaded
     */
    private SubjectNode getSubjectFor(ASTNode definition, Context context) throws LoadingException {
        String value = getValue(definition);
        if (value == null)
            // subject is invalid
            throw new LoadingException("Expected a valid node id", definition);
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
     * Creates a RDF list for the specified elements
     *
     * @param graph    The current graph
     * @param elements The elements of the list
     * @return The list's head
     */
    private Node createRDFList(GraphNode graph, List<Node> elements) {
        List<Node> filtered = new ArrayList<>(elements);
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
    private Node loadLiteralString(ASTNode node, Context context, NameInfo info) throws LoadingException {
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
    private Node loadValueNode(ASTNode node, Context context, NameInfo info) throws LoadingException {
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
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node    An AST node
     * @param context The parent context
     * @param info    The information on the current name (property)
     * @return The equivalent RDF node
     */
    private Node loadListNode(ASTNode node, GraphNode graph, Context context, NameInfo info) throws LoadingException {
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            if (KEYWORD_LIST.equals(key)) {
                return createRDFList(graph, loadArray(member.getChildren().get(1), graph, context, info));
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
    private List<Node> loadMultilingualValues(ASTNode node) throws LoadingException {
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
    private static String getValue(ASTNode node) throws LoadingException {
        if (node.getSymbol().getID() == JSONLDLexer.ID.LITERAL_NULL)
            return null;
        if (node.getSymbol().getID() != JSONLDLexer.ID.LITERAL_STRING)
            throw new LoadingException("Expected a string value", node);
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
    private static boolean isValueNode(ASTNode node) throws LoadingException {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            if (KEYWORD_VALUE.equals(key))
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
    private static boolean isListNode(ASTNode node) throws LoadingException {
        if (node.getSymbol().getID() != JSONLDParser.ID.object)
            return false;
        for (ASTNode member : node.getChildren()) {
            String key = getValue(member.getChildren().get(0));
            if (KEYWORD_LIST.equals(key))
                return true;
        }
        return false;
    }
}
