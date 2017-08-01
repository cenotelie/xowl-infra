/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
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

package org.xowl.infra.lsp.xowlserver;

import fr.cenotelie.hime.redist.ASTNode;
import fr.cenotelie.hime.redist.ParseResult;
import fr.cenotelie.hime.redist.Text;
import org.xowl.infra.lsp.engine.*;
import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.lsp.structures.DiagnosticSeverity;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.loaders.TurtleLexer;
import org.xowl.infra.store.loaders.TurtleLoader;
import org.xowl.infra.store.loaders.TurtleParser;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.URIUtils;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.SinkLogger;

import java.io.Reader;
import java.util.Collection;
import java.util.List;

/**
 * The analyzer for the rdf-ttl language
 *
 * @author Laurent Wouters
 */
public class XowlLsTurtleAnalyzer extends DocumentAnalyzerHime {
    /**
     * Initializes this analyzer
     */
    public XowlLsTurtleAnalyzer() {
        this(XowlLsTurtleAnalyzer.class.getCanonicalName(), "RDF (Turtle) Analyzer", "rdf-ttl");
    }

    /**
     * Initializes this analyzer
     *
     * @param identifier The unique identifier for this analyzer
     * @param name       The human readable name for the analyzer
     * @param language   The language to match for the analyzer
     */
    protected XowlLsTurtleAnalyzer(String identifier, String name, String language) {
        super(identifier, name, language);
    }

    @Override
    protected ParseResult parse(Reader reader) {
        Logger logger = new SinkLogger();
        TurtleLoader loader = new TurtleLoader();
        return loader.parse(logger, reader);
    }

    @Override
    protected DocumentSymbols findSymbols(String resourceUri, ASTNode root, Text input, SymbolFactory factory, Collection<Diagnostic> diagnostics) {
        DocumentSymbols symbols = new DocumentSymbols();
        XowlLsAnalysisContext context = new XowlLsAnalysisContext(resourceUri, input, factory, symbols, diagnostics);
        for (ASTNode child : root.getChildren()) {
            switch (child.getSymbol().getID()) {
                case TurtleParser.ID.prefixID:
                case TurtleParser.ID.sparqlPrefix:
                    loadPrefixID(context, child);
                    break;
                case TurtleParser.ID.base:
                case TurtleParser.ID.sparqlBase:
                    loadBase(context, child);
                    break;
                default:
                    inspectTriple(context, child);
                    break;
            }
        }
        return symbols;
    }

    /**
     * Loads a prefix and its associated namespace represented by the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     */
    protected void loadPrefixID(XowlLsAnalysisContext context, ASTNode node) {
        String prefix = node.getChildren().get(0).getValue();
        String uri = node.getChildren().get(1).getValue();
        prefix = prefix.substring(0, prefix.length() - 1);
        uri = TextUtils.unescape(uri.substring(1, uri.length() - 1));
        context.namespaces.put(prefix, uri);
    }

    /**
     * Loads the base URI represented by the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     */
    protected void loadBase(XowlLsAnalysisContext context, ASTNode node) {
        String value = node.getChildren().get(0).getValue();
        value = TextUtils.unescape(value.substring(1, value.length() - 1));
        context.baseURI = URIUtils.resolveRelative(context.baseURI, value);
    }

    /**
     * Inspect the triples represented by the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     */
    protected void inspectTriple(XowlLsAnalysisContext context, ASTNode node) {
        if (node.getChildren().get(0).getSymbol().getID() == TurtleParser.ID.predicateObjectList) {
            // the subject is a blank node
            inspectNodeBlankWithProperties(context, node.getChildren().get(0));
            if (node.getChildren().size() > 1)
                inspectProperties(context, node.getChildren().get(1));
        } else {
            inspectNode(context, node.getChildren().get(0), true);
            inspectProperties(context, node.getChildren().get(1));
        }
    }

    /**
     * Inspect the RDF node equivalent to the specified AST node
     *
     * @param context   The current context
     * @param node      An AST node
     * @param isSubject Whether the node is a subject
     */
    protected void inspectNode(XowlLsAnalysisContext context, ASTNode node, boolean isSubject) {
        switch (node.getSymbol().getID()) {
            case 0x003C: // a
                inspectNodeIsA(context, node, isSubject);
                break;
            case TurtleLexer.ID.IRIREF:
                inspectNodeIRIRef(context, node, isSubject);
                break;
            case TurtleLexer.ID.PNAME_LN:
                inspectNodePNameLN(context, node, isSubject);
                break;
            case TurtleLexer.ID.PNAME_NS:
                inspectNodePNameNS(context, node, isSubject);
                break;
            case TurtleLexer.ID.BLANK_NODE_LABEL:
                inspectNodeBlank(context, node, isSubject);
                break;
            case TurtleParser.ID.rdfLiteral:
                inspectNodeLiteral(context, node);
                break;
            case TurtleParser.ID.collection:
                inspectNodeCollection(context, node);
                break;
            case TurtleParser.ID.predicateObjectList:
                inspectNodeBlankWithProperties(context, node);
                break;
        }
    }

    /**
     * Inspects the RDF IRI node for the RDF type element
     *
     * @param context   The current context
     * @param node      An AST node
     * @param isSubject Whether the node is a subject
     */
    protected void inspectNodeIsA(XowlLsAnalysisContext context, ASTNode node, boolean isSubject) {
        onSymbol(context, node, Vocabulary.rdfType, XowlLsWorkspace.SYMBOL_ENTITY, isSubject);
    }

    /**
     * Inspects the RDF IRI node equivalent to the specified AST node
     *
     * @param context   The current context
     * @param node      An AST node
     * @param isSubject Whether the node is a subject
     */
    protected void inspectNodeIRIRef(XowlLsAnalysisContext context, ASTNode node, boolean isSubject) {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(1, value.length() - 1));
        value = URIUtils.resolveRelative(context.baseURI, value);
        onSymbol(context, node, value, XowlLsWorkspace.SYMBOL_ENTITY, isSubject);
    }

    /**
     * Inspects the RDF IRI node equivalent to the specified AST node (local name)
     *
     * @param context   The current context
     * @param node      An AST node
     * @param isSubject Whether the node is a subject
     */
    protected void inspectNodePNameLN(XowlLsAnalysisContext context, ASTNode node, boolean isSubject) {
        String value = node.getValue();
        value = getIRIForLocalName(context, node, value);
        onSymbol(context, node, value, XowlLsWorkspace.SYMBOL_ENTITY, isSubject);
    }

    /**
     * Inspects the RDF IRI node equivalent to the specified AST node (namespace)
     *
     * @param context   The current context
     * @param node      An AST node
     * @param isSubject Whether the node is a subject
     */
    protected void inspectNodePNameNS(XowlLsAnalysisContext context, ASTNode node, boolean isSubject) {
        String value = node.getValue();
        value = TextUtils.unescape(value.substring(0, value.length() - 1));
        value = context.namespaces.get(value);
        onSymbol(context, node, value, XowlLsWorkspace.SYMBOL_ENTITY, isSubject);
    }

    /**
     * Inspects the RDF blank node equivalent to the specified AST node
     *
     * @param context   The current context
     * @param node      An AST node
     * @param isSubject Whether the node is a subject
     */
    protected void inspectNodeBlank(XowlLsAnalysisContext context, ASTNode node, boolean isSubject) {
        String value = node.getValue();
        value = context.resource + "#" + TextUtils.unescape(value.substring(2));
        onSymbol(context, node, value, XowlLsWorkspace.SYMBOL_ENTITY, isSubject);
    }

    /**
     * Inspects the RDF Literal node equivalent to the specified AST node
     *
     * @param node An AST node
     */
    protected void inspectNodeLiteral(XowlLsAnalysisContext context, ASTNode node) {
        // No suffix, this is a naked string
        if (node.getChildren().size() <= 1)
            return;

        ASTNode suffixChild = node.getChildren().get(1);
        if (suffixChild.getSymbol().getID() == TurtleLexer.ID.IRIREF) {
            // Datatype is specified with an IRI
            String iri = suffixChild.getValue();
            iri = TextUtils.unescape(iri.substring(1, iri.length() - 1));
            iri = URIUtils.resolveRelative(context.baseURI, iri);
            onSymbol(context, suffixChild, iri, XowlLsWorkspace.SYMBOL_ENTITY, false);
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(context, suffixChild, suffixChild.getValue());
            onSymbol(context, suffixChild, local, XowlLsWorkspace.SYMBOL_ENTITY, false);
        } else if (suffixChild.getSymbol().getID() == TurtleLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getValue();
            ns = TextUtils.unescape(ns.substring(0, ns.length() - 1));
            ns = context.namespaces.get(ns);
            onSymbol(context, suffixChild, ns, XowlLsWorkspace.SYMBOL_ENTITY, false);
        }
    }

    /**
     * Inspects the RDF list node equivalent to the specified AST node representing a collection of RDF nodes
     *
     * @param context The current context
     * @param node    An AST node
     */
    protected void inspectNodeCollection(XowlLsAnalysisContext context, ASTNode node) {
        for (ASTNode child : node.getChildren())
            inspectNode(context, child, false);
    }

    /**
     * Inspects the RDF blank node (with its properties) equivalent to the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     */
    protected void inspectNodeBlankWithProperties(XowlLsAnalysisContext context, ASTNode node) {
        inspectProperties(context, node);
    }

    /**
     * Gets the full IRI for the specified escaped local name
     *
     * @param node  The parent ASt node
     * @param value An escaped local name
     * @return The equivalent full IRI
     */
    protected String getIRIForLocalName(XowlLsAnalysisContext context, ASTNode node, String value) {
        value = TextUtils.unescape(value);
        int index = 0;
        while (index != value.length()) {
            if (value.charAt(index) == ':') {
                String prefix = value.substring(0, index);
                String uri = context.namespaces.get(prefix);
                if (uri != null) {
                    String name = value.substring(index + 1);
                    return URIUtils.resolveRelative(context.baseURI, uri + name);
                }
            }
            index++;
        }
        context.diagnostics.add(new Diagnostic(
                getRangeFor(context.input, node),
                DiagnosticSeverity.ERROR,
                "rdf-ttl.0",
                name,
                "Failed to resolve local name " + value
        ));
        return null;
    }

    /**
     * Applies the RDF verbs and properties described in the specified AST node to the given RDF subject node
     *
     * @param context The current context
     * @param node    An AST node
     */
    protected void inspectProperties(XowlLsAnalysisContext context, ASTNode node) {
        int index = 0;
        List<ASTNode> children = node.getChildren();
        while (index != children.size()) {
            inspectNode(context, children.get(index), false);
            for (ASTNode objectNode : children.get(index + 1).getChildren()) {
                inspectNode(context, objectNode, false);
            }
            index += 2;
        }
    }

    /**
     * When a symbol is found
     *
     * @param context    The current context
     * @param node       The AST node for the symbol
     * @param identifier The identifier for the symbol
     * @param kind       The kind of symbol
     * @param isSubject  Whether this is a subject (definition)
     */
    protected void onSymbol(XowlLsAnalysisContext context, ASTNode node, String identifier, int kind, boolean isSubject) {
        Symbol symbol = context.factory.resolve(identifier);
        if (symbol.getKind() == 0)
            symbol.setKind(kind);
        if (isSubject)
            context.symbols.addDefinition(new DocumentSymbolReference(
                    symbol,
                    getRangeFor(context.input, node)));
        else
            context.symbols.addReference(new DocumentSymbolReference(
                    symbol,
                    getRangeFor(context.input, node)));
    }
}
