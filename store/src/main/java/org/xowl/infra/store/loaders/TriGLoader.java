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
import org.xowl.hime.redist.parsers.BaseLRParser;
import org.xowl.infra.store.Vocabulary;
import org.xowl.infra.store.rdf.*;
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.store.storage.cache.CachedNodes;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.http.URIUtils;

import java.io.IOException;
import java.io.Reader;

/**
 * Represents a loader of TriG syntax
 *
 * @author Laurent Wouters
 */
public class TriGLoader extends BaseTurtleLoader {
    /**
     * Initializes this loader
     */
    public TriGLoader() {
        this(new CachedNodes());
    }

    /**
     * Initializes this loader
     *
     * @param store The RDF store used to create nodes
     */
    public TriGLoader(NodeManager store) {
        super(store);
    }

    @Override
    protected BaseLRParser getParser(Reader reader) throws IOException {
        String content = IOUtils.read(reader);
        TriGLexer lexer = new TriGLexer(content);
        return new TriGParser(lexer);
    }

    @Override
    protected void loadDocument(ASTNode node) throws LoaderException {
        GraphNode baseGraph = graph;
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case TriGParser.ID.prefixID:
                case TriGParser.ID.sparqlPrefix:
                    loadPrefixID(child);
                    break;
                case TriGParser.ID.base:
                case TriGParser.ID.sparqlBase:
                    loadBase(child);
                    break;
                case TriGParser.ID.triples:
                    // reset the current graph
                    graph = baseGraph;
                    loadTriples(child);
                    break;
                case TriGParser.ID.graphAnonymous:
                    graph = baseGraph;
                    loadGraphContent(child);
                    break;
                case TriGParser.ID.graphNamed:
                    loadGraphNamed(child);
                    break;
            }
        }
    }

    /**
     * Loads the content of graph from the specified AST node
     *
     * @param node An AST node
     * @throws LoaderException When failing to load the input
     */
    protected void loadGraphContent(ASTNode node) throws LoaderException {
        for (ASTNode child : node.getChildren())
            loadTriples(child);
    }

    /**
     * Loads a named graph from the specified AST node
     *
     * @param node An AST node
     * @throws LoaderException When failing to load the input
     */
    protected void loadGraphNamed(ASTNode node) throws LoaderException {
        graph = (GraphNode) getNode(node.getChildren().get(0));
        loadGraphContent(node.getChildren().get(1));
    }

    /*
    Overridden to use TriGLexer and TriGParser IDs
     */
    @Override
    protected void loadTriples(ASTNode node) throws LoaderException {
        if (node.getChildren().get(0).getSymbol().getID() == TriGParser.ID.predicateObjectList) {
            // the subject is a blank node
            BlankNode subject = getNodeBlankWithProperties(node.getChildren().get(0));
            if (node.getChildren().size() > 1)
                applyProperties(subject, node.getChildren().get(1));
        } else {
            Node subject = getNode(node.getChildren().get(0));
            applyProperties((SubjectNode) subject, node.getChildren().get(1));
        }
    }

    /*
    Overridden to use TriGLexer and TriGParser IDs
     */
    @Override
    protected Node getNode(ASTNode node) throws LoaderException {
        switch (node.getSymbol().getID()) {
            case 0x0042: // a
                return getNodeIsA();
            case TriGLexer.ID.IRIREF:
                return getNodeIRIRef(node);
            case TriGLexer.ID.PNAME_LN:
                return getNodePNameLN(node);
            case TriGLexer.ID.PNAME_NS:
                return getNodePNameNS(node);
            case TriGLexer.ID.BLANK_NODE_LABEL:
                return getNodeBlank(node);
            case TriGLexer.ID.ANON:
                return getNodeAnon();
            case 0x0048: // true
                return getNodeTrue();
            case 0x0049: // false
                return getNodeFalse();
            case TriGLexer.ID.INTEGER:
                return getNodeInteger(node);
            case TriGLexer.ID.DECIMAL:
                return getNodeDecimal(node);
            case TriGLexer.ID.DOUBLE:
                return getNodeDouble(node);
            case TriGParser.ID.rdfLiteral:
                return getNodeLiteral(node);
            case TriGParser.ID.collection:
                return getNodeCollection(node);
            case TriGParser.ID.predicateObjectList:
                return getNodeBlankWithProperties(node);
        }
        throw new LoaderException("Unexpected node " + node.getValue(), node);
    }

    /*
    Overridden to use TriGLexer and TriGParser IDs
     */
    @Override
    protected LiteralNode getNodeLiteral(ASTNode node) throws LoaderException {
        // Compute the lexical value
        String value = null;
        ASTNode childString = node.getChildren().get(0);
        switch (childString.getSymbol().getID()) {
            case TriGLexer.ID.STRING_LITERAL_SINGLE_QUOTE:
            case TriGLexer.ID.STRING_LITERAL_QUOTE:
                value = childString.getValue();
                value = value.substring(1, value.length() - 1);
                value = TextUtils.unescape(value);
                break;
            case TriGLexer.ID.STRING_LITERAL_LONG_SINGLE_QUOTE:
            case TriGLexer.ID.STRING_LITERAL_LONG_QUOTE:
                value = childString.getValue();
                value = value.substring(3, value.length() - 3);
                value = TextUtils.unescape(value);
                break;
        }

        // No suffix, this is a naked string
        if (node.getChildren().size() <= 1)
            return store.getLiteralNode(value, Vocabulary.xsdString, null);

        ASTNode suffixChild = node.getChildren().get(1);
        if (suffixChild.getSymbol().getID() == TriGLexer.ID.LANGTAG) {
            // This is a language-tagged string
            String tag = suffixChild.getValue();
            return store.getLiteralNode(value, Vocabulary.rdfLangString, tag.substring(1));
        } else if (suffixChild.getSymbol().getID() == TriGLexer.ID.IRIREF) {
            // Datatype is specified with an IRI
            String iri = suffixChild.getValue();
            iri = TextUtils.unescape(iri.substring(1, iri.length() - 1));
            return store.getLiteralNode(value, URIUtils.resolveRelative(baseURI, iri), null);
        } else if (suffixChild.getSymbol().getID() == TriGLexer.ID.PNAME_LN) {
            // Datatype is specified with a local name
            String local = getIRIForLocalName(suffixChild, suffixChild.getValue());
            return store.getLiteralNode(value, local, null);
        } else if (suffixChild.getSymbol().getID() == TriGLexer.ID.PNAME_NS) {
            // Datatype is specified with a namespace
            String ns = suffixChild.getValue();
            ns = TextUtils.unescape(ns.substring(0, ns.length() - 1));
            ns = namespaces.get(ns);
            return store.getLiteralNode(value, ns, null);
        }
        throw new LoaderException("Unexpected node " + node.getValue(), node);
    }
}
