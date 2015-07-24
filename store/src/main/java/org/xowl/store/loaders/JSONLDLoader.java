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
 * Loader for JSON LD sources
 *
 * @author Laurent Wouters
 */
public class JSONLDLoader implements Loader {
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
        Index
    }

    /**
     * Represents the information about a property
     */
    private static class PropertyInfo {
        /**
         * The type of container for a multi-valued property
         */
        public final ContainerType containerType;
        /**
         * The full IRI of the property
         */
        public final String fullIRI;
        /**
         * The full IRI of the type for values of this property
         */
        public final String valueType;

        /**
         * Initializes this property information
         * @param definition The definition to load from
         * @param context The current context
         */
        public PropertyInfo(ASTNode definition, Context context) {
            switch (definition.getSymbol().getID()) {
                case JSONLDLexer.ID.LITERAL_STRING:
                    // this is an IRI
                    containerType = ContainerType.Undefined;
                    fullIRI = context.expandIRI(getValue(definition));
                    valueType = null;
                    break;
                case JSONLDParser.ID.object:
                    // this is an object
                    String container = null;
                    String id = null;
                    String type = null;
                    for (ASTNode member : definition.getChildren()) {
                        String key = getValue(member.getChildren().get(0));
                        if (KEYWORD_ID.equals(key)) {
                            id = context.expandIRI(getValue(member.getChildren().get(1)));
                        } else if (KEYWORD_TYPE.equals(key)) {
                            type = getValue(member.getChildren().get(1));
                            if (!KEYWORD_ID.equals(type))
                                type = context.expandIRI(type);
                        } else if (KEYWORD_CONTAINER.equals(key)) {
                            container = getValue(member.getChildren().get(1));
                        }
                    }
                    fullIRI = id;
                    if (KEYWORD_ID.equals(type))
                        valueType = fullIRI;
                    else
                        valueType = type;
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
                    fullIRI = null;
                    valueType = null;
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
         * The current namespaces
         */
        private final Map<String, PropertyInfo> properties;
        /**
         * The current language
         */
        private final String language;
        /**
         * The current base URI
         */
        private final String base;
        /**
         * The current vocabulary radical
         */
        private final String vocab;

        /**
         * Initializes an empty context
         */
        public Context() {
            this(null, null);
        }

        /**
         * Initializes this context with the specified parent
         * @param parent The parent context
         * @param definition The AST node to load from
         */
        public Context(Context parent, ASTNode definition) {
            this.parent = parent;
            this.properties = new HashMap<>();
            if (definition != null) {
                if (definition.getSymbol().getID() == JSONLDLexer.ID.LITERAL_STRING)
                    definition = loadExternalDocument(getValue(definition));
                if (definition != null) {
                    String tLanguage = null;
                    String tBase = null;
                    String tVocab = null;
                    for (ASTNode member : definition.getChildren()) {
                        String key = getValue(member.getChildren().get(0));


                    }
                    this.language = tLanguage;
                    this.base = tBase;
                    this.vocab = tVocab;
                    return;
                }
            }
            this.language = null;
            this.base = null;
            this.vocab = null;
        }

        /**
         * Expands the specified term to get the corresponding RDF node
         * @param term A term
         * @return The corresponding RDF node
         */
        public Node expand(String term) {
            if (term.contains(":")) {
                String[] parts = term.split(":");
                for (int i = 1; i != parts.length; i++) {
                    String prefix = rebuild(parts, 0, i - 1);
                    String suffix = rebuild(parts, i, parts.length - 1);
                    if (prefix.equals("_")) {
                        // this is a blank node
                        BlankNode result = blanks.get(suffix);
                        if (result == null) {
                            result = store.newNodeBlank();
                            blanks.put(suffix, result);
                        }
                        return result;
                    }
                    if (suffix.startsWith("//"))
                        return store.getNodeIRI(prefix + ":" + suffix);
                    String expandedPrefix = expandNamespace(prefix);
                    if (!expandedPrefix.equals(prefix))
                        // expanded
                        return store.getNodeIRI(expandedPrefix + suffix);
                }
                return null;
            } else {
                String expanded = expandNamespace(term);
                return expanded.equals(term) ? null : store.getNodeIRI(expanded);
            }
        }


        public String expandIRI(String term) {
            if (term.contains(":")) {
                String[] parts = term.split(":");
                for (int i = 1; i != parts.length; i++) {
                    String prefix = rebuild(parts, 0, i - 1);
                    String suffix = rebuild(parts, i, parts.length - 1);
                    if (suffix.startsWith("//"))
                        return store.getNodeIRI(prefix + ":" + suffix);
                    String expandedPrefix = expandNamespace(prefix);
                    if (!expandedPrefix.equals(prefix))
                        // expanded
                        return store.getNodeIRI(expandedPrefix + suffix);
                }
                return null;
            } else {
                String expanded = expandNamespace(term);
                return expanded.equals(term) ? null : store.getNodeIRI(expanded);
            }
        }


        /**
         * Rebuilds a split string
         * @param parts The parts
         * @param start The index of the first part
         * @param end The index of the last part
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
         * @param name A name
         * @return The full name
         */
        private String expandNamespace(String name) {
            Context current = this;
            while (current != null) {
                String result = current.namespaces.get(name);
                if (result != null)
                    return result;
                current = current.parent;
            }
            return name;
        }
    }

    /**
     * The RDF store to create nodes from
     */
    private final RDFStore store;
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
        blanks = new HashMap<>();
        RDFLoaderResult result = new RDFLoaderResult();
        GraphNode graph = store.getNodeIRI(uri);

        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;

        try {
            for (ASTNode triple : parseResult.getRoot().getChildren()) {
                Node n1 = getRDFNode(triple.getChildren().get(0));
                Node n2 = getRDFNode(triple.getChildren().get(1));
                Node n3 = getRDFNode(triple.getChildren().get(2));
                result.getQuads().add(new Quad(graph, (SubjectNode) n1, (Property) n2, n3));
            }
        } catch (IllegalArgumentException ex) {
            // IRI must be absolute
            return null;
        }

        return result;
    }

    @Override
    public OWLLoaderResult loadOWL(Logger logger, Reader reader, String uri) {
        throw new UnsupportedOperationException();
    }


    /**
     * Loads an external document at the specified IRI
     * @param iri The IRI of an auxiliary document
     * @return The parsed external document, or null if an error occured
     */
    private ASTNode loadExternalDocument(String iri) {
        return null;
    }




    /**
     * Loads a JSON-LD document from the specified AST node
     * @param node The AST node
     */
    private void loadDocument(ASTNode node) {
        GraphNode graph = store.getDefaultGraph();
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object:
                loadObject(node, graph);
                break;
            case JSONLDParser.ID.array:
                loadArray(node, graph);
                break;
        }
    }

    /**
     * Loads a JSON-LD object from the specified AST node
     * @param node The AST node
     * @param graph The parent graph
     * @param context The parent context
     * @return The corresponding RDF node
     */
    private SubjectNode loadObject(ASTNode node, GraphNode graph, Context context) {
        Map<String, ASTNode> members = new HashMap<>();
        for (ASTNode child : node.getChildren()) {
            String key = child.getChildren().get(0).getValue();
            key = key.substring(1, key.length() - 1);
            key = Utils.unescape(key);
            members.put(key, child);
        }

        Context current = new Context(context);
        ASTNode contextNode = members.get("@context");
        if (contextNode != null) {

        }

        SubjectNode subject = null;
        if (members.containsKey("@id")) {
            ASTNode valueNode = members.get("@id");

        }

        return null;
    }

    /**
     * Loads a JSON-LD array from the specified AST node
     * @param node The AST node
     * @param graph The parent graph
     * @param context The parent context
     * @return The corresponding RDF nodes
     */
    private List<Node> loadArray(ASTNode node, GraphNode graph, Context context) {
        List<Node> result = new ArrayList<>();
        for (ASTNode child : node.getChildren())
            result.add(loadValue(child, graph, context));
        return result;
    }

    /**
     * Loads a JSON-LD value from the specified AST node
     * @param node The AST node
     * @param graph The parent graph
     * @param context The parent context
     * @return The corresponding RDF node
     */
    private Node loadValue(ASTNode node, GraphNode graph, Context context) {
        switch (node.getSymbol().getID()) {
            case JSONLDParser.ID.object:
                return loadObject(node, graph, context);
            case JSONLDParser.ID.array: {
                // construct the RDF list
                break;
            }
            case JSONLDLexer.ID.LITERAL_INTEGER:
            case JSONLDLexer.ID.LITERAL_DECIMAL:
            case JSONLDLexer.ID.LITERAL_DOUBLE:
            case JSONLDLexer.ID.LITERAL_STRING:
            case JSONLDLexer.ID.LITERAL_NULL:
            case JSONLDLexer.ID.LITERAL_TRUE:
            case JSONLDLexer.ID.LITERAL_FALSE:
                break;
        }
        return null;
    }




    private Context loadContext(ASTNode node, Context parentContext) {
        Context context = new Context(parentContext);

        for (ASTNode child : node.getChildren()) {
            String key = getStringValue(child.getChildren().get(0));
            if (key.equals("@language")) {
                String value = getStringValue(child.getChildren().get(1));
                context.setLanguage(value);
            } else if (key.equals("@base")) {
                String value = getStringValue(child.getChildren().get(1));
                context.setLanguage(value);
            } else if (key.equals("@vocab")) {

            } else if (key.startsWith("@")) {

            } else {

            }
        }


        return context;
    }





    /**
     * Loads the specified node as a value expected to be an IRI
     * @param node The AST node
     * @param context The current context
     * @return The IRI, or null if the value is ill-formed
     */
    private String loadValueAsIRI(ASTNode node, Context context) {

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
     * Gets the decoded value of the specified AST node that holds a literal string
     * @param node An AST node that holds a literal string
     * @return The decoded value of the string
     */
    private static String getValue(ASTNode node) {
        if (node.getSymbol().getID() == JSONLDLexer.ID.LITERAL_NULL)
            return null;
        String value = node.getValue();
        value = value.substring(1, value.length() - 1);
        return Utils.unescape(value);
    }
}
