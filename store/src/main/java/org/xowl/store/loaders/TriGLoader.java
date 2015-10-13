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
import org.xowl.hime.redist.parsers.BaseLRParser;
import org.xowl.store.IRIs;
import org.xowl.store.rdf.GraphNode;
import org.xowl.store.storage.NodeManager;
import org.xowl.utils.Files;

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
     *
     * @param store The RDF store used to create nodes
     */
    public TriGLoader(NodeManager store) {
        super(store);
    }

    @Override
    protected BaseLRParser getParser(Reader reader) throws IOException {
        String content = Files.read(reader);
        TriGLexer lexer = new TriGLexer(content);
        return new TriGParser(lexer);
    }

    @Override
    protected void loadDocument(ASTNode node) throws LoaderException {
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
                    graph = store.getIRINode(IRIs.GRAPH_DEFAULT);
                    loadTriples(child);
                    break;
                case TriGParser.ID.graphAnonymous:
                    graph = store.getIRINode(IRIs.GRAPH_DEFAULT);
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
}
