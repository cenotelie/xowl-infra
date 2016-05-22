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

package org.xowl.infra.store.sparql;

import org.xowl.infra.store.Evaluator;
import org.xowl.infra.store.rdf.RDFPattern;
import org.xowl.infra.store.storage.NodeManager;

/**
 * The evaluation context of a SPARQL query
 *
 * @author Laurent Wouters
 */
public interface EvalContext {
    /**
     * Gets the evaluator
     *
     * @return The evaluator
     */
    Evaluator getEvaluator();

    /**
     * Gets the node manager
     *
     * @return The node manager
     */
    NodeManager getNodes();

    /**
     * Gets the solutions for a pattern of RDF quads
     *
     * @param pattern The RDF pattern to match
     * @return The solutions
     */
    Solutions getSolutions(RDFPattern pattern);
}
