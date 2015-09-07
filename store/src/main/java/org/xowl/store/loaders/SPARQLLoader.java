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
import org.xowl.store.rdf.BlankNode;
import org.xowl.store.rdf.IRINode;
import org.xowl.store.rdf.LiteralNode;
import org.xowl.store.rdf.Quad;
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
     * The loaded triples
     */
    private List<Quad> quads;
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
    }

    /**
     * Loads a query from the specified input
     *
     * @param logger The logger to use
     * @param reader The resource's reader
     * @return The loaded data
     */
    public List<Command> load(Logger logger, Reader reader) {
        List<Command> result = new ArrayList<>();
        quads = new ArrayList<>();
        baseURI = null;
        namespaces = new HashMap<>();
        blanks = new HashMap<>();

        ParseResult parseResult = parse(logger, reader);
        if (parseResult == null || !parseResult.isSuccess() || parseResult.getErrors().size() > 0)
            return Collections.emptyList();

        try {
            // prologue is always the first node
            loadPrologue(parseResult.getRoot().getChildren().get(0));
            ASTNode current = parseResult.getRoot().getChildren().get(1);
            switch (current.getSymbol().getID()) {
                case SPARQLParser.ID.create:
                    result.add(loadCommandCreate(current));
                    break;
                case SPARQLParser.ID.drop:
                    result.add(loadCommandDrop(current));
                    break;
                case SPARQLParser.ID.clear:
                    result.add(loadCommandClear(current));
                    break;
                case SPARQLParser.ID.copy:
                    result.add(loadCommandCopy(current));
                    break;
                case SPARQLParser.ID.move:
                    result.add(loadCommandMove(current));
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
        return result;
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
                iriNode = getNodeIRIRef(node);
                break;
            case SPARQLLexer.ID.PNAME_LN:
                iriNode = getNodePNameLN(node);
                break;
            case SPARQLLexer.ID.PNAME_NS:
                iriNode = getNodePNameNS(node);
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
     * Loads a graph reference from the specified AST node
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
}
