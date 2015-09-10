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
import org.xowl.store.rdf.Quad;
import org.xowl.store.rdf.Query;
import org.xowl.store.rdf.QuerySolution;

import java.util.Collection;

/**
 * A graph pattern represented by a template of quads
 *
 * @author Laurent Wouters
 */
public class GraphPatternQuads implements GraphPattern {
    /**
     * The RDF query
     */
    private final Query query;

    /**
     * Gets the RDF query represented by this pattern
     *
     * @return The RDF query
     */
    public Query getQuery() {
        return query;
    }

    /**
     * Initializes this pattern
     */
    public GraphPatternQuads() {
        this.query = new Query();
    }

    /**
     * Adds positive quads to this pattern
     *
     * @param quads The quads to add
     */
    public void addPositives(Collection<Quad> quads) {
        query.getPositives().addAll(quads);
    }

    /**
     * Adds a conjunction of negative quads to this pattern
     *
     * @param quads The conjunction of negative quads
     */
    public void addNegatives(Collection<Quad> quads) {
        query.getNegatives().add(quads);
    }

    @Override
    public Collection<QuerySolution> match(final Repository repository) throws EvalException {
        return repository.getRDFQueryEngine().execute(query);
    }
}
