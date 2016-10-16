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

import org.xowl.infra.store.rdf.Node;

import java.util.Map;

/**
 * Represents a pattern of RDF graphs
 *
 * @author Laurent Wouters
 */
public interface GraphPattern {
    /**
     * Evaluates this pattern
     *
     * @param context The evaluation context
     * @return The solutions
     * @throws EvalException When an error occurs during the evaluation
     */
    Solutions eval(EvalContext context) throws EvalException;

    /**
     * Recursively inspect this pattern and its children
     *
     * @param inspector The inspector
     */
    void inspect(Inspector inspector);

    /**
     * Gets a copy of this pattern
     *
     * @param parameters The parameters to be replaced during the clone
     * @return A copy of this pattern
     */
    GraphPattern clone(Map<String, Node> parameters);
}
