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

import org.xowl.infra.lsp.structures.CodeActionContext;
import org.xowl.infra.lsp.structures.Command;
import org.xowl.infra.lsp.structures.Range;

/**
 * A document service that provides code actions
 *
 * @author Laurent Wouters
 */
public interface DocumentActionProvider extends DocumentService {
    /**
     * Gets actions for the specified parameters
     *
     * @param document The document for which actions are requested
     * @param range    The range in the document
     * @param context  The context for the request
     * @return The resulting actions
     */
    Command[] getActions(Document document, Range range, CodeActionContext context);
}
