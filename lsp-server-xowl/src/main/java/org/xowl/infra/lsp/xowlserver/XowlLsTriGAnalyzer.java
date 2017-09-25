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
import org.xowl.infra.lsp.engine.DocumentSymbols;
import org.xowl.infra.lsp.engine.SymbolFactory;
import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.lsp.structures.DocumentLink;
import org.xowl.infra.store.loaders.TriGLexer;
import org.xowl.infra.store.loaders.TriGLoader;
import org.xowl.infra.store.loaders.TriGParser;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.URIUtils;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.SinkLogger;

import java.io.Reader;
import java.util.Collection;

/**
 * The analyzer for the rdf-ttl language
 *
 * @author Laurent Wouters
 */
public class XowlLsTriGAnalyzer extends XowlLsTurtleAnalyzer {
    /**
     * Initializes this analyzer
     */
    public XowlLsTriGAnalyzer() {
        super(XowlLsTriGAnalyzer.class.getCanonicalName(), "RDF (TriG) Analyzer", "rdf-trig");
    }

    @Override
    protected ParseResult parse(Reader reader) {
        Logger logger = new SinkLogger();
        TriGLoader loader = new TriGLoader();
        return loader.parse(logger, reader);
    }

    @Override
    protected void doAnalyze(String resourceUri, ASTNode root, Text input, SymbolFactory factory, DocumentSymbols symbols, Collection<Diagnostic> diagnostics, Collection<DocumentLink> links) {
        XowlLsAnalysisContext context = new XowlLsAnalysisContext(resourceUri, input, factory, symbols, diagnostics);
        for (ASTNode child : root.getChildren()) {
            switch (child.getSymbol().getID()) {
                case TriGParser.ID.prefixID:
                case TriGParser.ID.sparqlPrefix:
                    loadPrefixID(context, child);
                    break;
                case TriGParser.ID.base:
                case TriGParser.ID.sparqlBase:
                    loadBase(context, child);
                    break;
                case TriGParser.ID.triples:
                    // reset the current graph
                    inspectTriple(context, child);
                    break;
                case TriGParser.ID.graphAnonymous:
                    inspectGraphContent(context, child);
                    break;
                case TriGParser.ID.graphNamed:
                    inspectGraphNamed(context, child);
                    break;
            }
        }
    }

    /**
     * Inspect the content of graph from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     */
    protected void inspectGraphContent(XowlLsAnalysisContext context, ASTNode node) {
        for (ASTNode child : node.getChildren())
            inspectTriple(context, child);
    }

    /**
     * Inspect a named graph from the specified AST node
     *
     * @param context The current context
     * @param node    An AST node
     */
    protected void inspectGraphNamed(XowlLsAnalysisContext context, ASTNode node) {
        inspectNode(context, node.getChildren().get(0), true);
        inspectGraphContent(context, node.getChildren().get(1));
    }

    /*
    Overridden to use TriGLexer and TriGParser IDs
     */
    @Override
    protected void inspectTriple(XowlLsAnalysisContext context, ASTNode node) {
        if (node.getChildren().get(0).getSymbol().getID() == TriGParser.ID.predicateObjectList) {
            // the subject is a blank node
            inspectNodeBlankWithProperties(context, node.getChildren().get(0));
            if (node.getChildren().size() > 1)
                inspectProperties(context, node.getChildren().get(1));
        } else {
            inspectNode(context, node.getChildren().get(0), true);
            inspectProperties(context, node.getChildren().get(1));
        }
    }

    /*
    Overridden to use TriGLexer and TriGParser IDs
     */
    @Override
    protected void inspectNode(XowlLsAnalysisContext context, ASTNode node, boolean isDefinition) {
        switch (node.getSymbol().getID()) {
            case 0x003C: // a
                inspectNodeIsA(context, node, isDefinition);
                break;
            case TriGLexer.ID.IRIREF:
                inspectNodeIRIRef(context, node, isDefinition);
                break;
            case TriGLexer.ID.PNAME_LN:
                inspectNodePNameLN(context, node, isDefinition);
                break;
            case TriGLexer.ID.PNAME_NS:
                inspectNodePNameNS(context, node, isDefinition);
                break;
            case TriGLexer.ID.BLANK_NODE_LABEL:
                inspectNodeBlank(context, node, isDefinition);
                break;
            case TriGParser.ID.rdfLiteral:
                inspectNodeLiteral(context, node);
                break;
            case TriGParser.ID.collection:
                inspectNodeCollection(context, node);
                break;
            case TriGParser.ID.predicateObjectList:
                inspectNodeBlankWithProperties(context, node);
                break;
        }
    }

    /*
    Overridden to use TriGLexer and TriGParser IDs
     */
    @Override
    protected void inspectNodeLiteral(XowlLsAnalysisContext context, ASTNode node) {
        // No suffix, this is a naked string
        if (node.getChildren().size() <= 1)
            return;

        ASTNode suffixChild = node.getChildren().get(1);
        if (suffixChild.getSymbol().getID() == TriGLexer.ID.IRIREF) {
            // Datatype is specified with an IRI
            String iri = suffixChild.getValue();
            iri = TextUtils.unescape(iri.substring(1, iri.length() - 1));
            iri = URIUtils.resolveRelative(context.baseURI, iri);
            onSymbol(context, suffixChild, iri, false);
        } else if (suffixChild.getSymbol().getID() == TriGLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(context, suffixChild, suffixChild.getValue());
            onSymbol(context, suffixChild, local, false);
        } else if (suffixChild.getSymbol().getID() == TriGLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getValue();
            ns = TextUtils.unescape(ns.substring(0, ns.length() - 1));
            ns = context.namespaces.get(ns);
            onSymbol(context, suffixChild, ns, false);
        }
    }
}
