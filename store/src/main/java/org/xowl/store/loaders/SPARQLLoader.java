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
import org.xowl.store.sparql.*;
import org.xowl.store.storage.NodeManager;
import org.xowl.utils.Files;
import org.xowl.utils.Logger;
import org.xowl.utils.collections.Couple;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Loader for SPARQL queries
 *
 * @author Laurent Wouters
 */
public class SPARQLLoader {
    /**
     * The RDF store to create nodes from
     */
    private final NodeManager store;
    /**
     * The context's default IRIs
     */
    private final Collection<String> defaultIRIs;
    /**
     * The context's named IRIs
     */
    private final Collection<String> namedIRIs;
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
     * Map of the current variable nodes
     */
    private Map<String, VariableNode> variables;
    /**
     * The cached node for the RDF#nil element
     */
    private IRINode cacheNil;
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
    public SPARQLLoader(NodeManager store) {
        this.store = store;
        this.defaultIRIs = new ArrayList<>();
        this.namedIRIs = new ArrayList<>();
    }

    /**
     * Initializes this loader
     *
     * @param store       The RDF store used to create nodes
     * @param defaultIRIs The context's default IRIs
     * @param namedIRIs   The context's named IRIs
     */
    public SPARQLLoader(NodeManager store, Collection<String> defaultIRIs, Collection<String> namedIRIs) {
        this.store = store;
        this.defaultIRIs = new ArrayList<>(defaultIRIs);
        this.namedIRIs = new ArrayList<>(namedIRIs);
    }

    /**
     * Loads a query from the specified input
     *
     * @param logger The logger to use
     * @param reader The resource's reader
     * @return The loaded data
     */
    public List<Command> load(Logger logger, Reader reader) {
        baseURI = null;
        namespaces = new HashMap<>();
        blanks = new HashMap<>();
        variables = new HashMap<>();

        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return null;

        try {
            switch (parseResult.getRoot().getSymbol().getID()) {
                case SPARQLParser.ID.query:
                    Command comand = loadQuery(parseResult.getRoot());
                    return (comand == null ? null : Collections.singletonList(comand));
                case SPARQLParser.ID.update:
                    return loadUpdate(parseResult.getRoot());
            }
            return null;
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

    /**
     * Parses the specified input
     *
     * @param logger The logger to use
     * @param reader The input to parse
     * @return The result of the parsing operation
     */
    private ParseResult parse(Logger logger, Reader reader) {
        ParseResult result;
        try {
            String content = Files.read(reader);
            SPARQLLexer lexer = new SPARQLLexer(content);
            SPARQLParser parser = new SPARQLParser(lexer);
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


    /**
     * Loads a query from the specified AST
     *
     * @param node An AST node
     * @return The command
     */
    private Command loadQuery(ASTNode node) throws LoaderException {
        loadPrologue(node.getChildren().get(0));
        switch (node.getChildren().get(1).getSymbol().getID()) {
            case SPARQLParser.ID.select:
            case SPARQLParser.ID.construct:
            case SPARQLParser.ID.describe:
            case SPARQLParser.ID.ask:
                break;
        }
        return null;
    }

    /**
     * Loads a series of updates from the specified AST node
     *
     * @param node An AST node
     * @return The commands
     */
    private List<Command> loadUpdate(ASTNode node) throws LoaderException {
        List<Command> result = new ArrayList<>();
        while (node != null) {
            namespaces.clear();
            baseURI = null;
            loadPrologue(node.getChildren().get(0));
            if (node.getChildren().size() >= 2) {
                switch (node.getChildren().get(1).getSymbol().getID()) {
                    case SPARQLParser.ID.load:
                        result.add(loadCommandLoad(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.create:
                        result.add(loadCommandCreate(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.drop:
                        result.add(loadCommandDrop(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.clear:
                        result.add(loadCommandClear(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.copy:
                        result.add(loadCommandCopy(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.move:
                        result.add(loadCommandMove(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.add:
                        result.add(loadCommandAdd(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.insert:
                        result.add(loadCommandInsertData(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.delete:
                        result.add(loadCommandDeleteData(node.getChildren().get(1)));
                        break;
                    case SPARQLParser.ID.deleteWhere:
                        result.add(loadCommandDeleteWhere(node.getChildren().get(1)));
                        break;
                }
            }
            node = node.getChildren().size() >= 3 ? node.getChildren().get(2) : null;
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
                case SPARQLParser.ID.decl_base:
                    loadBase(child);
                    break;
                case SPARQLParser.ID.decl_prefix:
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
     * Loads a LOAD command from the specified AST node
     *
     * @param node An AST node
     * @return The LOAD command
     */
    private Command loadCommandLoad(ASTNode node) throws LoaderException {
        boolean isSilent = (node.getChildren().get(0).getSymbol().getID() == SPARQLLexer.ID.SILENT);
        int index = isSilent ? 1 : 0;
        String toLoad = loadGraphRef(node.getChildren().get(index)).y;
        String target = null;
        index++;
        if (index < node.getChildren().size())
            target = loadGraphRef(node.getChildren().get(index)).y;
        return new CommandLoad(toLoad, target, isSilent);
    }

    /**
     * Loads a CREATE command from the specified AST node
     *
     * @param node An AST node
     * @return The CREATE command
     */
    private Command loadCommandCreate(ASTNode node) throws LoaderException {
        int count = node.getChildren().size();
        boolean isSilent = (count >= 2);
        ASTNode child = node.getChildren().get(count - 1);
        IRINode iriNode = null;
        switch (child.getSymbol().getID()) {
            case SPARQLLexer.ID.IRIREF:
                iriNode = getNodeIRIRef(child);
                break;
            case SPARQLLexer.ID.PNAME_LN:
                iriNode = getNodePNameLN(child);
                break;
            case SPARQLLexer.ID.PNAME_NS:
                iriNode = getNodePNameNS(child);
                break;
        }
        return new CommandCreate(iriNode != null ? iriNode.getIRIValue() : null, isSilent);
    }

    /**
     * Loads a DROP command from the specified AST node
     *
     * @param node An AST node
     * @return The CREATE command
     */
    private Command loadCommandDrop(ASTNode node) throws LoaderException {
        int count = node.getChildren().size();
        boolean isSilent = (count >= 2);
        Couple<GraphReferenceType, String> ref = loadGraphRef(node.getChildren().get(count - 1));
        return new CommandDrop(ref.x, ref.y, isSilent);
    }

    /**
     * Loads a CLEAR command from the specified AST node
     *
     * @param node An AST node
     * @return The CREATE command
     */
    private Command loadCommandClear(ASTNode node) throws LoaderException {
        int count = node.getChildren().size();
        boolean isSilent = (count >= 2);
        Couple<GraphReferenceType, String> ref = loadGraphRef(node.getChildren().get(count - 1));
        return new CommandClear(ref.x, ref.y, isSilent);
    }

    /**
     * Loads a COPY command from the specified AST node
     *
     * @param node An AST node
     * @return The COPY command
     */
    private Command loadCommandCopy(ASTNode node) throws LoaderException {
        int count = node.getChildren().size();
        boolean isSilent = (count >= 3);
        Couple<GraphReferenceType, String> refOrigin = loadGraphRef(node.getChildren().get(count - 2));
        Couple<GraphReferenceType, String> refTarget = loadGraphRef(node.getChildren().get(count - 1));
        return new CommandCopy(refOrigin.x, refOrigin.y, refTarget.x, refTarget.y, isSilent);
    }

    /**
     * Loads a COPY command from the specified AST node
     *
     * @param node An AST node
     * @return The COPY command
     */
    private Command loadCommandMove(ASTNode node) throws LoaderException {
        int count = node.getChildren().size();
        boolean isSilent = (count >= 3);
        Couple<GraphReferenceType, String> refOrigin = loadGraphRef(node.getChildren().get(count - 2));
        Couple<GraphReferenceType, String> refTarget = loadGraphRef(node.getChildren().get(count - 1));
        return new CommandMove(refOrigin.x, refOrigin.y, refTarget.x, refTarget.y, isSilent);
    }

    /**
     * Loads a ADD command from the specified AST node
     *
     * @param node An AST node
     * @return The ADD command
     */
    private Command loadCommandAdd(ASTNode node) throws LoaderException {
        int count = node.getChildren().size();
        boolean isSilent = (count >= 3);
        Couple<GraphReferenceType, String> refOrigin = loadGraphRef(node.getChildren().get(count - 2));
        Couple<GraphReferenceType, String> refTarget = loadGraphRef(node.getChildren().get(count - 1));
        return new CommandAdd(refOrigin.x, refOrigin.y, refTarget.x, refTarget.y, isSilent);
    }

    /**
     * Loads an INSERT DATA command from the specified AST node
     *
     * @param node An AST node
     * @return The INSERT DATA command
     */
    private Command loadCommandInsertData(ASTNode node) throws LoaderException {
        List<Quad> quads = loadQuads(node.getChildren().get(0), store.getIRINode(NodeManager.DEFAULT_GRAPH));
        return new CommandInsertData(Collections.unmodifiableCollection(quads));
    }

    /**
     * Loads an DELETE DATA command from the specified AST node
     *
     * @param node An AST node
     * @return The DELETE DATA command
     */
    private Command loadCommandDeleteData(ASTNode node) throws LoaderException {
        List<Quad> quads = loadQuads(node.getChildren().get(0), store.getIRINode(NodeManager.DEFAULT_GRAPH));
        return new CommandDeleteData(Collections.unmodifiableCollection(quads));
    }

    /**
     * Loads an DELETE WHERE command from the specified AST node
     *
     * @param node An AST node
     * @return The DELETE WHERE command
     */
    private Command loadCommandDeleteWhere(ASTNode node) throws LoaderException {
        List<Quad> quads = loadQuads(node.getChildren().get(0), store.getIRINode(NodeManager.DEFAULT_GRAPH));
        return new CommandDeleteWhere(Collections.unmodifiableCollection(quads));
    }

    /**
     * Loads a graph reference from the specified AST node
     *
     * @param node An AST node
     * @return The reference
     */
    private Couple<GraphReferenceType, String> loadGraphRef(ASTNode node) throws LoaderException {
        GraphReferenceType refType = GraphReferenceType.Single;
        IRINode iriNode = null;
        switch (node.getSymbol().getID()) {
            case SPARQLLexer.ID.DEFAULT:
                refType = GraphReferenceType.Default;
                break;
            case SPARQLLexer.ID.NAMED:
                refType = GraphReferenceType.Named;
                break;
            case SPARQLLexer.ID.ALL:
                refType = GraphReferenceType.All;
                break;
            case SPARQLLexer.ID.IRIREF:
                iriNode = getNodeIRIRef(node);
                break;
            case SPARQLLexer.ID.PNAME_LN:
                iriNode = getNodePNameLN(node);
                break;
            case SPARQLLexer.ID.PNAME_NS:
                iriNode = getNodePNameNS(node);
                break;
        }
        return new Couple<>(refType, iriNode == null ? null : iriNode.getIRIValue());
    }

    /**
     * Loads quads from the specified AST node
     *
     * @param node  An AST node
     * @param graph The current graph to use
     * @return The quads
     */
    private List<Quad> loadQuads(ASTNode node, GraphNode graph) throws LoaderException {
        // quads -> triples_template? quads_supp*
        List<Quad> result = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case SPARQLParser.ID.triples_template:
                    loadTriples(child, graph, result);
                    break;
                case SPARQLParser.ID.quads_supp:
                    loadQuadsSupplementary(child, graph, result);
                    break;
            }
        }
        return result;
    }

    /**
     * Loads quads from the specified AST node
     *
     * @param node   An AST node
     * @param graph  The current graph to use
     * @param buffer The buffer of quads
     */
    private void loadTriples(ASTNode node, GraphNode graph, List<Quad> buffer) throws LoaderException {
        // triples_template -> triples_same_subj ('.'! triples_template? )?
        ASTNode current = node;
        while (current != null) {
            ASTNode sameSubj = node.getChildren().get(0);
            // triples_same_subj -> var_or_term    property_list_not_empty
            // triples_same_subj -> triples_node   property_list
            Node subject = getNode(sameSubj.getChildren().get(0), graph, buffer);
            // property_list_not_empty -> verb object_list (';'! (verb object_list)? )*
            List<ASTNode> members = sameSubj.getChildren().get(1).getChildren();
            for (int i = 0; i != members.size(); i++) {
                Node verb = getNode(members.get(i), graph, buffer);
                // object_list -> object (','! object)*
                // object -> graph_node^
                // graph_node -> var_or_term^ | triples_node^
                for (ASTNode objectNode : members.get(i + 1).getChildren()) {
                    Node object = getNode(objectNode, graph, buffer);
                    buffer.add(new Quad(graph, (SubjectNode) subject, (Property) verb, object));
                }
                i++;
            }
            if (current.getChildren().size() >= 2) {
                current = current.getChildren().get(1);
            } else {
                current = null;
            }
        }
    }

    /**
     * Loads quads from the specified AST node
     *
     * @param node   An AST node
     * @param graph  The current graph to use
     * @param buffer The buffer of quads
     */
    private void loadQuadsSupplementary(ASTNode node, GraphNode graph, List<Quad> buffer) throws LoaderException {
        // quads_supp -> quads_not_triples ('.'!)? triples_template?
        // quads_not_triples -> GRAPH! var_or_iri '{'! triples_template? '}'!
        ASTNode quadsNotTriples = node.getChildren().get(0);
        GraphNode inner = (GraphNode) getNode(quadsNotTriples.getChildren().get(0), graph, buffer);
        if (quadsNotTriples.getChildren().size() >= 2)
            loadTriples(quadsNotTriples.getChildren().get(1), inner, buffer);
        if (node.getChildren().size() >= 2)
            loadTriples(node.getChildren().get(1), graph, buffer);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param node   An AST node
     * @param graph  The current graph to use
     * @param buffer The buffer of quads
     * @return The equivalent RDF nodes
     */
    private Node getNode(ASTNode node, GraphNode graph, List<Quad> buffer) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case 0x00EE: // a
                return getNodeIsA();
            case SPARQLParser.ID.nil:
                return getNodeNil();
            case SPARQLLexer.ID.IRIREF:
                return getNodeIRIRef(node);
            case SPARQLLexer.ID.PNAME_LN:
                return getNodePNameLN(node);
            case SPARQLLexer.ID.PNAME_NS:
                return getNodePNameNS(node);
            case SPARQLLexer.ID.BLANK_NODE_LABEL:
                return getNodeBlank(node);
            case SPARQLLexer.ID.ANON:
                return getNodeAnon();
            case SPARQLLexer.ID.VARIABLE:
                return getVariable(node);
            case 0x0147: // true
                return getNodeTrue();
            case 0x0148: // false
                return getNodeFalse();
            case SPARQLLexer.ID.INTEGER:
                return getNodeInteger(node);
            case SPARQLLexer.ID.DECIMAL:
                return getNodeDecimal(node);
            case SPARQLLexer.ID.DOUBLE:
                return getNodeDouble(node);
            case SPARQLParser.ID.literal_rdf:
                return getNodeLiteral(node);
            case SPARQLParser.ID.collection:
                return getNodeCollection(node, graph, buffer);
            case SPARQLParser.ID.property_list_not_empty:
                return getNodeBlankWithProperties(node, graph, buffer);
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
     * Gets the RDF IRI node for the RDF nil element
     *
     * @return The RDF IRI node
     */
    private IRINode getNodeNil() {
        if (cacheNil == null)
            cacheNil = store.getIRINode(Vocabulary.rdfNil);
        return cacheNil;
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
        return store.getIRINode(Utils.uriResolveRelative(baseURI, value));
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
        value = Utils.unescape(value.substring(0, value.length() - 1));
        value = namespaces.get(value);
        return store.getIRINode(value);
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param node  The parent ASt node
     * @param value An escaped local name
     * @return The equivalent full IRI
     */
    private String getIRIForLocalName(ASTNode node, String value) throws LoaderException {
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
        throw new LoaderException("Failed to resolve local name " + value, node);
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
     * Gets the variable node for the specified AST node
     *
     * @param node An AST node
     * @return The associated variable node
     */
    private VariableNode getVariable(ASTNode node) {
        String name = node.getValue().substring(1);
        VariableNode variable = variables.get(name);
        if (variable == null) {
            variable = new VariableNode(name);
            variables.put(name, variable);
        }
        return variable;
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
                value = Utils.unescape(value);
                break;
            case TurtleLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
            case TurtleLexer.ID.STRING_LITERAL_LONG_QUOTE:
                value = childString.getValue();
                value = value.substring(3, value.length() - 3);
                value = Utils.unescape(value);
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
            iri = Utils.unescape(iri.substring(1, iri.length() - 1));
            return store.getLiteralNode(value, Utils.uriResolveRelative(baseURI, iri), null);
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(suffixChild, suffixChild.getValue());
            return store.getLiteralNode(value, local, null);
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getValue();
            ns = Utils.unescape(ns.substring(0, ns.length() - 1));
            ns = namespaces.get(ns);
            return store.getLiteralNode(value, ns, null);
        }
        throw new LoaderException("Unexpected node " + node.getValue(), node);
    }

    /**
     * Gets the RDF list node equivalent to the specified AST node representing a collection of RDF nodes
     *
     * @param node   An AST node
     * @param graph  The current graph to use
     * @param buffer The buffer of quads
     * @return A RDF list node
     */
    private Node getNodeCollection(ASTNode node, GraphNode graph, List<Quad> buffer) throws LoaderException {
        // collection -> '('! graph_node+ ')'!
        List<Node> elements = new ArrayList<>();
        for (ASTNode child : node.getChildren())
            elements.add(getNode(child, graph, buffer));
        if (elements.isEmpty())
            return getNodeNil();

        BlankNode[] proxies = new BlankNode[elements.size()];
        for (int i = 0; i != proxies.length; i++) {
            proxies[i] = store.getBlankNode();
            buffer.add(new Quad(graph, proxies[i], store.getIRINode(Vocabulary.rdfFirst), elements.get(i)));
        }
        for (int i = 0; i != proxies.length - 1; i++) {
            buffer.add(new Quad(graph, proxies[i], store.getIRINode(Vocabulary.rdfRest), proxies[i + 1]));
        }
        buffer.add(new Quad(graph, proxies[proxies.length - 1], store.getIRINode(Vocabulary.rdfRest), getNodeNil()));
        return proxies[0];
    }

    /**
     * Gets the RDF blank node (with its properties) equivalent to the specified AST node
     *
     * @param node   An AST node
     * @param graph  The current graph to use
     * @param buffer The buffer of quads
     * @return The equivalent RDF blank node
     */
    private BlankNode getNodeBlankWithProperties(ASTNode node, GraphNode graph, List<Quad> buffer) throws LoaderException {
        // blank_node_property_list -> '['! property_list_not_empty^ ']'!
        // property_list_not_empty	-> verb object_list (';'! (verb object_list)? )*
        BlankNode subject = store.getBlankNode();
        List<ASTNode> members = node.getChildren();
        for (int i = 0; i != members.size(); i++) {
            Node verb = getNode(members.get(i), graph, buffer);
            // object_list -> object (','! object)*
            // object -> graph_node^
            // graph_node -> var_or_term^ | triples_node^
            for (ASTNode objectNode : members.get(i + 1).getChildren()) {
                Node object = getNode(objectNode, graph, buffer);
                buffer.add(new Quad(graph, subject, (Property) verb, object));
            }
            i++;
        }
        return subject;
    }
}
