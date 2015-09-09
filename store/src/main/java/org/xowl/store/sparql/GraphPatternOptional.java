/*******************************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 ******************************************************************************/

package org.xowl.store.sparql;

import org.xowl.store.Repository;
import org.xowl.store.rdf.QuerySolution;

import java.util.Collection;

/**
 * A graph pattern with an optional part
 *
 * @author Laurent Wouters
 */
public class GraphPatternOptional implements GraphPattern {
    /**
     * The mandatory part of the pattern
     */
    private final GraphPattern mandatory;
    /**
     * The optional part of the pattern
     */
    private final GraphPattern optional;

    /**
     * Initializes this graph pattern
     *
     * @param mandatory The mandatory part of the pattern
     * @param optional  The optional part of the pattern
     */
    public GraphPatternOptional(GraphPattern mandatory, GraphPattern optional) {
        this.mandatory = mandatory;
        this.optional = optional;
    }

    @Override
    public Collection<QuerySolution> match(final Repository repository) throws EvalException {
        // not implemented yet
        throw new EvalException("OPTIONAL is not supported");
    }
}
