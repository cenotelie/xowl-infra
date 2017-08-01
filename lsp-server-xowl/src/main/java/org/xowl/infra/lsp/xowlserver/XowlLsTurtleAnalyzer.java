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
import org.xowl.infra.lsp.engine.DocumentAnalyzerHime;
import org.xowl.infra.lsp.engine.DocumentSymbols;
import org.xowl.infra.lsp.engine.SymbolFactory;
import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.store.loaders.TurtleLoader;
import org.xowl.infra.store.loaders.TurtleParser;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.SinkLogger;

import java.io.Reader;
import java.util.Collection;

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
        super(XowlLsNTriplesAnalyzer.class.getCanonicalName(), "RDF (Turtle) Analyzer", "rdf-ttl");
    }

    @Override
    protected ParseResult parse(Reader reader) {
        Logger logger = new SinkLogger();
        TurtleLoader loader = new TurtleLoader();
        return loader.parse(logger, reader);
    }

    @Override
    protected DocumentSymbols findSymbols(ASTNode root, Text input, SymbolFactory factory, Collection<Diagnostic> diagnostics) {
        DocumentSymbols symbols = new DocumentSymbols();
        for (ASTNode child : root.getChildren()) {
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
        return symbols;
    }
}
