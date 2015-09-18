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
import org.xowl.store.Datatypes;
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
        switch (node.getChildren().get(1).getSymbol().getID()) {
            case SPARQLParser.ID.select:
                return loadCommandSelect(node);
            case SPARQLParser.ID.construct1:
                return loadCommandConstruct1(node);
            case SPARQLParser.ID.construct2:
                return loadCommandConstruct2(node);
            case SPARQLParser.ID.describe:
                return loadCommandDescribe(node);
            case SPARQLParser.ID.ask:
                return loadCommandAsk(node);
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
                    case SPARQLParser.ID.modify:
                        result.add(loadCommandModify(node.getChildren().get(1)));
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
        switch (ref.x) {
            case Single:
                return new CommandDrop(GraphReferenceType.Single, Collections.singletonList(ref.y), isSilent);
            case Default:
                if (!defaultIRIs.isEmpty())
                    return new CommandDrop(GraphReferenceType.Single, new ArrayList<>(defaultIRIs), isSilent);
                return new CommandDrop(GraphReferenceType.Default, new ArrayList<String>(0), isSilent);
            case Named:
                if (!namedIRIs.isEmpty())
                    return new CommandDrop(GraphReferenceType.Single, new ArrayList<>(namedIRIs), isSilent);
                return new CommandDrop(GraphReferenceType.Named, new ArrayList<String>(0), isSilent);
            case All:
                if (!namedIRIs.isEmpty() || !defaultIRIs.isEmpty()) {
                    Collection<String> targets = new ArrayList<>(defaultIRIs);
                    targets.addAll(namedIRIs);
                    return new CommandDrop(GraphReferenceType.Single, targets, isSilent);
                }
                return new CommandDrop(GraphReferenceType.All, new ArrayList<String>(0), isSilent);
        }
        throw new LoaderException("Unrecognized DROP command", node);
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
        switch (ref.x) {
            case Single:
                return new CommandClear(GraphReferenceType.Single, Collections.singletonList(ref.y), isSilent);
            case Default:
                if (!defaultIRIs.isEmpty())
                    return new CommandClear(GraphReferenceType.Single, new ArrayList<>(defaultIRIs), isSilent);
                return new CommandClear(GraphReferenceType.Default, new ArrayList<String>(0), isSilent);
            case Named:
                if (!namedIRIs.isEmpty())
                    return new CommandClear(GraphReferenceType.Single, new ArrayList<>(namedIRIs), isSilent);
                return new CommandClear(GraphReferenceType.Named, new ArrayList<String>(0), isSilent);
            case All:
                if (!namedIRIs.isEmpty() || !defaultIRIs.isEmpty()) {
                    Collection<String> targets = new ArrayList<>(defaultIRIs);
                    targets.addAll(namedIRIs);
                    return new CommandClear(GraphReferenceType.Single, targets, isSilent);
                }
                return new CommandClear(GraphReferenceType.All, new ArrayList<String>(0), isSilent);
        }
        throw new LoaderException("Unrecognized CLEAR command", node);
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
        Collection<String> origins = new ArrayList<>();
        Collection<String> targets = new ArrayList<>();
        switch (refOrigin.x) {
            case Single:
                origins.add(refOrigin.y);
                break;
            case Default:
                if (!defaultIRIs.isEmpty())
                    origins.addAll(defaultIRIs);
                else
                    origins.add(NodeManager.DEFAULT_GRAPH);
                break;
        }
        switch (refTarget.x) {
            case Single:
                targets.add(refTarget.y);
                break;
            case Default:
                if (!defaultIRIs.isEmpty())
                    targets.addAll(defaultIRIs);
                else
                    targets.add(NodeManager.DEFAULT_GRAPH);
                break;
        }
        return new CommandCopy(origins, targets, isSilent);
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
        String origin = null;
        String target = null;
        switch (refOrigin.x) {
            case Single:
                origin = refOrigin.y;
                break;
            case Default:
                if (!defaultIRIs.isEmpty()) {
                    if (defaultIRIs.size() == 1)
                        origin = defaultIRIs.iterator().next();
                    else
                        throw new LoaderException("MOVE command is specified more than one DEFAULT graph by the protocol context", node);
                } else
                    origin = NodeManager.DEFAULT_GRAPH;
                break;
        }
        switch (refTarget.x) {
            case Single:
                target = refTarget.y;
                break;
            case Default:
                if (!defaultIRIs.isEmpty()) {
                    if (defaultIRIs.size() == 1)
                        target = defaultIRIs.iterator().next();
                    else
                        throw new LoaderException("MOVE command is specified more than one DEFAULT graph by the protocol context", node);
                } else
                    target = NodeManager.DEFAULT_GRAPH;
                break;
        }
        return new CommandMove(origin, target, isSilent);
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
        Collection<String> origins = new ArrayList<>();
        Collection<String> targets = new ArrayList<>();
        switch (refOrigin.x) {
            case Single:
                origins.add(refOrigin.y);
                break;
            case Default:
                if (!defaultIRIs.isEmpty())
                    origins.addAll(defaultIRIs);
                else
                    origins.add(NodeManager.DEFAULT_GRAPH);
                break;
        }
        switch (refTarget.x) {
            case Single:
                targets.add(refTarget.y);
                break;
            case Default:
                if (!defaultIRIs.isEmpty())
                    targets.addAll(defaultIRIs);
                else
                    targets.add(NodeManager.DEFAULT_GRAPH);
                break;
        }
        return new CommandAdd(origins, targets, isSilent);
    }

    /**
     * Loads a SELECT command from the specified AST node
     *
     * @param node An AST node
     * @return The SELECT command
     */
    private Command loadCommandSelect(ASTNode node) throws LoaderException {
        // query -> prologue (select | construct | describe | ask) clause_values
        // select -> clause_select clause_dataset* clause_where modifier ;
        // clause_select -> SELECT! clause_select_mod clause_select_vars ;
        ASTNode nodeSelect = node.getChildren().get(1);
        ASTNode clauseSelect = nodeSelect.getChildren().get(0);
        ASTNode clauseSelectMod = clauseSelect.getChildren().get(0);

        SPARQLContext context = new SPARQLContext(store, true);
        loadPrologue(node.getChildren().get(0));
        boolean isDistinct = (!clauseSelectMod.getChildren().isEmpty() && clauseSelectMod.getChildren().get(0).getSymbol().getID() == SPARQLLexer.ID.DISTINCT);
        boolean isReduced = (!clauseSelectMod.getChildren().isEmpty() && clauseSelectMod.getChildren().get(0).getSymbol().getID() == SPARQLLexer.ID.REDUCED);
        for (ASTNode child : nodeSelect.getChildren()) {
            if (child.getSymbol().getID() == SPARQLParser.ID.clause_dataset) {
                ASTNode inner = child.getChildren().get(0);
                if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_default) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    context.addDefaultGraph(iri);
                } else if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_named) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    context.addNamedIRI(iri);
                }
            }
        }
        GraphPattern where = loadGraphPattern(context, null, nodeSelect.getChildren().get(nodeSelect.getChildren().size() - 2).getChildren().get(0));
        GraphPatternModifier modifier = loadGraphPatternModifier(context, nodeSelect.getChildren().get(nodeSelect.getChildren().size() - 1));
        GraphPatternInlineData values = null;
        if (!node.getChildren().get(2).getChildren().isEmpty())
            values = new GraphPatternInlineData(loadDataBlock(context, node.getChildren().get(2)));
        GraphPatternSelect select = new GraphPatternSelect(isDistinct, isReduced, where, modifier, values);
        for (ASTNode child : clauseSelect.getChildren().get(1).getChildren()) {
            if (child.getSymbol().getID() == SPARQLLexer.ID.AS) {
                select.addToProjection((VariableNode) getVariable(context, child.getChildren().get(1)), loadExpression(context, null, child.getChildren().get(0)));
            } else {
                select.addToProjection((VariableNode) getVariable(context, child));
            }
        }
        return new CommandSelect(select);
    }

    /**
     * Loads the first form of a CONSTRUCT command from the specified AST node
     *
     * @param node An AST node
     * @return The ASK command
     */
    private Command loadCommandConstruct1(ASTNode node) throws LoaderException {
        // query -> prologue (select | construct | describe | ask) clause_values
        // construct -> construct1^ | construct2^
        // construct1 -> CONSTRUCT! construct_template clause_dataset* clause_where modifier
        ASTNode nodeConstruct = node.getChildren().get(1);

        SPARQLContext contextTemplate = new SPARQLContext(store, false);
        SPARQLContext contextWhere = new SPARQLContext(store, true);
        loadPrologue(node.getChildren().get(0));
        for (ASTNode child : nodeConstruct.getChildren()) {
            if (child.getSymbol().getID() == SPARQLParser.ID.clause_dataset) {
                ASTNode inner = child.getChildren().get(0);
                if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_default) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    contextTemplate.addDefaultGraph(iri);
                    contextWhere.addDefaultGraph(iri);
                } else if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_named) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    contextTemplate.addNamedIRI(iri);
                    contextWhere.addNamedIRI(iri);
                }
            }
        }
        GraphPattern where = loadGraphPattern(contextWhere, null, nodeConstruct.getChildren().get(nodeConstruct.getChildren().size() - 2).getChildren().get(0));
        GraphPatternModifier modifier = loadGraphPatternModifier(contextWhere, nodeConstruct.getChildren().get(nodeConstruct.getChildren().size() - 1));
        GraphPatternInlineData values = null;
        if (!node.getChildren().get(2).getChildren().isEmpty())
            values = new GraphPatternInlineData(loadDataBlock(contextWhere, node.getChildren().get(2)));
        GraphPatternSelect select = new GraphPatternSelect(false, false, where, modifier, values);

        GraphPattern template = loadGraphPatternTriples(contextTemplate, nodeConstruct.getChildren().get(0).getChildren().get(0), null);
        if (!(template instanceof GraphPatternQuads))
            throw new LoaderException("Target graph for the pattern is ambiguous", node);
        return new CommandConstruct(select, ((GraphPatternQuads) template).getQuery().getPositives());
    }

    /**
     * Loads the second form of a CONSTRUCT command from the specified AST node
     *
     * @param node An AST node
     * @return The ASK command
     */
    private Command loadCommandConstruct2(ASTNode node) throws LoaderException {
        // query -> prologue (select | construct | describe | ask) clause_values
        // construct -> construct1^ | construct2^
        // construct2 -> CONSTRUCT! clause_dataset* WHERE! '{'! triples_template? '}'! modifier
        ASTNode nodeConstruct = node.getChildren().get(1);

        SPARQLContext context = new SPARQLContext(store, true);
        GraphPattern template = null;
        loadPrologue(node.getChildren().get(0));
        for (ASTNode child : nodeConstruct.getChildren()) {
            if (child.getSymbol().getID() == SPARQLParser.ID.clause_dataset) {
                ASTNode inner = child.getChildren().get(0);
                if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_default) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    context.addDefaultGraph(iri);
                } else if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_named) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    context.addNamedIRI(iri);
                } else if (inner.getSymbol().getID() == SPARQLParser.ID.triples_template) {
                    template = loadGraphPatternTriples(context, inner, null);
                }
            }
        }
        if (template == null)
            template = new GraphPatternQuads();
        else if (!(template instanceof GraphPatternQuads))
            throw new LoaderException("Target graph for the pattern is ambiguous", node);
        GraphPatternModifier modifier = loadGraphPatternModifier(context, nodeConstruct.getChildren().get(nodeConstruct.getChildren().size() - 1));
        GraphPatternInlineData values = null;
        if (!node.getChildren().get(2).getChildren().isEmpty())
            values = new GraphPatternInlineData(loadDataBlock(context, node.getChildren().get(2)));
        GraphPatternSelect select = new GraphPatternSelect(false, false, template, modifier, values);
        return new CommandConstruct(select, ((GraphPatternQuads) template).getQuery().getPositives());
    }

    /**
     * Loads a DESCRIBE command from the specified AST node
     *
     * @param node An AST node
     * @return The ASK command
     */
    private Command loadCommandDescribe(ASTNode node) throws LoaderException {
        // query -> prologue (select | construct | describe | ask) clause_values
        // describe -> DESCRIBE! describe_vars clause_dataset* clause_where? modifier
        // describe_vars -> OP_MULT! | var_or_iri*

        ASTNode nodeDescribe = node.getChildren().get(1);

        SPARQLContext context = new SPARQLContext(store, true);
        loadPrologue(node.getChildren().get(0));
        for (ASTNode child : nodeDescribe.getChildren()) {
            if (child.getSymbol().getID() == SPARQLParser.ID.clause_dataset) {
                ASTNode inner = child.getChildren().get(0);
                if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_default) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    context.addDefaultGraph(iri);
                } else if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_named) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    context.addNamedIRI(iri);
                }
            }
        }

        GraphPattern where;
        ASTNode nodeWhere = nodeDescribe.getChildren().get(nodeDescribe.getChildren().size() - 2);
        if (nodeWhere.getSymbol().getID() == SPARQLParser.ID.clause_where)
            where = loadGraphPattern(context, null, nodeWhere.getChildren().get(0));
        else
            where = new GraphPatternQuads();
        GraphPatternModifier modifier = loadGraphPatternModifier(context, nodeDescribe.getChildren().get(nodeDescribe.getChildren().size() - 1));
        GraphPatternInlineData values = null;
        if (!node.getChildren().get(2).getChildren().isEmpty())
            values = new GraphPatternInlineData(loadDataBlock(context, node.getChildren().get(2)));
        GraphPatternSelect select = new GraphPatternSelect(false, false, where, modifier, values);

        CommandDescribe command = new CommandDescribe(select);
        for (ASTNode child : nodeDescribe.getChildren().get(0).getChildren()) {
            Node target = getNode(context, child, null, null);
            if (target != null) {
                if (target.getNodeType() == Node.TYPE_VARIABLE)
                    command.addTargetVariable((VariableNode) target);
                else
                    command.addTargetIRI(((IRINode) target).getIRIValue());
            }
        }
        return command;
    }

    /**
     * Loads an ASK command from the specified AST node
     *
     * @param node An AST node
     * @return The ASK command
     */
    private Command loadCommandAsk(ASTNode node) throws LoaderException {
        // query -> prologue (select | construct | describe | ask) clause_values
        // ask -> ASK! clause_dataset* clause_where modifier ;
        ASTNode nodeAsk = node.getChildren().get(1);

        SPARQLContext context = new SPARQLContext(store, true);
        loadPrologue(node.getChildren().get(0));
        for (ASTNode child : nodeAsk.getChildren()) {
            if (child.getSymbol().getID() == SPARQLParser.ID.clause_dataset) {
                ASTNode inner = child.getChildren().get(0);
                if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_default) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    context.addDefaultGraph(iri);
                } else if (inner.getSymbol().getID() == SPARQLParser.ID.clause_graph_named) {
                    String iri = loadGraphRef(inner.getChildren().get(0)).y;
                    context.addNamedIRI(iri);
                }
            }
        }
        GraphPattern where = loadGraphPattern(context, null, nodeAsk.getChildren().get(nodeAsk.getChildren().size() - 2).getChildren().get(0));
        GraphPatternModifier modifier = loadGraphPatternModifier(context, nodeAsk.getChildren().get(nodeAsk.getChildren().size() - 1));
        GraphPatternInlineData values = null;
        if (!node.getChildren().get(2).getChildren().isEmpty())
            values = new GraphPatternInlineData(loadDataBlock(context, node.getChildren().get(2)));
        GraphPatternSelect select = new GraphPatternSelect(false, false, where, modifier, values);
        return new CommandAsk(select);
    }

    /**
     * Loads an INSERT DATA command from the specified AST node
     *
     * @param node An AST node
     * @return The INSERT DATA command
     */
    private Command loadCommandInsertData(ASTNode node) throws LoaderException {
        SPARQLContext context = new SPARQLContext(store);
        for (String iri : defaultIRIs)
            context.addDefaultGraph(iri);
        for (String iri : namedIRIs)
            context.addNamedIRI(iri);
        List<Quad> quads = loadQuads(context, node.getChildren().get(0), null);
        return new CommandInsertData(Collections.unmodifiableCollection(quads));
    }

    /**
     * Loads an DELETE DATA command from the specified AST node
     *
     * @param node An AST node
     * @return The DELETE DATA command
     */
    private Command loadCommandDeleteData(ASTNode node) throws LoaderException {
        SPARQLContext context = new SPARQLContext(store);
        for (String iri : defaultIRIs)
            context.addDefaultGraph(iri);
        for (String iri : namedIRIs)
            context.addNamedIRI(iri);
        List<Quad> quads = loadQuads(context, node.getChildren().get(0), null);
        return new CommandDeleteData(Collections.unmodifiableCollection(quads));
    }

    /**
     * Loads an DELETE WHERE command from the specified AST node
     *
     * @param node An AST node
     * @return The DELETE WHERE command
     */
    private Command loadCommandDeleteWhere(ASTNode node) throws LoaderException {
        SPARQLContext context = new SPARQLContext(store, true);
        for (String iri : defaultIRIs)
            context.addDefaultGraph(iri);
        for (String iri : namedIRIs)
            context.addNamedIRI(iri);
        List<Quad> quads = loadQuads(context, node.getChildren().get(0), null);
        return new CommandDeleteWhere(Collections.unmodifiableCollection(quads));
    }

    /**
     * Loads a INSERT/DELETE command from the specified AST node
     *
     * @param node An AST node
     * @return The INSERT/DELETE command
     */
    private Command loadCommandModify(ASTNode node) throws LoaderException {
        SPARQLContext context = new SPARQLContext(store, true);
        GraphNode templateTarget = null;
        Collection<Quad> toInsert;
        Collection<Quad> toDelete;
        GraphPattern where;
        int index = 0;
        ASTNode current = node.getChildren().get(index);
        if (current.getSymbol().getID() != SPARQLParser.ID.clause_delete && current.getSymbol().getID() != SPARQLParser.ID.clause_insert) {
            // this is the WITH clause
            templateTarget = store.getIRINode(loadGraphRef(current).y);
            index++;
            current = node.getChildren().get(index);
        }
        if (current.getSymbol().getID() == SPARQLParser.ID.clause_delete) {
            toDelete = loadQuads(context, current.getChildren().get(0), templateTarget);
            index++;
            current = node.getChildren().get(index);
        } else {
            toDelete = new ArrayList<>();
        }
        if (current.getSymbol().getID() == SPARQLParser.ID.clause_insert) {
            toInsert = loadQuads(context, current.getChildren().get(0), templateTarget);
            index++;
            current = node.getChildren().get(index);
        } else {
            toInsert = new ArrayList<>();
        }
        for (String iri : defaultIRIs)
            context.addDefaultGraph(iri);
        for (String iri : namedIRIs)
            context.addNamedIRI(iri);
        if (templateTarget != null)
            context.addNamedIRI(((IRINode) templateTarget).getIRIValue());
        boolean ignore = context.isDatasetDefined();
        while (current.getSymbol().getID() == SPARQLParser.ID.clause_using) {
            if (!ignore) {
                if (current.getChildren().size() >= 2) {
                    context.addNamedIRI(loadGraphRef(current.getChildren().get(1)).y);
                } else {
                    context.addDefaultGraph(loadGraphRef(current.getChildren().get(0)).y);
                }
            }
            index++;
            current = node.getChildren().get(index);
        }
        where = loadGraphPattern(context, null, current);
        return new CommandModify(Collections.unmodifiableCollection(toInsert), Collections.unmodifiableCollection(toDelete), where);
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
     * Loads a graph pattern from the specified AST node
     *
     * @param context The current context
     * @param graph   The current graph, if any
     * @param node    An AST node
     * @return The graph pattern
     */
    private GraphPattern loadGraphPattern(SPARQLContext context, GraphNode graph, ASTNode node) throws LoaderException {
        // graph_pattern -> '{'! (sub_select^ | graph_pattern_group^) '}'!
        switch (node.getSymbol().getID()) {
            case SPARQLParser.ID.sub_select:
                return loadGraphPatternSubSelect(context, node);
            case SPARQLParser.ID.graph_pattern_group:
                return loadGraphPatternGroup(context, graph, node);
        }
        throw new LoaderException("Unrecognized graph pattern", node);
    }

    /**
     * Loads a graph pattern from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @return The graph pattern
     */
    private GraphPattern loadGraphPatternSubSelect(SPARQLContext context, ASTNode node) throws LoaderException {
        ASTNode clauseSelect = node.getChildren().get(0);
        ASTNode clauseSelectMod = clauseSelect.getChildren().get(0);
        boolean isDistinct = (!clauseSelectMod.getChildren().isEmpty() && clauseSelectMod.getChildren().get(0).getSymbol().getID() == SPARQLLexer.ID.DISTINCT);
        boolean isReduced = (!clauseSelectMod.getChildren().isEmpty() && clauseSelectMod.getChildren().get(0).getSymbol().getID() == SPARQLLexer.ID.REDUCED);

        GraphPattern where = loadGraphPattern(context, null, node.getChildren().get(1).getChildren().get(0));
        GraphPatternInlineData values = null;
        if (!node.getChildren().get(3).getChildren().isEmpty())
            values = new GraphPatternInlineData(loadDataBlock(context, node.getChildren().get(3).getChildren().get(0)));
        GraphPatternModifier modifier = loadGraphPatternModifier(context, node.getChildren().get(2));
        GraphPatternSelect select = new GraphPatternSelect(isDistinct, isReduced, where, modifier, values);

        for (ASTNode child : clauseSelect.getChildren().get(1).getChildren()) {
            if (child.getSymbol().getID() == SPARQLLexer.ID.AS) {
                select.addToProjection((VariableNode) getVariable(context, child.getChildren().get(1)), loadExpression(context, null, child.getChildren().get(0)));
            } else {
                select.addToProjection((VariableNode) getVariable(context, child));
            }
        }
        return select;
    }

    /**
     * Loads a group of graph pattern from the specified AST node
     *
     * @param context The current context
     * @param graph   The current graph, if any
     * @param node    An AST node
     * @return The overall graph pattern
     */
    private GraphPattern loadGraphPatternGroup(SPARQLContext context, GraphNode graph, ASTNode node) throws LoaderException {
        // graph_pattern_group -> triples_block? (graph_pattern_other ('.'!)? triples_block?)* ;

        // buffers of patterns read so far
        GraphPatternQuads base = new GraphPatternQuads();
        GraphPattern current = null;

        // build the child context for this group
        SPARQLContext currentContext = new SPARQLContext(context);

        // load all the quads
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case SPARQLParser.ID.triples_block: {
                    GraphPattern inner = loadGraphPatternTriples(currentContext, child, graph);
                    if (inner instanceof GraphPatternQuads) {
                        Query query = ((GraphPatternQuads) inner).getQuery();
                        if (query.getNegatives().isEmpty()) {
                            base.addPositives(query.getPositives());
                        } else {
                            current = new GraphPatternMinus(current == null ? base : current, inner);
                        }
                    } else {
                        current = new GraphPatternMinus(current == null ? base : current, inner);
                    }
                    break;
                }
                case SPARQLParser.ID.graph_pattern_optional: {
                    GraphPattern inner = loadGraphPattern(currentContext, graph, child.getChildren().get(0));
                    if (inner instanceof GraphPatternFilter) {
                        GraphPatternFilter filter = (GraphPatternFilter) inner;
                        current = new GraphPatternLeftJoin(current == null ? base : current, filter.getInner(), filter.getExpression());
                    } else {
                        current = new GraphPatternLeftJoin(current == null ? base : current, inner, new ExpressionConstant(true));
                    }
                    break;
                }
                case SPARQLParser.ID.graph_pattern_minus: {
                    GraphPattern inner = loadGraphPattern(currentContext, graph, child.getChildren().get(0));
                    if (inner instanceof GraphPatternQuads) {
                        Query query = ((GraphPatternQuads) inner).getQuery();
                        if (query.getNegatives().isEmpty()) {
                            base.addNegatives(query.getPositives());
                        } else {
                            current = new GraphPatternMinus(current == null ? base : current, inner);
                        }
                    } else {
                        current = new GraphPatternMinus(current == null ? base : current, inner);
                    }
                    break;
                }
                case SPARQLParser.ID.graph_pattern_graph: {
                    GraphPattern inner;
                    if (currentContext.isDatasetDefined())
                        inner = loadGraphPattern(currentContext, null, child.getChildren().get(1));
                    else {
                        GraphNode sub = (GraphNode) getNode(currentContext, child.getChildren().get(0), null, null);
                        inner = loadGraphPattern(currentContext, sub, child.getChildren().get(1));
                    }
                    if (inner instanceof GraphPatternQuads) {
                        Query query = ((GraphPatternQuads) inner).getQuery();
                        base.addPositives(query.getPositives());
                        for (Collection<Quad> conjunction : query.getNegatives())
                            base.addNegatives(conjunction);
                    } else {
                        current = new GraphPatternJoin(current == null ? base : current, inner);
                    }
                    break;
                }
                case SPARQLParser.ID.graph_pattern_service: {
                    GraphPattern inner = loadGraphPattern(currentContext, graph, child.getChildren().get(child.getChildren().size() - 1));
                    Node enpoint = getNode(currentContext, child.getChildren().get(child.getChildren().size() - 2), graph, null);
                    current = new GraphPatternService(current == null ? base : current, inner, enpoint, child.getChildren().size() >= 3);
                    break;
                }
                case SPARQLParser.ID.graph_pattern_filter: {
                    Expression expression = loadExpression(currentContext, graph, child.getChildren().get(0));
                    current = new GraphPatternFilter(current == null ? base : current, expression);
                    break;
                }
                case SPARQLParser.ID.graph_pattern_bind: {
                    Expression expression = loadExpression(currentContext, graph, child.getChildren().get(0));
                    Node variable = getVariable(currentContext, child.getChildren().get(1));
                    current = new GraphPatternBind(current == null ? base : current, (VariableNode) variable, expression);
                    break;
                }
                case SPARQLParser.ID.graph_pattern_data: {
                    Collection<QuerySolution> data = loadDataBlock(currentContext, child.getChildren().get(0));
                    current = new GraphPatternUnion(Arrays.asList(current == null ? base : current, new GraphPatternInlineData(data)));
                    break;
                }
                case SPARQLLexer.ID.UNION: {
                    GraphPattern inner = loadGraphPatternUnion(currentContext, graph, child);
                    if (current == null) {
                        if (base.getQuery().getPositives().isEmpty() && base.getQuery().getNegatives().isEmpty())
                            current = inner;
                        else
                            current = new GraphPatternJoin(base, inner);
                    } else
                        current = new GraphPatternJoin(current, inner);
                    break;
                }
                case SPARQLParser.ID.sub_select: {
                    GraphPattern inner = loadGraphPatternSubSelect(currentContext, child);
                    if (current == null) {
                        if (base.getQuery().getPositives().isEmpty() && base.getQuery().getNegatives().isEmpty())
                            current = inner;
                        else
                            current = new GraphPatternJoin(base, inner);
                    } else
                        current = new GraphPatternJoin(current, inner);
                    break;
                }
                case SPARQLParser.ID.graph_pattern_group: {
                    GraphPattern inner = loadGraphPatternGroup(currentContext, graph, child);
                    if (inner instanceof GraphPatternQuads) {
                        Query query = ((GraphPatternQuads) inner).getQuery();
                        base.addPositives(query.getPositives());
                        for (Collection<Quad> conjunction : query.getNegatives())
                            base.addNegatives(conjunction);
                    } else {
                        if (current == null) {
                            if (base.getQuery().getPositives().isEmpty() && base.getQuery().getNegatives().isEmpty())
                                current = inner;
                            else
                                current = new GraphPatternJoin(base, inner);
                        } else
                            current = new GraphPatternJoin(current, inner);
                    }
                    break;
                }
            }
        }
        return current == null ? base : current;
    }

    /**
     * Loads a union of graph patterns from the specified AST node
     *
     * @param context The current context
     * @param graph   The current graph, if any
     * @param node    An AST node
     * @return The overall graph pattern
     */
    private GraphPattern loadGraphPatternUnion(SPARQLContext context, GraphNode graph, ASTNode node) throws LoaderException {
        List<GraphPattern> inners = new ArrayList<>();
        ASTNode current = node;
        while (current.getSymbol().getID() == SPARQLLexer.ID.UNION) {
            GraphPattern right = loadGraphPattern(context, graph, current.getChildren().get(1));
            inners.add(right);
            current = current.getChildren().get(0);
        }
        inners.add(loadGraphPattern(context, graph, current));
        Collections.reverse(inners);
        return new GraphPatternUnion(inners);
    }

    /**
     * Loads a modifier of graph patterns from an AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @return The modifier
     */
    private GraphPatternModifier loadGraphPatternModifier(SPARQLContext context, ASTNode node) throws LoaderException {
        if (node.getChildren().isEmpty())
            return null;
        GraphPatternModifier modifier = new GraphPatternModifier();
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case SPARQLParser.ID.clause_group:
                    for (ASTNode condNode : child.getChildren()) {
                        if (condNode.getSymbol().getID() == SPARQLLexer.ID.AS)
                            modifier.addGroup(loadExpression(context, null, condNode.getChildren().get(0)),
                                    (VariableNode) getVariable(context, condNode.getChildren().get(1)));
                        else
                            modifier.addGroup(loadExpression(context, null, condNode));
                    }
                    break;
                case SPARQLParser.ID.clause_having:
                    for (ASTNode havingNode : child.getChildren()) {
                        modifier.addConstraint(loadExpression(context, null, havingNode));
                    }
                    break;
                case SPARQLParser.ID.clause_order:
                    for (ASTNode orderNode : child.getChildren()) {
                        if (orderNode.getSymbol().getID() == SPARQLLexer.ID.ASC)
                            modifier.addOrdering(loadExpression(context, null, orderNode));
                        else if (orderNode.getSymbol().getID() == SPARQLLexer.ID.DESC)
                            modifier.addOrdering(loadExpression(context, null, orderNode), true);
                        else
                            modifier.addOrdering(loadExpression(context, null, orderNode));
                    }
                    break;
                case SPARQLParser.ID.clauses_limit_offset:
                    for (ASTNode limitNode : child.getChildren()) {
                        if (limitNode.getSymbol().getID() == SPARQLParser.ID.clause_limit)
                            modifier.setLimit(Integer.parseInt(limitNode.getChildren().get(0).getValue()));
                        else if (limitNode.getSymbol().getID() == SPARQLParser.ID.clause_offset)
                            modifier.setOffset(Integer.parseInt(limitNode.getChildren().get(0).getValue()));
                    }
                    break;
            }
        }
        return modifier;
    }

    /**
     * Loads an inline data block from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @return The inline data
     */
    private Collection<QuerySolution> loadDataBlock(SPARQLContext context, ASTNode node) throws LoaderException {
        Collection<QuerySolution> result = new ArrayList<>();
        switch (node.getSymbol().getID()) {
            case SPARQLParser.ID.inline_data_one: {
                VariableNode variable = (VariableNode) getVariable(context, node.getChildren().get(0));
                for (int i = 1; i != node.getChildren().size(); i++) {
                    Node value = getNode(context, node.getChildren().get(i), null, null);
                    result.add(new QuerySolution(Collections.singletonList(new Couple<>(variable, value))));
                }
                break;
            }
            case SPARQLParser.ID.inline_data_full: {
                List<VariableNode> variables = new ArrayList<>();
                for (ASTNode child : node.getChildren().get(0).getChildren())
                    variables.add((VariableNode) getVariable(context, child));
                for (int i = 1; i != node.getChildren().size(); i++) {
                    List<Node> values = new ArrayList<>();
                    for (ASTNode child : node.getChildren().get(i).getChildren())
                        values.add(getNode(context, child, null, null));
                    List<Couple<VariableNode, Node>> bindings = new ArrayList<>();
                    for (int v = 0; v != variables.size(); v++)
                        bindings.add(new Couple<>(variables.get(i), values.get(i)));
                    result.add(new QuerySolution(bindings));
                }
                break;
            }
        }
        return result;
    }

    /**
     * Loads a SPARQL expression from the specified AST node
     *
     * @param context The current context
     * @param graph   The current graph, if any
     * @param node    An AST node
     * @return The expression
     */
    private Expression loadExpression(SPARQLContext context, GraphNode graph, ASTNode node) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case SPARQLLexer.ID.TRUE: // true
                return new ExpressionConstant(true);
            case SPARQLLexer.ID.FALSE: // false
                return new ExpressionConstant(false);
            case SPARQLLexer.ID.INTEGER:
                return new ExpressionConstant(Integer.valueOf(node.getValue()));
            case SPARQLLexer.ID.DECIMAL:
                return new ExpressionConstant(Double.valueOf(node.getValue()));
            case SPARQLLexer.ID.DOUBLE:
                return new ExpressionConstant(Double.valueOf(node.getValue()));
            case SPARQLLexer.ID.VARIABLE:
                return new ExpressionRDF(getVariable(context, node));
            case SPARQLParser.ID.literal_rdf:
                return new ExpressionConstant(Datatypes.toNative(getNodeLiteral(node)));
            case SPARQLLexer.ID.OP_BOR: // ||
                return new ExpressionOperator(ExpressionOperator.Op.BoolOr, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_BAND: // &&
                return new ExpressionOperator(ExpressionOperator.Op.BoolAnd, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_EQ: // =
                return new ExpressionOperator(ExpressionOperator.Op.Equal, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_NEQ: // !=
                return new ExpressionOperator(ExpressionOperator.Op.NotEqual, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_LESS: // <
                return new ExpressionOperator(ExpressionOperator.Op.Less, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_GREAT: // >
                return new ExpressionOperator(ExpressionOperator.Op.Greater, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_GEQ: // >=
                return new ExpressionOperator(ExpressionOperator.Op.GreaterOrEqual, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_LEQ: // <=
                return new ExpressionOperator(ExpressionOperator.Op.LessOrEqual, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_PLUS: // +
                if (node.getChildren().size() == 1)
                    return new ExpressionOperator(ExpressionOperator.Op.UnaryPlus, loadExpression(context, graph, node.getChildren().get(0)));
                else
                    return new ExpressionOperator(ExpressionOperator.Op.Plus, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_MINUS: // -
                if (node.getChildren().size() == 1)
                    return new ExpressionOperator(ExpressionOperator.Op.UnaryMinus, loadExpression(context, graph, node.getChildren().get(0)));
                else
                    return new ExpressionOperator(ExpressionOperator.Op.Minus, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_MULT: // *
                return new ExpressionOperator(ExpressionOperator.Op.Multiply, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_DIV: // /
                return new ExpressionOperator(ExpressionOperator.Op.Divide, loadExpression(context, graph, node.getChildren().get(0)), loadExpression(context, graph, node.getChildren().get(1)));
            case SPARQLLexer.ID.OP_NOT: // !
                return new ExpressionOperator(ExpressionOperator.Op.BoolNot, loadExpression(context, graph, node.getChildren().get(0)));
            case SPARQLLexer.ID.IN: { // IN
                Expression primary = loadExpression(context, graph, node.getChildren().get(0));
                List<Expression> range = new ArrayList<>();
                for (ASTNode child : node.getChildren().get(1).getChildren())
                    range.add(loadExpression(context, graph, child));
                return new ExpressionIn(primary, range);
            }
            case SPARQLLexer.ID.NOT: { // NOT IN
                Expression primary = loadExpression(context, graph, node.getChildren().get(0));
                List<Expression> range = new ArrayList<>();
                for (ASTNode child : node.getChildren().get(1).getChildren())
                    range.add(loadExpression(context, graph, child));
                return new ExpressionOperator(ExpressionOperator.Op.BoolNot, new ExpressionIn(primary, range));
            }
            case SPARQLParser.ID.function_call:
                return loadExpressionFunctionCall(context, graph, node);
            case SPARQLParser.ID.iri_or_function:
                if (node.getChildren().size() == 1)
                    return new ExpressionRDF(getNode(context, node.getChildren().get(0), null, null));
                else
                    return loadExpressionFunctionCall(context, graph, node);
            case SPARQLParser.ID.built_in_call:
                return loadExpressionBuiltin(context, graph, node);
        }
        throw new LoaderException("Unrecognized expression", node);
    }

    /**
     * Loads a built-in SPARQL expression from the specified AST node
     *
     * @param context The current context
     * @param graph   The current graph, if any
     * @param node    An AST node
     * @return The expression
     */
    private Expression loadExpressionFunctionCall(SPARQLContext context, GraphNode graph, ASTNode node) throws LoaderException {
        IRINode nameNode = ((IRINode) getNode(context, node.getChildren().get(0), null, null));
        if (nameNode == null)
            throw new LoaderException("Failed to get the function's name", node);
        List<Expression> arguments = new ArrayList<>();
        boolean isDistinct = false;
        for (ASTNode child : node.getChildren().get(1).getChildren()) {
            if (child.getSymbol().getID() == SPARQLLexer.ID.DISTINCT)
                isDistinct = true;
            else
                arguments.add(loadExpression(context, graph, child));
        }
        return new ExpressionFunctionCall(nameNode.getIRIValue(), arguments, isDistinct, null);
    }

    /**
     * Loads a built-in SPARQL expression from the specified AST node
     *
     * @param context The current context
     * @param graph   The current graph, if any
     * @param node    An AST node
     * @return The expression
     */
    private Expression loadExpressionBuiltin(SPARQLContext context, GraphNode graph, ASTNode node) throws LoaderException {
        if (node.getChildren().get(0).getSymbol().getID() != SPARQLLexer.ID.BUILTIN)
            return loadExpressionBuiltinExists(context, graph, node);

        String name = node.getChildren().get(0).getValue();
        List<Expression> arguments = new ArrayList<>();
        boolean isDistinct = !node.getChildren().get(1).getChildren().isEmpty();
        String separator = null;
        for (ASTNode child : node.getChildren().get(2).getChildren()) {
            if (child.getSymbol().getID() == SPARQLLexer.ID.DISTINCT)
                isDistinct = true;
            else
                arguments.add(loadExpression(context, graph, child));
        }
        if (!node.getChildren().get(3).getChildren().isEmpty()) {
            ASTNode childString = node.getChildren().get(3).getChildren().get(0);
            switch (childString.getSymbol().getID()) {
                case SPARQLLexer.ID.STRING_LITERAL_SINGLE_QUOTE:
                case SPARQLLexer.ID.STRING_LITERAL_QUOTE:
                    separator = childString.getValue();
                    separator = separator.substring(1, separator.length() - 1);
                    separator = Utils.unescape(separator);
                    break;
                case SPARQLLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
                case SPARQLLexer.ID.STRING_LITERAL_LONG_QUOTE:
                    separator = childString.getValue();
                    separator = separator.substring(3, separator.length() - 3);
                    separator = Utils.unescape(separator);
                    break;
            }
        }
        return new ExpressionFunctionCall(name, arguments, isDistinct, separator);
    }

    /**
     * Loads a built-in SPARQL expression from the specified AST node
     *
     * @param context The current context
     * @param graph   The current graph, if any
     * @param node    An AST node
     * @return The expression
     */
    private Expression loadExpressionBuiltinExists(SPARQLContext context, GraphNode graph, ASTNode node) throws LoaderException {
        GraphPattern pattern = loadGraphPattern(context, graph, node.getChildren().get(node.getChildren().size() - 1));
        Expression exp = new ExpressionExists(pattern);
        if (node.getChildren().size() >= 3)
            exp = new ExpressionOperator(ExpressionOperator.Op.BoolNot, exp);
        return exp;
    }

    /**
     * Loads a graph pattern specified as a block of triples from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph, if any
     * @return The quads
     */
    private GraphPattern loadGraphPatternTriples(SPARQLContext context, ASTNode node, GraphNode graph) throws LoaderException {
        if (graph != null) {
            GraphPatternQuads result = new GraphPatternQuads();
            loadTriples(context, node, graph, result.getQuery().getPositives());
            return result;
        }
        List<String> targets = new ArrayList<>();
        targets.addAll(context.getDefaultGraphs());
        targets.addAll(context.getNamedGraphs());
        if (targets.isEmpty())
            targets.add(NodeManager.DEFAULT_GRAPH);

        if (targets.size() == 1) {
            GraphPatternQuads result = new GraphPatternQuads();
            loadTriples(context, node, store.getIRINode(targets.get(0)), result.getQuery().getPositives());
            return result;
        }

        Collection<GraphPattern> elements = new ArrayList<>();
        for (String target : targets) {
            GraphPatternQuads element = new GraphPatternQuads();
            loadTriples(context, node, store.getIRINode(target), element.getQuery().getPositives());
            elements.add(element);
        }
        return new GraphPatternUnion(elements);
    }

    /**
     * Loads quads from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @return The quads
     */
    private List<Quad> loadQuads(SPARQLContext context, ASTNode node, GraphNode graph) throws LoaderException {
        // quads -> triples_template? quads_supp*
        if (graph != null)
            return loadQuadsForTarget(context, node, graph);

        List<String> targets = new ArrayList<>();
        targets.addAll(context.getDefaultGraphs());
        targets.addAll(context.getNamedGraphs());
        if (targets.isEmpty())
            targets.add(NodeManager.DEFAULT_GRAPH);

        if (targets.size() == 1)
            return loadQuadsForTarget(context, node, store.getIRINode(targets.get(0)));

        throw new LoaderException("Multiple target graph for the quads", node);
    }

    /**
     * Loads quads from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @return The quads
     */
    private List<Quad> loadQuadsForTarget(SPARQLContext context, ASTNode node, GraphNode graph) throws LoaderException {
        // quads -> triples_template? quads_supp*
        List<Quad> result = new ArrayList<>();
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case SPARQLParser.ID.triples_template:
                    loadTriples(context, child, graph, result);
                    break;
                case SPARQLParser.ID.quads_supp:
                    loadQuadsSupplementary(context, child, graph, result);
                    break;
            }
        }
        return result;
    }

    /**
     * Loads quads from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     */
    private void loadTriples(SPARQLContext context, ASTNode node, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        // triples_template -> triples_same_subj ('.'! triples_template? )?
        ASTNode current = node;
        while (current != null) {
            ASTNode sameSubj = current.getChildren().get(0);
            // triples_same_subj -> var_or_term    property_list_not_empty
            // triples_same_subj -> triples_node   property_list
            Node subject = getNode(context, sameSubj.getChildren().get(0), graph, buffer);
            // property_list_not_empty -> verb object_list (';'! (verb object_list)? )*
            List<ASTNode> members = sameSubj.getChildren().get(1).getChildren();
            for (int i = 0; i != members.size(); i++) {
                Node verb = getNode(context, members.get(i), graph, buffer);
                // object_list -> object (','! object)*
                // object -> graph_node^
                // graph_node -> var_or_term^ | triples_node^
                for (ASTNode objectNode : members.get(i + 1).getChildren()) {
                    Node object = getNode(context, objectNode, graph, buffer);
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
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     */
    private void loadQuadsSupplementary(SPARQLContext context, ASTNode node, GraphNode graph, List<Quad> buffer) throws LoaderException {
        // quads_supp -> quads_not_triples ('.'!)? triples_template?
        // quads_not_triples -> GRAPH! var_or_iri '{'! triples_template? '}'!
        ASTNode quadsNotTriples = node.getChildren().get(0);
        GraphNode inner = (GraphNode) getNode(context, quadsNotTriples.getChildren().get(0), graph, buffer);
        if (quadsNotTriples.getChildren().size() >= 2)
            loadTriples(context, quadsNotTriples.getChildren().get(1), inner, buffer);
        if (node.getChildren().size() >= 2)
            loadTriples(context, node.getChildren().get(1), graph, buffer);
    }

    /**
     * Gets the RDF node equivalent to the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @return The equivalent RDF nodes
     */
    private Node getNode(SPARQLContext context, ASTNode node, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case SPARQLLexer.ID.UNDEF:
                return null;
            case SPARQLLexer.ID.A: // a
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
                return getNodeBlank(context, node);
            case SPARQLLexer.ID.ANON:
                return getNodeAnon();
            case SPARQLLexer.ID.VARIABLE:
                return getVariable(context, node);
            case SPARQLLexer.ID.TRUE: // true
                return getNodeTrue();
            case SPARQLLexer.ID.FALSE: // false
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
                return getNodeCollection(context, node, graph, buffer);
            case SPARQLParser.ID.property_list_not_empty:
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
     * @param context The current context
     * @param node    An AST node
     * @return The equivalent RDF blank node
     */
    private Node getNodeBlank(SPARQLContext context, ASTNode node) {
        String value = node.getValue();
        value = Utils.unescape(value.substring(2));
        return context.resolveBlankNode(value);
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
     * @param context The current context
     * @param node    An AST node
     * @return The associated variable node
     */
    private Node getVariable(SPARQLContext context, ASTNode node) {
        String name = node.getValue().substring(1);
        return context.resolveVariable(name);
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
            case SPARQLLexer.ID.STRING_LITERAL_SINGLE_QUOTE:
            case SPARQLLexer.ID.STRING_LITERAL_QUOTE:
                value = childString.getValue();
                value = value.substring(1, value.length() - 1);
                value = Utils.unescape(value);
                break;
            case SPARQLLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
            case SPARQLLexer.ID.STRING_LITERAL_LONG_QUOTE:
                value = childString.getValue();
                value = value.substring(3, value.length() - 3);
                value = Utils.unescape(value);
                break;
        }

        // No suffix, this is a naked string
        if (node.getChildren().size() <= 1)
            return store.getLiteralNode(value, Vocabulary.xsdString, null);

        ASTNode suffixChild = node.getChildren().get(1);
        if (suffixChild.getSymbol().getID() == SPARQLLexer.ID.LANGTAG) {
            // This is a language-tagged string
            String tag = suffixChild.getValue();
            return store.getLiteralNode(value, Vocabulary.rdfLangString, tag.substring(1));
        } else if (suffixChild.getSymbol().getID() == SPARQLLexer.ID.IRIREF) {
            // Datatype is specified with an IRI
            String iri = suffixChild.getValue();
            iri = Utils.unescape(iri.substring(1, iri.length() - 1));
            return store.getLiteralNode(value, Utils.uriResolveRelative(baseURI, iri), null);
        } else if (suffixChild.getSymbol().getID() == SPARQLLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(suffixChild, suffixChild.getValue());
            return store.getLiteralNode(value, local, null);
        } else if (suffixChild.getSymbol().getID() == SPARQLLexer.ID.PNAME_NS) {
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
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @return A RDF list node
     */
    private Node getNodeCollection(SPARQLContext context, ASTNode node, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        // collection -> '('! graph_node+ ')'!
        List<Node> elements = new ArrayList<>();
        for (ASTNode child : node.getChildren())
            elements.add(getNode(context, child, graph, buffer));
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
     * @param context The current context
     * @param node    An AST node
     * @param graph   The current graph to use
     * @param buffer  The buffer of quads
     * @return The equivalent RDF blank node
     */
    private BlankNode getNodeBlankWithProperties(SPARQLContext context, ASTNode node, GraphNode graph, Collection<Quad> buffer) throws LoaderException {
        // blank_node_property_list -> '['! property_list_not_empty^ ']'!
        // property_list_not_empty	-> verb object_list (';'! (verb object_list)? )*
        BlankNode subject = store.getBlankNode();
        List<ASTNode> members = node.getChildren();
        for (int i = 0; i != members.size(); i++) {
            Node verb = getNode(context, members.get(i), graph, buffer);
            // object_list -> object (','! object)*
            // object -> graph_node^
            // graph_node -> var_or_term^ | triples_node^
            for (ASTNode objectNode : members.get(i + 1).getChildren()) {
                Node object = getNode(context, objectNode, graph, buffer);
                buffer.add(new Quad(graph, subject, (Property) verb, object));
            }
            i++;
        }
        return subject;
    }
}
