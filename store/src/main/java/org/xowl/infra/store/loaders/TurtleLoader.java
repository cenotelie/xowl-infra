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
import org.xowl.infra.store.storage.NodeManager;
import org.xowl.infra.utils.IOUtils;

import java.io.IOException;
import java.io.Reader;

/**
 * Represents a loader of Turtle syntax
 *
 * @author Laurent Wouters
 */
public class TurtleLoader extends BaseTurtleLoader {
    /**
     * Initializes this loader
     *
     * @param store The RDF store used to create nodes
     */
    public TurtleLoader(NodeManager store) {
        super(store);
    }

    @Override
    protected BaseLRParser getParser(Reader reader) throws IOException {
        String content = IOUtils.read(reader);
        TurtleLexer lexer = new TurtleLexer(content);
        return new TurtleParser(lexer);
    }

    @Override
    protected void loadDocument(ASTNode node) throws LoaderException {
        for (ASTNode child : node.getChildren()) {
            switch (child.getSymbol().getID()) {
                case TurtleParser.ID.prefixID:
                case TurtleParser.ID.sparqlPrefix:
                    loadPrefixID(child);
                    break;
                case TurtleParser.ID.base:
                case TurtleParser.ID.sparqlBase:
                    loadBase(child);
                    break;
                default:
                    loadTriples(child);
                    break;
            }
        }
    }
}
