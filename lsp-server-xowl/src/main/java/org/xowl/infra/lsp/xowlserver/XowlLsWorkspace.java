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

import org.xowl.infra.lsp.engine.DocumentAnalyzerProviderStatic;
import org.xowl.infra.lsp.engine.Workspace;
import org.xowl.infra.lsp.structures.SymbolKind;

import java.io.File;

/**
 * The workspace for the xOWL language server
 *
 * @author Laurent Wouters
 */
public class XowlLsWorkspace extends Workspace {
    /**
     * The symbol kind for entities (IRIs, blank nodes and anonymous nodes)
     */
    public static final int SYMBOL_ENTITY = SymbolKind.CLASS;
    /**
     * The symbol kind for ontologies
     */
    public static final int SYMBOL_ONTOLOGY = SymbolKind.CLASS;

    /**
     * Initializes this workspace
     */
    public XowlLsWorkspace() {
        super(new DocumentAnalyzerProviderStatic(
                new XowlLsNTriplesAnalyzer(),
                new XowlLsNQuadsAnalyzer(),
                new XowlLsTurtleAnalyzer(),
                new XowlLsTriGAnalyzer(),
                new XowlLsRdfXmlAnalyzer()
        ));
    }

    @Override
    protected boolean isWorkspaceIncluded(File file) {
        String name = file.getName();
        return (name.endsWith(".nt")
                || name.endsWith(".nt")
                || name.endsWith(".nq")
                || name.endsWith(".ttl")
                || name.endsWith(".trig")
                || name.endsWith(".rdf")
                || name.endsWith(".xrdf")
                || name.endsWith(".ofn")
                || name.endsWith(".fs")
                || name.endsWith(".owl")
                || name.endsWith(".owx")
                || name.endsWith(".xowl")
                || name.endsWith(".sparql")
                || name.endsWith(".denotation"));
    }

    @Override
    protected String getLanguageFor(File file) {
        String name = file.getName();
        if (name.endsWith(".nt"))
            return "rdf-nt";
        if (name.endsWith(".nq"))
            return "rdf-nq";
        if (name.endsWith(".ttl"))
            return "rdf-ttl";
        if (name.endsWith(".trig"))
            return "rdf-trig";
        if (name.endsWith(".rdf"))
            return "rdf-xml";
        if (name.endsWith(".xrdf"))
            return "xrdf";
        if (name.endsWith(".ofn"))
            return "owl-fs";
        if (name.endsWith(".fs"))
            return "owl-fs";
        if (name.endsWith(".owl"))
            return "owl-xml";
        if (name.endsWith(".owx"))
            return "owl-xml";
        if (name.endsWith(".xowl"))
            return "xowl";
        if (name.endsWith(".sparql"))
            return "sparql";
        if (name.endsWith(".denotation"))
            return "denotation";
        return "text";
    }
}
