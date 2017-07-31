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
import org.xowl.infra.lsp.engine.*;
import org.xowl.infra.store.loaders.NTriplesLexer;
import org.xowl.infra.store.loaders.NTriplesLoader;
import org.xowl.infra.utils.TextUtils;
import org.xowl.infra.utils.logging.BufferedLogger;

/**
 * The analyzer for the rdf-nt and rdf-nq language
 *
 * @author Laurent Wouters
 */
public class MyRdfNxAnalyzer implements DocumentAnalyzer {
    @Override
    public int getPriorityFor(Document document) {
        if (document.getLanguageId().equals("rdf-nt") || document.getLanguageId().equals("rdf-nq"))
            return 100;
        return -1;
    }

    @Override
    public DocumentSymbols getSymbols(SymbolFactory registry, Document document) {
        NTriplesLoader loader = new NTriplesLoader();
        BufferedLogger logger = new BufferedLogger();
        ParseResult result = loader.parse(logger, document.getCurrentVersion().getContent().getReader());
        if (result == null || !result.isSuccess() || !result.getErrors().isEmpty())
            return null;

        DocumentSymbols symbols = new DocumentSymbols();
        for (ASTNode triple : result.getRoot().getChildren()) {
            for (ASTNode node : triple.getChildren()) {
                if (node.getSymbol().getID() == NTriplesLexer.ID.IRIREF) {
                    String iri = node.getValue();
                    iri = TextUtils.unescape(iri.substring(1, iri.length() - 1));
                    Symbol symbol = registry.resolve(iri);
                    if (symbol.getKind() == 0)
                        symbol.setKind(MyWorkspace.SYMBOL_IRI);
                    symbols.addReference(new DocumentSymbolReference(
                            symbol,
                            MyWorkspace.getRangeFor(result.getInput(), node)));
                }
            }
        }
        return symbols;
    }
}
