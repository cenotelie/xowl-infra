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
import org.xowl.infra.store.loaders.NTriplesLexer;
import org.xowl.infra.store.loaders.NTriplesLoader;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.logging.Logger;
import org.xowl.infra.utils.logging.SinkLogger;

import java.io.Reader;

/**
 * The analyzer for the rdf-nt language
 *
 * @author Laurent Wouters
 */
public class XowlLsNTriplesAnalyzer extends DocumentAnalyzerHime {
    /**
     * Initializes this analyzer
     */
    public XowlLsNTriplesAnalyzer() {
        super(XowlLsNTriplesAnalyzer.class.getCanonicalName(), "RDF (N-Triples) Analyzer", "rdf-nt");
    }

    @Override
    protected ParseResult parse(Reader reader) {
        Logger logger = new SinkLogger();
        NTriplesLoader loader = new NTriplesLoader();
        return loader.parse(logger, reader);
    }

    @Override
    protected void doAnalyze(String resourceUri, ASTNode root, Text input, SymbolFactory factory, DocumentAnalysis analysis) {
        for (ASTNode triple : root.getChildren()) {
            boolean isFirst = true;
            for (ASTNode node : triple.getChildren()) {
                if (node.getSymbol().getID() == NTriplesLexer.ID.IRIREF) {
                    String iri = node.getValue();
                    iri = TextUtils.unescape(iri.substring(1, iri.length() - 1));
                    Symbol symbol = factory.resolve(iri);
                    if (symbol.getKind() == 0)
                        symbol.setKind(XowlLsWorkspace.SYMBOL_ENTITY);
                    if (isFirst)
                        analysis.getSymbols().addDefinition(new DocumentSymbolReference(
                                symbol,
                                getRangeFor(input, node)));
                    else
                        analysis.getSymbols().addReference(new DocumentSymbolReference(
                                symbol,
                                getRangeFor(input, node)));
                }
                isFirst = false;
            }
        }
    }
}
