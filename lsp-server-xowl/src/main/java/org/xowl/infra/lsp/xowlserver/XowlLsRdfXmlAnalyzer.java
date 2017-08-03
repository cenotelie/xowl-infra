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

import org.xowl.infra.lsp.engine.*;
import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.lsp.structures.DiagnosticSeverity;
import org.xowl.infra.lsp.structures.Position;
import org.xowl.infra.lsp.structures.Range;
import org.xowl.infra.utils.Identifiable;
import org.xowl.infra.utils.xml.Xml;
import org.xowl.infra.utils.xml.XmlElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * The analyzer for the rdf-xml language
 *
 * @author Laurent Wouters
 */
public class XowlLsRdfXmlAnalyzer implements Identifiable, DocumentAnalyzer {
    @Override
    public String getIdentifier() {
        return XowlLsRdfXmlAnalyzer.class.getCanonicalName();
    }

    @Override
    public String getName() {
        return "RDF (XML) Analyzer";
    }

    @Override
    public int getPriorityFor(Document document) {
        if (Objects.equals(document.getLanguageId(), "rdf-xml"))
            return PRIORITY_HIGH;
        return PRIORITY_NONE;
    }

    @Override
    public DocumentAnalysis analyze(SymbolFactory factory, Document document) {
        org.w3c.dom.Document xmlDocument;
        try {
            xmlDocument = Xml.parse(document.getCurrentVersion().getContent().getReader());
        } catch (Exception exception) {
            return new DocumentAnalysis(null, new Diagnostic[]{
                    new Diagnostic(
                            new Range(new Position(0, 0), new Position(0, 0)),
                            DiagnosticSeverity.ERROR,
                            CODE_PARSER_FAILURE,
                            getName(),
                            "The analysis failed"
                    )
            });
        }

        Collection<Diagnostic> diagnostics = new ArrayList<>();
        DocumentSymbols symbols = new DocumentSymbols();
        XowlLsAnalysisContext context = new XowlLsAnalysisContext(document.getUri(), null, factory, symbols, diagnostics);

        XmlElement root = new XmlElement(xmlDocument.getDocumentElement(), document.getUri());
        /*if (Vocabulary.rdfRDF.equals(root.getNodeIRI()))
            inspectDocument(context, root);
        else
            inspectElement(context, root);*/
        return new DocumentAnalysis(symbols, diagnostics.toArray(new Diagnostic[diagnostics.size()]));
    }
}
