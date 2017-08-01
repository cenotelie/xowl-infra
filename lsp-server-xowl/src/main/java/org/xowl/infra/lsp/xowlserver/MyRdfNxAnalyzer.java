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
import fr.cenotelie.hime.redist.ParseError;
import fr.cenotelie.hime.redist.ParseResult;
import org.xowl.infra.lsp.engine.*;
import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.lsp.structures.DiagnosticSeverity;
import org.xowl.infra.lsp.structures.Position;
import org.xowl.infra.lsp.structures.Range;
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
    public DocumentAnalysis analyze(SymbolFactory factory, Document document) {
        NTriplesLoader loader = new NTriplesLoader();
        BufferedLogger logger = new BufferedLogger();
        ParseResult result = loader.parse(logger, document.getCurrentVersion().getContent().getReader());
        if (result == null) {
            return new DocumentAnalysis(null, new Diagnostic[]{
                    new Diagnostic(
                            new Range(new Position(10, 0), new Position(0, 0)),
                            DiagnosticSeverity.ERROR,
                            "xowl.0",
                            "xOWL Analyzer",
                            "The analysis failed"
                    )
            });
        }
        if (!result.isSuccess() || !result.getErrors().isEmpty()) {
            Diagnostic[] diagnostics = new Diagnostic[result.getErrors().size()];
            int index = 0;
            for (ParseError error : result.getErrors()) {
                diagnostics[index++] = new Diagnostic(
                        new Range(
                                new Position(error.getPosition().getLine() - 1, error.getPosition().getColumn() - 1),
                                new Position(error.getPosition().getLine() - 1, error.getPosition().getColumn() - 1 + error.getLength() + 1)
                        ),
                        DiagnosticSeverity.ERROR,
                        "xowl.1",
                        "xOWL Parser",
                        error.getMessage()
                );
            }
            return new DocumentAnalysis(null, diagnostics);
        }

        DocumentSymbols symbols = new DocumentSymbols();
        for (ASTNode triple : result.getRoot().getChildren()) {
            boolean isFirst = true;
            for (ASTNode node : triple.getChildren()) {
                if (node.getSymbol().getID() == NTriplesLexer.ID.IRIREF) {
                    String iri = node.getValue();
                    iri = TextUtils.unescape(iri.substring(1, iri.length() - 1));
                    Symbol symbol = factory.resolve(iri);
                    if (symbol.getKind() == 0)
                        symbol.setKind(MyWorkspace.SYMBOL_IRI);
                    if (isFirst)
                        symbols.addDefinition(new DocumentSymbolReference(
                                symbol,
                                MyWorkspace.getRangeFor(result.getInput(), node)));
                    else
                        symbols.addReference(new DocumentSymbolReference(
                                symbol,
                                MyWorkspace.getRangeFor(result.getInput(), node)));
                }
                isFirst = false;
            }
        }
        return new DocumentAnalysis(symbols);
    }
}
