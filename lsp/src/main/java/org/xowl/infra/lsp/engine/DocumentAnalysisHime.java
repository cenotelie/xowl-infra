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

package org.xowl.infra.lsp.engine;

import fr.cenotelie.hime.redist.ASTNode;

/**
 * Represents the result of a document analysis, supplemented by the AST produced by the Hime parser associated to the document
 *
 * @author Laurent Wouters
 */
public class DocumentAnalysisHime extends DocumentAnalysis {
    /**
     * The root node for the document's AST
     */
    protected ASTNode root;

    /**
     * Gets the root node for the document's AST
     *
     * @return The root node for the document's AST
     */
    public ASTNode getRoot() {
        return root;
    }

    /**
     * Sets the root node for the document's AST
     *
     * @param root The root node for the document's AST
     */
    public void setRoot(ASTNode root) {
        this.root = root;
    }

    /**
     * Initializes this analysis
     *
     * @param version The version of the document used for this analysis
     */
    public DocumentAnalysisHime(DocumentVersion version) {
        super(version);
    }
}
