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

import org.xowl.infra.store.Repository;
import org.xowl.infra.store.rdf.Node;

/**
 * A graph pattern to be matched by an distant SPARQL endpoint
 *
 * @author Laurent Wouters
 */
public class GraphPatternService implements GraphPattern {
    /**
     * The inner graph pattern
     */
    private final GraphPattern inner;
    /**
     * The pattern to match on the remote endpoint
     */
    private final GraphPattern remote;
    /**
     * The service endpoint to use
     */
    private final Node service;
    /**
     * Whether silent failure is allowed
     */
    private final boolean isSilent;

    /**
     * Initializes this graph pattern
     *
     * @param inner    The inner graph pattern
     * @param remote   The pattern to match on the remote endpoint
     * @param service  The service endpoint to use
     * @param isSilent Whether silent failure is allowed
     */
    public GraphPatternService(GraphPattern inner, GraphPattern remote, Node service, boolean isSilent) {
        this.inner = inner;
        this.remote = remote;
        this.service = service;
        this.isSilent = isSilent;
    }

    @Override
    public Solutions match(final Repository repository) throws EvalException {
        // not implemented yet
        if (isSilent)
            return new SolutionsMultiset(0);
        throw new EvalException("SERVICE is not supported");
    }
}
